package com.xrpn.bridge

import com.xrpn.bridge.FListIteratorBidi.Companion.IX_START
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

class FListIteratorBidiTest : FunSpec({

    beforeTest {}

    test("hasNext") {
        FListIteratorBidi(FLNil).hasNext() shouldBe false
        FListIteratorBidi(intListOfNone).hasNext() shouldBe false
        FListIteratorBidi(intListOfOne).hasNext() shouldBe true
        FListIteratorBidi(intListOfTwo).hasNext() shouldBe true
    }

    test("next (once)") {
        shouldThrow<NoSuchElementException> {
            FListIteratorBidi(intListOfNone).next()
        }
        FListIteratorBidi(intListOfOne).next() shouldBe 1
        FListIteratorBidi(strListOfTwo).next() shouldBe "a"
    }

    test("next (drain)") {
        val iter = FListIteratorBidi(intListOfThree)
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

    test("bidi to and fro") {
        val iter = FListIteratorBidi(intListOfThree)

        // before start, next() never called
        iter.hasPrevious() shouldBe false
        iter.hasNext() shouldBe true
        shouldThrow<NoSuchElementException> {
            iter.previousIndex()
        }
        shouldThrow<NoSuchElementException> {
            iter.previous()
        }
        iter.nextIndex() shouldBe 0

        iter.next() shouldBe 1
        // at first element, index 0
        iter.hasPrevious() shouldBe true // this is the same as what the .next() above just provided
        iter.hasNext() shouldBe true
        iter.nextIndex() shouldBe 1
        iter.previousIndex() shouldBe IX_START
        iter.previous() shouldBe 1
        iter.nextIndex() shouldBe 0
        shouldThrow<NoSuchElementException> {
            iter.previousIndex()
        }
        shouldThrow<NoSuchElementException> {
            iter.previous()
        }

        iter.next() shouldBe 1
        iter.next() shouldBe 2
        // at second element, index 1
        iter.hasPrevious() shouldBe true
        iter.hasNext() shouldBe true
        iter.previousIndex() shouldBe 1
        iter.nextIndex() shouldBe 2

        iter.next() shouldBe 3
        // at third element, index 2
        iter.hasPrevious() shouldBe true
        iter.hasNext() shouldBe false

        iter.nextIndex() shouldBe intListOfThree.size
        iter.previousIndex() shouldBe 2
        iter.previous() shouldBe 3
        iter.previousIndex() shouldBe 1
        iter.nextIndex() shouldBe 2

        iter.previous() shouldBe 2
        iter.previousIndex() shouldBe 0
        iter.nextIndex() shouldBe 1
    }
})

