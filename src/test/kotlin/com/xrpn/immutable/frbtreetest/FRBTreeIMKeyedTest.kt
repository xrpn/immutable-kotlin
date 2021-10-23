package com.xrpn.immutable.frbtreetest

import com.xrpn.imapi.IMCollection
import com.xrpn.imapi.IMKeyed
import com.xrpn.immutable.*
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.emptyArrayOfStr
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.frbtree

private val iiTreeOfNone: IMKeyed<Int,Int> = FRBTree.nul<Int,Int>()
private val iiTreeOfTwo: IMKeyed<Int,Int> = FRBTree.of(1.toIAEntry(), 2.toIAEntry())
private val ixTreeOfTwo: IMKeyed<Int, FKSet<*, Int>> = FRBTree.of(FKSet.ofi(1).toIAEntry(), FKSet.ofs(1).toIAEntry())
private val ixxTreeOfTwo: FRBTree<Int, FKSet<Int, RTKVEntry<Int, FKSet<*, Int>>>> = FRBTree.of(
  FKSet.ofi(FKSet.ofi(1).toIAEntry(), FKSet.ofs(1).toIAEntry()).toIAEntry(),
  FKSet.ofi(FKSet.ofi(1).toIAEntry(), FKSet.ofs(1).toIAEntry()).toIAEntry()
)
private val ixxxTreeOfTwo: FRBTree<Int, Set<RTKVEntry<Int, FKSet<*, Int>>>> = FRBTree.of(
  setOf(FKSet.ofi(1).toIAEntry(), FKSet.ofs(1).toIAEntry()).toIAEntry(),
  setOf(FKSet.ofi(1).toIAEntry(), FKSet.ofs(1).toIAEntry()).toIAEntry()
)

class FRBTreeIMKeyedTest : FunSpec({

  // val repeatsHigh = Pair(50, 100)

  beforeTest {}

  test("fcontainsKey") {
    FRBTree.nul<Int, String>().fcontainsKey(zEntry.getk()) shouldBe false
    tailrec fun <A: Comparable<A>, B: Any> go(t: FRBTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
      when (acc) {
        is FLNil -> FLNil
        is FLCons -> {
          (t.fcontainsKey(acc.head.getk())) shouldBe true
          go(t, acc.tail)
        }
      }
    go(frbWikiTree, frbWikiPreorder)
    frbWikiTree.fcontainsKey(zEntry.getk()) shouldBe false
    go(frbSlideShareTree, frbSlideShareBreadthFirst)
    frbSlideShareTree.fcontainsKey(100) shouldBe false
  }

  test("fcontainsValue") {
    FRBTree.nul<Int, String>().fcontainsValue(zEntry.getv()) shouldBe false
    tailrec fun <A: Comparable<A>, B: Any> go(t: FRBTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
      when (acc) {
        is FLNil -> FLNil
        is FLCons -> {
          (t.fcontainsValue(acc.head.getv())) shouldBe true
          go(t, acc.tail)
        }
      }
    go(frbWikiTree, frbWikiPreorder)
    frbWikiTree.fcontainsValue(zEntry.getv()) shouldBe false
    go(frbSlideShareTree, frbSlideShareBreadthFirst)
    frbSlideShareTree.fcontainsValue(100) shouldBe false
  }

  test("fcountValue") {}
  test("ffilterKey") {}
  test("ffilterKeyNot") {}
  test("ffilterValue") {}
  test("ffilterValueNot") {}
  test("fget") {}
  test("fgetOrElse") {}
  test("fgetOrThrow") {}
  test("fpickKey") {}

})
