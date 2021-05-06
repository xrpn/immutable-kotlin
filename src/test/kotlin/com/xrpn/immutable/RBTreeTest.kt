package com.xrpn.immutable

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.property.arbitrary.int

private const val verbose = false

class RBTreeTest : FunSpec({

    beforeTest {
    }

//    afterTest { (testCase, result) ->
//    }

    test("co.insert items sorted asc") {
        var rbt = RBTree<Int, Int>()
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(0))
        println("tree with 0                   $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(1))
        println("tree with 0,1                 $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(2))
        println("tree with 0,1,2               $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(3))
        println("tree with 0,1,2,3             $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(4))
        println("tree with 0,1,2,3,4           $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(5))
        println("tree with 0,1,2,3,4,5         $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(6))
        println("tree with 0,1,2,3,4,5,6       $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(7))
        println("tree with 0,1,2,3,4,5,6,7     $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(8))
        println("tree with 0,1,2,3,4,5,6,7,8   $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(9))
        println("tree with 0,1,2,3,4,5,6,7,8,9 $rbt")
        rbt.rbSane() shouldBe true
    }

    test("co.insert items sorted desc") {
        var rbt = RBTree<Int, Int>()
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(8))
        println("8: $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(7))
        println("7: $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(6))
        println("6: $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(5))
        println("5: $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(4))
        println("4: $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(3))
        println("3: $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(2))
        println("2: $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(1))
        println("1: $rbt")
        rbt.rbSane() shouldBe true
        rbt.insert(TKVEntry.ofIntKey(0))
        println("0: $rbt")
        rbt.rbSane() shouldBe true
    }

    test("co.insert item (property) sorted asc, small") {
        // checkAll(PropTestConfig(iterations = 1, seed = 1882817875667961235), Arb.int(20..100)) { n ->
        checkAll(50, Arb.int(20..100)) { n ->
            // val n = 9
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val rbTree = RBTree.of(values.iterator())
            if (verbose || !rbTree.rbSane()) {
                print("size " + n)
                print(" (sorted), expected depth ${FRBTree.rbMaxDepth(n)}")
                print(", max depth " + rbTree.maxDepth())
                println(", min depth " + rbTree.minDepth())
                print("$rbTree")
            }
            rbTree.rbSane() shouldBe true
            rbTree.size() shouldBe n
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
            val rbTree = RBTree.of(values.iterator())
            displayRbtOnVerbose(rbTree, n)
            rbTree.rbSane() shouldBe true
            rbTree.size() shouldBe n
            val aut = rbTree.inorder(reverse = true)
            val testOracle = FList.of(values.iterator())
            aut shouldBe testOracle
        }
    }

    test("co.insert item (property) sorted asc, large") {
        checkAll(2, Arb.int(10000..100000)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val rbTree = RBTree.of(values.iterator())
            displayRbtOnVerbose(rbTree, n)
            rbTree.rbSane() shouldBe true
            rbTree.size() shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(values.iterator())
            aut shouldBe testOracle
        }
    }

    test("co.insert item (property) sorted desc, large") {
        checkAll(2, Arb.int(10000..100000)) { n ->
            val values = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            values.reverse()
            val rbTree = RBTree.of(values.iterator())
            displayRbtOnVerbose(rbTree, n)
            rbTree.rbSane() shouldBe true
            rbTree.size() shouldBe n
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
            val rbTree = RBTree.of(shuffled.iterator())
            displayRbtOnVerbose(rbTree, n)
            rbTree.rbSane() shouldBe true
            rbTree.size() shouldBe n
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
            val rbTree = RBTree.of(shuffled.iterator())
            displayRbtOnVerbose(rbTree, n)
            rbTree.rbSane() shouldBe true
            rbTree.size() shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(sorted.iterator())
            aut shouldBe testOracle
        }
    }

    test("co.insert item (property) random, large").config(enabled = true) {
        checkAll(5, Arb.int(10000..100000)) { n ->
            val sorted = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val shuffled = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            shuffled.shuffle()
            val rbTree = RBTree.of(shuffled.iterator())
            displayRbtOnVerbose(rbTree, n)
            rbTree.rbSane() shouldBe true
            rbTree.size() shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(sorted.iterator())
            aut shouldBe testOracle
        }
    }

    test("co.insert item (property) random reversed, large").config(enabled = true) {
        checkAll(5, Arb.int(10000..100000)) { n ->
            val sorted = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            val shuffled = (Array(n) { i: Int -> TKVEntry.of(i, i) })
            shuffled.shuffle()
            shuffled.reverse()
            val rbTree = RBTree.of(shuffled.iterator())
            displayRbtOnVerbose(rbTree, n)
            rbTree.rbSane() shouldBe true
            rbTree.size() shouldBe n
            val aut = rbTree.inorder()
            val testOracle = FList.of(sorted.iterator())
            aut shouldBe testOracle
        }
    }

})

internal fun displayRbtOnVerbose(rbTree: RBTree<Int, Int>, n: Int) {
    if (verbose || !rbTree.rbSane()) {
        print("NF size " + n)
        print(", expected depth ${FRBTree.rbMaxDepth(n)}")
        print(", max depth " + rbTree.maxDepth())
        println(", min depth " + rbTree.minDepth())
        // println("$rbTree")
    }
}
