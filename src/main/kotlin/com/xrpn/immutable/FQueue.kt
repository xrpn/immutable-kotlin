package com.xrpn.immutable

import java.util.*

//
// W       W  I  P P P
// W       W  I  P    P
// W   W   W  I  P P P
//  W W W W   I  P
//   W   W    I  P
//

sealed class FQueue<out A: Any> {

    fun isEmpty(): Boolean = this === FQueueBody.EMPTY

    fun len(): Int = if (this.isEmpty()) 0 else (this.getFront().size + this.getBack().size)

    fun size(): Int = this.len()

    internal fun forceFront(merge: Boolean = false): FQueue<A> = when(this.isEmpty()) {
        true -> emptyFQueue()
        false -> {
            this as FQueueBody<A>
            if (merge) FQueueBody.of(FList.flAppend(this.front, this.back.freverse()), FLNil)
            else when (this.front) {
                is FLNil -> FQueueBody.of(this.back.freverse(), FLNil)
                is FLCons -> this
            }
        }
    }

    private fun getFront(): FList<A> = (this as FQueueBody<A>).front

    internal fun forceBack(merge: Boolean = false): FQueue<A> = when(this.isEmpty()) {
        true -> emptyFQueue()
        false -> {
            this as FQueueBody<A>
            if (merge) FQueueBody.of(FLNil, FList.flAppend(this.back, this.front.freverse()))
            else when (this.back) {
                is FLNil -> FQueueBody.of(FLNil, this.front.freverse())
                is FLCons -> this
            }
        }
    }

    private fun getBack(): FList<A> = (this as FQueueBody<A>).back

    fun nullableDequeue(): Pair<A?, FQueue<A>> {
        return when(this.isEmpty()) {
            true -> Pair(null, emptyFQueue())
            false -> {
                this as FQueueBody<A>
                when (this.front) {
                    is FLCons -> Pair(this.front.head, FQueueBody.of(this.front.tail, this.back))
                    is FLNil -> when (this.back) {
                        is FLNil -> Pair(null, emptyFQueue())
                        is FLCons -> this.forceFront().nullableDequeue()
                    }
                }
            }
        }
    }

    fun dequeue(): Pair<A, FQueue<A>> {
        return when(this.isEmpty()) {
            true -> throw IllegalStateException("empty queue")
            false -> {
                this as FQueueBody<A>
                when (this.front) {
                    is FLCons -> Pair(this.front.head, FQueueBody.of(this.front.tail, this.back))
                    is FLNil -> when (this.back) {
                        is FLNil -> throw IllegalStateException("empty queue")
                        is FLCons -> this.forceFront().dequeue()
                    }
                }
            }
        }
    }

    fun peek(): A? {
        return when(this.isEmpty()) {
            true -> null
            false -> {
                this as FQueueBody<A>
                when (this.front) {
                    is FLCons -> this.front.head
                    is FLNil -> when (this.back) {
                        is FLNil -> null
                        is FLCons -> this.back.flast()
                    }
                }
            }
        }
    }

    fun drop(n: Int): FQueue<A> {

        tailrec fun deplete(q: FQueue<A>, n: Int): FQueue<A> {
            if (n == 0 || q.isEmpty()) return q
            val (_, shortQueue) = dequeue(q)
            return deplete(shortQueue, n - 1)
        }

        return deplete(this, n)
    }

    fun dropHead(): FQueue<A> = this.drop(1)

    // the head of the list is the first item out (i.e. the head of the queue)
    fun asList(): FList<A> = this.forceFront(merge = true).getFront()

    override fun equals(other: Any?): Boolean = when (this) {
        is FQueueBody -> other is FQueueBody<*> && equal2(this, other)
    }

    override fun hashCode(): Int = when(this) {
        is FQueueBody -> FQueueBody.hashCode(this)
    }

