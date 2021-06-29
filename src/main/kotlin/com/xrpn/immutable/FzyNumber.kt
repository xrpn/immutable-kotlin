package com.xrpn.immutable

abstract class Fuzzy<Q>(val qty: Q, val tol: Q): Comparable<Fuzzy<Q>> where Q: Comparable<Q> {
    override fun compareTo(other: Fuzzy<Q>): Int = when {
        this.equal(other) -> 0
        qty < other.qty -> -1
        else -> 1
    }
    override fun toString() = "${this::class.simpleName}($qty, $tol)"
    override fun equals(other: Any?): Boolean = TODO()
    override fun hashCode(): Int = TODO()
    abstract fun isZero(): Boolean
    abstract fun isUnity(): Boolean
    internal abstract fun equal(rhs: Fuzzy<Q>): Boolean
    internal abstract fun equal(rhs: Q): Boolean
}