package com.xrpn.bridge

import com.xrpn.imapi.IMBTree
import com.xrpn.immutable.TKVEntry

class FTreeIterator<out A, B: Any> internal constructor(val seed: IMBTree<A, B>, val resettable: Boolean = true): Iterator<TKVEntry<A, B>>, Sequence<TKVEntry<A, B>> where A: Any, A: Comparable<@UnsafeVariance A> {

    // iterator are inescapably stateful, mutable creatures
    private var current: IMBTree<A, B> = seed

    // not thread safe
    override fun hasNext(): Boolean = synchronized(current) {
        ! current.fempty()
    }

    // not thread safe
    override fun next(): TKVEntry<A, B> = synchronized(current){
        // must ALSO check (under the same lock) hasNext before calling (see nullableNext)
        return getNext()
    }

    fun reset(): Boolean = synchronized(current) {
        doReset()
    }

    fun resetIfEmpty():Boolean = synchronized(current) {
        if (current.fempty()) doReset() else false
    }

    fun nullableNext(): TKVEntry<A, B>? = synchronized(current) {
        if (current.fempty()) null else {
            val aux: Pair<TKVEntry<A, B>?, IMBTree<A, B>> = current.fpopAndReminder()
            current = aux.second
            aux.first
        }
    }

    private fun getNext(): TKVEntry<A, B> {
        if (current.fempty()) throw NoSuchElementException(MSG_EMPTY_ITERATOR)
        val aux: Pair<TKVEntry<A, B>?, IMBTree<A, B>> = current.fpopAndReminder()
        current = aux.second
        return aux.first!!
    }

    private fun doReset() = if (resettable) {
        current = seed
        true
    } else false

    override fun iterator(): Iterator<TKVEntry<A, B>> = this

    companion object {

        internal val MSG_EMPTY_ITERATOR = "empty iterator"

        internal inline fun <reified A, reified B: Any> toArray(n: Int, fli: FTreeIterator<A, B>) where A: Any, A: Comparable<A> = Array(n){ _ -> fli.next() }

        fun <A, B: Any, R> Sequence<TKVEntry<A, B>>.flatMap(
            transform: (TKVEntry<A, B>) -> Sequence<R>
        ): Sequence<R> where A: Any, A: Comparable<A> = when (this) {
            is FTreeIterator -> this.nullableNext()?.let{ transform(it) } ?: emptySequence()
            else -> if (this.iterator().hasNext()) transform(this.iterator().next()) else emptySequence()
        }
    }
}