package com.xrpn.immutable

import com.xrpn.hash.DigestHash.lChecksumHashCode
import com.xrpn.hash.DigestHash.lChecksumHashCodeReverse
import com.xrpn.hash.MrMr64
import com.xrpn.imapi.IMList
import com.xrpn.imapi.IMListEqual2
import com.xrpn.imapi.IMQueue
import com.xrpn.imapi.IMQueueCompanion
import com.xrpn.immutable.FQueueBody.Companion.empty

sealed class FQueue<out A: Any> : IMQueue<A> {

    fun isEmpty(): Boolean = this === empty

    val size: Int by lazy { this.fsize() }

    // ============ filtering

    override fun fdiscardFront(): FQueue<A> =
        fdropFront(1)

    override fun fdropFront(n: Int): FQueue<A> {

        tailrec fun deplete(q: FQueue<A>, n: Int): FQueue<A> =
            if (n == 0) q else deplete(fqDequeue(q).second, n - 1)

        return if (n <= 0) this else deplete(this, n)
    }

    override fun fdropFrontWhen(isMatch: (A) -> Boolean): FQueue<A> =
        if (ffrontMatch(isMatch)) fdiscardFront() else this

    override fun fdropIfFront(item: @UnsafeVariance A): FQueue<A> =
        fpeek()?.let { if (it == item) fdiscardFront() else this } ?: this

    override fun fdropFrontWhile(isMatch: (A) -> Boolean): FQueue<A> {

        tailrec fun deplete(q: FQueue<A>, changed: Boolean): FQueue<A> {
            if (!changed) return q
            val (newQue, depleted) = q.fpeek()?.let { if (isMatch(it)) Pair(q.fdiscardFront(), true) else Pair(q, false) } ?: Pair(q, false)
            return deplete(newQue, depleted)
        }

        return deplete(this, true)
    }

    override fun ffrontMatch(isMatch: (A) -> Boolean): Boolean =
        fpeek()?.let { isMatch(it) } ?: false

    override fun fdiscardBack(): FQueue<A> =
        fdropBack(1)

    override fun fdropBack(n: Int): FQueue<A> {

        tailrec fun deplete(q: FQueue<A>, n: Int): FQueue<A> =
            if (n == 0) q else deplete(fqTrim(q).second, n - 1)

        return if (n <= 0) this else deplete(this, n)
    }

    override fun fdropBackWhen(isMatch: (A) -> Boolean): FQueue<A> =
        if (fbackMatch(isMatch)) fdiscardBack() else this

    override fun fdropIfBack(item: @UnsafeVariance A): FQueue<A> =
        flast()?.let { if (it == item) fdiscardBack() else this } ?: this

    override fun fdropBackWhile(isMatch: (A) -> Boolean): FQueue<A> {

        tailrec fun deplete(q: FQueue<A>, changed: Boolean): FQueue<A> {
            if (!changed) return q
            val (newQue, depleted) = q.flast()?.let { if (isMatch(it)) Pair(q.fdiscardBack(), true) else Pair(q, false) } ?: Pair(q, false)
            return deplete(newQue, depleted)
        }

        return deplete(this, true)
    }

    override fun fbackMatch(isMatch: (A) -> Boolean): Boolean =
        flast()?.let { isMatch(it) } ?: false

    override fun fempty(): Boolean = isEmpty() || run {
        // TODO remove in time
        check((!fqIsFrontEmpty()) || (!fqIsBackEmpty()))
        false
    }

    override fun flast(): A? = if (fempty()) null else {
        this as FQueueBody<A>
        when (this.back) {
            is FLCons -> this.back.head
            is FLNil -> when (this.front) {
                is FLNil -> null
                is FLCons -> this.front.flast()
            }
        }
    }

    override fun fpeek(): A? = if (fempty()) null else {
        this as FQueueBody<A>
        when (this.front) {
            is FLCons -> this.front.head
            is FLNil -> when (this.back) {
                is FLNil -> null
                is FLCons -> this.back.flast()
            }
        }
    }

    override fun fpeekOrThrow(): A =
        fpeek() ?: throw IllegalStateException("peek on empty queue")

    // ============ grouping

    override fun fcount(isMatch: (A) -> Boolean): Int =
        this.fqGetFront().fcount(isMatch)+this.fqGetBack().fcount(isMatch)

    override fun fsize(): Int =
        this.fqGetFront().size + this.fqGetBack().size

    // ============ transforming

    override fun <B : Any> fdequeueMap(f: (A) -> B): Pair<B?, IMQueue<A>> =
        fpeek()?.let { Pair(f(it), fdiscardFront()) } ?: Pair(null, this)

    override fun freverse(): FQueue<A> =
        FQueueBody.of(fqGetBack().freverse(), fqGetFront().freverse())

    override fun <B : Any> fpeekMap(f: (A) -> B): B? =
        fpeek()?.let { f(it) }

    // ============ altering

    override fun fenqueue(back: @UnsafeVariance A): FQueue<A> =
        fqEnqueue(this, back)

