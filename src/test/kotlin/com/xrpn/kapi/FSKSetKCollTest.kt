package com.xrpn.kapi

import com.xrpn.bridge.FKSetIterator
import com.xrpn.imapi.IMSet
import com.xrpn.imapi.IntKeyType
import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.FKSet
import com.xrpn.immutable.FKSet.Companion.NOT_FOUND
import com.xrpn.immutable.FKSet.Companion.asFKSet
import com.xrpn.immutable.FKSet.Companion.emptyIMRSet
import com.xrpn.immutable.FKSet.Companion.emptyIMKSet
import com.xrpn.immutable.FKSet.Companion.ofi
import com.xrpn.immutable.FKSet.Companion.toIMKSet
import com.xrpn.immutable.emptyArrayOfStr
import com.xrpn.immutable.pmap1
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSSetOfNone: Collection<Int> = FKSet.ofs(*emptyArrayOfInt)
private val intSSetOfOneA: Collection<Int> = FKSet.ofs(0)
private val intSSetOfOne: Collection<Int> = FKSet.ofs(1)
private val intSSetOfOneB: Collection<Int> = FKSet.ofs(2)
private val intSSetOfOneC: Collection<Int> = FKSet.ofs(3)
private val intSSetOfOneD: Collection<Int> = FKSet.ofs(4)
private val intSSetOfTwoA: Collection<Int> = FKSet.ofs(*arrayOf<Int>(1,3))
private val intSSetOfTwo: Collection<Int> = FKSet.ofs(*arrayOf<Int>(1,2))
private val intSSetOfTwoB: Collection<Int> = FKSet.ofs(*arrayOf<Int>(0,2))
private val intSSetOfTwoC: Collection<Int> = FKSet.ofs(*arrayOf<Int>(1,4))
private val intSSetOfThree: Collection<Int> = FKSet.ofs(*arrayOf<Int>(1,2,3))
private val intSSetOfThreeA: Collection<Int> = FKSet.ofs(*arrayOf<Int>(1,2,5))
private val intSSetOfFive: Collection<Int> = FKSet.ofs(*arrayOf<Int>(1,2,3,4,5))
private val intSSetOfFiveA: Collection<Int> = FKSet.ofs(*arrayOf<Int>(1,2,3,2,1))
private val intSSetOfSix: Collection<Int> = FKSet.ofs(*arrayOf<Int>(1,2,3,3,2,1))
private val intSSetOf2: Collection<Int> = FKSet.ofs(2)
private val intSSetOf3: Collection<Int> = FKSet.ofs(3)
private val intSSetOf4: Collection<Int> = FKSet.ofs(4)
private val intSSetOf5: Collection<Int> = FKSet.ofs(5)
private val intSSetOf13: Collection<Int> = FKSet.ofs(1, 3)
private val intSSetOf23: Collection<Int> = FKSet.ofs(2, 3)
private val intSSetOf25: Collection<Int> = FKSet.ofs(2, 5)
private val intSSetOf45: Collection<Int> = FKSet.ofs(4, 5)
private val intSSetOf125: Collection<Int> = FKSet.ofs(1, 2, 5)
private val intSSetOf145: Collection<Int> = FKSet.ofs(1, 4, 5)
private val intSSetOf245: Collection<Int> = FKSet.ofs(2, 4, 5)
private val intSSetOf1245: Collection<Int> = FKSet.ofs(1, 2, 4, 5)
private val intSSetOf1235: Collection<Int> = FKSet.ofs(1, 2, 3, 5)
private val intSSetOf1345: Collection<Int> = FKSet.ofs(1, 3, 4, 5)


