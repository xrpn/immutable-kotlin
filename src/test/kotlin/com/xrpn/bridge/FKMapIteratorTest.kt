package com.xrpn.bridge

import com.xrpn.immutable.FKMap
import com.xrpn.immutable.TKVEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intIMapOfNone = FKMap.of(*arrayOf<Pair<Int, Int>>())
private val intIMapOfOne = FKMap.of(*arrayOf(Pair(1,1)))
private val intIMapOfTwo = FKMap.of(*arrayOf(Pair(1,1), Pair(2,2)))
private val intIMapOfThree = FKMap.of(*arrayOf(Pair(1,1), Pair(2,2), Pair(3,3)))
private val strSMapOfTwo = FKMap.of(*arrayOf(Pair("1","1"), Pair("2","2")))

class FKMapIteratorTest : FunSpec({

    beforeTest {}

    test("hasNext") {
        FKMapIterator(intIMapOfNone).hasNext() shouldBe false
        FKMapIterator(intIMapOfOne).hasNext() shouldBe true
        FKMapIterator(intIMapOfTwo).hasNext() shouldBe true
    }

    test("next (once)") {
        shouldThrow<NoSuchElementException> {
            FKMapIterator(intIMapOfNone).next()
        }
        val iter = FKMapIterator(intIMapOfOne)
        iter.hasNext() shouldBe true
        iter.next() shouldBe TKVEntry.ofkk(1,1)
        shouldThrow<NoSuchElementException> {
            iter.next()
        }
        val aux = FKMapIterator(strSMapOfTwo)
        aux.next() shouldBe TKVEntry.ofkk("1","1")
        aux.next() shouldBe TKVEntry.ofkk("2","2")
        shouldThrow<NoSuchElementException> {
            aux.next()
        }
    }

    test("nullableNext") {
        FKMapIterator(intIMapOfNone).nullableNext() shouldBe null
        FKMapIterator(intIMapOfOne).nullableNext() shouldBe TKVEntry.ofkk(1,1)
        val aux = FKMapIterator(strSMapOfTwo)
        aux.nullableNext() shouldBe TKVEntry.ofkk("1","1")
        aux.nullableNext() shouldBe TKVEntry.ofkk("2","2")
        aux.nullableNext() shouldBe null
    }

    test("next (drain)") {
        val iter = FKMapIterator(intIMapOfThree)
        iter.hasNext() shouldBe true
        iter.next() shouldBe TKVEntry.ofkk(1,1)
        iter.hasNext() shouldBe true
        iter.next() shouldBe TKVEntry.ofkk(2,2)
        iter.hasNext() shouldBe true
        iter.next() shouldBe TKVEntry.ofkk(3,3)
        iter.hasNext() shouldBe false
        shouldThrow<NoSuchElementException> {
            iter.next()
        }
    }

    test("ix to and fro") {
        val iter = FKMapIterator(intIMapOfThree)
        iter.hasNext() shouldBe true

        iter.next() shouldBe TKVEntry.ofkk(1,1)
        iter.hasNext() shouldBe true

        iter.next() shouldBe TKVEntry.ofkk(2,2)

        iter.resettable shouldBe true
        iter.resetIfEmpty() shouldBe false
        iter.reset() shouldBe true
        iter.hasNext() shouldBe true

        iter.next() shouldBe TKVEntry.ofkk(1,1)
        iter.hasNext() shouldBe true

        iter.next() shouldBe TKVEntry.ofkk(2,2)

        while (iter.hasNext()) iter.next()
        iter.hasNext() shouldBe false

        iter.resetIfEmpty() shouldBe true
        iter.hasNext() shouldBe true
        iter.next() shouldBe TKVEntry.ofkk(1,1)
    }
})