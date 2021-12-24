package com.xrpn.kapi

import com.xrpn.immutable.FLCons
import com.xrpn.immutable.FLNil
import com.xrpn.immutable.FList
import com.xrpn.immutable.emptyArrayOfInt
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

//private val intListOfNone: List<Int> = FList.of(*emptyArrayOfInt)
//private val intListOfOneA: List<Int> = FList.of(*arrayOf<Int>(0))
//private val intListOfOne: List<Int> = FList.of(*arrayOf<Int>(1))
//private val intListOfOneB: List<Int> = FList.of(*arrayOf<Int>(2))
//private val intListOfOneC: List<Int> = FList.of(*arrayOf<Int>(3))
//private val intListOfOneD: List<Int> = FList.of(*arrayOf<Int>(4))
//private val intListOfTwoA: List<Int> = FList.of(*arrayOf<Int>(1,3))
//private val intListOfTwo: List<Int> = FList.of(*arrayOf<Int>(1,2))
//private val intListOfTwoB: List<Int> = FList.of(*arrayOf<Int>(0,2))
//private val intListOfTwoC: List<Int> = FList.of(*arrayOf<Int>(1,4))
//private val intListOfThree: List<Int> = FList.of(*arrayOf<Int>(1,2,3))
//private val intListOfThreeA: List<Int> = FList.of(*arrayOf<Int>(1,2,5))
//private val intListOfFive: List<Int> = FList.of(*arrayOf<Int>(1,2,3,2,1))
//private val intListOfSix: List<Int> = FList.of(*arrayOf<Int>(1,2,3,3,2,1))

