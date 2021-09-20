package com.xrpn.imapi

import com.xrpn.immutable.TKVEntry

interface IMListAltering<out A: Any> {
    fun fappend(item: @UnsafeVariance A): IMList<A>
    fun fappendAll(elements: IMList<@UnsafeVariance A>): IMList<A>
    fun fprepend(item: @UnsafeVariance A): IMList<A>
    fun fprependAll(elements: IMList<@UnsafeVariance A>): IMList<A>
    operator fun plus(rhs: IMList<@UnsafeVariance A>): IMList<A>
    operator fun minus(rhs: IMList<@UnsafeVariance A>): IMList<A>
}

interface IMSetAltering<out K, out A: Any> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun faddSoO(item: IMSetOfOne<@UnsafeVariance K, @UnsafeVariance A>): IMSetNotEmpty<K, A>
}

interface IMSetNEAltering<out K, out A: Any> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun faddItem(item: @UnsafeVariance A): IMSetNotEmpty<K, A>
}

interface IMBTreeAltering<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun finsert(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B>
    fun finsertDup(item: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>, allowDups: Boolean): IMBTree<A, B>
    fun finserts(items: IMList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>): IMBTree<A, B>
    fun finsertt(items: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> =
        items.ffold(this as IMBTree<A, B>) { stub, tkv ->
            stub.finsert(tkv)
        }
    fun finsertsDup(items: IMList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>, allowDups: Boolean): IMBTree<A, B>
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