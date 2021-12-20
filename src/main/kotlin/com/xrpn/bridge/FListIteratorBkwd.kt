package com.xrpn.bridge

import com.xrpn.immutable.FLCons
import com.xrpn.immutable.FLNil
import com.xrpn.immutable.FList
import com.xrpn.immutable.FListRetrieval

class FListIteratorBkwd<out A: Any> internal constructor(val seed: FList<A>, val resettable: Boolean = true): ListIterator<A>, Iterable<A> {

    // iterator are inescapably stateful, mutable creatures
    private var current: FList<A> = seed.freverse()
    private var currentIx = seed.size

    // not thread safe
    override fun hasPrevious(): Boolean = synchronized(current) {
        ! current.fempty()
    }

    // not thread safe
    override fun previous(): A = synchronized(current){
        // must ALSO check (under the same lock) hasNext before calling (see nullableNext)
        return getPrevious()
    }

    fun reset(): Boolean = synchronized(current) {
        doReset()
    }

    fun resetIfEmpty():Boolean = synchronized(current) {
        if (current.fempty()) doReset() else false
    }

    fun nullablePrevious(): A? = synchronized(current) {
        if (current.fempty()) null else getPrevious()
    }

    override fun iterator(): Iterator<A> = this

    internal val retriever: FListRetrieval<A> = object : FListRetrieval<A> {
        override fun original(): FList<A> = seed
    }

    private fun getPrevious(): A {

        val b: FLCons<A> = when(current) {
            is FLNil -> throw NoSuchElementException(MSG_EMPTY_ITERATOR)
            is FLCons -> current as FLCons<A>
        }
        val res = b.head
        currentIx -= 1
        when (val bodyTail = b.tail) {
            is FLNil -> current = FLNil
            is FLCons -> current = FLCons(bodyTail.head, bodyTail.tail)
        }
        return res
    }

    private fun doReset() = if (resettable) {
        current = seed.freverse()
        currentIx = seed.size
        true
    } else false

    companion object {

        internal val MSG_EMPTY_ITERATOR = "empty iterator"

        internal inline fun <reified A: Any> toArray(n: Int, fli: FListIteratorBkwd<A>) = Array(n){ _ -> fli.previous() }

    }

    override fun hasNext(): Boolean = synchronized(current) { seed.size > currentIx}

    override fun nextIndex(): Int = synchronized(current) { currentIx }

    override fun next(): A {
        TODO("Not yet implemented")
    }

    override fun previousIndex(): Int = synchronized(current) { currentIx - 1 }
}