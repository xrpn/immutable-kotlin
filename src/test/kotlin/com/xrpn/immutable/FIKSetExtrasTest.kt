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

        (intSetOfThree or intSetOfNone).equal(intSetOfThree) shouldBe true
        (intSetOfThree or intSetOfThree).equal(intSetOfThree) shouldBe true
        (FKSet.ofi(2) or intSetOfThree).equal(intSetOfThree) shouldBe true
        (intSetOfThree or FKSet.ofi(2)).equal(intSetOfThree) shouldBe true
    }

    test("and"){
        (intSetOfNone and intSetOfNone).equal(intSetOfNone) shouldBe true
        (intSetOfNone and intSetOfOne).equal(intSetOfNone) shouldBe true

        (intSetOfOne and intSetOfNone).equal(intSetOfNone) shouldBe true
        (intSetOfOne and intSetOfOne).equal(intSetOfOne) shouldBe true
        (intSetOfOne and intSetOfThree).equal(intSetOfOne) shouldBe true
        (intSetOfThree and intSetOfOne).equal(intSetOfOne) shouldBe true

        (intSetOfTwo and intSetOfNone).equal(intSetOfNone) shouldBe true
        (intSetOfTwo and intSetOfTwo).equal(intSetOfTwo) shouldBe true
        (intSetOfTwo and intSetOfThree).equal(intSetOfTwo) shouldBe true
        (intSetOfThree and intSetOfTwo).equal(intSetOfTwo) shouldBe true

        (intSetOfThree and intSetOfNone).equal(intSetOfNone) shouldBe true
        (intSetOfThree and intSetOfThree).equal(intSetOfThree) shouldBe true
        (FKSet.ofi(2) and intSetOfThree).equal(FKSet.ofi(2)) shouldBe true
        (intSetOfThree and FKSet.ofi(2)).equal(FKSet.ofi(2)) shouldBe true
    }

    test("xor"){
        (intSetOfNone xor intSetOfNone).equal(intSetOfNone) shouldBe true
        (intSetOfNone xor intSetOfOne).equal(intSetOfOne) shouldBe true

        (intSetOfOne xor intSetOfNone).equal(intSetOfOne) shouldBe true
        (intSetOfOne xor intSetOfOne).equal(intSetOfNone) shouldBe true
        (intSetOfOne xor intSetOfThree).equal(FKSet.ofi(2,3)) shouldBe true
        (intSetOfThree xor intSetOfOne).equal(FKSet.ofi(2,3)) shouldBe true

        (intSetOfTwo xor intSetOfNone).equal(intSetOfTwo) shouldBe true
        (intSetOfTwo xor intSetOfTwo).equal(intSetOfNone) shouldBe true
        (intSetOfTwo xor intSetOfThree).equal(FKSet.ofi(3)) shouldBe true
        (intSetOfThree xor intSetOfTwo).equal(FKSet.ofi(3)) shouldBe true

        (intSetOfThree xor intSetOfNone).equal(intSetOfThree) shouldBe true
        (intSetOfThree xor intSetOfThree).equal(intSetOfNone) shouldBe true
        (FKSet.ofi(2) xor intSetOfThree).equal(FKSet.ofi(1,3)) shouldBe true
        (intSetOfThree xor FKSet.ofi(2)).equal(FKSet.ofi(1,3)) shouldBe true
    }

    test("not"){
        (intSetOfNone not intSetOfNone).equal(intSetOfNone) shouldBe true
        (intSetOfNone not intSetOfOne).equal(intSetOfNone) shouldBe true

        (intSetOfOne not intSetOfNone).equal(intSetOfOne) shouldBe true
        (intSetOfOne not intSetOfOne).equal(intSetOfNone) shouldBe true
        (intSetOfOne not intSetOfThree).equal(intSetOfNone) shouldBe true
        (intSetOfThree not intSetOfOne).equal(FKSet.ofi(2,3)) shouldBe true

        (intSetOfTwo not intSetOfNone).equal(intSetOfTwo) shouldBe true
        (intSetOfTwo not intSetOfTwo).equal(intSetOfNone) shouldBe true
        (intSetOfTwo not intSetOfThree).equal(intSetOfNone) shouldBe true
        (intSetOfThree not intSetOfTwo).equal(FKSet.ofi(3)) shouldBe true

        (intSetOfThree not intSetOfNone).equal(intSetOfThree) shouldBe true
        (intSetOfThree not intSetOfThree).equal(intSetOfNone) shouldBe true
        (FKSet.ofi(2) not intSetOfThree).equal(intSetOfNone) shouldBe true
        (intSetOfThree not FKSet.ofi(2)).equal(FKSet.ofi(1,3)) shouldBe true
    }

})
