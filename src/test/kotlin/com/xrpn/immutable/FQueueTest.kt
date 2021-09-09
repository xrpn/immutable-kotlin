package com.xrpn.immutable

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FQueueTest : FunSpec({

  beforeTest {
  }

  test("nullableDequeue (concise)") {
    FQueue.emptyIMQueue<Int>().fdequeue() shouldBe Pair(null, FQueue.emptyIMQueue<Int>())
  }

  test("nullableDequeue (ready)") {
    FQueue.of(*arrayOf<Int>(), readyToDequeue = true).fdequeue() shouldBe Pair(null, FQueue.emptyIMQueue<Int>())
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).fdequeue() shouldBe Pair(1, FQueue.emptyIMQueue<Int>())
    // FQueue(front=(1,2), back = ()) => 1, FQueue(front=(2), back=())
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).fdequeue() shouldBe Pair(1, FQueue.of(*arrayOf<Int>(2), readyToDequeue = true))
    // FQueue(front=(3,1,2), back = ()) => 3, FQueue(front=(1,2), back=())
    FQueue.of(*arrayOf<Int>(3, 1, 2), readyToDequeue = true).fdequeue() shouldBe Pair(3, FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true))
  }

  test("nullableDequeue (not ready)") {
    FQueue.of(*arrayOf<Int>()).fdequeue() shouldBe Pair(null, FQueue.emptyIMQueue<Int>())
    FQueue.of(*arrayOf<Int>(1)).fdequeue() shouldBe Pair(1, FQueue.emptyIMQueue<Int>())
    // FQueue(front=(), back=(2,1)) => 2, FQueue(front=(2), back=())
    FQueue.of(*arrayOf<Int>(1, 2)).fdequeue() shouldBe Pair(1, FQueue.of(*arrayOf<Int>(2), readyToDequeue = true))
    // FQueue(front=(), back=(2,1,3)) => 3, FQueue(front=(1,2), back=())
    FQueue.of(*arrayOf<Int>(3, 1, 2)).fdequeue() shouldBe Pair(3, FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true))
  }

  test("dequeue (concise)") {
    shouldThrow<IllegalStateException> {
      FQueue.emptyIMQueue<Int>().fdequeueOrThrow() shouldBe Pair(null, FQueue.emptyIMQueue<Int>())
    }
  }

  test("dequeue (ready)") {
    shouldThrow<IllegalStateException> {
      FQueue.of(*arrayOf<Int>(), readyToDequeue = true).fdequeueOrThrow()
    }
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).fdequeueOrThrow() shouldBe Pair(1, FQueue.emptyIMQueue<Int>())
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).fdequeueOrThrow() shouldBe Pair(1, FQueue.of(*arrayOf<Int>(2), readyToDequeue = true))
    FQueue.of(*arrayOf<Int>(3, 1, 2), readyToDequeue = true).fdequeueOrThrow() shouldBe Pair(3, FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true))
  }

  test("dequeue (not ready)") {
    shouldThrow<IllegalStateException> {
      FQueue.of(*arrayOf<Int>()).fdequeueOrThrow()
    }
    FQueue.of(*arrayOf<Int>(1)).fdequeueOrThrow() shouldBe Pair(1, FQueue.emptyIMQueue<Int>())
    FQueue.of(*arrayOf<Int>(1, 2)).fdequeueOrThrow() shouldBe Pair(1, FQueue.of(*arrayOf<Int>(2), readyToDequeue = true))
    FQueue.of(*arrayOf<Int>(3, 1, 2)).fdequeueOrThrow() shouldBe  Pair(3, FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true))
  }

  test("forceFront") {
    FQueue.emptyIMQueue<Int>().fqForceFront() shouldBe FQueue.emptyIMQueue<Int>()
    FQueue.emptyIMQueue<Int>().fqForceFront(merge = true) shouldBe FQueue.emptyIMQueue<Int>()
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).fqForceFront() shouldBe FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).fqForceFront() shouldBe FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).fqForceFront(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).fqForceFront(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)
    FQueue.of(*arrayOf<Int>(1)).fqForceFront() shouldBe FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)
    FQueue.of(*arrayOf<Int>(1, 2)).fqForceFront() shouldBe FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)
    FQueue.of(*arrayOf<Int>(1)).fqForceFront(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)
    FQueue.of(*arrayOf<Int>(1, 2)).fqForceFront(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)
  }

  test("forceBack") {
    FQueue.emptyIMQueue<Int>().fqForceBack() shouldBe FQueue.emptyIMQueue<Int>()
    FQueue.emptyIMQueue<Int>().fqForceBack(merge = true) shouldBe FQueue.emptyIMQueue<Int>()
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).fqForceBack() shouldBe FQueue.of(*arrayOf<Int>(1))
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).fqForceBack() shouldBe FQueue.of(*arrayOf<Int>(1, 2))
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).fqForceBack(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1))
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).fqForceBack(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1, 2))
    FQueue.of(*arrayOf<Int>(1)).fqForceBack() shouldBe FQueue.of(*arrayOf<Int>(1))
    FQueue.of(*arrayOf<Int>(1, 2)).fqForceBack() shouldBe FQueue.of(*arrayOf<Int>(1, 2))
    FQueue.of(*arrayOf<Int>(1)).fqForceBack(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1))
    FQueue.of(*arrayOf<Int>(1, 2)).fqForceBack(merge = true) shouldBe FQueue.of(*arrayOf<Int>(1, 2))
  }

  test("toFList") {
    FQueue.emptyIMQueue<Int>().toFList().fhead() shouldBe null
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).toFList().fhead() shouldBe 1
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).toFList().fhead() shouldBe 1
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).toFList().ftail().fhead() shouldBe 2
    FQueue.of(*arrayOf<Int>(1)).toFList().fhead() shouldBe 1
    FQueue.of(*arrayOf<Int>(1, 2)).toFList().fhead() shouldBe 1
    FQueue.of(*arrayOf<Int>(1, 2)).toFList().ftail().fhead() shouldBe 2
  }

  //
  // ================ companion object
  //

  test("co.==") {
    (FQueue.emptyIMQueue<Int>() == FQueue.emptyIMQueue<Int>()) shouldBe true
  }

  test("co.of varargs") {
    FQueue.of(*arrayOf<Int>()) shouldBe FQueue.emptyIMQueue<Int>()
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
    FQueue.of(arrayOf<Int>().iterator()) shouldBe FQueue.emptyIMQueue()
    // ready to dequeue
    FQueue.of(arrayOf<Int>(1).iterator(), readyToDequeue = true) shouldBe FQueueBody.of(FLCons(1, FLNil), FLNil)
    FQueue.of(arrayOf<Int>(1, 2).iterator(), readyToDequeue = true) shouldBe FQueueBody.of(FLCons(1, FLCons(2, FLNil)), FLNil)
    FQueue.of(arrayOf<Int>(1, 2, 3).iterator(), readyToDequeue = true) shouldBe FQueueBody.of(FLCons(1, FLCons(2, FLCons(3, FLNil))), FLNil)
    // ready to enqueue
    FQueue.of(arrayOf<Int>(1).iterator()) shouldBe FQueueBody.of(FLNil, FLCons(1, FLNil))
    FQueue.of(arrayOf<Int>(1, 2).iterator()) shouldBe FQueueBody.of(FLNil, FLCons(2, FLCons(1, FLNil)))
    FQueue.of(arrayOf<Int>(1, 2, 3).iterator()) shouldBe FQueueBody.of(FLNil, FLCons(3, FLCons(2, FLCons(1, FLNil))))
  }

  test("co.emptyFQueue") {
    (FQueue.emptyIMQueue<Int>() === FQueueBody.empty) shouldBe true
  }

  test("co.== (concise)") {
    (FQueue.of(*arrayOf(1)) == FQueue.emptyIMQueue<Int>()) shouldBe false
    FQueue.emptyIMQueue<Int>().equals(FQueue.of(*arrayOf(1))) shouldBe false
    (FQueue.of(*arrayOf<Int>(1)) == FQueue.of(*arrayOf<Int>(1))) shouldBe true
    (FQueue.of(*arrayOf(1)) == FQueue.of(*arrayOf<Int>(1, 2))) shouldBe false
    (FQueue.of(*arrayOf<Int>(1, 2)) == FQueue.of(*arrayOf(1))) shouldBe false
    (FQueue.of(*arrayOf<Int>(1, 2)) == FQueue.of(*arrayOf(1, 2))) shouldBe true
  }

  test("co.equal (not ready)") {
    FQueue.of(*arrayOf(1)).equal(FQueue.emptyIMQueue<Int>()) shouldBe false
    FQueue.emptyIMQueue<Int>().equal(FQueue.of(*arrayOf(1))) shouldBe false
    FQueue.of(*arrayOf<Int>(1)).fqStrongEqual(FQueue.of(*arrayOf<Int>(1))) shouldBe true
    FQueue.of(*arrayOf<Int>(1)).equal(FQueue.of(*arrayOf<Int>(1))) shouldBe true
    FQueue.of(*arrayOf(1)).equal(FQueue.of(*arrayOf<Int>(1, 2))) shouldBe false
    FQueue.of(*arrayOf<Int>(1, 2)).equal(FQueue.of(*arrayOf(1))) shouldBe false
    FQueue.of(*arrayOf<Int>(1, 2)).fqStrongEqual(FQueue.of(*arrayOf(1, 2))) shouldBe true
  }

  test("co.equal (ready left)") {
    (FQueue.of(*arrayOf<Int>(), readyToDequeue = true) === FQueue.of(*arrayOf<Int>())) shouldBe true
    FQueue.of(*arrayOf<Int>(), readyToDequeue = true).equal(FQueue.of(*arrayOf<Int>())) shouldBe true
    FQueue.of(*arrayOf<Int>(), readyToDequeue = true).equal(FQueue.of(*arrayOf<Int>(1))) shouldBe false
    FQueue.of(*arrayOf(1), readyToDequeue = true).equal(FQueue.of(*arrayOf<Int>())) shouldBe false
    FQueue.of(*arrayOf(1), readyToDequeue = true).equal(FQueue.emptyIMQueue<Int>()) shouldBe false
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).fqStrongEqual(FQueue.of(*arrayOf<Int>(1))) shouldBe false
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).fqStructuralEqual(FQueue.of(*arrayOf<Int>(1))) shouldBe true
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).equal(FQueue.of(*arrayOf<Int>(1))) shouldBe true
    FQueue.of(*arrayOf(1), readyToDequeue = true).equal(FQueue.of(*arrayOf<Int>(1, 2))) shouldBe false
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).equal(FQueue.of(*arrayOf(1))) shouldBe false
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).fqStrongEqual(FQueue.of(*arrayOf(1, 2))) shouldBe false
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).fqStructuralEqual(FQueue.of(*arrayOf(1, 2))) shouldBe true
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).equal(FQueue.of(*arrayOf(1, 2))) shouldBe true
  }

  test("co.equal (ready right)") {
    (FQueue.of(*arrayOf<Int>()) === FQueue.of(*arrayOf<Int>(), readyToDequeue = true)) shouldBe true
    FQueue.of(*arrayOf<Int>()).fqStrongEqual(FQueue.of(*arrayOf<Int>(), readyToDequeue = true)) shouldBe true
    FQueue.of(*arrayOf<Int>()).equal(FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)) shouldBe false
    FQueue.of(*arrayOf<Int>(1)).equal(FQueue.of(*arrayOf<Int>(), readyToDequeue = true)) shouldBe false
    FQueue.emptyIMQueue<Int>().equal(FQueue.of(*arrayOf(1), readyToDequeue = true)) shouldBe false
    FQueue.of(*arrayOf<Int>(1)).fqStrongEqual(FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)) shouldBe false
    FQueue.of(*arrayOf<Int>(1)).fqStructuralEqual(FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)) shouldBe true
    FQueue.of(*arrayOf<Int>(1)).equal(FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)) shouldBe true
    FQueue.of(*arrayOf(1)).equal(FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)) shouldBe false
    FQueue.of(*arrayOf<Int>(1, 2)).equal(FQueue.of(*arrayOf(1), readyToDequeue = true)) shouldBe false
    FQueue.of(*arrayOf<Int>(1, 2)).fqStrongEqual(FQueue.of(*arrayOf(1, 2), readyToDequeue = true)) shouldBe false
    FQueue.of(*arrayOf<Int>(1, 2)).fqStructuralEqual(FQueue.of(*arrayOf(1, 2), readyToDequeue = true)) shouldBe true
    FQueue.of(*arrayOf<Int>(1, 2)).equal(FQueue.of(*arrayOf(1, 2), readyToDequeue = true)) shouldBe true
  }

  test("co.equal (all ready)") {
    FQueue.of(*arrayOf<Int>(), readyToDequeue = true).fqStrongEqual(FQueue.of(*arrayOf<Int>(), readyToDequeue = true)) shouldBe true
    FQueue.of(*arrayOf<Int>(), readyToDequeue = true).equals(FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)) shouldBe false
    FQueue.of(*arrayOf(1), readyToDequeue = true).fqStrongEqual(FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)) shouldBe true
    FQueue.of(*arrayOf(1), readyToDequeue = true).equals(FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)) shouldBe false
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).equals(FQueue.of(*arrayOf(1), readyToDequeue = true)) shouldBe false
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).fqStrongEqual(FQueue.of(*arrayOf(1, 2), readyToDequeue = true)) shouldBe true
  }

  test("co.dequeue") {
    FQueue.emptyIMQueue<Int>().fdequeue() shouldBe Pair(null, FQueue.emptyIMQueue<Int>())
  }

  test("co.dequeue (ready)") {
    FQueue.of(*arrayOf<Int>(), readyToDequeue = true).fdequeue() shouldBe Pair(null, FQueue.emptyIMQueue<Int>())
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).fdequeue() shouldBe Pair(1, FQueue.emptyIMQueue<Int>())
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).fdequeue() shouldBe Pair(1, FQueue.of(*arrayOf<Int>(2), readyToDequeue = true))
    FQueue.of(*arrayOf<Int>(3, 1, 2), readyToDequeue = true).fdequeue() shouldBe Pair(3, FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true))
    val (itemA, qA) = FQueue.of(*arrayOf<Int>(3, 1, 2), readyToDequeue = true).fdequeue()
    itemA shouldBe 3
    val (itemB, qB) = qA.fdequeue()
    itemB shouldBe 1
    val (itemC, qC) = qB.fdequeue()
    itemC shouldBe 2
    qC shouldBe FQueue.emptyIMQueue()
  }

  test("co.dequeue (not ready)") {
    FQueue.of(*arrayOf<Int>()).fdequeue() shouldBe Pair(null, FQueue.emptyIMQueue<Int>())
    FQueue.of(*arrayOf<Int>(1)).fdequeue() shouldBe Pair(1, FQueue.emptyIMQueue<Int>())
    FQueue.of(*arrayOf<Int>(1, 2)).fdequeue() shouldBe Pair(1, FQueue.of(*arrayOf<Int>(2), readyToDequeue = true))
    FQueue.of(*arrayOf<Int>(3, 1, 2)).fdequeue() shouldBe Pair(3, FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true))
    val (itemA, qA) = FQueue.of(*arrayOf<Int>(3, 1, 2)).fdequeue()
    itemA shouldBe 3
    val (itemB, qB) = qA.fdequeue()
    itemB shouldBe 1
    val (itemC, qC) = qB.fdequeue()
    itemC shouldBe 2
    qC shouldBe FQueue.emptyIMQueue()
  }

  test("co.enqueue (not ready)") {
    // back fills in reverse order
    FQueue.emptyIMQueue<Int>().fenqueue(1) shouldBe FQueue.of(*arrayOf<Int>(1))
    FQueue.of(*arrayOf<Int>()).fenqueue(1) shouldBe FQueue.of(*arrayOf<Int>(1))
    FQueue.of(*arrayOf<Int>(1)).fenqueue(2) shouldBe FQueue.of(*arrayOf<Int>(1, 2))
    FQueue.of(*arrayOf<Int>(1, 2)).fenqueue(3) shouldBe FQueue.of(*arrayOf<Int>(1, 2, 3))
  }

  test("strongEqual") {
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).fqStrongEqual(FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = false)) shouldBe false
  }

  test("co.enqueue (ready)") {
    // makes no difference for a start from empty
    FQueue.of(*arrayOf<Int>(), readyToDequeue = true).fenqueue(1) shouldBe FQueue.of(*arrayOf<Int>(1))

    // here it is different
    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).fqForceBack(merge = true).fenqueue(2).fqStrongEqual(FQueue.of(*arrayOf<Int>(1, 2))) shouldBe true
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).fqForceBack(merge = true).fenqueue(3).fqStrongEqual(FQueue.of(*arrayOf<Int>(1, 2, 3))) shouldBe true

    FQueue.of(*arrayOf<Int>(1), readyToDequeue = true).fenqueue(2).fqForceFront(merge = true).fqStrongEqual(FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)) shouldBe true
    FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true).fenqueue(3).fqForceFront(merge = true).fqStrongEqual(FQueue.of(*arrayOf<Int>(1, 2, 3), readyToDequeue = true)) shouldBe true
  }
})
