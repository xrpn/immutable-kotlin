package com.xrpn.imapi

import com.xrpn.immutable.TKVEntry

interface IMListGrouping<out A: Any> {

    fun fcount(isMatch: (A) -> Boolean): Int // count the element that match the predicate
    fun ffindFirst(isMatch: (A) -> Boolean): Triple< /* before */ IMList<A>, A?, /* after */ IMList<A>> // Split the list at first match
    fun <B> fgroupBy(f: (A) -> B): IMMap<B, IMList<A>> where B: Any, B: Comparable<B> //	A map of collections created by the function f
    fun findexed(offset: Int = 0): IMList<Pair<A, Int>> // Each and all element contained in a tuple along with its offset-based index
    fun fpartition(isMatch: (A) -> Boolean): Pair</* true */ IMList<A>, /* false */ IMList<A>> // Two collections created by the predicate p
    fun fslidingWindow(size: Int, step: Int): IMList<IMList<A>> // Group elements into fixed size blocks by passing a sliding window of size, with step
    fun fslidingFullWindow(size: Int, step: Int): IMList<IMList<A>> // Group elements into fixed size blocks by passing a sliding window of size, with step
    fun fsize(): Int // number of elements
    fun fsplitAt(index: Int): Triple< /* before */ IMList<A>, A?, /* after */ IMList<A>> // Split the list at index
    fun <B: Any, C: Any> funzip(f: (A) -> Pair<B,C>): Pair<IMList<B>, IMList<C>> // The opposite of zip, breaks a collection into two collections by dividing each element into two pieces; such as breaking up a list of Tuple2 elements
    fun <B: Any, C: Any> fzipWith(xs: IMList<B>, f: (A, B) -> C): IMList<C>
    fun <B: Any> fzipWhen(xs: IMList<B>, isMatch: (A, B) -> Boolean): IMList<Pair<A, B>> // zip items at all matching indices if isMatch, or skip that index
    fun <B: Any> fzipWhile(xs: IMList<B>, isMatch: (A, B) -> Boolean): IMList<Pair<A, B>> // zip items as long as for matching indices isMatch, then stop
    fun <B: Any> fzipWith(xs: Iterator<B>): IMList<Pair<A,B>> // A collection of pairs by matching the list with the elements of the iterator
    fun fzipWithIndex(): IMList<Pair<A, Int>> // Each and all element contained in a tuple along with its 0-based index
    fun fzipWithIndex(startIndex: Int): IMList<Pair<A, Int>> // A sublist of elements from startIndex contained in a tuple along with its 0-based index
}

interface IMSetGrouping<out A: Any> {

    fun fcombinations(size: Int): IMSet<IMSet<A>> // all groups of "size" members from this set; order does not matter
    fun fcount(isMatch: (A) -> Boolean): Int // count the element that match the predicate
    fun <B> fgroupBy(f: (A) -> B): IMMap<B, IMSet<A>> where B: Any, B: Comparable<B> //	A map of collections created by the function f
    fun findexed(offset: Int = 0): IMSet<Pair<A, Int>> // Each and all element contained in a tuple along with an offset-based index
    fun fpartition(isMatch: (A) -> Boolean): Pair</* true */ IMSet<A>, /* false */ IMSet<A>> // Two collections created by the predicate p
    fun fpermutations(size: Int): IMSet<IMList<A>> // all groups of "size" members from this set; order does matter
    fun fpopAndReminder(): Pair<A?, IMSet<A>>
    fun fsize(): Int // number of elements
}

interface IMBTreeGrouping<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A>{

    fun fcount(isMatch: (TKVEntry<A, B>) -> Boolean): Int // count the element that match the predicate
    fun <C> fgroupBy(f: (TKVEntry<A, B>) -> C): IMMap<C, IMBTree<A, B>> where C: Any, C: Comparable<C>//	A map of collections created by the function f
    fun fpartition(isMatch: (TKVEntry<A, B>) -> Boolean): Pair</* true */ IMBTree<A, B>, /* false */ IMBTree<A, B>> // Two collections created by the predicate p
    fun fpopAndReminder(): Pair<TKVEntry<A, B>?, IMBTree<A, B>>
    fun fmaxDepth(): Int
    fun fminDepth(): Int
    fun fsize(): Int // number of elements
}