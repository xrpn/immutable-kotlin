package com.xrpn.immutable.frbtreetest

import com.xrpn.imapi.IMCollection
import com.xrpn.immutable.*
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.frbtree

private val iiTreeOfNone: IMCollection<TKVEntry<Int,Int>> = FRBTree.nul<Int,Int>()
private val iiTreeOfTwo: IMCollection<TKVEntry<Int,Int>> = FRBTree.of(1.toIAEntry(), 2.toIAEntry())
private val siTreeOfTwo: IMCollection<TKVEntry<String, Int>> = FRBTree.of(1.toSAEntry(), 2.toSAEntry())
private val ixTreeOfTwo: IMCollection<TKVEntry<Int, FKSet<*, Int>>> =
  FRBTree.of(FKSet.ofi(1).toIAEntry(), FKSet.ofs(1).toIAEntry())
private val ixxTreeOfTwo: FRBTree<Int, FKSet<Int, RTKVEntry<Int, FKSet<*, Int>>>> = FRBTree.of(
  FKSet.ofi(FKSet.ofi(1).toIAEntry(), FKSet.ofs(2).toIAEntry()).toIAEntry(),
  FKSet.ofi(FKSet.ofi(1).toIAEntry(), FKSet.ofi(2).toIAEntry()).toIAEntry()
)
private val iyxTreeOfTwo: FRBTree<Int, FKSet<Int, RTKVEntry<Int, FKSet<*, Int>>>> = FRBTree.of(
  FKSet.ofi(FKSet.ofi(1).toIAEntry()).toIAEntry(),
  FKSet.ofi(FKSet.ofi(1).toIAEntry(), FKSet.ofi(2).toIAEntry()).toIAEntry()
)
private val ixxsTreeOfTwo: FRBTree<Int, Set<RTKVEntry<Int, FKSet<*, Int>>>> = FRBTree.of(
  setOf(FKSet.ofi(1).toIAEntry(), FKSet.ofs(2).toIAEntry()).toIAEntry(),
  setOf(FKSet.ofi(1).toIAEntry(), FKSet.ofi(2).toIAEntry()).toIAEntry()
)

private val imTreeOfTwoA = run {
  val mm1 = mutableMapOf(("1" to 1), ("2" to 2))
  val mm2 = mutableMapOf((1 to 1), (2 to 2))
  return@run FRBTree.of(
    mm1.toIAEntry(),
    mm2.toIAEntry()
  )
}

private val imTreeOfTwoB = run {
  val mm1 = mutableMapOf((1 to "1"), (2 to "2"))
  val mm2 = mutableMapOf((1 to 1), (2 to 2))
  return@run FRBTree.of(
    mm1.toIAEntry(),
    mm2.toIAEntry()
  )
}

private val imTreeOfOneOK = run {
  val mm1 = mutableMapOf((1 to 1), (2 to 2))
  val mm2 = mutableMapOf((1 to 1), (2 to 2))
  return@run FRBTree.of(
    mm1.toIAEntry(),
    mm2.toIAEntry()
  )
}

private val imTreeOfTwoOK = run {
  val mm1 = mutableMapOf((1 to 1), (2 to 2))
  val mm2 = mutableMapOf((1 to 1), (2 to 2))
  return@run FBSTree.of( // FBSTree is OK
    mm1.toIAEntry(),
    mm2.toIAEntry(),
    allowDups = true
  )
}

private val ixxmTreeOfTwo = run {
  val s1 = setOf(FKSet.ofi(1).toIAEntry(), FKSet.ofs(1).toIAEntry())
  val s2 = setOf(FKSet.ofi(1).toIAEntry(), FKSet.ofs(1).toIAEntry())
  val s3 = setOf(FKSet.ofi("1").toIAEntry(), FKSet.ofs("1").toIAEntry())
  val s4 = setOf(FKSet.ofi("1").toIAEntry(), FKSet.ofs("1").toIAEntry())
  val mm1 = mutableMapOf(("1" to s1), ("2" to s2))
  val mm2 = mutableMapOf((1 to s3), (2 to s4))
  return@run FRBTree.of(
    mm1.toIAEntry(),
    mm2.toIAEntry()
  )
}

