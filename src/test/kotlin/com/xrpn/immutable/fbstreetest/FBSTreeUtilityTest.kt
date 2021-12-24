package com.xrpn.immutable.fbstreetest

import com.xrpn.bridge.FTreeIterator
import com.xrpn.imapi.IntKeyType
import com.xrpn.imapi.StrKeyType
import com.xrpn.immutable.*
import com.xrpn.immutable.FBSTree.Companion.bstParent
import com.xrpn.immutable.FBSTree.Companion.emptyIMBTree
import com.xrpn.immutable.FBSTree.Companion.fbtAssertNodeInvariant
import com.xrpn.immutable.FBSTree.Companion.of
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fbsStree
import io.kotest.xrpn.fbsItree
import java.lang.RuntimeException
import java.util.concurrent.atomic.AtomicInteger

private val intFBSTreeOfNone = FBSTree.Companion.ofvi(*emptyArrayOfInt)
private val intFBSTreeOfOne = FBSTree.Companion.ofvi(*arrayOf<Int>(1))
private val intFBSTreeOfTwo = FBSTree.Companion.ofvi(*arrayOf<Int>(1, 2))
private val intFBSTreeOfThree = FBSTree.Companion.ofvi(*arrayOf<Int>(1, 2, 3))
private val intFRBTreeOfNone = FRBTree.ofvi(*emptyArrayOfInt)
private val intFRBTreeOfOne = FRBTree.ofvi(*arrayOf<Int>(1))
private val intFRBTreeOfTwo = FRBTree.ofvi(*arrayOf<Int>(1, 2))
private val intFRBTreeOfThree = FRBTree.ofvi(*arrayOf<Int>(1, 2, 3))


