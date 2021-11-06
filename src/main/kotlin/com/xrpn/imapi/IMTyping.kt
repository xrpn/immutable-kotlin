package com.xrpn.imapi

import com.xrpn.immutable.FQueue
import com.xrpn.immutable.TKVEntry

interface IMListTyping<out A: Any>: IMCollection<A>, IMFoldable<A>, IMOrdered<A> {
    // IMCollection
    override fun fdropAll(items: IMCollection<@UnsafeVariance A>): IMList<A>
    override fun fdropItem(item: @UnsafeVariance A): IMList<A>
    override fun fdropWhen(isMatch: (A) -> Boolean): IMList<A> = this.ffilterNot(isMatch)
    override fun ffilter(isMatch: (A) -> Boolean): IMList<A> // return all elements that match the predicate p
    override fun ffilterNot(isMatch: (A) -> Boolean): IMList<A> // Return all elements that do not match the predicate p
    // IMOrdered
    override fun freverse(): IMList<A>
    override fun frotr(): IMList<A> // rotate right (A, B, C).frotr() becomes (C, A, B)
    override fun frotl(): IMList<A> // rotate left (A, B, C).frotl() becomes (B, C, A)
    override fun fswaph(): IMList<A> // swap head  (A, B, C).fswaph() becomes (B, A, C)
}

interface IMSetTyping<out A: Any>: IMCollection<A> {
    // IMCollection
    override fun fdropAll(items: IMCollection<@UnsafeVariance A>): IMSet<A>
    override fun fdropItem(item: @UnsafeVariance A): IMSet<A>
    override fun fdropWhen(isMatch: (A) -> Boolean): IMSet<A> = this.ffilterNot(isMatch)
    override fun ffilter(isMatch: (A) -> Boolean): IMSet<A> // return all elements that match the predicate p
    override fun ffilterNot(isMatch: (A) -> Boolean): IMSet<A> // Return all elements that do not match the predicate p
}

internal interface IMKSetTyping<out K, out A: Any>: IMSetTyping<A>, IMKeyed<K>, IMKeyedValue<K,A> where K: Any, K: Comparable<@UnsafeVariance K> {
    // IMKeyed
    override fun asIMCollection(): IMCollection<*> = this
    override fun ffilterKey(isMatch: (K) -> Boolean): IMKSet<K,A>
    override fun ffilterKeyNot(isMatch: (K) -> Boolean): IMKSet<K,A>
    // IMKeyedValue
    override fun asIMBTree(): IMBTree<K,A>
    override fun asIMMap(): IMMap<K,A>
    override fun ffilterValue(isMatch: (A) -> Boolean): IMKSet<K,A>
    override fun ffilterValueNot(isMatch: (A) -> Boolean): IMKSet<K,A>
    override fun ffindAnyValue(isMatch: (A) -> Boolean): A?
}

