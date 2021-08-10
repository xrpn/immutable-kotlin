package com.xrpn.immutable

class FMapIterator<out A, out B: Any> internal constructor (val seed: FMap<A, B>, val resettable: Boolean = true): Iterator<TKVEntry<A,B>>, Sequence<TKVEntry<A,B>> where A: Any, A: Comparable<@UnsafeVariance A> {

    // iterator are inescapably stateful, mutable creatures
    private var current: FList<TKVEntry<A, B>> = seed.entries()

    // not thread safe
    override fun hasNext(): Boolean = synchronized(this) { ! current.isEmpty() }

    // not thread safe
    override fun next(): TKVEntry<A,B> = synchronized(this){
        // must check (under the same lock) hasNext before calling (see nullableNext)
        val b: FLCons<TKVEntry<A,B>> = when(current) {
            is FLNil -> throw IllegalStateException("next invoked on empty iterator")
            is FLCons -> current as FLCons<TKVEntry<A,B>>
        }
        val res = b.head
        when (val bodyTail = b.tail) {
            is FLNil -> current = FLNil
            is FLCons -> current = FLCons(bodyTail.head, bodyTail.tail)
        }
        return res
    }

    fun reset(): Boolean = synchronized(this) {
        if (resettable) {current = seed.entries(); true} else false
    }

    fun resetIfEmpty():Boolean = synchronized(this) {
        if (resettable && current.isEmpty()) {current = seed.entries(); true} else false
    }

    fun nullableNext(): TKVEntry<A, B>? = synchronized(this) {
        if (current.isEmpty()) null else next()
    }

    override fun iterator(): Iterator<TKVEntry<A, B>> = this


    companion object {

        internal inline fun <reified A: Comparable<A>, reified B: Any> toArray(n: Int, fli: FMapIterator<A, B>) = Array<TKVEntry<A, B>>(n){ _ -> fli.next() }

        fun <A: Comparable<A>, B: Any, R> Sequence<TKVEntry<A, B>>.flatMap(
            transform: (TKVEntry<A, B>) -> Sequence<R>
        ): Sequence<R> = when (this) {
            is FMapIterator -> {
                val next = this.nullableNext()
                next?.let{ transform(next) } ?: emptySequence()
            }
            else -> if (this.iterator().hasNext()) transform(this.iterator().next()) else emptySequence()
        }
    }

}

