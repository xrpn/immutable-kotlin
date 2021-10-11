package com.xrpn.immutable.fkksettest

import com.xrpn.immutable.FKSet
import com.xrpn.immutable.FKSet.Companion.ofk
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intKKsetOfNone: FKSet<Int, Int> = FKSet.ofi(*arrayOf())
private val intKKsetOfOne = ofk(1)
private val intKKsetOfTwo = ofk(1, 2)
private val intKKsetOfTwoOfst1 = ofk(2, 3)
private val intKKsetOfTwoOfst2 = ofk(3, 4)
private val intKKsetOfThree = ofk(1, 2, 3)
private val intKKsetOfFour = ofk(1, 2, 3, 4)

private val longKKsetOfNone: FKSet<Long, Long> = FKSet.ofk(*arrayOf())
private val longKKsetOfOne = ofk(1L)
private val longKKsetOfTwo = ofk(1L, 2L)
private val longKKsetOfTwoOfst1 = ofk(2L, 3L)
private val longKKsetOfTwoOfst2 = ofk(3L, 4L)
private val longKKsetOfThree = ofk(1L, 2L, 3L)
private val longKKsetOfFour = ofk(1L, 2L, 3L, 4L)

class FKKSetExtrasTest : FunSpec({

    beforeTest {}

    test("or"){
        (intKKsetOfNone or intKKsetOfNone).equals(intKKsetOfNone) shouldBe true
        (intKKsetOfOne or intKKsetOfNone).equals(intKKsetOfOne) shouldBe true
        (intKKsetOfNone or intKKsetOfOne).equals(intKKsetOfOne) shouldBe true

        (intKKsetOfTwo or intKKsetOfTwo).equals(intKKsetOfTwo) shouldBe true
        (intKKsetOfTwo or intKKsetOfNone).equals(intKKsetOfTwo) shouldBe true
        (intKKsetOfNone or intKKsetOfTwo).equals(intKKsetOfTwo) shouldBe true
        (intKKsetOfTwo or intKKsetOfTwoOfst1).equals(intKKsetOfThree) shouldBe true
        (intKKsetOfTwoOfst1 or intKKsetOfTwo).equals(intKKsetOfThree) shouldBe true
        (intKKsetOfTwo or intKKsetOfTwoOfst2).equals(intKKsetOfFour) shouldBe true
        (intKKsetOfTwoOfst2 or intKKsetOfTwo).equals(intKKsetOfFour) shouldBe true

        (intKKsetOfThree or intKKsetOfNone).equals(intKKsetOfThree) shouldBe true
        (intKKsetOfThree or intKKsetOfThree).equals(intKKsetOfThree) shouldBe true
        (FKSet.ofi(2) or intKKsetOfThree).equals(intKKsetOfThree) shouldBe true
        (intKKsetOfThree or FKSet.ofi(2)).equals(intKKsetOfThree) shouldBe true

        (longKKsetOfNone or longKKsetOfNone).equals(longKKsetOfNone) shouldBe true
        (longKKsetOfOne or longKKsetOfNone).equals(longKKsetOfOne) shouldBe true
        (longKKsetOfNone or longKKsetOfOne).equals(longKKsetOfOne) shouldBe true

        (longKKsetOfTwo or longKKsetOfTwo).equals(longKKsetOfTwo) shouldBe true
        (longKKsetOfTwo or longKKsetOfNone).equals(longKKsetOfTwo) shouldBe true
        (longKKsetOfNone or longKKsetOfTwo).equals(longKKsetOfTwo) shouldBe true
        (longKKsetOfTwo or longKKsetOfTwoOfst1).equals(longKKsetOfThree) shouldBe true
        (longKKsetOfTwoOfst1 or longKKsetOfTwo).equals(longKKsetOfThree) shouldBe true
        (longKKsetOfTwo or longKKsetOfTwoOfst2).equals(longKKsetOfFour) shouldBe true
        (longKKsetOfTwoOfst2 or longKKsetOfTwo).equals(longKKsetOfFour) shouldBe true

        (longKKsetOfThree or longKKsetOfNone).equals(longKKsetOfThree) shouldBe true
        (longKKsetOfThree or longKKsetOfThree).equals(longKKsetOfThree) shouldBe true
        (FKSet.ofk(2L) or longKKsetOfThree).equals(longKKsetOfThree) shouldBe true
        (longKKsetOfThree or FKSet.ofk(2L)).equals(longKKsetOfThree) shouldBe true

    }

    test("and"){
        (intKKsetOfNone and intKKsetOfNone).equals(intKKsetOfNone) shouldBe true
        (intKKsetOfNone and intKKsetOfOne).equals(intKKsetOfNone) shouldBe true

        (intKKsetOfOne and intKKsetOfNone).equals(intKKsetOfNone) shouldBe true
        (intKKsetOfOne and intKKsetOfOne).equals(intKKsetOfOne) shouldBe true
        (intKKsetOfOne and intKKsetOfThree).equals(intKKsetOfOne) shouldBe true
        (intKKsetOfThree and intKKsetOfOne).equals(intKKsetOfOne) shouldBe true

        (intKKsetOfTwo and intKKsetOfNone).equals(intKKsetOfNone) shouldBe true
        (intKKsetOfTwo and intKKsetOfTwo).equals(intKKsetOfTwo) shouldBe true
        (intKKsetOfTwo and intKKsetOfThree).equals(intKKsetOfTwo) shouldBe true
        (intKKsetOfThree and intKKsetOfTwo).equals(intKKsetOfTwo) shouldBe true

        (intKKsetOfThree and intKKsetOfNone).equals(intKKsetOfNone) shouldBe true
        (intKKsetOfThree and intKKsetOfThree).equals(intKKsetOfThree) shouldBe true
        (FKSet.ofi(2) and intKKsetOfThree).equals(FKSet.ofi(2)) shouldBe true
        (intKKsetOfThree and FKSet.ofi(2)).equals(FKSet.ofi(2)) shouldBe true

        (longKKsetOfNone and longKKsetOfNone).equals(longKKsetOfNone) shouldBe true
        (longKKsetOfNone and longKKsetOfOne).equals(longKKsetOfNone) shouldBe true

        (longKKsetOfOne and longKKsetOfNone).equals(longKKsetOfNone) shouldBe true
        (longKKsetOfOne and longKKsetOfOne).equals(longKKsetOfOne) shouldBe true
        (longKKsetOfOne and longKKsetOfThree).equals(longKKsetOfOne) shouldBe true
        (longKKsetOfThree and longKKsetOfOne).equals(longKKsetOfOne) shouldBe true

        (longKKsetOfTwo and longKKsetOfNone).equals(longKKsetOfNone) shouldBe true
        (longKKsetOfTwo and longKKsetOfTwo).equals(longKKsetOfTwo) shouldBe true
        (longKKsetOfTwo and longKKsetOfThree).equals(longKKsetOfTwo) shouldBe true
        (longKKsetOfThree and longKKsetOfTwo).equals(longKKsetOfTwo) shouldBe true

        (longKKsetOfThree and longKKsetOfNone).equals(longKKsetOfNone) shouldBe true
        (longKKsetOfThree and longKKsetOfThree).equals(longKKsetOfThree) shouldBe true
        (FKSet.ofk(2L) and longKKsetOfThree).equals(FKSet.ofk(2L)) shouldBe true
        (longKKsetOfThree and FKSet.ofi(2)).equals(FKSet.ofk(2L)) shouldBe true

    }

    test("xor"){
        (intKKsetOfNone xor intKKsetOfNone).equals(intKKsetOfNone) shouldBe true
        (intKKsetOfNone xor intKKsetOfOne).equals(intKKsetOfOne) shouldBe true

        (intKKsetOfOne xor intKKsetOfNone).equals(intKKsetOfOne) shouldBe true
        (intKKsetOfOne xor intKKsetOfOne).equals(intKKsetOfNone) shouldBe true
        (intKKsetOfOne xor intKKsetOfThree).equals(FKSet.ofi(2,3)) shouldBe true
        (intKKsetOfThree xor intKKsetOfOne).equals(FKSet.ofi(2,3)) shouldBe true

        (intKKsetOfTwo xor intKKsetOfNone).equals(intKKsetOfTwo) shouldBe true
        (intKKsetOfTwo xor intKKsetOfTwo).equals(intKKsetOfNone) shouldBe true
        (intKKsetOfTwo xor intKKsetOfThree).equals(FKSet.ofi(3)) shouldBe true
        (intKKsetOfThree xor intKKsetOfTwo).equals(FKSet.ofi(3)) shouldBe true

        (intKKsetOfThree xor intKKsetOfNone).equals(intKKsetOfThree) shouldBe true
        (intKKsetOfThree xor intKKsetOfThree).equals(intKKsetOfNone) shouldBe true
        (FKSet.ofi(2) xor intKKsetOfThree).equals(FKSet.ofi(1,3)) shouldBe true
        (intKKsetOfThree xor FKSet.ofi(2)).equals(FKSet.ofi(1,3)) shouldBe true

        (longKKsetOfNone xor longKKsetOfNone).equals(longKKsetOfNone) shouldBe true
        (longKKsetOfNone xor longKKsetOfOne).equals(longKKsetOfOne) shouldBe true

        (longKKsetOfOne xor longKKsetOfNone).equals(longKKsetOfOne) shouldBe true
        (longKKsetOfOne xor longKKsetOfOne).equals(longKKsetOfNone) shouldBe true
        (longKKsetOfOne xor longKKsetOfThree).equals(FKSet.ofk(2L,3L)) shouldBe true
        (longKKsetOfThree xor longKKsetOfOne).equals(FKSet.ofk(2L,3L)) shouldBe true

        (longKKsetOfTwo xor longKKsetOfNone).equals(longKKsetOfTwo) shouldBe true
        (longKKsetOfTwo xor longKKsetOfTwo).equals(longKKsetOfNone) shouldBe true
        (longKKsetOfTwo xor longKKsetOfThree).equals(FKSet.ofk(3L)) shouldBe true
        (longKKsetOfThree xor longKKsetOfTwo).equals(FKSet.ofk(3L)) shouldBe true

        (longKKsetOfThree xor longKKsetOfNone).equals(longKKsetOfThree) shouldBe true
        (longKKsetOfThree xor longKKsetOfThree).equals(longKKsetOfNone) shouldBe true
        (FKSet.ofk(2L) xor longKKsetOfThree).equals(FKSet.ofk(1L,3L)) shouldBe true
        (longKKsetOfThree xor FKSet.ofk(2L)).equals(FKSet.ofk(1L,3L)) shouldBe true

    }

    test("not"){
        (intKKsetOfNone not intKKsetOfNone).equals(intKKsetOfNone) shouldBe true
        (intKKsetOfNone not intKKsetOfOne).equals(intKKsetOfNone) shouldBe true

        (intKKsetOfOne not intKKsetOfNone).equals(intKKsetOfOne) shouldBe true
        (intKKsetOfOne not intKKsetOfOne).equals(intKKsetOfNone) shouldBe true
        (intKKsetOfOne not intKKsetOfThree).equals(intKKsetOfNone) shouldBe true
        (intKKsetOfThree not intKKsetOfOne).equals(FKSet.ofi(2,3)) shouldBe true

        (intKKsetOfTwo not intKKsetOfNone).equals(intKKsetOfTwo) shouldBe true
        (intKKsetOfTwo not intKKsetOfTwo).equals(intKKsetOfNone) shouldBe true
        (intKKsetOfTwo not intKKsetOfThree).equals(intKKsetOfNone) shouldBe true
        (intKKsetOfThree not intKKsetOfTwo).equals(FKSet.ofi(3)) shouldBe true

        (intKKsetOfThree not intKKsetOfNone).equals(intKKsetOfThree) shouldBe true
        (intKKsetOfThree not intKKsetOfThree).equals(intKKsetOfNone) shouldBe true
        (FKSet.ofi(2) not intKKsetOfThree).equals(intKKsetOfNone) shouldBe true
        (intKKsetOfThree not FKSet.ofi(2)).equals(FKSet.ofi(1,3)) shouldBe true

        (longKKsetOfNone not longKKsetOfNone).equals(longKKsetOfNone) shouldBe true
        (longKKsetOfNone not longKKsetOfOne).equals(longKKsetOfNone) shouldBe true

        (longKKsetOfOne not longKKsetOfNone).equals(longKKsetOfOne) shouldBe true
        (longKKsetOfOne not longKKsetOfOne).equals(longKKsetOfNone) shouldBe true
        (longKKsetOfOne not longKKsetOfThree).equals(longKKsetOfNone) shouldBe true
        (longKKsetOfThree not longKKsetOfOne).equals(FKSet.ofk(2L,3L)) shouldBe true

        (longKKsetOfTwo not longKKsetOfNone).equals(longKKsetOfTwo) shouldBe true
        (longKKsetOfTwo not longKKsetOfTwo).equals(longKKsetOfNone) shouldBe true
        (longKKsetOfTwo not longKKsetOfThree).equals(longKKsetOfNone) shouldBe true
        (longKKsetOfThree not longKKsetOfTwo).equals(FKSet.ofk(3L)) shouldBe true

        (longKKsetOfThree not longKKsetOfNone).equals(longKKsetOfThree) shouldBe true
        (longKKsetOfThree not longKKsetOfThree).equals(longKKsetOfNone) shouldBe true
        (FKSet.ofk(2L) not longKKsetOfThree).equals(longKKsetOfNone) shouldBe true
        (longKKsetOfThree not FKSet.ofk(2L)).equals(FKSet.ofk(1L,3L)) shouldBe true

    }

})