class FRBTreeIMCollectionTest : FunSpec({

  val repeatsHigh = Pair(50, 100)

  beforeTest {}

  test("fcount") {
    iiTreeOfNone.fcount { true } shouldBe 0
    iiTreeOfNone.fcount { false } shouldBe 0
    Arb.frbtree(Arb.int(0..repeatsHigh.second)).checkAll(repeatsHigh.first) { frbt ->
      val mm = frbt.copyToMutableMap()
      val ss = mm.size // size without duplicates
      val ds = frbt.size // size with duplicates
      val aut = frbt as IMCollection<TKVEntry<Int,Int>>
      var tot = 0
      var totDups = 0
      var nonDistinct = 0
      for (entry in mm) {
        val aux = TKVEntry.ofme(entry)
        val counter = aut.fcount { it == aux }
        tot += counter
        if (frbt.fisDup(aux)) {
          nonDistinct += 1
          totDups += counter
        }
      }
      tot shouldBe ds
      (ss + totDups - nonDistinct) shouldBe ds
    }
  }

  test("fall") {
    iiTreeOfNone.fall { true } shouldBe true
    iiTreeOfNone.fall { false } shouldBe true
    Arb.frbtree(Arb.int(1..repeatsHigh.second)).checkAll(repeatsHigh.first) { frbt ->
      val ts = frbt.size
      val aut = frbt as IMCollection<TKVEntry<Int,Int>>
      var count: Int = 0
      aut.fall { count+=1; true } shouldBe true
      count shouldBe ts
      count = 0
      aut.fall { count+=1;false } shouldBe false
      count shouldBe ts
      count = 0
      aut.fall { count+=1; it > 0.toIAEntry() } shouldBe true
      count shouldBe ts
      count = 0
      aut.fall { count+=1; it > repeatsHigh.second.toIAEntry() } shouldBe false
      count shouldBe ts
    }
  }

  test("fany") {
    iiTreeOfNone.fany { true } shouldBe true
    iiTreeOfNone.fany { false } shouldBe true
    Arb.frbtree(Arb.int(1..repeatsHigh.second)).checkAll(repeatsHigh.first) { frbt ->
      val ts = frbt.size
      val aut = frbt as IMCollection<TKVEntry<Int,Int>>
      var count: Int = 0
      aut.fany { count+=1; true } shouldBe true
      count shouldBe 1
      count = 0
      aut.fany { count+=1; false } shouldBe false
      count shouldBe ts
      count = 0
      aut.fany { count+=1; it > 0.toIAEntry() } shouldBe true
      count shouldBe 1
      count = 0
      aut.fany { count+=1; it > repeatsHigh.second.toIAEntry() } shouldBe false
      count shouldBe ts
    }
  }

  test("fcontains") {
    iiTreeOfNone.fcontains(TKVEntry.ofkv(1, 1)) shouldBe false
    Arb.frbtree(Arb.int(1..repeatsHigh.second)).checkAll(repeatsHigh.first) { frbt ->
      val ts = frbt.size
      val sample = if (1 < ts) frbt.inorder().fdrop(ts / 2).fhead()!! else null
      val aut = frbt as IMCollection<TKVEntry<Int,Int>>
      aut.fcontains(TKVEntry.ofkv(0, 0)) shouldBe false
      sample?.let { aut.fcontains(sample) shouldBe true }
    }
  }

  test("fempty") {
    iiTreeOfNone.fempty() shouldBe true
    FRBTNil.fempty() shouldBe true
    FRBTree.ofvi(1).fempty() shouldBe false
  }

  // TODO dropWhen

  test ("ffilter, ffilterNot (A)") {
    fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
    fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
    checkAll(50, Arb.int(20..100)) { n ->
      val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      val svalues = values + values
      val ora1 = values.size
      svalues.size shouldBe (ora1 * 2)
      val tree: IMCollection<TKVEntry<Int,Int>> = FRBTree.of(svalues.iterator())
      tree.fsize() shouldBe ora1

      val sAll1 = tree.ffilter(pickIfLess(ora1))
      val snAll1 = tree.ffilterNot(pickIfLess(ora1))
      snAll1.fsize() shouldBe 0
      sAll1.fsize() shouldBe ora1
      val sEmpty1 = tree.ffilter(pickIfMore(ora1))
      val snEmpty1 = tree.ffilterNot(pickIfMore(ora1))
      sEmpty1.fsize() shouldBe 0
      snEmpty1.fsize() shouldBe ora1

      val ora2 = ora1 / 2
      val theRestSansOra2 = (ora1 - ora2) - 1

      val sAll2 = tree.ffilter(pickIfLess(ora2))
      val snAll2 = tree.ffilterNot(pickIfLess(ora2))
      sAll2.fsize() shouldBe ora2
      snAll2.fsize() shouldBe theRestSansOra2 + 1
      val sEmpty2 = tree.ffilter(pickIfMore(ora2))
      val snEmpty2 = tree.ffilterNot(pickIfMore(ora2))
      sEmpty2.fsize() shouldBe theRestSansOra2
      snEmpty2.fsize() shouldBe ora1 - theRestSansOra2
    }
  }

  test ("ffilter (B)") {
    fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
    fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
    checkAll(50, Arb.int(20..100)) { n ->
      val shuffled = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      shuffled.shuffle()
      val svalues = shuffled + shuffled
      val ora1 = shuffled.size
      svalues.size shouldBe (ora1 * 2)
      val tree: IMCollection<TKVEntry<Int,Int>> = FRBTree.of(svalues.iterator())
      tree.fsize() shouldBe ora1

      val sAll1 = tree.ffilter(pickIfLess(ora1))
      sAll1.fsize() shouldBe ora1
      val sEmpty1 = tree.ffilter(pickIfMore(ora1))
      sEmpty1.fsize() shouldBe 0

      val ora2 = ora1 / 2
      val theRestSansOra2 = (ora1 - ora2) - 1

      val sAll2 = tree.ffilter(pickIfLess(ora2))
      sAll2.fsize() shouldBe ora2
      val sEmpty2 = tree.ffilter(pickIfMore(ora2))
      sEmpty2.fsize() shouldBe theRestSansOra2
    }
  }

  test ("ffilter (C)") {
    fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
    fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
    checkAll(50, Arb.int(20..100)) { n ->
      val reversed = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      reversed.reverse()
      val svalues = reversed + reversed
      val ora1 = reversed.size
      svalues.size shouldBe (ora1 * 2)
      val tree: IMCollection<TKVEntry<Int,Int>> = FRBTree.of(svalues.iterator())
      tree.fsize() shouldBe ora1

      val sAll1 = tree.ffilter(pickIfLess(ora1))
      sAll1.fsize() shouldBe ora1
      val sEmpty1 = tree.ffilter(pickIfMore(ora1))
      sEmpty1.fsize() shouldBe 0

      val ora2 = ora1 / 2
      val theRestSansOra2 = (ora1 - ora2) - 1

      val sAll2 = tree.ffilter(pickIfLess(ora2))
      sAll2.fsize() shouldBe ora2
      val sEmpty2 = tree.ffilter(pickIfMore(ora2))
      sEmpty2.fsize() shouldBe theRestSansOra2
    }
  }

  test("ffindAny") {
    iiTreeOfNone.ffindAny { true } shouldBe null
    iiTreeOfNone.ffindAny { false } shouldBe null
    Arb.frbtree(Arb.int(1..repeatsHigh.second)).checkAll(repeatsHigh.first) { frbt ->
      val ts = frbt.size
      var count: Int = 0
      frbt.ffindAny{ count+=1; it == TKVEntry.ofkv(0, 0) } shouldBe null
      count shouldBe ts
      count = 0
      frbt.ffindAny{ count+=1; it > TKVEntry.ofkv(0, 0) } shouldNotBe null
      count shouldBe 1
      count = 0
      frbt.ffindAny{ count+=1; it > repeatsHigh.second.toIAEntry() } shouldBe null
      count shouldBe ts
      count = 0
      frbt.ffindAny{ count+=1; it < repeatsHigh.second.toIAEntry() } shouldNotBe null
      count shouldBe 1
      count = 0
      if (1 < ts) {
        val sample = frbt.inorder().fdrop(ts / 2).fhead()!!
        count = 0
        frbt.ffindAny{ count+=1; it == sample } shouldNotBe null
        (count >= 1) shouldBe true
        (count <= ts) shouldBe true
      }
    }
  }

  test("fisStrict") {
    iiTreeOfNone.fisStrict() shouldBe true
    iiTreeOfTwo.fisStrict() shouldBe true
    ixTreeOfTwo.fisStrict() shouldBe false
    ixxTreeOfTwo.fisStrict() shouldBe false
    iyxTreeOfTwo.fisStrict() shouldBe true
    ixxsTreeOfTwo.fisStrict() shouldBe false
    ixxmTreeOfTwo.fisStrict() shouldBe false
    imTreeOfTwoA.fisStrict() shouldBe false
    imTreeOfTwoB.fisStrict() shouldBe false
    imTreeOfOneOK.fisStrict() shouldBe true
    imTreeOfTwoOK.fisStrict() shouldBe true
  }

  test("fnone") {
    iiTreeOfNone.fnone { true } shouldBe true
    iiTreeOfNone.fnone { false } shouldBe true
    iiTreeOfTwo.fnone { it.getv() == 1 } shouldBe false
    iiTreeOfTwo.fnone { it.getv() > 10 } shouldBe true
  }

  test("fpick") {
    iiTreeOfNone.fpick() shouldBe null
    iiTreeOfTwo.fpick()?.let { it.getk()::class } shouldBe Int::class
    iiTreeOfTwo.fpick()?.let { it.getv()::class } shouldBe Int::class
    siTreeOfTwo.fpick()?.let { it.getk()::class } shouldBe String::class
    siTreeOfTwo.fpick()?.let { it.getv()::class } shouldBe Int::class
  }

  test("fsize") {
    iiTreeOfNone.fsize() shouldBe 0
    iiTreeOfTwo.fsize() shouldBe 2
  }

  test("fisNested") {
    iiTreeOfNone.fisNested() shouldBe false
    iiTreeOfTwo.fisNested() shouldBe false
    FRBTree.of(FList.emptyIMList<Int>().toIAEntry()).fisNested() shouldBe true
    FRBTree.of(FList.emptyIMList<Int>().toSAEntry()).fisNested() shouldBe true
    FRBTree.ofvi(emptyArray<Int>()).fisNested() shouldBe true
    FRBTree.ofvs(emptyArray<Int>()).fisNested() shouldBe true
    FRBTree.ofvi(setOf<Int>()).fisNested() shouldBe true
    FRBTree.ofvs(setOf<Int>()).fisNested() shouldBe true
    FRBTree.ofvi(*arrayOf(listOf<Int>())).fisNested() shouldBe true
    FRBTree.ofvs(*arrayOf(listOf<Int>())).fisNested() shouldBe true
    FRBTree.ofvi(mapOf<Int, Int>()).fisNested() shouldBe true
    FRBTree.ofvs(mapOf<Int, Int>()).fisNested() shouldBe true
    ixTreeOfTwo.fisNested() shouldBe true
    ixxTreeOfTwo.fisNested() shouldBe true
    ixxsTreeOfTwo.fisNested() shouldBe true
  }
})