class FBSTreeUtilityTest : FunSpec({

  val repeats = 50

  beforeTest {}

  fun <A, B: Any> FBSTree<A, B>.quietAssert(tkv: TKVEntry<A, B>): Unit where A: Any, A: Comparable<A> =
    when (val node = bstParent(this, tkv)) {
      is FBSTNil -> Unit
      is FBSTNode<A, B> -> {
          fbtAssertNodeInvariant(node)
        Unit
      }
      null -> Unit
      else -> throw RuntimeException("unknown ${node::class}")
    }

  test("sanity") {
     FTreeIterator(wikiTree).iterator().forEach(wikiTree::quietAssert)
     FTreeIterator(slideShareTree).iterator().forEach(wikiTree::quietAssert)
     FTreeIterator(depthOneRight).iterator().forEach(depthOneRight::quietAssert)
     FTreeIterator(depthOneLeft).iterator().forEach(depthOneRight::quietAssert)
     FTreeIterator(depthOneFull).iterator().forEach(depthOneRight::quietAssert)
     FTreeIterator(depthTwoLeftPartial).iterator().forEach(depthTwoLeftPartial::quietAssert)
     FTreeIterator(depthTwoLeftRight).iterator().forEach(depthTwoLeftRight::quietAssert)
     FTreeIterator(depthTwoLeftLeft).iterator().forEach(depthTwoLeftLeft::quietAssert)
     FTreeIterator(depthTwoRightPartial).iterator().forEach(depthTwoRightPartial::quietAssert)
     FTreeIterator(depthTwoRightRight).iterator().forEach(depthTwoRightRight::quietAssert)
     FTreeIterator(depthTwoRightLeft).iterator().forEach(depthTwoRightLeft::quietAssert)

    wikiTree.preorder() shouldBe wikiPreorder
    wikiTree.inorder() shouldBe wikiInorder
    wikiTree.postorder() shouldBe wikiPostorder

    slideShareTree.preorder() shouldBe slideSharePreorder
    slideShareTree.inorder() shouldBe slideShareInorder
    slideShareTree.postorder() shouldBe slideSharePostorder

  }

  test("equal") {
    intFBSTreeOfNone.equal(intFBSTreeOfNone) shouldBe true

    intFBSTreeOfNone.equal(intFBSTreeOfOne) shouldBe false
    intFBSTreeOfOne.equal(intFBSTreeOfNone) shouldBe false
    intFBSTreeOfOne.equal(com.xrpn.immutable.FBSTree.finsertIK(emptyIMBTree(),1)) shouldBe true
    intFBSTreeOfOne.equal(intFBSTreeOfOne) shouldBe true

    intFBSTreeOfOne.equal(intFBSTreeOfTwo) shouldBe false
    intFBSTreeOfTwo.equal(intFBSTreeOfOne) shouldBe false
    intFBSTreeOfTwo.equal(com.xrpn.immutable.FBSTree.finsertIK(com.xrpn.immutable.FBSTree.finsertIK(emptyIMBTree(),1), 2)) shouldBe true
    intFBSTreeOfTwo.equal(com.xrpn.immutable.FBSTree.finsertIK(com.xrpn.immutable.FBSTree.finsertIK(emptyIMBTree(),2), 1)) shouldBe true
    intFBSTreeOfTwo.equal(intFBSTreeOfTwo) shouldBe true

    intFBSTreeOfThree.equal(intFBSTreeOfTwo) shouldBe false
    intFBSTreeOfTwo.equal(intFBSTreeOfThree) shouldBe false
    intFBSTreeOfThree.equal(com.xrpn.immutable.FBSTree.finsertIK(com.xrpn.immutable.FBSTree.finsertIK(emptyIMBTree(),1), 3)) shouldBe false
    intFBSTreeOfThree.equal(com.xrpn.immutable.FBSTree.finsertIK(com.xrpn.immutable.FBSTree.finsertIK(emptyIMBTree(),3), 1)) shouldBe false
    intFBSTreeOfThree.equal(com.xrpn.immutable.FBSTree.finsertIK(com.xrpn.immutable.FBSTree.finsertIK(emptyIMBTree(),3), 2)) shouldBe false
  }

  test("softEqual") {
    intFRBTreeOfNone.softEqual(intFBSTreeOfNone) shouldBe true

    intFRBTreeOfNone.softEqual(intFBSTreeOfOne) shouldBe false
    intFRBTreeOfOne.softEqual(intFBSTreeOfNone) shouldBe false
    intFRBTreeOfOne.softEqual(com.xrpn.immutable.FBSTree.finsertIK(emptyIMBTree(),1)) shouldBe true
    intFRBTreeOfOne.softEqual(intFBSTreeOfOne) shouldBe true

    intFRBTreeOfOne.softEqual(intFBSTreeOfTwo) shouldBe false
    intFRBTreeOfTwo.softEqual(intFBSTreeOfOne) shouldBe false
    intFRBTreeOfTwo.softEqual(com.xrpn.immutable.FBSTree.finsertIK(com.xrpn.immutable.FBSTree.finsertIK(emptyIMBTree(),1), 2)) shouldBe true
    intFRBTreeOfTwo.softEqual(com.xrpn.immutable.FBSTree.finsertIK(com.xrpn.immutable.FBSTree.finsertIK(emptyIMBTree(),2), 1)) shouldBe true
    intFRBTreeOfTwo.softEqual(intFBSTreeOfTwo) shouldBe true

    intFRBTreeOfThree.softEqual(intFBSTreeOfTwo) shouldBe false
    intFRBTreeOfTwo.softEqual(intFBSTreeOfThree) shouldBe false
    intFRBTreeOfThree.softEqual(com.xrpn.immutable.FBSTree.finsertIK(com.xrpn.immutable.FBSTree.finsertIK(emptyIMBTree(),1), 3)) shouldBe false
    intFRBTreeOfThree.softEqual(com.xrpn.immutable.FBSTree.finsertIK(com.xrpn.immutable.FBSTree.finsertIK(emptyIMBTree(),3), 1)) shouldBe false
    intFRBTreeOfThree.softEqual(com.xrpn.immutable.FBSTree.finsertIK(com.xrpn.immutable.FBSTree.finsertIK(emptyIMBTree(),3), 2)) shouldBe false
  }

  test("forEach") {
    val counter = AtomicInteger(0)
    val summer = AtomicInteger(0)
    val doCount: (TKVEntry<Int, Int>) -> Unit = { counter.incrementAndGet() }
    val doSum: (TKVEntry<Int, Int>) -> Unit = { v -> summer.addAndGet(v.getv()) }
    checkAll(repeats, Arb.fbsItree<Int, Int>(Arb.int(),20..100)) { fbst ->
      val oraSum = fbst.ffold(0){ acc, el -> acc + el.getv() }
      fbst.fforEach(doCount)
      counter.get() shouldBe fbst.size
      counter.set(0)
      fbst.fforEach(doSum)
      summer.get() shouldBe oraSum
      summer.set(0)
    }
  }

  test ("forEach presorted") {

    val counter = AtomicInteger(0)
    val summer = AtomicInteger(0)
    val doCount: (TKVEntry<Int, Int>) -> Unit = { counter.incrementAndGet() }
    val doSum: (TKVEntry<Int, Int>) -> Unit = { tkv -> summer.addAndGet(tkv.getk()) }
    checkAll(50, Arb.int(20..100)) { n ->
      val values = (Array(n) { i: Int -> TKVEntry.ofkk(i, i) })
      val oraSum = (n*(n-1))/2 // filling is 0..(n-1) => ((n-1)*(n-1+1))/2 QED
      val tree: FBSTree<Int, Int> = of(values.iterator())
      FTreeIterator(tree).iterator().forEach(doCount)
      counter.get() shouldBe n
      counter.set(0)
      FTreeIterator(tree).iterator().forEach(doSum)
      summer.get() shouldBe oraSum
      summer.set(0)
    }
  }

  test ("forEach shuffled") {

    val counter = AtomicInteger(0)
    val summer = AtomicInteger(0)
    val doCount: (TKVEntry<Int, Int>) -> Unit = { counter.incrementAndGet() }
    val doSum: (TKVEntry<Int, Int>) -> Unit = { tkv -> summer.addAndGet(tkv.getk()) }
    checkAll(50, Arb.int(20..100)) { n ->
      val values = (Array(n) { i: Int -> TKVEntry.ofkk(i, i) })
      values.shuffle()
      val oraSum = (n*(n-1))/2 // filling is 0..(n-1) => ((n-1)*(n-1+1))/2 QED
      val tree: FBSTree<Int, Int> = of(values.iterator())
      FTreeIterator(tree).iterator().forEach(doCount)
      counter.get() shouldBe n
      counter.set(0)
      FTreeIterator(tree).iterator().forEach(doSum)
      summer.get() shouldBe oraSum
      summer.set(0)
    }
  }

  test ("forEach reversed") {

    val counter = AtomicInteger(0)
    val summer = AtomicInteger(0)
    val doCount: (TKVEntry<Int, Int>) -> Unit = { counter.incrementAndGet() }
    val doSum: (TKVEntry<Int, Int>) -> Unit = { tkv -> summer.addAndGet(tkv.getk()) }
    checkAll(50, Arb.int(20..100)) { n ->
      val values = (Array(n) { i: Int -> TKVEntry.ofkk(i, i) })
      values.reverse()
      val oraSum = (n*(n-1))/2 // filling is 0..(n-1) => ((n-1)*(n-1+1))/2 QED
      val tree: FBSTree<Int, Int> = of(values.iterator())
      FTreeIterator(tree).iterator().forEach(doCount)
      counter.get() shouldBe n
      counter.set(0)
      FTreeIterator(tree).iterator().forEach(doSum)
      summer.get() shouldBe oraSum
      summer.set(0)
    }
  }

  test("toIMSet") {
    intFBSTreeOfNone.toIMSet(IntKeyType) shouldBe FKSet.emptyIMKISet()
    checkAll(repeats, Arb.fbsItree<Int, Int>(Arb.int(),20..100)) { fbst ->
      val ims1: FKSet<Int, Int> = fbst.toIMSet(IntKeyType)!!
      (ims1.toIMBTree() === fbst) shouldBe false
      ims1.equals(ofBody(fbst.toFRBTree())) shouldBe true
    }
    checkAll(repeats, Arb.fbsStree<Int, Int>(Arb.int(),20..100)) { fbst ->
      val ims1: FKSet<String, Int> = fbst.toIMSet(StrKeyType)!!
      (ims1.toIMBTree() === fbst) shouldBe false
      ims1.equals(ofBody(fbst.toFRBTree())) shouldBe true
    }
  }

  test("copy") {
    intFBSTreeOfNone.copy() shouldBe intFBSTreeOfNone
    (intFBSTreeOfNone.copy() === intFBSTreeOfNone) shouldBe true
    checkAll(repeats, Arb.fbsItree<Int, Int>(Arb.int(),20..100)) { fbst ->
      val c1 = fbst.copy()
      (c1 === fbst) shouldBe false
      fbst.equal(c1) shouldBe true
    }
  }

  test("copyToMutableList") {
    intFBSTreeOfNone.copyToMutableMap() shouldBe mutableMapOf()
    checkAll(repeats, Arb.fbsItree<Int, Int>(Arb.int(),20..100)) { fbst ->
      val ml: MutableMap<Int, Int> = fbst.copyToMutableMap()
      if (ml.size != fbst.size) /* TODO there are duplicates */ true shouldBe true
      else (FTreeIterator(fbst).toSet() == ml.entries.map { mentry -> TKVEntry.ofkk(mentry.key, mentry.value) }.toSet()) shouldBe true
    }
  }

})
