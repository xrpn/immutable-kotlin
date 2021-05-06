package com.xrpn.order.fuzzy

import com.xrpn.order.fuzzy.FzyDoubleEquality.doubleFuzzyZero
import com.xrpn.order.fuzzy.FzyDoubleEquality.doubleFzyEqual
import com.xrpn.order.fuzzy.FzyDoubleEquality.equal as dEqual
import com.xrpn.order.fuzzy.FzyDoubleEquality.fuzzyDoubleIsZero
import com.xrpn.immutable.FzyDbl


class FzyDouble(
    qty: Double,
    tol: Double = defaultDoubleTol,
) : FzyDbl(qty, tol) {

    init {
        if (tol < 0.0) throw IllegalArgumentException("tol must be non-negative")
    }

    fun isZero() = qty.isFinite() && !qty.isNaN() && fuzzyDoubleIsZero(qty, tol)

    private fun equalsImpl(other: Any?): Boolean =
        when {
            this === other -> true
            other == null -> false
            other is FzyDouble -> other.equal(this)
            other is Double -> doubleFzyEqual(other, this)
            else -> false
        }

    override fun equal(rhs: FzyDbl): Boolean = dEqual(rhs as FzyDouble)
    override fun equal(rhs: Double): Boolean = dEqual(rhs.asFzyDouble())

    override fun equals(other: Any?): Boolean = equalsImpl(other)

    override fun hashCode(): Int {
        var result = qty.hashCode()
        result = 31 * result + tol.hashCode()
        return result
    }

    companion object {

        fun Double.fzyIsZero() = doubleFuzzyZero(this)
        fun Double.fzyEqual(rhs: Double, tol: Double = defaultDoubleTol) = doubleFzyEqual(this,rhs,tol)
        fun Double.fzyEqual(rhs: FzyDouble) = doubleFzyEqual(this,rhs)
        fun Double.asFzyDouble(): FzyDouble = FzyDouble(this, defaultDoubleTol)
    }
}