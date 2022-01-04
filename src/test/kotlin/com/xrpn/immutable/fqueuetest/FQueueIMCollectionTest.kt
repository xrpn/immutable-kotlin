package com.xrpn.immutable.fqueuetest

import com.xrpn.imapi.IMCommon
import com.xrpn.imapi.IMQueue
import com.xrpn.immutable.*
import com.xrpn.immutable.FKSet.Companion.emptyIMKISet
import com.xrpn.immutable.FQueue.Companion.emptyIMQueue
import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.emptyArrayOfStr
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intQueueOfNone: IMCommon<Int> = FQueue.of(*emptyArrayOfInt)
private val intQueueOfOne: IMCommon<Int> = FQueue.of(*arrayOf<Int>(1))
private val intQueueOfOneYR: IMCommon<Int> = FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)
private val intQueueOfOneA: IMCommon<Int> = FQueue.of(*arrayOf<Int>(3))
private val intQueueOfTwo: IMCommon<Int> = FQueue.of(*arrayOf<Int>(1,2))
private val intQueueOfTwoYR: IMCommon<Int> = FQueue.of(*arrayOf<Int>(1,2), readyToDequeue = true)
private val intQueueOfTwoA: IMCommon<Int> = FQueue.of(*arrayOf<Int>(1,3))
private val intQueueOfTworA: IMCommon<Int> = FQueue.of(*arrayOf<Int>(3,1))
private val intQueueOfThree2B = FQueueBody.of(FLCons(3, FLNil), FLCons(2,FLCons(1, FLNil)))
private val intQueueOfThree2F = FQueueBody.of(FLCons(3, FLCons(1, FLNil)), FLCons(2,FLNil))
private val intQueueOfFour: IMCommon<Int> = FQueue.of(*arrayOf<Int>(1,2,1,3))
private val intQueueOfFourFB: IMCommon<Int> = FQueueBody.of(FList.of(*arrayOf<Int>(1)),FList.of(*arrayOf<Int>(3,1,2)))
private val intQueueOfFourA: IMCommon<Int> = FQueue.of(*arrayOf<Int>(1,2,2,3))
private val intQueueOfFourB: IMCommon<Int> = FQueue.of(*arrayOf<Int>(1,2,3,2))
private val intQueueOfSix: IMCommon<Int> = FQueue.of(*arrayOf<Int>(1,2,3,3,2,1))
private val intSet: IMCommon<Int> = FKSet.ofs(1,2)

private val strQueueOfNone: IMCommon<String> = FQueue.of(*emptyArrayOfStr)
private val strQueueOfOne: IMCommon<String> = FQueue.of(*arrayOf<String>("1"))
private val strQueueOfOneYR: IMCommon<String> = FQueue.of(*arrayOf<String>("1"), readyToDequeue = true)
private val strQueueOfTwo: IMCommon<String> = FQueue.of(*arrayOf<String>("1","2"))
private val strQueueOfTwoYR: IMCommon<String> = FQueue.of(*arrayOf<String>("1","2"), readyToDequeue = true)
private val strQueueOfThree: IMCommon<String> = FQueue.of(*arrayOf<String>("1","2","3"))

