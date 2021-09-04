package com.xrpn.imapi

import com.xrpn.immutable.FSetOfOne
import com.xrpn.immutable.TKVEntry

interface IMListAltering<out A: Any> {
    fun fappend(item: @UnsafeVariance A): IMList<A>
    fun fappendAll(elements: IMList<@UnsafeVariance A>): IMList<A>
    fun fprepend(item: @UnsafeVariance A): IMList<A>
    fun fprependAll(elements: IMList<@UnsafeVariance A>): IMList<A>
    operator fun plus(rhs: IMList<@UnsafeVariance A>): IMList<A>
    operator fun minus(rhs: IMList<@UnsafeVariance A>): IMList<A>
}

interface IMSetAltering<out A: Any> {
    fun fadd(item: FSetOfOne<@UnsafeVariance A>): IMSet<A>
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