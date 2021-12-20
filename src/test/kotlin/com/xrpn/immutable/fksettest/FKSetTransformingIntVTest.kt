package com.xrpn.immutable.fksettest

import com.xrpn.imapi.IntKeyType
import com.xrpn.imapi.StrKeyType
import com.xrpn.immutable.FKSet
import com.xrpn.immutable.FKSet.Companion.asFKSet
import com.xrpn.immutable.FKSet.Companion.emptyIMKSet
import com.xrpn.immutable.FKSet.Companion.emptyIMKISet
import com.xrpn.immutable.FList
import com.xrpn.immutable.emptyArrayOfInt
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intKKSOfNone = FKSet.ofi(*emptyArrayOfInt)
private val intKKSOfOne = FKSet.ofi(1).nex<Int>()!!
private val intKKSOfTwo = FKSet.ofi(1, 2).nex<Int>()!!
private val intKKSOfTwoOfst1 = FKSet.ofi(2, 3).nex<Int>()!!
private val intKKSOfThree = FKSet.ofi(1, 2, 3).nex<Int>()!!
private val strKKSOfThree = FKSet.ofk("1", "2", "3").nex<Int>()!!
private val intKKSOfFour = FKSet.ofi(1, 2, 3, 4).nex<Int>()!!
private val intKKSOfFive = FKSet.ofi(1, 2, 3, 4, 5).nex<Int>()!!
private val strKKSOfFive = FKSet.ofk("1", "2", "3", "4", "5").nex<Int>()!!
private val intKKSOfTwonc = FKSet.ofk(1, 2)

private val intSSOfNone = FKSet.ofs(*emptyArrayOfInt)
private val intSSOfOne = FKSet.ofs(1) //.rne()!!
private val intSSOfTwo = FKSet.ofs(1, 2) //.rne()!!
private val intSSOfTwoOfst1 = FKSet.ofs(2, 3) //.rne()!!
private val intSSOfThree = FKSet.ofs(1, 2, 3) // .rne()!!
private val intSSOfFour = FKSet.ofs(1, 2, 3, 4) // .rne()!!
private val intSSOfFive = FKSet.ofs(1, 2, 3, 4, 5) // .rne()!!


