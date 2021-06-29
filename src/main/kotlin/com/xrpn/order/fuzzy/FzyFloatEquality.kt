package com.xrpn.order.fuzzy

const val defaultFloatTol: Float = 1E-6f
const val floatEps: Float = Float.MIN_VALUE

internal object FzyFloatEquality {

    internal fun fuzzyFloatIsZero(
        fp1: Float,
        tol: Float = floatEps
    ): Boolean = -tol <= fp1 && fp1 <= tol

    internal fun fuzzyFloatIsUnity(
        fp1: Float,
        tol: Float = floatEps,
    ): Boolean = fuzzyFloatEq(fp1, 1.0f, tol)

    private fun fuzzyFloatEq(
        fp1: Float,
        fp2: Float,
        tol: Float,
        maxVal: Float = Float.MAX_VALUE,
        minPosVal: Float = floatEps
    ): Boolean {
        if (fp1 == fp2) return true // according to IEEE 754
        if (fp1.isNaN() || fp2.isNaN() || tol.isNaN()) return false
        if (!(fp1.isFinite() && fp2.isFinite() && tol.isFinite())) return false
        val fp1z = fuzzyFloatIsZero(fp1, tol)
        val fp2z = fuzzyFloatIsZero(fp2, tol)
        if (fp1z && fp2z) return true
        if (fp1z || fp2z) return false
        val afp1 = kotlin.math.abs(fp1)
        val afp2 = kotlin.math.abs(fp2)
        /* ratio must be unity or close enough to unity */
        val divOverflow: Boolean /* whoops! */ = afp1 < 1.0f && afp1 * maxVal < afp2
        return if (divOverflow) false else {
            val divUnderflow /* whoops! */ = 1.0f < fp1 && fp2 < fp1 * minPosVal
            if (divUnderflow) false else {
                // tolerances on unity ratio
                val diff = 1.0f - tol
                val sum = 1.0f + tol
                // check ratio
                val signedRatio = fp1 / fp2
                //
                // NEXT TWO LINES
                // < if open interval, <= if closed interval
                //
                val a = diff < signedRatio
                val b = signedRatio < sum
                a && b
            }
        }
    }

    internal fun floatFuzzyIsZero(f: Float) = f.isFinite() && !f.isNaN() && fuzzyFloatIsZero(f)

    internal fun floatFuzzyIsUnity(f: Float) = f.isFinite() && !f.isNaN() && fuzzyFloatIsUnity(f)

    internal fun floatFzyEqual(lhs: Float, rhs: Float, tol: Float = defaultFloatTol) = fuzzyFloatEq(lhs, rhs, tol)

    internal fun floatFzyEqual(lhs: Float, rhs: FzyFloat) = fuzzyFloatEq(lhs, rhs.qty, rhs.tol)

    internal fun FzyFloat.equal(rhs: Float): Boolean = fuzzyFloatEq(qty, rhs, tol)

    internal fun FzyFloat.equal(rhs: FzyFloat): Boolean {
        if (this === rhs) return true
        return if (this.tol.toBits() == rhs.tol.toBits()) fuzzyFloatEq(this.qty, rhs.qty, tol) else false
    }
}