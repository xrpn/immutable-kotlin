package com.xrpn.immutable.fqueuetest

import com.xrpn.imapi.IMOrdered
import com.xrpn.immutable.*
import com.xrpn.immutable.FQueue.Companion.emptyIMQueue
import com.xrpn.immutable.emptyArrayOfInt
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fqueue

private val intQueueOfNoneNR = FQueue.of(*emptyArrayOfInt)
private val intQueueOfNoneYR = FQueue.of(*emptyArrayOfInt, readyToDequeue = true)
private val intQueueOfOne1NR = FQueue.of(*arrayOf<Int>(1))
private val intQueueOfOne2NR = FQueue.of(*arrayOf<Int>(2))
private val intQueueOfOne2YR = FQueue.of(*arrayOf<Int>(2), readyToDequeue = true)
private val intQueueOfTwoNR = FQueue.of(*arrayOf<Int>(1, 2))
private val intQueueOfTworNR = FQueue.of(*arrayOf<Int>(2, 1))
private val intQueueOfThreesNR = FQueue.of(*arrayOf<Int>(1, 2, 3))
private val intQueueOfThreesrNR = FQueue.of(*arrayOf<Int>(3, 2, 1))
private val intQueueOfOne1YR = FQueue.of(*arrayOf<Int>(1), readyToDequeue = true)
private val intQueueOfTwoYR = FQueue.of(*arrayOf<Int>(1, 2), readyToDequeue = true)
private val intQueueOfTwo23YR = FQueue.of(*arrayOf<Int>(2, 3), readyToDequeue = true)
private val intQueueOfThreeNR = FQueue.of(*arrayOf<Int>(3, 1, 2))
private val intQueueOfFourNR = FQueue.of(*arrayOf<Int>(3, 1, 2, 4))
private val intQueueOfThreeYR = FQueue.of(*arrayOf<Int>(3, 1, 2), readyToDequeue = true)
private val intQueueOfThreesYR = FQueue.of(*arrayOf<Int>(1, 2, 3), readyToDequeue = true)
private val intQueueOfThree2B = FQueueBody.of(FLCons(3, FLNil), FLCons(2,FLCons(1, FLNil)))
private val intQueueOfThreer2B = FQueueBody.of(FLCons(2,FLCons(1, FLNil)), FLCons(3, FLNil))
private val intQueueOfThree2F = FQueueBody.of(FLCons(3, FLCons(1, FLNil)), FLCons(2,FLNil))
private val intQueueOfFour2F = FQueueBody.of(FLCons(3, FLCons(1, FLNil)), FLCons(4, FLCons(2,FLNil)))
private val intQueueOfThreer2F = FQueueBody.of(FLCons(2,FLNil), FLCons(3, FLCons(1, FLNil)))
private val intListOfNone: IMOrdered<Int> = FList.of(*emptyArrayOfInt)
private val intListOfOne: IMOrdered<Int> = FList.of(*arrayOf<Int>(1))
private val intListOfOneB: IMOrdered<Int> = FList.of(*arrayOf<Int>(2))
private val intListOfTwo: IMOrdered<Int> = FList.of(*arrayOf<Int>(1,2))
private val intListOfTwoB: IMOrdered<Int> = FList.of(*arrayOf<Int>(2,3))
private val intListOfThree: IMOrdered<Int> = FList.of(*arrayOf<Int>(1,2,3))


