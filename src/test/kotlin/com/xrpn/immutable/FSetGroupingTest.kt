package com.xrpn.immutable

import com.xrpn.immutable.FSet.Companion.emptyIMSet
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
private val intSetOfFive = FSet.of(1, 2, 3, 4, 5)
private val intSetOfSix = FSet.of(1, 2, 3, 4, 5, 6)
private val oracleC = of(of(1), of(2), of(3), of(4),
                         of(1, 2), of(1, 3), of(1, 4),
                         of(2, 3), of(2, 4),
                         of(3, 4))
private val oracleD = of(of(1), of(2), of(3), of(4),
                         of(1, 2), of(1, 3), of(1, 4),
                         of(2, 3), of(2, 4),
                         of(3, 4),
                         of(1, 2, 3), of(1, 2, 4),
                         of(1, 3, 4),
                         of(2, 3, 4))
private val oracleE = of(of(1), of(2), of(3), of(4),
                         of(1, 2), of(1, 3), of(1, 4),
                         of(2, 3), of(2, 4),
                         of(3, 4),
                         of(1, 2, 3), of(1, 2, 4),
                         of(1, 3, 4),
                         of(2, 3, 4),
                         of(1, 2, 3, 4))
private val oracleF = of(FList.of(1, 2), FList.of(1, 3), FList.of(1, 4),
                         FList.of(2, 1), FList.of(3, 1), FList.of(4, 1),
                         FList.of(2, 3), FList.of(2, 4),
                         FList.of(3, 2), FList.of(4, 2),
                         FList.of(3, 4),
                         FList.of(4, 3))
