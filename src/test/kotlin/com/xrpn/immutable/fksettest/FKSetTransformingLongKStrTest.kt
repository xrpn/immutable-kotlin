package com.xrpn.immutable.fksettest

import com.xrpn.imapi.IntKeyType
import com.xrpn.imapi.StrKeyType
import com.xrpn.imapi.SymKeyType
import com.xrpn.immutable.FKSet
import com.xrpn.immutable.FKSet.Companion.asFKSet
import com.xrpn.immutable.FKSet.Companion.emptyIMKSet
import com.xrpn.immutable.FList
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


private val longKKSOfNone = FKSet.ofk(*arrayOf<Long>())
private val longKKSOfOne = FKSet.ofk(1L).necvs<Long>()!!
private val longKKSOfTwo = FKSet.ofk(1L, 2L).necvs<Long>()!!
private val longKKSOfTwoOfst1 = FKSet.ofk(2L, 3L).necvs<Long>()!!
private val longKKSOfThree = FKSet.ofk(1L, 2L, 3L).necvs<Long>()!!
private val strKKSOfThree = FKSet.ofk("1", "2", "3").necvs<Long>()!!
private val longKKSOfFour = FKSet.ofk(1L, 2L, 3L, 4L).necvs<Long>()!!
private val longKKSOfFive = FKSet.ofk(1L, 2L, 3L, 4L, 5L).necvs<Long>()!!
private val strKKSOfFive = FKSet.ofk("1", "2", "3", "4", "5").necvs<Long>()!!
private val longKKSOfTwonc = FKSet.ofs(1L, 2L)

private val longSSOfNone = FKSet.ofs(*arrayOf<Long>())
private val longSSOfOne = FKSet.ofs(1L)
private val longSSOfTwo = FKSet.ofs(1L, 2L)
private val longSSOfTwoOfst1 = FKSet.ofs(2L, 3L)
private val longSSOfThree = FKSet.ofs(1L, 2L, 3L)

private val longISOfThree = FKSet.ofi(1L, 2L, 3L)

private val longSSOfFour = FKSet.ofs(1L, 2L, 3L, 4L)
private val longSSOfFive = FKSet.ofs(1L, 2L, 3L, 4L, 5L)