class FQueueOrderingTest : FunSpec({

  tailrec fun prepare(q: FQueue<Int>, acc: FStack<Int>): FStack<Int> = if (q.fempty()) acc else {
    val (a: Int?, b: FQueue<Int>) = q.fpopAndRemainder()
    val newAcc = a?.let { acc.fpush(it) } ?: acc
    prepare(b, newAcc)
  }

  fun compare(a: FQueue<Int>, b: FStack<Int>, acc: Boolean): Boolean = if (!acc) false else if (0==a.fsize() && 0==b.fsize()) acc else {
    val (sa, sb) = b.fpop()
    val (qa, qb) = a.fdequeue()
    val newAcc = acc && a.fsize() == b.fsize() && (sa == qa)
    compare(qb, sb, newAcc)
  }

  val repeats = Triple(5, 3, 10)

  beforeTest {}

  test("fdrop (not ready)") {
    (intQueueOfNoneNR.fdrop(0) === intQueueOfNoneNR.fdiscardFront()) shouldBe true
    (intQueueOfOne1NR.fdrop(1) === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfTwoNR.fdrop(0) === intQueueOfTwoNR) shouldBe true
    (intQueueOfTwoNR.fdrop(1) === intQueueOfTwoNR.fdiscardFront()) shouldBe false
    intQueueOfTwoNR.fdrop(1).fqStrongEqual(intQueueOfTwoNR.fdiscardFront()) shouldBe true
    (intQueueOfTwoNR.fdrop(2) === emptyIMQueue<Int>()) shouldBe true
    intQueueOfThreeNR.fdrop(0) shouldBe intQueueOfThreeNR
    intQueueOfThreeNR.fdrop(1).fqStrongEqual(intQueueOfTwoYR) shouldBe true
    intQueueOfThreeNR.fdrop(2) shouldBe intQueueOfOne2NR
    intQueueOfThreeNR.fdrop(3) shouldBe FQueue.emptyIMQueue()
  }

  test("fdrop (ready)") {
    (intQueueOfNoneYR.fdrop(0) === intQueueOfNoneNR.fdiscardFront()) shouldBe true
    (intQueueOfOne1YR.fdrop(1) === emptyIMQueue<Int>()) shouldBe true
    (intQueueOfTwoYR.fdrop(0) === intQueueOfTwoYR) shouldBe true
    (intQueueOfTwoYR.fdrop(1) === intQueueOfTwoYR.fdiscardFront()) shouldBe false
    intQueueOfTwoYR.fdrop(1).fqStrongEqual(intQueueOfTwoNR.fdiscardFront()) shouldBe true
    (intQueueOfTwoYR.fdrop(2) === emptyIMQueue<Int>()) shouldBe true
    intQueueOfThreeYR.fdrop(0) shouldBe intQueueOfThreeNR
    intQueueOfThreeYR.fdrop(1).fqStrongEqual(intQueueOfTwoYR) shouldBe true
    intQueueOfThreeYR.fdrop(2) shouldBe intQueueOfOne2NR
    intQueueOfThreeYR.fdrop(3) shouldBe FQueue.emptyIMQueue()
  }

  test("fdrop") {
    intQueueOfThree2F.fdrop(1).fqSemanticEqual(intQueueOfTwoYR) shouldBe false
    intQueueOfThree2F.fdrop(1).fqSemanticEqual(intQueueOfTwoNR) shouldBe false
    intQueueOfThree2F.fdrop(1).equal(intQueueOfTwoYR) shouldBe true
    intQueueOfThree2F.fdrop(1).equal(intQueueOfTwoNR) shouldBe true
    intQueueOfThree2F.fdrop(2).fqStrongEqual(intQueueOfOne2NR) shouldBe true
    (intQueueOfThree2F.fdrop(3) === emptyIMQueue<Int>()) shouldBe true
    intQueueOfThree2B.fdrop(1).fqStrongEqual(intQueueOfTwoNR) shouldBe true
    intQueueOfThree2B.fdrop(1).equal(intQueueOfTwoYR) shouldBe true
    intQueueOfThree2B.fdrop(2).fqStrongEqual(intQueueOfOne2YR) shouldBe true
    (intQueueOfThree2B.fdrop(3) === emptyIMQueue<Int>()) shouldBe true
  }

  test("fdrop negative") {
    (intQueueOfThree2F.fdrop(-1) === intQueueOfThree2F) shouldBe true
    (intQueueOfThree2B.fdrop(-1) === intQueueOfThree2B) shouldBe true
  }

  test("fnext (ready)") {
    intQueueOfNoneYR.fnext() shouldBe Pair(null, emptyIMQueue<Int>())
    intQueueOfOne1YR.fnext() shouldBe Pair(1, emptyIMQueue<Int>())
    intQueueOfTwoYR.fnext() shouldBe Pair(1, intQueueOfOne2YR)
    intQueueOfTwoYR.fnext() shouldBe Pair(intQueueOfTwoYR.ffirst(), intQueueOfTwoYR.fdiscardFront())
    intQueueOfThreeYR.fnext() shouldBe Pair(3, intQueueOfTwoYR)
    intQueueOfThreeYR.fnext() shouldBe Pair(intQueueOfThreeYR.ffirst(), intQueueOfThreeYR.fdiscardFront())
    intQueueOfThreesYR.fnext() shouldBe Pair(1, intQueueOfTwo23YR)
    intQueueOfThreesYR.fnext() shouldBe Pair(intQueueOfThreesYR.ffirst(), intQueueOfThreesYR.fdiscardFront())
  }

  test("fnext (not ready)") {
    intQueueOfNoneNR.fnext() shouldBe Pair(null, emptyIMQueue<Int>())
    intQueueOfOne1NR.fdequeueOrThrow() shouldBe Pair(1, emptyIMQueue<Int>())
    intQueueOfTwoNR.fdequeueOrThrow() shouldBe Pair(1, intQueueOfOne2YR)
    intQueueOfTwoYR.fnext() shouldBe Pair(intQueueOfTwoYR.ffirst(), intQueueOfTwoYR.fdiscardFront())
    intQueueOfThreeNR.fdequeueOrThrow() shouldBe  Pair(3, intQueueOfTwoYR)
    intQueueOfThreeNR.fnext() shouldBe Pair(intQueueOfThreeNR.ffirst(), intQueueOfThreeNR.fdiscardFront())
    intQueueOfThreesNR.fnext() shouldBe Pair(1, intQueueOfTwo23YR)
    intQueueOfThreesNR.fnext() shouldBe Pair(intQueueOfThreesNR.ffirst(), intQueueOfThreesNR.fdiscardFront())
  }

  test("freverse") {
    (intQueueOfNoneNR.freverse() === emptyIMQueue<Int>()) shouldBe true
    intQueueOfOne1NR.freverse().fqStrongEqual(intQueueOfOne1YR) shouldBe true
    intQueueOfOne1YR.freverse().fqStrongEqual(intQueueOfOne1NR) shouldBe true

    intQueueOfTworNR.freverse().fqStrongEqual(intQueueOfTwoYR) shouldBe true
    intQueueOfTwoYR.freverse().fqStrongEqual(intQueueOfTworNR) shouldBe true
    compare(intQueueOfTwoYR, prepare(intQueueOfTwoYR.freverse(), FStack.emptyIMStack()), true) shouldBe true
    compare(intQueueOfTwoYR, prepare(intQueueOfTworNR, FStack.emptyIMStack()), true) shouldBe true
    compare(intQueueOfTworNR, prepare(intQueueOfTworNR.freverse(), FStack.emptyIMStack()), true) shouldBe true
    compare(intQueueOfTworNR, prepare(intQueueOfTwoYR, FStack.emptyIMStack()), true) shouldBe true

    intQueueOfThreesYR.freverse().fqStrongEqual(intQueueOfThreesrNR) shouldBe true
    intQueueOfThreesrNR.freverse().fqStrongEqual(intQueueOfThreesYR) shouldBe true
    compare(intQueueOfThreesYR, prepare(intQueueOfThreesYR.freverse(), FStack.emptyIMStack()), true) shouldBe true
    compare(intQueueOfThreesYR, prepare(intQueueOfThreesrNR, FStack.emptyIMStack()), true) shouldBe true
    compare(intQueueOfThreesrNR, prepare(intQueueOfThreesrNR.freverse(), FStack.emptyIMStack()), true) shouldBe true
    compare(intQueueOfThreesrNR, prepare(intQueueOfThreesYR, FStack.emptyIMStack()), true) shouldBe true

    intQueueOfThreer2F.freverse().fqStrongEqual(intQueueOfThree2F) shouldBe true
    intQueueOfThree2F.freverse().fqStrongEqual(intQueueOfThreer2F) shouldBe true
    compare(intQueueOfThree2F, prepare(intQueueOfThree2F.freverse(), FStack.emptyIMStack()), true) shouldBe true
    compare(intQueueOfThree2F, prepare(intQueueOfThreer2F, FStack.emptyIMStack()), true) shouldBe true
    compare(intQueueOfThreer2F, prepare(intQueueOfThreer2F.freverse(), FStack.emptyIMStack()), true) shouldBe true
    compare(intQueueOfThreer2F, prepare(intQueueOfThree2F, FStack.emptyIMStack()), true) shouldBe true

    intQueueOfThree2B.freverse().fqStrongEqual(intQueueOfThreer2B) shouldBe true
    intQueueOfThreer2B.freverse().fqStrongEqual(intQueueOfThree2B) shouldBe true
    compare(intQueueOfThree2B, prepare(intQueueOfThree2B.freverse(), FStack.emptyIMStack()), true) shouldBe true
    compare(intQueueOfThree2B, prepare(intQueueOfThreer2B, FStack.emptyIMStack()), true) shouldBe true
    compare(intQueueOfThreer2B, prepare(intQueueOfThreer2B.freverse(), FStack.emptyIMStack()), true) shouldBe true
    compare(intQueueOfThreer2B, prepare(intQueueOfThree2B, FStack.emptyIMStack()), true) shouldBe true
  }

  test("frotl (A, B, C).frotl() becomes (B, C, A)") {
    intQueueOfNoneNR.frotl() shouldBe intQueueOfNoneNR
    intQueueOfOne1NR.frotl() shouldBe intQueueOfOne1NR
    intQueueOfTwoNR.frotl() shouldBe intQueueOfTworNR
    intQueueOfTwoYR.frotl() shouldBe intQueueOfTworNR
  }

  test("frotl properties") {
    checkAll(repeats.first, Arb.fqueue(Arb.int(),repeats.second..repeats.third)) { fl ->
      tailrec fun go(ff: FQueue<Int>): Unit = if (ff.fempty()) Unit else {
        val (first, shortQueue) = ff.fdequeue()
        first?.let {
          if (ff.flast() != it) {
            val aut = ff.frotl()
            aut.flast() shouldBe it
            aut.ffirst() shouldBe shortQueue.ffirst()
          }
        }
        go(shortQueue)
      }
      go(fl)
    }
  }

  test("frotr (A, B, C).frotr() becomes (C, A, B)") {
    intQueueOfNoneNR.frotr() shouldBe intQueueOfNoneNR
    intQueueOfOne1NR.frotr() shouldBe intQueueOfOne1NR
    intQueueOfTwoNR.frotr() shouldBe intQueueOfTworNR
    intQueueOfTwoYR.frotr() shouldBe intQueueOfTworNR
  }

  test("frotr properties") {
    checkAll(repeats.first, Arb.fqueue(Arb.int(),repeats.second..repeats.third)) { fl ->
      tailrec fun go(ff: FQueue<Int>): Unit = if (ff.fempty()) Unit else {
        val (first, shortQueue) = ff.fdequeue()
        first?.let {
          if (ff.flast() != it) {
            val aut = ff.frotr()
            aut.fdequeue().second.ffirst() shouldBe it
            aut.ffirst() shouldBe ff.flast()
          }
        }
        go(shortQueue)
      }
      go(fl)
    }
  }

  test("fswaph (A, B, C).fswaph() becomes (B, A, C)") {
    intQueueOfNoneNR.fswaph() shouldBe intQueueOfNoneNR
    intQueueOfOne1NR.fswaph() shouldBe intQueueOfOne1NR
    intQueueOfTwoNR.fswaph() shouldBe intQueueOfTworNR
    intQueueOfTwoYR.fswaph() shouldBe intQueueOfTworNR
  }

  test("fswaph properties") {
    checkAll(repeats.first, Arb.fqueue(Arb.int(),repeats.second..repeats.third)) { fl ->
      tailrec fun go(ff: FQueue<Int>): Unit = if (ff.fempty()) Unit else {
        val (first, shortQueue) = ff.fdequeue()
        first?.let {
          if (ff.flast() != it) {
            val aut = ff.fswaph()
            aut.fdequeue().second.ffirst() shouldBe it
            aut.ffirst() shouldBe shortQueue.ffirst()
          }
        }
        go(shortQueue)
      }
      go(fl)
    }
  }

  test("fzip") {
    (intQueueOfNoneNR.fzip(intListOfNone) === intQueueOfNoneNR) shouldBe true
    (intQueueOfNoneNR.fzip(intListOfOne) === intQueueOfNoneYR) shouldBe true
    (intQueueOfOne1NR.fzip(intListOfNone) === intQueueOfNoneNR) shouldBe true
    (intQueueOfOne1NR.fzip(intQueueOfOne1NR).equals(FQueue.of(Pair(1,1), readyToDequeue = false))) shouldBe true
    (intQueueOfOne1NR.fzip(intListOfOne).equals(FQueue.of(Pair(1,1), readyToDequeue = false))) shouldBe true
    (intQueueOfOne1NR.fzip(intListOfOne).equals(FQueue.of(Pair(1,1), readyToDequeue = true))) shouldBe true
    (intQueueOfOne1NR.fzip(intListOfTwo).equals(FQueue.of(Pair(1,1), readyToDequeue = true))) shouldBe true

    (intQueueOfTwoNR.fzip(intListOfNone) === intQueueOfNoneNR) shouldBe true
    (intQueueOfTwoNR.fzip(intQueueOfOne1NR).equals(FQueue.of(Pair(1,1), readyToDequeue = false))) shouldBe true
    (intQueueOfTwoNR.fzip(intQueueOfOne1NR).equal(FQueue.of(Pair(1,1), readyToDequeue = false), strong = true)) shouldBe false
    (intQueueOfTwoNR.fzip(intListOfOne).equals(FQueue.of(Pair(1,1), readyToDequeue = false))) shouldBe true
    (intQueueOfTwoNR.fzip(intListOfOne).equals(FQueue.of(Pair(1,1), readyToDequeue = true))) shouldBe true
    (intQueueOfTwoNR.fzip(intListOfOne).equal(FQueue.of(Pair(1,1), readyToDequeue = true), strong = true)) shouldBe true
    (intQueueOfTwoNR.fzip(intListOfTwo).equals(FQueue.of(Pair(1,1), Pair(2,2), readyToDequeue = true))) shouldBe true
    (intQueueOfTwoNR.fzip(intListOfTwoB).equals(FQueue.of(Pair(1,2), Pair(2,3), readyToDequeue = true))) shouldBe true
    (intQueueOfTwoNR.fzip(intListOfThree).equals(FQueue.of(Pair(1,1), Pair(2,2), readyToDequeue = true))) shouldBe true

    (intQueueOfTwoYR.fzip(intListOfNone) === intQueueOfNoneNR) shouldBe true
    (intQueueOfTwoYR.fzip(intQueueOfOne1NR).equals(FQueue.of(Pair(1,1)))) shouldBe true
    (intQueueOfTwoYR.fzip(intListOfOne).equals(FQueue.of(Pair(1,1)))) shouldBe true
    (intQueueOfTwoYR.fzip(intListOfTwo).equals(FQueue.of(Pair(1,1), Pair(2,2), readyToDequeue = true))) shouldBe true
    (intQueueOfTwo23YR.fzip(intListOfTwo).equals(FQueue.of(Pair(2,1), Pair(3,2), readyToDequeue = true))) shouldBe true
    (intQueueOfTwoYR.fzip(intListOfTwoB).equals(FQueue.of(Pair(1,2), Pair(2,3), readyToDequeue = true))) shouldBe true
    (intQueueOfTwoYR.fzip(intListOfThree).equals(FQueue.of(Pair(1,1), Pair(2,2), readyToDequeue = true))) shouldBe true

    (intQueueOfFourNR.fzip(intQueueOfFour2F).equal(FQueue.of(Pair(3,3), Pair(1,1), Pair(2,2), Pair(4,4), readyToDequeue = true), strong = true)) shouldBe true
  }


})
