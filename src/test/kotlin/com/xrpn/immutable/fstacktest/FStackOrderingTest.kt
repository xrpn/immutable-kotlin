package com.xrpn.immutable.fstacktest

import com.xrpn.imapi.IMOrdered
import com.xrpn.imapi.IMStack
import com.xrpn.immutable.*
import com.xrpn.immutable.FStack.Companion.emptyIMStack
import com.xrpn.immutable.emptyArrayOfStr
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fstack

private val itemA = "A"
private val itemB = "B"
private val itemC = "C"
private val strStackOfNone = FStack.of(*emptyArrayOfStr)
private val strStackOfOneA = FStack.of(*arrayOf<String>(itemA))
private val strStackOfOneB = FStack.of(*arrayOf<String>(itemB))
private val strStackOfTwoAB = FStack.of(*arrayOf<String>(itemA, itemB))
private val strStackOfTwoBA = FStack.of(*arrayOf<String>(itemB, itemA))
private val strStackOfTwoBC = FStack.of(*arrayOf<String>(itemB, itemC))
private val strStackOfThree = FStack.of(*arrayOf<String>(itemA, itemB, itemC))
private val strStackOfThreer = FStack.of(*arrayOf<String>(itemC, itemB, itemA))
private val intListOfThree: IMOrdered<Int> = FList.of(*arrayOf<Int>(1,3,2))



