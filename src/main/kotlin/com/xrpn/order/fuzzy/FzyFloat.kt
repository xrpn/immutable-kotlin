package com.xrpn.order.fuzzy

import com.xrpn.immutable.Fuzzy
import com.xrpn.order.fuzzy.FzyFloatEquality.floatFuzzyIsUnity
import com.xrpn.order.fuzzy.FzyFloatEquality.fuzzyFloatIsZero
import com.xrpn.order.fuzzy.FzyFloatEquality.floatFzyEqual
import com.xrpn.order.fuzzy.FzyFloatEquality.equal as fEqual
import com.xrpn.order.fuzzy.FzyFloatEquality.floatFuzzyIsZero
import com.xrpn.order.fuzzy.FzyFloatEquality.fuzzyFloatIsUnity

class FzyFloat(
    qty: Float,
    tol: Float = defaultFloatTol,
    defeatOk: Boolean = false,
) : Fuzzy<Float>(qty, tol) {

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
    override fun isZero() = fuzzyFloatIsZero(qty, tol)
    override fun isUnity() = fuzzyFloatIsUnity(qty, tol)
    private fun equalsImpl(other: Any?): Boolean =
        when {
            this === other -> true
            other == null -> false
            other is FzyFloat -> fEqual(other)
            other is Float -> fEqual(other)
            else -> false
        }

    companion object {
        fun zero(tol: Float = defaultFloatTol): FzyFloat = FzyFloat(0.0f, tol)
        fun unity(tol: Float = defaultFloatTol): FzyFloat = FzyFloat(1.0f, tol)
        fun Float.fzyIsZero() = floatFuzzyIsZero(this)
        fun Float.fzyIsUnity() = floatFuzzyIsUnity(this)
        fun Float.fzyEqual(rhs: Float, tol: Float = defaultFloatTol) = floatFzyEqual(this, rhs, tol)
        fun Float.fzyEqual(rhs: FzyFloat) = floatFzyEqual(this, rhs)
        fun Float.asFzyFloat(tol: Float = defaultFloatTol): FzyFloat = FzyFloat(this, tol)
    }
}


