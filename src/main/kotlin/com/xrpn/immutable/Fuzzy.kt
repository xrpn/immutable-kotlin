package com.xrpn.immutable

interface TypeSafeFuzzyEquality<T: Any> {
    fun equal(rhs: Fuzzy<T>): Boolean
}

abstract class Fuzzy<out Q: Any>(val qty: Q, val tol: Q) {
    override fun toString() = "${this::class.simpleName}($qty, $tol):Fuzzy"
    override fun equals(other: Any?): Boolean = TODO() // marker, MUST be overridden
    override fun hashCode(): Int = TODO() // marker, MUST be overridden
    abstract fun isZero(): Boolean
    abstract fun isUnity(): Boolean
}