package com.xrpn.immutable

import com.xrpn.immutable.FKSet.Companion.emptyIMSet
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSetOfNone = FKSet.ofi(*arrayOf<Int>())
private val intSetOfOne = FKSet.ofi(1)
private val intSetOfTwo = FKSet.ofi(1, 2)
private val intSetOfTwoOfst1 = FKSet.ofi(2, 3)
private val intSetOfThree = FKSet.ofi(1, 2, 3)
private val intSetOfFour = FKSet.ofi(1, 2, 3, 4)
private val intSetOfFive = FKSet.ofi(1, 2, 3, 4, 5)


class FIKSetTransformingTest : FunSpec({

    test("fflatMap") {
        intSetOfNone.fflatMap {FKSet.ofi(it)} shouldBe emptyIMSet()
        intSetOfOne.fflatMap {FKSet.ofi(it)}.equals(intSetOfOne) shouldBe true
        fun arrayBuilderConst(arg: Int) = Array(arg) { arg }
        intSetOfTwo.fflatMap {FKSet.ofi(*arrayBuilderConst(it))}.equals(intSetOfTwo) shouldBe true
        fun arrayBuilderIncrement(arg: Int) = Array(arg) { i -> arg + i }
        intSetOfTwo.fflatMap {FKSet.ofi(*arrayBuilderIncrement(it))}.equals(intSetOfThree) shouldBe true
        intSetOfThree.fflatMap {FKSet.ofi(*arrayBuilderIncrement(it))}.equals(intSetOfFive) shouldBe true
    }

    test("ffold") {
        intSetOfNone.ffold(0) {acc, _ -> acc+1 } shouldBe 0
        intSetOfOne.ffold(0) {acc, _ -> acc+1 } shouldBe 1
        intSetOfTwo.ffold(0) {acc, _ -> acc+1 } shouldBe 2
        intSetOfThree.ffold(0) {acc, _ -> acc+1 } shouldBe 3
        intSetOfFour.ffold(0) {acc, el -> acc+el } shouldBe 10
    }

    test("fmap") {
        intSetOfNone.fmap { it + 1 } shouldBe emptyIMSet()
        intSetOfTwo.fmap { it + 1 }.equals(intSetOfTwoOfst1) shouldBe true
    }

    test("fmapToList") {
        intSetOfNone.fmapToList { it + 1 } shouldBe FList.emptyIMList()
        intSetOfThree.fmapToList { 4 } shouldBe FList.of(4, 4, 4)
    }

    test("freduce") {
        intSetOfNone.freduce {acc, _ -> acc+1 } shouldBe null
        intSetOfOne.freduce {acc, _ -> acc+1 } shouldBe 1
        intSetOfTwo.freduce {acc, _ -> acc+1 } shouldBe 2
        intSetOfThree.freduce {acc, _ -> acc+1 } shouldBe 3
        intSetOfFour.freduce {acc, el -> acc+el } shouldBe 10
    }
})
