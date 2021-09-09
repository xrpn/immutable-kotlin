package com.xrpn.immutable

import com.xrpn.immutable.FQueue.Companion.emptyIMQueue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intQueueOfNoneNR = FQueue.of(*arrayOf<Int>())
private val intQueueOfOne1NR = FQueue.of(*arrayOf<Int>(1))
private val intQueueOfOne2NR = FQueue.of(*arrayOf<Int>(2))
private val intQueueOfTwoNR = FQueue.of(*arrayOf<Int>(1, 2))
private val intQueueOfTworNR = FQueue.of(*arrayOf<Int>(2, 1))
private val intQueueOfThreeNR = FQueue.of(*arrayOf<Int>(3, 1, 2))
private val intQueueOfNoneYR = FQueue.of(*arrayOf<Int>(), readyToDequeue = true)
private val intQueueOfOne1YR = FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)
private val intQueueOfOne2YR = FQueue.of(*arrayOf<Int>(2), readyToDequeue = true)
private val intQueueOfTwoYR = FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)
private val intQueueOfTworYR = FQueue.of(*arrayOf<Int>(2, 1), readyToDequeue = true)
private val intQueueOfThreeYR = FQueue.of(*arrayOf<Int>(3, 1, 2), readyToDequeue = true)
private val intQueueOfThree2B = FQueueBody.of(FLCons(3, FLNil), FLCons(2,FLCons(1, FLNil)))
private val intQueueOfThree2F = FQueueBody.of(FLCons(3, FLCons(1, FLNil)), FLCons(2,FLNil))
private val intQueueOfTwo1F = FQueueBody.of(FLCons(1, FLNil), FLCons(2,FLNil))

