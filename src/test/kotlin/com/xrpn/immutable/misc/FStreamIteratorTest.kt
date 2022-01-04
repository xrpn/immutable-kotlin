package com.xrpn.immutable.misc

import com.xrpn.immutable.FSNil
import com.xrpn.immutable.FStreamIterator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FStreamIteratorTest : FunSpec({

    beforeTest {}

    test("hasNext") {
        FStreamIterator(FSNil).hasNext() shouldBe false
        FStreamIterator(intStreamOfNone()).hasNext() shouldBe false
        FStreamIterator(intStreamOfOne()).hasNext() shouldBe true
        FStreamIterator(intStreamOfTwo()).hasNext() shouldBe true
    }

    test("next (once)") {
        shouldThrow<IllegalStateException> {
            FStreamIterator(intStreamOfNone()).next()
        }
        FStreamIterator(intStreamOfOne()).next() shouldBe 1
        FStreamIterator(strStreamOfTwo()).next() shouldBe "a"
    }

    test("nullableNext") {
        FStreamIterator(intStreamOfNone()).nullableNext() shouldBe null
        FStreamIterator(intStreamOfOne()).nullableNext() shouldBe 1
        FStreamIterator(strStreamOfTwo()).nullableNext() shouldBe "a"
    }

    test("next (drain)") {
        val iter = FStreamIterator(intStreamOfThree())
        iter.hasNext() shouldBe true
        iter.next() shouldBe 1
        iter.hasNext() shouldBe true
        iter.next() shouldBe 2
        iter.hasNext() shouldBe true
        iter.next() shouldBe 3
        iter.hasNext() shouldBe false
    }

})

