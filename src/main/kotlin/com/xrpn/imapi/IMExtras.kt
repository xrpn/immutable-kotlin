package com.xrpn.imapi

import com.xrpn.immutable.FKMap

/*


 */

interface IMListExtras<out A: Any> {
    operator fun plus(rhs: IMList<@UnsafeVariance A>): IMList<A>
    operator fun minus(rhs: IMList<@UnsafeVariance A>): IMList<A>
}

interface IMRSetExtras<out A: Any> {
    infix fun or(rhs: IMRSet<@UnsafeVariance A>): IMRSet<A> = (this as IMRSet<A>).fOR(rhs)
    infix fun and(rhs: IMRSet<@UnsafeVariance A>): IMRSet<A> = (this as IMRSet<A>).fAND(rhs)
    infix fun xor(rhs: IMRSet<@UnsafeVariance A>): IMRSet<A> = (this as IMRSet<A>).fXOR(rhs)
    infix fun not(rhs: IMRSet<@UnsafeVariance A>): IMRSet<A> = (this as IMRSet<A>).fNOT(rhs)
}

interface IMSetExtras<out K, out A: Any>: IMRSetExtras<A> where K:Any, K:Comparable<@UnsafeVariance K>

interface IMMapExtras<out K, out V: Any> where K: Any, K: Comparable<@UnsafeVariance K> {

    operator fun contains(k: @UnsafeVariance K): Boolean
    operator fun set(k: @UnsafeVariance K, v: @UnsafeVariance V): IMMap<K, V>

//    infix fun or(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fOR(rhs)
//    infix fun and(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fAND(rhs)
//    infix fun xor(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fXOR(rhs)
    infix fun not(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fNOT(rhs)
}
