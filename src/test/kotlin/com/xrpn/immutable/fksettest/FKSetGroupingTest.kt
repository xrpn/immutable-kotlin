package com.xrpn.immutable.fksettest

import com.xrpn.hash.JohnsonTrotter
import com.xrpn.hash.JohnsonTrotter.smallFact
import com.xrpn.imapi.*
import com.xrpn.imapi.IMOrdered.Companion.unorderedEqual
import com.xrpn.immutable.*
import com.xrpn.immutable.FKSet.Companion.emptyIMKSet
import com.xrpn.immutable.FKSet.Companion.ofi
import com.xrpn.immutable.FKSet.Companion.ofs
import com.xrpn.immutable.FKSet.Companion.toIMISet
import com.xrpn.immutable.FList.Companion.of
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fiset
import io.kotest.xrpn.fsset
import java.lang.IllegalStateException

private val intKKSetOfNone = FKSet.ofi(*emptyArrayOfInt)

private val intKKSetOfOne = FKSet.ofi(1).necvs<Int>()!!
private val strKKSetOfOne = FKSet.ofs("1").necvs<String>()!!
private val intSSetOfOne = FKSet.ofs(1).nevs()!!
private val strISetOfOne = FKSet.ofi("1").nevs()!!

private val intKKSetOfTwo = FKSet.ofi(1, 2).necvs<Int>()!!
private val strKKSetOfTwo = FKSet.ofs("1", "2").necvs<String>()!!
private val intSSetOfTwo = FKSet.ofs(1, 2).nevs()!!
private val strISetOfTwo = FKSet.ofi("1", "2").nevs()!!

private val intKKSetOfThree = FKSet.ofi(1, 2, 3).necvs<Int>()!!
private val strKKSetOfThree = FKSet.ofs("1", "2", "3").necvs<String>()!!
private val intSSetOfThree = FKSet.ofs(1, 2, 3).nevs()!!
private val strISetOfThree = FKSet.ofi("1", "2", "3").nevs()!!

private val intKKSetOfFour = FKSet.ofi(1, 2, 3, 4).necvs<Int>()!!
private val strKKSetOfFour = FKSet.ofs("1", "2", "3", "4").necvs<String>()!!
private val intSSetOfFour = FKSet.ofs(1, 2, 3, 4).nevs()!!
private val strISetOfFour = FKSet.ofi("1", "2", "3", "4").nevs()!!


private val strISetOfFourABCD = FKSet.ofi("a","b","c","d").nevs()!!
private val intKKSetOfFive = FKSet.ofi(1, 2, 3, 4, 5).necvs<Int>()!!
private val intKKSetOfSix = FKSet.ofi(1, 2, 3, 4, 5, 6).necvs<Int>()!!
private val intKKSetOfSeven = FKSet.ofi(1, 2, 3, 4, 5, 6, 7).necvs<Int>()!!
private val intKKSetOfEight = FKSet.ofi(1, 2, 3, 4, 5, 6, 7, 8).necvs<Int>()!!
private val intKKSetOfNine = FKSet.ofi(1, 2, 3, 4, 5, 6, 7, 8, 9).necvs<Int>()!!
private val intKKSetOfTen = FKSet.ofi(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).necvs<Int>()!!
private val intKKSetOfEleven = FKSet.ofi(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11).necvs<Int>()!!
private val intKKSetOfTwelve = FKSet.ofi(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12).necvs<Int>()!!
private val intKKSetOfThirteen = FKSet.ofi(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13).necvs<Int>()!!

private val oracleC: FKSet<Int, FKSet<Int, Int>> = ofi(ofi(1), ofi(2), ofi(3), ofi(4),
                         ofi(1, 2), ofi(1, 3), ofi(1, 4),
                         ofi(2, 3), ofi(2, 4),
                         ofi(3, 4))
private val oracleD: FKSet<Int, FKSet<Int, Int>> = ofi(ofi(1), ofi(2), ofi(3), ofi(4),
                         ofi(1, 2), ofi(1, 3), ofi(1, 4),
                         ofi(2, 3), ofi(2, 4),
                         ofi(3, 4),
                         ofi(1, 2, 3), ofi(1, 2, 4),
                         ofi(1, 3, 4),
                         ofi(2, 3, 4))
private val oracleE: FKSet<Int, FKSet<Int, Int>> = ofi(ofi(1), ofi(2), ofi(3), ofi(4),
                         ofi(1, 2), ofi(1, 3), ofi(1, 4),
                         ofi(2, 3), ofi(2, 4),
                         ofi(3, 4),
                         ofi(1, 2, 3), ofi(1, 2, 4),
                         ofi(1, 3, 4),
                         ofi(2, 3, 4),
                         ofi(1, 2, 3, 4))
private val oracleF: FKSet<Int, FList<Int>> = ofi(FList.of(1, 2), FList.of(1, 3), FList.of(1, 4),
                         FList.of(2, 1), FList.of(3, 1), FList.of(4, 1),
                         FList.of(2, 3), FList.of(2, 4),
                         FList.of(3, 2), FList.of(4, 2),
                         FList.of(3, 4),
                         FList.of(4, 3))
