package com.xrpn.immutable.fqueuetest

import com.xrpn.immutable.*
import com.xrpn.immutable.FQueue.Companion.emptyIMQueue
import com.xrpn.immutable.emptyArrayOfInt
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import java.util.concurrent.atomic.AtomicInteger

private val intQueueOfNoneNR = FQueue.of(*emptyArrayOfInt)
private val intQueueOfOne1NR = FQueue.of(*arrayOf<Int>(1))
private val intQueueOfTwoNR = FQueue.of(*arrayOf<Int>(1, 2))
private val intQueueOfTworNR = FQueue.of(*arrayOf<Int>(2, 1))
private val intQueueOfThreeNR = FQueue.of(*arrayOf<Int>(3, 1, 2))
private val intQueueOfThreesNR = FQueue.of(*arrayOf<Int>(1, 2, 3))
private val intQueueOfThreesrNR = FQueue.of(*arrayOf<Int>(3, 2, 1))
private val intQueueOfNoneYR = FQueue.of(*emptyArrayOfInt, readyToDequeue = true)
private val intQueueOfOne1YR = FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)
private val intQueueOfOne2YR = FQueue.of(*arrayOf<Int>(2), readyToDequeue = true)
private val intQueueOfTwoYR = FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)
private val intQueueOfTwo23YR = FQueue.of(*arrayOf<Int>(2, 3), readyToDequeue = true)
private val intQueueOfThreeYR = FQueue.of(*arrayOf<Int>(3, 1, 2), readyToDequeue = true)
private val intQueueOfThreesYR = FQueue.of(*arrayOf<Int>(1, 2, 3), readyToDequeue = true)
private val intQueueOfThree2B = FQueueBody.of(FLCons(3, FLNil), FLCons(2,FLCons(1, FLNil)))
private val intQueueOfThreer2B = FQueueBody.of(FLCons(2,FLCons(1, FLNil)), FLCons(3, FLNil))
private val intQueueOfThree2F = FQueueBody.of(FLCons(3, FLCons(1, FLNil)), FLCons(2,FLNil))
private val intQueueOfThreer2F = FQueueBody.of(FLCons(2,FLNil), FLCons(3, FLCons(1, FLNil)))
private val intQueueOfTwoAF = FQueueBody.of(FLCons(1, FLNil), FLCons(2,FLNil))

