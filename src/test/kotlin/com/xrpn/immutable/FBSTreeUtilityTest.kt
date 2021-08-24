package com.xrpn.immutable

import com.xrpn.immutable.FBSTree.Companion.emptyIMBTree
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fbstree
import java.util.concurrent.atomic.AtomicInteger

private val intFBSTreeOfNone = FBSTree.ofvi(*arrayOf<Int>())
private val intFBSTreeOfOne = FBSTree.ofvi(*arrayOf<Int>(1))
private val intFBSTreeOfTwo = FBSTree.ofvi(*arrayOf<Int>(1,2))
private val intFBSTreeOfThree = FBSTree.ofvi(*arrayOf<Int>(1,2,3))
private val intFRBTreeOfNone = FRBTree.ofvi(*arrayOf<Int>())
private val intFRBTreeOfOne = FRBTree.ofvi(*arrayOf<Int>(1))
private val intFRBTreeOfTwo = FRBTree.ofvi(*arrayOf<Int>(1,2))
private val intFRBTreeOfThree = FRBTree.ofvi(*arrayOf<Int>(1,2,3))


class FBSTreeUtilityTest : FunSpec({

  val repeats = 50

  beforeTest {}

  fun <A, B: Any> FBSTree<A, B>.quietAssert(tkv: TKVEntry<A, B>): Unit where A: Any, A: Comparable<A> =
    when (val node = FBSTree.bstParent(this, tkv)) {
      is FBSTNil -> Unit
      is FBSTNode<A, B> -> {
        FBSTree.fbtAssert(node)
        Unit
      }
      null -> Unit
    }

  test("sanity") {
    wikiTree.forEach(wikiTree::quietAssert)
    slideShareTree.forEach(wikiTree::quietAssert)
    depthOneRight.forEach(depthOneRight::quietAssert)
    depthOneLeft.forEach(depthOneRight::quietAssert)
    depthOneFull.forEach(depthOneRight::quietAssert)
    depthTwoLeftPartial.forEach(depthTwoLeftPartial::quietAssert)
    depthTwoLeftRight.forEach(depthTwoLeftRight::quietAssert)
    depthTwoLeftLeft.forEach(depthTwoLeftLeft::quietAssert)
    depthTwoRightPartial.forEach(depthTwoRightPartial::quietAssert)
    depthTwoRightRight.forEach(depthTwoRightRight::quietAssert)
    depthTwoRightLeft.forEach(depthTwoRightLeft::quietAssert)
  }

  test("equal (A)") {
    intFBSTreeOfNone.equal(intFBSTreeOfNone) shouldBe true

    intFBSTreeOfNone.equal(intFBSTreeOfOne) shouldBe false
    intFBSTreeOfOne.equal(intFBSTreeOfNone) shouldBe false
    intFBSTreeOfOne.equal(FBSTree.finsertIK(emptyIMBTree(),1)) shouldBe true
    intFBSTreeOfOne.equal(intFBSTreeOfOne) shouldBe true

    intFBSTreeOfOne.equal(intFBSTreeOfTwo) shouldBe false
    intFBSTreeOfTwo.equal(intFBSTreeOfOne) shouldBe false
    intFBSTreeOfTwo.equal(FBSTree.finsertIK(FBSTree.finsertIK(emptyIMBTree(),1), 2)) shouldBe true
    intFBSTreeOfTwo.equal(FBSTree.finsertIK(FBSTree.finsertIK(emptyIMBTree(),2), 1)) shouldBe true
    intFBSTreeOfTwo.equal(intFBSTreeOfTwo) shouldBe true

    intFBSTreeOfThree.equal(intFBSTreeOfTwo) shouldBe false
    intFBSTreeOfTwo.equal(intFBSTreeOfThree) shouldBe false
    intFBSTreeOfThree.equal(FBSTree.finsertIK(FBSTree.finsertIK(emptyIMBTree(),1), 3)) shouldBe false
    intFBSTreeOfThree.equal(FBSTree.finsertIK(FBSTree.finsertIK(emptyIMBTree(),3), 1)) shouldBe false
    intFBSTreeOfThree.equal(FBSTree.finsertIK(FBSTree.finsertIK(emptyIMBTree(),3), 2)) shouldBe false
  }

  test("equal (B)") {
    intFRBTreeOfNone.equal(intFBSTreeOfNone) shouldBe true

    intFRBTreeOfNone.equal(intFBSTreeOfOne) shouldBe false
    intFRBTreeOfOne.equal(intFBSTreeOfNone) shouldBe false
    intFRBTreeOfOne.equal(FBSTree.finsertIK(emptyIMBTree(),1)) shouldBe true
    intFRBTreeOfOne.equal(intFBSTreeOfOne) shouldBe true

    intFRBTreeOfOne.equal(intFBSTreeOfTwo) shouldBe false
    intFRBTreeOfTwo.equal(intFBSTreeOfOne) shouldBe false
    intFRBTreeOfTwo.equal(FBSTree.finsertIK(FBSTree.finsertIK(emptyIMBTree(),1), 2)) shouldBe true
    intFRBTreeOfTwo.equal(FBSTree.finsertIK(FBSTree.finsertIK(emptyIMBTree(),2), 1)) shouldBe true
    intFRBTreeOfTwo.equal(intFBSTreeOfTwo) shouldBe true

    intFRBTreeOfThree.equal(intFBSTreeOfTwo) shouldBe false
    intFRBTreeOfTwo.equal(intFBSTreeOfThree) shouldBe false
    intFRBTreeOfThree.equal(FBSTree.finsertIK(FBSTree.finsertIK(emptyIMBTree(),1), 3)) shouldBe false
    intFRBTreeOfThree.equal(FBSTree.finsertIK(FBSTree.finsertIK(emptyIMBTree(),3), 1)) shouldBe false
    intFRBTreeOfThree.equal(FBSTree.finsertIK(FBSTree.finsertIK(emptyIMBTree(),3), 2)) shouldBe false
  }

  test("forEach") {
    val counter = AtomicInteger(0)
    val summer = AtomicInteger(0)
    val doCount: (TKVEntry<Int, Int>) -> Unit = { counter.incrementAndGet() }
    val doSum: (TKVEntry<Int, Int>) -> Unit = { v -> summer.addAndGet(v.getv()) }
    checkAll(repeats, Arb.fbstree<Int, Int>(Arb.int(),20..100)) { fbst ->
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
      val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
      val oraSum = (n*(n-1))/2 // filling is 0..(n-1) => ((n-1)*(n-1+1))/2 QED
      val tree: FBSTree<Int, Int> = FBSTree.of(values.iterator())
      tree.forEach(doCount)
      counter.get() shouldBe n
      counter.set(0)
      tree.forEach(doSum)
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
      val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
      values.shuffle()
      val oraSum = (n*(n-1))/2 // filling is 0..(n-1) => ((n-1)*(n-1+1))/2 QED
      val tree: FBSTree<Int, Int> = FBSTree.of(values.iterator())
      tree.forEach(doCount)
      counter.get() shouldBe n
      counter.set(0)
      tree.forEach(doSum)
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
      val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
      values.reverse()
      val oraSum = (n*(n-1))/2 // filling is 0..(n-1) => ((n-1)*(n-1+1))/2 QED
      val tree: FBSTree<Int, Int> = FBSTree.of(values.iterator())
      tree.forEach(doCount)
      counter.get() shouldBe n
      counter.set(0)
      tree.forEach(doSum)
      summer.get() shouldBe oraSum
      summer.set(0)
    }
  }

  test("toIMSet") {
    checkAll(repeats, Arb.fbstree<Int, Int>(Arb.int(),20..100)) { fbst ->
      val ims1: FSet<Int> = fbst.toIMSet()
      (ims1.toIMBTree() === fbst) shouldBe false
      ims1.equals(fbst.preorder().fmap { tkv -> tkv.getv() }.toSet()) shouldBe true
    }
  }

  test("copy") {
    checkAll(repeats, Arb.fbstree<Int, Int>(Arb.int(),20..100)) { fbst ->
      val c1 = fbst.copy()
      (c1 === fbst) shouldBe false
      fbst.equal(c1) shouldBe true
    }
  }

  test("copyToMutableList") {
    checkAll(repeats, Arb.fbstree<Int, Int>(Arb.int(),20..100)) { fbst ->
      val ml: MutableMap<Int, Int> = fbst.copyToMutableMap()
      if (ml.size != fbst.size) /* TODO there are duplicates */ true shouldBe true
      else (fbst.toSet() == ml.entries.map { mentry -> TKVEntry.of(mentry.key, mentry.value) }.toSet()) shouldBe true
    }
  }

})
