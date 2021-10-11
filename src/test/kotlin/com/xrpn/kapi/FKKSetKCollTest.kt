package com.xrpn.kapi

import com.xrpn.bridge.FKSetIterator
import com.xrpn.imapi.IMSet
import com.xrpn.imapi.IntKeyType
import com.xrpn.imapi.SymKeyType
import com.xrpn.imapi.StrKeyType
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

private val intKKOfNone: Collection<Int> = FKSet.ofi(*emptyArrayOfInt)
private val intKKOfOneA: Collection<Int> = FKSet.ofi(0)
private val intKKOfOne: Collection<Int> = FKSet.ofi(1)
private val intKKOfOneB: Collection<Int> = FKSet.ofi(2)
private val intKKOfOneC: Collection<Int> = FKSet.ofi(3)
private val intKKOfOneD: Collection<Int> = FKSet.ofi(4)
private val intKKOfTwoA: Collection<Int> = FKSet.ofi(*arrayOf<Int>(1,3))
private val intKKOfTwo: Collection<Int> = FKSet.ofi(*arrayOf<Int>(1,2))
private val intKKOfTwoB: Collection<Int> = FKSet.ofi(*arrayOf<Int>(0,2))
private val intKKOfTwoC: Collection<Int> = FKSet.ofi(*arrayOf<Int>(1,4))
private val intKKOfThree: Collection<Int> = FKSet.ofi(*arrayOf<Int>(1,2,3))
private val intKKOfThreeA: Collection<Int> = FKSet.ofi(*arrayOf<Int>(1,2,5))
private val intKKOfFive: Collection<Int> = FKSet.ofi(*arrayOf<Int>(1,2,3,4,5))
private val intKKOfFiveA: Collection<Int> = FKSet.ofi(*arrayOf<Int>(1,2,3,2,1))
private val intKKOfSix: Collection<Int> = FKSet.ofi(*arrayOf<Int>(1,2,3,3,2,1))
private val intKKOf2: Collection<Int> = FKSet.ofi(2)
private val intKKOf3: Collection<Int> = FKSet.ofi(3)
private val intKKOf4: Collection<Int> = FKSet.ofi(4)
private val intKKOf5: Collection<Int> = FKSet.ofi(5)
private val intKKOf13: Collection<Int> = FKSet.ofi(1, 3)
private val intKKOf23: Collection<Int> = FKSet.ofi(2, 3)
private val intKKOf25: Collection<Int> = FKSet.ofi(2, 5)
private val intKKOf45: Collection<Int> = FKSet.ofi(4, 5)
private val intKKOf125: Collection<Int> = FKSet.ofi(1, 2, 5)
private val intKKOf145: Collection<Int> = FKSet.ofi(1, 4, 5)
private val intKKOf245: Collection<Int> = FKSet.ofi(2, 4, 5)
private val intKKOf1245: Collection<Int> = FKSet.ofi(1, 2, 4, 5)
private val intKKOf1235: Collection<Int> = FKSet.ofi(1, 2, 3, 5)
private val intKKOf1345: Collection<Int> = FKSet.ofi(1, 3, 4, 5)