class FQueueTest : FunSpec({

  val repeats = 50

  beforeTest {}

  // ======== grouping

  test("fsize") {
    intQueueOfNoneNR.fsize() shouldBe 0
    intQueueOfNoneYR.fsize() shouldBe 0
    intQueueOfOne1NR.fsize() shouldBe 1
    intQueueOfOne1YR.fsize() shouldBe 1
    intQueueOfTwoNR.fsize() shouldBe 2
    intQueueOfTwoYR.fsize() shouldBe 2
    intQueueOfTwoAF.fsize() shouldBe 2
    intQueueOfThreeYR.fsize() shouldBe 3
    intQueueOfThreeNR.fsize() shouldBe 3
    intQueueOfThree2F.fsize() shouldBe 3
    intQueueOfThree2B.fsize() shouldBe 3
  }

  test("fcount") {
    intQueueOfNoneNR.fcount { true } shouldBe 0
    intQueueOfNoneYR.fcount { false } shouldBe 0
    intQueueOfOne1NR.fcount { true } shouldBe 1
    intQueueOfOne1YR.fcount { false } shouldBe 0
    intQueueOfTwoNR.fcount { true } shouldBe 2
    intQueueOfTwoYR.fcount { false } shouldBe 0
    intQueueOfTwoYR.fcount { it == 2 } shouldBe 1
    intQueueOfThreeYR.fcount { it < 3 } shouldBe 2
    intQueueOfThreeNR.fcount { it < 3 } shouldBe 2
    intQueueOfThree2F.fcount { it < 3 } shouldBe 2
    intQueueOfThree2B.fcount { it < 3 } shouldBe 2
    intQueueOfThreeYR.fcount { 2 < it } shouldBe 1
    intQueueOfThreeNR.fcount { 2 < it } shouldBe 1
    intQueueOfThree2F.fcount { 2 < it } shouldBe 1
    intQueueOfThree2B.fcount { 2 < it } shouldBe 1
  }

  // ======== transforming

  test("fdequeueMap") {
    intQueueOfNoneYR.fdequeueMap { it + 10 } shouldBe Pair(null, emptyIMQueue<Int>())
    intQueueOfOne1YR.fdequeueMap { it + 10 } shouldBe Pair(11, emptyIMQueue<Int>())
    intQueueOfTwoYR.fdequeueMap { it + 10 } shouldBe Pair(11, intQueueOfOne2YR)
    intQueueOfThreeYR.fdequeueMap { it + 10 } shouldBe Pair(13, intQueueOfTwoYR)
    intQueueOfThreesYR.fdequeueMap { it + 10 } shouldBe Pair(11, intQueueOfTwo23YR)
    intQueueOfNoneNR.fdequeueMap { it + 10 } shouldBe Pair(null, emptyIMQueue<Int>())
    intQueueOfOne1NR.fdequeueMap { it + 10 } shouldBe Pair(11, emptyIMQueue<Int>())
    intQueueOfTwoNR.fdequeueMap { it + 10 } shouldBe Pair(11, intQueueOfOne2YR)
    intQueueOfThreeNR.fdequeueMap { it + 10 } shouldBe Pair(13, intQueueOfTwoYR)
    intQueueOfThreesNR.fdequeueMap { it + 10 } shouldBe Pair(11, intQueueOfTwo23YR)
  }

  test("fpeekMap") {
    intQueueOfNoneYR.fpeekMap { it + 10 } shouldBe null
    intQueueOfOne1YR.fpeekMap { it + 10 } shouldBe 11
    intQueueOfTwoYR.fpeekMap { it + 10 } shouldBe 11
    intQueueOfThreeYR.fpeekMap { it + 10 } shouldBe 13
    intQueueOfThreesYR.fpeekMap { it + 10 } shouldBe 11
    intQueueOfNoneNR.fpeekMap { it + 10 } shouldBe null
    intQueueOfOne1NR.fpeekMap { it + 10 } shouldBe 11
    intQueueOfTwoNR.fpeekMap { it + 10 } shouldBe 11
    intQueueOfThreeNR.fpeekMap { it + 10 } shouldBe 13
    intQueueOfThreesNR.fpeekMap { it + 10 } shouldBe 11
  }

  // ======== altering

  test("fdequeue (ready)") {
    intQueueOfNoneYR.fdequeue() shouldBe Pair(null, emptyIMQueue<Int>())
    intQueueOfOne1YR.fdequeue() shouldBe Pair(1, emptyIMQueue<Int>())
    intQueueOfTwoYR.fdequeue() shouldBe Pair(1, intQueueOfOne2YR)
    intQueueOfTwoYR.fdequeue() shouldBe Pair(intQueueOfTwoYR.ffirst(), intQueueOfTwoYR.fdiscardFront())
    intQueueOfThreeYR.fdequeue() shouldBe Pair(3, intQueueOfTwoYR)
    intQueueOfThreeYR.fdequeue() shouldBe Pair(intQueueOfThreeYR.ffirst(), intQueueOfThreeYR.fdiscardFront())
    intQueueOfThreesYR.fdequeue() shouldBe Pair(1, intQueueOfTwo23YR)
    intQueueOfThreesYR.fdequeue() shouldBe Pair(intQueueOfThreesYR.ffirst(), intQueueOfThreesYR.fdiscardFront())
  }

  test("fdequeue (not ready)") {
    intQueueOfNoneNR.fdequeue() shouldBe Pair(null, emptyIMQueue<Int>())
    intQueueOfOne1NR.fdequeueOrThrow() shouldBe Pair(1, emptyIMQueue<Int>())
    intQueueOfTwoNR.fdequeueOrThrow() shouldBe Pair(1, intQueueOfOne2YR)
    intQueueOfTwoYR.fdequeue() shouldBe Pair(intQueueOfTwoYR.ffirst(), intQueueOfTwoYR.fdiscardFront())
    intQueueOfThreeNR.fdequeueOrThrow() shouldBe  Pair(3, intQueueOfTwoYR)
    intQueueOfThreeNR.fdequeue() shouldBe Pair(intQueueOfThreeNR.ffirst(), intQueueOfThreeNR.fdiscardFront())
    intQueueOfThreesNR.fdequeue() shouldBe Pair(1, intQueueOfTwo23YR)
    intQueueOfThreesNR.fdequeue() shouldBe Pair(intQueueOfThreesNR.ffirst(), intQueueOfThreesNR.fdiscardFront())
  }

  test("dequeue repeat (ready)") {
    intQueueOfNoneYR.fdequeue() shouldBe Pair(null, emptyIMQueue<Int>())
    intQueueOfOne1YR.fdequeue() shouldBe Pair(1, emptyIMQueue<Int>())
    intQueueOfTwoYR.fdequeue() shouldBe Pair(1, intQueueOfOne2YR)
    intQueueOfThreeYR.fdequeue() shouldBe Pair(3, intQueueOfTwoYR)
    val (itemA, qA) = intQueueOfThreeYR.fdequeue()
    itemA shouldBe 3
    val (itemB, qB) = qA.fdequeue()
    itemB shouldBe 1
    val (itemC, qC) = qB.fdequeue()
    itemC shouldBe 2
    qC shouldBe emptyIMQueue()
  }

  test("dequeue repeat (not ready)") {
    intQueueOfNoneNR.fdequeue() shouldBe Pair(null, emptyIMQueue<Int>())
    intQueueOfOne1NR.fdequeue() shouldBe Pair(1, emptyIMQueue<Int>())
    intQueueOfTwoNR.fdequeue() shouldBe Pair(1, intQueueOfOne2YR)
    intQueueOfThreeNR.fdequeue() shouldBe Pair(3, intQueueOfTwoYR)
    val (itemA, qA) = intQueueOfThreeNR.fdequeue()
    itemA shouldBe 3
    val (itemB, qB) = qA.fdequeue()
    itemB shouldBe 1
    val (itemC, qC) = qB.fdequeue()
    itemC shouldBe 2
    qC shouldBe emptyIMQueue()
  }

  test("dequeue vs ierator") {
    val iter = intQueueOfThreer2B.iterator()

    tailrec fun go(q: FQueue<Int>) {
      if (q.fempty()) {
        iter.hasNext() shouldBe false
        return
      }
      val (item, shortQueue) = q.fdequeue()
      iter.nullableNext() shouldBe item
      go(shortQueue)
    }

    go(intQueueOfThreer2B)
  }

  test("fdequeueOrThrow") {
    shouldThrow<IllegalStateException> {
      intQueueOfNoneNR.fdequeueOrThrow()
    }
    shouldThrow<IllegalStateException> {
      intQueueOfNoneYR.fdequeueOrThrow()
    }
    intQueueOfOne1NR.fdequeueOrThrow() shouldBe Pair(1, emptyIMQueue<Int>())
    intQueueOfTwoYR.fdequeueOrThrow() shouldBe Pair(1, intQueueOfOne2YR)
    intQueueOfThreeNR.fdequeueOrThrow() shouldBe Pair(3, intQueueOfTwoYR)
    intQueueOfThreesYR.fdequeueOrThrow() shouldBe Pair(1, intQueueOfTwo23YR)
  }

  test("fenqueue (not ready)") {
    // back fills in reverse order
    emptyIMQueue<Int>().fenqueue(1) shouldBe intQueueOfOne1NR
    intQueueOfNoneNR.fenqueue(1) shouldBe intQueueOfOne1NR
    intQueueOfOne1NR.fenqueue(2) shouldBe intQueueOfTwoNR
    intQueueOfTwoNR.fenqueue(3) shouldBe intQueueOfThreesNR
  }

  test("fenqueue (ready)") {
    // makes no difference for a start from empty
    intQueueOfNoneYR.fenqueue(1) shouldBe intQueueOfOne1NR

    // here it is different
    intQueueOfOne1YR.fqForceBack(merge = true).fenqueue(2).fqStrongEqual(intQueueOfTwoNR) shouldBe true
    intQueueOfTwoYR.fqForceBack(merge = true).fenqueue(3).fqStrongEqual(intQueueOfThreesNR) shouldBe true

    intQueueOfOne1YR.fenqueue(2).fqForceFront(merge = true).fqStrongEqual(intQueueOfTwoYR) shouldBe true
    intQueueOfTwoYR.fenqueue(3).fqForceFront(merge = true).fqStrongEqual(intQueueOfThreesYR) shouldBe true
  }

  // ======== utility

  test("==") {
    (intQueueOfOne1NR == emptyIMQueue<Int>()) shouldBe false
    emptyIMQueue<Int>().equal(intQueueOfOne1NR) shouldBe false
    (intQueueOfOne1NR == intQueueOfOne1NR) shouldBe true
    (intQueueOfOne1NR == intQueueOfTwoNR) shouldBe false
    (intQueueOfTwoNR == intQueueOfOne1NR) shouldBe false
    (intQueueOfTwoNR == intQueueOfTwoNR) shouldBe true
  }

  test("equal (not ready)") {
    intQueueOfOne1NR.equal(emptyIMQueue<Int>()) shouldBe false
    emptyIMQueue<Int>().equal(intQueueOfOne1NR) shouldBe false
    intQueueOfOne1NR.fqStrongEqual(intQueueOfOne1NR) shouldBe true
    intQueueOfOne1NR.equal(intQueueOfOne1NR) shouldBe true
    intQueueOfOne1NR.equal(intQueueOfTwoNR) shouldBe false
    intQueueOfTwoNR.equal(intQueueOfOne1NR) shouldBe false
    intQueueOfTwoNR.fqStrongEqual(intQueueOfTwoNR) shouldBe true
  }

  test("equal (ready left)") {
    (intQueueOfNoneYR === intQueueOfNoneNR) shouldBe true
    intQueueOfNoneYR.equal(intQueueOfNoneNR) shouldBe true
    intQueueOfNoneYR.equal(intQueueOfOne1NR) shouldBe false
    intQueueOfOne1YR.equal(intQueueOfNoneNR) shouldBe false
    intQueueOfOne1YR.equal(emptyIMQueue<Int>()) shouldBe false
    intQueueOfOne1YR.fqStrongEqual(intQueueOfOne1NR) shouldBe false
    intQueueOfOne1YR.fqStructuralEqual(intQueueOfOne1NR) shouldBe true
    intQueueOfOne1YR.equal(intQueueOfOne1NR) shouldBe true
    intQueueOfOne1YR.equal(intQueueOfTwoNR) shouldBe false
    intQueueOfTwoYR.equal(intQueueOfOne1NR) shouldBe false
    intQueueOfTwoYR.fqStrongEqual(intQueueOfTwoNR) shouldBe false
    intQueueOfTwoYR.fqStructuralEqual(intQueueOfTwoNR) shouldBe true
    intQueueOfTwoYR.equal(intQueueOfTwoNR) shouldBe true
  }

  test("equal (ready right)") {
    (intQueueOfNoneNR === intQueueOfNoneYR) shouldBe true
    intQueueOfNoneNR.fqStrongEqual(intQueueOfNoneYR) shouldBe true
    intQueueOfNoneNR.equal(intQueueOfOne1YR) shouldBe false
    intQueueOfOne1NR.equal(intQueueOfNoneYR) shouldBe false
    emptyIMQueue<Int>().equal(intQueueOfOne1YR) shouldBe false
    intQueueOfOne1NR.fqStrongEqual(intQueueOfOne1YR) shouldBe false
    intQueueOfOne1NR.fqStructuralEqual(intQueueOfOne1YR) shouldBe true
    intQueueOfOne1NR.equal(intQueueOfOne1YR) shouldBe true
    intQueueOfOne1NR.equal(intQueueOfTwoYR) shouldBe false
    intQueueOfTwoNR.equal(intQueueOfOne1YR) shouldBe false
    intQueueOfTwoNR.fqStrongEqual(intQueueOfTwoYR) shouldBe false
    intQueueOfTwoNR.fqStructuralEqual(intQueueOfTwoYR) shouldBe true
    intQueueOfTwoNR.equal(intQueueOfTwoYR) shouldBe true
  }

  test("equal (all ready)") {
    intQueueOfNoneYR.fqStrongEqual(intQueueOfNoneYR) shouldBe true
    intQueueOfNoneYR.equal(intQueueOfOne1YR) shouldBe false
    intQueueOfOne1YR.fqStrongEqual(intQueueOfOne1YR) shouldBe true
    intQueueOfOne1YR.equal(intQueueOfTwoYR) shouldBe false
    intQueueOfTwoYR.equal(intQueueOfOne1YR) shouldBe false
    intQueueOfTwoYR.fqStrongEqual(intQueueOfTwoYR) shouldBe true
  }

  test("fforEach") {
    val counter = AtomicInteger(0)
    val summer = AtomicInteger(0)
    val doCount: (Int) -> Unit = { counter.incrementAndGet() }
    val doSum: (Int) -> Unit = { v -> summer.addAndGet(v) }
    intQueueOfNoneNR.fforEach(doCount)
    counter.get() shouldBe 0
    intQueueOfNoneYR.fforEach(doSum)
    summer.get() shouldBe 0
    counter.set(0)
    summer.set(0)
    checkAll(repeats, Arb.list(Arb.int(),20..100)) { l ->
      val oraSum = l.fold(0){ acc, el -> acc + el }
      val fsqueue = FQueueBody.of(FList.of(l.subList(0, l.size/2)),FList.of(l.subList(l.size/2, l.size)))
      fsqueue.size shouldBe l.size
      fsqueue.fforEach(doCount)
      counter.get() shouldBe l.size
      counter.set(0)
      fsqueue.fforEach(doSum)
      summer.get() shouldBe oraSum
      summer.set(0)
    }
  }

  test("copy") {
    (intQueueOfNoneNR.copy() === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfOne1YR.copy() === intQueueOfOne1YR) shouldBe false
    intQueueOfThree2B.copy().fqStrongEqual(intQueueOfThree2B) shouldBe true
    intQueueOfThree2F.copy().fqStrongEqual(intQueueOfThree2F) shouldBe true
  }

  test("toIMList") {
    intQueueOfNoneNR.toIMList().fhead() shouldBe null
    intQueueOfOne1YR.toIMList().fhead() shouldBe 1
    intQueueOfTwoYR.toIMList().fhead() shouldBe 1
    intQueueOfTwoYR.toIMList().ftail().fhead() shouldBe 2
    intQueueOfOne1NR.toIMList().fhead() shouldBe 1
    intQueueOfTwoNR.toIMList().fhead() shouldBe 1
    intQueueOfTwoNR.toIMList().ftail().fhead() shouldBe 2
  }
  
  test("copyToMutableList") {
    intQueueOfNoneNR.copyToMutableList() shouldBe mutableListOf()
    intQueueOfOne1YR.copyToMutableList() shouldBe intQueueOfOne1YR.toFList().copyToMutableList()
    intQueueOfTwoYR.copyToMutableList() shouldBe intQueueOfTwoNR.toFList().copyToMutableList()
    intQueueOfOne1NR.copyToMutableList() shouldBe intQueueOfOne1NR.toFList().copyToMutableList()
    intQueueOfTwoNR.copyToMutableList() shouldBe intQueueOfTwoYR.toFList().copyToMutableList()
    intQueueOfThree2B.copyToMutableList() shouldBe intQueueOfThree2F.toFList().copyToMutableList()
  }

  // implementation

  test("strongEqual") {
    intQueueOfTwoYR.fqStrongEqual(intQueueOfTwoNR) shouldBe false
  }

  test("toFList") {
    emptyIMQueue<Int>().toFList().fhead() shouldBe null
    intQueueOfOne1YR.toFList().fhead() shouldBe 1
    intQueueOfTwoYR.toFList().fhead() shouldBe 1
    intQueueOfTwoYR.toFList().ftail().fhead() shouldBe 2
    intQueueOfOne1NR.toFList().fhead() shouldBe 1
    intQueueOfTwoNR.toFList().fhead() shouldBe 1
    intQueueOfTwoNR.toFList().ftail().fhead() shouldBe 2
  }

})
