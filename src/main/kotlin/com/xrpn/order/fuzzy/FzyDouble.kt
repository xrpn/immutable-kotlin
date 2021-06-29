package com.xrpn.order.fuzzy

import com.xrpn.immutable.Fuzzy
import com.xrpn.order.fuzzy.FzyDoubleEquality.doubleFuzzyIsUnity
import com.xrpn.order.fuzzy.FzyDoubleEquality.doubleFuzzyIsZero
import com.xrpn.order.fuzzy.FzyDoubleEquality.doubleFzyEqual
import com.xrpn.order.fuzzy.FzyDoubleEquality.fuzzyDoubleIsUnity
import com.xrpn.order.fuzzy.FzyDoubleEquality.equal as dEqual
import com.xrpn.order.fuzzy.FzyDoubleEquality.fuzzyDoubleIsZero


class FzyDouble(
    qty: Double,
    tol: Double = defaultDoubleTol,
    defeat: Boolean = false,
) : Fuzzy<Double>(qty, tol) {

    init {
        if (tol < 0.0) throw IllegalArgumentException("tol must be non-negative")
        if ((1.0 - tol == 1.0 + tol) && !defeat) throw IllegalArgumentException("tolerance $tol is unrepresentable")
    }

    override fun equals(other: Any?): Boolean = equalsImpl(other)
    override fun hashCode(): Int {
        var result = qty.hashCode()
        result = 31 * result + tol.hashCode()
        return result
    }
    override fun isZero() = fuzzyDoubleIsZero(qty, tol)
    override fun isUnity() = fuzzyDoubleIsUnity(qty, tol)
    private fun equalsImpl(other: Any?): Boolean =
        when {
            this === other -> true
            other == null -> false
            other is FzyDouble -> other.equal(this)
            other is Double -> this.equal(other)
            else -> false
        }
    override fun equal(rhs: Fuzzy<Double>): Boolean = dEqual(rhs as FzyDouble)
    override fun equal(rhs: Double): Boolean = dEqual(rhs.asFzyDouble())

    companion object {
        fun zero(tol: Double = defaultDoubleTol): FzyDouble = FzyDouble(0.0, tol)
        fun unity(tol: Double = defaultDoubleTol): FzyDouble = FzyDouble(1.0, tol)
        fun Double.fzyIsZero() = doubleFuzzyIsZero(this)
        fun Double.fzyIsUnity() = doubleFuzzyIsUnity(this)
        fun Double.fzyEqual(rhs: Double, tol: Double = defaultDoubleTol) = doubleFzyEqual(this,rhs,tol)
        fun Double.fzyEqual(rhs: FzyDouble) = doubleFzyEqual(this,rhs)
        fun Double.asFzyDouble(tol: Double = defaultDoubleTol): FzyDouble = FzyDouble(this, tol)
    }
}