    override fun fdequeue(): Pair<A?, FQueue<A>> =
        if (this.isEmpty()) Pair(null, emptyIMQueue()) else {
            this as FQueueBody<A>
            when (this.front) {
                is FLCons -> Pair(this.front.head, FQueueBody.of(this.front.tail, this.back))
                is FLNil -> when (this.back) {
                    is FLNil -> Pair(null, emptyIMQueue())
                    is FLCons -> this.fqForceFront().fdequeue()
                }
            }
        }

    override fun fdequeueOrThrow(): Pair<A, FQueue<A>> {
        val maybe = fdequeue()
        return maybe.first?.let { Pair(it, maybe.second) } ?: throw IllegalStateException("dequeue on empty queue")
    }

    // ============ utility

    override fun equal(rhs: IMQueue<@UnsafeVariance A>): Boolean = when {
        this === rhs -> true
        !fqSameSize(rhs) -> false
        rhs is FQueueBody -> fqStructuralEqual(rhs) || IMListEqual2(this.toFList(), rhs.toIMList())
        else -> false
    }

    override fun fforEach(f: (A) -> Unit) {
        fqGetFront().fforEach(f)
        fqGetBack().fforEach(f)
    }

    override fun copy(): FQueue<A> =
        FQueueBody.of(fqGetFront().copy(), fqGetBack().copy())

    override fun toIMList(): IMList<A> = toFList()

    override fun copyToMutableList(): MutableList<@UnsafeVariance A> =
        toFList().copyToMutableList()

    // ============ implementation

    internal fun fqForceFront(merge: Boolean = false): FQueue<A> =
        if (this.isEmpty()) emptyIMQueue() else {
            this as FQueueBody<A>
            if (merge) FQueueBody.of(FList.flAppend(this.front, this.back.freverse()), FLNil)
            else when (this.front) {
                is FLNil -> FQueueBody.of(this.back.freverse(), FLNil)
                is FLCons -> this
            }
        }

    internal fun fqGetFront(): FList<A> = (this as FQueueBody<A>).front

    internal fun fqIsFrontEmpty(): Boolean = null == fqGetFront().fhead()

    internal fun fqForceBack(merge: Boolean = false): FQueue<A> =
        if (this.isEmpty()) emptyIMQueue() else {
            this as FQueueBody<A>
            if (merge) FQueueBody.of(FLNil, FList.flAppend(this.back, this.front.freverse()))
            else when (this.back) {
                is FLNil -> FQueueBody.of(FLNil, this.front.freverse())
                is FLCons -> this
            }
        }

    internal fun fqGetBack(): FList<A> = (this as FQueueBody<A>).back

    internal fun fqIsBackEmpty(): Boolean = null == fqGetBack().fhead()

    internal fun fqSameSize(rhs: IMQueue<@UnsafeVariance A>): Boolean = when {
        this === rhs -> true
        fempty() && rhs.fempty() -> true
        fempty() || rhs.fempty() -> false
        fsize() != rhs.fsize() -> false
        else -> true
    }

    // identical
    internal fun fqStrongEqual(rhs: FQueue<@UnsafeVariance A>): Boolean {
        check(fqSameSize(rhs))
        val res = when {
            this === rhs -> true
            fqGetFront().fempty() && rhs.fqGetFront().fempty() -> fqGetBack().equal(rhs.fqGetBack())
            fqGetBack().fempty() && rhs.fqGetBack().fempty() -> fqGetFront().equal(rhs.fqGetFront())
            else -> false
        }

        return res
    }

    internal fun fqStructuralEqual(rhs: IMQueue<@UnsafeVariance A>): Boolean = when {
        rhs is FQueueBody -> when {
            fqStrongEqual(rhs) -> true
            else -> {
                check(fqSameSize(rhs))
                val thisFrontRhsBack = (fqGetBack().isEmpty() && rhs.fqGetFront().isEmpty())
                val thisBackRhsFront = (fqGetFront().isEmpty() && rhs.fqGetBack().isEmpty())
                when {
                    // items are in their entirety at opposite ends
                    thisFrontRhsBack -> fqGetFront().equal(rhs.fqGetBack().freverse())
                    thisBackRhsFront -> fqGetBack().freverse().equal(rhs.fqGetFront())
                    else -> false
                }
            }
        }
        else -> false
    }

    // the head of the list is the first item out (i.e. the head of the queue)
    fun toFList(): FList<A> = this.fqForceFront(merge = true).fqGetFront()

