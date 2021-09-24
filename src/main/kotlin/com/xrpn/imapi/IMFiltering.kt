package com.xrpn.imapi

import com.xrpn.immutable.TKVEntry

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
    fun ffind(isMatch: (A) -> Boolean): A? // Return the first element that matches the predicate p
    fun ffindLast(isMatch: (A) -> Boolean): A? // Return the last element that matches the predicate p
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

interface IMRSetFiltering<out A: Any> {
    fun fcontains(item: @UnsafeVariance A): Boolean
    fun fcontainsAny(items: IMRSet<@UnsafeVariance A>): Boolean
    fun fdropItem(item: @UnsafeVariance A): IMRSet<A>
    fun fdropAll(items: IMRSet<@UnsafeVariance A>): IMRSet<A>
    fun fdropWhen(isMatch: (A) -> Boolean): IMRSet<A> = this.ffilterNot(isMatch) // 	Drop all elements that match the predicate p
    fun fempty(): Boolean = fpick() == null
    fun ffilter(isMatch: (A) -> Boolean): IMRSet<A> // 	Return all elements that match the predicate p
    fun ffilterNot(isMatch: (A) -> Boolean): IMRSet<A> // 	Return all elements that do not match the predicate p
    fun ffind(isMatch: (A) -> Boolean): A? // Return a unique element that matches the predicate p or null
    fun fisSubsetOf(rhs: IMRSet<@UnsafeVariance A>): Boolean
    fun fpick(): A? // peek at one random element
    fun fAND(items: IMRSet<@UnsafeVariance A>): IMRSet<A>
    fun fNOT(items: IMRSet<@UnsafeVariance A>): IMRSet<A> = fdropAll(items)
    fun fOR(items: IMRSet<@UnsafeVariance A>): IMRSet<A>
    fun fXOR(items: IMRSet<@UnsafeVariance A>): IMRSet<A>
}

internal interface IMSetFiltering<out K, out A: Any>: IMRSetFiltering<A> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun fpickKey(): K  // peek at one random key
    fun fkeyType(): RestrictedKeyType<K>  // peek at one random key
}

interface IMMapFiltering<out K, out V: Any> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun fcontains(key: @UnsafeVariance K): Boolean
    fun fdrop(key: @UnsafeVariance K): IMMap<K, V>
    fun fdropAll(keys: IMRSet<@UnsafeVariance K>): IMMap<K, V>
    fun fdropkv(key: @UnsafeVariance K, value: @UnsafeVariance V): IMMap<K, V>
    fun fget(key: @UnsafeVariance K): V?
    fun fgetOrElse(key: @UnsafeVariance K, default: () -> @UnsafeVariance V): V = fget(key) ?: default()
    fun fgetOrThrow(key: @UnsafeVariance K): V = fget(key) ?: throw NoSuchElementException("no value for key $key")
    fun fempty(): Boolean = fpick() == null
    fun fpick(): TKVEntry<K, V>?

    fun ffilterEntry(isMatch: (TKVEntry<K, V>) -> Boolean): IMMap<K, V>
    fun ffilterNotEntry(isMatch: (TKVEntry<K, V>) -> Boolean): IMMap<K, V>
    fun ffilter(isMatch: (K) -> Boolean): IMMap<K, V>
    fun ffilterNot(isMatch: (K) -> Boolean): IMMap<K, V>
    fun fAND(items: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
    fun fNOT(items: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = fdropAll(items.fkeys())
    fun fOR(items: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
    fun fXOR(items: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
}

interface IMBTreeFiltering<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun fcontains(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Boolean
    fun fcontainsKey(key: @UnsafeVariance A): Boolean
    fun fcontainsValue(value: @UnsafeVariance B): Boolean
    fun fdropAll(items: IMList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): IMBTree<A, B>
    fun fdropAlt(items: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> =
        items.ffold(this as IMBTree<A,B>)  { stub, tkv -> if (stub.fcontains(tkv)) stub.fdropItem(tkv) else stub }
    fun fdropItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>
    fun fdropItemAll(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>
    fun fdropWhen(isMatch: (TKVEntry<A, B>) -> Boolean): IMBTree<A, B> = this.ffilterNot(isMatch) // 	Drop all elements that match the predicate p
    fun fempty(): Boolean = froot() == null
    fun ffilter(isMatch: (TKVEntry<A, B>) -> Boolean): IMBTree<A, B> // 	Return all elements that match the predicate p
    fun ffilterNot(isMatch: (TKVEntry<A, B>) -> Boolean): IMBTree<A, B> // 	Return all elements that do not match the predicate p
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
    fun fpick(): TKVEntry<A, B>? // peek at one random element
    fun frightMost(): TKVEntry<A, B>?
    fun froot(): TKVEntry<A, B>?
}

interface IMStackFiltering<out A: Any> {
    fun fdrop(n: Int): IMStack<A> // Return all elements after the first n elements
    fun fdropTopWhen(isMatch: (A) -> Boolean): IMStack<A> // True if top matches the oredicate
    fun fdropIfTop(item: @UnsafeVariance A): IMStack<A>
    fun fdropWhile(isMatch: (A) -> Boolean): IMStack<A> // Drop the top elements that match the predicate
    fun ftopMatch(isMatch: (A) -> Boolean): Boolean // True if top matches the oredicate
    fun fempty(): Boolean = ftop() == null
    fun ftop(): A? // the top element
    fun ftopOrThrow(): A // the top element
}

interface IMQueueFiltering<out A: Any> {
    fun fdiscardFront(): IMQueue<A> // Return all elements after the first n elements
    fun fdropFront(n: Int): IMQueue<A> // Return all elements after the first n elements
    fun fdropFrontWhile(isMatch: (A) -> Boolean): IMQueue<A> // Drop the front elements that match the predicate
    fun fdropFrontWhen(isMatch: (A) -> Boolean): IMQueue<A> // True if top matches the oredicate
    fun fdropIfFront(item: @UnsafeVariance A): IMQueue<A>
    fun ffrontMatch(isMatch: (A) -> Boolean): Boolean // True if top matches the oredicate
    fun fdiscardBack(): IMQueue<A> // Return all elements after the first n elements
    fun fdropBack(n: Int): IMQueue<A> // Return all elements after the first n elements
    fun fdropBackWhen(isMatch: (A) -> Boolean): IMQueue<A> // True if top matches the oredicate
    fun fdropBackWhile(isMatch: (A) -> Boolean): IMQueue<A> // Drop the front elements that match the predicate
    fun fdropIfBack(item: @UnsafeVariance A): IMQueue<A>
    fun fbackMatch(isMatch: (A) -> Boolean): Boolean // True if top matches the oredicate
    fun fempty(): Boolean
    fun flast(): A? // the end element
    fun fpeek(): A? // the front element
    fun fpeekOrThrow(): A // the top element
}