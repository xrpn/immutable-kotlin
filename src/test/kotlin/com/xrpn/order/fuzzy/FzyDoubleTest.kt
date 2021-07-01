package com.xrpn.order.fuzzy

import com.xrpn.order.fuzzy.FzyDouble.Companion.asFzyDouble
import com.xrpn.order.fuzzy.FzyDouble.Companion.fzyEqual
import com.xrpn.order.fuzzy.FzyDouble.Companion.unity
import com.xrpn.order.fuzzy.FzyDouble.Companion.zero
import com.xrpn.order.fuzzy.FzyDoubleEquality.fzyEqual
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.*
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.numericDoubles

class FzyDoubleTest : FunSpec({

    val alterDoubleTol = 2.0 * defaultDoubleTol
    val fzyDoubleDefaultArb: Arb<FzyDouble> = arbitrary { rs: RandomSource ->
        FzyDouble(rs.random.nextDouble())
    }
    val fzyDoubleAlterArb: Arb<FzyDouble> = arbitrary { rs: RandomSource ->
        FzyDouble(rs.random.nextDouble(), alterDoubleTol)
    }

    beforeTest {}

    beforeEach {}

    test("equals default") {
        checkAll(fzyDoubleDefaultArb) { fzd1 ->
            val tt = fzd1.tol
            val halfTt = fzd1.tol / 2.0
            val twiceTt = fzd1.tol * 2.0
            (fzd1 == fzd1) shouldBe true
            val aux0 = FzyDouble(fzd1.qty, fzd1.tol)
            (fzd1 == aux0) shouldBe true
            val aux1 = FzyDouble(fzd1.qty+fzd1.qty*halfTt)
            (fzd1.qty == aux1.qty) shouldBe false
            (fzd1 == aux1)  shouldBe true
            (fzd1 == FzyDouble(fzd1.qty, twiceTt))  shouldBe false
            fzd1.equals(fzd1.qty) shouldBe true
            fzd1.equals(fzd1.qty+tt) shouldBe false
            fzd1.equals(fzd1.qty-tt) shouldBe false
            fzd1.equals(fzd1.qty.toFloat()) shouldBe false
            fzd1.equals(null) shouldBe false
            fzd1.equals("") shouldBe false
        }
    }

    test("equals alter") {
        checkAll(fzyDoubleAlterArb) { fzda1 ->
            val tt = fzda1.tol
            val halfTt = fzda1.tol / 2.0
            val twiceTt = fzda1.tol * 2.0
            (fzda1 == fzda1) shouldBe true
            val aux0 = FzyDouble(fzda1.qty, fzda1.tol)
            (fzda1 == aux0) shouldBe true
            val aux1 = FzyDouble(fzda1.qty+fzda1.qty*halfTt,alterDoubleTol)
            (fzda1.qty == aux1.qty) shouldBe false
            (fzda1 == aux1)  shouldBe true
            (fzda1 == FzyDouble(fzda1.qty, twiceTt))  shouldBe false
            fzda1.equals(fzda1.qty) shouldBe true
            fzda1.equals(fzda1.qty+tt) shouldBe false
            fzda1.equals(fzda1.qty-tt) shouldBe false
            fzda1.equals(fzda1.qty.toFloat()) shouldBe false
            fzda1.equals(null) shouldBe false
            fzda1.equals("") shouldBe false
        }
    }

    test("equals mixed") {
        checkAll(fzyDoubleDefaultArb, fzyDoubleAlterArb) { fzd1, fzda1 ->
            fzd1.equals(fzda1) shouldBe false
            fzda1.equals(fzda1.qty) shouldBe true
        }
    }

    test("equal (zeroes)") {
        zero().equal(zero()) shouldBe true
        zero().equal(unity()) shouldBe false
        unity().equal(zero()) shouldBe false
        doubleEps.fzyEqual(zero()) shouldBe true
        zero().equals(doubleEps) shouldBe true
    }

    test("hashCode") {
        forAll<FzyDouble, FzyDouble>(PropTestConfig(iterations = 10),fzyDoubleDefaultArb,fzyDoubleDefaultArb) { fzd1, fzd2 ->
            fzd1.hashCode() == fzd1.hashCode()
                    && fzd2.hashCode() == fzd2.hashCode()
                    && fzd1.hashCode() != fzd2.hashCode()
        }
    }

    test("isZero") {
        FzyDouble.zero().isZero() shouldBe true
        FzyDouble.zero(alterDoubleTol).isZero() shouldBe true
    }

    test("isUnity") {
        FzyDouble.unity().isUnity() shouldBe true
        FzyDouble.unity(alterDoubleTol).isUnity() shouldBe true
    }

    test("Double.fzyEqual") {
        checkAll(Arb.numericDoubles()) { nd1 ->
            val nf1fzy = nd1.asFzyDouble()

            nd1.fzyEqual(nf1fzy) shouldBe true
            nd1.fzyEqual(nd1.asFzyDouble(alterDoubleTol)) shouldBe true
            nd1.fzyEqual(nd1, defaultDoubleTol) shouldBe true
            nd1.fzyEqual(nd1, alterDoubleTol) shouldBe true

            val tol = nf1fzy.tol
            val aux = if (nd1 == 0.0) 1.0 + tol * 20.0 else nd1 + nd1 * tol * 20.0
            val nf2 = if (nd1 != aux) aux else nd1 + 1.0
            (nd1 != nf2) shouldBe true
            nd1.fzyEqual(nf2.asFzyDouble()) shouldBe false
            nd1.fzyEqual(nf2, defaultDoubleTol) shouldBe false
            nd1.fzyEqual(nf2, alterDoubleTol) shouldBe false
        }
    }

    test("Double.fzyEqual qty pathology") {
        checkAll(Arb.double()) { nf1 ->
            val nf1fzy = nf1.asFzyDouble()
            nf1.fzyEqual(nf1fzy) shouldBe !nf1.isNaN()
            nf1.fzyEqual(nf1.asFzyDouble(alterDoubleTol)) shouldBe !nf1.isNaN()
            nf1.fzyEqual(nf1, defaultDoubleTol) shouldBe !nf1.isNaN()
            nf1.fzyEqual(nf1, alterDoubleTol) shouldBe !nf1.isNaN()
            nf1fzy.fzyEqual(nf1) shouldBe !nf1.isNaN()
            nf1fzy.fzyEqual(nf1fzy) shouldBe !nf1.isNaN()

        }
    }

    test("Double.fzyEqual tol pathology") {
        checkAll(Arb.numericDoubles(), Arb.double()) { nfQty, nfTol ->
            val tol = Math.abs(nfTol)
            val nf1fzy = FzyDouble(nfQty, tol, defeatOk = true)
            nfQty.fzyEqual(nf1fzy) shouldBe !tol.isNaN()
            nfQty.fzyEqual(nfQty.asFzyDouble(alterDoubleTol)) shouldBe true
            nfQty.fzyEqual(nfQty, tol) shouldBe !tol.isNaN()
            nf1fzy.fzyEqual(nfQty) shouldBe !tol.isNaN()
            nf1fzy.fzyEqual(nf1fzy) shouldBe !tol.isNaN()
        }
    }

})
