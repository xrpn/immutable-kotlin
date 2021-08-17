package com.xrpn.immutable

import com.xrpn.immutable.FList.Companion.toIMList
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.xrpn.flist
import java.util.concurrent.atomic.AtomicInteger

private val intListOfNone = FList.of(*arrayOf<Int>())
private val intListOfOne = FList.of(*arrayOf<Int>(1))
private val intListOfTwoA = FList.of(*arrayOf<Int>(1,3))
private val intListOfTwo = FList.of(*arrayOf<Int>(1,2))
private val intListOfTwoC = FList.of(*arrayOf<Int>(1,4))
private val intListOfThree = FList.of(*arrayOf<Int>(1,2,3))
private val intListOfThreeA = FList.of(*arrayOf<Int>(1,2,5))
private val intListOfSix = FList.of(*arrayOf<Int>(1,2,3,3,2,1))
private val strListOfNone = FList.of(*arrayOf<String>())
private val strListOfOne = FList.of(*arrayOf<String>("a"))
private val strListOfTwo = FList.of(*arrayOf<String>("a","b"))
private val strListOfThree = FList.of(*arrayOf<String>("a","b","c"))

class FListCompanionTest : FunSpec({

  val repeats = 10

  beforeTest {}

  // traversing stowaway

  test("forEach") {
    val counter = AtomicInteger(0)
    val summer = AtomicInteger(0)
    val doCount: (Int) -> Unit = { counter.incrementAndGet() }
    val doSum: (Int) -> Unit = { v -> summer.addAndGet(v) }
    checkAll(50, Arb.flist<Int, Int>(Arb.int(),20..100)) { fl ->
      val oraSum = fl.fold(0){ acc, el -> acc + el }
      fl.forEach(doCount)
      counter.get() shouldBe fl.size
      counter.set(0)
      fl.forEach(doSum)
      summer.get() shouldBe oraSum
      summer.set(0)
    }
  }

  test("copyToMutableList") {
    checkAll(50, Arb.flist<Int, Int>(Arb.int(),20..100)) { fl ->
      val ml = fl.copyToMutableList()
      (fl == ml) shouldBe true
    }
  }

  test("copy") {
    checkAll(50, Arb.flist<Int, Int>(Arb.int(),20..100)) { fl ->
      val fl1 = fl.copy()
      (fl1 === fl) shouldBe false
      fl.equal(fl1) shouldBe true
    }
  }

  // ==============================================================

  test("co.emptyFList") {
    FList.emptyIMList<Int>().isEmpty() shouldBe true
    FList.emptyIMList<Int>().fempty() shouldBe true
  }

  test("co.of varargs") {
    intListOfNone shouldBe FLNil
    intListOfOne shouldBe FLCons(1,FLNil)
    intListOfTwo shouldBe FLCons(1,FLCons(2,FLNil))
    intListOfThree shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
  }

  test("co.of iterator") {
    FList.of(arrayOf<Int>().iterator()) shouldBe FLNil
    FList.of(arrayOf<Int>(1).iterator()) shouldBe FLCons(1,FLNil)
    FList.of(arrayOf<Int>(1,2).iterator()) shouldBe FLCons(1,FLCons(2,FLNil))
    FList.of(arrayOf<Int>(1,2,3).iterator()) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
  }

  test("co.ofMap iterator") {
    strListOfNone shouldBe FList.ofMap(arrayOf<Int>().iterator(), ::fidentity)
    strListOfOne shouldBe FList.ofMap(arrayOf(0).iterator()) { a -> (a+'a'.code).toChar().toString() }
    strListOfTwo shouldBe FList.ofMap(arrayOf(0, 1).iterator()) { a -> (a+'a'.code).toChar().toString() }
    strListOfThree shouldBe FList.ofMap(arrayOf(0, 1, 2).iterator()) { a -> (a+'a'.code).toChar().toString() }
  }

  test("co.of List") {
    FList.of(emptyList()) shouldBe FLNil
    FList.of(listOf(1)) shouldBe FLCons(1,FLNil)
    FList.of(listOf(1,2)) shouldBe FLCons(1,FLCons(2,FLNil))
    FList.of(listOf(1,2,3).iterator()) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
  }

  test("co.ofMap List") {
    strListOfNone shouldBe FList.ofMap(emptyList<Int>()) { a -> a }
    strListOfOne shouldBe FList.ofMap(listOf(0)) { a -> (a+'a'.code).toChar().toString() }
    strListOfTwo shouldBe FList.ofMap(listOf(0, 1)) { a -> (a+'a'.code).toChar().toString() }
    strListOfThree shouldBe FList.ofMap(listOf(0, 1, 2)) { a -> (a+'a'.code).toChar().toString() }
  }

  test("co.append") {
    intListOfNone.fappend(0) shouldBe FLCons(0,FLNil)
    intListOfOne.fappend(0) shouldBe FLCons(1,FLCons(0,FLNil))
    FList.of(*arrayOf<Int>(2,1)).fappend(0) shouldBe FLCons(2, FLCons(1,FLCons(0,FLNil)))
    FList.of(*arrayOf<Int>(3,2,1)).fappend(0) shouldBe
            FLCons(3,FLCons(2,FLCons(1,FLCons(0,FLNil))))
  }

  test("co.appendAll") {
    intListOfNone.fappendAll(intListOfNone) shouldBe FLNil
    intListOfOne.fappendAll(intListOfNone) shouldBe intListOfOne
    intListOfOne.fappendAll(intListOfOne) shouldBe FLCons(1,FLCons(1,FLNil))
    intListOfOne.fappendAll(intListOfTwo) shouldBe FLCons(1,FLCons(1,FLCons(2,FLNil)))
    FList.of(*arrayOf<Int>(2,1)).fappendAll(intListOfThree) shouldBe FLCons(2, FLCons(1, intListOfThree))
    FList.of(*arrayOf<Int>(3,2,1)).fappendAll(intListOfThree) shouldBe
            FLCons(3,FLCons(2,FLCons(1,intListOfThree)))
  }

  test("co.hasSubsequence") {
    intListOfNone.fhasSubsequence(FList.of(*arrayOf<Int>())) shouldBe true

    intListOfOne.fhasSubsequence(intListOfNone) shouldBe true
    intListOfOne.fhasSubsequence(intListOfOne) shouldBe true
    intListOfOne.fhasSubsequence(intListOfTwo) shouldBe false

    intListOfTwo.fhasSubsequence(intListOfNone) shouldBe true
    intListOfTwo.fhasSubsequence(intListOfOne) shouldBe true
    intListOfTwo.fhasSubsequence(FLCons(2, FLNil)) shouldBe true
    intListOfTwo.fhasSubsequence(intListOfTwo) shouldBe true
    intListOfTwo.fhasSubsequence(intListOfTwo.freverse()) shouldBe false
    intListOfTwo.fhasSubsequence(intListOfThree) shouldBe false

    intListOfThree.fhasSubsequence(intListOfNone) shouldBe true
    intListOfThree.fhasSubsequence(intListOfOne) shouldBe true
    intListOfThree.fhasSubsequence(FLCons(2, FLNil)) shouldBe true
    intListOfThree.fhasSubsequence(FLCons(3, FLNil)) shouldBe true
    intListOfThree.fhasSubsequence(intListOfTwo) shouldBe true
    intListOfThree.fhasSubsequence(FLCons(2, FLCons(3, FLNil))) shouldBe true
    intListOfThree.fhasSubsequence(FLCons(1, FLCons(3, FLNil))) shouldBe false
    intListOfThree.fhasSubsequence(intListOfTwo.freverse()) shouldBe false
    intListOfThree.fhasSubsequence(intListOfThree.freverse()) shouldBe false
    intListOfThree.fhasSubsequence(intListOfThree) shouldBe true
  }

  test("co.prepend") {
    intListOfNone.fprepend(0) shouldBe FLCons(0,FLNil)
    intListOfOne.fprepend(0) shouldBe FLCons(0,FLCons(1,FLNil))
    FList.of(*arrayOf<Int>(2,1)).fprepend(0) shouldBe FLCons(0, FLCons(2,FLCons(1,FLNil)))
    FList.of(*arrayOf<Int>(3,2,1)).fprepend(0) shouldBe
            FLCons(0,FLCons(3,FLCons(2,FLCons(1,FLNil))))
  }

  test("co.prependAll") {
    intListOfNone.fprependAll(intListOfNone) shouldBe FLNil
    intListOfOne.fprependAll(intListOfNone) shouldBe intListOfOne
    intListOfOne.fprependAll(intListOfOne) shouldBe FLCons(1,FLCons(1,FLNil))
    intListOfOne.fprependAll(intListOfTwo) shouldBe FLCons(1,FLCons(2,FLCons(1,FLNil)))
    FList.of(*arrayOf<Int>(2,1)).fprependAll(intListOfThree) shouldBe intListOfThree.fappend(2).fappend(1)
    FList.of(*arrayOf<Int>(3,2,1)).fprependAll(intListOfThree) shouldBe intListOfSix
  }

  test("co.remove") {
    intListOfNone.fdropItem(0) shouldBe FLNil
    intListOfOne.fdropItem(0) shouldBe intListOfOne
    intListOfOne.fdropItem(1) shouldBe FLNil
    intListOfOne.fdropItem(2) shouldBe intListOfOne
    FList.of(*arrayOf<Int>(2,1)).fdropItem(2) shouldBe intListOfOne
    FList.of(*arrayOf<Int>(2,1,2)).fdropItem(2) shouldBe intListOfOne
    FList.of(*arrayOf<Int>(1, 2, 1, 2)).fdropItem(2) shouldBe FLCons(1, intListOfOne)
    intListOfSix.fdropItem(3) shouldBe FList.of(*arrayOf<Int>(1, 2, 2, 1))
    intListOfSix.fdropItem(2) shouldBe FList.of(*arrayOf<Int>(1, 3, 3, 1))
    intListOfSix.fdropItem(1) shouldBe FList.of(*arrayOf<Int>(2, 3, 3, 2))
  }

  test("co.removeAll") {
    intListOfNone.fdropAll(intListOfNone) shouldBe FLNil
    intListOfOne.fdropAll(intListOfNone) shouldBe intListOfOne
    intListOfOne.fdropAll(intListOfOne) shouldBe FLNil
    intListOfOne.fdropAll(intListOfTwo) shouldBe FLNil
    FList.of(*arrayOf<Int>(2,1)).fdropAll(intListOfThree) shouldBe FLNil
    FList.of(*arrayOf<Int>(3,2,1)).fdropAll(intListOfTwo) shouldBe FLCons(3, FLNil)
  }

  test("co.fappend") {
    FList.flAppend(intListOfNone, FList.of(*arrayOf<Int>())) shouldBe FLNil
    FList.flAppend(intListOfNone, FList.of(*arrayOf<Int>(2))) shouldBe FLCons(2,FLNil)
    FList.flAppend(intListOfOne, FList.of(*arrayOf<Int>())) shouldBe FLCons(1,FLNil)
    FList.flAppend(intListOfOne, FList.of(*arrayOf<Int>(2))) shouldBe FLCons(1,FLCons(2,FLNil))
    FList.flAppend(intListOfTwo, FList.of(*arrayOf<Int>(3,4))) shouldBe
            FLCons(1,FLCons(2,FLCons(3,FLCons(4,FLNil))))
  }

  test("co.fappendNested") {
    FList.fappendNested(FList.of(*arrayOf(FList.of(*arrayOf<Int>())))) shouldBe FLNil
    FList.fappendNested(FList.of(*arrayOf(FList.of(*arrayOf<Int>(1))))) shouldBe FLCons(1,FLNil)
    FList.fappendNested(FList.of(*arrayOf(intListOfNone, FList.of(*arrayOf<Int>())))) shouldBe FLNil
    FList.fappendNested(FList.of(*arrayOf(intListOfNone, FList.of(*arrayOf<Int>(1))))) shouldBe FLCons(1,FLNil)
    FList.fappendNested(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>())))) shouldBe FLCons(1,FLNil)
    FList.fappendNested(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>(2))))) shouldBe FLCons(1,FLCons(2,FLNil))
    FList.fappendNested(FList.of(*arrayOf(intListOfTwo, FList.of(*arrayOf<Int>(3))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
    FList.fappendNested(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>(2,3))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
    FList.fappendNested(FList.of(*arrayOf(intListOfTwo, FList.of(*arrayOf<Int>(3,4))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLCons(4,FLNil))))
  }

  test("co.fhasSubsequence") {
    FList.flHasSubsequence(intListOfNone, FList.of(*arrayOf<Int>())) shouldBe true

    FList.flHasSubsequence(intListOfOne, intListOfNone) shouldBe true
    FList.flHasSubsequence(intListOfOne, intListOfOne) shouldBe true
    FList.flHasSubsequence(intListOfOne, intListOfTwo) shouldBe false

    FList.flHasSubsequence(intListOfTwo, intListOfNone) shouldBe true
    FList.flHasSubsequence(intListOfTwo, intListOfOne) shouldBe true
    FList.flHasSubsequence(intListOfTwo, FLCons(2, FLNil)) shouldBe true
    FList.flHasSubsequence(intListOfTwo, intListOfTwo) shouldBe true
    FList.flHasSubsequence(intListOfTwo, intListOfTwo.freverse()) shouldBe false
    FList.flHasSubsequence(intListOfTwo, intListOfThree) shouldBe false

    FList.flHasSubsequence(intListOfThree, intListOfNone) shouldBe true
    FList.flHasSubsequence(intListOfThree, intListOfOne) shouldBe true
    FList.flHasSubsequence(intListOfThree, FLCons(2, FLNil)) shouldBe true
    FList.flHasSubsequence(intListOfThree, FLCons(3, FLNil)) shouldBe true
    FList.flHasSubsequence(intListOfThree, intListOfTwo) shouldBe true
    FList.flHasSubsequence(intListOfThree, FLCons(2, FLCons(3, FLNil))) shouldBe true
    FList.flHasSubsequence(intListOfThree, FLCons(1, FLCons(3, FLNil))) shouldBe false
    FList.flHasSubsequence(intListOfThree, intListOfTwo.freverse()) shouldBe false
    FList.flHasSubsequence(intListOfThree, intListOfThree.freverse()) shouldBe false
    FList.flHasSubsequence(intListOfThree, intListOfThree) shouldBe true
  }

  test("co.fsetHead") {
    FList.flSetHead(1, FList.of(*arrayOf<Int>())) shouldBe FLCons(1,FLNil)
    FList.flSetHead(1, FList.of(*arrayOf<Int>(2))) shouldBe FLCons(1,FLCons(2,FLNil))
    FList.flSetHead(1, FList.of(*arrayOf<Int>(2, 3))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
  }

  test("co.fsetLast") {
    FList.flSetLast(intListOfNone,0) shouldBe FLCons(0,FLNil)
    FList.flSetLast(intListOfOne,0) shouldBe FLCons(1,FLCons(0,FLNil))
    FList.flSetLast(FList.of(*arrayOf<Int>(2,1)),0) shouldBe FLCons(2, FLCons(1,FLCons(0,FLNil)))
    FList.flSetLast(FList.of(*arrayOf<Int>(3,2,1)), 0) shouldBe
            FLCons(3,FLCons(2,FLCons(1,FLCons(0,FLNil))))
  }

  test("co.equal") {
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

  test("co.toIMList") {
    Arb.list(Arb.int()).checkAll(repeats) { l ->
      l.toIMList() shouldBe FList.of(l)
    }
  }

  test("co.equal2") {
    FList.equal2(intListOfNone, FList.of(*arrayOf<Int>())) shouldBe true
    FList.equal2(FList.of(*arrayOf(1)), FList.of(*arrayOf<Int>())) shouldBe false
    FList.equal2(intListOfNone, FList.of(*arrayOf(1))) shouldBe false
    FList.equal2(intListOfOne, FList.of(*arrayOf<Int>(1))) shouldBe true
    FList.equal2(FList.of(*arrayOf(1)), FList.of(*arrayOf<Int>(1, 2))) shouldBe false
    FList.equal2(FList.of(*arrayOf<Int>(1, 2)), FList.of(*arrayOf(1))) shouldBe false
    FList.equal2(FList.of(*arrayOf<Int>(1, 2)), FList.of(*arrayOf(1, 2))) shouldBe true
  }

  test("co.isNested") {
    FList.isNested(FList.of(*arrayOf(FList.of(*arrayOf<Int>())))) shouldBe true
    FList.isNested(FList.of(*arrayOf(FList.of(*arrayOf<Int>(1))))) shouldBe true
    FList.isNested(intListOfNone) shouldBe false
    FList.isNested(intListOfOne) shouldBe false
  }

  test("co.firstNotEmpty") {
    val aux0: FList<FList<Int>> = FList.of(*arrayOf(FList.of(*arrayOf<Int>())))
    FList.firstNotEmpty(aux0) shouldBe null
    val aux1: FList<FList<Int>> = FList.of(*arrayOf(FList.of(*arrayOf<Int>(1))))
    FList.firstNotEmpty(aux1) shouldBe FLCons(1, FLNil)
    val aux1a: FList<FList<Int>> = FList.of(*arrayOf(FList.of(*arrayOf<Int>())), *arrayOf(FList.of(*arrayOf<Int>(3))))
    FList.firstNotEmpty(aux1a) shouldBe FLCons(3, FLNil)
    val aux2: FList<FList<Int>> = FList.of(*arrayOf(FList.of(*arrayOf<Int>())), *arrayOf(FList.of(*arrayOf<Int>(1, 2))))
    FList.firstNotEmpty(aux2) shouldBe FLCons( 1, FLCons(2, FLNil))
    val aux2a: FList<FList<Int>> = FList.of(*arrayOf(FList.of(*arrayOf<Int>())), *arrayOf(FList.of(*arrayOf<Int>())))
    FList.firstNotEmpty(aux2a) shouldBe null
    val aux_a = FLCons(aux0, FLCons(aux1, FLNil))
    FList.firstNotEmpty(aux_a) shouldBe aux0

    FList.firstNotEmpty(intListOfNone) shouldBe null
    FList.firstNotEmpty(intListOfOne) shouldBe null
  }

  test("co.fflatten") {
    FList.fflatten(FList.of(*arrayOf(FList.of(*arrayOf<Int>())))) shouldBe FLNil
    FList.fflatten(FList.of(*arrayOf(FList.of(*arrayOf<Int>(1))))) shouldBe FLCons(1,FLNil)
    FList.fflatten(FList.of(*arrayOf(intListOfNone, FList.of(*arrayOf<Int>())))) shouldBe FLNil
    FList.fflatten(FList.of(*arrayOf(intListOfNone, FList.of(*arrayOf<Int>(1))))) shouldBe FLCons(1,FLNil)
    FList.fflatten(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>())))) shouldBe FLCons(1,FLNil)
    FList.fflatten(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>(2))))) shouldBe FLCons(1,FLCons(2,FLNil))
    FList.fflatten(FList.of(*arrayOf(intListOfTwo, FList.of(*arrayOf<Int>(3))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
    FList.fflatten(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>(2,3))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
    FList.fflatten(FList.of(*arrayOf(intListOfTwo, FList.of(*arrayOf<Int>(3,4))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLCons(4,FLNil))))
  }

  test("co.freduceLeft") {

    val ss = { acc: Int, b: Int -> b - acc }

    FList.freduceLeft(intListOfNone, ss) shouldBe null
    FList.freduceLeft(FList.of(*arrayOf(1)), ss) shouldBe 1

    FList.freduceLeft(intListOfOne, ss) shouldBe 1
    FList.freduceLeft(intListOfTwo, ss) shouldBe 1
    FList.freduceLeft(intListOfTwoA, ss) shouldBe 2
    FList.freduceLeft(intListOfTwoC, ss) shouldBe 3
    FList.freduceLeft(intListOfThree, ss) shouldBe 2
    FList.freduceLeft(intListOfThreeA, ss) shouldBe 4

    FList.freduceLeft(FList.of(*arrayOf<Int>(2, 1)), ss) shouldBe -1
    FList.freduceLeft(FList.of(*arrayOf<Int>(3, 1, 1)), ss) shouldBe 3
    FList.freduceLeft(FList.of(*arrayOf<Int>(4, 1, 2)), ss) shouldBe 5
  }

  test("co.toArray") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val ary: Array<Int> = FList.toArray(fl)
      fl shouldBe FList.of(ary.iterator())
      fl shouldBe FList.of(*ary)
      fl shouldNotBe FList.of(ary)
    }
  }

  test("co.toFList") {
    Arb.list(Arb.int()).checkAll(repeats) { l ->
      l shouldBe FList.of(l)
    }
  }

})
