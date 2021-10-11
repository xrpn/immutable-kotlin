package com.xrpn.immutable.fkksettest

import com.xrpn.imapi.*
import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.FKSet.Companion.ofk
import com.xrpn.immutable.FKSet.Companion.ofs
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intKKSetOfNone = ofk(*emptyArrayOfInt)
private val intKKSetOfOne: IMRRSetNotEmpty<Int> = ofk(1).rrne()!!
private val intKKSetOfTwo: IMRRSetNotEmpty<Int> = ofk(1, 2).rrne()!!
private val intKKSetOfThree = ofk(1, 2, 3).rrne()!!

private val intSSetOfOne: IMRSetNotEmpty<Int> = ofs(1).rne()!!
private val intSSetOfTwo: IMRSetNotEmpty<Int> = ofs(1, 2).rne()!!

class FKKSetAlteringTest : FunSpec({

    beforeTest {}

    test("faddItem") {
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (intKKSetOfNone as IMKASetNotEmpty<Int, Int>)
        }
        (@Suppress("UNCHECKED_CAST") (intKKSetOfOne.faddItem(2) as IMKKSetNotEmpty<Int>)).strongEqual(intKKSetOfTwo) shouldBe true
        (@Suppress("UNCHECKED_CAST") (intKKSetOfTwo.faddItem(3) as IMKKSetNotEmpty<Int>)).strongEqual(intKKSetOfThree) shouldBe true
        intKKSetOfOne.faddItem(2).equal(intKKSetOfTwo) shouldBe true
        intKKSetOfTwo.faddItem(3).equal(intKKSetOfThree) shouldBe true
        intKKSetOfThree.faddItem(4).fsize() shouldBe 4

        intSSetOfOne.faddItem(2).equal(intKKSetOfTwo) shouldBe true
        intSSetOfOne.faddItem(2).equal(intSSetOfTwo) shouldBe true
    }

    test("faddItem on empty") {
        val aux1: IMSetNotEmpty<Int> = intKKSetOfNone.faddItem(1)
        aux1.equals(intKKSetOfOne)  shouldBe true
        val aux2: IMSetNotEmpty<Int> = intKKSetOfNone.faddItem(1, forceIntKey = false)
        aux2.equals(intKKSetOfOne)  shouldBe true
    }
})
