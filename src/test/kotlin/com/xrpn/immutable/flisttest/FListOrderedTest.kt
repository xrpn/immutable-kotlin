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
private val intListOfTwo: IMOrdered<Int> = FList.of(*arrayOf<Int>(1,2))
private val intListOfThree: IMOrdered<Int> = FList.of(*arrayOf<Int>(1,2,3))

class FListOrderedTest : FunSpec({

  val repeats = Triple(5, 3, 10)

  beforeTest {}

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
      tailrec fun go(ff: FList<Int>): Unit = if (ff.isEmpty()) Unit else {
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
      tailrec fun go(ff: FList<Int>): Unit = if (ff.isEmpty()) Unit else {
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
      tailrec fun go(ff: FList<Int>): Unit = if (ff.isEmpty()) Unit else {
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

})
