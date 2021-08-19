package com.xrpn.immutable

import com.xrpn.immutable.FBSTree.Companion.of
import com.xrpn.immutable.FBSTree.Companion.ofvi
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import kotlin.random.Random.Default.nextInt

class FBSTreeFilteringTest : FunSpec({

    beforeTest {}

    fun <A, B: Any> isChildMatchOracle(node: FBSTNode<A, B>, match: TKVEntry<A, B>): Pair<Boolean, Boolean> where A: Any, A: Comparable<A> {
        val leftChildMatch = (node.bLeft is FBSTNode) && node.bLeft.entry == match
        val rightChildMatch = (node.bRight is FBSTNode) && node.bRight.entry == match
        return Pair(leftChildMatch, rightChildMatch)
    }

    test("fempty") {
        FBSTNil.fempty() shouldBe true
        ofvi(1).fempty() shouldBe false
    }

    test("fcontains") {
        FBSTree.nul<Int, String>().fcontains(zEntry) shouldBe false
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    val p = t.fcontains(acc.head)
                    p shouldBe true
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        wikiTree.fcontains(zEntry) shouldBe false
        go(slideShareTree, slideShareBreadthFirst)
        slideShareTree.fcontains(TKVEntry.ofIntKey(100)) shouldBe false
    }

    test("fcontainsKey") {
        FBSTree.nul<Int, String>().fcontainsKey(zEntry.getk()) shouldBe false
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    val p = t.fcontainsKey(acc.head.getk())
                    p shouldBe true
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        wikiTree.fcontainsKey(zEntry.getk()) shouldBe false
        go(slideShareTree, slideShareBreadthFirst)
        slideShareTree.fcontainsKey(100) shouldBe false
    }

    // TODO dropItem null

    test("dropItem no dups") {

        tailrec fun <A: Comparable<A>, B: Any> goAll(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val deleted = t.fdropItem(acc.head)) {
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
                    val deleted = t.fdropItem(acc.head)
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
        wikiTree.fdropItem(zEntry) shouldBe wikiTree
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
        slideShareTree.fdropItem(TKVEntry.ofIntKey(100)) shouldBe slideShareTree
        goTele(slideShareTree, slideSharePreorder, slideShareInorder)
        goTele(slideShareTree, slideShareInorder, slideShareInorder)
        goTele(slideShareTree, slideSharePostorder, slideShareInorder)
        goTele(slideShareTree, slideShareBreadthFirst, slideShareInorder)
        goTele(slideShareTree, slideSharePreorder.freverse(), slideShareInorder)
        goTele(slideShareTree, slideShareInorder.freverse(), slideShareInorder)
        goTele(slideShareTree, slideSharePostorder.freverse(), slideShareInorder)
        goTele(slideShareTree, slideShareBreadthFirst.freverse(), slideShareInorder)
    }

    // TODO dropItemAll null

    test("ffdropItemAll") {
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>, inorder: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val deleted = t.fdropItemAll(acc.head)) {
                        is FBSTNode -> deleted.inorder() shouldBe inorder.ffilterNot { it == acc.head }
                        is FBSTNil -> true shouldBe false
                    }
                    go(t, acc.tail, inorder)
                }
            }
        val aux1 = wikiTree.finsertDup(wikiTree.froot()!!, allowDups = true)
        go(aux1, wikiPreorder, aux1.inorder())
        val aux2 = wikiTree.finsertDup(wikiTree.froot()!!, allowDups = true)
            .finsertDup(wikiTree.froot()!!, allowDups = true)
        go(aux2, wikiPreorder, aux2.inorder())
        val aux3 = wikiTree.finsertDup(wikiTree.fleftMost()!!, allowDups = true)
        go(aux3, wikiPreorder, aux3.inorder())
        val aux4 = wikiTree.finsertDup(wikiTree.frightMost()!!, allowDups = true)
        go(aux4, wikiPreorder, aux4.inorder())
        val aux5 = slideShareTree.finsert(slideShareTree.fleftMost()!!)
            .finsert(slideShareTree.fleftMost()!!)
        go(aux5, slideShareBreadthFirst, aux5.inorder())
        val aux6 = slideShareTree.finsert(slideShareTree.frightMost()!!)
            .finsert(slideShareTree.frightMost()!!)
        go(aux6, slideShareBreadthFirst, aux6.inorder())
    }


    // TODO ffilter, filterNot, ffind on nil

    test("ffilter, filterNot, ffind (A)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(50, Arb.int(20..100)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.of(i, i) }
            val svalues = values + values
            val ora1 = values.size
            svalues.size shouldBe (ora1 * 2)
            val tree: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree.size shouldBe (ora1 * 2)

            val sAll1: FBSTree<Int, Int> = tree.ffilter(pickIfLess(ora1))
            val snAll1: FBSTree<Int, Int> = tree.ffilterNot(pickIfLess(ora1))
            val saAll1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora1))
            sAll1.size shouldBe ora1
            snAll1.size shouldBe 0
            saAll1.size shouldBe ora1 * 2
            val sEmpty1: FBSTree<Int, Int> = tree.ffilter(pickIfMore(ora1))
            val snEmpty1: FBSTree<Int, Int> = tree.ffilterNot(pickIfMore(ora1))
            val saEmpty1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora1))
            sEmpty1.size shouldBe 0
            snEmpty1.size shouldBe ora1
            saEmpty1.size shouldBe 0

            val ora2 = ora1 / 2
            val theRestSansOra2 = (ora1 - ora2) - 1

            val sAll2: FBSTree<Int, Int> = tree.ffilter(pickIfLess(ora2))
            val snAll2: FBSTree<Int, Int> = tree.ffilterNot(pickIfLess(ora2))
            val saAll2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora2))
            sAll2.size shouldBe ora2
            snAll2.size shouldBe theRestSansOra2 + 1
            saAll2.size shouldBe ora2 * 2
            val sEmpty2: FBSTree<Int, Int> = tree.ffilter(pickIfMore(ora2))
            val snEmpty2: FBSTree<Int, Int> = tree.ffilterNot(pickIfMore(ora2))
            val saEmpty2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora2))
            sEmpty2.size shouldBe theRestSansOra2
            snEmpty2.size shouldBe ora1 - theRestSansOra2
            saEmpty2.size shouldBe (theRestSansOra2 * 2)
        }
    }

    test("ffilter, ffind (B)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(50, Arb.int(20..100)) { n ->
            val shuffled = Array(n) { i: Int -> TKVEntry.of(i, i) }
            shuffled.shuffle()
            val svalues = shuffled + shuffled
            val ora1 = shuffled.size
            svalues.size shouldBe (ora1 * 2)
            val tree: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree.size shouldBe (ora1 * 2)

            val sAll1: FBSTree<Int, Int> = tree.ffilter(pickIfLess(ora1))
            val saAll1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora1))
            sAll1.size shouldBe ora1
            saAll1.size shouldBe ora1 * 2
            val sEmpty1: FBSTree<Int, Int> = tree.ffilter(pickIfMore(ora1))
            val saEmpty1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora1))
            sEmpty1.size shouldBe 0
            saEmpty1.size shouldBe 0

            val ora2 = ora1 / 2
            val theRestSansOra2 = (ora1 - ora2) - 1

            val sAll2: FBSTree<Int, Int> = tree.ffilter(pickIfLess(ora2))
            val saAll2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora2))
            sAll2.size shouldBe ora2
            saAll2.size shouldBe ora2 * 2
            val sEmpty2: FBSTree<Int, Int> = tree.ffilter(pickIfMore(ora2))
            val saEmpty2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora2))
            saEmpty2.size shouldBe (theRestSansOra2 * 2)
            sEmpty2.size shouldBe theRestSansOra2
        }
    }

    test("ffilter, ffind (C)") {
        fun pickIfLess(n: Int): (TKVEntry<Int, Int>) -> Boolean = { it.getv() < n }
        fun pickIfMore(n: Int): (TKVEntry<Int, Int>) -> Boolean = { n < it.getv() }
        checkAll(50, Arb.int(20..100)) { n ->
            val reversed = Array(n) { i: Int -> TKVEntry.of(i, i) }
            reversed.reverse()
            val svalues = reversed + reversed
            val ora1 = reversed.size
            svalues.size shouldBe (ora1 * 2)
            val tree: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree.size shouldBe (ora1 * 2)

            val sAll1: FBSTree<Int, Int> = tree.ffilter(pickIfLess(ora1))
            val saAll1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora1))
            sAll1.size shouldBe ora1
            saAll1.size shouldBe ora1 * 2
            val sEmpty1: FBSTree<Int, Int> = tree.ffilter(pickIfMore(ora1))
            val saEmpty1: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora1))
            sEmpty1.size shouldBe 0
            saEmpty1.size shouldBe 0

            val ora2 = ora1 / 2
            val theRestSansOra2 = (ora1 - ora2) - 1

            val sAll2: FBSTree<Int, Int> = tree.ffilter(pickIfLess(ora2))
            val saAll2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfLess(ora2))
            sAll2.size shouldBe ora2
            saAll2.size shouldBe ora2 * 2
            val sEmpty2: FBSTree<Int, Int> = tree.ffilter(pickIfMore(ora2))
            val saEmpty2: FList<TKVEntry<Int, Int>> = tree.ffind(pickIfMore(ora2))
            saEmpty2.size shouldBe (theRestSansOra2 * 2)
            sEmpty2.size shouldBe theRestSansOra2
        }
    }

    // TODO ffind, ffindDistinct on nil

    test("ffind, ffindDistinct (A)") {
        // checkAll(PropTestConfig(seed = 5699135300091264211), Arb.int(20..100)) { n ->
        checkAll(50, Arb.int(20..100)) { n ->
            val values = Array(n) { i: Int -> TKVEntry.of(i, i) }
            val svalues = values + values
            val ora1 = values.size
            svalues.size shouldBe (ora1 * 2)
            val tree2: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree2.size shouldBe (ora1 * 2)
            val tree1: FBSTree<Int, Int> = of(svalues.iterator())
            tree1.size shouldBe ora1

            tree1.ffind { true } shouldBe tree1.preorder(reverse = true)

            val ora2 = ora1 / 2
            tree2.ffindDistinct { it.getv() == ora1 } shouldBe null
            tree2.ffindDistinct { it.getv() == ora2 } shouldBe null
            tree1.ffindDistinct { it.getv() == ora2 }?.getv() shouldBe ora2
        }
    }

    test("ffind, ffindDistinct (B)") {
        checkAll(50, Arb.int(20..100)) { n ->
            val shuffled = Array(n) { i: Int -> TKVEntry.of(i, i) }
            shuffled.shuffle()
            val svalues = shuffled + shuffled
            val ora1 = shuffled.size
            svalues.size shouldBe (ora1 * 2)
            val tree2: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree2.size shouldBe (ora1 * 2)
            val tree1: FBSTree<Int, Int> = of(svalues.iterator())
            tree1.size shouldBe ora1

            tree1.ffind { true } shouldBe tree1.preorder(reverse = true)

            val ora2 = ora1 / 2
            tree2.ffindDistinct { it.getv() == ora1 } shouldBe null
            tree2.ffindDistinct { it.getv() == ora2 } shouldBe null
            tree1.ffindDistinct { it.getv() == ora2 }?.getv() shouldBe ora2
        }
    }

    test("ffind, ffindDistinct (C)") {
        // TODO find on nil
        checkAll(50, Arb.int(20..100)) { n ->
            val reversed = Array(n) { i: Int -> TKVEntry.of(i, i) }
            reversed.reverse()
            val svalues = reversed + reversed
            val ora1 = reversed.size
            svalues.size shouldBe (ora1 * 2)
            val tree2: FBSTree<Int, Int> = of(svalues.iterator(), allowDups = true)
            tree2.size shouldBe (ora1 * 2)
            val tree1: FBSTree<Int, Int> = of(svalues.iterator())
            tree1.size shouldBe ora1

            tree1.ffind { true } shouldBe tree1.preorder(reverse = true)

            val ora2 = ora1 / 2
            tree2.ffindDistinct { it.getv() == ora1 } shouldBe null
            tree2.ffindDistinct { it.getv() == ora2 } shouldBe null
            tree1.ffindDistinct { it.getv() == ora2 }?.getv() shouldBe ora2
        }
    }

    test("ffindItem") {
        // TODO find on nil
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = t.ffindItem(acc.head)) {
                        is FBSTNode -> found.entry shouldBe acc.head
                        else -> fail("not found: ${acc.head}")
                    }
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        wikiTree.ffindItem(zEntry) shouldBe null
        go(slideShareTree, slideShareBreadthFirst)
        slideShareTree.ffindItem(TKVEntry.ofIntKey(100)) shouldBe null
    }

    test("ffindLastItem no dups") {
        // TODO find on nil
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = t.ffindLastItem(acc.head)) {
                        is FBSTNode -> {
                            found.entry shouldBe acc.head
                            isChildMatchOracle(found, acc.head) shouldBe Pair(false, false)
                        }
                        else -> fail("not found: ${acc.head}")
                    }
                    go(t, acc.tail)
                }
            }
        go(wikiTree, wikiPreorder)
        wikiTree.ffindLastItem(zEntry) shouldBe null
        go(slideShareTree, slideShareBreadthFirst)
        slideShareTree.ffindLastItem(TKVEntry.ofIntKey(100)) shouldBe null
    }

    test("ffindLastItem with dups") {
        // TODO find on nil
        tailrec fun <A: Comparable<A>, B: Any> go(t: FBSTree<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> =
            when (acc) {
                is FLNil -> FLNil
                is FLCons -> {
                    when (val found = t.ffindLastItem(acc.head)) {
                        is FBSTNode -> {
                            found.entry shouldBe acc.head
                            isChildMatchOracle(found, acc.head) shouldBe Pair(false, false)
                        }
                        else -> fail("not found: ${acc.head}")
                    }
                    go(t, acc.tail)
                }
            }
        go(wikiTree.finsertDup(wikiTree.froot()!!, allowDups = true), wikiPreorder)
        go(wikiTree.finsertDup(wikiTree.froot()!!, allowDups = true)
            .finsertDup(wikiTree.froot()!!, allowDups = true), wikiPreorder)
        go(wikiTree.finsertDup(wikiTree.fleftMost()!!, allowDups = true), wikiPreorder)
        go(wikiTree.finsertDup(wikiTree.frightMost()!!, allowDups = true), wikiPreorder)
        go(slideShareTree.finsert(slideShareTree.fleftMost()!!)
            .finsert(slideShareTree.fleftMost()!!), slideShareBreadthFirst)
        go(slideShareTree.finsert(slideShareTree.frightMost()!!)
            .finsert(slideShareTree.frightMost()!!), slideShareBreadthFirst)
    }

    test("fparentOf") {
        FBSTree.nul<Int, String>().fparentOf(TKVEntry.ofIntKey("")) shouldBe FBSTNil
        FBSTNode(mEntry).fparentOf(mEntry) shouldBe FBSTNil

        depthOneLeft.fparentOf(lEntry) shouldBe depthOneLeft
        depthOneRight.fparentOf(nEntry) shouldBe depthOneRight
        depthOneFull.fparentOf(lEntry) shouldBe depthOneFull
        depthOneFull.fparentOf(nEntry) shouldBe depthOneFull

        (depthTwoLeftRight.fparentOf(mEntry) as FBSTNode).entry shouldBe lEntry
        (depthTwoLeftRight.fparentOf(lEntry) as FBSTNode).entry shouldBe nEntry
        (depthTwoLeftRight.fparentOf(sEntry) as FBSTNode).entry shouldBe nEntry
        (depthTwoLeftLeft.fparentOf(eEntry) as FBSTNode).entry shouldBe lEntry
        (depthTwoLeftLeft.fparentOf(lEntry) as FBSTNode).entry shouldBe nEntry
        (depthTwoLeftLeft.fparentOf(sEntry) as FBSTNode).entry shouldBe nEntry
        (depthTwoRightRight.fparentOf(uEntry) as FBSTNode).entry shouldBe sEntry
        (depthTwoRightRight.fparentOf(sEntry) as FBSTNode).entry shouldBe nEntry
        (depthTwoRightRight.fparentOf(mEntry) as FBSTNode).entry shouldBe nEntry
        (depthTwoRightLeft.fparentOf(rEntry) as FBSTNode).entry shouldBe sEntry
        (depthTwoRightLeft.fparentOf(sEntry) as FBSTNode).entry shouldBe nEntry
        (depthTwoRightLeft.fparentOf(mEntry) as FBSTNode).entry shouldBe nEntry

        wikiTree.fparentOf(fEntry)  /* parent of root */ shouldBe FBSTNil
        (wikiTree.fparentOf(cEntry) as FBSTNode).entry shouldBe dEntry
        (wikiTree.fparentOf(hEntry) as FBSTNode).entry shouldBe iEntry
        wikiTree.fparentOf(zEntry) /* parent of missing value */ shouldBe FBSTNil

        (slideShareTree.fparentOf(n32Entry) as FBSTNode).entry shouldBe n17Entry
        (slideShareTree.fparentOf(n50Entry) as FBSTNode).entry shouldBe n78Entry
    }

    test("fleftMost") {
        FBSTNil.fleftMost() shouldBe null
    }

    test("fpick") {
        FBSTNil.fpick() shouldBe null
    }

    test("frightMost") {
        FBSTNil.frightMost() shouldBe null
    }

    test("fpick int") {
        for (size in IntRange(0, 20)) {
            val ary = IntArray(size) {nextInt()}
            val min = ary.minOrNull()
            of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).fpick() shouldBe min?.let { TKVEntry.ofIntKey(min) }
        }
    }

    test("fleftMost frightMost int") {
        for (size in IntRange(1, 20)) {
            val ary = IntArray(size) {nextInt()}
            val max = ary.maxOrNull()!!
            val min = ary.minOrNull()!!
            of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).fleftMost() shouldBe TKVEntry.ofIntKey(min)
            of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).frightMost() shouldBe TKVEntry.ofIntKey(max)
        }
    }

    test("froot") {
        FBSTNil.froot() shouldBe null
        for (size in IntRange(1, 20)) {
            val ary = IntArray(size) {nextInt()}
            of(FList.of(ary.iterator()).fmap { TKVEntry.ofIntKey(it) }).froot() shouldBe TKVEntry.ofIntKey(ary[0])
        }
    }
})
