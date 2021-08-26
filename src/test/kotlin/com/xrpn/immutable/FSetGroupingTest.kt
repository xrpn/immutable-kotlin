package com.xrpn.immutable

import com.xrpn.immutable.FSetOfOne.Companion.toSoO
import com.xrpn.immutable.FSet.Companion.of
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSetOfNone = FSet.of(*arrayOf<Int>())
private val intSetOfOne = FSet.of(1)
private val intSetOfTwo = FSet.of(1, 2)
private val intSetOfTwoOfst1 = FSet.of(2, 3)
private val intSetOfTwoOfst2 = FSet.of(3, 4)
private val intSetOfThree = FSet.of(1, 2, 3)
private val intSetOfFour = FSet.of(1, 2, 3, 4)

class FSetGroupingTest : FunSpec({

    test("fcartesian") {
        intSetOfNone.fcartesian(intSetOfNone).equals(intSetOfNone) shouldBe true
        intSetOfOne.fcartesian(intSetOfNone).equals(intSetOfNone) shouldBe true
        intSetOfNone.fcartesian(intSetOfOne).equals(intSetOfNone) shouldBe true

        intSetOfOne.fcartesian(intSetOfOne).equals(of(Pair(1,1))) shouldBe true

        intSetOfTwo.fcartesian(intSetOfOne).equals(of(Pair(1,1), Pair(2,1))) shouldBe true
        intSetOfOne.fcartesian(intSetOfTwo).equals(of(Pair(1,1), Pair(1,2))) shouldBe true

        intSetOfThree.fcartesian(intSetOfOne).equals(of(Pair(1,1),Pair(2,1),Pair(3,1))) shouldBe true
        intSetOfThree.fcartesian(intSetOfTwo).equals(of(Pair(1,1),Pair(2,1),Pair(3,1),Pair(1,2),Pair(2,2),Pair(3,2))) shouldBe true
        intSetOfOne.fcartesian(intSetOfThree).equals(of(Pair(1,1), Pair(1,2), Pair(1,3))) shouldBe true
        intSetOfTwo.fcartesian(intSetOfThree).equals(of(Pair(1,1), Pair(2,1), Pair(1,2), Pair(2,2), Pair(1,3), Pair(2,3))) shouldBe true
    }

    test("fcombinations") {
        intSetOfNone.fcombinations(0).equals(intSetOfNone) shouldBe true
        intSetOfNone.fcombinations(1).equals(intSetOfNone) shouldBe true
        intSetOfOne.fcombinations(-1).equals(intSetOfNone) shouldBe true
        intSetOfOne.fcombinations(0).equals(intSetOfNone) shouldBe true

        intSetOfOne.fcombinations(1).equals(of(intSetOfOne)) shouldBe true
        intSetOfOne.fcombinations(2).equals(of(intSetOfOne)) shouldBe true

        intSetOfTwo.fcombinations(1).equals(of(intSetOfOne, of(2))) shouldBe true
        intSetOfTwo.fcombinations(2).equals(of(of(1), of(2), of(1, 2))) shouldBe true
        intSetOfTwo.fcombinations(3).equals(of(of(1), of(2), of(1, 2))) shouldBe true

        intSetOfThree.fcombinations(1).equals(of(of(1), of(2), of(3))) shouldBe true
        val oracleA = of(of(1), of(2), of(3), of(1, 2), of(1, 3), of(3, 2))
        intSetOfThree.fcombinations(2).equals(oracleA) shouldBe true
        val oracleB = of(of(1), of(2), of(3), of(1, 2), of(1, 3), of(3, 2), of(3, 1, 2))
        intSetOfThree.fcombinations(3).equals(oracleB) shouldBe true
        intSetOfThree.fcombinations(4).equals(oracleB) shouldBe true

        intSetOfFour.fcombinations(1).equals(of(of(1), of(2), of(3), of(4))) shouldBe true
        val oracleC = of(of(1), of(2), of(3), of(4), of(1, 2), of(1, 3), of(1, 4), of(2, 3), of(2, 4), of(3, 4))
        intSetOfFour.fcombinations(2).equals(oracleC) shouldBe true
        val oracleD = of(of(1), of(2), of(3), of(4),
            of(1, 2), of(1, 3), of(1, 4),
            of(2, 3), of(2, 4),
            of(3, 4),
            of(1, 2, 3), of(1, 2, 4),
            of(1, 3, 4),
            of(2, 3, 4)
        )
        intSetOfFour.fcombinations(3).equals(oracleD) shouldBe true
        val oracleE = of(of(1), of(2), of(3), of(4),
            of(1, 2), of(1, 3), of(1, 4),
            of(2, 3), of(2, 4),
            of(3, 4),
            of(1, 2, 3), of(1, 2, 4),
            of(1, 3, 4),
            of(2, 3, 4),
            of(1, 2, 3, 4)
        )
        intSetOfFour.fcombinations(4).equals(oracleE) shouldBe true
        intSetOfFour.fcombinations(5).equals(oracleE) shouldBe true
    }

    test("fcount") { }

    test("fgroupBy") { }

    test("findexed") { }

    test("fpartition") { }

    test("fpermutations") { }

    test("fpopAndReminder") {
        val (nilPop, nilReminder) = FSet.emptyIMSet<Int>().fpopAndReminder()
        nilPop shouldBe null
        nilReminder shouldBe FSet.emptyIMSet<Int>()

        val (onePop, oneReminder) = FSet.of(1).fpopAndReminder()
        onePop shouldBe 1
        oneReminder shouldBe  FSet.emptyIMSet<Int>()

        // this traverses slideShareTree popping one element at a time, and rebuilding the set with the popped element
        val res = FSetBody(frbSlideShareTree).ffold(Pair(FSet.emptyIMSet<Int>(), FSetBody(frbSlideShareTree).fpopAndReminder())) { acc, _ ->
            val (rebuild, popAndStub) = acc
            val (pop, stub) = popAndStub
            Pair(rebuild.fadd(pop!!.toSoO()), stub.fpopAndReminder())
        }
        res.first.equals(FSetBody(frbSlideShareTree)) shouldBe true
        val (lastPopped, lastReminder) = res.second
        lastPopped shouldBe null
        lastReminder shouldBe FSet.emptyIMSet<Int>()
    }

    test("fsize") { }
})
