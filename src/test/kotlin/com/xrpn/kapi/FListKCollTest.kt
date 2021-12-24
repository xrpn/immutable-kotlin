package com.xrpn.kapi

import com.xrpn.immutable.*
import com.xrpn.immutable.FList.Companion.toIMList
import com.xrpn.immutable.emptyArrayOfInt
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

//private val intListOfNone: Collection<Int> = FList.of(*emptyArrayOfInt)
//private val intListOfOneA: Collection<Int> = FList.of(*arrayOf<Int>(0))
//private val intListOfOne: Collection<Int> = FList.of(*arrayOf<Int>(1))
//private val intListOfOneB: Collection<Int> = FList.of(*arrayOf<Int>(2))
//private val intListOfOneC: Collection<Int> = FList.of(*arrayOf<Int>(3))
//private val intListOfOneD: Collection<Int> = FList.of(*arrayOf<Int>(4))
//private val intListOfTwoA: Collection<Int> = FList.of(*arrayOf<Int>(1,3))
//private val intListOfTwo: Collection<Int> = FList.of(*arrayOf<Int>(1,2))
//private val intListOfTwoB: Collection<Int> = FList.of(*arrayOf<Int>(0,2))
//private val intListOfTwoC: Collection<Int> = FList.of(*arrayOf<Int>(1,4))
//private val intListOfThree: Collection<Int> = FList.of(*arrayOf<Int>(1,2,3))
//private val intListOfThreeA: Collection<Int> = FList.of(*arrayOf<Int>(1,2,5))
//private val intListOfFive: Collection<Int> = FList.of(*arrayOf<Int>(1,2,3,2,1))
//private val intListOfSix: Collection<Int> = FList.of(*arrayOf<Int>(1,2,3,3,2,1))

