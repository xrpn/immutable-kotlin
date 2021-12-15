package com.xrpn.immutable.frbtreetest

import com.xrpn.imapi.IMKSet
import com.xrpn.imapi.IntKeyType
import com.xrpn.immutable.*
import com.xrpn.immutable.FRBTree.Companion.emptyIMBTree
import com.xrpn.immutable.frbSlideShareTree
import com.xrpn.immutable.frbWikiTree
import com.xrpn.immutable.rbSlideShareTree
import com.xrpn.immutable.rbWikiTree
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.xrpn.frbStree
import io.kotest.xrpn.frbtree
import java.util.concurrent.atomic.AtomicInteger

private val intFRBTreeOfNone = FRBTree.ofvi(*emptyArrayOfInt)
private val intFRBSTreeOfNone = FRBTree.ofvs(*emptyArrayOfInt)
private val intFRBTreeOfOne = FRBTree.ofvi(*arrayOf<Int>(1))
private val intFRBTreeOfTwo = FRBTree.ofvi(*arrayOf<Int>(1, 2))
private val intFRBTreeOfThree = FRBTree.ofvi(*arrayOf<Int>(1, 2, 3))
private val intFBSTreeOfNone = FBSTree.ofvi(*emptyArrayOfInt)
private val intFBSTreeOfOne = FBSTree.ofvi(*arrayOf<Int>(1))
private val intFBSTreeOfTwo = FBSTree.ofvi(*arrayOf<Int>(1, 2))
private val intFBSTreeOfThree = FBSTree.ofvi(*arrayOf<Int>(1, 2, 3))


class FRBTreeUtilityTest  : FunSpec({

    val repeats = 50

    beforeTest {}

    test("sanity") {
        FRBTree.rbRootInvariant(ttDepthOneRight) shouldBe false
        FRBTree.rbRootInvariant(frbDepthOneLeft) shouldBe true
        FRBTree.rbRootInvariant(frbDepthOneFull) shouldBe true

        FRBTree.rbRootInvariant(ttDepthTwoLeftRight) shouldBe false
        FRBTree.rbRootInvariant(frbDepthTwoLeftLeft) shouldBe true
        FRBTree.rbRootInvariant(frbDepthTwoRightRight) shouldBe false
        FRBTree.rbRootInvariant(frbDepthTwoRightLeft) shouldBe true

        FRBTree.rbRootInvariant(ttDepthTwoLeftPartial) shouldBe false
        FRBTree.rbRootInvariant(ttDepthTwoRightPartial) shouldBe false

        FRBTree.rbRootInvariant(frbWikiTree) shouldBe true
        FRBTree.rbRootInvariant(frbSlideShareTree) shouldBe true

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
            val values = (Array(n) { i: Int -> TKVEntry.ofkk(i, i) })
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
            val values = (Array(n) { i: Int -> TKVEntry.ofkk(i, i) })
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
            val values = (Array(n) { i: Int -> TKVEntry.ofkk(i, i) })
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

    test("toIMRSet") {
        intFRBTreeOfNone.toIMSet(IntKeyType) shouldBe FKSet.emptyIMKSet()
        intFRBTreeOfNone.toIMSet(null) shouldBe null // not enough info to infer what kind of empty set
        checkAll(repeats, Arb.frbtree(Arb.int(),20..50)) { frbt ->
            val ims1: IMKSet<Int, Int> = frbt.toIMSet(null)!!
            (ims1.toIMBTree() === frbt) shouldBe true
            ims1.equals(ofBody(frbt)) shouldBe true
        }
        checkAll(repeats, Arb.frbtree(Arb.string(),20..50)) { frbt ->
            val ims1: IMKSet<Int, String> = frbt.toIMSet(null)!!
            (ims1.toIMBTree() === frbt) shouldBe true
            ims1.equals(ofBody(frbt)) shouldBe true
        }
        checkAll(repeats, Arb.frbStree(Arb.int(),20..50)) { frbt ->
            val ims1: IMKSet<String, Int> = frbt.toIMSet(null)!!
            (ims1.toIMBTree() === frbt) shouldBe true
            ims1.equals(ofBody(frbt)) shouldBe true
        }
        checkAll(repeats, Arb.frbStree(Arb.string(),20..50)) { frbt ->
            val ims1: IMKSet<String, String> = frbt.toIMSet(null)!!
            (ims1.toIMBTree() === frbt) shouldBe true
            ims1.equals(ofBody(frbt)) shouldBe true
        }
        nnodeRbtOf3.toIMSet(null) shouldBe null
        knodeRbtOf3.toIMSet(null)?.fsize() shouldBe 3
        inodeRbtOf3.toIMSet(null)?.fsize() shouldBe 3
        snodeRbtOf3.toIMSet(null)?.fsize() shouldBe 3
    }

    test("copy") {
        intFRBTreeOfNone.copy() shouldBe intFRBTreeOfNone
        (intFRBTreeOfNone.copy() === intFRBTreeOfNone) shouldBe true
        checkAll(repeats, Arb.frbtree<Int, Int>(Arb.int(),20..100)) { frbt ->
            val c1 = frbt.copy()
            (c1 === frbt) shouldBe false
            frbt.equal(c1) shouldBe true
        }
    }

    test("copyToMutableList") {
        intFRBTreeOfNone.copyToMutableMap() shouldBe mutableMapOf()
        checkAll(repeats, Arb.frbtree<Int, Int>(Arb.int(),20..100)) { frbt ->
            val ml: MutableMap<Int, Int> = frbt.copyToMutableMap()
            ml.size shouldBe frbt.size
            frbt.toSet() shouldBe ml.entries.map { mentry -> TKVEntry.ofkk(mentry.key, mentry.value) }.toSet()
        }
    }
})