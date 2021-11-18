package com.xrpn.imapi

import com.xrpn.immutable.FQueue
import com.xrpn.immutable.TKVEntry

interface IMListTyping<out A: Any>:
    IMCommon<A>,
    IMFoldable<A>,
    IMOrdered<A>,
    IMMappable<A, IMList<A>>,
    IMMapplicable<A, IMList<A>> {
    // IMCollection
    override fun fdropAll(items: IMCommon<@UnsafeVariance A>): IMList<A>
    override fun fdropItem(item: @UnsafeVariance A): IMList<A>
    override fun fdropWhen(isMatch: (A) -> Boolean): IMList<A> = this.ffilterNot(isMatch)
    override fun ffilter(isMatch: (A) -> Boolean): IMList<A> // return all elements that match the predicate p
    override fun ffilterNot(isMatch: (A) -> Boolean): IMList<A> // Return all elements that do not match the predicate p
    override fun fpopAndRemainder(): Pair<A?, IMList<A>>
    // IMOrdered
    override fun fdrop(n: Int): IMList<A> // Return all elements after the first n elements
    override fun freverse(): IMList<A>
    override fun frotr(): IMList<A> // rotate right (A, B, C).frotr() becomes (C, A, B)
    override fun frotl(): IMList<A> // rotate left (A, B, C).frotl() becomes (B, C, A)
    override fun fswaph(): IMList<A> // swap head  (A, B, C).fswaph() becomes (B, A, C)
    // IMMappable
    override fun <B: Any> fmap(f: (A) -> B): IMList<B> // 	Return a new sequence by applying the function f to each element in the List
    override fun <B: Any> flift2map(item: IMCommon<B>): IMList<B>    // IMMapplicable
    // IMMapplicable
    override fun <B: Any> flift2maply(item: IMMappable<B, IMCommon<B>>): IMList<B>
}

interface IMSetTyping<out A: Any>:
    IMCommon<A>,
    IMMappable<A, IMSet<Nothing>> {
    // IMCollection
    override fun fdropAll(items: IMCommon<@UnsafeVariance A>): IMSet<A>
    override fun fdropItem(item: @UnsafeVariance A): IMSet<A>
    override fun fdropWhen(isMatch: (A) -> Boolean): IMSet<A> = this.ffilterNot(isMatch)
    override fun ffilter(isMatch: (A) -> Boolean): IMSet<A> // return all elements that match the predicate p
    override fun ffilterNot(isMatch: (A) -> Boolean): IMSet<A> // Return all elements that do not match the predicate p
    // IMMappable
    override fun <B: Any> fmap(f: (A) -> B): IMSet<B>
    override fun <B: Any> flift2map(item: IMCommon<B>): IMSet<B>    // IMMapplicable
}

internal interface IMKSetTyping<out K, out A: Any>: IMSetTyping<A>, IMKeyed<K>, IMKeyedValue<K,A> where K: Any, K: Comparable<@UnsafeVariance K> {
    // IMKeyed
    override fun asIMCollection(): IMCommon<*> = this
    override fun fdropKeys(keys: IMSet<@UnsafeVariance K>): IMKSet<K, A>
    override fun ffilterKey(isMatch: (K) -> Boolean): IMKSet<K,A>
    override fun ffilterKeyNot(isMatch: (K) -> Boolean): IMKSet<K,A>
    // IMKeyedValue
    override fun asIMBTree(): IMBTree<K,A>
    override fun asIMMap(): IMMap<K,A>
    override fun ffilterValue(isMatch: (A) -> Boolean): IMKSet<K,A>
    override fun ffilterValueNot(isMatch: (A) -> Boolean): IMKSet<K,A>
    override fun ffindAnyValue(isMatch: (A) -> Boolean): A?
    override fun fAND(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): IMKSet<K,A>
    override fun fNOT(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): IMKSet<K,A>
    override fun fOR(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): IMKSet<K,A>
    override fun fXOR(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): IMKSet<K,A>
}

