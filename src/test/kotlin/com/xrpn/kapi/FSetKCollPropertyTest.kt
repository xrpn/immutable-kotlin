package com.xrpn.kapi

import com.xrpn.immutable.FList
import com.xrpn.immutable.FList.Companion.toIMList
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.xrpn.fsetAsCollection

class FSetKCollPropertyTest : FunSpec({

  beforeTest {}

  val repeats = 10
  fun <Z: Comparable<Z>> matchEqual(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle == aut }
  fun <Z: Comparable<Z>> matchLessThan(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle < aut }
  fun middle(s: Collection<Int>): Int {
    val max = s.maxOrNull()
    val min = s.minOrNull()
    return (max!! - min!!) / 2
  }
  fun reverseNumerical(t: Int): Int? = -t

  test("equals") {
    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      (fs.size >= 0) shouldBe true
      (fs.size == s.size) shouldBe true
      (fs == fs) shouldBe true
      (s == fs) shouldBe true
      fs.equals(s) shouldBe true
    }
    Arb.fsetAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      (fs.size >= 0) shouldBe true
      (fs == fs) shouldBe true
      (s == fs) shouldBe true
      (fs == s) shouldBe true
    }
    Arb.fsetAsCollection<String, String>(Arb.string(0..10)).checkAll(repeats) { fs ->
      val s = fs.toSet()
      (fs.size >= 0) shouldBe true
      (fs == fs) shouldBe true
      (s == fs) shouldBe true
      (fs == s) shouldBe true
    }
  }

  test("contains, all, any") {
    Arb.fsetAsCollection<String, String>(Arb.string(0..10)).checkAll(repeats) { fs ->
      val ss = fs.toSet()
      ss.all { s -> fs.contains(s) } shouldBe true
      ss.any { s -> fs.contains(s) } shouldBe true
    }
  }

  test("containsAll") {
    Arb.fsetAsCollection<String, String>(Arb.string(0..10)).checkAll(repeats, PropTestConfig(seed = -6619752318048995269)) { fs ->
      val s = fs.toSet()
      fs.containsAll(s) shouldBe true
    }
  }

  test("find (first)") {
    Arb.fsetAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fs ->
      val s = fs.toList()
      val ix = kotlin.random.Random.Default.nextInt(fs.size)
      val selection = s[ix]
      fs.find(matchEqual(selection)) shouldBe selection
    }
  }

  test("indexOf") {
    Arb.fsetAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fs ->
      val s = fs.toList()
      val ix = kotlin.random.Random.Default.nextInt(fs.size)
      val selection = s[ix]
      fs.indexOf(selection) shouldBe s.indexOfFirst(matchEqual(selection))
    }
  }

  test("indexOfirst") {
    Arb.fsetAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fs ->
      val s = fs.toList()
      val ix = kotlin.random.Random.Default.nextInt(fs.size)
      val selection = s[ix]
      fs.indexOfFirst(matchEqual(selection)) shouldBe s.indexOfFirst(matchEqual(selection))
    }
  }

  test("indexOfLast") {
    Arb.fsetAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fs ->
      val s = fs.toList()
      val ix = kotlin.random.Random.Default.nextInt(fs.size)
      val selection = s[ix]
      fs.indexOfLast(matchEqual(selection)) shouldBe s.indexOfLast(matchEqual(selection))
    }
  }

  test("last (find)") {
    Arb.fsetAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fs ->
      val s = fs.toList()
      val ix = kotlin.random.Random.Default.nextInt(fs.size)
      val selection = s[ix]
      fs.last(matchEqual(selection)) shouldBe selection
    }
  }

  test("lastIndexOf") {
    Arb.fsetAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fs ->
      val s = fs.toList()
      val ix = kotlin.random.Random.Default.nextInt(fs.size)
      val selection = s[ix]
      fs.lastIndexOf(selection) shouldBe s.indexOfLast(matchEqual(selection))
    }
  }

  test("findlast") {
    Arb.fsetAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fs ->
      val s = fs.toList()
      val ix = kotlin.random.Random.Default.nextInt(fs.size)
      val selection = s[ix]
      fs.findLast(matchEqual(selection)) shouldBe selection
    }
  }

  test("drop") {
    Arb.fsetAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      val n = kotlin.random.Random.Default.nextInt(fs.size)
      fs.drop(n) shouldBe s.drop(n)
    }
  }

  test("dropWhile") {
    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      val ora = middle(s)
      fs.dropWhile(matchLessThan(ora)) shouldBe s.dropWhile(matchLessThan(ora))
    }
  }

  test("filter") {
    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      val ora = middle(s)
      fs.filter(matchLessThan(ora)) shouldBe s.filter(matchLessThan(ora))
    }
  }

  test("filterNot") {

    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      val ora = middle(s)
      fs.filterNot(matchLessThan(ora)) shouldBe s.filterNot(matchLessThan(ora))
    }
  }

  test("take") {
    Arb.fsetAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      val n = kotlin.random.Random.Default.nextInt(fs.size)
      fs.take(n) shouldBe s.take(n)
    }
  }

  test("takeWhile") {
    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      val ora = middle(s)
      fs.takeWhile(matchLessThan(ora)) shouldBe s.takeWhile(matchLessThan(ora))
    }
  }

  test("reversed") {
    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.reversed() shouldBe s.reversed()
    }
  }

  test("sorted") {
    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.sorted() shouldBe s.sorted()
    }
  }

  test("sortedDescending") {
    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.sortedDescending() shouldBe s.sortedDescending()
    }
  }

  test("sortedBy") {
    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.sortedBy(::reverseNumerical) shouldBe s.sortedBy(::reverseNumerical)
    }
  }

  test("sortedByDescending") {
    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.sortedByDescending(::reverseNumerical) shouldBe s.sortedByDescending(::reverseNumerical)
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

    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.sortedWith(reverseNumCo) shouldBe s.sortedWith(reverseNumCo)
    }
  }

  test("associate") {

    fun f(t: Int): Pair<Int, Int> = Pair(t, -t)

    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.associate(::f) shouldBe s.associate(::f)
    }
  }

  test("associateBy") {

    fun f(t: Int): Int = -t

    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.associateBy(::f) shouldBe s.associateBy(::f)
    }
  }

  test("associateBy (k, v)") {

    fun f(t: Int): Int = -t
    fun g(t: Int): Int = 2*t

    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.associateBy(::f, ::g) shouldBe s.associateBy(::f, ::g)
    }
  }

  test("associateWith") {

    fun g(t: Int): Int = 2*t

    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.associateWith(::g) shouldBe s.associateWith(::g)
    }
  }

  test("flatMap") {
    checkAll(5, Arb.fsetAsCollection<Int, Int>(Arb.int()), Arb.fsetAsCollection<Int, Int>(Arb.int())) { fl1, fl2  ->
      val l1: List<Int> = fl1.toList()
      val l2: List<Int> = fl2.toList()
      val autFl = fl1.flatMap { outIt -> l2.map { inIt -> inIt + outIt } }
      val autL = l1.flatMap { outIt -> l2.map { inIt -> inIt + outIt } }
      autFl shouldBe autL
    }
  }

  test("groupBy") {

    fun f(t: Int): Int = -t

    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.groupBy(::f) shouldBe s.groupBy(::f)
    }
  }

  test("groupBy (k, v)") {

    fun f(t: Int): Int = -t
    fun g(t: Int): Int = 2*t

    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.groupBy(::f, ::g) shouldBe s.groupBy(::f, ::g)
    }
  }

  // TODO (maybe)
  // test("grouping") {
  //   fail("not implemented yet")
  // }

  test("map") {
    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.map{ it + 1 } shouldBe s.map{ it + 1 }
    }
  }

  test("distinct") {
    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.distinct() shouldBe s.distinct()
    }
  }

  test("distinctBy") {

    fun identity(oracle: Int) = oracle

    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.distinctBy(::identity) shouldBe s.distinctBy(::identity)
    }
  }

  test("count") {
    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.count() shouldBe s.size
    }
  }

  test("count matching") {
    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      val ora = middle(s)
      fs.count(matchEqual(ora)) shouldBe s.count(matchEqual(ora))
    }
  }

  test("fold") {

    val f = { acc: Int, b: Int -> acc - b }

    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.fold(1, f) shouldBe s.fold(1, f)
    }
  }

  test("none") {
    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      val ora = middle(s) * 2
      fs.none(matchLessThan(ora)) shouldBe s.none(matchLessThan(ora))
    }
  }

  test("reduce") {

    val f = { b: Int, a: Int -> a - b }

    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.reduce(f) shouldBe s.reduce(f)
    }
  }

  test("runningFold") {
    val f = { acc: Int, b: Int -> acc - b }

    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.runningFold(1, f) shouldBe s.runningFold(1, f)
    }
  }

  test("runningReduce") {
    val f = { b: Int, a: Int -> a - b }

    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.runningReduce(f) shouldBe s.runningReduce(f)
    }
  }

  test("partition") {
    Arb.fsetAsCollection<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      fs.partition(matchLessThan(1)) shouldBe s.partition(matchLessThan(1))
    }
  }

  test("windowed") {
    // Arb.fsetAsCollection<Char, Char>(Arb.char()).checkAll(PropTestConfig(iterations = 1, seed = -2438971832224874878)) { fs ->
    Arb.fsetAsCollection<Char, Char>(Arb.char()).checkAll(repeats) { fs ->
      val s = fs.toSet()
      val shortSize = kotlin.random.Random.Default.nextInt(fs.size)
      for (sz in (shortSize downTo 1 step 5)) {
        val shortStep = kotlin.random.Random.Default.nextInt(sz)
        val partial = 0 == shortStep % 2
        // fs.windowed(shortSize, shortStep, partial) shouldBe s.windowed(shortSize, shortStep, partial)
        fs.windowed(sz, if (0 == shortStep) 1 else shortStep, partial) shouldBe s.windowed(sz, if (0 == shortStep) 1 else shortStep, partial)
      }
    }
  }

  test("zip array") {
    checkAll(5, Arb.fsetAsCollection<Int, Int>(Arb.int()), Arb.fsetAsCollection<Int, Int>(Arb.int())) { fl1, fl2  ->
      val l1: List<Int> = fl1.toList()
      val a2: Array<Int> = FList.toArray(FList.of(fl2.toIMList()))
      fl1.zip (a2) shouldBe l1.zip(a2)
    }
  }

  test("zip iterable") {
    checkAll(5, Arb.fsetAsCollection<Int, Int>(Arb.int()), Arb.fsetAsCollection<Int, Int>(Arb.int())) { fl1, fl2  ->
      val l1: List<Int> = fl1.toList()
      val l2: List<Int> = fl2.toList()
      fl1.zip (l2) shouldBe l1.zip(l2)
    }
  }
})
