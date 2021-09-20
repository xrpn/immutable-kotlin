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

    test("fcontainsSoO") {
        intSetOfNone.fcontainsSoO(1.toISoO()) shouldBe false
        intSetOfOne.fcontainsSoO(0.toISoO()) shouldBe false
        intSetOfOne.fcontainsSoO(1.toISoO()) shouldBe true
        intSetOfOne.fcontainsSoO(2.toISoO()) shouldBe false
        intSetOfTwo.fcontainsSoO(0.toISoO()) shouldBe false
        intSetOfTwo.fcontainsSoO(1.toISoO()) shouldBe true
        intSetOfTwo.fcontainsSoO(2.toISoO()) shouldBe true
        intSetOfTwo.fcontainsSoO(3.toISoO()) shouldBe false
        intSetOfThree.fcontainsSoO(0.toISoO()) shouldBe false
        intSetOfThree.fcontainsSoO(1.toISoO()) shouldBe true
        intSetOfThree.fcontainsSoO(2.toISoO()) shouldBe true
        intSetOfThree.fcontainsSoO(3.toISoO()) shouldBe true
        intSetOfThree.fcontainsSoO(4.toISoO()) shouldBe false
    }

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

    test("fcontainsAny SoO") {
        intSetOfNone.fcontainsAny(1.toISoO()) shouldBe false
        intSetOfOne.fcontainsAny(0.toISoO()) shouldBe false
        intSetOfOne.fcontainsAny(1.toISoO()) shouldBe true
        intSetOfOne.fcontainsAny(2.toISoO()) shouldBe false
        intSetOfTwo.fcontainsAny(0.toISoO()) shouldBe false
        intSetOfTwo.fcontainsAny(1.toISoO()) shouldBe true
        intSetOfTwo.fcontainsAny(2.toISoO()) shouldBe true
        intSetOfTwo.fcontainsAny(3.toISoO()) shouldBe false
        intSetOfThree.fcontainsAny(0.toISoO()) shouldBe false
        intSetOfThree.fcontainsAny(1.toISoO()) shouldBe true
        intSetOfThree.fcontainsAny(2.toISoO()) shouldBe true
        intSetOfThree.fcontainsAny(3.toISoO()) shouldBe true
        intSetOfThree.fcontainsAny(4.toISoO()) shouldBe false
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

    test("fdropSoO") {
        intSetOfNone.fdropSoO(0.toISoO()).equal(intSetOfNone) shouldBe true
        intSetOfOne.fdropSoO(0.toISoO()).equal(intSetOfOne) shouldBe true
        intSetOfOne.fdropSoO(1.toISoO()).equal(intSetOfNone) shouldBe true
        
        intSetOfTwo.fdropSoO(0.toISoO()).equal(intSetOfTwo) shouldBe true
        intSetOfTwo.fdropSoO(1.toISoO()).equal(FKSet.ofi(2)) shouldBe true
        intSetOfTwo.fdropSoO(2.toISoO()).equal(intSetOfOne) shouldBe true
        intSetOfTwo.fdropSoO(3.toISoO()).equal(intSetOfTwo) shouldBe true

        intSetOfThree.fdropSoO(0.toISoO()).equal(intSetOfThree) shouldBe true
        intSetOfThree.fdropSoO(1.toISoO()).equal(FKSet.ofi(2, 3)) shouldBe true
        intSetOfThree.fdropSoO(2.toISoO()).equal(FKSet.ofi(1, 3)) shouldBe true
        intSetOfThree.fdropSoO(3.toISoO()).equal(intSetOfTwo) shouldBe true
        intSetOfThree.fdropSoO(4.toISoO()).equal(intSetOfThree) shouldBe true
    }

    test("fdropItem") {
        intSetOfNone.fdropItem(0).equal(intSetOfNone) shouldBe true
        intSetOfOne.fdropItem(0).equal(intSetOfOne) shouldBe true
        intSetOfOne.fdropItem(1).equal(intSetOfNone) shouldBe true

        intSetOfTwo.fdropItem(0).equal(intSetOfTwo) shouldBe true
        intSetOfTwo.fdropItem(1).equal(FKSet.ofi(2)) shouldBe true
        intSetOfTwo.fdropItem(2).equal(intSetOfOne) shouldBe true
        intSetOfTwo.fdropItem(3).equal(intSetOfTwo) shouldBe true

        intSetOfThree.fdropItem(0).equal(intSetOfThree) shouldBe true
        intSetOfThree.fdropItem(1).equal(FKSet.ofi(2, 3)) shouldBe true
        intSetOfThree.fdropItem(2).equal(FKSet.ofi(1, 3)) shouldBe true
        intSetOfThree.fdropItem(3).equal(intSetOfTwo) shouldBe true
        intSetOfThree.fdropItem(4).equal(intSetOfThree) shouldBe true
    }

    test("fdropAll SoO") {
        intSetOfNone.fdropAll(0.toISoO()).equal(intSetOfNone) shouldBe true
        intSetOfOne.fdropAll(0.toISoO()).equal(intSetOfOne) shouldBe true
        intSetOfOne.fdropAll(1.toISoO()).equal(intSetOfNone) shouldBe true

        intSetOfTwo.fdropAll(0.toISoO()).equal(intSetOfTwo) shouldBe true
        intSetOfTwo.fdropAll(1.toISoO()).equal(FKSet.ofi(2)) shouldBe true
        intSetOfTwo.fdropAll(2.toISoO()).equal(intSetOfOne) shouldBe true
        intSetOfTwo.fdropAll(3.toISoO()).equal(intSetOfTwo) shouldBe true

        intSetOfThree.fdropAll(0.toISoO()).equal(intSetOfThree) shouldBe true
        intSetOfThree.fdropAll(1.toISoO()).equal(FKSet.ofi(2, 3)) shouldBe true
        intSetOfThree.fdropAll(2.toISoO()).equal(FKSet.ofi(1, 3)) shouldBe true
        intSetOfThree.fdropAll(3.toISoO()).equal(intSetOfTwo) shouldBe true
        intSetOfThree.fdropAll(4.toISoO()).equal(intSetOfThree) shouldBe true
    }

    test("fdropAll") {
        intSetOfNone.fdropAll(intSetOfNone).equal(intSetOfNone) shouldBe true

        intSetOfOne.fdropAll(intSetOfNone).equal(intSetOfOne) shouldBe true
        intSetOfOne.fdropAll(intSetOfOne).equal(intSetOfNone) shouldBe true
        intSetOfOne.fdropAll(intSetOfTwo).equal(intSetOfNone) shouldBe true
        intSetOfOne.fdropAll(intSetMaverick).equal(intSetOfOne) shouldBe true

        intSetOfTwo.fdropAll(intSetOfNone).equal(intSetOfTwo) shouldBe true
        intSetOfTwo.fdropAll(intSetOfOne).equal(FKSet.ofi(2)) shouldBe true
        intSetOfTwo.fdropAll(intSetOfFour).equal(intSetOfNone) shouldBe true
        intSetOfTwo.fdropAll(intSetMaverick).equal(intSetOfTwo) shouldBe true

        intSetOfThree.fdropAll(intSetOfNone).equal(intSetOfThree) shouldBe true
        intSetOfThree.fdropAll(intSetOfFour).equal(intSetOfNone) shouldBe true
        intSetOfThree.fdropAll(intSetMaverick).equal(intSetOfThree) shouldBe true
    }

    test("fdropWhen") {
        intSetOfNone.fdropWhen { false }.equal(intSetOfNone) shouldBe true
        intSetOfNone.fdropWhen { true }.equal(intSetOfNone) shouldBe true

        intSetOfOne.fdropWhen { it == 1 }.equal(intSetOfNone) shouldBe true
        intSetOfOne.fdropWhen { it != 1 }.equal(intSetOfOne) shouldBe true

        intSetOfThree.fdropWhen { it < 2 }.equal(intSetOfTwo.fmap { it+1 }) shouldBe true
        intSetOfThree.fdropWhen { it >= 2 }.equal(intSetOfOne) shouldBe true
    }

    test("fempty") {
        intSetOfNone.fempty() shouldBe true
        intSetOfOne.fempty() shouldBe false
    }

    test("ffilter") {
        intSetOfNone.ffilter { false }.equal(intSetOfNone) shouldBe true
        intSetOfNone.ffilter { true }.equal(intSetOfNone) shouldBe true

        intSetOfOne.ffilter { it == 1 }.equal(intSetOfOne) shouldBe true
        intSetOfOne.ffilter { it != 1 }.equal(intSetOfNone) shouldBe true

        intSetOfThree.ffilter { it < 2 }.equal(intSetOfOne) shouldBe true
        intSetOfThree.ffilter { it >= 2 }.equal(intSetOfTwo.fmap { it+1 }) shouldBe true
    }
    
    test("ffilterNot") {
        intSetOfNone.ffilterNot { false }.equal(intSetOfNone) shouldBe true
        intSetOfNone.ffilterNot { true }.equal(intSetOfNone) shouldBe true

        intSetOfOne.ffilterNot { it == 1 }.equal(intSetOfNone) shouldBe true
        intSetOfOne.ffilterNot { it != 1 }.equal(intSetOfOne) shouldBe true

        intSetOfThree.ffilterNot { it < 2 }.equal(intSetOfTwo.fmap { it+1 }) shouldBe true
        intSetOfThree.ffilterNot { it >= 2 }.equal(intSetOfOne) shouldBe true
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
        intSetOfNone.fAND(intSetOfNone).equal(intSetOfNone) shouldBe true
        intSetOfNone.fAND(intSetOfOne).equal(intSetOfNone) shouldBe true

        intSetOfOne.fAND(intSetOfNone).equal(intSetOfNone) shouldBe true
        intSetOfOne.fAND(intSetOfOne).equal(intSetOfOne) shouldBe true
        intSetOfOne.fAND(intSetOfThree).equal(intSetOfOne) shouldBe true
        intSetOfThree.fAND(intSetOfOne).equal(intSetOfOne) shouldBe true

        intSetOfTwo.fAND(intSetOfNone).equal(intSetOfNone) shouldBe true
        intSetOfTwo.fAND(intSetOfTwo).equal(intSetOfTwo) shouldBe true
        intSetOfTwo.fAND(intSetOfThree).equal(intSetOfTwo) shouldBe true
        intSetOfThree.fAND(intSetOfTwo).equal(intSetOfTwo) shouldBe true

        intSetOfThree.fAND(intSetOfNone).equal(intSetOfNone) shouldBe true
        intSetOfThree.fAND(intSetOfThree).equal(intSetOfThree) shouldBe true
        FKSet.ofi(2).fAND(intSetOfThree).equal(FKSet.ofi(2)) shouldBe true
        intSetOfThree.fAND(FKSet.ofi(2)).equal(FKSet.ofi(2)) shouldBe true
    }

    test("fNOT") {
        intSetOfNone.fNOT(intSetOfNone).equal(intSetOfNone) shouldBe true
        intSetOfNone.fNOT(intSetOfOne).equal(intSetOfNone) shouldBe true

        intSetOfOne.fNOT(intSetOfNone).equal(intSetOfOne) shouldBe true
        intSetOfOne.fNOT(intSetOfOne).equal(intSetOfNone) shouldBe true
        intSetOfOne.fNOT(intSetOfThree).equal(intSetOfNone) shouldBe true
        intSetOfThree.fNOT(intSetOfOne).equal(FKSet.ofi(2,3)) shouldBe true

        intSetOfTwo.fNOT(intSetOfNone).equal(intSetOfTwo) shouldBe true
        intSetOfTwo.fNOT(intSetOfTwo).equal(intSetOfNone) shouldBe true
        intSetOfTwo.fNOT(intSetOfThree).equal(intSetOfNone) shouldBe true
        intSetOfThree.fNOT(intSetOfTwo).equal(FKSet.ofi(3)) shouldBe true

        intSetOfThree.fNOT(intSetOfNone).equal(intSetOfThree) shouldBe true
        intSetOfThree.fNOT(intSetOfThree).equal(intSetOfNone) shouldBe true
        FKSet.ofi(2).fNOT(intSetOfThree).equal(intSetOfNone) shouldBe true
        intSetOfThree.fNOT(FKSet.ofi(2)).equal(FKSet.ofi(1,3)) shouldBe true
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

        intSetOfThree.fOR(intSetOfNone).equal(intSetOfThree) shouldBe true
        intSetOfThree.fOR(intSetOfThree).equal(intSetOfThree) shouldBe true
        FKSet.ofi(2).fOR(intSetOfThree).equal(intSetOfThree) shouldBe true
        intSetOfThree.fOR(FKSet.ofi(2)).equal(intSetOfThree) shouldBe true
    }

    test("fXOR") {
        intSetOfNone.fXOR(intSetOfNone).equal(intSetOfNone) shouldBe true
        intSetOfNone.fXOR(intSetOfOne).equal(intSetOfOne) shouldBe true

        intSetOfOne.fXOR(intSetOfNone).equal(intSetOfOne) shouldBe true
        intSetOfOne.fXOR(intSetOfOne).equal(intSetOfNone) shouldBe true
        intSetOfOne.fXOR(intSetOfThree).equal(FKSet.ofi(2,3)) shouldBe true
        intSetOfThree.fXOR(intSetOfOne).equal(FKSet.ofi(2,3)) shouldBe true

        intSetOfTwo.fXOR(intSetOfNone).equal(intSetOfTwo) shouldBe true
        intSetOfTwo.fXOR(intSetOfTwo).equal(intSetOfNone) shouldBe true
        intSetOfTwo.fXOR(intSetOfThree).equal(FKSet.ofi(3)) shouldBe true
        intSetOfThree.fXOR(intSetOfTwo).equal(FKSet.ofi(3)) shouldBe true

        intSetOfThree.fXOR(intSetOfNone).equal(intSetOfThree) shouldBe true
        intSetOfThree.fXOR(intSetOfThree).equal(intSetOfNone) shouldBe true
        FKSet.ofi(2).fXOR(intSetOfThree).equal(FKSet.ofi(1,3)) shouldBe true
        intSetOfThree.fXOR(FKSet.ofi(2)).equal(FKSet.ofi(1,3)) shouldBe true
    }
})