    companion object {

        fun <A: Any> emptyFQueue(): FQueue<A> = FQueueBody.EMPTY

        fun <A: Any> enqueue(q: FQueue<A>, item: A): FQueue<A> {
            return if (q.getBack().isEmpty()) {
                FQueueBody.of(q.getFront(), FLCons(item, FLNil))
            } else {
                FQueueBody.of(q.getFront(), FLCons(item, q.getBack()))
            }
        }

        tailrec fun <A: Any> dequeue(q: FQueue<A>): Pair<A?, FQueue<A>> {
            return when(q.isEmpty()) {
                true -> Pair(null, emptyFQueue())
                false -> {
                    q as FQueueBody<A>
                    when (q.front) {
                        is FLCons -> Pair(q.front.head, FQueueBody.of(q.front.tail, q.back))
                        is FLNil -> when (q.back) {
                            is FLNil -> Pair(null, emptyFQueue())
                            is FLCons -> dequeue(q.forceFront())
                        }
                    }
                }
            }
        }

        fun <A: Any> of(vararg items: A, readyToDequeue: Boolean = false): FQueue<A> {
            if (items.isEmpty()) return emptyFQueue()
            return if (readyToDequeue) {
                val front = FList.of(*items)
                FQueueBody.of(front, FLNil)
            } else {
                var back : FList<A> = FLNil
                items.forEach {
                    back = FLCons(it, back)
                }
                FQueueBody.of(FLNil, back)
            }
        }

        fun <A: Any> of(items: Iterator<A>, readyToDequeue: Boolean = false): FQueue<A> {
            if (! items.hasNext()) return emptyFQueue()
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

        fun <A: Any> equal2(lhs: FQueue<A>, rhs: FQueue<A>): Boolean {
            if (lhs === rhs) return true
            // isEmpty() on a FList or FQueue is a very cheap test
            if (lhs.isEmpty() && rhs.isEmpty()) return true
            if (lhs.isEmpty() || rhs.isEmpty()) return false
            if (lhs.getFront().isEmpty() && rhs.getFront().isEmpty()) return lhs.getBack() == rhs.getBack()
            if (lhs.getBack().isEmpty() && rhs.getBack().isEmpty()) return lhs.getFront() == rhs.getFront()

            // do we know we have the same amount of items? (not so cheap, but OK)
            if (lhs.len() != rhs.len()) return false

            // yes; XOR -- front or back?
            if (    (lhs.getFront().isEmpty() || rhs.getFront().isEmpty())
                 && (lhs.getBack().isEmpty() || rhs.getBack().isEmpty())
               ) return when (lhs.getFront()) {
                // yes!
                // AND tested before, so => XOR (front|back) is empty.  Hence all items must be at the other end
                is FLNil -> rhs.getBack().isEmpty() && rhs.getFront() == lhs.getBack().freverse()
                is FLCons -> rhs.getFront().isEmpty() && rhs.getBack() == lhs.getFront().freverse()
            }

            // in the weeds, sigh. Expensive
            // we have the same amount of elements spread unevenly between front and back
            // this represents the order in which they would be processed by "dequeue"
            val dequeueLhs = FList.flAppend(lhs.getFront(),lhs.getBack().freverse())
            val dequeueRhs = FList.flAppend(rhs.getFront(),rhs.getBack().freverse())
            return dequeueLhs == dequeueRhs
        }

        fun <A: Any> FQueue<A>.equal(rhs: FQueue<A>) = equal2(this, rhs)

    }
}

internal class FQueueBody<out A: Any> private constructor(
    val front: FList<A>, // the ordering is so, that "head" is the next item to be dequeued -- First Out
    val back: FList<A>   // the ordering is so, that "head" is the last one enqueued -- Last In
) : FQueue<A>() {
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is FQueueBody<*> -> when {
            this.isEmpty() && other.isEmpty() -> true
            this.isEmpty() || other.isEmpty() -> false
            this.peek()!!::class == other.peek()!!::class -> equal2(this, other)
            else -> false
        }
        else -> false
    }

    override fun hashCode(): Int {
        var result = front.hashCode()
        result = 3 * result + back.hashCode()
        return result
    }

    override fun toString(): String {
        return if (this.isEmpty()) FQueue::class.simpleName+"(EMPTY)"
        else this::class.simpleName+"(front:"+front.toString()+", back:"+back.toString()+" )"
    }

    companion object {
        val EMPTY = FQueueBody(FLNil, FLNil)

        fun <A: Any> of(
            _front: FList<A> = FLNil,
            _back: FList<A> = FLNil
        ): FQueue<A> {
            return if ((_front is FLNil) && (_back is FLNil)) emptyFQueue() else FQueueBody(_front, _back)
        }

        fun <A: Any> hashCode(qb: FQueueBody<A>) = qb.hashCode()

    }
}

