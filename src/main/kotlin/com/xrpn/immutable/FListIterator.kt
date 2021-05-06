package com.xrpn.immutable

class FListIterator<out A: Any> internal constructor (val seed: FList<A>, val resettable: Boolean = true): Iterator<A>, Sequence<A> {

    // iterator are inescapably stateful, mutable creatures
    private var current: FList<A> = seed

    // not thread safe
    override fun hasNext(): Boolean = synchronized(this) { ! current.isEmpty() }

    // not thread safe
    override fun next(): A = synchronized(this){
        // must check (under the same lock) hasNext before calling (see nullableNext)
        val b: FLCons<A> = when(current) {
            is FLNil -> throw IllegalStateException("next invoked on empty iterator")
            is FLCons -> current as FLCons<A>
        }
        val res = b.head
        when (val bodyTail = b.tail) {
            is FLNil -> current = FLNil
            is FLCons -> current = FLCons(bodyTail.head, bodyTail.tail)
        }
        return res
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

        internal inline fun <reified A: Any> toArray(n: Int, fli: FListIterator<A>) = Array<A>(n){ _ -> fli.next() }

        fun <A, R> Sequence<A>.flatMap(
            transform: (A) -> Sequence<R>
        ): Sequence<R> = when (this) {
            is FListIterator -> {
                val next = this.nullableNext()
                next?.let{ transform(next) } ?: emptySequence()
            }
            else -> if (this.iterator().hasNext()) transform(this.iterator().next()) else emptySequence()
        }
    }

}

