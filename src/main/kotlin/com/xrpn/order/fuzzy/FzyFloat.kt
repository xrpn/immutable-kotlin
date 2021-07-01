package com.xrpn.order.fuzzy

import com.xrpn.immutable.Fuzzy
import com.xrpn.immutable.TypeSafeFuzzyEquality
import com.xrpn.order.fuzzy.FzyFloatEquality.fFzyIsUnity
import com.xrpn.order.fuzzy.FzyFloatEquality.fFzyEqual
import com.xrpn.order.fuzzy.FzyFloatEquality.fzyEqual
import com.xrpn.order.fuzzy.FzyFloatEquality.fFzyIsZero

class FzyFloat(
    qty: Float,
    tol: Float = defaultFloatTol,
    defeatOk: Boolean = false,
) : Fuzzy<Float>(qty, tol), TypeSafeFuzzyEquality<Float> {

    init {
        if (tol < 0.0f) throw IllegalArgumentException("tol must be non-negative")
        if ((1.0f - tol == 1.0f + tol) && !defeatOk) throw IllegalArgumentException("tolerance $tol is unrepresentable")
    }

    override fun equals(other: Any?): Boolean = equalsImpl(other)
    override fun hashCode(): Int {
        var result = qty.hashCode()
        result = 31 * result + tol.hashCode()
        return result
    }
    override fun isZero() = fFzyIsZero(qty, tol)
    override fun isUnity() = fFzyIsUnity(qty, tol)
    override fun equal(rhs: Fuzzy<Float>): Boolean = fzyEqual(rhs as FzyFloat)
    private fun equalsImpl(other: Any?): Boolean =
        when {
            this === other -> true
            other == null -> false
            other is FzyFloat -> fzyEqual(other)
            other is Float -> fzyEqual(other)
            else -> false
        }

    companion object {
        fun zero(tol: Float = defaultFloatTol): FzyFloat = FzyFloat(0.0f, tol)
        fun unity(tol: Float = defaultFloatTol): FzyFloat = FzyFloat(1.0f, tol)
        fun Float.fzyIsZero() = fFzyIsZero(this)
        fun Float.fzyIsUnity() = fFzyIsUnity(this)
        fun Float.fzyEqual(rhs: Float, tol: Float = defaultFloatTol) = fFzyEqual(this, rhs, tol)
        fun Float.fzyEqual(rhs: FzyFloat) = fFzyEqual(this, rhs)
        fun Float.asFzyFloat(tol: Float = defaultFloatTol): FzyFloat = FzyFloat(this, tol)
    }
}