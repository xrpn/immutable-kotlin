package com.xrpn.immutable.flisttest

import com.xrpn.imapi.IMCommon
import com.xrpn.imapi.IMList
import com.xrpn.immutable.*
import com.xrpn.immutable.FKSet.Companion.emptyIMKISet
import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.emptyArrayOfStr
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intListOfNone: IMCommon<Int> = FList.of(*emptyArrayOfInt)
private val intListOfOne: IMCommon<Int> = FList.of(*arrayOf<Int>(1))
private val intListOfTwo: IMCommon<Int> = FList.of(*arrayOf<Int>(1,2))
private val intListOfThree: IMCommon<Int> = FList.of(*arrayOf<Int>(1,2,3))
private val intListOfFour: IMCommon<Int> = FList.of(*arrayOf<Int>(1,2,1,3))
private val intListOfFourA: IMCommon<Int> = FList.of(*arrayOf<Int>(1,2,2,3))
private val intListOfFourB: IMCommon<Int> = FList.of(*arrayOf<Int>(1,2,3,2))
private val intListOfSix: IMCommon<Int> = FList.of(*arrayOf<Int>(1,2,3,3,2,1))
private val intSet: IMCommon<Int> = FKSet.ofs(1,2)


private val strListOfNone: IMCommon<String> = FList.of(*emptyArrayOfStr)
private val strListOfOne: IMCommon<String> = FList.of(*arrayOf<String>("1"))
private val strListOfTwo: IMCommon<String> = FList.of(*arrayOf<String>("1","2"))
private val strListOfThree: IMCommon<String> = FList.of(*arrayOf<String>("1","2","3"))

