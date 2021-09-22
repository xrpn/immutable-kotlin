package com.xrpn.immutable

import com.xrpn.imapi.IMSetNotEmpty
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSetOfNone = FKSet.ofi(*arrayOf<Int>())
private val intSetOfOne = @Suppress("UNCHECKED_CAST") (FKSet.ofi(1) as IMSetNotEmpty<Int, Int>)
private val intSetOfTwo = @Suppress("UNCHECKED_CAST") (FKSet.ofi(1, 2) as IMSetNotEmpty<Int, Int>)
private val intSetOfThree = @Suppress("UNCHECKED_CAST") (FKSet.ofi(1, 2, 3) as IMSetNotEmpty<Int, Int>)

class FIKSetAlteringTest : FunSpec({

    beforeTest {}

    test("faddItem") {
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (intSetOfNone as IMSetNotEmpty<Int, Int>)
        }
        intSetOfOne.faddItem(2).strongEqual(intSetOfTwo) shouldBe true
        intSetOfTwo.faddItem(3).strongEqual(intSetOfThree) shouldBe true
        intSetOfThree.faddItem(4).fsize() shouldBe 4
    }

})
