package com.xrpn.kapi

import com.xrpn.bridge.FTreeIterator
import com.xrpn.immutable.TKVEntry
import com.xrpn.immutable.FBSTree
import com.xrpn.immutable.FRBTree
import com.xrpn.immutable.FSet
import com.xrpn.immutable.FList
import com.xrpn.immutable.FLNil
import com.xrpn.immutable.FList.Companion.toIMList
import com.xrpn.immutable.FBSTree.Companion.NOT_FOUND
import com.xrpn.immutable.TKVEntry.Companion.toIAEntries
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intFbstOfNone: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(*arrayOf<Int>())
private val intFbstOfOneA: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(*arrayOf<Int>(0))
private val intFbstOfOne: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(*arrayOf<Int>(1))
private val intFbstOfOneB: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(*arrayOf<Int>(2))
private val intFbstOfOneC: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(*arrayOf<Int>(3))
private val intFbstOfOneD: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(*arrayOf<Int>(4))
private val intFbstOfTwoA: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(*arrayOf<Int>(1,3))
private val intFbstOfTwo: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(*arrayOf<Int>(1,2))
private val intFbstOfTwoB: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(*arrayOf<Int>(0,2))
private val intFbstOfTwoC: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(*arrayOf<Int>(1,4))
private val intFbstOfThree: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(*arrayOf<Int>(1,2,3))
private val intFbstOfThreeA: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(*arrayOf<Int>(1,2,5))
private val intFbstOfFive: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(*arrayOf<Int>(1,2,3,4,5))
private val intFbstOfFiveA: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(*arrayOf<Int>(1,2,3,2,1))
private val intFbstOfSix: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(*arrayOf<Int>(1,2,3,3,2,1))
private val intFbstOf2: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(2)
private val intFbstOf3: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(3)
private val intFbstOf4: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(4)
private val intFbstOf5: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(5)
private val intFbstOf13: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(1, 3)
private val intFbstOf23: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(2, 3)
private val intFbstOf25: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(2, 5)
private val intFbstOf45: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(4, 5)
private val intFbstOf125: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(1, 2, 5)
private val intFbstOf145: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(1, 4, 5)
private val intFbstOf245: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(2, 4, 5)
private val intFbstOf1245: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(1, 2, 4, 5)
private val intFbstOf1235: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(1, 2, 3, 5)
private val intFbstOf1345: Collection<TKVEntry<Int, Int>> = FBSTree.ofvi(1, 3, 4, 5)


