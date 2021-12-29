package com.xrpn.kapi

import com.xrpn.bridge.FTreeIterator
import com.xrpn.immutable.*
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.xrpn.frbtreeAsCollection

class FRBTreeKCollPropertyTest : FunSpec({

  beforeTest {}

  val repeats = 10
  fun <Z: Comparable<Z>> matchEqual(oracle: TKVEntry<Int, Z>): (TKVEntry<Int, Z>) -> Boolean = { aut: TKVEntry<Int, Z> -> oracle == aut }
  fun <Z: Comparable<Z>> matchLessThan(oracle: TKVEntry<Int, Z>): ((TKVEntry<Int, Z>)) -> Boolean = { aut: TKVEntry<Int, Z> -> oracle.getv() < aut.getv() }
  fun middle(l: Collection<TKVEntry<Int, Int>>): TKVEntry<Int, Int> {
    val max = l.map { tkv -> tkv.getv() }.maxOrNull()
    val min = l.map { tkv -> tkv.getv() }.minOrNull()
    return ((max!! - min!!) / 2).toIAEntry()
  }
  fun reverseNumerical(t: TKVEntry<Int, Int>): TKVEntry<Int, Int>? = (-t.getv()).toIAEntry()

  test("equals") {
    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      (frbt.size >= 0) shouldBe true
      (frbt == frbt) shouldBe true
    }
    Arb.frbtreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { frbt ->
      (frbt.size >= 0) shouldBe true
      (frbt == frbt) shouldBe true
    }
    Arb.frbtreeAsCollection<String, String>(Arb.string(0..10)).checkAll(repeats) { frbt ->
      (frbt.size >= 0) shouldBe true
      (frbt == frbt) shouldBe true
    }
  }

  test("contains, all, any") {
    Arb.frbtreeAsCollection<String, String>(Arb.string(0..10)).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      l.all { s -> frbt.contains(s) } shouldBe true
      l.any { s -> frbt.contains(s) } shouldBe true
    }
  }

  test("containsAll") {
    Arb.frbtreeAsCollection<String, String>(Arb.string(0..10)).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.containsAll(l.reversed()) shouldBe true
    }
  }

  test("find (first)") {
    Arb.frbtreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val ix = kotlin.random.Random.Default.nextInt(frbt.size)
      val selection: TKVEntry<Int, Char> = l[ix]
      frbt.find(matchEqual(selection)) shouldBe selection
    }
  }

  test("indexOf") {
    Arb.frbtreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val ix = kotlin.random.Random.Default.nextInt(frbt.size)
      val selection = l[ix]
      frbt.indexOf(selection) shouldBe l.indexOfFirst(matchEqual(selection))
    }
  }

  test("indexOfirst") {
    Arb.frbtreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val ix = kotlin.random.Random.Default.nextInt(frbt.size)
      val selection = l[ix]
      frbt.indexOfFirst(matchEqual(selection)) shouldBe l.indexOfFirst(matchEqual(selection))
    }
  }

  test("indexOfLast") {
    Arb.frbtreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val ix = kotlin.random.Random.Default.nextInt(frbt.size)
      val selection = l[ix]
      frbt.indexOfLast(matchEqual(selection)) shouldBe l.indexOfLast(matchEqual(selection))
    }
  }

  test("last (find)") {
    Arb.frbtreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val ix = kotlin.random.Random.Default.nextInt(frbt.size)
      val selection = l[ix]
      frbt.last(matchEqual(selection)) shouldBe selection
    }
  }

  test("lastIndexOf") {
    Arb.frbtreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val ix = kotlin.random.Random.Default.nextInt(frbt.size)
      val selection = l[ix]
      frbt.lastIndexOf(selection) shouldBe l.indexOfLast(matchEqual(selection))
    }
  }

  test("findlast") {
    Arb.frbtreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val ix = kotlin.random.Random.Default.nextInt(frbt.size)
      val selection = l[ix]
      frbt.findLast(matchEqual(selection)) shouldBe selection
    }
  }

  test("drop") {
    Arb.frbtreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val n = kotlin.random.Random.Default.nextInt(frbt.size)
      frbt.drop(n) shouldBe l.drop(n)
    }
  }

  test("dropWhile") {
    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val ora = middle(l)
      frbt.dropWhile(matchLessThan(ora)) shouldBe l.dropWhile(matchLessThan(ora))
    }
  }

  test("filter") {
    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val ora = middle(l)
      frbt.filter(matchLessThan(ora)) shouldBe l.filter(matchLessThan(ora))
    }
  }

  test("filterNot") {

    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val ora = middle(l)
      frbt.filterNot(matchLessThan(ora)) shouldBe l.filterNot(matchLessThan(ora))
    }
  }

  test("take") {
    Arb.frbtreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val n = kotlin.random.Random.Default.nextInt(frbt.size)
      frbt.take(n) shouldBe l.take(n)
    }
  }

  test("takeWhile") {
    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val ora = middle(l)
      frbt.takeWhile(matchLessThan(ora)) shouldBe l.takeWhile(matchLessThan(ora))
    }
  }

  test("reversed") {
    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.reversed() shouldBe l.reversed()
    }
  }

  test("sorted") {
    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.sorted() shouldBe l.sorted()
    }
  }

  test("sortedDescending") {
    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.sortedDescending() shouldBe l.sortedDescending()
    }
  }

  test("sortedBy") {
    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.sortedBy(::reverseNumerical) shouldBe l.sortedBy(::reverseNumerical)
    }
  }

  test("sortedByDescending") {
    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.sortedByDescending(::reverseNumerical) shouldBe l.sortedByDescending(::reverseNumerical)
    }
  }

  test("sortedWith") {

    val reverseNumCo: Comparator<TKVEntry<Int, Int>> = Comparator<TKVEntry<Int, Int>> { p0, p1 ->
      when {
        p0.getv() == p1.getv() -> 0
        p0.getv() > p1.getv() -> -1
        else -> 1
      }
    }

    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.sortedWith(reverseNumCo) shouldBe l.sortedWith(reverseNumCo)
    }
  }

  test("associate") {

    fun f(t: TKVEntry<Int, Int>): Pair<TKVEntry<Int, Int>, TKVEntry<Int, Int>> = Pair(t.getv().toIAEntry(), (-t.getv()).toIAEntry())

    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.associate(::f) shouldBe l.associate(::f)
    }
  }

  test("associateBy") {

    fun f(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), -t.getv())

    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.associateBy(::f) shouldBe l.associateBy(::f)
    }
  }

  test("associateBy (k, v)") {

    fun f(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), -t.getv())
    fun g(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), 2*t.getv())

    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.associateBy(::f, ::g) shouldBe l.associateBy(::f, ::g)
    }
  }

  test("associateWith") {

    fun g(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), 2*t.getv())

    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.associateWith(::g) shouldBe l.associateWith(::g)
    }
  }

  test("flatMap") {
    checkAll(5, Arb.frbtreeAsCollection<Int, Int>(Arb.int()), Arb.frbtreeAsCollection<Int, Int>(Arb.int())) { frbt1, frbt2  ->
      val l1: List<TKVEntry<Int, Int>> = frbt1.toList()
      val l2: List<TKVEntry<Int, Int>> = frbt2.toList()
      val autFl = frbt1.flatMap { outIt -> l2.map { inIt -> (inIt.getv() + outIt.getv()).toIAEntry() } }
      val autL = l1.flatMap { outIt -> l2.map { inIt -> (inIt.getv() + outIt.getv()).toIAEntry() } }
      autFl shouldBe autL
    }
  }

  test("groupBy") {

    fun f(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), -t.getv())

    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.groupBy(::f) shouldBe l.groupBy(::f)
    }
  }

  test("groupBy (k, v)") {

    fun f(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), -t.getv())
    fun g(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), 2*t.getv())

    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.groupBy(::f, ::g) shouldBe l.groupBy(::f, ::g)
    }
  }

  // TODO (maybe)
  // test("grouping") {
  //   fail("not implemented yet")
  // }

  test("map") {

    fun f(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), t.getv()+1)

    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.map(::f) shouldBe l.map(::f)
    }
  }

  test("distinct") {
    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.distinct() shouldBe l.distinct()
    }
  }

  test("distinctBy") {

    fun identity(oracle: TKVEntry<Int, Int>) = oracle

    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.distinctBy(::identity) shouldBe l.distinctBy(::identity)
    }
  }

  test("count") {
    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.count() shouldBe l.size
    }
  }

  test("count matching") {
    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val ora = middle(l)
      frbt.count(matchEqual(ora)) shouldBe l.count(matchEqual(ora))
    }
  }

  test("fold") {

    val f = { acc: TKVEntry<Int, Int>, b: TKVEntry<Int, Int> -> (acc.getk() - b.getv()).toIAEntry() }

    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.fold(1.toIAEntry(), f) shouldBe l.fold(1.toIAEntry(), f)
    }
  }

  test("none") {
    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val ora = (middle(l).getv() * 2).toIAEntry()
      frbt.none(matchLessThan(ora)) shouldBe l.none(matchLessThan(ora))
    }
  }

  test("reduce") {

    val f = { b:TKVEntry<Int, Int>, a: TKVEntry<Int, Int> -> (a.getv() - b.getv()).toIAEntry() }

    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.reduce(f) shouldBe l.reduce(f)
    }
  }

  test("runningFold") {
    val f = { acc: TKVEntry<Int, Int>, b: TKVEntry<Int, Int> -> (acc.getv() - b.getv()).toIAEntry() }

    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.runningFold(1.toIAEntry(), f) shouldBe l.runningFold(1.toIAEntry(), f)
    }
  }

  test("runningReduce") {
    val f = { b: TKVEntry<Int, Int>, a: TKVEntry<Int, Int> -> (a.getv() - b.getv()).toIAEntry() }

    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.runningReduce(f) shouldBe l.runningReduce(f)
    }
  }

  test("partition") {
    Arb.frbtreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      frbt.partition(matchLessThan(1.toIAEntry())) shouldBe l.partition(matchLessThan(1.toIAEntry()))
    }
  }

  test("windowed") {
    // Arb.frbtreeAsCollection<Char, Char>(Arb.char()).checkAll(PropTestConfig(iterations = 1, seed = -2438971832224874878)) { frbt ->
    Arb.frbtreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { frbt ->
      val l = frbt.toList()
      val shortSize = kotlin.random.Random.Default.nextInt(frbt.size)
      for (sz in (shortSize downTo 1 step 5)) {
        val shortStep = kotlin.random.Random.Default.nextInt(sz)
        val partial = 0 == shortStep % 2
        // frbt.windowed(shortSize, shortStep, partial) shouldBe l.windowed(shortSize, shortStep, partial)
        frbt.windowed(sz, if (0 == shortStep) 1 else shortStep, partial) shouldBe l.windowed(sz, if (0 == shortStep) 1 else shortStep, partial)
      }
    }
  }

  test("zip array") {
    checkAll(5, Arb.frbtreeAsCollection<Int, Int>(Arb.int()), Arb.frbtreeAsCollection<Int, Int>(Arb.int())) { frbt1, frbt2  ->
      val l1: List<TKVEntry<Int, Int>> = frbt1.toList()
      val a2: Array<TKVEntry<Int, Int>> = FRBTree.toArray( (frbt2.iterator() as FTreeIterator<Int, Int>).retriever.original() as FRBTree<Int,Int> )
      frbt1.zip (a2) shouldBe l1.zip(a2)
    }
  }

  test("zip iterable") {
    checkAll(5, Arb.frbtreeAsCollection<Int, Int>(Arb.int()), Arb.frbtreeAsCollection<Int, Int>(Arb.int())) { frbt1, frbt2  ->
      val l1: List<TKVEntry<Int, Int>> = frbt1.toList()
      val l2: List<TKVEntry<Int, Int>> = frbt2.toList()
      frbt1.zip (l2) shouldBe l1.zip(l2)
    }
  }
})
