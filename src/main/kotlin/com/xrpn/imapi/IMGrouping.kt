package com.xrpn.imapi

import com.xrpn.immutable.FList

interface IMListGrouping<out A: Any> {
    fun fcount(isMatch: (A) -> Boolean): Int // count the element that match the predicate
    fun fenumerate(offset: Int = 0): IMList<Pair<A, Int>> // Each and all element contained in a tuple along with its offset-based index
    fun ffindFirst(isMatch: (A) -> Boolean): Triple< /* before */ IMList<A>, A?, /* after */ IMList<A>> // Split the list at first match
    fun <B> fgroupBy(f: (A) -> B): Map<B, IMList<A>> //	A map of collections created by the function f
    fun fpartition(isMatch: (A) -> Boolean): Pair</* true */ IMList<A>, /* false */ IMList<A>> // Two collections created by the predicate p
    fun fslidingWindow(size: Int, step: Int): IMList<IMList<A>> // Group elements into fixed size blocks by passing a sliding window of size, with step
    fun fslidingFullWindow(size: Int, step: Int): IMList<IMList<A>> // Group elements into fixed size blocks by passing a sliding window of size, with step
    fun fsplitAt(index: Int): Triple< /* before */ IMList<A>, A?, /* after */ IMList<A>> // Split the list at index
    fun <B: Any, C: Any> funzip(f: (A) -> Pair<B,C>): Pair<IMList<B>, IMList<C>> // The opposite of zip, breaks a collection into two collections by dividing each element into two pieces; such as breaking up a list of Tuple2 elements
    fun <B: Any, C: Any> fzipWith(xs: IMList<B>, f: (A, B) -> C): IMList<C>
    fun <B: Any> fzipWith(xs: Iterator<B>): FList<Pair<A,B>> // A collection of pairs by matching the list with the elements of the iterator
    fun fzipWithIndex(): IMList<Pair<A, Int>> // Each and all element contained in a tuple along with its 0-based index
    fun fzipWithIndex(startIndex: Int): IMList<Pair<A, Int>> // A sublist of elements from startIndex contained in a tuple along with its 0-based index
}