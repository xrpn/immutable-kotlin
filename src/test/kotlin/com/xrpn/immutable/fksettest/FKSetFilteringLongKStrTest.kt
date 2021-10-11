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

private val longSSetOfNone = FKSet.ofs(*arrayOf<Long>())
private val longSSetOfOne = FKSet.ofs(1L).rne()!!
private val longSSetOfOne3 = FKSet.ofs(3L).rne()!!
private val longSSetOfTwo = FKSet.ofs(1L, 2L).rne()!!
private val longSSetOfTwoOfst1 = FKSet.ofs(2L, 3L).rne()!!
private val longSSetOfTwoOfst2 = FKSet.ofs(3L, 4L).rne()!!
private val longSSetOfThree = FKSet.ofs(1L, 2L, 3L).rne()!!
private val longSSetOfFour = FKSet.ofs(1L, 2L, 3L, 4L).rne()!!
private val longSSetMaverick = FKSet.ofs(113L, 97L).rne()!!

class FKSetFilteringLongKStrTest : FunSpec({

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
        
        longSSetOfNone.fcontains(1) shouldBe false
        longSSetOfOne.fcontains(0) shouldBe false
        longSSetOfOne.fcontains(1) shouldBe true
        longSSetOfOne.fcontains(2) shouldBe false
        longSSetOfTwo.fcontains(0) shouldBe false
        longSSetOfTwo.fcontains(1) shouldBe true
        longSSetOfTwo.fcontains(2) shouldBe true
        longSSetOfTwo.fcontains(3) shouldBe false
        longSSetOfThree.fcontains(0) shouldBe false
        longSSetOfThree.fcontains(1) shouldBe true
        longSSetOfThree.fcontains(2) shouldBe true
        longSSetOfThree.fcontains(3) shouldBe true
        longSSetOfThree.fcontains(4) shouldBe false

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

        longSSetOfNone.fcontainsAny(longSSetOfNone) shouldBe false

        longSSetOfOne.fcontainsAny(longSSetOfNone) shouldBe true
        longSSetOfOne.fcontainsAny(longSSetOfOne) shouldBe true
        longSSetOfOne.fcontainsAny(longSSetOfTwo) shouldBe true
        longSSetOfOne.fcontainsAny(longSSetMaverick) shouldBe false

        longSSetOfTwo.fcontainsAny(longSSetOfNone) shouldBe true
        longSSetOfTwo.fcontainsAny(longSSetOfOne) shouldBe true
        longSSetOfTwo.fcontainsAny(longSSetOfFour) shouldBe true
        longSSetOfTwo.fcontainsAny(longSSetMaverick) shouldBe false

        longSSetOfThree.fcontainsAny(longSSetOfNone) shouldBe true
        longSSetOfThree.fcontainsAny(longSSetOfFour) shouldBe true
        longSSetOfThree.fcontainsAny(longSSetMaverick) shouldBe false

        // mixed mode

        longKKSetOfThree.fcontainsAny(longSSetOfNone) shouldBe true
        longKKSetOfThree.fcontainsAny(longSSetOfFour) shouldBe true
        longKKSetOfThree.fcontainsAny(longSSetMaverick) shouldBe false

        longSSetOfThree.fcontainsAny(longKKSetOfNone) shouldBe true
        longSSetOfThree.fcontainsAny(longKKSetOfFour) shouldBe true
        longSSetOfThree.fcontainsAny(longKKSetMaverick) shouldBe false

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

        longSSetOfNone.fdropItem(0).equals(longSSetOfNone) shouldBe true
        longSSetOfOne.fdropItem(0).equals(longSSetOfOne) shouldBe true
        longSSetOfOne.fdropItem(1).equals(longSSetOfNone) shouldBe true

        longSSetOfTwo.fdropItem(0).equals(longSSetOfTwo) shouldBe true
        longSSetOfTwo.fdropItem(1).equals(FKSet.ofs(2L)) shouldBe true
        longSSetOfTwo.fdropItem(2).equals(longSSetOfOne) shouldBe true
        longSSetOfTwo.fdropItem(3).equals(longSSetOfTwo) shouldBe true

        longSSetOfThree.fdropItem(0).equals(longSSetOfThree) shouldBe true
        longSSetOfThree.fdropItem(1).equals(FKSet.ofs(2L, 3L)) shouldBe true
        longSSetOfThree.fdropItem(2).equals(FKSet.ofs(1L, 3L)) shouldBe true
        longSSetOfThree.fdropItem(3).equals(longSSetOfTwo) shouldBe true
        longSSetOfThree.fdropItem(4).equals(longSSetOfThree) shouldBe true

        // mixed mode

        longSSetOfThree.fdropItem(3).equal(longKKSetOfTwo) shouldBe true
        longKKSetOfThree.fdropItem(3).equal(longSSetOfTwo) shouldBe true
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

        longSSetOfNone.fdropAll(longSSetOfNone).equals(longSSetOfNone) shouldBe true

        longSSetOfOne.fdropAll(longSSetOfNone).equals(longSSetOfOne) shouldBe true
        longSSetOfOne.fdropAll(longSSetOfOne).equals(longSSetOfNone) shouldBe true
        longSSetOfOne.fdropAll(longSSetOfTwo).equals(longSSetOfNone) shouldBe true
        longSSetOfOne.fdropAll(longSSetMaverick).equals(longSSetOfOne) shouldBe true

        longSSetOfTwo.fdropAll(longSSetOfNone).equals(longSSetOfTwo) shouldBe true
        longSSetOfTwo.fdropAll(longSSetOfOne).equals(FKSet.ofs(2)) shouldBe false
        longSSetOfTwo.fdropAll(longSSetOfOne).equals(FKSet.ofs(2L)) shouldBe true
        longSSetOfTwo.fdropAll(longSSetOfOne).equal(FKSet.ofs(2)) shouldBe true
        longSSetOfTwo.fdropAll(longSSetOfFour).equals(longSSetOfNone) shouldBe true
        longSSetOfTwo.fdropAll(longSSetMaverick).equals(longSSetOfTwo) shouldBe true

        longSSetOfThree.fdropAll(longSSetOfNone).equals(longSSetOfThree) shouldBe true
        longSSetOfThree.fdropAll(longSSetOfFour).equals(longSSetOfNone) shouldBe true
        longSSetOfThree.fdropAll(longSSetMaverick).equals(longSSetOfThree) shouldBe true

        // mixed mode

        longSSetOfThree.fdropAll(longKKSetOfFour).equal(longSSetOfNone) shouldBe true
        longKKSetOfThree.fdropAll(longSSetOfFour).equal(longSSetOfNone) shouldBe true

    }

    test("fdropWhen") {
        longKKSetOfNone.fdropWhen { false }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfNone.fdropWhen { true }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.fdropWhen { it == 1L }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.fdropWhen { it != 1L }.equals(longKKSetOfOne) shouldBe true
        longKKSetOfThree.fdropWhen { it < 2L }.equals(longKKSetOfTwo.fmap { it+1 }) shouldBe true
        longKKSetOfThree.fdropWhen { it >= 2L }.equals(longKKSetOfOne) shouldBe true

        longSSetOfNone.fdropWhen { false }.equals(longSSetOfNone) shouldBe true
        longSSetOfNone.fdropWhen { true }.equals(longSSetOfNone) shouldBe true
        longSSetOfOne.fdropWhen { it == 1L }.equals(longSSetOfNone) shouldBe true
        longSSetOfOne.fdropWhen { it != 1L }.equals(longSSetOfOne) shouldBe true
        longSSetOfThree.fdropWhen { it < 2L }.equals(longSSetOfTwo.fmap { it+1 }) shouldBe true
        longSSetOfThree.fdropWhen { it >= 2L }.equals(longSSetOfOne) shouldBe true
    }

    test("fempty") {
        longKKSetOfNone.fempty() shouldBe true
        longKKSetOfOne.fempty() shouldBe false
        longSSetOfNone.fempty() shouldBe true
        longSSetOfOne.fempty() shouldBe false
        (longKKSetOfNone === longSSetOfNone) shouldBe true
    }

    test("ffilter") {
        longKKSetOfNone.ffilter { false }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfNone.ffilter { true }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.ffilter { it == 1L }.equals(longKKSetOfOne) shouldBe true
        longKKSetOfOne.ffilter { it != 1L }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfThree.ffilter { it < 2L }.equals(longKKSetOfOne) shouldBe true
        longKKSetOfThree.ffilter { it >= 2L }.equals(longKKSetOfTwo.fmap { it+1 }) shouldBe true

        longSSetOfNone.ffilter { false }.equals(longSSetOfNone) shouldBe true
        longSSetOfNone.ffilter { true }.equals(longSSetOfNone) shouldBe true
        longSSetOfOne.ffilter { it == 1L }.equals(longSSetOfOne) shouldBe true
        longSSetOfOne.ffilter { it != 1L }.equals(longSSetOfNone) shouldBe true
        longSSetOfThree.ffilter { it < 2L }.equals(longSSetOfOne) shouldBe true
        longSSetOfThree.ffilter { it >= 2L }.equals(longSSetOfTwo.fmap { it+1 }) shouldBe true
    }
    
    test("ffilterNot") {
        longKKSetOfNone.ffilterNot { false }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfNone.ffilterNot { true }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.ffilterNot { it == 1L }.equals(longKKSetOfNone) shouldBe true
        longKKSetOfOne.ffilterNot { it != 1L }.equals(longKKSetOfOne) shouldBe true
        longKKSetOfThree.ffilterNot { it < 2L }.equals(longKKSetOfTwo.fmap { it+1 }) shouldBe true
        longKKSetOfThree.ffilterNot { it >= 2L }.equals(longKKSetOfOne) shouldBe true

        longSSetOfNone.ffilterNot { false }.equals(longSSetOfNone) shouldBe true
        longSSetOfNone.ffilterNot { true }.equals(longSSetOfNone) shouldBe true
        longSSetOfOne.ffilterNot { it == 1L }.equals(longSSetOfNone) shouldBe true
        longSSetOfOne.ffilterNot { it != 1L }.equals(longSSetOfOne) shouldBe true
        longSSetOfThree.ffilterNot { it < 2L }.equals(longSSetOfTwo.fmap { it+1 }) shouldBe true
        longSSetOfThree.ffilterNot { it >= 2L }.equals(longSSetOfOne) shouldBe true
    }

    test("ffind") {
        longKKSetOfNone.ffind { false } shouldBe null
        longKKSetOfNone.ffind { true } shouldBe null
        longKKSetOfOne.ffind { it == 1L } shouldBe 1
        longKKSetOfOne.ffind { it != 1L } shouldBe null
        longKKSetOfThree.ffind { it < 2L } shouldBe 1
        longKKSetOfThree.ffind { it >= 2L } shouldBe null

        longSSetOfNone.ffind { false } shouldBe null
        longSSetOfNone.ffind { true } shouldBe null
        longSSetOfOne.ffind { it == 1L } shouldBe 1
        longSSetOfOne.ffind { it != 1L } shouldBe null
        longSSetOfThree.ffind { it < 2L } shouldBe 1
        longSSetOfThree.ffind { it >= 2L } shouldBe null
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

        longSSetOfNone.fisSubsetOf(longSSetOfNone) shouldBe true

        longSSetOfOne.fisSubsetOf(longSSetOfOne) shouldBe true
        longSSetOfOne.fisSubsetOf(longSSetOfNone) shouldBe false
        longSSetOfNone.fisSubsetOf(longSSetOfOne) shouldBe true
        longSSetOfOne.fisSubsetOf(longSSetMaverick) shouldBe false
        longSSetMaverick.fisSubsetOf(longSSetOfOne) shouldBe false
        longSSetOfOne.fisSubsetOf(longSSetOfTwo) shouldBe true
        longSSetOfTwo.fisSubsetOf(longSSetOfOne) shouldBe false

        longSSetOfThree.fisSubsetOf(longSSetOfThree) shouldBe true
        longSSetOfThree.fisSubsetOf(longSSetOfNone) shouldBe false
        longSSetOfNone.fisSubsetOf(longSSetOfThree) shouldBe true
        longSSetOfThree.fisSubsetOf(longSSetMaverick) shouldBe false
        longSSetMaverick.fisSubsetOf(longSSetOfThree) shouldBe false
        longSSetOfThree.fisSubsetOf(longSSetOfTwo) shouldBe false
        longSSetOfTwo.fisSubsetOf(longSSetOfThree) shouldBe true

        // mixed mode

        longSSetOfThree.fisSubsetOf(longKKSetOfThree) shouldBe true
        longSSetOfThree.fisSubsetOf(longKKSetOfNone) shouldBe false
        longSSetOfNone.fisSubsetOf(longKKSetOfThree) shouldBe true
        longSSetOfThree.fisSubsetOf(longKKSetMaverick) shouldBe false
        longSSetMaverick.fisSubsetOf(longKKSetOfThree) shouldBe false
        longSSetOfThree.fisSubsetOf(longKKSetOfTwo) shouldBe false
        longSSetOfTwo.fisSubsetOf(longKKSetOfThree) shouldBe true

        longKKSetOfThree.fisSubsetOf(longSSetOfThree) shouldBe true
        longKKSetOfThree.fisSubsetOf(longSSetOfNone) shouldBe false
        longKKSetOfNone.fisSubsetOf(longSSetOfThree) shouldBe true
        longKKSetOfThree.fisSubsetOf(longSSetMaverick) shouldBe false
        longKKSetMaverick.fisSubsetOf(longSSetOfThree) shouldBe false
        longKKSetOfThree.fisSubsetOf(longSSetOfTwo) shouldBe false
        longKKSetOfTwo.fisSubsetOf(longSSetOfThree) shouldBe true

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

        longSSetOfNone.fAND(longSSetOfNone).equals(longSSetOfNone) shouldBe true
        longSSetOfNone.fAND(longSSetOfOne).equals(longSSetOfNone) shouldBe true

        longSSetOfOne.fAND(longSSetOfNone).equals(longSSetOfNone) shouldBe true
        longSSetOfOne.fAND(longSSetOfOne).equals(longSSetOfOne) shouldBe true
        longSSetOfOne.fAND(longSSetOfThree).equals(longSSetOfOne) shouldBe true
        longSSetOfThree.fAND(longSSetOfOne).equals(longSSetOfOne) shouldBe true

        longSSetOfTwo.fAND(longSSetOfNone).equals(longSSetOfNone) shouldBe true
        longSSetOfTwo.fAND(longSSetOfTwo).equals(longSSetOfTwo) shouldBe true
        longSSetOfTwo.fAND(longSSetOfThree).equals(longSSetOfTwo) shouldBe true
        longSSetOfThree.fAND(longSSetOfTwo).equals(longSSetOfTwo) shouldBe true

        longSSetOfThree.fAND(longSSetOfNone).equals(longSSetOfNone) shouldBe true
        longSSetOfThree.fAND(longSSetOfThree).equals(longSSetOfThree) shouldBe true
        FKSet.ofi(2L).fAND(longSSetOfThree).equals(FKSet.ofi(2L)) shouldBe true
        longSSetOfThree.fAND(FKSet.ofi(2L)).equals(FKSet.ofs(2L)) shouldBe true

        // mixed mode

        longSSetOfThree.fAND(longKKSetOfTwo).equals(longSSetOfTwo) shouldBe true
        longKKSetOfThree.fAND(longSSetOfTwo).equals(longKKSetOfTwo) shouldBe true

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

        longSSetOfNone.fNOT(longSSetOfNone).equals(longSSetOfNone) shouldBe true
        longSSetOfNone.fNOT(longSSetOfOne).equals(longSSetOfNone) shouldBe true

        longSSetOfOne.fNOT(longSSetOfNone).equals(longSSetOfOne) shouldBe true
        longSSetOfOne.fNOT(longSSetOfOne).equals(longSSetOfNone) shouldBe true
        longSSetOfOne.fNOT(longSSetOfThree).equals(longSSetOfNone) shouldBe true
        longSSetOfThree.fNOT(longSSetOfOne).equals(FKSet.ofs(2L,3L)) shouldBe true

        longSSetOfTwo.fNOT(longSSetOfNone).equals(longSSetOfTwo) shouldBe true
        longSSetOfTwo.fNOT(longSSetOfTwo).equals(longSSetOfNone) shouldBe true
        longSSetOfTwo.fNOT(longSSetOfThree).equals(longSSetOfNone) shouldBe true
        longSSetOfThree.fNOT(longSSetOfTwo).equals(longSSetOfOne3) shouldBe true

        longSSetOfThree.fNOT(longSSetOfNone).equals(longSSetOfThree) shouldBe true
        longSSetOfThree.fNOT(longSSetOfThree).equals(longSSetOfNone) shouldBe true
        FKSet.ofs(2L).fNOT(longSSetOfThree).equals(longSSetOfNone) shouldBe true
        longSSetOfThree.fNOT(FKSet.ofi(2L)).equals(FKSet.ofs(1L,3L)) shouldBe true

        // mixed mode

        longSSetOfThree.fNOT(longKKSetOfTwo).equals(longSSetOfOne3) shouldBe true
        longKKSetOfThree.fNOT(longSSetOfTwo).equals(longKKSetOfOne3) shouldBe true
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

        longSSetOfNone.fOR(longSSetOfNone).equals(longSSetOfNone) shouldBe true
        longSSetOfOne.fOR(longSSetOfNone).equals(longSSetOfOne) shouldBe true
        longSSetOfNone.fOR(longSSetOfOne).equals(longSSetOfOne) shouldBe true

        longSSetOfTwo.fOR(longSSetOfTwo).equals(longSSetOfTwo) shouldBe true
        longSSetOfTwo.fOR(longSSetOfNone).equals(longSSetOfTwo) shouldBe true
        longSSetOfNone.fOR(longSSetOfTwo).equals(longSSetOfTwo) shouldBe true
        longSSetOfTwo.fOR(longSSetOfTwoOfst1).equals(longSSetOfThree) shouldBe true
        longSSetOfTwoOfst1.fOR(longSSetOfTwo).equals(longSSetOfThree) shouldBe true
        longSSetOfTwo.fOR(longSSetOfTwoOfst2).equals(longSSetOfFour) shouldBe true
        longSSetOfTwoOfst2.fOR(longSSetOfTwo).equals(longSSetOfFour) shouldBe true

        longSSetOfThree.fOR(longSSetOfNone).equals(longSSetOfThree) shouldBe true
        longSSetOfThree.fOR(longSSetOfThree).equals(longSSetOfThree) shouldBe true
        FKSet.ofs(2L).fOR(longSSetOfThree).equals(longSSetOfThree) shouldBe true
        longSSetOfThree.fOR(FKSet.ofs(2)).equals(longSSetOfThree) shouldBe true

        // mixed mode

        longSSetOfTwoOfst1.fOR(longKKSetOfTwo).equals(longSSetOfThree) shouldBe true
        longSSetOfTwo.fOR(longKKSetOfTwoOfst2).equals(longSSetOfFour) shouldBe true

        longKKSetOfTwoOfst1.fOR(longSSetOfTwo).equals(longKKSetOfThree) shouldBe true
        longKKSetOfTwo.fOR(longSSetOfTwoOfst2).equals(longKKSetOfFour) shouldBe true
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

        longSSetOfNone.fXOR(longSSetOfNone).equals(longSSetOfNone) shouldBe true
        longSSetOfNone.fXOR(longSSetOfOne).equals(longSSetOfOne) shouldBe true

        longSSetOfOne.fXOR(longSSetOfNone).equals(longSSetOfOne) shouldBe true
        longSSetOfOne.fXOR(longSSetOfOne).equals(longSSetOfNone) shouldBe true
        longSSetOfOne.fXOR(longSSetOfThree).equals(FKSet.ofs(2L,3L)) shouldBe true
        longSSetOfThree.fXOR(longSSetOfOne).equals(FKSet.ofs(2L,3L)) shouldBe true

        longSSetOfTwo.fXOR(longSSetOfNone).equals(longSSetOfTwo) shouldBe true
        longSSetOfTwo.fXOR(longSSetOfTwo).equals(longSSetOfNone) shouldBe true
        longSSetOfTwo.fXOR(longSSetOfThree).equals(longSSetOfOne3) shouldBe true
        longSSetOfThree.fXOR(longSSetOfTwo).equals(longSSetOfOne3) shouldBe true

        longSSetOfThree.fXOR(longSSetOfNone).equals(longSSetOfThree) shouldBe true
        longSSetOfThree.fXOR(longSSetOfThree).equals(longSSetOfNone) shouldBe true
        FKSet.ofs(2L).fXOR(longSSetOfThree).equals(FKSet.ofs(1L,3L)) shouldBe true
        longSSetOfThree.fXOR(FKSet.ofs(2L)).equals(FKSet.ofs(1L,3L)) shouldBe true

        // mixed mode

        longSSetOfTwo.fXOR(longKKSetOfThree).equals(longSSetOfOne3) shouldBe true
        longKKSetOfTwo.fXOR(longSSetOfThree).equals(longKKSetOfOne3) shouldBe true
    }
})
