package com.xrpn.imapi

/*


 */

interface IMListExtras<out A: Any> {
    operator fun plus(rhs: IMList<@UnsafeVariance A>): IMList<A>
    operator fun minus(rhs: IMList<@UnsafeVariance A>): IMList<A>
}

interface IMSetExtras<out K, out A: Any> where K: Any, K: Comparable<@UnsafeVariance K> {
    infix fun or(rhs: IMSet<@UnsafeVariance K, @UnsafeVariance A>): IMSet<K, A> = (this as IMSet<K, A>).fOR(rhs)
    infix fun and(rhs: IMSet<@UnsafeVariance K, @UnsafeVariance A>): IMSet<K, A> = (this as IMSet<K, A>).fAND(rhs)
    infix fun xor(rhs: IMSet<@UnsafeVariance K, @UnsafeVariance A>): IMSet<K, A> = (this as IMSet<K, A>).fXOR(rhs)
    infix fun not(rhs: IMSet<@UnsafeVariance K, @UnsafeVariance A>): IMSet<K, A> = (this as IMSet<K, A>).fNOT(rhs)
}

