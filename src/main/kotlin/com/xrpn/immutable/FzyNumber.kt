package com.xrpn.immutable

sealed class FzyNumber<out A : Number> {
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
}

abstract class FzyDbl(val qty: Double, val tol: Double): FzyNumber<Double>(), Comparable<FzyDbl> {
    override fun compareTo(other: FzyDbl): Int = when {
        this.equal(other) -> 0
        qty < other.qty -> -1
        else -> 1
    }
    internal abstract fun equal(rhs: FzyDbl): Boolean
    internal abstract fun equal(rhs: Double): Boolean
}

abstract class FzyFlt(val qty: Float, val tol: Float): FzyNumber<Float>(), Comparable<FzyFlt> {
    override fun compareTo(other: FzyFlt): Int = when {
        this.equal(other) -> 0
        qty < other.qty -> -1
        else -> 1
    }
    internal abstract fun equal(rhs: FzyFlt): Boolean
    internal abstract fun equal(rhs: Float): Boolean
}