package com.xrpn.kapi

import com.xrpn.immutable.*
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.xrpn.flistAsCollection

class FListKCollPropertyTest : FunSpec({

  beforeTest {}

  val repeats = 10
  fun <Z: Comparable<Z>> matchEqual(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle == aut }
  fun <Z: Comparable<Z>> matchLessThan(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle < aut }
  fun middle(l: Collection<Int>): Int {
    val max = l.maxOrNull()
    val min = l.minOrNull()
    return (max!! - min!!) / 2
  }
  fun reverseNumerical(t: Int): Int? = -t

  test("equals") {
    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      (fl.size >= 0) shouldBe true
      (fl == fl) shouldBe true
      (l == fl) shouldBe true
      (fl == l) shouldBe true
    }
    Arb.flistAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      (fl.size >= 0) shouldBe true
      (fl == fl) shouldBe true
      (l == fl) shouldBe true
      (fl == l) shouldBe true
    }
    Arb.flistAsCollection<String, String>(Arb.string(0..10)).checkAll(repeats) { fl ->
      val l = fl.toList()
      (fl.size >= 0) shouldBe true
      (fl == fl) shouldBe true
      (l == fl) shouldBe true
      (fl == l) shouldBe true
    }
  }

  test("contains, all, any") {
    Arb.flistAsCollection<String, String>(Arb.string(0..10)).checkAll(repeats) { fl ->
      val l = fl.toList()
      l.all { s -> fl.contains(s) } shouldBe true
      l.any { s -> fl.contains(s) } shouldBe true
    }
  }

  test("containsAll") {
    Arb.flistAsCollection<String, String>(Arb.string(0..10)).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.containsAll(l.reversed()) shouldBe true
    }
  }

  test("find (first)") {
    Arb.flistAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val selection = l[ix]
      fl.find(matchEqual(selection)) shouldBe selection
    }
  }

  test("indexOf") {
    Arb.flistAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val selection = l[ix]
      fl.indexOf(selection) shouldBe l.indexOfFirst(matchEqual(selection))
    }
  }

  test("indexOfirst") {
    Arb.flistAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val selection = l[ix]
      fl.indexOfFirst(matchEqual(selection)) shouldBe l.indexOfFirst(matchEqual(selection))
    }
  }

  test("indexOfLast") {
    Arb.flistAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val selection = l[ix]
      fl.indexOfLast(matchEqual(selection)) shouldBe l.indexOfLast(matchEqual(selection))
    }
  }

  test("last (find)") {
    Arb.flistAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val selection = l[ix]
      fl.last(matchEqual(selection)) shouldBe selection
    }
  }

  test("lastIndexOf") {
    Arb.flistAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val selection = l[ix]
      fl.lastIndexOf(selection) shouldBe l.indexOfLast(matchEqual(selection))
    }
  }

  test("findlast") {
    Arb.flistAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val selection = l[ix]
      fl.findLast(matchEqual(selection)) shouldBe selection
    }
  }

  test("drop") {
    Arb.flistAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val n = kotlin.random.Random.Default.nextInt(fl.size)
      fl.drop(n) shouldBe l.drop(n)
    }
  }

  test("dropWhile") {
    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ora = middle(l)
      fl.dropWhile(matchLessThan(ora)) shouldBe l.dropWhile(matchLessThan(ora))
    }
  }

  test("filter") {
    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ora = middle(l)
      fl.filter(matchLessThan(ora)) shouldBe l.filter(matchLessThan(ora))
    }
  }

  test("filterNot") {

    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ora = middle(l)
      fl.filterNot(matchLessThan(ora)) shouldBe l.filterNot(matchLessThan(ora))
    }
  }

  test("take") {
    Arb.flistAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val n = kotlin.random.Random.Default.nextInt(fl.size)
      fl.take(n) shouldBe l.take(n)
    }
  }

  test("takeWhile") {
    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ora = middle(l)
      fl.takeWhile(matchLessThan(ora)) shouldBe l.takeWhile(matchLessThan(ora))
    }
  }

  test("reversed") {
    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.reversed() shouldBe l.reversed()
    }
  }

  test("sorted") {
    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.sorted() shouldBe l.sorted()
    }
  }

  test("sortedDescending") {
    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.sortedDescending() shouldBe l.sortedDescending()
    }
  }

  test("sortedBy") {
    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.sortedBy(::reverseNumerical) shouldBe l.sortedBy(::reverseNumerical)
    }
  }

  test("sortedByDescending") {
    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.sortedByDescending(::reverseNumerical) shouldBe l.sortedByDescending(::reverseNumerical)
    }
  }

  test("sortedWith") {

    val reverseNumCo: Comparator<Int> = Comparator<Int> { p0, p1 ->
      when {
        p0 == p1 -> 0
        p0 > p1 -> -1
        else -> 1
      }
    }

    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.sortedWith(reverseNumCo) shouldBe l.sortedWith(reverseNumCo)
    }
  }

  test("associate") {

    fun f(t: Int): Pair<Int, Int> = Pair(t, -t)

    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.associate(::f) shouldBe l.associate(::f)
    }
  }

  test("associateBy") {

    fun f(t: Int): Int = -t

    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.associateBy(::f) shouldBe l.associateBy(::f)
    }
  }

  test("associateBy (k, v)") {

    fun f(t: Int): Int = -t
    fun g(t: Int): Int = 2*t

    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.associateBy(::f, ::g) shouldBe l.associateBy(::f, ::g)
    }
  }

  test("associateWith") {

    fun g(t: Int): Int = 2*t

    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.associateWith(::g) shouldBe l.associateWith(::g)
    }
  }

  test("flatMap") {
    checkAll(5, Arb.flistAsCollection<Int, Int>(Arb.int()), Arb.flistAsCollection<Int, Int>(Arb.int())) { fl1, fl2  ->
      val l1: List<Int> = fl1.toList()
      val l2: List<Int> = fl2.toList()
      val autFl = fl1.flatMap { outIt -> l2.map { inIt -> inIt + outIt } }
      val autL = l1.flatMap { outIt -> l2.map { inIt -> inIt + outIt } }
      autFl shouldBe autL
    }
  }

  test("groupBy") {

    fun f(t: Int): Int = -t

    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.groupBy(::f) shouldBe l.groupBy(::f)
    }
  }

  test("groupBy (k, v)") {

    fun f(t: Int): Int = -t
    fun g(t: Int): Int = 2*t

    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.groupBy(::f, ::g) shouldBe l.groupBy(::f, ::g)
    }
  }

  // TODO (maybe)
  // test("grouping") {
  //   fail("not implemented yet")
  // }

  test("map") {
    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.map{ it + 1 } shouldBe l.map{ it + 1 }
    }
  }

  test("distinct") {
    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.distinct() shouldBe l.distinct()
    }
  }

  test("distinctBy") {

    fun identity(oracle: Int) = oracle

    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.distinctBy(::identity) shouldBe l.distinctBy(::identity)
    }
  }

  test("count") {
    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.count() shouldBe l.size
    }
  }

  test("count matching") {
    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ora = middle(l)
      fl.count(matchEqual(ora)) shouldBe l.count(matchEqual(ora))
    }
  }

  test("fold") {

    val f = { acc: Int, b: Int -> acc - b }

    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.fold(1, f) shouldBe l.fold(1, f)
    }
  }

  test("none") {
    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ora = middle(l) * 2
      fl.none(matchLessThan(ora)) shouldBe l.none(matchLessThan(ora))
    }
  }

  test("reduce") {

    val f = { b: Int, a: Int -> a - b }

    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.reduce(f) shouldBe l.reduce(f)
    }
  }

  test("runningFold") {
    val f = { acc: Int, b: Int -> acc - b }

    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.runningFold(1, f) shouldBe l.runningFold(1, f)
    }
  }

  test("runningReduce") {
    val f = { b: Int, a: Int -> a - b }

    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.runningReduce(f) shouldBe l.runningReduce(f)
    }
  }

  test("partition") {
    Arb.flistAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.partition(matchLessThan(1)) shouldBe l.partition(matchLessThan(1))
    }
  }

  test("windowed") {
    // Arb.flistAsCollection<Char, Char>(Arb.char()).checkAll(PropTestConfig(iterations = 1, seed = -2438971832224874878)) { fl ->
    Arb.flistAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val shortSize = kotlin.random.Random.Default.nextInt(fl.size)
      for (sz in (shortSize downTo 1 step 5)) {
        val shortStep = kotlin.random.Random.Default.nextInt(sz)
        val partial = 0 == shortStep % 2
        // fl.windowed(shortSize, shortStep, partial) shouldBe l.windowed(shortSize, shortStep, partial)
        fl.windowed(sz, if (0 == shortStep) 1 else shortStep, partial) shouldBe l.windowed(sz, if (0 == shortStep) 1 else shortStep, partial)
      }
    }
  }

  test("zip array") {
    checkAll(5, Arb.flistAsCollection<Int, Int>(Arb.int()), Arb.flistAsCollection<Int, Int>(Arb.int())) { fl1, fl2  ->
      val l1: List<Int> = fl1.toList()
      val a2: Array<Int> = FList.toArray(fl2 as FList<Int>)
      fl1.zip (a2) shouldBe l1.zip(a2)
    }
  }

  test("zip iterable") {
    checkAll(5, Arb.flistAsCollection<Int, Int>(Arb.int()), Arb.flistAsCollection<Int, Int>(Arb.int())) { fl1, fl2  ->
      val l1: List<Int> = fl1.toList()
      val l2: List<Int> = fl2.toList()
      fl1.zip (l2) shouldBe l1.zip(l2)
    }
  }
})
