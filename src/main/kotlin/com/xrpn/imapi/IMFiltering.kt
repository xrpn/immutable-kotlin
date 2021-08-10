package com.xrpn.imapi

import com.xrpn.immutable.FSet
import com.xrpn.immutable.TKVEntry

interface IMListFiltering<out A: Any> {

    // fun fdistinct(): IMSet<A> // 	Return a new sequence with no duplicate elements
    fun fdrop(n: Int): IMList<A> // 	Return all elements after the first n elements
    fun fdropFirst(isMatch: (A) -> Boolean): IMList<A> // 	Drop the first element that matches the predicate p
    fun fdropRight(n: Int): IMList<A> //	Return all elements except the last n elements
    fun fdropWhen(isMatch: (A) -> Boolean): IMList<A> // 	Drop all elements that match the predicate p
    fun fdropWhile(isMatch: (A) -> Boolean): IMList<A> // 	Drop the first elements that match the predicate p
    fun ffilter(isMatch: (A) -> Boolean): IMList<A> // 	Return all elements that match the predicate p
    fun ffilterNot(isMatch: (A) -> Boolean): IMList<A> // 	Return all elements that do not match the predicate p
    fun ffindFromLeft(isMatch: (A) -> Boolean): A? // Return the first element that matches the predicate p
    fun ffindFromRight(isMatch: (A) -> Boolean): A? // Return the last element that matches the predicate p
    fun fgetOrNull(ix: Int): A? // element at ix, null if bad ix
    fun fhead(): A? // 	Returns the first element as a nullable
    fun fempty(): Boolean = (fhead()?.let { false } != false)
    fun finit(): IMList<A> // All elements except the last one
    fun flast(): A? // 	The last element as a nullable
    fun fslice(fromIndex: Int, toIndex: Int): /* [fromIndex, toIndex) */ IMList<A> // 	A sequence of elements from index f (from) to index u (until)
    fun fslice(atIxs: IMList<Int>): IMList<A> // 	A sequence of elements
    fun ftail(): IMList<A> // 	All elements after the first element
    fun ftake(n: Int): IMList<A> // 	The first n elements
    fun ftakeRight(n: Int): IMList<A> // 	The last n elements
    fun ftakeWhile(isMatch: (A) -> Boolean): IMList<A> // 	The first subset of elements that matches the predicate p
}

interface IMSetFiltering<out A: Any> {
    fun fpick(): A? // peek at one random element
    fun ffilter(isMatch: (A) -> Boolean): IMSet<A> // 	Return all elements that match the predicate p
    fun ffilterNot(isMatch: (A) -> Boolean): IMSet<A> // 	Return all elements that do not match the predicate p
    fun ffind(isMatch: (A) -> Boolean): A? // Return the element that matches the predicate p
    fun fempty(): Boolean = (fpick()?.let { false } != false)
}

interface IMTreeFiltering<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun fempty(): Boolean = (root()?.let { false } != false)
    fun root(): TKVEntry<A, B>?
    fun fpick(): TKVEntry<A, B>? // peek at one random element
    fun leftMost(): TKVEntry<A, B>?
    fun rightMost(): TKVEntry<A, B>?
    fun minDepth(): Int
    fun maxDepth(): Int
}