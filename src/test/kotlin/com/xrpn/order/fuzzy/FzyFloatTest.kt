package com.xrpn.order.fuzzy

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.*
import io.kotest.property.arbitrary.arbitrary

class FzyFloatTest : FunSpec({

    val fzyFloatArb: Arb<FzyFloat> = arbitrary { rs: RandomSource ->
        FzyFloat(rs.random.nextFloat())
    }

    beforeTest {}

    beforeEach {}

    test("equals") {
        checkAll(fzyFloatArb) { fzf1 ->
            val tt = fzf1.tol
            val halfTt = fzf1.tol / 2.0f
            val twiceTt = fzf1.tol * 2.0f
            (fzf1 == fzf1) shouldBe true
            val aux = FzyFloat(fzf1.qty+fzf1.qty*halfTt)
            (fzf1.qty == aux.qty) shouldBe false
            (fzf1 == aux)  shouldBe true
            (fzf1 == FzyFloat(fzf1.qty, twiceTt))  shouldBe false
            fzf1.equals(fzf1.qty) shouldBe true
            fzf1.equals(fzf1.qty+tt) shouldBe false
            fzf1.equals(fzf1.qty-tt) shouldBe false
            fzf1.equals(fzf1.qty.toDouble()) shouldBe false
            fzf1.equals(null) shouldBe false
            fzf1.equals("") shouldBe false
        }
    }

    test("hashCode") {
        forAll<FzyFloat, FzyFloat>(PropTestConfig(iterations = 10),fzyFloatArb,fzyFloatArb) { fzf1, fzf2 ->
            fzf1.hashCode() == fzf1.hashCode()
                    && fzf2.hashCode() == fzf2.hashCode()
                    && fzf1.hashCode() != fzf2.hashCode()
        }
    }

    test("isZero") {
        FzyFloat.zero().isZero() shouldBe true
    }

    test("isUnity") {
        FzyFloat.unity().isUnity() shouldBe true
    }

    test("equal") {
        checkAll(fzyFloatArb) { fzf1 ->
            fzf1.equal(fzf1) shouldBe true
            fzf1.equal(fzf1.qty) shouldBe true
            fzf1.equal(fzf1.qty+defaultFloatTol) shouldBe false
            fzf1.equal(fzf1.qty-defaultFloatTol) shouldBe false
        }
    }
})