class FListKCollTest : FunSpec({

  beforeTest {}

//  fun <Z: Comparable<Z>> matchEqual(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle == aut }
//  fun <Z: Comparable<Z>> matchLessThan(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle > aut }
//
//  // Any equals
//
//  test("FList equals") {
//    (intListOfNone == FList.of(*emptyArrayOfInt)) shouldBe true
//    (intListOfNone.equals(emptyList<Int>())) shouldBe true
//    (intListOfNone == emptyList<Int>()) shouldBe true
//    (intListOfNone == emptyList<Int>().toIMList()) shouldBe true
//    (intListOfNone == FList.of(*arrayOf(1))) shouldBe false
//    (intListOfNone == listOf(1)) shouldBe false
//    (intListOfNone == listOf(1).toIMList()) shouldBe false
//
//    (intListOfOne == FList.of(*emptyArrayOfInt)) shouldBe false
//    (intListOfOne == emptyList<Int>()) shouldBe false
//    (intListOfOne == FList.of(*arrayOf(1))) shouldBe true
//    (intListOfOne == listOf(1)) shouldBe true
//    (intListOfOne == listOf(1).toIMList()) shouldBe true
//    (intListOfOne == FList.of(*arrayOf(1, 2))) shouldBe false
//  }
//
//  test("FList equals miss") {
//    (intListOfOne == FList.of(*arrayOf(2))) shouldBe false
//    (intListOfTwo == listOf(1)) shouldBe false
//    (intListOfTwo == listOf(2)) shouldBe false
//    (intListOfTwo == listOf(2, 1)) shouldBe false
//    (intListOfTwo == intListOfThree) shouldBe false
//  }
//
//  test("Collections equals") {
//    (emptyList<Int>() == intListOfOne) shouldBe false
//    (emptyList<Int>() == intListOfOne.toList()) shouldBe false
//    (listOf(1) == intListOfOne) shouldBe true
//    (listOf(1) == intListOfOne.toList()) shouldBe true
//    (listOf(1) == intListOfTwo.toList()) shouldBe false
//    (listOf(1,2) == intListOfOne.toList()) shouldBe false
//
//    (emptyList<Int>() == intListOfNone) shouldBe true
//    (emptyList<Int>() == intListOfNone.toList()) shouldBe true
//    (listOf<Int>(1) == intListOfNone.toList()) shouldBe false
//  }
//
//  test("Collections equals miss") {
//    (listOf(2) == intListOfOne) shouldBe false
//    (listOf(1) == intListOfTwo) shouldBe false
//    (listOf(2) == intListOfTwo) shouldBe false
//    (listOf(2, 1) == intListOfTwo) shouldBe false
//    (listOf(1, 2) == intListOfThree) shouldBe false
//  }
//
//  // Collection -- methods or fields
//
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
//  // Iterator -- methods
//
//  test("iterator") {
//    val i0 = intListOfNone.iterator()
//    i0.hasNext() shouldBe false
//    shouldThrow<NoSuchElementException> {
//      i0.next()
//    }
//
//    val i1 = intListOfOne.iterator()
//    i1.hasNext() shouldBe true
//    i1.next() shouldBe 1
//    i1.hasNext() shouldBe false
//    shouldThrow<NoSuchElementException> {
//      i1.next()
//    }
//
//    val i2 = intListOfTwo.iterator()
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
//  // typeclass (Collection, Iterator, Iterable)
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
//  //  ignore
//  //  test("firstNotNullOf") {}
//
//  test("find first") {
//    shouldThrow<NoSuchElementException> {
//      intListOfNone.first(matchEqual(0))
//    }
//    intListOfNone.firstOrNull(matchEqual(0)) shouldBe null
//
//    shouldThrow<NoSuchElementException> {
//      intListOfOne.first(matchEqual(0))
//    }
//    intListOfOne.firstOrNull(matchEqual(0)) shouldBe null
//    intListOfOne.firstOrNull(matchEqual(1)) shouldBe 1
//
//    intListOfTwo.firstOrNull(matchEqual(0)) shouldBe null
//    intListOfTwo.first(matchEqual(1)) shouldBe 1
//    intListOfTwo.first(matchEqual(2)) shouldBe 2
//    intListOfTwo.firstOrNull(matchEqual(3)) shouldBe null
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
//  test("indexOfirst") {
//    intListOfNone.indexOfFirst(matchEqual(0)) shouldBe -1
//
//    intListOfOne.indexOfFirst(matchEqual(0)) shouldBe -1
//    intListOfOne.indexOfFirst(matchEqual(1)) shouldBe 0
//
//    intListOfSix.indexOfFirst(matchEqual(0)) shouldBe -1
//    intListOfSix.indexOfFirst(matchEqual(1)) shouldBe 0
//    intListOfSix.indexOfFirst(matchEqual(2)) shouldBe 1
//    intListOfSix.indexOfFirst(matchEqual(30)) shouldBe -1
//  }
//
//  test("indexOfLast") {
//    intListOfNone.indexOfLast(matchEqual(0)) shouldBe -1
//
//    intListOfOne.indexOfLast(matchEqual(0)) shouldBe -1
//    intListOfOne.indexOfLast(matchEqual(1)) shouldBe 0
//
//    intListOfSix.indexOfLast(matchEqual(0)) shouldBe -1
//    intListOfSix.indexOfLast(matchEqual(1)) shouldBe 5
//    intListOfSix.indexOfLast(matchEqual(2)) shouldBe 4
//    intListOfSix.indexOfLast(matchEqual(30)) shouldBe -1
//  }
//
//  test("last") {
//    shouldThrow<NoSuchElementException> {
//      intListOfNone.last()
//    }
//    intListOfNone.lastOrNull() shouldBe null
//    intListOfOne.lastOrNull() shouldBe 1
//    intListOfOne.last() shouldBe 1
//    intListOfTwo.last() shouldBe 2
//    intListOfThree.last() shouldBe 3
//  }
//
//  test("last (find)") {
//    shouldThrow<NoSuchElementException> {
//      intListOfNone.last(matchEqual(0))
//    }
//    intListOfNone.lastOrNull(matchEqual(0)) shouldBe null
//
//    intListOfOne.lastOrNull(matchEqual(1)) shouldBe 1
//    intListOfOne.last(matchEqual(1)) shouldBe 1
//    intListOfOne.lastOrNull(matchEqual(2)) shouldBe null
//
//    intListOfTwo.lastOrNull(matchEqual(0)) shouldBe null
//    intListOfTwo.last(matchEqual(1)) shouldBe 1
//    intListOfTwo.last(matchEqual(2)) shouldBe 2
//    shouldThrow<NoSuchElementException> {
//      intListOfTwo.last(matchEqual(3))
//    }
//    intListOfTwo.lastOrNull(matchEqual(3)) shouldBe null
//  }
//
//  test("lastIndexOf") {
//
//    intListOfNone.lastIndexOf(0) shouldBe -1
//
//    intListOfOne.lastIndexOf(0) shouldBe -1
//    intListOfOne.lastIndexOf(1) shouldBe 0
//
//    intListOfSix.lastIndexOf(0) shouldBe -1
//    intListOfSix.lastIndexOf(1) shouldBe 5
//    intListOfSix.lastIndexOf(2) shouldBe 4
//    intListOfSix.lastIndexOf(30) shouldBe -1
//  }
//
//  test("findlast") {
//    intListOfNone.findLast(matchEqual(0)) shouldBe null
//
//    intListOfOne.findLast(matchEqual(0)) shouldBe null
//    intListOfOne.findLast(matchEqual(1)) shouldBe 1
//
//    intListOfTwo.findLast(matchEqual(0)) shouldBe null
//    intListOfTwo.findLast(matchEqual(1)) shouldBe 1
//    intListOfTwo.findLast(matchEqual(2)) shouldBe 2
//    intListOfTwo.findLast(matchEqual(3)) shouldBe null
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
//  test("single (find)") {
//    shouldThrow<NoSuchElementException> {
//      intListOfNone.single(matchEqual(0))
//    }
//    intListOfNone.singleOrNull(matchEqual(0)) shouldBe null
//
//    intListOfOne.single(matchEqual(1)) shouldBe 1
//    intListOfOne.singleOrNull(matchEqual(1)) shouldBe 1
//
//    shouldThrow<NoSuchElementException> {
//      intListOfTwo.single(matchEqual(0))
//    }
//    intListOfTwo.singleOrNull(matchEqual(0)) shouldBe null
//    intListOfTwo.single(matchEqual(1)) shouldBe 1
//    intListOfTwo.single(matchEqual(2)) shouldBe 2
//    shouldThrow<NoSuchElementException> {
//      intListOfTwo.single(matchEqual(3))
//    }
//    intListOfTwo.singleOrNull(matchEqual(3)) shouldBe null
//
//    shouldThrow<NoSuchElementException> {
//      intListOfTwo.single(matchEqual(0))
//    }
//    intListOfTwo.singleOrNull(matchEqual(0)) shouldBe null
//    shouldThrow<NoSuchElementException> {
//      intListOfTwo.single(matchEqual(3))
//    }
//    intListOfTwo.singleOrNull(matchEqual(3)) shouldBe null
//  }
//
//  test("drop 0") {
//    intListOfNone.drop(0).toIMList() shouldBe FLNil
//    intListOfOne.drop(0).toIMList() shouldBe intListOfOne
//    intListOfTwo.drop(0).toIMList() shouldBe intListOfTwo
//  }
//
//  test("drop 1") {
//    val aux0: List<Int> = intListOfThree.drop(1)
//    val aux1: FList<Int> = FList.of(aux0)
//    aux1 shouldBe FLCons(2,FLCons(3,FLNil))
//    intListOfNone.drop(1).toIMList() shouldBe FLNil
//    intListOfOne.drop(1).toIMList() shouldBe FLNil
//    intListOfTwo.drop(1).toIMList() shouldBe FLCons(2,FLNil)
//  }
//
//  test("drop 2") {
//    intListOfNone.drop(2).toIMList() shouldBe FLNil
//    intListOfOne.drop(2).toIMList() shouldBe FLNil
//    intListOfTwo.drop(2).toIMList() shouldBe FLNil
//    intListOfThree.drop(2).toIMList() shouldBe FLCons(3,FLNil)
//    FList.of(*arrayOf<Int>(1,2,3,4)).drop(2).toIMList() shouldBe FLCons(3,FLCons(4,FLNil))
//  }
//
//  test("drop 3") {
//    intListOfNone.drop(3).toIMList() shouldBe FLNil
//    intListOfOne.drop(3).toIMList() shouldBe FLNil
//    intListOfTwo.drop(3).toIMList() shouldBe FLNil
//    intListOfThree.drop(3).toIMList() shouldBe FLNil
//    FList.of(*arrayOf<Int>(1,2,3,4)).drop(3).toIMList() shouldBe FLCons(4,FLNil)
//  }
//
//  test("dropWhile") {
//    intListOfNone.dropWhile { it > 1 }.toIMList() shouldBe FLNil
//    intListOfOne.dropWhile { it > 1 }.toIMList()  shouldBe FLCons(1,FLNil)
//    FList.of(*arrayOf<Int>(2,1)).dropWhile { it > 1 }.toIMList()  shouldBe FLCons(1,FLNil)
//    FList.of(*arrayOf<Int>(3,2,1)).dropWhile { it > 1 }.toIMList()  shouldBe FLCons(1,FLNil)
//    val a1 = FList.of(*arrayOf<Int>(3,2,1,0,3))
//    a1.dropWhile { it > 1 }.toIMList() shouldBe FLCons(1,FLCons(0, FLCons(3,FLNil)))
//    a1.dropWhile { it > 2 }.toIMList() shouldBe FLCons(2,FLCons(1,FLCons(0, FLCons(3,FLNil))))
//  }
//
//  test("filter") {
//    intListOfNone.filter {0 == it % 2}.toIMList() shouldBe FLNil
//    intListOfOne.filter {0 == it % 2}.toIMList() shouldBe FLNil
//    intListOfTwo.filter {0 == it % 2}.toIMList() shouldBe FLCons(2,FLNil)
//    intListOfThree.filter {0 == it % 2}.toIMList() shouldBe FLCons(2,FLNil)
//    FList.of(*arrayOf<Int>(1,2,3,4)).filter {0 == it % 2}.toIMList() shouldBe FLCons(2,FLCons(4,FLNil))
//  }
//
//  //  ignore
//  //  test("filter indexed") {}
//  //  test("filterIsInstance") {}
//
//  test("filterNot") {
//    intListOfNone.filterNot {0 == it % 2}.toIMList() shouldBe FLNil
//    intListOfOne.filterNot {0 == it % 2}.toIMList() shouldBe FLCons(1,FLNil)
//    intListOfTwo.filterNot {0 == it % 2}.toIMList() shouldBe FLCons(1,FLNil)
//    intListOfThree.filterNot {0 == it % 2}.toIMList() shouldBe FLCons(1,FLCons(3,FLNil))
//    FList.of(*arrayOf<Int>(1,2,3,4)).filterNot {0 == it % 2}.toIMList() shouldBe FLCons(1,FLCons(3,FLNil))
//  }
//
//  //  ignore
//  //  test("filterNotNull") {}
//
//  test("take 0") {
//    intListOfNone.take(0).toIMList() shouldBe FLNil
//    intListOfOne.take(0).toIMList() shouldBe FLNil
//    intListOfTwo.take(0).toIMList() shouldBe FLNil
//  }
//
//  test("take 1") {
//    intListOfNone.take(1).toIMList() shouldBe FLNil
//    intListOfOne.take(1).toIMList() shouldBe intListOfOne
//    intListOfTwo.take(1).toIMList() shouldBe intListOfOne
//    intListOfThree.take(1).toIMList() shouldBe intListOfOne
//  }
//
//  test("take 2") {
//    intListOfNone.take(2).toIMList() shouldBe FLNil
//    intListOfOne.take(2).toIMList() shouldBe intListOfOne
//    intListOfTwo.take(2).toIMList() shouldBe intListOfTwo
//    intListOfThree.take(2).toIMList() shouldBe intListOfTwo
//    FList.of(*arrayOf<Int>(1,2,3,4)).take(2) shouldBe intListOfTwo
//  }
//
//  test("take 3") {
//    intListOfNone.take(3).toIMList() shouldBe FLNil
//    intListOfOne.take(3).toIMList() shouldBe intListOfOne
//    intListOfTwo.take(3).toIMList() shouldBe intListOfTwo
//    intListOfThree.take(3).toIMList() shouldBe intListOfThree
//    FList.of(*arrayOf<Int>(1,2,3,4)).take(3).toIMList() shouldBe intListOfThree
//  }
//
//  test("takeWhile") {
//    intListOfNone.takeWhile { it > 1 }.toIMList() shouldBe FLNil
//    intListOfOne.takeWhile { it == 1 }.toIMList()  shouldBe FLCons(1,FLNil)
//    FList.of(*arrayOf<Int>(2,1)).takeWhile { it > 1 }.toIMList()  shouldBe FLCons(2,FLNil)
//    FList.of(*arrayOf<Int>(3,2,1)).takeWhile { it > 1 }.toIMList()  shouldBe FLCons(3,FLCons(2,FLNil))
//    FList.of(*arrayOf<Int>(3,2,1,0)).takeWhile { it != 1 }.toIMList() shouldBe FLCons(3,FLCons(2,FLNil))
//  }
//
//  test("reversed") {
//    intListOfNone.reversed().toIMList() shouldBe FLNil
//    intListOfOne.reversed().toIMList() shouldBe intListOfOne
//    intListOfTwo.reversed().toIMList() shouldBe (intListOfTwo as FList<Int>).freverse()
//    intListOfThree.reversed().toIMList() shouldBe (intListOfThree as FList<Int>).freverse()
//  }
//
//  test("sorted") {
//    intListOfNone.sorted().toIMList() shouldBe FLNil
//    intListOfOne.sorted().toIMList() shouldBe intListOfOne
//    intListOfTwo.sorted().toIMList() shouldBe intListOfTwo
//    intListOfThree.sorted().toIMList() shouldBe intListOfThree
//    intListOfSix.sorted().toIMList() shouldBe
//            FLCons(1, FLCons(1, FLCons(2, FLCons(2, FLCons(3, FLCons(3, FLNil))))))
//  }
//
//  test("sortedDescending") {
//    intListOfNone.sortedDescending().toIMList() shouldBe FLNil
//    intListOfOne.sortedDescending().toIMList() shouldBe intListOfOne
//    intListOfTwo.sortedDescending().toIMList() shouldBe  (intListOfTwo as FList<Int>).freverse()
//    intListOfThree.sortedDescending().toIMList() shouldBe (intListOfThree as FList<Int>).freverse()
//    intListOfSix.sortedDescending().toIMList() shouldBe
//            FLCons(3, FLCons(3, FLCons(2, FLCons(2, FLCons(1, FLCons(1, FLNil))))))
//  }
//
//  test("sortedBy") {
//
//    fun reverseNumerical(t: Int): Int? = -t
//
//    intListOfNone.sortedBy(::reverseNumerical).toIMList() shouldBe FLNil
//    intListOfOne.sortedBy(::reverseNumerical).toIMList() shouldBe intListOfOne
//    intListOfTwo.sortedBy(::reverseNumerical).toIMList() shouldBe (intListOfTwo as FList<Int>).freverse()
//    intListOfThree.sortedBy(::reverseNumerical).toIMList() shouldBe (intListOfThree as FList<Int>).freverse()
//    intListOfSix.sortedBy(::reverseNumerical).toIMList() shouldBe
//            FLCons(3, FLCons(3, FLCons(2, FLCons(2, FLCons(1, FLCons(1, FLNil))))))
//  }
//
//  test("sortedByDescending") {
//
//    fun reverseNumerical(t: Int): Int? = -t
//
//    intListOfNone.sortedByDescending(::reverseNumerical).toIMList() shouldBe FLNil
//    intListOfOne.sortedByDescending(::reverseNumerical).toIMList() shouldBe intListOfOne
//    intListOfTwo.sortedByDescending(::reverseNumerical).toIMList() shouldBe intListOfTwo
//    intListOfThree.sortedByDescending(::reverseNumerical).toIMList() shouldBe intListOfThree
//    intListOfSix.sortedByDescending(::reverseNumerical).toIMList() shouldBe
//            FLCons(1, FLCons(1, FLCons(2, FLCons(2, FLCons(3, FLCons(3, FLNil))))))
//  }
//
//  test("sortedWith") {
//
//    val reverseNumerical: Comparator<Int> = Comparator<Int> { p0, p1 ->
//      when {
//        p0 == p1 -> 0
//        p0 > p1 -> -1
//        else -> 1
//      }
//    }
//
//    intListOfNone.sortedWith(reverseNumerical).toIMList() shouldBe FLNil
//    intListOfOne.sortedWith(reverseNumerical).toIMList() shouldBe intListOfOne
//    intListOfTwo.sortedWith(reverseNumerical).toIMList() shouldBe (intListOfTwo as FList<Int>).freverse()
//    intListOfThree.sortedWith(reverseNumerical).toIMList() shouldBe (intListOfThree as FList<Int>).freverse()
//    intListOfSix.sortedWith(reverseNumerical).toIMList() shouldBe
//            FLCons(3, FLCons(3, FLCons(2, FLCons(2, FLCons(1, FLCons(1, FLNil))))))
//  }
//
//  test("associate") {
//
//    fun f(t: Int): Pair<Int, Int> = Pair(t, -t)
//
//    intListOfNone.associate(::f) shouldBe emptyMap()
//    intListOfOne.associate(::f) shouldBe mapOf( 1 to -1 )
//    intListOfTwo.associate(::f) shouldBe mapOf( 1 to -1, 2 to -2 )
//    intListOfThree.associate(::f) shouldBe mapOf( 1 to -1, 2 to -2, 3 to -3 )
//  }
//
//  test("associateBy") {
//
//    fun f(t: Int): Int = -t
//
//    intListOfNone.associateBy(::f) shouldBe emptyMap()
//    intListOfOne.associateBy(::f) shouldBe mapOf( -1 to 1 )
//    intListOfTwo.associateBy(::f) shouldBe mapOf( -1 to 1, -2 to 2 )
//    intListOfThree.associateBy(::f) shouldBe mapOf( -1 to 1, -2 to 2, -3 to 3 )
//  }
//
//  test("associateBy (k, v)") {
//
//    fun f(t: Int): Int = -t
//    fun g(t: Int): Int = 2*t
//
//    intListOfNone.associateBy(::f, ::g) shouldBe emptyMap()
//    intListOfOne.associateBy(::f, ::g) shouldBe mapOf( -1 to 2 )
//    intListOfTwo.associateBy(::f, ::g) shouldBe mapOf( -1 to 2, -2 to 4 )
//    intListOfThree.associateBy(::f, ::g) shouldBe mapOf( -1 to 2, -2 to 4, -3 to 6 )
//  }
//
//  test("associateWith") {
//
//    fun g(t: Int): Int = 2*t
//
//    intListOfNone.associateWith(::g) shouldBe emptyMap()
//    intListOfOne.associateWith(::g) shouldBe mapOf( 1 to 2 )
//    intListOfTwo.associateWith(::g) shouldBe mapOf( 1 to 2, 2 to 4 )
//    intListOfThree.associateWith(::g) shouldBe mapOf( 1 to 2, 2 to 4, 3 to 6 )
//  }
//
//  test("flatMap") {
//    intListOfNone.flatMap {it -> FLCons(it, FLNil)}.toIMList() shouldBe FLNil
//    intListOfOne.flatMap {it -> FLCons(it, FLNil)}.toIMList() shouldBe FLCons(1,FLNil)
//    fun arrayBuilderConst(arg: Int) = Array<Int>(arg) { _ -> arg }
//    intListOfTwo.flatMap {FList.of(*arrayBuilderConst(it))}.toIMList() shouldBe FLCons(1,FLCons(2,FLCons(2,FLNil)))
//    fun arrayBuilderIncrement(arg: Int) = Array<Int>(arg) { i -> arg + i }
//    intListOfTwo.flatMap {FList.of(*arrayBuilderIncrement(it))}.toIMList() shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
//    intListOfThree.flatMap {FList.of(*arrayBuilderIncrement(it))}.toIMList() shouldBe
//        FLCons(1,FLCons(2,FLCons(3,FLCons(3,FLCons(4,FLCons(5,FLNil))))))
//    intListOfThree.flatMap { i -> FList.of(i, i) }.toIMList() shouldBe
//        FLCons(1,FLCons(1,FLCons(2,FLCons(2,FLCons(3,FLCons(3,FLNil))))))
//  }
//
//  // ignore
//  // test("flatMapIndexed") {}
//
//  test("groupBy") {
//
//    fun f(t: Int): Int = -t
//
//    intListOfNone.groupBy(::f) shouldBe emptyMap()
//    intListOfFive.groupBy(::f) shouldBe mapOf( -1 to listOf(1, 1), -2 to listOf(2,2), -3 to listOf(3) )
//  }
//
//  test("groupBy (k, v)") {
//
//    fun f(t: Int): Int = -t
//    fun g(t: Int): Int = 2*t
//
//    intListOfNone.groupBy(::f, ::g) shouldBe emptyMap()
//    intListOfFive.groupBy(::f, ::g) shouldBe mapOf( -1 to listOf(2, 2), -2 to listOf(4,4), -3 to listOf(6) )
//  }
//
//  // TODO (maybe)
//  // test("grouping") {
//  //   fail("not implemented yet")
//  // }
//
//  test("map") {
//    intListOfNone.map { it + 1}.toIMList() shouldBe FLNil
//    intListOfOne.map { it + 1}.toIMList() shouldBe FLCons(2,FLNil)
//    intListOfTwo.map { it + 1}.toIMList() shouldBe FLCons(2,FLCons(3,FLNil))
//  }
//
//  // ignore
//  // test("mapIndexed") {}
//  // test("mapIndexedNotNull") {}
//  // test("mapNotNull") {}
//
//  test("withIndex") {
//    val nwi = intListOfNone.withIndex()
//    nwi.iterator().hasNext() shouldBe false
//
//    val twi = intListOfThree.withIndex()
//    val twii = twi.iterator()
//    val twiv0 = twii.next()
//    twiv0.index shouldBe 0
//    twiv0.value shouldBe 1
//    val twiv1 = twii.next()
//    twiv1.index shouldBe 1
//    twiv1.value shouldBe 2
//    val twiv2 = twii.next()
//    twiv2.index shouldBe 2
//    twiv2.value shouldBe 3
//    twii.hasNext() shouldBe false
//  }
//
//  test("distinct") {
//    intListOfNone.distinct().toIMList() shouldBe FLNil
//    intListOfOne.distinct() shouldBe intListOfOne.toList()
//    intListOfTwo.distinct() shouldBe intListOfTwo.toList()
//    intListOfFive.distinct() shouldBe intListOfThree.toList()
//    intListOfSix.distinct() shouldBe intListOfThree.toList()
//  }
//
//  test("distinctBy") {
//
//    fun identity(oracle: Int) = oracle
//
//    intListOfNone.distinctBy(::identity).toIMList() shouldBe FLNil
//    intListOfOne.distinctBy(::identity) shouldBe intListOfOne.toList()
//    intListOfTwo.distinctBy(::identity) shouldBe intListOfTwo.toList()
//    intListOfFive.distinctBy(::identity) shouldBe intListOfThree.toList()
//    intListOfSix.distinctBy(::identity) shouldBe intListOfThree.toList()
//  }
//
//  // ignore
//  // test("intersect") {}
//  // test("subtract") {}
//  // test("union") {}
//
//  test("all") {
//    intListOfNone.all(matchLessThan(0)) shouldBe true // by vacuous implication
//    intListOfOne.all(matchLessThan(1)) shouldBe false
//    intListOfOne.all(matchLessThan(2)) shouldBe true
//    intListOfThree.all(matchLessThan(2)) shouldBe false
//    intListOfThree.all(matchLessThan(4)) shouldBe true
//  }
//
//  test("any") {
//    intListOfNone.any(matchLessThan(0)) shouldBe false
//    intListOfOne.any(matchLessThan(1)) shouldBe false
//    intListOfOne.any(matchLessThan(2)) shouldBe true
//    intListOfThree.any(matchLessThan(1)) shouldBe false
//    intListOfThree.any(matchLessThan(2)) shouldBe true
//    intListOfThree.any(matchLessThan(4)) shouldBe true
//  }
//
//  test("(has) any") {
//    intListOfNone.any() shouldBe false
//    intListOfOne.any() shouldBe true
//    intListOfThree.any() shouldBe true
//  }
//
//  test("count") {
//    intListOfNone.count() shouldBe 0
//    intListOfOne.count() shouldBe 1
//    intListOfThree.count() shouldBe 3
//  }
//
//  test("count matching") {
//    intListOfNone.count(matchEqual(0)) shouldBe 0
//    intListOfFive.count(matchEqual(3)) shouldBe 1
//    intListOfSix.count(matchEqual(3)) shouldBe 2
//  }
//
//  test("fold") {
//
//    val s = { acc: Int, b: Int -> acc - b }
//
//    intListOfNone.fold(1, s) shouldBe 1
//    intListOfOne.fold(1, s) shouldBe 0
//    intListOfTwo.fold(1, s) shouldBe -2
//    intListOfTwoA.fold(1, s) shouldBe -3
//    intListOfThree.fold(1, s) shouldBe -5
//    intListOfThreeA.fold(1, s) shouldBe -7
//  }
//
//  // ignore
//  // test("foldIndexed") {}
//
//  test("(has) none") {
//    intListOfNone.none() shouldBe true
//    intListOfOne.none() shouldBe false
//    intListOfThree.none() shouldBe false
//  }
//
//  test("none") {
//    intListOfNone.none(matchLessThan(0)) shouldBe true
//    intListOfOne.none(matchLessThan(1)) shouldBe true
//    intListOfOne.none(matchLessThan(2)) shouldBe false
//    intListOfThree.none(matchLessThan(1)) shouldBe true
//    intListOfThree.none(matchLessThan(2)) shouldBe false
//    intListOfThree.none(matchLessThan(4)) shouldBe false
//  }
//
//  test("reduce") {
//
//    val ss = { acc: Int, b: Int -> b - acc }
//
//    shouldThrow<UnsupportedOperationException> {
//      intListOfNone.reduce(ss)
//    }
//    intListOfNone.reduceOrNull(ss) shouldBe null
//    intListOfOne.reduce(ss) shouldBe 1
//    intListOfTwo.reduce(ss) shouldBe 1
//    intListOfTwoA.reduce(ss) shouldBe 2
//    intListOfTwoC.reduce(ss) shouldBe 3
//    intListOfThree.reduce(ss) shouldBe 2
//    intListOfThreeA.reduce(ss) shouldBe 4
//  }
//
//  // ignore
//  // test("reduceIndexed") {}
//  // test("reduceIndexedOrNull") {}
//
//  test("runningFold") {
//    intListOfNone.runningFold(1) { acc, b -> acc - b } shouldBe listOf(1)
//    intListOfOne.runningFold(1) { acc, b -> acc - b } shouldBe listOf(1, 0)
//    intListOfTwo.runningFold(1) { acc, b -> acc - b } shouldBe listOf(1, 0, -2)
//    intListOfTwoA.runningFold(1) { acc, b -> acc - b } shouldBe listOf(1, 0, -3)
//    intListOfThree.runningFold(1) { acc, b -> acc - b } shouldBe listOf(1, 0, -2, -5)
//    intListOfThreeA.runningFold(1) { acc, b -> acc - b } shouldBe listOf(1, 0, -2, -7)
//  }
//
//  // ignore
//  // test("runningFoldIndexed") {}
//
//  test("runningReduce") {
//    intListOfNone.runningReduce { b, a -> a - b } shouldBe emptyList()
//    intListOfOne.runningReduce { b, acc -> acc - b } shouldBe listOf(1)
//    intListOfTwo.runningReduce { b, acc -> acc - b } shouldBe listOf(1, 1)
//    intListOfTwoA.runningReduce { b, acc -> acc - b } shouldBe listOf(1, 2)
//    intListOfTwoC.runningReduce { b, acc -> acc - b } shouldBe listOf(1, 3)
//    intListOfThree.runningReduce { b, acc -> acc - b } shouldBe listOf(1, 1, 2)
//    intListOfThreeA.runningReduce { b, acc -> acc - b } shouldBe listOf(1, 1, 4)
//  }
//
//  // ignore
//  // test("runningReduceIndexed") {}
//
//  test("partition") {
//    intListOfOne.partition(matchLessThan(1)) shouldBe Pair(emptyList(), listOf(1))
//    intListOfThree.partition(matchLessThan(2)) shouldBe Pair(listOf(1), listOf(2, 3))
//  }
//
//  test("windowed") {
//    intListOfThree.windowed(2) shouldBe FList.of(intListOfTwo,FList.of(2,3))
//  }
//
//  test("zip array") {
//    intListOfNone.zip(emptyArrayOfStr){a, b -> Pair(a,b)}.toIMList() shouldBe FLNil
//    intListOfOne.zip(emptyArrayOfStr){a, b -> Pair(a,b)}.toIMList() shouldBe FLNil
//    intListOfNone.zip(arrayOf<String>("a")){a, b -> Pair(a,b)}.toIMList() shouldBe FLNil
//    intListOfOne.zip(arrayOf<String>("a")){a, b -> Pair(a,b)}.toIMList() shouldBe FLCons(Pair(1,"a"),FLNil)
//    intListOfTwo.zip(arrayOf<String>("a")){a, b -> Pair(a,b)}.toIMList() shouldBe FLCons(Pair(1,"a"),FLNil)
//    intListOfOne.zip(arrayOf<String>("a","b")){a, b -> Pair(a,b)}.toIMList() shouldBe FLCons(Pair(1,"a"),FLNil)
//    intListOfTwo.zip(arrayOf<String>("a", "b")){a, b -> Pair(a,b)}.toIMList() shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
//    intListOfThree.zip(arrayOf<String>("a", "b")){a, b -> Pair(a,b)}.toIMList() shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
//    intListOfTwo.zip(arrayOf<String>("a", "b", "c")){a, b -> Pair(a,b)}.toIMList() shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
//  }
//
//  test("zip iterable") {
//    intListOfNone.zip(listOf<String>()).toIMList() shouldBe FLNil
//    intListOfOne.zip(listOf<String>()).toIMList() shouldBe FLNil
//    intListOfNone.zip(listOf("a")).toIMList() shouldBe FLNil
//    intListOfOne.zip(listOf("a")).toIMList() shouldBe FLCons(Pair(1,"a"),FLNil)
//    intListOfTwo.zip(listOf("a")).toIMList() shouldBe FLCons(Pair(1,"a"),FLNil)
//    intListOfOne.zip(listOf("a","b")).toIMList() shouldBe FLCons(Pair(1,"a"),FLNil)
//    intListOfTwo.zip(listOf("a","b")).toIMList() shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
//    intListOfThree.zip(listOf("a","b")).toIMList() shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
//    intListOfTwo.zip(listOf("a","b","c")).toIMList() shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
//  }

  // ignore
  // test("zipWithNext"){}
  // test("zipWithNext (transform)"){}

})
