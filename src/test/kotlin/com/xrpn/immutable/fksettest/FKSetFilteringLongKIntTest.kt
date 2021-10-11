package com.xrpn.immutable.fksettest

import com.xrpn.immutable.FKSet
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val longKKSetOfNone = FKSet.ofk(*arrayOf<Long>())
private val longKKSetOfOne = FKSet.ofk(1L).rrne()!!
private val longKKSetOfOne3 = FKSet.ofk(3L).rrne()!!
private val longKKSetOfTwo = FKSet.ofk(1L, 2L).rrne()!!
private val longKKSetOfTwoOfst1 = FKSet.ofk(2L, 3L).rrne()!!
private val longKKSetOfTwoOfst2 = FKSet.ofk(3L, 4L).rrne()!!
private val longKKSetOfThree = FKSet.ofk(1L, 2L, 3L).rrne()!!
private val longKKSetOfFour = FKSet.ofk(1L, 2L, 3L, 4L).rrne()!!
private val longKKSetMaverick = FKSet.ofk(113L, 97L).rrne()!!

private val longISetOfNone = FKSet.ofi(*arrayOf<Long>())
private val longISetOfOne = FKSet.ofi(1L).rne()!!
private val longISetOfOne3 = FKSet.ofi(3L).rne()!!
private val longISetOfTwo = FKSet.ofi(1L, 2L).rne()!!
private val longISetOfTwoOfst1 = FKSet.ofi(2L, 3L).rne()!!
private val longISetOfTwoOfst2 = FKSet.ofi(3L, 4L).rne()!!
private val longISetOfThree = FKSet.ofi(1L, 2L, 3L).rne()!!
private val longISetOfFour = FKSet.ofi(1L, 2L, 3L, 4L).rne()!!
private val longISetMaverick = FKSet.ofi(113L, 97L).rne()!!

