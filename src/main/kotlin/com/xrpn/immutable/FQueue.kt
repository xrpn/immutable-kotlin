package com.xrpn.immutable

import com.xrpn.bridge.FQueueIterator
import com.xrpn.hash.DigestHash.lChecksumHashCode
import com.xrpn.hash.DigestHash.lChecksumHashCodeReverse
import com.xrpn.hash.MrMr64
import com.xrpn.imapi.*
import com.xrpn.imapi.IMListEqual2
import com.xrpn.immutable.FList.Companion.emptyIMList
import com.xrpn.immutable.FQueueBody.Companion.empty
import java.util.*

internal interface FQueueRetrieval<out A: Any> {
    fun original(): FQueue<A>
}

sealed class FQueue<out A: Any> : IMQueue<A> {

    val size: Int by lazy { this.fsize() }

    private val iterable: Iterable<A> by lazy { object : Iterable<A>, FQueueRetrieval<A> {
        override fun iterator(): FQueueIterator<A> = FQueueIterator(this@FQueue)
        override fun original(): FQueue<A> = this@FQueue
    }}

    override fun asIterable(): Iterable<A> = iterable

    // imcommon

    override val seal: IMSC = IMSC.IMQUEUE

    override fun fcontains(item: @UnsafeVariance A?): Boolean =
        item?.let{ fqGetBack().fcontains(item) || fqGetFront().fcontains(item) } ?: false

    override fun fcount(isMatch: (A) -> Boolean): Int =
        fqGetFront().fcount(isMatch)+fqGetBack().fcount(isMatch)

    override fun fdropAll(items: IMCommon<@UnsafeVariance A>): FQueue<A> =
        if (items.fempty()) this else FQueueBody.of(fqGetFront().fdropAll(items),fqGetBack().fdropAll(items))

    override fun fdropItem(item: @UnsafeVariance A): FQueue<A> =
        FQueueBody.of(fqGetFront().fdropItem(item),fqGetBack().fdropItem(item))

    override fun fempty(): Boolean = this === empty || run {
        // TODO remove in time
        check((!fqIsFrontEmpty()) || (!fqIsBackEmpty()))
        false
    }

    override fun ffilter(isMatch: (A) -> Boolean): FQueue<A> {
        val filteredFront = fqGetFront().ffilter(isMatch)
        val filteredBack = fqGetBack().ffilter(isMatch)
        return if (filteredFront === fqGetFront() && filteredBack === fqGetBack()) this else FQueueBody.of(filteredFront,filteredBack)
    }

    override fun ffilterNot(isMatch: (A) -> Boolean): FQueue<A> =
        ffilter{ !isMatch(it) }

    override fun ffindAny(isMatch: (A) -> Boolean): A? =
        fqGetBack().ffindAny(isMatch) ?: fqGetFront().ffindAny(isMatch)

    override fun <R> ffold(z: R, f: (acc: R, A) -> R): R {

        tailrec fun go(q: FQueue<A>, r: R): Pair<FQueue<A>, R> =
            if (q.fempty()) Pair(q, r) else go(q.fdrop(1), f(r, q.fnext()!!))

        return go(this, z).second
    }

    override fun fisStrict(): Boolean =
        fqGetBack().fisStrict() && fqGetFront().fisStrict()

    override fun fpick(): A? =
        fqGetBack().fhead() ?: fqGetFront().fhead()

    override fun fpopAndRemainder(): Pair<A?, FQueue<A>> = fdequeue()

    override fun fsize(): Int =
        fqGetFront().size + fqGetBack().size

    override fun toEmpty(): IMQueue<A> = empty

    // ============ IMOrdered

    override fun fdrop(n: Int): FQueue<A> = when {
        n <= 0 -> this
        fsize() <= n -> empty
        0 < n -> {

            tailrec fun deplete(q: FQueue<A>, n: Int): FQueue<A> =
                if (n == 0) q else deplete(fqDequeue(q).second, n - 1)

            deplete(this, n)
        }
        else -> throw RuntimeException("internal error")
    }

    override fun fnext(): A? =
        ffirst()

