package com.xrpn.immutable.fksettest

import com.xrpn.imapi.IntKeyType
import com.xrpn.imapi.SymKeyType
import com.xrpn.immutable.FKSet
import com.xrpn.immutable.FKSet.Companion.asFKSet
import com.xrpn.immutable.FKSet.Companion.emptyIMKSet
import com.xrpn.immutable.FList
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


private val longKKSOfNone = FKSet.ofk(*arrayOf<Long>())
private val longKKSOfOne = FKSet.ofk(1L).nex<Long>()!!
private val longKKSOfTwo = FKSet.ofk(1L, 2L).nex<Long>()!!
private val longKKSOfTwoOfst1 = FKSet.ofk(2L, 3L).nex<Long>()!!
private val longKKSOfThree = FKSet.ofk(1L, 2L, 3L).nex<Long>()!!
private val intKKSOfThree = FKSet.ofk(1, 2, 3).nex<Long>()!!
private val longKKSOfFour = FKSet.ofk(1L, 2L, 3L, 4L).nex<Long>()!!
private val longKKSOfFive = FKSet.ofk(1L, 2L, 3L, 4L, 5L).nex<Long>()!!
private val intKKSOfFive = FKSet.ofk(1, 2, 3, 4, 5).nex<Long>()!!
private val longKKSOfTwonc = FKSet.ofs(1L, 2L)

private val longISOfNone = FKSet.ofi(*arrayOf<Long>())
private val longISOfOne = FKSet.ofi(1L)
private val longISOfTwo = FKSet.ofi(1L, 2L)
private val longISOfTwoOfst1 = FKSet.ofi(2L, 3L)
private val longISOfThree = FKSet.ofi(1L, 2L, 3L)
private val longISOfFour = FKSet.ofi(1L, 2L, 3L, 4L)
private val longISOfFive = FKSet.ofi(1L, 2L, 3L, 4L, 5L)

private val longSSOfThree = FKSet.ofs(1L, 2L, 3L)