class FStackOrderingTest : FunSpec({

  tailrec fun prepare(s: FStack<String>, acc: FStack<String>): FStack<String> = if (s.fempty()) acc else {
    val (a: String?, b: FStack<String>) = s.fpopAndRemainder()
    val newAcc = a?.let { acc.fpush(it) } ?: acc
    prepare(b, newAcc)
  }

  fun compare(a: FStack<String>, b: FStack<String>, acc: Boolean): Boolean = if (!acc) false else if (0==a.fsize() && 0==b.fsize()) acc else {
    val (sa, sb) = b.fpop()
    val (qa, qb) = a.fpop()
    val newAcc = acc && a.fsize() == b.fsize() && (sa == qa)
    compare(qb, sb, newAcc)
  }

  val repeats = Triple(5, 3, 10)

  beforeTest {}


  test("fdrop 0") {
    (strStackOfNone.fdrop(0) === strStackOfNone) shouldBe true
    (strStackOfOneA.fdrop(0) === strStackOfOneA) shouldBe true
    (strStackOfTwoAB.fdrop(0) === strStackOfTwoAB) shouldBe true
    (strStackOfThree.fdrop(0) === strStackOfThree) shouldBe true
  }

  test("fdrop 1") {
    strStackOfNone.fdrop(1) shouldBe strStackOfNone
    strStackOfOneA.fdrop(1) shouldBe strStackOfNone
    strStackOfTwoBA.fdrop(1) shouldBe strStackOfOneA
    strStackOfThree.fdrop(1) shouldBe strStackOfTwoBC
  }

  test("fdrop negative") {
    (strStackOfNone.fdrop(-1) === strStackOfNone) shouldBe true
    (strStackOfOneA.fdrop(-1) === strStackOfOneA) shouldBe true
    (strStackOfTwoAB.fdrop(-1) === strStackOfTwoAB) shouldBe true
    (strStackOfThree.fdrop(-1) === strStackOfThree) shouldBe true
  }

  test("fnext") {
    strStackOfNone.fnext() shouldBe Pair(null, strStackOfNone)
    strStackOfOneA.fnext() shouldBe Pair(itemA, strStackOfNone)
    strStackOfTwoAB.fnext() shouldBe Pair(itemA, strStackOfOneB)
    strStackOfTwoBA.fnext() shouldBe Pair(itemB, strStackOfOneA)
    strStackOfThree.fnext() shouldBe Pair(itemA, strStackOfTwoBC)
  }

  test("freverse") {
    (strStackOfNone.freverse() === emptyIMStack<Int>()) shouldBe true
    strStackOfOneA.freverse().equal(strStackOfOneA) shouldBe true

    strStackOfTwoBA.freverse().equal(strStackOfTwoAB) shouldBe true
    strStackOfTwoAB.freverse().equal(strStackOfTwoBA) shouldBe true
    compare(strStackOfTwoAB, prepare(strStackOfTwoAB.freverse(), FStack.emptyIMStack()), true) shouldBe true
    compare(strStackOfTwoAB, prepare(strStackOfTwoBA, FStack.emptyIMStack()), true) shouldBe true
    compare(strStackOfTwoBA, prepare(strStackOfTwoBA.freverse(), FStack.emptyIMStack()), true) shouldBe true
    compare(strStackOfTwoBA, prepare(strStackOfTwoAB, FStack.emptyIMStack()), true) shouldBe true

    strStackOfThree.freverse().equal(strStackOfThreer) shouldBe true
    strStackOfThreer.freverse().equal(strStackOfThree) shouldBe true
    compare(strStackOfThree, prepare(strStackOfThree.freverse(), FStack.emptyIMStack()), true) shouldBe true
    compare(strStackOfThree, prepare(strStackOfThreer, FStack.emptyIMStack()), true) shouldBe true
    compare(strStackOfThreer, prepare(strStackOfThreer.freverse(), FStack.emptyIMStack()), true) shouldBe true
    compare(strStackOfThreer, prepare(strStackOfThree, FStack.emptyIMStack()), true) shouldBe true

  }

  test("frotl (A, B, C).frotl() becomes (B, C, A)") {
    strStackOfNone.frotl() shouldBe strStackOfNone
    strStackOfOneA.frotl() shouldBe strStackOfOneA
    strStackOfTwoAB.frotl() shouldBe strStackOfTwoBA
  }

  test("frotl properties") {
    checkAll(repeats.first, Arb.fstack(Arb.int(),repeats.second..repeats.third)) { fl ->
      tailrec fun go(ff: FStack<Int>): Unit = if (ff.isEmpty()) Unit else {
        val (top, shortStack) = ff.fpop()
        top?.let {
          if (ff.toFList().asList().last() != it) {
            val aut: IMStack<Int> = ff.frotl()
            aut.toIMList().flast() shouldBe it
            aut.ftop() shouldBe shortStack.ftop()
          }
        }
        go(shortStack)
      }
      go(fl)
    }
  }

  test("frotr (A, B, C).frotr() becomes (C, A, B)") {
    strStackOfNone.frotr() shouldBe strStackOfNone
    strStackOfOneA.frotr() shouldBe strStackOfOneA
    strStackOfTwoAB.frotr() shouldBe strStackOfTwoBA
  }

  test("frotr properties") {
    checkAll(repeats.first, Arb.fstack(Arb.int(),repeats.second..repeats.third)) { fl ->
      tailrec fun go(ff: FStack<Int>): Unit = if (ff.isEmpty()) Unit else {
        val (top, shortStack) = ff.fpop()
        top?.let {
          if (ff.toIMList().flast() != it) {
            val aut = ff.frotr()
            aut.fpop().second.ftop() shouldBe it
            aut.ftop() shouldBe ff.toIMList().flast()
          }
        }
        go(shortStack)
      }
      go(fl)
    }
  }

  test("fswaph (A, B, C).fswaph() becomes (B, A, C)") {
    strStackOfNone.fswaph() shouldBe strStackOfNone
    strStackOfOneA.fswaph() shouldBe strStackOfOneA
    strStackOfTwoAB.fswaph() shouldBe strStackOfTwoBA
  }

  test("fswaph properties") {
    checkAll(repeats.first, Arb.fstack(Arb.int(),repeats.second..repeats.third)) { fl ->
      tailrec fun go(ff: FStack<Int>): Unit = if (ff.isEmpty()) Unit else {
        val (first, shortStack) = ff.fpop()
        first?.let {
          if (ff.toIMList().flast() != it) {
            val aut = ff.fswaph()
            aut.fpop().second.ftop() shouldBe it
            aut.ftop() shouldBe shortStack.ftop()
          }
        }
        go(shortStack)
      }
      go(fl)
    }
  }

  test("fzip") {
    (strStackOfNone.fzip(strStackOfNone) === strStackOfNone) shouldBe true
    (strStackOfNone.fzip(strStackOfOneA) === strStackOfNone) shouldBe true
    (strStackOfOneA.fzip(strStackOfNone) === strStackOfNone) shouldBe true
    strStackOfOneB.fzip(strStackOfOneA) shouldBe FStack.of(Pair(itemB, itemA))
    strStackOfOneB.fzip(strStackOfTwoAB) shouldBe FStack.of(Pair(itemB, itemA))
    (strStackOfTwoAB.fzip(strStackOfNone) === strStackOfNone) shouldBe true
    strStackOfTwoAB.fzip(strStackOfOneB) shouldBe FStack.of(Pair(itemA, itemB))
    strStackOfTwoAB.fzip(strStackOfTwoAB) shouldBe FStack.of(Pair(itemA, itemA), Pair(itemB, itemB))
    strStackOfTwoAB.fzip(strStackOfTwoBA) shouldBe FStack.of(Pair(itemA, itemB), Pair(itemB, itemA))
    strStackOfTwoBA.fzip(strStackOfTwoAB) shouldBe FStack.of(Pair(itemB, itemA), Pair(itemA, itemB))
    strStackOfThree.fzip(strStackOfThree) shouldBe FStack.of(Pair(itemA, itemA), Pair(itemB, itemB), Pair(itemC, itemC))
    strStackOfThree.fzip(intListOfThree) shouldBe FStack.of(Pair(itemA, 1), Pair(itemB, 3), Pair(itemC, 2))
  }


})
