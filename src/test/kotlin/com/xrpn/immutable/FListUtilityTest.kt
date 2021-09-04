package com.xrpn.immutable

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.flist
import java.util.concurrent.atomic.AtomicInteger

private val intListOfNone = FList.of(*arrayOf<Int>())
private val intListOfOne = FList.of(*arrayOf<Int>(1))
private val intListOfTwo = FList.of(*arrayOf<Int>(1,2))
private val intListOfThree = FList.of(*arrayOf<Int>(1,2,3))

class FListUtilityTest : FunSpec({

  val repeats = 50

  beforeTest {}

  test("equal") {
    intListOfNone.equal(intListOfNone) shouldBe true

    intListOfNone.equal(intListOfOne) shouldBe false
    intListOfOne.equal(intListOfNone) shouldBe false
    intListOfOne.equal(FLCons(1, FLNil)) shouldBe true
    intListOfOne.equal(intListOfOne) shouldBe true

    intListOfOne.equal(intListOfTwo) shouldBe false
    intListOfTwo.equal(intListOfOne) shouldBe false
    intListOfTwo.equal(FLCons(1, FLCons(2, FLNil))) shouldBe true
    intListOfTwo.equal(intListOfTwo.freverse()) shouldBe false
    intListOfTwo.equal(intListOfTwo) shouldBe true

    intListOfThree.equal(intListOfTwo) shouldBe false
    intListOfTwo.equal(intListOfThree) shouldBe false
    intListOfThree.equal(FLCons(1, FLCons(3, FLNil))) shouldBe false
    intListOfThree.equal(FLCons(3, FLCons(1, FLNil))) shouldBe false
  }

  test("forEach") {
    val counter = AtomicInteger(0)
    val summer = AtomicInteger(0)
    val doCount: (Int) -> Unit = { counter.incrementAndGet() }
    val doSum: (Int) -> Unit = { v -> summer.addAndGet(v) }
    intListOfNone.fforEach(doCount)
    counter.get() shouldBe 0
    intListOfNone.fforEach(doSum)
    summer.get() shouldBe 0
    counter.set(0)
    summer.set(0)
    checkAll(repeats, Arb.flist<Int, Int>(Arb.int(),20..100)) { fl ->
      val oraSum = fl.ffoldLeft(0){ acc, el -> acc + el }
      fl.fforEach(doCount)
      counter.get() shouldBe fl.size
      counter.set(0)
      fl.fforEach(doSum)
      summer.get() shouldBe oraSum
      summer.set(0)
    }
  }

  test("copy") {
    intListOfNone.copy() shouldBe intListOfNone
    checkAll(repeats, Arb.flist<Int, Int>(Arb.int(),20..100)) { fl ->
      val fl1 = fl.copy()
      (fl1 === fl) shouldBe false
      fl.equal(fl1) shouldBe true
      fl1.equal(fl) shouldBe true
    }
  }

  test("copyToMutableList") {
    intListOfNone.copyToMutableList() shouldBe mutableListOf()
    checkAll(repeats, Arb.flist<Int, Int>(Arb.int(),20..100)) { fl ->
      val ml: MutableList<Int> = fl.copyToMutableList()
      (fl == ml) shouldBe true
      (ml == fl) shouldBe true
    }
  }

})
