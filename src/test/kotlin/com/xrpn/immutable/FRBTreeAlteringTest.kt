package com.xrpn.immutable

import com.xrpn.imapi.IMBTree
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.flist

class FRBTreeAlteringTest : FunSpec({

    val repeatsHigh = Pair(50, 100)
    val repeatsMid = Pair(25, 500)
    val repeatsLow = Pair(2, 40000)

    val verbose = false

    fun displayRbOnVerbose(rbTree: IMBTree<Int, Int>, n: Int, force: Boolean = false) {
        if (verbose || !FRBTree.rbRootSane(rbTree as FRBTree<Int, Int>) || force) {
            print("FF size " + n)
            print(", expected depth ${FRBTree.rbMaxDepth(n)}")
            print(", max depth " + rbTree.fmaxDepth())
            println(", min depth " + rbTree.fminDepth())
            // println("$rbTree")
        }
    }

    beforeTest {}

    test("finsert items sorted asc") {
        val aux0 = FRBTree.nul<Int,Int>().finsert( TKVEntry.ofIntKey(0))
        if (verbose) println("tree with 0                   $aux0")
        FRBTree.rbRootSane(aux0) shouldBe true
        val aux1 = aux0.finsert( TKVEntry.ofIntKey(1))
        if (verbose) println("tree with 0,1                 $aux1")
        FRBTree.rbRootSane(aux1) shouldBe true
        val aux2 = aux1.finsert( TKVEntry.ofIntKey(2))
        if (verbose) println("tree with 0,1,2               $aux2")
        FRBTree.rbRootSane(aux2) shouldBe true
        val aux3 = aux2.finsert( TKVEntry.ofIntKey(repeatsLow.first))
        if (verbose) println("tree with 0,1,2,repeatsLow.first             $aux3")
        FRBTree.rbRootSane(aux3) shouldBe true
        val aux4 = aux3.finsert( TKVEntry.ofIntKey(4))
        if (verbose) println("tree with 0,1,2,repeatsLow.first,4           $aux4")
        FRBTree.rbRootSane(aux4) shouldBe true
        val aux5 = aux4.finsert( TKVEntry.ofIntKey(5))
        if (verbose) println("tree with 0,1,2,repeatsLow.first,4,5         $aux5")
        FRBTree.rbRootSane(aux5) shouldBe true
        val aux6 = aux5.finsert( TKVEntry.ofIntKey(6))
        if (verbose) println("tree with 0,1,2,repeatsLow.first,4,5,6       $aux6")
        FRBTree.rbRootSane(aux6) shouldBe true
        val aux7 = aux6.finsert( TKVEntry.ofIntKey(7))
        if (verbose) println("tree with 0,1,2,repeatsLow.first,4,5,6,7     $aux7")
        FRBTree.rbRootSane(aux7) shouldBe true
        val aux8 = aux7.finsert( TKVEntry.ofIntKey(8))
        if (verbose) println("tree with 0,1,2,repeatsLow.first,4,5,6,7,8   $aux8")
        FRBTree.rbRootSane(aux8) shouldBe true
        val aux9 = aux8.finsert( TKVEntry.ofIntKey(9))
        if (verbose) println("tree with 0,1,2,repeatsLow.first,4,5,6,7,8,9 $aux9")
        FRBTree.rbRootSane(aux9) shouldBe true
    }

    test("finsert items sorted desc") {
        val aux0 = FRBTree.nul<Int,Int>().finsert( TKVEntry.ofIntKey(8))
        if (verbose) println("8: $aux0")
        FRBTree.rbRootSane(aux0) shouldBe true
        val aux1 = aux0.finsert( TKVEntry.ofIntKey(7))
        if (verbose) println("7: $aux1")
        FRBTree.rbRootSane(aux1) shouldBe true
        val aux2 = aux1.finsert( TKVEntry.ofIntKey(6))
        if (verbose) println("6: $aux2")
        FRBTree.rbRootSane(aux2) shouldBe true
        val aux3 = aux2.finsert( TKVEntry.ofIntKey(5))
        if (verbose) println("5: $aux3")
        FRBTree.rbRootSane(aux3) shouldBe true
        val aux4 = aux3.finsert( TKVEntry.ofIntKey(4))
        if (verbose) println("4: $aux4")
        FRBTree.rbRootSane(aux4) shouldBe true
        val aux5 = aux4.finsert( TKVEntry.ofIntKey(repeatsLow.first))
        if (verbose) println("repeatsLow.first: $aux5")
        FRBTree.rbRootSane(aux5) shouldBe true
        val aux6 = aux5.finsert( TKVEntry.ofIntKey(2))
        if (verbose) println("2: $aux6")
        FRBTree.rbRootSane(aux6) shouldBe true
        val aux7 = aux6.finsert( TKVEntry.ofIntKey(1))
        if (verbose) println("1: $aux7")
        FRBTree.rbRootSane(aux7) shouldBe true
        val aux8 = aux7.finsert( TKVEntry.ofIntKey(0))
        if (verbose) println("0: $aux8")
        FRBTree.rbRootSane(aux8) shouldBe true
    }

    test("finsert item") {
        FRBTree.nul<Int,String>().finsert(mEntry) shouldBe FRBTNode(mEntry, FRBTree.BLACK)
        FRBTNode(mEntry).finsert(lEntry) shouldBe frbDepthOneLeft
        frbDepthOneLeft.finsert(nEntry) shouldBe frbDepthOneFull
        ttDepthTwoRightPartial.finsert(rEntry) shouldBe frbDepthTwoRightLeft
    }

    test("finsertDup item") {
        FRBTree.nul<Int,String>().finsertDup(mEntry, allowDups = false) shouldBe FRBTNode(mEntry, FRBTree.BLACK)
        FRBTree.nul<Int,String>()
            .finsertDup(mEntry, allowDups = true)
            .finsertDup(mEntry, allowDups = true) shouldBe FRBTNode(mEntry, FRBTree.BLACK)
        FRBTNode(mEntry).finsertDup(lEntry, allowDups = false) shouldBe frbDepthOneLeft
        FRBTNode(mEntry)
            .finsertDup(lEntry, allowDups = true)
            .finsertDup(lEntry, allowDups = true) shouldBe frbDepthOneLeft
        frbDepthOneLeft.finsertDup(nEntry, allowDups = false) shouldBe frbDepthOneFull
        frbDepthOneLeft
            .finsertDup(nEntry, allowDups = true)
            .finsertDup(nEntry, allowDups = true) shouldBe frbDepthOneFull
        ttDepthTwoRightPartial.finsertDup(rEntry, allowDups = false) shouldBe frbDepthTwoRightLeft
        ttDepthTwoRightPartial
            .finsertDup(rEntry, allowDups = true)
            .finsertDup(rEntry, allowDups = true) shouldBe frbDepthTwoRightLeft
    }

    test("finserts, finsertsDup") {
        Arb.flist<Int, Int>(Arb.int(-25, 25)).checkAll(repeatsHigh.first, PropTestConfig(seed = -3400901283900794903)) { fl ->
            val flkv: FList<TKVEntry<Int, Int>> = fl.fmap { it.toIAEntry() }
            val sl: List<TKVEntry<Int, Int>> = flkv.copyToMutableList().toSet().sorted()
            FRBTree.nul<Int, Int>().finserts(flkv).inorder() shouldBe sl
            FRBTree.nul<Int, Int>().finsertsDups(flkv, allowDups = false).inorder() shouldBe sl
            FRBTree.nul<Int, Int>().finsertsDups(flkv, allowDups = true).inorder() shouldBe sl
        }
    }

    test("finsert item (property) compare sorted asc, small") {
        checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val frbTree = FRBTree.of(values.iterator())
            displayRbOnVerbose(frbTree, n)
            FRBTree.rbRootSane(frbTree) shouldBe true
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

    test("finsert item (property) compare sorted desc, small") {
        checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
            // println("n:$n")
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            values.reverse()
            val frbTree = FRBTree.of(values.iterator())
            displayRbOnVerbose(frbTree, n)
            FRBTree.rbRootSane(frbTree) shouldBe true
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

    test("finsert item (property) sorted asc, small") {
        checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val rbTree = FRBTree.of(values.iterator())
            displayRbOnVerbose(rbTree, n)
            FRBTree.rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(values.iterator())
            aut shouldBe testOracle
        }
    }

    test("finsert item (property) sorted desc, small") {
        checkAll(repeatsHigh.first, Arb.int(20..repeatsHigh.second)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            values.reverse()
            val rbTree = FRBTree.of(values.iterator())
            displayRbOnVerbose(rbTree, n)
            FRBTree.rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder(reverse = true)
            val testOracle = FList.of(values.iterator())
            aut shouldBe testOracle
        }
    }

    test("finsert item (property) sorted asc, large").config(enabled = true) {
        checkAll(repeatsLow.first, Arb.int(10000..repeatsLow.second)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val rbTree = FRBTree.of(values.iterator())
            displayRbOnVerbose(rbTree, n)
            FRBTree.rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(values.iterator())
            aut shouldBe testOracle
        }
    }

    test("finsert item (property) sorted desc, large").config(enabled = true) {
        checkAll(repeatsLow.first, Arb.int(10000..repeatsLow.second)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            values.reverse()
            val rbTree = FRBTree.of(values.iterator())
            displayRbOnVerbose(rbTree, n)
            FRBTree.rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder(reverse = true)
            val testOracle = FList.of(values.iterator())
            aut shouldBe testOracle
        }
    }

    test("finsert item (property) random, small") {
        checkAll(repeatsMid.first, Arb.int(10..repeatsMid.second)) { n ->
            val sorted = Array(n) { i: Int -> TKVEntry.of(i, i) }
            val shuffled = Array(n) { i: Int -> TKVEntry.of(i, i) }
            // shuffled.shuffle(Random(seed = 5792981224933522729))
            shuffled.shuffle()
            val rbTree = FRBTree.of(shuffled.iterator())
            displayRbOnVerbose(rbTree, n)
            FRBTree.rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(sorted.iterator())
            aut shouldBe testOracle
        }
    }

    test("finsert item (property) random reversed, small") {
        checkAll(repeatsMid.first, Arb.int(10..repeatsMid.second)) { n ->
            val sorted = Array(n) { i: Int -> TKVEntry.of(i, i) }
            val shuffled = Array(n) { i: Int -> TKVEntry.of(i, i) }
            // shuffled.shuffle(Random(seed = 5792981224933522729))
            shuffled.shuffle()
            shuffled.reverse()
            val rbTree = FRBTree.of(shuffled.iterator())
            displayRbOnVerbose(rbTree, n)
            FRBTree.rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(sorted.iterator())
            aut shouldBe testOracle
        }
    }

    test("finsert item (property) random, large").config(enabled = true) {
        checkAll(repeatsLow.first, Arb.int(10000..repeatsLow.second)) { n ->
            val sorted = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val shuffled = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            shuffled.shuffle()
            val rbTree = FRBTree.of(shuffled.iterator())
            displayRbOnVerbose(rbTree, n)
            FRBTree.rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(sorted.iterator())
            aut shouldBe testOracle
        }
    }

    test("finsert item (property) random reversed, large").config(enabled = true) {
        checkAll(repeatsLow.first, Arb.int(10000..repeatsLow.second)) { n ->
            val sorted = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val shuffled = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            shuffled.shuffle()
            shuffled.reverse()
            val rbTree = FRBTree.of(shuffled.iterator())
            displayRbOnVerbose(rbTree, n)
            FRBTree.rbRootSane(rbTree) shouldBe true
            rbTree.size shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(sorted.iterator())
            aut shouldBe testOracle
        }
    }
})