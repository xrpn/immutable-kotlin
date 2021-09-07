package com.xrpn.bridge

import com.xrpn.immutable.FIKSet

class FIKSetIterator<out A: Any> internal constructor(val seed: FIKSet<A>, val resettable: Boolean = true): Iterator<A>, Sequence<A> {

    private val iter = FTreeIterator(seed.toIMBTree())

    // not thread safe
    override fun hasNext(): Boolean = iter.hasNext()

    // not thread safe
    override fun next(): A {
        val aux: A = iter.next().getv()
        return aux
    }

    fun reset(): Boolean = iter.reset()

    fun resetIfEmpty():Boolean = iter.resetIfEmpty()

    fun nullableNext(): A? = iter.nullableNext()?.getv()

    override fun iterator(): Iterator<A> = this

    companion object {

        internal val MSG_EMPTY_ITERATOR = "empty iterator"

        internal inline fun <reified A: Any> toArray(n: Int, fli: FIKSetIterator<A>) = Array<A>(n){ _ -> fli.next() }

        fun <A, R> Sequence<A>.flatMap(
            transform: (A) -> Sequence<R>
        ): Sequence<R> = when (this) {
            is FIKSetIterator -> this.nullableNext()?.let{ transform(it) } ?: emptySequence()
            else -> if (this.iterator().hasNext()) transform(this.iterator().next()) else emptySequence()
        }
    }
}