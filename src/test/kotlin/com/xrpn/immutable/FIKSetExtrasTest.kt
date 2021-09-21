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

        (intSetOfThree or intSetOfNone).strongEqual(intSetOfThree) shouldBe true
        (intSetOfThree or intSetOfThree).strongEqual(intSetOfThree) shouldBe true
        (FKSet.ofi(2) or intSetOfThree).strongEqual(intSetOfThree) shouldBe true
        (intSetOfThree or FKSet.ofi(2)).strongEqual(intSetOfThree) shouldBe true
    }

    test("and"){
        (intSetOfNone and intSetOfNone).strongEqual(intSetOfNone) shouldBe true
        (intSetOfNone and intSetOfOne).strongEqual(intSetOfNone) shouldBe true

        (intSetOfOne and intSetOfNone).strongEqual(intSetOfNone) shouldBe true
        (intSetOfOne and intSetOfOne).strongEqual(intSetOfOne) shouldBe true
        (intSetOfOne and intSetOfThree).strongEqual(intSetOfOne) shouldBe true
        (intSetOfThree and intSetOfOne).strongEqual(intSetOfOne) shouldBe true

        (intSetOfTwo and intSetOfNone).strongEqual(intSetOfNone) shouldBe true
        (intSetOfTwo and intSetOfTwo).strongEqual(intSetOfTwo) shouldBe true
        (intSetOfTwo and intSetOfThree).strongEqual(intSetOfTwo) shouldBe true
        (intSetOfThree and intSetOfTwo).strongEqual(intSetOfTwo) shouldBe true

        (intSetOfThree and intSetOfNone).strongEqual(intSetOfNone) shouldBe true
        (intSetOfThree and intSetOfThree).strongEqual(intSetOfThree) shouldBe true
        (FKSet.ofi(2) and intSetOfThree).strongEqual(FKSet.ofi(2)) shouldBe true
        (intSetOfThree and FKSet.ofi(2)).strongEqual(FKSet.ofi(2)) shouldBe true
    }

    test("xor"){
        (intSetOfNone xor intSetOfNone).strongEqual(intSetOfNone) shouldBe true
        (intSetOfNone xor intSetOfOne).strongEqual(intSetOfOne) shouldBe true

        (intSetOfOne xor intSetOfNone).strongEqual(intSetOfOne) shouldBe true
        (intSetOfOne xor intSetOfOne).strongEqual(intSetOfNone) shouldBe true
        (intSetOfOne xor intSetOfThree).strongEqual(FKSet.ofi(2,3)) shouldBe true
        (intSetOfThree xor intSetOfOne).strongEqual(FKSet.ofi(2,3)) shouldBe true

        (intSetOfTwo xor intSetOfNone).strongEqual(intSetOfTwo) shouldBe true
        (intSetOfTwo xor intSetOfTwo).strongEqual(intSetOfNone) shouldBe true
        (intSetOfTwo xor intSetOfThree).strongEqual(FKSet.ofi(3)) shouldBe true
        (intSetOfThree xor intSetOfTwo).strongEqual(FKSet.ofi(3)) shouldBe true

        (intSetOfThree xor intSetOfNone).strongEqual(intSetOfThree) shouldBe true
        (intSetOfThree xor intSetOfThree).strongEqual(intSetOfNone) shouldBe true
        (FKSet.ofi(2) xor intSetOfThree).strongEqual(FKSet.ofi(1,3)) shouldBe true
        (intSetOfThree xor FKSet.ofi(2)).strongEqual(FKSet.ofi(1,3)) shouldBe true
    }

    test("not"){
        (intSetOfNone not intSetOfNone).strongEqual(intSetOfNone) shouldBe true
        (intSetOfNone not intSetOfOne).strongEqual(intSetOfNone) shouldBe true

        (intSetOfOne not intSetOfNone).strongEqual(intSetOfOne) shouldBe true
        (intSetOfOne not intSetOfOne).strongEqual(intSetOfNone) shouldBe true
        (intSetOfOne not intSetOfThree).strongEqual(intSetOfNone) shouldBe true
        (intSetOfThree not intSetOfOne).strongEqual(FKSet.ofi(2,3)) shouldBe true

        (intSetOfTwo not intSetOfNone).strongEqual(intSetOfTwo) shouldBe true
        (intSetOfTwo not intSetOfTwo).strongEqual(intSetOfNone) shouldBe true
        (intSetOfTwo not intSetOfThree).strongEqual(intSetOfNone) shouldBe true
        (intSetOfThree not intSetOfTwo).strongEqual(FKSet.ofi(3)) shouldBe true

        (intSetOfThree not intSetOfNone).strongEqual(intSetOfThree) shouldBe true
        (intSetOfThree not intSetOfThree).strongEqual(intSetOfNone) shouldBe true
        (FKSet.ofi(2) not intSetOfThree).strongEqual(intSetOfNone) shouldBe true
        (intSetOfThree not FKSet.ofi(2)).strongEqual(FKSet.ofi(1,3)) shouldBe true
    }

})
