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

class FListIteratorBkwdTest : FunSpec({

    beforeTest {}

    test("hasPrevious") {
        FListIteratorBkwd(FLNil).hasNext() shouldBe false
        FListIteratorBkwd(intListOfNone).hasPrevious() shouldBe false
        FListIteratorBkwd(intListOfOne).hasPrevious() shouldBe true
        FListIteratorBkwd(intListOfTwo).hasPrevious() shouldBe true
    }

    test("previous (once)") {
        shouldThrow<NoSuchElementException> {
            FListIteratorBkwd(intListOfNone).previous()
        }
    }

    test("next (once)") {
        shouldThrow<NotImplementedError> {
            FListIteratorBkwd(intListOfThree).next()
        }
    }

    test("nullablePrevious") {
        FListIteratorBkwd(intListOfNone).nullablePrevious() shouldBe null
        FListIteratorBkwd(intListOfOne).nullablePrevious() shouldBe 1
        FListIteratorBkwd(strListOfTwo).nullablePrevious() shouldBe "b"
    }

    test("next (drain)") {
        val iter = FListIteratorBkwd(intListOfThree)
        iter.hasPrevious() shouldBe true
        iter.previous() shouldBe 3
        iter.hasPrevious() shouldBe true
        iter.previous() shouldBe 2
        iter.hasPrevious() shouldBe true
        iter.previous() shouldBe 1
        iter.hasPrevious() shouldBe false
        shouldThrow<NoSuchElementException> {
            iter.previous()
        }
    }
})