    companion object: IMQueueCompanion {

        override fun <A: Any> emptyIMQueue(): FQueue<A> = empty

        override fun <A: Any> of(vararg items: A, readyToDequeue: Boolean): FQueue<A> {
            if (items.isEmpty()) return emptyIMQueue()
            return if (readyToDequeue) {
                val front = FList.of(items.iterator())
                FQueueBody.of(front, FLNil)
            } else {
                var back : FList<A> = FLNil
                items.forEach {
                    back = FLCons(it, back)
                }
                FQueueBody.of(FLNil, back)
            }
        }

        override fun <A: Any> of(items: Iterator<A>, readyToDequeue: Boolean): FQueue<A> {
            if (! items.hasNext()) return emptyIMQueue()
            return if (readyToDequeue) {
                val front = FList.of(items)
                FQueueBody.of(front, FLNil)
            } else {
                var back : FList<A> = FLNil
                items.forEach {
                    back = FLCons(it, back)
                }
                FQueueBody.of(FLNil, back)
            }
        }

        override fun <A : Any> of(items: List<A>): FQueue<A> {
            if (items.isEmpty()) return emptyIMQueue()
            val front = FList.of(items)
            return FQueueBody.of(front, FLNil)
        }

        override fun <A : Any> of(items: IMList<A>): FQueue<A> {
            if (items.fempty()) return emptyIMQueue()
            return FQueueBody.of(items, FLNil)
        }

        override fun <B, A : Any> ofMap(items: Iterator<B>, f: (B) -> A, readyToDequeue: Boolean): IMQueue<A> {
            if (! items.hasNext()) return emptyIMQueue()
            return if (readyToDequeue) {
                val front = FList.ofMap(items, f)
                FQueueBody.of(front, FLNil)
            } else {
                var back : FList<A> = FLNil
                items.forEach {
                    back = FLCons(f(it), back)
                }
                FQueueBody.of(FLNil, back)
            }
        }

        override fun <A : Any, B> ofMap(items: List<B>, f: (B) -> A): IMQueue<A> {
            if (items.isEmpty()) return emptyIMQueue()
            val front = FList.ofMap(items, f)
            return FQueueBody.of(front, FLNil)
        }

        override fun <A : Any> Collection<A>.toIMQueue(): IMQueue<A> =
            of(this.iterator())

        // ========= implementation

        private fun <A: Any> fqEnqueue(q: FQueue<A>, item: A): FQueue<A> {
            return if (q.fqGetBack().isEmpty()) {
                FQueueBody.of(q.fqGetFront(), FLCons(item, FLNil))
            } else {
                FQueueBody.of(q.fqGetFront(), FLCons(item, q.fqGetBack()))
            }
        }

        private tailrec fun <A: Any> fqDequeue(q: FQueue<A>): Pair<A?, FQueue<A>> =
            if (q.isEmpty()) Pair(null, emptyIMQueue()) else {
                q as FQueueBody<A>
                when (q.front) {
                    is FLCons -> Pair(q.front.head, FQueueBody.of(q.front.tail, q.back))
                    is FLNil -> when (q.back) {
                        is FLNil -> Pair(null, emptyIMQueue())
                        is FLCons -> fqDequeue(q.fqForceFront())
                    }
                }
            }

        // remove one element from the back
        private tailrec fun <A: Any> fqTrim(q: FQueue<A>): Pair<A?, FQueue<A>> =
            if (q.isEmpty()) Pair(null, emptyIMQueue()) else {
                q as FQueueBody<A>
                when (q.back) {
                    is FLCons -> Pair(q.back.head, FQueueBody.of(q.front, q.back.tail))
                    is FLNil -> when (q.front) {
                        is FLNil -> Pair(null, emptyIMQueue())
                        is FLCons -> fqTrim(q.fqForceBack())
                    }
                }
            }

    }
}

internal class FQueueBody<out A: Any> private constructor(
    val front: FList<A>, // the ordering is so, that "head" is the next item to be dequeued -- First Out
    val back: FList<A>   // the ordering is so, that "head" is the last one enqueued -- Last In
) : FQueue<A>() {

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is FQueueBody<*> -> {
            @Suppress("UNCHECKED_CAST") if (fqSameSize(other as IMQueue<A>)) when {
                this.fpeek()!!::class == other.fpeek()!!::class -> {
                    @Suppress("UNCHECKED_CAST") this.equal(other as FQueue<A>)
                }
                else -> false
            } else false
        }
        else -> false
    }

    val hash: Int by lazy {
        // must be consistent with equals()
        val cs = MrMr64()
        lChecksumHashCode(cs, fqGetFront()){ it.hashCode().toLong() }
        lChecksumHashCodeReverse(cs, fqGetBack()){ it.hashCode().toLong() }
    }

    override fun hashCode(): Int = hash

    val show: String by lazy {
        if (this.isEmpty()) FQueue::class.simpleName+"(*)"
        else this::class.simpleName+"(front:"+front.toString()+", back:"+back.toString()+" )"
    }

    override fun toString(): String = show

    companion object {
        val empty = FQueueBody(FLNil, FLNil)

        fun <A: Any> of(
            front: IMList<A>,
            back: IMList<A>
        ): FQueue<A> = when {
            front is FLNil -> when (back) {
                is FLNil -> emptyIMQueue()
                is FLCons -> FQueueBody(front, back)
                else -> throw RuntimeException()
            }
            front is FLCons -> when (back) {
                is FLNil -> FQueueBody(front, back)
                is FLCons -> FQueueBody(front, back)
                else -> throw RuntimeException()
            }
            else -> throw RuntimeException()
        }

        fun <A: Any> hashCode(qb: FQueueBody<A>) = qb.hashCode()
    }


}

