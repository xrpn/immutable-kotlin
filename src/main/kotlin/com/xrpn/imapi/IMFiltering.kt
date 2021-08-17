package com.xrpn.imapi

import com.xrpn.immutable.*

interface IMListFiltering<out A: Any> {

    // fun fdistinct(): IMSet<A> // 	Return a new sequence with no duplicate elements
    fun fdrop(n: Int): IMList<A> // 	Return all elements after the first n elements
    fun fdropAll(items: IMList<@UnsafeVariance A>): IMList<A>
    fun fdropItem(item: @UnsafeVariance A): IMList<A>
    fun fdropFirst(isMatch: (A) -> Boolean): IMList<A> // 	Drop the first element that matches the predicate p
    fun fdropRight(n: Int): IMList<A> //	Return all elements except the last n elements
    fun fdropWhen(isMatch: (A) -> Boolean): IMList<A> = this.ffilterNot(isMatch) // 	Drop all elements that match the predicate p
    fun fdropWhile(isMatch: (A) -> Boolean): IMList<A> // 	Drop the first elements that match the predicate p
    fun ffilter(isMatch: (A) -> Boolean): IMList<A> // 	Return all elements that match the predicate p
    fun ffilterNot(isMatch: (A) -> Boolean): IMList<A> // 	Return all elements that do not match the predicate p
    fun ffindFromLeft(isMatch: (A) -> Boolean): A? // Return the first element that matches the predicate p
    fun ffindFromRight(isMatch: (A) -> Boolean): A? // Return the last element that matches the predicate p
    fun fgetOrNull(ix: Int): A? // element at ix, null if bad ix
    fun fhasSubsequence(sub: IMList<@UnsafeVariance A>): Boolean
    fun fempty(): Boolean = fhead() == null
    fun fhead(): A? // 	Returns the first element as a nullable
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
    fun fcontains(item: @UnsafeVariance A): Boolean
    fun fdropItem(item: @UnsafeVariance A): IMSet<A>
    fun fdropAll(items: FSet<@UnsafeVariance A>): IMSet<A>
    fun fdropWhen(isMatch: (A) -> Boolean): IMSet<A> = this.ffilterNot(isMatch) // 	Drop all elements that match the predicate p
    fun fempty(): Boolean = fpick() == null
    fun ffilter(isMatch: (A) -> Boolean): IMSet<A> // 	Return all elements that match the predicate p
    fun ffilterNot(isMatch: (A) -> Boolean): IMSet<A> // 	Return all elements that do not match the predicate p
    fun ffind(isMatch: (A) -> Boolean): A? // Return a unique element that matches the predicate p or null
    fun fisSubsetOf(rhs: IMSet<@UnsafeVariance A>): Boolean
    fun fpick(): A? // peek at one random element
    fun fretainsOnly(items: IMSet<@UnsafeVariance A>): IMSet<A>
    fun fsymmetricDifference(items: IMSet<@UnsafeVariance A>): IMSet<A>
}

interface IMBTreeFiltering<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {

    fun fcontains(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Boolean
    fun fcontainsKey(key: @UnsafeVariance A): Boolean
    fun fdropItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>
    fun fdropItemAll(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>
    fun fdropAll(items: IMList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): IMBTree<A, B>

    fun fempty(): Boolean = froot() == null
    fun ffilter(isMatch: (TKVEntry<A, B>) -> Boolean): IMBTree<A, B> // 	Return all elements that match the predicate p
    fun ffilterNot(isMatch: (TKVEntry<A, B>) -> Boolean): IMBTree<A, B> // 	Return all elements that do not match the predicate p

    fun ffind(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>?  // Returns the subtree rooted at item; may contains dups
    fun ffindKey(key: @UnsafeVariance A): IMBTree<A, B>?  // Returns the subtree rooted at key; may contains dups
    fun ffindLast(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>? // Returns the subtree rooted at item; no dups in it
    fun ffindLastKey(key: @UnsafeVariance A): IMBTree<A, B>?// Returns the subtree rooted at key; no dups in it
    fun ffindValueOfKey(key: @UnsafeVariance A): B? // Returns the value associated with key
    fun fparentOf(child: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> // Returns the smallest super-tree of item; or the tree if item is root

    fun ffindDistinct(isMatch: (TKVEntry<A, B>) -> Boolean): TKVEntry<A, B>? {  // Return the element that matches the predicate p
        val found = ffind(isMatch)
        return if (found.fempty() || 1 < found.fsize()) null else found.fhead()
    }
    fun ffind(isMatch: (TKVEntry<A, B>) -> Boolean): IMList<TKVEntry<A, B>> // Return the element that matches the predicate p
    fun fleftMost(): TKVEntry<A, B>?
    fun fpick(): TKVEntry<A, B>? // peek at one random element
    fun frightMost(): TKVEntry<A, B>?
    fun froot(): TKVEntry<A, B>?
}