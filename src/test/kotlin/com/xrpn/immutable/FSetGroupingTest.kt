package com.xrpn.immutable

import com.xrpn.hash.JohnsonTrotter
import com.xrpn.hash.JohnsonTrotter.smallFact
import com.xrpn.immutable.FSet.Companion.emptyIMSet
import com.xrpn.immutable.FSet.Companion.of
import com.xrpn.immutable.FSetOfOne.Companion.toSoO
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSetOfNone = FSet.of(*arrayOf<Int>())
private val intSetOfOne = FSet.of(1)
private val intSetOfTwo = FSet.of(1, 2)
private val intSetOfThree = FSet.of(1, 2, 3)
private val intSetOfFour = FSet.of(1, 2, 3, 4)
private val strSetOfFour = FSet.of("a","b","c","d")
private val intSetOfFive = FSet.of(1, 2, 3, 4, 5)
private val intSetOfSix = FSet.of(1, 2, 3, 4, 5, 6)
private val intSetOfSeven = FSet.of(1, 2, 3, 4, 5, 6, 7)
private val intSetOfEight = FSet.of(1, 2, 3, 4, 5, 6, 7, 8)
private val intSetOfNine = FSet.of(1, 2, 3, 4, 5, 6, 7, 8, 9)
private val intSetOfTen = FSet.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
private val intSetOfEleven = FSet.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)
private val intSetOfTwelve = FSet.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

private val oracleC: FSet<FSet<Int>> = of(of(1), of(2), of(3), of(4),
                         of(1, 2), of(1, 3), of(1, 4),
                         of(2, 3), of(2, 4),
                         of(3, 4))
private val oracleD: FSet<FSet<Int>> = of(of(1), of(2), of(3), of(4),
                         of(1, 2), of(1, 3), of(1, 4),
                         of(2, 3), of(2, 4),
                         of(3, 4),
                         of(1, 2, 3), of(1, 2, 4),
                         of(1, 3, 4),
                         of(2, 3, 4))
private val oracleE: FSet<FSet<Int>> = of(of(1), of(2), of(3), of(4),
                         of(1, 2), of(1, 3), of(1, 4),
                         of(2, 3), of(2, 4),
                         of(3, 4),
                         of(1, 2, 3), of(1, 2, 4),
                         of(1, 3, 4),
                         of(2, 3, 4),
                         of(1, 2, 3, 4))
private val oracleF: FSet<FList<Int>> = of(FList.of(1, 2), FList.of(1, 3), FList.of(1, 4),
                         FList.of(2, 1), FList.of(3, 1), FList.of(4, 1),
                         FList.of(2, 3), FList.of(2, 4),
                         FList.of(3, 2), FList.of(4, 2),
                         FList.of(3, 4),
                         FList.of(4, 3))
private val oracleG: FSet<FList<Int>> = of(FList.of(1, 2), FList.of(1, 3), FList.of(1, 4), FList.of(1, 5),
                         FList.of(2, 1), FList.of(3, 1), FList.of(4, 1), FList.of(5, 1),
                         FList.of(2, 3), FList.of(2, 4), FList.of(2, 5),
                         FList.of(3, 2), FList.of(4, 2), FList.of(5, 2),
                         FList.of(3, 4), FList.of(3, 5),
                         FList.of(4, 3), FList.of(5, 3),
                         FList.of(4, 5),
                         FList.of(5, 4))

