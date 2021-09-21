package com.xrpn.immutable

import com.xrpn.imapi.IMBTree
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fset
import java.util.concurrent.atomic.AtomicInteger

private val intSetOfNone = FKSet.ofi(*arrayOf<Int>())
private val intSetOfOne = FKSet.ofi(1)
private val intSetOfTwo = FKSet.ofi(1, 2)
private val intSetOfTwoOfst1 = FKSet.ofi(2, 3)
private val intSetOfThree = FKSet.ofi(1, 2, 3)

class FIKSetUtilityTest : FunSpec({

    val repeats = 50

    beforeTest {}

    test("equal") { TODO() }

    test("strongEqual") {
        intSetOfNone.strongEqual(intSetOfNone) shouldBe true
        intSetOfNone.strongEqual(intSetOfOne) shouldBe false
        intSetOfOne.strongEqual(intSetOfNone) shouldBe false
        intSetOfOne.strongEqual(intSetOfOne) shouldBe true
        intSetOfOne.strongEqual(FKSet.ofi(1)) shouldBe true
        FKSet.ofi(1).strongEqual(intSetOfOne) shouldBe true
        intSetOfOne.strongEqual(intSetOfTwo) shouldBe false
        intSetOfTwo.strongEqual(intSetOfOne) shouldBe false
        intSetOfTwo.strongEqual(intSetOfTwo) shouldBe true
        intSetOfTwo.strongEqual(FKSet.ofi(1,2)) shouldBe true
        FKSet.ofi(1,2).strongEqual(intSetOfTwo) shouldBe true
        intSetOfTwo.strongEqual(intSetOfTwoOfst1) shouldBe false
        intSetOfTwoOfst1.strongEqual(intSetOfTwo) shouldBe false
        intSetOfTwo.strongEqual(intSetOfThree) shouldBe false
        intSetOfTwoOfst1.strongEqual(intSetOfThree) shouldBe false
        intSetOfThree.strongEqual(intSetOfTwo) shouldBe false
        intSetOfThree.strongEqual(intSetOfTwoOfst1) shouldBe false
        intSetOfThree.strongEqual(intSetOfThree) shouldBe true
    }

    test("fforEach") {
        val counter = AtomicInteger(0)
        val summer = AtomicInteger(0)
        val doCount: (Int) -> Unit = { counter.incrementAndGet() }
        val doSum: (Int) -> Unit = { v -> summer.addAndGet(v) }
        intSetOfNone.fforEach(doCount)
        counter.get() shouldBe 0
        intSetOfNone.fforEach(doSum)
        summer.get() shouldBe 0
        counter.set(0)
        summer.set(0)
        checkAll(repeats, Arb.fset<Int, Int>(Arb.int(),20..100)) { fs ->
            val oraSum = fs.ffold(0){ acc, el -> acc + el }
            fs.fforEach(doCount)
            counter.get() shouldBe fs.size
            counter.set(0)
            fs.fforEach(doSum)
            summer.get() shouldBe oraSum
            summer.set(0)
        }
    }

    test("toIMBTree") {
        intSetOfNone.toIMBTree() shouldBe FRBTree.emptyIMBTree()
        checkAll(repeats, Arb.fset<Int, Int>(Arb.int(),20..100)) { fs ->
            val frbt: IMBTree<Int, Int> = fs.toIMBTree()
            val fbst = FBSTree.of(frbt.breadthFirst())
            val fs1 = FKSet.ofi(fbst)
            fs.equals(fs1) shouldBe true
        }
    }

    test("copy") {
        intSetOfNone.copy().equals(intSetOfNone) shouldBe true
        checkAll(repeats, Arb.fset<Int, Int>(Arb.int(),20..100)) { fs ->
            val fs1 = fs.copy()
            (fs1 === fs) shouldBe false
            fs.strongEqual(fs1) shouldBe true
        }
    }

    test("copyToMutableSet") {
        intSetOfNone.copyToMutableSet() shouldBe mutableSetOf()
        checkAll(repeats, Arb.fset<Int, Int>(Arb.int(),20..100)) { fs ->
            val ms: MutableSet<Int> = fs.copyToMutableSet()
            (fs == ms) shouldBe true
            (ms == fs) shouldBe true
        }
    }
})
