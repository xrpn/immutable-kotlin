package com.xrpn.immutable

import com.xrpn.immutable.FBSTree.Companion.fbtAssert
import com.xrpn.immutable.FBSTree.Companion.nul
import com.xrpn.immutable.FBSTree.Companion.bstParent
import com.xrpn.immutable.FBSTree.Companion.bstPrune
import com.xrpn.immutable.FBSTree.Companion.bstFind
import com.xrpn.immutable.FBSTree.Companion.bstFindLast
import com.xrpn.immutable.FBSTree.Companion.addGraftTestingGremlin
import com.xrpn.immutable.FBSTree.Companion.bstContains2
import com.xrpn.immutable.FBSTree.Companion.bstInsert
import com.xrpn.immutable.FBSTree.Companion.bstDelete
import io.kotest.assertions.fail
import io.kotest.property.Arb
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.random.Random.Default.nextInt

class FBSTreeCompanionTest : FunSpec({

    beforeTest {}

    fun <A, B: Any> isChildMatchValidation(node: FBSTNode<A, B>, match: TKVEntry<A, B>): Pair<Boolean, Boolean> where A: Any, A: Comparable<A> {
        val leftChildMatch = (node.bLeft is FBSTNode) && node.bLeft.entry == match
        val rightChildMatch = (node.bRight is FBSTNode) && node.bRight.entry == match
        return Pair(leftChildMatch, rightChildMatch)
    }

    test("co.nul") {
        nul<Int, Int>() shouldBe FBSTNil
    }

    test("co.==") {
        (nul<Int, Int>() == nul<Int, Int>()) shouldBe true
        nul<Int, Int>().equal(nul<Int, Int>()) shouldBe true
        nul<Int, Int>().equal(FBSTNil) shouldBe true
        // should not compile
        // FBSTNil.equal(nul<Int, Int>()) shouldBe true
        FBSTNode(aEntry, FBSTNil, FBSTNil).equal(FBSTNode(aEntry, FBSTNil, FBSTNil)) shouldBe true
    }

    test("co.find") {

        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = bstFind(t, acc.head)) {
                        is FBSTNode -> found.entry shouldBe acc.head
                        else -> fail("not found: ${acc.head}")
                    }
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        bstFind(wikiTree, zEntry) shouldBe null
        go(slideShareTree, slideShareBreadthFirst)
        bstFind(slideShareTree, TKVEntry.ofIntKey(100)) shouldBe null
    }

    test("co.findLast no dups") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = bstFindLast(t, acc.head)) {
                        is FBSTNode -> {
                            found.entry shouldBe acc.head
                            isChildMatchValidation(found, acc.head) shouldBe Pair(false, false)
                        }
                        else -> fail("not found: ${acc.head}")
                    }
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        bstFindLast(wikiTree, zEntry) shouldBe null
        go(slideShareTree, slideShareBreadthFirst)
        bstFindLast(slideShareTree, TKVEntry.ofIntKey(100)) shouldBe null
    }

    test("co.findLast with dups") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = bstFindLast(t, acc.head)) {
                        is FBSTNode -> {
                            found.entry shouldBe acc.head
                            isChildMatchValidation(found, acc.head) shouldBe Pair(false, false)
                        }
                        else -> fail("not found: ${acc.head}")
                    }
                    go(t, acc.tail)
                }
            }
        go(bstInsert(wikiTree, wikiTree.froot()!!, allowDups = true), wikiPreorder)
        go(bstInsert(
            bstInsert(wikiTree,
                wikiTree.froot()!!, allowDups = true),
            wikiTree.froot()!!, allowDups = true),
            wikiPreorder)
        go(bstInsert(wikiTree, wikiTree.fleftMost()!!, allowDups = true), wikiPreorder)
        go(bstInsert(wikiTree, wikiTree.frightMost()!!, allowDups = true), wikiPreorder)
        go(bstInsert(
            bstInsert(slideShareTree, slideShareTree.fleftMost()!!),
            slideShareTree.fleftMost()!!),
            slideShareBreadthFirst)
        go(bstInsert(
            bstInsert(slideShareTree, slideShareTree.frightMost()!!),
            slideShareTree.frightMost()!!),
            slideShareBreadthFirst)
    }

    test("co.parent") {
        bstParent(FBSTNil, TKVEntry.ofIntKey("")) shouldBe FBSTNil
        bstParent(FBSTNode(mEntry), mEntry) shouldBe FBSTNil

        bstParent(depthOneLeft, lEntry) shouldBe depthOneLeft
        bstParent(depthOneRight, nEntry) shouldBe depthOneRight
        bstParent(depthOneFull, lEntry) shouldBe depthOneFull
        bstParent(depthOneFull, nEntry) shouldBe depthOneFull

        (bstParent(depthTwoLeftRight, mEntry) as FBSTNode).entry shouldBe lEntry
        (bstParent(depthTwoLeftRight, lEntry) as FBSTNode).entry shouldBe nEntry
        (bstParent(depthTwoLeftRight, sEntry) as FBSTNode).entry shouldBe nEntry
        (bstParent(depthTwoLeftLeft, eEntry) as FBSTNode).entry shouldBe lEntry
        (bstParent(depthTwoLeftLeft, lEntry) as FBSTNode).entry shouldBe nEntry
        (bstParent(depthTwoLeftLeft, sEntry) as FBSTNode).entry shouldBe nEntry
        (bstParent(depthTwoRightRight, uEntry) as FBSTNode).entry shouldBe sEntry
        (bstParent(depthTwoRightRight, sEntry) as FBSTNode).entry shouldBe nEntry
        (bstParent(depthTwoRightRight, mEntry) as FBSTNode).entry shouldBe nEntry
        (bstParent(depthTwoRightLeft, rEntry) as FBSTNode).entry shouldBe sEntry
        (bstParent(depthTwoRightLeft, sEntry) as FBSTNode).entry shouldBe nEntry
        (bstParent(depthTwoRightLeft, mEntry) as FBSTNode).entry shouldBe nEntry

        bstParent(wikiTree, fEntry)  /* parent of root */ shouldBe FBSTNil
        (bstParent(wikiTree, cEntry) as FBSTNode).entry shouldBe dEntry
        (bstParent(wikiTree, hEntry) as FBSTNode).entry shouldBe iEntry
        bstParent(wikiTree, zEntry) /* parent of missing value */ shouldBe FBSTNil

        (bstParent(slideShareTree, n32Entry) as FBSTNode).entry shouldBe n17Entry
        (bstParent(slideShareTree, n50Entry) as FBSTNode).entry shouldBe n78Entry
    }

    test("co.contains2") {
        bstContains2(FBSTNil, zEntry) shouldBe false
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    val p = bstContains2(t, acc.head)
                    p shouldBe true
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        bstContains2(wikiTree, zEntry) shouldBe false
        go(slideShareTree, slideShareBreadthFirst)
        bstContains2(slideShareTree, TKVEntry.ofIntKey(100)) shouldBe false
    }

    test("co.contains") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    (t.fcontains(acc.head)) shouldBe true
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        bstContains2(wikiTree, zEntry) shouldBe false
        go(slideShareTree, slideShareBreadthFirst)
        bstContains2(slideShareTree, TKVEntry.ofIntKey(100)) shouldBe false
    }

    test("co.prune") {
        bstPrune(wikiTree, zEntry) /* missing match */ shouldBe wikiTree
        bstPrune(wikiTree, fEntry) /* prune at root */ shouldBe FBSTNil

        bstPrune(depthOneLeft,lEntry) shouldBe FBSTNode(mEntry)
        bstPrune(depthOneRight,nEntry) shouldBe FBSTNode(mEntry)
        bstPrune(depthOneFull,lEntry) shouldBe depthOneRight
        bstPrune(depthOneFull,nEntry) shouldBe depthOneLeft

        bstPrune(depthTwoLeftRight, sEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(lEntry,
                    FBSTNil,
                    fbtAssert(FBSTNode(mEntry))
                ))
            ))
        bstPrune(depthTwoLeftRight, mEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(lEntry)),
                fbtAssert(FBSTNode(sEntry))
            ))
        bstPrune(depthTwoLeftRight, lEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                FBSTNil,
                fbtAssert(FBSTNode(sEntry))
            ))
        bstPrune(depthTwoLeftRight, nEntry) shouldBe FBSTNil

        bstPrune(depthTwoLeftLeft, sEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(lEntry,
                    fbtAssert(FBSTNode(eEntry)),
                    FBSTNil))
            ))
        bstPrune(depthTwoLeftLeft, eEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(lEntry)),
                fbtAssert(FBSTNode(sEntry))
            ))
        bstPrune(depthTwoLeftLeft, lEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                FBSTNil,
                fbtAssert(FBSTNode(sEntry))
            ))
        bstPrune(depthTwoLeftLeft, nEntry) shouldBe FBSTNil

        bstPrune(depthTwoRightRight, uEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(mEntry)),
                fbtAssert(FBSTNode(sEntry))
            ))
        bstPrune(depthTwoRightRight, sEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(mEntry)),
                FBSTNil
            ))
        bstPrune(depthTwoRightRight, mEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                FBSTNil,
                fbtAssert(FBSTNode(sEntry,
                    FBSTNil,
                    fbtAssert(FBSTNode(uEntry))))))
        bstPrune(depthTwoRightRight, nEntry) shouldBe FBSTNil

        bstPrune(depthTwoRightLeft, rEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(mEntry)),
                fbtAssert(FBSTNode(sEntry))
            ))
        bstPrune(depthTwoRightLeft, sEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                fbtAssert(FBSTNode(mEntry)),
                FBSTNil
            ))
        bstPrune(depthTwoRightLeft, mEntry) shouldBe
            fbtAssert(FBSTNode(nEntry,
                FBSTNil,
                fbtAssert(FBSTNode(sEntry,
                    fbtAssert(FBSTNode(rEntry)),
                    FBSTNil))
            ))
        bstPrune(depthTwoRightLeft, nEntry) shouldBe FBSTNil
    }

    test("co.insert item") {
        bstInsert(FBSTNil,mEntry) shouldBe FBSTNode(mEntry)
        bstInsert(FBSTNode(mEntry),lEntry) shouldBe depthOneLeft
        bstInsert(FBSTNode(mEntry),nEntry) shouldBe depthOneRight

        bstInsert(depthOneLeft,nEntry) shouldBe depthOneFull
        bstInsert(depthOneRight,lEntry) shouldBe depthOneFull

        bstInsert(depthTwoLeftPartial,mEntry) shouldBe depthTwoLeftRight
        bstInsert(depthTwoLeftPartial,eEntry) shouldBe depthTwoLeftLeft
        bstInsert(depthTwoRightPartial,uEntry) shouldBe depthTwoRightRight
        bstInsert(depthTwoRightPartial,rEntry) shouldBe depthTwoRightLeft

        // --
        bstInsert(FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry)),pEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNil, FBSTNode(pEntry)))
        bstInsert(FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry)),nEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNode(nEntry), FBSTNil))
        bstInsert(FBSTNode(mEntry, FBSTNode(cEntry), FBSTNode(zEntry)),dEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNil, FBSTNode(dEntry)), FBSTNode(zEntry))
        bstInsert(FBSTNode(mEntry, FBSTNode(cEntry), FBSTNode(zEntry)),bEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNode(bEntry), FBSTNil), FBSTNode(zEntry))
        // --
        bstInsert(FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry)),pEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNil, FBSTNode(pEntry)))
        bstInsert(FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNil, FBSTNode(pEntry))),nEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNode(nEntry), FBSTNode(pEntry)))
        // --
        bstInsert(FBSTNode(mEntry, FBSTNode(cEntry), FBSTNode(zEntry)),dEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNil, FBSTNode(dEntry)), FBSTNode(zEntry))
        bstInsert(FBSTNode(mEntry, FBSTNode(cEntry, FBSTNil, FBSTNode(dEntry)), FBSTNode(zEntry)), bEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNode(bEntry), FBSTNode(dEntry)), FBSTNode(zEntry))
    }

    test("co.insert item dups multiple") {
        bstInsert(FBSTNil, mEntry, allowDups = true) shouldBe FBSTNode(mEntry)
        bstInsert(FBSTNode(mEntry), mEntry, allowDups = true) shouldBe FBSTNode(
            mEntry,
            FBSTNil,
            FBSTNode(mEntry)
        )
        bstInsert(FBSTNode(mEntry), lEntry, allowDups = true) shouldBe depthOneLeft
        bstInsert(FBSTNode(mEntry), nEntry, allowDups = true) shouldBe depthOneRight

        bstInsert(FBSTNode(mEntry), mEntry, allowDups = true) shouldBe FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry))
        bstInsert(depthOneLeft, nEntry, allowDups = true) shouldBe depthOneFull
        bstInsert(depthOneRight, lEntry, allowDups = true) shouldBe depthOneFull

        // dups right
        bstInsert(depthOneLeft, mEntry, allowDups = true) shouldBe
            FBSTNode(
                mEntry,
                FBSTNode(lEntry),
                FBSTNode(mEntry)
            )
        bstInsert(
            bstInsert(depthOneLeft, mEntry, allowDups = true),
            mEntry, allowDups = true
        ) shouldBe
            FBSTNode(
                mEntry,
                FBSTNode(lEntry),
                FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry))
            )
        bstInsert(
            bstInsert(
                bstInsert(depthOneLeft, mEntry, allowDups = true),
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
        bstInsert(depthOneLeft, lEntry, allowDups = true) shouldBe
            FBSTNode(
                mEntry,
                FBSTNode(lEntry, FBSTNil, FBSTNode(lEntry)),
                FBSTNil
            )
        bstInsert(
            bstInsert(depthOneLeft, lEntry, allowDups = true),
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
        bstInsert(
            bstInsert(
                bstInsert(depthOneLeft, lEntry, allowDups = true),
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
        bstInsert(depthOneRight, mEntry, allowDups = true) shouldBe
            FBSTNode(mEntry, FBSTNil, depthOneRight)
        bstInsert(
            bstInsert(depthOneRight, mEntry, allowDups = true),
            mEntry, allowDups = true
        ) shouldBe
            FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry, FBSTNil, depthOneRight))
        bstInsert(
            bstInsert(
                bstInsert(depthOneRight, mEntry, allowDups = true),
                mEntry, allowDups = true
            ),
            mEntry, allowDups = true
        ) shouldBe
            FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry, FBSTNil, depthOneRight)))

        // dups left
        bstInsert(depthOneRight, nEntry, allowDups = true) shouldBe
            FBSTNode(
                mEntry,
                FBSTNil,
                FBSTNode(nEntry, FBSTNil, FBSTNode(nEntry))
            )
        bstInsert(
            bstInsert(depthOneRight, nEntry, allowDups = true),
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
        bstInsert(
            bstInsert(
                bstInsert(depthOneRight, nEntry, allowDups = true),
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

        bstInsert(depthTwoLeftPartial,mEntry, allowDups = true) shouldBe depthTwoLeftRight
        bstInsert(depthTwoLeftPartial,eEntry, allowDups = true) shouldBe depthTwoLeftLeft
        bstInsert(depthTwoRightPartial,uEntry, allowDups = true) shouldBe depthTwoRightRight
        bstInsert(depthTwoRightPartial,rEntry, allowDups = true) shouldBe depthTwoRightLeft

        bstInsert(depthTwoLeftRight,nEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNil,
                    FBSTNode(mEntry)),
                FBSTNode(nEntry,
                    FBSTNil,
                    FBSTNode(sEntry)))
        bstInsert(depthTwoLeftRight,lEntry, allowDups = true) shouldBe
        FBSTNode(nEntry,
            FBSTNode(lEntry,
                FBSTNil,
                FBSTNode(lEntry,
                    FBSTNil,
                    FBSTNode(mEntry))),
            FBSTNode(sEntry))
        bstInsert(depthTwoLeftRight,mEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNil,
                    FBSTNode(mEntry,
                        FBSTNil,
                        FBSTNode(mEntry))),
                FBSTNode(sEntry))
        bstInsert(depthTwoLeftRight,sEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNil,
                    FBSTNode(mEntry)),
                FBSTNode(sEntry, FBSTNil, FBSTNode(sEntry)))

        bstInsert(depthTwoLeftLeft,nEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNode(eEntry),
                    FBSTNil),
                FBSTNode(nEntry,
                    FBSTNil,
                    FBSTNode(sEntry)))
        bstInsert(depthTwoLeftLeft,lEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNode(eEntry),
                    FBSTNode(lEntry)),
                FBSTNode(sEntry))
        bstInsert(depthTwoLeftLeft,eEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNode(eEntry,
                        FBSTNil,
                        FBSTNode(eEntry)),
                    FBSTNil),
                FBSTNode(sEntry))
        bstInsert(depthTwoLeftLeft,sEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNode(eEntry),
                    FBSTNil),
                FBSTNode(sEntry, FBSTNil, FBSTNode(sEntry)))

        bstInsert(depthTwoRightRight,nEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(nEntry,
                    FBSTNil,
                    FBSTNode(sEntry,
                        FBSTNil,
                        FBSTNode(uEntry))))
        bstInsert(depthTwoRightRight,mEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry,
                    FBSTNil,
                    FBSTNode(mEntry)),
                FBSTNode(sEntry,
                    FBSTNil,
                    FBSTNode(uEntry)))
        bstInsert(depthTwoRightRight,sEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(sEntry,
                    FBSTNil,
                    FBSTNode(sEntry,
                        FBSTNil,
                        FBSTNode(uEntry))))
        bstInsert(depthTwoRightRight,uEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(sEntry,
                    FBSTNil,
                    FBSTNode(uEntry,
                        FBSTNil,
                        FBSTNode(uEntry))))

        bstInsert(depthTwoRightLeft,nEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(nEntry,
                    FBSTNil,
                    FBSTNode(sEntry,
                        FBSTNode(rEntry),
                        FBSTNil)))
        bstInsert(depthTwoRightLeft,mEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry,
                    FBSTNil,
                    FBSTNode(mEntry)),
                FBSTNode(sEntry,
                    FBSTNode(rEntry),
                    FBSTNil))
        bstInsert(depthTwoRightLeft,sEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(sEntry,
                    FBSTNode(rEntry),
                    FBSTNode(sEntry)))
        bstInsert(depthTwoRightLeft,rEntry, allowDups = true) shouldBe
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
            val bst = FBSTree.ofvi(values.iterator(), allowDups = true)
            bst.size shouldBe n
            val aut = bst.inorder()
            values.sort()
            val testOracle = FList.of(values.iterator()).fmap { TKVEntry.ofIntKey(it) }
            aut shouldBe testOracle
        }
    }

    test("co.insert item (property), large") {
        checkAll(2, Arb.int(10000..50000)) { n ->
            val values = IntArray(n) { _: Int -> nextInt() }
            val bst = FBSTree.ofvi(values.iterator(), allowDups = true)
            bst.size shouldBe n
            val aut = bst.inorder()
            values.sort()
            val testOracle = FList.of(values.iterator()).fmap { TKVEntry.ofIntKey(it) }
            aut shouldBe testOracle
        }
    }

    test("co.addGraftTesting") {
        addGraftTestingGremlin(FBSTNil, FBSTNil) shouldBe FBSTNil
        addGraftTestingGremlin(depthOneFull, FBSTNil) shouldBe depthOneFull
        addGraftTestingGremlin(FBSTNil, depthOneFull) shouldBe depthOneFull

        addGraftTestingGremlin(bstPrune(depthOneFull, nEntry), FBSTNode(nEntry)) shouldBe depthOneFull
        addGraftTestingGremlin(bstPrune(depthOneFull, lEntry), FBSTNode(lEntry)) shouldBe depthOneFull

        addGraftTestingGremlin(bstPrune(depthTwoLeftRight, mEntry), FBSTNode(mEntry)) shouldBe depthTwoLeftRight
        addGraftTestingGremlin(bstPrune(depthTwoLeftRight, sEntry), bstFind(depthTwoLeftRight,sEntry)!!) shouldBe depthTwoLeftRight
        addGraftTestingGremlin(bstPrune(depthTwoLeftRight, lEntry), bstFind(depthTwoLeftRight,lEntry)!!) shouldBe depthTwoLeftRight

        addGraftTestingGremlin(bstPrune(depthTwoLeftLeft, eEntry), FBSTNode(eEntry)) shouldBe depthTwoLeftLeft
        addGraftTestingGremlin(bstPrune(depthTwoLeftLeft, sEntry), bstFind(depthTwoLeftLeft,sEntry)!!) shouldBe depthTwoLeftLeft
        addGraftTestingGremlin(bstPrune(depthTwoLeftLeft, lEntry), bstFind(depthTwoLeftLeft,lEntry)!!) shouldBe depthTwoLeftLeft

        addGraftTestingGremlin(bstPrune(depthTwoRightRight, uEntry), FBSTNode(uEntry)) shouldBe depthTwoRightRight
        addGraftTestingGremlin(bstPrune(depthTwoRightRight, sEntry), bstFind(depthTwoRightRight,sEntry)!!) shouldBe depthTwoRightRight
        addGraftTestingGremlin(bstPrune(depthTwoRightRight, mEntry), bstFind(depthTwoRightRight,mEntry)!!) shouldBe depthTwoRightRight

        addGraftTestingGremlin(bstPrune(depthTwoRightLeft, rEntry), FBSTNode(rEntry)) shouldBe depthTwoRightLeft
        addGraftTestingGremlin(bstPrune(depthTwoRightLeft, sEntry), bstFind(depthTwoRightLeft,sEntry)!!) shouldBe depthTwoRightLeft
        addGraftTestingGremlin(bstPrune(depthTwoRightLeft, mEntry), bstFind(depthTwoRightLeft,mEntry)!!) shouldBe depthTwoRightLeft
    }

    test("co.delete no dups") {

        tailrec fun <A: Comparable<A>, B: Any> goAll(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val deleted = bstDelete(t, acc.head)) {
                        is FBSTNode -> {
                            deleted.inorder() shouldBe inorder.ffilterNot { it == acc.head }
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
                    val deleted = bstDelete(t, acc.head)
                    val oracle = inorder.ffilterNot { it == acc.head }
                    when (deleted) {
                        is FBSTNode -> {
                            deleted.inorder() shouldBe oracle
                        }
                        is FBSTNil -> deleted.size shouldBe 0
                    }
                    goTele(deleted, acc.tail, oracle)
                }
            }

        goAll(wikiTree, wikiPreorder, wikiInorder)
        goAll(wikiTree, wikiInorder, wikiInorder)
        goAll(wikiTree, wikiPostorder, wikiInorder)
        goAll(wikiTree, wikiPreorder.freverse(), wikiInorder)
        goAll(wikiTree, wikiInorder.freverse(), wikiInorder)
        goAll(wikiTree, wikiPostorder.freverse(), wikiInorder)
        bstDelete(wikiTree, zEntry) shouldBe wikiTree
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
        bstDelete(slideShareTree, TKVEntry.ofIntKey(100)) shouldBe slideShareTree
        goTele(slideShareTree, slideSharePreorder, slideShareInorder)
        goTele(slideShareTree, slideShareInorder, slideShareInorder)
        goTele(slideShareTree, slideSharePostorder, slideShareInorder)
        goTele(slideShareTree, slideShareBreadthFirst, slideShareInorder)
        goTele(slideShareTree, slideSharePreorder.freverse(), slideShareInorder)
        goTele(slideShareTree, slideShareInorder.freverse(), slideShareInorder)
        goTele(slideShareTree, slideSharePostorder.freverse(), slideShareInorder)
        goTele(slideShareTree, slideShareBreadthFirst.freverse(), slideShareInorder)
    }

    test("co.delete with dups (all dups)") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val deleted = bstDelete(t, acc.head)) {
                        is FBSTNode -> deleted.inorder() shouldBe inorder.ffilterNot { it == acc.head }
                        is FBSTNil -> true shouldBe false
                    }
                    go(t, acc.tail, inorder)
                }
            }
        val aux1 = bstInsert(wikiTree, wikiTree.froot()!!, allowDups = true)
        go(aux1, wikiPreorder, aux1.inorder())
        val aux2 = bstInsert(
            bstInsert(wikiTree,
                wikiTree.froot()!!, allowDups = true),
            wikiTree.froot()!!, allowDups = true)
        go(aux2, wikiPreorder, aux2.inorder())
        val aux3 = bstInsert(wikiTree, wikiTree.fleftMost()!!, allowDups = true)
        go(aux3, wikiPreorder, aux3.inorder())
        val aux4 = bstInsert(wikiTree, wikiTree.frightMost()!!, allowDups = true)
        go(aux4, wikiPreorder, aux4.inorder())
        val aux5 = bstInsert(
            bstInsert(slideShareTree, slideShareTree.fleftMost()!!),
            slideShareTree.fleftMost()!!)
        go(aux5, slideShareBreadthFirst, aux5.inorder())
        val aux6 = bstInsert(
            bstInsert(slideShareTree, slideShareTree.frightMost()!!),
            slideShareTree.frightMost()!!)
        go(aux6, slideShareBreadthFirst, aux6.inorder())
    }

    test("co.delete with dups (single)") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val deleted = bstDelete(t, acc.head, atMostOne = true)) {
                        is FBSTNode -> deleted.inorder() shouldBe inorder.fdropFirst { it == acc.head }
                        is FBSTNil -> true shouldBe false
                    }
                    go(t, acc.tail, inorder)
                }
            }
        val aux1 = bstInsert(wikiTree, wikiTree.froot()!!, allowDups = true)
        go(aux1, wikiPreorder, aux1.inorder())
        val aux2 = bstInsert(
            bstInsert(wikiTree,
                wikiTree.froot()!!, allowDups = true),
            wikiTree.froot()!!, allowDups = true)
        go(aux2, wikiPreorder, aux2.inorder())
        val aux3 = bstInsert(wikiTree, wikiTree.fleftMost()!!, allowDups = true)
        go(aux3, wikiPreorder, aux3.inorder())
        val aux4 = bstInsert(wikiTree, wikiTree.frightMost()!!, allowDups = true)
        go(aux4, wikiPreorder, aux4.inorder())
        val aux5 = bstInsert(
            bstInsert(slideShareTree, slideShareTree.fleftMost()!!),
            slideShareTree.fleftMost()!!)
        go(aux5, slideShareBreadthFirst, aux5.inorder())
        val aux6 = bstInsert(
            bstInsert(slideShareTree, slideShareTree.frightMost()!!),
            slideShareTree.frightMost()!!)
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
