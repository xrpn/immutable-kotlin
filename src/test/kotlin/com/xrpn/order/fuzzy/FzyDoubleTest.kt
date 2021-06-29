package com.xrpn.order.fuzzy

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.*
import io.kotest.property.arbitrary.arbitrary

class FzyDoubleTest : FunSpec({

    val fzyDoubleArb: Arb<FzyDouble> = arbitrary { rs: RandomSource ->
        FzyDouble(rs.random.nextDouble())
    }

    beforeTest {}

    beforeEach {}

    test("equals") {
        checkAll(fzyDoubleArb) { fzd1 ->
            val tt = fzd1.tol
            val halfTt = fzd1.tol / 2.0
            val twiceTt = fzd1.tol * 2.0
            (fzd1 == fzd1) shouldBe true
            val aux = FzyDouble(fzd1.qty+fzd1.qty*halfTt)
            (fzd1.qty == aux.qty) shouldBe false
            (fzd1 == aux)  shouldBe true
            (fzd1 == FzyDouble(fzd1.qty, twiceTt))  shouldBe false
            fzd1.equals(fzd1.qty) shouldBe true
            fzd1.equals(fzd1.qty+tt) shouldBe false
            fzd1.equals(fzd1.qty-tt) shouldBe false
            fzd1.equals(fzd1.qty.toDouble()) shouldBe false
            fzd1.equals(null) shouldBe false
            fzd1.equals("") shouldBe false
        }
    }

    test("hashCode") {
        forAll<FzyDouble, FzyDouble>(PropTestConfig(iterations = 10),fzyDoubleArb,fzyDoubleArb) { fzd1, fzd2 ->
            fzd1.hashCode() == fzd1.hashCode()
                    && fzd2.hashCode() == fzd2.hashCode()
                    && fzd1.hashCode() != fzd2.hashCode()
        }
    }

    test("isZero") {
        FzyDouble.zero().isZero() shouldBe true
    }

    test("isUnity") {
        FzyDouble.unity().isUnity() shouldBe true
    }

    test("equal") {
        checkAll(fzyDoubleArb) { fzd1 ->
            fzd1.equal(fzd1) shouldBe true
            fzd1.equal(fzd1.qty) shouldBe true
            fzd1.equal(fzd1.qty+defaultFloatTol) shouldBe false
            fzd1.equal(fzd1.qty-defaultFloatTol) shouldBe false
        }
    }

})