class FKSetTransformingLongKIntTest : FunSpec({

    test("fflatMap") {
        asFKSet<Long, Long>(longKKSOfNone).fflatMap {FKSet.ofs(it)} shouldBe emptyIMKSet(SymKeyType(Long::class))
        (asFKSet<Long, Long>(longKKSOfNone).fflatMap {FKSet.ofs(it)} === emptyIMKSet<Long,Long>(SymKeyType(Long::class))) shouldBe true
        longKKSOfOne.fflatMap {FKSet.ofi(it)}.equals(longISOfOne) shouldBe true
        longKKSOfOne.fflatMap {FKSet.ofs(it)}.equals(longKKSOfOne) shouldBe false
        longKKSOfOne.fflatMap {FKSet.ofk(it)}.equals(longKKSOfOne) shouldBe true
        longKKSOfOne.fflatMapKK {FKSet.ofi(it)}.equals(longISOfOne) shouldBe true
        longKKSOfOne.fflatMapKK {FKSet.ofs(it)}.equals(longKKSOfOne) shouldBe false
        longKKSOfOne.fflatMapKK {FKSet.ofk(it)}.equals(longKKSOfOne) shouldBe true

        fun arrayBuilderConst(arg: Long) = Array(arg.toInt()) { arg }
        fun arrayBuilderConstI(arg: Long) = Array(arg.toInt()) { arg.toInt() }
        longKKSOfTwo.fflatMap {FKSet.ofk(*arrayBuilderConst(it))}.equals(longKKSOfTwo) shouldBe true
        longKKSOfTwo.fflatMapKK {FKSet.ofk(*arrayBuilderConst(it))}.equals(longKKSOfTwo) shouldBe true

        fun arrayBuilderIncrement(arg: Long) = Array(arg.toInt()) { i -> arg + i.toLong() }
        fun arrayBuilderIncrementI(arg: Long) = Array(arg.toInt()) { i -> arg.toInt() + i }
        longKKSOfTwo.fflatMap {FKSet.ofk(*arrayBuilderIncrement(it))}.equals(longKKSOfThree) shouldBe true
        longKKSOfTwo.fflatMap {FKSet.ofi(*arrayBuilderIncrement(it))}.equals(longISOfThree) shouldBe true
        longKKSOfTwo.fflatMap {FKSet.ofk(*arrayBuilderIncrementI(it))}.equals(intKKSOfThree) shouldBe true
        longKKSOfTwo.fflatMap {FKSet.ofs(*arrayBuilderIncrement(it))}.equals(longSSOfThree) shouldBe true

        longKKSOfTwo.fflatMapKK {FKSet.ofk(*arrayBuilderIncrement(it))}.equals(longKKSOfThree) shouldBe true
        longKKSOfTwo.fflatMapKK {FKSet.ofi(*arrayBuilderIncrement(it))}.equals(longISOfThree) shouldBe true
        longKKSOfTwo.fflatMapKK {FKSet.ofk(*arrayBuilderIncrementI(it))}.equals(intKKSOfThree) shouldBe true
        longKKSOfTwo.fflatMapKK {FKSet.ofs(*arrayBuilderIncrement(it))}.equals(longSSOfThree) shouldBe true

        longKKSOfTwonc.fflatMap {FKSet.ofk(*arrayBuilderIncrement(it))}.equals(longKKSOfThree) shouldBe true
        // longKKSOfTwonc.fflatMapKK {FKSet.ofs(*arrayBuilderIncrement(it.toInt()))} // OK, must not compile
        longKKSOfThree.fflatMap {FKSet.ofk(*arrayBuilderIncrement(it))}.equals(longKKSOfFive) shouldBe true

        intKKSOfThree.fflatMap {FKSet.ofk(*arrayBuilderIncrement(it.toLong()))}.equals(longKKSOfFive) shouldBe true
        intKKSOfThree.fflatMap {FKSet.ofi(*arrayBuilderIncrement(it.toLong()))}.equals(longISOfFive) shouldBe true
        longKKSOfThree.fflatMapKK { FKSet.ofk(*arrayBuilderConstI(it)) }.equals(intKKSOfThree) shouldBe true
        longKKSOfThree.fflatMapKK { FKSet.ofk(*arrayBuilderIncrementI(it)) }.equals(intKKSOfFive) shouldBe true

        longKKSOfFive.fflatMap {FKSet.ofk(*arrayBuilderConst(it))}.equals(longKKSOfFive) shouldBe true
        longKKSOfFive.fflatMap {FKSet.ofi(*arrayBuilderConstI(it))}.equals(intKKSOfFive) shouldBe true
        longKKSOfFive.fflatMap {FKSet.ofk(*arrayBuilderConstI(it))}.equals(intKKSOfFive) shouldBe true

        asFKSet<Int, Long>(longISOfNone).fflatMap {FKSet.ofi(it.toInt())} shouldBe emptyIMKSet<Int,Long>(IntKeyType)
        (asFKSet<Int, Long>(longISOfNone).fflatMap {FKSet.ofi(it.toInt())} === emptyIMKSet<Int,Long>(IntKeyType)) shouldBe true
        longISOfOne.fflatMap {FKSet.ofi(it)}.equals(longISOfOne) shouldBe true
        longISOfTwo.fflatMap {FKSet.ofi(*arrayBuilderConst(it))}.equals(longISOfTwo) shouldBe true
        longISOfTwo.fflatMap {FKSet.ofi(*arrayBuilderIncrement(it))}.equals(longISOfThree) shouldBe true
        longISOfThree.fflatMap {FKSet.ofi(*arrayBuilderIncrement(it))}.equals(longISOfFive) shouldBe true

        intKKSOfThree.fflatMap {FKSet.ofi(*arrayBuilderIncrement(it.toLong()))}.equals(longISOfFive) shouldBe true
        longISOfThree.fflatMap {FKSet.ofk(*arrayBuilderIncrementI(it)) }.equals(intKKSOfFive) shouldBe true

        longISOfFive.fflatMap {FKSet.ofk(*arrayBuilderConst(it))}.equals(longKKSOfFive) shouldBe true
        longISOfFive.fflatMap {FKSet.ofi(*arrayBuilderConstI(it))}.equals(intKKSOfFive) shouldBe true
        longISOfFive.fflatMap {FKSet.ofk(*arrayBuilderConstI(it))}.equals(intKKSOfFive) shouldBe true
    }

    test("ffold") {
        longKKSOfNone.ffold(0) {acc, _ -> acc+1 } shouldBe 0
        longKKSOfOne.ffold(0) {acc, _ -> acc+1 } shouldBe 1
        longKKSOfTwo.ffold(0) {acc, _ -> acc+1 } shouldBe 2
        longKKSOfThree.ffold(0) {acc, _ -> acc+1 } shouldBe 3
        longKKSOfFour.ffold(0) {acc, el -> acc+el.toInt() } shouldBe 10

        longISOfNone.ffold(0) {acc, _ -> acc+1 } shouldBe 0
        longISOfOne.ffold(0) {acc, _ -> acc+1 } shouldBe 1
        longISOfTwo.ffold(0) {acc, _ -> acc+1 } shouldBe 2
        longISOfThree.ffold(0) {acc, _ -> acc+1 } shouldBe 3
        longISOfFour.ffold(0) {acc, el -> acc+el.toInt() } shouldBe 10
    }

    test("fmap") {
        longKKSOfNone.fmap { it + 1L } shouldBe emptyIMKSet(SymKeyType(Long::class))
        (longKKSOfNone.fmap { it + 1L } === emptyIMKSet<Long,Long>(SymKeyType(Long::class))) shouldBe true
        longKKSOfTwo.fmap { it + 1L }.equals(longKKSOfTwoOfst1) shouldBe true
        longKKSOfFive.fmap { it.toInt() }.equals(intKKSOfFive) shouldBe true
        longKKSOfFive.fmapKK { it.toInt() }.equals(intKKSOfFive) shouldBe true

        longISOfNone.fmap { it + 1L } shouldBe emptyIMKSet<Int,Long>(IntKeyType)
        (longISOfNone.fmap { it + 1L } === emptyIMKSet<Int,Long>(IntKeyType)) shouldBe true
        longISOfTwo.fmap { it + 1L }.equals(longISOfTwoOfst1) shouldBe true
        longISOfFive.fmap { it.toInt() }.equals(intKKSOfFive) shouldBe true
    }

    test("fmapToList") {
        longKKSOfNone.fmapToList { it + 1 } shouldBe FList.emptyIMList()
        longKKSOfThree.fmapToList { 4 } shouldBe FList.of(4, 4, 4)

        longISOfNone.fmapToList { it + 1 } shouldBe FList.emptyIMList()
        longISOfThree.fmapToList { 4 } shouldBe FList.of(4, 4, 4)
    }

    test("freduce") {
        longKKSOfNone.freduce { acc, v -> acc+v } shouldBe null
        longKKSOfOne.freduce { acc, v -> acc+v } shouldBe 1
        longKKSOfTwo.freduce {acc, v -> acc+v } shouldBe 3
        longKKSOfThree.freduce {acc, v -> acc+v } shouldBe 6
        longKKSOfFour.freduce {acc, v -> acc+v } shouldBe 10

        longISOfNone.freduce { acc, v -> acc+v } shouldBe null
        longISOfOne.freduce { acc, v -> acc+v } shouldBe 1
        longISOfTwo.freduce {acc, v -> acc+v } shouldBe 3
        longISOfThree.freduce {acc, v -> acc+v } shouldBe 6
        longISOfFour.freduce {acc, v -> acc+v } shouldBe 10
    }
})
