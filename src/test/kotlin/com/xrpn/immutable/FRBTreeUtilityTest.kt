package com.xrpn.immutable

import com.xrpn.immutable.FRBTree.Companion.emptyIMBTree
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.frbtree
import java.util.concurrent.atomic.AtomicInteger

private val intFRBTreeOfNone = FRBTree.ofvi(*arrayOf<Int>())
private val intFRBTreeOfOne = FRBTree.ofvi(*arrayOf<Int>(1))
private val intFRBTreeOfTwo = FRBTree.ofvi(*arrayOf<Int>(1,2))
private val intFRBTreeOfThree = FRBTree.ofvi(*arrayOf<Int>(1,2,3))
private val intFBSTreeOfNone = FBSTree.ofvi(*arrayOf<Int>())
private val intFBSTreeOfOne = FBSTree.ofvi(*arrayOf<Int>(1))
private val intFBSTreeOfTwo = FBSTree.ofvi(*arrayOf<Int>(1,2))
private val intFBSTreeOfThree = FBSTree.ofvi(*arrayOf<Int>(1,2,3))


class FRBTreeUtilityTest  : FunSpec({

    val repeats = 50

    beforeTest {}

    test("sanity") {
        FRBTree.rbRootSane(ttDepthOneRight) shouldBe false
        FRBTree.rbRootSane(frbDepthOneLeft) shouldBe true
        FRBTree.rbRootSane(frbDepthOneFull) shouldBe true

        FRBTree.rbRootSane(ttDepthTwoLeftRight) shouldBe false
        FRBTree.rbRootSane(frbDepthTwoLeftLeft) shouldBe true
        FRBTree.rbRootSane(frbDepthTwoRightRight) shouldBe false
        FRBTree.rbRootSane(frbDepthTwoRightLeft) shouldBe true

        FRBTree.rbRootSane(ttDepthTwoLeftPartial) shouldBe false
        FRBTree.rbRootSane(ttDepthTwoRightPartial) shouldBe false

        FRBTree.rbRootSane(frbWikiTree) shouldBe true
        FRBTree.rbRootSane(frbSlideShareTree) shouldBe true

        rbWikiTree.preorder() shouldBe frbWikiTree.preorder()
        rbWikiTree.inorder() shouldBe frbWikiTree.inorder()
        rbWikiTree.postorder() shouldBe frbWikiTree.postorder()

        rbSlideShareTree.preorder() shouldBe frbSlideShareTree.preorder()
        rbSlideShareTree.inorder() shouldBe frbSlideShareTree.inorder()
        rbSlideShareTree.postorder() shouldBe frbSlideShareTree.postorder()
    }

    test("equal (A)") {
        
        intFRBTreeOfNone.equal(intFRBTreeOfNone) shouldBe true

        intFRBTreeOfNone.equal(intFRBTreeOfOne) shouldBe false
        intFRBTreeOfOne.equal(intFRBTreeOfNone) shouldBe false
        intFRBTreeOfOne.equal(FRBTree.finsertIK(emptyIMBTree(),1)) shouldBe true
        intFRBTreeOfOne.equal(intFRBTreeOfOne) shouldBe true

        intFRBTreeOfOne.equal(intFRBTreeOfTwo) shouldBe false
        intFRBTreeOfTwo.equal(intFRBTreeOfOne) shouldBe false
        intFRBTreeOfTwo.equal(FRBTree.finsertIK(FRBTree.finsertIK(emptyIMBTree(),1), 2)) shouldBe true
        intFRBTreeOfTwo.equal(FRBTree.finsertIK(FRBTree.finsertIK(emptyIMBTree(),2), 1)) shouldBe true
        intFRBTreeOfTwo.equal(intFRBTreeOfTwo) shouldBe true

        intFRBTreeOfThree.equal(intFRBTreeOfTwo) shouldBe false
        intFRBTreeOfTwo.equal(intFRBTreeOfThree) shouldBe false
        intFRBTreeOfThree.equal(FRBTree.finsertIK(FRBTree.finsertIK(emptyIMBTree(),1), 3)) shouldBe false
        intFRBTreeOfThree.equal(FRBTree.finsertIK(FRBTree.finsertIK(emptyIMBTree(),3), 1)) shouldBe false
        intFRBTreeOfThree.equal(FRBTree.finsertIK(FRBTree.finsertIK(emptyIMBTree(),3), 2)) shouldBe false
    }

    test("equal (B)") {
        
        intFBSTreeOfNone.equal(intFRBTreeOfNone) shouldBe true

        intFBSTreeOfNone.equal(intFRBTreeOfOne) shouldBe false
        intFBSTreeOfOne.equal(intFRBTreeOfNone) shouldBe false
        intFBSTreeOfOne.equal(FRBTree.finsertIK(emptyIMBTree(),1)) shouldBe true
        intFBSTreeOfOne.equal(intFRBTreeOfOne) shouldBe true

        intFBSTreeOfOne.equal(intFRBTreeOfTwo) shouldBe false
        intFBSTreeOfTwo.equal(intFRBTreeOfOne) shouldBe false
        intFBSTreeOfTwo.equal(FRBTree.finsertIK(FBSTree.finsertIK(emptyIMBTree(),1), 2)) shouldBe true
        intFRBTreeOfTwo.equal(FRBTree.finsertIK(FBSTree.finsertIK(emptyIMBTree(),2), 1)) shouldBe true
        intFBSTreeOfTwo.equal(intFRBTreeOfTwo) shouldBe true

        intFBSTreeOfThree.equal(intFRBTreeOfTwo) shouldBe false
        intFBSTreeOfTwo.equal(intFRBTreeOfThree) shouldBe false
        intFBSTreeOfThree.equal(FRBTree.finsertIK(FRBTree.finsertIK(emptyIMBTree(),1), 3)) shouldBe false
        intFBSTreeOfThree.equal(FRBTree.finsertIK(FBSTree.finsertIK(emptyIMBTree(),3), 1)) shouldBe false
        intFBSTreeOfThree.equal(FRBTree.finsertIK(FBSTree.finsertIK(emptyIMBTree(),3), 2)) shouldBe false
    }

    test ("forEach presorted") {

        val counter = AtomicInteger(0)
        val summer = AtomicInteger(0)
        val doCount: (TKVEntry<Int, Int>) -> Unit = { counter.incrementAndGet() }
        val doSum: (TKVEntry<Int, Int>) -> Unit = { tkv -> summer.addAndGet(tkv.getk()) }
        checkAll(50, Arb.int(20..100)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val oraSum = (n*(n-1))/2 // filling is 0..(n-1) => ((n-1)*(n-1+1))/2 QED
            val tree: FRBTree<Int, Int> = FRBTree.of(values.iterator())
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
            val tree: FRBTree<Int, Int> = FRBTree.of(values.iterator())
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
            val tree: FRBTree<Int, Int> = FRBTree.of(values.iterator())
            tree.forEach(doCount)
            counter.get() shouldBe n
            counter.set(0)
            tree.forEach(doSum)
            summer.get() shouldBe oraSum
            summer.set(0)
        }
    }

    test("toIMSet") {
        checkAll(repeats, Arb.frbtree<Int, Int>(Arb.int(),20..100)) { frbt ->
            val ims1: FSet<Int> = frbt.toIMSet()
            (ims1.toIMBTree() === frbt) shouldBe true
            ims1.equals(frbt) shouldBe true
            ims1.equals(frbt.preorder().fmap { tkv -> tkv.getv() }.toSet()) shouldBe true
        }
    }

    test("copy") {
        checkAll(repeats, Arb.frbtree<Int, Int>(Arb.int(),20..100)) { frbt ->
            val c1 = frbt.copy()
            (c1 === frbt) shouldBe false
            frbt.equal(c1) shouldBe true
        }
    }

    test("copyToMutableList") {
        checkAll(repeats, Arb.frbtree<Int, Int>(Arb.int(),20..100)) { frbt ->
            val ml: MutableMap<Int, Int> = frbt.copyToMutableMap()
            ml.size shouldBe frbt.size
            frbt.toSet() shouldBe ml.entries.map { mentry -> TKVEntry.of(mentry.key, mentry.value) }.toSet()
        }
    }
})