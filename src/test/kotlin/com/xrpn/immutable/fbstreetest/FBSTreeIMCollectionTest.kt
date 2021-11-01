package com.xrpn.immutable.fbstreetest

import com.xrpn.imapi.IMBTreeEqual2
import com.xrpn.imapi.IMCollection
import com.xrpn.immutable.*
import com.xrpn.immutable.FBSTree.Companion.emptyIMBTree
import com.xrpn.immutable.FBSTree.Companion.nul
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
import com.xrpn.order.fuzzy.FzyDouble
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.*
import io.kotest.property.arbitrary.IntShrinker
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.int
import io.kotest.xrpn.fbstree
import kotlin.random.Random
import kotlin.random.nextInt

private val iiTreeOfNone: IMCollection<TKVEntry<Int,Int>> = nul<Int,Int>()
private val iiTreeOfTwo: IMCollection<TKVEntry<Int,Int>> = FBSTree.of(1.toIAEntry(), 2.toIAEntry())
private val siTreeOfTwo: IMCollection<TKVEntry<String, Int>> = FBSTree.of(1.toSAEntry(), 2.toSAEntry())
private val ixTreeOfTwo: IMCollection<TKVEntry<Int, FKSet<*, Int>>> =
  FBSTree.of(FKSet.ofi(1).toIAEntry(), FKSet.ofs(1).toIAEntry())
private val ixxTreeOfTwo: FBSTree<Int, FKSet<Int, RTKVEntry<Int, FKSet<*, Int>>>> = FBSTree.of(
  FKSet.ofi(FKSet.ofi(1).toIAEntry(), FKSet.ofs(2).toIAEntry()).toIAEntry(),
  FKSet.ofi(FKSet.ofi(1).toIAEntry(), FKSet.ofi(2).toIAEntry()).toIAEntry()
)
private val iyxTreeOfTwo: FBSTree<Int, FKSet<Int, RTKVEntry<Int, FKSet<*, Int>>>> = FBSTree.of(
  FKSet.ofi(FKSet.ofi(1).toIAEntry()).toIAEntry(),
  FKSet.ofi(FKSet.ofi(1).toIAEntry(), FKSet.ofi(2).toIAEntry()).toIAEntry()
)
private val ixxsTreeOfTwo: FBSTree<Int, Set<RTKVEntry<Int, FKSet<*, Int>>>> = FBSTree.of(
  setOf(FKSet.ofi(1).toIAEntry(), FKSet.ofs(2).toIAEntry()).toIAEntry(),
  setOf(FKSet.ofi(1).toIAEntry(), FKSet.ofi(2).toIAEntry()).toIAEntry()
)

private val mmI2S = mutableMapOf((1 to "1"), (2 to "2"))
private val mmS2I = mutableMapOf(("1" to 1), ("2" to 2))
private val mmI2I = mutableMapOf((1 to 1), (2 to 2))

private val imTreeOfTwoA = FBSTree.of(mmS2I.toIAEntry(),mmI2I.toIAEntry())
private val imTreeOfTwoB = FBSTree.of(mmI2S.toIAEntry(), mmI2I.toIAEntry())
private val imTreeOfTwoC = FBSTree.ofvi( emptyMap<Int, Int>() )
private val imTreeOfOneOK =  FBSTree.of( mmI2I.toIAEntry() )

