package com.xrpn.immutable

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FQueueTest : FunSpec({

  beforeTest {
  }

//  afterTest { (testCase, result) ->
//  }

  test("nullableDequeue (concise)") {
    FQueue.emptyFQueue<Int>().nullableDequeue() shouldBe Pair(null, FQueue.emptyFQueue<Int>())
  }

  test("nullableDequeue (ready)") {
    FQueue.of(*arrayOf<Int>(), readyToDequeue = true).nullableDequeue() shouldBe Pair(null, FQueue.emptyFQueue<Int>())
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).nullableDequeue() shouldBe Pair(1, FQueue.emptyFQueue<Int>())
    // FQueue(front=(1,2), back = ()) => 1, FQueue(front=(2), back=())
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).nullableDequeue() shouldBe Pair(1, FQueue.of(*arrayOf<Int>(2), readyToDequeue = true))
    // FQueue(front=(3,1,2), back = ()) => 3, FQueue(front=(1,2), back=())
    FQueue.of(*arrayOf<Int>(3, 1, 2), readyToDequeue = true).nullableDequeue() shouldBe Pair(3, FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true))
  }

  test("nullableDequeue (not ready)") {
    FQueue.of(*arrayOf<Int>()).nullableDequeue() shouldBe Pair(null, FQueue.emptyFQueue<Int>())
    FQueue.of(*arrayOf<Int>(1)).nullableDequeue() shouldBe Pair(1, FQueue.emptyFQueue<Int>())
    // FQueue(front=(), back=(2,1)) => 2, FQueue(front=(2), back=())
    FQueue.of(*arrayOf<Int>(1, 2)).nullableDequeue() shouldBe Pair(1, FQueue.of(*arrayOf<Int>(2), readyToDequeue = true))
    // FQueue(front=(), back=(2,1,3)) => 3, FQueue(front=(1,2), back=())
    FQueue.of(*arrayOf<Int>(3, 1, 2)).nullableDequeue() shouldBe Pair(3, FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true))
  }

  test("dequeue (concise)") {
    shouldThrow<IllegalStateException> {
      FQueue.emptyFQueue<Int>().dequeue() shouldBe Pair(null, FQueue.emptyFQueue<Int>())
    }
  }

  test("dequeue (ready)") {
    shouldThrow<IllegalStateException> {
      FQueue.of(*arrayOf<Int>(), readyToDequeue = true).dequeue()
    }
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).dequeue() shouldBe Pair(1, FQueue.emptyFQueue<Int>())
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).dequeue() shouldBe Pair(1, FQueue.of(*arrayOf<Int>(2), readyToDequeue = true))
    FQueue.of(*arrayOf<Int>(3, 1, 2), readyToDequeue = true).dequeue() shouldBe Pair(3, FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true))
  }

  test("dequeue (not ready)") {
    shouldThrow<IllegalStateException> {
      FQueue.of(*arrayOf<Int>()).dequeue()
    }
    FQueue.of(*arrayOf<Int>(1)).dequeue() shouldBe Pair(1, FQueue.emptyFQueue<Int>())
    FQueue.of(*arrayOf<Int>(1, 2)).dequeue() shouldBe Pair(1, FQueue.of(*arrayOf<Int>(2), readyToDequeue = true))
    FQueue.of(*arrayOf<Int>(3, 1, 2)).dequeue() shouldBe  Pair(3, FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true))
  }

  test("peek (ready)") {
    FQueue.of(*arrayOf<Int>(), readyToDequeue = true).peek() shouldBe null
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).peek() shouldBe 1
    FQueue.of(*arrayOf<Int>(2, 1), readyToDequeue = true).peek() shouldBe 2
    FQueue.of(*arrayOf<Int>(3, 1, 2), readyToDequeue = true).peek() shouldBe 3
  }

  test("peek (not ready)") {
    FQueue.of(*arrayOf<Int>()).peek() shouldBe null
    FQueue.of(*arrayOf<Int>(1)).peek() shouldBe 1
    FQueue.of(*arrayOf<Int>(1, 2)).peek() shouldBe 1
    FQueue.of(*arrayOf<Int>(3, 1, 2)).peek() shouldBe 3
  }

  test("forceFront") {
    FQueue.emptyFQueue<Int>().forceFront() shouldBe FQueue.emptyFQueue<Int>()
    FQueue.emptyFQueue<Int>().forceFront(merge = true) shouldBe FQueue.emptyFQueue<Int>()
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).forceFront() shouldBe FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).forceFront() shouldBe FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).forceFront(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).forceFront(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)
    FQueue.of(*arrayOf<Int>(1)).forceFront() shouldBe FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)
    FQueue.of(*arrayOf<Int>(1, 2)).forceFront() shouldBe FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)
    FQueue.of(*arrayOf<Int>(1)).forceFront(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)
    FQueue.of(*arrayOf<Int>(1, 2)).forceFront(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)
  }

  test("forceBack") {
    FQueue.emptyFQueue<Int>().forceBack() shouldBe FQueue.emptyFQueue<Int>()
    FQueue.emptyFQueue<Int>().forceBack(merge = true) shouldBe FQueue.emptyFQueue<Int>()
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).forceBack() shouldBe FQueue.of(*arrayOf<Int>(1))
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).forceBack() shouldBe FQueue.of(*arrayOf<Int>(1, 2))
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).forceBack(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1))
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).forceBack(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1, 2))
    FQueue.of(*arrayOf<Int>(1)).forceBack() shouldBe FQueue.of(*arrayOf<Int>(1))
    FQueue.of(*arrayOf<Int>(1, 2)).forceBack() shouldBe FQueue.of(*arrayOf<Int>(1, 2))
    FQueue.of(*arrayOf<Int>(1)).forceBack(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1))
    FQueue.of(*arrayOf<Int>(1, 2)).forceBack(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1, 2))
  }

  test("drop (not ready)") {
    FQueue.of(*arrayOf<Int>()).drop(0) shouldBe FQueue.emptyFQueue()
    FQueue.of(*arrayOf<Int>(1)).drop(0) shouldBe FQueue.of(*arrayOf<Int>(1))
    FQueue.of(*arrayOf<Int>(1)).drop(1) shouldBe FQueue.emptyFQueue()
    FQueue.of(*arrayOf<Int>(1, 2)).drop(0) shouldBe FQueue.of(*arrayOf<Int>(1, 2))
    FQueue.of(*arrayOf<Int>(1, 2)).drop(1) shouldBe FQueue.of(*arrayOf<Int>(2))
    FQueue.of(*arrayOf<Int>(1, 2)).drop(2) shouldBe FQueue.emptyFQueue()
    FQueue.of(*arrayOf<Int>(3, 1, 2)).drop(0) shouldBe FQueue.of(*arrayOf<Int>(3, 1, 2))
    FQueue.of(*arrayOf<Int>(3, 1, 2)).drop(1) shouldBe FQueue.of(*arrayOf<Int>(1, 2))
    FQueue.of(*arrayOf<Int>(3, 1, 2)).drop(2) shouldBe FQueue.of(*arrayOf<Int>(2))
    FQueue.of(*arrayOf<Int>(3, 1, 2)).drop(3) shouldBe FQueue.emptyFQueue()
  }

  test("asList") {
    FQueue.emptyFQueue<Int>().asList().head() shouldBe null
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).asList().head() shouldBe 1
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).asList().head() shouldBe 1
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).asList().tail().head() shouldBe 2
    FQueue.of(*arrayOf<Int>(1)).asList().head() shouldBe 1
    FQueue.of(*arrayOf<Int>(1, 2)).asList().head() shouldBe 1
    FQueue.of(*arrayOf<Int>(1, 2)).asList().tail().head() shouldBe 2
  }

  //
  // ================ companion object
  //

  test("co.==") {
    (FQueue.emptyFQueue<Int>() == FQueue.emptyFQueue<Int>()) shouldBe true
  }

  test("co.of varargs") {
    FQueue.of(*arrayOf<Int>()) shouldBe FQueue.emptyFQueue<Int>()
    // ready to dequeue
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true) shouldBe FQueueBody.of(FLCons(1, FLNil), FLNil)
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true) shouldBe FQueueBody.of(FLCons(1, FLCons(2, FLNil)), FLNil)
    FQueue.of(*arrayOf<Int>(1, 2, 3), readyToDequeue = true) shouldBe FQueueBody.of(FLCons(1, FLCons(2, FLCons(3, FLNil))), FLNil)
    // ready to enqueue
    FQueue.of(*arrayOf<Int>(1)) shouldBe FQueueBody.of(FLNil, FLCons(1, FLNil))
    FQueue.of(*arrayOf<Int>(1, 2)) shouldBe FQueueBody.of(FLNil, FLCons(2, FLCons(1, FLNil)))
    FQueue.of(*arrayOf<Int>(1, 2, 3)) shouldBe FQueueBody.of(FLNil, FLCons(3, FLCons(2, FLCons(1, FLNil))))
  }

  test("co.of iterator") {
    FQueue.of(arrayOf<Int>().iterator()) shouldBe FQueue.emptyFQueue()
    // ready to dequeue
    FQueue.of(arrayOf<Int>(1).iterator(), readyToDequeue = true) shouldBe FQueueBody.of(FLCons(1, FLNil), FLNil)
    FQueue.of(arrayOf<Int>(1, 2).iterator(), readyToDequeue = true) shouldBe FQueueBody.of(FLCons(1, FLCons(2, FLNil)), FLNil)
    FQueue.of(arrayOf<Int>(1, 2, 3).iterator(), readyToDequeue = true) shouldBe FQueueBody.of(FLCons(1, FLCons(2, FLCons(3, FLNil))), FLNil)
    // ready to enqueue
    FQueue.of(arrayOf<Int>(1).iterator()) shouldBe FQueueBody.of(FLNil, FLCons(1, FLNil))
    FQueue.of(arrayOf<Int>(1, 2).iterator()) shouldBe FQueueBody.of(FLNil, FLCons(2, FLCons(1, FLNil)))
    FQueue.of(arrayOf<Int>(1, 2, 3).iterator()) shouldBe FQueueBody.of(FLNil, FLCons(3, FLCons(2, FLCons(1, FLNil))))
  }

  test("co.equal (concise)") {
    (FQueue.emptyFQueue<Int>() == FQueue.emptyFQueue<Int>()) shouldBe true
    (FQueue.emptyFQueue<Int>() == FQueue.emptyFQueue<Int>()) shouldBe true
    FQueue.equal2(FQueue.emptyFQueue<Int>(), FQueue.emptyFQueue<Int>()) shouldBe true
  }

  test("co.== (concise)") {
    (FQueue.of(*arrayOf(1)) == FQueue.emptyFQueue<Int>()) shouldBe false
    (FQueue.emptyFQueue<Int>() == FQueue.of(*arrayOf(1))) shouldBe false
    (FQueue.of(*arrayOf<Int>(1)) == FQueue.of(*arrayOf<Int>(1))) shouldBe true
    (FQueue.of(*arrayOf(1)) == FQueue.of(*arrayOf<Int>(1, 2))) shouldBe false
    (FQueue.of(*arrayOf<Int>(1, 2)) == FQueue.of(*arrayOf(1))) shouldBe false
    (FQueue.of(*arrayOf<Int>(1, 2)) == FQueue.of(*arrayOf(1, 2))) shouldBe true
  }

  test("co.equal (not ready)") {
    FQueue.equal2(FQueue.of(*arrayOf(1)), FQueue.emptyFQueue<Int>()) shouldBe false
    FQueue.equal2(FQueue.emptyFQueue<Int>(), FQueue.of(*arrayOf(1))) shouldBe false
    FQueue.equal2(FQueue.of(*arrayOf<Int>(1)), FQueue.of(*arrayOf<Int>(1))) shouldBe true
    FQueue.equal2(FQueue.of(*arrayOf(1)), FQueue.of(*arrayOf<Int>(1, 2))) shouldBe false
    FQueue.equal2(FQueue.of(*arrayOf<Int>(1, 2)), FQueue.of(*arrayOf(1))) shouldBe false
    FQueue.equal2(FQueue.of(*arrayOf<Int>(1, 2)), FQueue.of(*arrayOf(1, 2))) shouldBe true
  }

  test("co.equal (ready left)") {
    FQueue.equal2(FQueue.of(*arrayOf<Int>(), readyToDequeue = true), FQueue.of(*arrayOf<Int>())) shouldBe true
    FQueue.equal2(FQueue.of(*arrayOf<Int>(), readyToDequeue = true), FQueue.of(*arrayOf<Int>(1))) shouldBe false
    FQueue.equal2(FQueue.of(*arrayOf(1), readyToDequeue = true), FQueue.of(*arrayOf<Int>())) shouldBe false
    FQueue.equal2(FQueue.of(*arrayOf(1), readyToDequeue = true), FQueue.emptyFQueue<Int>()) shouldBe false
    FQueue.equal2(FQueue.of(*arrayOf<Int>(1), readyToDequeue = true), FQueue.of(*arrayOf<Int>(1))) shouldBe true
    FQueue.equal2(FQueue.of(*arrayOf(1), readyToDequeue = true), FQueue.of(*arrayOf<Int>(1, 2))) shouldBe false
    FQueue.equal2(FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true), FQueue.of(*arrayOf(1))) shouldBe false
    FQueue.equal2(FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true), FQueue.of(*arrayOf(1, 2))) shouldBe true
  }

  test("co.equal (ready right)") {
    FQueue.equal2(FQueue.of(*arrayOf<Int>()), FQueue.of(*arrayOf<Int>(), readyToDequeue = true)) shouldBe true
    FQueue.equal2(FQueue.of(*arrayOf<Int>()), FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)) shouldBe false
    FQueue.equal2(FQueue.of(*arrayOf<Int>(1)), FQueue.of(*arrayOf<Int>(), readyToDequeue = true)) shouldBe false
    FQueue.equal2(FQueue.emptyFQueue<Int>(), FQueue.of(*arrayOf(1), readyToDequeue = true)) shouldBe false
    FQueue.equal2(FQueue.of(*arrayOf<Int>(1)), FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)) shouldBe true
    FQueue.equal2(FQueue.of(*arrayOf(1)), FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)) shouldBe false
    FQueue.equal2(FQueue.of(*arrayOf<Int>(1, 2)), FQueue.of(*arrayOf(1), readyToDequeue = true)) shouldBe false
    FQueue.equal2(FQueue.of(*arrayOf<Int>(1, 2)), FQueue.of(*arrayOf(1, 2), readyToDequeue = true)) shouldBe true
  }

  test("co.equal (all ready)") {
    FQueue.equal2(FQueue.of(*arrayOf<Int>(), readyToDequeue = true), FQueue.of(*arrayOf<Int>(), readyToDequeue = true)) shouldBe true
    FQueue.equal2(FQueue.of(*arrayOf<Int>(), readyToDequeue = true), FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)) shouldBe false
    FQueue.equal2(FQueue.of(*arrayOf(1), readyToDequeue = true), FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)) shouldBe true
    FQueue.equal2(FQueue.of(*arrayOf(1), readyToDequeue = true), FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)) shouldBe false
    FQueue.equal2(FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true), FQueue.of(*arrayOf(1), readyToDequeue = true)) shouldBe false
    FQueue.equal2(FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true), FQueue.of(*arrayOf(1, 2), readyToDequeue = true)) shouldBe true
  }

  test("co.dequeue") {
    FQueue.dequeue(FQueue.emptyFQueue<Int>()) shouldBe Pair(null, FQueue.emptyFQueue<Int>())
  }

  test("co.dequeue (ready)") {
    FQueue.dequeue(FQueue.of(*arrayOf<Int>(), readyToDequeue = true)) shouldBe Pair(null, FQueue.emptyFQueue<Int>())
    FQueue.dequeue(FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)) shouldBe Pair(1, FQueue.emptyFQueue<Int>())
    FQueue.dequeue(FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)) shouldBe Pair(1, FQueue.of(*arrayOf<Int>(2), readyToDequeue = true))
    FQueue.dequeue(FQueue.of(*arrayOf<Int>(3, 1, 2), readyToDequeue = true)) shouldBe Pair(3, FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true))
    val (itemA, qA) = FQueue.dequeue(FQueue.of(*arrayOf<Int>(3, 1, 2), readyToDequeue = true))
    itemA shouldBe 3
    val (itemB, qB) = FQueue.dequeue(qA)
    itemB shouldBe 1
    val (itemC, qC) = FQueue.dequeue(qB)
    itemC shouldBe 2
    qC shouldBe FQueue.emptyFQueue()
  }

  test("co.dequeue (not ready)") {
    FQueue.dequeue(FQueue.of(*arrayOf<Int>())) shouldBe Pair(null, FQueue.emptyFQueue<Int>())
    FQueue.dequeue(FQueue.of(*arrayOf<Int>(1))) shouldBe Pair(1, FQueue.emptyFQueue<Int>())
    FQueue.dequeue(FQueue.of(*arrayOf<Int>(1, 2))) shouldBe Pair(1, FQueue.of(*arrayOf<Int>(2), readyToDequeue = true))
    FQueue.dequeue(FQueue.of(*arrayOf<Int>(3, 1, 2))) shouldBe Pair(3, FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true))
    val (itemA, qA) = FQueue.dequeue(FQueue.of(*arrayOf<Int>(3, 1, 2)))
    itemA shouldBe 3
    val (itemB, qB) = FQueue.dequeue(qA)
    itemB shouldBe 1
    val (itemC, qC) = FQueue.dequeue(qB)
    itemC shouldBe 2
    qC shouldBe FQueue.emptyFQueue()
  }

  test("co.enqueue (not ready)") {
    // back fills in reverse order
    FQueue.enqueue(FQueue.emptyFQueue<Int>(), 1) shouldBe FQueue.of(*arrayOf<Int>(1))
    FQueue.enqueue(FQueue.of(*arrayOf<Int>()), 1) shouldBe FQueue.of(*arrayOf<Int>(1))
    FQueue.enqueue(FQueue.of(*arrayOf<Int>(1)),2) shouldBe FQueue.of(*arrayOf<Int>(1, 2))
    FQueue.enqueue(FQueue.of(*arrayOf<Int>(1, 2)),3) shouldBe FQueue.of(*arrayOf<Int>(1, 2, 3))
  }

  test("co.enqueue (ready)") {
    // makes no difference for a start from empty
    FQueue.enqueue(FQueue.of(*arrayOf<Int>(), readyToDequeue = true), 1) shouldBe FQueue.of(*arrayOf<Int>(1))

    // here it is different
    FQueue.enqueue(FQueue.of(*arrayOf<Int>(1), readyToDequeue = true),2).forceBack(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1, 2))
    FQueue.enqueue(FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true),3).forceBack(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1, 2, 3))

    FQueue.enqueue(FQueue.of(*arrayOf<Int>(1), readyToDequeue = true),2).forceFront(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)
    FQueue.enqueue(FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true),3).forceFront(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1, 2, 3), readyToDequeue = true)
  }
})