class FSKSetKCollTest : FunSpec({

  beforeTest {}

  fun <Z: Comparable<Z>> matchEqual(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle == aut }
  fun <Z: Comparable<Z>> matchLessThan(oracle: Z): (Z) -> Boolean = { aut: Z -> aut < oracle }

  // Any equals

  test("FSKSet equals") {
    (intSSetOfNone == FKSet.ofi(*emptyArrayOfInt)) shouldBe true
    (intSSetOfNone.equals(emptySet<Int>())) shouldBe true
    (intSSetOfNone == emptySet<Int>()) shouldBe true
    (intSSetOfNone == emptySet<Int>().toIMKSet(IntKeyType)) shouldBe true
    (intSSetOfNone == FKSet.ofi(*arrayOf(1))) shouldBe false
    (intSSetOfNone == setOf(1)) shouldBe false
    (intSSetOfNone == setOf(1).toIMKSet(IntKeyType)) shouldBe false

    (intSSetOfOne == FKSet.ofi(*emptyArrayOfInt)) shouldBe false
    (intSSetOfOne == emptySet<Int>()) shouldBe false
    (intSSetOfOne == FKSet.ofi(*arrayOf(1))) shouldBe false
    (intSSetOfOne == FKSet.ofs(*arrayOf(1))) shouldBe true
    (intSSetOfOne == setOf(1)) shouldBe true
    (intSSetOfOne == setOf(1).toIMKSet(IntKeyType)) shouldBe false
    (intSSetOfOne == setOf(1).toIMKSet(IntKeyType)) shouldBe false
    (intSSetOfOne == FKSet.ofi(*arrayOf(1, 2))) shouldBe false
  }

  test("FSKSet equals miss") {
    (intSSetOfOne.equals(FKSet.ofi(*arrayOf(2)))) shouldBe false
    (intSSetOfTwo == setOf(1)) shouldBe false
    (intSSetOfTwo == setOf(2)) shouldBe false
    (intSSetOfTwo == setOf(2, 1)) shouldBe true
    (intSSetOfTwo == setOf(1, 2)) shouldBe true
    (intSSetOfTwo == intSSetOfThree) shouldBe false
    (intSSetOfThree == setOf(1, 2, 3)) shouldBe true
    (intSSetOfThree == setOf(1, 3, 2)) shouldBe true
    (intSSetOfThree == setOf(2, 1, 3)) shouldBe true
    (intSSetOfThree == setOf(3, 2, 1)) shouldBe true
  }

  test("Collections equals") {
    (emptySet<Int>() == intSSetOfOne) shouldBe false
    (emptySet<Int>() == intSSetOfOne.toSet()) shouldBe false
    (setOf(1) == intSSetOfOne) shouldBe true
    (setOf(1) == intSSetOfOne.toSet()) shouldBe true
    (setOf(1) == intSSetOfTwo.toSet()) shouldBe false
    (setOf(1,2) == intSSetOfOne.toSet()) shouldBe false

    (emptySet<Int>() == intSSetOfNone) shouldBe true
    (emptySet<Int>() == intSSetOfNone.toSet()) shouldBe true
    (setOf<Int>(1) == intSSetOfNone.toSet()) shouldBe false
  }

  test("Collections equals miss") {
    (setOf(2) == intSSetOfOne) shouldBe false
    (setOf(1) == intSSetOfTwo) shouldBe false
    (setOf(2) == intSSetOfTwo) shouldBe false
    (setOf(2, 1) == intSSetOfTwo) shouldBe true
    (setOf(1, 2) == intSSetOfTwo) shouldBe true
    (setOf(1, 2) == intSSetOfThree) shouldBe false
    (setOf(1, 2, 3) == intSSetOfThree) shouldBe true
    (setOf(1, 3, 2) == intSSetOfThree) shouldBe true
    (setOf(2, 1, 3) == intSSetOfThree) shouldBe true
    (setOf(3, 2, 1) == intSSetOfThree) shouldBe true
  }

  // Collection -- methods or fields

  test("size") {
    intSSetOfNone.size shouldBe 0
    intSSetOfOne.size shouldBe 1
    intSSetOfTwo.size shouldBe 2
    intSSetOfThree.size shouldBe 3
  }

  test("isEmpty") {
    intSSetOfNone.isEmpty() shouldBe true
    intSSetOfOne.isEmpty() shouldBe false
    intSSetOfTwo.isEmpty() shouldBe false
    intSSetOfThree.isEmpty() shouldBe false
  }

  test("contains") {
    intSSetOfNone.contains(0) shouldBe false
    intSSetOfOne.contains(0) shouldBe false
    intSSetOfOne.contains(1) shouldBe true
    intSSetOfOne.contains(2) shouldBe false
    intSSetOfTwo.contains(0) shouldBe false
    intSSetOfTwo.contains(1) shouldBe true
    intSSetOfTwo.contains(2) shouldBe true
    intSSetOfTwo.contains(3) shouldBe false
  }

  test("containsAll") {
    intSSetOfNone.containsAll(intSSetOfNone) shouldBe true
    intSSetOfNone.containsAll(intSSetOfOne) shouldBe false

    intSSetOfOne.containsAll(intSSetOfNone) shouldBe true
    intSSetOfOne.containsAll(intSSetOfOne) shouldBe true
    intSSetOfOne.containsAll(intSSetOfTwo) shouldBe false

    intSSetOfTwo.containsAll(intSSetOfNone) shouldBe true
    intSSetOfTwo.containsAll(intSSetOfOne) shouldBe true
    intSSetOfTwo.containsAll(intSSetOfTwo) shouldBe true
    intSSetOfTwo.containsAll(intSSetOfThree) shouldBe false

    intSSetOfThree.containsAll(intSSetOfNone) shouldBe true
    intSSetOfThree.containsAll(intSSetOfOne) shouldBe true
    intSSetOfThree.containsAll(intSSetOfOneB) shouldBe true
    intSSetOfThree.containsAll(intSSetOfOneC) shouldBe true
    intSSetOfThree.containsAll(intSSetOfTwo) shouldBe true
    intSSetOfThree.containsAll(intSSetOfTwoA) shouldBe true
    intSSetOfThree.containsAll(intSSetOfOneA) shouldBe false
    intSSetOfThree.containsAll(intSSetOfOneD) shouldBe false
    intSSetOfThree.containsAll(intSSetOfTwoB) shouldBe false
    intSSetOfThree.containsAll(intSSetOfTwoC) shouldBe false
  }

  // Iterator -- methods

  test("iterator a") {
    val i0 = intSSetOfNone.iterator()
    i0.hasNext() shouldBe false
    shouldThrow<NoSuchElementException> {
      i0.next()
    }

    val i1 = intSSetOfOne.iterator()
    i1.hasNext() shouldBe true
    i1.next() shouldBe 1
    i1.hasNext() shouldBe false
    shouldThrow<NoSuchElementException> {
      i1.next()
    }

    val i2 = intSSetOfTwo.iterator()
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
    val itr = @Suppress("UNCHECKED_CAST") (intSSetOfFiveA.iterator() as FKSetIterator<Int, Int>)
    for (i in (1..intSSetOfFiveA.size)) {
      val tmp = itr.nullableNext()
      tmp?.let { true } shouldBe true
    }
    (itr.nullableNext()?.let { false } ?: true) shouldBe true
  }

  // typeclass (Collection, Iterator, Iterable)

  test("first") {

    shouldThrow<NoSuchElementException> {
      intSSetOfNone.first()
    }
    intSSetOfNone.firstOrNull() shouldBe null

    intSSetOfOne.firstOrNull() shouldBe 1
    val aux = intSSetOfTwo.first()
    (aux == 1 || aux == 2) shouldBe true
    (aux in intSSetOfTwo) shouldBe true
  }

  //  ignore
  //  test("firstNotNullOf") {}

  test("find first") {
    shouldThrow<NoSuchElementException> {
      intSSetOfNone.first(matchEqual(0))
    }
    intSSetOfNone.firstOrNull(matchEqual(0)) shouldBe null

    shouldThrow<NoSuchElementException> {
      intSSetOfOne.first(matchEqual(0))
    }
    intSSetOfOne.firstOrNull(matchEqual(0)) shouldBe null
    intSSetOfOne.firstOrNull(matchEqual(1)) shouldBe 1

    intSSetOfTwo.firstOrNull(matchEqual(0)) shouldBe null
    intSSetOfTwo.first(matchEqual(1)) shouldBe 1
    intSSetOfTwo.first(matchEqual(2)) shouldBe 2
    intSSetOfTwo.firstOrNull(matchEqual(3)) shouldBe null
  }

  test("indexOf") {
    intSSetOfNone.indexOf(0) shouldBe NOT_FOUND

    intSSetOfOne.indexOf(0) shouldBe NOT_FOUND
    intSSetOfOne.indexOf(1) shouldBe 0
    intSSetOfOne.indexOf(2) shouldBe NOT_FOUND

    intSSetOfTwo.indexOf(0) shouldBe NOT_FOUND
    val aux1 = intSSetOfTwo.indexOf(1)
    (aux1 == 0 || aux1 == 1) shouldBe true
    val aux2 = intSSetOfTwo.indexOf(2)
    ((aux2 == 0 || aux2 == 1) && (aux1 != aux2)) shouldBe true
    intSSetOfTwo.indexOf(3) shouldBe NOT_FOUND
  }

  test("indexOfirst") {
    intSSetOfNone.indexOfFirst(matchEqual(0)) shouldBe NOT_FOUND

    intSSetOfOne.indexOfFirst(matchEqual(0)) shouldBe NOT_FOUND
    intSSetOfOne.indexOfFirst(matchEqual(1)) shouldBe 0

    intSSetOfSix.indexOfFirst(matchEqual(0)) shouldBe NOT_FOUND
    val aux1 = intSSetOfSix.indexOfFirst(matchEqual(1))
    (aux1 in (0..intSSetOfSix.size)) shouldBe true
    val aux2 = intSSetOfSix.indexOfFirst(matchEqual(2))
    (aux2 in (0..intSSetOfSix.size) && aux1 != aux2) shouldBe true
    intSSetOfSix.indexOfFirst(matchEqual(30)) shouldBe NOT_FOUND
  }

  test("indexOfLast") {
    intSSetOfNone.indexOfLast(matchEqual(0)) shouldBe NOT_FOUND

    intSSetOfOne.indexOfLast(matchEqual(0)) shouldBe NOT_FOUND
    intSSetOfOne.indexOfLast(matchEqual(1)) shouldBe 0

    intSSetOfSix.indexOfLast(matchEqual(0)) shouldBe NOT_FOUND
    val aux1 = intSSetOfSix.indexOfLast(matchEqual(1))
    (aux1 in (0..intSSetOfSix.size)) shouldBe true
    val aux2 = intSSetOfSix.indexOfLast(matchEqual(2))
    (aux2 in (0..intSSetOfSix.size) && aux1 != aux2) shouldBe true
    intSSetOfSix.indexOfLast(matchEqual(30)) shouldBe NOT_FOUND
  }

  test("last") {
    shouldThrow<NoSuchElementException> {
      intSSetOfNone.last()
    }
    intSSetOfNone.lastOrNull() shouldBe null
    intSSetOfOne.lastOrNull() shouldBe 1
    intSSetOfOne.last() shouldBe 1
    val aux1 = intSSetOfTwo.last()
    (aux1 in intSSetOfTwo) shouldBe true
    val aux2 = intSSetOfThree.last()
    (aux2 in intSSetOfThree) shouldBe true
  }

  test("last (find)") {
    shouldThrow<NoSuchElementException> {
      intSSetOfNone.last(matchEqual(0))
    }
    intSSetOfNone.lastOrNull(matchEqual(0)) shouldBe null

    intSSetOfOne.lastOrNull(matchEqual(1)) shouldBe 1
    intSSetOfOne.last(matchEqual(1)) shouldBe 1
    intSSetOfOne.lastOrNull(matchEqual(2)) shouldBe null

    intSSetOfTwo.lastOrNull(matchEqual(0)) shouldBe null
    intSSetOfTwo.last(matchEqual(1)) shouldBe 1
    intSSetOfTwo.last(matchEqual(2)) shouldBe 2
    shouldThrow<NoSuchElementException> {
      intSSetOfTwo.last(matchEqual(3))
    }
    intSSetOfTwo.lastOrNull(matchEqual(3)) shouldBe null
  }

  test("lastIndexOf") {

    intSSetOfNone.lastIndexOf(0) shouldBe NOT_FOUND

    intSSetOfOne.lastIndexOf(0) shouldBe NOT_FOUND
    intSSetOfOne.lastIndexOf(1) shouldBe 0

    intSSetOfSix.lastIndexOf(0) shouldBe NOT_FOUND

    val aux1 = intSSetOfSix.lastIndexOf(1)
    (aux1 in (0..intSSetOfSix.size)) shouldBe true
    val aux2 = intSSetOfSix.lastIndexOf(2)
    (aux2 in (0..intSSetOfSix.size) && aux1 != aux2) shouldBe true
    intSSetOfSix.lastIndexOf(30) shouldBe NOT_FOUND
  }

  test("findlast") {
    intSSetOfNone.findLast(matchEqual(0)) shouldBe null

    intSSetOfOne.findLast(matchEqual(0)) shouldBe null
    intSSetOfOne.findLast(matchEqual(1)) shouldBe 1

    intSSetOfTwo.findLast(matchEqual(0)) shouldBe null
    intSSetOfTwo.findLast(matchEqual(1)) shouldBe 1
    intSSetOfTwo.findLast(matchEqual(2)) shouldBe 2
    intSSetOfTwo.findLast(matchEqual(3)) shouldBe null
  }

  test("single") {
    shouldThrow<NoSuchElementException> {
      intSSetOfNone.single()
    }
    intSSetOfNone.singleOrNull() shouldBe null

    intSSetOfOne.single() shouldBe 1
    intSSetOfOne.singleOrNull() shouldBe 1

    shouldThrow<IllegalArgumentException> {
      intSSetOfTwo.single()
    }
    intSSetOfTwo.singleOrNull() shouldBe null
  }

  test("single (find)") {
    shouldThrow<NoSuchElementException> {
      intSSetOfNone.single(matchEqual(0))
    }
    intSSetOfNone.singleOrNull(matchEqual(0)) shouldBe null

    intSSetOfOne.single(matchEqual(1)) shouldBe 1
    intSSetOfOne.singleOrNull(matchEqual(1)) shouldBe 1

    shouldThrow<NoSuchElementException> {
      intSSetOfTwo.single(matchEqual(0))
    }
    intSSetOfTwo.singleOrNull(matchEqual(0)) shouldBe null
    intSSetOfTwo.single(matchEqual(1)) shouldBe 1
    intSSetOfTwo.single(matchEqual(2)) shouldBe 2
    shouldThrow<NoSuchElementException> {
      intSSetOfTwo.single(matchEqual(3))
    }
    intSSetOfTwo.singleOrNull(matchEqual(3)) shouldBe null

    shouldThrow<NoSuchElementException> {
      intSSetOfTwo.single(matchEqual(0))
    }
    intSSetOfTwo.singleOrNull(matchEqual(0)) shouldBe null
    shouldThrow<NoSuchElementException> {
      intSSetOfTwo.single(matchEqual(3))
    }
    intSSetOfTwo.singleOrNull(matchEqual(3)) shouldBe null
  }

  test("drop 0") {
    intSSetOfNone.drop(0).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfOne.drop(0).toIMKSet(IntKeyType) shouldBe intSSetOfOne
    intSSetOfTwo.drop(0).toIMKSet(IntKeyType) shouldBe intSSetOfTwo
  }

  test("drop 1") {
    val aux0: Set<Int> = intSSetOfThree.drop(1).toSet()
    val aux1: IMSet<Int> = aux0.toIMKSet(IntKeyType)!!
    (aux1.fsize() == intSSetOfThree.size - 1) shouldBe true
    intSSetOfThree.containsAll(asFKSet<Int,Int>(aux1)) shouldBe true
    intSSetOfNone.drop(1).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfOne.drop(1).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    val aux2 = intSSetOfTwo.drop(1).toSet()
    (aux2.size == intSSetOfTwo.size - 1) shouldBe true
    intSSetOfTwo.containsAll(aux2) shouldBe true
  }

  test("drop 2") {
    intSSetOfNone.drop(2).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfOne.drop(2).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfTwo.drop(2).toIMKSet(IntKeyType) shouldBe emptyIMRSet()

    val aux1 = intSSetOfThree.drop(2).toSet()
    (aux1.size == intSSetOfThree.size - 2) shouldBe true
    intSSetOfThree.containsAll(aux1) shouldBe true

    val a2 = FKSet.ofi(*arrayOf<Int>(1,2,3,4))
    val aux2 = a2.drop(2).toSet()
    (aux2.size == a2.size - 2) shouldBe true
    a2.containsAll(aux2) shouldBe true
  }

  test("drop 3") {
    intSSetOfNone.drop(3).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfOne.drop(3).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfTwo.drop(3).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfThree.drop(3).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    val a2 = FKSet.ofi(*arrayOf<Int>(1,2,3,4))
    val aux2 = a2.drop(3).toSet()
    (aux2.size == a2.size - 3) shouldBe true
    a2.containsAll(aux2) shouldBe true
  }

  test("dropWhile") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FKSet.ofi(*arrayOf<Int>(2,1)).dropWhile { it > 1 }
    }
  }

  test("filter") {
    intSSetOfNone.filter {0 == it % 2}.toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfOne.filter {0 == it % 2}.toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfTwo.filter {0 == it % 2}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(2)
    intSSetOfThree.filter {0 == it % 2}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(2)
    FKSet.ofi(*arrayOf<Int>(1,2,3,4)).filter {0 == it % 2}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(2, 4)
  }

  //  ignore
  //  test("filter indexed") {}
  //  test("filterIsInstance") {}

  test("filterNot") {
    intSSetOfNone.filterNot {0 == it % 2}.toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfOne.filterNot {0 == it % 2}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1)
    intSSetOfTwo.filterNot {0 == it % 2}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1)
    intSSetOfThree.filterNot {0 == it % 2}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1,3)
    FKSet.ofi(*arrayOf<Int>(1,2,3,4)).filterNot {0 == it % 2}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1, 3)
  }

  //  ignore
  //  test("filterNotNull") {}

  test("take 0") {
    intSSetOfNone.take(0).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfOne.take(0).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfTwo.take(0).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
  }

  test("take 1") {
    intSSetOfNone.take(1).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfOne.take(1).toIMKSet(IntKeyType) shouldBe intSSetOfOne

    val aux1 = intSSetOfTwo.take(1).toSet()
    aux1.size shouldBe 1
    intSSetOfTwo.containsAll(aux1) shouldBe true

    val aux2 = intSSetOfThree.take(1).toSet()
    aux2.size shouldBe 1
    intSSetOfThree.containsAll(aux2) shouldBe true
  }

  test("take 2") {
    intSSetOfNone.take(2).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfOne.take(2).toIMKSet(IntKeyType) shouldBe intSSetOfOne

    val aux1 = intSSetOfTwo.take(2).toSet()
    aux1.size shouldBe 2
    intSSetOfTwo.containsAll(aux1) shouldBe true

    val aux2 = intSSetOfThree.take(2).toSet()
    aux2.size shouldBe 2
    intSSetOfThree.containsAll(aux2) shouldBe true

    val a3 = FKSet.ofi(*arrayOf<Int>(1,2,3,4))
    val aux3 = a3.take(2)
    aux3.size shouldBe 2
    a3.containsAll(aux3) shouldBe true
  }

  test("take 3") {
    intSSetOfNone.take(3).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfOne.take(3).toIMKSet(IntKeyType) shouldBe intSSetOfOne
    intSSetOfTwo.take(3).toIMKSet(IntKeyType) shouldBe intSSetOfTwo
    intSSetOfThree.take(3).toIMKSet(IntKeyType) shouldBe intSSetOfThree
    val a3 = FKSet.ofi(*arrayOf<Int>(1,2,3,4))
    val aux3 = a3.take(3)
    aux3.size shouldBe 3
    a3.containsAll(aux3) shouldBe true
  }

  test("takeWhile") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FKSet.ofi(*arrayOf<Int>(2,1)).takeWhile { it > 1 }
    }
  }

  test("reversed") {
    intSSetOfNone.reversed().toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intSSetOfOne.reversed().toIMKSet(IntKeyType) shouldBe intSSetOfOne
    intSSetOfTwo.reversed().toIMKSet(IntKeyType) shouldBe intSSetOfTwo.toIMKSet(IntKeyType)
    intSSetOfThree.reversed().toIMKSet(IntKeyType) shouldBe intSSetOfThree.toIMKSet(IntKeyType)
  }

  test("sorted") {
    intSSetOfNone.sorted().toSet() shouldBe emptyIMKSet<Int, Int>()
    intSSetOfOne.sorted().toSet() shouldBe intSSetOfOne
    intSSetOfTwo.sorted().toSet() shouldBe intSSetOfTwo
    intSSetOfThree.sorted().toSet() shouldBe intSSetOfThree
    intSSetOfSix.sorted().toSet() shouldBe intSSetOfThree
  }

  test("sortedDescending") {
    intSSetOfNone.sortedDescending().toSet() shouldBe emptyIMKSet<Int, Int>()
    intSSetOfOne.sortedDescending().toSet() shouldBe intSSetOfOne
    intSSetOfTwo.sortedDescending().toSet() shouldBe  intSSetOfTwo
    intSSetOfThree.sortedDescending().toSet() shouldBe intSSetOfThree
    intSSetOfSix.sortedDescending().toSet() shouldBe intSSetOfThree
  }

  test("sortedBy") {

    fun reverseNumerical(t: Int): Int? = -t

    intSSetOfNone.sortedBy(::reverseNumerical).toSet() shouldBe emptyIMKSet<Int, Int>()
    intSSetOfOne.sortedBy(::reverseNumerical).toSet() shouldBe intSSetOfOne
    intSSetOfTwo.sortedBy(::reverseNumerical).toSet() shouldBe intSSetOfTwo
    intSSetOfThree.sortedBy(::reverseNumerical).toSet() shouldBe intSSetOfThree
    intSSetOfSix.sortedBy(::reverseNumerical).toSet() shouldBe intSSetOfThree
  }

  test("sortedByDescending") {

    fun reverseNumerical(t: Int): Int? = -t

    intSSetOfNone.sortedByDescending(::reverseNumerical).toIMKSet(IntKeyType) shouldBe emptyIMKSet<Int, Int>()
    intSSetOfOne.sortedByDescending(::reverseNumerical).toIMKSet(IntKeyType) shouldBe intSSetOfOne
    intSSetOfTwo.sortedByDescending(::reverseNumerical).toIMKSet(IntKeyType) shouldBe intSSetOfTwo
    intSSetOfThree.sortedByDescending(::reverseNumerical).toIMKSet(IntKeyType) shouldBe intSSetOfThree
    intSSetOfSix.sortedByDescending(::reverseNumerical).toIMKSet(IntKeyType) shouldBe intSSetOfThree
  }

  test("sortedWith") {

    val reverseNumerical: Comparator<Int> = Comparator<Int> { p0, p1 ->
      when {
        p0 == p1 -> 0
        p0 > p1 -> -1
        else -> 1
      }
    }

    intSSetOfNone.sortedWith(reverseNumerical).toIMKSet(IntKeyType) shouldBe emptyIMKSet<Int, Int>()
    intSSetOfOne.sortedWith(reverseNumerical).toIMKSet(IntKeyType) shouldBe intSSetOfOne
    intSSetOfTwo.sortedWith(reverseNumerical).toIMKSet(IntKeyType) shouldBe intSSetOfTwo
    intSSetOfThree.sortedWith(reverseNumerical).toIMKSet(IntKeyType) shouldBe intSSetOfThree
    intSSetOfSix.sortedWith(reverseNumerical).toIMKSet(IntKeyType) shouldBe intSSetOfThree
  }

  test("associate") {

    fun f(t: Int): Pair<Int, Int> = Pair(t, -t)

    intSSetOfNone.associate(::f) shouldBe emptyMap()
    intSSetOfOne.associate(::f) shouldBe mapOf( 1 to -1 )
    intSSetOfTwo.associate(::f) shouldBe mapOf( 1 to -1, 2 to -2 )
    intSSetOfThree.associate(::f) shouldBe mapOf( 1 to -1, 2 to -2, 3 to -3 )
  }

  test("associateBy") {

    fun f(t: Int): Int = -t

    intSSetOfNone.associateBy(::f) shouldBe emptyMap()
    intSSetOfOne.associateBy(::f) shouldBe mapOf( -1 to 1 )
    intSSetOfTwo.associateBy(::f) shouldBe mapOf( -1 to 1, -2 to 2 )
    intSSetOfThree.associateBy(::f) shouldBe mapOf( -1 to 1, -2 to 2, -3 to 3 )
  }

  test("associateBy (k, v)") {

    fun f(t: Int): Int = -t
    fun g(t: Int): Int = 2*t

    intSSetOfNone.associateBy(::f, ::g) shouldBe emptyMap()
    intSSetOfOne.associateBy(::f, ::g) shouldBe mapOf( -1 to 2 )
    intSSetOfTwo.associateBy(::f, ::g) shouldBe mapOf( -1 to 2, -2 to 4 )
    intSSetOfThree.associateBy(::f, ::g) shouldBe mapOf( -1 to 2, -2 to 4, -3 to 6 )
  }

  test("associateWith") {

    fun g(t: Int): Int = 2*t

    intSSetOfNone.associateWith(::g) shouldBe emptyMap()
    intSSetOfOne.associateWith(::g) shouldBe mapOf( 1 to 2 )
    intSSetOfTwo.associateWith(::g) shouldBe mapOf( 1 to 2, 2 to 4 )
    intSSetOfThree.associateWith(::g) shouldBe mapOf( 1 to 2, 2 to 4, 3 to 6 )
  }

  test("flatMap") {
    intSSetOfNone.flatMap{ FKSet.ofi(it) }.toIMKSet(IntKeyType) shouldBe emptyIMKSet<Int, Int>()
    intSSetOfOne.flatMap{ FKSet.ofi(it) }.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1)
    fun arrayBuilderConst(arg: Int) = Array<Int>(arg) { _ -> arg }
    intSSetOfTwo.flatMap {FKSet.ofi(*arrayBuilderConst(it))}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1, 2)
    fun arrayBuilderIncrement(arg: Int) = Array<Int>(arg) { i -> arg + i }
    intSSetOfTwo.flatMap {FKSet.ofi(*arrayBuilderIncrement(it))}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1, 2, 3)
    intSSetOfThree.flatMap {FKSet.ofi(*arrayBuilderIncrement(it))}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1, 2, 3, 4, 5)
    intSSetOfThree.flatMap { i -> FKSet.ofi(i, i) }.toIMKSet(IntKeyType) shouldBe intSSetOfThree
  }

  // ignore
  // test("flatMapIndexed") {}

  test("groupBy") {

    fun f(t: Int): Int = -t

    intSSetOfNone.groupBy(::f) shouldBe emptyMap()
    intSSetOfFiveA.groupBy(::f) shouldBe mapOf( -1 to setOf(1, 1), -2 to setOf(2,2), -3 to setOf(3) )
  }

  test("groupBy (k, v)") {

    fun f(t: Int): Int = -t
    fun g(t: Int): Int = 2*t

    intSSetOfNone.groupBy(::f, ::g) shouldBe emptyMap()
    intSSetOfFiveA.groupBy(::f, ::g) shouldBe mapOf( -1 to setOf(2, 2), -2 to setOf(4,4), -3 to setOf(6) )
  }

  // TODO (maybe)
  // test("grouping") {
  //   fail("not implemented yet")
  // }

  test("map") {
    intSSetOfNone.map { it + 1}.toIMKSet(IntKeyType) shouldBe emptyIMKSet<Int, Int>()
    intSSetOfOne.map { it + 1}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(2)
    intSSetOfTwo.map { it + 1}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(2, 3)
  }

  // ignore
  // test("mapIndexed") {}
  // test("mapIndexedNotNull") {}
  // test("mapNotNull") {}

  test("withIndex") {
    val nwi: Iterable<IndexedValue<Int>> = intSSetOfNone.withIndex()
    nwi.iterator().hasNext() shouldBe false

    val twi = intSSetOfThree.withIndex()
    val twii = twi.iterator()
    val twiv0 = twii.next()
    twiv0.index shouldBe 0
    val aux1 = twiv0.value
    (aux1 in intSSetOfThree) shouldBe true
    val twiv1 = twii.next()
    twiv1.index shouldBe 1
    val aux2 = twiv1.value
    (aux2 in intSSetOfThree) shouldBe true
    (aux1 != aux2) shouldBe true
    val twiv2 = twii.next()
    twiv2.index shouldBe 2
    val aux3 = twiv2.value
    (aux3 in intSSetOfThree) shouldBe true
    (aux3 !in setOf(aux1, aux2)) shouldBe true
    twii.hasNext() shouldBe false
  }

  test("distinct") {
    intSSetOfNone.distinct().toIMKSet(IntKeyType) shouldBe emptyIMKSet<Int, Int>()
    intSSetOfOne.distinct() shouldBe intSSetOfOne.toSet()
    intSSetOfTwo.distinct() shouldBe intSSetOfTwo.toSet()
    intSSetOfFiveA.distinct() shouldBe intSSetOfThree.toSet()
    intSSetOfSix.distinct() shouldBe intSSetOfThree.toSet()
  }

  test("distinctBy") {

    fun identity(oracle: Int) = oracle

    intSSetOfNone.distinctBy(::identity).toIMKSet(IntKeyType) shouldBe emptyIMKSet<Int, Int>()
    intSSetOfOne.distinctBy(::identity) shouldBe intSSetOfOne.toSet()
    intSSetOfTwo.distinctBy(::identity) shouldBe intSSetOfTwo.toSet()
    intSSetOfFiveA.distinctBy(::identity) shouldBe intSSetOfThree.toSet()
    intSSetOfSix.distinctBy(::identity) shouldBe intSSetOfThree.toSet()
  }

   test("intersect") {
     intSSetOfNone.intersect(intSSetOfNone) shouldBe intSSetOfNone

     intSSetOfOne.intersect(intSSetOfNone) shouldBe intSSetOfNone
     intSSetOfTwo.intersect(intSSetOfNone) shouldBe intSSetOfNone
     intSSetOfThree.intersect(intSSetOfNone) shouldBe intSSetOfNone

     intSSetOfNone.intersect(intSSetOfOne) shouldBe intSSetOfNone
     intSSetOfNone.intersect(intSSetOfTwo) shouldBe intSSetOfNone
     intSSetOfNone.intersect(intSSetOfThree) shouldBe intSSetOfNone

     intSSetOfOne.intersect(intSSetOfOne) shouldBe intSSetOfOne
     intSSetOfTwo.intersect(intSSetOfTwo) shouldBe intSSetOfTwo
     intSSetOfThree.intersect(intSSetOfThree) shouldBe intSSetOfThree

     intSSetOfTwo.intersect(intSSetOfOne) shouldBe intSSetOfOne
     intSSetOfOne.intersect(intSSetOfTwo) shouldBe intSSetOfOne

     intSSetOfOne.intersect(intSSetOfThree) shouldBe intSSetOfOne
     intSSetOfOne.intersect(intSSetOf13) shouldBe intSSetOfOne
     intSSetOfOne.intersect(intSSetOf25) shouldBe intSSetOfNone
     intSSetOfOne.intersect(intSSetOf45) shouldBe intSSetOfNone

     intSSetOfTwo.intersect(intSSetOfThree) shouldBe intSSetOfTwo
     intSSetOfTwo.intersect(intSSetOf13) shouldBe intSSetOfOne
     intSSetOfTwo.intersect(intSSetOf25) shouldBe intSSetOf2
     intSSetOfTwo.intersect(intSSetOf45) shouldBe intSSetOfNone

     intSSetOf13.intersect(intSSetOfOne) shouldBe intSSetOfOne
     intSSetOf13.intersect(intSSetOfTwo) shouldBe intSSetOfOne
     intSSetOf13.intersect(intSSetOfThree) shouldBe intSSetOf13
     intSSetOf13.intersect(intSSetOf25) shouldBe intSSetOfNone
     intSSetOf13.intersect(intSSetOf45) shouldBe intSSetOfNone

     intSSetOf25.intersect(intSSetOfOne) shouldBe intSSetOfNone
     intSSetOf25.intersect(intSSetOfTwo) shouldBe intSSetOf2
     intSSetOf25.intersect(intSSetOfThree) shouldBe intSSetOf2
     intSSetOf25.intersect(intSSetOf13) shouldBe intSSetOfNone
     intSSetOf25.intersect(intSSetOf45) shouldBe intSSetOf5

     intSSetOf45.intersect(intSSetOfOne) shouldBe intSSetOfNone
     intSSetOf45.intersect(intSSetOfTwo) shouldBe intSSetOfNone
     intSSetOf45.intersect(intSSetOfThree) shouldBe intSSetOfNone
     intSSetOf45.intersect(intSSetOf13) shouldBe intSSetOfNone
     intSSetOf45.intersect(intSSetOf25) shouldBe intSSetOf5

     intSSetOfThree.intersect(intSSetOfOne) shouldBe intSSetOfOne
     intSSetOfThree.intersect(intSSetOfTwo) shouldBe intSSetOfTwo
     intSSetOfThree.intersect(intSSetOf13) shouldBe intSSetOf13
     intSSetOfThree.intersect(intSSetOf25) shouldBe intSSetOf2
     intSSetOfThree.intersect(intSSetOf45) shouldBe intSSetOfNone
   }

   test("subtract") {
     intSSetOfNone.subtract(intSSetOfNone) shouldBe intSSetOfNone

     intSSetOfOne.subtract(intSSetOfNone) shouldBe intSSetOfOne
     intSSetOfTwo.subtract(intSSetOfNone) shouldBe intSSetOfTwo
     intSSetOfThree.subtract(intSSetOfNone) shouldBe intSSetOfThree

     intSSetOfNone.subtract(intSSetOfOne) shouldBe intSSetOfNone
     intSSetOfNone.subtract(intSSetOfTwo) shouldBe intSSetOfNone
     intSSetOfNone.subtract(intSSetOfThree) shouldBe intSSetOfNone

     intSSetOfOne.subtract(intSSetOfOne) shouldBe intSSetOfNone
     intSSetOfTwo.subtract(intSSetOfTwo) shouldBe intSSetOfNone
     intSSetOfThree.subtract(intSSetOfThree) shouldBe intSSetOfNone

     intSSetOfTwo.subtract(intSSetOfOne) shouldBe intSSetOf2
     intSSetOfOne.subtract(intSSetOfTwo) shouldBe intSSetOfNone

     intSSetOfOne.subtract(intSSetOfThree) shouldBe intSSetOfNone
     intSSetOfOne.subtract(intSSetOf13) shouldBe intSSetOfNone
     intSSetOfOne.subtract(intSSetOf25) shouldBe intSSetOfOne
     intSSetOfOne.subtract(intSSetOf45) shouldBe intSSetOfOne

     intSSetOfTwo.subtract(intSSetOfThree) shouldBe intSSetOfNone
     intSSetOfTwo.subtract(intSSetOf13) shouldBe intSSetOf2
     intSSetOfTwo.subtract(intSSetOf25) shouldBe intSSetOfOne
     intSSetOfTwo.subtract(intSSetOf45) shouldBe intSSetOfTwo

     intSSetOf13.subtract(intSSetOfOne) shouldBe intSSetOf3
     intSSetOf13.subtract(intSSetOfTwo) shouldBe intSSetOf3
     intSSetOf13.subtract(intSSetOfThree) shouldBe intSSetOfNone
     intSSetOf13.subtract(intSSetOf25) shouldBe intSSetOf13
     intSSetOf13.subtract(intSSetOf45) shouldBe intSSetOf13

     intSSetOf25.subtract(intSSetOfOne) shouldBe intSSetOf25
     intSSetOf25.subtract(intSSetOfTwo) shouldBe intSSetOf5
     intSSetOf25.subtract(intSSetOfThree) shouldBe intSSetOf5
     intSSetOf25.subtract(intSSetOf13) shouldBe intSSetOf25
     intSSetOf25.subtract(intSSetOf45) shouldBe intSSetOf2

     intSSetOf45.subtract(intSSetOfOne) shouldBe intSSetOf45
     intSSetOf45.subtract(intSSetOfTwo) shouldBe intSSetOf45
     intSSetOf45.subtract(intSSetOfThree) shouldBe intSSetOf45
     intSSetOf45.subtract(intSSetOf13) shouldBe intSSetOf45
     intSSetOf45.subtract(intSSetOf25) shouldBe intSSetOf4

     intSSetOfThree.subtract(intSSetOfOne) shouldBe intSSetOf23
     intSSetOfThree.subtract(intSSetOfTwo) shouldBe intSSetOf3
     intSSetOfThree.subtract(intSSetOf13) shouldBe intSSetOf2
     intSSetOfThree.subtract(intSSetOf25) shouldBe intSSetOf13
     intSSetOfThree.subtract(intSSetOf45) shouldBe intSSetOfThree
   }

  test("union") {
    intSSetOfNone.union(intSSetOfNone) shouldBe intSSetOfNone

    intSSetOfOne.union(intSSetOfNone) shouldBe intSSetOfOne
    intSSetOfTwo.union(intSSetOfNone) shouldBe intSSetOfTwo
    intSSetOfThree.union(intSSetOfNone) shouldBe intSSetOfThree

    intSSetOfNone.union(intSSetOfOne) shouldBe intSSetOfOne
    intSSetOfNone.union(intSSetOfTwo) shouldBe intSSetOfTwo
    intSSetOfNone.union(intSSetOfThree) shouldBe intSSetOfThree

    intSSetOfOne.union(intSSetOfOne) shouldBe intSSetOfOne
    intSSetOfTwo.union(intSSetOfTwo) shouldBe intSSetOfTwo
    intSSetOfThree.union(intSSetOfThree) shouldBe intSSetOfThree

    intSSetOfTwo.union(intSSetOfOne) shouldBe intSSetOfTwo
    intSSetOfOne.union(intSSetOfTwo) shouldBe intSSetOfTwo

    intSSetOfOne.union(intSSetOfThree) shouldBe intSSetOfThree
    intSSetOfOne.union(intSSetOf13) shouldBe intSSetOf13
    intSSetOfOne.union(intSSetOf25) shouldBe intSSetOf125
    intSSetOfOne.union(intSSetOf45) shouldBe intSSetOf145

    intSSetOfTwo.union(intSSetOfThree) shouldBe intSSetOfThree
    intSSetOfTwo.union(intSSetOf13) shouldBe intSSetOfThree
    intSSetOfTwo.union(intSSetOf25) shouldBe intSSetOf125
    intSSetOfTwo.union(intSSetOf45) shouldBe intSSetOf1245

    intSSetOf13.union(intSSetOfOne) shouldBe intSSetOf13
    intSSetOf13.union(intSSetOfTwo) shouldBe intSSetOfThree
    intSSetOf13.union(intSSetOfThree) shouldBe intSSetOfThree
    intSSetOf13.union(intSSetOf25) shouldBe intSSetOf1235
    intSSetOf13.union(intSSetOf45) shouldBe intSSetOf1345

    intSSetOf25.union(intSSetOfOne) shouldBe intSSetOf125
    intSSetOf25.union(intSSetOfTwo) shouldBe intSSetOf125
    intSSetOf25.union(intSSetOfThree) shouldBe intSSetOf1235
    intSSetOf25.union(intSSetOf13) shouldBe intSSetOf1235
    intSSetOf25.union(intSSetOf45) shouldBe intSSetOf245

    intSSetOf45.union(intSSetOfOne) shouldBe intSSetOf145
    intSSetOf45.union(intSSetOfTwo) shouldBe intSSetOf1245
    intSSetOf45.union(intSSetOfThree) shouldBe intSSetOfFive
    intSSetOf45.union(intSSetOf13) shouldBe intSSetOf1345
    intSSetOf45.union(intSSetOf25) shouldBe intSSetOf245

    intSSetOfThree.union(intSSetOfOne) shouldBe intSSetOfThree
    intSSetOfThree.union(intSSetOfTwo) shouldBe intSSetOfThree
    intSSetOfThree.union(intSSetOf13) shouldBe intSSetOfThree
    intSSetOfThree.union(intSSetOf25) shouldBe intSSetOf1235
    intSSetOfThree.union(intSSetOf45) shouldBe intSSetOfFive
  }

  test("all") {
    intSSetOfNone.all(matchLessThan(0)) shouldBe true // by vacuous implication
    intSSetOfOne.all(matchLessThan(1)) shouldBe false
    intSSetOfOne.all(matchLessThan(2)) shouldBe true
    intSSetOfThree.all(matchLessThan(2)) shouldBe false
    intSSetOfThree.all(matchLessThan(4)) shouldBe true
  }

  test("any") {
    intSSetOfNone.any(matchLessThan(0)) shouldBe false
    intSSetOfOne.any(matchLessThan(1)) shouldBe false
    intSSetOfOne.any(matchLessThan(2)) shouldBe true
    intSSetOfThree.any(matchLessThan(1)) shouldBe false
    intSSetOfThree.any(matchLessThan(2)) shouldBe true
    intSSetOfThree.any(matchLessThan(4)) shouldBe true
  }

  test("(has) any") {
    intSSetOfNone.any() shouldBe false
    intSSetOfOne.any() shouldBe true
    intSSetOfThree.any() shouldBe true
  }

  test("count") {
    intSSetOfNone.count() shouldBe 0
    intSSetOfOne.count() shouldBe 1
    intSSetOfThree.count() shouldBe 3
  }

  test("count matching") {
    intSSetOfNone.count(matchEqual(0)) shouldBe 0
    intSSetOfFiveA.count(matchEqual(3)) shouldBe 1
    intSSetOfSix.count(matchEqual(3)) shouldBe 1
  }

  test("fold") {

    val s = { acc: Int, b: Int -> acc - b }

    intSSetOfNone.fold(1, s) shouldBe 1
    intSSetOfOne.fold(1, s) shouldBe 0
    intSSetOfTwo.fold(1, s) shouldBe -2
    intSSetOfTwoA.fold(1, s) shouldBe -3
    intSSetOfThree.fold(1, s) shouldBe -5
    intSSetOfThreeA.fold(1, s) shouldBe -7
  }

  // ignore
  // test("foldIndexed") {}

  test("(has) none") {
    intSSetOfNone.none() shouldBe true
    intSSetOfOne.none() shouldBe false
    intSSetOfThree.none() shouldBe false
  }

  test("none") {
    intSSetOfNone.none(matchLessThan(0)) shouldBe true
    intSSetOfOne.none(matchLessThan(1)) shouldBe true
    intSSetOfOne.none(matchLessThan(2)) shouldBe false
    intSSetOfThree.none(matchLessThan(1)) shouldBe true
    intSSetOfThree.none(matchLessThan(2)) shouldBe false
    intSSetOfThree.none(matchLessThan(4)) shouldBe false
  }

  test("reduce") {

    // since order is not a property of Set, f MUST be commutative
    val ss = { acc: Int, b: Int -> b + acc }

    shouldThrow<UnsupportedOperationException> {
      intSSetOfNone.reduce(ss)
    }
    intSSetOfNone.reduceOrNull(ss) shouldBe null
    intSSetOfOne.reduce(ss) shouldBe 1
    intSSetOfTwo.reduce(ss) shouldBe 3
    intSSetOfTwoA.reduce(ss) shouldBe 4
    intSSetOfTwoC.reduce(ss) shouldBe 5
    intSSetOfThree.reduce(ss) shouldBe 6
    intSSetOfThreeA.reduce(ss) shouldBe 8
  }

  // ignore
  // test("reduceIndexed") {}
  // test("reduceIndexedOrNull") {}

  test("runningFold") {
    shouldThrow<RuntimeException> {
      val ss = { acc: Int, b: Int -> b + acc }
      @Suppress("DEPRECATION")
      FKSet.ofi(*arrayOf<Int>(2,1)).runningFold(1, ss)
    }
  }

  test("runningFoldIndexed") {
    shouldThrow<RuntimeException> {
      val ss = { index: Int, acc: Int, b: Int -> b + acc + index }
      @Suppress("DEPRECATION")
      FKSet.ofi(*arrayOf<Int>(2,1)).runningFoldIndexed(1, ss)
    }
  }

  test("runningReduce") {
    shouldThrow<RuntimeException> {
      val ss = { acc: Int, b: Int -> b + acc }
      @Suppress("DEPRECATION")
      FKSet.ofi(*arrayOf<Int>(2,1)).runningReduce(ss)
    }
  }

  test("runningReduceIndexed") {
    shouldThrow<RuntimeException> {
      val ss = { index: Int, acc: Int, b: Int -> b + acc + index }
      @Suppress("DEPRECATION")
      FKSet.ofi(*arrayOf<Int>(2,1)).runningReduceIndexed(ss)
    }
  }

  test("partition") {
    (intSSetOfOne.partition(matchLessThan(1)).pmap1 { l -> ofi(l.iterator()) }) shouldBe Pair(emptyIMKSet<Int, Int>(), setOf(1))
    (intSSetOfThree.partition(matchLessThan(2)).pmap1 { l -> ofi(l.iterator()) }) shouldBe Pair(setOf(1), setOf(2, 3))
  }

  test("windowed") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FKSet.ofi(*arrayOf<Int>(2,1)).windowed(2)
    }
  }

  test("zip array") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FKSet.ofi(*arrayOf<Int>(2,1)).zip(emptyArrayOfStr){ a, b -> Pair(a,b)}
    }
  }

  test("zip iterable") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FKSet.ofi(*arrayOf<Int>(2,1)).zip(setOf<String>())
    }
  }

  test("zipWithNext") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FKSet.ofi(*arrayOf<Int>(2,1)).zipWithNext()
    }
  }

  test("zipWithNext transform") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FKSet.ofi(*arrayOf<Int>(2,1)).zipWithNext { a, b -> Pair(a, b) }
    }
  }
})
