package com.xrpn.immutable.fksettest

import com.xrpn.immutable.FKSet
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val longKKSetOfNone = FKSet.ofk(*arrayOf<Long>())
private val longKKSetOfOne = FKSet.ofk(1L).necvs<Long>()!!
private val longKKSetOfOne3 = FKSet.ofk(3L).necvs<Long>()!!
private val longKKSetOfTwo = FKSet.ofk(1L, 2L).necvs<Long>()!!
private val longKKSetOfTwoOfst1 = FKSet.ofk(2L, 3L).necvs<Long>()!!
private val longKKSetOfTwoOfst2 = FKSet.ofk(3L, 4L).necvs<Long>()!!
private val longKKSetOfThree = FKSet.ofk(1L, 2L, 3L).necvs<Long>()!!
private val longKKSetOfFour = FKSet.ofk(1L, 2L, 3L, 4L).necvs<Long>()!!
private val longKKSetMaverick = FKSet.ofk(113L, 97L).necvs<Long>()!!

private val longISetOfNone = FKSet.ofi(*arrayOf<Long>())
private val longISetOfOne = FKSet.ofi(1L).nevs()!!
private val longISetOfOne3 = FKSet.ofi(3L).nevs()!!
private val longISetOfTwo = FKSet.ofi(1L, 2L).nevs()!!
private val longISetOfTwoOfst1 = FKSet.ofi(2L, 3L).nevs()!!
private val longISetOfTwoOfst2 = FKSet.ofi(3L, 4L).nevs()!!
private val longISetOfThree = FKSet.ofi(1L, 2L, 3L).nevs()!!
private val longISetOfFour = FKSet.ofi(1L, 2L, 3L, 4L).nevs()!!
private val longISetMaverick = FKSet.ofi(113L, 97L).nevs()!!

class FKSetLogicLongKIntTest : FunSpec({

    beforeTest {}

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
