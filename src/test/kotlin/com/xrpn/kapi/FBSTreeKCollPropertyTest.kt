package com.xrpn.kapi

import com.xrpn.immutable.*
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.xrpn.fbstreeAsCollection

class FBSTreeKCollPropertyTest : FunSpec({

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
    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats, PropTestConfig(seed = 760386855994704737)) { fbst ->
      val l = FRBTree.of(fbst.iterator())
      (fbst.size >= 0) shouldBe true
      (fbst == fbst) shouldBe true
      if (l.size == fbst.size) {
        (fbst.equals(l)) shouldBe true
        (l.equals(fbst)) shouldBe true
      } else {
        val noDups = FBSTree.of(l.preorder())
        (l.equals(fbst)) shouldBe false
        (l.equals(noDups)) shouldBe false
        (fbst.equals(l)) shouldBe false
        (noDups.equals(l)) shouldBe false
      }
    }
    Arb.fbstreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fbst ->
      val l = FRBTree.of(fbst.iterator())
      (fbst.size >= 0) shouldBe true
      (fbst == fbst) shouldBe true
      if (l.size == fbst.size) {
        (fbst.equals(l)) shouldBe true
        (l.equals(fbst)) shouldBe true
      } else {
        val noDups = FBSTree.of(l.preorder())
        (l.equals(fbst)) shouldBe false
        (l.equals(noDups)) shouldBe false
        (fbst.equals(l)) shouldBe false
        (noDups.equals(l)) shouldBe false
      }
    }
    Arb.fbstreeAsCollection<String, String>(Arb.string(0..10)).checkAll(repeats) { fbst ->
      val l = FRBTree.of(fbst.iterator())
      (fbst.size >= 0) shouldBe true
      (fbst == fbst) shouldBe true
      if (l.size == fbst.size) {
        (fbst.equals(l)) shouldBe true
        (l.equals(fbst)) shouldBe true
      } else {
        val noDups = FBSTree.of(l.preorder())
        (l.equals(fbst)) shouldBe false
        (l.equals(noDups)) shouldBe false
        (fbst.equals(l)) shouldBe false
        (noDups.equals(l)) shouldBe false
      }
    }
  }

  test("contains, all, any") {
    Arb.fbstreeAsCollection<String, String>(Arb.string(0..10)).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      l.all { s -> fbst.contains(s) } shouldBe true
      l.any { s -> fbst.contains(s) } shouldBe true
    }
  }

  test("containsAll") {
    Arb.fbstreeAsCollection<String, String>(Arb.string(0..10)).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.containsAll(l.reversed()) shouldBe true
    }
  }

  test("find (first)") {
    Arb.fbstreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val ix = kotlin.random.Random.Default.nextInt(fbst.size)
      val selection: TKVEntry<Int, Char> = l[ix]
      fbst.find(matchEqual(selection)) shouldBe selection
    }
  }

  test("indexOf") {
    Arb.fbstreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val ix = kotlin.random.Random.Default.nextInt(fbst.size)
      val selection = l[ix]
      fbst.indexOf(selection) shouldBe l.indexOfFirst(matchEqual(selection))
    }
  }

  test("indexOfirst") {
    Arb.fbstreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val ix = kotlin.random.Random.Default.nextInt(fbst.size)
      val selection = l[ix]
      fbst.indexOfFirst(matchEqual(selection)) shouldBe l.indexOfFirst(matchEqual(selection))
    }
  }

  test("indexOfLast") {
    Arb.fbstreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val ix = kotlin.random.Random.Default.nextInt(fbst.size)
      val selection = l[ix]
      fbst.indexOfLast(matchEqual(selection)) shouldBe l.indexOfLast(matchEqual(selection))
    }
  }

  test("last (find)") {
    Arb.fbstreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val ix = kotlin.random.Random.Default.nextInt(fbst.size)
      val selection = l[ix]
      fbst.last(matchEqual(selection)) shouldBe selection
    }
  }

  test("lastIndexOf") {
    Arb.fbstreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val ix = kotlin.random.Random.Default.nextInt(fbst.size)
      val selection = l[ix]
      fbst.lastIndexOf(selection) shouldBe l.indexOfLast(matchEqual(selection))
    }
  }

  test("findlast") {
    Arb.fbstreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val ix = kotlin.random.Random.Default.nextInt(fbst.size)
      val selection = l[ix]
      fbst.findLast(matchEqual(selection)) shouldBe selection
    }
  }

  test("drop") {
    Arb.fbstreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val n = kotlin.random.Random.Default.nextInt(fbst.size)
      fbst.drop(n) shouldBe l.drop(n)
    }
  }

  test("dropWhile") {
    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val ora = middle(l)
      fbst.dropWhile(matchLessThan(ora)) shouldBe l.dropWhile(matchLessThan(ora))
    }
  }

  test("filter") {
    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val ora = middle(l)
      fbst.filter(matchLessThan(ora)) shouldBe l.filter(matchLessThan(ora))
    }
  }

  test("filterNot") {

    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val ora = middle(l)
      fbst.filterNot(matchLessThan(ora)) shouldBe l.filterNot(matchLessThan(ora))
    }
  }

  test("take") {
    Arb.fbstreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val n = kotlin.random.Random.Default.nextInt(fbst.size)
      fbst.take(n) shouldBe l.take(n)
    }
  }

  test("takeWhile") {
    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val ora = middle(l)
      fbst.takeWhile(matchLessThan(ora)) shouldBe l.takeWhile(matchLessThan(ora))
    }
  }

  test("reversed") {
    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.reversed() shouldBe l.reversed()
    }
  }

  test("sorted") {
    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.sorted() shouldBe l.sorted()
    }
  }

  test("sortedDescending") {
    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.sortedDescending() shouldBe l.sortedDescending()
    }
  }

  test("sortedBy") {
    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.sortedBy(::reverseNumerical) shouldBe l.sortedBy(::reverseNumerical)
    }
  }

  test("sortedByDescending") {
    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.sortedByDescending(::reverseNumerical) shouldBe l.sortedByDescending(::reverseNumerical)
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

    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.sortedWith(reverseNumCo) shouldBe l.sortedWith(reverseNumCo)
    }
  }

  test("associate") {

    fun f(t: TKVEntry<Int, Int>): Pair<TKVEntry<Int, Int>, TKVEntry<Int, Int>> = Pair(t.getv().toIAEntry(), (-t.getv()).toIAEntry())

    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.associate(::f) shouldBe l.associate(::f)
    }
  }

  test("associateBy") {

    fun f(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), -t.getv())

    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.associateBy(::f) shouldBe l.associateBy(::f)
    }
  }

  test("associateBy (k, v)") {

    fun f(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), -t.getv())
    fun g(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), 2*t.getv())

    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.associateBy(::f, ::g) shouldBe l.associateBy(::f, ::g)
    }
  }

  test("associateWith") {

    fun g(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), 2*t.getv())

    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.associateWith(::g) shouldBe l.associateWith(::g)
    }
  }

  test("flatMap") {
    checkAll(5, Arb.fbstreeAsCollection<Int, Int>(Arb.int()), Arb.fbstreeAsCollection<Int, Int>(Arb.int())) { fbst1, fbst2  ->
      val l1: List<TKVEntry<Int, Int>> = fbst1.toList()
      val l2: List<TKVEntry<Int, Int>> = fbst2.toList()
      val autFl = fbst1.flatMap { outIt -> l2.map { inIt -> (inIt.getv() + outIt.getv()).toIAEntry() } }
      val autL = l1.flatMap { outIt -> l2.map { inIt -> (inIt.getv() + outIt.getv()).toIAEntry() } }
      autFl shouldBe autL
    }
  }

  test("groupBy") {

    fun f(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), -t.getv())

    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.groupBy(::f) shouldBe l.groupBy(::f)
    }
  }

  test("groupBy (k, v)") {

    fun f(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), -t.getv())
    fun g(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), 2*t.getv())

    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.groupBy(::f, ::g) shouldBe l.groupBy(::f, ::g)
    }
  }

  // TODO (maybe)
  // test("grouping") {
  //   fail("not implemented yet")
  // }

  test("map") {

    fun f(t: TKVEntry<Int, Int>): TKVEntry<Int, Int> = TKVEntry.ofkk(t.getk(), t.getv()+1)

    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.map(::f) shouldBe l.map(::f)
    }
  }

  test("distinct") {
    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.distinct() shouldBe l.distinct()
    }
  }

  test("distinctBy") {

    fun identity(oracle: TKVEntry<Int, Int>) = oracle

    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.distinctBy(::identity) shouldBe l.distinctBy(::identity)
    }
  }

  test("count") {
    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.count() shouldBe l.size
    }
  }

  test("count matching") {
    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val ora = middle(l)
      fbst.count(matchEqual(ora)) shouldBe l.count(matchEqual(ora))
    }
  }

  test("fold") {

    val f = { acc: TKVEntry<Int, Int>, b: TKVEntry<Int, Int> -> (acc.getk() - b.getv()).toIAEntry() }

    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.fold(1.toIAEntry(), f) shouldBe l.fold(1.toIAEntry(), f)
    }
  }

  test("none") {
    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val ora = (middle(l).getv() * 2).toIAEntry()
      fbst.none(matchLessThan(ora)) shouldBe l.none(matchLessThan(ora))
    }
  }

  test("reduce") {

    val f = { b:TKVEntry<Int, Int>, a: TKVEntry<Int, Int> -> (a.getv() - b.getv()).toIAEntry() }

    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.reduce(f) shouldBe l.reduce(f)
    }
  }

  test("runningFold") {
    val f = { acc: TKVEntry<Int, Int>, b: TKVEntry<Int, Int> -> (acc.getv() - b.getv()).toIAEntry() }

    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.runningFold(1.toIAEntry(), f) shouldBe l.runningFold(1.toIAEntry(), f)
    }
  }

  test("runningReduce") {
    val f = { b: TKVEntry<Int, Int>, a: TKVEntry<Int, Int> -> (a.getv() - b.getv()).toIAEntry() }

    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.runningReduce(f) shouldBe l.runningReduce(f)
    }
  }

  test("partition") {
    Arb.fbstreeAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      fbst.partition(matchLessThan(1.toIAEntry())) shouldBe l.partition(matchLessThan(1.toIAEntry()))
    }
  }

  test("windowed") {
    // Arb.fbstreeAsCollection<Char, Char>(Arb.char()).checkAll(PropTestConfig(iterations = 1, seed = -2438971832224874878)) { fbst ->
    Arb.fbstreeAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fbst ->
      val l = fbst.toList()
      val shortSize = kotlin.random.Random.Default.nextInt(fbst.size)
      for (sz in (shortSize downTo 1 step 5)) {
        val shortStep = kotlin.random.Random.Default.nextInt(sz)
        val partial = 0 == shortStep % 2
        // fbst.windowed(shortSize, shortStep, partial) shouldBe l.windowed(shortSize, shortStep, partial)
        fbst.windowed(sz, if (0 == shortStep) 1 else shortStep, partial) shouldBe l.windowed(sz, if (0 == shortStep) 1 else shortStep, partial)
      }
    }
  }

  test("zip array") {
    checkAll(5, Arb.fbstreeAsCollection<Int, Int>(Arb.int()), Arb.fbstreeAsCollection<Int, Int>(Arb.int())) { fbst1, fbst2  ->
      val l1: List<TKVEntry<Int, Int>> = fbst1.toList()
      val a2: Array<TKVEntry<Int, Int>> = FBSTree.toArray(fbst2 as FBSTree<Int, Int>)
      fbst1.zip (a2) shouldBe l1.zip(a2)
    }
  }

  test("zip iterable") {
    checkAll(5, Arb.fbstreeAsCollection<Int, Int>(Arb.int()), Arb.fbstreeAsCollection<Int, Int>(Arb.int())) { fbst1, fbst2  ->
      val l1: List<TKVEntry<Int, Int>> = fbst1.toList()
      val l2: List<TKVEntry<Int, Int>> = fbst2.toList()
      fbst1.zip (l2) shouldBe l1.zip(l2)
    }
  }
})
