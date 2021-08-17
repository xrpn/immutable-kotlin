package com.xrpn.immutable

import com.xrpn.immutable.FStream.Companion.fsCons

//
// W       W  I  P P P
// W       W  I  P    P
// W   W   W  I  P P P
//  W W W W   I  P
//   W   W    I  P
//

class FStreamIterator<out A: Any>
        internal constructor (val seed: FStream<A>, val resettable: Boolean = false): Iterator<A>, Sequence<A> {

    // iterator are inescapably stateful, mutable creatures
    private var current: FStream<A> = seed

    // not thread safe
    override fun hasNext(): Boolean = synchronized(this) { ! current.isEmpty() }

    // not thread safe
    override fun next(): A = synchronized(this){

        // must check (under the same lock) hasNext before calling (see nullableNext)
        val b: FSCons<A> = when(current) {
            is FSNil -> throw IllegalStateException("next invoked on empty iterator")
            is FSCons -> current as FSCons<A>
        }
        val res: () -> A = b.head
        when (val bodyTail: FStream<A> = b.tail()) {
            is FSNil -> current = FStream.emptyFStream()
            is FSCons -> current = fsCons(bodyTail.head, bodyTail.tail)
        }
        return res()
    }

    fun reset(): Boolean = synchronized(this) {
        if (resettable) {current = seed; true} else false
    }

    fun resetIfEmpty():Boolean = synchronized(this) {
        if (resettable && current.isEmpty()) {current = seed; true} else false
    }

    fun nullableNext(): A? = synchronized(this) {
        if (current.isEmpty()) null else next()
    }

    override fun iterator(): Iterator<A> = this

    companion object {

        fun <A, R> Sequence<A>.flatMap(
            transform: (A) -> Sequence<R>
        ): Sequence<R> = when (this) {
            is FStreamIterator -> {
                val next = this.nullableNext()
                next?.let{ transform(next) } ?: emptySequence()
            }
            else -> if (this.iterator().hasNext()) transform(this.iterator().next()) else emptySequence()
        }
    }
}