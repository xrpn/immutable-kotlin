package com.xrpn.bridge

import com.xrpn.immutable.FLCons
import com.xrpn.immutable.FLNil
import com.xrpn.immutable.FList
import com.xrpn.immutable.FQueue

class FQueueIterator<out A: Any> internal constructor(val seed: FQueue<A>, val resettable: Boolean = true): Iterator<A>, Sequence<A> {

    // iterator are inescapably stateful, mutable creatures
    private var current: FList<A> = seed.toFList()

    // not thread safe
    override fun hasNext(): Boolean = synchronized(current) {
        ! current.isEmpty()
    }

    // not thread safe
    override fun next(): A = synchronized(current){
        // must ALSO check (under the same lock) hasNext before calling (see nullableNext)
        return getNext()
    }

    fun reset(): Boolean = synchronized(current) {
        doReset()
    }

    fun resetIfEmpty():Boolean = synchronized(current) {
        if (current.isEmpty()) doReset() else false
    }

    fun nullableNext(): A? = synchronized(current) {
        if (current.isEmpty()) null else getNext()
    }

    private fun getNext(): A {

        val b: FLCons<A> = when(current) {
            is FLNil -> throw NoSuchElementException(MSG_EMPTY_ITERATOR)
            is FLCons -> current as FLCons<A>
        }
        val res = b.head
        current = when (val bodyTail = b.tail) {
            is FLNil -> FLNil
            is FLCons -> FLCons(bodyTail.head, bodyTail.tail)
        }
        return res
    }

    private fun doReset() = if (resettable) {
        current = seed.toFList()
        true
    } else false

    override fun iterator(): Iterator<A> = this


    companion object {

        internal const val MSG_EMPTY_ITERATOR = "empty iterator"

        internal inline fun <reified A: Any> toArray(n: Int, fli: FQueueIterator<A>) = Array(n){ fli.next() }

        fun <A, R> Sequence<A>.flatMap(
            transform: (A) -> Sequence<R>
        ): Sequence<R> = when (val itr = this.iterator()) {
            is FQueueIterator -> itr.nullableNext()?.let{ transform(it) } ?: emptySequence()
            else -> if (itr.hasNext()) transform(itr.next()) else emptySequence()
        }
    }
}