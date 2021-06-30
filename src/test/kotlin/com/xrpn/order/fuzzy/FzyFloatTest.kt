package com.xrpn.order.fuzzy

import com.xrpn.order.fuzzy.FzyFloat.Companion.asFzyFloat
import com.xrpn.order.fuzzy.FzyFloat.Companion.fzyEqual
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.*
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.numericFloats

class FzyFloatTest : FunSpec({

    val alterFloatTol = 2.0f * defaultFloatTol
    val fzyFloatDefaultArb: Arb<FzyFloat> = arbitrary { rs: RandomSource ->
        FzyFloat(rs.random.nextFloat())
    }
    val fzyFloatAlterArb: Arb<FzyFloat> = arbitrary { rs: RandomSource ->
        FzyFloat(rs.random.nextFloat(), alterFloatTol)
    }

    beforeTest {}

    beforeEach {}

    test("equals default") {
        checkAll(fzyFloatDefaultArb) { fzf1 ->
            val tt = fzf1.tol
            val halfTt = fzf1.tol / 2.0f
            val twiceTt = fzf1.tol * 2.0f
            (fzf1 == fzf1) shouldBe true
            val aux0 = FzyFloat(fzf1.qty,fzf1.tol)
            (fzf1 == aux0) shouldBe true
            val aux1 = FzyFloat(fzf1.qty+fzf1.qty*halfTt)
            (fzf1.qty == aux1.qty) shouldBe false
            (fzf1 == aux1)  shouldBe true
            (fzf1 == FzyFloat(fzf1.qty, twiceTt))  shouldBe false
            fzf1.equals(fzf1.qty) shouldBe true
            fzf1.equals(fzf1.qty+tt) shouldBe false
            fzf1.equals(fzf1.qty-tt) shouldBe false
            fzf1.equals(fzf1.qty.toDouble()) shouldBe false
            fzf1.equals(null) shouldBe false
            fzf1.equals("") shouldBe false
        }
    }

    test("equals alter") {
        checkAll(fzyFloatAlterArb) { fzfa1 ->
            val tt = fzfa1.tol
            val halfTt = fzfa1.tol / 2.0f
            val twiceTt = fzfa1.tol * 2.0f
            (fzfa1 == fzfa1) shouldBe true
            val aux0 = FzyFloat(fzfa1.qty,fzfa1.tol)
            (fzfa1 == aux0) shouldBe true
            val aux1 = FzyFloat(fzfa1.qty+fzfa1.qty*halfTt,alterFloatTol)
            (fzfa1.qty == aux1.qty) shouldBe false
            (fzfa1 == aux1)  shouldBe true
            (fzfa1 == FzyFloat(fzfa1.qty, twiceTt))  shouldBe false
            fzfa1.equals(fzfa1.qty) shouldBe true
            fzfa1.equals(fzfa1.qty+tt) shouldBe false
            fzfa1.equals(fzfa1.qty-tt) shouldBe false
            fzfa1.equals(fzfa1.qty.toDouble()) shouldBe false
            fzfa1.equals(null) shouldBe false
            fzfa1.equals("") shouldBe false
        }
    }

    test("equals mixed") {
        checkAll(fzyFloatDefaultArb, fzyFloatAlterArb) { fzf1, fzfa1 ->
            (fzf1 == fzfa1) shouldBe false
            fzfa1.equals(fzfa1.qty) shouldBe true
        }
    }

    test("hashCode") {
        forAll<FzyFloat, FzyFloat>(PropTestConfig(iterations = 10),fzyFloatDefaultArb,fzyFloatDefaultArb) { fzf1, fzf2 ->
            fzf1.hashCode() == fzf1.hashCode()
                    && fzf2.hashCode() == fzf2.hashCode()
                    && fzf1.hashCode() != fzf2.hashCode()
        }
    }

    test("isZero") {
        FzyFloat.zero().isZero() shouldBe true
        FzyFloat.zero(alterFloatTol).isZero() shouldBe true
    }

    test("isUnity") {
        FzyFloat.unity().isUnity() shouldBe true
        FzyFloat.unity(alterFloatTol).isUnity() shouldBe true
    }

    test("Float.fzyEqual") {
        checkAll(Arb.numericFloats()) { nf1 ->
            val nf1fzy = nf1.asFzyFloat()

            nf1.fzyEqual(nf1fzy) shouldBe true
            nf1.fzyEqual(nf1.asFzyFloat(alterFloatTol)) shouldBe true
            nf1.fzyEqual(nf1, defaultFloatTol) shouldBe true
            nf1.fzyEqual(nf1, alterFloatTol) shouldBe true

            val tol = nf1fzy.tol
            val aux = if (nf1 == 0.0f) 1.0f + tol * 20.0f else nf1 + nf1 * tol * 20.0f
            val nf2 = if (nf1 != aux) aux else nf1 + 1.0f
            (nf1 != nf2) shouldBe true
            nf1.fzyEqual(nf2.asFzyFloat()) shouldBe false
            nf1.fzyEqual(nf2, defaultFloatTol) shouldBe false
            nf1.fzyEqual(nf2, alterFloatTol) shouldBe false
        }
    }

})