interface IMMapTyping<out K, out V: Any>:
    IMCommon<TKVEntry<K,V>>,
    IMKeyed<K>, IMKeyedValue<K,V>,
    IMKMappable<K, V, IMMap<Nothing,Nothing>>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    // IMCollection
    override fun fdropAll(items: IMCommon<TKVEntry<@UnsafeVariance K, @UnsafeVariance V>>): IMMap<K, V>
    override fun fdropItem(item: TKVEntry<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
    override fun fdropWhen(isMatch: (TKVEntry<K, V>) -> Boolean): IMMap<K, V> = this.ffilterNot(isMatch)
    override fun ffilter(isMatch: (TKVEntry<K, V>) -> Boolean): IMMap<K, V> // return all elements that match the predicate p
    override fun ffilterNot(isMatch: (TKVEntry<K, V>) -> Boolean): IMMap<K, V> // Return all elements that do not match the predicate p
    // IMKeyed
    override fun asIMCollection(): IMCommon<*> = this
    override fun fdropKeys(keys: IMSet<@UnsafeVariance K>): IMMap<K,V>
    override fun ffilterKey(isMatch: (K) -> Boolean): IMMap<K,V>
    override fun ffilterKeyNot(isMatch: (K) -> Boolean): IMMap<K,V>
    override fun fpickKey(): K? = fpick()?.getk()  // peekk at one random key
    // IMKeyedValue
    override fun asIMBTree(): IMBTree<K,V>
    override fun ffilterValue(isMatch: (V) -> Boolean): IMMap<K,V>
    override fun ffilterValueNot(isMatch: (V) -> Boolean): IMMap<K,V>
    override fun fpickValue(): V? = fpick()?.getv()
    override fun fAND(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
    override fun fNOT(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
    override fun fOR(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
    override fun fXOR(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
    //IMKMappable
    override fun <C, D: Any> fmap(f: (TKVEntry<K,V>) -> TKVEntry<C,D>): IMMap<C,D> where C: Any, C: Comparable<@UnsafeVariance C>
}

interface IMBTreeTyping<out A, out B: Any>:
    IMCommon<TKVEntry<A,B>>,
    IMKeyed<A>,
    IMKeyedValue<A,B>,
    IMKMappable<A, B, IMBTree<Nothing,Nothing>>
        where A: Any, A: Comparable<@UnsafeVariance A> {
    // IMCollection
    override fun fdropAll(items: IMCommon<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): IMBTree<A,B>
    override fun fdropItem(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>
    override fun fdropWhen(isMatch: (TKVEntry<A, B>) -> Boolean): IMBTree<A,B> = this.ffilterNot(isMatch)
    override fun ffilter(isMatch: (TKVEntry<A, B>) -> Boolean): IMBTree<A,B> // return all elements that match the predicate p
    override fun ffilterNot(isMatch: (TKVEntry<A, B>) -> Boolean): IMBTree<A,B> // Return all elements that do not match the predicate p
    override fun fpopAndRemainder(): Pair<TKVEntry<A, B>?, IMBTree<A, B>>
    // IMKeyed
    override fun asIMCollection(): IMCommon<*> = this
    override fun fdropKeys(keys: IMSet<@UnsafeVariance A>): IMBTree<A,B>
    override fun ffilterKey(isMatch: (A) -> Boolean): IMBTree<A,B>
    override fun ffilterKeyNot(isMatch: (A) -> Boolean): IMBTree<A,B>
    override fun fpickKey(): A? = fpick()?.getk()
    // IMKeyedValue
    override fun asIMMap(): IMMap<A,B>
    override fun ffilterValue(isMatch: (B) -> Boolean): IMBTree<A,B>
    override fun ffilterValueNot(isMatch: (B) -> Boolean): IMBTree<A,B>
    override fun ffindAnyValue(isMatch: (B) -> Boolean): B?
    override fun fpickValue(): B? = fpick()?.getv()
    override fun fAND(items: IMKeyedValue<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>
    override fun fNOT(items: IMKeyedValue<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>
    override fun fOR(items: IMKeyedValue<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>
    override fun fXOR(items: IMKeyedValue<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>
    //IMKMappable
    override fun <C, D: Any> fmap(f: (TKVEntry<A,B>) -> TKVEntry<C,D>): IMBTree<C,D> where C: Any, C: Comparable<@UnsafeVariance C>
}

interface IMStackTyping<out A: Any>:
    IMCommon<A>,
    IMOrdered<A>,
    IMMappable<A, IMStack<Nothing>> {
    // IMCollection
    override fun fdropAll(items: IMCommon<@UnsafeVariance A>): IMStack<A>
    override fun fdropItem(item:  @UnsafeVariance A): IMStack<A>
    override fun fdropWhen(isMatch: (A) -> Boolean): IMStack<A> = this.ffilterNot(isMatch)
    override fun ffilter(isMatch: (A) -> Boolean): IMStack<A> // return all elements that match the predicate p
    override fun ffilterNot(isMatch: (A) -> Boolean): IMStack<A> // Return all elements that do not match the predicate p
    override fun fpopAndRemainder(): Pair<A?, IMStack<A>>
    // IMOrdered
    override fun fdrop(n: Int): IMStack<A> // Return all elements after the first n elements
    override fun freverse(): IMStack<A>
    override fun frotr(): IMStack<A> // rotate right (A, B, C).frotr() becomes (C, A, B)
    override fun frotl(): IMStack<A> // rotate left (A, B, C).frotl() becomes (B, C, A)
    override fun fswaph(): IMStack<A> // swap head  (A, B, C).fswaph() becomes (B, A, C)
    // IMMappable
    override fun <B: Any> fmap(f: (A) -> B): IMStack<B> // 	Return a new sequence by applying the function f to each element in the List
    override fun <B: Any> flift2map(item: IMCommon<B>): IMStack<B>    // IMMapplicable
}

interface IMQueueTyping<out A: Any>:
    IMCommon<A>,
    IMOrdered<A>,
    IMMappable<A, IMQueue<Nothing>>  {
    // IMCollection
    override fun fdropAll(items: IMCommon<@UnsafeVariance A>): FQueue<A>
    override fun fdropItem(item: @UnsafeVariance A): FQueue<A>
    override fun fdropWhen(isMatch: (A) -> Boolean): IMQueue<A> = this.ffilterNot(isMatch)
    override fun ffilter(isMatch: (A) -> Boolean): IMQueue<A> // return all elements that match the predicate p
    override fun ffilterNot(isMatch: (A) -> Boolean): IMQueue<A> // Return all elements that do not match the predicate p
    override fun fpopAndRemainder(): Pair<A?, IMQueue<A>>
    // IMOrdered
    override fun fdrop(n: Int): IMQueue<A> // Return all elements after the first n elements
    override fun freverse(): IMQueue<A>
    override fun frotr(): IMQueue<A> // rotate right (A, B, C).frotr() becomes (C, A, B)
    override fun frotl(): IMQueue<A> // rotate left (A, B, C).frotl() becomes (B, C, A)
    override fun fswaph(): IMQueue<A> // swap head  (A, B, C).fswaph() becomes (B, A, C)
    // IMMappable
    override fun <B: Any> fmap(f: (A) -> B): IMQueue<B> // 	Return a new sequence by applying the function f to each element in the List
    override fun <B: Any> flift2map(item: IMCommon<B>): IMQueue<B>    // IMMapplicable
}