    override fun freverse(): FQueue<A> =
        FQueueBody.of(fqGetBack(), fqGetFront())

    override fun frotl(): FQueue<A> = if (fempty() || (1 == fsize())) this else { // (A, B, C).frotl() becomes (B, C, A)
        val (head, shortQueue) = fdequeue()
        shortQueue.fenqueue(head!!)
    }

    override fun frotr(): FQueue<A> = /* (A, B, C).frotr() becomes (C, A, B) */ when {
        fempty() -> this
        1 == fsize() -> this
        2 == fsize() -> frotl()
        else -> {
            val last = flast()!!
            val shortQueue = fdiscardBack()
            val front = FLCons(last, shortQueue.fqForceFront(merge = true).fqGetFront())
            FQueueBody.of(front, FLNil)
        }
    }

    override fun fswaph(): FQueue<A> = /* (A, B, C).fswaph() becomes (B, A, C) */ when {
        fempty() -> this
        1 == fsize() -> this
        2 == fsize() -> frotl()
        else -> {
            val (head1, shortQueue1) = fdequeue()
            val (head2, shortQueue2) = shortQueue1.fdequeue()
            val aux = FLCons(head2!!, FLCons(head1!!, shortQueue2.fqForceFront(merge = true).fqGetFront()))
            FQueueBody.of(aux, FLNil)
        }
    }

    override fun <B : Any> fzip(items: IMOrdered<B>): IMQueue<Pair<A, B>> =
        if (fempty()) empty
        else items.fnext()?.let { FQueueBody.of(toFList().fzip(items),emptyIMList()) } ?: empty

    // ============ IMMappable

    override fun <B: Any> fmap(f: (A) -> B): IMQueue<B> =
        FQueueBody.of(fqGetFront().fmap(f),fqGetBack().fmap(f))

    // ============ IMMapplicable

    override fun <T: Any> fapp(op: (IMQueue<A>) -> ITMap<T>): ITMapp<T> =
        IMMappOp.flift2mapp(op(this))!!

    // ============ filtering

    override fun fdiscardFront(): FQueue<A> =
        fdrop(1)

    override fun fdropFrontWhen(isMatch: (A) -> Boolean): FQueue<A> =
        if (fqFrontMatch(isMatch)) fdiscardFront() else this

    override fun fdropFrontWhile(isMatch: (A) -> Boolean): FQueue<A> {

        tailrec fun deplete(q: FQueue<A>, changed: Boolean): FQueue<A> {
            if (!changed) return q
            val (newQue, depleted) = q.ffirst()?.let { if (isMatch(it)) Pair(q.fdiscardFront(), true) else Pair(q, false) } ?: Pair(q, false)
            return deplete(newQue, depleted)
        }

        return deplete(this, true)
    }

    override fun fdropIfFront(item: @UnsafeVariance A): FQueue<A> =
        ffirst()?.let { if (it == item) fdiscardFront() else this } ?: this

    override fun fdiscardBack(): FQueue<A> =
        fdropBack(1)

    override fun fdropBack(n: Int): FQueue<A> {

        tailrec fun deplete(q: FQueue<A>, n: Int): FQueue<A> =
            if (n == 0) q else deplete(fqTrim(q).second, n - 1)

        return if (n <= 0) this else deplete(this, n)
    }

    override fun fdropBackWhen(isMatch: (A) -> Boolean): FQueue<A> =
        if (fqBackMatch(isMatch)) fdiscardBack() else this

    override fun fdropBackWhile(isMatch: (A) -> Boolean): FQueue<A> {

        tailrec fun deplete(q: FQueue<A>, changed: Boolean): FQueue<A> {
            if (!changed) return q
            val (newQue, depleted) = q.flast()?.let { if (isMatch(it)) Pair(q.fdiscardBack(), true) else Pair(q, false) } ?: Pair(q, false)
            return deplete(newQue, depleted)
        }

        return deplete(this, true)
    }

    override fun fdropIfBack(item: @UnsafeVariance A): FQueue<A> =
        flast()?.let { if (it == item) fdiscardBack() else this } ?: this

