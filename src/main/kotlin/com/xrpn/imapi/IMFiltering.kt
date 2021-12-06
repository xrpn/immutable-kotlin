package com.xrpn.imapi

import com.xrpn.immutable.TKVEntry

interface IMListFiltering<out A: Any> {

    fun fdropFirst(isMatch: (A) -> Boolean): IMList<A> // 	Drop the first element that matches the predicate p
    fun fdropRight(n: Int): IMList<A> //	Return all elements except the last n elements
    fun fdropWhile(isMatch: (A) -> Boolean): IMList<A> // 	Drop the first elements that match the predicate p

    fun ffind(isMatch: (A) -> Boolean): A? // Return the first element that matches the predicate p
    fun ffindLast(isMatch: (A) -> Boolean): A? // Return the last element that matches the predicate p
    fun fgetOrNull(ix: Int): A? // element at ix, null if bad ix
    fun fhasSubsequence(sub: IMList<@UnsafeVariance A>): Boolean
    fun fhead(): A? // 	Returns the first element as a nullable
    fun finit(): IMList<A> // All elements except the last one
    fun flast(): A? // 	The last element as a nullable
    fun fslice(
        fromIndex: Int,
        toIndex: Int
    ): /* [fromIndex, toIndex) */ IMList<A> // 	A sequence of elements from index f (from) to index u (until)

    fun fselect(atIxs: IMList<Int>): IMList<A> // 	A sequence of elements
    fun ftail(): IMList<A> // 	All elements after the first element
    fun ftake(n: Int): IMList<A> // 	The first n elements
    fun ftakeRight(n: Int): IMList<A> // 	The last n elements
    fun ftakeWhile(isMatch: (A) -> Boolean): IMList<A> // 	The first subset of elements that matches the predicate p
}

interface IMSetFiltering<out A: Any>: IMSetTyping<A> {
    fun fcontainsAny(items: IMSet<@UnsafeVariance A>): Boolean

    fun ffind(isMatch: (A) -> Boolean): A? // Return a unique element that matches the predicate p or null
    fun fisSubsetOf(rhs: IMSet<@UnsafeVariance A>): Boolean
}

internal interface IMKSetFiltering<out K, out A: Any> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun fkeyType(): RestrictedKeyType<K>  // peek at one random key
    fun fdropAllEntries(items: IMCommon<TKVEntry<@UnsafeVariance K, @UnsafeVariance A>>): IMKSet<K,A>
}

interface IMMapFiltering<out K, out V: Any> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun fdrop(key: @UnsafeVariance K): IMMap<K, V>
    fun fdropkv(key: @UnsafeVariance K, value: @UnsafeVariance V): IMMap<K, V>
}

interface IMBTreeFiltering<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun fdropAlt(items: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> =
        items.ffold(this as IMBTree<A,B>)  { stub, tkv -> if (stub.fcontains(tkv)) stub.fdropItem(tkv) else stub }
    fun fdropItemAll(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>
    fun ffind(isMatch: (TKVEntry<A, B>) -> Boolean): IMList<TKVEntry<A, B>> // Return the element that matches the predicate p
    fun ffindDistinct(isMatch: (TKVEntry<A, B>) -> Boolean): TKVEntry<A, B>? {  // Return the element that matches the predicate p
        val found = ffind(isMatch)
        return if (found.fempty() || 1 < found.fsize()) null else found.fhead()
    }
    fun ffindItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>?  // Returns the subtree rooted at item; may contains dups
    fun ffindKey(key: @UnsafeVariance A): IMBTree<A, B>?  // Returns the subtree rooted at key; may contains dups
    fun ffindLastItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>? // Returns the subtree rooted at item; no dups in it
    fun ffindLastKey(key: @UnsafeVariance A): IMBTree<A, B>?// Returns the subtree rooted at key; no dups in it
    fun ffindValueOfKey(key: @UnsafeVariance A): B? // Returns the value associated with key
    fun fleftMost(): TKVEntry<A, B>?
    fun fhasDups(): Boolean
    fun fisDup(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Boolean
    fun fparentOf(child: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>? // Returns the smallest super-tree of item; or the tree if item is root
    fun fpeek(): TKVEntry<A, B>?
    fun frestrictedKey(): RestrictedKeyType<A>?
    fun frightMost(): TKVEntry<A, B>?
    fun froot(): TKVEntry<A, B>?
}

interface IMStackFiltering<out A: Any> {
    fun fdropIfTop(item: @UnsafeVariance A): IMStack<A>
    fun fdropTopWhen(isMatch: (A) -> Boolean): IMStack<A> // True if top matches the oredicate
    fun fdropWhile(isMatch: (A) -> Boolean): IMStack<A> // Drop the top elements that match the predicate
    fun ftopMatch(isMatch: (A) -> Boolean): Boolean // True if top matches the oredicate
    fun ftop(): A? // the top element
    fun ftopOrThrow(): A // the top element
}

interface IMQueueFiltering<out A: Any> {
    fun fdiscardFront(): IMQueue<A> // Return all elements after the first element
    fun fdropFrontWhile(isMatch: (A) -> Boolean): IMQueue<A> // Drop the front elements that match the predicate
    fun fdropFrontWhen(isMatch: (A) -> Boolean): IMQueue<A> // True if top matches the oredicate
    fun fdropIfFront(item: @UnsafeVariance A): IMQueue<A>
    fun fdiscardBack(): IMQueue<A> // Return all elements after the first n elements
    fun fdropBack(n: Int): IMQueue<A> // Return all elements except the last n elements
    fun fdropBackWhen(isMatch: (A) -> Boolean): IMQueue<A> // True if top matches the oredicate
    fun fdropBackWhile(isMatch: (A) -> Boolean): IMQueue<A> // Drop the front elements that match the predicate
    fun fdropIfBack(item: @UnsafeVariance A): IMQueue<A>
    fun flast(): A? // the end element, if any
    fun ffirst(): A? // the top element, if any
    fun ffirstOrThrow(): A // the top element
    fun fpeek(): A? = ffirst()
}

interface IMDjFiltering<out L, out R> {
    fun left(): L?
    fun right(): R?
    fun isLeft(): Boolean
    fun isRight(): Boolean
    fun <C> bireduce(fl: (L) -> C, fr: (R) -> C): C
    fun <C: Any, D: Any> bimap(fl: (L) -> C, fr: (R) -> D): IMDj<C, D>

}