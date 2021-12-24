package com.xrpn.bridge

import com.xrpn.immutable.*

class FStackIterator<out A: Any> internal constructor(val seed: FStack<A>, val resettable: Boolean = true): Iterator<A>, Iterable<A> {

    // iterator are inescapably stateful, mutable creatures
    private var current: FList<A> = seed.toFList()

    // not thread safe
    override fun hasNext(): Boolean = synchronized(current) {
        ! current.fempty()
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
        if (current.fempty()) doReset() else false
    }

    fun nullableNext(): A? = synchronized(current) {
        if (current.fempty()) null else getNext()
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

    internal val retriever: FStackRetrieval<A> by lazy { object : FStackRetrieval<A> {
        override fun original(): FStack<A> = seed
    }}

    companion object {

        internal const val MSG_EMPTY_ITERATOR = "empty iterator"

        internal inline fun <reified A: Any> toArray(n: Int, fli: FStackIterator<A>) = Array(n){ fli.next() }

        fun <A, R> Sequence<A>.flatMap(
            transform: (A) -> Sequence<R>
        ): Sequence<R> = when (val itr = this.iterator()) {
            is FStackIterator -> itr.nullableNext()?.let{ transform(it) } ?: emptySequence()
            else -> if (itr.hasNext()) transform(itr.next()) else emptySequence()
        }
    }
}