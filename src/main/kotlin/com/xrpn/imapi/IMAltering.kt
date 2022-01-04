package com.xrpn.imapi

import com.xrpn.immutable.*

interface IMWritable {
    fun <A: Any> fadd(src: A, dest: IMCommon<A>): IMCommon<A>?
}

interface IMOrderedWritable {
    fun <A: Any> fadd(src: A, dest: IMOrdered<A>): IMOrdered<A>?
}

interface IMOrderedAltering: IMOrderedWritable {
    override fun <A: Any> fadd(src: A, dest: IMOrdered<A>): IMOrdered<A>?
    fun <A: Any> faddAll(src: IMCommon<A>, dest: IMOrdered<A>): IMOrdered<A> =
        src.ffold(dest) { imkv, tkv -> fadd(tkv, imkv)?.let { it } ?: imkv }
}

interface IMKeyedValueWritable {
    fun <A, B: Any> fadd(src: TKVEntry<A,B>, dest: IMKeyedValue<A, B>): IMKeyedValue<A, B>? where A: Any, A: Comparable<A>
}

interface IMKeyedValueAltering: IMKeyedValueWritable {
    override fun <A, B: Any> fadd(src: TKVEntry<A, B>, dest: IMKeyedValue<A, B>): IMKeyedValue<A, B>? where A: Any, A: Comparable<A>
    fun <A, B: Any> faddAll(src: IMCommon<TKVEntry<A, B>>, dest: IMKeyedValue<A, B>): IMKeyedValue<A, B> where A: Any, A: Comparable<A> =
        src.ffold(dest) { imkv, tkv -> fadd(tkv, imkv)?.let { it } ?: imkv }
}

interface IMListWritable: IMWritable {
    override fun <A: Any> fadd(src: A, dest: IMCommon<A>): IMList<A>?
    fun <A: Any> fappend(src: A, dest: IMList<A>): IMList<A>?
    fun <A: Any> fappendAll(src: IMList<A>, dest: IMList<A>): IMList<A>?
    fun <A: Any> fprepend(src: A, dest: IMList<A>): IMList<A>?
    fun <A: Any> fprependAll(src: IMList<A>, dest: IMList<A>): IMList<A>?
}

interface IMStackAltering<out A: Any> {
    fun fpop(): Pair<A?, IMStack<A>>
    fun fpopOrThrow(): Pair<A, IMStack<A>>
}

interface IMStackWritable: IMWritable {
    override fun <A: Any> fadd(src: A, dest: IMCommon<A>): IMStack<A>?
    fun <A: Any> fpush(top: A, dest: IMStack<A>): IMStack<A>?
}

interface IMQueueAltering<out A: Any> {
    fun fdequeue(): Pair<A?, IMQueue<A>>
    fun fdequeueOrThrow(): Pair<A, IMQueue<A>>
}

interface IMQueueWritable: IMWritable {
    override fun <A: Any> fadd(src: A, dest: IMCommon<A>): IMQueue<A>?
    fun <A: Any> fenqueue(back: A, dest: IMQueue<A>): IMQueue<A>?
}

interface IMRSetAltering<out A: Any> { // }: IMWritable<A> {
    fun faddItem(item: @UnsafeVariance A): IMRSetNotEmpty<A>
//    override fun fadd(item: @UnsafeVariance A): IMSet<A> = @Suppress("UNCHECKED_CAST") (faddItem(item) as IMSet<A>)
}

interface IMSetAltering<out A: Any>: IMRSetAltering<A> {
    override fun faddItem(item: @UnsafeVariance A): IMRSetNotEmpty<A>
}

interface IMXSetAltering<out A>: IMRSetAltering<A> where A: Any, A: Comparable<@UnsafeVariance A> {
    override fun faddItem(item: @UnsafeVariance A): IMRSetNotEmpty<A>
}

internal interface IMKASetAltering<out K, out A: Any>: IMSetAltering<A> where K: Any, K: Comparable<@UnsafeVariance K>

internal interface IMKKSetAltering<out K>: IMSetAltering<K> where K: Any, K: Comparable<@UnsafeVariance K>

interface IMMapAltering<out K, out V: Any> /* : IMWritable<TKVEntry<K,V>> */ where K: Any, K: Comparable<@UnsafeVariance K> {
    fun fputkv(key: @UnsafeVariance K, value: @UnsafeVariance V): IMMap<K, V>
    fun fputPair(p: Pair<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
    fun fputList(l: FList<TKVEntry<@UnsafeVariance K, @UnsafeVariance V>>): IMMap<K, V>
    fun fputTree(t: IMBTree<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
    fun fputMap(m: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
//    override fun fadd(item: TKVEntry<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K,V>
}

interface IMBTreeAltering: IMKeyedValueWritable {
    override fun <A, B: Any> fadd(src: TKVEntry<A,B>, dest: IMKeyedValue<A, B>): IMBTree<A, B>? where A: Any, A: Comparable<A>
    fun <A, B: Any> fadd(src: TKVEntry<A, B>, dest: IMBTree<A, B>): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> faddAll(src: IMCommon<TKVEntry<A, B>>, dest: IMBTree<A, B>): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> finserts(src: IMKeyedValue<A, B>, dest: IMBTree<A, B>): IMBTree<A, B> where A: Any, A: Comparable<A>
}