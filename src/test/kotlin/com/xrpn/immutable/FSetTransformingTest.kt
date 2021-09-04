package com.xrpn.immutable

import com.xrpn.immutable.FSet.Companion.emptyIMSet
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSetOfNone = FSet.of(*arrayOf<Int>())
private val intSetOfOne = FSet.of(1)
private val intSetOfTwo = FSet.of(1, 2)
private val intSetOfTwoOfst1 = FSet.of(2, 3)
private val intSetOfThree = FSet.of(1, 2, 3)
private val intSetOfFour = FSet.of(1, 2, 3, 4)
private val intSetOfFive = FSet.of(1, 2, 3, 4, 5)


class FSetTransformingTest : FunSpec({

    test("fflatMap") {
        intSetOfNone.fflatMap {FSet.of(it)} shouldBe emptyIMSet()
        intSetOfOne.fflatMap {FSet.of(it)}.equals(intSetOfOne) shouldBe true
        fun arrayBuilderConst(arg: Int) = Array(arg) { arg }
        intSetOfTwo.fflatMap {FSet.of(*arrayBuilderConst(it))}.equals(intSetOfTwo) shouldBe true
        fun arrayBuilderIncrement(arg: Int) = Array(arg) { i -> arg + i }
        intSetOfTwo.fflatMap {FSet.of(*arrayBuilderIncrement(it))}.equals(intSetOfThree) shouldBe true
        intSetOfThree.fflatMap {FSet.of(*arrayBuilderIncrement(it))}.equals(intSetOfFive) shouldBe true
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
