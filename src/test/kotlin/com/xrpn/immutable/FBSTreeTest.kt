package com.xrpn.immutable

import com.xrpn.immutable.FBSTree.Companion.fbtAssert
import com.xrpn.immutable.FBSTree.Companion.isChildMatch
import com.xrpn.immutable.FBSTree.Companion.nul
import com.xrpn.immutable.FBSTree.Companion.parent
import com.xrpn.immutable.FBSTree.Companion.prune
import com.xrpn.immutable.FBSTree.Companion.find
import com.xrpn.immutable.FBSTree.Companion.findLast
import com.xrpn.immutable.FBSTree.Companion.addGraft
import com.xrpn.immutable.FBSTree.Companion.contains
import com.xrpn.immutable.FBSTree.Companion.contains2
import com.xrpn.immutable.FBSTree.Companion.insert
import com.xrpn.immutable.FBSTree.Companion.delete
import com.xrpn.immutable.FBSTree.Companion.equal
import io.kotest.property.Arb
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.random.Random.Default.nextInt

class FBSTreeTest : FunSpec({

    beforeTest {
    }

//    afterTest { (testCase, result) ->
//    }

    test("root") {
        FBSTNil.root() shouldBe null
        for (size in IntRange(1, 20)) {
            val ary: IntArray = IntArray(size) {nextInt()}
            FBSTree.of(FList.of(ary.iterator()).map { TKVEntry.ofIntKey(it) }).root() shouldBe TKVEntry.ofIntKey(ary[0])
        }
    }

    test("min") {
        FBSTNil.leftMost() shouldBe null
    }

    test("max") {
        FBSTNil.rightMost() shouldBe null
    }

    test("min max int") {
        for (size in IntRange(1, 20)) {
            val ary: IntArray = IntArray(size) {nextInt()}
            val max = ary.maxOrNull()!!
            val min = ary.minOrNull()!!
            FBSTree.of(FList.of(ary.iterator()).map { TKVEntry.ofIntKey(it) }).leftMost() shouldBe TKVEntry.ofIntKey(min)
            FBSTree.of(FList.of(ary.iterator()).map { TKVEntry.ofIntKey(it) }).rightMost() shouldBe TKVEntry.ofIntKey(max)
        }
    }

    test("preorder") {
        FBSTNil.preorder() shouldBe FLNil
        FBSTNode(mEntry).preorder() shouldBe FLCons(mEntry, FLNil)

        depthOneRight.preorder() shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        depthOneLeft.preorder() shouldBe FLCons(mEntry, FLCons(lEntry, FLNil))
        depthOneFull.preorder() shouldBe depthOneFullPreorder

        depthTwoLeftRight.preorder() shouldBe depthTwoLeftRightPreorder
        depthTwoLeftLeft.preorder() shouldBe depthTwoLeftLeftPreorder
        depthTwoRightRight.preorder() shouldBe depthTwoRightRightPreorder
        depthTwoRightLeft.preorder() shouldBe depthTwoRightLeftPreorder

        wikiTree.preorder() shouldBe
            FLCons(fEntry,
                FLCons(bEntry,
                    FLCons(aEntry,
                        FLCons(dEntry,
                            FLCons(cEntry,
                                FLCons(eEntry,
                                    FLCons(gEntry,
                                        FLCons(iEntry,
                                            FLCons(hEntry, FLNil)))))))))
        slideShareTree.preorder() shouldBe slideSharePreorder
    }

    test("preorder reverse") {
        FBSTNil.preorder(reverse = true) shouldBe FLNil
        FBSTNode(mEntry).preorder(reverse = true) shouldBe FLCons(mEntry, FLNil)

        depthOneRight.preorder(reverse = true) shouldBe FLCons(nEntry, FLCons(mEntry, FLNil))
        depthOneLeft.preorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        depthOneFull.preorder(reverse = true) shouldBe depthOneFullPreorder.reverse()

        depthTwoLeftRight.preorder(reverse = true) shouldBe depthTwoLeftRightPreorder.reverse()
        depthTwoLeftLeft.preorder(reverse = true) shouldBe depthTwoLeftLeftPreorder.reverse()
        depthTwoRightRight.preorder(reverse = true) shouldBe depthTwoRightRightPreorder.reverse()
        depthTwoRightLeft.preorder(reverse = true) shouldBe depthTwoRightLeftPreorder.reverse()

        wikiTree.preorder(reverse = true) shouldBe
            FLCons(hEntry,
                FLCons(iEntry,
                    FLCons(gEntry,
                        FLCons(eEntry,
                            FLCons(cEntry,
                                FLCons(dEntry,
                                    FLCons(aEntry,
                                        FLCons(bEntry,
                                            FLCons(fEntry, FLNil)))))))))
        slideShareTree.preorder(reverse = true) shouldBe slideSharePreorder.reverse()
    }

    test("inorder") {
        FBSTNil.inorder() shouldBe FLNil
        fbtAssert(FBSTNode(mEntry)).inorder() shouldBe FLCons(mEntry, FLNil)

        depthOneRight.inorder() shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        depthOneLeft.inorder() shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        depthOneFull.inorder() shouldBe depthOneFullInorder

        depthTwoLeftRight.inorder() shouldBe depthTwoLeftRightInorder
        depthTwoLeftLeft.inorder() shouldBe depthTwoLeftLeftInorder
        depthTwoRightRight.inorder() shouldBe depthTwoRightRightInorder
        depthTwoRightLeft.inorder() shouldBe depthTwoRightLeftInorder

        wikiTree.inorder() shouldBe
            FLCons(aEntry,
                FLCons(bEntry,
                    FLCons(cEntry,
                        FLCons(dEntry,
                            FLCons(eEntry,
                                FLCons(fEntry,
                                    FLCons(gEntry,
                                        FLCons(hEntry,
                                            FLCons(iEntry, FLNil)))))))))
        slideShareTree.inorder() shouldBe slideShareInorder
    }

    test("inorder reverse") {
        FBSTNil.inorder(reverse = true) shouldBe FLNil
        fbtAssert(FBSTNode(mEntry)).inorder(reverse = true) shouldBe FLCons(mEntry, FLNil).reverse()

        depthOneRight.inorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLNil)).reverse()
        depthOneLeft.inorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLNil)).reverse()
        depthOneFull.inorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLCons(nEntry, FLNil))).reverse()

        depthTwoLeftRight.inorder(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLCons(nEntry, FLCons(sEntry, FLNil)))).reverse()
        depthTwoLeftLeft.inorder(reverse = true) shouldBe FLCons(eEntry, FLCons(lEntry, FLCons(nEntry, FLCons(sEntry, FLNil)))).reverse()
        depthTwoRightRight.inorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLCons(sEntry, FLCons(uEntry, FLNil)))).reverse()
        depthTwoRightLeft.inorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLCons(rEntry, FLCons(sEntry, FLNil)))).reverse()

        wikiTree.inorder(reverse = true) shouldBe
            FLCons(aEntry,
                FLCons(bEntry,
                    FLCons(cEntry,
                        FLCons(dEntry,
                            FLCons(eEntry,
                                FLCons(fEntry,
                                    FLCons(gEntry,
                                        FLCons(hEntry,
                                            FLCons(iEntry, FLNil))))))))).reverse()
        slideShareTree.inorder(reverse = true) shouldBe slideShareInorder.reverse()
    }

    test("postorder") {
        FBSTNil.postorder() shouldBe FLNil
        FBSTNode(mEntry).postorder() shouldBe FLCons(mEntry, FLNil)

        depthOneRight.postorder() shouldBe FLCons(nEntry, FLCons(mEntry, FLNil))
        depthOneLeft.postorder() shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        depthOneFull.postorder() shouldBe depthOneFullPostorder

        depthTwoLeftRight.postorder() shouldBe depthTwoLeftRightPostorder
        depthTwoLeftLeft.postorder() shouldBe depthTwoLeftLeftPostorder
        depthTwoRightRight.postorder() shouldBe depthTwoRightRightPostorder
        depthTwoRightLeft.postorder() shouldBe depthTwoRightLeftPostorder

        wikiTree.postorder() shouldBe
            FLCons(aEntry,
                FLCons(cEntry,
                    FLCons(eEntry,
                        FLCons(dEntry,
                            FLCons(bEntry,
                                FLCons(hEntry,
                                    FLCons(iEntry,
                                        FLCons(gEntry,
                                            FLCons(fEntry, FLNil)))))))))
        slideShareTree.postorder() shouldBe slideSharePostorder
    }

    test("postorder reverse") {
        FBSTNil.postorder(reverse = true) shouldBe FLNil
        FBSTNode(mEntry).postorder(reverse = true) shouldBe FLCons(mEntry, FLNil)

        depthOneRight.postorder(reverse = true) shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        depthOneLeft.postorder(reverse = true) shouldBe FLCons(mEntry, FLCons(lEntry, FLNil))
        depthOneFull.postorder(reverse = true) shouldBe depthOneFullPostorder.reverse()

        depthTwoLeftRight.postorder(reverse = true) shouldBe depthTwoLeftRightPostorder.reverse()
        depthTwoLeftLeft.postorder(reverse = true) shouldBe depthTwoLeftLeftPostorder.reverse()
        depthTwoRightRight.postorder(reverse = true) shouldBe depthTwoRightRightPostorder.reverse()
        depthTwoRightLeft.postorder(reverse = true) shouldBe depthTwoRightLeftPostorder.reverse()

        wikiTree.postorder(reverse = true) shouldBe // reverse of A, C, E, D, B, H, I, G, F.
            FLCons(fEntry,
                FLCons(gEntry,
                    FLCons(iEntry,
                        FLCons(hEntry,
                            FLCons(bEntry,
                                FLCons(dEntry,
                                    FLCons(eEntry,
                                        FLCons(cEntry,
                                            FLCons(aEntry, FLNil)))))))))
        slideShareTree.postorder(reverse = true) shouldBe slideSharePostorder.reverse()
    }

    test("breadthFirst") {
        FBSTNil.breadthFirst() shouldBe FLNil
        FBSTNode(mEntry).breadthFirst() shouldBe FLCons(mEntry, FLNil)

        depthOneRight.breadthFirst() shouldBe FLCons(mEntry, FLCons(nEntry, FLNil))
        depthOneLeft.breadthFirst() shouldBe FLCons(mEntry, FLCons(lEntry, FLNil))
        depthOneFull.breadthFirst() shouldBe depthOneFullBreadthFirst

        depthTwoLeftRight.breadthFirst() shouldBe depthTwoLeftRightBreadthFirst
        depthTwoLeftLeft.breadthFirst() shouldBe depthTwoLeftLeftBreadthFirst
        depthTwoRightRight.breadthFirst() shouldBe depthTwoRightRightBreadthFirst
        depthTwoRightLeft.breadthFirst() shouldBe depthTwoRightLeftBreadthFirst

        wikiTree.breadthFirst() shouldBe
            FLCons(fEntry,
                FLCons(bEntry,
                    FLCons(gEntry,
                        FLCons(aEntry,
                            FLCons(dEntry,
                                FLCons(iEntry,
                                    FLCons(cEntry,
                                        FLCons(eEntry,
                                            FLCons(hEntry, FLNil)))))))))
        slideShareTree.breadthFirst() shouldBe slideShareBreadthFirst
    }

    test("breadthFirst reverse") {
        FBSTNil.breadthFirst(reverse = true) shouldBe FLNil
        FBSTNode(mEntry).breadthFirst(reverse = true) shouldBe FLCons(mEntry, FLNil)

        depthOneRight.breadthFirst(reverse = true) shouldBe FLCons(nEntry, FLCons(mEntry, FLNil))
        depthOneLeft.breadthFirst(reverse = true) shouldBe FLCons(lEntry, FLCons(mEntry, FLNil))
        depthOneFull.breadthFirst(reverse = true) shouldBe depthOneFullBreadthFirst.reverse()

        depthTwoLeftRight.breadthFirst(reverse = true) shouldBe depthTwoLeftRightBreadthFirst.reverse()
        depthTwoLeftLeft.breadthFirst(reverse = true) shouldBe depthTwoLeftLeftBreadthFirst.reverse()
        depthTwoRightRight.breadthFirst(reverse = true) shouldBe depthTwoRightRightBreadthFirst.reverse()
        depthTwoRightLeft.breadthFirst(reverse = true) shouldBe depthTwoRightLeftBreadthFirst.reverse()

        wikiTree.breadthFirst(reverse = true) shouldBe
            FLCons(hEntry,
                FLCons(eEntry,
                    FLCons(cEntry,
                        FLCons(iEntry,
                            FLCons(dEntry,
                                FLCons(aEntry,
                                    FLCons(gEntry,
                                        FLCons(bEntry,
                                            FLCons(fEntry, FLNil)))))))))
        slideShareTree.breadthFirst(reverse = true) shouldBe slideShareBreadthFirst.reverse()
    }

    test("size") {
        FBSTNil.size() shouldBe 0
        FBSTNode(mEntry).size() shouldBe 1

        depthOneRight.size() shouldBe 2
        depthOneLeft.size() shouldBe 2
        depthOneFull.size() shouldBe 3

        depthTwoLeftRight.size() shouldBe 4
        depthTwoLeftLeft.size() shouldBe 4
        depthTwoRightRight.size() shouldBe 4
        depthTwoRightLeft.size() shouldBe 4

        wikiTree.size() shouldBe 9
        slideShareTree.size() shouldBe 8
    }

    test("maxDepth") {
        FBSTNil.maxDepth() shouldBe 0
        FBSTNode(mEntry).maxDepth() shouldBe 1

        depthOneRight.maxDepth() shouldBe 2
        depthOneLeft.maxDepth() shouldBe 2
        depthOneFull.maxDepth() shouldBe 2

        depthTwoLeftRight.maxDepth() shouldBe 3
        depthTwoLeftLeft.maxDepth() shouldBe 3
        depthTwoRightRight.maxDepth() shouldBe 3
        depthTwoRightLeft.maxDepth() shouldBe 3

        wikiTree.maxDepth() shouldBe 4
        slideShareTree.maxDepth() shouldBe 4
    }

    test("minDepth") {
        FBSTNil.minDepth() shouldBe 0
        // FBTNode(mEntry).minDepth() shouldBe 1

        depthOneRight.minDepth() shouldBe 2
        depthOneLeft.minDepth() shouldBe 2
        depthOneFull.minDepth() shouldBe 2

        depthTwoLeftRight.minDepth() shouldBe 2
        depthTwoLeftLeft.minDepth() shouldBe 2
        depthTwoRightRight.minDepth() shouldBe 2
        depthTwoRightLeft.minDepth() shouldBe 2

        wikiTree.minDepth() shouldBe 3
        slideShareTree.minDepth() shouldBe 3
    }

    test("map") {
        FBSTNil.mapi { 2 } shouldBe FBSTNil

        depthOneRight.mapi { s -> "z$s" }.inorder() shouldBe depthOneRight.inorder().map { TKVEntry.ofIntKey("z${it.getv()}") }
        depthOneRight.maps { s -> "z$s" }.inorder() shouldBe depthOneRight.inorder().map { TKVEntry.ofStrKey("z${it.getv()}") }
        depthOneLeft.mapi { s -> "z$s" }.inorder() shouldBe depthOneLeft.inorder().map { TKVEntry.ofIntKey("z${it.getv()}") }
        depthOneLeft.maps { s -> "z$s" }.inorder() shouldBe depthOneLeft.inorder().map { TKVEntry.ofStrKey("z${it.getv()}") }
        depthOneFull.mapi { s -> "z$s" }.inorder() shouldBe depthOneFull.inorder().map { TKVEntry.ofIntKey("z${it.getv()}") }
        depthOneFull.maps { s -> "z$s" }.inorder() shouldBe depthOneFull.inorder().map { TKVEntry.ofStrKey("z${it.getv()}") }
        wikiTree.mapi { s -> "z$s" }.inorder() shouldBe wikiTree.inorder().map { TKVEntry.ofIntKey("z${it.getv()}") }
        wikiTree.maps { s -> "z$s" }.inorder() shouldBe wikiTree.inorder().map { TKVEntry.ofStrKey("z${it.getv()}") }
        slideShareTree.mapi { s -> "z$s" }.inorder() shouldBe slideShareTree.inorder().map { TKVEntry.ofIntKey("z${it.getv()}") }
        slideShareTree.maps { s -> "z$s" }.inorder() shouldBe slideShareTree.inorder().map { TKVEntry.ofStrKey("z${it.getv()}") }
    }

    //
    // companion object
    //

    test("co.nul") {
        nul<Int, Int>() shouldBe FBSTNil
    }

    test("co.==") {
        (nul<Int, Int>() == nul<Int, Int>()) shouldBe true
        nul<Int, Int>().equal(nul<Int, Int>()) shouldBe true
        nul<Int, Int>().equal(FBSTNil) shouldBe true
        FBSTNil.equal(nul<Int, Int>()) shouldBe true
        FBSTNode(aEntry, FBSTNil, FBSTNil).equal(FBSTNode(aEntry, FBSTNil, FBSTNil)) shouldBe true
    }

    test("co.find") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = find(t, acc.head)) {
                        is FBSTNode -> found.entry shouldBe acc.head
                        is FBSTNil -> true shouldBe false
                    }
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        find(wikiTree, zEntry) shouldBe FBSTNil
        go(slideShareTree, slideShareBreadthFirst)
        find(slideShareTree, TKVEntry.ofIntKey(100)) shouldBe FBSTNil
    }

    test("co.findLast no dups") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = findLast(t, acc.head)) {
                        is FBSTNode -> {
                            found.entry shouldBe acc.head
                            isChildMatch(found, acc.head) shouldBe Pair(false, false)
                        }
                        is FBSTNil -> true shouldBe false
                    }
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        findLast(wikiTree, zEntry) shouldBe FBSTNil
        go(slideShareTree, slideShareBreadthFirst)
        findLast(slideShareTree, TKVEntry.ofIntKey(100)) shouldBe FBSTNil
    }

    test("co.findLast with dups") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = findLast(t, acc.head)) {
                        is FBSTNode -> {
                            found.entry shouldBe acc.head
                            isChildMatch(found, acc.head) shouldBe Pair(false, false)
                        }
                        is FBSTNil -> true shouldBe false
                    }
                    go(t, acc.tail)
                }
            }
        go(insert(wikiTree, wikiTree.root()!!, allowDups = true), wikiPreorder)
        go(insert(
            insert(wikiTree,
                wikiTree.root()!!, allowDups = true),
            wikiTree.root()!!, allowDups = true),
            wikiPreorder)
        go(insert(wikiTree, wikiTree.leftMost()!!, allowDups = true), wikiPreorder)
        go(insert(wikiTree, wikiTree.rightMost()!!, allowDups = true), wikiPreorder)
        go(insert(
            insert(slideShareTree, slideShareTree.leftMost()!!),
            slideShareTree.leftMost()!!),
            slideShareBreadthFirst)
        go(insert(
            insert(slideShareTree, slideShareTree.rightMost()!!),
            slideShareTree.rightMost()!!),
            slideShareBreadthFirst)
    }

    test("co.parent") {
        parent(FBSTNil, TKVEntry.ofIntKey("")) shouldBe FBSTNil
        parent(FBSTNode(mEntry), mEntry) shouldBe FBSTNil

        parent(depthOneLeft, lEntry) shouldBe depthOneLeft
        parent(depthOneRight, nEntry) shouldBe depthOneRight
        parent(depthOneFull, lEntry) shouldBe depthOneFull
        parent(depthOneFull, nEntry) shouldBe depthOneFull

        (parent(depthTwoLeftRight, mEntry) as FBSTNode).entry shouldBe lEntry
        (parent(depthTwoLeftRight, lEntry) as FBSTNode).entry shouldBe nEntry
        (parent(depthTwoLeftRight, sEntry) as FBSTNode).entry shouldBe nEntry
        (parent(depthTwoLeftLeft, eEntry) as FBSTNode).entry shouldBe lEntry
        (parent(depthTwoLeftLeft, lEntry) as FBSTNode).entry shouldBe nEntry
        (parent(depthTwoLeftLeft, sEntry) as FBSTNode).entry shouldBe nEntry
        (parent(depthTwoRightRight, uEntry) as FBSTNode).entry shouldBe sEntry
        (parent(depthTwoRightRight, sEntry) as FBSTNode).entry shouldBe nEntry
        (parent(depthTwoRightRight, mEntry) as FBSTNode).entry shouldBe nEntry
        (parent(depthTwoRightLeft, rEntry) as FBSTNode).entry shouldBe sEntry
        (parent(depthTwoRightLeft, sEntry) as FBSTNode).entry shouldBe nEntry
        (parent(depthTwoRightLeft, mEntry) as FBSTNode).entry shouldBe nEntry

        parent(wikiTree, fEntry)  /* parent of root */ shouldBe FBSTNil
        (parent(wikiTree, cEntry) as FBSTNode).entry shouldBe dEntry
        (parent(wikiTree, hEntry) as FBSTNode).entry shouldBe iEntry
        parent(wikiTree, zEntry) /* parent of missing value */ shouldBe FBSTNil

        (parent(slideShareTree, n32Entry) as FBSTNode).entry shouldBe n17Entry
        (parent(slideShareTree, n50Entry) as FBSTNode).entry shouldBe n78Entry
    }

    test("co.contains2") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    contains2(t, acc.head) shouldBe true
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        contains2(wikiTree, zEntry) shouldBe false
        go(slideShareTree, slideShareBreadthFirst)
        contains2(slideShareTree, TKVEntry.ofIntKey(100)) shouldBe false
    }

    test("co.contains") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    (acc.head in t) shouldBe true
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        contains2(wikiTree, zEntry) shouldBe false
        go(slideShareTree, slideShareBreadthFirst)
        contains2(slideShareTree, TKVEntry.ofIntKey(100)) shouldBe false
    }

    test("co.prune") {
        prune(wikiTree, zEntry) /* missing match */ shouldBe wikiTree
        prune(wikiTree, fEntry) /* prune at root */ shouldBe FBSTNil

        prune(depthOneLeft,lEntry) shouldBe FBSTNode(mEntry)
        prune(depthOneRight,nEntry) shouldBe FBSTNode(mEntry)
        prune(depthOneFull,lEntry) shouldBe depthOneRight
        prune(depthOneFull,nEntry) shouldBe depthOneLeft

        prune(depthTwoLeftRight, sEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(lEntry,
                    FBSTNil,
                    fbtAssert(FBSTNode(mEntry))
                ))
            ))
        prune(depthTwoLeftRight, mEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(lEntry)),
                fbtAssert(FBSTNode(sEntry))
            ))
        prune(depthTwoLeftRight, lEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                FBSTNil,
                fbtAssert(FBSTNode(sEntry))
            ))
        prune(depthTwoLeftRight, nEntry) shouldBe FBSTNil

        prune(depthTwoLeftLeft, sEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(lEntry,
                    fbtAssert(FBSTNode(eEntry)),
                    FBSTNil))
            ))
        prune(depthTwoLeftLeft, eEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(lEntry)),
                fbtAssert(FBSTNode(sEntry))
            ))
        prune(depthTwoLeftLeft, lEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                FBSTNil,
                fbtAssert(FBSTNode(sEntry))
            ))
        prune(depthTwoLeftLeft, nEntry) shouldBe FBSTNil

        prune(depthTwoRightRight, uEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(mEntry)),
                fbtAssert(FBSTNode(sEntry))
            ))
        prune(depthTwoRightRight, sEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(mEntry)),
                FBSTNil
            ))
        prune(depthTwoRightRight, mEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                FBSTNil,
                fbtAssert(FBSTNode(sEntry,
                    FBSTNil,
                    fbtAssert(FBSTNode(uEntry))))))
        prune(depthTwoRightRight, nEntry) shouldBe FBSTNil

        prune(depthTwoRightLeft, rEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(mEntry)),
                fbtAssert(FBSTNode(sEntry))
            ))
        prune(depthTwoRightLeft, sEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(mEntry)),
                FBSTNil
            ))
        prune(depthTwoRightLeft, mEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                FBSTNil,
                fbtAssert(FBSTNode(sEntry,
                    fbtAssert(FBSTNode(rEntry)),
                    FBSTNil))
            ))
        prune(depthTwoRightLeft, nEntry) shouldBe FBSTNil
    }

    test("co.insert item") {
        insert(FBSTNil,mEntry) shouldBe FBSTNode(mEntry)
        insert(FBSTNode(mEntry),lEntry) shouldBe depthOneLeft
        insert(FBSTNode(mEntry),nEntry) shouldBe depthOneRight

        insert(depthOneLeft,nEntry) shouldBe depthOneFull
        insert(depthOneRight,lEntry) shouldBe depthOneFull

        insert(depthTwoLeftPartial,mEntry) shouldBe depthTwoLeftRight
        insert(depthTwoLeftPartial,eEntry) shouldBe depthTwoLeftLeft
        insert(depthTwoRightPartial,uEntry) shouldBe depthTwoRightRight
        insert(depthTwoRightPartial,rEntry) shouldBe depthTwoRightLeft

        // --
        insert(FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry)),pEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNil, FBSTNode(pEntry)))
        insert(FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry)),nEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNode(nEntry), FBSTNil))
        insert(FBSTNode(mEntry, FBSTNode(cEntry), FBSTNode(zEntry)),dEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNil, FBSTNode(dEntry)), FBSTNode(zEntry))
        insert(FBSTNode(mEntry, FBSTNode(cEntry), FBSTNode(zEntry)),bEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNode(bEntry), FBSTNil), FBSTNode(zEntry))
        // --
        insert(FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry)),pEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNil, FBSTNode(pEntry)))
        insert(FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNil, FBSTNode(pEntry))),nEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNode(nEntry), FBSTNode(pEntry)))
        // --
        insert(FBSTNode(mEntry, FBSTNode(cEntry), FBSTNode(zEntry)),dEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNil, FBSTNode(dEntry)), FBSTNode(zEntry))
        insert(FBSTNode(mEntry, FBSTNode(cEntry, FBSTNil, FBSTNode(dEntry)), FBSTNode(zEntry)), bEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNode(bEntry), FBSTNode(dEntry)), FBSTNode(zEntry))
        // --
        print(insert(FBSTNode(mEntry, FBSTNode(cEntry, FBSTNode(bEntry), FBSTNode(dEntry)), FBSTNode(zEntry)), bEntry))

    }

    test("co.insert item dups multiple") {
        insert(FBSTNil, mEntry, allowDups = true) shouldBe FBSTNode(mEntry)
        insert(FBSTNode(mEntry), mEntry, allowDups = true) shouldBe FBSTNode(
            mEntry,
            FBSTNil,
            FBSTNode(mEntry)
        )
        insert(FBSTNode(mEntry), lEntry, allowDups = true) shouldBe depthOneLeft
        insert(FBSTNode(mEntry), nEntry, allowDups = true) shouldBe depthOneRight

        insert(FBSTNode(mEntry), mEntry, allowDups = true) shouldBe FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry))
        insert(depthOneLeft, nEntry, allowDups = true) shouldBe depthOneFull
        insert(depthOneRight, lEntry, allowDups = true) shouldBe depthOneFull

        // dups right
        insert(depthOneLeft, mEntry, allowDups = true) shouldBe
            FBSTNode(
                mEntry,
                FBSTNode(lEntry),
                FBSTNode(mEntry)
            )
        insert(
            insert(depthOneLeft, mEntry, allowDups = true),
            mEntry, allowDups = true
        ) shouldBe
            FBSTNode(
                mEntry,
                FBSTNode(lEntry),
                FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry))
            )
        insert(
            insert(
                insert(depthOneLeft, mEntry, allowDups = true),
                mEntry, allowDups = true
            ),
            mEntry, allowDups = true
        ) shouldBe
            FBSTNode(
                mEntry,
                FBSTNode(lEntry),
                FBSTNode(
                    mEntry,
                    FBSTNil,
                    FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry))
                )
            )

        // dups left
        insert(depthOneLeft, lEntry, allowDups = true) shouldBe
            FBSTNode(
                mEntry,
                FBSTNode(lEntry, FBSTNil, FBSTNode(lEntry)),
                FBSTNil
            )
        insert(
            insert(depthOneLeft, lEntry, allowDups = true),
            lEntry, allowDups = true
        ) shouldBe
            FBSTNode(
                mEntry,
                FBSTNode(
                    lEntry,
                    FBSTNil,
                    FBSTNode(lEntry, FBSTNil, FBSTNode(lEntry))
                ),
                FBSTNil
            )
        insert(
            insert(
                insert(depthOneLeft, lEntry, allowDups = true),
                lEntry, allowDups = true
            ),
            lEntry, allowDups = true
        ) shouldBe
            FBSTNode(
                mEntry,
                FBSTNode(
                    lEntry,
                    FBSTNil,
                    FBSTNode(
                        lEntry,
                        FBSTNil,
                        FBSTNode(lEntry, FBSTNil, FBSTNode(lEntry))
                    )
                ),
                FBSTNil
            )

        // dups right
        insert(depthOneRight, mEntry, allowDups = true) shouldBe
            FBSTNode(mEntry, FBSTNil, depthOneRight)
        insert(
            insert(depthOneRight, mEntry, allowDups = true),
            mEntry, allowDups = true
        ) shouldBe
            FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry, FBSTNil, depthOneRight))
        insert(
            insert(
                insert(depthOneRight, mEntry, allowDups = true),
                mEntry, allowDups = true
            ),
            mEntry, allowDups = true
        ) shouldBe
            FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry, FBSTNil, depthOneRight)))

        // dups left
        insert(depthOneRight, nEntry, allowDups = true) shouldBe
            FBSTNode(
                mEntry,
                FBSTNil,
                FBSTNode(nEntry, FBSTNil, FBSTNode(nEntry))
            )
        insert(
            insert(depthOneRight, nEntry, allowDups = true),
            nEntry, allowDups = true
        ) shouldBe
            FBSTNode(
                mEntry,
                FBSTNil,
                FBSTNode(
                    nEntry,
                    FBSTNil,
                    FBSTNode(nEntry, FBSTNil, FBSTNode(nEntry))
                )
            )
        insert(
            insert(
                insert(depthOneRight, nEntry, allowDups = true),
                nEntry, allowDups = true
            ),
            nEntry, allowDups = true
        ) shouldBe
            FBSTNode(
                mEntry,
                FBSTNil,
                FBSTNode(
                    nEntry,
                    FBSTNil,
                    FBSTNode(
                        nEntry,
                        FBSTNil,
                        FBSTNode(nEntry, FBSTNil, FBSTNode(nEntry))
                    )
                )
            )
    }

    test("co.insert item dups single") {

        insert(depthTwoLeftPartial,mEntry, allowDups = true) shouldBe depthTwoLeftRight
        insert(depthTwoLeftPartial,eEntry, allowDups = true) shouldBe depthTwoLeftLeft
        insert(depthTwoRightPartial,uEntry, allowDups = true) shouldBe depthTwoRightRight
        insert(depthTwoRightPartial,rEntry, allowDups = true) shouldBe depthTwoRightLeft

        insert(depthTwoLeftRight,nEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNil,
                    FBSTNode(mEntry)),
                FBSTNode(nEntry,
                    FBSTNil,
                    FBSTNode(sEntry)))
        insert(depthTwoLeftRight,lEntry, allowDups = true) shouldBe
        FBSTNode(nEntry,
            FBSTNode(lEntry,
                FBSTNil,
                FBSTNode(lEntry,
                    FBSTNil,
                    FBSTNode(mEntry))),
            FBSTNode(sEntry))
        insert(depthTwoLeftRight,mEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNil,
                    FBSTNode(mEntry,
                        FBSTNil,
                        FBSTNode(mEntry))),
                FBSTNode(sEntry))
        insert(depthTwoLeftRight,sEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNil,
                    FBSTNode(mEntry)),
                FBSTNode(sEntry, FBSTNil, FBSTNode(sEntry)))

        insert(depthTwoLeftLeft,nEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNode(eEntry),
                    FBSTNil),
                FBSTNode(nEntry,
                    FBSTNil,
                    FBSTNode(sEntry)))
        insert(depthTwoLeftLeft,lEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNode(eEntry),
                    FBSTNode(lEntry)),
                FBSTNode(sEntry))
        insert(depthTwoLeftLeft,eEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNode(eEntry,
                        FBSTNil,
                        FBSTNode(eEntry)),
                    FBSTNil),
                FBSTNode(sEntry))
        insert(depthTwoLeftLeft,sEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNode(eEntry),
                    FBSTNil),
                FBSTNode(sEntry, FBSTNil, FBSTNode(sEntry)))

        insert(depthTwoRightRight,nEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(nEntry,
                    FBSTNil,
                    FBSTNode(sEntry,
                        FBSTNil,
                        FBSTNode(uEntry))))
        insert(depthTwoRightRight,mEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry,
                    FBSTNil,
                    FBSTNode(mEntry)),
                FBSTNode(sEntry,
                    FBSTNil,
                    FBSTNode(uEntry)))
        insert(depthTwoRightRight,sEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(sEntry,
                    FBSTNil,
                    FBSTNode(sEntry,
                        FBSTNil,
                        FBSTNode(uEntry))))
        insert(depthTwoRightRight,uEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(sEntry,
                    FBSTNil,
                    FBSTNode(uEntry,
                        FBSTNil,
                        FBSTNode(uEntry))))

        insert(depthTwoRightLeft,nEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(nEntry,
                    FBSTNil,
                    FBSTNode(sEntry,
                        FBSTNode(rEntry),
                        FBSTNil)))
        insert(depthTwoRightLeft,mEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry,
                    FBSTNil,
                    FBSTNode(mEntry)),
                FBSTNode(sEntry,
                    FBSTNode(rEntry),
                    FBSTNil))
        insert(depthTwoRightLeft,sEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(sEntry,
                    FBSTNode(rEntry),
                    FBSTNode(sEntry)))
        insert(depthTwoRightLeft,rEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(sEntry,
                    FBSTNode(rEntry,
                        FBSTNil,
                        FBSTNode(rEntry)),
                    FBSTNil))

    }

    test("co.insert item (property), small") {
        checkAll(50, Arb.int(20..100)) { n ->
            val values = IntArray(n) { _: Int -> nextInt() }
            val bst = FBSTree.ofValues<Int, Int>(values.iterator(), allowDups = true)
            bst.size() shouldBe n
            val aut = bst.inorder()
            values.sort()
            val testOracle = FList.of(values.iterator()).map { TKVEntry.ofIntKey(it) }
            aut shouldBe testOracle
        }
    }

    test("co.insert item (property), large") {
        checkAll(2, Arb.int(10000..50000)) { n ->
            val values = IntArray(n) { _: Int -> nextInt() }
            val bst = FBSTree.ofValues<Int, Int>(values.iterator(), allowDups = true)
            bst.size() shouldBe n
            print("size "+n)
            print(", max depth "+bst.maxDepth())
            println          (", min depth "+bst.minDepth())
            val aut = bst.inorder()
            values.sort()
            val testOracle = FList.of(values.iterator()).map { TKVEntry.ofIntKey(it) }
            aut shouldBe testOracle
        }
    }

    test("co.addGraft") {
        addGraft(FBSTNil, FBSTNil) shouldBe FBSTNil
        addGraft(depthOneFull, FBSTNil) shouldBe depthOneFull
        addGraft(FBSTNil, depthOneFull) shouldBe depthOneFull

        addGraft(prune(depthOneFull, nEntry), FBSTNode(nEntry)) shouldBe depthOneFull
        addGraft(prune(depthOneFull, lEntry), FBSTNode(lEntry)) shouldBe depthOneFull

        addGraft(prune(depthTwoLeftRight, mEntry), FBSTNode(mEntry)) shouldBe depthTwoLeftRight
        addGraft(prune(depthTwoLeftRight, sEntry), find(depthTwoLeftRight,sEntry)) shouldBe depthTwoLeftRight
        addGraft(prune(depthTwoLeftRight, lEntry), find(depthTwoLeftRight,lEntry)) shouldBe depthTwoLeftRight

        addGraft(prune(depthTwoLeftLeft, eEntry), FBSTNode(eEntry)) shouldBe depthTwoLeftLeft
        addGraft(prune(depthTwoLeftLeft, sEntry), find(depthTwoLeftLeft,sEntry)) shouldBe depthTwoLeftLeft
        addGraft(prune(depthTwoLeftLeft, lEntry), find(depthTwoLeftLeft,lEntry)) shouldBe depthTwoLeftLeft

        addGraft(prune(depthTwoRightRight, uEntry), FBSTNode(uEntry)) shouldBe depthTwoRightRight
        addGraft(prune(depthTwoRightRight, sEntry), find(depthTwoRightRight,sEntry)) shouldBe depthTwoRightRight
        addGraft(prune(depthTwoRightRight, mEntry), find(depthTwoRightRight,mEntry)) shouldBe depthTwoRightRight

        addGraft(prune(depthTwoRightLeft, rEntry), FBSTNode(rEntry)) shouldBe depthTwoRightLeft
        addGraft(prune(depthTwoRightLeft, sEntry), find(depthTwoRightLeft,sEntry)) shouldBe depthTwoRightLeft
        addGraft(prune(depthTwoRightLeft, mEntry), find(depthTwoRightLeft,mEntry)) shouldBe depthTwoRightLeft
    }

    test("co.delete no dups") {

        tailrec fun <A: Comparable<A>, B: Any> goAll(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val deleted = delete(t, acc.head)) {
                        is FBSTNode -> {
                            deleted.inorder() shouldBe inorder.filterNot { it == acc.head }
                        }
                        is FBSTNil -> true shouldBe false
                    }
                    goAll(t, acc.tail, inorder)
                }
            }

        tailrec fun <A: Comparable<A>, B: Any> goTele(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    val deleted = delete(t, acc.head)
                    val oracle = inorder.filterNot { it == acc.head }
                    when (deleted) {
                        is FBSTNode -> {
                            deleted.inorder() shouldBe oracle
                        }
                        is FBSTNil -> deleted.size() shouldBe 0
                    }
                    goTele(deleted, acc.tail, oracle)
                }
            }

        goAll(wikiTree, wikiPreorder, wikiInorder)
        goAll(wikiTree, wikiInorder, wikiInorder)
        goAll(wikiTree, wikiPostorder, wikiInorder)
        goAll(wikiTree, wikiPreorder.reverse(), wikiInorder)
        goAll(wikiTree, wikiInorder.reverse(), wikiInorder)
        goAll(wikiTree, wikiPostorder.reverse(), wikiInorder)
        delete(wikiTree, zEntry) shouldBe wikiTree
        goTele(wikiTree, wikiPreorder, wikiInorder)
        goTele(wikiTree, wikiInorder, wikiInorder)
        goTele(wikiTree, wikiPostorder, wikiInorder)
        goTele(wikiTree, wikiPreorder.reverse(), wikiInorder)
        goTele(wikiTree, wikiInorder.reverse(), wikiInorder)
        goTele(wikiTree, wikiPostorder.reverse(), wikiInorder)

        goAll(slideShareTree, slideSharePreorder, slideShareInorder)
        goAll(slideShareTree, slideShareInorder, slideShareInorder)
        goAll(slideShareTree, slideSharePostorder, slideShareInorder)
        goAll(slideShareTree, slideShareBreadthFirst, slideShareInorder)
        goAll(slideShareTree, slideSharePreorder.reverse(), slideShareInorder)
        goAll(slideShareTree, slideShareInorder.reverse(), slideShareInorder)
        goAll(slideShareTree, slideSharePostorder.reverse(), slideShareInorder)
        goAll(slideShareTree, slideShareBreadthFirst.reverse(), slideShareInorder)
        delete(slideShareTree, TKVEntry.ofIntKey(100)) shouldBe slideShareTree
        goTele(slideShareTree, slideSharePreorder, slideShareInorder)
        goTele(slideShareTree, slideShareInorder, slideShareInorder)
        goTele(slideShareTree, slideSharePostorder, slideShareInorder)
        goTele(slideShareTree, slideShareBreadthFirst, slideShareInorder)
        goTele(slideShareTree, slideSharePreorder.reverse(), slideShareInorder)
        goTele(slideShareTree, slideShareInorder.reverse(), slideShareInorder)
        goTele(slideShareTree, slideSharePostorder.reverse(), slideShareInorder)
        goTele(slideShareTree, slideShareBreadthFirst.reverse(), slideShareInorder)
    }

    test("co.delete with dups (all dups)") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val deleted = delete(t, acc.head)) {
                        is FBSTNode -> deleted.inorder() shouldBe inorder.filterNot { it == acc.head }
                        is FBSTNil -> true shouldBe false
                    }
                    go(t, acc.tail, inorder)
                }
            }
        val aux1 = insert(wikiTree, wikiTree.root()!!, allowDups = true)
        go(aux1, wikiPreorder, aux1.inorder())
        val aux2 = insert(
            insert(wikiTree,
                wikiTree.root()!!, allowDups = true),
            wikiTree.root()!!, allowDups = true)
        go(aux2, wikiPreorder, aux2.inorder())
        val aux3 = insert(wikiTree, wikiTree.leftMost()!!, allowDups = true)
        go(aux3, wikiPreorder, aux3.inorder())
        val aux4 = insert(wikiTree, wikiTree.rightMost()!!, allowDups = true)
        go(aux4, wikiPreorder, aux4.inorder())
        val aux5 = insert(
            insert(slideShareTree, slideShareTree.leftMost()!!),
            slideShareTree.leftMost()!!)
        go(aux5, slideShareBreadthFirst, aux5.inorder())
        val aux6 = insert(
            insert(slideShareTree, slideShareTree.rightMost()!!),
            slideShareTree.rightMost()!!)
        go(aux6, slideShareBreadthFirst, aux6.inorder())
    }

    test("co.delete with dups (single)") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val deleted = delete(t, acc.head, onlyOneIfDuplicate = true)) {
                        is FBSTNode -> deleted.inorder() shouldBe inorder.dropFirst { it == acc.head }
                        is FBSTNil -> true shouldBe false
                    }
                    go(t, acc.tail, inorder)
                }
            }
        val aux1 = insert(wikiTree, wikiTree.root()!!, allowDups = true)
        go(aux1, wikiPreorder, aux1.inorder())
        val aux2 = insert(
            insert(wikiTree,
                wikiTree.root()!!, allowDups = true),
            wikiTree.root()!!, allowDups = true)
        go(aux2, wikiPreorder, aux2.inorder())
        val aux3 = insert(wikiTree, wikiTree.leftMost()!!, allowDups = true)
        go(aux3, wikiPreorder, aux3.inorder())
        val aux4 = insert(wikiTree, wikiTree.rightMost()!!, allowDups = true)
        go(aux4, wikiPreorder, aux4.inorder())
        val aux5 = insert(
            insert(slideShareTree, slideShareTree.leftMost()!!),
            slideShareTree.leftMost()!!)
        go(aux5, slideShareBreadthFirst, aux5.inorder())
        val aux6 = insert(
            insert(slideShareTree, slideShareTree.rightMost()!!),
            slideShareTree.rightMost()!!)
        go(aux6, slideShareBreadthFirst, aux6.inorder())
    }

    test("co.of(list)") {
        FBSTree.of<Int, Int>(FLNil) shouldBe FBSTNil
        FBSTree.of(FList.of(*arrayOf(mEntry,lEntry,nEntry))) shouldBe FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(nEntry))
        FBSTree.of(FList.of(*arrayOf(mEntry,cEntry,bEntry,dEntry,zEntry,bEntry)), allowDups = true) shouldBe
            FBSTNode(mEntry,
                FBSTNode(cEntry,
                    FBSTNode(bEntry, FBSTNil,
                        FBSTNode(bEntry, FBSTNil, FBSTNil)),
                    FBSTNode(dEntry, FBSTNil, FBSTNil)),
                FBSTNode(zEntry, FBSTNil, FBSTNil))
        FBSTree.of(FList.of(*arrayOf(mEntry,cEntry,bEntry,dEntry,zEntry,bEntry)) /*, allowDups = false */) shouldBe
                FBSTNode(mEntry,
                    FBSTNode(cEntry,
                        FBSTNode(bEntry, FBSTNil, FBSTNil),
                        FBSTNode(dEntry, FBSTNil, FBSTNil)),
                    FBSTNode(zEntry, FBSTNil, FBSTNil))
        FBSTree.of(wikiPreorder) shouldBe wikiTree
        FBSTree.of(slideSharePreorder) shouldBe slideShareTree
    }
})
