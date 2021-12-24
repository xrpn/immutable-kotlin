package com.xrpn.bridge

import com.xrpn.imapi.IMBTree
import com.xrpn.immutable.*

//
// W       W  I  P P P
// W       W  I  P    P
// W   W   W  I  P P P
//  W W W W   I  P
//   W   W    I  P
//

class FKMapIterator<out K, out V: Any> internal constructor (val seed: FKMap<K, V>, val resettable: Boolean = true): Iterator<TKVEntry<K, V>>, Iterable<TKVEntry<K, V>> where K: Any, K: Comparable<@UnsafeVariance K> {

    private val iter = FTreeIterator(seed.toIMBTree())

    // not thread safe
    override fun hasNext(): Boolean = iter.hasNext()

    // not thread safe
    override fun next(): TKVEntry<K, V> = try {
        iter.next()
    } catch (ex: Exception) {
        throw ex
    }

    fun reset(): Boolean = iter.reset()

    fun resetIfEmpty():Boolean = iter.resetIfEmpty()

    fun nullableNext(): TKVEntry<K, V>? = iter.nullableNext()

    override fun iterator(): FKMapIterator<K, V> = this

    internal val retriever: FKMapRetrieval<K, V> by lazy { object : FKMapRetrieval<K, V> {
        override fun original(): FKMap<K, V> = seed
    }}

    companion object {

        internal inline fun <reified K, reified A: Any> toArray(n: Int, fli: FKSetIterator<K, A>): Array<A> where K: Any, K: Comparable<@UnsafeVariance K> = Array(n){ fli.next() }

        fun <K, V: Any, R> Sequence<TKVEntry<K, V>>.flatMap(
            transform: (TKVEntry<K, V>) -> Sequence<R>
        ): Sequence<R> where K: Any, K: Comparable<K> = when (val itr: Iterator<TKVEntry<K, V>> = this.iterator()) {
            is FKMapIterator<*,*> -> (itr as FKMapIterator<*,*>).nullableNext()?.let{ transform(@Suppress("UNCHECKED_CAST")(it as TKVEntry<K, V>)) } ?: emptySequence()
            else -> if (itr.hasNext()) transform( itr.next() ) else emptySequence()
        }
    }

}

