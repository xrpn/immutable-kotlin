package com.xrpn.imapi

import com.xrpn.immutable.*

interface IMListAltering<out A: Any> {
    fun fappend(item: @UnsafeVariance A): IMList<A>
    fun fappendAll(elements: IMList<@UnsafeVariance A>): IMList<A>
    fun fprepend(item: @UnsafeVariance A): IMList<A>
    fun fprependAll(elements: IMList<@UnsafeVariance A>): IMList<A>
}

interface IMRSetAltering<out A: Any> {
    fun faddItem(item: @UnsafeVariance A): IMRSetNotEmpty<A>
}

interface IMSetAltering<out A: Any>: IMRSetAltering<A> {
    override fun faddItem(item: @UnsafeVariance A): IMRSetNotEmpty<A>
}

interface IMXSetAltering<out A>: IMRSetAltering<A> where A: Any, A: Comparable<@UnsafeVariance A> {
    override fun faddItem(item: @UnsafeVariance A): IMRSetNotEmpty<A>
}

internal interface IMKASetAltering<out K, out A: Any>: IMSetAltering<A> where K: Any, K: Comparable<@UnsafeVariance K>

internal interface IMKKSetAltering<out K>: IMSetAltering<K> where K: Any, K: Comparable<@UnsafeVariance K>

interface IMMapAltering<out K, out V: Any> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun fputkv(key: @UnsafeVariance K, value: @UnsafeVariance V): IMMap<K, V>
    fun fputPair(p: Pair<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
    fun fputList(l: FList<TKVEntry<@UnsafeVariance K, @UnsafeVariance V>>): IMMap<K, V>
    fun fputTree(t: IMBTree<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
    fun fputMap(m: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V>
}

interface IMBTreeAltering<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun finsert(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>
    fun finserts(items: IMList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): IMBTree<A, B>
    fun finsertt(items: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> =
        items.ffold(this as IMBTree<A, B>) { stub, tkv -> stub.finsert(tkv) }
}

interface IMStackAltering<out A: Any> {
    fun fpop(): Pair<A?, IMStack<A>>
    fun fpopOrThrow(): Pair<A, IMStack<A>>
    fun fpush(top: @UnsafeVariance A): IMStack<A>
}

interface IMQueueAltering<out A: Any> {
    fun fdequeue(): Pair<A?, IMQueue<A>>
    fun fdequeueOrThrow(): Pair<A, IMQueue<A>>
    fun fenqueue(back: @UnsafeVariance A): IMQueue<A>
}