class FKSetTransformingIntVTest : FunSpec({

    test("fflatMap") {
        asFKSet<Int,Int>(intKKSOfNone).fflatMap {FKSet.ofi(it)} shouldBe emptyIMKSet(IntKeyType)
        (asFKSet<Int,Int>(intKKSOfNone).fflatMap {FKSet.ofi(it)} === emptyIMKSet<Int,Int>(IntKeyType)) shouldBe true
        intKKSOfOne.fflatMap {FKSet.ofi(it)}.equals(intKKSOfOne) shouldBe true
        intKKSOfOne.fflatMapKK {FKSet.ofi(it)}.equals(intKKSOfOne) shouldBe true
        fun arrayBuilderConst(arg: Int) = Array(arg) { arg }
        intKKSOfTwo.fflatMap {FKSet.ofi(*arrayBuilderConst(it))}.equals(intKKSOfTwo) shouldBe true
        intKKSOfTwo.fflatMapKK {FKSet.ofi(*arrayBuilderConst(it))}.equals(intKKSOfTwo) shouldBe true
        fun arrayBuilderIncrement(arg: Int) = Array(arg) { i -> arg + i }
        intKKSOfTwo.fflatMap {FKSet.ofi(*arrayBuilderIncrement(it))}.equals(intKKSOfThree) shouldBe true
        intKKSOfTwo.fflatMapKK {FKSet.ofi(*arrayBuilderIncrement(it))}.equals(intKKSOfThree) shouldBe true

        intKKSOfTwonc.fflatMap {FKSet.ofi(*arrayBuilderIncrement(it))}.equals(intKKSOfThree) shouldBe true
        // intKKSOfTwonc.fflatMapKK {FKSet.ofi(*arrayBuilderIncrement(it))} // OK, does not compile
        intKKSOfThree.fflatMap {FKSet.ofi(*arrayBuilderIncrement(it))}.equals(intKKSOfFive) shouldBe true

        strKKSOfThree.fflatMap {FKSet.ofi(*arrayBuilderIncrement(it.toInt()))}.equals(intKKSOfFive) shouldBe true
        fun arrayBuilderIncrementS(arg: Int) = Array(arg) { i -> (arg + i).toString() }
        intKKSOfThree.fflatMapKK { FKSet.ofk(*arrayBuilderIncrementS(it)) }.equals(strKKSOfFive) shouldBe true

        asFKSet<String,Int>(intSSOfNone).fflatMap {FKSet.ofi(it)} shouldBe emptyIMKSet(StrKeyType)
        (asFKSet<String,Int>(intSSOfNone).fflatMap {FKSet.ofi(it)} === emptyIMKSet<String, Int>(StrKeyType)) shouldBe true
        intSSOfOne.fflatMap {FKSet.ofs(it)}.equals(intSSOfOne) shouldBe true
        intSSOfTwo.fflatMap {FKSet.ofs(*arrayBuilderConst(it))}.equals(intSSOfTwo) shouldBe true
        intSSOfTwo.fflatMap {FKSet.ofs(*arrayBuilderIncrement(it))}.equals(intSSOfThree) shouldBe true
        intSSOfThree.fflatMap {FKSet.ofs(*arrayBuilderIncrement(it))}.equals(intSSOfFive) shouldBe true
        intSSOfThree.fflatMap {FKSet.ofk(*arrayBuilderIncrementS(it)) }.equals(strKKSOfFive) shouldBe true
    }

    test("ffold") {
        intKKSOfNone.ffold(0) {acc, _ -> acc+1 } shouldBe 0
        intKKSOfOne.ffold(0) {acc, _ -> acc+1 } shouldBe 1
        intKKSOfTwo.ffold(0) {acc, _ -> acc+1 } shouldBe 2
        intKKSOfThree.ffold(0) {acc, _ -> acc+1 } shouldBe 3
        intKKSOfFour.ffold(0) {acc, el -> acc+el } shouldBe 10

        intSSOfNone.ffold(0) {acc, _ -> acc+1 } shouldBe 0
        intSSOfOne.ffold(0) {acc, _ -> acc+1 } shouldBe 1
        intSSOfTwo.ffold(0) {acc, _ -> acc+1 } shouldBe 2
        intSSOfThree.ffold(0) {acc, _ -> acc+1 } shouldBe 3
        intSSOfFour.ffold(0) {acc, el -> acc+el } shouldBe 10
    }

    test("fmap") {
        intKKSOfNone.fmap { it + 1 } shouldBe emptyIMKISet()
        (intKKSOfNone.fmap { it + 1 } === emptyIMKSet<Int,Int>(IntKeyType)) shouldBe true
        (intKKSOfNone.fmap { it + 1 } === emptyIMKSet<String,Int>(StrKeyType)) shouldBe false
        intKKSOfTwo.fmap { it + 1 }.equals(intKKSOfTwoOfst1) shouldBe true
        intKKSOfFive.fmap { it.toString() }.equals(strKKSOfFive) shouldBe true
        intKKSOfFive.fmapKK { it.toString() }.equals(strKKSOfFive) shouldBe true

        intSSOfNone.fmap { it + 1 } shouldBe emptyIMKSet(StrKeyType)
        (intSSOfNone.fmap { it + 1 } === emptyIMKSet<String,Int>(StrKeyType)) shouldBe true
        (intSSOfNone.fmap { it + 1 } === emptyIMKSet<Int,Int>(IntKeyType)) shouldBe false
        intSSOfTwo.fmap { it + 1 }.equals(intSSOfTwoOfst1) shouldBe true
        intSSOfFive.fmap { it.toString() }.equals(strKKSOfFive) shouldBe true
    }

    test("fmapToList") {
        intKKSOfNone.fmapToList { it + 1 } shouldBe FList.emptyIMList()
        intKKSOfThree.fmapToList { 4 } shouldBe FList.of(4, 4, 4)

        intSSOfNone.fmapToList { it + 1 } shouldBe FList.emptyIMList()
        intSSOfThree.fmapToList { 4 } shouldBe FList.of(4, 4, 4)
    }

    test("freduce") {
        intKKSOfNone.freduce {acc, _ -> acc+1 } shouldBe null
        intKKSOfOne.freduce {acc, _ -> acc+1 } shouldBe 1
        intKKSOfTwo.freduce {acc, _ -> acc+1 } shouldBe 2
        intKKSOfThree.freduce {acc, _ -> acc+1 } shouldBe 3
        intKKSOfFour.freduce {acc, el -> acc+el } shouldBe 10

        intSSOfNone.freduce {acc, _ -> acc+1 } shouldBe null
        intSSOfOne.freduce {acc, _ -> acc+1 } shouldBe 1
        intSSOfTwo.freduce {acc, _ -> acc+1 } shouldBe 2
        intSSOfThree.freduce {acc, _ -> acc+1 } shouldBe 3
        intSSOfFour.freduce {acc, el -> acc+el } shouldBe 10
    }
})
