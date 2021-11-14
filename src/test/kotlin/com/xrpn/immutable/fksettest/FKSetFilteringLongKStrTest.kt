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

private val longSSetOfNone = FKSet.ofs(*arrayOf<Long>())
private val longSSetOfOne = FKSet.ofs(1L).ne()!!
private val longSSetOfOne3 = FKSet.ofs(3L).ne()!!
private val longSSetOfTwo = FKSet.ofs(1L, 2L).ne()!!
private val longSSetOfTwoOfst1 = FKSet.ofs(2L, 3L).ne()!!
private val longSSetOfTwoOfst2 = FKSet.ofs(3L, 4L).ne()!!
private val longSSetOfThree = FKSet.ofs(1L, 2L, 3L).ne()!!
private val longSSetOfFour = FKSet.ofs(1L, 2L, 3L, 4L).ne()!!
private val longSSetMaverick = FKSet.ofs(113L, 97L).ne()!!

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
        longKKSetOfNone.equals(longSSetOfNone) shouldBe true
        longKKSetOfNone.equal(longSSetOfNone) shouldBe true
        (longKKSetOfNone === longSSetOfNone) shouldBe false
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

        (longSSetOfNone and longSSetOfNone).equals(longSSetOfNone) shouldBe true
        (longSSetOfNone and longSSetOfOne).equals(longSSetOfNone) shouldBe true

        (longSSetOfOne and longSSetOfNone).equals(longSSetOfNone) shouldBe true
        (longSSetOfOne and longSSetOfOne).equals(longSSetOfOne) shouldBe true
        (longSSetOfOne and longSSetOfThree).equals(longSSetOfOne) shouldBe true
        (longSSetOfThree and longSSetOfOne).equals(longSSetOfOne) shouldBe true

        (longSSetOfTwo and longSSetOfNone).equals(longSSetOfNone) shouldBe true
        (longSSetOfTwo and longSSetOfTwo).equals(longSSetOfTwo) shouldBe true
        (longSSetOfTwo and longSSetOfThree).equals(longSSetOfTwo) shouldBe true
        (longSSetOfThree and longSSetOfTwo).equals(longSSetOfTwo) shouldBe true

        (longSSetOfThree and longSSetOfNone).equals(longSSetOfNone) shouldBe true
        (longSSetOfThree and longSSetOfThree).equals(longSSetOfThree) shouldBe true
        (FKSet.ofi(2L) and longSSetOfThree).equals(FKSet.ofi(2L)) shouldBe true
        (longSSetOfThree and FKSet.ofi(2L)).equals(FKSet.ofs(2L)) shouldBe true

        // mixed mode

        (longSSetOfThree and longKKSetOfTwo).equals(longSSetOfTwo) shouldBe true
        (longKKSetOfThree and longSSetOfTwo).equals(longKKSetOfTwo) shouldBe true

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

        (longSSetOfNone not longSSetOfNone).equals(longSSetOfNone) shouldBe true
        (longSSetOfNone not longSSetOfOne).equals(longSSetOfNone) shouldBe true

        (longSSetOfOne not longSSetOfNone).equals(longSSetOfOne) shouldBe true
        (longSSetOfOne not longSSetOfOne).equals(longSSetOfNone) shouldBe true
        (longSSetOfOne not longSSetOfThree).equals(longSSetOfNone) shouldBe true
        (longSSetOfThree not longSSetOfOne).equals(FKSet.ofs(2L,3L)) shouldBe true

        (longSSetOfTwo not longSSetOfNone).equals(longSSetOfTwo) shouldBe true
        (longSSetOfTwo not longSSetOfTwo).equals(longSSetOfNone) shouldBe true
        (longSSetOfTwo not longSSetOfThree).equals(longSSetOfNone) shouldBe true
        (longSSetOfThree not longSSetOfTwo).equals(longSSetOfOne3) shouldBe true

        (longSSetOfThree not longSSetOfNone).equals(longSSetOfThree) shouldBe true
        (longSSetOfThree not longSSetOfThree).equals(longSSetOfNone) shouldBe true
        (FKSet.ofs(2L) not longSSetOfThree).equals(longSSetOfNone) shouldBe true
        (longSSetOfThree not FKSet.ofi(2L)).equals(FKSet.ofs(1L,3L)) shouldBe true

        // mixed mode

        (longSSetOfThree not longKKSetOfTwo).equals(longSSetOfOne3) shouldBe true
        (longKKSetOfThree not longSSetOfTwo).equals(longKKSetOfOne3) shouldBe true
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

        (longSSetOfNone or longSSetOfNone).equals(longSSetOfNone) shouldBe true
        (longSSetOfOne or longSSetOfNone).equals(longSSetOfOne) shouldBe true
        (longSSetOfNone or longSSetOfOne).equals(longSSetOfOne) shouldBe true

        (longSSetOfTwo or longSSetOfTwo).equals(longSSetOfTwo) shouldBe true
        (longSSetOfTwo or longSSetOfNone).equals(longSSetOfTwo) shouldBe true
        (longSSetOfNone or longSSetOfTwo).equals(longSSetOfTwo) shouldBe true
        (longSSetOfTwo or longSSetOfTwoOfst1).equals(longSSetOfThree) shouldBe true
        (longSSetOfTwoOfst1 or longSSetOfTwo).equals(longSSetOfThree) shouldBe true
        (longSSetOfTwo or longSSetOfTwoOfst2).equals(longSSetOfFour) shouldBe true
        (longSSetOfTwoOfst2 or longSSetOfTwo).equals(longSSetOfFour) shouldBe true

        (longSSetOfThree or longSSetOfNone).equals(longSSetOfThree) shouldBe true
        (longSSetOfThree or longSSetOfThree).equals(longSSetOfThree) shouldBe true
        (FKSet.ofs(2L) or longSSetOfThree).equals(longSSetOfThree) shouldBe true
        (longSSetOfThree or FKSet.ofs(2)).equals(longSSetOfThree) shouldBe true

        // mixed mode

        (longSSetOfTwoOfst1 or longKKSetOfTwo).equals(longSSetOfThree) shouldBe true
        (longSSetOfTwo or longKKSetOfTwoOfst2).equals(longSSetOfFour) shouldBe true

        (longKKSetOfTwoOfst1 or longSSetOfTwo).equals(longKKSetOfThree) shouldBe true
        (longKKSetOfTwo or longSSetOfTwoOfst2).equals(longKKSetOfFour) shouldBe true
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

        (longSSetOfNone xor longSSetOfNone).equals(longSSetOfNone) shouldBe true
        (longSSetOfNone xor longSSetOfOne).equals(longSSetOfOne) shouldBe true

        (longSSetOfOne xor longSSetOfNone).equals(longSSetOfOne) shouldBe true
        (longSSetOfOne xor longSSetOfOne).equals(longSSetOfNone) shouldBe true
        (longSSetOfOne xor longSSetOfThree).equals(FKSet.ofs(2L,3L)) shouldBe true
        (longSSetOfThree xor longSSetOfOne).equals(FKSet.ofs(2L,3L)) shouldBe true

        (longSSetOfTwo xor longSSetOfNone).equals(longSSetOfTwo) shouldBe true
        (longSSetOfTwo xor longSSetOfTwo).equals(longSSetOfNone) shouldBe true
        (longSSetOfTwo xor longSSetOfThree).equals(longSSetOfOne3) shouldBe true
        (longSSetOfThree xor longSSetOfTwo).equals(longSSetOfOne3) shouldBe true

        (longSSetOfThree xor longSSetOfNone).equals(longSSetOfThree) shouldBe true
        (longSSetOfThree xor longSSetOfThree).equals(longSSetOfNone) shouldBe true
        (FKSet.ofs(2L) xor longSSetOfThree).equals(FKSet.ofs(1L,3L)) shouldBe true
        (longSSetOfThree xor FKSet.ofs(2L)).equals(FKSet.ofs(1L,3L)) shouldBe true

        // mixed mode

        (longSSetOfTwo xor longKKSetOfThree).equals(longSSetOfOne3) shouldBe true
        (longKKSetOfTwo xor longSSetOfThree).equals(longKKSetOfOne3) shouldBe true
    }
})