    override fun flast(): A? = if (fempty()) null else {
        this as FQueueBody<A>
        when (back) {
            is FLCons -> back.head
            is FLNil -> when (front) {
                is FLNil -> null
                is FLCons -> front.flast()
            }
        }
    }

    override fun ffirst(): A? = if (fempty()) null else {
        this as FQueueBody<A>
        when (front) {
            is FLCons -> front.head
            is FLNil -> when (back) {
                is FLNil -> null
                is FLCons -> back.flast()
            }
        }
    }

    override fun ffirstOrThrow(): A =
        ffirst() ?: throw IllegalStateException("peek on empty queue")

    // ============ grouping (NOP)

    // ============ transforming

    override fun <B : Any> fdequeueMap(f: (A) -> B): Pair<B?, IMQueue<A>> =
        ffirst()?.let { Pair(f(it), fdiscardFront()) } ?: Pair(null, this)

    override fun <B : Any> fpeekMap(f: (A) -> B): B? =
        ffirst()?.let { f(it) }

    // ============ altering

    override fun fdequeue(): Pair<A?, FQueue<A>> =
        if (fempty()) Pair(null, emptyIMQueue()) else {
            this as FQueueBody<A>
            when (front) {
                is FLCons -> Pair(front.head, FQueueBody.of(front.tail, back))
                is FLNil -> when (back) {
                    is FLNil -> Pair(null, emptyIMQueue())
                    is FLCons -> fqForceFront().fdequeue()
                }
            }
        }

    override fun fdequeueOrThrow(): Pair<A, FQueue<A>> {
        val maybe = fdequeue()
        return maybe.first?.let { Pair(it, maybe.second) } ?: throw IllegalStateException("dequeue on empty queue")
    }

    internal fun fenqueue(back: @UnsafeVariance A): FQueue<A> =
        fqEnqueue(this, back)

    // ============ utility

    override fun equal(rhs: IMQueue<@UnsafeVariance A>, strong: Boolean): Boolean = when {
        this === rhs -> true
        !fqSameSize(rhs) -> false
        rhs is FQueueBody -> {
            val structural = fqStructuralEqual(rhs)
            val semantic: Boolean by lazy { fqSemanticEqual(rhs) }
            structural || (!strong && semantic)
        }
        else -> false
    }

    override fun fforEach(f: (A) -> Unit) {
        fqGetFront().fforEach(f)
        fqGetBack().fforEach(f)
    }

    override fun copy(): FQueue<A> =
        FQueueBody.of(fqGetFront().copy(), fqGetBack().copy())

    override fun copyToMutableList(): MutableList<@UnsafeVariance A> =
        toFList().copyToMutableList()

    override fun toIMList(): IMList<A> = toFList()

    // ============ implementation

    internal fun fqBackMatch(isMatch: (A) -> Boolean): Boolean =
        flast()?.let { isMatch(it) } ?: false

    internal fun fqFrontMatch(isMatch: (A) -> Boolean): Boolean =
        ffirst()?.let { isMatch(it) } ?: false

    private val forceFrontYesMerge: FQueue<A> by lazy {
        this as FQueueBody
        FQueueBody.of(FList.flAppend(front, back.freverse()), FLNil)
    }
    private val forceFrontNoMerge: FQueue<A> by lazy {
        this as FQueueBody
        when (front) {
            is FLNil -> FQueueBody.of(back.freverse(), FLNil)
            is FLCons -> this
        }
    }
    internal fun fqForceFront(merge: Boolean = false): FQueue<A> =
        if (fempty()) emptyIMQueue() else {
            this as FQueueBody<A>
            if (merge) forceFrontYesMerge
            else forceFrontNoMerge
        }

    internal fun fqGetFront(): FList<A> = (this as FQueueBody<A>).front

    internal fun fqIsFrontEmpty(): Boolean = null == fqGetFront().fhead()

