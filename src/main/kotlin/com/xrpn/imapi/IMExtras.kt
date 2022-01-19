package com.xrpn.imapi

import com.xrpn.immutable.FKSet

interface IMListExtras<out A: Any> {
    operator fun plus(rhs: IMList<@UnsafeVariance A>): IMList<A>
    operator fun minus(rhs: IMList<@UnsafeVariance A>): IMList<A>
}

interface IMSetExtras<out A: Any> {

    operator fun contains(element: @UnsafeVariance A): Boolean

    infix fun or(rhs: IMSet<@UnsafeVariance A>): IMSet<A> {
        this as FKSet<*, A>
        @Suppress("UNCHECKED_CAST") (rhs as IMKeyedValue<Nothing, A>)
        return fOR(rhs)
    }

    infix fun and(rhs: IMSet<@UnsafeVariance A>): IMSet<A> {
        this as FKSet<*, A>
        @Suppress("UNCHECKED_CAST") (rhs as IMKeyedValue<Nothing, A>)
        return fAND(rhs)
    }

    infix fun xor(rhs: IMSet<@UnsafeVariance A>): IMSet<A> {
        this as FKSet<*, A>
        @Suppress("UNCHECKED_CAST") (rhs as IMKeyedValue<Nothing, A>)
        return fXOR(rhs)
    }

    infix fun not(rhs: IMSet<@UnsafeVariance A>): IMSet<A> {
        this as FKSet<*, A>
        @Suppress("UNCHECKED_CAST") (rhs as IMKeyedValue<Nothing, A>)
        return fNOT(rhs)
    }
}

internal interface IMKSetExtras<out K, out A: Any>: IMSetExtras<A> where K:Any, K:Comparable<@UnsafeVariance K> {

    operator fun set(k: @UnsafeVariance K, v: @UnsafeVariance A): IMSet<A>

    infix fun or(rhs: IMKSet<@UnsafeVariance K, @UnsafeVariance A>): IMSet<A> = (this as FKSet<K, A>).fOR(rhs)
    infix fun and(rhs: IMKSet<@UnsafeVariance K, @UnsafeVariance A>): IMSet<A> = (this as FKSet<K, A>).fAND(rhs)
    infix fun xor(rhs: IMKSet<@UnsafeVariance K, @UnsafeVariance A>): IMSet<A> = (this as FKSet<K, A>).fXOR(rhs)
    infix fun not(rhs: IMKSet<@UnsafeVariance K, @UnsafeVariance A>): IMSet<A> = (this as FKSet<K, A>).fNOT(rhs)

}

interface IMMapExtras<out K, out V: Any> where K: Any, K: Comparable<@UnsafeVariance K> {

    operator fun contains(k: @UnsafeVariance K): Boolean
    operator fun set(k: @UnsafeVariance K, v: @UnsafeVariance V): IMMap<K, V>
    operator fun get(key: @UnsafeVariance K): V?

    infix fun or(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fOR(rhs)
    infix fun and(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fAND(rhs)
    infix fun xor(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fXOR(rhs)
    infix fun not(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fNOT(rhs)
}

interface IMBTreeExtras<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {

    // TODO operator fun contains(k: @UnsafeVariance K): Boolean = fcontainsKey(k)
    operator fun set(k: @UnsafeVariance A, v: @UnsafeVariance B): IMBTree<A, B>
    operator fun get(key: @UnsafeVariance A): B?

    infix fun or(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> {
        this as IMBTree<A,B>
        return rhs.tibBTree<A,B>()?.fOR(this,rhs) ?: this
    }
    infix fun and(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> {
        this as IMBTree<A,B>
        return rhs.tibBTree<A,B>()?.fAND(this,rhs) ?: this
    }
    infix fun xor(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> {
        this as IMBTree<A,B>
        return rhs.tibBTree<A,B>()?.fXOR(this,rhs) ?: this
    }
    infix fun not(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> {
        this as IMBTree<A,B>
        return rhs.tibBTree<A,B>()?.fNOT(this,rhs) ?: this
    }
}
