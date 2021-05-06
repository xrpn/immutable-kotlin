package com.xrpn.order.fuzzy

import com.xrpn.order.fuzzy.FzyFloatEquality.fuzzyFloatIsZero
import com.xrpn.order.fuzzy.FzyFloatEquality.floatFzyEqual
import com.xrpn.order.fuzzy.FzyFloatEquality.equal as fEqual
import com.xrpn.order.fuzzy.FzyFloatEquality.floatFuzzyZero
import com.xrpn.immutable.FzyFlt

class FzyFloat(
    qty: Float,
    tol: Float = defaultFloatTol,
) : FzyFlt(qty, tol) {

    init {
        if (tol < 0.0f) throw IllegalArgumentException("tol must be non-negative")
    }

    fun isZero() = qty.isFinite() && !qty.isNaN() && fuzzyFloatIsZero(qty, tol)

    private fun equalsImpl(other: Any?): Boolean =
        when {
            this === other -> true
            other == null -> false
            other is FzyFloat -> other.equal(this)
            other is Float -> floatFzyEqual(other, this)
            else -> false
        }

    override fun equal(rhs: FzyFlt): Boolean = fEqual(rhs as FzyFloat)
    override fun equal(rhs: Float): Boolean = fEqual(rhs.asFzyFloat())

    override fun equals(other: Any?): Boolean = equalsImpl(other)

    override fun hashCode(): Int {
        var result = qty.hashCode()
        result = 31 * result + tol.hashCode()
        return result
    }

    companion object {

        fun Float.fzyIsZero() = floatFuzzyZero(this)
        fun Float.fzyEqual(rhs: Float, tol: Float = defaultFloatTol) = floatFzyEqual(this, rhs, tol)
        fun Float.fzyEqual(rhs: FzyFloat) = floatFzyEqual(this, rhs)
        fun Float.asFzyFloat(): FzyFloat = FzyFloat(this, defaultFloatTol)
    }
}


