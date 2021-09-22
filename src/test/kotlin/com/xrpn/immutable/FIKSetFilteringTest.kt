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
        intSetOfNone.fdropItem(0).strongEqual(intSetOfNone) shouldBe true
        intSetOfOne.fdropItem(0).strongEqual(intSetOfOne) shouldBe true
        intSetOfOne.fdropItem(1).strongEqual(intSetOfNone) shouldBe true

        intSetOfTwo.fdropItem(0).strongEqual(intSetOfTwo) shouldBe true
        intSetOfTwo.fdropItem(1).strongEqual(FKSet.ofi(2)) shouldBe true
        intSetOfTwo.fdropItem(2).strongEqual(intSetOfOne) shouldBe true
        intSetOfTwo.fdropItem(3).strongEqual(intSetOfTwo) shouldBe true

        intSetOfThree.fdropItem(0).strongEqual(intSetOfThree) shouldBe true
        intSetOfThree.fdropItem(1).strongEqual(FKSet.ofi(2, 3)) shouldBe true
        intSetOfThree.fdropItem(2).strongEqual(FKSet.ofi(1, 3)) shouldBe true
        intSetOfThree.fdropItem(3).strongEqual(intSetOfTwo) shouldBe true
        intSetOfThree.fdropItem(4).strongEqual(intSetOfThree) shouldBe true
    }

    test("fdropAll") {
        intSetOfNone.fdropAll(intSetOfNone).strongEqual(intSetOfNone) shouldBe true

        intSetOfOne.fdropAll(intSetOfNone).strongEqual(intSetOfOne) shouldBe true
        intSetOfOne.fdropAll(intSetOfOne).strongEqual(intSetOfNone) shouldBe true
        intSetOfOne.fdropAll(intSetOfTwo).strongEqual(intSetOfNone) shouldBe true
        intSetOfOne.fdropAll(intSetMaverick).strongEqual(intSetOfOne) shouldBe true

        intSetOfTwo.fdropAll(intSetOfNone).strongEqual(intSetOfTwo) shouldBe true
        intSetOfTwo.fdropAll(intSetOfOne).strongEqual(FKSet.ofi(2)) shouldBe true
        intSetOfTwo.fdropAll(intSetOfFour).strongEqual(intSetOfNone) shouldBe true
        intSetOfTwo.fdropAll(intSetMaverick).strongEqual(intSetOfTwo) shouldBe true

        intSetOfThree.fdropAll(intSetOfNone).strongEqual(intSetOfThree) shouldBe true
        intSetOfThree.fdropAll(intSetOfFour).strongEqual(intSetOfNone) shouldBe true
        intSetOfThree.fdropAll(intSetMaverick).strongEqual(intSetOfThree) shouldBe true
    }

    test("fdropWhen") {
        intSetOfNone.fdropWhen { false }.strongEqual(intSetOfNone) shouldBe true
        intSetOfNone.fdropWhen { true }.strongEqual(intSetOfNone) shouldBe true

        intSetOfOne.fdropWhen { it == 1 }.strongEqual(intSetOfNone) shouldBe true
        intSetOfOne.fdropWhen { it != 1 }.strongEqual(intSetOfOne) shouldBe true

        intSetOfThree.fdropWhen { it < 2 }.strongEqual(intSetOfTwo.fmap { it+1 }) shouldBe true
        intSetOfThree.fdropWhen { it >= 2 }.strongEqual(intSetOfOne) shouldBe true
    }

    test("fempty") {
        intSetOfNone.fempty() shouldBe true
        intSetOfOne.fempty() shouldBe false
    }

    test("ffilter") {
        intSetOfNone.ffilter { false }.strongEqual(intSetOfNone) shouldBe true
        intSetOfNone.ffilter { true }.strongEqual(intSetOfNone) shouldBe true

        intSetOfOne.ffilter { it == 1 }.strongEqual(intSetOfOne) shouldBe true
        intSetOfOne.ffilter { it != 1 }.strongEqual(intSetOfNone) shouldBe true

        intSetOfThree.ffilter { it < 2 }.strongEqual(intSetOfOne) shouldBe true
        intSetOfThree.ffilter { it >= 2 }.strongEqual(intSetOfTwo.fmap { it+1 }) shouldBe true
    }
    
    test("ffilterNot") {
        intSetOfNone.ffilterNot { false }.strongEqual(intSetOfNone) shouldBe true
        intSetOfNone.ffilterNot { true }.strongEqual(intSetOfNone) shouldBe true

        intSetOfOne.ffilterNot { it == 1 }.strongEqual(intSetOfNone) shouldBe true
        intSetOfOne.ffilterNot { it != 1 }.strongEqual(intSetOfOne) shouldBe true

        intSetOfThree.ffilterNot { it < 2 }.strongEqual(intSetOfTwo.fmap { it+1 }) shouldBe true
        intSetOfThree.ffilterNot { it >= 2 }.strongEqual(intSetOfOne) shouldBe true
    }

    test("ffind") {
        intSetOfNone.ffindDistinct { false } shouldBe null
        intSetOfNone.ffindDistinct { true } shouldBe null

        intSetOfOne.ffindDistinct { it == 1 } shouldBe 1
        intSetOfOne.ffindDistinct { it != 1 } shouldBe null

        intSetOfThree.ffindDistinct { it < 2 } shouldBe 1
        intSetOfThree.ffindDistinct { it >= 2 } shouldBe null
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
        intSetOfNone.fAND(intSetOfNone).strongEqual(intSetOfNone) shouldBe true
        intSetOfNone.fAND(intSetOfOne).strongEqual(intSetOfNone) shouldBe true

        intSetOfOne.fAND(intSetOfNone).strongEqual(intSetOfNone) shouldBe true
        intSetOfOne.fAND(intSetOfOne).strongEqual(intSetOfOne) shouldBe true
        intSetOfOne.fAND(intSetOfThree).strongEqual(intSetOfOne) shouldBe true
        intSetOfThree.fAND(intSetOfOne).strongEqual(intSetOfOne) shouldBe true

        intSetOfTwo.fAND(intSetOfNone).strongEqual(intSetOfNone) shouldBe true
        intSetOfTwo.fAND(intSetOfTwo).strongEqual(intSetOfTwo) shouldBe true
        intSetOfTwo.fAND(intSetOfThree).strongEqual(intSetOfTwo) shouldBe true
        intSetOfThree.fAND(intSetOfTwo).strongEqual(intSetOfTwo) shouldBe true

        intSetOfThree.fAND(intSetOfNone).strongEqual(intSetOfNone) shouldBe true
        intSetOfThree.fAND(intSetOfThree).strongEqual(intSetOfThree) shouldBe true
        FKSet.ofi(2).fAND(intSetOfThree).strongEqual(FKSet.ofi(2)) shouldBe true
        intSetOfThree.fAND(FKSet.ofi(2)).strongEqual(FKSet.ofi(2)) shouldBe true
    }

    test("fNOT") {
        intSetOfNone.fNOT(intSetOfNone).strongEqual(intSetOfNone) shouldBe true
        intSetOfNone.fNOT(intSetOfOne).strongEqual(intSetOfNone) shouldBe true

        intSetOfOne.fNOT(intSetOfNone).strongEqual(intSetOfOne) shouldBe true
        intSetOfOne.fNOT(intSetOfOne).strongEqual(intSetOfNone) shouldBe true
        intSetOfOne.fNOT(intSetOfThree).strongEqual(intSetOfNone) shouldBe true
        intSetOfThree.fNOT(intSetOfOne).strongEqual(FKSet.ofi(2,3)) shouldBe true

        intSetOfTwo.fNOT(intSetOfNone).strongEqual(intSetOfTwo) shouldBe true
        intSetOfTwo.fNOT(intSetOfTwo).strongEqual(intSetOfNone) shouldBe true
        intSetOfTwo.fNOT(intSetOfThree).strongEqual(intSetOfNone) shouldBe true
        intSetOfThree.fNOT(intSetOfTwo).strongEqual(FKSet.ofi(3)) shouldBe true

        intSetOfThree.fNOT(intSetOfNone).strongEqual(intSetOfThree) shouldBe true
        intSetOfThree.fNOT(intSetOfThree).strongEqual(intSetOfNone) shouldBe true
        FKSet.ofi(2).fNOT(intSetOfThree).strongEqual(intSetOfNone) shouldBe true
        intSetOfThree.fNOT(FKSet.ofi(2)).strongEqual(FKSet.ofi(1,3)) shouldBe true
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

        intSetOfThree.fOR(intSetOfNone).strongEqual(intSetOfThree) shouldBe true
        intSetOfThree.fOR(intSetOfThree).strongEqual(intSetOfThree) shouldBe true
        FKSet.ofi(2).fOR(intSetOfThree).strongEqual(intSetOfThree) shouldBe true
        intSetOfThree.fOR(FKSet.ofi(2)).strongEqual(intSetOfThree) shouldBe true
    }

    test("fXOR") {
        intSetOfNone.fXOR(intSetOfNone).strongEqual(intSetOfNone) shouldBe true
        intSetOfNone.fXOR(intSetOfOne).strongEqual(intSetOfOne) shouldBe true

        intSetOfOne.fXOR(intSetOfNone).strongEqual(intSetOfOne) shouldBe true
        intSetOfOne.fXOR(intSetOfOne).strongEqual(intSetOfNone) shouldBe true
        intSetOfOne.fXOR(intSetOfThree).strongEqual(FKSet.ofi(2,3)) shouldBe true
        intSetOfThree.fXOR(intSetOfOne).strongEqual(FKSet.ofi(2,3)) shouldBe true

        intSetOfTwo.fXOR(intSetOfNone).strongEqual(intSetOfTwo) shouldBe true
        intSetOfTwo.fXOR(intSetOfTwo).strongEqual(intSetOfNone) shouldBe true
        intSetOfTwo.fXOR(intSetOfThree).strongEqual(FKSet.ofi(3)) shouldBe true
        intSetOfThree.fXOR(intSetOfTwo).strongEqual(FKSet.ofi(3)) shouldBe true

        intSetOfThree.fXOR(intSetOfNone).strongEqual(intSetOfThree) shouldBe true
        intSetOfThree.fXOR(intSetOfThree).strongEqual(intSetOfNone) shouldBe true
        FKSet.ofi(2).fXOR(intSetOfThree).strongEqual(FKSet.ofi(1,3)) shouldBe true
        intSetOfThree.fXOR(FKSet.ofi(2)).strongEqual(FKSet.ofi(1,3)) shouldBe true
    }
})
