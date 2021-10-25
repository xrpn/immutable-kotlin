package com.xrpn.imapi

/*


 */

interface IMListExtras<out A: Any> {
    operator fun plus(rhs: IMList<@UnsafeVariance A>): IMList<A>
    operator fun minus(rhs: IMList<@UnsafeVariance A>): IMList<A>
}

interface IMSetExtras<out A: Any> {
    operator fun contains(element: @UnsafeVariance A): Boolean

    infix fun or(rhs: IMSet<@UnsafeVariance A>): IMSet<A> = (this as IMSet<A>).fOR(rhs)
    infix fun and(rhs: IMSet<@UnsafeVariance A>): IMSet<A> = (this as IMSet<A>).fAND(rhs)
    infix fun xor(rhs: IMSet<@UnsafeVariance A>): IMSet<A> = (this as IMSet<A>).fXOR(rhs)
    infix fun not(rhs: IMSet<@UnsafeVariance A>): IMSet<A> = (this as IMSet<A>).fNOT(rhs)
}

interface IMKSetExtras<out K, out A: Any>: IMSetExtras<A> where K:Any, K:Comparable<@UnsafeVariance K> {
    operator fun set(k: @UnsafeVariance K, v: @UnsafeVariance A): IMSet<A>
}

interface IMMapExtras<out K, out V: Any> where K: Any, K: Comparable<@UnsafeVariance K> {

    operator fun contains(k: @UnsafeVariance K): Boolean
    operator fun set(k: @UnsafeVariance K, v: @UnsafeVariance V): IMMap<K, V>

    infix fun or(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fOR(rhs)
    infix fun and(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fAND(rhs)
    infix fun xor(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fXOR(rhs)
    infix fun not(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fNOT(rhs)
}

interface IMBTreeExtras<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {

    operator fun set(k: @UnsafeVariance A, v: @UnsafeVariance B): IMBTree<A, B>
    operator fun get(key: @UnsafeVariance A): B?

    infix fun or(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> = (this as IMBTree<A, B>).fOR(rhs)
    infix fun and(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> = (this as IMBTree<A, B>).fAND(rhs)
    infix fun xor(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> = (this as IMBTree<A, B>).fXOR(rhs)
    infix fun not(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> = (this as IMBTree<A, B>).fNOT(rhs)
}
