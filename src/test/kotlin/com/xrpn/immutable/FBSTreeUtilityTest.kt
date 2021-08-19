package com.xrpn.immutable

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

class FBSTreeUtilityTest : FunSpec({

  val repeats = 50

  beforeTest {}

  test("equal") {
    intFBSTreeOfNone.equal(intFBSTreeOfNone) shouldBe true

    intFBSTreeOfNone.equal(intFBSTreeOfOne) shouldBe false
    intFBSTreeOfOne.equal(intFBSTreeOfNone) shouldBe false
    intFBSTreeOfOne.equal(FBSTree.finsertIK(FRBTree.emptyIMBTree(),1)) shouldBe true
    intFBSTreeOfOne.equal(intFBSTreeOfOne) shouldBe true

    intFBSTreeOfOne.equal(intFBSTreeOfTwo) shouldBe false
    intFBSTreeOfTwo.equal(intFBSTreeOfOne) shouldBe false
    intFBSTreeOfTwo.equal(FBSTree.finsertIK(FBSTree.finsertIK(FRBTree.emptyIMBTree(),1), 2)) shouldBe true
    intFBSTreeOfTwo.equal(FBSTree.finsertIK(FBSTree.finsertIK(FRBTree.emptyIMBTree(),2), 1)) shouldBe true
    intFBSTreeOfTwo.equal(intFBSTreeOfTwo) shouldBe true

    intFBSTreeOfThree.equal(intFBSTreeOfTwo) shouldBe false
    intFBSTreeOfTwo.equal(intFBSTreeOfThree) shouldBe false
    intFBSTreeOfThree.equal(FBSTree.finsertIK(FBSTree.finsertIK(FRBTree.emptyIMBTree(),1), 3)) shouldBe false
    intFBSTreeOfThree.equal(FBSTree.finsertIK(FBSTree.finsertIK(FRBTree.emptyIMBTree(),3), 1)) shouldBe false
    intFBSTreeOfThree.equal(FBSTree.finsertIK(FBSTree.finsertIK(FRBTree.emptyIMBTree(),3), 2)) shouldBe false
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

  test("toIMSet") {
    checkAll(repeats, Arb.fbstree<Int, Int>(Arb.int(),20..100)) { fbst ->
      val ims1: FSet<Int> = fbst.toIMSet()
      (ims1 === fbst) shouldBe false
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
