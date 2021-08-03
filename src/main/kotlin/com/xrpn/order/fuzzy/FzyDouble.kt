package com.xrpn.order.fuzzy

import com.xrpn.immutable.Fuzzy
import com.xrpn.immutable.TypeSafeFuzzyEquality
import com.xrpn.order.fuzzy.FzyDoubleEquality.dFzyIsUnity
import com.xrpn.order.fuzzy.FzyDoubleEquality.dFzyIsZero
import com.xrpn.order.fuzzy.FzyDoubleEquality.dFzyEqual
import com.xrpn.order.fuzzy.FzyDoubleEquality.fzyEqual

class FzyDouble(
    qty: Double,
    tol: Double = defaultDoubleTol,
    defeatOk: Boolean = false,
) : Fuzzy<Double>(qty, tol), TypeSafeFuzzyEquality<Double> {

    init {
        if (tol < 0.0) throw IllegalArgumentException("tol must be non-negative")
        if ((1.0 - tol == 1.0 + tol) && !defeatOk) throw IllegalArgumentException("tolerance $tol is unrepresentable")
    }

    override fun equals(other: Any?): Boolean = equalsImpl(other)
    override fun hashCode(): Int {
        var result = qty.hashCode()
        result = 31 * result + tol.hashCode()
        return result
    }
    override fun isZero() = dFzyIsZero(qty, tol)
    override fun isUnity() = dFzyIsUnity(qty, tol)
    override fun equal(rhs: Fuzzy<Double>): Boolean = fzyEqual(rhs as FzyDouble)
    private fun equalsImpl(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is FzyDouble -> fzyEqual(other)
        other is Double -> fzyEqual(other)
        else -> false
    }

    companion object {
        fun zero(tol: Double = defaultDoubleTol): FzyDouble = FzyDouble(0.0, tol)
        fun unity(tol: Double = defaultDoubleTol): FzyDouble = FzyDouble(1.0, tol)
        fun Double.fzyIsZero() = dFzyIsZero(this)
        fun Double.fzyIsUnity() = dFzyIsUnity(this)
        fun Double.fzyEqual(rhs: Double, tol: Double = defaultDoubleTol) = dFzyEqual(this,rhs,tol)
        fun Double.fzyEqual(rhs: FzyDouble) = dFzyEqual(this,rhs)
        fun Double.asFzyDouble(tol: Double = defaultDoubleTol): FzyDouble = FzyDouble(this, tol)
    }
}