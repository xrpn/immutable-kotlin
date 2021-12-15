package com.xrpn.immutable.flisttest

import com.xrpn.imapi.IMOrdered
import com.xrpn.immutable.FLCons
import com.xrpn.immutable.FLNil
import com.xrpn.immutable.FList
import com.xrpn.immutable.emptyArrayOfInt
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.flist

private val intListOfNone: IMOrdered<Int> = FList.of(*emptyArrayOfInt)
private val intListOfOne: IMOrdered<Int> = FList.of(*arrayOf<Int>(1))
private val intListOfOneB: IMOrdered<Int> = FList.of(*arrayOf<Int>(2))
private val intListOfTwo: IMOrdered<Int> = FList.of(*arrayOf<Int>(1,2))
private val intListOfTwoB: IMOrdered<Int> = FList.of(*arrayOf<Int>(2,3))
private val intListOfThree: IMOrdered<Int> = FList.of(*arrayOf<Int>(1,2,3))

class FListOrderedTest : FunSpec({

  val repeats = Triple(5, 3, 10)

  beforeTest {}

  test("fdrop 0") {
    intListOfNone.fdrop(0) shouldBe FLNil
    intListOfOne.fdrop(0) shouldBe intListOfOne
    intListOfTwo.fdrop(0) shouldBe intListOfTwo
    intListOfThree.fdrop(0) shouldBe intListOfThree
  }

  test("fdrop 1") {
    intListOfNone.fdrop(1) shouldBe FLNil
    intListOfOne.fdrop(1) shouldBe FLNil
    intListOfTwo.fdrop(1) shouldBe FLCons(2,FLNil)
    intListOfThree.fdrop(1) shouldBe FLCons(2,FLCons(3,FLNil))
  }

  test("fdrop 2") {
    intListOfNone.fdrop(2) shouldBe FLNil
    intListOfOne.fdrop(2) shouldBe FLNil
    intListOfTwo.fdrop(2) shouldBe FLNil
    intListOfThree.fdrop(2) shouldBe FLCons(3,FLNil)
    FList.of(*arrayOf<Int>(1,2,3,4)).fdrop(2) shouldBe FLCons(3,FLCons(4,FLNil))
  }

  test("fdrop 3") {
    intListOfNone.fdrop(3) shouldBe FLNil
    intListOfOne.fdrop(3) shouldBe FLNil
    intListOfTwo.fdrop(3) shouldBe FLNil
    intListOfThree.fdrop(3) shouldBe FLNil
    FList.of(*arrayOf<Int>(1,2,3,4)).fdrop(3) shouldBe FLCons(4,FLNil)
  }

  test("fdrop negative") {
    intListOfNone.fdrop(-1) shouldBe FLNil
    intListOfOne.fdrop(-1) shouldBe intListOfOne
    intListOfTwo.fdrop(-1) shouldBe intListOfTwo
    intListOfThree.fdrop(-1) shouldBe intListOfThree
  }

  test("fnext") {
    intListOfNone.fnext() shouldBe Pair(null, FLNil)
    intListOfOne.fnext() shouldBe Pair(1, FLNil)
    intListOfTwo.fnext() shouldBe Pair(1, intListOfOneB)
    intListOfThree.fnext() shouldBe Pair(1, intListOfTwoB)
  }

  test("freverse") {
    intListOfNone.freverse() shouldBe intListOfNone
    (intListOfNone.freverse() === intListOfNone) shouldBe true
    intListOfOne.freverse() shouldBe intListOfOne
    (intListOfOne.freverse() === intListOfOne) shouldBe true
    intListOfTwo.freverse() shouldBe FLCons(2, FLCons(1, FLNil))
    intListOfThree.freverse() shouldBe FLCons(3, FLCons(2, FLCons(1, FLNil)))
  }

  test("frotl (A, B, C).frotl() becomes (B, C, A)") {
    intListOfNone.frotl() shouldBe intListOfNone
    intListOfOne.frotl() shouldBe intListOfOne
    intListOfTwo.frotl() shouldBe FLCons(2, FLCons(1, FLNil))
    intListOfThree.frotl() shouldBe FLCons(2, FLCons(3, FLCons(1, FLNil)))
  }

  test("frotl properties") {
    checkAll(repeats.first, Arb.flist(Arb.int(),repeats.second..repeats.third)) { fl ->
      tailrec fun go(ff: FList<Int>): Unit = if (ff.fempty()) Unit else {
        ff.fhead()?.let {
          if (ff.flast() != it) {
            val aut = ff.frotl()
            aut.flast() shouldBe it
            aut.fhead() shouldBe ff.ftail().fhead()
          }
        }
        go(ff.ftail())
      }
      go(fl)
    }
  }

  test("frotr (A, B, C).frotr() becomes (C, A, B)") {
    intListOfNone.frotr() shouldBe intListOfNone
    intListOfOne.frotr() shouldBe intListOfOne
    intListOfTwo.frotr() shouldBe FLCons(2, FLCons(1, FLNil))
    intListOfThree.frotr() shouldBe FLCons(3, FLCons(1, FLCons(2, FLNil)))
  }

  test("frotr properties") {
    checkAll(repeats.first, Arb.flist(Arb.int(),repeats.second..repeats.third)) { fl ->
      tailrec fun go(ff: FList<Int>): Unit = if (ff.fempty()) Unit else {
        ff.fhead()?.let {
          if (ff.flast() != it) {
            val aut = ff.frotr()
            aut.ftail().fhead() shouldBe it
            aut.fhead() shouldBe ff.flast()
          }
        }
        go(ff.ftail())
      }
      go(fl)
    }
  }

  test("fswaph (A, B, C).fswaph() becomes (B, A, C)") {
    intListOfNone.fswaph() shouldBe intListOfNone
    intListOfOne.fswaph() shouldBe intListOfOne
    intListOfTwo.fswaph() shouldBe FLCons(2, FLCons(1, FLNil))
    intListOfThree.fswaph() shouldBe FLCons(2, FLCons(1, FLCons(3, FLNil)))
  }

  test("fswaph properties") {
    checkAll(repeats.first, Arb.flist(Arb.int(),repeats.second..repeats.third)) { fl ->
      tailrec fun go(ff: FList<Int>): Unit = if (ff.fempty()) Unit else {
        ff.fhead()?.let {
          if (ff.flast() != it) {
            val aut = ff.fswaph()
            aut.ftail().fhead() shouldBe it
            aut.fhead() shouldBe ff.ftail().fhead()
          }
        }
        go(ff.ftail())
      }
      go(fl)
    }
  }

  test("fzip") {
    (intListOfNone.fzip(intListOfNone) === intListOfNone) shouldBe true
    (intListOfNone.fzip(intListOfOne) === intListOfNone) shouldBe true
    (intListOfOne.fzip(intListOfNone) === intListOfNone) shouldBe true
    intListOfOne.fzip(intListOfOne) shouldBe FLCons(Pair(1,1), FLNil)
    intListOfOne.fzip(intListOfTwo) shouldBe FLCons(Pair(1,1), FLNil)
    (intListOfTwo.fzip(intListOfNone) === intListOfNone) shouldBe true
    intListOfTwo.fzip(intListOfOne) shouldBe FLCons(Pair(1,1), FLNil)
    intListOfTwo.fzip(intListOfTwo) shouldBe FLCons(Pair(1,1), FLCons(Pair(2,2), FLNil))
    intListOfTwo.fzip(intListOfTwoB) shouldBe FLCons(Pair(1,2), FLCons(Pair(2,3), FLNil))
    intListOfTwoB.fzip(intListOfTwo) shouldBe FLCons(Pair(2,1), FLCons(Pair(3,2), FLNil))
    intListOfTwoB.fzip(intListOfThree) shouldBe FLCons(Pair(2,1), FLCons(Pair(3,2), FLNil))
  }

})
