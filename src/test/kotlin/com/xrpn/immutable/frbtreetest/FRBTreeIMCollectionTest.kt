package com.xrpn.immutable.frbtreetest

import com.xrpn.imapi.IMBTreeUtility
import com.xrpn.imapi.IMCollection
import com.xrpn.immutable.*
import com.xrpn.immutable.FRBTree.Companion.emptyIMBTree
import com.xrpn.immutable.FRBTree.Companion.nul
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.frbtree
import kotlin.random.Random

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

private val mmI2S = mutableMapOf((1 to "1"), (2 to "2"))
private val mmS2I = mutableMapOf(("1" to 1), ("2" to 2))
private val mmI2I = mutableMapOf((1 to 1), (2 to 2))

private val imTreeOfTwoA = FRBTree.of(mmS2I.toIAEntry(),mmI2I.toIAEntry())
private val imTreeOfTwoB = FRBTree.of(mmI2S.toIAEntry(), mmI2I.toIAEntry())
private val imTreeOfTwoC = FRBTree.ofvi( emptyMap<Int, Int>() )
private val imTreeOfOneOK =  FRBTree.of( mmI2I.toIAEntry() )

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
  return@run FRBTree.of(
    mm1.toIAEntry(),
    mm2.toIAEntry()
  )
}

