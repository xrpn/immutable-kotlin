package com.xrpn.kapi

import com.xrpn.bridge.FSetIterator
import com.xrpn.immutable.FSet
import com.xrpn.immutable.FSet.Companion.emptyIMSet
import com.xrpn.immutable.FSet.Companion.of
import com.xrpn.immutable.FSet.Companion.toFSet
import com.xrpn.immutable.pmap1
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSetOfNone: Collection<Int> = FSet.of(*arrayOf<Int>())
private val intSetOfOneA: Collection<Int> = FSet.of(*arrayOf<Int>(0))
private val intSetOfOne: Collection<Int> = FSet.of(*arrayOf<Int>(1))
private val intSetOfOneB: Collection<Int> = FSet.of(*arrayOf<Int>(2))
private val intSetOfOneC: Collection<Int> = FSet.of(*arrayOf<Int>(3))
private val intSetOfOneD: Collection<Int> = FSet.of(*arrayOf<Int>(4))
private val intSetOfTwoA: Collection<Int> = FSet.of(*arrayOf<Int>(1,3))
private val intSetOfTwo: Collection<Int> = FSet.of(*arrayOf<Int>(1,2))
private val intSetOfTwoB: Collection<Int> = FSet.of(*arrayOf<Int>(0,2))
private val intSetOfTwoC: Collection<Int> = FSet.of(*arrayOf<Int>(1,4))
private val intSetOfThree: Collection<Int> = FSet.of(*arrayOf<Int>(1,2,3))
private val intSetOfThreeA: Collection<Int> = FSet.of(*arrayOf<Int>(1,2,5))
private val intSetOfFive: Collection<Int> = FSet.of(*arrayOf<Int>(1,2,3,2,1))
private val intSetOfSix: Collection<Int> = FSet.of(*arrayOf<Int>(1,2,3,3,2,1))

