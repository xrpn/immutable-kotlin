package com.xrpn.immutable

import com.xrpn.imapi.IMBTree
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fset
import java.util.concurrent.atomic.AtomicInteger

private val intSetOfNone = FSet.of(*arrayOf<Int>())
private val intSetOfOne = FSet.of(1)
private val intSetOfTwo = FSet.of(1, 2)
private val intSetOfTwoOfst1 = FSet.of(2, 3)
private val intSetOfThree = FSet.of(1, 2, 3)

class FSetUtilityTest : FunSpec({

    val repeats = 50

    beforeTest {}

    test("equal") {
        intSetOfNone.equal(intSetOfNone) shouldBe true
        intSetOfNone.equal(intSetOfOne) shouldBe false
        intSetOfOne.equal(intSetOfNone) shouldBe false
        intSetOfOne.equal(intSetOfOne) shouldBe true
        intSetOfOne.equal(FSet.of(1)) shouldBe true
        FSet.of(1).equal(intSetOfOne) shouldBe true
        intSetOfOne.equal(intSetOfTwo) shouldBe false
        intSetOfTwo.equal(intSetOfOne) shouldBe false
        intSetOfTwo.equal(intSetOfTwo) shouldBe true
        intSetOfTwo.equal(FSet.of(1,2)) shouldBe true
        FSet.of(1,2).equal(intSetOfTwo) shouldBe true
        intSetOfTwo.equal(intSetOfTwoOfst1) shouldBe false
        intSetOfTwoOfst1.equal(intSetOfTwo) shouldBe false
        intSetOfTwo.equal(intSetOfThree) shouldBe false
        intSetOfTwoOfst1.equal(intSetOfThree) shouldBe false
        intSetOfThree.equal(intSetOfTwo) shouldBe false
        intSetOfThree.equal(intSetOfTwoOfst1) shouldBe false
        intSetOfThree.equal(intSetOfThree) shouldBe true
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
            val fs1 = FSet.of(fbst)
            fs.equals(fs1) shouldBe true
        }
    }

    test("copy") {
        intSetOfNone.copy().equals(intSetOfNone) shouldBe true
        checkAll(repeats, Arb.fset<Int, Int>(Arb.int(),20..100)) { fs ->
            val fs1 = fs.copy()
            (fs1 === fs) shouldBe false
            fs.equal(fs1) shouldBe true
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