class FKKSetKCollTest : FunSpec({

  beforeTest {}

  fun <Z: Comparable<Z>> matchEqual(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle == aut }
  fun <Z: Comparable<Z>> matchLessThan(oracle: Z): (Z) -> Boolean = { aut: Z -> aut < oracle }

  // Any equals

  test("FIKSet equals") {
    (intKKOfNone == FKSet.ofi(*emptyArrayOfInt)) shouldBe true
    (intKKOfNone.equals(emptySet<Int>())) shouldBe true
    (intKKOfNone == emptySet<Int>()) shouldBe true
    (intKKOfNone == emptySet<Int>().toIMKSet(IntKeyType)) shouldBe true
    (intKKOfNone == FKSet.ofi(*arrayOf(1))) shouldBe false
    (intKKOfNone == setOf(1)) shouldBe false
    (intKKOfNone == setOf(1).toIMKSet(IntKeyType)) shouldBe false

    (intKKOfOne == FKSet.ofi(*emptyArrayOfInt)) shouldBe false
    (intKKOfOne == emptySet<Int>()) shouldBe false
    (intKKOfOne == FKSet.ofs(*arrayOf(1))) shouldBe false
    (intKKOfOne == FKSet.ofi(*arrayOf(1))) shouldBe true
    (intKKOfOne == setOf(1)) shouldBe true
    (intKKOfOne == setOf(1).toIMKSet(IntKeyType)) shouldBe true
    (intKKOfOne == setOf(1).toIMKSet(SymKeyType(Int::class))) shouldBe true
    (intKKOfOne == setOf(1).toIMKSet(StrKeyType)) shouldBe false
    (intKKOfOne == FKSet.ofi(*arrayOf(1, 2))) shouldBe false
  }

  test("FIKSet equals miss") {
    (intKKOfOne.equals(FKSet.ofi(*arrayOf(2)))) shouldBe false
    (intKKOfTwo == setOf(1)) shouldBe false
    (intKKOfTwo == setOf(2)) shouldBe false
    (intKKOfTwo == setOf(2, 1)) shouldBe true
    (intKKOfTwo == setOf(1, 2)) shouldBe true
    (intKKOfTwo == intKKOfThree) shouldBe false
    (intKKOfThree == setOf(1, 2, 3)) shouldBe true
    (intKKOfThree == setOf(1, 3, 2)) shouldBe true
    (intKKOfThree == setOf(2, 1, 3)) shouldBe true
    (intKKOfThree == setOf(3, 2, 1)) shouldBe true
  }

  test("Collections equals") {
    (emptySet<Int>() == intKKOfOne) shouldBe false
    (emptySet<Int>() == intKKOfOne.toSet()) shouldBe false
    (setOf(1) == intKKOfOne) shouldBe true
    (setOf(1) == intKKOfOne.toSet()) shouldBe true
    (setOf(1) == intKKOfTwo.toSet()) shouldBe false
    (setOf(1,2) == intKKOfOne.toSet()) shouldBe false

    (emptySet<Int>() == intKKOfNone) shouldBe true
    (emptySet<Int>() == intKKOfNone.toSet()) shouldBe true
    (setOf<Int>(1) == intKKOfNone.toSet()) shouldBe false
  }

  test("Collections equals miss") {
    (setOf(2) == intKKOfOne) shouldBe false
    (setOf(1) == intKKOfTwo) shouldBe false
    (setOf(2) == intKKOfTwo) shouldBe false
    (setOf(2, 1) == intKKOfTwo) shouldBe true
    (setOf(1, 2) == intKKOfTwo) shouldBe true
    (setOf(1, 2) == intKKOfThree) shouldBe false
    (setOf(1, 2, 3) == intKKOfThree) shouldBe true
    (setOf(1, 3, 2) == intKKOfThree) shouldBe true
    (setOf(2, 1, 3) == intKKOfThree) shouldBe true
    (setOf(3, 2, 1) == intKKOfThree) shouldBe true
  }

  // Collection -- methods or fields

  test("size") {
    intKKOfNone.size shouldBe 0
    intKKOfOne.size shouldBe 1
    intKKOfTwo.size shouldBe 2
    intKKOfThree.size shouldBe 3
  }

  test("isEmpty") {
    intKKOfNone.isEmpty() shouldBe true
    intKKOfOne.isEmpty() shouldBe false
    intKKOfTwo.isEmpty() shouldBe false
    intKKOfThree.isEmpty() shouldBe false
  }

  test("contains") {
    intKKOfNone.contains(0) shouldBe false
    intKKOfOne.contains(0) shouldBe false
    intKKOfOne.contains(1) shouldBe true
    intKKOfOne.contains(2) shouldBe false
    intKKOfTwo.contains(0) shouldBe false
    intKKOfTwo.contains(1) shouldBe true
    intKKOfTwo.contains(2) shouldBe true
    intKKOfTwo.contains(3) shouldBe false
  }

  test("containsAll") {
    intKKOfNone.containsAll(intKKOfNone) shouldBe true
    intKKOfNone.containsAll(intKKOfOne) shouldBe false

    intKKOfOne.containsAll(intKKOfNone) shouldBe true
    intKKOfOne.containsAll(intKKOfOne) shouldBe true
    intKKOfOne.containsAll(intKKOfTwo) shouldBe false

    intKKOfTwo.containsAll(intKKOfNone) shouldBe true
    intKKOfTwo.containsAll(intKKOfOne) shouldBe true
    intKKOfTwo.containsAll(intKKOfTwo) shouldBe true
    intKKOfTwo.containsAll(intKKOfThree) shouldBe false

    intKKOfThree.containsAll(intKKOfNone) shouldBe true
    intKKOfThree.containsAll(intKKOfOne) shouldBe true
    intKKOfThree.containsAll(intKKOfOneB) shouldBe true
    intKKOfThree.containsAll(intKKOfOneC) shouldBe true
    intKKOfThree.containsAll(intKKOfTwo) shouldBe true
    intKKOfThree.containsAll(intKKOfTwoA) shouldBe true
    intKKOfThree.containsAll(intKKOfOneA) shouldBe false
    intKKOfThree.containsAll(intKKOfOneD) shouldBe false
    intKKOfThree.containsAll(intKKOfTwoB) shouldBe false
    intKKOfThree.containsAll(intKKOfTwoC) shouldBe false
  }

  // Iterator -- methods

  test("iterator a") {
    val i0 = intKKOfNone.iterator()
    i0.hasNext() shouldBe false
    shouldThrow<NoSuchElementException> {
      i0.next()
    }

    val i1 = intKKOfOne.iterator()
    i1.hasNext() shouldBe true
    i1.next() shouldBe 1
    i1.hasNext() shouldBe false
    shouldThrow<NoSuchElementException> {
      i1.next()
    }

    val i2 = intKKOfTwo.iterator()
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
    val itr = @Suppress("UNCHECKED_CAST") (intKKOfFiveA.iterator() as FKSetIterator<Int, Int>)
    for (i in (1..intKKOfFiveA.size)) {
      val tmp = itr.nullableNext()
      tmp?.let { true } shouldBe true
    }
    (itr.nullableNext()?.let { false } ?: true) shouldBe true
  }

  // typeclass (Collection, Iterator, Iterable)

  test("first") {

    shouldThrow<NoSuchElementException> {
      intKKOfNone.first()
    }
    intKKOfNone.firstOrNull() shouldBe null

    intKKOfOne.firstOrNull() shouldBe 1
    val aux = intKKOfTwo.first()
    (aux == 1 || aux == 2) shouldBe true
    (aux in intKKOfTwo) shouldBe true
  }

  //  ignore
  //  test("firstNotNullOf") {}

  test("find first") {
    shouldThrow<NoSuchElementException> {
      intKKOfNone.first(matchEqual(0))
    }
    intKKOfNone.firstOrNull(matchEqual(0)) shouldBe null

    shouldThrow<NoSuchElementException> {
      intKKOfOne.first(matchEqual(0))
    }
    intKKOfOne.firstOrNull(matchEqual(0)) shouldBe null
    intKKOfOne.firstOrNull(matchEqual(1)) shouldBe 1

    intKKOfTwo.firstOrNull(matchEqual(0)) shouldBe null
    intKKOfTwo.first(matchEqual(1)) shouldBe 1
    intKKOfTwo.first(matchEqual(2)) shouldBe 2
    intKKOfTwo.firstOrNull(matchEqual(3)) shouldBe null
  }

  test("indexOf") {
    intKKOfNone.indexOf(0) shouldBe NOT_FOUND

    intKKOfOne.indexOf(0) shouldBe NOT_FOUND
    intKKOfOne.indexOf(1) shouldBe 0
    intKKOfOne.indexOf(2) shouldBe NOT_FOUND

    intKKOfTwo.indexOf(0) shouldBe NOT_FOUND
    val aux1 = intKKOfTwo.indexOf(1)
    (aux1 == 0 || aux1 == 1) shouldBe true
    val aux2 = intKKOfTwo.indexOf(2)
    ((aux2 == 0 || aux2 == 1) && (aux1 != aux2)) shouldBe true
    intKKOfTwo.indexOf(3) shouldBe NOT_FOUND
  }

  test("indexOfirst") {
    intKKOfNone.indexOfFirst(matchEqual(0)) shouldBe NOT_FOUND

    intKKOfOne.indexOfFirst(matchEqual(0)) shouldBe NOT_FOUND
    intKKOfOne.indexOfFirst(matchEqual(1)) shouldBe 0

    intKKOfSix.indexOfFirst(matchEqual(0)) shouldBe NOT_FOUND
    val aux1 = intKKOfSix.indexOfFirst(matchEqual(1))
    (aux1 in (0..intKKOfSix.size)) shouldBe true
    val aux2 = intKKOfSix.indexOfFirst(matchEqual(2))
    (aux2 in (0..intKKOfSix.size) && aux1 != aux2) shouldBe true
    intKKOfSix.indexOfFirst(matchEqual(30)) shouldBe NOT_FOUND
  }

  test("indexOfLast") {
    intKKOfNone.indexOfLast(matchEqual(0)) shouldBe NOT_FOUND

    intKKOfOne.indexOfLast(matchEqual(0)) shouldBe NOT_FOUND
    intKKOfOne.indexOfLast(matchEqual(1)) shouldBe 0

    intKKOfSix.indexOfLast(matchEqual(0)) shouldBe NOT_FOUND
    val aux1 = intKKOfSix.indexOfLast(matchEqual(1))
    (aux1 in (0..intKKOfSix.size)) shouldBe true
    val aux2 = intKKOfSix.indexOfLast(matchEqual(2))
    (aux2 in (0..intKKOfSix.size) && aux1 != aux2) shouldBe true
    intKKOfSix.indexOfLast(matchEqual(30)) shouldBe NOT_FOUND
  }

  test("last") {
    shouldThrow<NoSuchElementException> {
      intKKOfNone.last()
    }
    intKKOfNone.lastOrNull() shouldBe null
    intKKOfOne.lastOrNull() shouldBe 1
    intKKOfOne.last() shouldBe 1
    val aux1 = intKKOfTwo.last()
    (aux1 in intKKOfTwo) shouldBe true
    val aux2 = intKKOfThree.last()
    (aux2 in intKKOfThree) shouldBe true
  }

  test("last (find)") {
    shouldThrow<NoSuchElementException> {
      intKKOfNone.last(matchEqual(0))
    }
    intKKOfNone.lastOrNull(matchEqual(0)) shouldBe null

    intKKOfOne.lastOrNull(matchEqual(1)) shouldBe 1
    intKKOfOne.last(matchEqual(1)) shouldBe 1
    intKKOfOne.lastOrNull(matchEqual(2)) shouldBe null

    intKKOfTwo.lastOrNull(matchEqual(0)) shouldBe null
    intKKOfTwo.last(matchEqual(1)) shouldBe 1
    intKKOfTwo.last(matchEqual(2)) shouldBe 2
    shouldThrow<NoSuchElementException> {
      intKKOfTwo.last(matchEqual(3))
    }
    intKKOfTwo.lastOrNull(matchEqual(3)) shouldBe null
  }

  test("lastIndexOf") {

    intKKOfNone.lastIndexOf(0) shouldBe NOT_FOUND

    intKKOfOne.lastIndexOf(0) shouldBe NOT_FOUND
    intKKOfOne.lastIndexOf(1) shouldBe 0

    intKKOfSix.lastIndexOf(0) shouldBe NOT_FOUND

    val aux1 = intKKOfSix.lastIndexOf(1)
    (aux1 in (0..intKKOfSix.size)) shouldBe true
    val aux2 = intKKOfSix.lastIndexOf(2)
    (aux2 in (0..intKKOfSix.size) && aux1 != aux2) shouldBe true
    intKKOfSix.lastIndexOf(30) shouldBe NOT_FOUND
  }

  test("findlast") {
    intKKOfNone.findLast(matchEqual(0)) shouldBe null

    intKKOfOne.findLast(matchEqual(0)) shouldBe null
    intKKOfOne.findLast(matchEqual(1)) shouldBe 1

    intKKOfTwo.findLast(matchEqual(0)) shouldBe null
    intKKOfTwo.findLast(matchEqual(1)) shouldBe 1
    intKKOfTwo.findLast(matchEqual(2)) shouldBe 2
    intKKOfTwo.findLast(matchEqual(3)) shouldBe null
  }

  test("single") {
    shouldThrow<NoSuchElementException> {
      intKKOfNone.single()
    }
    intKKOfNone.singleOrNull() shouldBe null

    intKKOfOne.single() shouldBe 1
    intKKOfOne.singleOrNull() shouldBe 1

    shouldThrow<IllegalArgumentException> {
      intKKOfTwo.single()
    }
    intKKOfTwo.singleOrNull() shouldBe null
  }

  test("single (find)") {
    shouldThrow<NoSuchElementException> {
      intKKOfNone.single(matchEqual(0))
    }
    intKKOfNone.singleOrNull(matchEqual(0)) shouldBe null

    intKKOfOne.single(matchEqual(1)) shouldBe 1
    intKKOfOne.singleOrNull(matchEqual(1)) shouldBe 1

    shouldThrow<NoSuchElementException> {
      intKKOfTwo.single(matchEqual(0))
    }
    intKKOfTwo.singleOrNull(matchEqual(0)) shouldBe null
    intKKOfTwo.single(matchEqual(1)) shouldBe 1
    intKKOfTwo.single(matchEqual(2)) shouldBe 2
    shouldThrow<NoSuchElementException> {
      intKKOfTwo.single(matchEqual(3))
    }
    intKKOfTwo.singleOrNull(matchEqual(3)) shouldBe null

    shouldThrow<NoSuchElementException> {
      intKKOfTwo.single(matchEqual(0))
    }
    intKKOfTwo.singleOrNull(matchEqual(0)) shouldBe null
    shouldThrow<NoSuchElementException> {
      intKKOfTwo.single(matchEqual(3))
    }
    intKKOfTwo.singleOrNull(matchEqual(3)) shouldBe null
  }

  test("drop 0") {
    intKKOfNone.drop(0).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfOne.drop(0).toIMKSet(IntKeyType) shouldBe intKKOfOne
    intKKOfTwo.drop(0).toIMKSet(IntKeyType) shouldBe intKKOfTwo
  }

  test("drop 1") {
    val aux0: Set<Int> = intKKOfThree.drop(1).toSet()
    val aux1: IMSet<Int> = aux0.toIMKSet(IntKeyType)!!
    (aux1.fsize() == intKKOfThree.size - 1) shouldBe true
    intKKOfThree.containsAll(asFKSet<Int,Int>(aux1)) shouldBe true
    intKKOfNone.drop(1).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfOne.drop(1).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    val aux2 = intKKOfTwo.drop(1).toSet()
    (aux2.size == intKKOfTwo.size - 1) shouldBe true
    intKKOfTwo.containsAll(aux2) shouldBe true
  }

  test("drop 2") {
    intKKOfNone.drop(2).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfOne.drop(2).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfTwo.drop(2).toIMKSet(IntKeyType) shouldBe emptyIMRSet()

    val aux1 = intKKOfThree.drop(2).toSet()
    (aux1.size == intKKOfThree.size - 2) shouldBe true
    intKKOfThree.containsAll(aux1) shouldBe true

    val a2 = FKSet.ofi(*arrayOf<Int>(1,2,3,4))
    val aux2 = a2.drop(2).toSet()
    (aux2.size == a2.size - 2) shouldBe true
    a2.containsAll(aux2) shouldBe true
  }

  test("drop 3") {
    intKKOfNone.drop(3).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfOne.drop(3).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfTwo.drop(3).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfThree.drop(3).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
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
    intKKOfNone.filter {0 == it % 2}.toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfOne.filter {0 == it % 2}.toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfTwo.filter {0 == it % 2}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(2)
    intKKOfThree.filter {0 == it % 2}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(2)
    FKSet.ofi(*arrayOf<Int>(1,2,3,4)).filter {0 == it % 2}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(2, 4)
  }

  //  ignore
  //  test("filter indexed") {}
  //  test("filterIsInstance") {}

  test("filterNot") {
    intKKOfNone.filterNot {0 == it % 2}.toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfOne.filterNot {0 == it % 2}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1)
    intKKOfTwo.filterNot {0 == it % 2}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1)
    intKKOfThree.filterNot {0 == it % 2}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1,3)
    FKSet.ofi(*arrayOf<Int>(1,2,3,4)).filterNot {0 == it % 2}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1, 3)
  }

  //  ignore
  //  test("filterNotNull") {}

  test("take 0") {
    intKKOfNone.take(0).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfOne.take(0).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfTwo.take(0).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
  }

  test("take 1") {
    intKKOfNone.take(1).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfOne.take(1).toIMKSet(IntKeyType) shouldBe intKKOfOne

    val aux1 = intKKOfTwo.take(1).toSet()
    aux1.size shouldBe 1
    intKKOfTwo.containsAll(aux1) shouldBe true

    val aux2 = intKKOfThree.take(1).toSet()
    aux2.size shouldBe 1
    intKKOfThree.containsAll(aux2) shouldBe true
  }

  test("take 2") {
    intKKOfNone.take(2).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfOne.take(2).toIMKSet(IntKeyType) shouldBe intKKOfOne

    val aux1 = intKKOfTwo.take(2).toSet()
    aux1.size shouldBe 2
    intKKOfTwo.containsAll(aux1) shouldBe true

    val aux2 = intKKOfThree.take(2).toSet()
    aux2.size shouldBe 2
    intKKOfThree.containsAll(aux2) shouldBe true

    val a3 = FKSet.ofi(*arrayOf<Int>(1,2,3,4))
    val aux3 = a3.take(2)
    aux3.size shouldBe 2
    a3.containsAll(aux3) shouldBe true
  }

  test("take 3") {
    intKKOfNone.take(3).toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfOne.take(3).toIMKSet(IntKeyType) shouldBe intKKOfOne
    intKKOfTwo.take(3).toIMKSet(IntKeyType) shouldBe intKKOfTwo
    intKKOfThree.take(3).toIMKSet(IntKeyType) shouldBe intKKOfThree
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
    intKKOfNone.reversed().toIMKSet(IntKeyType) shouldBe emptyIMRSet()
    intKKOfOne.reversed().toIMKSet(IntKeyType) shouldBe intKKOfOne
    intKKOfTwo.reversed().toIMKSet(IntKeyType) shouldBe intKKOfTwo.toIMKSet(IntKeyType)
    intKKOfThree.reversed().toIMKSet(IntKeyType) shouldBe intKKOfThree.toIMKSet(IntKeyType)
  }

  test("sorted") {
    intKKOfNone.sorted().toSet() shouldBe emptyIMKSet<Int, Int>()
    intKKOfOne.sorted().toSet() shouldBe intKKOfOne
    intKKOfTwo.sorted().toSet() shouldBe intKKOfTwo
    intKKOfThree.sorted().toSet() shouldBe intKKOfThree
    intKKOfSix.sorted().toSet() shouldBe intKKOfThree
  }

  test("sortedDescending") {
    intKKOfNone.sortedDescending().toSet() shouldBe emptyIMKSet<Int, Int>()
    intKKOfOne.sortedDescending().toSet() shouldBe intKKOfOne
    intKKOfTwo.sortedDescending().toSet() shouldBe  intKKOfTwo
    intKKOfThree.sortedDescending().toSet() shouldBe intKKOfThree
    intKKOfSix.sortedDescending().toSet() shouldBe intKKOfThree
  }

  test("sortedBy") {

    fun reverseNumerical(t: Int): Int? = -t

    intKKOfNone.sortedBy(::reverseNumerical).toSet() shouldBe emptyIMKSet<Int, Int>()
    intKKOfOne.sortedBy(::reverseNumerical).toSet() shouldBe intKKOfOne
    intKKOfTwo.sortedBy(::reverseNumerical).toSet() shouldBe intKKOfTwo
    intKKOfThree.sortedBy(::reverseNumerical).toSet() shouldBe intKKOfThree
    intKKOfSix.sortedBy(::reverseNumerical).toSet() shouldBe intKKOfThree
  }

  test("sortedByDescending") {

    fun reverseNumerical(t: Int): Int? = -t

    intKKOfNone.sortedByDescending(::reverseNumerical).toIMKSet(IntKeyType) shouldBe emptyIMKSet<Int, Int>()
    intKKOfOne.sortedByDescending(::reverseNumerical).toIMKSet(IntKeyType) shouldBe intKKOfOne
    intKKOfTwo.sortedByDescending(::reverseNumerical).toIMKSet(IntKeyType) shouldBe intKKOfTwo
    intKKOfThree.sortedByDescending(::reverseNumerical).toIMKSet(IntKeyType) shouldBe intKKOfThree
    intKKOfSix.sortedByDescending(::reverseNumerical).toIMKSet(IntKeyType) shouldBe intKKOfThree
  }

  test("sortedWith") {

    val reverseNumerical: Comparator<Int> = Comparator<Int> { p0, p1 ->
      when {
        p0 == p1 -> 0
        p0 > p1 -> -1
        else -> 1
      }
    }

    intKKOfNone.sortedWith(reverseNumerical).toIMKSet(IntKeyType) shouldBe emptyIMKSet<Int, Int>()
    intKKOfOne.sortedWith(reverseNumerical).toIMKSet(IntKeyType) shouldBe intKKOfOne
    intKKOfTwo.sortedWith(reverseNumerical).toIMKSet(IntKeyType) shouldBe intKKOfTwo
    intKKOfThree.sortedWith(reverseNumerical).toIMKSet(IntKeyType) shouldBe intKKOfThree
    intKKOfSix.sortedWith(reverseNumerical).toIMKSet(IntKeyType) shouldBe intKKOfThree
  }

  test("associate") {

    fun f(t: Int): Pair<Int, Int> = Pair(t, -t)

    intKKOfNone.associate(::f) shouldBe emptyMap()
    intKKOfOne.associate(::f) shouldBe mapOf( 1 to -1 )
    intKKOfTwo.associate(::f) shouldBe mapOf( 1 to -1, 2 to -2 )
    intKKOfThree.associate(::f) shouldBe mapOf( 1 to -1, 2 to -2, 3 to -3 )
  }

  test("associateBy") {

    fun f(t: Int): Int = -t

    intKKOfNone.associateBy(::f) shouldBe emptyMap()
    intKKOfOne.associateBy(::f) shouldBe mapOf( -1 to 1 )
    intKKOfTwo.associateBy(::f) shouldBe mapOf( -1 to 1, -2 to 2 )
    intKKOfThree.associateBy(::f) shouldBe mapOf( -1 to 1, -2 to 2, -3 to 3 )
  }

  test("associateBy (k, v)") {

    fun f(t: Int): Int = -t
    fun g(t: Int): Int = 2*t

    intKKOfNone.associateBy(::f, ::g) shouldBe emptyMap()
    intKKOfOne.associateBy(::f, ::g) shouldBe mapOf( -1 to 2 )
    intKKOfTwo.associateBy(::f, ::g) shouldBe mapOf( -1 to 2, -2 to 4 )
    intKKOfThree.associateBy(::f, ::g) shouldBe mapOf( -1 to 2, -2 to 4, -3 to 6 )
  }

  test("associateWith") {

    fun g(t: Int): Int = 2*t

    intKKOfNone.associateWith(::g) shouldBe emptyMap()
    intKKOfOne.associateWith(::g) shouldBe mapOf( 1 to 2 )
    intKKOfTwo.associateWith(::g) shouldBe mapOf( 1 to 2, 2 to 4 )
    intKKOfThree.associateWith(::g) shouldBe mapOf( 1 to 2, 2 to 4, 3 to 6 )
  }

  test("flatMap") {
    intKKOfNone.flatMap{ FKSet.ofi(it) }.toIMKSet(IntKeyType) shouldBe emptyIMKSet<Int, Int>()
    intKKOfOne.flatMap{ FKSet.ofi(it) }.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1)
    fun arrayBuilderConst(arg: Int) = Array<Int>(arg) { _ -> arg }
    intKKOfTwo.flatMap {FKSet.ofi(*arrayBuilderConst(it))}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1, 2)
    fun arrayBuilderIncrement(arg: Int) = Array<Int>(arg) { i -> arg + i }
    intKKOfTwo.flatMap {FKSet.ofi(*arrayBuilderIncrement(it))}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1, 2, 3)
    intKKOfThree.flatMap {FKSet.ofi(*arrayBuilderIncrement(it))}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(1, 2, 3, 4, 5)
    intKKOfThree.flatMap { i -> FKSet.ofi(i, i) }.toIMKSet(IntKeyType) shouldBe intKKOfThree
  }

  // ignore
  // test("flatMapIndexed") {}

  test("groupBy") {

    fun f(t: Int): Int = -t

    intKKOfNone.groupBy(::f) shouldBe emptyMap()
    intKKOfFiveA.groupBy(::f) shouldBe mapOf( -1 to setOf(1, 1), -2 to setOf(2,2), -3 to setOf(3) )
  }

  test("groupBy (k, v)") {

    fun f(t: Int): Int = -t
    fun g(t: Int): Int = 2*t

    intKKOfNone.groupBy(::f, ::g) shouldBe emptyMap()
    intKKOfFiveA.groupBy(::f, ::g) shouldBe mapOf( -1 to setOf(2, 2), -2 to setOf(4,4), -3 to setOf(6) )
  }

  // TODO (maybe)
  // test("grouping") {
  //   fail("not implemented yet")
  // }

  test("map") {
    intKKOfNone.map { it + 1}.toIMKSet(IntKeyType) shouldBe emptyIMKSet<Int, Int>()
    intKKOfOne.map { it + 1}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(2)
    intKKOfTwo.map { it + 1}.toIMKSet(IntKeyType) shouldBe FKSet.ofi(2, 3)
  }

  // ignore
  // test("mapIndexed") {}
  // test("mapIndexedNotNull") {}
  // test("mapNotNull") {}

  test("withIndex") {
    val nwi: Iterable<IndexedValue<Int>> = intKKOfNone.withIndex()
    nwi.iterator().hasNext() shouldBe false

    val twi = intKKOfThree.withIndex()
    val twii = twi.iterator()
    val twiv0 = twii.next()
    twiv0.index shouldBe 0
    val aux1 = twiv0.value
    (aux1 in intKKOfThree) shouldBe true
    val twiv1 = twii.next()
    twiv1.index shouldBe 1
    val aux2 = twiv1.value
    (aux2 in intKKOfThree) shouldBe true
    (aux1 != aux2) shouldBe true
    val twiv2 = twii.next()
    twiv2.index shouldBe 2
    val aux3 = twiv2.value
    (aux3 in intKKOfThree) shouldBe true
    (aux3 !in setOf(aux1, aux2)) shouldBe true
    twii.hasNext() shouldBe false
  }

  test("distinct") {
    intKKOfNone.distinct().toIMKSet(IntKeyType) shouldBe emptyIMKSet<Int, Int>()
    intKKOfOne.distinct() shouldBe intKKOfOne.toSet()
    intKKOfTwo.distinct() shouldBe intKKOfTwo.toSet()
    intKKOfFiveA.distinct() shouldBe intKKOfThree.toSet()
    intKKOfSix.distinct() shouldBe intKKOfThree.toSet()
  }

  test("distinctBy") {

    fun identity(oracle: Int) = oracle

    intKKOfNone.distinctBy(::identity).toIMKSet(IntKeyType) shouldBe emptyIMKSet<Int, Int>()
    intKKOfOne.distinctBy(::identity) shouldBe intKKOfOne.toSet()
    intKKOfTwo.distinctBy(::identity) shouldBe intKKOfTwo.toSet()
    intKKOfFiveA.distinctBy(::identity) shouldBe intKKOfThree.toSet()
    intKKOfSix.distinctBy(::identity) shouldBe intKKOfThree.toSet()
  }

   test("intersect") {
     intKKOfNone.intersect(intKKOfNone) shouldBe intKKOfNone

     intKKOfOne.intersect(intKKOfNone) shouldBe intKKOfNone
     intKKOfTwo.intersect(intKKOfNone) shouldBe intKKOfNone
     intKKOfThree.intersect(intKKOfNone) shouldBe intKKOfNone

     intKKOfNone.intersect(intKKOfOne) shouldBe intKKOfNone
     intKKOfNone.intersect(intKKOfTwo) shouldBe intKKOfNone
     intKKOfNone.intersect(intKKOfThree) shouldBe intKKOfNone

     intKKOfOne.intersect(intKKOfOne) shouldBe intKKOfOne
     intKKOfTwo.intersect(intKKOfTwo) shouldBe intKKOfTwo
     intKKOfThree.intersect(intKKOfThree) shouldBe intKKOfThree

     intKKOfTwo.intersect(intKKOfOne) shouldBe intKKOfOne
     intKKOfOne.intersect(intKKOfTwo) shouldBe intKKOfOne

     intKKOfOne.intersect(intKKOfThree) shouldBe intKKOfOne
     intKKOfOne.intersect(intKKOf13) shouldBe intKKOfOne
     intKKOfOne.intersect(intKKOf25) shouldBe intKKOfNone
     intKKOfOne.intersect(intKKOf45) shouldBe intKKOfNone

     intKKOfTwo.intersect(intKKOfThree) shouldBe intKKOfTwo
     intKKOfTwo.intersect(intKKOf13) shouldBe intKKOfOne
     intKKOfTwo.intersect(intKKOf25) shouldBe intKKOf2
     intKKOfTwo.intersect(intKKOf45) shouldBe intKKOfNone

     intKKOf13.intersect(intKKOfOne) shouldBe intKKOfOne
     intKKOf13.intersect(intKKOfTwo) shouldBe intKKOfOne
     intKKOf13.intersect(intKKOfThree) shouldBe intKKOf13
     intKKOf13.intersect(intKKOf25) shouldBe intKKOfNone
     intKKOf13.intersect(intKKOf45) shouldBe intKKOfNone

     intKKOf25.intersect(intKKOfOne) shouldBe intKKOfNone
     intKKOf25.intersect(intKKOfTwo) shouldBe intKKOf2
     intKKOf25.intersect(intKKOfThree) shouldBe intKKOf2
     intKKOf25.intersect(intKKOf13) shouldBe intKKOfNone
     intKKOf25.intersect(intKKOf45) shouldBe intKKOf5

     intKKOf45.intersect(intKKOfOne) shouldBe intKKOfNone
     intKKOf45.intersect(intKKOfTwo) shouldBe intKKOfNone
     intKKOf45.intersect(intKKOfThree) shouldBe intKKOfNone
     intKKOf45.intersect(intKKOf13) shouldBe intKKOfNone
     intKKOf45.intersect(intKKOf25) shouldBe intKKOf5

     intKKOfThree.intersect(intKKOfOne) shouldBe intKKOfOne
     intKKOfThree.intersect(intKKOfTwo) shouldBe intKKOfTwo
     intKKOfThree.intersect(intKKOf13) shouldBe intKKOf13
     intKKOfThree.intersect(intKKOf25) shouldBe intKKOf2
     intKKOfThree.intersect(intKKOf45) shouldBe intKKOfNone
   }

   test("subtract") {
     intKKOfNone.subtract(intKKOfNone) shouldBe intKKOfNone

     intKKOfOne.subtract(intKKOfNone) shouldBe intKKOfOne
     intKKOfTwo.subtract(intKKOfNone) shouldBe intKKOfTwo
     intKKOfThree.subtract(intKKOfNone) shouldBe intKKOfThree

     intKKOfNone.subtract(intKKOfOne) shouldBe intKKOfNone
     intKKOfNone.subtract(intKKOfTwo) shouldBe intKKOfNone
     intKKOfNone.subtract(intKKOfThree) shouldBe intKKOfNone

     intKKOfOne.subtract(intKKOfOne) shouldBe intKKOfNone
     intKKOfTwo.subtract(intKKOfTwo) shouldBe intKKOfNone
     intKKOfThree.subtract(intKKOfThree) shouldBe intKKOfNone

     intKKOfTwo.subtract(intKKOfOne) shouldBe intKKOf2
     intKKOfOne.subtract(intKKOfTwo) shouldBe intKKOfNone

     intKKOfOne.subtract(intKKOfThree) shouldBe intKKOfNone
     intKKOfOne.subtract(intKKOf13) shouldBe intKKOfNone
     intKKOfOne.subtract(intKKOf25) shouldBe intKKOfOne
     intKKOfOne.subtract(intKKOf45) shouldBe intKKOfOne

     intKKOfTwo.subtract(intKKOfThree) shouldBe intKKOfNone
     intKKOfTwo.subtract(intKKOf13) shouldBe intKKOf2
     intKKOfTwo.subtract(intKKOf25) shouldBe intKKOfOne
     intKKOfTwo.subtract(intKKOf45) shouldBe intKKOfTwo

     intKKOf13.subtract(intKKOfOne) shouldBe intKKOf3
     intKKOf13.subtract(intKKOfTwo) shouldBe intKKOf3
     intKKOf13.subtract(intKKOfThree) shouldBe intKKOfNone
     intKKOf13.subtract(intKKOf25) shouldBe intKKOf13
     intKKOf13.subtract(intKKOf45) shouldBe intKKOf13

     intKKOf25.subtract(intKKOfOne) shouldBe intKKOf25
     intKKOf25.subtract(intKKOfTwo) shouldBe intKKOf5
     intKKOf25.subtract(intKKOfThree) shouldBe intKKOf5
     intKKOf25.subtract(intKKOf13) shouldBe intKKOf25
     intKKOf25.subtract(intKKOf45) shouldBe intKKOf2

     intKKOf45.subtract(intKKOfOne) shouldBe intKKOf45
     intKKOf45.subtract(intKKOfTwo) shouldBe intKKOf45
     intKKOf45.subtract(intKKOfThree) shouldBe intKKOf45
     intKKOf45.subtract(intKKOf13) shouldBe intKKOf45
     intKKOf45.subtract(intKKOf25) shouldBe intKKOf4

     intKKOfThree.subtract(intKKOfOne) shouldBe intKKOf23
     intKKOfThree.subtract(intKKOfTwo) shouldBe intKKOf3
     intKKOfThree.subtract(intKKOf13) shouldBe intKKOf2
     intKKOfThree.subtract(intKKOf25) shouldBe intKKOf13
     intKKOfThree.subtract(intKKOf45) shouldBe intKKOfThree
   }

  test("union") {
    intKKOfNone.union(intKKOfNone) shouldBe intKKOfNone

    intKKOfOne.union(intKKOfNone) shouldBe intKKOfOne
    intKKOfTwo.union(intKKOfNone) shouldBe intKKOfTwo
    intKKOfThree.union(intKKOfNone) shouldBe intKKOfThree

    intKKOfNone.union(intKKOfOne) shouldBe intKKOfOne
    intKKOfNone.union(intKKOfTwo) shouldBe intKKOfTwo
    intKKOfNone.union(intKKOfThree) shouldBe intKKOfThree

    intKKOfOne.union(intKKOfOne) shouldBe intKKOfOne
    intKKOfTwo.union(intKKOfTwo) shouldBe intKKOfTwo
    intKKOfThree.union(intKKOfThree) shouldBe intKKOfThree

    intKKOfTwo.union(intKKOfOne) shouldBe intKKOfTwo
    intKKOfOne.union(intKKOfTwo) shouldBe intKKOfTwo

    intKKOfOne.union(intKKOfThree) shouldBe intKKOfThree
    intKKOfOne.union(intKKOf13) shouldBe intKKOf13
    intKKOfOne.union(intKKOf25) shouldBe intKKOf125
    intKKOfOne.union(intKKOf45) shouldBe intKKOf145

    intKKOfTwo.union(intKKOfThree) shouldBe intKKOfThree
    intKKOfTwo.union(intKKOf13) shouldBe intKKOfThree
    intKKOfTwo.union(intKKOf25) shouldBe intKKOf125
    intKKOfTwo.union(intKKOf45) shouldBe intKKOf1245

    intKKOf13.union(intKKOfOne) shouldBe intKKOf13
    intKKOf13.union(intKKOfTwo) shouldBe intKKOfThree
    intKKOf13.union(intKKOfThree) shouldBe intKKOfThree
    intKKOf13.union(intKKOf25) shouldBe intKKOf1235
    intKKOf13.union(intKKOf45) shouldBe intKKOf1345

    intKKOf25.union(intKKOfOne) shouldBe intKKOf125
    intKKOf25.union(intKKOfTwo) shouldBe intKKOf125
    intKKOf25.union(intKKOfThree) shouldBe intKKOf1235
    intKKOf25.union(intKKOf13) shouldBe intKKOf1235
    intKKOf25.union(intKKOf45) shouldBe intKKOf245

    intKKOf45.union(intKKOfOne) shouldBe intKKOf145
    intKKOf45.union(intKKOfTwo) shouldBe intKKOf1245
    intKKOf45.union(intKKOfThree) shouldBe intKKOfFive
    intKKOf45.union(intKKOf13) shouldBe intKKOf1345
    intKKOf45.union(intKKOf25) shouldBe intKKOf245

    intKKOfThree.union(intKKOfOne) shouldBe intKKOfThree
    intKKOfThree.union(intKKOfTwo) shouldBe intKKOfThree
    intKKOfThree.union(intKKOf13) shouldBe intKKOfThree
    intKKOfThree.union(intKKOf25) shouldBe intKKOf1235
    intKKOfThree.union(intKKOf45) shouldBe intKKOfFive
  }

  test("all") {
    intKKOfNone.all(matchLessThan(0)) shouldBe true // by vacuous implication
    intKKOfOne.all(matchLessThan(1)) shouldBe false
    intKKOfOne.all(matchLessThan(2)) shouldBe true
    intKKOfThree.all(matchLessThan(2)) shouldBe false
    intKKOfThree.all(matchLessThan(4)) shouldBe true
  }

  test("any") {
    intKKOfNone.any(matchLessThan(0)) shouldBe false
    intKKOfOne.any(matchLessThan(1)) shouldBe false
    intKKOfOne.any(matchLessThan(2)) shouldBe true
    intKKOfThree.any(matchLessThan(1)) shouldBe false
    intKKOfThree.any(matchLessThan(2)) shouldBe true
    intKKOfThree.any(matchLessThan(4)) shouldBe true
  }

  test("(has) any") {
    intKKOfNone.any() shouldBe false
    intKKOfOne.any() shouldBe true
    intKKOfThree.any() shouldBe true
  }

  test("count") {
    intKKOfNone.count() shouldBe 0
    intKKOfOne.count() shouldBe 1
    intKKOfThree.count() shouldBe 3
  }

  test("count matching") {
    intKKOfNone.count(matchEqual(0)) shouldBe 0
    intKKOfFiveA.count(matchEqual(3)) shouldBe 1
    intKKOfSix.count(matchEqual(3)) shouldBe 1
  }

  test("fold") {

    val s = { acc: Int, b: Int -> acc - b }

    intKKOfNone.fold(1, s) shouldBe 1
    intKKOfOne.fold(1, s) shouldBe 0
    intKKOfTwo.fold(1, s) shouldBe -2
    intKKOfTwoA.fold(1, s) shouldBe -3
    intKKOfThree.fold(1, s) shouldBe -5
    intKKOfThreeA.fold(1, s) shouldBe -7
  }

  // ignore
  // test("foldIndexed") {}

  test("(has) none") {
    intKKOfNone.none() shouldBe true
    intKKOfOne.none() shouldBe false
    intKKOfThree.none() shouldBe false
  }

  test("none") {
    intKKOfNone.none(matchLessThan(0)) shouldBe true
    intKKOfOne.none(matchLessThan(1)) shouldBe true
    intKKOfOne.none(matchLessThan(2)) shouldBe false
    intKKOfThree.none(matchLessThan(1)) shouldBe true
    intKKOfThree.none(matchLessThan(2)) shouldBe false
    intKKOfThree.none(matchLessThan(4)) shouldBe false
  }

  test("reduce") {

    // since order is not a property of Set, f MUST be commutative
    val ss = { acc: Int, b: Int -> b + acc }

    shouldThrow<UnsupportedOperationException> {
      intKKOfNone.reduce(ss)
    }
    intKKOfNone.reduceOrNull(ss) shouldBe null
    intKKOfOne.reduce(ss) shouldBe 1
    intKKOfTwo.reduce(ss) shouldBe 3
    intKKOfTwoA.reduce(ss) shouldBe 4
    intKKOfTwoC.reduce(ss) shouldBe 5
    intKKOfThree.reduce(ss) shouldBe 6
    intKKOfThreeA.reduce(ss) shouldBe 8
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
    (intKKOfOne.partition(matchLessThan(1)).pmap1 { l -> ofi(l.iterator()) }) shouldBe Pair(emptyIMKSet<Int, Int>(), setOf(1))
    (intKKOfThree.partition(matchLessThan(2)).pmap1 { l -> ofi(l.iterator()) }) shouldBe Pair(setOf(1), setOf(2, 3))
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