class FRBTreeIMCollectionTest : FunSpec({

  val repeatsMid = Pair(25, 100)
  val repeatsHigh = Pair(50, 100)

  beforeTest {}

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

  test("dropAll (nil)") {
    FBSTree.nul<Int, Int>().fdropAll(FList.emptyIMList<TKVEntry<Int, Int>>()) shouldBe FRBTree.emptyIMBTree()
    FBSTree.nul<Int, Int>().fdropAll(FLCons(1.toIAEntry(), FLNil)) shouldBe FRBTree.emptyIMBTree()
  }

  test("fdropAll") {
    FRBTree.ofvi(1, 2, 3).fdropAll(FList.emptyIMList()) shouldBe FRBTree.ofvi(1, 2, 3)
    FRBTree.ofvi(1, 2, 3).fdropAll(FList.of(1.toIAEntry(), 2.toIAEntry())) shouldBe FRBTree.ofvi(3)
    FRBTree.ofvi(1, 2, 3).fdropAll(iiTreeOfTwo) shouldBe FRBTree.ofvi(3)
    FRBTree.ofvi(1, 2, 3, 4).fdropAll(FList.of(1.toIAEntry(), 2.toIAEntry())) shouldBe FRBTree.ofvi(3, 4)
    FRBTree.ofvi(1, 2, 3).fdropAll(FList.of(2.toIAEntry(), 3.toIAEntry())) shouldBe FRBTree.ofvi(1)
    FRBTree.ofvi(1, 2, 3, 4).fdropAll(FList.of(2.toIAEntry(), 3.toIAEntry())) shouldBe FRBTree.ofvi(1, 4)
    FRBTree.ofvi(1, 2, 3).fdropAll(FList.of(1.toIAEntry(), 3.toIAEntry())) shouldBe FRBTree.ofvi(2)
    FRBTree.ofvi(1, 2, 3, 4).fdropAll(FList.of(1.toIAEntry(), 3.toIAEntry())) shouldBe FRBTree.ofvi(2, 4)
    FRBTree.ofvi(4, 3, 1, 2).fdropAll(FList.of(1.toIAEntry(), 2.toIAEntry())).equals(FRBTree.ofvi(3, 4)) shouldBe true
    FRBTree.ofvi(4, 3, 1, 2).fdropAll(iiTreeOfTwo).equals(FRBTree.ofvi(3, 4)) shouldBe true
  }

  test("fdropItem") {
    FRBTree.nul<Int, Int>().fdropItem(1.toIAEntry()) shouldBe FRBTree.emptyIMBTree()

    tailrec fun goAllWiki(frb: FRBTree<Int, String>, acc: FList<TKVEntry<Int, String>>, inorder: FList<TKVEntry<Int, String>>): FList<TKVEntry<Int, String>> =
      when (acc) {
        is FLNil -> FLNil
        is FLCons -> {
          val rbDeleted: RBTree<Int, String> = rbWikiTree.copy()
          rbDeleted.rbDelete(TKVEntry.ofkv(acc.head.getk(), acc.head.getv()))
          when (val deleted = frb.fdropItem(acc.head)) {
            is FRBTNode -> {
              FRBTree.rbRootInvariant(deleted) shouldBe true
              val aut1in = deleted.inorder()
              val oracle = inorder.ffilterNot { it == acc.head }
              aut1in shouldBe oracle
              IMBTreeUtility.strongEqual(deleted, rbDeleted) shouldBe true
            }
            is FRBTNil -> {
              true shouldBe false
            }
          }
          goAllWiki(frb, acc.tail, inorder)
        }
      }

    // tailrec fun <A: Comparable<A>, B: Any> goAllSS(frb: FRBTree<A,B>, acc: FList<TKVEntry<A,B>>, inorder: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> =
    tailrec fun goAllSS(frb: FRBTree<Int, Int>, acc: FList<TKVEntry<Int, Int>>, inorder: FList<TKVEntry<Int, Int>>): FList<TKVEntry<Int, Int>> =
      when (acc) {
        is FLNil -> FLNil
        is FLCons -> {
          val rbDeleted: RBTree<Int, Int> = rbSlideShareTree.copy()
          rbDeleted.rbDelete(TKVEntry.ofkk(acc.head.getk(), acc.head.getv()))
          when (val deleted = frb.fdropItem(acc.head)) {
            is FRBTNode -> {
              FRBTree.rbRootInvariant(deleted) shouldBe true
              val aut1in = deleted.inorder()
              val oracle = inorder.ffilterNot { it == acc.head }
              aut1in shouldBe oracle
              IMBTreeUtility.strongEqual(deleted, rbDeleted) shouldBe true
            }
            is FRBTNil -> {
              true shouldBe false
            }
          }
          goAllSS(frb, acc.tail, inorder)
        }
      }

    tailrec fun <A: Comparable<A>, B: Any> goTele(t: FRBTree<A, B>, rbDeleted: RBTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
      when (acc) {
        is FLNil -> FLNil
        is FLCons -> {
          rbDeleted.rbDelete(acc.head)
          val deleted = t.fdropItem(acc.head)
          val oracle = inorder.ffilterNot { it == acc.head }
          when (deleted) {
            is FRBTNode -> {
              FRBTree.rbRootInvariant(deleted) shouldBe true
              val aut1in = deleted.inorder()
              aut1in shouldBe oracle
              IMBTreeUtility.strongEqual(deleted, rbDeleted) shouldBe true
            }
            is FRBTNil -> {
              deleted.size shouldBe 0
              rbDeleted.size() shouldBe 0
            }
          }
          goTele(deleted, rbDeleted, acc.tail, oracle)
        }
      }
    goAllWiki(frbWikiTree, frbWikiPreorder, frbWikiInorder)
    goAllWiki(frbWikiTree, frbWikiPostorder, frbWikiInorder)
    goAllWiki(frbWikiTree, frbWikiInorder, frbWikiInorder)
    goAllWiki(frbWikiTree, frbWikiBreadthFirst, frbWikiInorder)
    goAllWiki(frbWikiTree, frbWikiPreorder.freverse(), frbWikiInorder)
    goAllWiki(frbWikiTree, frbWikiPostorder.freverse(), frbWikiInorder)
    goAllWiki(frbWikiTree, frbWikiInorder.freverse(), frbWikiInorder)
    goAllWiki(frbWikiTree, frbWikiBreadthFirst.freverse(), frbWikiInorder)
    var rbMutable = rbWikiTree.copy()
    goTele(frbWikiTree, rbMutable, frbWikiPreorder, frbWikiInorder)
    rbMutable = rbWikiTree.copy()
    goTele(frbWikiTree, rbMutable, frbWikiPostorder, frbWikiInorder)
    rbMutable = rbWikiTree.copy()
    goTele(frbWikiTree, rbMutable, frbWikiInorder, frbWikiInorder)
    rbMutable = rbWikiTree.copy()
    goTele(frbWikiTree, rbMutable, frbWikiBreadthFirst, frbWikiInorder)
    rbMutable = rbWikiTree.copy()
    goTele(frbWikiTree, rbMutable, frbWikiPreorder.freverse(), frbWikiInorder)
    rbMutable = rbWikiTree.copy()
    goTele(frbWikiTree, rbMutable, frbWikiPostorder.freverse(), frbWikiInorder)
    rbMutable = rbWikiTree.copy()
    goTele(frbWikiTree, rbMutable, frbWikiInorder.freverse(), frbWikiInorder)
    rbMutable = rbWikiTree.copy()
    goTele(frbWikiTree, rbMutable, frbWikiBreadthFirst.freverse(), frbWikiInorder)

    frbWikiTree.fdropItem(zEntry) shouldBe frbWikiTree

    goAllSS(frbSlideShareTree, frbSlideSharePreorder, frbSlideShareInorder)
    goAllSS(frbSlideShareTree, frbSlideSharePostorder, frbSlideShareInorder)
    goAllSS(frbSlideShareTree, frbSlideShareInorder, frbSlideShareInorder)
    goAllSS(frbSlideShareTree, frbSlideShareBreadthFirst, frbSlideShareInorder)
    goAllSS(frbSlideShareTree, frbSlideSharePreorder.freverse(), frbSlideShareInorder)
    goAllSS(frbSlideShareTree, frbSlideSharePostorder.freverse(), frbSlideShareInorder)
    goAllSS(frbSlideShareTree, frbSlideShareInorder.freverse(), frbSlideShareInorder)
    goAllSS(frbSlideShareTree, frbSlideShareBreadthFirst.freverse(), frbSlideShareInorder)
    var rbMutableSs = rbSlideShareTree.copy()
    goTele(frbSlideShareTree, rbMutableSs, frbSlideSharePreorder, frbSlideShareInorder)
    rbMutableSs = rbSlideShareTree.copy()
    goTele(frbSlideShareTree, rbMutableSs, frbSlideSharePostorder, frbSlideShareInorder)
    rbMutableSs = rbSlideShareTree.copy()
    goTele(frbSlideShareTree, rbMutableSs, frbSlideShareInorder, frbSlideShareInorder)
    rbMutableSs = rbSlideShareTree.copy()
    goTele(frbSlideShareTree, rbMutableSs, frbSlideShareBreadthFirst, frbSlideShareInorder)
    rbMutableSs = rbSlideShareTree.copy()
    goTele(frbSlideShareTree, rbMutableSs, frbSlideSharePreorder.freverse(), frbSlideShareInorder)
    rbMutableSs = rbSlideShareTree.copy()
    goTele(frbSlideShareTree, rbMutableSs, frbSlideSharePostorder.freverse(), frbSlideShareInorder)
    rbMutableSs = rbSlideShareTree.copy()
    goTele(frbSlideShareTree, rbMutableSs, frbSlideShareInorder.freverse(), frbSlideShareInorder)
    rbMutableSs = rbSlideShareTree.copy()
    goTele(frbSlideShareTree, rbMutableSs, frbSlideShareBreadthFirst.freverse(), frbSlideShareInorder)

    frbSlideShareTree.fdropItem(TKVEntry.ofIntKey(100)) shouldBe frbSlideShareTree
  }

  test("fdropItem (property), sorted asc") {
    checkAll(repeatsMid.first, Arb.int(30..repeatsMid.second)) { n ->
      val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      val ix1 = Random.nextInt(0, n)
      val frbTree = FRBTree.of(values.iterator())
      val aut = frbTree.fdropItem(TKVEntry.ofIntKey(ix1))
      aut.size shouldBe n - 1
      FRBTree.rbRootInvariant(aut) shouldBe true
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
      val frbTree = FRBTree.of(values.iterator())
      val aut = frbTree.fdropItem(TKVEntry.ofIntKey(ix1))
      aut.size shouldBe n - 1
      FRBTree.rbRootInvariant(aut) shouldBe true
      val testOracle = FList.of(values.iterator())
        .ffilterNot { it == TKVEntry.ofIntKey(ix1) }
      aut.inorder() shouldBe testOracle
    }
  }

  test("fdropItem (property), shuffled") {

    checkAll(PropTestConfig(iterations = repeatsMid.first), Arb.int(30..repeatsMid.second)) { n ->
      val values = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      val shuffled = Array(n) { i: Int -> TKVEntry.ofkk(i, i) }
      val rs = RandomSource.seeded(7979028980642872582)
      shuffled.shuffle(rs.random)
      val randoms = IntArray(n/10) { i: Int -> i }
      randoms.shuffle(rs.random)
      val ix1 = randoms[0]
      val ix2 = randoms[1]
      val ix3 = randoms[2]
      val frbTree = FRBTree.of(shuffled.iterator())
      val aux0 = frbTree.fdropItem(TKVEntry.ofIntKey(ix1))
      val aux1 = aux0.fdropItem(TKVEntry.ofIntKey(ix2))
      val aut = aux1.fdropItem(TKVEntry.ofIntKey(ix3))
      aut.size shouldBe n - 3
      FRBTree.rbRootInvariant(aut) shouldBe true
      val testOracle = FList.of(values.iterator())
        .ffilterNot { it == TKVEntry.ofIntKey(ix1) }
        .ffilterNot { it == TKVEntry.ofIntKey(ix2) }
        .ffilterNot { it == TKVEntry.ofIntKey(ix3) }
      val autInorder = aut.inorder()
      autInorder shouldBe testOracle

      goDropItemTele(aut, aut.breadthFirst(), autInorder)
    }
  }

  test("fempty") {
    iiTreeOfNone.fempty() shouldBe true
    FRBTNil.fempty() shouldBe true
    FRBTree.ofvi(1).fempty() shouldBe false
  }

  test("ffilter, filterNot (nil)") {
    nul<Int, Int>().ffilter { true } shouldBe emptyIMBTree()
    (nul<Int, Int>().ffilter { true } === emptyIMBTree<Int,Int>()) shouldBe true
    nul<Int, Int>().ffilterNot { false } shouldBe emptyIMBTree()
  }

  test ("ffilter (A), ffilterNot, dropWhen") {
    fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
    fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
    checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
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
    checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
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
      frbt.ffindAny{ count+=1; it <= repeatsHigh.second.toIAEntry() } shouldNotBe null
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
    val (nilPop, nilRemainder) = FRBTree.emptyIMBTree<Int, Int>().fpopAndRemainder()
    nilPop shouldBe null
    nilRemainder shouldBe FRBTree.emptyIMBTree()

    val (onePop, oneRemainder) = FRBTree.ofvi(1).fpopAndRemainder()
    onePop shouldBe 1.toIAEntry()
    oneRemainder shouldBe FRBTree.emptyIMBTree()

    // this traverses slideShareTree popping one element at a time, and rebuilds the tree with the popped element
    // could probably have been a forEach...  It's always a fold in the end.
    val res = frbSlideShareTree.ffold(Pair(FRBTree.nul<Int, Int>(), frbSlideShareTree.fpopAndRemainder())) { acc, _ ->
      val (rebuild, popAndStub) = acc
      val (pop, stub) = popAndStub
      Pair(rebuild.finsert(pop!!), stub.fpopAndRemainder())
    }
    res.first shouldBe slideShareTree
    val (lastPopped, lastRemainder) = res.second
    lastPopped shouldBe null
    lastRemainder shouldBe FRBTree.emptyIMBTree()
  }

  test("fsize") {
    iiTreeOfNone.fsize() shouldBe 0
    iiTreeOfTwo.fsize() shouldBe 2
  }

  test("fisNested") {
    iiTreeOfNone.fisNested() shouldBe null
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
