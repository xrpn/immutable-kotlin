package com.xrpn.imapi

import com.xrpn.immutable.TKVEntry

interface IMListTransforming<out A: Any> {

    fun <B: Any> fflatMap(f: (A) -> IMList<B>): IMList<B>  // 	When working with sequences, it works like map followed by flatten
    fun <B> ffoldLeft(z: B, f: (acc: B, A) -> B): B // 	“Fold” the elements of the list using the binary operator o, using an initial seed s, going from left to right (see also reduceLeft)
    fun <B> ffoldRight(z: B, f: (A, acc: B) -> B): B // 	“Fold” the elements of the list using the binary operator o, using an initial seed s, going from right to left (see also reduceRight)
    fun <B: Any> fmap(f: (A) -> B): IMList<B> // 	Return a new sequence by applying the function f to each element in the List
    fun freduceLeft(f: (acc: A, A) -> @UnsafeVariance A): A? // 	“Reduce” the elements of the list using the binary operator o, going from left to right
    fun freduceRight(f: (A, acc: A) -> @UnsafeVariance A): A? // 	“Reduce” the elements of the list using the binary operator o, going from right to left
    fun freverse(): IMList<A>
}

interface IMSetTransforming<out A: Any> {

    fun <B: Any> fflatMap(f: (A) -> IMSet<B>): IMSet<B>  // 	When working with sequences, it works like map followed by flatten
    // since order is not a property of Set, f MUST be commutative
    fun <B> ffold(z: B, f: (acc: B, A) -> B): B // 	“Fold” the elements of the list using the binary operator o, using an initial seed s, going from left to right (see also reduceLeft)
    fun <B: Any> fmap(f: (A) -> B): IMSet<B> // 	Return a new sequence by applying the function f to each element in the List
    fun <B: Any> fmapToList(f: (A) -> B): IMList<B> // 	Return a new sequence by applying the function f to each element in the List
    // since order is not a property of Set, f MUST be commutative
    fun freduce(f: (acc: A, A) -> @UnsafeVariance A): A? // 	“Reduce” the elements of the list using the binary operator o, going from left to right
}

interface IMBTreeTransforming<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {

    fun <C, D: Any> fflatMap(f: (TKVEntry<A, B>) -> IMBTree<C, D>): IMBTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C>  // 	When working with sequences, it works like map followed by flatten
    fun <C> ffold(z: C, f: (acc: C, TKVEntry<A, B>) -> C): C // 	“Fold” the value of the tree using the binary operator o, using an initial seed s, going from left to right (see also reduceLeft)
    fun <C> ffoldv(z: C, f: (acc: C, B) -> C): C // 	“Fold” the value of the tree using the binary operator o, using an initial seed s, going from left to right (see also reduceLeft)
    fun <C, D: Any> fmap(f: (TKVEntry<A, B>) -> TKVEntry<C, D>): IMBTree<C, D> where C: Any, C: Comparable<@UnsafeVariance C> // 	Return a new sequence by applying the function f to each element in the List
    fun <C, D: Any> fmapToList(f: (TKVEntry<A, B>) -> TKVEntry<C, D>): IMList<TKVEntry<C, D>> where C: Any, C: Comparable<@UnsafeVariance C> // 	Return a new sequence by applying the function f to each element in the List
    fun <C: Any> fmapv(f: (B) -> C): IMBTree<A, C>  // 	Return a new sequence by applying the function f to each element in the List
    fun <C: Any> fmapvToList(f: (B) -> C): IMList<C>  // 	Return a new sequence by applying the function f to each element in the List
    fun freduce(f: (acc: B, B) -> @UnsafeVariance B): B? // 	“Reduce” the elements of the list using the binary operator o, going from left to right
}