class FQueueFilteringTest : FunSpec({

  beforeTest {}

  test("data") {
    intQueueOfThree2F.equals(intQueueOfThree2B) shouldBe true
    intQueueOfThree2B.equals(intQueueOfThree2F) shouldBe true
    intQueueOfThreeYR.equals(intQueueOfThree2F) shouldBe true
    intQueueOfThree2F.equals(intQueueOfThreeNR) shouldBe true
    intQueueOfThreeYR.equals(intQueueOfThreeNR) shouldBe true
  }

  test("discardFront (not ready)") {
    (intQueueOfNoneNR.fdiscardFront() === FQueue.emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1NR.fdiscardFront() === FQueue.emptyIMQueue<Int>()) shouldBe true
    intQueueOfTwoNR.fdiscardFront().fqStrongEqual(intQueueOfOne2NR) shouldBe false
    intQueueOfTwoNR.fdiscardFront().fqStrongEqual(intQueueOfOne2YR) shouldBe true
    intQueueOfTwoNR.fdiscardFront().fqStructuralEqual(intQueueOfOne2NR) shouldBe true
    intQueueOfThreeNR.fdiscardFront().fqStrongEqual(intQueueOfTwoNR) shouldBe false
    intQueueOfThreeNR.fdiscardFront().fqStrongEqual(intQueueOfTwoYR) shouldBe true
    intQueueOfThreeNR.fdiscardFront().fqStructuralEqual(intQueueOfTwoNR) shouldBe true
  }

  test("discardFront (ready)") {
    (intQueueOfNoneYR.fdiscardFront() === FQueue.emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1YR.fdiscardFront() === FQueue.emptyIMQueue<Int>()) shouldBe true
    intQueueOfTwoYR.fdiscardFront().fqStrongEqual(intQueueOfOne2YR) shouldBe true
    intQueueOfTwoYR.fdiscardFront().fqStrongEqual(intQueueOfOne2NR) shouldBe false
    intQueueOfTwoYR.fdiscardFront().fqStructuralEqual(intQueueOfOne2NR) shouldBe true
    intQueueOfThreeYR.fdiscardFront().fqStrongEqual(intQueueOfTwoYR) shouldBe true
    intQueueOfThreeYR.fdiscardFront().fqStrongEqual(intQueueOfTwoNR) shouldBe false
    intQueueOfThreeYR.fdiscardFront().fqStructuralEqual(intQueueOfTwoNR) shouldBe true
  }

  test("dropFront (not ready)") {
    (intQueueOfNoneNR.fdropFront(0) === intQueueOfNoneNR.fdiscardFront()) shouldBe true
    (intQueueOfOne1NR.fdropFront(1) === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfTwoNR.fdropFront(0) === intQueueOfTwoNR) shouldBe true
    (intQueueOfTwoNR.fdropFront(1) === intQueueOfTwoNR.fdiscardFront()) shouldBe false
    intQueueOfTwoNR.fdropFront(1).fqStrongEqual(intQueueOfTwoNR.fdiscardFront()) shouldBe true
    (intQueueOfTwoNR.fdropFront(2) === emptyIMQueue<Int>()) shouldBe true
    intQueueOfThreeNR.fdropFront(0) shouldBe intQueueOfThreeNR
    intQueueOfThreeNR.fdropFront(1).fqStrongEqual(intQueueOfTwoYR) shouldBe true
    intQueueOfThreeNR.fdropFront(2) shouldBe intQueueOfOne2NR
    intQueueOfThreeNR.fdropFront(3) shouldBe FQueue.emptyIMQueue()
  }

  test("dropFront (ready)") {
    (intQueueOfNoneYR.fdropFront(0) === intQueueOfNoneNR.fdiscardFront()) shouldBe true
    (intQueueOfOne1YR.fdropFront(1) === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfTwoYR.fdropFront(0) === intQueueOfTwoYR) shouldBe true
    (intQueueOfTwoYR.fdropFront(1) === intQueueOfTwoYR.fdiscardFront()) shouldBe false
    intQueueOfTwoYR.fdropFront(1).fqStrongEqual(intQueueOfTwoNR.fdiscardFront()) shouldBe true
    (intQueueOfTwoYR.fdropFront(2) === emptyIMQueue<Int>()) shouldBe true
    intQueueOfThreeYR.fdropFront(0) shouldBe intQueueOfThreeNR
    intQueueOfThreeYR.fdropFront(1).fqStrongEqual(intQueueOfTwoYR) shouldBe true
    intQueueOfThreeYR.fdropFront(2) shouldBe intQueueOfOne2NR
    intQueueOfThreeYR.fdropFront(3) shouldBe FQueue.emptyIMQueue()
  }

  test("dropFront") {
    intQueueOfThree2F.fdropFront(1).fqStrongEqual(intQueueOfTwoYR) shouldBe false
    intQueueOfThree2F.fdropFront(1).fqStructuralEqual(intQueueOfTwoYR) shouldBe false
    intQueueOfThree2F.fdropFront(1).equal(intQueueOfTwoYR) shouldBe true
    intQueueOfThree2F.fdropFront(2).fqStrongEqual(intQueueOfOne2NR) shouldBe true
    (intQueueOfThree2F.fdropFront(3) === emptyIMQueue<Int>()) shouldBe true
    intQueueOfThree2B.fdropFront(1).fqStrongEqual(intQueueOfTwoNR) shouldBe true
    intQueueOfThree2B.fdropFront(1).equal(intQueueOfTwoYR) shouldBe true
    intQueueOfThree2B.fdropFront(2).fqStrongEqual(intQueueOfOne2YR) shouldBe true
    (intQueueOfThree2B.fdropFront(3) === emptyIMQueue<Int>()) shouldBe true
  }

  test("fdropFrontWhile (not ready)"){
    (intQueueOfNoneNR.fdropFrontWhile { false } === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfNoneNR.fdropFrontWhile { true } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1NR.fdropFrontWhile { false } shouldBe intQueueOfOne1NR
    (intQueueOfOne1NR.fdropFrontWhile { true }  === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1NR.fdropFrontWhile { it == 1 } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1NR.fdropFrontWhile { it < 1 } shouldBe intQueueOfOne1NR
    intQueueOfTwoNR.fdropFrontWhile { it < 2 }.fqStrongEqual(intQueueOfOne2YR) shouldBe true
    intQueueOfTwoNR.fdropFrontWhile { 1 < it }.fqStrongEqual(intQueueOfTwoNR) shouldBe true
    intQueueOfTworNR.fdropFrontWhile { 1 < it }.fqStrongEqual(intQueueOfOne1YR) shouldBe true
    intQueueOfThreeNR.fdropFrontWhile { false } shouldBe intQueueOfThreeNR
    intQueueOfThreeNR.fdropFrontWhile { it != 2 }.fqStrongEqual(intQueueOfOne2YR)
  }

  test("fdropFrontWhile (ready)"){
    (intQueueOfNoneYR.fdropFrontWhile { false } === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfNoneYR.fdropFrontWhile { true } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1YR.fdropFrontWhile { false } shouldBe intQueueOfOne1NR
    (intQueueOfOne1YR.fdropFrontWhile { true }  === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1YR.fdropFrontWhile { it == 1 } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1YR.fdropFrontWhile { it < 1 } shouldBe intQueueOfOne1NR
    intQueueOfTwoYR.fdropFrontWhile { it < 2 }.fqStrongEqual(intQueueOfOne2YR) shouldBe true
    intQueueOfTwoYR.fdropFrontWhile { 1 < it }.fqStrongEqual(intQueueOfTwoYR) shouldBe true
    intQueueOfTworYR.fdropFrontWhile { 1 < it }.fqStrongEqual(intQueueOfOne1YR) shouldBe true
    intQueueOfThreeYR.fdropFrontWhile { false } shouldBe intQueueOfThreeNR
    intQueueOfThreeYR.fdropFrontWhile { it != 2 }.fqStrongEqual(intQueueOfOne2YR)
  }

  test("fdropFrontWhile"){
    intQueueOfThree2F.fdropFrontWhile { false }.fqStrongEqual(intQueueOfThree2F) shouldBe true
    (intQueueOfThree2F.fdropFrontWhile { true } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfThree2F.fdropFrontWhile { it != 2 }.fqStrongEqual(intQueueOfOne2YR)
    intQueueOfThree2F.fdropFrontWhile { it == 3 }.fqStrongEqual(intQueueOfTwo1F)
    intQueueOfThree2B.fdropFrontWhile { false }.fqStrongEqual(intQueueOfThree2B) shouldBe true
    (intQueueOfThree2B.fdropFrontWhile { true } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfThree2B.fdropFrontWhile { it != 2 }.fqStrongEqual(intQueueOfOne2YR)
    intQueueOfThree2B.fdropFrontWhile { it == 3 }.fqStrongEqual(intQueueOfTwoNR)
  }

  test("fdropFrontWhen"){}
  test("fdropIfFront"){}
  test("ffrontMatch"){}
  test("fdiscardBack"){}
  test("fdropBack"){}
  test("fdropBackWhen"){}
  test("fdropBackWhile"){}
  test("fdropIfBack"){}
  test("fbackMatch"){}
  test("fempty"){}
  test("flast"){}

  test("peek (ready)") {
    intQueueOfNoneYR.fpeek() shouldBe null
    intQueueOfOne1YR.fpeek() shouldBe 1
    intQueueOfTworYR.fpeek() shouldBe 2
    intQueueOfThreeYR.fpeek() shouldBe 3
  }

  test("peek (not ready)") {
    intQueueOfNoneNR.fpeek() shouldBe null
    intQueueOfOne1NR.fpeek() shouldBe 1
    intQueueOfTwoNR.fpeek() shouldBe 1
    intQueueOfThreeNR.fpeek() shouldBe 3
  }

  test("fpeekOrThrow"){}

})
