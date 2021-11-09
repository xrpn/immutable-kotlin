package com.xrpn.immutable.fqueuetest

import com.xrpn.immutable.*
import com.xrpn.immutable.FQueue.Companion.emptyIMQueue
import com.xrpn.immutable.emptyArrayOfInt
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intQueueOfNoneNR = FQueue.of(*emptyArrayOfInt)
private val intQueueOfOne1NR = FQueue.of(*arrayOf<Int>(1))
private val intQueueOfOne2NR = FQueue.of(*arrayOf<Int>(2))
private val intQueueOfOne3NR = FQueue.of(*arrayOf<Int>(3))
private val intQueueOfTwoNR = FQueue.of(*arrayOf<Int>(1, 2))
private val intQueueOfTworNR = FQueue.of(*arrayOf<Int>(2, 1))
private val intQueueOfTwo31NR = FQueue.of(*arrayOf<Int>(3, 1))
private val intQueueOfThreeNR = FQueue.of(*arrayOf<Int>(3, 1, 2))
private val intQueueOfNoneYR = FQueue.of(*emptyArrayOfInt, readyToDequeue = true)
private val intQueueOfOne1YR = FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)
private val intQueueOfOne2YR = FQueue.of(*arrayOf<Int>(2), readyToDequeue = true)
private val intQueueOfOne3YR = FQueue.of(*arrayOf<Int>(3), readyToDequeue = true)
private val intQueueOfTwoYR = FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)
private val intQueueOfTworYR = FQueue.of(*arrayOf<Int>(2, 1), readyToDequeue = true)
private val intQueueOfTwo31YR = FQueue.of(*arrayOf<Int>(3, 1), readyToDequeue = true)
private val intQueueOfThreeYR = FQueue.of(*arrayOf<Int>(3, 1, 2), readyToDequeue = true)
private val intQueueOfThree2B = FQueueBody.of(FLCons(3, FLNil), FLCons(2,FLCons(1, FLNil)))
private val intQueueOfThree2F = FQueueBody.of(FLCons(3, FLCons(1, FLNil)), FLCons(2,FLNil))
private val intQueueOfTwoAF = FQueueBody.of(FLCons(1, FLNil), FLCons(2,FLNil))
private val intQueueOfTwoBF = FQueueBody.of(FLCons(3, FLNil), FLCons(1,FLNil))

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
    intQueueOfTwoNR.fdiscardFront().fqStructuralEqual(intQueueOfOne2YR) shouldBe true
    intQueueOfThreeNR.fdiscardFront().fqStrongEqual(intQueueOfTwoNR) shouldBe false
    intQueueOfThreeNR.fdiscardFront().fqStrongEqual(intQueueOfTwoYR) shouldBe true
    intQueueOfThreeNR.fdiscardFront().fqStructuralEqual(intQueueOfTwoNR) shouldBe true
    intQueueOfThreeNR.fdiscardFront().fqStructuralEqual(intQueueOfTwoYR) shouldBe true
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
    intQueueOfThree2F.fdropFrontWhile { it == 3 }.fqStrongEqual(intQueueOfTwoAF)
    intQueueOfThree2B.fdropFrontWhile { false }.fqStrongEqual(intQueueOfThree2B) shouldBe true
    (intQueueOfThree2B.fdropFrontWhile { true } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfThree2B.fdropFrontWhile { it != 2 }.fqStrongEqual(intQueueOfOne2YR)
    intQueueOfThree2B.fdropFrontWhile { it == 3 }.fqStrongEqual(intQueueOfTwoNR)
  }

  test("fdropFrontWhen (not ready)"){
    (intQueueOfNoneNR.fdropFrontWhen { false } === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfNoneNR.fdropFrontWhen { true } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1NR.fdropFrontWhen { false } shouldBe intQueueOfOne1NR
    (intQueueOfOne1NR.fdropFrontWhen { true }  === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1NR.fdropFrontWhen { it == 1 } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1NR.fdropFrontWhen { it < 1 } shouldBe intQueueOfOne1NR
    intQueueOfTwoNR.fdropFrontWhen { it < 2 }.fqStrongEqual(intQueueOfOne2YR) shouldBe true
    intQueueOfTwoNR.fdropFrontWhen { 1 < it }.fqStrongEqual(intQueueOfTwoNR) shouldBe true
    intQueueOfTworNR.fdropFrontWhen { 1 < it }.fqStrongEqual(intQueueOfOne1YR) shouldBe true
    intQueueOfThreeNR.fdropFrontWhen { false } shouldBe intQueueOfThreeNR
    intQueueOfThreeNR.fdropFrontWhen { it != 2 }.fqStrongEqual(intQueueOfTwoYR)
  }
  
  test("fdropFrontWhen (ready)"){
    (intQueueOfNoneYR.fdropFrontWhen { false } === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfNoneYR.fdropFrontWhen { true } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1YR.fdropFrontWhen { false } shouldBe intQueueOfOne1NR
    (intQueueOfOne1YR.fdropFrontWhen { true }  === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1YR.fdropFrontWhen { it == 1 } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1YR.fdropFrontWhen { it < 1 } shouldBe intQueueOfOne1NR
    intQueueOfTwoYR.fdropFrontWhen { it < 2 }.fqStrongEqual(intQueueOfOne2YR) shouldBe true
    intQueueOfTwoYR.fdropFrontWhen { 1 < it }.fqStrongEqual(intQueueOfTwoYR) shouldBe true
    intQueueOfTworYR.fdropFrontWhen { 1 < it }.fqStrongEqual(intQueueOfOne1YR) shouldBe true
    intQueueOfThreeYR.fdropFrontWhen { false } shouldBe intQueueOfThreeNR
    intQueueOfThreeYR.fdropFrontWhen { it != 2 }.fqStrongEqual(intQueueOfTwoYR)
  }

  test("fdropIfFront (not ready)"){
    (intQueueOfNoneNR.fdropIfFront(0) === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1NR.fdropIfFront(1) === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1NR.fdropIfFront(2) === intQueueOfOne1NR) shouldBe true
    intQueueOfTwoNR.fdropIfFront(2).fqStrongEqual(intQueueOfTwoNR) shouldBe true
    intQueueOfTworNR.fdropIfFront(2).fqStrongEqual(intQueueOfOne1YR) shouldBe true
    intQueueOfThreeNR.fdropIfFront(3).fqStrongEqual(intQueueOfTwoYR) shouldBe true
  }
  
  test("fdropIfFront (ready)"){
    (intQueueOfNoneYR.fdropIfFront(0) === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1YR.fdropIfFront(1) === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1YR.fdropIfFront(2) === intQueueOfOne1YR) shouldBe true
    intQueueOfTwoYR.fdropIfFront(2).fqStrongEqual(intQueueOfTwoYR) shouldBe true
    intQueueOfTworYR.fdropIfFront(2).fqStrongEqual(intQueueOfOne1YR) shouldBe true
    intQueueOfThreeYR.fdropIfFront(3).fqStrongEqual(intQueueOfTwoYR) shouldBe true
  }

  test("fdropIfFront") {
    intQueueOfTwoAF.fdropIfFront(2).fqStrongEqual(intQueueOfTwoAF) shouldBe true
    intQueueOfTwoAF.fdropIfFront(1).fqStrongEqual(intQueueOfOne2NR) shouldBe true
    intQueueOfThree2F.fdropIfFront(3).fqStrongEqual(intQueueOfTwoAF) shouldBe true
    intQueueOfThree2B.fdropIfFront(3).fqStrongEqual(intQueueOfTwoNR) shouldBe true
  }

  test("ffrontMatch (not ready"){
    intQueueOfTwoNR.fqFrontMatch { it < 2 } shouldBe true
    intQueueOfNoneNR.fqFrontMatch { false } shouldBe false
    intQueueOfNoneNR.fqFrontMatch { true } shouldBe false
    intQueueOfOne1NR.fqFrontMatch { false } shouldBe false
    intQueueOfOne1NR.fqFrontMatch { true } shouldBe true
    intQueueOfOne1NR.fqFrontMatch { it == 1 } shouldBe true
    intQueueOfOne1NR.fqFrontMatch { it < 1 } shouldBe false
    intQueueOfTwoNR.fqFrontMatch { it < 2 } shouldBe true
    intQueueOfTwoNR.fqFrontMatch { 1 < it } shouldBe false
    intQueueOfTworNR.fqFrontMatch { 1 < it } shouldBe true
    intQueueOfThreeNR.fqFrontMatch { false } shouldBe false
    intQueueOfThreeNR.fqFrontMatch { it != 2 } shouldBe true
  }

  test("ffrontMatch (ready)"){
    intQueueOfNoneYR.fqFrontMatch { false } shouldBe false
    intQueueOfNoneYR.fqFrontMatch { true } shouldBe false
    intQueueOfOne1YR.fqFrontMatch { false } shouldBe false
    intQueueOfOne1YR.fqFrontMatch { true } shouldBe true
    intQueueOfOne1YR.fqFrontMatch { it == 1 } shouldBe true
    intQueueOfOne1YR.fqFrontMatch { it < 1 } shouldBe false
    intQueueOfTwoYR.fqFrontMatch { it < 2 } shouldBe true
    intQueueOfTwoYR.fqFrontMatch { 1 < it } shouldBe false
    intQueueOfTworYR.fqFrontMatch { 1 < it } shouldBe true
    intQueueOfThreeYR.fqFrontMatch { false } shouldBe false
    intQueueOfThreeYR.fqFrontMatch { it != 2 } shouldBe true
  }

  test("fdiscardBack (not ready)"){
    (intQueueOfNoneNR.fdiscardBack() === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1NR.fdiscardBack() === emptyIMQueue<Int>()) shouldBe true
    intQueueOfTwoNR.fdiscardBack().fqStrongEqual(intQueueOfOne1NR) shouldBe true
    intQueueOfTwoNR.fdiscardBack().fqStrongEqual(intQueueOfOne1YR) shouldBe false
    intQueueOfTwoNR.fdiscardBack().fqStructuralEqual(intQueueOfOne1NR) shouldBe true
    intQueueOfTwoNR.fdiscardBack().fqStructuralEqual(intQueueOfOne1YR) shouldBe true
    intQueueOfThreeNR.fdiscardBack().fqStrongEqual(intQueueOfTwo31NR) shouldBe true
    intQueueOfThreeNR.fdiscardBack().fqStrongEqual(intQueueOfTwo31YR) shouldBe false
    intQueueOfThreeNR.fdiscardBack().fqStructuralEqual(intQueueOfTwo31NR) shouldBe true
    intQueueOfThreeNR.fdiscardBack().fqStructuralEqual(intQueueOfTwo31YR) shouldBe true
  }

  test("fdiscardBack (ready)"){
    (intQueueOfNoneYR.fdiscardBack() === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1YR.fdiscardBack() === emptyIMQueue<Int>()) shouldBe true
    intQueueOfTwoYR.fdiscardBack().fqStrongEqual(intQueueOfOne1NR) shouldBe true
    intQueueOfTwoYR.fdiscardBack().fqStrongEqual(intQueueOfOne1YR) shouldBe false
    intQueueOfTwoYR.fdiscardBack().fqStructuralEqual(intQueueOfOne1NR) shouldBe true
    intQueueOfTwoYR.fdiscardBack().fqStructuralEqual(intQueueOfOne1YR) shouldBe true
    intQueueOfThreeYR.fdiscardBack().fqStrongEqual(intQueueOfTwo31NR) shouldBe true
    intQueueOfThreeYR.fdiscardBack().fqStrongEqual(intQueueOfTwo31YR) shouldBe false
    intQueueOfThreeYR.fdiscardBack().fqStructuralEqual(intQueueOfTwo31NR) shouldBe true
    intQueueOfThreeYR.fdiscardBack().fqStructuralEqual(intQueueOfTwo31YR) shouldBe true
  }

  test("fdropBack (not ready)"){
    (intQueueOfNoneNR.fdropBack(0) === intQueueOfNoneNR.fdiscardBack()) shouldBe true
    (intQueueOfOne1NR.fdropBack(1) === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfTwoNR.fdropBack(0) === intQueueOfTwoNR) shouldBe true
    (intQueueOfTwoNR.fdropBack(1) === intQueueOfTwoNR.fdiscardBack()) shouldBe false
    intQueueOfTwoNR.fdropBack(1).fqStrongEqual(intQueueOfTwoNR.fdiscardBack()) shouldBe true
    intQueueOfTwoNR.fdropBack(1).fqStrongEqual(intQueueOfTwoYR.fdiscardBack()) shouldBe true
    (intQueueOfTwoNR.fdropBack(2) === emptyIMQueue<Int>()) shouldBe true
    intQueueOfThreeNR.fdropBack(0) shouldBe intQueueOfThreeNR
    intQueueOfThreeNR.fdropBack(1).fqStrongEqual(intQueueOfTwo31NR) shouldBe true
    intQueueOfThreeNR.fdropBack(2).fqStrongEqual(intQueueOfOne3NR)
    intQueueOfThreeNR.fdropBack(3) shouldBe emptyIMQueue()
  }
  
  test("fdropBack (ready)"){
    (intQueueOfNoneYR.fdropBack(0) === intQueueOfNoneYR.fdiscardBack()) shouldBe true
    (intQueueOfOne1YR.fdropBack(1) === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfTwoYR.fdropBack(0) === intQueueOfTwoYR) shouldBe true
    (intQueueOfTwoYR.fdropBack(1) === intQueueOfTwoYR.fdiscardBack()) shouldBe false
    intQueueOfTwoYR.fdropBack(1).fqStrongEqual(intQueueOfTwoYR.fdiscardBack()) shouldBe true
    intQueueOfTwoYR.fdropBack(1).fqStrongEqual(intQueueOfTwoNR.fdiscardBack()) shouldBe true
    (intQueueOfTwoYR.fdropBack(2) === emptyIMQueue<Int>()) shouldBe true
    intQueueOfThreeYR.fdropBack(0) shouldBe intQueueOfThreeNR
    intQueueOfThreeYR.fdropBack(1).fqStrongEqual(intQueueOfTwo31NR) shouldBe true
    intQueueOfThreeYR.fdropBack(2).fqStrongEqual(intQueueOfOne3NR)
    intQueueOfThreeYR.fdropBack(3) shouldBe emptyIMQueue()
  }
  
  test("fdropBack"){
    intQueueOfThree2F.fdropBack(1).fqStrongEqual(intQueueOfTwo31NR) shouldBe false
    intQueueOfThree2F.fdropBack(1).fqStrongEqual(intQueueOfTwo31YR) shouldBe true
    intQueueOfThree2F.fdropBack(1).fqStructuralEqual(intQueueOfTwo31NR) shouldBe true
    intQueueOfThree2F.fdropBack(2).fqStrongEqual(intQueueOfOne3NR) shouldBe true
    (intQueueOfThree2F.fdropBack(3) === emptyIMQueue<Int>()) shouldBe true
    intQueueOfThree2B.fdropBack(1).fqStrongEqual(intQueueOfTwoBF) shouldBe true
    intQueueOfThree2B.fdropBack(1).fqStructuralEqual(intQueueOfTwo31YR) shouldBe false
    intQueueOfThree2B.fdropBack(1).fqStructuralEqual(intQueueOfTwo31YR) shouldBe false
    intQueueOfThree2B.fdropBack(1).equal(intQueueOfTwo31YR) shouldBe true
    intQueueOfThree2B.fdropBack(1).equal(intQueueOfTwo31NR) shouldBe true
    intQueueOfThree2B.fdropBack(2).fqStrongEqual(intQueueOfOne3YR) shouldBe true
    (intQueueOfThree2B.fdropBack(3) === emptyIMQueue<Int>()) shouldBe true
  }
  
  test("fdropBackWhile (not ready)"){
    (intQueueOfNoneNR.fdropBackWhile { false } === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfNoneNR.fdropBackWhile { true } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1NR.fdropBackWhile { false } shouldBe intQueueOfOne1NR
    (intQueueOfOne1NR.fdropBackWhile { true }  === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1NR.fdropBackWhile { it == 1 } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1NR.fdropBackWhile { it < 1 } shouldBe intQueueOfOne1NR
    intQueueOfTwoNR.fdropBackWhile { it < 2 }.fqStrongEqual(intQueueOfTwoNR) shouldBe true
    intQueueOfTwoNR.fdropBackWhile { 1 < it }.fqStrongEqual(intQueueOfOne1NR) shouldBe true
    intQueueOfTworNR.fdropBackWhile { 1 < it }.fqStrongEqual(intQueueOfTworNR) shouldBe true
    intQueueOfThreeNR.fdropBackWhile { false } shouldBe intQueueOfThreeNR
    intQueueOfThreeNR.fdropBackWhile { it != 3 }.fqStrongEqual(intQueueOfOne3YR)
  }

  test("fdropBackWhile (ready)"){
    (intQueueOfNoneYR.fdropBackWhile { false } === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfNoneYR.fdropBackWhile { true } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1YR.fdropBackWhile { false } shouldBe intQueueOfOne1YR
    (intQueueOfOne1YR.fdropBackWhile { true }  === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1YR.fdropBackWhile { it == 1 } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1YR.fdropBackWhile { it < 1 } shouldBe intQueueOfOne1YR
    intQueueOfTwoYR.fdropBackWhile { it < 2 }.fqStrongEqual(intQueueOfTwoYR) shouldBe true
    intQueueOfTwoYR.fdropBackWhile { 1 < it }.fqStrongEqual(intQueueOfOne1NR) shouldBe true
    intQueueOfTworYR.fdropBackWhile { 1 < it }.fqStrongEqual(intQueueOfTworYR) shouldBe true
    intQueueOfThreeYR.fdropBackWhile { false } shouldBe intQueueOfThreeNR
    intQueueOfThreeYR.fdropBackWhile { it != 3 }.fqStrongEqual(intQueueOfOne3YR)
  }

  test("fdropBackWhile"){
    intQueueOfThree2F.fdropBackWhile { false }.fqStrongEqual(intQueueOfThree2F) shouldBe true
    (intQueueOfThree2F.fdropBackWhile { true } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfThree2F.fdropBackWhile { it != 3 }.fqStrongEqual(intQueueOfOne3NR)
    intQueueOfThree2F.fdropBackWhile { it == 2 }.fqStrongEqual(intQueueOfTwoBF)
    intQueueOfThree2B.fdropBackWhile { false }.fqStrongEqual(intQueueOfThree2B) shouldBe true
    (intQueueOfThree2B.fdropBackWhile { true } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfThree2B.fdropBackWhile { it == 2 }.fqStrongEqual(intQueueOfTwoBF)
    intQueueOfThree2B.fdropBackWhile { it != 3 }.fqStrongEqual(intQueueOfOne3YR)
  }

  test("fdropBackWhen (not ready)"){
    (intQueueOfNoneNR.fdropBackWhen { false } === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfNoneNR.fdropBackWhen { true } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1NR.fdropBackWhen { false } shouldBe intQueueOfOne1NR
    (intQueueOfOne1NR.fdropBackWhen { true }  === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1NR.fdropBackWhen { it == 1 } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1NR.fdropBackWhen { it < 1 } shouldBe intQueueOfOne1NR
    intQueueOfTwoNR.fdropBackWhen { it < 2 }.fqStrongEqual(intQueueOfTwoNR) shouldBe true
    intQueueOfTwoNR.fdropBackWhen { 1 < it }.fqStrongEqual(intQueueOfOne1NR) shouldBe true
    intQueueOfTworNR.fdropBackWhen { 1 < it }.fqStrongEqual(intQueueOfTworNR) shouldBe true
    intQueueOfThreeNR.fdropBackWhen { false } shouldBe intQueueOfThreeNR
    intQueueOfThreeNR.fdropBackWhen { it != 1 }.fqStrongEqual(intQueueOfTwo31YR)
  }

  test("fdropBackWhen (ready)"){
    (intQueueOfNoneYR.fdropBackWhen { false } === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfNoneYR.fdropBackWhen { true } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1YR.fdropBackWhen { false } shouldBe intQueueOfOne1YR
    (intQueueOfOne1YR.fdropBackWhen { true }  === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1YR.fdropBackWhen { it == 1 } === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1YR.fdropBackWhen { it < 1 } shouldBe intQueueOfOne1YR
    intQueueOfTwoYR.fdropBackWhen { it < 2 }.fqStrongEqual(intQueueOfTwoYR) shouldBe true
    intQueueOfTwoYR.fdropBackWhen { 1 < it }.fqStrongEqual(intQueueOfOne1NR) shouldBe true
    intQueueOfTworYR.fdropBackWhen { 1 < it }.fqStrongEqual(intQueueOfTworYR) shouldBe true
    intQueueOfThreeYR.fdropBackWhen { false } shouldBe intQueueOfThreeNR
    intQueueOfThreeYR.fdropBackWhen { it != 1 }.fqStrongEqual(intQueueOfTwo31YR)
  }

  test("fdropIfBack (not ready)") {
    (intQueueOfNoneNR.fdropIfBack(0) === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1NR.fdropIfBack(1) === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1NR.fdropIfBack(2) === intQueueOfOne1NR) shouldBe true
    intQueueOfTwoNR.fdropIfBack(2).fqStrongEqual(intQueueOfOne1NR) shouldBe true
    intQueueOfTworNR.fdropIfBack(2).fqStrongEqual(intQueueOfTworNR) shouldBe true
    intQueueOfThreeNR.fdropIfBack(2).fqStrongEqual(intQueueOfTwo31NR) shouldBe true
  }

  test("fdropIfBack (ready)") {
    (intQueueOfNoneYR.fdropIfBack(0) === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1YR.fdropIfBack(1) === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1YR.fdropIfBack(2) === intQueueOfOne1YR) shouldBe true
    intQueueOfTwoYR.fdropIfBack(2).fqStrongEqual(intQueueOfOne1NR) shouldBe true
    intQueueOfTworYR.fdropIfBack(2).fqStrongEqual(intQueueOfTworYR) shouldBe true
    intQueueOfThreeYR.fdropIfBack(2).fqStrongEqual(intQueueOfTwo31NR) shouldBe true
  }

  test("fdropIfBack") {
    intQueueOfTwoAF.fdropIfBack(2).fqStrongEqual(intQueueOfOne1YR) shouldBe true
    intQueueOfTwoAF.fdropIfBack(1).fqStrongEqual(intQueueOfTwoAF) shouldBe true
    intQueueOfThree2F.fdropIfBack(2).fqStrongEqual(intQueueOfTwo31YR) shouldBe true
    intQueueOfThree2B.fdropIfBack(2).fqStrongEqual(intQueueOfTwoBF) shouldBe true
  }

  test("fbackMatch (not ready)"){
    intQueueOfNoneNR.fqBackMatch { false } shouldBe false
    intQueueOfNoneNR.fqBackMatch { true } shouldBe false
    intQueueOfOne1NR.fqBackMatch { false } shouldBe false
    intQueueOfOne1NR.fqBackMatch { true } shouldBe true
    intQueueOfOne1NR.fqBackMatch { it == 1 } shouldBe true
    intQueueOfOne1NR.fqBackMatch { it < 1 } shouldBe false
    intQueueOfTwoNR.fqBackMatch { it < 2 } shouldBe false
    intQueueOfTwoNR.fqBackMatch { 1 < it } shouldBe true
    intQueueOfTworNR.fqBackMatch { 1 < it } shouldBe false
    intQueueOfThreeNR.fqBackMatch { false } shouldBe false
    intQueueOfThreeNR.fqBackMatch { it != 1 } shouldBe true
  }

  test("fbackMatch (ready)"){
    intQueueOfNoneYR.fqBackMatch { false } shouldBe false
    intQueueOfNoneYR.fqBackMatch { true } shouldBe false
    intQueueOfOne1YR.fqBackMatch { false } shouldBe false
    intQueueOfOne1YR.fqBackMatch { true } shouldBe true
    intQueueOfOne1YR.fqBackMatch { it == 1 } shouldBe true
    intQueueOfOne1YR.fqBackMatch { it < 1 } shouldBe false
    intQueueOfTwoYR.fqBackMatch { it < 2 } shouldBe false
    intQueueOfTwoYR.fqBackMatch { 1 < it } shouldBe true
    intQueueOfTworYR.fqBackMatch { 1 < it } shouldBe false
    intQueueOfThreeYR.fqBackMatch { false } shouldBe false
    intQueueOfThreeYR.fqBackMatch { it != 1 } shouldBe true
  }

  test("fempty") {
    intQueueOfNoneYR.fempty() shouldBe true
    intQueueOfNoneNR.fempty() shouldBe true
    intQueueOfOne1YR.fempty() shouldBe false
    intQueueOfOne1NR.fempty() shouldBe false
  }

  test("flast (not ready)"){
    intQueueOfNoneNR.flast() shouldBe null
    intQueueOfOne1NR.flast() shouldBe 1
    intQueueOfTworNR.flast() shouldBe 1
    intQueueOfThreeNR.flast() shouldBe 2
  }

  test("flast (ready)"){
    intQueueOfNoneYR.flast() shouldBe null
    intQueueOfOne1YR.flast() shouldBe 1
    intQueueOfTworYR.flast() shouldBe 1
    intQueueOfThreeYR.flast() shouldBe 2
  }

  test("flast"){
    intQueueOfThree2F.flast() shouldBe 2
    intQueueOfThree2B.flast() shouldBe 2
  }

  test("peek (not ready)") {
    intQueueOfNoneNR.fpeek() shouldBe null
    intQueueOfOne1NR.fpeek() shouldBe 1
    intQueueOfTwoNR.fpeek() shouldBe 1
    intQueueOfThreeNR.fpeek() shouldBe 3
  }

  test("peek (ready)") {
    intQueueOfNoneYR.fpeek() shouldBe null
    intQueueOfOne1YR.fpeek() shouldBe 1
    intQueueOfTworYR.fpeek() shouldBe 2
    intQueueOfThreeYR.fpeek() shouldBe 3
  }

  test("fpeek"){
    intQueueOfThree2F.fpeek() shouldBe 3
    intQueueOfThree2B.fpeek() shouldBe 3
  }

  test("fpeekOrThrow (not ready)"){
    shouldThrow<IllegalStateException> {
      intQueueOfNoneNR.ffirstOrThrow()
    }
    intQueueOfOne1NR.ffirstOrThrow() shouldBe 1
    intQueueOfTwoNR.ffirstOrThrow() shouldBe 1
    intQueueOfThreeNR.ffirstOrThrow() shouldBe 3
  }

})
