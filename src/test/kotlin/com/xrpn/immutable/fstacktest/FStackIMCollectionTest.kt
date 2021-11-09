package com.xrpn.immutable.fstacktest

import com.xrpn.imapi.IMCollection
import com.xrpn.imapi.IMStack
import com.xrpn.immutable.*
import com.xrpn.immutable.FKSet.Companion.emptyIMRSet
import com.xrpn.immutable.FStack.Companion.emptyIMStack
import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.emptyArrayOfStr
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intStackOfNone: IMCollection<Int> = FStack.of(*emptyArrayOfInt)
private val intStackOfOne: IMCollection<Int> = FStack.of(*arrayOf<Int>(1))
private val intStackOfOneA: IMCollection<Int> = FStack.of(*arrayOf<Int>(3))
private val intStackOfTwo: IMCollection<Int> = FStack.of(*arrayOf<Int>(1,2))
private val intStackOfTwoA: IMCollection<Int> = FStack.of(*arrayOf<Int>(1,3))
private val intStackOfThree: IMCollection<Int> = FStack.of(*arrayOf<Int>(1,2,3))
private val intStackOfFour: IMCollection<Int> = FStack.of(*arrayOf<Int>(1,2,1,3))
private val intStackOfFourA: IMCollection<Int> = FStack.of(*arrayOf<Int>(1,2,2,3))
private val intStackOfFourB: IMCollection<Int> = FStack.of(*arrayOf<Int>(1,2,3,2))
private val intStackOfSix: IMCollection<Int> = FStack.of(*arrayOf<Int>(1,2,3,3,2,1))
private val intSet: IMCollection<Int> = FKSet.ofs(1,2)


private val strStackOfNone: IMCollection<String> = FStack.of(*emptyArrayOfStr)
private val strStackOfOne: IMCollection<String> = FStack.of(*arrayOf<String>("1"))
private val strStackOfTwo: IMCollection<String> = FStack.of(*arrayOf<String>("1","2"))
private val strStackOfThree: IMCollection<String> = FStack.of(*arrayOf<String>("1","2","3"))

