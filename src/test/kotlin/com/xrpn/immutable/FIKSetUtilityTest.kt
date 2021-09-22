package com.xrpn.immutable

import com.xrpn.imapi.IMBTree
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.fset
import java.util.concurrent.atomic.AtomicInteger

private val intISetOfNone = FKSet.ofi(*arrayOf<Int>())
private val intISetOfOne = FKSet.ofi(1)
private val intISetOfTwo = FKSet.ofi(1, 2)
private val intISetOfTwoOfst1 = FKSet.ofi(2, 3)
private val intISetOfThree = FKSet.ofi(1, 2, 3)

private val intSSetOfNone = FKSet.ofs(*arrayOf<Int>())
private val intSSetOfOne = FKSet.ofs(1)
private val intSSetOfTwo = FKSet.ofs(1, 2)
private val intSSetOfTwoOfst1 = FKSet.ofs(2, 3)
private val intSSetOfThree = FKSet.ofs(1, 2, 3)

class FIKSetUtilityTest : FunSpec({

    val repeats = 50

    beforeTest {}

    test("equal same") {
        intISetOfNone.equal(intISetOfNone) shouldBe true
        intISetOfNone.equal(intISetOfOne) shouldBe false
        intISetOfOne.equal(intISetOfNone) shouldBe false
        intISetOfOne.equal(intISetOfOne) shouldBe true
        intISetOfOne.equal(FKSet.ofi(1)) shouldBe true
        FKSet.ofi(1).equal(intISetOfOne) shouldBe true
        intISetOfOne.equal(intISetOfTwo) shouldBe false
        intISetOfTwo.equal(intISetOfOne) shouldBe false
        intISetOfTwo.equal(intISetOfTwo) shouldBe true
        intISetOfTwo.equal(FKSet.ofi(1,2)) shouldBe true
        FKSet.ofi(1,2).equal(intISetOfTwo) shouldBe true
        intISetOfTwo.equal(intISetOfTwoOfst1) shouldBe false
        intISetOfTwoOfst1.equal(intISetOfTwo) shouldBe false
        intISetOfTwo.equal(intISetOfThree) shouldBe false
        intISetOfTwoOfst1.equal(intISetOfThree) shouldBe false
        intISetOfThree.equal(intISetOfTwo) shouldBe false
        intISetOfThree.equal(intISetOfTwoOfst1) shouldBe false
        intISetOfThree.equal(intISetOfThree) shouldBe true
    }

    test("equal not same") {
        intSSetOfNone.equal(intISetOfNone) shouldBe true
        intSSetOfNone.equal(intISetOfOne) shouldBe false
        intSSetOfOne.equal(intISetOfNone) shouldBe false
        intSSetOfOne.equal(intISetOfOne) shouldBe true
        intSSetOfOne.equal(FKSet.ofi(1)) shouldBe true
        FKSet.ofs(1).equal(intISetOfOne) shouldBe true
        intSSetOfOne.equal(intISetOfTwo) shouldBe false
        intSSetOfTwo.equal(intISetOfOne) shouldBe false
        intSSetOfTwo.equal(intISetOfTwo) shouldBe true
        intSSetOfTwo.equal(FKSet.ofi(1,2)) shouldBe true
        FKSet.ofs(1,2).equal(intISetOfTwo) shouldBe true
        intSSetOfTwo.equal(intISetOfTwoOfst1) shouldBe false
        intSSetOfTwoOfst1.equal(intISetOfTwo) shouldBe false
        intSSetOfTwo.equal(intISetOfThree) shouldBe false
        intSSetOfTwoOfst1.equal(intISetOfThree) shouldBe false
        intSSetOfThree.equal(intISetOfTwo) shouldBe false
        intSSetOfThree.equal(intISetOfTwoOfst1) shouldBe false
        intSSetOfThree.equal(intISetOfThree) shouldBe true
    }

    test("strongEqual") {
        intISetOfNone.strongEqual(intISetOfNone) shouldBe true
        intISetOfNone.strongEqual(intISetOfOne) shouldBe false
        intISetOfOne.strongEqual(intISetOfNone) shouldBe false
        intISetOfOne.strongEqual(intISetOfOne) shouldBe true
        intISetOfOne.strongEqual(FKSet.ofi(1)) shouldBe true
        FKSet.ofi(1).strongEqual(intISetOfOne) shouldBe true
        intISetOfOne.strongEqual(intISetOfTwo) shouldBe false
        intISetOfTwo.strongEqual(intISetOfOne) shouldBe false
        intISetOfTwo.strongEqual(intISetOfTwo) shouldBe true
        intISetOfTwo.strongEqual(FKSet.ofi(1,2)) shouldBe true
        FKSet.ofi(1,2).strongEqual(intISetOfTwo) shouldBe true
        intISetOfTwo.strongEqual(intISetOfTwoOfst1) shouldBe false
        intISetOfTwoOfst1.strongEqual(intISetOfTwo) shouldBe false
        intISetOfTwo.strongEqual(intISetOfThree) shouldBe false
        intISetOfTwoOfst1.strongEqual(intISetOfThree) shouldBe false
        intISetOfThree.strongEqual(intISetOfTwo) shouldBe false
        intISetOfThree.strongEqual(intISetOfTwoOfst1) shouldBe false
        intISetOfThree.strongEqual(intISetOfThree) shouldBe true
    }

    test("fforEach") {
        val counter = AtomicInteger(0)
        val summer = AtomicInteger(0)
        val doCount: (Int) -> Unit = { counter.incrementAndGet() }
        val doSum: (Int) -> Unit = { v -> summer.addAndGet(v) }
        intISetOfNone.fforEach(doCount)
        counter.get() shouldBe 0
        intISetOfNone.fforEach(doSum)
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
        intISetOfNone.toIMBTree() shouldBe FRBTree.emptyIMBTree()
        checkAll(repeats, Arb.fset<Int, Int>(Arb.int(),20..100)) { fs ->
            val frbt: IMBTree<Int, Int> = fs.toIMBTree()
            val fbst = FBSTree.of(frbt.breadthFirst())
            val fs1 = FKSet.ofi(fbst)
            fs.equals(fs1) shouldBe true
        }
    }

    test("toIMSetNonEmptu") {
        intISetOfNone.toIMSetNotEmpty() shouldBe null
        checkAll(repeats, Arb.fset<Int, Int>(Arb.int(),20..100)) { fs ->
            val frbt: IMBTree<Int, Int> = fs.toIMBTree()
            val fbst = FBSTree.of(frbt.breadthFirst())
            val fs1 = FKSet.ofi(fbst)
            (fs1.toIMSetNotEmpty() === fs1) shouldBe true
            fs1.toIMSetNotEmpty()?.equals(fs) shouldBe true
        }
    }

    test("copy") {
        intISetOfNone.copy().equals(intISetOfNone) shouldBe true
        checkAll(repeats, Arb.fset<Int, Int>(Arb.int(),20..100)) { fs ->
            val fs1 = fs.copy()
            (fs1 === fs) shouldBe false
            fs.strongEqual(fs1) shouldBe true
        }
    }

    test("copyToMutableSet") {
        intISetOfNone.copyToMutableSet() shouldBe mutableSetOf()
        checkAll(repeats, Arb.fset<Int, Int>(Arb.int(),20..100)) { fs ->
            val ms: MutableSet<Int> = fs.copyToMutableSet()
            (fs == ms) shouldBe true
            (ms == fs) shouldBe true
        }
    }
})
