package com.xrpn.bridge

import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.FLNil
import com.xrpn.immutable.FList
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intListOfNone = FList.of(*emptyArrayOfInt)
private val intListOfOne = FList.of(*arrayOf<Int>(1))
private val intListOfTwo = FList.of(*arrayOf<Int>(1,2))
private val intListOfThree = FList.of(*arrayOf<Int>(1,2,3))
private val strListOfTwo = FList.of(*arrayOf<String>("a","b"))

class FListIteratorFwdTest : FunSpec({

    beforeTest {}

    test("hasNext") {
        FListIteratorFwd(FLNil).hasNext() shouldBe false
        FListIteratorFwd(intListOfNone).hasNext() shouldBe false
        FListIteratorFwd(intListOfOne).hasNext() shouldBe true
        FListIteratorFwd(intListOfTwo).hasNext() shouldBe true
    }

    test("next (once)") {
        shouldThrow<NoSuchElementException> {
            FListIteratorFwd(intListOfNone).next()
        }
        val iter = FListIteratorFwd(intListOfOne)
        iter.next() shouldBe 1
        shouldThrow<NoSuchElementException> {
            iter.next()
        }
        FListIteratorFwd(strListOfTwo).next() shouldBe "a"
    }

    test("nullableNext") {
        FListIteratorFwd(intListOfNone).nullableNext() shouldBe null
        FListIteratorFwd(intListOfOne).nullableNext() shouldBe 1
        FListIteratorFwd(strListOfTwo).nullableNext() shouldBe "a"
    }

    test("next (drain)") {
        val iter = FListIteratorFwd(intListOfThree)
        iter.hasNext() shouldBe true
        iter.next() shouldBe 1
        iter.hasNext() shouldBe true
        iter.next() shouldBe 2
        iter.hasNext() shouldBe true
        iter.next() shouldBe 3
        iter.hasNext() shouldBe false
        shouldThrow<NoSuchElementException> {
            iter.next()
        }
    }

    test("ix to and fro") {
        val iter = FListIteratorFwd(intListOfThree)
        iter.hasNext() shouldBe true

        iter.next() shouldBe 1
        iter.hasNext() shouldBe true

        iter.next() shouldBe 2

        iter.resettable shouldBe true
        iter.resetIfEmpty() shouldBe false
        iter.reset() shouldBe true
        iter.hasNext() shouldBe true

        iter.next() shouldBe 1
        iter.hasNext() shouldBe true

        iter.next() shouldBe 2

        while (iter.hasNext()) iter.next()
        iter.hasNext() shouldBe false

        iter.resetIfEmpty() shouldBe true
        iter.hasNext() shouldBe true
        iter.next() shouldBe 1
    }
})

