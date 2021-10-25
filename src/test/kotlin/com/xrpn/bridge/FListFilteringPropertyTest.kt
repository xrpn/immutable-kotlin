package com.xrpn.bridge

import com.xrpn.immutable.FList
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.flist

class FListFilteringPropertyTest : FunSpec({

  val repeats = 10
  fun <Z: Comparable<Z>> matchEqual(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle == aut }
  fun <Z: Comparable<Z>> matchLessThan(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle < aut }
  fun middle(l: List<Int>): Int {
    val max = l.maxOrNull()
    val min = l.minOrNull()
    return (max!! - min!!) / 2
  }

  beforeTest {}

  test("fdrop") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      for (n in (0..fl.size)) {
        fl.fdrop(n) shouldBe fl.drop(n)
      }
    }
  }

  test("fdropFirst") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val item = fl.get(ix)
      // no matching functionality
      fl.fdropFirst(matchEqual(item)) shouldBe fl.filterNot(matchEqual(fl.first(matchEqual(item))))
    }
  }

  test("fdropRight") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      for (n in (0..fl.size)) {
        fl.fdropRight(n) shouldBe fl.dropLast(n)
      }
    }
  }

  test("fdropWhile") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val ora = middle(fl)
      fl.fdropWhile(matchLessThan(ora)) shouldBe fl.dropWhile(matchLessThan(ora))
    }
  }

  test("fdropWhen") {
    // Arb.flist<Int, Int>(Arb.int()).checkAll(repeats, PropTestConfig(seed=2890448575695491053)) { fl ->
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val ora = middle(fl)
      val aux: MutableList<Int> = fl.copyToMutableList()
      aux.removeIf(matchLessThan(ora))
      fl.fdropWhen(matchLessThan(ora)) shouldBe aux
      fl.fdropWhen(matchLessThan(ora)) shouldBe fl.ffilterNot(matchLessThan(ora))
    }
  }

  test("ffilter") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val ora = middle(fl)
      fl.ffilter(matchLessThan(ora)) shouldBe fl.filter(matchLessThan(ora))
    }
  }

  test("ffilterNot") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val ora = middle(fl)
      fl.ffilterNot(matchLessThan(ora)) shouldBe fl.filterNot(matchLessThan(ora))
      fl.fdropWhen(matchLessThan(ora)) shouldBe fl.ffilterNot(matchLessThan(ora))
    }
  }

  test("ffindFromLeft") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val item = fl.get(ix)
      fl.ffind(matchEqual(item)) shouldBe fl.find(matchEqual(item))
    }
  }

  test("ffindFromRight") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val item = fl.get(ix)
      fl.ffindLast(matchEqual(item)) shouldBe fl.findLast(matchEqual(item))
    }
  }

  test ("fgetOrNull") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      for (ix in (-1..fl.size+1)) {
        fl.fgetOrNull(ix) shouldBe fl.elementAtOrNull(ix)
      }
    }
  }

  test("fhead") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      fl.fhead() shouldBe fl.first()
    }
  }

  test("finit") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      // no matching functionality
      fl.finit() shouldBe fl.filterNot(matchEqual(fl.last()))
      fl.finit() shouldBe fl.dropLast(1)
    }
  }

  test("flast") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      fl.flast() shouldBe fl.last()
    }
  }

  test("fslice (ix, ix)") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val aux1 = kotlin.random.Random.Default.nextInt(fl.size)
      val aux2 = kotlin.random.Random.Default.nextInt(fl.size)
      fl.fslice(aux1, aux2) shouldBe fl.subList(aux1, aux2)
    }
  }

  test("fslice (list)") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val aux1 = kotlin.random.Random.Default.nextInt(fl.size)
      val aux2 = kotlin.random.Random.Default.nextInt(fl.size)
      val ixs = FList.of(aux1, aux2)
      fl.fselect(ixs) shouldBe fl.slice(ixs)
      fl.fselect(FList.of()) shouldBe fl.slice(FList.of())
      fl.fselect(FList.of(aux1, fl.size-1)) shouldBe fl.slice(FList.of(aux1, fl.size-1))
      fl.fselect(FList.of(fl.size-1, aux2)) shouldBe fl.slice(FList.of(fl.size-1, aux2))
    }
  }

  test("ftail") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      fl.ftail() shouldBe fl.drop(1)
    }
  }

  test("ftake") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      for (n in (0..fl.size)) {
        fl.ftake(n) shouldBe fl.take(n)
      }
    }
  }

  test("ftakeRight") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      for (n in (0..fl.size)) {
        fl.ftakeRight(n) shouldBe fl.takeLast(n)
      }
    }
  }

  test("ftakeWhile") {
    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val ora = middle(fl)
      fl.ftakeWhile(matchLessThan(ora)) shouldBe fl.takeWhile(matchLessThan(ora))
    }
  }

})