class FBSTreeKCollTest : FunSpec({

  beforeTest {}

  fun <Z: Comparable<Z>> matchEqual(oracle: Int): (TKVEntry<Int, Z>) -> Boolean = { aut: TKVEntry<Int, Z> -> oracle.toIAEntry() == aut }
  fun matchLessThan(oracle: Int): ((TKVEntry<Int, Int>)) -> Boolean = { aut: TKVEntry<Int, Int> -> aut.getv() < oracle }
  fun reverseNumerical(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = (-t.getv()).toIAEntry()

  // Any equals

  test("FTree equals") {
    (intFbstOfNone == FBSTree.ofvi(*arrayOf<Int>())) shouldBe true
    (intFbstOfNone == FRBTree.ofvi(*arrayOf())) shouldBe true
    (intFbstOfNone == emptySet<Int>()) shouldBe false
    (intFbstOfNone == FBSTree.ofvi(*arrayOf(1))) shouldBe false
    (intFbstOfNone == FRBTree.ofvi(*arrayOf(1))) shouldBe false
    (intFbstOfNone == setOf(1)) shouldBe false
    (intFbstOfNone == FRBTree.ofvi(*arrayOf(1))) shouldBe false
    (intFbstOfNone == setOf(1)) shouldBe false

    (intFbstOfOne == FBSTree.ofvi(*arrayOf<Int>())) shouldBe false
    (intFbstOfOne == FRBTree.ofvi(*arrayOf<Int>())) shouldBe false
    (intFbstOfOne == emptySet<Int>()) shouldBe false
    (intFbstOfOne == FBSTree.ofvi(*arrayOf(1))) shouldBe true
    (intFbstOfOne == FRBTree.ofvi(*arrayOf(1))) shouldBe true
    (intFbstOfOne == setOf(1.toIAEntry())) shouldBe false
    (intFbstOfOne == FBSTree.ofvi(*arrayOf(1, 2))) shouldBe false
  }

  test("FTree equals miss") {
    (intFbstOfOne.equals(FBSTree.ofvi(*arrayOf(2)))) shouldBe false
    (intFbstOfOne.equals(FBSTree.ofvi(*arrayOf(2)))) shouldBe false
    (intFbstOfTwo == setOf(1.toIAEntry(), 2.toIAEntry())) shouldBe false
    (intFbstOfTwo == FSet.of(1, 2)) shouldBe false
    (intFbstOfTwo == intFbstOfThree) shouldBe false
    (intFbstOfThree == setOf(1.toIAEntry(), 2.toIAEntry(), 3.toIAEntry())) shouldBe false
    (intFbstOfThree == FSet.of(1, 2, 3)) shouldBe false
  }

  test("Collections equals") {
    (emptySet<Int>() == intFbstOfOne) shouldBe false
    (emptySet<Int>() == intFbstOfOne.toSet()) shouldBe false
    (setOf(1.toIAEntry()) == intFbstOfOne) shouldBe false
    (setOf(1.toIAEntry()) == intFbstOfOne) shouldBe false
    (FSet.of(1) == intFbstOfOne) shouldBe false
    (setOf(1.toIAEntry()) == intFbstOfOne.toSet()) shouldBe true
    (setOf(1.toIAEntry(),2.toIAEntry()) == intFbstOfTwo.toSet()) shouldBe true
    (listOf(1.toIAEntry()) == intFbstOfOne.toList()) shouldBe true
    (listOf(1.toIAEntry(),2.toIAEntry()) == intFbstOfTwo.toList()) shouldBe true

    (emptySet<Int>() == intFbstOfNone) shouldBe false
    (emptySet<Int>() == intFbstOfNone.toSet()) shouldBe true
    (setOf(1.toIAEntry()) == intFbstOfNone.toSet()) shouldBe false
  }

  test("Collections equals miss") {
    (setOf(1.toIAEntry()) == intFbstOfOne) shouldBe false
    (listOf(1.toIAEntry()) == intFbstOfOne) shouldBe false
    (setOf(1.toIAEntry()) == intFbstOfTwo) shouldBe false
    (setOf(1.toIAEntry(), 2.toIAEntry()) == intFbstOfTwo) shouldBe false
    (listOf(1.toIAEntry(), 2.toIAEntry()) == intFbstOfTwo) shouldBe false
    (setOf(1.toIAEntry(), 2.toIAEntry()) == intFbstOfThree) shouldBe false
    (setOf(1.toIAEntry(), 2.toIAEntry(), 3.toIAEntry()) == intFbstOfThree) shouldBe false
    (listOf(1.toIAEntry(), 2.toIAEntry(), 3.toIAEntry()) == intFbstOfThree) shouldBe false
    (setOf(1.toIAEntry(), 3.toIAEntry(), 2.toIAEntry()) == intFbstOfThree) shouldBe false
  }

  // Collection -- methods or fields

  test("size") {
    intFbstOfNone.size shouldBe 0
    intFbstOfOne.size shouldBe 1
    intFbstOfTwo.size shouldBe 2
    intFbstOfThree.size shouldBe 3
  }

  test("isEmpty") {
    intFbstOfNone.isEmpty() shouldBe true
    intFbstOfOne.isEmpty() shouldBe false
    intFbstOfTwo.isEmpty() shouldBe false
    intFbstOfThree.isEmpty() shouldBe false
  }

  test("contains") {
    intFbstOfNone.contains(0.toIAEntry()) shouldBe false
    intFbstOfOne.contains(0.toIAEntry()) shouldBe false
    intFbstOfOne.contains(1.toIAEntry()) shouldBe true
    intFbstOfOne.contains(2.toIAEntry()) shouldBe false
    intFbstOfTwo.contains(0.toIAEntry()) shouldBe false
    intFbstOfTwo.contains(1.toIAEntry()) shouldBe true
    intFbstOfTwo.contains(2.toIAEntry()) shouldBe true
    intFbstOfTwo.contains(3.toIAEntry()) shouldBe false
  }

  test("containsAll") {
    intFbstOfNone.containsAll(intFbstOfNone) shouldBe true
    intFbstOfNone.containsAll(intFbstOfOne) shouldBe false

    intFbstOfOne.containsAll(intFbstOfNone) shouldBe true
    intFbstOfOne.containsAll(intFbstOfOne) shouldBe true
    intFbstOfOne.containsAll(intFbstOfTwo) shouldBe false

    intFbstOfTwo.containsAll(intFbstOfNone) shouldBe true
    intFbstOfTwo.containsAll(intFbstOfOne) shouldBe true
    intFbstOfTwo.containsAll(intFbstOfTwo) shouldBe true
    intFbstOfTwo.containsAll(intFbstOfThree) shouldBe false

    intFbstOfThree.containsAll(intFbstOfNone) shouldBe true
    intFbstOfThree.containsAll(intFbstOfOne) shouldBe true
    intFbstOfThree.containsAll(intFbstOfOneB) shouldBe true
    intFbstOfThree.containsAll(intFbstOfOneC) shouldBe true
    intFbstOfThree.containsAll(intFbstOfTwo) shouldBe true
    intFbstOfThree.containsAll(intFbstOfTwoA) shouldBe true
    intFbstOfThree.containsAll(intFbstOfOneA) shouldBe false
    intFbstOfThree.containsAll(intFbstOfOneD) shouldBe false
    intFbstOfThree.containsAll(intFbstOfTwoB) shouldBe false
    intFbstOfThree.containsAll(intFbstOfTwoC) shouldBe false
  }

  test("iteration order") {
    intFbstOfThree shouldBe (intFbstOfThree as FBSTree<Int, Int>).inorder()
    intFbstOfFive shouldBe (intFbstOfFive as FBSTree<Int, Int>).inorder()
  }

  // Iterator -- methods

  test("iterator a") {
    val i0 = intFbstOfNone.iterator()
    i0.hasNext() shouldBe false
    shouldThrow<NoSuchElementException> {
      i0.next()
    }

    val i1 = intFbstOfOne.iterator()
    i1.hasNext() shouldBe true
    i1.next() shouldBe 1.toIAEntry()
    i1.hasNext() shouldBe false
    shouldThrow<NoSuchElementException> {
      i1.next()
    }

    val i2 = intFbstOfTwo.iterator()
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
    val itr = intFbstOfFiveA.iterator() as FTreeIterator<Int, Int>
    for (i in (1..intFbstOfFiveA.size)) {
      val tmp = itr.nullableNext()
      tmp?.let { true } shouldBe true
    }
    (itr.nullableNext()?.let { false } ?: true) shouldBe true
  }

  // typeclass (Collection, Iterator, Iterable)

  test("first") {

    shouldThrow<NoSuchElementException> {
      intFbstOfNone.first()
    }
    intFbstOfNone.firstOrNull() shouldBe null

    intFbstOfOne.firstOrNull() shouldBe 1.toIAEntry()
    val aux = intFbstOfTwo.first()
    (aux == 1.toIAEntry() || aux == 2.toIAEntry()) shouldBe true
    (aux in intFbstOfTwo) shouldBe true
  }

  //  ignore
  //  test("firstNotNullOf") {}

  test("find first") {
    shouldThrow<NoSuchElementException> {
      intFbstOfNone.first(matchEqual(0))
    }
    intFbstOfNone.firstOrNull(matchEqual(0)) shouldBe null

    shouldThrow<NoSuchElementException> {
      intFbstOfOne.first(matchEqual(0))
    }
    intFbstOfOne.firstOrNull(matchEqual(0)) shouldBe null
    intFbstOfOne.firstOrNull(matchEqual(1)) shouldBe 1.toIAEntry()

    intFbstOfTwo.firstOrNull(matchEqual(0)) shouldBe null
    intFbstOfTwo.first(matchEqual(1)) shouldBe 1.toIAEntry()
    intFbstOfTwo.first(matchEqual(2)) shouldBe 2.toIAEntry()
    intFbstOfTwo.firstOrNull(matchEqual(3)) shouldBe null
  }

  test("indexOf") {
    intFbstOfNone.indexOf(0.toIAEntry()) shouldBe NOT_FOUND

    intFbstOfOne.indexOf(0.toIAEntry()) shouldBe NOT_FOUND
    intFbstOfOne.indexOf(1.toIAEntry()) shouldBe 0
    intFbstOfOne.indexOf(2.toIAEntry()) shouldBe NOT_FOUND

    intFbstOfTwo.indexOf(0.toIAEntry()) shouldBe NOT_FOUND
    val aux1 = intFbstOfTwo.indexOf(1.toIAEntry())
    (aux1 == 0 || aux1 == 1) shouldBe true
    val aux2 = intFbstOfTwo.indexOf(2.toIAEntry())
    ((aux2 == 0 || aux2 == 1) && (aux1 != aux2)) shouldBe true
    intFbstOfTwo.indexOf(3.toIAEntry()) shouldBe NOT_FOUND
  }

  test("indexOfirst") {
    intFbstOfNone.indexOfFirst(matchEqual(0)) shouldBe NOT_FOUND

    intFbstOfOne.indexOfFirst(matchEqual(0)) shouldBe NOT_FOUND
    intFbstOfOne.indexOfFirst(matchEqual(1)) shouldBe 0

    intFbstOfSix.indexOfFirst(matchEqual(0)) shouldBe NOT_FOUND
    val aux1 = intFbstOfSix.indexOfFirst(matchEqual(1))
    (aux1 in (0..intFbstOfSix.size)) shouldBe true
    val aux2 = intFbstOfSix.indexOfFirst(matchEqual(2))
    (aux2 in (0..intFbstOfSix.size) && aux1 != aux2) shouldBe true
    intFbstOfSix.indexOfFirst(matchEqual(30)) shouldBe NOT_FOUND
  }

  test("indexOfLast") {
    intFbstOfNone.indexOfLast(matchEqual(0)) shouldBe NOT_FOUND

    intFbstOfOne.indexOfLast(matchEqual(0)) shouldBe NOT_FOUND
    intFbstOfOne.indexOfLast(matchEqual(1)) shouldBe 0

    intFbstOfSix.indexOfLast(matchEqual(0)) shouldBe NOT_FOUND
    val aux1 = intFbstOfSix.indexOfLast(matchEqual(1))
    (aux1 in (0..intFbstOfSix.size)) shouldBe true
    val aux2 = intFbstOfSix.indexOfLast(matchEqual(2))
    (aux2 in (0..intFbstOfSix.size) && aux1 != aux2) shouldBe true
    intFbstOfSix.indexOfLast(matchEqual(30)) shouldBe NOT_FOUND
  }

  test("last") {
    shouldThrow<NoSuchElementException> {
      intFbstOfNone.last()
    }
    intFbstOfNone.lastOrNull() shouldBe null
    intFbstOfOne.lastOrNull() shouldBe 1.toIAEntry()
    intFbstOfOne.last() shouldBe 1.toIAEntry()
    val aux1 = intFbstOfTwo.last()
    (aux1 in intFbstOfTwo) shouldBe true
    val aux2 = intFbstOfThree.last()
    (aux2 in intFbstOfThree) shouldBe true
  }

  test("last (find)") {
    shouldThrow<NoSuchElementException> {
      intFbstOfNone.last(matchEqual(0))
    }
    intFbstOfNone.lastOrNull(matchEqual(0)) shouldBe null

    intFbstOfOne.lastOrNull(matchEqual(1)) shouldBe 1.toIAEntry()
    intFbstOfOne.last(matchEqual(1)) shouldBe 1.toIAEntry()
    intFbstOfOne.lastOrNull(matchEqual(2)) shouldBe null

    intFbstOfTwo.lastOrNull(matchEqual(0)) shouldBe null
    intFbstOfTwo.last(matchEqual(1)) shouldBe 1.toIAEntry()
    intFbstOfTwo.last(matchEqual(2)) shouldBe 2.toIAEntry()
    shouldThrow<NoSuchElementException> {
      intFbstOfTwo.last(matchEqual(3))
    }
    intFbstOfTwo.lastOrNull(matchEqual(3)) shouldBe null
  }

  test("lastIndexOf") {

    intFbstOfNone.lastIndexOf(0.toIAEntry()) shouldBe NOT_FOUND

    intFbstOfOne.lastIndexOf(0.toIAEntry()) shouldBe NOT_FOUND
    intFbstOfOne.lastIndexOf(1.toIAEntry()) shouldBe 0

    intFbstOfSix.lastIndexOf(0.toIAEntry()) shouldBe NOT_FOUND

    val aux1 = intFbstOfSix.lastIndexOf(1.toIAEntry())
    (aux1 in (0..intFbstOfSix.size)) shouldBe true
    val aux2 = intFbstOfSix.lastIndexOf(2.toIAEntry())
    (aux2 in (0..intFbstOfSix.size) && aux1 != aux2) shouldBe true
    intFbstOfSix.lastIndexOf(30.toIAEntry()) shouldBe NOT_FOUND
  }

  test("findlast") {
    intFbstOfNone.findLast(matchEqual(0)) shouldBe null

    intFbstOfOne.findLast(matchEqual(0)) shouldBe null
    intFbstOfOne.findLast(matchEqual(1)) shouldBe 1.toIAEntry()

    intFbstOfTwo.findLast(matchEqual(0)) shouldBe null
    intFbstOfTwo.findLast(matchEqual(1)) shouldBe 1.toIAEntry()
    intFbstOfTwo.findLast(matchEqual(2)) shouldBe 2.toIAEntry()
    intFbstOfTwo.findLast(matchEqual(3)) shouldBe null
  }

  test("single") {
    shouldThrow<NoSuchElementException> {
      intFbstOfNone.single()
    }
    intFbstOfNone.singleOrNull() shouldBe null

    intFbstOfOne.single() shouldBe 1.toIAEntry()
    intFbstOfOne.singleOrNull() shouldBe 1.toIAEntry()

    shouldThrow<IllegalArgumentException> {
      intFbstOfTwo.single()
    }
    intFbstOfTwo.singleOrNull() shouldBe null
  }

  test("single (find)") {
    shouldThrow<NoSuchElementException> {
      intFbstOfNone.single(matchEqual(0))
    }
    intFbstOfNone.singleOrNull(matchEqual(0)) shouldBe null

    intFbstOfOne.single(matchEqual(1)) shouldBe 1.toIAEntry()
    intFbstOfOne.singleOrNull(matchEqual(1)) shouldBe 1.toIAEntry()

    shouldThrow<NoSuchElementException> {
      intFbstOfTwo.single(matchEqual(0))
    }
    intFbstOfTwo.singleOrNull(matchEqual(0)) shouldBe null
    intFbstOfTwo.single(matchEqual(1)) shouldBe 1.toIAEntry()
    intFbstOfTwo.single(matchEqual(2)) shouldBe 2.toIAEntry()
    shouldThrow<NoSuchElementException> {
      intFbstOfTwo.single(matchEqual(3))
    }
    intFbstOfTwo.singleOrNull(matchEqual(3)) shouldBe null

    shouldThrow<NoSuchElementException> {
      intFbstOfTwo.single(matchEqual(0))
    }
    intFbstOfTwo.singleOrNull(matchEqual(0)) shouldBe null
    shouldThrow<NoSuchElementException> {
      intFbstOfTwo.single(matchEqual(3))
    }
    intFbstOfTwo.singleOrNull(matchEqual(3)) shouldBe null
  }

  test("drop 0") {
    intFbstOfNone.drop(0) shouldBe emptyList()
    intFbstOfOne.drop(0) shouldBe intFbstOfOne
    intFbstOfTwo.drop(0) shouldBe intFbstOfTwo
  }

  test("drop 1") {
    val aux0: List<TKVEntry<Int, Int>> = intFbstOfThree.drop(1)
    (aux0.size == intFbstOfThree.size - 1) shouldBe true
    intFbstOfThree.containsAll(aux0) shouldBe true
    intFbstOfNone.drop(1) shouldBe emptyList()
    intFbstOfOne.drop(1) shouldBe emptyList()
    val aux2 = intFbstOfTwo.drop(1).toSet()
    (aux2.size == intFbstOfTwo.size - 1) shouldBe true
    intFbstOfTwo.containsAll(aux2) shouldBe true
  }

  test("drop 2") {
    intFbstOfNone.drop(2) shouldBe emptyList()
    intFbstOfOne.drop(2) shouldBe emptyList()
    intFbstOfTwo.drop(2) shouldBe emptyList()

    val aux1 = intFbstOfThree.drop(2).toSet()
    (aux1.size == intFbstOfThree.size - 2) shouldBe true
    intFbstOfThree.containsAll(aux1) shouldBe true

    val a2 = FBSTree.ofvi(*arrayOf<Int>(1,2,3,4))
    val aux2 = a2.drop(2).toSet()
    (aux2.size == a2.size - 2) shouldBe true
    a2.containsAll(aux2) shouldBe true
  }

  test("drop 3") {
    intFbstOfNone.drop(3) shouldBe emptyList()
    intFbstOfOne.drop(3) shouldBe emptyList()
    intFbstOfTwo.drop(3) shouldBe emptyList()
    intFbstOfThree.drop(3) shouldBe emptyList()
    val a2 = FBSTree.ofvi(*arrayOf<Int>(1,2,3,4))
    val aux2 = a2.drop(3).toSet()
    (aux2.size == a2.size - 3) shouldBe true
    a2.containsAll(aux2) shouldBe true
  }

  test("dropWhile") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FBSTree.ofvi(*arrayOf<Int>(2,1)).dropWhile { it > 1.toIAEntry() }
    }
  }

  test("filter") {
    intFbstOfNone.filter {0 == it.getv() % 2} shouldBe emptyList()
    intFbstOfOne.filter {0 == it.getv() % 2} shouldBe emptyList()
    intFbstOfTwo.filter {0 == it.getv() % 2} shouldBe FBSTree.ofvi(2)
    intFbstOfThree.filter {0 == it.getv() % 2} shouldBe FBSTree.ofvi(2)
    FBSTree.ofvi(*arrayOf<Int>(1,2,3,4)).filter {0 == it.getv() % 2} shouldBe FBSTree.ofvi(2, 4)
  }

  //  ignore
  //  test("filter indexed") {}
  //  test("filterIsInstance") {}

  test("filterNot") {
    intFbstOfNone.filterNot {0 == it.getv() % 2} shouldBe emptyList()
    intFbstOfOne.filterNot {0 == it.getv() % 2} shouldBe FBSTree.ofvi(1)
    intFbstOfTwo.filterNot {0 == it.getv() % 2} shouldBe FBSTree.ofvi(1)
    intFbstOfThree.filterNot {0 == it.getv() % 2} shouldBe FBSTree.ofvi(1,3)
    FBSTree.ofvi(*arrayOf<Int>(1,2,3,4)).filterNot {0 == it.getv() % 2} shouldBe FBSTree.ofvi(1, 3)
  }

  //  ignore
  //  test("filterNotNull") {}

  test("take 0") {
    intFbstOfNone.take(0) shouldBe emptyList()
    intFbstOfOne.take(0) shouldBe emptyList()
    intFbstOfTwo.take(0) shouldBe emptyList()
  }

  test("take 1") {
    intFbstOfNone.take(1) shouldBe emptyList()
    intFbstOfOne.take(1) shouldBe intFbstOfOne

    val aux1 = intFbstOfTwo.take(1).toSet()
    aux1.size shouldBe 1
    intFbstOfTwo.containsAll(aux1) shouldBe true

    val aux2 = intFbstOfThree.take(1).toSet()
    aux2.size shouldBe 1
    intFbstOfThree.containsAll(aux2) shouldBe true
  }

  test("take 2") {
    intFbstOfNone.take(2) shouldBe emptyList()
    intFbstOfOne.take(2) shouldBe intFbstOfOne

    val aux1 = intFbstOfTwo.take(2).toSet()
    aux1.size shouldBe 2
    intFbstOfTwo.containsAll(aux1) shouldBe true

    val aux2 = intFbstOfThree.take(2).toSet()
    aux2.size shouldBe 2
    intFbstOfThree.containsAll(aux2) shouldBe true

    val a3 = FBSTree.ofvi(*arrayOf<Int>(1,2,3,4))
    val aux3 = a3.take(2)
    aux3.size shouldBe 2
    a3.containsAll(aux3) shouldBe true
  }

  test("take 3") {
    intFbstOfNone.take(3) shouldBe emptyList()
    intFbstOfOne.take(3) shouldBe intFbstOfOne
    intFbstOfTwo.take(3) shouldBe intFbstOfTwo
    intFbstOfThree.take(3) shouldBe intFbstOfThree
    val a3 = FBSTree.ofvi(*arrayOf<Int>(1,2,3,4))
    val aux3 = a3.take(3)
    aux3.size shouldBe 3
    a3.containsAll(aux3) shouldBe true
  }

  test("takeWhile") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FBSTree.ofvi(*arrayOf<Int>(2,1)).takeWhile { it.getv() > 1 }
    }
  }

  test("reversed") {
    intFbstOfNone.reversed() shouldBe emptyList()
    intFbstOfOne.reversed() shouldBe (intFbstOfOne as FBSTree<Int,Int>).inorder(reverse = true)
    intFbstOfTwo.reversed() shouldBe (intFbstOfTwo as FBSTree<Int,Int>).inorder(reverse = true)
    intFbstOfThree.reversed() shouldBe (intFbstOfThree as FBSTree<Int,Int>).inorder(reverse = true)
  }

  test("sorted") {
    intFbstOfNone.sorted().toSet() shouldBe emptyList()
    intFbstOfOne.sorted().toSet() shouldBe intFbstOfOne
    intFbstOfTwo.sorted().toSet() shouldBe intFbstOfTwo
    intFbstOfThree.sorted().toSet() shouldBe intFbstOfThree
    intFbstOfSix.sorted().toSet() shouldBe intFbstOfThree
  }

  test("sortedDescending") {
    intFbstOfNone.sortedDescending().toSet() shouldBe emptyList()
    intFbstOfOne.sortedDescending().toSet() shouldBe intFbstOfOne
    intFbstOfTwo.sortedDescending().toSet() shouldBe  intFbstOfTwo.toSet()
    intFbstOfThree.sortedDescending().toSet() shouldBe intFbstOfThree.toSet()
    intFbstOfSix.sortedDescending().toSet() shouldBe intFbstOfThree.toSet()
  }

  test("sortedBy") {

    intFbstOfNone.sortedBy(::reverseNumerical) shouldBe emptyList()
    intFbstOfOne.sortedBy(::reverseNumerical) shouldBe intFbstOfOne
    intFbstOfTwo.sortedBy(::reverseNumerical) shouldBe (intFbstOfTwo as FBSTree<Int, Int>).inorder(reverse = true)
    intFbstOfThree.sortedBy(::reverseNumerical) shouldBe (intFbstOfThree as FBSTree<Int, Int>).inorder(reverse = true)
    intFbstOfSix.sortedBy(::reverseNumerical) shouldBe intFbstOfThree.inorder(reverse = true)
  }

  test("sortedByDescending") {

    intFbstOfNone.sortedByDescending(::reverseNumerical) shouldBe emptyList()
    intFbstOfOne.sortedByDescending(::reverseNumerical) shouldBe intFbstOfOne
    intFbstOfTwo.sortedByDescending(::reverseNumerical) shouldBe intFbstOfTwo
    intFbstOfThree.sortedByDescending(::reverseNumerical) shouldBe intFbstOfThree
    intFbstOfSix.sortedByDescending(::reverseNumerical) shouldBe intFbstOfThree
  }

  test("sortedWith") {

    val reverseNumerical: Comparator<TKVEntry<Int, Int>> = Comparator { p0, p1 ->
      when {
        p0 == p1 -> 0
        p0 > p1 -> -1
        else -> 1
      }
    }

    intFbstOfNone.sortedWith(reverseNumerical) shouldBe emptyList()
    intFbstOfOne.sortedWith(reverseNumerical) shouldBe intFbstOfOne
    intFbstOfTwo.sortedWith(reverseNumerical) shouldBe (intFbstOfTwo as FBSTree<Int,Int>).inorder(reverse = true)
    intFbstOfThree.sortedWith(reverseNumerical) shouldBe (intFbstOfThree as FBSTree<Int,Int>).inorder(reverse = true)
    intFbstOfSix.sortedWith(reverseNumerical) shouldBe intFbstOfThree.inorder(reverse = true)
  }

  test("associate") {

    fun f(t: TKVEntry<Int, Int>): Pair<TKVEntry<Int, Int>, TKVEntry<Int, Int>> = Pair(t, (-t.getv()).toIAEntry())

    intFbstOfNone.associate(::f) shouldBe emptyMap()
    intFbstOfOne.associate(::f) shouldBe mapOf( 1.toIAEntry() to (-1).toIAEntry() )
    intFbstOfTwo.associate(::f) shouldBe mapOf( 1.toIAEntry() to (-1).toIAEntry(), 2.toIAEntry() to (-2).toIAEntry() )
    intFbstOfThree.associate(::f) shouldBe mapOf( 1.toIAEntry() to (-1).toIAEntry(), 2.toIAEntry() to (-2).toIAEntry(), 3.toIAEntry() to (-3).toIAEntry() )
  }

  test("associateBy") {

    fun f(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = (-t.getv()).toIAEntry()

    intFbstOfNone.associateBy(::f) shouldBe emptyMap()
    intFbstOfOne.associateBy(::f) shouldBe mapOf( (-1).toIAEntry() to 1.toIAEntry() )
    intFbstOfTwo.associateBy(::f) shouldBe mapOf( (-1).toIAEntry() to 1.toIAEntry(), (-2).toIAEntry() to 2.toIAEntry() )
    intFbstOfThree.associateBy(::f) shouldBe mapOf( (-1).toIAEntry() to 1.toIAEntry(), (-2).toIAEntry() to 2.toIAEntry(), (-3).toIAEntry() to 3.toIAEntry() )
  }

  test("associateBy (k, v)") {

    fun f(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = (-t.getv()).toIAEntry()
    fun g(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = (2*t.getv()).toIAEntry()

    intFbstOfNone.associateBy(::f, ::g) shouldBe emptyMap()
    intFbstOfOne.associateBy(::f, ::g) shouldBe mapOf( (-1).toIAEntry() to 2.toIAEntry() )
    intFbstOfTwo.associateBy(::f, ::g) shouldBe mapOf( (-1).toIAEntry() to 2.toIAEntry(), (-2).toIAEntry() to 4.toIAEntry() )
    intFbstOfThree.associateBy(::f, ::g) shouldBe mapOf( (-1).toIAEntry() to 2.toIAEntry(), (-2).toIAEntry() to 4.toIAEntry(), (-3).toIAEntry() to 6.toIAEntry() )
  }

  test("associateWith") {

    fun g(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = (2*t.getv()).toIAEntry()

    intFbstOfNone.associateWith(::g) shouldBe emptyMap()
    intFbstOfOne.associateWith(::g) shouldBe mapOf( 1.toIAEntry() to 2.toIAEntry() )
    intFbstOfTwo.associateWith(::g) shouldBe mapOf( 1.toIAEntry() to 2.toIAEntry(), 2.toIAEntry() to 4.toIAEntry() )
    intFbstOfThree.associateWith(::g) shouldBe mapOf( 1.toIAEntry() to 2.toIAEntry(), 2.toIAEntry() to 4.toIAEntry(), 3.toIAEntry() to 6.toIAEntry() )
  }

  test("flatMap") {
    intFbstOfNone.flatMap{ FBSTree.ofvi(it) } shouldBe emptyList()
    intFbstOfOne.flatMap{ FBSTree.ofvi(it) } shouldBe FBSTree.ofvi(1.toIAEntry())
    fun arrayBuilderConst(arg: Int) = Array(arg) { _ -> arg }
    intFbstOfTwo.flatMap {FBSTree.ofvi(*arrayBuilderConst(it.getv()))} shouldBe FBSTree.ofvi(1, 2)
    fun arrayBuilderIncrement(arg: Int) = Array(arg) { i -> (arg + i) }
    intFbstOfTwo.flatMap {FBSTree.ofvi(*arrayBuilderIncrement(it.getv()))} shouldBe FBSTree.ofvi(1, 2, 3)
    intFbstOfThree.flatMap {FBSTree.ofvi(*arrayBuilderIncrement(it.getv()))} shouldBe FList.of(1, 2, 3, 3, 4, 5).toIMList().toIAEntries()
    intFbstOfThree.flatMap { i -> FBSTree.ofvi(i, i) } shouldBe FBSTree.ofvi(1.toIAEntry(), 2.toIAEntry(), 3.toIAEntry(),)
  }

  // ignore
  // test("flatMapIndexed") {}

  test("groupBy") {

    fun f(t: TKVEntry<Int, Int>): Int = -t.getv()

    intFbstOfNone.groupBy(::f) shouldBe emptyMap()
    intFbstOfFiveA.groupBy(::f) shouldBe mapOf( -1 to setOf(1.toIAEntry(), 1.toIAEntry()), -2 to setOf(2.toIAEntry(), 2.toIAEntry()), -3 to setOf(3.toIAEntry()) )
  }

  test("groupBy (k, v)") {

    fun f(t: TKVEntry<Int, Int>): Int = -t.getv()
    fun g(t: TKVEntry<Int, Int>): Int = 2*t.getv()

    intFbstOfNone.groupBy(::f, ::g) shouldBe emptyMap()
    intFbstOfFiveA.groupBy(::f, ::g) shouldBe mapOf( -1 to setOf(2, 2), -2 to setOf(4,4), -3 to setOf(6) )
  }

  // TODO (maybe)
  // test("grouping") {
  //   fail("not implemented yet")
  // }

  test("map") {
    intFbstOfNone.map { it.getv() + 1} shouldBe emptyList()
    intFbstOfOne.map { it.getv() + 1} shouldBe FList.of(2)
    intFbstOfTwo.map { it.getv() + 1} shouldBe FList.of(2, 3)
  }

  // ignore
  // test("mapIndexed") {}
  // test("mapIndexedNotNull") {}
  // test("mapNotNull") {}

  test("withIndex") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FBSTree.ofvi(*arrayOf<Int>(2,1)).withIndex()
    }
  }

  test("distinct") {
    intFbstOfNone.distinct() shouldBe emptyList()
    intFbstOfOne.distinct() shouldBe intFbstOfOne
    intFbstOfTwo.distinct() shouldBe intFbstOfTwo
    intFbstOfFiveA.distinct() shouldBe intFbstOfThree
    intFbstOfSix.distinct() shouldBe intFbstOfThree
  }

  test("distinctBy") {

    fun identity(oracle: TKVEntry<Int, Int>) = oracle

    intFbstOfNone.distinctBy(::identity) shouldBe emptyList()
    intFbstOfOne.distinctBy(::identity) shouldBe intFbstOfOne
    intFbstOfTwo.distinctBy(::identity) shouldBe intFbstOfTwo
    intFbstOfFiveA.distinctBy(::identity) shouldBe intFbstOfThree
    intFbstOfSix.distinctBy(::identity) shouldBe intFbstOfThree
  }

   test("intersect") {
     intFbstOfNone.intersect(intFbstOfNone) shouldBe intFbstOfNone.toSet()

     intFbstOfOne.intersect(intFbstOfNone) shouldBe intFbstOfNone.toSet()
     intFbstOfTwo.intersect(intFbstOfNone) shouldBe intFbstOfNone.toSet()
     intFbstOfThree.intersect(intFbstOfNone) shouldBe intFbstOfNone.toSet()

     intFbstOfNone.intersect(intFbstOfOne) shouldBe intFbstOfNone.toSet()
     intFbstOfNone.intersect(intFbstOfTwo) shouldBe intFbstOfNone.toSet()
     intFbstOfNone.intersect(intFbstOfThree) shouldBe intFbstOfNone.toSet()

     intFbstOfOne.intersect(intFbstOfOne) shouldBe intFbstOfOne.toSet()
     intFbstOfTwo.intersect(intFbstOfTwo) shouldBe intFbstOfTwo.toSet()
     intFbstOfThree.intersect(intFbstOfThree) shouldBe intFbstOfThree.toSet()

     intFbstOfTwo.intersect(intFbstOfOne) shouldBe intFbstOfOne.toSet()
     intFbstOfOne.intersect(intFbstOfTwo) shouldBe intFbstOfOne.toSet()

     intFbstOfOne.intersect(intFbstOfThree) shouldBe intFbstOfOne.toSet()
     intFbstOfOne.intersect(intFbstOf13) shouldBe intFbstOfOne.toSet()
     intFbstOfOne.intersect(intFbstOf25) shouldBe intFbstOfNone.toSet()
     intFbstOfOne.intersect(intFbstOf45) shouldBe intFbstOfNone.toSet()

     intFbstOfTwo.intersect(intFbstOfThree) shouldBe intFbstOfTwo.toSet()
     intFbstOfTwo.intersect(intFbstOf13) shouldBe intFbstOfOne.toSet()
     intFbstOfTwo.intersect(intFbstOf25) shouldBe intFbstOf2.toSet()
     intFbstOfTwo.intersect(intFbstOf45) shouldBe intFbstOfNone.toSet()

     intFbstOf13.intersect(intFbstOfOne) shouldBe intFbstOfOne.toSet()
     intFbstOf13.intersect(intFbstOfTwo) shouldBe intFbstOfOne.toSet()
     intFbstOf13.intersect(intFbstOfThree) shouldBe intFbstOf13.toSet()
     intFbstOf13.intersect(intFbstOf25) shouldBe intFbstOfNone.toSet()
     intFbstOf13.intersect(intFbstOf45) shouldBe intFbstOfNone.toSet()

     intFbstOf25.intersect(intFbstOfOne) shouldBe intFbstOfNone.toSet()
     intFbstOf25.intersect(intFbstOfTwo) shouldBe intFbstOf2.toSet()
     intFbstOf25.intersect(intFbstOfThree) shouldBe intFbstOf2.toSet()
     intFbstOf25.intersect(intFbstOf13) shouldBe intFbstOfNone.toSet()
     intFbstOf25.intersect(intFbstOf45) shouldBe intFbstOf5.toSet()

     intFbstOf45.intersect(intFbstOfOne) shouldBe intFbstOfNone.toSet()
     intFbstOf45.intersect(intFbstOfTwo) shouldBe intFbstOfNone.toSet()
     intFbstOf45.intersect(intFbstOfThree) shouldBe intFbstOfNone.toSet()
     intFbstOf45.intersect(intFbstOf13) shouldBe intFbstOfNone.toSet()
     intFbstOf45.intersect(intFbstOf25) shouldBe intFbstOf5.toSet()

     intFbstOfThree.intersect(intFbstOfOne) shouldBe intFbstOfOne.toSet()
     intFbstOfThree.intersect(intFbstOfTwo) shouldBe intFbstOfTwo.toSet()
     intFbstOfThree.intersect(intFbstOf13) shouldBe intFbstOf13.toSet()
     intFbstOfThree.intersect(intFbstOf25) shouldBe intFbstOf2.toSet()
     intFbstOfThree.intersect(intFbstOf45) shouldBe intFbstOfNone.toSet()
   }

   test("subtract") {
     intFbstOfNone.subtract(intFbstOfNone) shouldBe intFbstOfNone.toSet()

     intFbstOfOne.subtract(intFbstOfNone) shouldBe intFbstOfOne.toSet()
     intFbstOfTwo.subtract(intFbstOfNone) shouldBe intFbstOfTwo.toSet()
     intFbstOfThree.subtract(intFbstOfNone) shouldBe intFbstOfThree.toSet()

     intFbstOfNone.subtract(intFbstOfOne) shouldBe intFbstOfNone.toSet()
     intFbstOfNone.subtract(intFbstOfTwo) shouldBe intFbstOfNone.toSet()
     intFbstOfNone.subtract(intFbstOfThree) shouldBe intFbstOfNone.toSet()

     intFbstOfOne.subtract(intFbstOfOne) shouldBe intFbstOfNone.toSet()
     intFbstOfTwo.subtract(intFbstOfTwo) shouldBe intFbstOfNone.toSet()
     intFbstOfThree.subtract(intFbstOfThree) shouldBe intFbstOfNone.toSet()

     intFbstOfTwo.subtract(intFbstOfOne) shouldBe intFbstOf2.toSet()
     intFbstOfOne.subtract(intFbstOfTwo) shouldBe intFbstOfNone.toSet()

     intFbstOfOne.subtract(intFbstOfThree) shouldBe intFbstOfNone.toSet()
     intFbstOfOne.subtract(intFbstOf13) shouldBe intFbstOfNone.toSet()
     intFbstOfOne.subtract(intFbstOf25) shouldBe intFbstOfOne.toSet()
     intFbstOfOne.subtract(intFbstOf45) shouldBe intFbstOfOne.toSet()

     intFbstOfTwo.subtract(intFbstOfThree) shouldBe intFbstOfNone.toSet()
     intFbstOfTwo.subtract(intFbstOf13) shouldBe intFbstOf2.toSet()
     intFbstOfTwo.subtract(intFbstOf25) shouldBe intFbstOfOne.toSet()
     intFbstOfTwo.subtract(intFbstOf45) shouldBe intFbstOfTwo.toSet()

     intFbstOf13.subtract(intFbstOfOne) shouldBe intFbstOf3.toSet()
     intFbstOf13.subtract(intFbstOfTwo) shouldBe intFbstOf3.toSet()
     intFbstOf13.subtract(intFbstOfThree) shouldBe intFbstOfNone.toSet()
     intFbstOf13.subtract(intFbstOf25) shouldBe intFbstOf13.toSet()
     intFbstOf13.subtract(intFbstOf45) shouldBe intFbstOf13.toSet()

     intFbstOf25.subtract(intFbstOfOne) shouldBe intFbstOf25.toSet()
     intFbstOf25.subtract(intFbstOfTwo) shouldBe intFbstOf5.toSet()
     intFbstOf25.subtract(intFbstOfThree) shouldBe intFbstOf5.toSet()
     intFbstOf25.subtract(intFbstOf13) shouldBe intFbstOf25.toSet()
     intFbstOf25.subtract(intFbstOf45) shouldBe intFbstOf2.toSet()

     intFbstOf45.subtract(intFbstOfOne) shouldBe intFbstOf45.toSet()
     intFbstOf45.subtract(intFbstOfTwo) shouldBe intFbstOf45.toSet()
     intFbstOf45.subtract(intFbstOfThree) shouldBe intFbstOf45.toSet()
     intFbstOf45.subtract(intFbstOf13) shouldBe intFbstOf45.toSet()
     intFbstOf45.subtract(intFbstOf25) shouldBe intFbstOf4.toSet()

     intFbstOfThree.subtract(intFbstOfOne) shouldBe intFbstOf23.toSet()
     intFbstOfThree.subtract(intFbstOfTwo) shouldBe intFbstOf3.toSet()
     intFbstOfThree.subtract(intFbstOf13) shouldBe intFbstOf2.toSet()
     intFbstOfThree.subtract(intFbstOf25) shouldBe intFbstOf13.toSet()
     intFbstOfThree.subtract(intFbstOf45) shouldBe intFbstOfThree.toSet()
   }

  test("union") {
    intFbstOfNone.union(intFbstOfNone) shouldBe intFbstOfNone.toSet()

    intFbstOfOne.union(intFbstOfNone) shouldBe intFbstOfOne.toSet()
    intFbstOfTwo.union(intFbstOfNone) shouldBe intFbstOfTwo.toSet()
    intFbstOfThree.union(intFbstOfNone) shouldBe intFbstOfThree.toSet()

    intFbstOfNone.union(intFbstOfOne) shouldBe intFbstOfOne.toSet()
    intFbstOfNone.union(intFbstOfTwo) shouldBe intFbstOfTwo.toSet()
    intFbstOfNone.union(intFbstOfThree) shouldBe intFbstOfThree.toSet()

    intFbstOfOne.union(intFbstOfOne) shouldBe intFbstOfOne.toSet()
    intFbstOfTwo.union(intFbstOfTwo) shouldBe intFbstOfTwo.toSet()
    intFbstOfThree.union(intFbstOfThree) shouldBe intFbstOfThree.toSet()

    intFbstOfTwo.union(intFbstOfOne) shouldBe intFbstOfTwo.toSet()
    intFbstOfOne.union(intFbstOfTwo) shouldBe intFbstOfTwo.toSet()

    intFbstOfOne.union(intFbstOfThree) shouldBe intFbstOfThree.toSet()
    intFbstOfOne.union(intFbstOf13) shouldBe intFbstOf13.toSet()
    intFbstOfOne.union(intFbstOf25) shouldBe intFbstOf125.toSet()
    intFbstOfOne.union(intFbstOf45) shouldBe intFbstOf145.toSet()

    intFbstOfTwo.union(intFbstOfThree) shouldBe intFbstOfThree.toSet()
    intFbstOfTwo.union(intFbstOf13) shouldBe intFbstOfThree.toSet()
    intFbstOfTwo.union(intFbstOf25) shouldBe intFbstOf125.toSet()
    intFbstOfTwo.union(intFbstOf45) shouldBe intFbstOf1245.toSet()

    intFbstOf13.union(intFbstOfOne) shouldBe intFbstOf13.toSet()
    intFbstOf13.union(intFbstOfTwo) shouldBe intFbstOfThree.toSet()
    intFbstOf13.union(intFbstOfThree) shouldBe intFbstOfThree.toSet()
    intFbstOf13.union(intFbstOf25) shouldBe intFbstOf1235.toSet()
    intFbstOf13.union(intFbstOf45) shouldBe intFbstOf1345.toSet()

    intFbstOf25.union(intFbstOfOne) shouldBe intFbstOf125.toSet()
    intFbstOf25.union(intFbstOfTwo) shouldBe intFbstOf125.toSet()
    intFbstOf25.union(intFbstOfThree) shouldBe intFbstOf1235.toSet()
    intFbstOf25.union(intFbstOf13) shouldBe intFbstOf1235.toSet()
    intFbstOf25.union(intFbstOf45) shouldBe intFbstOf245.toSet()

    intFbstOf45.union(intFbstOfOne) shouldBe intFbstOf145.toSet()
    intFbstOf45.union(intFbstOfTwo) shouldBe intFbstOf1245.toSet()
    intFbstOf45.union(intFbstOfThree) shouldBe intFbstOfFive.toSet()
    intFbstOf45.union(intFbstOf13) shouldBe intFbstOf1345.toSet()
    intFbstOf45.union(intFbstOf25) shouldBe intFbstOf245.toSet()

    intFbstOfThree.union(intFbstOfOne) shouldBe intFbstOfThree.toSet()
    intFbstOfThree.union(intFbstOfTwo) shouldBe intFbstOfThree.toSet()
    intFbstOfThree.union(intFbstOf13) shouldBe intFbstOfThree.toSet()
    intFbstOfThree.union(intFbstOf25) shouldBe intFbstOf1235.toSet()
    intFbstOfThree.union(intFbstOf45) shouldBe intFbstOfFive.toSet()
  }

  test("all") {
    intFbstOfNone.all(matchLessThan(0)) shouldBe true // by vacuous implication
    intFbstOfOne.all(matchLessThan(1)) shouldBe false
    intFbstOfOne.all(matchLessThan(2)) shouldBe true
    intFbstOfThree.all(matchLessThan(2)) shouldBe false
    intFbstOfThree.all(matchLessThan(4)) shouldBe true
  }

  test("any") {
    intFbstOfNone.any(matchLessThan(0)) shouldBe false
    intFbstOfOne.any(matchLessThan(1)) shouldBe false
    intFbstOfOne.any(matchLessThan(2)) shouldBe true
    intFbstOfThree.any(matchLessThan(1)) shouldBe false
    intFbstOfThree.any(matchLessThan(2)) shouldBe true
    intFbstOfThree.any(matchLessThan(4)) shouldBe true
  }

  test("(has) any") {
    intFbstOfNone.any() shouldBe false
    intFbstOfOne.any() shouldBe true
    intFbstOfThree.any() shouldBe true
  }

  test("count") {
    intFbstOfNone.count() shouldBe 0
    intFbstOfOne.count() shouldBe 1
    intFbstOfThree.count() shouldBe 3
  }

  test("count matching") {
    intFbstOfNone.count(matchEqual(0)) shouldBe 0
    intFbstOfFiveA.count(matchEqual(3)) shouldBe 1
    intFbstOfSix.count(matchEqual(3)) shouldBe 1
  }

  test("fold") {

    val s = { acc: Int, b: TKVEntry<Int, Int> -> acc - b.getv() }

    intFbstOfNone.fold(1, s) shouldBe 1
    intFbstOfOne.fold(1, s) shouldBe 0
    intFbstOfTwo.fold(1, s) shouldBe -2
    intFbstOfTwoA.fold(1, s) shouldBe -3
    intFbstOfThree.fold(1, s) shouldBe -5
    intFbstOfThreeA.fold(1, s) shouldBe -7
  }

  // ignore
  // test("foldIndexed") {}

  test("(has) none") {
    intFbstOfNone.none() shouldBe true
    intFbstOfOne.none() shouldBe false
    intFbstOfThree.none() shouldBe false
  }

  test("none") {
    intFbstOfNone.none(matchLessThan(0)) shouldBe true
    intFbstOfOne.none(matchLessThan(1)) shouldBe true
    intFbstOfOne.none(matchLessThan(2)) shouldBe false
    intFbstOfThree.none(matchLessThan(1)) shouldBe true
    intFbstOfThree.none(matchLessThan(2)) shouldBe false
    intFbstOfThree.none(matchLessThan(4)) shouldBe false
  }

  test("reduce") {

    // since order is not a property of Set, f MUST be commutative
    val ss = { acc: TKVEntry<Int, Int>, b: TKVEntry<Int, Int> -> (b.getv() + acc.getv()).toIAEntry() }

    shouldThrow<UnsupportedOperationException> {
      intFbstOfNone.reduce(ss)
    }
    intFbstOfNone.reduceOrNull(ss) shouldBe null
    intFbstOfOne.reduce(ss) shouldBe 1.toIAEntry()
    intFbstOfTwo.reduce(ss) shouldBe 3.toIAEntry()
    intFbstOfTwoA.reduce(ss) shouldBe 4.toIAEntry()
    intFbstOfTwoC.reduce(ss) shouldBe 5.toIAEntry()
    intFbstOfThree.reduce(ss) shouldBe 6.toIAEntry()
    intFbstOfThreeA.reduce(ss) shouldBe 8.toIAEntry()
  }

  // ignore
  // test("reduceIndexed") {}
  // test("reduceIndexedOrNull") {}

  test("runningFold") {
    shouldThrow<RuntimeException> {
      val ss = { acc: Int, b: TKVEntry<Int, Int> -> b.getv() + acc }
      @Suppress("DEPRECATION")
      FBSTree.ofvi(*arrayOf<Int>(2,1)).runningFold(1, ss)
    }
  }

  test("runningFoldIndexed") {
    shouldThrow<RuntimeException> {
      val ss = { index: Int, acc: Int, b: TKVEntry<Int,Int> -> b.getv() + acc + index }
      @Suppress("DEPRECATION")
      FBSTree.ofvi(*arrayOf<Int>(2,1)).runningFoldIndexed(1, ss)
    }
  }

  test("runningReduce") {
    shouldThrow<RuntimeException> {
      val ss = { _: TKVEntry<Int,Int>, _: TKVEntry<Int,Int> -> TKVEntry.of(0,0) }
      @Suppress("DEPRECATION")
      FBSTree.ofvi(*arrayOf<Int>(2,1)).runningReduce(ss)
    }
  }

  test("runningReduceIndexed") {
    shouldThrow<RuntimeException> {
      val ss = { index: Int, acc: TKVEntry<Int,Int>, b: TKVEntry<Int,Int> -> (b.getv() + acc.getv() + index).toIAEntry() }
      @Suppress("DEPRECATION")
      FBSTree.ofvi(*arrayOf<Int>(2,1)).runningReduceIndexed(ss)
    }
  }

  test("partition") {
    intFbstOfOne.partition(matchLessThan(1)) shouldBe Pair(FLNil, FList.of(1.toIAEntry()))
    intFbstOfThree.partition(matchLessThan(2)) shouldBe Pair(FList.of(1.toIAEntry()), FList.of(2.toIAEntry(), 3.toIAEntry()))
  }

  test("windowed") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FBSTree.ofvi(*arrayOf<Int>(2,1)).windowed(2)
    }
  }

  test("zip array") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FBSTree.ofvi(*arrayOf<Int>(2,1)).zip(arrayOf<String>()){a, b -> Pair(a,b)}
    }
  }

  test("zip iterable") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FBSTree.ofvi(*arrayOf<Int>(2,1)).zip(setOf<String>())
    }
  }

  test("zipWithNext") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FBSTree.ofvi(*arrayOf<Int>(2,1)).zipWithNext()
    }
  }

  test("zipWithNext transform") {
    shouldThrow<RuntimeException> {
      @Suppress("DEPRECATION")
      FBSTree.ofvi(*arrayOf<Int>(2,1)).zipWithNext { a, b -> Pair(a, b) }
    }
  }
})
