package com.xrpn.imapi

import com.xrpn.immutable.FList.Companion.emptyIMList
import com.xrpn.immutable.TKVEntry

interface IMFoldable<out A: Any> {
    fun <R> ffold(z: R, f: (acc: R, A) -> R): R
}

interface IMListTransforming<out A: Any>: IMFoldable<A> {

    fun <B: Any> fflatMap(f: (A) -> IMList<B>): IMList<B>  // 	When working with sequences, it works like map followed by flatten
    fun <B> ffoldLeft(z: B, f: (acc: B, A) -> B): B // 	“Fold” the elements of the list using the binary operator o, using an initial seed s, going from left to right (see also reduceLeft)
    fun <B> ffoldRight(z: B, f: (A, acc: B) -> B): B // 	“Fold” the elements of the list using the binary operator o, using an initial seed s, going from right to left (see also reduceRight)
    fun <B: Any> fmap(f: (A) -> B): IMList<B> // 	Return a new sequence by applying the function f to each element in the List
    fun freduceLeft(f: (acc: A, A) -> @UnsafeVariance A): A? // 	“Reduce” the elements of the list using the binary operator o, going from left to right
    fun freduceRight(f: (A, acc: A) -> @UnsafeVariance A): A? // 	“Reduce” the elements of the list using the binary operator o, going from right to left
    fun freverse(): IMList<A>
    fun frotr(): IMList<A> // rotate right (A, B, C).frotr() becomes (C, A, B)
    fun frotl(): IMList<A> // rotate left (A, B, C).frotl() becomes (B, C, A)
    fun fswaph(): IMList<A> // swap head  (A, B, C).fswaph() becomes (B, A, C)

    override fun <R> ffold(z: R, f: (acc: R, A) -> R): R = ffoldLeft(z, f)
}

interface IMSetTransforming<out A: Any>: IMFoldable<A> {
    // fun <B: Any> ffold(z: B, f: (acc: B, A) -> B): B // 	“Fold” the elements of the list using the binary operator o, using an initial seed s, going from left to right (see also reduceLeft)
    fun <B: Any> fmapToList(f: (A) -> B): IMList<B> // 	Return a new sequence by applying the function f to each element in the List
    // since order is not a property of Set, f MUST be commutative
    fun freduce(f: (acc: A, A) -> @UnsafeVariance A): A? // 	“Reduce” the elements of the list using the binary operator o, going from left to right
    fun <B: Any> fflatMap(f: (A) -> IMSet<@UnsafeVariance B>): IMSet<B>  // 	When working with sequences, it works like map followed by flatten
    fun <B: Any> fmap(f: (A) -> B): IMSet<B> // 	Return a new sequence by applying the function f to each element in the List
}

interface IMRRSetTransforming<out A: Any> {
    fun <B> fflatMapKK(f: (A) -> IMSet<@UnsafeVariance B>): IMSet<B> where B: Any, B: Comparable<B>  // 	When working with sequences, it works like map followed by flatten
    fun <B> fmapKK(f: (A) -> B): IMSet<B> where B: Any, B: Comparable<B> // 	Return a new sequence by applying the function f to each element in the List
}

internal interface IMKSetTransforming<out K, out A: Any>: IMSetTransforming<A> where K: Any, K: Comparable<@UnsafeVariance K>

internal interface IMKKSetTransforming<out K>: IMSetTransforming<K>, IMRRSetTransforming<K> where K: Any, K: Comparable<@UnsafeVariance K>

