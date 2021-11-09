package com.xrpn.immutable.fstacktest

import com.xrpn.immutable.*
import com.xrpn.immutable.FStack.Companion.emptyIMStack
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
private val strStackOfNone = FStack.of(*emptyArrayOfStr)
private val intStackOfNone = FStack.of(*emptyArrayOfInt)
private val strStackOfOneA = FStack.of(*arrayOf<String>(itemA))
private val strStackOfOneB = FStack.of(*arrayOf<String>(itemB))
private val strStackOfOneC = FStack.of(*arrayOf<String>(itemC))
private val strStackOfTwoAB = FStack.of(*arrayOf<String>(itemA, itemB))
private val strStackOfTwoBA = FStack.of(*arrayOf<String>(itemB, itemA))
private val strStackOfTwoBC = FStack.of(*arrayOf<String>(itemB, itemC))
private val strStackOfThree = FStack.of(*arrayOf<String>(itemA, itemB, itemC))

private val intStackOfOne = FStack.of(*arrayOf<Int>(1))
private val intStackOfOneB = FStack.of(*arrayOf<Int>(2))
private val intStackOfTwo = FStack.of(*arrayOf<Int>(1, 2))
private val intStackOfTwoB = FStack.of(*arrayOf<Int>(2, 3))

class FStackTest : FunSpec({

    val repeats = 50

    beforeTest {}

    // grouping (NOP)

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

    test("pop vs iterator") {
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

    // transforming

    test("fmap") {
        (strStackOfNone.fmap { it + 1} === emptyIMStack<String>()) shouldBe true
        intStackOfOne.fmap { it + 1 }.equals(intStackOfOneB) shouldBe true
        intStackOfTwo.fmap { it + 1 }.equals(intStackOfTwoB) shouldBe true
    }

    test("fpopMap") {
        strStackOfNone.fpopMap { "Z" } shouldBe Pair(null, strStackOfNone)
        strStackOfOneA.fpopMap { itemB } shouldBe Pair(itemB, strStackOfNone)
        strStackOfTwoBC.fpopMap { itemA } shouldBe Pair(itemA, strStackOfOneC)
        strStackOfTwoBC.fpopMap { it } shouldBe Pair(itemB, strStackOfOneC)
        strStackOfThree.fpopMap { itemC } shouldBe Pair(itemC, strStackOfTwoBC)
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
