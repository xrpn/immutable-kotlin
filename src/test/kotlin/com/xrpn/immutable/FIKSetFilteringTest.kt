package com.xrpn.immutable

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fset

private val intSetOfNone = FKSet.ofi(*arrayOf<Int>())
private val intSetOfOne = FKSet.ofi(1)
private val intSetOfTwo = FKSet.ofi(1, 2)
private val intSetOfTwoOfst1 = FKSet.ofi(2, 3)
private val intSetOfTwoOfst2 = FKSet.ofi(3, 4)
private val intSetOfThree = FKSet.ofi(1, 2, 3)
private val intSetOfFour = FKSet.ofi(1, 2, 3, 4)
private val intSetMaverick = FKSet.ofi(113, 97)

class FIKSetFilteringTest : FunSpec({

    val repeats = 50
    
    beforeTest {}

    test("fcontains") {
        intSetOfNone.fcontains(1) shouldBe false
        intSetOfOne.fcontains(0) shouldBe false
        intSetOfOne.fcontains(1) shouldBe true
        intSetOfOne.fcontains(2) shouldBe false
        intSetOfTwo.fcontains(0) shouldBe false
        intSetOfTwo.fcontains(1) shouldBe true
        intSetOfTwo.fcontains(2) shouldBe true
        intSetOfTwo.fcontains(3) shouldBe false
        intSetOfThree.fcontains(0) shouldBe false
        intSetOfThree.fcontains(1) shouldBe true
        intSetOfThree.fcontains(2) shouldBe true
        intSetOfThree.fcontains(3) shouldBe true
        intSetOfThree.fcontains(4) shouldBe false
    }

    test("fcontainsAny") {
        intSetOfNone.fcontainsAny(intSetOfNone) shouldBe false

        intSetOfOne.fcontainsAny(intSetOfNone) shouldBe true
        intSetOfOne.fcontainsAny(intSetOfOne) shouldBe true
        intSetOfOne.fcontainsAny(intSetOfTwo) shouldBe true
        intSetOfOne.fcontainsAny(intSetMaverick) shouldBe false

        intSetOfTwo.fcontainsAny(intSetOfNone) shouldBe true
        intSetOfTwo.fcontainsAny(intSetOfOne) shouldBe true
        intSetOfTwo.fcontainsAny(intSetOfFour) shouldBe true
        intSetOfTwo.fcontainsAny(intSetMaverick) shouldBe false

        intSetOfThree.fcontainsAny(intSetOfNone) shouldBe true
        intSetOfThree.fcontainsAny(intSetOfFour) shouldBe true
        intSetOfThree.fcontainsAny(intSetMaverick) shouldBe false
    }

    test("fdropItem") {
        intSetOfNone.fdropItem(0).equals(intSetOfNone) shouldBe true
        intSetOfOne.fdropItem(0).equals(intSetOfOne) shouldBe true
        intSetOfOne.fdropItem(1).equals(intSetOfNone) shouldBe true

        intSetOfTwo.fdropItem(0).equals(intSetOfTwo) shouldBe true
        intSetOfTwo.fdropItem(1).equals(FKSet.ofi(2)) shouldBe true
        intSetOfTwo.fdropItem(2).equals(intSetOfOne) shouldBe true
        intSetOfTwo.fdropItem(3).equals(intSetOfTwo) shouldBe true

        intSetOfThree.fdropItem(0).equals(intSetOfThree) shouldBe true
        intSetOfThree.fdropItem(1).equals(FKSet.ofi(2, 3)) shouldBe true
        intSetOfThree.fdropItem(2).equals(FKSet.ofi(1, 3)) shouldBe true
        intSetOfThree.fdropItem(3).equals(intSetOfTwo) shouldBe true
        intSetOfThree.fdropItem(4).equals(intSetOfThree) shouldBe true
    }

    test("fdropAll") {
        intSetOfNone.fdropAll(intSetOfNone).equals(intSetOfNone) shouldBe true

        intSetOfOne.fdropAll(intSetOfNone).equals(intSetOfOne) shouldBe true
        intSetOfOne.fdropAll(intSetOfOne).equals(intSetOfNone) shouldBe true
        intSetOfOne.fdropAll(intSetOfTwo).equals(intSetOfNone) shouldBe true
        intSetOfOne.fdropAll(intSetMaverick).equals(intSetOfOne) shouldBe true

        intSetOfTwo.fdropAll(intSetOfNone).equals(intSetOfTwo) shouldBe true
        intSetOfTwo.fdropAll(intSetOfOne).equals(FKSet.ofi(2)) shouldBe true
        intSetOfTwo.fdropAll(intSetOfFour).equals(intSetOfNone) shouldBe true
        intSetOfTwo.fdropAll(intSetMaverick).equals(intSetOfTwo) shouldBe true

        intSetOfThree.fdropAll(intSetOfNone).equals(intSetOfThree) shouldBe true
        intSetOfThree.fdropAll(intSetOfFour).equals(intSetOfNone) shouldBe true
        intSetOfThree.fdropAll(intSetMaverick).equals(intSetOfThree) shouldBe true
    }

    test("fdropWhen") {
        intSetOfNone.fdropWhen { false }.equals(intSetOfNone) shouldBe true
        intSetOfNone.fdropWhen { true }.equals(intSetOfNone) shouldBe true

        intSetOfOne.fdropWhen { it == 1 }.equals(intSetOfNone) shouldBe true
        intSetOfOne.fdropWhen { it != 1 }.equals(intSetOfOne) shouldBe true

        intSetOfThree.fdropWhen { it < 2 }.equals(intSetOfTwo.fmap { it+1 }) shouldBe true
        intSetOfThree.fdropWhen { it >= 2 }.equals(intSetOfOne) shouldBe true
    }

    test("fempty") {
        intSetOfNone.fempty() shouldBe true
        intSetOfOne.fempty() shouldBe false
    }

    test("ffilter") {
        intSetOfNone.ffilter { false }.equals(intSetOfNone) shouldBe true
        intSetOfNone.ffilter { true }.equals(intSetOfNone) shouldBe true

        intSetOfOne.ffilter { it == 1 }.equals(intSetOfOne) shouldBe true
        intSetOfOne.ffilter { it != 1 }.equals(intSetOfNone) shouldBe true

        intSetOfThree.ffilter { it < 2 }.equals(intSetOfOne) shouldBe true
        intSetOfThree.ffilter { it >= 2 }.equals(intSetOfTwo.fmap { it+1 }) shouldBe true
    }
    
    test("ffilterNot") {
        intSetOfNone.ffilterNot { false }.equals(intSetOfNone) shouldBe true
        intSetOfNone.ffilterNot { true }.equals(intSetOfNone) shouldBe true

        intSetOfOne.ffilterNot { it == 1 }.equals(intSetOfNone) shouldBe true
        intSetOfOne.ffilterNot { it != 1 }.equals(intSetOfOne) shouldBe true

        intSetOfThree.ffilterNot { it < 2 }.equals(intSetOfTwo.fmap { it+1 }) shouldBe true
        intSetOfThree.ffilterNot { it >= 2 }.equals(intSetOfOne) shouldBe true
    }

    test("ffind") {
        intSetOfNone.ffind { false } shouldBe null
        intSetOfNone.ffind { true } shouldBe null

        intSetOfOne.ffind { it == 1 } shouldBe 1
        intSetOfOne.ffind { it != 1 } shouldBe null

        intSetOfThree.ffind { it < 2 } shouldBe 1
        intSetOfThree.ffind { it >= 2 } shouldBe null
    }

    test("fisSubsetOf") {
        intSetOfNone.fisSubsetOf(intSetOfNone) shouldBe true

        intSetOfOne.fisSubsetOf(intSetOfOne) shouldBe true
        intSetOfOne.fisSubsetOf(intSetOfNone) shouldBe false
        intSetOfNone.fisSubsetOf(intSetOfOne) shouldBe true
        intSetOfOne.fisSubsetOf(intSetMaverick) shouldBe false
        intSetMaverick.fisSubsetOf(intSetOfOne) shouldBe false
        intSetOfOne.fisSubsetOf(intSetOfTwo) shouldBe true
        intSetOfTwo.fisSubsetOf(intSetOfOne) shouldBe false

        intSetOfThree.fisSubsetOf(intSetOfThree) shouldBe true
        intSetOfThree.fisSubsetOf(intSetOfNone) shouldBe false
        intSetOfNone.fisSubsetOf(intSetOfThree) shouldBe true
        intSetOfThree.fisSubsetOf(intSetMaverick) shouldBe false
        intSetMaverick.fisSubsetOf(intSetOfThree) shouldBe false
        intSetOfThree.fisSubsetOf(intSetOfTwo) shouldBe false
        intSetOfTwo.fisSubsetOf(intSetOfThree) shouldBe true
    }

    test("fpick") {
        intSetOfNone.fpick() shouldBe null
        checkAll(repeats, Arb.fset<Int, Int>(Arb.int(),20..100)) { fs ->
            fs.fcontains(fs.fpick()!!) shouldBe true
        }
    }

    test("fAND") { 
        intSetOfNone.fAND(intSetOfNone).equals(intSetOfNone) shouldBe true
        intSetOfNone.fAND(intSetOfOne).equals(intSetOfNone) shouldBe true

        intSetOfOne.fAND(intSetOfNone).equals(intSetOfNone) shouldBe true
        intSetOfOne.fAND(intSetOfOne).equals(intSetOfOne) shouldBe true
        intSetOfOne.fAND(intSetOfThree).equals(intSetOfOne) shouldBe true
        intSetOfThree.fAND(intSetOfOne).equals(intSetOfOne) shouldBe true

        intSetOfTwo.fAND(intSetOfNone).equals(intSetOfNone) shouldBe true
        intSetOfTwo.fAND(intSetOfTwo).equals(intSetOfTwo) shouldBe true
        intSetOfTwo.fAND(intSetOfThree).equals(intSetOfTwo) shouldBe true
        intSetOfThree.fAND(intSetOfTwo).equals(intSetOfTwo) shouldBe true

        intSetOfThree.fAND(intSetOfNone).equals(intSetOfNone) shouldBe true
        intSetOfThree.fAND(intSetOfThree).equals(intSetOfThree) shouldBe true
        FKSet.ofi(2).fAND(intSetOfThree).equals(FKSet.ofi(2)) shouldBe true
        intSetOfThree.fAND(FKSet.ofi(2)).equals(FKSet.ofi(2)) shouldBe true
    }

    test("fNOT") {
        intSetOfNone.fNOT(intSetOfNone).equals(intSetOfNone) shouldBe true
        intSetOfNone.fNOT(intSetOfOne).equals(intSetOfNone) shouldBe true

        intSetOfOne.fNOT(intSetOfNone).equals(intSetOfOne) shouldBe true
        intSetOfOne.fNOT(intSetOfOne).equals(intSetOfNone) shouldBe true
        intSetOfOne.fNOT(intSetOfThree).equals(intSetOfNone) shouldBe true
        intSetOfThree.fNOT(intSetOfOne).equals(FKSet.ofi(2,3)) shouldBe true

        intSetOfTwo.fNOT(intSetOfNone).equals(intSetOfTwo) shouldBe true
        intSetOfTwo.fNOT(intSetOfTwo).equals(intSetOfNone) shouldBe true
        intSetOfTwo.fNOT(intSetOfThree).equals(intSetOfNone) shouldBe true
        intSetOfThree.fNOT(intSetOfTwo).equals(FKSet.ofi(3)) shouldBe true

        intSetOfThree.fNOT(intSetOfNone).equals(intSetOfThree) shouldBe true
        intSetOfThree.fNOT(intSetOfThree).equals(intSetOfNone) shouldBe true
        FKSet.ofi(2).fNOT(intSetOfThree).equals(intSetOfNone) shouldBe true
        intSetOfThree.fNOT(FKSet.ofi(2)).equals(FKSet.ofi(1,3)) shouldBe true
    }

    test("fOR") {
        intSetOfNone.fOR(intSetOfNone).equals(intSetOfNone) shouldBe true
        intSetOfOne.fOR(intSetOfNone).equals(intSetOfOne) shouldBe true
        intSetOfNone.fOR(intSetOfOne).equals(intSetOfOne) shouldBe true

        intSetOfTwo.fOR(intSetOfTwo).equals(intSetOfTwo) shouldBe true
        intSetOfTwo.fOR(intSetOfNone).equals(intSetOfTwo) shouldBe true
        intSetOfNone.fOR(intSetOfTwo).equals(intSetOfTwo) shouldBe true
        intSetOfTwo.fOR(intSetOfTwoOfst1).equals(intSetOfThree) shouldBe true
        intSetOfTwoOfst1.fOR(intSetOfTwo).equals(intSetOfThree) shouldBe true
        intSetOfTwo.fOR(intSetOfTwoOfst2).equals(intSetOfFour) shouldBe true
        intSetOfTwoOfst2.fOR(intSetOfTwo).equals(intSetOfFour) shouldBe true

        intSetOfThree.fOR(intSetOfNone).equals(intSetOfThree) shouldBe true
        intSetOfThree.fOR(intSetOfThree).equals(intSetOfThree) shouldBe true
        FKSet.ofi(2).fOR(intSetOfThree).equals(intSetOfThree) shouldBe true
        intSetOfThree.fOR(FKSet.ofi(2)).equals(intSetOfThree) shouldBe true
    }

    test("fXOR") {
        intSetOfNone.fXOR(intSetOfNone).equals(intSetOfNone) shouldBe true
        intSetOfNone.fXOR(intSetOfOne).equals(intSetOfOne) shouldBe true

        intSetOfOne.fXOR(intSetOfNone).equals(intSetOfOne) shouldBe true
        intSetOfOne.fXOR(intSetOfOne).equals(intSetOfNone) shouldBe true
        intSetOfOne.fXOR(intSetOfThree).equals(FKSet.ofi(2,3)) shouldBe true
        intSetOfThree.fXOR(intSetOfOne).equals(FKSet.ofi(2,3)) shouldBe true

        intSetOfTwo.fXOR(intSetOfNone).equals(intSetOfTwo) shouldBe true
        intSetOfTwo.fXOR(intSetOfTwo).equals(intSetOfNone) shouldBe true
        intSetOfTwo.fXOR(intSetOfThree).equals(FKSet.ofi(3)) shouldBe true
        intSetOfThree.fXOR(intSetOfTwo).equals(FKSet.ofi(3)) shouldBe true

        intSetOfThree.fXOR(intSetOfNone).equals(intSetOfThree) shouldBe true
        intSetOfThree.fXOR(intSetOfThree).equals(intSetOfNone) shouldBe true
        FKSet.ofi(2).fXOR(intSetOfThree).equals(FKSet.ofi(1,3)) shouldBe true
        intSetOfThree.fXOR(FKSet.ofi(2)).equals(FKSet.ofi(1,3)) shouldBe true
    }
})
