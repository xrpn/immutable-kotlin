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

private val longSSetOfNone = FKSet.ofs(*arrayOf<Long>())
private val longSSetOfOne = FKSet.ofs(1L).nevs()!!
private val longSSetOfOne3 = FKSet.ofs(3L).nevs()!!
private val longSSetOfTwo = FKSet.ofs(1L, 2L).nevs()!!
private val longSSetOfTwoOfst1 = FKSet.ofs(2L, 3L).nevs()!!
private val longSSetOfTwoOfst2 = FKSet.ofs(3L, 4L).nevs()!!
private val longSSetOfThree = FKSet.ofs(1L, 2L, 3L).nevs()!!
private val longSSetOfFour = FKSet.ofs(1L, 2L, 3L, 4L).nevs()!!
private val longSSetMaverick = FKSet.ofs(113L, 97L).nevs()!!

class FKSetLogicLongKStrTest : FunSpec({

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
