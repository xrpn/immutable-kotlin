package com.xrpn.bridge

import com.xrpn.immutable.FQueue
import com.xrpn.immutable.FQueue.Companion.emptyIMQueue
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intQueueOfNone = FQueue.of(*arrayOf<Int>())
private val intQueueOfOne = FQueue.of(*arrayOf<Int>(1))
private val intQueueOfTwo = FQueue.of(*arrayOf<Int>(1,2))
private val intQueueOfThree = FQueue.of(*arrayOf<Int>(1,2,3))
private val strListOfTwo = FQueue.of(*arrayOf<String>("a","b"))

class FQueueIteratorTest : FunSpec({

    beforeTest {}

    test("hasNext") {
        FQueueIterator(emptyIMQueue()).hasNext() shouldBe false
        FQueueIterator(intQueueOfNone).hasNext() shouldBe false
        FQueueIterator(intQueueOfOne).hasNext() shouldBe true
        FQueueIterator(intQueueOfTwo).hasNext() shouldBe true
    }

    test("next (once)") {
        shouldThrow<NoSuchElementException> {
            FQueueIterator(intQueueOfNone).next()
        }
        val iter = FQueueIterator(intQueueOfOne)
        iter.next() shouldBe 1
        shouldThrow<NoSuchElementException> {
            iter.next()
        }
        FQueueIterator(strListOfTwo).next() shouldBe "a"
    }

    test("nullableNext") {
        FQueueIterator(intQueueOfNone).nullableNext() shouldBe null
        FQueueIterator(intQueueOfOne).nullableNext() shouldBe 1
        FQueueIterator(strListOfTwo).nullableNext() shouldBe "a"
    }

    test("next (drain)") {
        val iter = FQueueIterator(intQueueOfThree)
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
        val iter = FQueueIterator(intQueueOfThree)
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

