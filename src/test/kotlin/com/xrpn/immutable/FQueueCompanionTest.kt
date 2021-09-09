package com.xrpn.immutable

import com.xrpn.imapi.IMList
import com.xrpn.immutable.FQueue.Companion.emptyIMQueue
import com.xrpn.immutable.FQueue.Companion.ofMap
import com.xrpn.immutable.FQueue.Companion.of
import com.xrpn.immutable.FQueue.Companion.toIMQueue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldStartWith

private val intQueueOfNoneNR = FQueue.of(*arrayOf<Int>())
private val strQueueOfNoneNR = FQueue.of(*arrayOf<String>())
private val intQueueOfOne1NR = FQueue.of(*arrayOf<Int>(1))
private val strQueueOfOneNR = FQueue.of(*arrayOf<String>("1"))
private val intQueueOfOne2NR = FQueue.of(*arrayOf<Int>(2))
private val intQueueOfTwoNR = FQueue.of(*arrayOf<Int>(1, 2))
private val intQueueOfTworNR = FQueue.of(*arrayOf<Int>(2, 1))
private val intQueueOfThreeNR = FQueue.of(*arrayOf<Int>(3, 1, 2))
private val intQueueOfThreesNR = FQueue.of(*arrayOf<Int>(1, 2, 3))
private val intQueueOfNoneYR = FQueue.of(*arrayOf<Int>(), readyToDequeue = true)
private val intQueueOfOne1YR = FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)
private val strQueueOfOneYR = FQueue.of(*arrayOf<String>("1"), readyToDequeue = true)
private val intQueueOfOne2YR = FQueue.of(*arrayOf<Int>(2), readyToDequeue = true)
private val intQueueOfTwoYR = FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)
private val intQueueOfTworYR = FQueue.of(*arrayOf<Int>(2, 1), readyToDequeue = true)
private val intQueueOfThreeYR = FQueue.of(*arrayOf<Int>(3, 1, 2), readyToDequeue = true)
private val intQueueOfThreesYR = FQueue.of(*arrayOf<Int>(1, 2, 3), readyToDequeue = true)
private val intQueueOfThree2B = FQueueBody.of(FLCons(3, FLNil), FLCons(2,FLCons(1, FLNil)))
private val intQueueOfThree2F = FQueueBody.of(FLCons(3, FLCons(1, FLNil)), FLCons(2,FLNil))