interface IMMapTransforming<out K, out V: Any>: IMFoldable<TKVEntry<K,V>> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun <C, D: Any> fflatMap(f: (TKVEntry<K, V>) -> IMMap<C, D>): IMMap<C, D> where C: Any, C: Comparable<@UnsafeVariance C>
    fun <J> fmapKeys(f: (TKVEntry<K, V>) -> J): IMMap<J, V> where J: Any, J: Comparable<@UnsafeVariance J>
    fun <W: Any> fmapValues(f: (TKVEntry<K, V>) -> W): IMMap<K, W>
    // fun <C> ffold(z: C, f: (acc: C, TKVEntry<K, V>) -> C): C
    fun <C, D: Any> fmap(f: (TKVEntry<K, V>) -> TKVEntry<C, D>): IMMap<C, D> where C: Any, C: Comparable<@UnsafeVariance C> // 	Return a new sequence by applying the function f to each element in the List
    fun <T: Any> fmapToList(f: (TKVEntry<K, V>) -> T): IMList<T> = // 	Return a new sequence by applying the function f to each element in the List
        this.ffold(emptyIMList()) { acc, tkv -> acc.fprepend(f(tkv)) }
    fun <W: Any> fmapvToList(f: (V) -> W): IMList<W> = // 	Return a new sequence by applying the function f to each element in the List
        this.ffold(emptyIMList()) { acc, tkv -> acc.fprepend(f(tkv.getv())) }
    fun freducev(f: (acc: V, V) -> @UnsafeVariance V): V?
}

interface IMBTreeTransforming<out A, out B: Any>: IMFoldable<TKVEntry<A,B>> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun <C, D: Any> fflatMap(f: (TKVEntry<A, B>) -> IMBTree<C, D>): IMBTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C>  // 	When working with sequences, it works like map followed by flatten
    fun <C, D: Any> fflatMapDup(allowDups: Boolean, f: (TKVEntry<A, B>) -> IMBTree<C, D>): IMBTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C>  // 	When working with sequences, it works like map followed by flatten    // since order is an ambiguous property of Set, f SHOULD be commutative
    // since order is an ambiguous property of Tree, f SHOULD be commutative
    // fun <C> ffold(z: C, f: (acc: C, TKVEntry<A, B>) -> C): C // 	“Fold” the value of the tree using the binary operator o, using an initial seed s, going from left to right (see also reduceLeft)
    // since order is an ambiguous property of Tree, f SHOULD be commutative
    fun <C> ffoldv(z: C, f: (acc: C, B) -> C): C = // 	“Fold” the value of the tree using the binary operator o, using an initial seed s, going from left to right (see also reduceLeft)
        this.ffold(z) { acc, tkv -> f(acc, tkv.getv()) }
    fun <C, D: Any> fmap(f: (TKVEntry<A, B>) -> TKVEntry<C, D>): IMBTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C> // 	Return a new sequence by applying the function f to each element in the List
    fun <C, D: Any> fmapDup(allowDups: Boolean, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): IMBTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C> // 	Return a new sequence by applying the function f to each element in the List
    fun <T: Any> fmapToList(f: (TKVEntry<A, B>) -> T): IMList<T> = // 	Return a new sequence by applying the function f to each element in the List
        this.ffold(emptyIMList()) { acc, tkv -> acc.fprepend(f(tkv)) }
    fun <C: Any> fmapvToList(f: (B) -> C): IMList<C> = // 	Return a new sequence by applying the function f to each element in the List
        this.ffold(emptyIMList()) { acc, tkv -> acc.fprepend(f(tkv.getv())) }
    // since order is an ambiguous property of Tree, f SHOULD be commutative
    fun freduce(f: (acc: TKVEntry<A,B>, TKVEntry<A,B>) -> TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): TKVEntry<A,B>? // 	“Reduce” the elements of the list using the binary operator o, going from left to right
}

interface IMStackTransforming<out A: Any>: IMFoldable<A> {

    fun <B: Any> fpopMap(f: (A) -> B): Pair<B?, IMStack<A>> // Apply f to the top, pop, return the rest
    fun freverse(): IMStack<A>
    fun <B: Any> ftopMap(f: (A) -> B): B? // Apply f to the top
}

interface IMQueueTransforming<out A: Any>: IMFoldable<A> {

    fun <B: Any> fdequeueMap(f: (A) -> B): Pair<B?, IMQueue<A>> // Apply f to the top, pop, return the rest
    fun freverse(): IMQueue<A>
    fun <B: Any> fpeekMap(f: (A) -> B): B? // Apply f to the top
}