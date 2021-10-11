package com.xrpn.bridge

import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.FKSet
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSetOfNone = FKSet.ofi(*emptyArrayOfInt)
private val intSetOfOne = FKSet.ofi(*arrayOf<Int>(1))
private val intSetOfTwo = FKSet.ofi(*arrayOf<Int>(1,2))
private val intSetOfThree = FKSet.ofi(*arrayOf<Int>(1,2,3))
private val strSetOfTwo = FKSet.ofi(*arrayOf<String>("a","b"))

class FKSetIteratorTest : FunSpec({

    beforeTest {}

    test("hasNext") {
        FKSetIterator(intSetOfNone).hasNext() shouldBe false
        FKSetIterator(intSetOfOne).hasNext() shouldBe true
        FKSetIterator(intSetOfTwo).hasNext() shouldBe true
    }

    test("next (once)") {
        shouldThrow<NoSuchElementException> {
            FKSetIterator(intSetOfNone).next()
        }
        val iter = FKSetIterator(intSetOfOne)
        iter.next() shouldBe 1
        shouldThrow<NoSuchElementException> {
            iter.next()
        }
        val aux = FKSetIterator(strSetOfTwo)
        aux.next() shouldBe "a"
        aux.next() shouldBe "b"
        shouldThrow<NoSuchElementException> {
            aux.next()
        }
    }

    test("nullableNext") {
        FKSetIterator(intSetOfNone).nullableNext() shouldBe null
        FKSetIterator(intSetOfOne).nullableNext() shouldBe 1
        val aux = FKSetIterator(strSetOfTwo)
        aux.nullableNext() shouldBe "a"
        aux.nullableNext() shouldBe "b"
        aux.nullableNext() shouldBe null
    }

    test("next (drain)") {
        val iter = FKSetIterator(intSetOfThree)
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
        val iter = FKSetIterator(intSetOfThree)
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