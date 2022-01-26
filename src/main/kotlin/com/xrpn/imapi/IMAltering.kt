package com.xrpn.imapi

import com.xrpn.immutable.*

interface IMWritable<A: Any> {
    fun fadd(src: A, dest: IMCommon<A>): IMCommon<A>?
}

interface IMOrderedWritable<A: Any>: IMWritable<A> {
    override fun fadd(src: A, dest: IMCommon<A>): IMOrdered<A>?
    fun faddOrdered(src: A, dest: IMOrdered<A>): IMOrdered<A>
    fun faddAll(src: IMCommon<A>, dest: IMOrdered<A>): IMOrdered<A> =
        src.ffold(dest) { imkv, tkv -> fadd(tkv, imkv)?.let { it } ?: imkv }
}

interface IMKeyedValueLogic<K, V: Any> where K: Any, K: Comparable<K> {
    fun fAND(src: IMKeyedValue<K, V>, origin: IMKeyedValue<K, V>): IMKeyedValue<K, V>
    fun fNOT(src: IMKeyedValue<K, V>, origin: IMKeyedValue<K, V>): IMKeyedValue<K, V>
    fun fOR(src: IMKeyedValue<K, V>, origin: IMKeyedValue<K, V>): IMKeyedValue<K, V>
    fun fXOR(src: IMKeyedValue<K, V>, origin: IMKeyedValue<K, V>): IMKeyedValue<K, V>
}

interface IMKeyedValueWritable<K, V: Any>  where K: Any, K: Comparable<K> {
    fun fadd(src: TKVEntry<K, V>, dest: IMKeyedValue<K, V>): IMKeyedValue<K, V>?
    fun faddAll(src: IMCommon<TKVEntry<K, V>>, dest: IMKeyedValue<K, V>): IMKeyedValue<K, V> =
        src.ffold(dest) { imkv, tkv -> fadd(tkv, imkv) ?: imkv }
}

interface IMListWritable<A: Any>: IMWritable<A> {
    override fun fadd(src: A, dest: IMCommon<A>): FList<A>? =
        (dest as? FList<A>)?.fappend(src)
    fun fappend(src: A, dest: IMList<A>): IMList<A>
    fun fappendAll(src: IMList<A>, dest: IMList<A>): IMList<A>
    fun fprepend(src: A, dest: IMList<A>): IMList<A>
    fun fprependAll(src: IMList<A>, dest: IMList<A>): IMList<A>
}

interface IMStackAltering<out A: Any> { // specific names
    fun fpop(): Pair<A?, IMStack<A>>
    fun fpopOrThrow(): Pair<A, IMStack<A>>
}

interface IMStackWritable<A: Any>: IMWritable<A> {
    override fun fadd(src: A, dest: IMCommon<A>): FStack<A>? =
        (dest as? FStack<A>)?.fpush(src)
    fun fpush(top: A, dest: IMStack<A>): IMStack<A>
}

interface IMQueueAltering<out A: Any> { // specific names
    fun fdequeue(): Pair<A?, IMQueue<A>>
    fun fdequeueOrThrow(): Pair<A, IMQueue<A>>
}

interface IMQueueWritable<A: Any>: IMWritable<A> {
    override fun fadd(src: A, dest: IMCommon<A>): IMQueue<A>? =
        (dest as? FQueue<A>)?.fenqueue(src)
    fun fenqueue(back: A, dest: IMQueue<A>): IMQueue<A>
}

interface IMSetLogic<A:Any>  {
    fun fAND(src: IMSet<A>, origin: IMSet<A>): IMSet<A>
    fun fNOT(src: IMSet<A>, origin: IMSet<A>): IMSet<A>
    fun fOR(src: IMSet<A>, origin: IMSet<A>): IMSet<A>
    fun fXOR(src: IMSet<A>, origin: IMSet<A>): IMSet<A>
}

