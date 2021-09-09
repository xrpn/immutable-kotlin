package com.xrpn.bridge

import com.xrpn.immutable.FStack
import com.xrpn.immutable.FStack.Companion.emptyIMStack
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intStackOfNone = FStack.of(*arrayOf<Int>())
private val intStackOfOne = FStack.of(*arrayOf<Int>(1))
private val intStackOfTwo = FStack.of(*arrayOf<Int>(1,2))
private val intStackOfThree = FStack.of(*arrayOf<Int>(1,2,3))
private val strListOfTwo = FStack.of(*arrayOf<String>("a","b"))

class FStackIteratorTest : FunSpec({

    beforeTest {}

    test("hasNext") {
        FStackIterator(emptyIMStack()).hasNext() shouldBe false
        FStackIterator(intStackOfNone).hasNext() shouldBe false
        FStackIterator(intStackOfOne).hasNext() shouldBe true
        FStackIterator(intStackOfTwo).hasNext() shouldBe true
    }

    test("next (once)") {
        shouldThrow<NoSuchElementException> {
            FStackIterator(intStackOfNone).next()
        }
        val iter = FStackIterator(intStackOfOne)
        iter.next() shouldBe 1
        shouldThrow<NoSuchElementException> {
            iter.next()
        }
        FStackIterator(strListOfTwo).next() shouldBe "a"
    }

    test("nullableNext") {
        FStackIterator(intStackOfNone).nullableNext() shouldBe null
        FStackIterator(intStackOfOne).nullableNext() shouldBe 1
        FStackIterator(strListOfTwo).nullableNext() shouldBe "a"
    }

    test("next (drain)") {
        val iter = FStackIterator(intStackOfThree)
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
        val iter = FStackIterator(intStackOfThree)
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