class FKSetFilteringLongKIntTest : FunSpec({

    beforeTest {}

    test("fcontains") {
        longKKSetOfNone.fcontains(1) shouldBe false
        longKKSetOfOne.fcontains(0) shouldBe false
        longKKSetOfOne.fcontains(1) shouldBe true
        longKKSetOfOne.fcontains(2) shouldBe false
        longKKSetOfTwo.fcontains(0) shouldBe false
        longKKSetOfTwo.fcontains(1) shouldBe true
        longKKSetOfTwo.fcontains(2) shouldBe true
        longKKSetOfTwo.fcontains(3) shouldBe false
        longKKSetOfThree.fcontains(0) shouldBe false
        longKKSetOfThree.fcontains(1) shouldBe true
        longKKSetOfThree.fcontains(2) shouldBe true
        longKKSetOfThree.fcontains(3) shouldBe true
        longKKSetOfThree.fcontains(4) shouldBe false
        
        longISetOfNone.fcontains(1) shouldBe false
        longISetOfOne.fcontains(0) shouldBe false
        longISetOfOne.fcontains(1) shouldBe true
        longISetOfOne.fcontains(2) shouldBe false
        longISetOfTwo.fcontains(0) shouldBe false
        longISetOfTwo.fcontains(1) shouldBe true
        longISetOfTwo.fcontains(2) shouldBe true
        longISetOfTwo.fcontains(3) shouldBe false
        longISetOfThree.fcontains(0) shouldBe false
        longISetOfThree.fcontains(1) shouldBe true
        longISetOfThree.fcontains(2) shouldBe true
        longISetOfThree.fcontains(3) shouldBe true
        longISetOfThree.fcontains(4) shouldBe false

    }

    test("fcontainsAny") {
        longKKSetOfNone.fcontainsAny(longKKSetOfNone) shouldBe false

        longKKSetOfOne.fcontainsAny(longKKSetOfNone) shouldBe true
        longKKSetOfOne.fcontainsAny(longKKSetOfOne) shouldBe true
        longKKSetOfOne.fcontainsAny(longKKSetOfTwo) shouldBe true
        longKKSetOfOne.fcontainsAny(longKKSetMaverick) shouldBe false

        longKKSetOfTwo.fcontainsAny(longKKSetOfNone) shouldBe true
        longKKSetOfTwo.fcontainsAny(longKKSetOfOne) shouldBe true
        longKKSetOfTwo.fcontainsAny(longKKSetOfFour) shouldBe true
        longKKSetOfTwo.fcontainsAny(longKKSetMaverick) shouldBe false

        longKKSetOfThree.fcontainsAny(longKKSetOfNone) shouldBe true
        longKKSetOfThree.fcontainsAny(longKKSetOfFour) shouldBe true
        longKKSetOfThree.fcontainsAny(longKKSetMaverick) shouldBe false

        longISetOfNone.fcontainsAny(longISetOfNone) shouldBe false

        longISetOfOne.fcontainsAny(longISetOfNone) shouldBe true
        longISetOfOne.fcontainsAny(longISetOfOne) shouldBe true
        longISetOfOne.fcontainsAny(longISetOfTwo) shouldBe true
        longISetOfOne.fcontainsAny(longISetMaverick) shouldBe false

        longISetOfTwo.fcontainsAny(longISetOfNone) shouldBe true
        longISetOfTwo.fcontainsAny(longISetOfOne) shouldBe true
        longISetOfTwo.fcontainsAny(longISetOfFour) shouldBe true
        longISetOfTwo.fcontainsAny(longISetMaverick) shouldBe false

        longISetOfThree.fcontainsAny(longISetOfNone) shouldBe true
        longISetOfThree.fcontainsAny(longISetOfFour) shouldBe true
        longISetOfThree.fcontainsAny(longISetMaverick) shouldBe false

        // mixed mode

        longKKSetOfThree.fcontainsAny(longISetOfNone) shouldBe true
        longKKSetOfThree.fcontainsAny(longISetOfFour) shouldBe true
        longKKSetOfThree.fcontainsAny(longISetMaverick) shouldBe false

        longISetOfThree.fcontainsAny(longKKSetOfNone) shouldBe true
        longISetOfThree.fcontainsAny(longKKSetOfFour) shouldBe true
        longISetOfThree.fcontainsAny(longKKSetMaverick) shouldBe false

    }

    test("fdropItem") {
        longKKSetOfNone.fdropItem(0).equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.fdropItem(0).equals(longKKSetOfOne) shouldBe true
        longKKSetOfOne.fdropItem(1).equals(longKKSetOfNone) shouldBe true

        longKKSetOfTwo.fdropItem(0).equals(longKKSetOfTwo) shouldBe true
        longKKSetOfTwo.fdropItem(1).equals(FKSet.ofk(2L)) shouldBe true
        longKKSetOfTwo.fdropItem(2).equals(longKKSetOfOne) shouldBe true
        longKKSetOfTwo.fdropItem(3).equals(longKKSetOfTwo) shouldBe true

        longKKSetOfThree.fdropItem(0).equals(longKKSetOfThree) shouldBe true
        longKKSetOfThree.fdropItem(1).equals(FKSet.ofk(2L, 3L)) shouldBe true
        longKKSetOfThree.fdropItem(2).equals(FKSet.ofk(1L, 3L)) shouldBe true
        longKKSetOfThree.fdropItem(3).equals(longKKSetOfTwo) shouldBe true
        longKKSetOfThree.fdropItem(4).equals(longKKSetOfThree) shouldBe true

        longISetOfNone.fdropItem(0).equals(longISetOfNone) shouldBe true
        longISetOfOne.fdropItem(0).equals(longISetOfOne) shouldBe true
        longISetOfOne.fdropItem(1).equals(longISetOfNone) shouldBe true

        longISetOfTwo.fdropItem(0).equals(longISetOfTwo) shouldBe true
        longISetOfTwo.fdropItem(1).equals(FKSet.ofi(2L)) shouldBe true
        longISetOfTwo.fdropItem(2).equals(longISetOfOne) shouldBe true
        longISetOfTwo.fdropItem(3).equals(longISetOfTwo) shouldBe true

        longISetOfThree.fdropItem(0).equals(longISetOfThree) shouldBe true
        longISetOfThree.fdropItem(1).equals(FKSet.ofi(2L, 3L)) shouldBe true
        longISetOfThree.fdropItem(2).equals(FKSet.ofi(1L, 3L)) shouldBe true
        longISetOfThree.fdropItem(3).equals(longISetOfTwo) shouldBe true
        longISetOfThree.fdropItem(4).equals(longISetOfThree) shouldBe true

        // mixed mode

        longISetOfThree.fdropItem(3).equal(longKKSetOfTwo) shouldBe true
        longKKSetOfThree.fdropItem(3).equal(longISetOfTwo) shouldBe true
    }

    test("fdropAll") {
        longKKSetOfNone.fdropAll(longKKSetOfNone).equals(longKKSetOfNone) shouldBe true

        longKKSetOfOne.fdropAll(longKKSetOfNone).equals(longKKSetOfOne) shouldBe true
        longKKSetOfOne.fdropAll(longKKSetOfOne).equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.fdropAll(longKKSetOfTwo).equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.fdropAll(longKKSetMaverick).equals(longKKSetOfOne) shouldBe true

        longKKSetOfTwo.fdropAll(longKKSetOfNone).equals(longKKSetOfTwo) shouldBe true
        longKKSetOfTwo.fdropAll(longKKSetOfOne).equals(FKSet.ofk(2)) shouldBe false
        longKKSetOfTwo.fdropAll(longKKSetOfOne).equals(FKSet.ofk(2L)) shouldBe true
        longKKSetOfTwo.fdropAll(longKKSetOfFour).equals(longKKSetOfNone) shouldBe true
        longKKSetOfTwo.fdropAll(longKKSetMaverick).equals(longKKSetOfTwo) shouldBe true

        longKKSetOfThree.fdropAll(longKKSetOfNone).equals(longKKSetOfThree) shouldBe true
        longKKSetOfThree.fdropAll(longKKSetOfFour).equals(longKKSetOfNone) shouldBe true
        longKKSetOfThree.fdropAll(longKKSetMaverick).equals(longKKSetOfThree) shouldBe true

        longISetOfNone.fdropAll(longISetOfNone).equals(longISetOfNone) shouldBe true

        longISetOfOne.fdropAll(longISetOfNone).equals(longISetOfOne) shouldBe true
        longISetOfOne.fdropAll(longISetOfOne).equals(longISetOfNone) shouldBe true
        longISetOfOne.fdropAll(longISetOfTwo).equals(longISetOfNone) shouldBe true
        longISetOfOne.fdropAll(longISetMaverick).equals(longISetOfOne) shouldBe true

        longISetOfTwo.fdropAll(longISetOfNone).equals(longISetOfTwo) shouldBe true
        longISetOfTwo.fdropAll(longISetOfOne).equals(FKSet.ofi(2)) shouldBe false
        longISetOfTwo.fdropAll(longISetOfOne).equals(FKSet.ofi(2L)) shouldBe true
        longISetOfTwo.fdropAll(longISetOfOne).equal(FKSet.ofi(2)) shouldBe true
        longISetOfTwo.fdropAll(longISetOfFour).equals(longISetOfNone) shouldBe true
        longISetOfTwo.fdropAll(longISetMaverick).equals(longISetOfTwo) shouldBe true

        longISetOfThree.fdropAll(longISetOfNone).equals(longISetOfThree) shouldBe true
        longISetOfThree.fdropAll(longISetOfFour).equals(longISetOfNone) shouldBe true
        longISetOfThree.fdropAll(longISetMaverick).equals(longISetOfThree) shouldBe true

        // mixed mode

        longISetOfThree.fdropAll(longKKSetOfFour).equal(longISetOfNone) shouldBe true
        longKKSetOfThree.fdropAll(longISetOfFour).equal(longISetOfNone) shouldBe true

    }

    test("fdropWhen") {
        longKKSetOfNone.fdropWhen { false }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfNone.fdropWhen { true }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.fdropWhen { it == 1L }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.fdropWhen { it != 1L }.equals(longKKSetOfOne) shouldBe true
        longKKSetOfThree.fdropWhen { it < 2L }.equals(longKKSetOfTwo.fmap { it+1 }) shouldBe true
        longKKSetOfThree.fdropWhen { it >= 2L }.equals(longKKSetOfOne) shouldBe true

        longISetOfNone.fdropWhen { false }.equals(longISetOfNone) shouldBe true
        longISetOfNone.fdropWhen { true }.equals(longISetOfNone) shouldBe true
        longISetOfOne.fdropWhen { it == 1L }.equals(longISetOfNone) shouldBe true
        longISetOfOne.fdropWhen { it != 1L }.equals(longISetOfOne) shouldBe true
        longISetOfThree.fdropWhen { it < 2L }.equals(longISetOfTwo.fmap { it+1 }) shouldBe true
        longISetOfThree.fdropWhen { it >= 2L }.equals(longISetOfOne) shouldBe true
    }

    test("fempty") {
        longKKSetOfNone.fempty() shouldBe true
        longKKSetOfOne.fempty() shouldBe false
        longISetOfNone.fempty() shouldBe true
        longISetOfOne.fempty() shouldBe false
        (longKKSetOfNone === longISetOfNone) shouldBe true
    }

    test("ffilter") {
        longKKSetOfNone.ffilter { false }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfNone.ffilter { true }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.ffilter { it == 1L }.equals(longKKSetOfOne) shouldBe true
        longKKSetOfOne.ffilter { it != 1L }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfThree.ffilter { it < 2L }.equals(longKKSetOfOne) shouldBe true
        longKKSetOfThree.ffilter { it >= 2L }.equals(longKKSetOfTwo.fmap { it+1 }) shouldBe true

        longISetOfNone.ffilter { false }.equals(longISetOfNone) shouldBe true
        longISetOfNone.ffilter { true }.equals(longISetOfNone) shouldBe true
        longISetOfOne.ffilter { it == 1L }.equals(longISetOfOne) shouldBe true
        longISetOfOne.ffilter { it != 1L }.equals(longISetOfNone) shouldBe true
        longISetOfThree.ffilter { it < 2L }.equals(longISetOfOne) shouldBe true
        longISetOfThree.ffilter { it >= 2L }.equals(longISetOfTwo.fmap { it+1 }) shouldBe true
    }
    
    test("ffilterNot") {
        longKKSetOfNone.ffilterNot { false }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfNone.ffilterNot { true }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.ffilterNot { it == 1L }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.ffilterNot { it != 1L }.equals(longKKSetOfOne) shouldBe true
        longKKSetOfThree.ffilterNot { it < 2L }.equals(longKKSetOfTwo.fmap { it+1 }) shouldBe true
        longKKSetOfThree.ffilterNot { it >= 2L }.equals(longKKSetOfOne) shouldBe true

        longISetOfNone.ffilterNot { false }.equals(longISetOfNone) shouldBe true
        longISetOfNone.ffilterNot { true }.equals(longISetOfNone) shouldBe true
        longISetOfOne.ffilterNot { it == 1L }.equals(longISetOfNone) shouldBe true
        longISetOfOne.ffilterNot { it != 1L }.equals(longISetOfOne) shouldBe true
        longISetOfThree.ffilterNot { it < 2L }.equals(longISetOfTwo.fmap { it+1 }) shouldBe true
        longISetOfThree.ffilterNot { it >= 2L }.equals(longISetOfOne) shouldBe true
    }

    test("ffind") {
        longKKSetOfNone.ffind { false } shouldBe null
        longKKSetOfNone.ffind { true } shouldBe null
        longKKSetOfOne.ffind { it == 1L } shouldBe 1
        longKKSetOfOne.ffind { it != 1L } shouldBe null
        longKKSetOfThree.ffind { it < 2L } shouldBe 1
        longKKSetOfThree.ffind { it >= 2L } shouldBe null

        longISetOfNone.ffind { false } shouldBe null
        longISetOfNone.ffind { true } shouldBe null
        longISetOfOne.ffind { it == 1L } shouldBe 1
        longISetOfOne.ffind { it != 1L } shouldBe null
        longISetOfThree.ffind { it < 2L } shouldBe 1
        longISetOfThree.ffind { it >= 2L } shouldBe null
    }

    test("fisSubsetOf") {
        longKKSetOfNone.fisSubsetOf(longKKSetOfNone) shouldBe true

        longKKSetOfOne.fisSubsetOf(longKKSetOfOne) shouldBe true
        longKKSetOfOne.fisSubsetOf(longKKSetOfNone) shouldBe false
        longKKSetOfNone.fisSubsetOf(longKKSetOfOne) shouldBe true
        longKKSetOfOne.fisSubsetOf(longKKSetMaverick) shouldBe false
        longKKSetMaverick.fisSubsetOf(longKKSetOfOne) shouldBe false
        longKKSetOfOne.fisSubsetOf(longKKSetOfTwo) shouldBe true
        longKKSetOfTwo.fisSubsetOf(longKKSetOfOne) shouldBe false

        longKKSetOfThree.fisSubsetOf(longKKSetOfThree) shouldBe true
        longKKSetOfThree.fisSubsetOf(longKKSetOfNone) shouldBe false
        longKKSetOfNone.fisSubsetOf(longKKSetOfThree) shouldBe true
        longKKSetOfThree.fisSubsetOf(longKKSetMaverick) shouldBe false
        longKKSetMaverick.fisSubsetOf(longKKSetOfThree) shouldBe false
        longKKSetOfThree.fisSubsetOf(longKKSetOfTwo) shouldBe false
        longKKSetOfTwo.fisSubsetOf(longKKSetOfThree) shouldBe true

        longISetOfNone.fisSubsetOf(longISetOfNone) shouldBe true

        longISetOfOne.fisSubsetOf(longISetOfOne) shouldBe true
        longISetOfOne.fisSubsetOf(longISetOfNone) shouldBe false
        longISetOfNone.fisSubsetOf(longISetOfOne) shouldBe true
        longISetOfOne.fisSubsetOf(longISetMaverick) shouldBe false
        longISetMaverick.fisSubsetOf(longISetOfOne) shouldBe false
        longISetOfOne.fisSubsetOf(longISetOfTwo) shouldBe true
        longISetOfTwo.fisSubsetOf(longISetOfOne) shouldBe false

        longISetOfThree.fisSubsetOf(longISetOfThree) shouldBe true
        longISetOfThree.fisSubsetOf(longISetOfNone) shouldBe false
        longISetOfNone.fisSubsetOf(longISetOfThree) shouldBe true
        longISetOfThree.fisSubsetOf(longISetMaverick) shouldBe false
        longISetMaverick.fisSubsetOf(longISetOfThree) shouldBe false
        longISetOfThree.fisSubsetOf(longISetOfTwo) shouldBe false
        longISetOfTwo.fisSubsetOf(longISetOfThree) shouldBe true

        // mixed mode

        longISetOfThree.fisSubsetOf(longKKSetOfThree) shouldBe true
        longISetOfThree.fisSubsetOf(longKKSetOfNone) shouldBe false
        longISetOfNone.fisSubsetOf(longKKSetOfThree) shouldBe true
        longISetOfThree.fisSubsetOf(longKKSetMaverick) shouldBe false
        longISetMaverick.fisSubsetOf(longKKSetOfThree) shouldBe false
        longISetOfThree.fisSubsetOf(longKKSetOfTwo) shouldBe false
        longISetOfTwo.fisSubsetOf(longKKSetOfThree) shouldBe true

        longKKSetOfThree.fisSubsetOf(longISetOfThree) shouldBe true
        longKKSetOfThree.fisSubsetOf(longISetOfNone) shouldBe false
        longKKSetOfNone.fisSubsetOf(longISetOfThree) shouldBe true
        longKKSetOfThree.fisSubsetOf(longISetMaverick) shouldBe false
        longKKSetMaverick.fisSubsetOf(longISetOfThree) shouldBe false
        longKKSetOfThree.fisSubsetOf(longISetOfTwo) shouldBe false
        longKKSetOfTwo.fisSubsetOf(longISetOfThree) shouldBe true

    }

    test("fAND") {
        longKKSetOfNone.fAND(longKKSetOfNone).equals(longKKSetOfNone) shouldBe true
        longKKSetOfNone.fAND(longKKSetOfOne).equals(longKKSetOfNone) shouldBe true

        longKKSetOfOne.fAND(longKKSetOfNone).equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.fAND(longKKSetOfOne).equals(longKKSetOfOne) shouldBe true
        longKKSetOfOne.fAND(longKKSetOfThree).equals(longKKSetOfOne) shouldBe true
        longKKSetOfThree.fAND(longKKSetOfOne).equals(longKKSetOfOne) shouldBe true

        longKKSetOfTwo.fAND(longKKSetOfNone).equals(longKKSetOfNone) shouldBe true
        longKKSetOfTwo.fAND(longKKSetOfTwo).equals(longKKSetOfTwo) shouldBe true
        longKKSetOfTwo.fAND(longKKSetOfThree).equals(longKKSetOfTwo) shouldBe true
        longKKSetOfThree.fAND(longKKSetOfTwo).equals(longKKSetOfTwo) shouldBe true

        longKKSetOfThree.fAND(longKKSetOfNone).equals(longKKSetOfNone) shouldBe true
        longKKSetOfThree.fAND(longKKSetOfThree).equals(longKKSetOfThree) shouldBe true
        FKSet.ofk(2L).fAND(longKKSetOfThree).equals(FKSet.ofk(2L)) shouldBe true
        longKKSetOfThree.fAND(FKSet.ofi(2L)).equals(FKSet.ofk(2L)) shouldBe true

        longISetOfNone.fAND(longISetOfNone).equals(longISetOfNone) shouldBe true
        longISetOfNone.fAND(longISetOfOne).equals(longISetOfNone) shouldBe true

        longISetOfOne.fAND(longISetOfNone).equals(longISetOfNone) shouldBe true
        longISetOfOne.fAND(longISetOfOne).equals(longISetOfOne) shouldBe true
        longISetOfOne.fAND(longISetOfThree).equals(longISetOfOne) shouldBe true
        longISetOfThree.fAND(longISetOfOne).equals(longISetOfOne) shouldBe true

        longISetOfTwo.fAND(longISetOfNone).equals(longISetOfNone) shouldBe true
        longISetOfTwo.fAND(longISetOfTwo).equals(longISetOfTwo) shouldBe true
        longISetOfTwo.fAND(longISetOfThree).equals(longISetOfTwo) shouldBe true
        longISetOfThree.fAND(longISetOfTwo).equals(longISetOfTwo) shouldBe true

        longISetOfThree.fAND(longISetOfNone).equals(longISetOfNone) shouldBe true
        longISetOfThree.fAND(longISetOfThree).equals(longISetOfThree) shouldBe true
        FKSet.ofs(2L).fAND(longISetOfThree).equals(FKSet.ofs(2L)) shouldBe true
        longISetOfThree.fAND(FKSet.ofi(2L)).equals(FKSet.ofi(2L)) shouldBe true

        // mixed mode

        longISetOfThree.fAND(longKKSetOfTwo).equals(longISetOfTwo) shouldBe true
        longKKSetOfThree.fAND(longISetOfTwo).equals(longKKSetOfTwo) shouldBe true

    }

    test("fNOT") {
        longKKSetOfNone.fNOT(longKKSetOfNone).equals(longKKSetOfNone) shouldBe true
        longKKSetOfNone.fNOT(longKKSetOfOne).equals(longKKSetOfNone) shouldBe true

        longKKSetOfOne.fNOT(longKKSetOfNone).equals(longKKSetOfOne) shouldBe true
        longKKSetOfOne.fNOT(longKKSetOfOne).equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.fNOT(longKKSetOfThree).equals(longKKSetOfNone) shouldBe true
        longKKSetOfThree.fNOT(longKKSetOfOne).equals(FKSet.ofk(2L,3L)) shouldBe true

        longKKSetOfTwo.fNOT(longKKSetOfNone).equals(longKKSetOfTwo) shouldBe true
        longKKSetOfTwo.fNOT(longKKSetOfTwo).equals(longKKSetOfNone) shouldBe true
        longKKSetOfTwo.fNOT(longKKSetOfThree).equals(longKKSetOfNone) shouldBe true
        longKKSetOfThree.fNOT(longKKSetOfTwo).equals(longKKSetOfOne3) shouldBe true

        longKKSetOfThree.fNOT(longKKSetOfNone).equals(longKKSetOfThree) shouldBe true
        longKKSetOfThree.fNOT(longKKSetOfThree).equals(longKKSetOfNone) shouldBe true
        FKSet.ofi(2L).fNOT(longKKSetOfThree).equals(longKKSetOfNone) shouldBe true
        longKKSetOfThree.fNOT(FKSet.ofi(2L)).equals(FKSet.ofk(1L,3L)) shouldBe true

        longISetOfNone.fNOT(longISetOfNone).equals(longISetOfNone) shouldBe true
        longISetOfNone.fNOT(longISetOfOne).equals(longISetOfNone) shouldBe true

        longISetOfOne.fNOT(longISetOfNone).equals(longISetOfOne) shouldBe true
        longISetOfOne.fNOT(longISetOfOne).equals(longISetOfNone) shouldBe true
        longISetOfOne.fNOT(longISetOfThree).equals(longISetOfNone) shouldBe true
        longISetOfThree.fNOT(longISetOfOne).equals(FKSet.ofi(2L,3L)) shouldBe true

        longISetOfTwo.fNOT(longISetOfNone).equals(longISetOfTwo) shouldBe true
        longISetOfTwo.fNOT(longISetOfTwo).equals(longISetOfNone) shouldBe true
        longISetOfTwo.fNOT(longISetOfThree).equals(longISetOfNone) shouldBe true
        longISetOfThree.fNOT(longISetOfTwo).equals(longISetOfOne3) shouldBe true

        longISetOfThree.fNOT(longISetOfNone).equals(longISetOfThree) shouldBe true
        longISetOfThree.fNOT(longISetOfThree).equals(longISetOfNone) shouldBe true
        FKSet.ofi(2L).fNOT(longISetOfThree).equals(longISetOfNone) shouldBe true
        longISetOfThree.fNOT(FKSet.ofi(2L)).equals(FKSet.ofi(1L,3L)) shouldBe true

        // mixed mode

        longISetOfThree.fNOT(longKKSetOfTwo).equals(longISetOfOne3) shouldBe true
        longKKSetOfThree.fNOT(longISetOfTwo).equals(longKKSetOfOne3) shouldBe true
    }

    test("fOR") {
        longKKSetOfNone.fOR(longKKSetOfNone).equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.fOR(longKKSetOfNone).equals(longKKSetOfOne) shouldBe true
        longKKSetOfNone.fOR(longKKSetOfOne).equals(longKKSetOfOne) shouldBe true

        longKKSetOfTwo.fOR(longKKSetOfTwo).equals(longKKSetOfTwo) shouldBe true
        longKKSetOfTwo.fOR(longKKSetOfNone).equals(longKKSetOfTwo) shouldBe true
        longKKSetOfNone.fOR(longKKSetOfTwo).equals(longKKSetOfTwo) shouldBe true
        longKKSetOfTwo.fOR(longKKSetOfTwoOfst1).equals(longKKSetOfThree) shouldBe true
        longKKSetOfTwoOfst1.fOR(longKKSetOfTwo).equals(longKKSetOfThree) shouldBe true
        longKKSetOfTwo.fOR(longKKSetOfTwoOfst2).equals(longKKSetOfFour) shouldBe true
        longKKSetOfTwoOfst2.fOR(longKKSetOfTwo).equals(longKKSetOfFour) shouldBe true

        longKKSetOfThree.fOR(longKKSetOfNone).equals(longKKSetOfThree) shouldBe true
        longKKSetOfThree.fOR(longKKSetOfThree).equals(longKKSetOfThree) shouldBe true
        longKKSetOfThree.fOR(FKSet.ofi(2L)).equals(longKKSetOfThree) shouldBe true

        longISetOfNone.fOR(longISetOfNone).equals(longISetOfNone) shouldBe true
        longISetOfOne.fOR(longISetOfNone).equals(longISetOfOne) shouldBe true
        longISetOfNone.fOR(longISetOfOne).equals(longISetOfOne) shouldBe true

        longISetOfTwo.fOR(longISetOfTwo).equals(longISetOfTwo) shouldBe true
        longISetOfTwo.fOR(longISetOfNone).equals(longISetOfTwo) shouldBe true
        longISetOfNone.fOR(longISetOfTwo).equals(longISetOfTwo) shouldBe true
        longISetOfTwo.fOR(longISetOfTwoOfst1).equals(longISetOfThree) shouldBe true
        longISetOfTwoOfst1.fOR(longISetOfTwo).equals(longISetOfThree) shouldBe true
        longISetOfTwo.fOR(longISetOfTwoOfst2).equals(longISetOfFour) shouldBe true
        longISetOfTwoOfst2.fOR(longISetOfTwo).equals(longISetOfFour) shouldBe true

        longISetOfThree.fOR(longISetOfNone).equals(longISetOfThree) shouldBe true
        longISetOfThree.fOR(longISetOfThree).equals(longISetOfThree) shouldBe true
        FKSet.ofi(2L).fOR(longISetOfThree).equals(longISetOfThree) shouldBe true
        longISetOfThree.fOR(FKSet.ofs(2)).equals(longISetOfThree) shouldBe true

        // mixed mode

        longISetOfTwoOfst1.fOR(longKKSetOfTwo).equals(longISetOfThree) shouldBe true
        longISetOfTwo.fOR(longKKSetOfTwoOfst2).equals(longISetOfFour) shouldBe true

        longKKSetOfTwoOfst1.fOR(longISetOfTwo).equals(longKKSetOfThree) shouldBe true
        longKKSetOfTwo.fOR(longISetOfTwoOfst2).equals(longKKSetOfFour) shouldBe true
    }

    test("fXOR") {
        longKKSetOfNone.fXOR(longKKSetOfNone).equals(longKKSetOfNone) shouldBe true
        longKKSetOfNone.fXOR(longKKSetOfOne).equals(longKKSetOfOne) shouldBe true

        longKKSetOfOne.fXOR(longKKSetOfNone).equals(longKKSetOfOne) shouldBe true
        longKKSetOfOne.fXOR(longKKSetOfOne).equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.fXOR(longKKSetOfThree).equals(FKSet.ofk(2L,3L)) shouldBe true
        longKKSetOfThree.fXOR(longKKSetOfOne).equals(FKSet.ofk(2L,3L)) shouldBe true

        longKKSetOfTwo.fXOR(longKKSetOfNone).equals(longKKSetOfTwo) shouldBe true
        longKKSetOfTwo.fXOR(longKKSetOfTwo).equals(longKKSetOfNone) shouldBe true
        longKKSetOfTwo.fXOR(longKKSetOfThree).equals(longKKSetOfOne3) shouldBe true
        longKKSetOfThree.fXOR(longKKSetOfTwo).equals(longKKSetOfOne3) shouldBe true

        longKKSetOfThree.fXOR(longKKSetOfNone).equals(longKKSetOfThree) shouldBe true
        longKKSetOfThree.fXOR(longKKSetOfThree).equals(longKKSetOfNone) shouldBe true
        FKSet.ofi(2L).fXOR(longKKSetOfThree).equals(FKSet.ofi(1L,3L)) shouldBe true
        longKKSetOfThree.fXOR(FKSet.ofi(2L)).equals(FKSet.ofk(1L,3L)) shouldBe true

        longISetOfNone.fXOR(longISetOfNone).equals(longISetOfNone) shouldBe true
        longISetOfNone.fXOR(longISetOfOne).equals(longISetOfOne) shouldBe true

        longISetOfOne.fXOR(longISetOfNone).equals(longISetOfOne) shouldBe true
        longISetOfOne.fXOR(longISetOfOne).equals(longISetOfNone) shouldBe true
        longISetOfOne.fXOR(longISetOfThree).equals(FKSet.ofi(2L,3L)) shouldBe true
        longISetOfThree.fXOR(longISetOfOne).equals(FKSet.ofi(2L,3L)) shouldBe true

        longISetOfTwo.fXOR(longISetOfNone).equals(longISetOfTwo) shouldBe true
        longISetOfTwo.fXOR(longISetOfTwo).equals(longISetOfNone) shouldBe true
        longISetOfTwo.fXOR(longISetOfThree).equals(longISetOfOne3) shouldBe true
        longISetOfThree.fXOR(longISetOfTwo).equals(longISetOfOne3) shouldBe true

        longISetOfThree.fXOR(longISetOfNone).equals(longISetOfThree) shouldBe true
        longISetOfThree.fXOR(longISetOfThree).equals(longISetOfNone) shouldBe true
        FKSet.ofs(2L).fXOR(longISetOfThree).equals(FKSet.ofs(1L,3L)) shouldBe true
        longISetOfThree.fXOR(FKSet.ofs(2L)).equals(FKSet.ofi(1L,3L)) shouldBe true

        // mixed mode

        longISetOfTwo.fXOR(longKKSetOfThree).equals(longISetOfOne3) shouldBe true
        longKKSetOfTwo.fXOR(longISetOfThree).equals(longKKSetOfOne3) shouldBe true
    }
})