private val oracleG: FKSet<Int, FList<Int>> = ofi(FList.of(1, 2), FList.of(1, 3), FList.of(1, 4), FList.of(1, 5),
                         FList.of(2, 1), FList.of(3, 1), FList.of(4, 1), FList.of(5, 1),
                         FList.of(2, 3), FList.of(2, 4), FList.of(2, 5),
                         FList.of(3, 2), FList.of(4, 2), FList.of(5, 2),
                         FList.of(3, 4), FList.of(3, 5),
                         FList.of(4, 3), FList.of(5, 3),
                         FList.of(4, 5),
                         FList.of(5, 4))



class FKSetGroupingTest : FunSpec({

    val longTest = false
    val verbose = false
    val repeats = 50

    beforeTest {}

    test("fcartesian") {
        intKKSetOfNone.fcartesian(intKKSetOfNone).equals(intKKSetOfNone) shouldBe true
        intKKSetOfOne.fcartesian(intKKSetOfNone).equals(intKKSetOfNone) shouldBe true
        intSSetOfOne.fcartesian(intKKSetOfNone).equals(intKKSetOfNone) shouldBe true
        intKKSetOfNone.fcartesian(intKKSetOfOne).equals(intKKSetOfNone) shouldBe true

        val ora1i = ofs(Pair(1,1))
        val ora1s: FKSet<Int, Pair<String, String>> = ofi(Pair("1","1"))
        intKKSetOfOne.fcartesian(intKKSetOfOne).equal(ora1i) shouldBe true
        (intKKSetOfOne.fcartesian(intKKSetOfOne) == ora1s) shouldBe false
        intKKSetOfOne.fcartesian(intKKSetOfOne).equal(ora1i) shouldBe true
        intKKSetOfOne.fcartesian(intSSetOfOne).equal(ora1i) shouldBe true
        intSSetOfOne.fcartesian(intSSetOfOne).equal(ora1i) shouldBe true

        strKKSetOfOne.fcartesian(strKKSetOfOne).equal(ora1s) shouldBe true
        (intKKSetOfOne.fcartesian(intKKSetOfOne) == ora1i) shouldBe true
        strISetOfOne.fcartesian(strKKSetOfOne).equal(ora1s) shouldBe true
        strKKSetOfOne.fcartesian(strISetOfOne).equal(ora1s) shouldBe true
        strISetOfOne.fcartesian(strISetOfOne).equal(ora1s) shouldBe true

        intKKSetOfTwo.fcartesian(intKKSetOfOne).equals(ofi(Pair(1,1), Pair(2,1))) shouldBe true
        intKKSetOfOne.fcartesian(intKKSetOfTwo).equals(ofi(Pair(1,1), Pair(1,2))) shouldBe true

        intKKSetOfThree.fcartesian(intKKSetOfOne).equals(ofi(Pair(1,1),Pair(2,1),Pair(3,1))) shouldBe true
        intKKSetOfThree.fcartesian(intKKSetOfTwo).equals(ofi(Pair(1,1),Pair(2,1),Pair(3,1),Pair(1,2),Pair(2,2),Pair(3,2))) shouldBe true
        intKKSetOfOne.fcartesian(intKKSetOfThree).equals(ofi(Pair(1,1), Pair(1,2), Pair(1,3))) shouldBe true
        intKKSetOfTwo.fcartesian(intKKSetOfThree).equals(ofi(Pair(1,1), Pair(2,1), Pair(1,2), Pair(2,2), Pair(1,3), Pair(2,3))) shouldBe true
    }

    test("fcombinations coverage") {
//        intKKSetOfNone.fcombinations(-1).equals(intKKSetOfNone) shouldBe true
//        intKKSetOfNone.fcombinations(0).equals(intKKSetOfNone) shouldBe true
//        intKKSetOfNone.fcombinations(1).equals(intKKSetOfNone) shouldBe true
//        intKKSetOfOne.fcombinations(-1).equals(intKKSetOfNone) shouldBe true
//        intKKSetOfOne.fcombinations(0).equals(intKKSetOfNone) shouldBe true
//
//        (intKKSetOfOne.fcombinations(1) == ofi(intKKSetOfOne)) shouldBe true
//        (intKKSetOfOne.fcombinations(2) == ofi(intKKSetOfOne)) shouldBe true
//        (strISetOfOne.fcombinations(1) == ofi(strISetOfOne)) shouldBe true
//        (strISetOfOne.fcombinations(2) == ofi(strISetOfOne)) shouldBe true
//        (strISetOfOne.fcombinations(2) == ofs(strISetOfOne)) shouldBe true
//        intKKSetOfOne.fcombinations(1).equal(ofs(intKKSetOfOne)) shouldBe true
//        intKKSetOfOne.fcombinations(2).equal(ofs(intKKSetOfOne)) shouldBe true
//        strISetOfOne.fcombinations(1).equal(ofs(strISetOfOne)) shouldBe true
//        strISetOfOne.fcombinations(2).equal(ofs(strISetOfOne)) shouldBe true
//
//        (strKKSetOfOne.fcombinations(1) == ofs(strKKSetOfOne)) shouldBe true
//        (strKKSetOfOne.fcombinations(2) == ofs(strKKSetOfOne)) shouldBe true
//        (intSSetOfOne.fcombinations(1) == ofs(intSSetOfOne)) shouldBe true
//        (intSSetOfOne.fcombinations(2) == ofs(intSSetOfOne)) shouldBe true
//        (intSSetOfOne.fcombinations(2) == ofi(intSSetOfOne)) shouldBe true
//        strKKSetOfOne.fcombinations(1).equal(ofi(strKKSetOfOne)) shouldBe true
//        strKKSetOfOne.fcombinations(2).equal(ofi(strKKSetOfOne)) shouldBe true
//        intSSetOfOne.fcombinations(1).equal(ofi(intSSetOfOne)) shouldBe true
//        intSSetOfOne.fcombinations(2).equal(ofi(intSSetOfOne)) shouldBe true
//
//        val iorai2ii = ofi(ofi(1), ofi(2))
//        val iorai2si = ofi(ofs(1), ofi(2)) // this is a variance abomination
//        val ioras2ss = ofs(ofs(1), ofs(2))
//        val ioras2si = ofs(ofs(1), ofi(2))
//        val iorai3iii = ofi(ofi(1), ofi(2), ofi(1, 2))
//        val iorai3isi = ofi(ofi(1), ofs(2), ofi(1, 2))
//        val ioras3sss = ofs(ofs(1), ofs(2), ofs(1, 2))
//        val ioras3isi = ofi(ofi(1), ofs(2), ofi(1, 2))
//        val sorai2ii = ofi(ofi("1"), ofi("2"))
//        val sorai2si = ofi(ofs("1"), ofi("2"))
//        val soras2ss = ofs(ofs("1"), ofs("2"))
//        val soras2si = ofs(ofs("1"), ofi("2"))
//        val sorai3iii = ofi(ofi("1"), ofi("2"), ofi("1", "2"))
//        val sorai3isi = ofi(ofi("1"), ofs("2"), ofi("1", "2"))
//        val soras3sss = ofs(ofs("1"), ofs("2"), ofs("1", "2"))
//        val soras3isi = ofi(ofi("1"), ofs("2"), ofi("1", "2"))
//
//        intKKSetOfTwo.fcombinations(1).equals(iorai2ii) shouldBe true
//        intKKSetOfTwo.fcombinations(1).equals(iorai2si) shouldBe true
//        intKKSetOfTwo.fcombinations(1).equal(iorai2si) shouldBe true
//        intKKSetOfTwo.fcombinations(1).equal(ioras2si) shouldBe true
//        intKKSetOfTwo.fcombinations(2).equals(iorai3iii) shouldBe true
//        intKKSetOfTwo.fcombinations(2).equals(iorai3isi) shouldBe true
//        intKKSetOfTwo.fcombinations(2).equal(iorai3isi) shouldBe true
//        intKKSetOfTwo.fcombinations(3).equals(iorai3iii) shouldBe true
//
//        strISetOfTwo.fcombinations(1).equals(sorai2ii) shouldBe true
//        strISetOfTwo.fcombinations(1).equals(sorai2si) shouldBe true
//        strISetOfTwo.fcombinations(1).equal(sorai2si) shouldBe true
//        strISetOfTwo.fcombinations(1).equal(soras2si) shouldBe true
//        strISetOfTwo.fcombinations(2).equals(sorai3iii) shouldBe true
//        strISetOfTwo.fcombinations(2).equals(sorai3isi) shouldBe true
//        strISetOfTwo.fcombinations(2).equal(sorai3isi) shouldBe true
//        strISetOfTwo.fcombinations(3).equals(sorai3iii) shouldBe true
//
//        intSSetOfTwo.fcombinations(1).equals(ioras2ss) shouldBe true
//        intSSetOfTwo.fcombinations(1).equals(ioras2si) shouldBe true
//        intSSetOfTwo.fcombinations(1).equal(iorai2si) shouldBe true
//        intSSetOfTwo.fcombinations(1).equal(ioras2si) shouldBe true
//        intSSetOfTwo.fcombinations(2).equals(ioras3sss) shouldBe true
//        intSSetOfTwo.fcombinations(2).equals(ioras3isi) shouldBe true
//        intSSetOfTwo.fcombinations(2).equal(ioras3isi) shouldBe true
//        intSSetOfTwo.fcombinations(3).equals(ioras3sss) shouldBe true
//
//        strKKSetOfTwo.fcombinations(1).equals(soras2ss) shouldBe true
//        strKKSetOfTwo.fcombinations(1).equals(soras2si) shouldBe true
//        strKKSetOfTwo.fcombinations(1).equal(sorai2si) shouldBe true
//        strKKSetOfTwo.fcombinations(1).equal(soras2si) shouldBe true
//        strKKSetOfTwo.fcombinations(2).equals(soras3sss) shouldBe true
//        strKKSetOfTwo.fcombinations(2).equals(soras3isi) shouldBe true
//        strKKSetOfTwo.fcombinations(2).equal(soras3isi) shouldBe true
//        strKKSetOfTwo.fcombinations(3).equals(soras3sss) shouldBe true
    }

    test("fcombinations") {
        unorderedEqual(intKKSetOfThree.fcombinations(1),of(ofi(1), ofi(2), ofi(3))) shouldBe true
        val oracleA = of(ofi(1), ofi(2), ofi(3), ofi(1, 2), ofi(1, 3), ofi(3, 2))
        unorderedEqual(intKKSetOfThree.fcombinations(2),oracleA) shouldBe true
        val oracleB = of(ofi(1), ofi(2), ofi(3), ofi(1, 2), ofi(1, 3), ofi(3, 2), ofi(3, 1, 2))
        unorderedEqual(intKKSetOfThree.fcombinations(3), oracleB) shouldBe true
        unorderedEqual(intKKSetOfThree.fcombinations(4), oracleB) shouldBe true

        unorderedEqual(intKKSetOfFour.fcombinations(1),of(ofi(1), ofi(2), ofi(3), ofi(4))) shouldBe true
        unorderedEqual(intKKSetOfFour.fcombinations(2), oracleC.asSet()) shouldBe true
        unorderedEqual(intKKSetOfFour.fcombinations(3), oracleD.asSet()) shouldBe true
        unorderedEqual(intKKSetOfFour.fcombinations(4), oracleE.asSet()) shouldBe true
        unorderedEqual(intKKSetOfFour.fcombinations(5), oracleE.asSet()) shouldBe true

        intKKSetOfFive.fcombinations(1).fsize() shouldBe 5 // 5! / (5-1)! 1!
        // intKKSetOfFive.fcombinations(2).fsize() shouldBe 15
        intKKSetOfFive.fcombinations(2).ffilter { it.fsize() == 2 }.fsize() shouldBe 10 // 5! / (5-2)! 2!
        intKKSetOfFive.fcombinations(3).fsize() shouldBe 25
        intKKSetOfFive.fcombinations(3).ffilter { it.fsize() == 3 }.fsize() shouldBe 10 // 5! / (5-3)! 3!
        intKKSetOfFive.fcombinations(4).fsize() shouldBe 30
        intKKSetOfFive.fcombinations(4).ffilter { it.fsize() == 4 }.fsize() shouldBe 5 // 5! / (5-4)! 4!
        intKKSetOfFive.fcombinations(5).fsize() shouldBe 31
        intKKSetOfFive.fcombinations(5).ffilter { it.fsize() == 5 }.fsize() shouldBe 1 // 5! / (5-5)! 5!

        intKKSetOfSix.fcombinations(1).fsize() shouldBe 6 // 6! / (6-1)! 1!
        intKKSetOfSix.fcombinations(2).fsize() shouldBe 21
        intKKSetOfSix.fcombinations(2).ffilter { it.fsize() == 2 }.fsize() shouldBe 15 // 6! / (6-2)! 2!
        intKKSetOfSix.fcombinations(3).fsize() shouldBe 41
        intKKSetOfSix.fcombinations(3).ffilter { it.fsize() == 3 }.fsize() shouldBe 20 // 6! / (6-3)! 3!
        intKKSetOfSix.fcombinations(4).fsize() shouldBe 56
        intKKSetOfSix.fcombinations(4).ffilter { it.fsize() == 4 }.fsize() shouldBe 15 // 6! / (6-4)! 4!
        intKKSetOfSix.fcombinations(5).fsize() shouldBe 62
        intKKSetOfSix.fcombinations(5).ffilter { it.fsize() == 5 }.fsize() shouldBe 6 // 6! / (6-5)! 5!
        intKKSetOfSix.fcombinations(6).fsize() shouldBe 63
        intKKSetOfSix.fcombinations(6).ffilter { it.fsize() == 6 }.fsize() shouldBe 1 // 6! / (6-6)! 6!

        intKKSetOfSeven.fcombinations(1).fsize() shouldBe 7 // 7! / (7-1)! 1!
        intKKSetOfSeven.fcombinations(2).fsize() shouldBe 28
        intKKSetOfSeven.fcombinations(2).ffilter { it.fsize() == 2 }.fsize() shouldBe 21 // 7! / (7-2)! 2!
        intKKSetOfSeven.fcombinations(3).fsize() shouldBe 63
        intKKSetOfSeven.fcombinations(3).ffilter { it.fsize() == 3 }.fsize() shouldBe 35 // 7! / (7-3)! 3!
        intKKSetOfSeven.fcombinations(4).fsize() shouldBe 98
        intKKSetOfSeven.fcombinations(4).ffilter { it.fsize() == 4 }.fsize() shouldBe 35 // 7! / (7-4)! 4!
        intKKSetOfSeven.fcombinations(5).fsize() shouldBe 119
        intKKSetOfSeven.fcombinations(5).ffilter { it.fsize() == 5 }.fsize() shouldBe 21 // 7! / (7-5)! 5!
        intKKSetOfSeven.fcombinations(6).fsize() shouldBe 126
        intKKSetOfSeven.fcombinations(6).ffilter { it.fsize() == 6 }.fsize() shouldBe 7 // 7! / (7-6)! 6!
        intKKSetOfSeven.fcombinations(7).fsize() shouldBe 127
        intKKSetOfSeven.fcombinations(7).ffilter { it.fsize() == 7 }.fsize() shouldBe 1 // 7! / (7-7)! 7!

        fun tot(n: Int): Int {
            var acc = 0
            for (i in (1 ..n)) { acc += (smallFact(n) / (smallFact(n - i) * smallFact(i))) }
            return acc
        }

        intKKSetOfEight.fcombinations(8).fsize() shouldBe tot(8)
        intKKSetOfEight.fcombinations(8).ffilter { it.fsize() == 8 }.fsize() shouldBe 1
        intKKSetOfNine.fcombinations(9).fsize() shouldBe tot(9)
        intKKSetOfNine.fcombinations(9).ffilter { it.fsize() == 9 }.fsize() shouldBe 1
        intKKSetOfTen.fcombinations(10).fsize() shouldBe tot(10)
        intKKSetOfTen.fcombinations(10).ffilter { it.fsize() == 10 }.fsize() shouldBe 1
        intKKSetOfEleven.fcombinations(11).fsize() shouldBe tot(11)
        intKKSetOfEleven.fcombinations(11).ffilter { it.fsize() == 11 }.fsize() shouldBe 1
        intKKSetOfTwelve.fcombinations(12).fsize() shouldBe tot(12)
        intKKSetOfTwelve.fcombinations(12).ffilter { it.fsize() == 12 }.fsize() shouldBe 1
        intKKSetOfTwelve.fcombinations(13).fsize() shouldBe tot(12)

        shouldThrow<IllegalStateException> {
            intKKSetOfThirteen.fcombinations(12).fsize()
        }

    }

    test("fcount") {
        intKKSetOfNone.fcount { true } shouldBe 0
        intKKSetOfNone.fcount { false } shouldBe 0
        intKKSetOfThree.fcount { it == 2 } shouldBe 1
        intKKSetOfFour.fcount { it in 2..3 } shouldBe 2
        intKKSetOfTen.fcount { true } shouldBe 10
        intKKSetOfTen.fcount { false } shouldBe 0
    }

    test("fgroupBy").config(enabled = false) {
        fail("need FMap done to make this happen")
    }

    test("findexed") {

        intKKSetOfNone.findexed() shouldBe emptyIMKSet<Int, Int>(IntKeyType)

        val ix4offset1: IMSet<Pair<String, Int>> = strISetOfFourABCD.findexed(1)
        ix4offset1.fmap { p -> p.second }.equals(intKKSetOfFour) shouldBe true

        val ix4offset0 = strISetOfFourABCD.findexed(0).nevs()!!
        ix4offset0.fmap { p -> p.second+1 }.equals(intKKSetOfFour) shouldBe true
        ix4offset0.fmap { p -> p.first }.equals(strISetOfFourABCD) shouldBe true

        val ix4offsetDefault = strISetOfFourABCD.findexed().nevs()!!
        ix4offsetDefault.fmap { p -> p.second+1 }.equals(intKKSetOfFour) shouldBe true
        ix4offsetDefault.fmap { p -> p.first }.equals(strISetOfFourABCD) shouldBe true
    }

    test("fpartition") {
        intKKSetOfNone.fpartition {true} shouldBe Pair(emptyIMKSet<Int, Int>(IntKeyType), emptyIMKSet<Int, Int>(IntKeyType))
        intKKSetOfNone.fpartition {false} shouldBe Pair(emptyIMKSet<Int, Int>(IntKeyType), emptyIMKSet<Int, Int>(IntKeyType))

        val (pt1kki, pf1kki) = intKKSetOfFour.fpartition { it < 3 }
        pt1kki.equals(intKKSetOfTwo) shouldBe true
        pf1kki.equals(intKKSetOfTwo.fmap { it+2 }) shouldBe true

        val (pt2kki, pf2kki) = intKKSetOfFour.fpartition { it < 1 }
        pt2kki.equals(intKKSetOfNone) shouldBe true
        pf2kki.equals(intKKSetOfFour) shouldBe true

        val (pt1si, pf1si) = intSSetOfFour.fpartition { it < 3 }
        pt1si.equals(intSSetOfTwo) shouldBe true
        pf1si.equals(intSSetOfTwo.fmap { it+2 }) shouldBe true

        val (pt2si, pf2si) = intSSetOfFour.fpartition { it < 1 }
        pt2si.fempty() shouldBe true
        pf2si.equals(intSSetOfFour) shouldBe true

        val (pt1is, pf1is) = strISetOfFour.fpartition { it < "3" }
        pt1is.equals(strISetOfTwo) shouldBe true
        pf1is.equals(strISetOfTwo.fmap { (it[0].code+2).toChar().toString() }) shouldBe true

        val (pt2is, pf2is) = strISetOfFour.fpartition { it < "1" }
        pt2is.fempty() shouldBe true
        pf2is.equals(strISetOfFour) shouldBe true

        val (pt1kks, pf1kks) = strKKSetOfFour.fpartition { it < "3" }
        pt1kks.equals(strKKSetOfTwo) shouldBe true
        pf1kks.equals(strKKSetOfTwo.fmap { (it[0].code+2).toChar().toString() }) shouldBe true

        val (pt2kks, pf2kks) = strKKSetOfFour.fpartition { it < "1" }
        pt2kks.fempty() shouldBe true
        pf2kks.equals(strKKSetOfFour) shouldBe true
    }

    test("fpermutations coverage") {

        intKKSetOfNone.fpermutations(-1).equals(intKKSetOfNone) shouldBe true
        intKKSetOfNone.fpermutations(0).equals(intKKSetOfNone) shouldBe true
        intKKSetOfNone.fpermutations(1).equals(intKKSetOfNone) shouldBe true

        intKKSetOfThree.fpermutations(0) shouldBe emptyIMKSet<Int, Int>(IntKeyType)
        val threeByOneKK = intKKSetOfThree.fpermutations(1)
        threeByOneKK.size shouldBe 3 // 3! / (3-1)!
        val threeByTwoKKI = intKKSetOfThree.fpermutations(2)
        threeByTwoKKI.equals(ofi(FList.of(1,2),FList.of(1,3),FList.of(2,3),FList.of(2,1),FList.of(3,1),FList.of(3,2))) shouldBe true
        threeByTwoKKI.size shouldBe 6 // 3! / (3-2)!
        val threeByThreeKKI = intKKSetOfThree.fpermutations(3)
        threeByThreeKKI.equals(ofi(FList.of(1,2,3),FList.of(1,3,2),FList.of(2,3,1),FList.of(2,1,3),FList.of(3,1,2),FList.of(3,2,1))) shouldBe true
        threeByThreeKKI.size shouldBe 6 // 3! / (3-3)!
        intKKSetOfThree.fpermutations(4) shouldBe emptyIMKSet<Int, Int>(IntKeyType)

        intSSetOfThree.fpermutations(0) shouldBe emptyIMKSet<Int, Int>(IntKeyType)
        val threeByOneS = intSSetOfThree.fpermutations(1)
        threeByOneS.size shouldBe 3 // 3! / (3-1)!
        val threeByTwoS = intSSetOfThree.fpermutations(2)
        threeByTwoS.equals(ofi(FList.of(1,2),FList.of(1,3),FList.of(2,3),FList.of(2,1),FList.of(3,1),FList.of(3,2))) shouldBe true
        threeByTwoS.size shouldBe 6 // 3! / (3-2)!
        val threeByThreeS = intSSetOfThree.fpermutations(3)
        threeByThreeS.equals(ofi(FList.of(1,2,3),FList.of(1,3,2),FList.of(2,3,1),FList.of(2,1,3),FList.of(3,1,2),FList.of(3,2,1))) shouldBe true
        threeByThreeS.size shouldBe 6 // 3! / (3-3)!
        intSSetOfThree.fpermutations(4) shouldBe emptyIMKSet<Int, Int>(IntKeyType)

        strKKSetOfThree.fpermutations(0) shouldBe emptyIMKSet<Int, String>(IntKeyType)
        val threeByOneKKS = strKKSetOfThree.fpermutations(1)
        threeByOneKKS.size shouldBe 3 // 3! / (3-1)!
        val threeByTwoKKS = strKKSetOfThree.fpermutations(2)
        threeByTwoKKS.equals(ofi(FList.of("1","2"),FList.of("1","3"),FList.of("2","3"),FList.of("2","1"),FList.of("3","1"),FList.of("3","2"))) shouldBe true
        threeByTwoKKS.size shouldBe 6 // 3! / (3-2)!
        val threeByThreeKKS = strKKSetOfThree.fpermutations(3)
        threeByThreeKKS.equals(ofi(FList.of("1","2","3"),FList.of("1","3","2"),FList.of("2","3","1"),FList.of("2","1","3"),FList.of("3","1","2"),FList.of("3","2","1"))) shouldBe true
        threeByThreeKKS.size shouldBe 6 // 3! / (3-3)!
        strKKSetOfThree.fpermutations(4) shouldBe emptyIMKSet<Int, Int>(IntKeyType)

        strISetOfThree.fpermutations(0) shouldBe emptyIMKSet<Int, String>(IntKeyType)
        val threeByOneI = strISetOfThree.fpermutations(1)
        threeByOneI.size shouldBe 3 // 3! / (3-1)!
        val threeByTwoI = strISetOfThree.fpermutations(2)
        threeByTwoI.equals(ofi(FList.of("1","2"),FList.of("1","3"),FList.of("2","3"),FList.of("2","1"),FList.of("3","1"),FList.of("3","2"))) shouldBe true
        threeByTwoI.size shouldBe 6 // 3! / (3-2)!
        val threeByThreeI = strISetOfThree.fpermutations(3)
        threeByThreeI.equals(ofi(FList.of("1","2","3"),FList.of("1","3","2"),FList.of("2","3","1"),FList.of("2","1","3"),FList.of("3","1","2"),FList.of("3","2","1"))) shouldBe true
        threeByThreeI.size shouldBe 6 // 3! / (3-3)!
        strISetOfThree.fpermutations(4) shouldBe emptyIMKSet<Int, Int>(IntKeyType)
    }

    test("fpermutations") {

        val permutationsNow = System.currentTimeMillis()

        intKKSetOfFour.fpermutations(0) shouldBe emptyIMKSet<Int, Int>(IntKeyType)
        val fourByOne = intKKSetOfFour.fpermutations(1)
        fourByOne.size shouldBe 4 // 4! / (4-1)!
        val fourByTwo = intKKSetOfFour.fpermutations(2)
        fourByTwo.toIMISet()?.equals(oracleF) shouldBe true
        fourByTwo.size shouldBe 12 // 4! / (4-2)!
        val fourByThree = intKKSetOfFour.fpermutations(3)
        fourByThree.size shouldBe 24 // 4! / (4-3)!
        val fourByFour = intKKSetOfFour.fpermutations(4)
        fourByFour.size shouldBe 24 // 4! / (4-4)! == 4!
        intKKSetOfFour.fpermutations(5) shouldBe emptyIMKSet<Int, Int>(IntKeyType)

        val fiveByOne = intKKSetOfFive.fpermutations(1)
        fiveByOne.size shouldBe 5 // 5! / (5-1)!
        val fiveByTwo = intKKSetOfFive.fpermutations(2)
        fiveByTwo.size shouldBe 20 // 5! / (5-2)!
        fiveByTwo.toIMISet()?.equals(oracleG) shouldBe true
        val fiveByThree = intKKSetOfFive.fpermutations(3)
        fiveByThree.size shouldBe 60 // 5! / (5-3)!
        val fiveByfour = intKKSetOfFive.fpermutations(4)
        fiveByfour.size shouldBe 120 // 5! / (5-4)!
        val fiveByfive = intKKSetOfFive.fpermutations(5)
        fiveByfive.size shouldBe 120 // 5! / (5-5)!
        intKKSetOfFive.fpermutations(6) shouldBe emptyIMKSet<Int, Int>(IntKeyType)

        val sixByOne = intKKSetOfSix.fpermutations(1)
        sixByOne.size shouldBe 6 // 6! / (6-1)!
        val sixByTwo = intKKSetOfSix.fpermutations(2)
        sixByTwo.size shouldBe 30 // 6! / (6-2)!
        val sixByThree = intKKSetOfSix.fpermutations(3)
        sixByThree.size shouldBe 120 // 6! / (6-3)!
        val sixByFour = intKKSetOfSix.fpermutations(4)
        sixByFour.size shouldBe 360 // 6! / (6-4)!
        val sixByFive = intKKSetOfSix.fpermutations(5)
        sixByFive.size shouldBe 720 // 6! / (6-5)!
        val sixBySix = intKKSetOfSix.fpermutations(6)
        sixBySix.size shouldBe 720 // 6! / (6-6)! = 6!
        intKKSetOfSix.fpermutations(7) shouldBe emptyIMKSet<Int, Int>(IntKeyType)

        if (verbose) println("permutations in ${System.currentTimeMillis() - permutationsNow}")
    }

    test("fpermute") {
        intKKSetOfNone.fpermute() shouldBe emptyIMKSet<Int, Int>(IntKeyType)
        intKKSetOfOne.fpermute() shouldBe ofi(intKKSetOfOne)
        intKKSetOfTwo.fpermute() shouldBe ofi(FList.of(1,2),FList.of(2, 1))
        intKKSetOfThree.fpermute().size shouldBe 6 // 3!

        val fourp = intKKSetOfFour.fpermute()
        fourp.size shouldBe 24 // 3!
        val aryls4: ArrayList<TKVEntry<Int, Int>> = FT.fset2listary(intKKSetOfFour)!!
        val p4jt: FKSet<Int, FList<Int>> = JohnsonTrotter.jtPermutations(aryls4).fold(emptyIMKSet<Int, FList<Int>>(IntKeyType)) { s, aryl ->
            s.fOR(ofi(*arrayOf(FList.ofMap(aryl) { tkv -> tkv.getv() })))
        }
        fourp.toIMISet()?.equals(p4jt) shouldBe true

        val fivep = intKKSetOfFive.fpermute()
        fivep.size shouldBe 120 // 5!
        val aryls5: ArrayList<TKVEntry<Int, Int>> = FT.fset2listary(intKKSetOfFive)!!
        val p5jt: FKSet<Int, FList<Int>> = JohnsonTrotter.jtPermutations(aryls5).fold(emptyIMKSet<Int, FList<Int>>(IntKeyType)) { s, aryl ->
            s.fOR(ofi(*arrayOf(FList.ofMap(aryl) { tkv -> tkv.getv() })))
        }
        fivep.toIMISet()?.equals(p5jt) shouldBe true

        val sixpNow = System.currentTimeMillis()
        val sixp = intKKSetOfSix.fpermute()
        if (verbose) println("sixp in ${System.currentTimeMillis() - sixpNow}")
        sixp.size shouldBe 720 // 6!
        val aryls6: ArrayList<TKVEntry<Int, Int>> = FT.fset2listary(intKKSetOfSix)!!
        val p6jt: FKSet<Int, FList<Int>> = JohnsonTrotter.jtPermutations(aryls6).fold(emptyIMKSet<Int, FList<Int>>(IntKeyType)) { s, aryl ->
            s.fOR(ofi(*arrayOf(FList.ofMap(aryl) { tkv -> tkv.getv() })))
        }
        sixp.toIMISet()?.equals(p6jt) shouldBe true

        val sevenpNow = System.currentTimeMillis()
        val sevenp = intKKSetOfSeven.fpermute()
        if (verbose) println("sevenp in ${System.currentTimeMillis() - sevenpNow}")
        sevenp.size shouldBe 5040 // 7!
        val aryls7: ArrayList<TKVEntry<Int, Int>> = FT.fset2listary(intKKSetOfSeven)!!
        val p7jt: FKSet<Int, FList<Int>> =
            JohnsonTrotter.jtPermutations(aryls7).fold(emptyIMKSet<Int, FList<Int>>(IntKeyType)) { s, aryl ->
                s.fOR(ofi(*arrayOf(FList.ofMap(aryl) { tkv -> tkv.getv() })))
            }
        sevenp.toIMISet()?.equals(p7jt) shouldBe true

        if (longTest) {
            // ~1.5 s on machine Avogadro
            val eightpNow = System.currentTimeMillis()
            val eightp = intKKSetOfEight.fpermute()
            if (verbose) println("eightp in ${System.currentTimeMillis() - eightpNow}")
            eightp.size shouldBe 40320 // 8!

            // ~2 or 3 s on machine Avogadro
            val ninepNow = System.currentTimeMillis()
            val ninep = intKKSetOfNine.fpermute()
            if (verbose) println("ninep in ${System.currentTimeMillis() - ninepNow}")
            ninep.size shouldBe 362880 // 9!
        }
    }

    test("fpopAndRemainder simple") {
        val (nilPop, nilRemainder) = emptyIMKSet<Int, Int>(IntKeyType).fpopAndRemainder()
        nilPop shouldBe null
        nilRemainder shouldBe emptyIMKSet<Int, Int>(IntKeyType)

        val (onePop, oneRemainder) = ofi(1).fpopAndRemainder()
        onePop shouldBe 1
        oneRemainder shouldBe emptyIMKSet(IntKeyType)

    }

    test("fpopAndRemainder ik") {
        // this traverses slideShareTree popping one element at a time, and rebuilding the set with the popped element
        val resIk = ofFIKSBody(frbSlideShareTree).ffold(Pair(emptyIMKSet(IntKeyType), ofFIKSBody(frbSlideShareTree).fpopAndRemainder())) { acc: Pair<FKSet<Int, Int>, Pair<Int?, FKSet<Int, Int>>>, _ ->
            val (rebuild: IMKSet<Int, Int>, popAndStub: Pair<Int?, FKSet<Int, Int>>) = acc
            val (pop, shrink) = popAndStub
            val grow = rebuild.fOR(ofi(pop!!))
            Pair(grow, shrink.fpopAndRemainder())
        }
        resIk.first.equals(ofFIKSBody(frbSlideShareTree)) shouldBe true
        val (lastPoppedIk, lastRemainderIk) = resIk.second
        lastPoppedIk shouldBe null
        lastRemainderIk shouldBe emptyIMKSet(IntKeyType)
    }

    test("fpopAndRemainder sk") {
        val sstSk: FRBTree<String, Int> = frbSlideShareTree.fmap { tkv -> tkv.getv().toSAEntry() }
        // this traverses slideShareTree popping one element at a time, and rebuilding the set with the popped element
        val resSk = ofFSKSBody(sstSk).ffold(Pair(emptyIMKSet(StrKeyType), ofFSKSBody(sstSk).fpopAndRemainder())) { acc: Pair<FKSet<String, Int>, Pair<Int?, FKSet<String, Int>>>, _ ->
            val (rebuild: IMKSet<String, Int>, popAndStub: Pair<Int?, FKSet<String, Int>>) = acc
            val (pop, shrink) = popAndStub
            val grow = rebuild.fOR(ofs(pop!!))
            Pair(grow, shrink.fpopAndRemainder())
        }
        resSk.first.equals(ofFSKSBody(sstSk)) shouldBe true
        val (lastPoppedSk, lastRemainderSk) = resSk.second
        lastPoppedSk shouldBe null
        lastRemainderSk shouldBe FKSet.emptyIMKISet()
    }

    test("fpopAndRemainder properties") {
        checkAll(repeats, Arb.fiset(Arb.int(),20..100)) { fii: FKSet<Int, Int> ->
            if (!fii.fempty()) {
                @Suppress("UNCHECKED_CAST") (fii as IMKSetNotEmpty<Int, Int>)
                val body = fii.toIMBTree() as FRBTNode<Int,Int>
                val res = ofBody(body)!!.ffold(Pair(emptyIMKSet<Int, Int>(IntKeyType), ofBody(body)!!.fpopAndRemainder())) { acc: Pair<FKSet<Int, Int>, Pair<Int?, FKSet<Int, Int>>>, _ ->
                    val (rebuild: IMKSet<Int, Int>, popAndStub: Pair<Int?, FKSet<Int, Int>>) = acc
                    val (pop, shrink) = popAndStub
                    val grow = rebuild.fOR(ofi(pop!!))
                    Pair(grow, shrink.fpopAndRemainder())
                }
                res.first.equals(fii) shouldBe true
                val (lastPopped, lastRemainder) = res.second
                lastPopped shouldBe null
                lastRemainder shouldBe emptyIMKSet(IntKeyType)
            }
        }
        checkAll(repeats, Arb.fsset(Arb.int(),20..100)) { fsi: FKSet<String, Int> ->
            if (!fsi.fempty()) {
                @Suppress("UNCHECKED_CAST") (fsi as IMKSetNotEmpty<String, Int>)
                val body = fsi.toIMBTree() as FRBTree<String, Int>
                val res = ofBody(body)!!.ffold(Pair(emptyIMKSet<String, Int>(StrKeyType), ofBody(body)!!.fpopAndRemainder())) { acc: Pair<FKSet<String, Int>, Pair<Int?, FKSet<String, Int>>>, _ ->
                    val (rebuild: IMKSet<String, Int>, popAndStub: Pair<Int?, FKSet<String, Int>>) = acc
                    val (pop, shrink) = popAndStub
                    val grow: FKSet<String, Int> = rebuild.fOR(ofs(pop!!))
                    Pair(grow, shrink.fpopAndRemainder())
                }
                res.first.equals(fsi) shouldBe true
                val (lastPopped, lastRemainder) = res.second
                lastPopped shouldBe null
                lastRemainder shouldBe FKSet.emptyIMKISet()
            }
        }
    }

    test("fsize") {
        intKKSetOfNone.fsize() shouldBe 0
        intKKSetOfOne.fsize() shouldBe 1
        intKKSetOfTwo.fsize() shouldBe 2
        intKKSetOfThree.fsize() shouldBe 3
        intKKSetOfFour.fsize() shouldBe 4
        strISetOfFourABCD.fsize() shouldBe 4
        intKKSetOfFive.fsize() shouldBe 5
        intKKSetOfSix.fsize() shouldBe 6
        intKKSetOfSeven.fsize() shouldBe 7
        intKKSetOfEight.fsize() shouldBe 8
        intKKSetOfNine.fsize() shouldBe 9
        intKKSetOfTen.fsize() shouldBe 10
        intKKSetOfEleven.fsize() shouldBe 11
        intKKSetOfTwelve.fsize() shouldBe 12
    }
})
