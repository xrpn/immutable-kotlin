package com.xrpn.bridge

import com.xrpn.imapi.IMSet
import com.xrpn.immutable.FSet

class FSetIterator<out A: Any> internal constructor(val seed: FSet<A>, val resettable: Boolean = true): Iterator<A>, Sequence<A> {

    // iterator are inescapably stateful, mutable creatures
    private var current: FSet<A> = seed

    // not thread safe
    override fun hasNext(): Boolean = synchronized(current) {
        0 < current.size
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
        if (current.isEmpty()) null else {
            val aux: Pair<A?, IMSet<A>> = current.popAndReminder()
            current = aux.second as FSet<A>
            aux.first
        }
    }

    private fun getNext(): A {
        if (current.isEmpty()) throw NoSuchElementException(MSG_EMPTY_ITERATOR)
        val aux: Pair<A?, IMSet<A>> = current.popAndReminder()
        current = aux.second as FSet<A>
        return aux.first!!
    }

    private fun doReset() = if (resettable) {
        current = seed
        true
    } else false

    override fun iterator(): Iterator<A> = this

    companion object {

        internal val MSG_EMPTY_ITERATOR = "empty iterator"

        internal inline fun <reified A: Any> toArray(n: Int, fli: FSetIterator<A>) = Array<A>(n){ _ -> fli.next() }

        fun <A, R> Sequence<A>.flatMap(
            transform: (A) -> Sequence<R>
        ): Sequence<R> = when (this) {
            is FSetIterator -> this.nullableNext()?.let{ transform(it) } ?: emptySequence()
            else -> if (this.iterator().hasNext()) transform(this.iterator().next()) else emptySequence()
        }
    }
}