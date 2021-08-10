package com.xrpn.immutable

import com.xrpn.imapi.IMBTreeTraversable
import com.xrpn.immutable.FRBTree.Companion.BLACK
import com.xrpn.immutable.FRBTree.Companion.RED
import com.xrpn.immutable.FRBTree.Companion.find
import com.xrpn.immutable.FRBTree.Companion.frbtPartAssert
import com.xrpn.immutable.FRBTree.Companion.rbMaxDepth
import com.xrpn.immutable.FRBTree.Companion.insert
import com.xrpn.immutable.FRBTree.Companion.nul
import com.xrpn.immutable.FRBTree.Companion.rbRootSane
import com.xrpn.immutable.FRBTree.Companion.of
import com.xrpn.immutable.FRBTree.Companion.parent
import com.xrpn.immutable.FRBTree.Companion.contains2
import com.xrpn.immutable.FRBTree.Companion.contains
import com.xrpn.immutable.FRBTree.Companion.delete
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.int
import kotlin.random.Random.Default.nextInt

private const val verbose = false

class FRBTreeTest : FunSpec({

    beforeTest {
        rbWikiTree = RBTree.of(frbWikiInorder)
        rbSlideShareTree = RBTree.of(frbSlideShareInorder)
    }

    test("sanity") {
        rbRootSane(ttDepthOneRight) shouldBe false
        rbRootSane(frbDepthOneLeft) shouldBe true
        rbRootSane(frbDepthOneFull) shouldBe true

        rbRootSane(ttDepthTwoLeftRight) shouldBe false
        rbRootSane(frbDepthTwoLeftLeft) shouldBe true
        rbRootSane(frbDepthTwoRightRight) shouldBe false
        rbRootSane(frbDepthTwoRightLeft) shouldBe true

        rbRootSane(ttDepthTwoLeftPartial) shouldBe false
        rbRootSane(ttDepthTwoRightPartial) shouldBe false

        rbRootSane(frbWikiTree) shouldBe true
        rbRootSane(frbSlideShareTree) shouldBe true

        rbWikiTree.preorder() shouldBe frbWikiTree.preorder()
        rbWikiTree.inorder() shouldBe frbWikiTree.inorder()
        rbWikiTree.postorder() shouldBe frbWikiTree.postorder()

        rbSlideShareTree.preorder() shouldBe frbSlideShareTree.preorder()
        rbSlideShareTree.inorder() shouldBe frbSlideShareTree.inorder()
        rbSlideShareTree.postorder() shouldBe frbSlideShareTree.postorder()
    }

//    afterTest { (testCase, result) ->
//    }

    test("root") {
        FRBTNil.root() shouldBe null
        val ary = IntArray(1) { nextInt() }
        of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).root() shouldBe TKVEntry.ofIntKey(ary[0])
        val itemAry = Array(1) { TKVEntry.ofIntKey(nextInt()) }
        of(itemAry.iterator()).root() shouldBe itemAry[0]
    }

    test("min") {
        FRBTNil.leftMost() shouldBe null
    }

    test("max") {
        FRBTNil.rightMost() shouldBe null
    }

    test("min max int") {
        for (size in IntRange(1, 20)) {
            val ary = IntArray(size) { nextInt() }
            val max = ary.maxOrNull()!!
            val min = ary.minOrNull()!!
            of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).leftMost() shouldBe TKVEntry.ofIntKey(min)
            of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).rightMost() shouldBe TKVEntry.ofIntKey(max)
        }
    }

    test("preorder") {
        FRBTNil.preorder() shouldBe FLNil
        FRBTNode(mEntry).preorder() shouldBe FLCons(mEntry, FLNil)

        ttDepthOneRight.preorder() shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        frbDepthOneLeft.preorder() shouldBe FLCons(mEntry, FLCons(lEntry, FLNil))
        frbDepthOneFull.preorder() shouldBe frbDepthOneFullPreorder

        ttDepthTwoLeftRight.preorder() shouldBe ttDepthTwoLeftRightPreorder
        frbDepthTwoLeftLeft.preorder() shouldBe frbDepthTwoLeftLeftPreorder
        frbDepthTwoRightRight.preorder() shouldBe frbDepthTwoRightRightPreorder
        frbDepthTwoRightLeft.preorder() shouldBe frbDepthTwoRightLeftPreorder
    }

    test("preorder reverse") {
        FRBTNil.preorder(reverse = true) shouldBe FLNil
        FRBTNode(mEntry).preorder(reverse = true) shouldBe FLCons(mEntry, FLNil)

        ttDepthOneRight.preorder(reverse = true) shouldBe FLCons(nEntry, FLCons(mEntry, FLNil))
        frbDepthOneLeft.preorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        frbDepthOneFull.preorder(reverse = true) shouldBe frbDepthOneFullPreorder.freverse()

        ttDepthTwoLeftRight.preorder(reverse = true) shouldBe ttDepthTwoLeftRightPreorder.freverse()
        frbDepthTwoLeftLeft.preorder(reverse = true) shouldBe frbDepthTwoLeftLeftPreorder.freverse()
        frbDepthTwoRightRight.preorder(reverse = true) shouldBe frbDepthTwoRightRightPreorder.freverse()
        frbDepthTwoRightLeft.preorder(reverse = true) shouldBe frbDepthTwoRightLeftPreorder.freverse()

        frbWikiTree.preorder(reverse = true) shouldBe frbWikiPreorder.freverse()
        frbSlideShareTree.preorder(reverse = true) shouldBe frbSlideSharePreorder.freverse()
    }

    test("inorder") {
        FRBTNil.inorder() shouldBe FLNil
        frbtPartAssert(FRBTNode(mEntry, BLACK)).inorder() shouldBe FLCons(mEntry, FLNil)

        ttDepthOneRight.inorder() shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        frbDepthOneLeft.inorder() shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        frbDepthOneFull.inorder() shouldBe frbDepthOneFullInorder

        ttDepthTwoLeftRight.inorder() shouldBe ttDepthTwoLeftRightInorder
        frbDepthTwoLeftLeft.inorder() shouldBe frbDepthTwoLeftLeftInorder
        frbDepthTwoRightRight.inorder() shouldBe frbDepthTwoRightRightInorder
        frbDepthTwoRightLeft.inorder() shouldBe frbDepthTwoRightLeftInorder

        frbWikiTree.inorder() shouldBe frbWikiInorder
        frbSlideShareTree.inorder() shouldBe frbSlideShareInorder
    }

    test("inorder reverse") {
        FRBTNil.inorder(reverse = true) shouldBe FLNil
        frbtPartAssert(FRBTNode(mEntry)).inorder(reverse = true) shouldBe FLCons(mEntry, FLNil).freverse()

        ttDepthOneRight.inorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLNil)).freverse()
        frbDepthOneLeft.inorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLNil)).freverse()
        frbDepthOneFull.inorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLCons(nEntry, FLNil))).freverse()

        ttDepthTwoLeftRight.inorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLCons(nEntry, FLCons(sEntry, FLNil)))).freverse()
        frbDepthTwoLeftLeft.inorder(reverse = true) shouldBe FLCons(eEntry, FLCons(lEntry, FLCons(nEntry, FLCons(sEntry, FLNil)))).freverse()
        frbDepthTwoRightRight.inorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLCons(sEntry, FLCons(uEntry, FLNil)))).freverse()
        frbDepthTwoRightLeft.inorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLCons(rEntry, FLCons(sEntry, FLNil)))).freverse()

        frbWikiTree.inorder(reverse = true) shouldBe frbWikiInorder.freverse()
        frbSlideShareTree.inorder(reverse = true) shouldBe frbSlideShareInorder.freverse()
    }

    test("postorder") {
        FRBTNil.postorder() shouldBe FLNil
        FRBTNode(mEntry).postorder() shouldBe FLCons(mEntry, FLNil)

        ttDepthOneRight.postorder() shouldBe FLCons(nEntry, FLCons(mEntry, FLNil))
        frbDepthOneLeft.postorder() shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        frbDepthOneFull.postorder() shouldBe frbDepthOneFullPostorder

        ttDepthTwoLeftRight.postorder() shouldBe ttDepthTwoLeftRightPostorder
        frbDepthTwoLeftLeft.postorder() shouldBe frbDepthTwoLeftLeftPostorder
        frbDepthTwoRightRight.postorder() shouldBe frbDepthTwoRightRightPostorder
        frbDepthTwoRightLeft.postorder() shouldBe frbDepthTwoRightLeftPostorder
    }

    test("postorder reverse") {
        FRBTNil.postorder(reverse = true) shouldBe FLNil
        FRBTNode(mEntry).postorder(reverse = true) shouldBe FLCons(mEntry, FLNil)

        ttDepthOneRight.postorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        frbDepthOneLeft.postorder(reverse = true) shouldBe FLCons(mEntry, FLCons(lEntry, FLNil))
        frbDepthOneFull.postorder(reverse = true) shouldBe frbDepthOneFullPostorder.freverse()

        ttDepthTwoLeftRight.postorder(reverse = true) shouldBe ttDepthTwoLeftRightPostorder.freverse()
        frbDepthTwoLeftLeft.postorder(reverse = true) shouldBe frbDepthTwoLeftLeftPostorder.freverse()
        frbDepthTwoRightRight.postorder(reverse = true) shouldBe frbDepthTwoRightRightPostorder.freverse()
        frbDepthTwoRightLeft.postorder(reverse = true) shouldBe frbDepthTwoRightLeftPostorder.freverse()

        frbWikiTree.postorder(reverse = true) shouldBe frbWikiPostorder.freverse()
        frbSlideShareTree.postorder(reverse = true) shouldBe frbSlideSharePostorder.freverse()
    }

    test("breadthFirst") {
        FRBTNil.breadthFirst() shouldBe FLNil
        FRBTNode(mEntry).breadthFirst() shouldBe FLCons(mEntry, FLNil)

        ttDepthOneRight.breadthFirst() shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        frbDepthOneLeft.breadthFirst() shouldBe FLCons(mEntry, FLCons(lEntry, FLNil))
        frbDepthOneFull.breadthFirst() shouldBe frbDepthOneFullBreadthFirst

        ttDepthTwoLeftRight.breadthFirst() shouldBe ttDepthTwoLeftRightBreadthFirst
        frbDepthTwoLeftLeft.breadthFirst() shouldBe frbDepthTwoLeftLeftBreadthFirst
        frbDepthTwoRightRight.breadthFirst() shouldBe frbDepthTwoRightRightBreadthFirst
        frbDepthTwoRightLeft.breadthFirst() shouldBe frbDepthTwoRightLeftBreadthFirst

        frbWikiTree.breadthFirst() shouldBe frbWikiBreadthFirst
        frbSlideShareTree.breadthFirst() shouldBe frbSlideShareBreadthFirst
    }

    test("breadthFirst reverse") {
        FRBTNil.breadthFirst(reverse = true) shouldBe FLNil
        FRBTNode(mEntry).breadthFirst(reverse = true) shouldBe FLCons(mEntry, FLNil)

        ttDepthOneRight.breadthFirst(reverse = true) shouldBe FLCons(nEntry, FLCons(mEntry, FLNil))
        frbDepthOneLeft.breadthFirst(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        frbDepthOneFull.breadthFirst(reverse = true) shouldBe frbDepthOneFullBreadthFirst.freverse()

        ttDepthTwoLeftRight.breadthFirst(reverse = true) shouldBe ttDepthTwoLeftRightBreadthFirst.freverse()
        frbDepthTwoLeftLeft.breadthFirst(reverse = true) shouldBe frbDepthTwoLeftLeftBreadthFirst.freverse()
        frbDepthTwoRightRight.breadthFirst(reverse = true) shouldBe frbDepthTwoRightRightBreadthFirst.freverse()
        frbDepthTwoRightLeft.breadthFirst(reverse = true) shouldBe frbDepthTwoRightLeftBreadthFirst.freverse()

        frbWikiTree.breadthFirst(reverse = true) shouldBe frbWikiBreadthFirst.freverse()
        frbSlideShareTree.breadthFirst(reverse = true) shouldBe frbSlideShareBreadthFirst.freverse()
    }

    test("size") {
        FRBTNil.size shouldBe 0
        FRBTNode(mEntry).size shouldBe 1

        ttDepthOneRight.size shouldBe 2
        frbDepthOneLeft.size shouldBe 2
        frbDepthOneFull.size shouldBe 3

        ttDepthTwoLeftRight.size shouldBe 4
        frbDepthTwoLeftLeft.size shouldBe 4
        frbDepthTwoRightRight.size shouldBe 4
        frbDepthTwoRightLeft.size shouldBe 4

        frbWikiTree.size shouldBe 9
        frbSlideShareTree.size shouldBe 8
    }

    test("maxDepth") {
        FRBTNil.maxDepth() shouldBe 0
        FRBTNode(TKVEntry.of(mEntry.hashCode(),mEntry)).maxDepth() shouldBe 1

        ttDepthOneRight.maxDepth() shouldBe 2
        frbDepthOneLeft.maxDepth() shouldBe 2
        frbDepthOneFull.maxDepth() shouldBe 2

        ttDepthTwoLeftRight.maxDepth() shouldBe 3
        frbDepthTwoLeftLeft.maxDepth() shouldBe 3
        frbDepthTwoRightRight.maxDepth() shouldBe 3
        frbDepthTwoRightLeft.maxDepth() shouldBe 3

        frbWikiTree.maxDepth() shouldBe 4
        frbSlideShareTree.maxDepth() shouldBe 4
    }

    test("minDepth") {
        FRBTNil.minDepth() shouldBe 0
        FRBTNode(mEntry).minDepth() shouldBe 1

        ttDepthOneRight.minDepth() shouldBe 2
        frbDepthOneLeft.minDepth() shouldBe 2
        frbDepthOneFull.minDepth() shouldBe 2

        ttDepthTwoLeftRight.minDepth() shouldBe 2
        frbDepthTwoLeftLeft.minDepth() shouldBe 2
        frbDepthTwoRightRight.minDepth() shouldBe 2
        frbDepthTwoRightLeft.minDepth() shouldBe 2

        frbWikiTree.minDepth() shouldBe 3
        frbSlideShareTree.minDepth() shouldBe 3
    }

    test("map") {
        FRBTNil.mapi { 2 } shouldBe FRBTNil

        frbDepthOneLeft.mapi { s -> "z$s" }.inorder() shouldBe frbDepthOneLeft.inorder().fmap { TKVEntry.ofIntKey("z${it.getv()}") }
        frbDepthOneLeft.maps { s -> "z$s" }.inorder() shouldBe frbDepthOneLeft.inorder().fmap { TKVEntry.ofStrKey("z${it.getv()}") }
        FRBTree.map(frbDepthOneLeft, {it + 2}, { "F$it"} ) shouldBe FRBTNode(TKVEntry.of(mEntry.getk() + 2, "F${mEntry.getv()}"), FRBTree.BLACK,
                                                                        FRBTNode(TKVEntry.of(lEntry.getk() + 2, "F${lEntry.getv()}")),
                                                                        FRBTNil)
        frbDepthOneFull.mapi { s -> "z$s" }.inorder() shouldBe frbDepthOneFull.inorder().fmap { TKVEntry.ofIntKey("z${it.getv()}") }
        frbDepthOneFull.maps { s -> "z$s" }.inorder() shouldBe frbDepthOneFull.inorder().fmap { TKVEntry.ofStrKey("z${it.getv()}") }
        frbWikiTree.mapi { s -> "z$s" }.inorder() shouldBe frbWikiTree.inorder().fmap { TKVEntry.ofIntKey("z${it.getv()}") }
        frbWikiTree.maps { s -> "z$s" }.inorder() shouldBe frbWikiTree.inorder().fmap { TKVEntry.ofStrKey("z${it.getv()}") }
        frbSlideShareTree.mapi { s -> "z$s" }.inorder() shouldBe frbSlideShareTree.inorder().fmap { TKVEntry.ofIntKey("z${it.getv()}") }
        frbSlideShareTree.maps { s -> "z$s" }.inorder() shouldBe frbSlideShareTree.inorder().fmap { TKVEntry.ofStrKey("z${it.getv()}") }
    }

    //
    // companion object
    //

    test("co.nul") {
        nul<Int, Int>() shouldBe FRBTNil
    }

    test("co.==") {
        (nul<Int, Int>() == nul<Int, Int>()) shouldBe true
        (nul<Int, Int>() == FRBTNil) shouldBe true
        (FRBTNil == nul<Int, Int>()) shouldBe true
        (FRBTNode(TKVEntry.of(aEntry.hashCode(),aEntry)) == FRBTNode(TKVEntry.of(aEntry.hashCode(),aEntry))) shouldBe true
    }

    test("co.find") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FRBTree<A,B>, acc: FList<TKVEntry<A,B>>): FList<A> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = find(t, acc.head)) {
                        is FRBTNode -> found.entry shouldBe acc.head
                        is FRBTNil -> true shouldBe false
                    }
                    go(t, acc.tail)
                }
            }
        go(frbWikiTree, frbWikiPreorder)
        find(frbWikiTree, zEntry) shouldBe FRBTNil
        go(frbSlideShareTree, frbSlideShareBreadthFirst)
        find(frbSlideShareTree, TKVEntry.ofIntKey(100)) shouldBe FRBTNil
    }

    test("co.parent") {
        parent(FRBTNil, TKVEntry.ofIntKey("")) shouldBe FRBTNil
        parent(FRBTNode(mEntry), mEntry) shouldBe FRBTNil

        parent(frbDepthOneLeft, lEntry) shouldBe frbDepthOneLeft
        parent(ttDepthOneRight, nEntry) shouldBe ttDepthOneRight
        parent(frbDepthOneFull, lEntry) shouldBe frbDepthOneFull
        parent(frbDepthOneFull, nEntry) shouldBe frbDepthOneFull

        (parent(ttDepthTwoLeftRight, mEntry) as FRBTNode).entry shouldBe lEntry
        (parent(ttDepthTwoLeftRight, lEntry) as FRBTNode).entry shouldBe nEntry
        (parent(ttDepthTwoLeftRight, sEntry) as FRBTNode).entry shouldBe nEntry
        (parent(frbDepthTwoLeftLeft, eEntry) as FRBTNode).entry shouldBe lEntry
        (parent(frbDepthTwoLeftLeft, lEntry) as FRBTNode).entry shouldBe nEntry
        (parent(frbDepthTwoLeftLeft, sEntry) as FRBTNode).entry shouldBe nEntry
        (parent(frbDepthTwoRightRight, uEntry) as FRBTNode).entry shouldBe sEntry
        (parent(frbDepthTwoRightRight, sEntry) as FRBTNode).entry shouldBe nEntry
        (parent(frbDepthTwoRightRight, mEntry) as FRBTNode).entry shouldBe nEntry
        (parent(frbDepthTwoRightLeft, rEntry) as FRBTNode).entry shouldBe sEntry
        (parent(frbDepthTwoRightLeft, sEntry) as FRBTNode).entry shouldBe nEntry
        (parent(frbDepthTwoRightLeft, mEntry) as FRBTNode).entry shouldBe nEntry

        parent(frbWikiTree, mEntry)  /* parent of root */ shouldBe FRBTNil
        (parent(frbWikiTree, cEntry) as FRBTNode).entry shouldBe bEntry
        (parent(frbWikiTree, hEntry) as FRBTNode).entry shouldBe dEntry
        parent(frbWikiTree, zEntry) /* parent of missing value */ shouldBe FRBTNil

        (parent(frbSlideShareTree, n32Entry) as FRBTNode).entry shouldBe n48Entry
        (parent(frbSlideShareTree, n50Entry) as FRBTNode).entry shouldBe n62Entry
    }

    test("co.contains2") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FRBTree<A, B>, acc: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    contains2(t, acc.head) shouldBe true
                    go(t, acc.tail)
                }
            }
        go(frbWikiTree, frbWikiPreorder)
        contains2(frbWikiTree, zEntry) shouldBe false
        go(frbSlideShareTree, frbSlideShareBreadthFirst)
        contains2(frbSlideShareTree, TKVEntry.ofIntKey(100)) shouldBe false
    }

    test("co.contains item") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FRBTree<A, B>, acc: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    (acc.head in t) shouldBe true
                    go(t, acc.tail)
                }
            }
        go(frbWikiTree, frbWikiPreorder)
        contains2(frbWikiTree, zEntry) shouldBe false
        go(frbSlideShareTree, frbSlideShareBreadthFirst)
        contains2(frbSlideShareTree, TKVEntry.ofIntKey(100)) shouldBe false
    }

    test("co.contains key") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FRBTree<A, B>, acc: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    (acc.head.getk() in t) shouldBe true
                    go(t, acc.tail)
                }
            }
        go(frbWikiTree, frbWikiPreorder)
        contains2(frbWikiTree, zEntry) shouldBe false
        go(frbSlideShareTree, frbSlideShareBreadthFirst)
        contains2(frbSlideShareTree, TKVEntry.ofIntKey(100)) shouldBe false
    }

    test("co.insert item") {
        insert(FRBTNil,mEntry) shouldBe FRBTNode(mEntry, BLACK)
        insert(FRBTNode(mEntry),lEntry) shouldBe frbDepthOneLeft
        insert(frbDepthOneLeft,nEntry) shouldBe frbDepthOneFull
        insert(ttDepthTwoRightPartial,rEntry) shouldBe frbDepthTwoRightLeft
    }

    test("co.insert items sorted asc") {
        val aux0 = insert(FRBTNil, TKVEntry.ofIntKey(0))
        println("tree with 0                   $aux0")
        aux0 as FRBTNode
        rbRootSane(aux0) shouldBe true
        val aux1 = insert(aux0, TKVEntry.ofIntKey(1))
        println("tree with 0,1                 $aux1")
        aux1 as FRBTNode
        rbRootSane(aux1) shouldBe true
        val aux2 = insert(aux1, TKVEntry.ofIntKey(2))
        println("tree with 0,1,2               $aux2")
        aux2 as FRBTNode
        rbRootSane(aux2) shouldBe true
        val aux3 = insert(aux2, TKVEntry.ofIntKey(3))
        println("tree with 0,1,2,3             $aux3")
        aux3 as FRBTNode
        rbRootSane(aux3) shouldBe true
        val aux4 = insert(aux3, TKVEntry.ofIntKey(4))
        println("tree with 0,1,2,3,4           $aux4")
        aux4 as FRBTNode
        rbRootSane(aux4) shouldBe true
        val aux5 = insert(aux4, TKVEntry.ofIntKey(5))
        println("tree with 0,1,2,3,4,5         $aux5")
        aux5 as FRBTNode
        rbRootSane(aux5) shouldBe true
        val aux6 = insert(aux5, TKVEntry.ofIntKey(6))
        println("tree with 0,1,2,3,4,5,6       $aux6")
        aux6 as FRBTNode
        rbRootSane(aux6) shouldBe true
        val aux7 = insert(aux6, TKVEntry.ofIntKey(7))
        println("tree with 0,1,2,3,4,5,6,7     $aux7")
        aux7 as FRBTNode
        rbRootSane(aux7) shouldBe true
        val aux8 = insert(aux7, TKVEntry.ofIntKey(8))
        println("tree with 0,1,2,3,4,5,6,7,8   $aux8")
        aux8 as FRBTNode
        rbRootSane(aux8) shouldBe true
        val aux9 = insert(aux8, TKVEntry.ofIntKey(9))
        println("tree with 0,1,2,3,4,5,6,7,8,9 $aux9")
        aux9 as FRBTNode
        rbRootSane(aux9) shouldBe true
    }

    test("co.insert items sorted desc") {
        val aux0 = insert(FRBTNil, TKVEntry.ofIntKey(8))
        println("8: $aux0")
        aux0 as FRBTNode
        rbRootSane(aux0) shouldBe true
        val aux1 = insert(aux0, TKVEntry.ofIntKey(7))
        println("7: $aux1")
        aux1 as FRBTNode
        rbRootSane(aux1) shouldBe true
        val aux2 = insert(aux1, TKVEntry.ofIntKey(6))
        println("6: $aux2")
        aux2 as FRBTNode
        rbRootSane(aux2) shouldBe true
        val aux3 = insert(aux2, TKVEntry.ofIntKey(5))
        println("5: $aux3")
        aux3 as FRBTNode
        rbRootSane(aux3) shouldBe true
        val aux4 = insert(aux3, TKVEntry.ofIntKey(4))
        println("4: $aux4")
        aux4 as FRBTNode
        rbRootSane(aux4) shouldBe true
        val aux5 = insert(aux4, TKVEntry.ofIntKey(3))
        println("3: $aux5")
        aux5 as FRBTNode
        rbRootSane(aux5) shouldBe true
        val aux6 = insert(aux5, TKVEntry.ofIntKey(2))
        println("2: $aux6")
        aux6 as FRBTNode
        rbRootSane(aux6) shouldBe true
        val aux7 = insert(aux6, TKVEntry.ofIntKey(1))
        println("1: $aux7")
        aux7 as FRBTNode
        rbRootSane(aux7) shouldBe true
        val aux8 = insert(aux7, TKVEntry.ofIntKey(0))
        println("0: $aux8")
        aux8 as FRBTNode
        rbRootSane(aux8) shouldBe true
    }

    test("co.insert item (property) compare sorted asc, small") {
        // checkAll(PropTestConfig(iterations = 1, seed = 1882817875667961235), Arb.int(20..100)) { n ->
        checkAll(50, Arb.int(20..100)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val frbTree = of(values.iterator())
            displayRbOnVerbose(frbTree, n)
            rbRootSane(frbTree) shouldBe true
            frbTree.size shouldBe n
            val rbTree = RBTree.of(values.iterator())
            displayRbtOnVerbose(rbTree, n)
            rbTree.rbSane() shouldBe true
            rbTree.size() shouldBe n
            val faut = frbTree.inorder()
            val aut = rbTree.inorder()
            faut shouldBe aut
            val fautr = frbTree.inorder(reverse = true)
            val autr = rbTree.inorder(reverse = true)
            fautr shouldBe autr
            val fautpre = frbTree.preorder()
            val autpre = rbTree.preorder()
            fautpre shouldBe autpre
            val fautpost = frbTree.postorder()
            val autpost = rbTree.postorder()
            fautpost shouldBe autpost
        }
    }

    test("co.insert item (property) compare sorted desc, small") {
        // checkAll(PropTestConfig(iterations = 1, seed = 1882817875667961235), Arb.int(20..100)) { n ->
        checkAll(50, Arb.int(20..100)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            values.reverse()
            val frbTree = of(values.iterator())
            displayRbOnVerbose(frbTree, n)
            rbRootSane(frbTree) shouldBe true
            frbTree.size shouldBe n
            val rbTree = RBTree.of(values.iterator())
            displayRbtOnVerbose(rbTree, n)
            rbTree.rbSane() shouldBe true
            rbTree.size() shouldBe n
            val faut = frbTree.inorder()
            val aut = rbTree.inorder()
            faut shouldBe aut
            val fautr = frbTree.inorder(reverse = true)
            val autr = rbTree.inorder(reverse = true)
            fautr shouldBe autr
            val fautpre = frbTree.preorder()
            val autpre = rbTree.preorder()
            fautpre shouldBe autpre
            val fautpost = frbTree.postorder()
            val autpost = rbTree.postorder()
            fautpost shouldBe autpost
        }
    }

    test("co.insert item (property) sorted asc, small") {
        // checkAll(PropTestConfig(iterations = 1, seed = 1882817875667961235), Arb.int(20..100)) { n ->
        checkAll(50, Arb.int(20..100)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val rbTree = of(values.iterator())
            displayRbOnVerbose(rbTree, n)
            rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(values.iterator())
            aut shouldBe testOracle
        }
    }

    test("co.insert item (property) sorted desc, small") {
        // checkAll(PropTestConfig(iterations = 1, seed = 1882817875667961235), Arb.int(20..100)) { n ->
        checkAll(50, Arb.int(20..100)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            values.reverse()
            val rbTree = of(values.iterator())
            displayRbOnVerbose(rbTree, n)
            rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder(reverse = true)
            val testOracle = FList.of(values.iterator())
            aut shouldBe testOracle
        }
    }

    test("co.insert item (property) sorted asc, large").config(enabled = true) {
        checkAll(2, Arb.int(10000..100000)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val rbTree = of(values.iterator())
            displayRbOnVerbose(rbTree, n)
            rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(values.iterator())
            aut shouldBe testOracle
        }
    }

    test("co.insert item (property) sorted desc, large").config(enabled = true) {
        checkAll(2, Arb.int(10000..100000)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            values.reverse()
            val rbTree = of(values.iterator())
            displayRbOnVerbose(rbTree, n)
            rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder(reverse = true)
            val testOracle = FList.of(values.iterator())
            aut shouldBe testOracle
        }
    }

    test("co.insert item (property) random, small") {
        // checkAll(PropTestConfig(iterations = 50, seed = 5792981224933522729), Arb.int(20..100)) { n ->
        checkAll(500, Arb.int(10..400)) { n ->
            val sorted = Array(n) { i: Int -> TKVEntry.of(i, i) }
            val shuffled = Array(n) { i: Int -> TKVEntry.of(i, i) }
            // shuffled.shuffle(Random(seed = 5792981224933522729))
            shuffled.shuffle()
            val rbTree = of(shuffled.iterator())
            displayRbOnVerbose(rbTree, n)
            rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(sorted.iterator())
            aut shouldBe testOracle
        }
    }

    test("co.insert item (property) random reversed, small") {
        // checkAll(PropTestConfig(iterations = 50, seed = 5792981224933522729), Arb.int(20..100)) { n ->
        checkAll(500, Arb.int(10..400)) { n ->
            val sorted = Array(n) { i: Int -> TKVEntry.of(i, i) }
            val shuffled = Array(n) { i: Int -> TKVEntry.of(i, i) }
            // shuffled.shuffle(Random(seed = 5792981224933522729))
            shuffled.shuffle()
            shuffled.reverse()
            val rbTree = of(shuffled.iterator())
            displayRbOnVerbose(rbTree, n)
            rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(sorted.iterator())
            aut shouldBe testOracle
        }
    }

    test("co.insert item (property) random, large").config(enabled = true) {
        checkAll(3, Arb.int(10000..100000)) { n ->
            val sorted = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val shuffled = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            shuffled.shuffle()
            val rbTree = of(shuffled.iterator())
            displayRbOnVerbose(rbTree, n, true)
            rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(sorted.iterator())
            aut shouldBe testOracle
        }
    }

    test("co.insert item (property) random reversed, large").config(enabled = true) {
        checkAll(3, Arb.int(10000..100000)) { n ->
            val sorted = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val shuffled = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            shuffled.shuffle()
            shuffled.reverse()
            val rbTree = of(shuffled.iterator())
            displayRbOnVerbose(rbTree, n, true)
            rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(sorted.iterator())
            aut shouldBe testOracle
        }
    }

    test("co.delete") {

        // tailrec fun <A: Comparable<A>, B: Any> goAllWiki(frb: FRBTree<A,B>, acc: FList<TKVEntry<A,B>>, inorder: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> =
        tailrec fun goAllWiki(frb: FRBTree<Int, String>, acc: FList<TKVEntry<Int,String>>, inorder: FList<TKVEntry<Int,String>>): FList<TKVEntry<Int,String>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    val rbDeleted: RBTree<Int, String> = rbWikiTree.copy()
                    rbDeleted.rbDelete(TKVEntry.of(acc.head.getk(), acc.head.getv()))
                    when (val deleted = delete(frb, acc.head)) {
                        is FRBTNode -> {
                            rbRootSane(deleted) shouldBe true
                            val aut1in = deleted.inorder()
                            val oracle = inorder.ffilterNot { it == acc.head }
                            aut1in shouldBe oracle
                            IMBTreeTraversable.strongEqual(deleted, rbDeleted) shouldBe true
                        }
                        is FRBTNil -> {
                            true shouldBe false
                        }
                    }
                    goAllWiki(frb, acc.tail, inorder)
                }
            }

        // tailrec fun <A: Comparable<A>, B: Any> goAllSS(frb: FRBTree<A,B>, acc: FList<TKVEntry<A,B>>, inorder: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> =
        tailrec fun goAllSS(frb: FRBTree<Int,Int>, acc: FList<TKVEntry<Int,Int>>, inorder: FList<TKVEntry<Int,Int>>): FList<TKVEntry<Int,Int>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    val rbDeleted: RBTree<Int, Int> = rbSlideShareTree.copy()
                    rbDeleted.rbDelete(TKVEntry.of(acc.head.getk(), acc.head.getv()))
                    when (val deleted = delete(frb, acc.head)) {
                        is FRBTNode -> {
                            rbRootSane(deleted) shouldBe true
                            val aut1in = deleted.inorder()
                            val oracle = inorder.ffilterNot { it == acc.head }
                            aut1in shouldBe oracle
                            IMBTreeTraversable.strongEqual(deleted, rbDeleted) shouldBe true
                        }
                        is FRBTNil -> {
                            true shouldBe false
                        }
                    }
                    goAllSS(frb, acc.tail, inorder)
                }
            }

        tailrec fun <A: Comparable<A>, B: Any> goTele(t: FRBTree<A,B>, rbDeleted: RBTree<A,B>, acc: FList<TKVEntry<A,B>>, inorder: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    rbDeleted.rbDelete(acc.head)
                    val deleted = delete(t, acc.head)
                    val oracle = inorder.ffilterNot { it == acc.head }
                    when (deleted) {
                        is FRBTNode -> {
                            rbRootSane(deleted) shouldBe true
                            val aut1in = deleted.inorder()
                            aut1in shouldBe oracle
                            IMBTreeTraversable.strongEqual(deleted, rbDeleted) shouldBe true
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

        delete(frbWikiTree, zEntry) shouldBe frbWikiTree
        
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

        delete(frbSlideShareTree, TKVEntry.ofIntKey(100)) shouldBe frbSlideShareTree
    }

    test("co.delete (property), sorted asc") {
        checkAll(500, Arb.int(30..1000)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.of(i, i) }
            val ix1 = nextInt(0, n)
            val frbTree = of(values.iterator())
            val aut = delete(frbTree, TKVEntry.ofIntKey(ix1))
            aut.size shouldBe n - 1
            rbRootSane(aut) shouldBe true
            val testOracle = FList.of(values.iterator())
                .ffilterNot { it == TKVEntry.ofIntKey(ix1) }
            aut.inorder() shouldBe testOracle
        }
    }

    test("co.delete (property), sorted desc") {
        checkAll(500, Arb.int(30..1000)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.of(i, i) }
            val reversed = Array(n) { i: Int -> TKVEntry.of(i, i) }
            reversed.reverse()
            val ix1 = nextInt(0, n)
            val frbTree = of(values.iterator())
            val aut = delete(frbTree, TKVEntry.ofIntKey(ix1))
            aut.size shouldBe n - 1
            rbRootSane(aut) shouldBe true
            val testOracle = FList.of(values.iterator())
                .ffilterNot { it == TKVEntry.ofIntKey(ix1) }
            aut.inorder() shouldBe testOracle
        }
    }

    test("co.delete (property), shuffled") {
        checkAll(500, Arb.int(30..1000)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.of(i, i) }
            val shuffled = Array(n) { i: Int -> TKVEntry.of(i, i) }
            shuffled.shuffle()
            val randoms = IntArray(n/10) { i: Int -> i }
            randoms.shuffle()
            val ix1 = randoms[0]
            val ix2 = randoms[1]
            val ix3 = randoms[2]
            val frbTree = of(shuffled.iterator())
            val aux0 = delete(frbTree, TKVEntry.ofIntKey(ix1))
            val aux1 = delete(aux0, TKVEntry.ofIntKey(ix2))
            val aut = delete(aux1, TKVEntry.ofIntKey(ix3))
            aut.size shouldBe n - 3
            rbRootSane(aut) shouldBe true
            val testOracle = FList.of(values.iterator())
                .ffilterNot { it == TKVEntry.ofIntKey(ix1) }
                .ffilterNot { it == TKVEntry.ofIntKey(ix2) }
                .ffilterNot { it == TKVEntry.ofIntKey(ix3) }
            aut.inorder() shouldBe testOracle
        }
    }

    test("co.of(list)") {
        of(FList.of(*arrayOf(mEntry,lEntry,nEntry))) shouldBe FRBTNode(mEntry, BLACK, FRBTNode(lEntry, BLACK), FRBTNode(nEntry, BLACK))
        // TWO                             vvvvvv               vvvvvv
        of(FList.of(*arrayOf(mEntry,cEntry,bEntry,dEntry,zEntry,bEntry))) shouldBe
            FRBTNode(mEntry, BLACK,
                FRBTNode(cEntry, RED,
                    // ONE   vvvvvv
                    FRBTNode(bEntry, BLACK),
                    FRBTNode(dEntry, BLACK)),
                FRBTNode(zEntry, BLACK))
    }
})

private fun displayRbOnVerbose(rbTree: FRBTree<Int, Int>, n: Int, force: Boolean = false) {
    if (verbose || !rbRootSane(rbTree) || force) {
        print("FF size " + n)
        print(", expected depth ${rbMaxDepth(n)}")
        print(", max depth " + rbTree.maxDepth())
        println(", min depth " + rbTree.minDepth())
        // println("$rbTree")
    }
}
