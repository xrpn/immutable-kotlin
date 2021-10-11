package com.xrpn.immutable.fsksettest

import com.xrpn.imapi.IMKASetNotEmpty
import com.xrpn.imapi.IMRRSetNotEmpty
import com.xrpn.imapi.IMSetNotEmpty
import com.xrpn.immutable.FKSet
import com.xrpn.immutable.emptyArrayOfInt
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intSSetOfNone = FKSet.ofs(*emptyArrayOfInt)
private val intSSetOfOne = FKSet.ofs(1).rne()!!
private val intSSetOfTwo = FKSet.ofs(1, 2).rne()!!
private val intSSetOfThree = FKSet.ofs(1, 2, 3).rne()!!

private val intKKSetOfOne: IMRRSetNotEmpty<Int> = FKSet.ofk(1).rrne()!!
private val intKKSetOfTwo: IMRRSetNotEmpty<Int> = FKSet.ofk(1, 2).rrne()!!

class FSKSetAlteringTest : FunSpec({

    beforeTest {}

    test("faddItem") {
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (intSSetOfNone as IMKASetNotEmpty<String, Int>)
        }
        (@Suppress("UNCHECKED_CAST") (intSSetOfOne.faddItem(2) as IMKASetNotEmpty<String, Int>)).strongEqual(intSSetOfTwo) shouldBe true
        (@Suppress("UNCHECKED_CAST") (intSSetOfTwo.faddItem(3) as IMKASetNotEmpty<String, Int>)).strongEqual(intSSetOfThree) shouldBe true
        intSSetOfOne.faddItem(2).equal(intSSetOfTwo) shouldBe true
        intSSetOfTwo.faddItem(3).equal(intSSetOfThree) shouldBe true
        intSSetOfThree.faddItem(4).fsize() shouldBe 4

        intKKSetOfOne.faddItem(2).equal(intSSetOfTwo) shouldBe true
        intKKSetOfTwo.faddItem(3).equal(intSSetOfThree) shouldBe true
    }

    test("faddItem on empty") {
        val aux1: IMSetNotEmpty<Int> = intSSetOfNone.faddItem(1)
        aux1.equals(intKKSetOfOne) shouldBe true
        val aux2: IMSetNotEmpty<Int> = intSSetOfNone.faddItem(1, forceIntKey = false)
        aux2.equals(intKKSetOfOne) shouldBe true
    }
})