private val oracleG = of(FList.of(1, 2), FList.of(1, 3), FList.of(1, 4), FList.of(1, 5),
                         FList.of(2, 1), FList.of(3, 1), FList.of(4, 1), FList.of(5, 1),
                         FList.of(2, 3), FList.of(2, 4), FList.of(2, 5),
                         FList.of(3, 2), FList.of(4, 2), FList.of(5, 2),
                         FList.of(3, 4), FList.of(3, 5),
                         FList.of(4, 3), FList.of(5, 3),
                         FList.of(4, 5),
                         FList.of(5, 4))

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
        intSetOfNone.fcombinations(-1).equals(intSetOfNone) shouldBe true
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
        intSetOfFour.fcombinations(2).equals(oracleC) shouldBe true
        intSetOfFour.fcombinations(3).equals(oracleD) shouldBe true
        intSetOfFour.fcombinations(4).equals(oracleE) shouldBe true
        intSetOfFour.fcombinations(5).equals(oracleE) shouldBe true

        intSetOfFive.fcombinations(1).size shouldBe 5 // 5! / (5-1)! 1!
        intSetOfFive.fcombinations(2).size shouldBe 15
        intSetOfFive.fcombinations(2).ffilter { it.size == 2 }.size shouldBe 10 // 5! / (5-2)! 2!
        intSetOfFive.fcombinations(3).size shouldBe 25
        intSetOfFive.fcombinations(3).ffilter { it.size == 3 }.size shouldBe 10 // 5! / (5-3)! 3!
        intSetOfFive.fcombinations(4).size shouldBe 30
        intSetOfFive.fcombinations(4).ffilter { it.size == 4 }.size shouldBe 5 // 5! / (5-4)! 4!
        intSetOfFive.fcombinations(5).size shouldBe 31
        intSetOfFive.fcombinations(5).ffilter { it.size == 5 }.size shouldBe 1 // 5! / (5-5)! 5!

        intSetOfSix.fcombinations(1).size shouldBe 6 // 6! / (6-1)! 1!
        intSetOfSix.fcombinations(2).size shouldBe 21
        intSetOfSix.fcombinations(2).ffilter { it.size == 2 }.size shouldBe 15 // 6! / (6-2)! 2!
        intSetOfSix.fcombinations(3).size shouldBe 41
        intSetOfSix.fcombinations(3).ffilter { it.size == 3 }.size shouldBe 20 // 6! / (6-3)! 3!
        intSetOfSix.fcombinations(4).size shouldBe 56
        intSetOfSix.fcombinations(4).ffilter { it.size == 4 }.size shouldBe 15 // 6! / (6-4)! 4!
        intSetOfSix.fcombinations(5).size shouldBe 62
        intSetOfSix.fcombinations(5).ffilter { it.size == 5 }.size shouldBe 6 // 6! / (6-5)! 5!
        intSetOfSix.fcombinations(6).size shouldBe 63
        intSetOfSix.fcombinations(6).ffilter { it.size == 6 }.size shouldBe 1 // 6! / (6-6)! 6!
    }

    test("fcount") { }

    test("fgroupBy") { }

    test("findexed") { }

    test("fpartition") { }

    test("fpermutations") {

        intSetOfNone.fpermutations(-1).equals(intSetOfNone) shouldBe true
        intSetOfNone.fpermutations(0).equals(intSetOfNone) shouldBe true
        intSetOfNone.fpermutations(1).equals(intSetOfNone) shouldBe true

        intSetOfThree.fpermutations(0) shouldBe emptyIMSet()
        val threeByOne = intSetOfThree.fpermutations(1)
        threeByOne.size shouldBe 3 // 3! / (3-1)!
        val threeByTwo = intSetOfThree.fpermutations(2)
        threeByTwo.equals(of(FList.of(1,2),FList.of(1,3),FList.of(2,3),FList.of(2,1),FList.of(3,1),FList.of(3,2))) shouldBe true
        threeByTwo.size shouldBe 6 // 3! / (3-2)!
        val threeByThree = intSetOfThree.fpermutations(3)
        threeByThree.equals(of(FList.of(1,2,3),FList.of(1,3,2),FList.of(2,3,1),FList.of(2,1,3),FList.of(3,1,2),FList.of(3,2,1))) shouldBe true
        threeByThree.size shouldBe 6 // 3! / (3-3)!
        intSetOfThree.fpermutations(4) shouldBe emptyIMSet()

        intSetOfFour.fpermutations(0) shouldBe emptyIMSet()
        val fourByOne = intSetOfFour.fpermutations(1)
        fourByOne.size shouldBe 4 // 4! / (4-1)!
        val fourByTwo = intSetOfFour.fpermutations(2)
        fourByTwo.equals(oracleF) shouldBe true
        fourByTwo.size shouldBe 12 // 4! / (4-2)!
        val fourByThree = intSetOfFour.fpermutations(3)
        fourByThree.size shouldBe 24 // 4! / (4-3)!
//        val fourByFour = intSetOfFour.fpermutations(4)
//        fourByFour.size shouldBe 24 // 4! / (4-4)!
        intSetOfFour.fpermutations(5) shouldBe emptyIMSet()

//        val fiveByOne = intSetOfFive.fpermutations(1)
//        fiveByOne.size shouldBe 5 // 5! / (5-1)!
//        val fiveByTwo = intSetOfFive.fpermutations(2)
//        fiveByTwo.size shouldBe 20 // 5! / (5-2)!
//        fiveByTwo.equals(oracleG) shouldBe true
//        val fiveByThree = intSetOfFive.fpermutations(3)
//        fiveByThree.size shouldBe 60 // 5! / (5-3)!
//        val fiveByfour = intSetOfFive.fpermutations(4)
//        fiveByfour.size shouldBe 120 // 5! / (5-4)!
////        val fiveByfive = intSetOfFive.fpermutations(5)
////        fiveByfive.size shouldBe 120 // 5! / (5-5)!
//
////        val sixByFive = intSetOfSix.fpermutations(5)
////        sixByFive.size shouldBe 120 // 6! / (6-5)!
//        val sixByFive = intSetOfSix.fpermutations(6)
//        sixByFive.size shouldBe 720 // 6! / (6-6)!

    }

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