class FSetKCollTest : FunSpec({

  beforeTest {}

  fun <Z: Comparable<Z>> matchEqual(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle == aut }
  fun <Z: Comparable<Z>> matchLessThan(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle > aut }

  // Any equals

  test("FSet equals") {
    (intSetOfNone == FSet.of(*arrayOf<Int>())) shouldBe true
    (intSetOfNone.equals(emptySet<Int>())) shouldBe true
    (intSetOfNone == emptySet<Int>()) shouldBe true
    (intSetOfNone == emptySet<Int>().toFSet()) shouldBe true
    (intSetOfNone == FSet.of(*arrayOf(1))) shouldBe false
    (intSetOfNone == setOf(1)) shouldBe false
    (intSetOfNone == setOf(1).toFSet()) shouldBe false

    (intSetOfOne == FSet.of(*arrayOf<Int>())) shouldBe false
    (intSetOfOne == emptySet<Int>()) shouldBe false
    (intSetOfOne == FSet.of(*arrayOf(1))) shouldBe true
    (intSetOfOne == setOf(1)) shouldBe true
    (intSetOfOne == setOf(1).toFSet()) shouldBe true
    (intSetOfOne == FSet.of(*arrayOf(1, 2))) shouldBe false
  }

  test("FSet equals miss") {
    (intSetOfOne == FSet.of(*arrayOf(2))) shouldBe false
    (intSetOfTwo == setOf(1)) shouldBe false
    (intSetOfTwo == setOf(2)) shouldBe false
    (intSetOfTwo == setOf(2, 1)) shouldBe true
    (intSetOfTwo == setOf(1, 2)) shouldBe true
    (intSetOfTwo == intSetOfThree) shouldBe false
    (intSetOfThree == setOf(1, 2, 3)) shouldBe true
    (intSetOfThree == setOf(1, 3, 2)) shouldBe true
    (intSetOfThree == setOf(2, 1, 3)) shouldBe true
    (intSetOfThree == setOf(3, 2, 1)) shouldBe true
  }

  test("Collections equals") {
    (emptySet<Int>() == intSetOfOne) shouldBe false
    (emptySet<Int>() == intSetOfOne.toSet()) shouldBe false
    (setOf(1) == intSetOfOne) shouldBe true
    (setOf(1) == intSetOfOne.toSet()) shouldBe true
    (setOf(1) == intSetOfTwo.toSet()) shouldBe false
    (setOf(1,2) == intSetOfOne.toSet()) shouldBe false

    (emptySet<Int>() == intSetOfNone) shouldBe true
    (emptySet<Int>() == intSetOfNone.toSet()) shouldBe true
    (setOf<Int>(1) == intSetOfNone.toSet()) shouldBe false
  }

  test("Collections equals miss") {
    (setOf(2) == intSetOfOne) shouldBe false
    (setOf(1) == intSetOfTwo) shouldBe false
    (setOf(2) == intSetOfTwo) shouldBe false
    (setOf(2, 1) == intSetOfTwo) shouldBe true
    (setOf(1, 2) == intSetOfTwo) shouldBe true
    (setOf(1, 2) == intSetOfThree) shouldBe false
    (setOf(1, 2, 3) == intSetOfThree) shouldBe true
    (setOf(1, 3, 2) == intSetOfThree) shouldBe true
    (setOf(2, 1, 3) == intSetOfThree) shouldBe true
    (setOf(3, 2, 1) == intSetOfThree) shouldBe true
  }

  // Collection -- methods or fields

  test("size") {
    intSetOfNone.size shouldBe 0
    intSetOfOne.size shouldBe 1
    intSetOfTwo.size shouldBe 2
    intSetOfThree.size shouldBe 3
  }

  test("isEmpty") {
    intSetOfNone.isEmpty() shouldBe true
    intSetOfOne.isEmpty() shouldBe false
    intSetOfTwo.isEmpty() shouldBe false
    intSetOfThree.isEmpty() shouldBe false
  }

  test("contains") {
    intSetOfNone.contains(0) shouldBe false
    intSetOfOne.contains(0) shouldBe false
    intSetOfOne.contains(1) shouldBe true
    intSetOfOne.contains(2) shouldBe false
    intSetOfTwo.contains(0) shouldBe false
    intSetOfTwo.contains(1) shouldBe true
    intSetOfTwo.contains(2) shouldBe true
    intSetOfTwo.contains(3) shouldBe false
  }

  test("containsAll") {
    intSetOfNone.containsAll(intSetOfNone) shouldBe true
    intSetOfNone.containsAll(intSetOfOne) shouldBe false

    intSetOfOne.containsAll(intSetOfNone) shouldBe true
    intSetOfOne.containsAll(intSetOfOne) shouldBe true
    intSetOfOne.containsAll(intSetOfTwo) shouldBe false

    intSetOfTwo.containsAll(intSetOfNone) shouldBe true
    intSetOfTwo.containsAll(intSetOfOne) shouldBe true
    intSetOfTwo.containsAll(intSetOfTwo) shouldBe true
    intSetOfTwo.containsAll(intSetOfThree) shouldBe false

    intSetOfThree.containsAll(intSetOfNone) shouldBe true
    intSetOfThree.containsAll(intSetOfOne) shouldBe true
    intSetOfThree.containsAll(intSetOfOneB) shouldBe true
    intSetOfThree.containsAll(intSetOfOneC) shouldBe true
    intSetOfThree.containsAll(intSetOfTwo) shouldBe true
    intSetOfThree.containsAll(intSetOfTwoA) shouldBe true
    intSetOfThree.containsAll(intSetOfOneA) shouldBe false
    intSetOfThree.containsAll(intSetOfOneD) shouldBe false
    intSetOfThree.containsAll(intSetOfTwoB) shouldBe false
    intSetOfThree.containsAll(intSetOfTwoC) shouldBe false
  }

  // Iterator -- methods

  test("iterator a") {
    val i0 = intSetOfNone.iterator()
    i0.hasNext() shouldBe false
    shouldThrow<NoSuchElementException> {
      i0.next()
    }

    val i1 = intSetOfOne.iterator()
    i1.hasNext() shouldBe true
    i1.next() shouldBe 1
    i1.hasNext() shouldBe false
    shouldThrow<NoSuchElementException> {
      i1.next()
    }

    val i2 = intSetOfTwo.iterator()
    i2.hasNext() shouldBe true
    val aux1 = i2.next()
    (aux1 == 1 || aux1 == 2) shouldBe true
    i2.hasNext() shouldBe true
    val aux2 = i2.next()
    ((aux2 == 1 || aux2 == 2) && (aux1 != aux2)) shouldBe true
    i2.hasNext() shouldBe false
    shouldThrow<NoSuchElementException> {
      i2.next()
    }
  }

  test("iterator b") {
    val itr = intSetOfFive.iterator() as FSetIterator<Int>
    for (i in (1..intSetOfFive.size)) {
      val tmp = itr.nullableNext()
      tmp?.let { true } shouldBe true
    }
    (itr.nullableNext()?.let { false } ?: true) shouldBe true
  }

  // typeclass (Collection, Iterator, Iterable)

  test("first") {

    shouldThrow<NoSuchElementException> {
      intSetOfNone.first()
    }
    intSetOfNone.firstOrNull() shouldBe null

    intSetOfOne.firstOrNull() shouldBe 1
    val aux = intSetOfTwo.first()
    (aux == 1 || aux == 2) shouldBe true
    (aux in intSetOfTwo) shouldBe true
  }

  //  ignore
  //  test("firstNotNullOf") {}

  test("find first") {
    shouldThrow<NoSuchElementException> {
      intSetOfNone.first(matchEqual(0))
    }
    intSetOfNone.firstOrNull(matchEqual(0)) shouldBe null

    shouldThrow<NoSuchElementException> {
      intSetOfOne.first(matchEqual(0))
    }
    intSetOfOne.firstOrNull(matchEqual(0)) shouldBe null
    intSetOfOne.firstOrNull(matchEqual(1)) shouldBe 1

    intSetOfTwo.firstOrNull(matchEqual(0)) shouldBe null
    intSetOfTwo.first(matchEqual(1)) shouldBe 1
    intSetOfTwo.first(matchEqual(2)) shouldBe 2
    intSetOfTwo.firstOrNull(matchEqual(3)) shouldBe null
  }

  test("indexOf") {
    intSetOfNone.indexOf(0) shouldBe -1

    intSetOfOne.indexOf(0) shouldBe -1
    intSetOfOne.indexOf(1) shouldBe 0
    intSetOfOne.indexOf(2) shouldBe -1

    intSetOfTwo.indexOf(0) shouldBe -1
    val aux1 = intSetOfTwo.indexOf(1)
    (aux1 == 0 || aux1 == 1) shouldBe true
    val aux2 = intSetOfTwo.indexOf(2)
    ((aux2 == 0 || aux2 == 1) && (aux1 != aux2)) shouldBe true
    intSetOfTwo.indexOf(3) shouldBe -1
  }

  test("indexOfirst") {
    intSetOfNone.indexOfFirst(matchEqual(0)) shouldBe -1

    intSetOfOne.indexOfFirst(matchEqual(0)) shouldBe -1
    intSetOfOne.indexOfFirst(matchEqual(1)) shouldBe 0

    intSetOfSix.indexOfFirst(matchEqual(0)) shouldBe -1
    val aux1 = intSetOfSix.indexOfFirst(matchEqual(1))
    (aux1 in (0..intSetOfSix.size)) shouldBe true
    val aux2 = intSetOfSix.indexOfFirst(matchEqual(2))
    (aux2 in (0..intSetOfSix.size) && aux1 != aux2) shouldBe true
    intSetOfSix.indexOfFirst(matchEqual(30)) shouldBe -1
  }

  test("indexOfLast") {
    intSetOfNone.indexOfLast(matchEqual(0)) shouldBe -1

    intSetOfOne.indexOfLast(matchEqual(0)) shouldBe -1
    intSetOfOne.indexOfLast(matchEqual(1)) shouldBe 0

    intSetOfSix.indexOfLast(matchEqual(0)) shouldBe -1
    val aux1 = intSetOfSix.indexOfLast(matchEqual(1))
    (aux1 in (0..intSetOfSix.size)) shouldBe true
    val aux2 = intSetOfSix.indexOfLast(matchEqual(2))
    (aux2 in (0..intSetOfSix.size) && aux1 != aux2) shouldBe true
    intSetOfSix.indexOfLast(matchEqual(30)) shouldBe -1
  }

  test("last") {
    shouldThrow<NoSuchElementException> {
      intSetOfNone.last()
    }
    intSetOfNone.lastOrNull() shouldBe null
    intSetOfOne.lastOrNull() shouldBe 1
    intSetOfOne.last() shouldBe 1
    val aux1 = intSetOfTwo.last()
    (aux1 in intSetOfTwo) shouldBe true
    val aux2 = intSetOfThree.last()
    (aux2 in intSetOfThree) shouldBe true
  }

  test("last (find)") {
    shouldThrow<NoSuchElementException> {
      intSetOfNone.last(matchEqual(0))
    }
    intSetOfNone.lastOrNull(matchEqual(0)) shouldBe null

    intSetOfOne.lastOrNull(matchEqual(1)) shouldBe 1
    intSetOfOne.last(matchEqual(1)) shouldBe 1
    intSetOfOne.lastOrNull(matchEqual(2)) shouldBe null

    intSetOfTwo.lastOrNull(matchEqual(0)) shouldBe null
    intSetOfTwo.last(matchEqual(1)) shouldBe 1
    intSetOfTwo.last(matchEqual(2)) shouldBe 2
    shouldThrow<NoSuchElementException> {
      intSetOfTwo.last(matchEqual(3))
    }
    intSetOfTwo.lastOrNull(matchEqual(3)) shouldBe null
  }

  test("lastIndexOf") {

    intSetOfNone.lastIndexOf(0) shouldBe -1

    intSetOfOne.lastIndexOf(0) shouldBe -1
    intSetOfOne.lastIndexOf(1) shouldBe 0

    intSetOfSix.lastIndexOf(0) shouldBe -1

    val aux1 = intSetOfSix.lastIndexOf(1)
    (aux1 in (0..intSetOfSix.size)) shouldBe true
    val aux2 = intSetOfSix.lastIndexOf(2)
    (aux2 in (0..intSetOfSix.size) && aux1 != aux2) shouldBe true
    intSetOfSix.lastIndexOf(30) shouldBe -1
  }

  test("findlast") {
    intSetOfNone.findLast(matchEqual(0)) shouldBe null

    intSetOfOne.findLast(matchEqual(0)) shouldBe null
    intSetOfOne.findLast(matchEqual(1)) shouldBe 1

    intSetOfTwo.findLast(matchEqual(0)) shouldBe null
    intSetOfTwo.findLast(matchEqual(1)) shouldBe 1
    intSetOfTwo.findLast(matchEqual(2)) shouldBe 2
    intSetOfTwo.findLast(matchEqual(3)) shouldBe null
  }

  test("single") {
    shouldThrow<NoSuchElementException> {
      intSetOfNone.single()
    }
    intSetOfNone.singleOrNull() shouldBe null

    intSetOfOne.single() shouldBe 1
    intSetOfOne.singleOrNull() shouldBe 1

    shouldThrow<IllegalArgumentException> {
      intSetOfTwo.single()
    }
    intSetOfTwo.singleOrNull() shouldBe null
  }

  test("single (find)") {
    shouldThrow<NoSuchElementException> {
      intSetOfNone.single(matchEqual(0))
    }
    intSetOfNone.singleOrNull(matchEqual(0)) shouldBe null

    intSetOfOne.single(matchEqual(1)) shouldBe 1
    intSetOfOne.singleOrNull(matchEqual(1)) shouldBe 1

    shouldThrow<NoSuchElementException> {
      intSetOfTwo.single(matchEqual(0))
    }
    intSetOfTwo.singleOrNull(matchEqual(0)) shouldBe null
    intSetOfTwo.single(matchEqual(1)) shouldBe 1
    intSetOfTwo.single(matchEqual(2)) shouldBe 2
    shouldThrow<NoSuchElementException> {
      intSetOfTwo.single(matchEqual(3))
    }
    intSetOfTwo.singleOrNull(matchEqual(3)) shouldBe null

    shouldThrow<NoSuchElementException> {
      intSetOfTwo.single(matchEqual(0))
    }
    intSetOfTwo.singleOrNull(matchEqual(0)) shouldBe null
    shouldThrow<NoSuchElementException> {
      intSetOfTwo.single(matchEqual(3))
    }
    intSetOfTwo.singleOrNull(matchEqual(3)) shouldBe null
  }

  test("drop 0") {
    intSetOfNone.drop(0).toFSet() shouldBe emptyIMSet()
    intSetOfOne.drop(0).toFSet() shouldBe intSetOfOne
    intSetOfTwo.drop(0).toFSet() shouldBe intSetOfTwo
  }

  test("drop 1") {
    val aux0: Set<Int> = intSetOfThree.drop(1).toSet()
    val aux1: FSet<Int> = aux0.toFSet()
    (aux1.size == intSetOfThree.size - 1) shouldBe true
    intSetOfThree.containsAll(aux1) shouldBe true
    intSetOfNone.drop(1).toFSet() shouldBe emptyIMSet()
    intSetOfOne.drop(1).toFSet() shouldBe emptyIMSet()
    val aux2 = intSetOfTwo.drop(1).toSet()
    (aux2.size == intSetOfTwo.size - 1) shouldBe true
    intSetOfTwo.containsAll(aux2) shouldBe true
  }

  test("drop 2") {
    intSetOfNone.drop(2).toFSet() shouldBe emptyIMSet()
    intSetOfOne.drop(2).toFSet() shouldBe emptyIMSet()
    intSetOfTwo.drop(2).toFSet() shouldBe emptyIMSet()

    val aux1 = intSetOfThree.drop(2).toSet()
    (aux1.size == intSetOfThree.size - 2) shouldBe true
    intSetOfThree.containsAll(aux1) shouldBe true

    val a2 = FSet.of(*arrayOf<Int>(1,2,3,4))
    val aux2 = a2.drop(2).toSet()
    (aux2.size == a2.size - 2) shouldBe true
    a2.containsAll(aux2) shouldBe true
  }

  test("drop 3") {
    intSetOfNone.drop(3).toFSet() shouldBe emptyIMSet()
    intSetOfOne.drop(3).toFSet() shouldBe emptyIMSet()
    intSetOfTwo.drop(3).toFSet() shouldBe emptyIMSet()
    intSetOfThree.drop(3).toFSet() shouldBe emptyIMSet()
    val a2 = FSet.of(*arrayOf<Int>(1,2,3,4))
    val aux2 = a2.drop(3).toSet()
    (aux2.size == a2.size - 3) shouldBe true
    a2.containsAll(aux2) shouldBe true
  }

  test("dropWhile") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FSet.of(*arrayOf<Int>(2,1)).dropWhile { it > 1 }
    }
  }

  test("filter") {
    intSetOfNone.filter {0 == it % 2}.toFSet() shouldBe emptyIMSet()
    intSetOfOne.filter {0 == it % 2}.toFSet() shouldBe emptyIMSet()
    intSetOfTwo.filter {0 == it % 2}.toFSet() shouldBe FSet.of(2)
    intSetOfThree.filter {0 == it % 2}.toFSet() shouldBe FSet.of(2)
    FSet.of(*arrayOf<Int>(1,2,3,4)).filter {0 == it % 2}.toFSet() shouldBe FSet.of(2, 4)
  }

  //  ignore
  //  test("filter indexed") {}
  //  test("filterIsInstance") {}

  test("filterNot") {
    intSetOfNone.filterNot {0 == it % 2}.toFSet() shouldBe emptyIMSet()
    intSetOfOne.filterNot {0 == it % 2}.toFSet() shouldBe FSet.of(1)
    intSetOfTwo.filterNot {0 == it % 2}.toFSet() shouldBe FSet.of(1)
    intSetOfThree.filterNot {0 == it % 2}.toFSet() shouldBe FSet.of(1,3)
    FSet.of(*arrayOf<Int>(1,2,3,4)).filterNot {0 == it % 2}.toFSet() shouldBe FSet.of(1, 3)
  }

  //  ignore
  //  test("filterNotNull") {}

  test("take 0") {
    intSetOfNone.take(0).toFSet() shouldBe emptyIMSet()
    intSetOfOne.take(0).toFSet() shouldBe emptyIMSet()
    intSetOfTwo.take(0).toFSet() shouldBe emptyIMSet()
  }

  test("take 1") {
    intSetOfNone.take(1).toFSet() shouldBe emptyIMSet()
    intSetOfOne.take(1).toFSet() shouldBe intSetOfOne

    val aux1 = intSetOfTwo.take(1).toSet()
    aux1.size shouldBe 1
    intSetOfTwo.containsAll(aux1) shouldBe true

    val aux2 = intSetOfThree.take(1).toSet()
    aux2.size shouldBe 1
    intSetOfThree.containsAll(aux2) shouldBe true
  }

  test("take 2") {
    intSetOfNone.take(2).toFSet() shouldBe emptyIMSet()
    intSetOfOne.take(2).toFSet() shouldBe intSetOfOne

    val aux1 = intSetOfTwo.take(2).toSet()
    aux1.size shouldBe 2
    intSetOfTwo.containsAll(aux1) shouldBe true

    val aux2 = intSetOfThree.take(2).toSet()
    aux2.size shouldBe 2
    intSetOfThree.containsAll(aux2) shouldBe true

    val a3 = FSet.of(*arrayOf<Int>(1,2,3,4))
    val aux3 = a3.take(2)
    aux3.size shouldBe 2
    a3.containsAll(aux3) shouldBe true
  }

  test("take 3") {
    intSetOfNone.take(3).toFSet() shouldBe emptyIMSet()
    intSetOfOne.take(3).toFSet() shouldBe intSetOfOne
    intSetOfTwo.take(3).toFSet() shouldBe intSetOfTwo
    intSetOfThree.take(3).toFSet() shouldBe intSetOfThree
    val a3 = FSet.of(*arrayOf<Int>(1,2,3,4))
    val aux3 = a3.take(3)
    aux3.size shouldBe 3
    a3.containsAll(aux3) shouldBe true
  }

  test("takeWhile") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FSet.of(*arrayOf<Int>(2,1)).takeWhile { it > 1 }
    }
  }

  test("reversed") {
    intSetOfNone.reversed().toFSet() shouldBe emptyIMSet()
    intSetOfOne.reversed().toFSet() shouldBe intSetOfOne
    intSetOfTwo.reversed().toFSet() shouldBe intSetOfTwo.toFSet()
    intSetOfThree.reversed().toFSet() shouldBe intSetOfThree.toFSet()
  }

  test("sorted") {
    intSetOfNone.sorted().toSet() shouldBe emptyIMSet()
    intSetOfOne.sorted().toSet() shouldBe intSetOfOne
    intSetOfTwo.sorted().toSet() shouldBe intSetOfTwo
    intSetOfThree.sorted().toSet() shouldBe intSetOfThree
    intSetOfSix.sorted().toSet() shouldBe intSetOfThree
  }

  test("sortedDescending") {
    intSetOfNone.sortedDescending().toSet() shouldBe emptyIMSet()
    intSetOfOne.sortedDescending().toSet() shouldBe intSetOfOne
    intSetOfTwo.sortedDescending().toSet() shouldBe  intSetOfTwo
    intSetOfThree.sortedDescending().toSet() shouldBe intSetOfThree
    intSetOfSix.sortedDescending().toSet() shouldBe intSetOfThree
  }

  test("sortedBy") {

    fun reverseNumerical(t: Int): Int? = -t

    intSetOfNone.sortedBy(::reverseNumerical).toSet() shouldBe emptyIMSet()
    intSetOfOne.sortedBy(::reverseNumerical).toSet() shouldBe intSetOfOne
    intSetOfTwo.sortedBy(::reverseNumerical).toSet() shouldBe intSetOfTwo
    intSetOfThree.sortedBy(::reverseNumerical).toSet() shouldBe intSetOfThree
    intSetOfSix.sortedBy(::reverseNumerical).toSet() shouldBe intSetOfThree
  }

  test("sortedByDescending") {

    fun reverseNumerical(t: Int): Int? = -t

    intSetOfNone.sortedByDescending(::reverseNumerical).toFSet() shouldBe emptyIMSet()
    intSetOfOne.sortedByDescending(::reverseNumerical).toFSet() shouldBe intSetOfOne
    intSetOfTwo.sortedByDescending(::reverseNumerical).toFSet() shouldBe intSetOfTwo
    intSetOfThree.sortedByDescending(::reverseNumerical).toFSet() shouldBe intSetOfThree
    intSetOfSix.sortedByDescending(::reverseNumerical).toFSet() shouldBe intSetOfThree
  }

  test("sortedWith") {

    val reverseNumerical: Comparator<Int> = Comparator<Int> { p0, p1 ->
      when {
        p0 == p1 -> 0
        p0 > p1 -> -1
        else -> 1
      }
    }

    intSetOfNone.sortedWith(reverseNumerical).toFSet() shouldBe emptyIMSet()
    intSetOfOne.sortedWith(reverseNumerical).toFSet() shouldBe intSetOfOne
    intSetOfTwo.sortedWith(reverseNumerical).toFSet() shouldBe intSetOfTwo
    intSetOfThree.sortedWith(reverseNumerical).toFSet() shouldBe intSetOfThree
    intSetOfSix.sortedWith(reverseNumerical).toFSet() shouldBe intSetOfThree
  }

  test("associate") {

    fun f(t: Int): Pair<Int, Int> = Pair(t, -t)

    intSetOfNone.associate(::f) shouldBe emptyMap()
    intSetOfOne.associate(::f) shouldBe mapOf( 1 to -1 )
    intSetOfTwo.associate(::f) shouldBe mapOf( 1 to -1, 2 to -2 )
    intSetOfThree.associate(::f) shouldBe mapOf( 1 to -1, 2 to -2, 3 to -3 )
  }

  test("associateBy") {

    fun f(t: Int): Int = -t

    intSetOfNone.associateBy(::f) shouldBe emptyMap()
    intSetOfOne.associateBy(::f) shouldBe mapOf( -1 to 1 )
    intSetOfTwo.associateBy(::f) shouldBe mapOf( -1 to 1, -2 to 2 )
    intSetOfThree.associateBy(::f) shouldBe mapOf( -1 to 1, -2 to 2, -3 to 3 )
  }

  test("associateBy (k, v)") {

    fun f(t: Int): Int = -t
    fun g(t: Int): Int = 2*t

    intSetOfNone.associateBy(::f, ::g) shouldBe emptyMap()
    intSetOfOne.associateBy(::f, ::g) shouldBe mapOf( -1 to 2 )
    intSetOfTwo.associateBy(::f, ::g) shouldBe mapOf( -1 to 2, -2 to 4 )
    intSetOfThree.associateBy(::f, ::g) shouldBe mapOf( -1 to 2, -2 to 4, -3 to 6 )
  }

  test("associateWith") {

    fun g(t: Int): Int = 2*t

    intSetOfNone.associateWith(::g) shouldBe emptyMap()
    intSetOfOne.associateWith(::g) shouldBe mapOf( 1 to 2 )
    intSetOfTwo.associateWith(::g) shouldBe mapOf( 1 to 2, 2 to 4 )
    intSetOfThree.associateWith(::g) shouldBe mapOf( 1 to 2, 2 to 4, 3 to 6 )
  }

  test("flatMap") {
    intSetOfNone.flatMap{ FSet.of(it) }.toFSet() shouldBe emptyIMSet()
    intSetOfOne.flatMap{ FSet.of(it) }.toFSet() shouldBe FSet.of(1)
    fun arrayBuilderConst(arg: Int) = Array<Int>(arg) { _ -> arg }
    intSetOfTwo.flatMap {FSet.of(*arrayBuilderConst(it))}.toFSet() shouldBe FSet.of(1, 2)
    fun arrayBuilderIncrement(arg: Int) = Array<Int>(arg) { i -> arg + i }
    intSetOfTwo.flatMap {FSet.of(*arrayBuilderIncrement(it))}.toFSet() shouldBe FSet.of(1, 2, 3)
    intSetOfThree.flatMap {FSet.of(*arrayBuilderIncrement(it))}.toFSet() shouldBe FSet.of(1, 2, 3, 4, 5)
    intSetOfThree.flatMap { i -> FSet.of(i, i) }.toFSet() shouldBe intSetOfThree
  }

  // ignore
  // test("flatMapIndexed") {}

  test("groupBy") {

    fun f(t: Int): Int = -t

    intSetOfNone.groupBy(::f) shouldBe emptyMap()
    intSetOfFive.groupBy(::f) shouldBe mapOf( -1 to setOf(1, 1), -2 to setOf(2,2), -3 to setOf(3) )
  }

  test("groupBy (k, v)") {

    fun f(t: Int): Int = -t
    fun g(t: Int): Int = 2*t

    intSetOfNone.groupBy(::f, ::g) shouldBe emptyMap()
    intSetOfFive.groupBy(::f, ::g) shouldBe mapOf( -1 to setOf(2, 2), -2 to setOf(4,4), -3 to setOf(6) )
  }

  // TODO (maybe)
  // test("grouping") {
  //   fail("not implemented yet")
  // }

  test("map") {
    intSetOfNone.map { it + 1}.toFSet() shouldBe emptyIMSet()
    intSetOfOne.map { it + 1}.toFSet() shouldBe FSet.of(2)
    intSetOfTwo.map { it + 1}.toFSet() shouldBe FSet.of(2, 3)
  }

  // ignore
  // test("mapIndexed") {}
  // test("mapIndexedNotNull") {}
  // test("mapNotNull") {}

  test("withIndex") {
    val nwi: Iterable<IndexedValue<Int>> = intSetOfNone.withIndex()
    nwi.iterator().hasNext() shouldBe false

    val twi = intSetOfThree.withIndex()
    val twii = twi.iterator()
    val twiv0 = twii.next()
    twiv0.index shouldBe 0
    val aux1 = twiv0.value
    (aux1 in intSetOfThree) shouldBe true
    val twiv1 = twii.next()
    twiv1.index shouldBe 1
    val aux2 = twiv1.value
    (aux2 in intSetOfThree) shouldBe true
    (aux1 != aux2) shouldBe true
    val twiv2 = twii.next()
    twiv2.index shouldBe 2
    val aux3 = twiv2.value
    (aux3 in intSetOfThree) shouldBe true
    (aux3 !in setOf(aux1, aux2)) shouldBe true
    twii.hasNext() shouldBe false
  }

  test("distinct") {
    intSetOfNone.distinct().toFSet() shouldBe emptyIMSet()
    intSetOfOne.distinct() shouldBe intSetOfOne.toSet()
    intSetOfTwo.distinct() shouldBe intSetOfTwo.toSet()
    intSetOfFive.distinct() shouldBe intSetOfThree.toSet()
    intSetOfSix.distinct() shouldBe intSetOfThree.toSet()
  }

  test("distinctBy") {

    fun identity(oracle: Int) = oracle

    intSetOfNone.distinctBy(::identity).toFSet() shouldBe emptyIMSet()
    intSetOfOne.distinctBy(::identity) shouldBe intSetOfOne.toSet()
    intSetOfTwo.distinctBy(::identity) shouldBe intSetOfTwo.toSet()
    intSetOfFive.distinctBy(::identity) shouldBe intSetOfThree.toSet()
    intSetOfSix.distinctBy(::identity) shouldBe intSetOfThree.toSet()
  }

  // ignore
  // test("intersect") {}
  // test("subtract") {}
  // test("union") {}

  test("all") {
    intSetOfNone.all(matchLessThan(0)) shouldBe true // by vacuous implication
    intSetOfOne.all(matchLessThan(1)) shouldBe false
    intSetOfOne.all(matchLessThan(2)) shouldBe true
    intSetOfThree.all(matchLessThan(2)) shouldBe false
    intSetOfThree.all(matchLessThan(4)) shouldBe true
  }

  test("any") {
    intSetOfNone.any(matchLessThan(0)) shouldBe false
    intSetOfOne.any(matchLessThan(1)) shouldBe false
    intSetOfOne.any(matchLessThan(2)) shouldBe true
    intSetOfThree.any(matchLessThan(1)) shouldBe false
    intSetOfThree.any(matchLessThan(2)) shouldBe true
    intSetOfThree.any(matchLessThan(4)) shouldBe true
  }

  test("(has) any") {
    intSetOfNone.any() shouldBe false
    intSetOfOne.any() shouldBe true
    intSetOfThree.any() shouldBe true
  }

  test("count") {
    intSetOfNone.count() shouldBe 0
    intSetOfOne.count() shouldBe 1
    intSetOfThree.count() shouldBe 3
  }

  test("count matching") {
    intSetOfNone.count(matchEqual(0)) shouldBe 0
    intSetOfFive.count(matchEqual(3)) shouldBe 1
    intSetOfSix.count(matchEqual(3)) shouldBe 1
  }

  test("fold") {

    val s = { acc: Int, b: Int -> acc - b }

    intSetOfNone.fold(1, s) shouldBe 1
    intSetOfOne.fold(1, s) shouldBe 0
    intSetOfTwo.fold(1, s) shouldBe -2
    intSetOfTwoA.fold(1, s) shouldBe -3
    intSetOfThree.fold(1, s) shouldBe -5
    intSetOfThreeA.fold(1, s) shouldBe -7
  }

  // ignore
  // test("foldIndexed") {}

  test("(has) none") {
    intSetOfNone.none() shouldBe true
    intSetOfOne.none() shouldBe false
    intSetOfThree.none() shouldBe false
  }

  test("none") {
    intSetOfNone.none(matchLessThan(0)) shouldBe true
    intSetOfOne.none(matchLessThan(1)) shouldBe true
    intSetOfOne.none(matchLessThan(2)) shouldBe false
    intSetOfThree.none(matchLessThan(1)) shouldBe true
    intSetOfThree.none(matchLessThan(2)) shouldBe false
    intSetOfThree.none(matchLessThan(4)) shouldBe false
  }

  test("reduce") {

    // since order is not a property of Set, f MUST be commutative
    val ss = { acc: Int, b: Int -> b + acc }

    shouldThrow<UnsupportedOperationException> {
      intSetOfNone.reduce(ss)
    }
    intSetOfNone.reduceOrNull(ss) shouldBe null
    intSetOfOne.reduce(ss) shouldBe 1
    intSetOfTwo.reduce(ss) shouldBe 3
    intSetOfTwoA.reduce(ss) shouldBe 4
    intSetOfTwoC.reduce(ss) shouldBe 5
    intSetOfThree.reduce(ss) shouldBe 6
    intSetOfThreeA.reduce(ss) shouldBe 8
  }

  // ignore
  // test("reduceIndexed") {}
  // test("reduceIndexedOrNull") {}

  test("runningFold") {
    shouldThrow<RuntimeException> {
      val ss = { acc: Int, b: Int -> b + acc }
      @Suppress("DEPRECATION")
      FSet.of(*arrayOf<Int>(2,1)).runningFold(1, ss)
    }
  }

  // ignore
  // test("runningFoldIndexed") {}

  test("runningReduce") {
    shouldThrow<RuntimeException> {
      val ss = { acc: Int, b: Int -> b + acc }
      @Suppress("DEPRECATION")
      FSet.of(*arrayOf<Int>(2,1)).runningReduce(ss)
    }
  }

  // ignore
  // test("runningReduceIndexed") {}

  test("partition") {
    (intSetOfOne.partition(matchLessThan(1)).pmap1 { l -> of(l.iterator()) }) shouldBe Pair(emptyIMSet(), setOf(1))
    (intSetOfThree.partition(matchLessThan(2)).pmap1 { l -> of(l.iterator()) }) shouldBe Pair(setOf(1), setOf(2, 3))
  }

  test("windowed") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FSet.of(*arrayOf<Int>(2,1)).windowed(2)
    }
  }

  test("zip array") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FSet.of(*arrayOf<Int>(2,1)).zip(arrayOf<String>()){a, b -> Pair(a,b)}
    }
  }

  test("zip iterable") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FSet.of(*arrayOf<Int>(2,1)).zip(setOf<String>())
    }
  }

  // ignore
  // test("zipWithNext"){}
  // test("zipWithNext (transform)"){}
})
