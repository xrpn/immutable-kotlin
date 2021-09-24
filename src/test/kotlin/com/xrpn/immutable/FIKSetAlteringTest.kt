package com.xrpn.immutable

import com.xrpn.imapi.IMSetNotEmpty
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSetOfNone = FKSet.ofi(*arrayOf<Int>())
private val intSetOfOne: IMSetNotEmpty<Int, Int> = @Suppress("UNCHECKED_CAST") (FKSet.ofi(1) as IMSetNotEmpty<Int, Int>)
private val intSetOfTwo: IMSetNotEmpty<Int, Int> = @Suppress("UNCHECKED_CAST") (FKSet.ofi(1, 2) as IMSetNotEmpty<Int, Int>)
private val intSetOfThree: IMSetNotEmpty<Int, Int> = @Suppress("UNCHECKED_CAST") (FKSet.ofi(1, 2, 3) as IMSetNotEmpty<Int, Int>)

class FIKSetAlteringTest : FunSpec({

    beforeTest {}

    test("faddItem") {
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (intSetOfNone as IMSetNotEmpty<Int, Int>)
        }
        (@Suppress("UNCHECKED_CAST") (intSetOfOne.faddItem(2) as IMSetNotEmpty<Int, Int>)).strongEqual(intSetOfTwo) shouldBe true
        (@Suppress("UNCHECKED_CAST") (intSetOfTwo.faddItem(3) as IMSetNotEmpty<Int, Int>)).strongEqual(intSetOfThree) shouldBe true
        intSetOfOne.faddItem(2).safeEqual(intSetOfTwo) shouldBe true
        intSetOfTwo.faddItem(3).safeEqual(intSetOfThree) shouldBe true
        intSetOfThree.faddItem(4).fsize() shouldBe 4
    }

})
