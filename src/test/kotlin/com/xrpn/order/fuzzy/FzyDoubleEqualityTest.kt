package com.xrpn.order.fuzzy

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import com.xrpn.order.fuzzy.FzyDouble.Companion.fzyEqual
import com.xrpn.order.fuzzy.FzyDouble.Companion.fzyIsZero
import com.xrpn.order.fuzzy.FzyDouble.Companion.asFzyDouble
import com.xrpn.order.fuzzy.FzyDouble.Companion.fzyIsUnity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.numericDoubles


class FzyDoubleEqualityTest : FunSpec({

    beforeTest {
    }

    test("pathology") {
        Double.NaN.fzyEqual(1.0) shouldBe false
        Double.NaN.fzyEqual(Double.NaN) shouldBe false
        Double.NaN.asFzyDouble().isZero() shouldBe false

        Double.POSITIVE_INFINITY.fzyEqual(Double.POSITIVE_INFINITY) shouldBe true
        Double.NEGATIVE_INFINITY.fzyEqual(Double.POSITIVE_INFINITY) shouldBe false
        Double.POSITIVE_INFINITY.fzyEqual(Double.NEGATIVE_INFINITY) shouldBe false
        Double.NEGATIVE_INFINITY.fzyEqual(Double.NEGATIVE_INFINITY) shouldBe true
        FzyDouble(Double.NaN).equals(Double.NaN) shouldBe false
        (FzyDouble(Double.NaN) == FzyDouble(Double.NaN)) shouldBe false
        FzyDouble(Double.POSITIVE_INFINITY).equals(Double.POSITIVE_INFINITY) shouldBe true
        FzyDouble(Double.NEGATIVE_INFINITY).equals(Double.POSITIVE_INFINITY) shouldBe false
        FzyDouble(Double.POSITIVE_INFINITY).equals(Double.NEGATIVE_INFINITY) shouldBe false
        FzyDouble(Double.NEGATIVE_INFINITY).equals(Double.NEGATIVE_INFINITY) shouldBe true
        Double.MAX_VALUE.fzyEqual(Double.MAX_VALUE) shouldBe true
    }

    test("zeroes") {
        val fd0 = FzyDouble(0.0)
        val fd1 = FzyDouble(Double.MIN_VALUE)
        val fd2 = FzyDouble(-Double.MIN_VALUE)
        val fd3 = FzyDouble(1.0)
        fd0.isZero() shouldBe true
        fd1.isZero() shouldBe true
        fd2.isZero() shouldBe true
        fd3.isZero() shouldBe false
        Double.MIN_VALUE.fzyIsZero() shouldBe true
        0.0.fzyIsZero() shouldBe true
        (-Double.MIN_VALUE).fzyIsZero() shouldBe true
        1.0.fzyIsZero() shouldBe false
        Double.NaN.fzyIsZero() shouldBe false
        Double.POSITIVE_INFINITY.fzyIsZero() shouldBe false
        Double.NEGATIVE_INFINITY.fzyIsZero() shouldBe false
    }

    test("unity") {
        val fd0 = FzyDouble(1.0)
        val fd00 = FzyDouble(1.0, doubleEps, true)
        val fd1 = FzyDouble(1.0+defaultDoubleTol)
        val fd2 = FzyDouble(1.0-defaultDoubleTol)
        val fd3 = FzyDouble(1.0+defaultDoubleTol, 2.0*defaultDoubleTol)
        val fd4 = FzyDouble(1.0-defaultDoubleTol, 2.0*defaultDoubleTol)
        fd0.isUnity() shouldBe true
        fd00.isUnity() shouldBe true
        fd1.isUnity() shouldBe false
        fd2.isUnity() shouldBe false
        fd3.isUnity() shouldBe true
        fd4.isUnity() shouldBe true
        1.0.fzyIsUnity() shouldBe true
        (1.0+defaultDoubleTol).fzyIsUnity() shouldBe false
        (1.0-defaultDoubleTol).fzyIsUnity() shouldBe false
        Double.NaN.fzyIsUnity() shouldBe false
        Double.NEGATIVE_INFINITY.fzyIsUnity() shouldBe false
        Double.POSITIVE_INFINITY.fzyIsUnity() shouldBe false
    }

    test("throw if defeated"){
        shouldThrow<IllegalArgumentException> {
            FzyDouble(1.0, doubleEps)
        }
        shouldThrow<IllegalArgumentException> {
            FzyDouble(1.0, -0.0004)
        }
    }

    test("bounds") {
        (FzyDouble(Double.MAX_VALUE) == FzyDouble(Double.MAX_VALUE)) shouldBe true
        (FzyDouble(-Double.MAX_VALUE) == FzyDouble(-Double.MAX_VALUE)) shouldBe true
        FzyDouble(Double.MAX_VALUE).equals(Double.MAX_VALUE) shouldBe true
        FzyDouble(-Double.MAX_VALUE).equals(-Double.MAX_VALUE) shouldBe true

        (FzyDouble(Double.MIN_VALUE) == FzyDouble(Double.MIN_VALUE)) shouldBe true
        (FzyDouble(Double.MIN_VALUE) == FzyDouble(-Double.MIN_VALUE)) shouldBe true
        (FzyDouble(-Double.MIN_VALUE) == FzyDouble(-Double.MIN_VALUE)) shouldBe true
        FzyDouble(Double.MIN_VALUE).equals(Double.MIN_VALUE) shouldBe true
        FzyDouble(Double.MIN_VALUE).equals(-Double.MIN_VALUE) shouldBe true
        FzyDouble(-Double.MIN_VALUE).equals(-Double.MIN_VALUE) shouldBe true

        Double.MAX_VALUE.fzyEqual(Double.MAX_VALUE) shouldBe true
        (-Double.MAX_VALUE).fzyEqual(-Double.MAX_VALUE) shouldBe true
        Double.MAX_VALUE.fzyEqual(FzyDouble(Double.MAX_VALUE)) shouldBe true
        (-Double.MAX_VALUE).fzyEqual(FzyDouble(-Double.MAX_VALUE)) shouldBe true

        Double.MIN_VALUE.fzyEqual(Double.MIN_VALUE) shouldBe true
        Double.MIN_VALUE.fzyEqual(-Double.MIN_VALUE) shouldBe true
        (-Double.MIN_VALUE).fzyEqual(-Double.MIN_VALUE) shouldBe true
        Double.MIN_VALUE.fzyEqual(FzyDouble(Double.MIN_VALUE)) shouldBe true
        Double.MIN_VALUE.fzyEqual(FzyDouble(-Double.MIN_VALUE)) shouldBe true
        (-Double.MIN_VALUE).fzyEqual(FzyDouble(-Double.MIN_VALUE)) shouldBe true
    }

    test("underflow") {
        (FzyDouble(Double.MAX_VALUE) == FzyDouble(1E-20)) shouldBe false
        (FzyDouble(-Double.MAX_VALUE) == FzyDouble(-1E-20)) shouldBe false
        FzyDouble(Double.MAX_VALUE).equals(1E-20) shouldBe false
        FzyDouble(-Double.MAX_VALUE).equals(-1E-20) shouldBe false
    }

    test("overflow") {
        (FzyDouble(1E-20) == FzyDouble(Double.MAX_VALUE)) shouldBe false
        (FzyDouble(-1E-20) == FzyDouble(-Double.MAX_VALUE)) shouldBe false
        FzyDouble(1E-20).equals(Double.MAX_VALUE) shouldBe false
        FzyDouble(-1E-20).equals(-Double.MAX_VALUE) shouldBe false
    }

    test("same zeroes") {
        val fd0a = FzyDouble.zero()
        val fd0b = FzyDouble.zero()
        val fd00 = FzyDouble.zero(defaultDoubleTol*2.0)
        val fd1 = FzyDouble.unity()
        FzyDoubleEquality.isSameZeroes(fd0a, fd0a) shouldBe true
        FzyDoubleEquality.isSameZeroes(fd0a, fd0b) shouldBe true
        FzyDoubleEquality.isSameZeroes(fd0b, fd0a) shouldBe true
        FzyDoubleEquality.isSameZeroes(fd0a, fd00) shouldBe false
        FzyDoubleEquality.isSameZeroes(fd00, fd0a) shouldBe false
        FzyDoubleEquality.isSameZeroes(fd0a, fd1) shouldBe false
        FzyDoubleEquality.isSameZeroes(fd1, fd0a) shouldBe false
    }

    test("double properties, reflexive") {
        checkAll(Arb.double(), Arb.numericDoubles()) { f1, f2 ->
            val ff1a = FzyDouble(f1)
            val ff1b = FzyDouble(f1)
            val ff2a = FzyDouble(f2)
            val ff2b = FzyDouble(f2)

            ff1a.equals(f1) shouldBe !f1.isNaN()
            ff1b.equals(f1) shouldBe !f1.isNaN()
            ff2a.equals(f2) shouldBe true
            ff2b.equals(f2) shouldBe true

            f1.fzyEqual(f1) shouldBe !f1.isNaN()
            f1.fzyEqual(ff1a) shouldBe !f1.isNaN()
            f1.fzyEqual(ff1b) shouldBe !f1.isNaN()
            ff1a.equals(f1) shouldBe !f1.isNaN()
            ff1b.equals(f1) shouldBe !f1.isNaN()

            (ff1a == ff1b) shouldBe !f1.isNaN()
            (ff1b == ff1a) shouldBe !f1.isNaN()

            ff2a.equals(f2) shouldBe true
            ff2b.equals(f2) shouldBe true
            (ff2a == ff2b) shouldBe true
            (ff2b == ff2a) shouldBe true
        }
    }

    test("double properties, symmetric") {
        checkAll(Arb.double(), Arb.numericDoubles()) { f1, f2 ->
            val ff1 = FzyDouble(f1)
            val f1d = f1 / (1.0 + (defaultDoubleTol /2.0))
            val ff1d = FzyDouble(f1d)
            val ff2 = FzyDouble(f2)
            val f2d = f2 / (1.0 + (defaultDoubleTol /2.0))
            val ff2d = FzyDouble(f2d)

            (!ff1.equals(f1d) && f1.fzyIsZero()) shouldBe false
            (f1.fzyIsZero() && ff1 != ff1d) shouldBe false

            f1.fzyEqual(f1d) shouldBe !f1.isNaN()
            f1.fzyEqual(ff1d) shouldBe !f1.isNaN()
            f1d.fzyEqual(f1) shouldBe !f1.isNaN()
            f1d.fzyEqual(ff1) shouldBe !f1.isNaN()

            ff1.equals(f1d) shouldBe !f1.isNaN()
            (ff1d == ff1) shouldBe !f1.isNaN()
            ff1d.equals(f1) shouldBe !f1.isNaN()
            ff1.equals(f1d) shouldBe !f1.isNaN()

            (!ff2.equals(f2d) && f2.fzyIsZero()) shouldBe false
            (f2.fzyIsZero() && ff2 != ff2d) shouldBe false

            f2.fzyEqual(f2d) shouldBe true
            f2.fzyEqual(ff2d) shouldBe true
            f2d.fzyEqual(f2) shouldBe true
            f2d.fzyEqual(ff2) shouldBe true

            ff2.equals(f2d) shouldBe true
            (ff2d == ff2) shouldBe true
            ff2d.equals(f2) shouldBe true
            ff2.equals(f2d) shouldBe true
        }
    }

    test("double properties, transitive") {
        checkAll(Arb.double(), Arb.numericDoubles()) { f1, f2 ->
            val ff1 = FzyDouble(f1)
            val f1a = f1 / (1.0 + (defaultDoubleTol /1.7))
            val ff1a = FzyDouble(f1a)
            val f1b = f1 / (1.0 + (defaultDoubleTol /1.3))
            val ff1b = FzyDouble(f1b)

            val ff2 = FzyDouble(f2)
            val f2a = f2 / (1.0 + (defaultDoubleTol /1.9))
            val ff2a = FzyDouble(f2a)
            val f2b = f2 / (1.0 + (defaultDoubleTol /1.5))
            val ff2b = FzyDouble(f2b)

            (!ff1.equals(f1a) && f1.fzyIsZero()) shouldBe false
            (f1.fzyIsZero() && ff1 != ff1a) shouldBe false
            (!ff1.equals(f1b) && f1.fzyIsZero()) shouldBe false
            (f1.fzyIsZero() && ff1 != ff1b) shouldBe false

            // f1 == f1a, f1a == f1b => f1 == f1b
            f1.fzyEqual(f1a) shouldBe !f1.isNaN()
            f1.fzyEqual(ff1a) shouldBe !f1.isNaN()
            f1a.fzyEqual(f1b) shouldBe !f1.isNaN()
            f1a.fzyEqual(ff1b) shouldBe !f1.isNaN()
            f1b.fzyEqual(f1) shouldBe !f1.isNaN()
            f1b.fzyEqual(ff1) shouldBe !f1.isNaN()

            ff1.equals(f1a) shouldBe !f1.isNaN()
            (ff1 == ff1a) shouldBe !f1.isNaN()
            ff1a.equals(f1b) shouldBe !f1.isNaN()
            (ff1a == ff1b) shouldBe !f1.isNaN()
            ff1b.equals(f1) shouldBe !f1.isNaN()
            (ff1b == ff1) shouldBe !f1.isNaN()

            (!ff2.equals(f2a) && f2.fzyIsZero()) shouldBe false
            (f2.fzyIsZero() && ff2 != ff2a) shouldBe false
            (!ff2.equals(f2b) && f2.fzyIsZero()) shouldBe false
            (f2.fzyIsZero() && ff2 != ff2b) shouldBe false

            // f2 == f2a, f2a == f2b => f2 == f2b
            f2.fzyEqual(f2a) shouldBe true
            f2.fzyEqual(ff2a) shouldBe true
            f2a.fzyEqual(f2b) shouldBe true
            f2a.fzyEqual(ff2b) shouldBe true
            f2b.fzyEqual(f2) shouldBe true
            f2b.fzyEqual(ff2) shouldBe true

            ff2.equals(f2a) shouldBe true
            (ff2 == ff2a) shouldBe true
            ff2a.equals(f2b) shouldBe true
            (ff2a == ff2b) shouldBe true
            ff2b.equals(f2) shouldBe true
            (ff2b == ff2) shouldBe true
        }
    }
})