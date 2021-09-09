package com.xrpn.immutable

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.flist
import java.util.concurrent.atomic.AtomicInteger

private val itemA = "A"
private val itemB = "B"
private val itemC = "C"
private val strStackOfNone = FStack.of(*arrayOf<String>())
private val intStackOfNone = FStack.of(*arrayOf<Int>())
private val strStackOfOneA = FStack.of(*arrayOf<String>(itemA))
private val strStackOfOneB = FStack.of(*arrayOf<String>(itemB))
private val strStackOfOneC = FStack.of(*arrayOf<String>(itemC))
private val strStackOfTwoAB = FStack.of(*arrayOf<String>(itemA, itemB))
private val strStackOfTwoBA = FStack.of(*arrayOf<String>(itemB, itemA))
private val strStackOfTwoBC = FStack.of(*arrayOf<String>(itemB, itemC))
private val strStackOfTwoCB = FStack.of(*arrayOf<String>(itemC, itemB))
private val strStackOfThree = FStack.of(*arrayOf<String>(itemA, itemB, itemC))

class FStackTest : FunSpec({

    val repeats = 50

    beforeTest {}

    // altering

    test("fpop") {
        var aux = strStackOfNone.fpop()
        aux.first shouldBe null
        aux.second shouldBe strStackOfNone

        aux = strStackOfOneA.fpop()
        aux.first shouldBe itemA
        aux.second shouldBe strStackOfNone

        aux = strStackOfTwoAB.fpop()
        aux.first shouldBe itemA
        aux.second shouldBe strStackOfOneB

        aux = strStackOfTwoBC.fpop()
        aux.first shouldBe itemB
        aux.second shouldBe strStackOfOneC

        aux = strStackOfThree.fpop()
        aux.first shouldBe itemA
        aux.second shouldBe strStackOfTwoBC
        aux = aux.second.fpop()
        aux.first shouldBe itemB
        aux.second shouldBe strStackOfOneC
        aux = aux.second.fpop()
        aux.first shouldBe itemC
        aux.second shouldBe strStackOfNone
    }

    test("pop vs ierator") {
        val iter = strStackOfThree.iterator()

        tailrec fun go(s: FStack<String>) {
            if (s.fempty()) {
                iter.hasNext() shouldBe false
                return
            }
            val (item, shortStack) = s.fpop()
            iter.nullableNext() shouldBe item
            go(shortStack)
        }

        go(strStackOfThree)
    }


    test("fpopOrThrow") {
        shouldThrow<IllegalStateException> {
            strStackOfNone.fpopOrThrow()
        }

        var aux = strStackOfOneA.fpopOrThrow()
        aux.first shouldBe itemA
        aux.second shouldBe strStackOfNone

        aux = strStackOfTwoAB.fpopOrThrow()
        aux.first shouldBe itemA
        aux.second shouldBe strStackOfOneB

        aux = strStackOfTwoBC.fpopOrThrow()
        aux.first shouldBe itemB
        aux.second shouldBe strStackOfOneC

        aux = strStackOfThree.fpopOrThrow()
        aux.first shouldBe itemA
        aux.second shouldBe strStackOfTwoBC
        aux = aux.second.fpopOrThrow()
        aux.first shouldBe itemB
        aux.second shouldBe strStackOfOneC
        aux = aux.second.fpopOrThrow()
        aux.first shouldBe itemC
        aux.second shouldBe strStackOfNone
        shouldThrow<IllegalStateException> {
            aux.second.fpopOrThrow()
        }
    }

    test("fpush") {
        FStack.emptyIMStack<String>().fpush("a") shouldBe FStackBody.of(FLCons("a", FLNil))
        FStackBody.of(FLCons("a", FLNil)).fpush("b") shouldBe FStackBody.of(FLCons("b", FLCons("a", FLNil)))

        strStackOfNone.fpush(itemB) shouldBe strStackOfOneB
        strStackOfOneA.fpush(itemB) shouldBe strStackOfTwoBA
        strStackOfOneC.fpush(itemB) shouldBe strStackOfTwoBC
        strStackOfOneC.fpush(itemB).fpush(itemA) shouldBe strStackOfThree
    }

    // filtering

    test("fdrop") {
        strStackOfNone.fdrop(-1) shouldBe strStackOfNone
        strStackOfNone.fdrop(0) shouldBe strStackOfNone
        strStackOfNone.fdrop(1) shouldBe strStackOfNone

        strStackOfOneA.fdrop(-1) shouldBe strStackOfOneA
        strStackOfOneA.fdrop(0) shouldBe strStackOfOneA
        strStackOfOneA.fdrop(1) shouldBe strStackOfNone
        strStackOfOneA.fdrop(2) shouldBe strStackOfNone

        strStackOfThree.fdrop(-1) shouldBe strStackOfThree
        strStackOfThree.fdrop(0) shouldBe strStackOfThree
        strStackOfThree.fdrop(1) shouldBe strStackOfTwoBC
        strStackOfThree.fdrop(2) shouldBe strStackOfOneC
        strStackOfThree.fdrop(3) shouldBe strStackOfNone
        strStackOfThree.fdrop(4) shouldBe strStackOfNone
    }

    test("fdropIfTop") {
        strStackOfNone.fdropIfTop("FOO") shouldBe strStackOfNone
        strStackOfNone.fdropIfTop(itemA) shouldBe strStackOfNone

        strStackOfOneA.fdropIfTop("FOO") shouldBe strStackOfOneA
        strStackOfOneA.fdropIfTop(itemB) shouldBe strStackOfOneA
        strStackOfOneA.fdropIfTop(itemA) shouldBe strStackOfNone

        strStackOfThree.fdropIfTop("FOO") shouldBe strStackOfThree
        strStackOfThree.fdropIfTop(itemB) shouldBe strStackOfThree
        strStackOfThree.fdropIfTop(itemA) shouldBe strStackOfTwoBC
    }

    test("fdropTopWhen") {
        strStackOfNone.fdropTopWhen {true} shouldBe strStackOfNone
        strStackOfNone.fdropTopWhen {false} shouldBe strStackOfNone

        strStackOfOneA.fdropTopWhen {false} shouldBe strStackOfOneA
        strStackOfOneA.fdropTopWhen {true} shouldBe strStackOfNone
        strStackOfOneA.fdropTopWhen { it == itemB } shouldBe strStackOfOneA
        strStackOfOneA.fdropTopWhen { it == itemA } shouldBe strStackOfNone

        strStackOfTwoAB.fdropTopWhen {false} shouldBe strStackOfTwoAB
        strStackOfTwoAB.fdropTopWhen {true} shouldBe strStackOfOneB
        strStackOfTwoAB.fdropTopWhen { it == itemB } shouldBe strStackOfTwoAB
        strStackOfTwoAB.fdropTopWhen { it == itemA } shouldBe strStackOfOneB
        strStackOfTwoBA.fdropTopWhen { it == itemB } shouldBe strStackOfOneA
        strStackOfTwoBA.fdropTopWhen { it == itemA } shouldBe strStackOfTwoBA
    }

    test("fdropTopWhile") {
        strStackOfNone.fdropWhile {true} shouldBe strStackOfNone
        strStackOfNone.fdropWhile {false} shouldBe strStackOfNone

        strStackOfOneA.fdropWhile {false} shouldBe strStackOfOneA
        strStackOfOneA.fdropWhile {true} shouldBe strStackOfNone
        strStackOfOneA.fdropWhile { it == "FOO" } shouldBe strStackOfOneA
        strStackOfOneA.fdropWhile { it == itemA } shouldBe strStackOfNone

        strStackOfThree.fdropWhile {false} shouldBe strStackOfThree
        strStackOfThree.fdropWhile {true} shouldBe strStackOfNone
        strStackOfThree.fdropWhile { it == "FOO" } shouldBe strStackOfThree
        strStackOfThree.fdropWhile { it == itemA } shouldBe strStackOfTwoBC
        strStackOfThree.fdropWhile { it < itemB } shouldBe strStackOfTwoBC
        strStackOfThree.fdropWhile { it < itemC } shouldBe strStackOfOneC
        strStackOfThree.fdropWhile { itemB < it } shouldBe strStackOfThree
    }

    test("ftopMatch") {
        strStackOfNone.ftopMatch {true} shouldBe false
        strStackOfNone.ftopMatch {false} shouldBe false

        strStackOfOneA.ftopMatch {false} shouldBe false
        strStackOfOneA.ftopMatch {true} shouldBe true
        strStackOfOneA.ftopMatch { it == itemB } shouldBe false
        strStackOfOneA.ftopMatch { it == itemA } shouldBe true

        strStackOfTwoAB.ftopMatch {false} shouldBe false
        strStackOfTwoAB.ftopMatch {true} shouldBe true
        strStackOfTwoAB.ftopMatch { it == itemB } shouldBe false
        strStackOfTwoAB.ftopMatch { it == itemA } shouldBe true
        strStackOfTwoBA.ftopMatch { it == itemB } shouldBe true
        strStackOfTwoBA.ftopMatch { it == itemA } shouldBe false
    }

    test("fempty") {
        FStack.emptyIMStack<Int>().fempty() shouldBe true
        FStackBody.of(FLCons("a", FLNil)).fempty() shouldBe false
    }

    test("ftop") {
        FStack.emptyIMStack<Int>().ftop() shouldBe null
        FStackBody.of(FLCons("a", FLNil)).ftop() shouldBe "a"
        FStackBody.of(FLCons("b", FLCons("a", FLNil))).ftop() shouldBe "b"
        strStackOfNone.ftop() shouldBe null
        strStackOfTwoAB.ftop() shouldBe itemA
        strStackOfTwoBC.ftop() shouldBe itemB
    }

    test("ftopOrThrow") {
        shouldThrow<IllegalStateException> {
            strStackOfNone.ftopOrThrow()
        }
        strStackOfTwoAB.ftopOrThrow() shouldBe itemA
        strStackOfTwoBC.ftopOrThrow() shouldBe itemB
    }

    // grouping

    test("fcount") {
        strStackOfNone.fcount { true } shouldBe 0
        strStackOfNone.fcount { false } shouldBe 0
        strStackOfOneA.fcount { true } shouldBe 1
        strStackOfOneA.fcount { false } shouldBe 0
        strStackOfOneA.fcount { it == itemA } shouldBe 1
        strStackOfOneA.fcount { it == itemB } shouldBe 0
        strStackOfThree.fcount { it < itemB } shouldBe 1
        strStackOfThree.fcount { it < itemC } shouldBe 2
    }

    test("fsize") {
        strStackOfNone.fsize() shouldBe 0
        strStackOfOneA.fsize() shouldBe 1
        strStackOfThree.fsize() shouldBe 3
    }

    // transforming

    test("fpopMap") {
        strStackOfNone.fpopMap { "Z" } shouldBe Pair(null, strStackOfNone)
        strStackOfOneA.fpopMap { itemB } shouldBe Pair(itemB, strStackOfNone)
        strStackOfTwoBC.fpopMap { itemA } shouldBe Pair(itemA, strStackOfOneC)
        strStackOfTwoBC.fpopMap { it } shouldBe Pair(itemB, strStackOfOneC)
        strStackOfThree.fpopMap { itemC } shouldBe Pair(itemC, strStackOfTwoBC)
    }

    test("freverse") {
        strStackOfNone.freverse() shouldBe strStackOfNone
        strStackOfOneA.freverse() shouldBe strStackOfOneA
        strStackOfTwoBC.freverse() shouldBe strStackOfTwoCB
        strStackOfThree.freverse().ftop() shouldBe itemC
        strStackOfThree.freverse().fpop().second shouldBe strStackOfTwoBA
    }

    test("ftopMap") {
        strStackOfNone.ftopMap { "Z" } shouldBe null
        strStackOfOneA.ftopMap { itemB } shouldBe itemB
        strStackOfTwoBC.ftopMap { itemA } shouldBe itemA
        strStackOfTwoBC.ftopMap { it } shouldBe itemB
        strStackOfThree.ftopMap { itemC } shouldBe itemC
    }

    // utility

    test("equal") {
        FStack.emptyIMStack<Int>().equal(FStack.emptyIMStack<Int>()) shouldBe true
        FStackBody.of(FLCons("a", FLCons("b", FLNil))).equal(FStackBody.of(FLCons("a", FLCons("b", FLNil)))) shouldBe true
        strStackOfNone.equal(strStackOfNone) shouldBe true
        // should NOT compile strStackOfNone.equal(intStackOfNone)
        strStackOfOneA.equal(strStackOfOneA) shouldBe true
        strStackOfOneB.equal(strStackOfOneA) shouldBe false
        strStackOfOneA.equal(strStackOfOneB) shouldBe false
        strStackOfTwoAB.equal(strStackOfTwoAB) shouldBe true
        strStackOfTwoBA.equal(strStackOfTwoAB) shouldBe false
        strStackOfTwoAB.equal(strStackOfTwoBA) shouldBe false
    }

    test("fforEach") {
        val counter = AtomicInteger(0)
        val summer = AtomicInteger(0)
        val doCount: (Int) -> Unit = { counter.incrementAndGet() }
        val doSum: (Int) -> Unit = { v -> summer.addAndGet(v) }
        intStackOfNone.fforEach(doCount)
        counter.get() shouldBe 0
        intStackOfNone.fforEach(doSum)
        summer.get() shouldBe 0
        counter.set(0)
        summer.set(0)
        checkAll(repeats, Arb.flist<Int, Int>(Arb.int(),20..100)) { fl ->
            val oraSum = fl.ffoldLeft(0){ acc, el -> acc + el }
            val fstack = FStackBody.of(fl)
            fstack.fforEach(doCount)
            counter.get() shouldBe fl.size
            counter.set(0)
            fstack.fforEach(doSum)
            summer.get() shouldBe oraSum
            summer.set(0)
        }
    }

    test("copy") {
        intStackOfNone.copy() shouldBe intStackOfNone
        (intStackOfNone.copy() === intStackOfNone) shouldBe true
        checkAll(repeats, Arb.flist<Int, Int>(Arb.int(),20..100)) { fl ->
            val fstack = FStackBody.of(fl)
            val fstack1 = fstack.copy()
            (fstack1 === fstack) shouldBe false
            fstack.equal(fstack1) shouldBe true
            fstack1.equal(fstack) shouldBe true
        }
    }

    test("toIMList") {
        intStackOfNone.toIMList() shouldBe FList.emptyIMList()
        checkAll(repeats, Arb.flist<Int, Int>(Arb.int(),20..100)) { fl ->
            val fstack = FStackBody.of(fl)
            fstack.toIMList().equal(fl) shouldBe true
        }
    }

    test("copyToMutableList") {
        intStackOfNone.copyToMutableList() shouldBe mutableListOf()
        checkAll(repeats, Arb.flist<Int, Int>(Arb.int(),20..100)) { fl ->
            val fstack = FStackBody.of(fl)
            fstack.copyToMutableList() shouldBe fl.copyToMutableList()
        }
    }

})
