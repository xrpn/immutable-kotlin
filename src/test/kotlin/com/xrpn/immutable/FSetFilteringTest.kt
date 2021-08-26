package com.xrpn.immutable

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSetOfNone = FSet.of(*arrayOf<Int>())
private val intSetOfOne = FSet.of(1)
private val intSetOfTwo = FSet.of(1, 2)
private val intSetOfTwoOfst1 = FSet.of(2, 3)
private val intSetOfTwoOfst2 = FSet.of(3, 4)
private val intSetOfThree = FSet.of(1, 2, 3)
private val intSetOfFour = FSet.of(1, 2, 3, 4)

class FSetFilteringTest : FunSpec({

    beforeTest {}

    test("fcontains") { }

    test("fcontainsAny") { }

    test("fdropItem") { }

    test("fdropAll") { }

    test("fdropWhen") { }

    test("fempty") { }

    test("ffilter") { }

    test("ffilterNot") { }

    test("ffind") { }

    test("fisSubsetOf") { }

    test("fpick") { }

    test("fAND") { }

    test("fNOT") { }

    test("fOR") {
        intSetOfNone.fOR(intSetOfNone).equals(intSetOfNone) shouldBe true
        intSetOfOne.fOR(intSetOfNone).equals(intSetOfOne) shouldBe true
        intSetOfNone.fOR(intSetOfOne).equals(intSetOfOne) shouldBe true

        intSetOfTwo.fOR(intSetOfTwo).equals(intSetOfTwo) shouldBe true
        intSetOfTwo.fOR(intSetOfTwoOfst1).equals(intSetOfThree) shouldBe true
        intSetOfTwoOfst1.fOR(intSetOfTwo).equals(intSetOfThree) shouldBe true
        intSetOfTwo.fOR(intSetOfTwoOfst2).equals(intSetOfFour) shouldBe true
        intSetOfTwoOfst2.fOR(intSetOfTwo).equals(intSetOfFour) shouldBe true
    }

    test("fXOR") { }
})
