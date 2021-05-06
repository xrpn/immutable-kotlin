package com.xrpn.order.fuzzy

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import com.xrpn.order.fuzzy.FzyDouble.Companion.fzyEqual
import com.xrpn.order.fuzzy.FzyDouble.Companion.fzyIsZero
import com.xrpn.order.fuzzy.FzyFloat.Companion.fzyEqual
import com.xrpn.order.fuzzy.FzyFloat.Companion.fzyIsZero
import com.xrpn.order.fuzzy.FzyDouble.Companion.asFzyDouble
import com.xrpn.order.fuzzy.FzyFloat.Companion.asFzyFloat
import io.kotest.property.arbitrary.*


class FzyEqualityTest : FunSpec({

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
        FzyDouble(Double.NaN).equal(Double.NaN) shouldBe false
        FzyDouble(Double.POSITIVE_INFINITY).equal(Double.POSITIVE_INFINITY) shouldBe true
        FzyDouble(Double.NEGATIVE_INFINITY).equal(Double.POSITIVE_INFINITY) shouldBe false
        FzyDouble(Double.POSITIVE_INFINITY).equal(Double.NEGATIVE_INFINITY) shouldBe false
        FzyDouble(Double.NEGATIVE_INFINITY).equal(Double.NEGATIVE_INFINITY) shouldBe true
        Double.MAX_VALUE.fzyEqual(Double.MAX_VALUE) shouldBe true
        
        Float.NaN.fzyEqual(1.0f) shouldBe false
        Float.NaN.fzyEqual(Float.NaN) shouldBe false
        Float.NaN.asFzyFloat().isZero() shouldBe false

        Float.POSITIVE_INFINITY.fzyEqual(Float.POSITIVE_INFINITY) shouldBe true
        Float.NEGATIVE_INFINITY.fzyEqual(Float.POSITIVE_INFINITY) shouldBe false
        Float.POSITIVE_INFINITY.fzyEqual(Float.NEGATIVE_INFINITY) shouldBe false
        Float.NEGATIVE_INFINITY.fzyEqual(Float.NEGATIVE_INFINITY) shouldBe true
        FzyFloat(Float.NaN).equal(Float.NaN) shouldBe false
        FzyFloat(Float.NaN).equal(FzyFloat(Float.NaN)) shouldBe false
        FzyFloat(Float.POSITIVE_INFINITY).equal(Float.POSITIVE_INFINITY) shouldBe true
        FzyFloat(Float.NEGATIVE_INFINITY).equal(Float.POSITIVE_INFINITY) shouldBe false
        FzyFloat(Float.POSITIVE_INFINITY).equal(Float.NEGATIVE_INFINITY) shouldBe false
        FzyFloat(Float.NEGATIVE_INFINITY).equal(Float.NEGATIVE_INFINITY) shouldBe true
        Float.MAX_VALUE.fzyEqual(Float.MAX_VALUE) shouldBe true
    }

    test("zeroes") {
        val ff0 = FzyFloat(0.0f)
        val ff1 = FzyFloat(Float.MIN_VALUE)
        val ff2 = FzyFloat(-Float.MIN_VALUE)
        val ff3 = FzyFloat(1.0f)
        val fd0 = FzyDouble(0.0)
        val fd1 = FzyDouble(Double.MIN_VALUE)
        val fd2 = FzyDouble(-Double.MIN_VALUE)
        val fd3 = FzyDouble(1.0)
        ff0.isZero() shouldBe true
        ff1.isZero() shouldBe true
        ff2.isZero() shouldBe true
        ff3.isZero() shouldBe false
        fd0.isZero() shouldBe true
        fd1.isZero() shouldBe true
        fd2.isZero() shouldBe true
        fd3.isZero() shouldBe false
        0.0f.fzyIsZero() shouldBe true
        Double.MIN_VALUE.fzyIsZero() shouldBe true
        (-Float.MIN_VALUE).fzyIsZero() shouldBe true
        1.0f.fzyIsZero() shouldBe false
        Float.NaN.fzyIsZero() shouldBe false
        0.0.fzyIsZero() shouldBe true
        Double.MIN_VALUE.fzyIsZero() shouldBe true
        (-Double.MIN_VALUE).fzyIsZero() shouldBe true
        1.0.fzyIsZero() shouldBe false
        Double.NaN.fzyIsZero() shouldBe false
    }

    test("bounds") {
        FzyDouble(Double.MAX_VALUE).equal(FzyDouble(Double.MAX_VALUE)) shouldBe true
        FzyDouble(-Double.MAX_VALUE).equal(FzyDouble(-Double.MAX_VALUE)) shouldBe true
        FzyDouble(Double.MAX_VALUE).equal(Double.MAX_VALUE) shouldBe true
        FzyDouble(-Double.MAX_VALUE).equal(-Double.MAX_VALUE) shouldBe true

        FzyDouble(Double.MIN_VALUE).equal(FzyDouble(Double.MIN_VALUE)) shouldBe true
        FzyDouble(Double.MIN_VALUE).equal(FzyDouble(-Double.MIN_VALUE)) shouldBe true
        FzyDouble(-Double.MIN_VALUE).equal(FzyDouble(-Double.MIN_VALUE)) shouldBe true
        FzyDouble(Double.MIN_VALUE).equal(Double.MIN_VALUE) shouldBe true
        FzyDouble(Double.MIN_VALUE).equal(-Double.MIN_VALUE) shouldBe true
        FzyDouble(-Double.MIN_VALUE).equal(-Double.MIN_VALUE) shouldBe true

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

        FzyFloat(Float.MAX_VALUE).equal(FzyFloat(Float.MAX_VALUE)) shouldBe true
        FzyFloat(-Float.MAX_VALUE).equal(FzyFloat(-Float.MAX_VALUE)) shouldBe true
        FzyFloat(Float.MAX_VALUE).equal(Float.MAX_VALUE) shouldBe true
        FzyFloat(-Float.MAX_VALUE).equal(-Float.MAX_VALUE) shouldBe true

        FzyFloat(Float.MIN_VALUE).equal(FzyFloat(Float.MIN_VALUE)) shouldBe true
        FzyFloat(Float.MIN_VALUE).equal(FzyFloat(-Float.MIN_VALUE)) shouldBe true
        FzyFloat(-Float.MIN_VALUE).equal(FzyFloat(-Float.MIN_VALUE)) shouldBe true
        FzyFloat(Float.MIN_VALUE).equal(Float.MIN_VALUE) shouldBe true
        FzyFloat(Float.MIN_VALUE).equal(-Float.MIN_VALUE) shouldBe true
        FzyFloat(-Float.MIN_VALUE).equal(-Float.MIN_VALUE) shouldBe true

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
            ff1a.equal(f1) shouldBe !f1.isNaN()
            ff1b.equal(f1) shouldBe !f1.isNaN()

            ff1a.equal(ff1b) shouldBe !f1.isNaN()
            ff1b.equal(ff1a) shouldBe !f1.isNaN()

            ff2a.equal(f2) shouldBe true
            ff2b.equal(f2) shouldBe true
            ff2a.equal(ff2b) shouldBe true
            ff2b.equal(ff2a) shouldBe true
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
            (f1.fzyIsZero() && !ff1.equals(ff1d)) shouldBe false

            f1.fzyEqual(f1d) shouldBe !f1.isNaN()
            f1.fzyEqual(ff1d) shouldBe !f1.isNaN()
            f1d.fzyEqual(f1) shouldBe !f1.isNaN()
            f1d.fzyEqual(ff1) shouldBe !f1.isNaN()

            ff1.equal(f1d) shouldBe !f1.isNaN()
            ff1d.equal(ff1) shouldBe !f1.isNaN()
            ff1d.equal(f1) shouldBe !f1.isNaN()
            ff1.equal(f1d) shouldBe !f1.isNaN()

            (!ff2.equals(f2d) && f2.fzyIsZero()) shouldBe false
            (f2.fzyIsZero() && !ff2.equals(ff2d)) shouldBe false

            f2.fzyEqual(f2d) shouldBe true
            f2.fzyEqual(ff2d) shouldBe true
            f2d.fzyEqual(f2) shouldBe true
            f2d.fzyEqual(ff2) shouldBe true

            ff2.equal(f2d) shouldBe true
            ff2d.equal(ff2) shouldBe true
            ff2d.equal(f2) shouldBe true
            ff2.equal(f2d) shouldBe true
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
            (f1.fzyIsZero() && !ff1.equals(ff1a)) shouldBe false
            (!ff1.equals(f1b) && f1.fzyIsZero()) shouldBe false
            (f1.fzyIsZero() && !ff1.equals(ff1b)) shouldBe false

            // f1 == f1a, f1a == f1b => f1 == f1b
            f1.fzyEqual(f1a) shouldBe !f1.isNaN()
            f1.fzyEqual(ff1a) shouldBe !f1.isNaN()
            f1a.fzyEqual(f1b) shouldBe !f1.isNaN()
            f1a.fzyEqual(ff1b) shouldBe !f1.isNaN()
            f1b.fzyEqual(f1) shouldBe !f1.isNaN()
            f1b.fzyEqual(ff1) shouldBe !f1.isNaN()

            ff1.equal(f1a) shouldBe !f1.isNaN()
            ff1.equal(ff1a) shouldBe !f1.isNaN()
            ff1a.equal(f1b) shouldBe !f1.isNaN()
            ff1a.equal(ff1b) shouldBe !f1.isNaN()
            ff1b.equal(f1) shouldBe !f1.isNaN()
            ff1b.equal(ff1) shouldBe !f1.isNaN()

            (!ff2.equals(f2a) && f2.fzyIsZero()) shouldBe false
            (f2.fzyIsZero() && !ff2.equals(ff2a)) shouldBe false
            (!ff2.equals(f2b) && f2.fzyIsZero()) shouldBe false
            (f2.fzyIsZero() && !ff2.equals(ff2b)) shouldBe false

            // f2 == f2a, f2a == f2b => f2 == f2b
            f2.fzyEqual(f2a) shouldBe true
            f2.fzyEqual(ff2a) shouldBe true
            f2a.fzyEqual(f2b) shouldBe true
            f2a.fzyEqual(ff2b) shouldBe true
            f2b.fzyEqual(f2) shouldBe true
            f2b.fzyEqual(ff2) shouldBe true

            ff2.equal(f2a) shouldBe true
            ff2.equal(ff2a) shouldBe true
            ff2a.equal(f2b) shouldBe true
            ff2a.equal(ff2b) shouldBe true
            ff2b.equal(f2) shouldBe true
            ff2b.equal(ff2) shouldBe true
        }
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
            ff1a.equal(f1) shouldBe !f1.isNaN()
            ff1b.equal(f1) shouldBe !f1.isNaN()

            ff1a.equal(ff1b) shouldBe !f1.isNaN()
            ff1b.equal(ff1a) shouldBe !f1.isNaN()

            ff2a.equal(f2) shouldBe true
            ff2b.equal(f2) shouldBe true
            ff2a.equal(ff2b) shouldBe true
            ff2b.equal(ff2a) shouldBe true
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
            (f1.fzyIsZero() && !ff1.equals(ff1d)) shouldBe false

            f1.fzyEqual(f1d) shouldBe !f1.isNaN()
            f1.fzyEqual(ff1d) shouldBe !f1.isNaN()
            f1d.fzyEqual(f1) shouldBe !f1.isNaN()
            f1d.fzyEqual(ff1) shouldBe !f1.isNaN()

            ff1.equal(f1d) shouldBe !f1.isNaN()
            ff1d.equal(ff1) shouldBe !f1.isNaN()
            ff1d.equal(f1) shouldBe !f1.isNaN()
            ff1.equal(f1d) shouldBe !f1.isNaN()

            (!ff2.equals(f2d) && f2.fzyIsZero()) shouldBe false
            (f2.fzyIsZero() && !ff2.equals(ff2d)) shouldBe false

            f2.fzyEqual(f2d) shouldBe true
            f2.fzyEqual(ff2d) shouldBe true
            f2d.fzyEqual(f2) shouldBe true
            f2d.fzyEqual(ff2) shouldBe true

            ff2.equal(f2d) shouldBe true
            ff2d.equal(ff2) shouldBe true
            ff2d.equal(f2) shouldBe true
            ff2.equal(f2d) shouldBe true
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
            (f1.fzyIsZero() && !ff1.equals(ff1a)) shouldBe false
            (!ff1.equals(f1b) && f1.fzyIsZero()) shouldBe false
            (f1.fzyIsZero() && !ff1.equals(ff1b)) shouldBe false

            // f1 == f1a, f1a == f1b => f1 == f1b
            f1.fzyEqual(f1a) shouldBe !f1.isNaN()
            f1.fzyEqual(ff1a) shouldBe !f1.isNaN()
            f1a.fzyEqual(f1b) shouldBe !f1.isNaN()
            f1a.fzyEqual(ff1b) shouldBe !f1.isNaN()
            f1b.fzyEqual(f1) shouldBe !f1.isNaN()
            f1b.fzyEqual(ff1) shouldBe !f1.isNaN()

            ff1.equal(f1a) shouldBe !f1.isNaN()
            ff1.equal(ff1a) shouldBe !f1.isNaN()
            ff1a.equal(f1b) shouldBe !f1.isNaN()
            ff1a.equal(ff1b) shouldBe !f1.isNaN()
            ff1b.equal(f1) shouldBe !f1.isNaN()
            ff1b.equal(ff1) shouldBe !f1.isNaN()

            (!ff2.equals(f2a) && f2.fzyIsZero()) shouldBe false
            (f2.fzyIsZero() && !ff2.equals(ff2a)) shouldBe false
            (!ff2.equals(f2b) && f2.fzyIsZero()) shouldBe false
            (f2.fzyIsZero() && !ff2.equals(ff2b)) shouldBe false

            // f2 == f2a, f2a == f2b => f2 == f2b
            f2.fzyEqual(f2a) shouldBe true
            f2.fzyEqual(ff2a) shouldBe true
            f2a.fzyEqual(f2b) shouldBe true
            f2a.fzyEqual(ff2b) shouldBe true
            f2b.fzyEqual(f2) shouldBe true
            f2b.fzyEqual(ff2) shouldBe true

            ff2.equal(f2a) shouldBe true
            ff2.equal(ff2a) shouldBe true
            ff2a.equal(f2b) shouldBe true
            ff2a.equal(ff2b) shouldBe true
            ff2b.equal(f2) shouldBe true
            ff2b.equal(ff2) shouldBe true
        }
    }
    
})