class FStackIMCollectionTest : FunSpec({

  beforeTest {}

  test("fall") {
    intStackOfNone.fall { true } shouldBe true
    intStackOfNone.fall { false } shouldBe true
    intStackOfThree.fall { it > 0 } shouldBe true
    intStackOfThree.fall { it > 1 } shouldBe false
    strStackOfThree.fall { it > "0" } shouldBe true
    strStackOfThree.fall { it > "1" } shouldBe false
  }

  test("fany") {
    intStackOfNone.fany { true } shouldBe true
    intStackOfNone.fany { false } shouldBe true
    intStackOfThree.fany { it > 3 } shouldBe false
    intStackOfThree.fany { it > 1 } shouldBe true
    strStackOfThree.fany { it > "3" } shouldBe false
    strStackOfThree.fany { it > "1" } shouldBe true
  }

  test("fcontains") {
    intStackOfNone.fcontains(1) shouldBe false

    intStackOfOne.fcontains(0) shouldBe false
    intStackOfOne.fcontains(1) shouldBe true
    intStackOfTwo.fcontains(0) shouldBe false
    intStackOfTwo.fcontains(1) shouldBe true
    intStackOfTwo.fcontains(2) shouldBe true
    intStackOfThree.fcontains(0) shouldBe false
    intStackOfThree.fcontains(1) shouldBe true
    intStackOfThree.fcontains(3) shouldBe true

    strStackOfOne.fcontains("0") shouldBe false
    strStackOfOne.fcontains("1") shouldBe true
    strStackOfTwo.fcontains("0") shouldBe false
    strStackOfTwo.fcontains("1") shouldBe true
    strStackOfTwo.fcontains("2") shouldBe true
    strStackOfThree.fcontains("0") shouldBe false
    strStackOfThree.fcontains("1") shouldBe true
    strStackOfThree.fcontains("3") shouldBe true
  }

  test("fcount") {
    intStackOfNone.fcount { _ -> true } shouldBe 0
    intStackOfNone.fcount { _ -> false } shouldBe 0
    intStackOfOne.fcount { _ -> true } shouldBe 1
    intStackOfOne.fcount { 0 < it } shouldBe 1
    intStackOfOne.fcount { it < 0 } shouldBe 0
    intStackOfOne.fcount { _ -> false } shouldBe 0
    intStackOfTwo.fcount { _ -> true } shouldBe 2
    intStackOfTwo.fcount { 0 < it } shouldBe 2
    intStackOfTwo.fcount { 1 < it } shouldBe 1
    intStackOfTwo.fcount { it < 0 } shouldBe 0
    intStackOfTwo.fcount { _ -> false } shouldBe 0
  }

  test("fdropAll") {
    intStackOfNone.fdropAll(intStackOfNone as IMStack<Int>) shouldBe emptyIMStack<Int>()
    intStackOfOne.fdropAll(intStackOfNone) shouldBe intStackOfOne
    intStackOfOne.fdropAll(intStackOfOne as IMStack<Int>) shouldBe emptyIMStack<Int>()
    intStackOfOne.fdropAll(intStackOfTwo as IMStack<Int>) shouldBe emptyIMStack<Int>()
    (intStackOfOne.fdropAll(FLNil) === intStackOfOne) shouldBe true
    FStack.of(*arrayOf<Int>(2,1)).fdropAll(intStackOfThree as IMStack<Int>) shouldBe emptyIMStack<Int>()
    FStack.of(*arrayOf<Int>(3,2,1)).fdropAll(intStackOfTwo) shouldBe intStackOfOneA
    intStackOfFour.fdropAll(intSet) shouldBe intStackOfOneA
    intStackOfFour.fdropAll(emptyIMRSet()) shouldBe intStackOfFour
    (intStackOfFour.fdropAll(emptyIMRSet()) === intStackOfFour) shouldBe true
    intStackOfFourA.fdropAll(intSet) shouldBe intStackOfOneA
    intStackOfFourB.fdropAll(intSet) shouldBe intStackOfOneA
    intStackOfFour.fdropAll(intStackOfTwo) shouldBe intStackOfOneA
    intStackOfFourA.fdropAll(intStackOfTwo) shouldBe intStackOfOneA
    intStackOfFourB.fdropAll(intStackOfTwo) shouldBe intStackOfOneA
  }

  test("fdropItem") {
    (intStackOfNone.fdropItem(0) === intStackOfNone) shouldBe true
    intStackOfOne.fdropItem(0) shouldBe intStackOfOne
    intStackOfOne.fdropItem(1) shouldBe emptyIMStack<Int>()
    intStackOfOne.fdropItem(2) shouldBe intStackOfOne
    FStack.of(*arrayOf<Int>(2,1)).fdropItem(2) shouldBe intStackOfOne
    FStack.of(*arrayOf<Int>(2,1,2)).fdropItem(2) shouldBe intStackOfOne
    FStack.of(*arrayOf<Int>(1, 2, 1, 2)).fdropItem(2) shouldBe FStack.of(1, 1)
    intStackOfSix.fdropItem(3) shouldBe FStack.of(*arrayOf<Int>(1, 2, 2, 1))
    intStackOfSix.fdropItem(2) shouldBe FStack.of(*arrayOf<Int>(1, 3, 3, 1))
    intStackOfSix.fdropItem(1) shouldBe FStack.of(*arrayOf<Int>(2, 3, 3, 2))
  }

  test("fdropWhen") {
    intStackOfNone.fdropWhen { it > 1 } shouldBe emptyIMStack<Int>()
    intStackOfOne.fdropWhen { it > 1 }  shouldBe intStackOfOne
    (intStackOfOne.fdropWhen { it > 1 } === intStackOfOne) shouldBe true
    FStack.of(*arrayOf<Int>(2,1)).fdropWhen { it > 1 }  shouldBe intStackOfOne
    FStack.of(*arrayOf<Int>(3,2,1)).fdropWhen { it > 1 }  shouldBe intStackOfOne
    FStack.of(*arrayOf<Int>(3,2,1,0)).fdropWhen { it > 1 } shouldBe FStack.of(1, 0)
    intStackOfFour.fdropWhen { it > 1 } shouldBe FStack.of(1, 1)
    (intStackOfFour.fdropWhen { false } === intStackOfFour) shouldBe true
    intStackOfFourA.fdropWhen { it < 2 } shouldBe FStack.of(2, 2, 3)
    intStackOfFourA.fdropWhen { it < 3 } shouldBe intStackOfOneA
    intStackOfFourB.fdropWhen { it < 3 } shouldBe intStackOfOneA
  }

  test("fempty") {
    emptyIMStack<Int>().fempty() shouldBe true
    FStack.of("a").fempty() shouldBe false
    
    intStackOfNone.fempty() shouldBe true
    strStackOfNone.fempty() shouldBe true
    (intStackOfNone === strStackOfNone) shouldBe true
    intStackOfOne.fempty() shouldBe false
    strStackOfTwo.fempty() shouldBe false
  }

  test("ffilter") {
    intStackOfNone.ffilter {0 == it % 2} shouldBe emptyIMStack<Int>()
    intStackOfOne.ffilter {0 == it % 2} shouldBe emptyIMStack<Int>()
    (intStackOfOne.ffilter {true} === intStackOfOne) shouldBe true
    intStackOfTwo.ffilter {0 == it % 2} shouldBe FStack.of(2)
    (intStackOfTwo.ffilter {true} === intStackOfTwo) shouldBe true
    intStackOfThree.ffilter {0 == it % 2} shouldBe FStack.of(2)
    (intStackOfThree.ffilter {true} === intStackOfThree) shouldBe true
    FStack.of(*arrayOf<Int>(1,2,3,4)).ffilter {0 == it % 2} shouldBe FStack.of(2,4)
  }

  test("ffilterNot") {
    intStackOfNone.ffilterNot {0 == it % 2} shouldBe emptyIMStack<Int>()
    intStackOfOne.ffilterNot {0 == it % 2} shouldBe intStackOfOne
    (intStackOfOne.ffilterNot {false} === intStackOfOne) shouldBe true
    intStackOfTwo.ffilterNot {0 == it % 2} shouldBe intStackOfOne
    (intStackOfTwo.ffilterNot {false} === intStackOfTwo) shouldBe true
    intStackOfThree.ffilterNot {0 == it % 2} shouldBe intStackOfTwoA
    (intStackOfThree.ffilterNot {false} === intStackOfThree) shouldBe true
    FStack.of(*arrayOf<Int>(1,2,3,4)).ffilterNot {0 == it % 2} shouldBe intStackOfTwoA
  }

  test("ffindAny") {
    intStackOfNone.ffindAny { true } shouldBe null
    intStackOfNone.ffindAny { false } shouldBe null

    intStackOfOne.ffindAny { it == 0 } shouldBe null
    intStackOfOne.ffindAny { it == 1 } shouldBe 1
    intStackOfTwo.ffindAny { it == 0 } shouldBe null
    intStackOfTwo.ffindAny { it == 1 } shouldBe 1
    intStackOfTwo.ffindAny { it == 2 } shouldBe 2
    intStackOfThree.ffindAny { it == 0 } shouldBe null
    intStackOfThree.ffindAny { it == 1 } shouldBe 1
    intStackOfThree.ffindAny { it == 3 } shouldBe 3

    strStackOfOne.ffindAny { it == "0" } shouldBe null
    strStackOfOne.ffindAny { it == "1" } shouldBe "1"
    strStackOfTwo.ffindAny { it == "0" } shouldBe null
    strStackOfTwo.ffindAny { it == "1" } shouldBe "1"
    strStackOfTwo.ffindAny { it == "2" } shouldBe "2"
    strStackOfThree.ffindAny { it == "0" } shouldBe null
    strStackOfThree.ffindAny { it == "1" } shouldBe "1"
    strStackOfThree.ffindAny { it == "3" } shouldBe "3"
  }

  test("fisStrict") {
    intStackOfNone.fisStrict() shouldBe true
    strStackOfThree.fisStrict() shouldBe true
    intStackOfThree.fisStrict() shouldBe true
    FStack.of(FKSet.ofi("A"), FKSet.ofs("A")).fisStrict() shouldBe false
    FStack.of(setOf(FKSet.ofi("A"), FKSet.ofs("A"))).fisStrict() shouldBe false
    FStack.of(setOf(FKSet.ofi("A"), FKSet.ofi("A"), setOf(FKSet.ofi("A"), FKSet.ofs("A")))).fisStrict() shouldBe false
    FStack.of(FKSet.ofi(FKSet.ofi("A"), FKSet.ofi("A"), setOf(FKSet.ofi("A"), FKSet.ofs("A")))).fisStrict() shouldBe false
    FStack.of(mutableMapOf(("1" to 1), ("2" to 2)), mutableMapOf((1 to 1), (2 to 2))).fisStrict() shouldBe false
    FStack.of(mutableMapOf((1 to "1"), (2 to "2")), mutableMapOf((1 to 1), (2 to 2))).fisStrict() shouldBe false
    FStack.of(mutableMapOf((1 to 1), (2 to 2)), mutableMapOf((1 to 1), (2 to 2))).fisStrict() shouldBe true
  }

  test("fnone") {
    intStackOfNone.fnone { true } shouldBe true
    intStackOfNone.fnone { false } shouldBe true
    intStackOfOne.fnone { false } shouldBe true
    intStackOfOne.fnone { true } shouldBe false
    intStackOfTwo.fnone { it == 1 } shouldBe false
    intStackOfTwo.fnone { it > 10 } shouldBe true
  }

  test("fpick") {
    intStackOfNone.fpick() shouldBe null
    intStackOfOne.fpick()?.let { it::class } shouldBe Int::class
    strStackOfOne.fpick()?.let { it::class } shouldBe String::class
  }

  test("fpickNotEmpty") {
    intStackOfNone.fpickNotEmpty() shouldBe null
    intStackOfOne.fpickNotEmpty()?.let { it::class } shouldBe Int::class
    strStackOfOne.fpickNotEmpty()?.let { it::class } shouldBe String::class
    FStack.of(FKSet.ofi("A"), FKSet.ofs("A")).fpickNotEmpty()?.equals(FKSet.ofi("A")) shouldBe true
    FStack.of(emptySet<Int>(), emptySet<Int>()).fpickNotEmpty() shouldBe null
    FStack.of(emptySet<Int>(), emptySet<Int>(), FKSet.ofi("A")).fpickNotEmpty()?.equals(FKSet.ofi("A")) shouldBe true
  }

  test ("fpopAndReminder") {
    val (pop1, reminder1) = intStackOfNone.fpopAndRemainder()
    pop1 shouldBe null
    reminder1.fempty() shouldBe true
    val (pop2, reminder2) = intStackOfOne.fpopAndRemainder()
    pop2 shouldBe intStackOfOne.fpick()
    reminder2.fempty() shouldBe true
    val (pop3, reminder3) = intStackOfTwo.fpopAndRemainder()
    pop3 shouldBe intStackOfOne.fpick()
    reminder3.equals(FStack.of(2)) shouldBe true
    val (pop4, reminder4) = intStackOfThree.fpopAndRemainder()
    pop4 shouldBe intStackOfOne.fpick()
    reminder4.equals(FStack.of(2, 3)) shouldBe true
  }

  test("fsize") {
    intStackOfNone.fsize() shouldBe 0
    intStackOfOne.fsize() shouldBe 1
    intStackOfTwo.fsize() shouldBe 2
    intStackOfThree.fsize() shouldBe 3
  }

  test("fisNested") {
    intStackOfNone.fisNested() shouldBe null
    intStackOfOne.fisNested() shouldBe false
    intStackOfTwo.fisNested() shouldBe false
    FStack.of(intStackOfNone).fisNested() shouldBe true
    FStack.of(emptyArray<Int>()).fisNested() shouldBe true
    FStack.of(setOf<Int>()).fisNested() shouldBe true
    FStack.of(*arrayOf(listOf<Int>())).fisNested() shouldBe true
    FStack.of(mapOf<Int, Int>()).fisNested() shouldBe true
  }
})
