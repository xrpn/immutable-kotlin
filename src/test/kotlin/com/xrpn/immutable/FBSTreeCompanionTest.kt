package com.xrpn.immutable

import com.xrpn.immutable.FBSTree.Companion.fbtAssert
import com.xrpn.immutable.FBSTree.Companion.isChildMatch
import com.xrpn.immutable.FBSTree.Companion.nul
import com.xrpn.immutable.FBSTree.Companion.fparent
import com.xrpn.immutable.FBSTree.Companion.prune
import com.xrpn.immutable.FBSTree.Companion.ffind
import com.xrpn.immutable.FBSTree.Companion.ffindLast
import com.xrpn.immutable.FBSTree.Companion.addGraft
import com.xrpn.immutable.FBSTree.Companion.fcontains2
import com.xrpn.immutable.FBSTree.Companion.finsert
import com.xrpn.immutable.FBSTree.Companion.finserts
import com.xrpn.immutable.FBSTree.Companion.fdelete
import com.xrpn.immutable.FBSTree.Companion.equal
import com.xrpn.immutable.FBSTree.Companion.fcontainsItem
import io.kotest.property.Arb
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.random.Random.Default.nextInt

class FBSTreeCompanionTest : FunSpec({

    beforeTest {}

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
                    when (val found = ffind(t, acc.head)) {
                        is FBSTNode -> found.entry shouldBe acc.head
                        is FBSTNil -> true shouldBe false
                    }
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        ffind(wikiTree, zEntry) shouldBe FBSTNil
        go(slideShareTree, slideShareBreadthFirst)
        ffind(slideShareTree, TKVEntry.ofIntKey(100)) shouldBe FBSTNil
    }

    test("co.findLast no dups") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = ffindLast(t, acc.head)) {
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
        ffindLast(wikiTree, zEntry) shouldBe FBSTNil
        go(slideShareTree, slideShareBreadthFirst)
        ffindLast(slideShareTree, TKVEntry.ofIntKey(100)) shouldBe FBSTNil
    }

    test("co.findLast with dups") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = ffindLast(t, acc.head)) {
                        is FBSTNode -> {
                            found.entry shouldBe acc.head
                            isChildMatch(found, acc.head) shouldBe Pair(false, false)
                        }
                        is FBSTNil -> true shouldBe false
                    }
                    go(t, acc.tail)
                }
            }
        go(finsert(wikiTree, wikiTree.froot()!!, allowDups = true), wikiPreorder)
        go(finsert(
            finsert(wikiTree,
                wikiTree.froot()!!, allowDups = true),
            wikiTree.froot()!!, allowDups = true),
            wikiPreorder)
        go(finsert(wikiTree, wikiTree.fleftMost()!!, allowDups = true), wikiPreorder)
        go(finsert(wikiTree, wikiTree.frightMost()!!, allowDups = true), wikiPreorder)
        go(finsert(
            finsert(slideShareTree, slideShareTree.fleftMost()!!),
            slideShareTree.fleftMost()!!),
            slideShareBreadthFirst)
        go(finsert(
            finsert(slideShareTree, slideShareTree.frightMost()!!),
            slideShareTree.frightMost()!!),
            slideShareBreadthFirst)
    }

    test("co.parent") {
        fparent(FBSTNil, TKVEntry.ofIntKey("")) shouldBe FBSTNil
        fparent(FBSTNode(mEntry), mEntry) shouldBe FBSTNil

        fparent(depthOneLeft, lEntry) shouldBe depthOneLeft
        fparent(depthOneRight, nEntry) shouldBe depthOneRight
        fparent(depthOneFull, lEntry) shouldBe depthOneFull
        fparent(depthOneFull, nEntry) shouldBe depthOneFull

        (fparent(depthTwoLeftRight, mEntry) as FBSTNode).entry shouldBe lEntry
        (fparent(depthTwoLeftRight, lEntry) as FBSTNode).entry shouldBe nEntry
        (fparent(depthTwoLeftRight, sEntry) as FBSTNode).entry shouldBe nEntry
        (fparent(depthTwoLeftLeft, eEntry) as FBSTNode).entry shouldBe lEntry
        (fparent(depthTwoLeftLeft, lEntry) as FBSTNode).entry shouldBe nEntry
        (fparent(depthTwoLeftLeft, sEntry) as FBSTNode).entry shouldBe nEntry
        (fparent(depthTwoRightRight, uEntry) as FBSTNode).entry shouldBe sEntry
        (fparent(depthTwoRightRight, sEntry) as FBSTNode).entry shouldBe nEntry
        (fparent(depthTwoRightRight, mEntry) as FBSTNode).entry shouldBe nEntry
        (fparent(depthTwoRightLeft, rEntry) as FBSTNode).entry shouldBe sEntry
        (fparent(depthTwoRightLeft, sEntry) as FBSTNode).entry shouldBe nEntry
        (fparent(depthTwoRightLeft, mEntry) as FBSTNode).entry shouldBe nEntry

        fparent(wikiTree, fEntry)  /* parent of root */ shouldBe FBSTNil
        (fparent(wikiTree, cEntry) as FBSTNode).entry shouldBe dEntry
        (fparent(wikiTree, hEntry) as FBSTNode).entry shouldBe iEntry
        fparent(wikiTree, zEntry) /* parent of missing value */ shouldBe FBSTNil

        (fparent(slideShareTree, n32Entry) as FBSTNode).entry shouldBe n17Entry
        (fparent(slideShareTree, n50Entry) as FBSTNode).entry shouldBe n78Entry
    }

    test("co.contains2") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    fcontains2(t, acc.head) shouldBe true
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        fcontains2(wikiTree, zEntry) shouldBe false
        go(slideShareTree, slideShareBreadthFirst)
        fcontains2(slideShareTree, TKVEntry.ofIntKey(100)) shouldBe false
    }

    test("co.contains") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    (t.fcontainsItem(acc.head)) shouldBe true
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        fcontains2(wikiTree, zEntry) shouldBe false
        go(slideShareTree, slideShareBreadthFirst)
        fcontains2(slideShareTree, TKVEntry.ofIntKey(100)) shouldBe false
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
        finsert(FBSTNil,mEntry) shouldBe FBSTNode(mEntry)
        finsert(FBSTNode(mEntry),lEntry) shouldBe depthOneLeft
        finsert(FBSTNode(mEntry),nEntry) shouldBe depthOneRight

        finsert(depthOneLeft,nEntry) shouldBe depthOneFull
        finsert(depthOneRight,lEntry) shouldBe depthOneFull

        finsert(depthTwoLeftPartial,mEntry) shouldBe depthTwoLeftRight
        finsert(depthTwoLeftPartial,eEntry) shouldBe depthTwoLeftLeft
        finsert(depthTwoRightPartial,uEntry) shouldBe depthTwoRightRight
        finsert(depthTwoRightPartial,rEntry) shouldBe depthTwoRightLeft

        // --
        finsert(FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry)),pEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNil, FBSTNode(pEntry)))
        finsert(FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry)),nEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNode(nEntry), FBSTNil))
        finsert(FBSTNode(mEntry, FBSTNode(cEntry), FBSTNode(zEntry)),dEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNil, FBSTNode(dEntry)), FBSTNode(zEntry))
        finsert(FBSTNode(mEntry, FBSTNode(cEntry), FBSTNode(zEntry)),bEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNode(bEntry), FBSTNil), FBSTNode(zEntry))
        // --
        finsert(FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry)),pEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNil, FBSTNode(pEntry)))
        finsert(FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNil, FBSTNode(pEntry))),nEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(lEntry), FBSTNode(oEntry, FBSTNode(nEntry), FBSTNode(pEntry)))
        // --
        finsert(FBSTNode(mEntry, FBSTNode(cEntry), FBSTNode(zEntry)),dEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNil, FBSTNode(dEntry)), FBSTNode(zEntry))
        finsert(FBSTNode(mEntry, FBSTNode(cEntry, FBSTNil, FBSTNode(dEntry)), FBSTNode(zEntry)), bEntry) shouldBe
            FBSTNode(mEntry, FBSTNode(cEntry, FBSTNode(bEntry), FBSTNode(dEntry)), FBSTNode(zEntry))
        // --
        print(finsert(FBSTNode(mEntry, FBSTNode(cEntry, FBSTNode(bEntry), FBSTNode(dEntry)), FBSTNode(zEntry)), bEntry))

    }

    test("co.insert item dups multiple") {
        finsert(FBSTNil, mEntry, allowDups = true) shouldBe FBSTNode(mEntry)
        finsert(FBSTNode(mEntry), mEntry, allowDups = true) shouldBe FBSTNode(
            mEntry,
            FBSTNil,
            FBSTNode(mEntry)
        )
        finsert(FBSTNode(mEntry), lEntry, allowDups = true) shouldBe depthOneLeft
        finsert(FBSTNode(mEntry), nEntry, allowDups = true) shouldBe depthOneRight

        finsert(FBSTNode(mEntry), mEntry, allowDups = true) shouldBe FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry))
        finsert(depthOneLeft, nEntry, allowDups = true) shouldBe depthOneFull
        finsert(depthOneRight, lEntry, allowDups = true) shouldBe depthOneFull

        // dups right
        finsert(depthOneLeft, mEntry, allowDups = true) shouldBe
            FBSTNode(
                mEntry,
                FBSTNode(lEntry),
                FBSTNode(mEntry)
            )
        finsert(
            finsert(depthOneLeft, mEntry, allowDups = true),
            mEntry, allowDups = true
        ) shouldBe
            FBSTNode(
                mEntry,
                FBSTNode(lEntry),
                FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry))
            )
        finsert(
            finsert(
                finsert(depthOneLeft, mEntry, allowDups = true),
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
        finsert(depthOneLeft, lEntry, allowDups = true) shouldBe
            FBSTNode(
                mEntry,
                FBSTNode(lEntry, FBSTNil, FBSTNode(lEntry)),
                FBSTNil
            )
        finsert(
            finsert(depthOneLeft, lEntry, allowDups = true),
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
        finsert(
            finsert(
                finsert(depthOneLeft, lEntry, allowDups = true),
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
        finsert(depthOneRight, mEntry, allowDups = true) shouldBe
            FBSTNode(mEntry, FBSTNil, depthOneRight)
        finsert(
            finsert(depthOneRight, mEntry, allowDups = true),
            mEntry, allowDups = true
        ) shouldBe
            FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry, FBSTNil, depthOneRight))
        finsert(
            finsert(
                finsert(depthOneRight, mEntry, allowDups = true),
                mEntry, allowDups = true
            ),
            mEntry, allowDups = true
        ) shouldBe
            FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry, FBSTNil, FBSTNode(mEntry, FBSTNil, depthOneRight)))

        // dups left
        finsert(depthOneRight, nEntry, allowDups = true) shouldBe
            FBSTNode(
                mEntry,
                FBSTNil,
                FBSTNode(nEntry, FBSTNil, FBSTNode(nEntry))
            )
        finsert(
            finsert(depthOneRight, nEntry, allowDups = true),
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
        finsert(
            finsert(
                finsert(depthOneRight, nEntry, allowDups = true),
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

        finsert(depthTwoLeftPartial,mEntry, allowDups = true) shouldBe depthTwoLeftRight
        finsert(depthTwoLeftPartial,eEntry, allowDups = true) shouldBe depthTwoLeftLeft
        finsert(depthTwoRightPartial,uEntry, allowDups = true) shouldBe depthTwoRightRight
        finsert(depthTwoRightPartial,rEntry, allowDups = true) shouldBe depthTwoRightLeft

        finsert(depthTwoLeftRight,nEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNil,
                    FBSTNode(mEntry)),
                FBSTNode(nEntry,
                    FBSTNil,
                    FBSTNode(sEntry)))
        finsert(depthTwoLeftRight,lEntry, allowDups = true) shouldBe
        FBSTNode(nEntry,
            FBSTNode(lEntry,
                FBSTNil,
                FBSTNode(lEntry,
                    FBSTNil,
                    FBSTNode(mEntry))),
            FBSTNode(sEntry))
        finsert(depthTwoLeftRight,mEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNil,
                    FBSTNode(mEntry,
                        FBSTNil,
                        FBSTNode(mEntry))),
                FBSTNode(sEntry))
        finsert(depthTwoLeftRight,sEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNil,
                    FBSTNode(mEntry)),
                FBSTNode(sEntry, FBSTNil, FBSTNode(sEntry)))

        finsert(depthTwoLeftLeft,nEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNode(eEntry),
                    FBSTNil),
                FBSTNode(nEntry,
                    FBSTNil,
                    FBSTNode(sEntry)))
        finsert(depthTwoLeftLeft,lEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNode(eEntry),
                    FBSTNode(lEntry)),
                FBSTNode(sEntry))
        finsert(depthTwoLeftLeft,eEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNode(eEntry,
                        FBSTNil,
                        FBSTNode(eEntry)),
                    FBSTNil),
                FBSTNode(sEntry))
        finsert(depthTwoLeftLeft,sEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(lEntry,
                    FBSTNode(eEntry),
                    FBSTNil),
                FBSTNode(sEntry, FBSTNil, FBSTNode(sEntry)))

        finsert(depthTwoRightRight,nEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(nEntry,
                    FBSTNil,
                    FBSTNode(sEntry,
                        FBSTNil,
                        FBSTNode(uEntry))))
        finsert(depthTwoRightRight,mEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry,
                    FBSTNil,
                    FBSTNode(mEntry)),
                FBSTNode(sEntry,
                    FBSTNil,
                    FBSTNode(uEntry)))
        finsert(depthTwoRightRight,sEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(sEntry,
                    FBSTNil,
                    FBSTNode(sEntry,
                        FBSTNil,
                        FBSTNode(uEntry))))
        finsert(depthTwoRightRight,uEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(sEntry,
                    FBSTNil,
                    FBSTNode(uEntry,
                        FBSTNil,
                        FBSTNode(uEntry))))

        finsert(depthTwoRightLeft,nEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(nEntry,
                    FBSTNil,
                    FBSTNode(sEntry,
                        FBSTNode(rEntry),
                        FBSTNil)))
        finsert(depthTwoRightLeft,mEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry,
                    FBSTNil,
                    FBSTNode(mEntry)),
                FBSTNode(sEntry,
                    FBSTNode(rEntry),
                    FBSTNil))
        finsert(depthTwoRightLeft,sEntry, allowDups = true) shouldBe
            FBSTNode(nEntry,
                FBSTNode(mEntry),
                FBSTNode(sEntry,
                    FBSTNode(rEntry),
                    FBSTNode(sEntry)))
        finsert(depthTwoRightLeft,rEntry, allowDups = true) shouldBe
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
            print("size "+n)
            print(", max depth "+bst.fmaxDepth())
            println          (", min depth "+bst.fminDepth())
            val aut = bst.inorder()
            values.sort()
            val testOracle = FList.of(values.iterator()).fmap { TKVEntry.ofIntKey(it) }
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
        addGraft(prune(depthTwoLeftRight, sEntry), ffind(depthTwoLeftRight,sEntry)) shouldBe depthTwoLeftRight
        addGraft(prune(depthTwoLeftRight, lEntry), ffind(depthTwoLeftRight,lEntry)) shouldBe depthTwoLeftRight

        addGraft(prune(depthTwoLeftLeft, eEntry), FBSTNode(eEntry)) shouldBe depthTwoLeftLeft
        addGraft(prune(depthTwoLeftLeft, sEntry), ffind(depthTwoLeftLeft,sEntry)) shouldBe depthTwoLeftLeft
        addGraft(prune(depthTwoLeftLeft, lEntry), ffind(depthTwoLeftLeft,lEntry)) shouldBe depthTwoLeftLeft

        addGraft(prune(depthTwoRightRight, uEntry), FBSTNode(uEntry)) shouldBe depthTwoRightRight
        addGraft(prune(depthTwoRightRight, sEntry), ffind(depthTwoRightRight,sEntry)) shouldBe depthTwoRightRight
        addGraft(prune(depthTwoRightRight, mEntry), ffind(depthTwoRightRight,mEntry)) shouldBe depthTwoRightRight

        addGraft(prune(depthTwoRightLeft, rEntry), FBSTNode(rEntry)) shouldBe depthTwoRightLeft
        addGraft(prune(depthTwoRightLeft, sEntry), ffind(depthTwoRightLeft,sEntry)) shouldBe depthTwoRightLeft
        addGraft(prune(depthTwoRightLeft, mEntry), ffind(depthTwoRightLeft,mEntry)) shouldBe depthTwoRightLeft
    }

    test("co.delete no dups") {

        tailrec fun <A: Comparable<A>, B: Any> goAll(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val deleted = fdelete(t, acc.head)) {
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
                    val deleted = fdelete(t, acc.head)
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
        fdelete(wikiTree, zEntry) shouldBe wikiTree
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
        fdelete(slideShareTree, TKVEntry.ofIntKey(100)) shouldBe slideShareTree
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
                    when (val deleted = fdelete(t, acc.head)) {
                        is FBSTNode -> deleted.inorder() shouldBe inorder.ffilterNot { it == acc.head }
                        is FBSTNil -> true shouldBe false
                    }
                    go(t, acc.tail, inorder)
                }
            }
        val aux1 = finsert(wikiTree, wikiTree.froot()!!, allowDups = true)
        go(aux1, wikiPreorder, aux1.inorder())
        val aux2 = finsert(
            finsert(wikiTree,
                wikiTree.froot()!!, allowDups = true),
            wikiTree.froot()!!, allowDups = true)
        go(aux2, wikiPreorder, aux2.inorder())
        val aux3 = finsert(wikiTree, wikiTree.fleftMost()!!, allowDups = true)
        go(aux3, wikiPreorder, aux3.inorder())
        val aux4 = finsert(wikiTree, wikiTree.frightMost()!!, allowDups = true)
        go(aux4, wikiPreorder, aux4.inorder())
        val aux5 = finsert(
            finsert(slideShareTree, slideShareTree.fleftMost()!!),
            slideShareTree.fleftMost()!!)
        go(aux5, slideShareBreadthFirst, aux5.inorder())
        val aux6 = finsert(
            finsert(slideShareTree, slideShareTree.frightMost()!!),
            slideShareTree.frightMost()!!)
        go(aux6, slideShareBreadthFirst, aux6.inorder())
    }

    test("co.delete with dups (single)") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val deleted = fdelete(t, acc.head, atMostOne = true)) {
                        is FBSTNode -> deleted.inorder() shouldBe inorder.fdropFirst { it == acc.head }
                        is FBSTNil -> true shouldBe false
                    }
                    go(t, acc.tail, inorder)
                }
            }
        val aux1 = finsert(wikiTree, wikiTree.froot()!!, allowDups = true)
        go(aux1, wikiPreorder, aux1.inorder())
        val aux2 = finsert(
            finsert(wikiTree,
                wikiTree.froot()!!, allowDups = true),
            wikiTree.froot()!!, allowDups = true)
        go(aux2, wikiPreorder, aux2.inorder())
        val aux3 = finsert(wikiTree, wikiTree.fleftMost()!!, allowDups = true)
        go(aux3, wikiPreorder, aux3.inorder())
        val aux4 = finsert(wikiTree, wikiTree.frightMost()!!, allowDups = true)
        go(aux4, wikiPreorder, aux4.inorder())
        val aux5 = finsert(
            finsert(slideShareTree, slideShareTree.fleftMost()!!),
            slideShareTree.fleftMost()!!)
        go(aux5, slideShareBreadthFirst, aux5.inorder())
        val aux6 = finsert(
            finsert(slideShareTree, slideShareTree.frightMost()!!),
            slideShareTree.frightMost()!!)
        go(aux6, slideShareBreadthFirst, aux6.inorder())
    }

    test("co.of(list)") {
        val foo = finserts(wikiTree, FList.of(fEntry, fEntry, fEntry), allowDups = true)
        print("$foo")
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
