package com.xrpn.immutable.fstacktest

import com.xrpn.immutable.*
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
private val strStackOfOneA = FStack.of(*arrayOf<String>(itemA))
private val strStackOfOneB = FStack.of(*arrayOf<String>(itemB))
private val strStackOfOneC = FStack.of(*arrayOf<String>(itemC))
private val strStackOfTwoAB = FStack.of(*arrayOf<String>(itemA, itemB))
private val strStackOfTwoBA = FStack.of(*arrayOf<String>(itemB, itemA))
private val strStackOfTwoBC = FStack.of(*arrayOf<String>(itemB, itemC))
private val strStackOfThree = FStack.of(*arrayOf<String>(itemA, itemB, itemC))

class FStackFilteringTest : FunSpec({

    beforeTest {}

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

    test("fdropWhile") {
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

})