class FSetGroupingTest : FunSpec({

    // val repeats = 50
    val longTest = false
    val verbose = true

    beforeTest {}

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

        intSetOfSeven.fcombinations(1).size shouldBe 7 // 7! / (7-1)! 1!
        intSetOfSeven.fcombinations(2).size shouldBe 28
        intSetOfSeven.fcombinations(2).ffilter { it.size == 2 }.size shouldBe 21 // 7! / (7-2)! 2!
        intSetOfSeven.fcombinations(3).size shouldBe 63
        intSetOfSeven.fcombinations(3).ffilter { it.size == 3 }.size shouldBe 35 // 7! / (7-3)! 3!
        intSetOfSeven.fcombinations(4).size shouldBe 98
        intSetOfSeven.fcombinations(4).ffilter { it.size == 4 }.size shouldBe 35 // 7! / (7-4)! 4!
        intSetOfSeven.fcombinations(5).size shouldBe 119
        intSetOfSeven.fcombinations(5).ffilter { it.size == 5 }.size shouldBe 21 // 7! / (7-5)! 5!
        intSetOfSeven.fcombinations(6).size shouldBe 126
        intSetOfSeven.fcombinations(6).ffilter { it.size == 6 }.size shouldBe 7 // 7! / (7-6)! 6!
        intSetOfSeven.fcombinations(7).size shouldBe 127
        intSetOfSeven.fcombinations(7).ffilter { it.size == 7 }.size shouldBe 1 // 7! / (7-7)! 7!

        fun tot(n: Int): Int {
            var acc = 0
            for (i in (1 ..n)) { acc += (smallFact(n) / (smallFact(n - i) * smallFact(i))) }
            return acc
        }

        intSetOfEight.fcombinations(8).size shouldBe tot(8)
        intSetOfEight.fcombinations(8).filter { it.size == 8 }.size shouldBe 1
        intSetOfNine.fcombinations(9).size shouldBe tot(9)
        intSetOfNine.fcombinations(9).filter { it.size == 9 }.size shouldBe 1
        intSetOfTen.fcombinations(10).size shouldBe tot(10)
        intSetOfTen.fcombinations(10).filter { it.size == 10 }.size shouldBe 1
        intSetOfEleven.fcombinations(11).size shouldBe tot(11)
        intSetOfEleven.fcombinations(11).filter { it.size == 11 }.size shouldBe 1
        intSetOfTwelve.fcombinations(12).size shouldBe tot(12)
        intSetOfTwelve.fcombinations(12).filter { it.size == 12 }.size shouldBe 1
    }

    test("fcount") {
        intSetOfNone.fcount { true } shouldBe 0
        intSetOfNone.fcount { false } shouldBe 0
        intSetOfThree.fcount { it == 2 } shouldBe 1
        intSetOfFour.fcount { it in 2..3 } shouldBe 2
        intSetOfTen.fcount { true } shouldBe 10
        intSetOfTen.fcount { false } shouldBe 0
    }

    test("fgroupBy").config(enabled = false) {
        fail("need FMap done to make this happen")
    }

    test("findexed") {

        intSetOfNone.findexed() shouldBe emptyIMSet()

        val ix4offset1: FSet<Pair<String, Int>> = strSetOfFour.findexed(1)
        ix4offset1.fmap { p -> p.second }.equals(intSetOfFour) shouldBe true
        ix4offset1.fmap { p -> p.first }.equals(strSetOfFour) shouldBe true

        val ix4offset0: FSet<Pair<String, Int>> = strSetOfFour.findexed(0)
        ix4offset0.fmap { p -> p.second+1 }.equals(intSetOfFour) shouldBe true
        ix4offset0.fmap { p -> p.first }.equals(strSetOfFour) shouldBe true

        val ix4offsetDefault: FSet<Pair<String, Int>> = strSetOfFour.findexed()
        ix4offsetDefault.fmap { p -> p.second+1 }.equals(intSetOfFour) shouldBe true
        ix4offsetDefault.fmap { p -> p.first }.equals(strSetOfFour) shouldBe true
    }

    test("fpartition") {
        intSetOfNone.fpartition {true} shouldBe Pair(emptyIMSet(), emptyIMSet())
        intSetOfNone.fpartition {false} shouldBe Pair(emptyIMSet(), emptyIMSet())

        val (pt1, pf1) = intSetOfFour.fpartition { it < 3 }
        pt1.equals(intSetOfTwo) shouldBe true
        pf1.equals(intSetOfTwo.fmap { it+2 }) shouldBe true

        val (pt2, pf2) = intSetOfFour.fpartition { it < 1 }
        pt2.equals(intSetOfNone) shouldBe true
        pf2.equals(intSetOfFour) shouldBe true
    }

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
        val fourByFour = intSetOfFour.fpermutations(4)
        fourByFour.size shouldBe 24 // 4! / (4-4)! == 4!
        intSetOfFour.fpermutations(5) shouldBe emptyIMSet()

        val fiveByOne = intSetOfFive.fpermutations(1)
        fiveByOne.size shouldBe 5 // 5! / (5-1)!
        val fiveByTwo = intSetOfFive.fpermutations(2)
        fiveByTwo.size shouldBe 20 // 5! / (5-2)!
        fiveByTwo.equals(oracleG) shouldBe true
        val fiveByThree = intSetOfFive.fpermutations(3)
        fiveByThree.size shouldBe 60 // 5! / (5-3)!
        val fiveByfour = intSetOfFive.fpermutations(4)
        fiveByfour.size shouldBe 120 // 5! / (5-4)!
        val fiveByfive = intSetOfFive.fpermutations(5)
        fiveByfive.size shouldBe 120 // 5! / (5-5)!
        intSetOfFive.fpermutations(6) shouldBe emptyIMSet()

        val sixByOne = intSetOfSix.fpermutations(1)
        sixByOne.size shouldBe 6 // 6! / (6-1)!
        val sixByTwo = intSetOfSix.fpermutations(2)
        sixByTwo.size shouldBe 30 // 6! / (6-2)!
        val sixByThree = intSetOfSix.fpermutations(3)
        sixByThree.size shouldBe 120 // 6! / (6-3)!
        val sixByFour = intSetOfSix.fpermutations(4)
        sixByFour.size shouldBe 360 // 6! / (6-4)!
        val sixByFive = intSetOfSix.fpermutations(5)
        sixByFive.size shouldBe 720 // 6! / (6-5)!
        val sixBySix = intSetOfSix.fpermutations(6)
        sixBySix.size shouldBe 720 // 6! / (6-6)! = 6!
        intSetOfSix.fpermutations(7) shouldBe emptyIMSet()

    }

    test("fpermute") {
        intSetOfNone.fpermute() shouldBe emptyIMSet()
        intSetOfOne.fpermute() shouldBe of(intSetOfOne)
        intSetOfTwo.fpermute() shouldBe of(FList.of(1,2),FList.of(2, 1))
        intSetOfThree.fpermute().size shouldBe 6 // 3!

        val fourp = intSetOfFour.fpermute()
        fourp.size shouldBe 24 // 3!
        val aryls4: ArrayList<TKVEntry<Int, Int>> = ArrayList(intSetOfFour.toIMBTree() as FRBTree<Int, Int>)
        val p4jt: FSet<FList<Int>> = JohnsonTrotter.jtPermutations(aryls4).fold(emptyIMSet()) { s, aryl ->
            s.fadd(FList.ofMap(aryl) { tkv -> tkv.getv() }.toSoO())
        }
        fourp.equals(p4jt) shouldBe true

        val fivep = intSetOfFive.fpermute()
        fivep.size shouldBe 120 // 5!
        val aryls5: ArrayList<TKVEntry<Int, Int>> = ArrayList(intSetOfFive.toIMBTree() as FRBTree<Int, Int>)
        val p5jt: FSet<FList<Int>> = JohnsonTrotter.jtPermutations(aryls5).fold(emptyIMSet()) { s, aryl ->
            s.fadd(FList.ofMap(aryl) { tkv -> tkv.getv() }.toSoO())
        }
        fivep.equals(p5jt) shouldBe true

        val sixpNow = System.currentTimeMillis()
        val sixp = intSetOfSix.fpermute()
        if (verbose) println("sixp in ${System.currentTimeMillis() - sixpNow}")
        sixp.size shouldBe 720 // 6!
        val aryls6: ArrayList<TKVEntry<Int, Int>> = ArrayList(intSetOfSix.toIMBTree() as FRBTree<Int, Int>)
        val p6jt: FSet<FList<Int>> = JohnsonTrotter.jtPermutations(aryls6).fold(emptyIMSet()) { s, aryl ->
            s.fadd(FList.ofMap(aryl) { tkv -> tkv.getv() }.toSoO())
        }
        sixp.equals(p6jt) shouldBe true

        val sevenpNow = System.currentTimeMillis()
        val sevenp = intSetOfSeven.fpermute()
        if (verbose) println("sevenp in ${System.currentTimeMillis() - sevenpNow}")
        sevenp.size shouldBe 5040 // 7!
        val aryls7: ArrayList<TKVEntry<Int, Int>> = ArrayList(intSetOfSeven.toIMBTree() as FRBTree<Int, Int>)
        val p7jt: FSet<FList<Int>> = JohnsonTrotter.jtPermutations(aryls7).fold(emptyIMSet()) { s, aryl ->
            s.fadd(FList.ofMap(aryl) { tkv -> tkv.getv() }.toSoO())
        }
        sevenp.equals(p7jt) shouldBe true

        if (longTest) {
            // ~1.5 s on machine Avogadro
            val eightpNow = System.currentTimeMillis()
            val eightp = intSetOfEight.fpermute()
            if (verbose) println("eightp in ${System.currentTimeMillis() - eightpNow}")
            eightp.size shouldBe 40320 // 8!

            // ~2 or 3 s on machine Avogadro
            val ninepNow = System.currentTimeMillis()
            val ninep = intSetOfNine.fpermute()
            if (verbose) println("ninep in ${System.currentTimeMillis() - ninepNow}")
            ninep.size shouldBe 362880 // 9!
        }
    }

    test("fpopAndReminder") {
        val (nilPop, nilReminder) = FSet.emptyIMSet<Int>().fpopAndReminder()
        nilPop shouldBe null
        nilReminder shouldBe FSet.emptyIMSet<Int>()

        val (onePop, oneReminder) = FSet.of(1).fpopAndReminder()
        onePop shouldBe 1
        oneReminder shouldBe  FSet.emptyIMSet<Int>()

        // this traverses slideShareTree popping one element at a time, and rebuilding the set with the popped element
        val res = FSetBody.of(frbSlideShareTree).ffold(Pair(FSet.emptyIMSet<Int>(), FSetBody.of(frbSlideShareTree).fpopAndReminder())) { acc, _ ->
            val (rebuild, popAndStub) = acc
            val (pop, stub) = popAndStub
            Pair(rebuild.fadd(pop!!.toSoO()), stub.fpopAndReminder())
        }
        res.first.equals(FSetBody.of(frbSlideShareTree)) shouldBe true
        val (lastPopped, lastReminder) = res.second
        lastPopped shouldBe null
        lastReminder shouldBe FSet.emptyIMSet<Int>()
    }

    test("fsize") {
        intSetOfNone.fsize() shouldBe 0
        intSetOfOne.fsize() shouldBe 1
        intSetOfTwo.fsize() shouldBe 2
        intSetOfThree.fsize() shouldBe 3
        intSetOfFour.fsize() shouldBe 4
        strSetOfFour.fsize() shouldBe 4
        intSetOfFive.fsize() shouldBe 5
        intSetOfSix.fsize() shouldBe 6
        intSetOfSeven.fsize() shouldBe 7
        intSetOfEight.fsize() shouldBe 8
        intSetOfNine.fsize() shouldBe 9
        intSetOfTen.fsize() shouldBe 10
        intSetOfEleven.fsize() shouldBe 11
        intSetOfTwelve.fsize() shouldBe 12
    }
})
