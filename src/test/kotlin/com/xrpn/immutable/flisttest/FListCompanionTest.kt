package com.xrpn.immutable.flisttest

import com.xrpn.imapi.IMList
import com.xrpn.imapi.IMListEqual2
import com.xrpn.immutable.*
import com.xrpn.immutable.FList.Companion.toIMList
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll
import io.kotest.xrpn.flist

private val intListOfNone = FList.of(*emptyArrayOfInt)
private val intListOfOne = FList.of(*arrayOf<Int>(1))
private val intListOfTwo = FList.of(*arrayOf<Int>(1,2))
private val intListOfThree = FList.of(*arrayOf<Int>(1,2,3))
private val strListOfNone = FList.of(*emptyArrayOfStr)
private val strListOfOne = FList.of(*arrayOf<String>("a"))
private val strListOfTwo = FList.of(*arrayOf<String>("a","b"))
private val strListOfThree = FList.of(*arrayOf<String>("a","b","c"))

class FListCompanionTest : FunSpec({

  val repeats = 10

  beforeTest {}

  test("equals") {
    FList.emptyIMList<Int>().equals(null) shouldBe false
    FList.emptyIMList<Int>().equals(emptyList<Int>()) shouldBe true
    FList.emptyIMList<Int>().equals(1) shouldBe false
    /* Sigh... */ intListOfNone.equals(strListOfNone) shouldBe true
    intListOfTwo.equals(null) shouldBe false
    intListOfTwo.equals(strListOfNone) shouldBe false
    intListOfTwo.equals(strListOfTwo) shouldBe false
    intListOfTwo.equals(emptyList<Int>()) shouldBe false
    intListOfTwo.equals(listOf("foobar")) shouldBe false
    intListOfTwo.equals(listOf("foobar","babar")) shouldBe false
    intListOfTwo.equals(1) shouldBe false
  }

  test("toString() hashCode()") {
    FList.emptyIMList<Int>().toString() shouldBe "FLNil"
    val aux = FList.emptyIMList<Int>().hashCode()
    for (i in (1..100)) {
       aux shouldBe FList.emptyIMList<Int>().hashCode()
    }
    intListOfTwo.toString() shouldStartWith "${FList::class.simpleName}:"
    val aux2 = intListOfTwo.hashCode()
    for (i in (1..100)) {
      aux2 shouldBe intListOfTwo.hashCode()
    }
    for (i in (1..100)) {
      FLCons.hashCode(intListOfTwo as FLCons) shouldBe intListOfTwo.hashCode()
    }
  }

  // IMListCompanion

  test("co.emptyFList") {
    FList.emptyIMList<Int>().isEmpty() shouldBe true
    FList.emptyIMList<Int>().fempty() shouldBe true
  }

  test("co.[ IMListEqual2 ]") {
    IMListEqual2(intListOfNone, FList.of(*emptyArrayOfInt)) shouldBe true
    IMListEqual2(FList.of(*arrayOf(1)), FList.of(*emptyArrayOfInt)) shouldBe false
    IMListEqual2(intListOfNone, FList.of(*arrayOf(1))) shouldBe false
    IMListEqual2(intListOfOne, FList.of(*arrayOf<Int>(1))) shouldBe true
    IMListEqual2(FList.of(*arrayOf(1)), FList.of(*arrayOf<Int>(1, 2))) shouldBe false
    IMListEqual2(FList.of(*arrayOf<Int>(1, 2)), FList.of(*arrayOf(1))) shouldBe false
    IMListEqual2(FList.of(*arrayOf<Int>(1, 2)), FList.of(*arrayOf(1, 2))) shouldBe true
  }

  test("co.of varargs") {
    intListOfNone shouldBe FLNil
    intListOfOne shouldBe FLCons(1,FLNil)
    intListOfTwo shouldBe FLCons(1,FLCons(2,FLNil))
    intListOfThree shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
  }

  test("co.of iterator") {
    FList.of(emptyArrayOfInt.iterator()) shouldBe FLNil
    FList.of(arrayOf<Int>(1).iterator()) shouldBe FLCons(1,FLNil)
    FList.of(arrayOf<Int>(1,2).iterator()) shouldBe FLCons(1,FLCons(2,FLNil))
    FList.of(arrayOf<Int>(1,2,3).iterator()) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
  }

  test("co.of List") {
    FList.of(emptyList()) shouldBe FLNil
    FList.of(listOf(1)) shouldBe FLCons(1,FLNil)
    FList.of(listOf(1,2)) shouldBe FLCons(1,FLCons(2,FLNil))
    FList.of(listOf(1,2,3)) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
  }

  test("co.of IMList") {
    FList.of(intListOfNone as IMList<Int>) shouldBe FLNil
    FList.of(intListOfOne as IMList<Int>) shouldBe FLCons(1,FLNil)
    FList.of(intListOfTwo as IMList<Int>) shouldBe FLCons(1,FLCons(2,FLNil))
    FList.of(intListOfThree as IMList<Int>) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
  }

  test("co.ofMap iterator") {
    strListOfNone shouldBe FList.ofMap(emptyArrayOfInt.iterator(), ::fidentity)
    strListOfOne shouldBe FList.ofMap(arrayOf(0).iterator()) { a -> (a+'a'.code).toChar().toString() }
    strListOfTwo shouldBe FList.ofMap(arrayOf(0, 1).iterator()) { a -> (a+'a'.code).toChar().toString() }
    strListOfThree shouldBe FList.ofMap(arrayOf(0, 1, 2).iterator()) { a -> (a+'a'.code).toChar().toString() }
  }

  test("co.ofMap List") {
    strListOfNone shouldBe FList.ofMap(emptyList<Int>()) { a -> a }
    strListOfOne shouldBe FList.ofMap(listOf(0)) { a -> (a+'a'.code).toChar().toString() }
    strListOfTwo shouldBe FList.ofMap(listOf(0, 1)) { a -> (a+'a'.code).toChar().toString() }
    strListOfThree shouldBe FList.ofMap(listOf(0, 1, 2)) { a -> (a+'a'.code).toChar().toString() }
  }

  test("co.toIMList") {
    Arb.list(Arb.int()).checkAll(repeats) { l ->
      l.toIMList() shouldBe FList.of(l)
    }
    Arb.set(Arb.int()).checkAll(repeats) { s ->
      s.toIMList() shouldBe FList.of(s.iterator())
    }
  }

  // implementation

  test("co.NOT_FOUND") {
    FList.NOT_FOUND shouldBe -1
  }

  test("co.isNested") {
    FList.isNested(FList.of(*arrayOf(FList.of(*emptyArrayOfInt)))) shouldBe true
    FList.isNested(FList.of(*arrayOf(FList.of(*arrayOf<Int>(1))))) shouldBe true
    FList.isNested(intListOfNone) shouldBe false
    FList.isNested(intListOfOne) shouldBe false
  }

  test("co.firstNotEmpty") {
    val aux0: FList<FList<Int>> = FList.of(*arrayOf(FList.of(*emptyArrayOfInt)))
    FList.firstNotEmpty(aux0) shouldBe null
    val aux1: FList<FList<Int>> = FList.of(*arrayOf(FList.of(*arrayOf<Int>(1))))
    FList.firstNotEmpty(aux1) shouldBe FLCons(1, FLNil)
    val aux1a: FList<FList<Int>> = FList.of(*arrayOf(FList.of(*emptyArrayOfInt)), *arrayOf(FList.of(*arrayOf<Int>(3))))
    FList.firstNotEmpty(aux1a) shouldBe FLCons(3, FLNil)
    val aux2: FList<FList<Int>> = FList.of(*arrayOf(FList.of(*emptyArrayOfInt)), *arrayOf(FList.of(*arrayOf<Int>(1, 2))))
    FList.firstNotEmpty(aux2) shouldBe FLCons( 1, FLCons(2, FLNil))
    val aux2a: FList<FList<Int>> = FList.of(*arrayOf(FList.of(*emptyArrayOfInt)), *arrayOf(FList.of(*emptyArrayOfInt)))
    FList.firstNotEmpty(aux2a) shouldBe null
    val aux_a = FLCons(aux0, FLCons(aux1, FLNil))
    FList.firstNotEmpty(aux_a) shouldBe aux0

    FList.firstNotEmpty(intListOfNone) shouldBe null
    FList.firstNotEmpty(intListOfOne) shouldBe null
  }

  test("co.fflatten") {
    FList.fflatten(FList.of(*arrayOf(FList.of(*emptyArrayOfInt)))) shouldBe FLNil
    FList.fflatten(FList.of(*arrayOf(FList.of(*arrayOf<Int>(1))))) shouldBe FLCons(1,FLNil)
    FList.fflatten(FList.of(*arrayOf(intListOfNone, FList.of(*emptyArrayOfInt)))) shouldBe FLNil
    FList.fflatten(FList.of(*arrayOf(intListOfNone, FList.of(*arrayOf<Int>(1))))) shouldBe FLCons(1,FLNil)
    FList.fflatten(FList.of(*arrayOf(intListOfOne, FList.of(*emptyArrayOfInt)))) shouldBe FLCons(1,FLNil)
    FList.fflatten(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>(2))))) shouldBe FLCons(1,FLCons(2,FLNil))
    FList.fflatten(FList.of(*arrayOf(intListOfTwo, FList.of(*arrayOf<Int>(3))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
    FList.fflatten(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>(2,3))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
    FList.fflatten(FList.of(*arrayOf(intListOfTwo, FList.of(*arrayOf<Int>(3,4))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLCons(4,FLNil))))
  }

  test("co.fappendNested") {
    FList.fappendNested(FList.of(*arrayOf(FList.of(*emptyArrayOfInt)))) shouldBe FLNil
    FList.fappendNested(FList.of(*arrayOf(FList.of(*arrayOf<Int>(1))))) shouldBe FLCons(1,FLNil)
    FList.fappendNested(FList.of(*arrayOf(intListOfNone, FList.of(*emptyArrayOfInt)))) shouldBe FLNil
    FList.fappendNested(FList.of(*arrayOf(intListOfNone, FList.of(*arrayOf<Int>(1))))) shouldBe FLCons(1,FLNil)
    FList.fappendNested(FList.of(*arrayOf(intListOfOne, FList.of(*emptyArrayOfInt)))) shouldBe FLCons(1,FLNil)
    FList.fappendNested(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>(2))))) shouldBe FLCons(1,FLCons(2,FLNil))
    FList.fappendNested(FList.of(*arrayOf(intListOfTwo, FList.of(*arrayOf<Int>(3))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
    FList.fappendNested(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>(2,3))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
    FList.fappendNested(FList.of(*arrayOf(intListOfTwo, FList.of(*arrayOf<Int>(3,4))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLCons(4,FLNil))))
  }

  test("co.toArray") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val ary: Array<Int> = FList.toArray(fl)
      fl shouldBe FList.of(ary.iterator())
      fl shouldBe FList.of(*ary)
      fl shouldNotBe FList.of(ary)
    }
  }
})
