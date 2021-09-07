package com.xrpn.kapi

import com.xrpn.bridge.FTreeIterator
import com.xrpn.immutable.TKVEntry
import com.xrpn.immutable.FRBTree
import com.xrpn.immutable.FIKSet
import com.xrpn.immutable.FList
import com.xrpn.immutable.FLNil
import com.xrpn.immutable.FList.Companion.toIMList
import com.xrpn.immutable.FRBTree.Companion.NOT_FOUND
import com.xrpn.immutable.TKVEntry.Companion.toIAEntries
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intFrbtOfNone: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(*arrayOf<Int>())
private val intFrbtOfOneA: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(*arrayOf<Int>(0))
private val intFrbtOfOne: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(*arrayOf<Int>(1))
private val intFrbtOfOneB: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(*arrayOf<Int>(2))
private val intFrbtOfOneC: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(*arrayOf<Int>(3))
private val intFrbtOfOneD: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(*arrayOf<Int>(4))
private val intFrbtOfTwoA: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(*arrayOf<Int>(1,3))
private val intFrbtOfTwo: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(*arrayOf<Int>(1,2))
private val intFrbtOfTwoB: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(*arrayOf<Int>(0,2))
private val intFrbtOfTwoC: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(*arrayOf<Int>(1,4))
private val intFrbtOfThree: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(*arrayOf<Int>(1,2,3))
private val intFrbtOfThreeA: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(*arrayOf<Int>(1,2,5))
private val intFrbtOfFive: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(*arrayOf<Int>(1,2,3,4,5))
private val intFrbtOfFiveA: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(*arrayOf<Int>(1,2,3,2,1))
private val intFrbtOfSix: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(*arrayOf<Int>(1,2,3,3,2,1))
private val intFrbtOf2: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(2)
private val intFrbtOf3: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(3)
private val intFrbtOf4: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(4)
private val intFrbtOf5: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(5)
private val intFrbtOf13: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(1, 3)
private val intFrbtOf23: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(2, 3)
private val intFrbtOf25: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(2, 5)
private val intFrbtOf45: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(4, 5)
private val intFrbtOf125: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(1, 2, 5)
private val intFrbtOf145: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(1, 4, 5)
private val intFrbtOf245: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(2, 4, 5)
private val intFrbtOf1245: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(1, 2, 4, 5)
private val intFrbtOf1235: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(1, 2, 3, 5)
private val intFrbtOf1345: Collection<TKVEntry<Int, Int>> = FRBTree.ofvi(1, 3, 4, 5)