interface IMSetWritable<A: Any>: IMWritable<A> { // }: IMWritable<A> {
    override fun fadd(src: A, dest: IMCommon<A>): IMSetNotEmpty<A>?
    fun faddUniq(src: A, dest: IMSet<A>): Pair<Boolean, IMSetNotEmpty<A>>
    fun faddUniqs(src: IMCommon<A>, dest: IMSet<A>): Pair<Int, IMSetNotEmpty<A>?>
//    fun <C> faddcUniq(src: C, dest: IMSet<C>): Pair<Boolean, IMSetNotEmpty<C>> where C: Comparable<A>
//    fun <C> faddcUniqs(src: IMCommon<C>, dest: IMSet<C>): Pair<Int, IMSetNotEmpty<C>?> where C: Comparable<A>
}

internal interface IMKSetLogic<K,A:Any> where K: Any, K: Comparable<K> {
    fun fAND(src: IMKeyedValue<K, A>, origin: IMKSet<K, A>): IMKSet<K,A>
    fun fNOT(src: IMKeyedValue<K, A>, origin: IMKSet<K, A>): IMKSet<K,A>
    fun fOR(src: IMKeyedValue<K, A>, origin: IMKSet<K, A>): IMKSet<K,A>
    fun fXOR(src: IMKeyedValue<K, A>, origin: IMKSet<K, A>): IMKSet<K,A>
}

internal interface IMKSetWritable<K,A:Any>: IMKeyedValueWritable<K,A> where K: Any, K: Comparable<K> {
    override fun fadd(src: TKVEntry<K,A>, dest: IMKeyedValue<K, A>): IMKSetNotEmpty<K, A>?
    fun faddUniq(src: TKVEntry<K,A>, dest: IMKSet<K,A>): Pair<Boolean, IMKSetNotEmpty<K, A>>
    fun faddUniqs(src: IMCommon<TKVEntry<K,A>>, dest: IMKSet<K,A>): Pair<Int, IMKSetNotEmpty<K, A>?>
    fun faddkUniq(src: TKVEntry<K,K>, dest: IMKSet<K,K>): Pair<Boolean, IMKSetNotEmpty<K,K>>
    fun faddkUniqs(src: IMCommon<TKVEntry<K,K>>, dest: IMKSet<K,K>): Pair<Int, IMKSetNotEmpty<K,K>?>
}

interface IMMapAltering<out K, out V: Any> /* : IMWritable<TKVEntry<K,V>> */ where K: Any, K: Comparable<@UnsafeVariance K> {
    fun fputkv(key: @UnsafeVariance K, value: @UnsafeVariance V): IMMap<K, V>
    fun fputPair(p: Pair<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
    fun fputList(l: FList<TKVEntry<@UnsafeVariance K, @UnsafeVariance V>>): IMMap<K, V>
    fun fputTree(t: IMBTree<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
    fun fputMap(m: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
//    override fun fadd(item: TKVEntry<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K,V>
}

interface IMBTreeLogic<A,B: Any>  where A: Any, A: Comparable<A> {
    fun fAND(src: IMKeyedValue<A, B>, origin: IMBTree<A, B>): IMBTree<A, B>
    fun fNOT(src: IMKeyedValue<A, B>, origin: IMBTree<A, B>): IMBTree<A, B>
    fun fOR(src: IMKeyedValue<A, B>, origin: IMBTree<A, B>): IMBTree<A, B>
    fun fXOR(src: IMKeyedValue<A, B>, origin: IMBTree<A, B>): IMBTree<A, B>
}

interface IMBTreeWritable<A,B:Any>: IMKeyedValueWritable<A,B> where A: Any, A: Comparable<A> {
    override fun fadd(src: TKVEntry<A,B>, dest: IMKeyedValue<A, B>): IMBTree<A, B>?
    fun fadd(src: TKVEntry<A, B>, dest: IMBTree<A, B>): IMBTree<A, B>
    fun faddAll(src: IMCommon<TKVEntry<A, B>>, dest: IMBTree<A, B>): IMBTree<A, B>
    fun finserts(src: IMKeyedValue<A, B>, dest: IMBTree<A, B>): IMBTree<A, B>
}