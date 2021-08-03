package com.xrpn.order.fuzzy

const val defaultDoubleTol: Double = 1E-15
const val doubleEps: Double = Double.MIN_VALUE

// after
// https://adtmag.com/Articles/2000/03/16/Comparing-Floats-How-To-Determine-if-Floating-Quantities-Are-Close-Enough-Once-a-Tolerance-Has-Been.aspx?Page=2

internal object FzyDoubleEquality {

    private fun fuzzyDoubleIsZero(
        fp1: Double,
        tol: Double,
    ): Boolean = -tol <= fp1 && fp1 <= tol

    private fun fuzzyDoubleEq(
        fp1: Double,
        fp2: Double,
        tol: Double,
        maxVal: Double = Double.MAX_VALUE,
        minPosVal: Double = doubleEps
    ): Boolean {
        if (fp1 == fp2) return !tol.isNaN() // according to IEEE 754
        if (fp1.isNaN() || fp2.isNaN() || tol.isNaN()) return false
        if (!(fp1.isFinite() && fp2.isFinite() && tol.isFinite())) return false
        val fp1z = fuzzyDoubleIsZero(fp1, minPosVal)
        val fp2z = fuzzyDoubleIsZero(fp2, minPosVal)
        if (fp1z && fp2z) return true
        if (fp1z || fp2z) return false
        val afp1 = kotlin.math.abs(fp1)
        val afp2 = kotlin.math.abs(fp2)
        /* ratio must be unity or close enough to unity */
        val divOverflow: Boolean /* whoops! */ = afp1 < 1.0 && afp1 * maxVal < afp2
        return if (divOverflow) false else {
            val divUnderflow /* whoops! */ = 1.0 < fp1 && fp2 < fp1 * minPosVal
            if (divUnderflow) false else {
                // tolerances on unity ratio
                val diff = 1.0 - tol
                val sum = 1.0 + tol
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

    internal fun dFzyIsZero(d: Double, tol: Double = doubleEps) =
        d.isFinite() && !d.isNaN() && tol.isFinite() && !tol.isNaN() && fuzzyDoubleIsZero(d, tol)

    internal fun dFzyIsUnity(d: Double, tol: Double = defaultDoubleTol) = fuzzyDoubleEq(d, 1.0, tol)

    internal fun dFzyEqual(lhs: Double, rhs: Double, tol: Double = defaultDoubleTol) = fuzzyDoubleEq(lhs, rhs, tol)

    internal fun dFzyEqual(lhs: Double, rhs: FzyDouble) = fuzzyDoubleEq(lhs, rhs.qty, rhs.tol)

    fun FzyDouble.fzyEqual(rhs: Double): Boolean = fuzzyDoubleEq(qty, rhs, tol)

    fun FzyDouble.fzyEqual(rhs: FzyDouble): Boolean {
        if (this === rhs) return !(qty.isNaN() || tol.isNaN())
        return if (this.tol.toBits() == rhs.tol.toBits()) fuzzyDoubleEq(this.qty, rhs.qty, tol) else false
    }

    fun isSameZeroes(lhsZero: FzyDouble, rhsZero: FzyDouble): Boolean {
        val isLhsZero = dFzyIsZero(lhsZero.qty, lhsZero.tol)
        if ((lhsZero === rhsZero) && isLhsZero) return true
        return if (isLhsZero && dFzyIsZero(rhsZero.qty, rhsZero.tol)) {
            lhsZero.tol.toBits() == rhsZero.tol.toBits()
        } else false
    }
}