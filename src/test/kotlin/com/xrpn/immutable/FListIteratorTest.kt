package com.xrpn.immutable

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FListIteratorTest : FunSpec({

    beforeTest {
    }

//  afterTest { (testCase, result) ->
//  }

    test("hasNext") {
        FListIterator(FLNil).hasNext() shouldBe false
        FListIterator(intListOfNone).hasNext() shouldBe false
        FListIterator(intListOfOne).hasNext() shouldBe true
        FListIterator(intListOfTwo).hasNext() shouldBe true
    }

    test("next (once)") {
        shouldThrow<IllegalStateException> {
            FListIterator(intListOfNone).next()
        }
        FListIterator(intListOfOne).next() shouldBe 1
        FListIterator(strListOfTwo).next() shouldBe "a"
    }

    test("nullableNext") {
        FListIterator(intListOfNone).nullableNext() shouldBe null
        FListIterator(intListOfOne).nullableNext() shouldBe 1
        FListIterator(strListOfTwo).nullableNext() shouldBe "a"
    }

    test("next (drain)") {
        val iter = FListIterator(intListOfThree)
        iter.hasNext() shouldBe true
        iter.next() shouldBe 1
        iter.hasNext() shouldBe true
        iter.next() shouldBe 2
        iter.hasNext() shouldBe true
        iter.next() shouldBe 3
        iter.hasNext() shouldBe false
    }

})

