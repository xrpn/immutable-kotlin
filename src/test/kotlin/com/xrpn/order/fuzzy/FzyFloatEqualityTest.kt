package com.xrpn.order.fuzzy

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import com.xrpn.order.fuzzy.FzyFloat.Companion.fzyEqual
import com.xrpn.order.fuzzy.FzyFloat.Companion.fzyIsZero
import com.xrpn.order.fuzzy.FzyFloat.Companion.asFzyFloat
import com.xrpn.order.fuzzy.FzyFloat.Companion.fzyIsUnity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.property.arbitrary.float
import io.kotest.property.arbitrary.numericFloats


class FzyFloatEqualityTest : FunSpec({

    beforeTest {
    }

    test("pathology") {
        Float.NaN.fzyEqual(1.0f) shouldBe false
        Float.NaN.fzyEqual(Float.NaN) shouldBe false
        Float.NaN.asFzyFloat().isZero() shouldBe false

        Float.POSITIVE_INFINITY.fzyEqual(Float.POSITIVE_INFINITY) shouldBe true
        Float.NEGATIVE_INFINITY.fzyEqual(Float.POSITIVE_INFINITY) shouldBe false
        Float.POSITIVE_INFINITY.fzyEqual(Float.NEGATIVE_INFINITY) shouldBe false
        Float.NEGATIVE_INFINITY.fzyEqual(Float.NEGATIVE_INFINITY) shouldBe true
        FzyFloat(Float.NaN).equals(Float.NaN) shouldBe false
        (FzyFloat(Float.NaN) == FzyFloat(Float.NaN)) shouldBe false
        FzyFloat(Float.POSITIVE_INFINITY).equals(Float.POSITIVE_INFINITY) shouldBe true
        FzyFloat(Float.NEGATIVE_INFINITY).equals(Float.POSITIVE_INFINITY) shouldBe false
        FzyFloat(Float.POSITIVE_INFINITY).equals(Float.NEGATIVE_INFINITY) shouldBe false
        FzyFloat(Float.NEGATIVE_INFINITY).equals(Float.NEGATIVE_INFINITY) shouldBe true
        Float.MAX_VALUE.fzyEqual(Float.MAX_VALUE) shouldBe true
    }

    test("zeroes") {
        val ff0 = FzyFloat(0.0f)
        val ff1 = FzyFloat(Float.MIN_VALUE)
        val ff2 = FzyFloat(-Float.MIN_VALUE)
        val ff3 = FzyFloat(1.0f)
        ff0.isZero() shouldBe true
        ff1.isZero() shouldBe true
        ff2.isZero() shouldBe true
        ff3.isZero() shouldBe false
        Float.MIN_VALUE.fzyIsZero() shouldBe true
        0.0f.fzyIsZero() shouldBe true
        (-Float.MIN_VALUE).fzyIsZero() shouldBe true
        1.0f.fzyIsZero() shouldBe false
        Float.NaN.fzyIsZero() shouldBe false
        Float.POSITIVE_INFINITY.fzyIsZero() shouldBe false
        Float.NEGATIVE_INFINITY.fzyIsZero() shouldBe false
    }

    test("unity") {
        val fd0 = FzyFloat(1.0f)
        val fd00 = FzyFloat(1.0f, floatEps, true)
        val fd1 = FzyFloat(1.0f+defaultFloatTol)
        val fd2 = FzyFloat(1.0f-defaultFloatTol)
        val fd3 = FzyFloat(1.0f+defaultFloatTol, 2.0f*defaultFloatTol)
        val fd4 = FzyFloat(1.0f-defaultFloatTol, 2.0f*defaultFloatTol)
        fd0.isUnity() shouldBe true
        fd00.isUnity() shouldBe true
        fd1.isUnity() shouldBe false
        fd2.isUnity() shouldBe false
        fd3.isUnity() shouldBe true
        fd4.isUnity() shouldBe true
        1.0f.fzyIsUnity() shouldBe true
        (1.0f+defaultFloatTol).fzyIsUnity() shouldBe false
        (1.0f-defaultFloatTol).fzyIsUnity() shouldBe false
        Float.NaN.fzyIsUnity() shouldBe false
        Float.NEGATIVE_INFINITY.fzyIsUnity() shouldBe false
        Float.POSITIVE_INFINITY.fzyIsUnity() shouldBe false
    }

    test("throw if defeated"){
        shouldThrow<IllegalArgumentException> {
            FzyFloat(1.0f, floatEps)
        }
        shouldThrow<IllegalArgumentException> {
            FzyFloat(1.0f, -0.0004f)
        }
    }

    test("bounds") {
        (FzyFloat(Float.MAX_VALUE) == FzyFloat(Float.MAX_VALUE)) shouldBe true
        (FzyFloat(-Float.MAX_VALUE) == FzyFloat(-Float.MAX_VALUE)) shouldBe true
        FzyFloat(Float.MAX_VALUE).equals(Float.MAX_VALUE) shouldBe true
        FzyFloat(-Float.MAX_VALUE).equals(-Float.MAX_VALUE) shouldBe true

        (FzyFloat(Float.MIN_VALUE) == FzyFloat(Float.MIN_VALUE)) shouldBe true
        (FzyFloat(Float.MIN_VALUE) == FzyFloat(-Float.MIN_VALUE)) shouldBe true
        (FzyFloat(-Float.MIN_VALUE) == FzyFloat(-Float.MIN_VALUE)) shouldBe true
        FzyFloat(Float.MIN_VALUE).equals(Float.MIN_VALUE) shouldBe true
        FzyFloat(Float.MIN_VALUE).equals(-Float.MIN_VALUE) shouldBe true
        FzyFloat(-Float.MIN_VALUE).equals(-Float.MIN_VALUE) shouldBe true

        Float.MAX_VALUE.fzyEqual(Float.MAX_VALUE) shouldBe true
        (-Float.MAX_VALUE).fzyEqual(-Float.MAX_VALUE) shouldBe true
        Float.MAX_VALUE.fzyEqual(FzyFloat(Float.MAX_VALUE)) shouldBe true
        (-Float.MAX_VALUE).fzyEqual(FzyFloat(-Float.MAX_VALUE)) shouldBe true

        Float.MIN_VALUE.fzyEqual(Float.MIN_VALUE) shouldBe true
        Float.MIN_VALUE.fzyEqual(-Float.MIN_VALUE) shouldBe true
        (-Float.MIN_VALUE).fzyEqual(-Float.MIN_VALUE) shouldBe true
        Float.MIN_VALUE.fzyEqual(FzyFloat(Float.MIN_VALUE)) shouldBe true
        Float.MIN_VALUE.fzyEqual(FzyFloat(-Float.MIN_VALUE)) shouldBe true
        (-Float.MIN_VALUE).fzyEqual(FzyFloat(-Float.MIN_VALUE)) shouldBe true
    }

    test("float properties, reflexive") {
        checkAll(Arb.float(), Arb.numericFloats()) { f1, f2 ->
            val ff1a = FzyFloat(f1)
            val ff1b = FzyFloat(f1)
            val ff2a = FzyFloat(f2)
            val ff2b = FzyFloat(f2)

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

    test("float properties, symmetric") {
        checkAll(Arb.float(), Arb.numericFloats()) { f1, f2 ->
            val ff1 = FzyFloat(f1)
            val f1d = ff1.qty / (1.0f + (defaultFloatTol /2.0f))
            val ff1d = FzyFloat(f1d)
            val ff2 = FzyFloat(f2)
            val f2d = ff2.qty / (1.0f + (defaultFloatTol /2.0f))
            val ff2d = FzyFloat(f2d)

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

    test("float properties, transitive") {
        checkAll(Arb.float(), Arb.numericFloats()) { f1, f2 ->
            val ff1 = FzyFloat(f1)
            val f1a = f1 / (1.0f + (defaultFloatTol /1.7f))
            val ff1a = FzyFloat(f1a)
            val f1b = f1 / (1.0f + (defaultFloatTol /1.3f))
            val ff1b = FzyFloat(f1b)

            val ff2 = FzyFloat(f2)
            val f2a = f2 / (1.0f + (defaultFloatTol /1.9f))
            val ff2a = FzyFloat(f2a)
            val f2b = f2 / (1.0f + (defaultFloatTol /1.5f))
            val ff2b = FzyFloat(f2b)

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