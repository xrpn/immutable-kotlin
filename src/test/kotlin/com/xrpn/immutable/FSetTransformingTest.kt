package com.xrpn.immutable

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSetOfNone = FSet.of(*arrayOf<Int>())
private val intSetOfOne = FSet.of(1)
private val intSetOfTwo = FSet.of(1, 2)
private val intSetOfTwoOfst1 = FSet.of(2, 3)
private val intSetOfTwoOfst2 = FSet.of(3, 4)
private val intSetOfThree = FSet.of(1, 2, 3)
private val intSetOfFour = FSet.of(1, 2, 3, 4)


class FSetTransformingTest : FunSpec({

    test("fflatMap") { }

    test("ffold") {
        intSetOfNone.ffold(0) {acc, _ -> acc+1 } shouldBe 0
        intSetOfOne.ffold(0) {acc, _ -> acc+1 } shouldBe 1
        intSetOfTwo.ffold(0) {acc, _ -> acc+1 } shouldBe 2
        intSetOfThree.ffold(0) {acc, _ -> acc+1 } shouldBe 3
        intSetOfFour.ffold(0) {acc, el -> acc+el } shouldBe 10
    }

    test("fmap") { }

    test("fmapToList") { }

    test("freduce") { }
})