    internal fun fqForceBack(merge: Boolean = false): FQueue<A> =
        if (fempty()) emptyIMQueue() else {
            this as FQueueBody<A>
            if (merge) FQueueBody.of(FLNil, FList.flAppend(back, front.freverse()))
            else when (back) {
                is FLNil -> FQueueBody.of(FLNil, front.freverse())
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
    internal fun fqStructuralEqual(rhs: FQueue<@UnsafeVariance A>): Boolean {
        check(fqSameSize(rhs))
        val res = when {
            this === rhs -> true
            fqGetFront().fempty() && rhs.fqGetFront().fempty() -> fqGetBack().equal(rhs.fqGetBack())
            fqGetFront().fempty() || rhs.fqGetFront().fempty() -> false
            fqGetBack().fempty() && rhs.fqGetBack().fempty() -> fqGetFront().equal(rhs.fqGetFront())
            fqGetBack().fempty() || rhs.fqGetBack().fempty() -> false
            else -> fqGetFront().fsize() == rhs.fqGetFront().fsize()
                    && fqGetBack().fsize() == rhs.fqGetBack().fsize()
                    && IMListEqual2(fqGetFront(), rhs.fqGetFront())
                    && IMListEqual2(fqGetBack(), rhs.fqGetBack())
        }

        return res
    }

    internal fun fqSemanticEqual(rhs: IMQueue<@UnsafeVariance A>): Boolean = when {
        rhs is FQueueBody -> when {
            fqStructuralEqual(rhs) -> true
            else -> {
                check(fqSameSize(rhs))
                val thisFrontRhsBack = (fqGetBack().fempty() && rhs.fqGetFront().fempty())
                val thisBackRhsFront = (fqGetFront().fempty() && rhs.fqGetBack().fempty())
                when {
                    // items are in their entirety at opposite ends
                    thisFrontRhsBack -> IMListEqual2(fqGetFront(), rhs.fqGetBack().freverse())
                    thisBackRhsFront -> IMListEqual2(fqGetBack().freverse(), rhs.fqGetFront())
                    else -> IMListEqual2(toFList(), rhs.toIMList())
                }
            }
        }
        else -> false
    }

    // the head of the list is the first item out (i.e. the head of the queue)
    fun toFList(): FList<A> = fqForceFront(merge = true).fqGetFront()

    companion object: IMQueueCompanion, IMQueueWritable, IMWritable {

        override fun <A: Any> emptyIMQueue(): FQueue<A> = empty

        override fun <A : Any> fadd(src: A, dest: IMCommon<A>): IMQueue<A>? =
            (dest as? FQueue<A>)?.fenqueue(src)

        override fun <A : Any> fenqueue(back: A, dest: IMQueue<A>): IMQueue<A>?  =
            (dest as? FQueue<A>)?.fenqueue(back)

        override fun <A : Any> of(vararg items: A, readyToDequeue: Boolean): FQueue<A> {
            if (items.isEmpty()) return emptyIMQueue()
            return if (readyToDequeue) {
                val front = FList.of(items.iterator())
                FQueueBody.of(front, FLNil)
            } else {
                var back : FList<A> = FLNil
                items.forEach { back = FLCons(it, back) }
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
                items.forEach { back = FLCons(it, back) }
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

        override fun <B, A : Any> ofMap(items: Iterator<B>, readyToDequeue: Boolean, f: (B) -> A): FQueue<A> {
            if (! items.hasNext()) return emptyIMQueue()
            return if (readyToDequeue) {
                val front = FList.ofMap(items, f)
                FQueueBody.of(front, FLNil)
            } else {
                var back : FList<A> = FLNil
                items.forEach { back = FLCons(f(it), back) }
                FQueueBody.of(FLNil, back)
            }
        }

        override fun <B, A : Any> ofMap(items: List<B>, f: (B) -> A): FQueue<A> {
            if (items.isEmpty()) return emptyIMQueue()
            val front = FList.ofMap(items, f)
            return FQueueBody.of(front, FLNil)
        }

        override fun <A : Any> Collection<A>.toIMQueue(): IMQueue<A> =
            of(iterator())

        // ========= implementation

        private fun <A: Any> fqEnqueue(q: FQueue<A>, item: A): FQueue<A> {
            return if (q.fqGetBack().fempty()) {
                FQueueBody.of(q.fqGetFront(), FLCons(item, FLNil))
            } else {
                FQueueBody.of(q.fqGetFront(), FLCons(item, q.fqGetBack()))
            }
        }

        private tailrec fun <A: Any> fqDequeue(q: FQueue<A>): Pair<A?, FQueue<A>> =
            if (q.fempty()) Pair(null, emptyIMQueue()) else {
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
            if (q.fempty()) Pair(null, emptyIMQueue()) else {
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
    val back: FList<A>,   // the ordering is so, that "head" is the last one enqueued -- Last In
) : FQueue<A>() {

    override fun equals(other: Any?): Boolean = if (fempty()) emptyEquals(other) else when {
        this === other -> true
        other == null -> false
        other is FQueueBody<*> -> (@Suppress("UNCHECKED_CAST") (other as? IMQueue<A>))?.let{ oth ->
            if (fqSameSize(oth)) when {
                ffirst().isStrictly(other.ffirst()) -> equal(oth) /* TODO remove in time */ && run { check(hashCode()==other.hashCode()); true }
                else -> false
            } else false
        } ?: false
        other is Iterable<*> ->  when(val iter = other.iterator()) {
            is FQueueIterator<*> -> equals(iter.retriever.original())
            else -> false
        }
        else -> false
    }

    override fun softEqual(rhs: Any?): Boolean = equals(rhs) || when (rhs) {
        is Queue<*> -> when {
            fempty() -> rhs.isEmpty()
            rhs.isEmpty() -> fempty()
            fsize() != rhs.size -> false
            fpick()!!.isStrictlyNot(rhs.peek()!!) -> false
            else -> rhs.equals(this)
        }
        is IMOrdered<*> -> IMOrdered.softEqual(this,rhs)
        else -> false
    }

    val hash: Int by lazy {
        // must be consistent with equals()
        if (fempty()) emptyHashCode else {
            val cs = MrMr64()
            when {
                fqGetBack().fempty() -> lChecksumHashCode(cs, fqGetFront()){ it.hashCode().toLong() }
                fqGetFront().fempty() -> lChecksumHashCodeReverse(cs, fqGetBack()){ it.hashCode().toLong() }
                else -> {
                    lChecksumHashCode(cs, fqGetFront()){ it.hashCode().toLong() }
                    lChecksumHashCodeReverse(cs, fqGetBack()){ it.hashCode().toLong() }
                }
            }
        }
    }

    override fun hashCode(): Int = hash

    val show: String by lazy {
        if (fempty()) FQueue::class.simpleName+"(*)"
        else {
            val sFront = if (front.fempty()) " " else front.ffoldLeft(""){ str, item -> "$str $item" }
            val sBack = if (back.fempty()) " " else {
                val sBackAcc = StringBuilder().append(' ')
                back.fforEachReverse { item -> sBackAcc.append(item).append(' ') }
                sBackAcc.toString()
            }
            this::class.simpleName+"{$sFront$sBack}"
        }
    }

    override fun toString(): String = show

    companion object {
        val empty = FQueueBody(FLNil, FLNil)
        private val emptyEquality = object { val proxy = IMCommonEmpty.Companion.IMCommonEmptyEquality() } // this gives me a distinct hashCode
        private val emptyEquals: (Any?) -> Boolean = { other -> emptyEquality.proxy.equals(other) }
        private val emptyHashCode: Int by lazy {  emptyEquality.proxy.hashCode() }

        fun <A: Any> of(
            front: IMList<A>,
            back: IMList<A>
        ): FQueue<A> = when {
            front is FLNil -> when (back) {
                is FLNil -> emptyIMQueue()
                is FLCons -> FQueueBody(front, back)
                else -> throw RuntimeException("internal error")
            }
            front is FLCons -> when (back) {
                is FLNil -> FQueueBody(front, back)
                is FLCons -> FQueueBody(front, back)
                else -> throw RuntimeException("internal error")
            }
            else -> throw RuntimeException("internal error")
        }

        fun <A: Any> hashCode(qb: FQueueBody<A>) = qb.hashCode()
    }

}

