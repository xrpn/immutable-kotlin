package com.xrpn.immutable

import com.xrpn.immutable.FIKSetOfOne.Companion.toSoO
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSetOfNone = FIKSet.of(*arrayOf<Int>())
private val intSetOfOne = FIKSet.of(1)
private val intSetOfTwo = FIKSet.of(1, 2)
private val intSetOfThree = FIKSet.of(1, 2, 3)

class FIKSetAlteringTest : FunSpec({

    beforeTest {}

    test("fadd") {
        intSetOfNone.fadd(1.toSoO()).equals(intSetOfOne) shouldBe true
        intSetOfOne.fadd(2.toSoO()).equals(intSetOfTwo) shouldBe true
        intSetOfTwo.fadd(3.toSoO()).equals(intSetOfThree) shouldBe true
        intSetOfThree.fadd(4.toSoO()).fsize() shouldBe 4
    }

})