class FRBTreeKCollTest : FunSpec({

  beforeTest {}

  fun <Z: Comparable<Z>> matchEqual(oracle: Int): (TKVEntry<Int, Z>) -> Boolean = { aut: TKVEntry<Int, Z> -> oracle.toIAEntry() == aut }
  fun matchLessThan(oracle: Int): ((TKVEntry<Int, Int>)) -> Boolean = { aut: TKVEntry<Int, Int> -> aut.getv() < oracle }
  fun reverseNumerical(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = (-t.getv()).toIAEntry()

  // Any equals

  test("FTree equals") {
    (intFrbtOfNone == FRBTree.ofvi(*arrayOf<Int>())) shouldBe true
    (intFrbtOfNone == emptySet<Int>()) shouldBe false
    (intFrbtOfNone == FIKSet.emptyIMSet<Int>()) shouldBe false
    (intFrbtOfNone == FRBTree.ofvi(*arrayOf(1))) shouldBe false
    (intFrbtOfNone == setOf(1)) shouldBe false
    (intFrbtOfNone == FIKSet.of(1)) shouldBe false

    (intFrbtOfOne == FRBTree.ofvi(*arrayOf<Int>())) shouldBe false
    (intFrbtOfOne == emptySet<Int>()) shouldBe false
    (intFrbtOfOne == FIKSet.emptyIMSet<Int>()) shouldBe false
    (intFrbtOfOne == FRBTree.ofvi(*arrayOf(1))) shouldBe true
    (intFrbtOfOne == setOf(1.toIAEntry())) shouldBe false
    (intFrbtOfOne == FIKSet.of(1)) shouldBe false
    (intFrbtOfOne == FRBTree.ofvi(*arrayOf(1, 2))) shouldBe false
  }

  test("FTree equals miss") {
    (intFrbtOfOne == (FRBTree.ofvi(*arrayOf(2)))) shouldBe false
    (intFrbtOfTwo == setOf(1.toIAEntry())) shouldBe false
    (intFrbtOfTwo == setOf(2.toIAEntry())) shouldBe false
    (intFrbtOfTwo == setOf(2.toIAEntry(), 1.toIAEntry())) shouldBe false
    (intFrbtOfTwo == setOf(1.toIAEntry(), 2.toIAEntry())) shouldBe false
    intFrbtOfTwo.equals(FIKSet.of(1)) shouldBe false
    (intFrbtOfTwo == FIKSet.of(2)) shouldBe false
    (intFrbtOfTwo == FIKSet.of(2, 1)) shouldBe false
    (intFrbtOfTwo == FIKSet.of(1, 2)) shouldBe false
    (intFrbtOfTwo == intFrbtOfThree) shouldBe false
    (intFrbtOfThree == setOf(1.toIAEntry(), 2.toIAEntry(), 3.toIAEntry())) shouldBe false
    (intFrbtOfThree == setOf(1.toIAEntry(), 3.toIAEntry(), 2.toIAEntry())) shouldBe false
    (intFrbtOfThree == setOf(2.toIAEntry(), 1.toIAEntry(), 3.toIAEntry())) shouldBe false
    (intFrbtOfThree == setOf(3.toIAEntry(), 2.toIAEntry(), 1.toIAEntry())) shouldBe false
    (intFrbtOfThree == FIKSet.of(1, 2, 3)) shouldBe false
    (intFrbtOfThree == FIKSet.of(1, 3, 2)) shouldBe false
    (intFrbtOfThree == FIKSet.of(2, 1, 3)) shouldBe false
    (intFrbtOfThree == FIKSet.of(3, 2, 1)) shouldBe false
  }

  test("Collections equals") {
    (emptySet<Int>() == intFrbtOfOne) shouldBe false
    (emptySet<Int>() == intFrbtOfOne.toSet()) shouldBe false
    (setOf(1.toIAEntry()) == intFrbtOfOne) shouldBe false
    (setOf(1.toIAEntry()) == intFrbtOfOne.toSet()) shouldBe true
    (listOf(1.toIAEntry()) == intFrbtOfOne) shouldBe false
    (listOf(1.toIAEntry()) == intFrbtOfOne.toList()) shouldBe true
    (FIKSet.of(1) == intFrbtOfOne) shouldBe false
    (FIKSet.of(1.toIAEntry()) == intFrbtOfOne.toSet()) shouldBe true
    (setOf(1.toIAEntry()) == intFrbtOfTwo.toSet()) shouldBe false
    (setOf(1.toIAEntry()) == intFrbtOfTwo.toList()) shouldBe false
    (setOf(1.toIAEntry(),2.toIAEntry()) == intFrbtOfOne.toSet()) shouldBe false
    (setOf(1.toIAEntry(),2.toIAEntry()) == intFrbtOfTwo.toSet()) shouldBe true

    (emptySet<Int>() == intFrbtOfNone) shouldBe false
    (emptySet<Int>() == intFrbtOfNone.toSet()) shouldBe true
    (setOf(1.toIAEntry()) == intFrbtOfNone.toSet()) shouldBe false
  }

  test("Collections equals miss") {
    (setOf(2.toIAEntry()) == intFrbtOfOne) shouldBe false
    (setOf(1.toIAEntry()) == intFrbtOfTwo) shouldBe false
    (setOf(2.toIAEntry()) == intFrbtOfTwo) shouldBe false
    (setOf(2.toIAEntry(), 1.toIAEntry()) == intFrbtOfTwo) shouldBe false
    (setOf(1.toIAEntry(), 2.toIAEntry()) == intFrbtOfTwo) shouldBe false
    (setOf(1.toIAEntry(), 2.toIAEntry()) == intFrbtOfThree) shouldBe false
    (setOf(1.toIAEntry(), 2.toIAEntry(), 3.toIAEntry()) == intFrbtOfThree) shouldBe false
    (setOf(1.toIAEntry(), 3.toIAEntry(), 2.toIAEntry()) == intFrbtOfThree) shouldBe false
    (setOf(2.toIAEntry(), 1.toIAEntry(), 3.toIAEntry()) == intFrbtOfThree) shouldBe false
    (setOf(3.toIAEntry(), 2.toIAEntry(), 1.toIAEntry()) == intFrbtOfThree) shouldBe false
  }

  // Collection -- methods or fields

  test("size") {
    intFrbtOfNone.size shouldBe 0
    intFrbtOfOne.size shouldBe 1
    intFrbtOfTwo.size shouldBe 2
    intFrbtOfThree.size shouldBe 3
  }

  test("isEmpty") {
    intFrbtOfNone.isEmpty() shouldBe true
    intFrbtOfOne.isEmpty() shouldBe false
    intFrbtOfTwo.isEmpty() shouldBe false
    intFrbtOfThree.isEmpty() shouldBe false
  }

  test("contains") {
    intFrbtOfNone.contains(0.toIAEntry()) shouldBe false
    intFrbtOfOne.contains(0.toIAEntry()) shouldBe false
    intFrbtOfOne.contains(1.toIAEntry()) shouldBe true
    intFrbtOfOne.contains(2.toIAEntry()) shouldBe false
    intFrbtOfTwo.contains(0.toIAEntry()) shouldBe false
    intFrbtOfTwo.contains(1.toIAEntry()) shouldBe true
    intFrbtOfTwo.contains(2.toIAEntry()) shouldBe true
    intFrbtOfTwo.contains(3.toIAEntry()) shouldBe false
  }

  test("containsAll") {
    intFrbtOfNone.containsAll(intFrbtOfNone) shouldBe true
    intFrbtOfNone.containsAll(intFrbtOfOne) shouldBe false

    intFrbtOfOne.containsAll(intFrbtOfNone) shouldBe true
    intFrbtOfOne.containsAll(intFrbtOfOne) shouldBe true
    intFrbtOfOne.containsAll(intFrbtOfTwo) shouldBe false

    intFrbtOfTwo.containsAll(intFrbtOfNone) shouldBe true
    intFrbtOfTwo.containsAll(intFrbtOfOne) shouldBe true
    intFrbtOfTwo.containsAll(intFrbtOfTwo) shouldBe true
    intFrbtOfTwo.containsAll(intFrbtOfThree) shouldBe false

    intFrbtOfThree.containsAll(intFrbtOfNone) shouldBe true
    intFrbtOfThree.containsAll(intFrbtOfOne) shouldBe true
    intFrbtOfThree.containsAll(intFrbtOfOneB) shouldBe true
    intFrbtOfThree.containsAll(intFrbtOfOneC) shouldBe true
    intFrbtOfThree.containsAll(intFrbtOfTwo) shouldBe true
    intFrbtOfThree.containsAll(intFrbtOfTwoA) shouldBe true
    intFrbtOfThree.containsAll(intFrbtOfOneA) shouldBe false
    intFrbtOfThree.containsAll(intFrbtOfOneD) shouldBe false
    intFrbtOfThree.containsAll(intFrbtOfTwoB) shouldBe false
    intFrbtOfThree.containsAll(intFrbtOfTwoC) shouldBe false
  }

  test("iteration order") {
    intFrbtOfThree shouldBe (intFrbtOfThree as FRBTree<Int, Int>).inorder()
    intFrbtOfFive shouldBe (intFrbtOfFive as FRBTree<Int, Int>).inorder()
  }

  // Iterator -- methods

  test("iterator a") {
    val i0 = intFrbtOfNone.iterator()
    i0.hasNext() shouldBe false
    shouldThrow<NoSuchElementException> {
      i0.next()
    }

    val i1 = intFrbtOfOne.iterator()
    i1.hasNext() shouldBe true
    i1.next() shouldBe 1.toIAEntry()
    i1.hasNext() shouldBe false
    shouldThrow<NoSuchElementException> {
      i1.next()
    }

    val i2 = intFrbtOfTwo.iterator()
    i2.hasNext() shouldBe true
    val aux1 = i2.next()
    (aux1 == 1.toIAEntry() || aux1 == 2.toIAEntry()) shouldBe true
    i2.hasNext() shouldBe true
    val aux2 = i2.next()
    ((aux2 == 1.toIAEntry() || aux2 == 2.toIAEntry()) && (aux1 != aux2)) shouldBe true
    i2.hasNext() shouldBe false
    shouldThrow<NoSuchElementException> {
      i2.next()
    }
  }

  test("iterator b") {
    val itr = intFrbtOfFiveA.iterator() as FTreeIterator<Int, Int>
    for (i in (1..intFrbtOfFiveA.size)) {
      val tmp = itr.nullableNext()
      tmp?.let { true } shouldBe true
    }
    (itr.nullableNext()?.let { false } ?: true) shouldBe true
  }

  // typeclass (Collection, Iterator, Iterable)

  test("first") {

    shouldThrow<NoSuchElementException> {
      intFrbtOfNone.first()
    }
    intFrbtOfNone.firstOrNull() shouldBe null

    intFrbtOfOne.firstOrNull() shouldBe 1.toIAEntry()
    val aux = intFrbtOfTwo.first()
    (aux == 1.toIAEntry() || aux == 2.toIAEntry()) shouldBe true
    (aux in intFrbtOfTwo) shouldBe true
  }

  //  ignore
  //  test("firstNotNullOf") {}

  test("find first") {
    shouldThrow<NoSuchElementException> {
      intFrbtOfNone.first(matchEqual(0))
    }
    intFrbtOfNone.firstOrNull(matchEqual(0)) shouldBe null

    shouldThrow<NoSuchElementException> {
      intFrbtOfOne.first(matchEqual(0))
    }
    intFrbtOfOne.firstOrNull(matchEqual(0)) shouldBe null
    intFrbtOfOne.firstOrNull(matchEqual(1)) shouldBe 1.toIAEntry()

    intFrbtOfTwo.firstOrNull(matchEqual(0)) shouldBe null
    intFrbtOfTwo.first(matchEqual(1)) shouldBe 1.toIAEntry()
    intFrbtOfTwo.first(matchEqual(2)) shouldBe 2.toIAEntry()
    intFrbtOfTwo.firstOrNull(matchEqual(3)) shouldBe null
  }

  test("indexOf") {
    intFrbtOfNone.indexOf(0.toIAEntry()) shouldBe NOT_FOUND

    intFrbtOfOne.indexOf(0.toIAEntry()) shouldBe NOT_FOUND
    intFrbtOfOne.indexOf(1.toIAEntry()) shouldBe 0
    intFrbtOfOne.indexOf(2.toIAEntry()) shouldBe NOT_FOUND

    intFrbtOfTwo.indexOf(0.toIAEntry()) shouldBe NOT_FOUND
    val aux1 = intFrbtOfTwo.indexOf(1.toIAEntry())
    (aux1 == 0 || aux1 == 1) shouldBe true
    val aux2 = intFrbtOfTwo.indexOf(2.toIAEntry())
    ((aux2 == 0 || aux2 == 1) && (aux1 != aux2)) shouldBe true
    intFrbtOfTwo.indexOf(3.toIAEntry()) shouldBe NOT_FOUND
  }

  test("indexOfirst") {
    intFrbtOfNone.indexOfFirst(matchEqual(0)) shouldBe NOT_FOUND

    intFrbtOfOne.indexOfFirst(matchEqual(0)) shouldBe NOT_FOUND
    intFrbtOfOne.indexOfFirst(matchEqual(1)) shouldBe 0

    intFrbtOfSix.indexOfFirst(matchEqual(0)) shouldBe NOT_FOUND
    val aux1 = intFrbtOfSix.indexOfFirst(matchEqual(1))
    (aux1 in (0..intFrbtOfSix.size)) shouldBe true
    val aux2 = intFrbtOfSix.indexOfFirst(matchEqual(2))
    (aux2 in (0..intFrbtOfSix.size) && aux1 != aux2) shouldBe true
    intFrbtOfSix.indexOfFirst(matchEqual(30)) shouldBe NOT_FOUND
  }

  test("indexOfLast") {
    intFrbtOfNone.indexOfLast(matchEqual(0)) shouldBe NOT_FOUND

    intFrbtOfOne.indexOfLast(matchEqual(0)) shouldBe NOT_FOUND
    intFrbtOfOne.indexOfLast(matchEqual(1)) shouldBe 0

    intFrbtOfSix.indexOfLast(matchEqual(0)) shouldBe NOT_FOUND
    val aux1 = intFrbtOfSix.indexOfLast(matchEqual(1))
    (aux1 in (0..intFrbtOfSix.size)) shouldBe true
    val aux2 = intFrbtOfSix.indexOfLast(matchEqual(2))
    (aux2 in (0..intFrbtOfSix.size) && aux1 != aux2) shouldBe true
    intFrbtOfSix.indexOfLast(matchEqual(30)) shouldBe NOT_FOUND
  }

  test("last") {
    shouldThrow<NoSuchElementException> {
      intFrbtOfNone.last()
    }
    intFrbtOfNone.lastOrNull() shouldBe null
    intFrbtOfOne.lastOrNull() shouldBe 1.toIAEntry()
    intFrbtOfOne.last() shouldBe 1.toIAEntry()
    val aux1 = intFrbtOfTwo.last()
    (aux1 in intFrbtOfTwo) shouldBe true
    val aux2 = intFrbtOfThree.last()
    (aux2 in intFrbtOfThree) shouldBe true
  }

  test("last (find)") {
    shouldThrow<NoSuchElementException> {
      intFrbtOfNone.last(matchEqual(0))
    }
    intFrbtOfNone.lastOrNull(matchEqual(0)) shouldBe null

    intFrbtOfOne.lastOrNull(matchEqual(1)) shouldBe 1.toIAEntry()
    intFrbtOfOne.last(matchEqual(1)) shouldBe 1.toIAEntry()
    intFrbtOfOne.lastOrNull(matchEqual(2)) shouldBe null

    intFrbtOfTwo.lastOrNull(matchEqual(0)) shouldBe null
    intFrbtOfTwo.last(matchEqual(1)) shouldBe 1.toIAEntry()
    intFrbtOfTwo.last(matchEqual(2)) shouldBe 2.toIAEntry()
    shouldThrow<NoSuchElementException> {
      intFrbtOfTwo.last(matchEqual(3))
    }
    intFrbtOfTwo.lastOrNull(matchEqual(3)) shouldBe null
  }

  test("lastIndexOf") {

    intFrbtOfNone.lastIndexOf(0.toIAEntry()) shouldBe NOT_FOUND

    intFrbtOfOne.lastIndexOf(0.toIAEntry()) shouldBe NOT_FOUND
    intFrbtOfOne.lastIndexOf(1.toIAEntry()) shouldBe 0

    intFrbtOfSix.lastIndexOf(0.toIAEntry()) shouldBe NOT_FOUND

    val aux1 = intFrbtOfSix.lastIndexOf(1.toIAEntry())
    (aux1 in (0..intFrbtOfSix.size)) shouldBe true
    val aux2 = intFrbtOfSix.lastIndexOf(2.toIAEntry())
    (aux2 in (0..intFrbtOfSix.size) && aux1 != aux2) shouldBe true
    intFrbtOfSix.lastIndexOf(30.toIAEntry()) shouldBe NOT_FOUND
  }

  test("findlast") {
    intFrbtOfNone.findLast(matchEqual(0)) shouldBe null

    intFrbtOfOne.findLast(matchEqual(0)) shouldBe null
    intFrbtOfOne.findLast(matchEqual(1)) shouldBe 1.toIAEntry()

    intFrbtOfTwo.findLast(matchEqual(0)) shouldBe null
    intFrbtOfTwo.findLast(matchEqual(1)) shouldBe 1.toIAEntry()
    intFrbtOfTwo.findLast(matchEqual(2)) shouldBe 2.toIAEntry()
    intFrbtOfTwo.findLast(matchEqual(3)) shouldBe null
  }

  test("single") {
    shouldThrow<NoSuchElementException> {
      intFrbtOfNone.single()
    }
    intFrbtOfNone.singleOrNull() shouldBe null

    intFrbtOfOne.single() shouldBe 1.toIAEntry()
    intFrbtOfOne.singleOrNull() shouldBe 1.toIAEntry()

    shouldThrow<IllegalArgumentException> {
      intFrbtOfTwo.single()
    }
    intFrbtOfTwo.singleOrNull() shouldBe null
  }

  test("single (find)") {
    shouldThrow<NoSuchElementException> {
      intFrbtOfNone.single(matchEqual(0))
    }
    intFrbtOfNone.singleOrNull(matchEqual(0)) shouldBe null

    intFrbtOfOne.single(matchEqual(1)) shouldBe 1.toIAEntry()
    intFrbtOfOne.singleOrNull(matchEqual(1)) shouldBe 1.toIAEntry()

    shouldThrow<NoSuchElementException> {
      intFrbtOfTwo.single(matchEqual(0))
    }
    intFrbtOfTwo.singleOrNull(matchEqual(0)) shouldBe null
    intFrbtOfTwo.single(matchEqual(1)) shouldBe 1.toIAEntry()
    intFrbtOfTwo.single(matchEqual(2)) shouldBe 2.toIAEntry()
    shouldThrow<NoSuchElementException> {
      intFrbtOfTwo.single(matchEqual(3))
    }
    intFrbtOfTwo.singleOrNull(matchEqual(3)) shouldBe null

    shouldThrow<NoSuchElementException> {
      intFrbtOfTwo.single(matchEqual(0))
    }
    intFrbtOfTwo.singleOrNull(matchEqual(0)) shouldBe null
    shouldThrow<NoSuchElementException> {
      intFrbtOfTwo.single(matchEqual(3))
    }
    intFrbtOfTwo.singleOrNull(matchEqual(3)) shouldBe null
  }

  test("drop 0") {
    intFrbtOfNone.drop(0) shouldBe emptyList()
    intFrbtOfOne.drop(0) shouldBe intFrbtOfOne
    intFrbtOfTwo.drop(0) shouldBe intFrbtOfTwo
  }

  test("drop 1") {
    val aux0: List<TKVEntry<Int, Int>> = intFrbtOfThree.drop(1)
    (aux0.size == intFrbtOfThree.size - 1) shouldBe true
    intFrbtOfThree.containsAll(aux0) shouldBe true
    intFrbtOfNone.drop(1) shouldBe emptyList()
    intFrbtOfOne.drop(1) shouldBe emptyList()
    val aux2 = intFrbtOfTwo.drop(1).toSet()
    (aux2.size == intFrbtOfTwo.size - 1) shouldBe true
    intFrbtOfTwo.containsAll(aux2) shouldBe true
  }

  test("drop 2") {
    intFrbtOfNone.drop(2) shouldBe emptyList()
    intFrbtOfOne.drop(2) shouldBe emptyList()
    intFrbtOfTwo.drop(2) shouldBe emptyList()

    val aux1 = intFrbtOfThree.drop(2).toSet()
    (aux1.size == intFrbtOfThree.size - 2) shouldBe true
    intFrbtOfThree.containsAll(aux1) shouldBe true

    val a2 = FRBTree.ofvi(*arrayOf<Int>(1,2,3,4))
    val aux2 = a2.drop(2).toSet()
    (aux2.size == a2.size - 2) shouldBe true
    a2.containsAll(aux2) shouldBe true
  }

  test("drop 3") {
    intFrbtOfNone.drop(3) shouldBe emptyList()
    intFrbtOfOne.drop(3) shouldBe emptyList()
    intFrbtOfTwo.drop(3) shouldBe emptyList()
    intFrbtOfThree.drop(3) shouldBe emptyList()
    val a2 = FRBTree.ofvi(*arrayOf<Int>(1,2,3,4))
    val aux2 = a2.drop(3).toSet()
    (aux2.size == a2.size - 3) shouldBe true
    a2.containsAll(aux2) shouldBe true
  }

  test("dropWhile") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FRBTree.ofvi(*arrayOf<Int>(2,1)).dropWhile { it > 1.toIAEntry() }
    }
  }

  test("filter") {
    intFrbtOfNone.filter {0 == it.getv() % 2} shouldBe emptyList()
    intFrbtOfOne.filter {0 == it.getv() % 2} shouldBe emptyList()
    intFrbtOfTwo.filter {0 == it.getv() % 2} shouldBe FRBTree.ofvi(2)
    intFrbtOfThree.filter {0 == it.getv() % 2} shouldBe FRBTree.ofvi(2)
    FRBTree.ofvi(*arrayOf<Int>(1,2,3,4)).filter {0 == it.getv() % 2} shouldBe FRBTree.ofvi(2, 4)
  }

  //  ignore
  //  test("filter indexed") {}
  //  test("filterIsInstance") {}

  test("filterNot") {
    intFrbtOfNone.filterNot {0 == it.getv() % 2} shouldBe emptyList()
    intFrbtOfOne.filterNot {0 == it.getv() % 2} shouldBe FRBTree.ofvi(1)
    intFrbtOfTwo.filterNot {0 == it.getv() % 2} shouldBe FRBTree.ofvi(1)
    intFrbtOfThree.filterNot {0 == it.getv() % 2} shouldBe FRBTree.ofvi(1,3)
    FRBTree.ofvi(*arrayOf<Int>(1,2,3,4)).filterNot {0 == it.getv() % 2} shouldBe FRBTree.ofvi(1, 3)
  }

  //  ignore
  //  test("filterNotNull") {}

  test("take 0") {
    intFrbtOfNone.take(0) shouldBe emptyList()
    intFrbtOfOne.take(0) shouldBe emptyList()
    intFrbtOfTwo.take(0) shouldBe emptyList()
  }

  test("take 1") {
    intFrbtOfNone.take(1) shouldBe emptyList()
    intFrbtOfOne.take(1) shouldBe intFrbtOfOne

    val aux1 = intFrbtOfTwo.take(1).toSet()
    aux1.size shouldBe 1
    intFrbtOfTwo.containsAll(aux1) shouldBe true

    val aux2 = intFrbtOfThree.take(1).toSet()
    aux2.size shouldBe 1
    intFrbtOfThree.containsAll(aux2) shouldBe true
  }

  test("take 2") {
    intFrbtOfNone.take(2) shouldBe emptyList()
    intFrbtOfOne.take(2) shouldBe intFrbtOfOne

    val aux1 = intFrbtOfTwo.take(2).toSet()
    aux1.size shouldBe 2
    intFrbtOfTwo.containsAll(aux1) shouldBe true

    val aux2 = intFrbtOfThree.take(2).toSet()
    aux2.size shouldBe 2
    intFrbtOfThree.containsAll(aux2) shouldBe true

    val a3 = FRBTree.ofvi(*arrayOf<Int>(1,2,3,4))
    val aux3 = a3.take(2)
    aux3.size shouldBe 2
    a3.containsAll(aux3) shouldBe true
  }

  test("take 3") {
    intFrbtOfNone.take(3) shouldBe emptyList()
    intFrbtOfOne.take(3) shouldBe intFrbtOfOne
    intFrbtOfTwo.take(3) shouldBe intFrbtOfTwo
    intFrbtOfThree.take(3) shouldBe intFrbtOfThree
    val a3 = FRBTree.ofvi(*arrayOf<Int>(1,2,3,4))
    val aux3 = a3.take(3)
    aux3.size shouldBe 3
    a3.containsAll(aux3) shouldBe true
  }

  test("takeWhile") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FRBTree.ofvi(*arrayOf<Int>(2,1)).takeWhile { it.getv() > 1 }
    }
  }

  test("reversed") {
    intFrbtOfNone.reversed() shouldBe emptyList()
    intFrbtOfOne.reversed() shouldBe intFrbtOfOne
    intFrbtOfTwo.reversed() shouldBe (intFrbtOfTwo as FRBTree<Int,Int>).inorder(reverse = true)
    intFrbtOfThree.reversed() shouldBe (intFrbtOfThree as FRBTree<Int,Int>).inorder(reverse = true)
  }

  test("sorted") {
    intFrbtOfNone.sorted().toSet() shouldBe emptyList()
    intFrbtOfOne.sorted().toSet() shouldBe intFrbtOfOne
    intFrbtOfTwo.sorted().toSet() shouldBe intFrbtOfTwo
    intFrbtOfThree.sorted().toSet() shouldBe intFrbtOfThree
    intFrbtOfSix.sorted().toSet() shouldBe intFrbtOfThree
  }

  test("sortedDescending") {
    intFrbtOfNone.sortedDescending() shouldBe emptyList()
    intFrbtOfOne.sortedDescending() shouldBe (intFrbtOfOne as FRBTree<Int,Int>).inorder(reverse = true)
    intFrbtOfTwo.sortedDescending() shouldBe (intFrbtOfTwo as FRBTree<Int,Int>).inorder(reverse = true)
    intFrbtOfThree.sortedDescending() shouldBe (intFrbtOfThree as FRBTree<Int,Int>).inorder(reverse = true)
    intFrbtOfSix.sortedDescending() shouldBe (intFrbtOfThree).inorder(reverse = true)
  }

  test("sortedBy") {

    intFrbtOfNone.sortedBy(::reverseNumerical) shouldBe emptyList()
    intFrbtOfOne.sortedBy(::reverseNumerical) shouldBe intFrbtOfOne
    intFrbtOfTwo.sortedBy(::reverseNumerical) shouldBe (intFrbtOfTwo as FRBTree<Int, Int>).inorder(reverse = true)
    intFrbtOfThree.sortedBy(::reverseNumerical) shouldBe (intFrbtOfThree as FRBTree<Int, Int>).inorder(reverse = true)
    intFrbtOfSix.sortedBy(::reverseNumerical) shouldBe intFrbtOfThree.inorder(reverse = true)
  }

  test("sortedByDescending") {

    intFrbtOfNone.sortedByDescending(::reverseNumerical) shouldBe emptyList()
    intFrbtOfOne.sortedByDescending(::reverseNumerical) shouldBe intFrbtOfOne
    intFrbtOfTwo.sortedByDescending(::reverseNumerical) shouldBe intFrbtOfTwo
    intFrbtOfThree.sortedByDescending(::reverseNumerical) shouldBe intFrbtOfThree
    intFrbtOfSix.sortedByDescending(::reverseNumerical) shouldBe intFrbtOfThree
  }

  test("sortedWith") {

    val reverseNumerical: Comparator<TKVEntry<Int, Int>> = Comparator { p0, p1 ->
      when {
        p0 == p1 -> 0
        p0 > p1 -> -1
        else -> 1
      }
    }

    intFrbtOfNone.sortedWith(reverseNumerical) shouldBe emptyList()
    intFrbtOfOne.sortedWith(reverseNumerical) shouldBe intFrbtOfOne
    intFrbtOfTwo.sortedWith(reverseNumerical) shouldBe (intFrbtOfTwo as FRBTree<Int,Int>).inorder(reverse = true)
    intFrbtOfThree.sortedWith(reverseNumerical) shouldBe (intFrbtOfThree as FRBTree<Int,Int>).inorder(reverse = true)
    intFrbtOfSix.sortedWith(reverseNumerical) shouldBe intFrbtOfThree.inorder(reverse = true)
  }

  test("associate") {

    fun f(t: TKVEntry<Int, Int>): Pair<TKVEntry<Int, Int>, TKVEntry<Int, Int>> = Pair(t, (-t.getv()).toIAEntry())

    intFrbtOfNone.associate(::f) shouldBe emptyMap()
    intFrbtOfOne.associate(::f) shouldBe mapOf( 1.toIAEntry() to (-1).toIAEntry() )
    intFrbtOfTwo.associate(::f) shouldBe mapOf( 1.toIAEntry() to (-1).toIAEntry(), 2.toIAEntry() to (-2).toIAEntry() )
    intFrbtOfThree.associate(::f) shouldBe mapOf( 1.toIAEntry() to (-1).toIAEntry(), 2.toIAEntry() to (-2).toIAEntry(), 3.toIAEntry() to (-3).toIAEntry() )
  }

  test("associateBy") {

    fun f(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = (-t.getv()).toIAEntry()

    intFrbtOfNone.associateBy(::f) shouldBe emptyMap()
    intFrbtOfOne.associateBy(::f) shouldBe mapOf( (-1).toIAEntry() to 1.toIAEntry() )
    intFrbtOfTwo.associateBy(::f) shouldBe mapOf( (-1).toIAEntry() to 1.toIAEntry(), (-2).toIAEntry() to 2.toIAEntry() )
    intFrbtOfThree.associateBy(::f) shouldBe mapOf( (-1).toIAEntry() to 1.toIAEntry(), (-2).toIAEntry() to 2.toIAEntry(), (-3).toIAEntry() to 3.toIAEntry() )
  }

  test("associateBy (k, v)") {

    fun f(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = (-t.getv()).toIAEntry()
    fun g(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = (2*t.getv()).toIAEntry()

    intFrbtOfNone.associateBy(::f, ::g) shouldBe emptyMap()
    intFrbtOfOne.associateBy(::f, ::g) shouldBe mapOf( (-1).toIAEntry() to 2.toIAEntry() )
    intFrbtOfTwo.associateBy(::f, ::g) shouldBe mapOf( (-1).toIAEntry() to 2.toIAEntry(), (-2).toIAEntry() to 4.toIAEntry() )
    intFrbtOfThree.associateBy(::f, ::g) shouldBe mapOf( (-1).toIAEntry() to 2.toIAEntry(), (-2).toIAEntry() to 4.toIAEntry(), (-3).toIAEntry() to 6.toIAEntry() )
  }

  test("associateWith") {

    fun g(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = (2*t.getv()).toIAEntry()

    intFrbtOfNone.associateWith(::g) shouldBe emptyMap()
    intFrbtOfOne.associateWith(::g) shouldBe mapOf( 1.toIAEntry() to 2.toIAEntry() )
    intFrbtOfTwo.associateWith(::g) shouldBe mapOf( 1.toIAEntry() to 2.toIAEntry(), 2.toIAEntry() to 4.toIAEntry() )
    intFrbtOfThree.associateWith(::g) shouldBe mapOf( 1.toIAEntry() to 2.toIAEntry(), 2.toIAEntry() to 4.toIAEntry(), 3.toIAEntry() to 6.toIAEntry() )
  }

  test("flatMap") {
    intFrbtOfNone.flatMap{ FRBTree.ofvi(it) } shouldBe emptyList()
    intFrbtOfOne.flatMap{ FRBTree.ofvi(it) } shouldBe FRBTree.ofvi(1.toIAEntry())
    fun arrayBuilderConst(arg: Int) = Array(arg) { _ -> arg }
    intFrbtOfTwo.flatMap {FRBTree.ofvi(*arrayBuilderConst(it.getv()))} shouldBe FRBTree.ofvi(1, 2)
    fun arrayBuilderIncrement(arg: Int) = Array(arg) { i -> (arg + i) }
    intFrbtOfTwo.flatMap {FRBTree.ofvi(*arrayBuilderIncrement(it.getv()))} shouldBe FRBTree.ofvi(1, 2, 3)
    intFrbtOfThree.flatMap {FRBTree.ofvi(*arrayBuilderIncrement(it.getv()))} shouldBe FList.of(1, 2, 3, 3, 4, 5).toIMList().toIAEntries()
    intFrbtOfThree.flatMap { i -> FRBTree.ofvi(i, i) } shouldBe FRBTree.ofvi(1.toIAEntry(), 2.toIAEntry(), 3.toIAEntry(),)
  }

  // ignore
  // test("flatMapIndexed") {}

  test("groupBy") {

    fun f(t: TKVEntry<Int, Int>): Int = -t.getv()

    intFrbtOfNone.groupBy(::f) shouldBe emptyMap()
    intFrbtOfFiveA.groupBy(::f) shouldBe mapOf( -1 to setOf(1.toIAEntry(), 1.toIAEntry()), -2 to setOf(2.toIAEntry(), 2.toIAEntry()), -3 to setOf(3.toIAEntry()) )
  }

  test("groupBy (k, v)") {

    fun f(t: TKVEntry<Int, Int>): Int = -t.getv()
    fun g(t: TKVEntry<Int, Int>): Int = 2*t.getv()

    intFrbtOfNone.groupBy(::f, ::g) shouldBe emptyMap()
    intFrbtOfFiveA.groupBy(::f, ::g) shouldBe mapOf( -1 to setOf(2, 2), -2 to setOf(4,4), -3 to setOf(6) )
  }

  // TODO (maybe)
  // test("grouping") {
  //   fail("not implemented yet")
  // }

  test("map") {
    intFrbtOfNone.map { it.getv() + 1} shouldBe emptyList()
    intFrbtOfOne.map { it.getv() + 1} shouldBe FList.of(2)
    intFrbtOfTwo.map { it.getv() + 1} shouldBe FList.of(2, 3)
  }

  // ignore
  // test("mapIndexed") {}
  // test("mapIndexedNotNull") {}
  // test("mapNotNull") {}

  test("withIndex") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FRBTree.ofvi(*arrayOf<Int>(2,1)).withIndex()
    }
  }

  test("distinct") {
    intFrbtOfNone.distinct() shouldBe emptyList()
    intFrbtOfOne.distinct() shouldBe intFrbtOfOne
    intFrbtOfTwo.distinct() shouldBe intFrbtOfTwo
    intFrbtOfFiveA.distinct() shouldBe intFrbtOfThree
    intFrbtOfSix.distinct() shouldBe intFrbtOfThree
  }

  test("distinctBy") {

    fun identity(oracle: TKVEntry<Int, Int>) = oracle

    intFrbtOfNone.distinctBy(::identity) shouldBe emptyList()
    intFrbtOfOne.distinctBy(::identity) shouldBe intFrbtOfOne
    intFrbtOfTwo.distinctBy(::identity) shouldBe intFrbtOfTwo
    intFrbtOfFiveA.distinctBy(::identity) shouldBe intFrbtOfThree
    intFrbtOfSix.distinctBy(::identity) shouldBe intFrbtOfThree
  }

   test("intersect") {
     intFrbtOfNone.intersect(intFrbtOfNone) shouldBe intFrbtOfNone

     intFrbtOfOne.intersect(intFrbtOfNone) shouldBe intFrbtOfNone
     intFrbtOfTwo.intersect(intFrbtOfNone) shouldBe intFrbtOfNone
     intFrbtOfThree.intersect(intFrbtOfNone) shouldBe intFrbtOfNone

     intFrbtOfNone.intersect(intFrbtOfOne) shouldBe intFrbtOfNone
     intFrbtOfNone.intersect(intFrbtOfTwo) shouldBe intFrbtOfNone
     intFrbtOfNone.intersect(intFrbtOfThree) shouldBe intFrbtOfNone

     intFrbtOfOne.intersect(intFrbtOfOne) shouldBe intFrbtOfOne
     intFrbtOfTwo.intersect(intFrbtOfTwo) shouldBe intFrbtOfTwo
     intFrbtOfThree.intersect(intFrbtOfThree) shouldBe intFrbtOfThree

     intFrbtOfTwo.intersect(intFrbtOfOne) shouldBe intFrbtOfOne
     intFrbtOfOne.intersect(intFrbtOfTwo) shouldBe intFrbtOfOne

     intFrbtOfOne.intersect(intFrbtOfThree) shouldBe intFrbtOfOne
     intFrbtOfOne.intersect(intFrbtOf13) shouldBe intFrbtOfOne
     intFrbtOfOne.intersect(intFrbtOf25) shouldBe intFrbtOfNone
     intFrbtOfOne.intersect(intFrbtOf45) shouldBe intFrbtOfNone

     intFrbtOfTwo.intersect(intFrbtOfThree) shouldBe intFrbtOfTwo
     intFrbtOfTwo.intersect(intFrbtOf13) shouldBe intFrbtOfOne
     intFrbtOfTwo.intersect(intFrbtOf25) shouldBe intFrbtOf2
     intFrbtOfTwo.intersect(intFrbtOf45) shouldBe intFrbtOfNone

     intFrbtOf13.intersect(intFrbtOfOne) shouldBe intFrbtOfOne
     intFrbtOf13.intersect(intFrbtOfTwo) shouldBe intFrbtOfOne
     intFrbtOf13.intersect(intFrbtOfThree) shouldBe intFrbtOf13
     intFrbtOf13.intersect(intFrbtOf25) shouldBe intFrbtOfNone
     intFrbtOf13.intersect(intFrbtOf45) shouldBe intFrbtOfNone

     intFrbtOf25.intersect(intFrbtOfOne) shouldBe intFrbtOfNone
     intFrbtOf25.intersect(intFrbtOfTwo) shouldBe intFrbtOf2
     intFrbtOf25.intersect(intFrbtOfThree) shouldBe intFrbtOf2
     intFrbtOf25.intersect(intFrbtOf13) shouldBe intFrbtOfNone
     intFrbtOf25.intersect(intFrbtOf45) shouldBe intFrbtOf5

     intFrbtOf45.intersect(intFrbtOfOne) shouldBe intFrbtOfNone
     intFrbtOf45.intersect(intFrbtOfTwo) shouldBe intFrbtOfNone
     intFrbtOf45.intersect(intFrbtOfThree) shouldBe intFrbtOfNone
     intFrbtOf45.intersect(intFrbtOf13) shouldBe intFrbtOfNone
     intFrbtOf45.intersect(intFrbtOf25) shouldBe intFrbtOf5

     intFrbtOfThree.intersect(intFrbtOfOne) shouldBe intFrbtOfOne
     intFrbtOfThree.intersect(intFrbtOfTwo) shouldBe intFrbtOfTwo
     intFrbtOfThree.intersect(intFrbtOf13) shouldBe intFrbtOf13
     intFrbtOfThree.intersect(intFrbtOf25) shouldBe intFrbtOf2
     intFrbtOfThree.intersect(intFrbtOf45) shouldBe intFrbtOfNone
   }

   test("subtract") {
     intFrbtOfNone.subtract(intFrbtOfNone) shouldBe intFrbtOfNone

     intFrbtOfOne.subtract(intFrbtOfNone) shouldBe intFrbtOfOne
     intFrbtOfTwo.subtract(intFrbtOfNone) shouldBe intFrbtOfTwo
     intFrbtOfThree.subtract(intFrbtOfNone) shouldBe intFrbtOfThree

     intFrbtOfNone.subtract(intFrbtOfOne) shouldBe intFrbtOfNone
     intFrbtOfNone.subtract(intFrbtOfTwo) shouldBe intFrbtOfNone
     intFrbtOfNone.subtract(intFrbtOfThree) shouldBe intFrbtOfNone

     intFrbtOfOne.subtract(intFrbtOfOne) shouldBe intFrbtOfNone
     intFrbtOfTwo.subtract(intFrbtOfTwo) shouldBe intFrbtOfNone
     intFrbtOfThree.subtract(intFrbtOfThree) shouldBe intFrbtOfNone

     intFrbtOfTwo.subtract(intFrbtOfOne) shouldBe intFrbtOf2
     intFrbtOfOne.subtract(intFrbtOfTwo) shouldBe intFrbtOfNone

     intFrbtOfOne.subtract(intFrbtOfThree) shouldBe intFrbtOfNone
     intFrbtOfOne.subtract(intFrbtOf13) shouldBe intFrbtOfNone
     intFrbtOfOne.subtract(intFrbtOf25) shouldBe intFrbtOfOne
     intFrbtOfOne.subtract(intFrbtOf45) shouldBe intFrbtOfOne

     intFrbtOfTwo.subtract(intFrbtOfThree) shouldBe intFrbtOfNone
     intFrbtOfTwo.subtract(intFrbtOf13) shouldBe intFrbtOf2
     intFrbtOfTwo.subtract(intFrbtOf25) shouldBe intFrbtOfOne
     intFrbtOfTwo.subtract(intFrbtOf45) shouldBe intFrbtOfTwo

     intFrbtOf13.subtract(intFrbtOfOne) shouldBe intFrbtOf3
     intFrbtOf13.subtract(intFrbtOfTwo) shouldBe intFrbtOf3
     intFrbtOf13.subtract(intFrbtOfThree) shouldBe intFrbtOfNone
     intFrbtOf13.subtract(intFrbtOf25) shouldBe intFrbtOf13
     intFrbtOf13.subtract(intFrbtOf45) shouldBe intFrbtOf13

     intFrbtOf25.subtract(intFrbtOfOne) shouldBe intFrbtOf25
     intFrbtOf25.subtract(intFrbtOfTwo) shouldBe intFrbtOf5
     intFrbtOf25.subtract(intFrbtOfThree) shouldBe intFrbtOf5
     intFrbtOf25.subtract(intFrbtOf13) shouldBe intFrbtOf25
     intFrbtOf25.subtract(intFrbtOf45) shouldBe intFrbtOf2

     intFrbtOf45.subtract(intFrbtOfOne) shouldBe intFrbtOf45
     intFrbtOf45.subtract(intFrbtOfTwo) shouldBe intFrbtOf45
     intFrbtOf45.subtract(intFrbtOfThree) shouldBe intFrbtOf45
     intFrbtOf45.subtract(intFrbtOf13) shouldBe intFrbtOf45
     intFrbtOf45.subtract(intFrbtOf25) shouldBe intFrbtOf4

     intFrbtOfThree.subtract(intFrbtOfOne) shouldBe intFrbtOf23
     intFrbtOfThree.subtract(intFrbtOfTwo) shouldBe intFrbtOf3
     intFrbtOfThree.subtract(intFrbtOf13) shouldBe intFrbtOf2
     intFrbtOfThree.subtract(intFrbtOf25) shouldBe intFrbtOf13
     intFrbtOfThree.subtract(intFrbtOf45) shouldBe intFrbtOfThree
   }

  test("union") {
    intFrbtOfNone.union(intFrbtOfNone) shouldBe intFrbtOfNone

    intFrbtOfOne.union(intFrbtOfNone) shouldBe intFrbtOfOne
    intFrbtOfTwo.union(intFrbtOfNone) shouldBe intFrbtOfTwo
    intFrbtOfThree.union(intFrbtOfNone) shouldBe intFrbtOfThree

    intFrbtOfNone.union(intFrbtOfOne) shouldBe intFrbtOfOne
    intFrbtOfNone.union(intFrbtOfTwo) shouldBe intFrbtOfTwo
    intFrbtOfNone.union(intFrbtOfThree) shouldBe intFrbtOfThree

    intFrbtOfOne.union(intFrbtOfOne) shouldBe intFrbtOfOne
    intFrbtOfTwo.union(intFrbtOfTwo) shouldBe intFrbtOfTwo
    intFrbtOfThree.union(intFrbtOfThree) shouldBe intFrbtOfThree

    intFrbtOfTwo.union(intFrbtOfOne) shouldBe intFrbtOfTwo
    intFrbtOfOne.union(intFrbtOfTwo) shouldBe intFrbtOfTwo

    intFrbtOfOne.union(intFrbtOfThree) shouldBe intFrbtOfThree
    intFrbtOfOne.union(intFrbtOf13) shouldBe intFrbtOf13
    intFrbtOfOne.union(intFrbtOf25) shouldBe intFrbtOf125
    intFrbtOfOne.union(intFrbtOf45) shouldBe intFrbtOf145

    intFrbtOfTwo.union(intFrbtOfThree) shouldBe intFrbtOfThree
    intFrbtOfTwo.union(intFrbtOf13) shouldBe intFrbtOfThree
    intFrbtOfTwo.union(intFrbtOf25) shouldBe intFrbtOf125
    intFrbtOfTwo.union(intFrbtOf45) shouldBe intFrbtOf1245

    intFrbtOf13.union(intFrbtOfOne) shouldBe intFrbtOf13
    intFrbtOf13.union(intFrbtOfTwo).sorted() shouldBe (intFrbtOfThree as FRBTree<Int,Int>).inorder()
    intFrbtOf13.union(intFrbtOfThree).sorted() shouldBe intFrbtOfThree.inorder()
    intFrbtOf13.union(intFrbtOf25).sorted() shouldBe (intFrbtOf1235 as FRBTree<Int,Int>).inorder()
    intFrbtOf13.union(intFrbtOf45) shouldBe intFrbtOf1345

    intFrbtOf25.union(intFrbtOfOne).sorted() shouldBe (intFrbtOf125 as FRBTree<Int,Int>).inorder()
    intFrbtOf25.union(intFrbtOfTwo).sorted() shouldBe intFrbtOf125.inorder()
    intFrbtOf25.union(intFrbtOfThree).sorted() shouldBe intFrbtOf1235.inorder()
    intFrbtOf25.union(intFrbtOf13).sorted() shouldBe intFrbtOf1235.inorder()
    intFrbtOf25.union(intFrbtOf45).sorted() shouldBe (intFrbtOf245 as FRBTree<Int,Int>).inorder()

    intFrbtOf45.union(intFrbtOfOne).sorted() shouldBe (intFrbtOf145 as FRBTree<Int,Int>).inorder()
    intFrbtOf45.union(intFrbtOfTwo).sorted() shouldBe (intFrbtOf1245 as FRBTree<Int,Int>).inorder()
    intFrbtOf45.union(intFrbtOfThree).sorted() shouldBe (intFrbtOfFive as FRBTree<Int,Int>).inorder()
    intFrbtOf45.union(intFrbtOf13).sorted() shouldBe (intFrbtOf1345 as FRBTree<Int,Int>).inorder()
    intFrbtOf45.union(intFrbtOf25).sorted() shouldBe intFrbtOf245.inorder()

    intFrbtOfThree.union(intFrbtOfOne) shouldBe intFrbtOfThree
    intFrbtOfThree.union(intFrbtOfTwo) shouldBe intFrbtOfThree
    intFrbtOfThree.union(intFrbtOf13) shouldBe intFrbtOfThree
    intFrbtOfThree.union(intFrbtOf25) shouldBe intFrbtOf1235
    intFrbtOfThree.union(intFrbtOf45) shouldBe intFrbtOfFive
  }

  test("all") {
    intFrbtOfNone.all(matchLessThan(0)) shouldBe true // by vacuous implication
    intFrbtOfOne.all(matchLessThan(1)) shouldBe false
    intFrbtOfOne.all(matchLessThan(2)) shouldBe true
    intFrbtOfThree.all(matchLessThan(2)) shouldBe false
    intFrbtOfThree.all(matchLessThan(4)) shouldBe true
  }

  test("any") {
    intFrbtOfNone.any(matchLessThan(0)) shouldBe false
    intFrbtOfOne.any(matchLessThan(1)) shouldBe false
    intFrbtOfOne.any(matchLessThan(2)) shouldBe true
    intFrbtOfThree.any(matchLessThan(1)) shouldBe false
    intFrbtOfThree.any(matchLessThan(2)) shouldBe true
    intFrbtOfThree.any(matchLessThan(4)) shouldBe true
  }

  test("(has) any") {
    intFrbtOfNone.any() shouldBe false
    intFrbtOfOne.any() shouldBe true
    intFrbtOfThree.any() shouldBe true
  }

  test("count") {
    intFrbtOfNone.count() shouldBe 0
    intFrbtOfOne.count() shouldBe 1
    intFrbtOfThree.count() shouldBe 3
  }

  test("count matching") {
    intFrbtOfNone.count(matchEqual(0)) shouldBe 0
    intFrbtOfFiveA.count(matchEqual(3)) shouldBe 1
    intFrbtOfSix.count(matchEqual(3)) shouldBe 1
  }

  test("fold") {

    val s = { acc: Int, b: TKVEntry<Int, Int> -> acc - b.getv() }

    intFrbtOfNone.fold(1, s) shouldBe 1
    intFrbtOfOne.fold(1, s) shouldBe 0
    intFrbtOfTwo.fold(1, s) shouldBe -2
    intFrbtOfTwoA.fold(1, s) shouldBe -3
    intFrbtOfThree.fold(1, s) shouldBe -5
    intFrbtOfThreeA.fold(1, s) shouldBe -7
  }

  // ignore
  // test("foldIndexed") {}

  test("(has) none") {
    intFrbtOfNone.none() shouldBe true
    intFrbtOfOne.none() shouldBe false
    intFrbtOfThree.none() shouldBe false
  }

  test("none") {
    intFrbtOfNone.none(matchLessThan(0)) shouldBe true
    intFrbtOfOne.none(matchLessThan(1)) shouldBe true
    intFrbtOfOne.none(matchLessThan(2)) shouldBe false
    intFrbtOfThree.none(matchLessThan(1)) shouldBe true
    intFrbtOfThree.none(matchLessThan(2)) shouldBe false
    intFrbtOfThree.none(matchLessThan(4)) shouldBe false
  }

  test("reduce") {

    // since order is not a property of Set, f MUST be commutative
    val ss = { acc: TKVEntry<Int, Int>, b: TKVEntry<Int, Int> -> (b.getv() + acc.getv()).toIAEntry() }

    shouldThrow<UnsupportedOperationException> {
      intFrbtOfNone.reduce(ss)
    }
    intFrbtOfNone.reduceOrNull(ss) shouldBe null
    intFrbtOfOne.reduce(ss) shouldBe 1.toIAEntry()
    intFrbtOfTwo.reduce(ss) shouldBe 3.toIAEntry()
    intFrbtOfTwoA.reduce(ss) shouldBe 4.toIAEntry()
    intFrbtOfTwoC.reduce(ss) shouldBe 5.toIAEntry()
    intFrbtOfThree.reduce(ss) shouldBe 6.toIAEntry()
    intFrbtOfThreeA.reduce(ss) shouldBe 8.toIAEntry()
  }

  // ignore
  // test("reduceIndexed") {}
  // test("reduceIndexedOrNull") {}

  test("runningFold") {
    shouldThrow<RuntimeException> {
      val ss = { acc: Int, b: TKVEntry<Int, Int> -> b.getv() + acc }
      @Suppress("DEPRECATION")
      FRBTree.ofvi(*arrayOf<Int>(2,1)).runningFold(1, ss)
    }
  }

  test("runningFoldIndexed") {
    shouldThrow<RuntimeException> {
      val ss = { index: Int, acc: Int, b: TKVEntry<Int,Int> -> b.getv() + acc + index }
      @Suppress("DEPRECATION")
      FRBTree.ofvi(*arrayOf<Int>(2,1)).runningFoldIndexed(1, ss)
    }
  }

  test("runningReduce") {
    shouldThrow<RuntimeException> {
      val ss = { _: TKVEntry<Int,Int>, _: TKVEntry<Int,Int> -> TKVEntry.of(0,0) }
      @Suppress("DEPRECATION")
      FRBTree.ofvi(*arrayOf<Int>(2,1)).runningReduce(ss)
    }
  }

  test("runningReduceIndexed") {
    shouldThrow<RuntimeException> {
      val ss = { index: Int, acc: TKVEntry<Int,Int>, b: TKVEntry<Int,Int> -> (b.getv() + acc.getv() + index).toIAEntry() }
      @Suppress("DEPRECATION")
      FRBTree.ofvi(*arrayOf<Int>(2,1)).runningReduceIndexed(ss)
    }
  }

  test("partition") {
    intFrbtOfOne.partition(matchLessThan(1)) shouldBe Pair(FLNil, FList.of(1.toIAEntry()))
    intFrbtOfThree.partition(matchLessThan(2)) shouldBe Pair(FList.of(1.toIAEntry()), FList.of(2.toIAEntry(), 3.toIAEntry()))
  }

  test("windowed") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FRBTree.ofvi(*arrayOf<Int>(2,1)).windowed(2)
    }
  }

  test("zip array") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FRBTree.ofvi(*arrayOf<Int>(2,1)).zip(arrayOf<String>()){a, b -> Pair(a,b)}
    }
  }

  test("zip iterable") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FRBTree.ofvi(*arrayOf<Int>(2,1)).zip(setOf<String>())
    }
  }

  test("zipWithNext") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FRBTree.ofvi(*arrayOf<Int>(2,1)).zipWithNext()
    }
  }

  test("zipWithNext transform") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FRBTree.ofvi(*arrayOf<Int>(2,1)).zipWithNext { a, b -> Pair(a, b) }
    }
  }
})