private val imTreeOfTwoOK = run {
  val mm1 = mutableMapOf((1 to 1), (2 to 2))
  return@run FBSTree.of( // FBSTree is OK
    mm1.toIAEntry(),
    mmI2I.toIAEntry(),
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
  return@run FBSTree.of(
    mm1.toIAEntry(),
    mm2.toIAEntry()
  )
}

class FBSTreeIMCollectionTest : FunSpec({

  val repeatsMid = Pair(25, 100)
  val repeatsHigh = Pair(50, 100)

  beforeTest {}

  test("fall") {
    iiTreeOfNone.fall { true } shouldBe true
    iiTreeOfNone.fall { false } shouldBe true
    Arb.fbstree(Arb.int(1..repeatsHigh.second)).checkAll(repeatsHigh.first) { fbst ->
      val ts = fbst.size
      val aut = fbst as IMCollection<TKVEntry<Int,Int>>
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
    Arb.fbstree(Arb.int(1..repeatsHigh.second)).checkAll(repeatsHigh.first) { fbst ->
      val ts = fbst.size
      val aut = fbst as IMCollection<TKVEntry<Int,Int>>
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

  test("fcontains (A)") {
    nul<Int, String>().fcontains(zEntry) shouldBe false
    tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
      when (acc) {
        is FLNil -> FLNil
        is FLCons -> {
          val p = t.fcontains(acc.head)
          p shouldBe true
          go(t, acc.tail)
        }
      }
    go(wikiTree, wikiPreorder)
    wikiTree.fcontains(zEntry) shouldBe false
    go(slideShareTree, slideShareBreadthFirst)
    slideShareTree.fcontains(TKVEntry.ofIntKey(100)) shouldBe false
  }

  test("fcontains (B)") {
    iiTreeOfNone.fcontains(TKVEntry.ofkv(1, 1)) shouldBe false
    Arb.fbstree(Arb.int(1..repeatsHigh.second)).checkAll(repeatsHigh.first) { fbst ->
      val ts = fbst.size
      val sample = if (1 < ts) fbst.inorder().fdrop(ts / 2).fhead()!! else null
      val aut = fbst as IMCollection<TKVEntry<Int,Int>>
      aut.fcontains(TKVEntry.ofkv(0, 0)) shouldBe false
      sample?.let { aut.fcontains(sample) shouldBe true }
    }
  }

  test("fcount") {
    iiTreeOfNone.fcount { true } shouldBe 0
    iiTreeOfNone.fcount { false } shouldBe 0
    Arb.fbstree(Arb.int(0..repeatsHigh.second)).checkAll(repeatsHigh.first) { fbst ->
      val mm = fbst.copyToMutableMap()
      val ss = mm.size // size without duplicates
      val ds = fbst.size // size with duplicates
      val aut = fbst as IMCollection<TKVEntry<Int,Int>>
      var tot = 0
      var totDups = 0
      var nonDistinct = 0
      for (entry in mm) {
        val aux = TKVEntry.ofme(entry)
        val counter = aut.fcount { it == aux }
        tot += counter
        if (fbst.fisDup(aux)) {
          nonDistinct += 1
          totDups += counter
        }
      }
      tot shouldBe ds
      (ss + totDups - nonDistinct) shouldBe ds
    }
  }

  test("dropAll (nil)") {
    nul<Int, Int>().fdropAll(FList.emptyIMList<TKVEntry<Int, Int>>()) shouldBe emptyIMBTree()
    nul<Int, Int>().fdropAll(FLCons(1.toIAEntry(), FLNil)) shouldBe emptyIMBTree()
  }

  test("fdropAll") {
    FBSTree.ofvi(1, 2, 3).fdropAll(FList.emptyIMList()) shouldBe FBSTree.ofvi(1, 2, 3)
    FBSTree.ofvi(1, 2, 3).fdropAll(FList.of(1.toIAEntry(), 2.toIAEntry())) shouldBe FBSTree.ofvi(3)
    FBSTree.ofvi(1, 2, 3).fdropAll(iiTreeOfTwo) shouldBe FBSTree.ofvi(3)
    FBSTree.ofvi(1, 2, 3, 4).fdropAll(FList.of(1.toIAEntry(), 2.toIAEntry())) shouldBe FBSTree.ofvi(3, 4)
    FBSTree.ofvi(1, 2, 3).fdropAll(FList.of(2.toIAEntry(), 3.toIAEntry())) shouldBe FBSTree.ofvi(1)
    FBSTree.ofvi(1, 2, 3, 4).fdropAll(FList.of(2.toIAEntry(), 3.toIAEntry())) shouldBe FBSTree.ofvi(1, 4)
    FBSTree.ofvi(1, 2, 3).fdropAll(FList.of(1.toIAEntry(), 3.toIAEntry())) shouldBe FBSTree.ofvi(2)
    FBSTree.ofvi(1, 2, 3, 4).fdropAll(FList.of(1.toIAEntry(), 3.toIAEntry())) shouldBe FBSTree.ofvi(2, 4)
    FBSTree.ofvi(4, 3, 1, 2).fdropAll(FList.of(1.toIAEntry(), 2.toIAEntry())).equals(FBSTree.ofvi(3, 4)) shouldBe true
    FBSTree.ofvi(4, 3, 1, 2).fdropAll(iiTreeOfTwo).equals(FBSTree.ofvi(3, 4)) shouldBe true
  }

  test("dropItem") {
    nul<Int, Int>().fdropItem(1.toIAEntry()) shouldBe emptyIMBTree()

    goAll(wikiTree, wikiPreorder, wikiInorder)
    goAll(wikiTree, wikiInorder, wikiInorder)
    goAll(wikiTree, wikiPostorder, wikiInorder)
    goAll(wikiTree, wikiPreorder.freverse(), wikiInorder)
    goAll(wikiTree, wikiInorder.freverse(), wikiInorder)
    goAll(wikiTree, wikiPostorder.freverse(), wikiInorder)
    wikiTree.fdropItem(zEntry) shouldBe wikiTree
    goTele(wikiTree, wikiPreorder, wikiInorder)
    goTele(wikiTree, wikiInorder, wikiInorder)
    goTele(wikiTree, wikiPostorder, wikiInorder)
    goTele(wikiTree, wikiPreorder.freverse(), wikiInorder)
    goTele(wikiTree, wikiInorder.freverse(), wikiInorder)
    goTele(wikiTree, wikiPostorder.freverse(), wikiInorder)

    goAll(slideShareTree, slideSharePreorder, slideShareInorder)
    goAll(slideShareTree, slideShareInorder, slideShareInorder)
    goAll(slideShareTree, slideSharePostorder, slideShareInorder)
    goAll(slideShareTree, slideShareBreadthFirst, slideShareInorder)
    goAll(slideShareTree, slideSharePreorder.freverse(), slideShareInorder)
    goAll(slideShareTree, slideShareInorder.freverse(), slideShareInorder)
    goAll(slideShareTree, slideSharePostorder.freverse(), slideShareInorder)
    goAll(slideShareTree, slideShareBreadthFirst.freverse(), slideShareInorder)
    slideShareTree.fdropItem(TKVEntry.ofIntKey(100)) shouldBe slideShareTree
    goTele(slideShareTree, slideSharePreorder, slideShareInorder)
    goTele(slideShareTree, slideShareInorder, slideShareInorder)
    goTele(slideShareTree, slideSharePostorder, slideShareInorder)
    goTele(slideShareTree, slideShareBreadthFirst, slideShareInorder)
    goTele(slideShareTree, slideSharePreorder.freverse(), slideShareInorder)
    goTele(slideShareTree, slideShareInorder.freverse(), slideShareInorder)
    goTele(slideShareTree, slideSharePostorder.freverse(), slideShareInorder)
    goTele(slideShareTree, slideShareBreadthFirst.freverse(), slideShareInorder)

    // remove only one
    val aux5a = slideShareTree.finsertDup(slideShareTree.fleftMost()!!, allowDups = true)
    val aux5b = aux5a.finsertDup(slideShareTree.fleftMost()!!, allowDups = true)
    IMBTreeEqual2(aux5b.fdropItem(slideShareTree.fleftMost()!!), aux5a) shouldBe true
  }

  test("ffdropItemAll") {
    nul<Int, Int>().fdropItemAll(1.toIAEntry()) shouldBe emptyIMBTree()
    (nul<Int, Int>().fdropItemAll(1.toIAEntry()) === emptyIMBTree<Int,Int>()) shouldBe true
    tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
      when (acc) {
        is FLNil -> FLNil
        is FLCons -> {
          when (val deleted = t.fdropItemAll(acc.head)) {
            is FBSTNode -> deleted.inorder() shouldBe inorder.ffilterNot { it == acc.head }
            is FBSTNil -> true shouldBe false
          }
          go(t, acc.tail, inorder)
        }
      }
    val aux1 = wikiTree.finsertDup(wikiTree.froot()!!, allowDups = true)
    go(aux1, wikiPreorder, aux1.inorder())
    val aux2 = wikiTree.finsertDup(wikiTree.froot()!!, allowDups = true)
      .finsertDup(wikiTree.froot()!!, allowDups = true)
    go(aux2, wikiPreorder, aux2.inorder())
    val aux3 = wikiTree.finsertDup(wikiTree.fleftMost()!!, allowDups = true)
    go(aux3, wikiPreorder, aux3.inorder())
    val aux4 = wikiTree.finsertDup(wikiTree.frightMost()!!, allowDups = true)
    go(aux4, wikiPreorder, aux4.inorder())
    val aux5 = slideShareTree.finsert(slideShareTree.fleftMost()!!)
      .finsert(slideShareTree.fleftMost()!!)
    go(aux5, slideShareBreadthFirst, aux5.inorder())
    val aux6 = slideShareTree.finsert(slideShareTree.frightMost()!!)
      .finsert(slideShareTree.frightMost()!!)
    go(aux6, slideShareBreadthFirst, aux6.inorder())
  }


  test("fdropItem (property), sorted asc") {
    checkAll(repeatsMid.first, Arb.int(30..repeatsMid.second)) { n ->
      val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      val ix1 = Random.nextInt(0, n)
      val fbsTree = FBSTree.of(values.iterator())
      val aut = fbsTree.fdropItem(TKVEntry.ofIntKey(ix1))
      aut.size shouldBe n - 1
      FBSTree.fbtAssert(aut as FBSTNode<Int, Int>)
      val testOracle = FList.of(values.iterator())
        .ffilterNot { it == TKVEntry.ofIntKey(ix1) }
      aut.inorder() shouldBe testOracle
    }
  }

  test("fdropItem (property), sorted desc") {
    checkAll(repeatsMid.first, Arb.int(30..repeatsMid.second)) { n ->
      val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      val reversed = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      reversed.reverse()
      val ix1 = Random.nextInt(0, n)
      val fbsTree = FBSTree.of(values.iterator())
      val aut = fbsTree.fdropItem(TKVEntry.ofIntKey(ix1))
      aut.size shouldBe n - 1
      FBSTree.fbtAssert(aut as FBSTNode<Int, Int>)
      val testOracle = FList.of(values.iterator())
        .ffilterNot { it == TKVEntry.ofIntKey(ix1) }
      aut.inorder() shouldBe testOracle
    }
  }

  test("fdropItem (property), shuffled") {
    var count = 0
    checkAll(PropTestConfig(iterations = repeatsMid.first), Arb.int(30..repeatsMid.second)) { n ->
      count += 1
      val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      val shuffled = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      val rs = RandomSource.seeded(7979028980642872582)
      shuffled.shuffle(rs.random)
      val randoms = IntArray(n/10) { i: Int -> i }
      randoms.shuffle(rs.random)
      val ix1 = randoms[0]
      val ix2 = randoms[1]
      val ix3 = randoms[2]
      val fbsTree = FBSTree.of(shuffled.iterator())
      val aux0 = fbsTree.fdropItem(TKVEntry.ofIntKey(ix1))
      if (11 == count)
        println("from test file< count=$count")
      val aux1 = aux0.fdropItem(TKVEntry.ofIntKey(ix2))
      val aut = aux1.fdropItem(TKVEntry.ofIntKey(ix3))
      aut.size shouldBe n - 3
      FBSTree.fbtAssert(aut as FBSTNode<Int, Int>)
      val testOracle = FList.of(values.iterator())
        .ffilterNot { it == TKVEntry.ofIntKey(ix1) }
        .ffilterNot { it == TKVEntry.ofIntKey(ix2) }
        .ffilterNot { it == TKVEntry.ofIntKey(ix3) }
      val autInorder = aut.inorder()
      autInorder shouldBe testOracle

      goTele(aut, aut.breadthFirst(), autInorder)
    }
  }

  test("fempty") {
    iiTreeOfNone.fempty() shouldBe true
    FBSTNil.fempty() shouldBe true
    FBSTree.ofvi(1).fempty() shouldBe false
  }

  test("ffilter, filterNot (nil)") {
    nul<Int, Int>().ffilter { true } shouldBe FRBTree.emptyIMBTree()
    (nul<Int, Int>().ffilter { true } === emptyIMBTree<Int,Int>()) shouldBe true
    (nul<Int, Int>().ffilter { true } === FRBTree.emptyIMBTree<Int,Int>()) shouldBe false
    nul<Int, Int>().ffilterNot { false } shouldBe FRBTree.emptyIMBTree()
  }

  test ("ffilter (A), ffilterNot, dropWhen") {
    fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
    fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
    checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
      val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      val svalues = values + values
      val ora1 = values.size
      svalues.size shouldBe (ora1 * 2)
      val tree: IMCollection<TKVEntry<Int,Int>> = FBSTree.of(svalues.iterator())
      tree.fsize() shouldBe ora1

      val sAll1 = tree.ffilter(pickIfLess(ora1))
      val snAll1 = tree.ffilterNot(pickIfLess(ora1))
      snAll1.fsize() shouldBe 0
      sAll1.fsize() shouldBe ora1
      snAll1.equals(tree.fdropWhen(pickIfLess(ora1))) shouldBe true
      val sEmpty1 = tree.ffilter(pickIfMore(ora1))
      val snEmpty1 = tree.ffilterNot(pickIfMore(ora1))
      sEmpty1.fsize() shouldBe 0
      snEmpty1.fsize() shouldBe ora1
      snEmpty1.equals(tree.fdropWhen(pickIfMore(ora1))) shouldBe true

      val ora2 = ora1 / 2
      val theRestSansOra2 = (ora1 - ora2) - 1

      val sAll2 = tree.ffilter(pickIfLess(ora2))
      val snAll2 = tree.ffilterNot(pickIfLess(ora2))
      sAll2.fsize() shouldBe ora2
      snAll2.fsize() shouldBe theRestSansOra2 + 1
      snAll2.equals(tree.fdropWhen(pickIfLess(ora2))) shouldBe true
      val sEmpty2 = tree.ffilter(pickIfMore(ora2))
      val snEmpty2 = tree.ffilterNot(pickIfMore(ora2))
      sEmpty2.fsize() shouldBe theRestSansOra2
      snEmpty2.fsize() shouldBe ora1 - theRestSansOra2
      snEmpty2.equals(tree.fdropWhen(pickIfMore(ora2))) shouldBe true
    }
  }

  test ("ffilter (B)") {
    fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
    fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
    checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
      val shuffled = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      shuffled.shuffle()
      val svalues = shuffled + shuffled
      val ora1 = shuffled.size
      svalues.size shouldBe (ora1 * 2)
      val tree: IMCollection<TKVEntry<Int,Int>> = FBSTree.of(svalues.iterator())
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
    checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
      val reversed = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      reversed.reverse()
      val svalues = reversed + reversed
      val ora1 = reversed.size
      svalues.size shouldBe (ora1 * 2)
      val tree: IMCollection<TKVEntry<Int,Int>> = FBSTree.of(svalues.iterator())
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
    Arb.fbstree(Arb.int(1..repeatsHigh.second)).checkAll(repeatsHigh.first) { fbst ->
      val ts = fbst.size
      var count: Int = 0
      fbst.ffindAny{ count+=1; it == TKVEntry.ofkv(0, 0) } shouldBe null
      count shouldBe ts
      count = 0
      fbst.ffindAny{ count+=1; it > TKVEntry.ofkv(0, 0) } shouldNotBe null
      count shouldBe 1
      count = 0
      fbst.ffindAny{ count+=1; it > repeatsHigh.second.toIAEntry() } shouldBe null
      count shouldBe ts
      count = 0
      fbst.ffindAny{ count+=1; it <= repeatsHigh.second.toIAEntry() } shouldNotBe null
      count shouldBe 1
      count = 0
      if (1 < ts) {
        val sample = fbst.inorder().fdrop(ts / 2).fhead()!!
        count = 0
        fbst.ffindAny{ count+=1; it == sample } shouldNotBe null
        (count >= 1) shouldBe true
        (count <= ts) shouldBe true
      }
    }
  }

  test("ffindAny (B)") {
    tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<A> =
      when (acc) {
        is FLNil -> FLNil
        is FLCons -> {
          if (t.ffindAny{ it == acc.head }?.let{ itAny -> itAny == acc.head } != true)  fail("not found: ${acc.head}")
          go(t, acc.tail)
        }
      }
    go(wikiTree, wikiPreorder)
    go(wikiTree, wikiPostorder)
    go(wikiTree, wikiInorder)
    wikiTree.ffindAny{ it == zEntry } shouldBe null
    go(slideShareTree, slideShareBreadthFirst)
    slideShareTree.ffindAny{ it == TKVEntry.ofIntKey(100) } shouldBe null
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

  test("fpickNotEmpty()") {
    iiTreeOfNone.fpickNotEmpty() shouldBe null
    iiTreeOfTwo.fpickNotEmpty()?.let { it.getk()::class } shouldBe Int::class
    iiTreeOfTwo.fpickNotEmpty()?.let { it.getv()::class } shouldBe Int::class
    siTreeOfTwo.fpickNotEmpty()?.let { it.getk()::class } shouldBe String::class
    siTreeOfTwo.fpickNotEmpty()?.let { it.getv()::class } shouldBe Int::class
    imTreeOfTwoB.fpickNotEmpty()?.equals(mmI2S.toIAEntry()) shouldBe true
    imTreeOfTwoC.fpickNotEmpty() shouldBe null
  }

  test("fpopAndRemainder") {
    val (nilPop, nilRemainder) = emptyIMBTree<Int, Int>().fpopAndRemainder()
    nilPop shouldBe null
    nilRemainder shouldBe emptyIMBTree()

    val (onePop, oneRemainder) = FBSTree.ofvi(1).fpopAndRemainder()
    onePop shouldBe 1.toIAEntry()
    oneRemainder shouldBe emptyIMBTree()

    // this traverses slideShareTree popping one element at a time, and rebuilds the tree with the popped element
    // could probably have been a forEach...  It's always a fold in the end.
    val res = slideShareTree.ffold(Pair(nul<Int, Int>(), slideShareTree.fpopAndRemainder())) { acc, _ ->
      val (rebuild, popAndStub) = acc
      val (pop, stub) = popAndStub
      Pair(rebuild.finsert(pop!!), stub.fpopAndRemainder())
    }
    res.first shouldBe slideShareTree
    val (lastPopped, lastRemainder) = res.second
    lastPopped shouldBe null
    lastRemainder shouldBe emptyIMBTree()
  }

  test("fsize") {
    iiTreeOfNone.fsize() shouldBe 0
    iiTreeOfTwo.fsize() shouldBe 2
  }

  test("fisNested") {
    iiTreeOfNone.fisNested() shouldBe null
    iiTreeOfTwo.fisNested() shouldBe false
    FBSTree.of(FList.emptyIMList<Int>().toIAEntry()).fisNested() shouldBe true
    FBSTree.of(FList.emptyIMList<Int>().toSAEntry()).fisNested() shouldBe true
    FBSTree.ofvi(emptyArray<Int>()).fisNested() shouldBe true
    FBSTree.ofvs(emptyArray<Int>()).fisNested() shouldBe true
    FBSTree.ofvi(setOf<Int>()).fisNested() shouldBe true
    FBSTree.ofvs(setOf<Int>()).fisNested() shouldBe true
    FBSTree.ofvi(*arrayOf(listOf<Int>())).fisNested() shouldBe true
    FBSTree.ofvs(*arrayOf(listOf<Int>())).fisNested() shouldBe true
    FBSTree.ofvi(mapOf<Int, Int>()).fisNested() shouldBe true
    FBSTree.ofvs(mapOf<Int, Int>()).fisNested() shouldBe true
    ixTreeOfTwo.fisNested() shouldBe true
    ixxTreeOfTwo.fisNested() shouldBe true
    ixxsTreeOfTwo.fisNested() shouldBe true
  }
})