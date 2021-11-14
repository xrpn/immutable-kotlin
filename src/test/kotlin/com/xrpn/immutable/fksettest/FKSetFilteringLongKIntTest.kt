package com.xrpn.immutable.fksettest

import com.xrpn.immutable.FKSet
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val longKKSetOfNone = FKSet.ofk(*arrayOf<Long>())
private val longKKSetOfOne = FKSet.ofk(1L).nex<Long>()!!
private val longKKSetOfOne3 = FKSet.ofk(3L).nex<Long>()!!
private val longKKSetOfTwo = FKSet.ofk(1L, 2L).nex<Long>()!!
private val longKKSetOfTwoOfst1 = FKSet.ofk(2L, 3L).nex<Long>()!!
private val longKKSetOfTwoOfst2 = FKSet.ofk(3L, 4L).nex<Long>()!!
private val longKKSetOfThree = FKSet.ofk(1L, 2L, 3L).nex<Long>()!!
private val longKKSetOfFour = FKSet.ofk(1L, 2L, 3L, 4L).nex<Long>()!!
private val longKKSetMaverick = FKSet.ofk(113L, 97L).nex<Long>()!!

private val longISetOfNone = FKSet.ofi(*arrayOf<Long>())
private val longISetOfOne = FKSet.ofi(1L).ne()!!
private val longISetOfOne3 = FKSet.ofi(3L).ne()!!
private val longISetOfTwo = FKSet.ofi(1L, 2L).ne()!!
private val longISetOfTwoOfst1 = FKSet.ofi(2L, 3L).ne()!!
private val longISetOfTwoOfst2 = FKSet.ofi(3L, 4L).ne()!!
private val longISetOfThree = FKSet.ofi(1L, 2L, 3L).ne()!!
private val longISetOfFour = FKSet.ofi(1L, 2L, 3L, 4L).ne()!!
private val longISetMaverick = FKSet.ofi(113L, 97L).ne()!!

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
        longKKSetOfNone.equals(longISetOfNone) shouldBe true
        (longKKSetOfNone === longISetOfNone) shouldBe false
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
        (longKKSetOfNone and longKKSetOfOne).equals(longKKSetOfNone) shouldBe true

        (longKKSetOfOne and longKKSetOfNone).equals(longKKSetOfNone) shouldBe true
        (longKKSetOfOne and longKKSetOfOne).equals(longKKSetOfOne) shouldBe true
        (longKKSetOfOne and longKKSetOfThree).equals(longKKSetOfOne) shouldBe true
        (longKKSetOfThree and longKKSetOfOne).equals(longKKSetOfOne) shouldBe true

        (longKKSetOfTwo and longKKSetOfNone).equals(longKKSetOfNone) shouldBe true
        (longKKSetOfTwo and longKKSetOfTwo).equals(longKKSetOfTwo) shouldBe true
        (longKKSetOfTwo and longKKSetOfThree).equals(longKKSetOfTwo) shouldBe true
        (longKKSetOfThree and longKKSetOfTwo).equals(longKKSetOfTwo) shouldBe true

        (longKKSetOfThree and longKKSetOfNone).equals(longKKSetOfNone) shouldBe true
        (longKKSetOfThree and longKKSetOfThree).equals(longKKSetOfThree) shouldBe true
        (FKSet.ofk(2L) and longKKSetOfThree).equals(FKSet.ofk(2L)) shouldBe true
        (longKKSetOfThree and FKSet.ofi(2L)).equals(FKSet.ofk(2L)) shouldBe true

        (longISetOfNone and longISetOfNone).equals(longISetOfNone) shouldBe true
        (longISetOfNone and longISetOfOne).equals(longISetOfNone) shouldBe true

        (longISetOfOne and longISetOfNone).equals(longISetOfNone) shouldBe true
        (longISetOfOne and longISetOfOne).equals(longISetOfOne) shouldBe true
        (longISetOfOne and longISetOfThree).equals(longISetOfOne) shouldBe true
        (longISetOfThree and longISetOfOne).equals(longISetOfOne) shouldBe true

        (longISetOfTwo and longISetOfNone).equals(longISetOfNone) shouldBe true
        (longISetOfTwo and longISetOfTwo).equals(longISetOfTwo) shouldBe true
        (longISetOfTwo and longISetOfThree).equals(longISetOfTwo) shouldBe true
        (longISetOfThree and longISetOfTwo).equals(longISetOfTwo) shouldBe true

        (longISetOfThree and longISetOfNone).equals(longISetOfNone) shouldBe true
        (longISetOfThree and longISetOfThree).equals(longISetOfThree) shouldBe true
        (FKSet.ofs(2L) and longISetOfThree).equals(FKSet.ofs(2L)) shouldBe true
        (longISetOfThree and FKSet.ofi(2L)).equals(FKSet.ofi(2L)) shouldBe true

        // mixed mode

        (longISetOfThree and longKKSetOfTwo).equals(longISetOfTwo) shouldBe true
        (longKKSetOfThree and longISetOfTwo).equals(longKKSetOfTwo) shouldBe true

    }

    test("fNOT") {
        longKKSetOfNone.fNOT(longKKSetOfNone).equals(longKKSetOfNone) shouldBe true
        (longKKSetOfNone not longKKSetOfOne).equals(longKKSetOfNone) shouldBe true

        (longKKSetOfOne not longKKSetOfNone).equals(longKKSetOfOne) shouldBe true
        (longKKSetOfOne not longKKSetOfOne).equals(longKKSetOfNone) shouldBe true
        (longKKSetOfOne not longKKSetOfThree).equals(longKKSetOfNone) shouldBe true
        (longKKSetOfThree not longKKSetOfOne).equals(FKSet.ofk(2L,3L)) shouldBe true

        (longKKSetOfTwo not longKKSetOfNone).equals(longKKSetOfTwo) shouldBe true
        (longKKSetOfTwo not longKKSetOfTwo).equals(longKKSetOfNone) shouldBe true
        (longKKSetOfTwo not longKKSetOfThree).equals(longKKSetOfNone) shouldBe true
        (longKKSetOfThree not longKKSetOfTwo).equals(longKKSetOfOne3) shouldBe true

        (longKKSetOfThree not longKKSetOfNone).equals(longKKSetOfThree) shouldBe true
        (longKKSetOfThree not longKKSetOfThree).equals(longKKSetOfNone) shouldBe true
        (FKSet.ofi(2L) not longKKSetOfThree).equals(longKKSetOfNone) shouldBe true
        (longKKSetOfThree not FKSet.ofi(2L)).equals(FKSet.ofk(1L,3L)) shouldBe true

        (longISetOfNone not longISetOfNone).equals(longISetOfNone) shouldBe true
        (longISetOfNone not longISetOfOne).equals(longISetOfNone) shouldBe true

        (longISetOfOne not longISetOfNone).equals(longISetOfOne) shouldBe true
        (longISetOfOne not longISetOfOne).equals(longISetOfNone) shouldBe true
        (longISetOfOne not longISetOfThree).equals(longISetOfNone) shouldBe true
        (longISetOfThree not longISetOfOne).equals(FKSet.ofi(2L,3L)) shouldBe true

        (longISetOfTwo not longISetOfNone).equals(longISetOfTwo) shouldBe true
        (longISetOfTwo not longISetOfTwo).equals(longISetOfNone) shouldBe true
        (longISetOfTwo not longISetOfThree).equals(longISetOfNone) shouldBe true
        (longISetOfThree not longISetOfTwo).equals(longISetOfOne3) shouldBe true

        (longISetOfThree not longISetOfNone).equals(longISetOfThree) shouldBe true
        (longISetOfThree not longISetOfThree).equals(longISetOfNone) shouldBe true
        (FKSet.ofi(2L) not longISetOfThree).equals(longISetOfNone) shouldBe true
        (longISetOfThree not FKSet.ofi(2L)).equals(FKSet.ofi(1L,3L)) shouldBe true

        // mixed mode

        (longISetOfThree not longKKSetOfTwo).equals(longISetOfOne3) shouldBe true
        (longKKSetOfThree not longISetOfTwo).equals(longKKSetOfOne3) shouldBe true
    }

    test("fOR") {
        longKKSetOfNone.fOR(longKKSetOfNone).equals(longKKSetOfNone) shouldBe true
        (longKKSetOfOne or longKKSetOfNone).equals(longKKSetOfOne) shouldBe true
        (longKKSetOfNone or longKKSetOfOne).equals(longKKSetOfOne) shouldBe true

        (longKKSetOfTwo or longKKSetOfTwo).equals(longKKSetOfTwo) shouldBe true
        (longKKSetOfTwo or longKKSetOfNone).equals(longKKSetOfTwo) shouldBe true
        (longKKSetOfNone or longKKSetOfTwo).equals(longKKSetOfTwo) shouldBe true
        (longKKSetOfTwo or longKKSetOfTwoOfst1).equals(longKKSetOfThree) shouldBe true
        (longKKSetOfTwoOfst1 or longKKSetOfTwo).equals(longKKSetOfThree) shouldBe true
        (longKKSetOfTwo or longKKSetOfTwoOfst2).equals(longKKSetOfFour) shouldBe true
        (longKKSetOfTwoOfst2 or longKKSetOfTwo).equals(longKKSetOfFour) shouldBe true

        (longKKSetOfThree or longKKSetOfNone).equals(longKKSetOfThree) shouldBe true
        (longKKSetOfThree or longKKSetOfThree).equals(longKKSetOfThree) shouldBe true
        (longKKSetOfThree or FKSet.ofi(2L)).equals(longKKSetOfThree) shouldBe true

        (longISetOfNone or longISetOfNone).equals(longISetOfNone) shouldBe true
        (longISetOfOne or longISetOfNone).equals(longISetOfOne) shouldBe true
        (longISetOfNone or longISetOfOne).equals(longISetOfOne) shouldBe true

        (longISetOfTwo or longISetOfTwo).equals(longISetOfTwo) shouldBe true
        (longISetOfTwo or longISetOfNone).equals(longISetOfTwo) shouldBe true
        (longISetOfNone or longISetOfTwo).equals(longISetOfTwo) shouldBe true
        (longISetOfTwo or longISetOfTwoOfst1).equals(longISetOfThree) shouldBe true
        (longISetOfTwoOfst1 or longISetOfTwo).equals(longISetOfThree) shouldBe true
        (longISetOfTwo or longISetOfTwoOfst2).equals(longISetOfFour) shouldBe true
        (longISetOfTwoOfst2 or longISetOfTwo).equals(longISetOfFour) shouldBe true

        (longISetOfThree or longISetOfNone).equals(longISetOfThree) shouldBe true
        (longISetOfThree or longISetOfThree).equals(longISetOfThree) shouldBe true
        (FKSet.ofi(2L) or longISetOfThree).equals(longISetOfThree) shouldBe true
        (longISetOfThree or FKSet.ofs(2)).equals(longISetOfThree) shouldBe true

        // mixed mode

        (longISetOfTwoOfst1 or longKKSetOfTwo).equals(longISetOfThree) shouldBe true
        (longISetOfTwo or longKKSetOfTwoOfst2).equals(longISetOfFour) shouldBe true

        (longKKSetOfTwoOfst1 or longISetOfTwo).equals(longKKSetOfThree) shouldBe true
        (longKKSetOfTwo or longISetOfTwoOfst2).equals(longKKSetOfFour) shouldBe true
    }

    test("fXOR") {
        longKKSetOfNone.fXOR(longKKSetOfNone).equals(longKKSetOfNone) shouldBe true
        (longKKSetOfNone xor longKKSetOfOne).equals(longKKSetOfOne) shouldBe true

        (longKKSetOfOne xor longKKSetOfNone).equals(longKKSetOfOne) shouldBe true
        (longKKSetOfOne xor longKKSetOfOne).equals(longKKSetOfNone) shouldBe true
        (longKKSetOfOne xor longKKSetOfThree).equals(FKSet.ofk(2L,3L)) shouldBe true
        (longKKSetOfThree xor longKKSetOfOne).equals(FKSet.ofk(2L,3L)) shouldBe true

        (longKKSetOfTwo xor longKKSetOfNone).equals(longKKSetOfTwo) shouldBe true
        (longKKSetOfTwo xor longKKSetOfTwo).equals(longKKSetOfNone) shouldBe true
        (longKKSetOfTwo xor longKKSetOfThree).equals(longKKSetOfOne3) shouldBe true
        (longKKSetOfThree xor longKKSetOfTwo).equals(longKKSetOfOne3) shouldBe true

        (longKKSetOfThree xor longKKSetOfNone).equals(longKKSetOfThree) shouldBe true
        (longKKSetOfThree xor longKKSetOfThree).equals(longKKSetOfNone) shouldBe true
        (FKSet.ofi(2L) xor longKKSetOfThree).equals(FKSet.ofi(1L,3L)) shouldBe true
        (longKKSetOfThree xor FKSet.ofi(2L)).equals(FKSet.ofk(1L,3L)) shouldBe true

        (longISetOfNone xor longISetOfNone).equals(longISetOfNone) shouldBe true
        (longISetOfNone xor longISetOfOne).equals(longISetOfOne) shouldBe true

        (longISetOfOne xor longISetOfNone).equals(longISetOfOne) shouldBe true
        (longISetOfOne xor longISetOfOne).equals(longISetOfNone) shouldBe true
        (longISetOfOne xor longISetOfThree).equals(FKSet.ofi(2L,3L)) shouldBe true
        (longISetOfThree xor longISetOfOne).equals(FKSet.ofi(2L,3L)) shouldBe true

        (longISetOfTwo xor longISetOfNone).equals(longISetOfTwo) shouldBe true
        (longISetOfTwo xor longISetOfTwo).equals(longISetOfNone) shouldBe true
        (longISetOfTwo xor longISetOfThree).equals(longISetOfOne3) shouldBe true
        (longISetOfThree xor longISetOfTwo).equals(longISetOfOne3) shouldBe true

        (longISetOfThree xor longISetOfNone).equals(longISetOfThree) shouldBe true
        (longISetOfThree xor longISetOfThree).equals(longISetOfNone) shouldBe true
        (FKSet.ofs(2L) xor longISetOfThree).equals(FKSet.ofs(1L,3L)) shouldBe true
        (longISetOfThree xor FKSet.ofs(2L)).equals(FKSet.ofi(1L,3L)) shouldBe true

        // mixed mode

        (longISetOfTwo xor longKKSetOfThree).equals(longISetOfOne3) shouldBe true
        (longKKSetOfTwo xor longISetOfThree).equals(longKKSetOfOne3) shouldBe true
    }
})
