package com.xrpn.immutable

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSetOfNone: FKSet<Int, Int> = FKSet.ofi(*arrayOf())
private val intSetOfOne = FKSet.ofi(1)
private val intSetOfTwo = FKSet.ofi(1, 2)
private val intSetOfTwoOfst1 = FKSet.ofi(2, 3)
private val intSetOfTwoOfst2 = FKSet.ofi(3, 4)
private val intSetOfThree = FKSet.ofi(1, 2, 3)
private val intSetOfFour = FKSet.ofi(1, 2, 3, 4)

class FIKSetExtrasTest : FunSpec({

    beforeTest {}

    test("or"){
        (intSetOfNone or intSetOfNone).equals(intSetOfNone) shouldBe true
        (intSetOfOne or intSetOfNone).equals(intSetOfOne) shouldBe true
        (intSetOfNone or intSetOfOne).equals(intSetOfOne) shouldBe true

        (intSetOfTwo or intSetOfTwo).equals(intSetOfTwo) shouldBe true
        (intSetOfTwo or intSetOfNone).equals(intSetOfTwo) shouldBe true
        (intSetOfNone or intSetOfTwo).equals(intSetOfTwo) shouldBe true
        (intSetOfTwo or intSetOfTwoOfst1).equals(intSetOfThree) shouldBe true
        (intSetOfTwoOfst1 or intSetOfTwo).equals(intSetOfThree) shouldBe true
        (intSetOfTwo or intSetOfTwoOfst2).equals(intSetOfFour) shouldBe true
        (intSetOfTwoOfst2 or intSetOfTwo).equals(intSetOfFour) shouldBe true

        (intSetOfThree or intSetOfNone).equals(intSetOfThree) shouldBe true
        (intSetOfThree or intSetOfThree).equals(intSetOfThree) shouldBe true
        (FKSet.ofi(2) or intSetOfThree).equals(intSetOfThree) shouldBe true
        (intSetOfThree or FKSet.ofi(2)).equals(intSetOfThree) shouldBe true
    }

    test("and"){
        (intSetOfNone and intSetOfNone).equals(intSetOfNone) shouldBe true
        (intSetOfNone and intSetOfOne).equals(intSetOfNone) shouldBe true

        (intSetOfOne and intSetOfNone).equals(intSetOfNone) shouldBe true
        (intSetOfOne and intSetOfOne).equals(intSetOfOne) shouldBe true
        (intSetOfOne and intSetOfThree).equals(intSetOfOne) shouldBe true
        (intSetOfThree and intSetOfOne).equals(intSetOfOne) shouldBe true

        (intSetOfTwo and intSetOfNone).equals(intSetOfNone) shouldBe true
        (intSetOfTwo and intSetOfTwo).equals(intSetOfTwo) shouldBe true
        (intSetOfTwo and intSetOfThree).equals(intSetOfTwo) shouldBe true
        (intSetOfThree and intSetOfTwo).equals(intSetOfTwo) shouldBe true

        (intSetOfThree and intSetOfNone).equals(intSetOfNone) shouldBe true
        (intSetOfThree and intSetOfThree).equals(intSetOfThree) shouldBe true
        (FKSet.ofi(2) and intSetOfThree).equals(FKSet.ofi(2)) shouldBe true
        (intSetOfThree and FKSet.ofi(2)).equals(FKSet.ofi(2)) shouldBe true
    }

    test("xor"){
        (intSetOfNone xor intSetOfNone).equals(intSetOfNone) shouldBe true
        (intSetOfNone xor intSetOfOne).equals(intSetOfOne) shouldBe true

        (intSetOfOne xor intSetOfNone).equals(intSetOfOne) shouldBe true
        (intSetOfOne xor intSetOfOne).equals(intSetOfNone) shouldBe true
        (intSetOfOne xor intSetOfThree).equals(FKSet.ofi(2,3)) shouldBe true
        (intSetOfThree xor intSetOfOne).equals(FKSet.ofi(2,3)) shouldBe true

        (intSetOfTwo xor intSetOfNone).equals(intSetOfTwo) shouldBe true
        (intSetOfTwo xor intSetOfTwo).equals(intSetOfNone) shouldBe true
        (intSetOfTwo xor intSetOfThree).equals(FKSet.ofi(3)) shouldBe true
        (intSetOfThree xor intSetOfTwo).equals(FKSet.ofi(3)) shouldBe true

        (intSetOfThree xor intSetOfNone).equals(intSetOfThree) shouldBe true
        (intSetOfThree xor intSetOfThree).equals(intSetOfNone) shouldBe true
        (FKSet.ofi(2) xor intSetOfThree).equals(FKSet.ofi(1,3)) shouldBe true
        (intSetOfThree xor FKSet.ofi(2)).equals(FKSet.ofi(1,3)) shouldBe true
    }

    test("not"){
        (intSetOfNone not intSetOfNone).equals(intSetOfNone) shouldBe true
        (intSetOfNone not intSetOfOne).equals(intSetOfNone) shouldBe true

        (intSetOfOne not intSetOfNone).equals(intSetOfOne) shouldBe true
        (intSetOfOne not intSetOfOne).equals(intSetOfNone) shouldBe true
        (intSetOfOne not intSetOfThree).equals(intSetOfNone) shouldBe true
        (intSetOfThree not intSetOfOne).equals(FKSet.ofi(2,3)) shouldBe true

        (intSetOfTwo not intSetOfNone).equals(intSetOfTwo) shouldBe true
        (intSetOfTwo not intSetOfTwo).equals(intSetOfNone) shouldBe true
        (intSetOfTwo not intSetOfThree).equals(intSetOfNone) shouldBe true
        (intSetOfThree not intSetOfTwo).equals(FKSet.ofi(3)) shouldBe true

        (intSetOfThree not intSetOfNone).equals(intSetOfThree) shouldBe true
        (intSetOfThree not intSetOfThree).equals(intSetOfNone) shouldBe true
        (FKSet.ofi(2) not intSetOfThree).equals(intSetOfNone) shouldBe true
        (intSetOfThree not FKSet.ofi(2)).equals(FKSet.ofi(1,3)) shouldBe true
    }

})
