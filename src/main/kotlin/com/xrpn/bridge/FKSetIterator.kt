package com.xrpn.bridge

import com.xrpn.immutable.FKSet
import com.xrpn.immutable.FKSetRetrieval

class FKSetIterator<out K, out A: Any> internal constructor(val seed: FKSet<K, A>, val resettable: Boolean = true): Iterator<A>, Sequence<A>, Iterable<A> where K: Any, K: Comparable<@UnsafeVariance K> {

    private val iter = FTreeIterator(seed.toIMBTree())

    // not thread safe
    override fun hasNext(): Boolean = iter.hasNext()

    // not thread safe
    override fun next(): A = try {
        iter.next().getv()
    } catch (ex: Exception) {
        throw ex
    }

    fun reset(): Boolean = iter.reset()

    fun resetIfEmpty():Boolean = iter.resetIfEmpty()

    fun nullableNext(): A? = iter.nullableNext()?.getv()

    override fun iterator(): Iterator<A> = this

    internal val retriever: FKSetRetrieval<K, A> = object : FKSetRetrieval<K, A> {
        override fun original(): FKSet<K, A> = seed
    }

    companion object {

        internal inline fun <reified K, reified A: Any> toArray(n: Int, fli: FKSetIterator<K, A>): Array<A> where K: Any, K: Comparable<@UnsafeVariance K> = Array(n){ fli.next() }

        fun <A: Any, R> Sequence<A>.flatMap(
            transform: (A) -> Sequence<R>
        ): Sequence<R> = when (val itr: Iterator<A> = this.iterator()) {
            is FKSetIterator<*,*> -> (itr as FKSetIterator<*,A>).nullableNext()?.let{ transform(it) } ?: emptySequence()
            else -> if (itr.hasNext()) transform( itr.next() ) else emptySequence()
        }
    }
}

