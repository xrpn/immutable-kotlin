package com.xrpn.imapi

import com.xrpn.immutable.TKVEntry

interface IMListTransforming<out A: Any> {
    fun <B: Any> fflatMap(f: (A) -> IMList<B>): IMList<B>  // 	When working with sequences, it works like map followed by flatten
    fun <B> ffoldLeft(z: B, f: (acc: B, A) -> B): B // 	“Fold” the elements of the list using the binary operator o, using an initial seed s, going from left to right (see also reduceLeft)
    fun <B> ffoldRight(z: B, f: (A, acc: B) -> B): B // 	“Fold” the elements of the list using the binary operator o, using an initial seed s, going from right to left (see also reduceRight)
    fun freduceLeft(f: (acc: A, A) -> @UnsafeVariance A): A? // 	“Reduce” the elements of the list using the binary operator o, going from left to right
    fun freduceRight(f: (A, acc: A) -> @UnsafeVariance A): A? // 	“Reduce” the elements of the list using the binary operator o, going from right to left
}

interface IMSetTransforming<out A: Any> {
    fun <B: Any> fflatMap(f: (A) -> IMSet<@UnsafeVariance B>): IMSet<B>  // 	When working with sequences, it works like map followed by flatten
    fun <B: Any> fmapToList(f: (A) -> B): IMList<B> // 	Return a new sequence by applying the function f to each element in the List
}

interface IMXSetTransforming<out A: Any> {
    fun <B> fflatMapKK(f: (A) -> IMSet<@UnsafeVariance B>): IMSet<B> where B: Any, B: Comparable<B>  // 	When working with sequences, it works like map followed by flatten
    fun <B> fmapKK(f: (A) -> B): IMSet<B> where B: Any, B: Comparable<B> // 	Return a new sequence by applying the function f to each element in the List
}

internal interface IMKSetTransforming<out K, out A: Any>: IMSetTransforming<A> where K: Any, K: Comparable<@UnsafeVariance K>

internal interface IMKKSetTransforming<out K>: IMSetTransforming<K>, IMXSetTransforming<K> where K: Any, K: Comparable<@UnsafeVariance K>

interface IMMapTransforming<out K, out V: Any> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun <C, D: Any> fflatMap(f: (TKVEntry<K, V>) -> IMMap<C, D>): IMMap<C, D> where C: Any, C: Comparable<@UnsafeVariance C>
    fun <J> fmapKeys(f: (TKVEntry<K, V>) -> J): IMMap<J, V> where J: Any, J: Comparable<@UnsafeVariance J>
    fun <W: Any> fmapValues(f: (TKVEntry<K, V>) -> W): IMMap<K, W>
}

interface IMBTreeTransforming<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun <C, D: Any> fflatMap(f: (TKVEntry<A, B>) -> IMBTree<C, D>): IMBTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C>  // 	When working with sequences, it works like map followed by flatten
}

interface IMStackTransforming<out A: Any> {
    fun <B: Any> fpopMap(f: (A) -> B): Pair<B?, IMStack<A>> // Apply f to the top, pop, return the rest
    fun <B: Any> ftopMap(f: (A) -> B): B? // Apply f to the top
}

interface IMQueueTransforming<out A: Any> {
    fun <B: Any> fdequeueMap(f: (A) -> B): Pair<B?, IMQueue<A>> // Apply f to the top, pop, return the rest
    fun <B: Any> fpeekMap(f: (A) -> B): B? // Apply f to the top
}