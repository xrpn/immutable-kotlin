package com.xrpn.immutable.fksettest

import com.xrpn.immutable.FKSet
import com.xrpn.immutable.FKSet.Companion.asFKSet
import com.xrpn.immutable.FKSet.Companion.emptyIMRSet
import com.xrpn.immutable.FList
import com.xrpn.immutable.emptyArrayOfStr
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


private val strKKSOfNone = FKSet.ofs(*emptyArrayOfStr)
private val strKKSOfOne = FKSet.ofs("1").rrne()!!
private val strKKSOfTwo = FKSet.ofs("1", "2").rrne()!!
private val strKKSOfTwoOfst1 = FKSet.ofs("2", "3").rrne()!!
private val strKKSOfThree = FKSet.ofs("1", "2", "3").rrne()!!
private val intKKSOfThree = FKSet.ofk(1, 2, 3).rrne()!!
private val strKKSOfFour = FKSet.ofs("1", "2", "3", "4").rrne()!!
private val strKKSOfFive = FKSet.ofs("1", "2", "3", "4", "5").rrne()!!
private val intKKSOfFive = FKSet.ofk(1, 2, 3, 4, 5).rrne()!!
private val strKKSOfTwonc = FKSet.ofs("1", "2")

private val strISOfNone = FKSet.ofi(*emptyArrayOfStr)
private val strISOfOne = FKSet.ofi("1")
private val strISOfTwo = FKSet.ofi("1", "2")
private val strISOfTwoOfst1 = FKSet.ofi("2", "3")
private val strISOfThree = FKSet.ofi("1", "2", "3")
private val strISOfFour = FKSet.ofi("1", "2", "3", "4")
private val strISOfFive = FKSet.ofi("1", "2", "3", "4", "5")


