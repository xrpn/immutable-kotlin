package com.xrpn.immutable.fsksettest

import com.xrpn.bridge.FKSetIterator
import com.xrpn.imapi.IMBTree
import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.FBSTree
import com.xrpn.immutable.FKSet
import com.xrpn.immutable.FRBTree
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.xrpn.fiset
import java.util.concurrent.atomic.AtomicInteger

private val intKKOfNone = FKSet.ofi(*emptyArrayOfInt)
private val intKKOfOne = FKSet.ofi(1)
private val intKKOfTwo = FKSet.ofi(1, 2)
private val intKKOfTwoOfst1 = FKSet.ofi(2, 3)
private val intKKOfThree = FKSet.ofi(1, 2, 3)

private val intSSetOfNone = FKSet.ofs(*emptyArrayOfInt)
private val intSSetOfOne = FKSet.ofs(1)
private val intSSetOfTwo = FKSet.ofs(1, 2)
private val intSSetOfTwoOfst1 = FKSet.ofs(2, 3)
private val intSSetOfThree = FKSet.ofs(1, 2, 3)

class FSKSetUtilityTest : FunSpec({

    val repeats = 50

    beforeTest {}

    test("equal same") {
        intKKOfNone.equal(intKKOfNone) shouldBe true
        intKKOfNone.equal(intKKOfOne) shouldBe false
        intKKOfOne.equal(intKKOfNone) shouldBe false
        intKKOfOne.equal(intKKOfOne) shouldBe true
        intKKOfOne.equal(FKSet.ofi(1)) shouldBe true
        FKSet.ofi(1).equal(intKKOfOne) shouldBe true
        intKKOfOne.equal(intKKOfTwo) shouldBe false
        intKKOfTwo.equal(intKKOfOne) shouldBe false
        intKKOfTwo.equal(intKKOfTwo) shouldBe true
        intKKOfTwo.equal(FKSet.ofi(1,2)) shouldBe true
        FKSet.ofi(1,2).equal(intKKOfTwo) shouldBe true
        intKKOfTwo.equal(intKKOfTwoOfst1) shouldBe false
        intKKOfTwoOfst1.equal(intKKOfTwo) shouldBe false
        intKKOfTwo.equal(intKKOfThree) shouldBe false
        intKKOfTwoOfst1.equal(intKKOfThree) shouldBe false
        intKKOfThree.equal(intKKOfTwo) shouldBe false
        intKKOfThree.equal(intKKOfTwoOfst1) shouldBe false
        intKKOfThree.equal(intKKOfThree) shouldBe true
    }

    test("equal not same") {
        intSSetOfNone.equal(intKKOfNone) shouldBe true
        intSSetOfNone.equal(intKKOfOne) shouldBe false
        intSSetOfOne.equal(intKKOfNone) shouldBe false
        intSSetOfOne.equal(intKKOfOne) shouldBe true
        intSSetOfOne.equal(FKSet.ofi(1)) shouldBe true
        FKSet.ofs(1).equal(intKKOfOne) shouldBe true
        intSSetOfOne.equal(intKKOfTwo) shouldBe false
        intSSetOfTwo.equal(intKKOfOne) shouldBe false
        intSSetOfTwo.equal(intKKOfTwo) shouldBe true
        intSSetOfTwo.equal(FKSet.ofi(1,2)) shouldBe true
        FKSet.ofs(1,2).equal(intKKOfTwo) shouldBe true
        intSSetOfTwo.equal(intKKOfTwoOfst1) shouldBe false
        intSSetOfTwoOfst1.equal(intKKOfTwo) shouldBe false
        intSSetOfTwo.equal(intKKOfThree) shouldBe false
        intSSetOfTwoOfst1.equal(intKKOfThree) shouldBe false
        intSSetOfThree.equal(intKKOfTwo) shouldBe false
        intSSetOfThree.equal(intKKOfTwoOfst1) shouldBe false
        intSSetOfThree.equal(intKKOfThree) shouldBe true
    }

    test("strongEqual") {
        intKKOfNone.equal(intKKOfNone) shouldBe true
        intKKOfNone.equal(intKKOfOne) shouldBe false
        intKKOfOne.equal(intKKOfNone) shouldBe false
        intKKOfOne.equal(intKKOfOne) shouldBe true
        intKKOfOne.equal(FKSet.ofi(1)) shouldBe true
        FKSet.ofi(1).equal(intKKOfOne) shouldBe true
        intKKOfOne.equal(intKKOfTwo) shouldBe false
        intKKOfTwo.equal(intKKOfOne) shouldBe false
        intKKOfTwo.equal(intKKOfTwo) shouldBe true
        intKKOfTwo.equal(FKSet.ofi(1,2)) shouldBe true
        FKSet.ofi(1,2).equal(intKKOfTwo) shouldBe true
        intKKOfTwo.equal(intKKOfTwoOfst1) shouldBe false
        intKKOfTwoOfst1.equal(intKKOfTwo) shouldBe false
        intKKOfTwo.equal(intKKOfThree) shouldBe false
        intKKOfTwoOfst1.equal(intKKOfThree) shouldBe false
        intKKOfThree.equal(intKKOfTwo) shouldBe false
        intKKOfThree.equal(intKKOfTwoOfst1) shouldBe false
        intKKOfThree.equal(intKKOfThree) shouldBe true
    }

    test("fforEach") {
        val counter = AtomicInteger(0)
        val summer = AtomicInteger(0)
        val doCount: (Int) -> Unit = { counter.incrementAndGet() }
        val doSum: (Int) -> Unit = { v -> summer.addAndGet(v) }
        intKKOfNone.fforEach(doCount)
        counter.get() shouldBe 0
        intKKOfNone.fforEach(doSum)
        summer.get() shouldBe 0
        counter.set(0)
        summer.set(0)
        checkAll(repeats, Arb.fiset<Int, Int>(Arb.int(),20..100)) { fs ->
            val oraSum = fs.ffold(0){ acc, el -> acc + el }
            fs.fforEach(doCount)
            counter.get() shouldBe fs.fsize()
            counter.set(0)
            fs.fforEach(doSum)
            summer.get() shouldBe oraSum
            summer.set(0)
        }
    }

    test("toIMBTree") {
        intKKOfNone.toIMBTree() shouldBe FRBTree.emptyIMBTree()
        checkAll(repeats, Arb.fiset<Int, Int>(Arb.int(),20..100)) { fs ->
            val frbt: IMBTree<Int, Int> = fs.toIMBTree()
            val fbst = FBSTree.of(frbt.breadthFirst())
            val fs1 = FKSet.ofi(fbst)
            fs.equals(fs1) shouldBe true
        }
    }

    test("toIMSetNonEmpty") {
        intKKOfNone.asIMSetNotEmpty() shouldBe null
        checkAll(repeats, Arb.fiset<Int, Int>(Arb.int(),20..100)) { fs ->
            val frbt: IMBTree<Int, Int> = fs.toIMBTree()
            val fbst = FBSTree.of(frbt.breadthFirst())
            val fs1: FKSet<Int, Int> = FKSet.ofi(fbst)!!
            (fs1.asIMSetNotEmpty() == null) shouldBe true
            (fs1.asIMXSetNotEmpty<Int>() === fs1) shouldBe true
            fs1.asIMXSetNotEmpty<Int>()?.equal(fs) shouldBe true
        }
        checkAll(repeats, Arb.fiset(Arb.string(),20..100)) { fs ->
            val frbt: IMBTree<Int, String> = fs.toIMBTree()
            val fbst: FBSTree<Int, String> = FBSTree.of(frbt.breadthFirst())
            val fs1: FKSet<Int, String> = FKSet.ofi(fbst)!!
            (fs1.asIMSetNotEmpty() === fs1) shouldBe true
            (fs1.asIMXSetNotEmpty<Int>() == null) shouldBe true
            fs1.asIMSetNotEmpty()?.equal(fs) shouldBe true
        }
    }

    test("copy") {
        intKKOfNone.copy().equals(intKKOfNone) shouldBe true
        checkAll(repeats, Arb.fiset<Int, Int>(Arb.int(),20..100)) { fs ->
            val fs1 = fs.copy()
            (fs1 === fs) shouldBe false
            fs.equal(fs1) shouldBe true
        }
    }

    test("copyToMutableSet") {
        intKKOfNone.copyToMutableSet() shouldBe mutableSetOf()
        checkAll(repeats, Arb.fiset<Int, Int>(Arb.int(),20..100)) { fs ->
            val ms: MutableSet<Int> = fs.copyToMutableSet()
            (fs == ms) shouldBe false
            (ms == fs) shouldBe false
            (FKSetIterator(fs).toSet() == ms) shouldBe true
            (ms == FKSetIterator(fs).toSet()) shouldBe true
        }
    }
})