class FQueueIMCollectionTest : FunSpec({

  beforeTest {}

  test("fall") {
    intQueueOfNone.fall { true } shouldBe true
    intQueueOfNone.fall { false } shouldBe true
    intQueueOfThree2B.fall { it > 0 } shouldBe true
    intQueueOfThree2B.fall { it > 1 } shouldBe false
    intQueueOfThree2F.fall { it > 0 } shouldBe true
    intQueueOfThree2F.fall { it > 1 } shouldBe false
    strQueueOfThree.fall { it > "0" } shouldBe true
    strQueueOfThree.fall { it > "1" } shouldBe false
  }

  test("fany") {
    intQueueOfNone.fany { true } shouldBe true
    intQueueOfNone.fany { false } shouldBe true
    intQueueOfThree2B.fany { it > 3 } shouldBe false
    intQueueOfThree2B.fany { it > 1 } shouldBe true
    intQueueOfThree2F.fany { it > 3 } shouldBe false
    intQueueOfThree2F.fany { it > 1 } shouldBe true
    strQueueOfThree.fany { it > "3" } shouldBe false
    strQueueOfThree.fany { it > "1" } shouldBe true
  }

  test("fcontains") {
    intQueueOfNone.fcontains(1) shouldBe false

    intQueueOfOne.fcontains(0) shouldBe false
    intQueueOfOne.fcontains(1) shouldBe true
    intQueueOfOneYR.fcontains(0) shouldBe false
    intQueueOfOneYR.fcontains(1) shouldBe true
    intQueueOfTwo.fcontains(0) shouldBe false
    intQueueOfTwo.fcontains(1) shouldBe true
    intQueueOfTwo.fcontains(2) shouldBe true
    intQueueOfTwoYR.fcontains(0) shouldBe false
    intQueueOfTwoYR.fcontains(1) shouldBe true
    intQueueOfTwoYR.fcontains(2) shouldBe true
    intQueueOfThree2B.fcontains(0) shouldBe false
    intQueueOfThree2B.fcontains(1) shouldBe true
    intQueueOfThree2B.fcontains(3) shouldBe true
    intQueueOfThree2F.fcontains(0) shouldBe false
    intQueueOfThree2F.fcontains(1) shouldBe true
    intQueueOfThree2F.fcontains(3) shouldBe true

    strQueueOfOne.fcontains("0") shouldBe false
    strQueueOfOne.fcontains("1") shouldBe true
    strQueueOfTwo.fcontains("0") shouldBe false
    strQueueOfTwo.fcontains("1") shouldBe true
    strQueueOfTwo.fcontains("2") shouldBe true
    strQueueOfThree.fcontains("0") shouldBe false
    strQueueOfThree.fcontains("1") shouldBe true
    strQueueOfThree.fcontains("3") shouldBe true
  }

  test("fcount") {
    intQueueOfNone.fcount { _ -> true } shouldBe 0
    intQueueOfNone.fcount { _ -> false } shouldBe 0
    intQueueOfOne.fcount { _ -> true } shouldBe 1
    intQueueOfOne.fcount { 0 < it } shouldBe 1
    intQueueOfOne.fcount { it < 0 } shouldBe 0
    intQueueOfOne.fcount { _ -> false } shouldBe 0
    intQueueOfOneYR.fcount { _ -> true } shouldBe 1
    intQueueOfOneYR.fcount { 0 < it } shouldBe 1
    intQueueOfOneYR.fcount { it < 0 } shouldBe 0
    intQueueOfOneYR.fcount { _ -> false } shouldBe 0
    intQueueOfTwoYR.fcount { _ -> true } shouldBe 2
    intQueueOfTwo.fcount { 0 < it } shouldBe 2
    intQueueOfTwo.fcount { 1 < it } shouldBe 1
    intQueueOfTwo.fcount { it < 0 } shouldBe 0
    intQueueOfTwo.fcount { _ -> false } shouldBe 0
    intQueueOfTwoYR.fcount { 0 < it } shouldBe 2
    intQueueOfTwoYR.fcount { 1 < it } shouldBe 1
    intQueueOfTwoYR.fcount { it < 0 } shouldBe 0
    intQueueOfTwoYR.fcount { _ -> false } shouldBe 0
  }

  test("fdropAll") {
    intQueueOfNone.fdropAll(intQueueOfNone as IMQueue<Int>) shouldBe emptyIMQueue<Int>()
    intQueueOfOne.fdropAll(intQueueOfNone) shouldBe intQueueOfOne
    intQueueOfOne.fdropAll(intQueueOfOne as IMQueue<Int>) shouldBe emptyIMQueue<Int>()
    intQueueOfOne.fdropAll(intQueueOfTwo as IMQueue<Int>) shouldBe emptyIMQueue<Int>()
    intQueueOfOne.fdropAll(intQueueOfOneYR as IMQueue<Int>) shouldBe emptyIMQueue<Int>()
    intQueueOfOne.fdropAll(intQueueOfTwoYR as IMQueue<Int>) shouldBe emptyIMQueue<Int>()
    intQueueOfOne.fdropAll(emptyIMQueue()) shouldBe intQueueOfOne
    (intQueueOfOne.fdropAll(emptyIMQueue()) === intQueueOfOne) shouldBe true
    intQueueOfOneYR.fdropAll(intQueueOfNone) shouldBe intQueueOfOne
    intQueueOfOneYR.fdropAll(intQueueOfOne) shouldBe emptyIMQueue<Int>()
    intQueueOfOneYR.fdropAll(intQueueOfTwo) shouldBe emptyIMQueue<Int>()
    intQueueOfOneYR.fdropAll(intQueueOfOneYR) shouldBe emptyIMQueue<Int>()
    intQueueOfOneYR.fdropAll(intQueueOfTwoYR) shouldBe emptyIMQueue<Int>()
    (intQueueOfOneYR.fdropAll(emptyIMQueue()) === intQueueOfOneYR) shouldBe true
    FQueue.of(*arrayOf<Int>(2,1)).fdropAll(intQueueOfThree2B as IMQueue<Int>) shouldBe emptyIMQueue<Int>()
    FQueue.of(*arrayOf<Int>(2,1)).fdropAll(intQueueOfThree2F as IMQueue<Int>) shouldBe emptyIMQueue<Int>()
    FQueue.of(*arrayOf<Int>(3,2,1)).fdropAll(intQueueOfTwo) shouldBe intQueueOfOneA
    intQueueOfFour.fdropAll(intSet) shouldBe intQueueOfOneA
    intQueueOfFourFB.fdropAll(intSet) shouldBe intQueueOfOneA
    intQueueOfFour.fdropAll(emptyIMKISet()) shouldBe intQueueOfFour
    (intQueueOfFour.fdropAll(emptyIMKISet()) === intQueueOfFour) shouldBe true
    (intQueueOfFourFB.fdropAll(emptyIMKISet()) === intQueueOfFourFB) shouldBe true
    intQueueOfFourA.fdropAll(intSet) shouldBe intQueueOfOneA
    intQueueOfFourB.fdropAll(intSet) shouldBe intQueueOfOneA
    intQueueOfFour.fdropAll(intQueueOfTwo) shouldBe intQueueOfOneA
    intQueueOfFourFB.fdropAll(intQueueOfTwo) shouldBe intQueueOfOneA
    intQueueOfFourA.fdropAll(intQueueOfTwo) shouldBe intQueueOfOneA
    intQueueOfFourB.fdropAll(intQueueOfTwo) shouldBe intQueueOfOneA
  }

  test("fdropItem") {
    (intQueueOfNone.fdropItem(0) === intQueueOfNone) shouldBe true
    intQueueOfOne.fdropItem(0) shouldBe intQueueOfOne
    intQueueOfOne.fdropItem(1) shouldBe emptyIMQueue<Int>()
    intQueueOfOne.fdropItem(2) shouldBe intQueueOfOne
    intQueueOfOneYR.fdropItem(0) shouldBe intQueueOfOne
    intQueueOfOneYR.fdropItem(1) shouldBe emptyIMQueue<Int>()
    intQueueOfOneYR.fdropItem(2) shouldBe intQueueOfOne
    FQueue.of(*arrayOf<Int>(2,1)).fdropItem(2) shouldBe intQueueOfOne
    FQueue.of(*arrayOf<Int>(2,1,2)).fdropItem(2) shouldBe intQueueOfOne
    FQueue.of(*arrayOf<Int>(1, 2, 1, 2)).fdropItem(2) shouldBe FQueue.of(1, 1)
    intQueueOfSix.fdropItem(3) shouldBe FQueue.of(*arrayOf<Int>(1, 2, 2, 1))
    intQueueOfSix.fdropItem(2) shouldBe FQueue.of(*arrayOf<Int>(1, 3, 3, 1))
    intQueueOfSix.fdropItem(1) shouldBe FQueue.of(*arrayOf<Int>(2, 3, 3, 2))
  }

  test("fdropWhen") {
    intQueueOfNone.fdropWhen { it > 1 } shouldBe emptyIMQueue<Int>()
    intQueueOfOne.fdropWhen { it > 1 }  shouldBe intQueueOfOne
    (intQueueOfOne.fdropWhen { it > 1 } === intQueueOfOne) shouldBe true
    intQueueOfOneYR.fdropWhen { it > 1 }  shouldBe intQueueOfOneYR
    (intQueueOfOneYR.fdropWhen { it > 1 } === intQueueOfOneYR) shouldBe true
    FQueue.of(*arrayOf<Int>(2,1)).fdropWhen { it > 1 }  shouldBe intQueueOfOne
    FQueue.of(*arrayOf<Int>(3,2,1)).fdropWhen { it > 1 }  shouldBe intQueueOfOne
    FQueue.of(*arrayOf<Int>(3,2,1,0)).fdropWhen { it > 1 } shouldBe FQueue.of(1, 0)
    intQueueOfFour.fdropWhen { it > 1 } shouldBe FQueue.of(1, 1)
    intQueueOfFourFB.fdropWhen { it > 1 } shouldBe FQueue.of(1, 1)
    intQueueOfFourA.fdropWhen { it < 2 } shouldBe FQueue.of(2, 2, 3)
    intQueueOfFourA.fdropWhen { it < 3 } shouldBe intQueueOfOneA
    intQueueOfFourB.fdropWhen { it < 3 } shouldBe intQueueOfOneA
  }

  test("fempty") {
    emptyIMQueue<Int>().fempty() shouldBe true
    FQueue.of("a").fempty() shouldBe false

    intQueueOfNone.fempty() shouldBe true
    strQueueOfNone.fempty() shouldBe true
    (intQueueOfNone === strQueueOfNone) shouldBe true
    intQueueOfOne.fempty() shouldBe false
    intQueueOfOneYR.fempty() shouldBe false
    strQueueOfTwo.fempty() shouldBe false
    strQueueOfTwoYR.fempty() shouldBe false
  }

  test("ffilter") {
    intQueueOfNone.ffilter {0 == it % 2} shouldBe emptyIMQueue<Int>()
    intQueueOfOne.ffilter {0 == it % 2} shouldBe emptyIMQueue<Int>()

    (intQueueOfOne.ffilter {0 == it % 2} === intQueueOfOne) shouldBe false
    (intQueueOfOne.ffilter {0 == it % 2} === intQueueOfNone) shouldBe true
    (intQueueOfOne.ffilter {1 == it} === intQueueOfOne) shouldBe true

    intQueueOfOneYR.ffilter {0 == it % 2} shouldBe emptyIMQueue<Int>()
    intQueueOfOneYR.ffilter {0 == it % 2} shouldBe emptyIMQueue<Int>()
    (intQueueOfOneYR.ffilter {1 == it} === intQueueOfOneYR) shouldBe true

    (intQueueOfOneYR.ffilter {1 == it} === intQueueOfOne) shouldBe false

    intQueueOfTwo.ffilter {0 == it % 2} shouldBe FQueue.of(2)
    intQueueOfTwoYR.ffilter {0 == it % 2} shouldBe FQueue.of(2)
    intQueueOfThree2B.ffilter {0 == it % 2} shouldBe FQueue.of(2)
    intQueueOfThree2F.ffilter {0 == it % 2} shouldBe FQueue.of(2)
    FQueue.of(*arrayOf<Int>(1,2,3,4)).ffilter {0 == it % 2} shouldBe FQueue.of(2,4)
  }

  test("ffilterNot") {
    intQueueOfNone.ffilterNot {0 == it % 2} shouldBe emptyIMQueue<Int>()
    intQueueOfOne.ffilterNot {0 == it % 2} shouldBe intQueueOfOne
    (intQueueOfOne.ffilterNot {0 == it % 2} === intQueueOfOne) shouldBe true
    intQueueOfOneYR.ffilterNot {0 == it % 2} shouldBe intQueueOfOne
    (intQueueOfOneYR.ffilterNot {0 == it % 2} === intQueueOfOneYR) shouldBe true
    intQueueOfTwo.ffilterNot {0 == it % 2} shouldBe intQueueOfOne
    intQueueOfTwoYR.ffilterNot {0 == it % 2} shouldBe intQueueOfOneYR
    intQueueOfThree2B.ffilterNot {0 == it % 2} shouldBe intQueueOfTworA
    intQueueOfThree2F.ffilterNot {0 == it % 2} shouldBe intQueueOfTworA
    FQueue.of(*arrayOf<Int>(1,2,3,4)).ffilterNot {0 == it % 2} shouldBe intQueueOfTwoA
  }

  test("ffindAny") {
    intQueueOfNone.ffindAny { true } shouldBe null
    intQueueOfNone.ffindAny { false } shouldBe null

    intQueueOfOne.ffindAny { it == 0 } shouldBe null
    intQueueOfOne.ffindAny { it == 1 } shouldBe 1
    intQueueOfOneYR.ffindAny { it == 0 } shouldBe null
    intQueueOfOneYR.ffindAny { it == 1 } shouldBe 1
    intQueueOfTwo.ffindAny { it == 0 } shouldBe null
    intQueueOfTwo.ffindAny { it == 1 } shouldBe 1
    intQueueOfTwo.ffindAny { it == 2 } shouldBe 2
    intQueueOfTwoYR.ffindAny { it == 0 } shouldBe null
    intQueueOfTwoYR.ffindAny { it == 1 } shouldBe 1
    intQueueOfTwoYR.ffindAny { it == 2 } shouldBe 2
    intQueueOfThree2B.ffindAny { it == 0 } shouldBe null
    intQueueOfThree2B.ffindAny { it == 1 } shouldBe 1
    intQueueOfThree2B.ffindAny { it == 3 } shouldBe 3
    intQueueOfThree2F.ffindAny { it == 0 } shouldBe null
    intQueueOfThree2F.ffindAny { it == 1 } shouldBe 1
    intQueueOfThree2F.ffindAny { it == 3 } shouldBe 3

    strQueueOfOne.ffindAny { it == "0" } shouldBe null
    strQueueOfOne.ffindAny { it == "1" } shouldBe "1"
    strQueueOfTwo.ffindAny { it == "0" } shouldBe null
    strQueueOfTwo.ffindAny { it == "1" } shouldBe "1"
    strQueueOfTwo.ffindAny { it == "2" } shouldBe "2"
    strQueueOfThree.ffindAny { it == "0" } shouldBe null
    strQueueOfThree.ffindAny { it == "1" } shouldBe "1"
    strQueueOfThree.ffindAny { it == "3" } shouldBe "3"
  }

  test("fisStrict") {
    intQueueOfNone.fisStrict() shouldBe true
    strQueueOfThree.fisStrict() shouldBe true
    intQueueOfThree2B.fisStrict() shouldBe true
    intQueueOfThree2F.fisStrict() shouldBe true
    FQueue.of(FKSet.ofi("A"), FKSet.ofs("A")).fisStrict() shouldBe false
    FQueue.of(setOf(FKSet.ofi("A"), FKSet.ofs("B"))).fisStrict() shouldBe false
    FQueue.of(setOf(FKSet.ofi("A"), FKSet.ofi("B"), setOf(FKSet.ofi("A"), FKSet.ofs("C")))).fisStrict() shouldBe false
    FQueue.of(FKSet.ofi(FKSet.ofi("A"), FKSet.ofi("B"), setOf(FKSet.ofi("A"), FKSet.ofs("C")))).fisStrict() shouldBe false
    FQueue.of(mutableMapOf(("1" to 1), ("2" to 2)), mutableMapOf((1 to 1), (2 to 2))).fisStrict() shouldBe false
    FQueue.of(mutableMapOf((1 to "1"), (2 to "2")), mutableMapOf((1 to 1), (2 to 2))).fisStrict() shouldBe false
    FQueue.of(mutableMapOf((1 to 1), (2 to 2)), mutableMapOf((1 to 1), (2 to 2))).fisStrict() shouldBe true
  }

  test("fnone") {
    intQueueOfNone.fnone { true } shouldBe true
    intQueueOfNone.fnone { false } shouldBe true
    intQueueOfOne.fnone { false } shouldBe true
    intQueueOfOne.fnone { true } shouldBe false
    intQueueOfOneYR.fnone { false } shouldBe true
    intQueueOfOneYR.fnone { true } shouldBe false
    intQueueOfTwo.fnone { it == 1 } shouldBe false
    intQueueOfTwo.fnone { it > 10 } shouldBe true
    intQueueOfTwoYR.fnone { it == 1 } shouldBe false
    intQueueOfTwoYR.fnone { it > 10 } shouldBe true
  }

  test("fpick") {
    intQueueOfNone.fpick() shouldBe null
    intQueueOfOne.fpick()?.let { it::class } shouldBe Int::class
    strQueueOfOne.fpick()?.let { it::class } shouldBe String::class
    intQueueOfOneYR.fpick()?.let { it::class } shouldBe Int::class
    strQueueOfOneYR.fpick()?.let { it::class } shouldBe String::class
  }

  test("fpickNotEmpty") {
    intQueueOfNone.fpickNotEmpty() shouldBe null
    intQueueOfOne.fpickNotEmpty()?.let { it::class } shouldBe Int::class
    strQueueOfOne.fpickNotEmpty()?.let { it::class } shouldBe String::class
    FQueue.of(FKSet.ofi("A"), FKSet.ofs("A")).fpickNotEmpty()?.equals(FKSet.ofs("A")) shouldBe true
    FQueue.of(emptySet<Int>(), emptySet<Int>()).fpickNotEmpty() shouldBe null
    FQueue.of(emptySet<Int>(), emptySet<Int>(), FKSet.ofi("A")).fpickNotEmpty()?.equals(FKSet.ofi("A")) shouldBe true
    FQueue.of(FKSet.ofi("A"), FKSet.ofs("A"), readyToDequeue = true).fpickNotEmpty()?.equals(FKSet.ofi("A")) shouldBe true
    FQueue.of(emptySet<Int>(), emptySet<Int>(), readyToDequeue = true).fpickNotEmpty() shouldBe null
    FQueue.of(emptySet<Int>(), emptySet<Int>(), FKSet.ofi("A"), readyToDequeue = true).fpickNotEmpty()?.equals(FKSet.ofi("A")) shouldBe true
  }

  test ("fpopAndReminder") {
    val (pop1, reminder1) = intQueueOfNone.fpopAndRemainder()
    pop1 shouldBe null
    reminder1.fempty() shouldBe true
    val (pop2, reminder2) = intQueueOfOne.fpopAndRemainder()
    pop2 shouldBe intQueueOfOne.fpick()
    reminder2.fempty() shouldBe true
    val (pop2YR, reminder2YR) = intQueueOfOneYR.fpopAndRemainder()
    pop2YR shouldBe intQueueOfOneYR.fpick()
    reminder2YR.fempty() shouldBe true
    val (pop3, reminder3) = intQueueOfTwo.fpopAndRemainder()
    pop3 shouldBe intQueueOfOne.fpick()
    reminder3.equals(FQueue.of(2)) shouldBe true
    val (pop3YR, reminder3YR) = intQueueOfTwoYR.fpopAndRemainder()
    pop3YR shouldBe intQueueOfOneYR.fpick()
    reminder3YR.equals(FQueue.of(2)) shouldBe true
    val (pop4, reminder4) = intQueueOfThree2B.fpopAndRemainder()
    pop4 shouldBe 3
    reminder4.equals(FQueue.of(1, 2)) shouldBe true
    val (pop4a, reminder4a) = reminder4.fpopAndRemainder()
    pop4a shouldBe 1
    reminder4a.equals(FQueue.of(2)) shouldBe true
    val (pop4F, reminder4F) = intQueueOfThree2F.fpopAndRemainder()
    pop4F shouldBe 3
    reminder4F.equals(FQueue.of(1, 2)) shouldBe true
    val (pop4Fa, reminder4Fa) = reminder4F.fpopAndRemainder()
    pop4Fa shouldBe 1
    reminder4Fa.equals(FQueue.of(2)) shouldBe true
  }

  test("fsize") {
    intQueueOfNone.fsize() shouldBe 0
    intQueueOfOne.fsize() shouldBe 1
    intQueueOfOneYR.fsize() shouldBe 1
    intQueueOfTwo.fsize() shouldBe 2
    intQueueOfTwoYR.fsize() shouldBe 2
    intQueueOfThree2B.fsize() shouldBe 3
    intQueueOfThree2F.fsize() shouldBe 3
  }

  test("fisNested") {
    intQueueOfNone.fisNested() shouldBe null
    intQueueOfOne.fisNested() shouldBe false
    intQueueOfOneYR.fisNested() shouldBe false
    intQueueOfTwo.fisNested() shouldBe false
    intQueueOfTwoYR.fisNested() shouldBe false
    FQueue.of(intQueueOfNone).fisNested() shouldBe true
    FQueue.of(emptyArray<Int>()).fisNested() shouldBe true
    FQueue.of(setOf<Int>()).fisNested() shouldBe true
    FQueue.of(*arrayOf(listOf<Int>())).fisNested() shouldBe true
    FQueue.of(mapOf<Int, Int>()).fisNested() shouldBe true
  }
})
