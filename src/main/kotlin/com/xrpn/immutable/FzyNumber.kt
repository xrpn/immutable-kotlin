package com.xrpn.immutable

abstract class Fuzzy<out Q>(val qty: Q, val tol: Q) {
    override fun toString() = "${this::class.simpleName}($qty, $tol):Fuzzy"
    override fun equals(other: Any?): Boolean = TODO()
    override fun hashCode(): Int = TODO()
    abstract fun isZero(): Boolean
    abstract fun isUnity(): Boolean
}