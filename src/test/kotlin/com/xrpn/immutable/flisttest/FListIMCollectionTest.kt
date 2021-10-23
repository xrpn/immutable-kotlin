package com.xrpn.immutable.flisttest

import com.xrpn.imapi.IMCollection
import com.xrpn.immutable.*
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.emptyArrayOfStr
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intListOfNone: IMCollection<Int> = FList.of(*emptyArrayOfInt)
private val intListOfOne: IMCollection<Int> = FList.of(*arrayOf<Int>(1))
private val intListOfTwo: IMCollection<Int> = FList.of(*arrayOf<Int>(1,2))
private val intListOfThree: IMCollection<Int> = FList.of(*arrayOf<Int>(1,2,3))
private val intListOfFour: IMCollection<Int> = FList.of(*arrayOf<Int>(1,2,1,3))
private val intListOfFourA: IMCollection<Int> = FList.of(*arrayOf<Int>(1,2,2,3))
private val intListOfFourB: IMCollection<Int> = FList.of(*arrayOf<Int>(1,2,3,2))

private val strListOfNone: IMCollection<String> = FList.of(*emptyArrayOfStr)
private val strListOfOne: IMCollection<String> = FList.of(*arrayOf<String>("1"))
private val strListOfTwo: IMCollection<String> = FList.of(*arrayOf<String>("1","2"))
private val strListOfThree: IMCollection<String> = FList.of(*arrayOf<String>("1","2","3"))

class FListIMCollectionTest : FunSpec({

  beforeTest {}

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

  test("fdropWhen") {
    intListOfNone.fdropWhen { it > 1 } shouldBe FLNil
    intListOfOne.fdropWhen { it > 1 }  shouldBe FLCons(1, FLNil)
    FList.of(*arrayOf<Int>(2,1)).fdropWhen { it > 1 }  shouldBe FLCons(1, FLNil)
    FList.of(*arrayOf<Int>(3,2,1)).fdropWhen { it > 1 }  shouldBe FLCons(1, FLNil)
    FList.of(*arrayOf<Int>(3,2,1,0)).fdropWhen { it > 1 } shouldBe FLCons(1, FLCons(0, FLNil))
    intListOfFour.fdropWhen { it > 1 } shouldBe FLCons(1, FLCons(1, FLNil))
    intListOfFourA.fdropWhen { it < 2 } shouldBe FLCons(2, FLCons(2, FLCons(3, FLNil)))
    intListOfFourA.fdropWhen { it < 3 } shouldBe FLCons(3, FLNil)
    intListOfFourB.fdropWhen { it < 3 } shouldBe FLCons(3, FLNil)
  }

  test("ffilter") {
    intListOfNone.ffilter {0 == it % 2} shouldBe FLNil
    intListOfOne.ffilter {0 == it % 2} shouldBe FLNil
    intListOfTwo.ffilter {0 == it % 2} shouldBe FLCons(2,FLNil)
    intListOfThree.ffilter {0 == it % 2} shouldBe FLCons(2,FLNil)
    FList.of(*arrayOf<Int>(1,2,3,4)).ffilter {0 == it % 2} shouldBe FLCons(2,FLCons(4,FLNil))
  }

  test("ffilterNot") {
    intListOfNone.ffilterNot {0 == it % 2} shouldBe FLNil
    intListOfOne.ffilterNot {0 == it % 2} shouldBe FLCons(1,FLNil)
    intListOfTwo.ffilterNot {0 == it % 2} shouldBe FLCons(1,FLNil)
    intListOfThree.ffilterNot {0 == it % 2} shouldBe FLCons(1,FLCons(3,FLNil))
    FList.of(*arrayOf<Int>(1,2,3,4)).ffilterNot {0 == it % 2} shouldBe FLCons(1,FLCons(3,FLNil))
  }

  test("fempty") {
    intListOfNone.fempty() shouldBe true
    strListOfNone.fempty() shouldBe true
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

  test("fsize") {
    intListOfNone.fsize() shouldBe 0
    intListOfOne.fsize() shouldBe 1
    intListOfTwo.fsize() shouldBe 2
    intListOfThree.fsize() shouldBe 3
  }

  test("fisNested") {
    intListOfNone.fisNested() shouldBe false
    intListOfOne.fisNested() shouldBe false
    intListOfTwo.fisNested() shouldBe false
    FList.of(intListOfNone).fisNested() shouldBe true
    FList.of(emptyArray<Int>()).fisNested() shouldBe true
    FList.of(setOf<Int>()).fisNested() shouldBe true
    FList.of(*arrayOf(listOf<Int>())).fisNested() shouldBe true
    FList.of(mapOf<Int, Int>()).fisNested() shouldBe true
  }
})
