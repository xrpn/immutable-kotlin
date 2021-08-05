package com.xrpn.bridge

import com.xrpn.immutable.FList
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.flist

class FListGroupingTest : FunSpec({

  val repeats = 10
  fun <Z> matchEqualNullable(oracle: Z?): (Z) -> Boolean = { aut: Z -> oracle == aut }
  fun <Z: Comparable<Z>> matchLessThan(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle < aut }
  fun middle(l: List<Int>): Int {
    val max = l.maxOrNull()
    val min = l.minOrNull()
    return (max!! - min!!) / 2
  }

  beforeTest {}

  test("fcount") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val ora = middle(fl)
      fl.fcount(matchLessThan(ora)) shouldBe fl.count(matchLessThan(ora))
    }
  }

  test("fenumerate") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      fl.fenumerate() shouldBe fl.zip(0..fl.size)
    }
  }

  test("fenumerate offset") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      for (n in (0..10)) {
        fl.fenumerate(n) shouldBe fl.zip(n..(fl.size+n))
      }
    }
  }

  test("ffindFirst") {
    // Arb.flist<Int, Int>(Arb.int()).checkAll(PropTestConfig(iterations = 1, seed = -3295811929144312111)) { fl ->
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val maybeIx = kotlin.random.Random.Default.nextInt(fl.size) + (fl.size / 2)
      val maybeOra = fl.fgetOrNull(maybeIx)
      // no matching functionality
      fl.ffindFirst(matchEqualNullable(maybeOra)) shouldBe Triple(fl.take(maybeIx), maybeOra, fl.drop(maybeIx+1))
    }
  }

  test("fgroupBy") {
    fail("need FMap done to make this happen")
    // intListOfNone.fgroupBy { a -> a.toString() } shouldBe emptyMap()
  }

  test("fpartition") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val item = fl.get(ix)
      fl.fpartition(matchLessThan(item)) shouldBe fl.partition(matchLessThan(item))
    }
  }

  test("fslidingWindow") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val ix1 = kotlin.random.Random.Default.nextInt(fl.size) + 1
      val ix2 = kotlin.random.Random.Default.nextInt(fl.size) + 1
      fl.fslidingWindow(ix1, ix2) shouldBe fl.windowed(ix1, ix2, partialWindows = true)
    }
  }

  test("fslidingFullWindow") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val ix1 = kotlin.random.Random.Default.nextInt(fl.size) + 1
      val ix2 = kotlin.random.Random.Default.nextInt(fl.size) + 1
      fl.fslidingFullWindow(ix1, ix2) shouldBe fl.windowed(ix1, ix2, partialWindows = false)
    }
  }

  test("fsplitAt") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val maybeIx = kotlin.random.Random.Default.nextInt(fl.size) + (fl.size / 2)
      val maybeOra = fl.fgetOrNull(maybeIx)
      // no matching functionality
      fl.fsplitAt(maybeIx) shouldBe Triple(fl.take(maybeIx), maybeOra, fl.drop(maybeIx+1))
    }
  }

  test("funzip") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val zipped: FList<Pair<Int, Int>> = fl.fenumerate()
      zipped.funzip { p -> p } shouldBe zipped.unzip()
    }
  }

  test("zipWith") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      fl.fzipWith(fl) {a, b -> Pair(a,b)} shouldBe fl.zip(fl)
    }
  }

  test("zipWith iterable") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      fl.fzipWith(fl.iterator()) shouldBe fl.zip(fl)
    }
  }


  test("fzipWithIndex") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      fl.fzipWithIndex() shouldBe fl.zip((0..fl.size))
    }
  }

  test("fzipWithIndex offset") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      for (n in (0..fl.size)) {
        // no matching functionality
        fl.fzipWithIndex(n) shouldBe fl.drop(n).zip((0..fl.size))
      }
    }
  }
})