class FKSetTransformingStrVTest : FunSpec({

    test("fflatMap") {
        asFKSet<String,String>(strKKSOfNone).fflatMap {FKSet.ofs(it)} shouldBe emptyIMRSet()
        strKKSOfOne.fflatMap {FKSet.ofs(it)}.equals(strKKSOfOne) shouldBe true
        strKKSOfOne.fflatMapKK {FKSet.ofs(it)}.equals(strKKSOfOne) shouldBe true
        fun arrayBuilderConst(arg: Int) = Array(arg) { arg.toString() }
        strKKSOfTwo.fflatMap {FKSet.ofs(*arrayBuilderConst(it.toInt()))}.equals(strKKSOfTwo) shouldBe true
        strKKSOfTwo.fflatMapKK {FKSet.ofs(*arrayBuilderConst(it.toInt()))}.equals(strKKSOfTwo) shouldBe true
        fun arrayBuilderIncrement(arg: Int) = Array(arg) { i -> (arg + i).toString() }
        strKKSOfTwo.fflatMap {FKSet.ofs(*arrayBuilderIncrement(it.toInt()))}.equals(strKKSOfThree) shouldBe true
        strKKSOfTwo.fflatMapKK {FKSet.ofs(*arrayBuilderIncrement(it.toInt()))}.equals(strKKSOfThree) shouldBe true

        strKKSOfTwonc.fflatMap {FKSet.ofs(*arrayBuilderIncrement(it.toInt()))}.equals(strKKSOfThree) shouldBe true
        // strKKSOfTwonc.fflatMapKK {FKSet.ofs(*arrayBuilderIncrement(it.toInt()))} // OK, does not compile
        strKKSOfThree.fflatMap {FKSet.ofs(*arrayBuilderIncrement(it.toInt()))}.equals(strKKSOfFive) shouldBe true

        intKKSOfThree.fflatMap {FKSet.ofs(*arrayBuilderIncrement(it))}.equals(strKKSOfFive) shouldBe true
        fun arrayBuilderIncrementI(arg: Int) = Array(arg) { i -> arg + i }
        strKKSOfThree.fflatMapKK { FKSet.ofk(*arrayBuilderIncrementI(it.toInt())) }.equals(intKKSOfFive) shouldBe true

        asFKSet<Int, String>(strISOfNone).fflatMap {FKSet.ofi(it.toInt())} shouldBe emptyIMRSet()
        strISOfOne.fflatMap {FKSet.ofi(it)}.equals(strISOfOne) shouldBe true
        strISOfTwo.fflatMap {FKSet.ofi(*arrayBuilderConst(it.toInt()))}.equals(strISOfTwo) shouldBe true
        strISOfTwo.fflatMap {FKSet.ofi(*arrayBuilderIncrement(it.toInt()))}.equals(strISOfThree) shouldBe true
        strISOfThree.fflatMap {FKSet.ofi(*arrayBuilderIncrement(it.toInt()))}.equals(strISOfFive) shouldBe true
        strISOfThree.fflatMap {FKSet.ofk(*arrayBuilderIncrementI(it.toInt())) }.equals(intKKSOfFive) shouldBe true
    }

    test("ffold") {
        strKKSOfNone.ffold(0) {acc, _ -> acc+1 } shouldBe 0
        strKKSOfOne.ffold(0) {acc, _ -> acc+1 } shouldBe 1
        strKKSOfTwo.ffold(0) {acc, _ -> acc+1 } shouldBe 2
        strKKSOfThree.ffold(0) {acc, _ -> acc+1 } shouldBe 3
        strKKSOfFour.ffold(0) {acc, el -> acc+el.toInt() } shouldBe 10

        strISOfNone.ffold(0) {acc, _ -> acc+1 } shouldBe 0
        strISOfOne.ffold(0) {acc, _ -> acc+1 } shouldBe 1
        strISOfTwo.ffold(0) {acc, _ -> acc+1 } shouldBe 2
        strISOfThree.ffold(0) {acc, _ -> acc+1 } shouldBe 3
        strISOfFour.ffold(0) {acc, el -> acc+el.toInt() } shouldBe 10
    }

    test("fmap") {
        strKKSOfNone.fmap { it + 1 } shouldBe emptyIMRSet()
        strKKSOfTwo.fmap { (it[0].code + 1).toChar().toString() }.equals(strKKSOfTwoOfst1) shouldBe true
        strKKSOfFive.fmap { it.toInt() }.equals(intKKSOfFive) shouldBe true
        strKKSOfFive.fmapKK { it.toInt() }.equals(intKKSOfFive) shouldBe true

        strISOfNone.fmap { it + 1 } shouldBe emptyIMRSet()
        strISOfTwo.fmap { (it[0].code + 1).toChar().toString() }.equals(strISOfTwoOfst1) shouldBe true
        strISOfFive.fmap { it.toInt() }.equals(intKKSOfFive) shouldBe true
    }

    test("fmapToList") {
        strKKSOfNone.fmapToList { it + 1 } shouldBe FList.emptyIMList()
        strKKSOfThree.fmapToList { 4 } shouldBe FList.of(4, 4, 4)

        strISOfNone.fmapToList { it + 1 } shouldBe FList.emptyIMList()
        strISOfThree.fmapToList { 4 } shouldBe FList.of(4, 4, 4)
    }

    test("freduce") {
        strKKSOfNone.freduce { acc, v -> acc+v } shouldBe null
        strKKSOfOne.freduce { acc, v -> acc+v } shouldBe "1"
        strKKSOfTwo.freduce {acc, v -> acc+v } shouldBe "12"
        strKKSOfThree.freduce {acc, v -> acc+v } shouldBe "132"
        strKKSOfFour.freduce {acc, v -> acc+v } shouldBe "1324"

        strISOfNone.freduce { acc, v -> acc+v } shouldBe null
        strISOfOne.freduce { acc, v -> acc+v } shouldBe "1"
        strISOfTwo.freduce {acc, v -> acc+v } shouldBe "12"
        strISOfThree.freduce {acc, v -> acc+v } shouldBe "132"
        strISOfFour.freduce {acc, v -> acc+v } shouldBe "1324"
    }
})
