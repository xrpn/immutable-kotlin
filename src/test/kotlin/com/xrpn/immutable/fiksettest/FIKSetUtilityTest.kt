package com.xrpn.immutable.fiksettest

import com.xrpn.imapi.IMBTree
import com.xrpn.immutable.emptyArrayOfStr
import com.xrpn.immutable.FBSTree
import com.xrpn.immutable.FKSet
import com.xrpn.immutable.FRBTree
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.xrpn.fsset
import java.util.concurrent.atomic.AtomicInteger

private val strKKSetOfNone = FKSet.ofs(*emptyArrayOfStr)
private val strKKSetOfOne = FKSet.ofs("1")
private val strKKSetOfTwo = FKSet.ofs("1", "2")
private val strKKSetOfTwoOfst1 = FKSet.ofs("2", "3")
private val strKKSetOfThree = FKSet.ofs("1", "2", "3")

private val strISetOfNone = FKSet.ofi(*emptyArrayOfStr)
private val strISetOfOne = FKSet.ofi("1")
private val strISetOfTwo = FKSet.ofi("1", "2")
private val strISetOfTwoOfst1 = FKSet.ofi("2", "3")
private val strISetOfThree = FKSet.ofi("1", "2", "3")

class FIKSetUtilityTest : FunSpec({

    val repeats = 50

    beforeTest {}

    test("equal same") {
        strKKSetOfNone.equal(strKKSetOfNone) shouldBe true
        strKKSetOfNone.equal(strKKSetOfOne) shouldBe false
        strKKSetOfOne.equal(strKKSetOfNone) shouldBe false
        strKKSetOfOne.equal(strKKSetOfOne) shouldBe true
        strKKSetOfOne.equals(FKSet.ofi("1")) shouldBe false
        FKSet.ofi("1").equals(strKKSetOfOne) shouldBe false
        strKKSetOfOne.equal(FKSet.ofi("1")) shouldBe true
        FKSet.ofi("1").equal(strKKSetOfOne) shouldBe true
        strKKSetOfOne.equal(strKKSetOfTwo) shouldBe false
        strKKSetOfTwo.equal(strKKSetOfOne) shouldBe false
        strKKSetOfTwo.equal(strKKSetOfTwo) shouldBe true
        strKKSetOfTwo.equals(FKSet.ofi("1","2")) shouldBe false
        FKSet.ofi("1","2").equals(strKKSetOfTwo) shouldBe false
        strKKSetOfTwo.equal(FKSet.ofi("1","2")) shouldBe true
        FKSet.ofi("1","2").equal(strKKSetOfTwo) shouldBe true
        strKKSetOfTwo.equal(strKKSetOfTwoOfst1) shouldBe false
        strKKSetOfTwoOfst1.equal(strKKSetOfTwo) shouldBe false
        strKKSetOfTwo.equal(strKKSetOfThree) shouldBe false
        strKKSetOfTwoOfst1.equal(strKKSetOfThree) shouldBe false
        strKKSetOfThree.equal(strKKSetOfTwo) shouldBe false
        strKKSetOfThree.equal(strKKSetOfTwoOfst1) shouldBe false
        strKKSetOfThree.equal(strKKSetOfThree) shouldBe true
    }

    test("equal not same") {
        strISetOfNone.equal(strKKSetOfNone) shouldBe true
        strISetOfNone.equal(strKKSetOfOne) shouldBe false
        strISetOfOne.equal(strKKSetOfNone) shouldBe false
        strISetOfOne.equal(strKKSetOfOne) shouldBe true
        strISetOfOne.equals(FKSet.ofk("1")) shouldBe false
        FKSet.ofk("1").equals(strISetOfOne) shouldBe false
        strISetOfOne.equal(FKSet.ofk("1")) shouldBe true
        FKSet.ofk("1").equal(strISetOfOne) shouldBe true
        strISetOfOne.equal(strKKSetOfTwo) shouldBe false
        strISetOfTwo.equal(strKKSetOfOne) shouldBe false
        strISetOfTwo.equal(strKKSetOfTwo) shouldBe true
        strISetOfTwo.equals(FKSet.ofk("1","2")) shouldBe false
        FKSet.ofk("1","2").equals(strISetOfTwo) shouldBe false
        strISetOfTwo.equal(FKSet.ofk("1","2")) shouldBe true
        FKSet.ofk("1","2").equal(strISetOfTwo) shouldBe true
        strISetOfTwo.equal(strKKSetOfTwoOfst1) shouldBe false
        strISetOfTwoOfst1.equal(strKKSetOfTwo) shouldBe false
        strISetOfTwo.equal(strKKSetOfThree) shouldBe false
        strISetOfTwoOfst1.equal(strKKSetOfThree) shouldBe false
        strISetOfThree.equal(strKKSetOfTwo) shouldBe false
        strISetOfThree.equal(strKKSetOfTwoOfst1) shouldBe false
        strISetOfThree.equal(strKKSetOfThree) shouldBe true
    }

    test("strongEqual") {
        strKKSetOfNone.strongEqual(strKKSetOfNone) shouldBe true
        strKKSetOfNone.strongEqual(strKKSetOfOne) shouldBe false
        strKKSetOfOne.strongEqual(strKKSetOfNone) shouldBe false
        strKKSetOfOne.strongEqual(strKKSetOfOne) shouldBe true
        strKKSetOfOne.strongEqual(strISetOfOne) shouldBe false
        strISetOfOne.strongEqual(strKKSetOfOne) shouldBe false
        strKKSetOfOne.strongEqual(strKKSetOfTwo) shouldBe false
        strKKSetOfTwo.strongEqual(strKKSetOfOne) shouldBe false
        strKKSetOfTwo.strongEqual(strKKSetOfTwo) shouldBe true
        strKKSetOfTwo.strongEqual(strISetOfTwo) shouldBe false
        strISetOfTwo.strongEqual(strKKSetOfTwo) shouldBe false
        strKKSetOfTwo.strongEqual(strKKSetOfTwoOfst1) shouldBe false
        strKKSetOfTwoOfst1.strongEqual(strKKSetOfTwo) shouldBe false
        strKKSetOfTwo.strongEqual(strKKSetOfThree) shouldBe false
        strKKSetOfTwoOfst1.strongEqual(strKKSetOfThree) shouldBe false
        strKKSetOfThree.strongEqual(strKKSetOfTwo) shouldBe false
        strKKSetOfThree.strongEqual(strKKSetOfTwoOfst1) shouldBe false
        strKKSetOfThree.strongEqual(strKKSetOfThree) shouldBe true
    }

    test("fforEach") {
        val counter = AtomicInteger(0)
        val summer = AtomicInteger(0)
        val doCount: (Int) -> Unit = { counter.incrementAndGet() }
        val doSum: (Int) -> Unit = { v -> summer.addAndGet(v) }
        checkAll(repeats, Arb.fsset(Arb.int(),20..100)) { fs ->
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
        strKKSetOfNone.toIMBTree() shouldBe FRBTree.emptyIMBTree()
        checkAll(repeats, Arb.fsset(Arb.int(),20..100)) { fs ->
            val frbt: IMBTree<String, Int> = fs.toIMBTree()
            val fbst = FBSTree.of(frbt.breadthFirst())
            val fs1 = FKSet.ofs(fbst)
            fs.equals(fs1) shouldBe true
        }
    }

    test("toIMSetNonEmpty") {
        strKKSetOfNone.asIMRSetNotEmpty() shouldBe null
        checkAll(repeats, Arb.fsset(Arb.int(),20..100)) { fs ->
            val frbt: IMBTree<String, Int> = fs.toIMBTree()
            val fbst = FBSTree.of(frbt.breadthFirst())
            val fs1: FKSet<String, Int> = FKSet.ofs(fbst)!!
            (fs1.asIMRRSetNotEmpty() == null) shouldBe true
            (fs1.asIMRSetNotEmpty() === fs1) shouldBe true
            fs1.asIMRSetNotEmpty()?.equal(fs) shouldBe true
        }
        checkAll(repeats, Arb.fsset(Arb.string(),20..100)) { fs ->
            val frbt: IMBTree<String, String> = fs.toIMBTree()
            val fbst: FBSTree<String, String> = FBSTree.of(frbt.breadthFirst())
            val fs1: FKSet<String, String> = FKSet.ofs(fbst)!!
            (fs1.asIMRRSetNotEmpty() === fs1) shouldBe true
            (fs1.asIMRSetNotEmpty() == null) shouldBe true
            fs1.asIMRRSetNotEmpty()?.equal(fs) shouldBe true
        }
    }

    test("copy") {
        strKKSetOfNone.copy().equals(strKKSetOfNone) shouldBe true
        checkAll(repeats, Arb.fsset(Arb.int(),20..100)) { fs ->
            val fs1 = fs.copy()
            (fs1 === fs) shouldBe false
            fs.strongEqual(fs1) shouldBe true
        }
    }

    test("copyToMutableSet") {
        strKKSetOfNone.copyToMutableSet() shouldBe mutableSetOf()
        checkAll(repeats, Arb.fsset(Arb.int(),20..100)) { fs ->
            val ms: MutableSet<Int> = fs.copyToMutableSet()
            (fs == ms) shouldBe true
            (ms == fs) shouldBe true
        }
    }
})
