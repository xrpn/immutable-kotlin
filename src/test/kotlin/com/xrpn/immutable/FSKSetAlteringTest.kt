package com.xrpn.immutable

import com.xrpn.imapi.IMSetNotEmpty
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSetOfNone = FKSet.ofs(*arrayOf<Int>())
private val intSetOfOne = @Suppress("UNCHECKED_CAST") (FKSet.ofs(1) as IMSetNotEmpty<String, Int>)
private val intSetOfTwo = @Suppress("UNCHECKED_CAST") (FKSet.ofs(1, 2) as IMSetNotEmpty<String, Int>)
private val intSetOfThree = @Suppress("UNCHECKED_CAST") (FKSet.ofs(1, 2, 3) as IMSetNotEmpty<String, Int>)

class FSKSetAlteringTest : FunSpec({

    beforeTest {}

    test("faddItem") {
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (intSetOfNone as IMSetNotEmpty<String, Int>)
        }
        (@Suppress("UNCHECKED_CAST") (intSetOfOne.faddItem(2) as IMSetNotEmpty<String, Int>)).strongEqual(intSetOfTwo) shouldBe true
        (@Suppress("UNCHECKED_CAST") (intSetOfTwo.faddItem(3) as IMSetNotEmpty<String, Int>)).strongEqual(intSetOfThree) shouldBe true
        intSetOfOne.faddItem(2).safeEqual(intSetOfTwo) shouldBe true
        intSetOfTwo.faddItem(3).safeEqual(intSetOfThree) shouldBe true
        intSetOfThree.faddItem(4).fsize() shouldBe 4
    }

})
