package com.xrpn.imapi

import com.xrpn.immutable.FList
import com.xrpn.immutable.TKVEntry

interface IMListGrouping<out A: Any>: IMCollection<A> {

    fun ffindFirst(isMatch: (A) -> Boolean): Triple< /* before */ IMList<A>, A?, /* after */ IMList<A>> // Split the list at first match
    fun <B> fgroupBy(f: (A) -> B): IMMap<B, IMList<A>> where B: Any, B: Comparable<B> //	A map of collections created by the function f
    fun findexed(offset: Int = 0): IMList<Pair<A, Int>> // Each and all element contained in a tuple along with its offset-based index
    fun fpartition(isMatch: (A) -> Boolean): Pair</* true */ IMList<A>, /* false */ IMList<A>> // Two collections created by the predicate p
    fun fslidingWindow(size: Int, step: Int): IMList<IMList<A>> // Group elements into fixed size blocks by passing a sliding window of size, with step
    fun fslidingFullWindow(size: Int, step: Int): IMList<IMList<A>> // Group elements into fixed size blocks by passing a sliding window of size, with step
    fun fsplitAt(index: Int): Triple< /* before */ IMList<A>, A?, /* after */ IMList<A>> // Split the list at index
    fun <B: Any, C: Any> funzip(f: (A) -> Pair<B,C>): Pair<IMList<B>, IMList<C>> // The opposite of zip, breaks a collection into two collections by dividing each element into two pieces; such as breaking up a list of Tuple2 elements
    fun <B: Any, C: Any> fzipWith(xs: IMList<B>, f: (A, B) -> C): IMList<C>
    fun <B: Any> fzipWhen(xs: IMList<B>, isMatch: (A, B) -> Boolean): IMList<Pair<A, B>> // zip items at all matching indices if isMatch, or skip that index
    fun <B: Any> fzipWhile(xs: IMList<B>, isMatch: (A, B) -> Boolean): IMList<Pair<A, B>> // zip items as long as for matching indices isMatch, then stop
    fun <B: Any> fzipWith(xs: Iterator<B>): IMList<Pair<A,B>> // A collection of pairs by matching the list with the elements of the iterator
    fun fzipWithIndex(): IMList<Pair<A, Int>> // Each and all element contained in a tuple along with its 0-based index
    fun fzipWithIndex(startIndex: Int): IMList<Pair<A, Int>> // A sublist of elements from startIndex contained in a tuple along with its 0-based index
}

interface IMSetGrouping<out A: Any>: IMCollection<A> {
    fun <B: Any> fcartesian(rhs: IMSet<@UnsafeVariance B>): IMSet<Pair<A, B>> // cartesian product
    fun fcombinations(maxSize: Int): IMSet<IMSet<A>> // all unique, non-empty subsets up to "size" members from this set; order does not matter
    fun <B> fgroupBy(f: (A) -> B): IMMap<B, IMSet<@UnsafeVariance A>> where B: Any, B: Comparable<B> //	A map of collections created by the function f
    fun findexed(offset: Int = 0): IMSet<Pair<A, Int>> // Each and all element contained in a tuple along with an offset-based index
    fun fpartition(isMatch: (A) -> Boolean): Pair</* true */ IMSet<A>, /* false */ IMSet<A>> // Two collections created by the predicate p
    // Collection is a set (small(er) size) or a list (large(r) size)
    fun fpermutations(maxSize: Int): Collection<IMList<A>> // all unique, non-empty collections of "size" members from this set, caution suggested, O(size!) algorithm.
    // Collection is a set (small(er) size -- less than 9) or a list (large(r) size -- 9 through 12)
    fun fpermute(): Collection<IMList<A>> // the permutations of this (whole) set; there are n! of them, caution suggested, O(size!) algorithm.

    override fun fpopAndRemainder(): Pair<A?, IMSet<A>>
}

internal interface IMKSetGrouping<out K, out A: Any>: IMSetGrouping<A> where K: Any, K: Comparable<@UnsafeVariance K>

interface IMMapGrouping<out K, out V: Any>: IMCollection<TKVEntry<K, V>> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun fentries(): IMSet<TKVEntry<K,V>>
    fun fkeys(): IMSet<K>
    fun <R: Comparable<R>> maxBy(f: (V) -> R): TKVEntry<K, V>?
    fun <R: Comparable<R>> maxOf(f: (V) -> R): R? = maxBy(f)?.let { f(it.getv())}
    fun <R: Comparable<R>> minBy(f: (V) -> R): TKVEntry<K, V>?
    fun <R: Comparable<R>> minOf(f: (V) -> R): R? = minBy(f)?.let { f(it.getv())}
    fun fpartition(isMatch: (TKVEntry<K, V>) -> Boolean): Pair</* true */ IMMap<K, V>, /* false */ IMMap<K, V>> // Two collections created by the predicate p
    fun fvalues(): FList<V>

    override fun fpopAndRemainder(): Pair<TKVEntry<K, V>?, IMMap<K, V>>
}

interface IMBTreeGrouping<out A, out B: Any>: IMCollection<TKVEntry<A, B>> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun <C> fgroupBy(f: (TKVEntry<A, B>) -> C): IMMap<C, IMBTree<A, B>> where C: Any, C: Comparable<C>//	A map of collections created by the function f
    fun fpartition(isMatch: (TKVEntry<A, B>) -> Boolean): Pair</* true */ IMBTree<A, B>, /* false */ IMBTree<A, B>> // Two collections created by the predicate p
    fun fmaxDepth(): Int
    fun fminDepth(): Int
}

interface IMStackGrouping<out A: Any>: IMCollection<A>

interface IMQueueGrouping<out A: Any>: IMCollection<A>