class FListIMCollectionTest : FunSpec({

  beforeTest {}

  test("fall") {
    intListOfNone.fall { true } shouldBe true
    intListOfNone.fall { false } shouldBe true
    intListOfThree.fall { it > 0 } shouldBe true
    intListOfThree.fall { it > 1 } shouldBe false
    strListOfThree.fall { it > "0" } shouldBe true
    strListOfThree.fall { it > "1" } shouldBe false
  }

  test("fany") {
    intListOfNone.fany { true } shouldBe true
    intListOfNone.fany { false } shouldBe true
    intListOfThree.fany { it > 3 } shouldBe false
    intListOfThree.fany { it > 1 } shouldBe true
    strListOfThree.fany { it > "3" } shouldBe false
    strListOfThree.fany { it > "1" } shouldBe true
  }

  test("fcontains") {
    intListOfNone.fcontains(1) shouldBe false

    intListOfOne.fcontains(0) shouldBe false
    intListOfOne.fcontains(1) shouldBe true
    intListOfTwo.fcontains(0) shouldBe false
    intListOfTwo.fcontains(1) shouldBe true
    intListOfTwo.fcontains(2) shouldBe true
    intListOfThree.fcontains(0) shouldBe false
    intListOfThree.fcontains(1) shouldBe true
    intListOfThree.fcontains(3) shouldBe true

    strListOfOne.fcontains("0") shouldBe false
    strListOfOne.fcontains("1") shouldBe true
    strListOfTwo.fcontains("0") shouldBe false
    strListOfTwo.fcontains("1") shouldBe true
    strListOfTwo.fcontains("2") shouldBe true
    strListOfThree.fcontains("0") shouldBe false
    strListOfThree.fcontains("1") shouldBe true
    strListOfThree.fcontains("3") shouldBe true
  }

  test("fcount") {
    intListOfNone.fcount { _ -> true } shouldBe 0
    intListOfNone.fcount { _ -> false } shouldBe 0
    intListOfOne.fcount { _ -> true } shouldBe 1
    intListOfOne.fcount { 0 < it } shouldBe 1
    intListOfOne.fcount { it < 0 } shouldBe 0
    intListOfOne.fcount { _ -> false } shouldBe 0
    intListOfTwo.fcount { _ -> true } shouldBe 2
    intListOfTwo.fcount { 0 < it } shouldBe 2
    intListOfTwo.fcount { 1 < it } shouldBe 1
    intListOfTwo.fcount { it < 0 } shouldBe 0
    intListOfTwo.fcount { _ -> false } shouldBe 0
  }

  test("fdropAll") {
    intListOfNone.fdropAll(intListOfNone as IMList<Int>) shouldBe FLNil
    intListOfOne.fdropAll(intListOfNone) shouldBe intListOfOne
    intListOfOne.fdropAll(intListOfOne as IMList<Int>) shouldBe FLNil
    intListOfOne.fdropAll(FLNil) shouldBe intListOfOne
    (intListOfOne.fdropAll(FLNil) === intListOfOne) shouldBe true
    intListOfOne.fdropAll(intListOfTwo as IMList<Int>) shouldBe FLNil
    FList.of(*arrayOf<Int>(2,1)).fdropAll(intListOfThree as IMList<Int>) shouldBe FLNil
    FList.of(*arrayOf<Int>(3,2,1)).fdropAll(intListOfTwo) shouldBe FLCons(3, FLNil)
    intListOfFour.fdropAll(intSet) shouldBe FList.of(*arrayOf<Int>(3))
    intListOfFour.fdropAll(emptyIMKISet()) shouldBe intListOfFour
    (intListOfFour.fdropAll(emptyIMKISet()) === intListOfFour) shouldBe true
    intListOfFourA.fdropAll(intSet) shouldBe FList.of(*arrayOf<Int>(3))
    intListOfFourB.fdropAll(intSet) shouldBe FList.of(*arrayOf<Int>(3))
    intListOfFour.fdropAll(intListOfTwo) shouldBe FList.of(*arrayOf<Int>(3))
    intListOfFourA.fdropAll(intListOfTwo) shouldBe FList.of(*arrayOf<Int>(3))
    intListOfFourB.fdropAll(intListOfTwo) shouldBe FList.of(*arrayOf<Int>(3))
  }

  test("fdropItem") {
    intListOfNone.fdropItem(0) shouldBe FLNil
    intListOfOne.fdropItem(0) shouldBe intListOfOne
    intListOfOne.fdropItem(1) shouldBe FLNil
    intListOfOne.fdropItem(2) shouldBe intListOfOne
    FList.of(*arrayOf<Int>(2,1)).fdropItem(2) shouldBe intListOfOne
    FList.of(*arrayOf<Int>(2,1,2)).fdropItem(2) shouldBe intListOfOne
    FList.of(*arrayOf<Int>(1, 2, 1, 2)).fdropItem(2) shouldBe FLCons(1, intListOfOne as FList<Int>)
    intListOfSix.fdropItem(3) shouldBe FList.of(*arrayOf<Int>(1, 2, 2, 1))
    intListOfSix.fdropItem(2) shouldBe FList.of(*arrayOf<Int>(1, 3, 3, 1))
    intListOfSix.fdropItem(1) shouldBe FList.of(*arrayOf<Int>(2, 3, 3, 2))
  }

  test("fdropWhen") {
    intListOfNone.fdropWhen { it > 1 } shouldBe FLNil
    intListOfOne.fdropWhen { it > 1 } shouldBe FLCons(1, FLNil)
    (intListOfOne.fdropWhen { false } === intListOfOne) shouldBe true
    FList.of(*arrayOf<Int>(2,1)).fdropWhen { it > 1 }  shouldBe FLCons(1, FLNil)
    FList.of(*arrayOf<Int>(3,2,1)).fdropWhen { it > 1 }  shouldBe FLCons(1, FLNil)
    FList.of(*arrayOf<Int>(3,2,1,0)).fdropWhen { it > 1 } shouldBe FLCons(1, FLCons(0, FLNil))
    intListOfFour.fdropWhen { it > 1 } shouldBe FLCons(1, FLCons(1, FLNil))
    intListOfFour.fdropWhen { false } shouldBe intListOfFour
    (intListOfFour.fdropWhen { false } === intListOfFour) shouldBe true
    intListOfFourA.fdropWhen { it < 2 } shouldBe FLCons(2, FLCons(2, FLCons(3, FLNil)))
    intListOfFourA.fdropWhen { it < 3 } shouldBe FLCons(3, FLNil)
    intListOfFourB.fdropWhen { it < 3 } shouldBe FLCons(3, FLNil)
  }

  test("fempty") {
    intListOfNone.fempty() shouldBe true
    strListOfNone.fempty() shouldBe true
    (intListOfNone === strListOfNone) shouldBe true
    intListOfOne.fempty() shouldBe false
    strListOfTwo.fempty() shouldBe false
  }

  test("ffilter") {
    intListOfNone.ffilter {0 == it % 2} shouldBe FLNil
    intListOfOne.ffilter {0 == it % 2} shouldBe FLNil
    (intListOfOne.ffilter {true} === intListOfOne) shouldBe true
    intListOfTwo.ffilter {0 == it % 2} shouldBe FLCons(2,FLNil)
    (intListOfTwo.ffilter {true} === intListOfTwo) shouldBe true
    intListOfThree.ffilter {0 == it % 2} shouldBe FLCons(2,FLNil)
    (intListOfThree.ffilter {true} === intListOfThree) shouldBe true
    FList.of(*arrayOf<Int>(1,2,3,4)).ffilter {0 == it % 2} shouldBe FLCons(2,FLCons(4,FLNil))
  }

  test("ffilterNot") {
    intListOfNone.ffilterNot {0 == it % 2} shouldBe FLNil
    intListOfOne.ffilterNot {0 == it % 2} shouldBe FLCons(1,FLNil)
    intListOfOne.ffilterNot {false} shouldBe intListOfOne
    (intListOfOne.ffilterNot {false} === intListOfOne) shouldBe true
    intListOfTwo.ffilterNot {0 == it % 2} shouldBe FLCons(1,FLNil)
    (intListOfTwo.ffilterNot {false} === intListOfTwo) shouldBe true
    intListOfThree.ffilterNot {0 == it % 2} shouldBe FLCons(1,FLCons(3,FLNil))
    (intListOfThree.ffilterNot {false} === intListOfThree) shouldBe true
    FList.of(*arrayOf<Int>(1,2,3,4)).ffilterNot {0 == it % 2} shouldBe FLCons(1,FLCons(3,FLNil))
  }

  test("ffindAny") {
    intListOfNone.ffindAny { true } shouldBe null
    intListOfNone.ffindAny { false } shouldBe null

    intListOfOne.ffindAny { it == 0 } shouldBe null
    intListOfOne.ffindAny { it == 1 } shouldBe 1
    intListOfTwo.ffindAny { it == 0 } shouldBe null
    intListOfTwo.ffindAny { it == 1 } shouldBe 1
    intListOfTwo.ffindAny { it == 2 } shouldBe 2
    intListOfThree.ffindAny { it == 0 } shouldBe null
    intListOfThree.ffindAny { it == 1 } shouldBe 1
    intListOfThree.ffindAny { it == 3 } shouldBe 3

    strListOfOne.ffindAny { it == "0" } shouldBe null
    strListOfOne.ffindAny { it == "1" } shouldBe "1"
    strListOfTwo.ffindAny { it == "0" } shouldBe null
    strListOfTwo.ffindAny { it == "1" } shouldBe "1"
    strListOfTwo.ffindAny { it == "2" } shouldBe "2"
    strListOfThree.ffindAny { it == "0" } shouldBe null
    strListOfThree.ffindAny { it == "1" } shouldBe "1"
    strListOfThree.ffindAny { it == "3" } shouldBe "3"
  }

  test("fisStrict") {
    intListOfNone.fisStrict() shouldBe true
    strListOfThree.fisStrict() shouldBe true
    intListOfThree.fisStrict() shouldBe true
    FList.of(FKSet.ofi("A"), FKSet.ofs("A")).fisStrict() shouldBe false
    FList.of(setOf(FKSet.ofi("A"), FKSet.ofs("A"))).fisStrict() shouldBe false
    FList.of(setOf(FKSet.ofi("A"), FKSet.ofi("A"), setOf(FKSet.ofi("A"), FKSet.ofs("A")))).fisStrict() shouldBe false
    FList.of(FKSet.ofi(FKSet.ofi("A"), FKSet.ofi("A"), setOf(FKSet.ofi("A"), FKSet.ofs("A")))).fisStrict() shouldBe false
    FList.of(mutableMapOf(("1" to 1), ("2" to 2)), mutableMapOf((1 to 1), (2 to 2))).fisStrict() shouldBe false
    FList.of(mutableMapOf((1 to "1"), (2 to "2")), mutableMapOf((1 to 1), (2 to 2))).fisStrict() shouldBe false
    FList.of(mutableMapOf((1 to 1), (2 to 2)), mutableMapOf((1 to 1), (2 to 2))).fisStrict() shouldBe true
  }

  test("fnone") {
    intListOfNone.fnone { true } shouldBe true
    intListOfNone.fnone { false } shouldBe true
    intListOfOne.fnone { false } shouldBe true
    intListOfOne.fnone { true } shouldBe false
    intListOfTwo.fnone { it == 1 } shouldBe false
    intListOfTwo.fnone { it > 10 } shouldBe true
  }

  test("fpick") {
    intListOfNone.fpick() shouldBe null
    intListOfOne.fpick()?.let { it::class } shouldBe Int::class
    strListOfOne.fpick()?.let { it::class } shouldBe String::class
  }

  test("fpickNotEmpty") {
    intListOfNone.fpickNotEmpty() shouldBe null
    intListOfOne.fpickNotEmpty()?.let { it::class } shouldBe Int::class
    strListOfOne.fpickNotEmpty()?.let { it::class } shouldBe String::class
    FList.of(FKSet.ofi("A"), FKSet.ofs("A")).fpickNotEmpty()?.equals(FKSet.ofi("A")) shouldBe true
    FList.of(emptySet<Int>(), emptySet<Int>()).fpickNotEmpty() shouldBe null
    FList.of(emptySet<Int>(), emptySet<Int>(), FKSet.ofi("A")).fpickNotEmpty()?.equals(FKSet.ofi("A")) shouldBe true
  }

  test ("fpopAndReminder") {
    val (pop1, reminder1) = intListOfNone.fpopAndRemainder()
    pop1 shouldBe null
    reminder1.fempty() shouldBe true
    val (pop2, reminder2) = intListOfOne.fpopAndRemainder()
    pop2 shouldBe intListOfOne.fpick()
    reminder2.fempty() shouldBe true
    val (pop3, reminder3) = intListOfTwo.fpopAndRemainder()
    pop3 shouldBe intListOfOne.fpick()
    reminder3.equals(FLCons(2, FLNil)) shouldBe true
    val (pop4, reminder4) = intListOfThree.fpopAndRemainder()
    pop4 shouldBe intListOfOne.fpick()
    reminder4.equals(FLCons(2, FLCons(3, FLNil))) shouldBe true
  }

  test("fsize") {
    intListOfNone.fsize() shouldBe 0
    intListOfOne.fsize() shouldBe 1
    intListOfTwo.fsize() shouldBe 2
    intListOfThree.fsize() shouldBe 3
  }

  test("fisNested") {
    intListOfNone.fisNested() shouldBe null
    intListOfOne.fisNested() shouldBe false
    intListOfTwo.fisNested() shouldBe false
    FList.of(intListOfNone).fisNested() shouldBe true
    FList.of(emptyArray<Int>()).fisNested() shouldBe true
    FList.of(setOf<Int>()).fisNested() shouldBe true
    FList.of(*arrayOf(listOf<Int>())).fisNested() shouldBe true
    FList.of(mapOf<Int, Int>()).fisNested() shouldBe true
  }
})