class FQueueCompanionTest : FunSpec({

  beforeTest {}

  test("equals") {
    intQueueOfNoneNR.equals(intQueueOfNoneNR) shouldBe true
    intQueueOfNoneYR.equals(intQueueOfNoneNR) shouldBe true
    intQueueOfNoneNR.equals(null) shouldBe false
    intQueueOfNoneYR.equals(null) shouldBe false
    intQueueOfOne1NR.equals(null) shouldBe false
    intQueueOfOne1YR.equals(null) shouldBe false
    intQueueOfOne1YR.equals(intQueueOfOne1NR) shouldBe true
    strQueueOfOneYR.equals(strQueueOfOneNR) shouldBe true
    strQueueOfOneYR.equals(intQueueOfOne1NR) shouldBe false
    strQueueOfOneYR.equals(intQueueOfOne1YR) shouldBe false
    /* sigh */ intQueueOfNoneNR.equals(strQueueOfNoneNR) shouldBe true
    intQueueOfOne1YR.equals(1) shouldBe false
    strQueueOfOneYR.equals("1") shouldBe false
    1.equals(intQueueOfOne1YR) shouldBe false
    "1".equals(strQueueOfOneYR) shouldBe false
  }

  test("hashCode") {
    intQueueOfNoneYR.hashCode() shouldBe intQueueOfNoneNR.hashCode()
    strQueueOfOneYR.hashCode() shouldBe strQueueOfOneNR.hashCode()
    intQueueOfOne1YR.hashCode() shouldBe intQueueOfOne1NR.hashCode()
    intQueueOfOne2YR.hashCode() shouldBe intQueueOfOne2NR.hashCode()
    intQueueOfTwoYR.hashCode() shouldBe intQueueOfTwoNR.hashCode()
    intQueueOfTwoYR.hashCode() shouldNotBe intQueueOfTworYR.hashCode()
    intQueueOfTwoYR.hashCode() shouldNotBe intQueueOfTworNR.hashCode()
    intQueueOfThreeYR.hashCode() shouldBe intQueueOfThreeNR.hashCode()
    intQueueOfThreeYR.hashCode() shouldBe intQueueOfThree2F.hashCode()
    intQueueOfThreeYR.hashCode() shouldBe intQueueOfThree2B.hashCode()
  }

  test("toString() hashCode()") {
    emptyIMQueue<Int>().toString() shouldBe "FQueue(*)"
    val aux = emptyIMQueue<Int>().hashCode()
    for (i in (1..100)) {
      aux shouldBe emptyIMQueue<Int>().hashCode()
    }
    intQueueOfThree2B.toString() shouldStartWith "${FQueueBody::class.simpleName}{ "
    val aux2 = intQueueOfThree2B.hashCode()
    for (i in (1..100)) {
      aux2 shouldBe intQueueOfThree2B.hashCode()
    }
    for (i in (1..100)) {
      FQueueBody.hashCode(intQueueOfThree2B as FQueueBody<Int>) shouldBe aux2
    }
  }

  // IMQueueCompanion

  test("co.emptyIMQueue"){
    (emptyIMQueue<Int>() === FQueueBody.empty) shouldBe true
  }

  test("co.of vararg (not ready)") {
    of(*arrayOf<Int>()) shouldBe emptyIMQueue()
    of(1) shouldBe FQueueBody.of(FLNil, FLCons(1, FLNil))
    of(1, 2) shouldBe FQueueBody.of(FLNil, FLCons(2, FLCons(1, FLNil)))
  }

  test("co.of vararg (ready)") {
    of(*arrayOf<Int>(), readyToDequeue = true) shouldBe emptyIMQueue()
    of(1, readyToDequeue = true) shouldBe FQueueBody.of(FLCons(1, FLNil), FLNil)
    of(1, 2, readyToDequeue = true) shouldBe FQueueBody.of(FLCons(1, FLCons(2, FLNil)), FLNil)
  }

  test("co.of varargs") {
    intQueueOfNoneNR shouldBe emptyIMQueue<Int>()
    intQueueOfOne1YR shouldBe FQueueBody.of(FLCons(1, FLNil), FLNil)
    intQueueOfTwoYR shouldBe FQueueBody.of(FLCons(1, FLCons(2, FLNil)), FLNil)
    intQueueOfThreesYR shouldBe FQueueBody.of(FLCons(1, FLCons(2, FLCons(3, FLNil))), FLNil)
    intQueueOfOne1NR shouldBe FQueueBody.of(FLNil, FLCons(1, FLNil))
    intQueueOfTwoNR shouldBe FQueueBody.of(FLNil, FLCons(2, FLCons(1, FLNil)))
    intQueueOfThreesNR shouldBe FQueueBody.of(FLNil, FLCons(3, FLCons(2, FLCons(1, FLNil))))
  }

  test("co.of Iterator (not ready)") {
    of(emptyList<Int>().iterator()) shouldBe emptyIMQueue()
    of(listOf(1).iterator()) shouldBe FQueueBody.of(FLNil, FLCons(1, FLNil))
    of(listOf(1, 2).iterator()) shouldBe FQueueBody.of(FLNil, FLCons(2, FLCons(1, FLNil)))
  }

  test("co.of Iterator (ready)") {
    of(emptyList<Int>().iterator(), readyToDequeue = true) shouldBe emptyIMQueue()
    of(listOf(1).iterator(), readyToDequeue = true) shouldBe FQueueBody.of(FLNil, FLCons(1, FLNil))
    of(listOf(1, 2).iterator(), readyToDequeue = true) shouldBe FQueueBody.of(FLNil, FLCons(2, FLCons(1, FLNil)))
  }

  test("co.of List") {
    of(emptyList<Int>()) shouldBe emptyIMQueue()
    of(listOf(1)) shouldBe FQueueBody.of(FLNil, FLCons(1, FLNil))
    of(listOf(1, 2)) shouldBe FQueueBody.of(FLNil, FLCons(2, FLCons(1, FLNil)))
  }

  test("co.of IMList") {
    of(FList.emptyIMList<Int>() as IMList<Int>) shouldBe emptyIMQueue()
    of(FList.of(1) as IMList<Int>) shouldBe FQueueBody.of(FLNil, FLCons(1, FLNil))
    of(FList.of(1, 2) as IMList<Int>) shouldBe FQueueBody.of(FLNil, FLCons(2, FLCons(1, FLNil)))
  }

  test("co.ofMap Iterator (not ready)") {
    ofMap(emptyList<Int>().iterator(), readyToDequeue = false){ item -> item+10 } shouldBe emptyIMQueue()
    ofMap(listOf(1).iterator(), readyToDequeue = false){ item -> item+10 } shouldBe FQueueBody.of(FLNil, FLCons(11, FLNil))
    ofMap(listOf(1, 2).iterator(), readyToDequeue = false){ item -> item+10 } shouldBe FQueueBody.of(FLNil, FLCons(12, FLCons(11, FLNil)))
  }

  test("co.ofMap Iterator (ready)") {
    ofMap(emptyList<Int>().iterator(), readyToDequeue = false){ item -> item+10 } shouldBe emptyIMQueue()
    ofMap(listOf(1).iterator(), readyToDequeue = false){ item -> item+10 } shouldBe FQueueBody.of(FLNil, FLCons(11, FLNil))
    ofMap(listOf(1, 2).iterator(), readyToDequeue = false){ item -> item+10 } shouldBe FQueueBody.of(FLCons(11, FLCons(12, FLNil)), FLNil)
  }

  test("co.ofMap List") {
    ofMap(emptyList<Int>()){ item -> item+10 } shouldBe emptyIMQueue()
    ofMap(listOf(1)){ item -> item+10 } shouldBe FQueueBody.of(FLCons(11, FLNil), FLNil)
    ofMap(listOf(1, 2)){ item -> item+10 } shouldBe FQueueBody.of(FLCons(11, FLCons(12, FLNil)), FLNil)
  }

  test("co.Collection.toIMQueue()") {
    setOf(1).toIMQueue() shouldBe FQueueBody.of(FLNil, FLCons(1, FLNil))
    setOf(1, 2).toIMQueue() shouldBe FQueueBody.of(FLNil, FLCons(2, FLCons(1, FLNil)))
  }

  // implementation

  test("forceFront") {
    emptyIMQueue<Int>().fqForceFront() shouldBe emptyIMQueue<Int>()
    emptyIMQueue<Int>().fqForceFront(merge = true) shouldBe emptyIMQueue<Int>()
    intQueueOfOne1YR.fqForceFront() shouldBe intQueueOfOne1YR
    intQueueOfTwoYR.fqForceFront() shouldBe intQueueOfTwoYR
    intQueueOfOne1YR.fqForceFront(merge = true) shouldBe intQueueOfOne1YR
    intQueueOfTwoYR.fqForceFront(merge = true) shouldBe intQueueOfTwoYR
    intQueueOfOne1NR.fqForceFront() shouldBe intQueueOfOne1YR
    intQueueOfTwoNR.fqForceFront() shouldBe intQueueOfTwoYR
    intQueueOfOne1NR.fqForceFront(merge = true) shouldBe intQueueOfOne1YR
    intQueueOfTwoNR.fqForceFront(merge = true) shouldBe intQueueOfTwoYR
  }

  test("forceBack") {
    emptyIMQueue<Int>().fqForceBack() shouldBe emptyIMQueue<Int>()
    emptyIMQueue<Int>().fqForceBack(merge = true) shouldBe emptyIMQueue<Int>()
    intQueueOfOne1YR.fqForceBack() shouldBe intQueueOfOne1NR
    intQueueOfTwoYR.fqForceBack() shouldBe intQueueOfTwoNR
    intQueueOfOne1YR.fqForceBack(merge = true) shouldBe intQueueOfOne1NR
    intQueueOfTwoYR.fqForceBack(merge = true) shouldBe intQueueOfTwoNR
    intQueueOfOne1NR.fqForceBack() shouldBe intQueueOfOne1NR
    intQueueOfTwoNR.fqForceBack() shouldBe intQueueOfTwoNR
    intQueueOfOne1NR.fqForceBack(merge = true) shouldBe intQueueOfOne1NR
    intQueueOfTwoNR.fqForceBack(merge = true) shouldBe intQueueOfTwoNR
  }

})