interface IMMapTyping<out K, out V: Any>: IMCollection<TKVEntry<K,V>>, IMKeyed<K>, IMKeyedValue<K,V> where K: Any, K: Comparable<@UnsafeVariance K> {
    // IMCollection
    override fun fdropAll(items: IMCollection<TKVEntry<@UnsafeVariance K, @UnsafeVariance V>>): IMMap<K, V>
    override fun fdropItem(item: TKVEntry<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
    override fun fdropWhen(isMatch: (TKVEntry<K, V>) -> Boolean): IMMap<K, V> = this.ffilterNot(isMatch)
    override fun ffilter(isMatch: (TKVEntry<K, V>) -> Boolean): IMMap<K, V> // return all elements that match the predicate p
    override fun ffilterNot(isMatch: (TKVEntry<K, V>) -> Boolean): IMMap<K, V> // Return all elements that do not match the predicate p
    // IMKeyed
    override fun asIMCollection(): IMCollection<*> = this
    override fun ffilterKey(isMatch: (K) -> Boolean): IMMap<K,V>
    override fun ffilterKeyNot(isMatch: (K) -> Boolean): IMMap<K,V>
    override fun fpickKey(): K? = fpick()?.getk()  // peekk at one random key
    // IMKeyedValue
    override fun asIMBTree(): IMBTree<K,V>
    override fun ffilterValue(isMatch: (V) -> Boolean): IMMap<K,V>
    override fun ffilterValueNot(isMatch: (V) -> Boolean): IMMap<K,V>
    override fun fpickValue(): V? = fpick()?.getv()
}

interface IMBTreeTyping<out A, out B: Any>: IMCollection<TKVEntry<A,B>>, IMKeyed<A>, IMKeyedValue<A,B> where A: Any, A: Comparable<@UnsafeVariance A> {
    // IMCollection
    override fun fdropAll(items: IMCollection<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): IMBTree<A,B>
    override fun fdropItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>
    override fun fdropWhen(isMatch: (TKVEntry<A, B>) -> Boolean): IMBTree<A,B> = this.ffilterNot(isMatch)
    override fun ffilter(isMatch: (TKVEntry<A, B>) -> Boolean): IMBTree<A,B> // return all elements that match the predicate p
    override fun ffilterNot(isMatch: (TKVEntry<A, B>) -> Boolean): IMBTree<A,B> // Return all elements that do not match the predicate p
    override fun fpopAndRemainder(): Pair<TKVEntry<A, B>?, IMBTree<A, B>>
    // IMKeyed
    override fun asIMCollection(): IMCollection<*> = this
    override fun ffilterKey(isMatch: (A) -> Boolean): IMBTree<A,B>
    override fun ffilterKeyNot(isMatch: (A) -> Boolean): IMBTree<A,B>
    override fun fpickKey(): A? = fpick()?.getk()
    // IMKeyedValue
    override fun asIMMap(): IMMap<A,B>
    override fun ffilterValue(isMatch: (B) -> Boolean): IMBTree<A,B>
    override fun ffilterValueNot(isMatch: (B) -> Boolean): IMBTree<A,B>
    override fun ffindAnyValue(isMatch: (B) -> Boolean): B?
    override fun fpickValue(): B? = fpick()?.getv()
}

interface IMStackTyping<out A: Any>: IMCollection<A>, IMOrdered<A> {
    // IMCollection
    override fun fdropAll(items: IMCollection<@UnsafeVariance A>): IMStack<A>
    override fun fdropItem(item:  @UnsafeVariance A): IMStack<A>
    override fun fdropWhen(isMatch: (A) -> Boolean): IMStack<A> = this.ffilterNot(isMatch)
    override fun ffilter(isMatch: (A) -> Boolean): IMStack<A> // return all elements that match the predicate p
    override fun ffilterNot(isMatch: (A) -> Boolean): IMStack<A> // Return all elements that do not match the predicate p
    // IMOrdered
    override fun freverse(): IMStack<A>
    override fun frotr(): IMStack<A> // rotate right (A, B, C).frotr() becomes (C, A, B)
    override fun frotl(): IMStack<A> // rotate left (A, B, C).frotl() becomes (B, C, A)
    override fun fswaph(): IMStack<A> // swap head  (A, B, C).fswaph() becomes (B, A, C)
}

interface IMQueueTyping<out A: Any>: IMCollection<A>, IMOrdered<A> {
    // IMCollection
    override fun fdropAll(items: IMCollection<@UnsafeVariance A>): FQueue<A>
    override fun fdropItem(item: @UnsafeVariance A): FQueue<A>
    override fun fdropWhen(isMatch: (A) -> Boolean): IMQueue<A> = this.ffilterNot(isMatch)
    override fun ffilter(isMatch: (A) -> Boolean): IMQueue<A> // return all elements that match the predicate p
    override fun ffilterNot(isMatch: (A) -> Boolean): IMQueue<A> // Return all elements that do not match the predicate p
    // IMOrdered
    override fun freverse(): IMQueue<A>
    override fun frotr(): IMQueue<A> // rotate right (A, B, C).frotr() becomes (C, A, B)
    override fun frotl(): IMQueue<A> // rotate left (A, B, C).frotl() becomes (B, C, A)
    override fun fswaph(): IMQueue<A> // swap head  (A, B, C).fswaph() becomes (B, A, C)
}