class FListKListTest : FunSpec({

  beforeTest {}

  // methods

//  test("size") {
//    intListOfNone.size shouldBe 0
//    intListOfOne.size shouldBe 1
//    intListOfTwo.size shouldBe 2
//    intListOfThree.size shouldBe 3
//  }
//
//  test("isEmpty") {
//    intListOfNone.isEmpty() shouldBe true
//    intListOfOne.isEmpty() shouldBe false
//    intListOfTwo.isEmpty() shouldBe false
//    intListOfThree.isEmpty() shouldBe false
//  }
//
//  test("contains") {
//    intListOfNone.contains(0) shouldBe false
//    intListOfOne.contains(0) shouldBe false
//    intListOfOne.contains(1) shouldBe true
//    intListOfOne.contains(2) shouldBe false
//    intListOfTwo.contains(0) shouldBe false
//    intListOfTwo.contains(1) shouldBe true
//    intListOfTwo.contains(2) shouldBe true
//    intListOfTwo.contains(3) shouldBe false
//  }
//
//  test("containsAll") {
//    intListOfNone.containsAll(intListOfNone) shouldBe true
//    intListOfNone.containsAll(intListOfOne) shouldBe false
//
//    intListOfOne.containsAll(intListOfNone) shouldBe true
//    intListOfOne.containsAll(intListOfOne) shouldBe true
//    intListOfOne.containsAll(intListOfTwo) shouldBe false
//
//    intListOfTwo.containsAll(intListOfNone) shouldBe true
//    intListOfTwo.containsAll(intListOfOne) shouldBe true
//    intListOfTwo.containsAll(intListOfTwo) shouldBe true
//    intListOfTwo.containsAll(intListOfThree) shouldBe false
//
//    intListOfThree.containsAll(intListOfNone) shouldBe true
//    intListOfThree.containsAll(intListOfOne) shouldBe true
//    intListOfThree.containsAll(intListOfOneB) shouldBe true
//    intListOfThree.containsAll(intListOfOneC) shouldBe true
//    intListOfThree.containsAll(intListOfTwo) shouldBe true
//    intListOfThree.containsAll(intListOfTwoA) shouldBe true
//    intListOfThree.containsAll(intListOfOneA) shouldBe false
//    intListOfThree.containsAll(intListOfOneD) shouldBe false
//    intListOfThree.containsAll(intListOfTwoB) shouldBe false
//    intListOfThree.containsAll(intListOfTwoC) shouldBe false
//  }
//
//  test("get by index") {
//    val iobNoneException = shouldThrow<IndexOutOfBoundsException> {
//      intListOfNone.get(0)
//    }
//    iobNoneException shouldNotBe null
//
//    intListOfOne.get(0) shouldBe 1
//    val iobOneException = shouldThrow<IndexOutOfBoundsException> {
//      intListOfOne.get(1)
//    }
//    iobOneException shouldNotBe null
//
//    intListOfTwo.get(0) shouldBe 1
//    intListOfTwo.get(1) shouldBe 2
//    val iobTwoException = shouldThrow<IndexOutOfBoundsException> {
//      intListOfTwo.get(2)
//    }
//    iobTwoException shouldNotBe null
//  }
//
//  test("indexOf") {
//    intListOfNone.indexOf(0) shouldBe -1
//
//    intListOfOne.indexOf(0) shouldBe -1
//    intListOfOne.indexOf(1) shouldBe 0
//    intListOfOne.indexOf(2) shouldBe -1
//
//    intListOfTwo.indexOf(0) shouldBe -1
//    intListOfTwo.indexOf(1) shouldBe 0
//    intListOfTwo.indexOf(2) shouldBe 1
//    intListOfTwo.indexOf(3) shouldBe -1
//  }
//
//  test("lastIndexOf") {
//    intListOfNone.lastIndexOf(0) shouldBe -1
//
//    intListOfOne.lastIndexOf(0) shouldBe -1
//    intListOfOne.lastIndexOf(1) shouldBe 0
//    intListOfOne.lastIndexOf(2) shouldBe -1
//
//    intListOfTwo.lastIndexOf(0) shouldBe -1
//    intListOfTwo.lastIndexOf(1) shouldBe 0
//    intListOfTwo.lastIndexOf(2) shouldBe 1
//    intListOfTwo.lastIndexOf(3) shouldBe -1
//
//    intListOfFive.lastIndexOf(0) shouldBe -1
//    intListOfFive.lastIndexOf(1) shouldBe 4
//    intListOfFive.lastIndexOf(2) shouldBe 3
//    intListOfFive.lastIndexOf(3) shouldBe 2
//    intListOfFive.lastIndexOf(4) shouldBe -1
//
//    intListOfSix.lastIndexOf(0) shouldBe -1
//    intListOfSix.lastIndexOf(1) shouldBe 5
//    intListOfSix.lastIndexOf(2) shouldBe 4
//    intListOfSix.lastIndexOf(3) shouldBe 3
//    intListOfSix.lastIndexOf(4) shouldBe -1
//  }
//
//  // ListIterator -- methods forward
//
//  test("listIterator") {
//    val i0 = intListOfNone.listIterator()
//    i0.hasNext() shouldBe false
//    shouldThrow<NoSuchElementException> {
//      i0.next()
//    }
//
//    val i1 = intListOfOne.listIterator()
//    i1.hasNext() shouldBe true
//    i1.next() shouldBe 1
//    i1.hasNext() shouldBe false
//    shouldThrow<NoSuchElementException> {
//      i1.next()
//    }
//
//    val i2 = intListOfTwo.listIterator()
//    i2.hasNext() shouldBe true
//    i2.next() shouldBe 1
//    i2.hasNext() shouldBe true
//    i2.next() shouldBe 2
//    i2.hasNext() shouldBe false
//    shouldThrow<NoSuchElementException> {
//      i2.next()
//    }
//  }
//
//  // ListIterator -- methods backwards
//
//  test("listIterator back") {
//    val i0 = intListOfNone.listIterator()
//    i0.hasPrevious() shouldBe false
//    shouldThrow<NoSuchElementException> {
//      i0.previous()
//    }
//
//    val i1 = intListOfOne.listIterator()
//    i1.hasPrevious() shouldBe false
//    shouldThrow<NoSuchElementException> {
//      i1.previous()
//    }
//
//    val i2 = intListOfTwo.listIterator()
//    i2.hasPrevious() shouldBe false
//    shouldThrow<NoSuchElementException> {
//      i2.previous()
//    }
//  }
//
//  test("listIterator indexed") {
//    val i0 = intListOfNone.listIterator(0)
//    i0.hasNext() shouldBe false
//    shouldThrow<NoSuchElementException> {
//      i0.next()
//    }
//
//    val i1 = intListOfOne.listIterator(0)
//    i1.hasNext() shouldBe true
//    i1.next() shouldBe 1
//    i1.hasNext() shouldBe false
//    shouldThrow<NoSuchElementException> {
//      i1.next()
//    }
//
//    val i2 = intListOfTwo.listIterator(0)
//    i2.hasNext() shouldBe true
//    i2.next() shouldBe 1
//    i2.hasNext() shouldBe true
//    i2.next() shouldBe 2
//    i2.hasNext() shouldBe false
//    shouldThrow<NoSuchElementException> {
//      i2.next()
//    }
//  }
//
//  test("listIterator indexed back") {
//    val i0 = intListOfNone.listIterator(intListOfNone.size)
//    i0.hasPrevious() shouldBe false
//    shouldThrow<NoSuchElementException> {
//      i0.previous()
//    }
//
//    val i1 = intListOfOne.listIterator(intListOfOne.size)
//    i1.hasPrevious() shouldBe true
//    i1.previous() shouldBe 1
//    i1.hasPrevious() shouldBe false
//    shouldThrow<NoSuchElementException> {
//      i1.previous()
//    }
//
//    val i2 = intListOfTwo.listIterator(intListOfTwo.size)
//    i2.hasPrevious() shouldBe true
//    i2.previous() shouldBe 2
//    i2.hasPrevious() shouldBe true
//    i2.previous() shouldBe 1
//    i2.hasPrevious() shouldBe false
//    shouldThrow<NoSuchElementException> {
//      i2.previous()
//    }
//  }
//
//  test("subList") {
//    intListOfSix.subList(0, 0) shouldBe (intListOfNone as FList<*>)
//    intListOfSix.subList(0, 1) shouldBe (intListOfOne as FList<*>)
//    intListOfSix.subList(0, 2) shouldBe (intListOfTwo as FList<*>)
//    intListOfSix.subList(0, 3) shouldBe (intListOfThree as FList<*>)
//    intListOfSix.subList(3, 6) shouldBe (intListOfThree as FList<*>).freverse()
//  }
//
//  // typeclass
//
//  test("elementAt") {
//    shouldThrow<IndexOutOfBoundsException> {
//      intListOfNone.elementAt(0)
//    }
//    intListOfNone.elementAtOrNull(0) shouldBe null
//
//    intListOfOne.elementAt(0) shouldBe 1
//    shouldThrow<IndexOutOfBoundsException> {
//      intListOfOne.elementAt(1)
//    }
//    intListOfOne.elementAtOrNull(1) shouldBe null
//
//    intListOfTwo.elementAt(0) shouldBe 1
//    intListOfTwo.elementAt(1) shouldBe 2
//    shouldThrow<IndexOutOfBoundsException> {
//      intListOfTwo.elementAt(2)
//    }
//    intListOfTwo.elementAtOrNull(2) shouldBe null
//  }
//
//  test("elementAtOrElse") {
//
//    fun elseValue(ix: Int): Int = Int.MAX_VALUE - ix
//
//    intListOfNone.elementAtOrElse(0, ::elseValue) shouldBe Int.MAX_VALUE
//
//    intListOfOne.elementAtOrElse(0, ::elseValue) shouldBe 1
//    intListOfOne.elementAtOrElse(1, ::elseValue) shouldBe Int.MAX_VALUE-1
//
//    intListOfTwo.elementAtOrElse(0, ::elseValue) shouldBe 1
//    intListOfTwo.elementAtOrElse(1, ::elseValue) shouldBe 2
//    intListOfTwo.elementAtOrElse(2, ::elseValue) shouldBe Int.MAX_VALUE-2
//  }
//
//  test("findLast") {
//
//    fun <Z> isMatch(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle == aut }
//
//    intListOfNone.findLast(isMatch(0)) shouldBe null
//
//    intListOfOne.findLast(isMatch(0)) shouldBe null
//    intListOfOne.findLast(isMatch(1)) shouldBe 1
//
//    intListOfTwo.findLast(isMatch(0)) shouldBe null
//    intListOfTwo.findLast(isMatch(1)) shouldBe 1
//    intListOfTwo.findLast(isMatch(2)) shouldBe 2
//    intListOfTwo.findLast(isMatch(3)) shouldBe null
//  }
//
//  test("first") {
//
//    shouldThrow<NoSuchElementException> {
//      intListOfNone.first()
//    }
//    intListOfNone.firstOrNull() shouldBe null
//
//    intListOfOne.firstOrNull() shouldBe 1
//    intListOfTwo.first() shouldBe 1
//  }
//
//  test("getOrElse") {
//
//    fun elseValue(ix: Int): Int = Int.MAX_VALUE - ix
//
//    intListOfNone.getOrElse(0, ::elseValue) shouldBe Int.MAX_VALUE
//
//    intListOfOne.getOrElse(0, ::elseValue) shouldBe 1
//    intListOfOne.getOrElse(1, ::elseValue) shouldBe Int.MAX_VALUE-1
//
//    intListOfTwo.getOrElse(0, ::elseValue) shouldBe 1
//    intListOfTwo.getOrElse(1, ::elseValue) shouldBe 2
//    intListOfTwo.getOrElse(2, ::elseValue) shouldBe Int.MAX_VALUE-2
//  }
//
//  test("getOrNull") {
//
//    intListOfNone.getOrNull(0) shouldBe null
//
//    intListOfOne.getOrNull(0) shouldBe 1
//    intListOfOne.getOrNull(1) shouldBe null
//
//    intListOfTwo.getOrNull(0) shouldBe 1
//    intListOfTwo.getOrNull(1) shouldBe 2
//    intListOfTwo.getOrNull(2) shouldBe null
//  }
//
//  test("indexOfirst") {
//
//    fun <Z> isMatch(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle == aut }
//
//    intListOfNone.indexOfFirst(isMatch(0)) shouldBe -1
//
//    intListOfOne.indexOfFirst(isMatch(0)) shouldBe -1
//    intListOfOne.indexOfFirst(isMatch(1)) shouldBe 0
//
//    intListOfSix.indexOfFirst(isMatch(0)) shouldBe -1
//    intListOfSix.indexOfFirst(isMatch(1)) shouldBe 0
//    intListOfSix.indexOfFirst(isMatch(2)) shouldBe 1
//    intListOfSix.indexOfFirst(isMatch(30)) shouldBe -1
//  }
//
//  test("indexOfLast") {
//
//    fun <Z> isMatch(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle == aut }
//
//    intListOfNone.indexOfLast(isMatch(0)) shouldBe -1
//
//    intListOfOne.indexOfLast(isMatch(0)) shouldBe -1
//    intListOfOne.indexOfLast(isMatch(1)) shouldBe 0
//
//    intListOfSix.indexOfLast(isMatch(0)) shouldBe -1
//    intListOfSix.indexOfLast(isMatch(1)) shouldBe 5
//    intListOfSix.indexOfLast(isMatch(2)) shouldBe 4
//    intListOfSix.indexOfLast(isMatch(30)) shouldBe -1
//  }
//
//  test("last") {
//    shouldThrow<NoSuchElementException> {
//      intListOfNone.last()
//    }
//    intListOfOne.last() shouldBe 1
//    intListOfTwo.last() shouldBe 2
//    intListOfThree.last() shouldBe 3
//  }
//
//  test("last (find)") {
//
//    fun <Z> isMatch(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle == aut }
//
//    shouldThrow<NoSuchElementException> {
//      intListOfNone.last(isMatch(0))
//    }
//    intListOfNone.lastOrNull(isMatch(0)) shouldBe null
//
//    intListOfOne.lastOrNull(isMatch(0)) shouldBe null
//    intListOfOne.lastOrNull(isMatch(1)) shouldBe 1
//    shouldThrow<NoSuchElementException> {
//      intListOfOne.last(isMatch(2))
//    }
//
//    intListOfSix.lastOrNull(isMatch(0)) shouldBe null
//    intListOfSix.last(isMatch(1)) shouldBe 1
//    intListOfSix.last(isMatch(2)) shouldBe 2
//    intListOfSix.lastOrNull(isMatch(30)) shouldBe null
//  }
//
//  test("single") {
//    shouldThrow<NoSuchElementException> {
//      intListOfNone.single()
//    }
//    intListOfNone.singleOrNull() shouldBe null
//
//    intListOfOne.single() shouldBe 1
//    intListOfOne.singleOrNull() shouldBe 1
//
//    shouldThrow<IllegalArgumentException> {
//      intListOfTwo.single()
//    }
//    intListOfTwo.singleOrNull() shouldBe null
//  }
//
//  test("dropLast") {
//    intListOfNone.dropLast(1) shouldBe intListOfNone
//    intListOfOne.dropLast(0)  shouldBe intListOfOne
//    intListOfOne.dropLast(1)  shouldBe intListOfNone
//    intListOfThree.dropLast(0)  shouldBe intListOfThree
//    intListOfThree.dropLast(1)  shouldBe FLCons(1, FLCons(2, FLNil))
//    intListOfThree.dropLast(2)  shouldBe FLCons(1, FLNil)
//    intListOfThree.dropLast(3)  shouldBe intListOfNone
//  }
//
//  test("dropLastWhile") {
//    intListOfSix.dropLastWhile { it < 3 } shouldBe FLCons(1, FLCons(2, FLCons(3, FLCons(3, FLNil))))
//    intListOfSix.dropLastWhile { it < 4 } shouldBe intListOfNone
//  }
//
//  test("slice of range") {
//    intListOfSix.slice(0 until 0) shouldBe (intListOfNone as FList<*>)
//    intListOfSix.slice(0 until 1) shouldBe (intListOfOne as FList<*>)
//    intListOfSix.slice(0..1) shouldBe (intListOfTwo as FList<*>)
//    intListOfSix.slice(0 until 2) shouldBe (intListOfTwo as FList<*>)
//    intListOfSix.slice(0..2) shouldBe (intListOfThree as FList<*>)
//    intListOfSix.slice(0 until 3) shouldBe (intListOfThree as FList<*>)
//    intListOfSix.slice(3 until 6) shouldBe (intListOfThree as FList<*>).freverse()
//    // noteworthy
//    intListOfSix.slice(3..6) shouldBe (intListOfThree as FList<*>).freverse()
//    intListOfSix.slice(3..100) shouldBe (intListOfThree as FList<*>).freverse()
//  }
//
//  test("slice of iterable") {
//    intListOfFive.slice(listOf(0, 2, 3)) shouldBe listOf(1,3,2)
//    intListOfSix.slice(listOf(0, 2, 3)) shouldBe listOf(1,3,3)
//  }
//
//  test("takeLast") {
//    intListOfNone.takeLast(1) shouldBe intListOfNone
//    intListOfOne.takeLast(0)  shouldBe intListOfNone
//    intListOfOne.takeLast(1)  shouldBe intListOfOne
//    intListOfThree.takeLast(0)  shouldBe intListOfNone
//    intListOfThree.takeLast(1)  shouldBe FLCons(3, FLNil)
//    intListOfThree.takeLast(2)  shouldBe FLCons(2, FLCons(3, FLNil))
//    intListOfThree.takeLast(3)  shouldBe intListOfThree
//  }
//
//  test("takeLastWhile") {
//    intListOfSix.takeLastWhile { it < 3 } shouldBe FLCons(2, FLCons(1, FLNil))
//    intListOfSix.takeLastWhile { it < 4 } shouldBe intListOfSix
//  }
//
//  test("foldRight diff") {
//
//    val d = { a: Int, acc: Int -> a - acc}
//
//    intListOfNone.foldRight(0, d) shouldBe 0
//    intListOfOne.foldRight(0, d) shouldBe 1
//    FList.of(*arrayOf<Int>(2,1)).foldRight(0, d)  shouldBe 1
//    FList.of(*arrayOf<Int>(3,2,1)).foldRight(0, d)  shouldBe 2
//    FList.of(*arrayOf<Int>(3,2,1,0)).foldRight(0, d) shouldBe 2
//  }
//
//  test("foldRight sum") {
//
//    val s = { a: Int, acc: Int -> a + acc}
//
//    intListOfNone.foldRight(0, s) shouldBe 0
//    intListOfOne.foldRight(0, s) shouldBe 1
//    FList.of(*arrayOf<Int>(2,1)).foldRight(0, s)  shouldBe 3
//    FList.of(*arrayOf<Int>(3,2,1)).foldRight(0, s)  shouldBe 6
//    FList.of(*arrayOf<Int>(3,2,1,0)).foldRight(0, s) shouldBe 6
//  }
//
//  test("foldRight product") {
//
//    val ss = { a: Int, acc: Int -> a * acc }
//
//    intListOfNone.foldRight(1, ss)  shouldBe 1
//    intListOfOne.foldRight(1, ss) shouldBe 1
//    FList.of(*arrayOf<Int>(2,1)).foldRight(1, ss) shouldBe 2
//    FList.of(*arrayOf<Int>(3,2,1)).foldRight(1, ss) shouldBe 6
//    FList.of(*arrayOf<Int>(3,2,1,0)).foldRight(1, ss) shouldBe 0
//  }
//
//  // ignore
//  // test("foldRight indexed sum") {}
//
//  test("reduceRight") {
//
//    val ss = { b: Int, acc: Int -> acc - b }
//
//    shouldThrow<UnsupportedOperationException> {
//      intListOfNone.reduceRight(ss)
//    }
//    intListOfNone.reduceRightOrNull(ss) shouldBe null
//    intListOfOne.reduceRight(ss) shouldBe 1
//    intListOfTwo.reduceRight(ss) shouldBe 1
//    intListOfTwoA.reduceRight(ss) shouldBe 2
//    intListOfTwoC.reduceRight(ss) shouldBe 3
//    intListOfThree.reduceRight(ss) shouldBe 0
//    intListOfThreeA.reduceRight(ss) shouldBe 2
//  }

  // ignore
  // test("reduceRightIndexed") {}
  // test("reduceRightIndexedOrNull") {}
  // test("firstNotNullOf") {}

  //  afterTest { (testCase, result) ->
  //  }

})