class FKSetTransformingLongKStrTest : FunSpec({

    test("fflatMap") {
        asFKSet<Long, Long>(longKKSOfNone).fflatMap {FKSet.ofs(it)} shouldBe emptyIMKSet<Long,Long>(SymKeyType(Long::class))
        (asFKSet<Long, Long>(longKKSOfNone).fflatMap {FKSet.ofs(it)} === emptyIMKSet<Long,Long>(SymKeyType(Long::class))) shouldBe true
        longKKSOfOne.fflatMap {FKSet.ofs(it)}.equals(longSSOfOne) shouldBe true
        longKKSOfOne.fflatMap {FKSet.ofi(it)}.equals(longKKSOfOne) shouldBe false
        longKKSOfOne.fflatMap {FKSet.ofk(it)}.equals(longKKSOfOne) shouldBe true
        longKKSOfOne.fflatMapKK {FKSet.ofs(it)}.equals(longSSOfOne) shouldBe true
        longKKSOfOne.fflatMapKK {FKSet.ofi(it)}.equals(longKKSOfOne) shouldBe false
        longKKSOfOne.fflatMapKK {FKSet.ofk(it)}.equals(longKKSOfOne) shouldBe true

        fun arrayBuilderConst(arg: Long) = Array(arg.toInt()) { arg }
        fun arrayBuilderConstS(arg: Long) = Array(arg.toInt()) { arg.toString() }
        longKKSOfTwo.fflatMap {FKSet.ofk(*arrayBuilderConst(it))}.equals(longKKSOfTwo) shouldBe true
        longKKSOfTwo.fflatMapKK {FKSet.ofk(*arrayBuilderConst(it))}.equals(longKKSOfTwo) shouldBe true

        fun arrayBuilderIncrement(arg: Long) = Array(arg.toInt()) { i -> arg + i.toLong() }
        fun arrayBuilderIncrementS(arg: Long) = Array(arg.toInt()) { i -> (arg.toString()[0].code + i).toChar().toString() }
        longKKSOfTwo.fflatMap {FKSet.ofk(*arrayBuilderIncrement(it))}.equals(longKKSOfThree) shouldBe true
        longKKSOfTwo.fflatMap {FKSet.ofs(*arrayBuilderIncrement(it))}.equals(longSSOfThree) shouldBe true
        longKKSOfTwo.fflatMap {FKSet.ofk(*arrayBuilderIncrementS(it))}.equals(strKKSOfThree) shouldBe true
        longKKSOfTwo.fflatMap {FKSet.ofi(*arrayBuilderIncrement(it))}.equals(longISOfThree) shouldBe true

        longKKSOfTwo.fflatMapKK {FKSet.ofk(*arrayBuilderIncrement(it))}.equals(longKKSOfThree) shouldBe true
        longKKSOfTwo.fflatMapKK {FKSet.ofs(*arrayBuilderIncrement(it))}.equals(longSSOfThree) shouldBe true
        longKKSOfTwo.fflatMapKK {FKSet.ofk(*arrayBuilderIncrementS(it))}.equals(strKKSOfThree) shouldBe true
        longKKSOfTwo.fflatMapKK {FKSet.ofi(*arrayBuilderIncrement(it))}.equals(longISOfThree) shouldBe true

        longKKSOfTwonc.fflatMap {FKSet.ofk(*arrayBuilderIncrement(it))}.equals(longKKSOfThree) shouldBe true
        // longKKSOfTwonc.fflatMapKK {FKSet.ofs(*arrayBuilderIncrement(it.toInt()))} // OK, must not compile
        longKKSOfThree.fflatMap {FKSet.ofk(*arrayBuilderIncrement(it))}.equals(longKKSOfFive) shouldBe true

        strKKSOfThree.fflatMap {FKSet.ofk(*arrayBuilderIncrement(it.toLong()))}.equals(longKKSOfFive) shouldBe true
        strKKSOfThree.fflatMap {FKSet.ofs(*arrayBuilderIncrement(it.toLong()))}.equals(longSSOfFive) shouldBe true
        longKKSOfThree.fflatMapKK { FKSet.ofk(*arrayBuilderConstS(it)) }.equals(strKKSOfThree) shouldBe true
        longKKSOfThree.fflatMapKK { FKSet.ofk(*arrayBuilderIncrementS(it)) }.equals(strKKSOfFive) shouldBe true

        longKKSOfFive.fflatMap {FKSet.ofk(*arrayBuilderConst(it))}.equals(longKKSOfFive) shouldBe true
        longKKSOfFive.fflatMap {FKSet.ofs(*arrayBuilderConstS(it))}.equals(strKKSOfFive) shouldBe true
        longKKSOfFive.fflatMap {FKSet.ofk(*arrayBuilderConstS(it))}.equals(strKKSOfFive) shouldBe true

        asFKSet<Int, Long>(longSSOfNone).fflatMap {FKSet.ofs(it.toInt())} shouldBe emptyIMKSet<Int,Long>(IntKeyType)
        (asFKSet<Int, Long>(longSSOfNone).fflatMap {FKSet.ofs(it.toInt())} === emptyIMKSet<Int,Long>(IntKeyType)) shouldBe true
        longSSOfOne.fflatMap {FKSet.ofs(it)}.equals(longSSOfOne) shouldBe true
        longSSOfTwo.fflatMap {FKSet.ofs(*arrayBuilderConst(it))}.equals(longSSOfTwo) shouldBe true
        longSSOfTwo.fflatMap {FKSet.ofs(*arrayBuilderIncrement(it))}.equals(longSSOfThree) shouldBe true
        longSSOfThree.fflatMap {FKSet.ofs(*arrayBuilderIncrement(it))}.equals(longSSOfFive) shouldBe true

        strKKSOfThree.fflatMap {FKSet.ofs(*arrayBuilderIncrement(it.toLong()))}.equals(longSSOfFive) shouldBe true
        longSSOfThree.fflatMap {FKSet.ofk(*arrayBuilderIncrementS(it)) }.equals(strKKSOfFive) shouldBe true

        longSSOfFive.fflatMap {FKSet.ofk(*arrayBuilderConst(it))}.equals(longKKSOfFive) shouldBe true
        longSSOfFive.fflatMap {FKSet.ofs(*arrayBuilderConstS(it))}.equals(strKKSOfFive) shouldBe true
        longSSOfFive.fflatMap {FKSet.ofk(*arrayBuilderConstS(it))}.equals(strKKSOfFive) shouldBe true
    }

    test("ffold") {
        longKKSOfNone.ffold(0) {acc, _ -> acc+1 } shouldBe 0
        longKKSOfOne.ffold(0) {acc, _ -> acc+1 } shouldBe 1
        longKKSOfTwo.ffold(0) {acc, _ -> acc+1 } shouldBe 2
        longKKSOfThree.ffold(0) {acc, _ -> acc+1 } shouldBe 3
        longKKSOfFour.ffold(0) {acc, el -> acc+el.toInt() } shouldBe 10

        longSSOfNone.ffold(0) {acc, _ -> acc+1 } shouldBe 0
        longSSOfOne.ffold(0) {acc, _ -> acc+1 } shouldBe 1
        longSSOfTwo.ffold(0) {acc, _ -> acc+1 } shouldBe 2
        longSSOfThree.ffold(0) {acc, _ -> acc+1 } shouldBe 3
        longSSOfFour.ffold(0) {acc, el -> acc+el.toInt() } shouldBe 10
    }

    test("fmap") {
        longKKSOfNone.fmap { it + 1L } shouldBe emptyIMKSet(SymKeyType(Long::class))
        (longKKSOfNone.fmap { it + 1L } === emptyIMKSet<Long,Long>(SymKeyType(Long::class))) shouldBe true
        longKKSOfTwo.fmap { it + 1L }.equals(longKKSOfTwoOfst1) shouldBe true
        longKKSOfFive.fmap { it.toString() }.equals(strKKSOfFive) shouldBe true
        longKKSOfFive.fmapKK { it.toString() }.equals(strKKSOfFive) shouldBe true

        longSSOfNone.fmap { it + 1L } shouldBe emptyIMKSet<String,Long>(StrKeyType)
        (longSSOfNone.fmap { it + 1L } === emptyIMKSet<String,Long>(StrKeyType)) shouldBe true
        longSSOfTwo.fmap { it + 1L }.equals(longSSOfTwoOfst1) shouldBe true
        longSSOfFive.fmap { it.toString() }.equals(strKKSOfFive) shouldBe true
    }

    test("fmapToList") {
        longKKSOfNone.fmapToList { it + 1 } shouldBe FList.emptyIMList()
        longKKSOfThree.fmapToList { 4 } shouldBe FList.of(4, 4, 4)

        longSSOfNone.fmapToList { it + 1 } shouldBe FList.emptyIMList()
        longSSOfThree.fmapToList { 4 } shouldBe FList.of(4, 4, 4)
    }

    test("freduce") {
        longKKSOfNone.freduce { acc, v -> acc+v } shouldBe null
        longKKSOfOne.freduce { acc, v -> acc+v } shouldBe 1
        longKKSOfTwo.freduce {acc, v -> acc+v } shouldBe 3
        longKKSOfThree.freduce {acc, v -> acc+v } shouldBe 6
        longKKSOfFour.freduce {acc, v -> acc+v } shouldBe 10

        longSSOfNone.freduce { acc, v -> acc+v } shouldBe null
        longSSOfOne.freduce { acc, v -> acc+v } shouldBe 1
        longSSOfTwo.freduce {acc, v -> acc+v } shouldBe 3
        longSSOfThree.freduce {acc, v -> acc+v } shouldBe 6
        longSSOfFour.freduce {acc, v -> acc+v } shouldBe 10
    }
})
