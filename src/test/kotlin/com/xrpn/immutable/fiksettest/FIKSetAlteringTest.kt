package com.xrpn.immutable.fiksettest

import com.xrpn.imapi.IMKASetNotEmpty
import com.xrpn.imapi.IMRRSetNotEmpty
import com.xrpn.imapi.IMSetNotEmpty
import com.xrpn.immutable.emptyArrayOfStr
import com.xrpn.immutable.FKSet
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val strISetOfNone = FKSet.ofi(*emptyArrayOfStr)
private val strISetOfOne = FKSet.ofi("1").rne()!!
private val strISetOfTwo = FKSet.ofi("1", "2").rne()!!
private val strISetOfThree = FKSet.ofi("1", "2", "3").rne()!!

private val strKKSetOfOne: IMRRSetNotEmpty<String> = FKSet.ofk("1").rrne()!!
private val strKKSetOfTwo: IMRRSetNotEmpty<String> = FKSet.ofk("1", "2").rrne()!!

class FIKSetAlteringTest : FunSpec({

    beforeTest {}

    test("faddItem") {
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (strISetOfNone as IMKASetNotEmpty<Int, String>)
        }
        (@Suppress("UNCHECKED_CAST") (strISetOfOne.faddItem("2") as IMKASetNotEmpty<Int, String>)).strongEqual(strISetOfTwo) shouldBe true
        (@Suppress("UNCHECKED_CAST") (strISetOfTwo.faddItem("3") as IMKASetNotEmpty<Int, String>)).strongEqual(strISetOfThree) shouldBe true
        strISetOfOne.faddItem("2").equal(strISetOfTwo) shouldBe true
        strISetOfTwo.faddItem("3").equal(strISetOfThree) shouldBe true
        strISetOfThree.faddItem("4").fsize() shouldBe 4

        strKKSetOfOne.faddItem("2").equal(strISetOfTwo) shouldBe true
        strKKSetOfTwo.faddItem("3").equal(strISetOfThree) shouldBe true
    }


    test("faddItem on empty") {
        val aux1: IMSetNotEmpty<String> = strISetOfNone.faddItem("1")
        aux1.equals(strISetOfOne)  shouldBe true
        val aux2: IMSetNotEmpty<String> = strISetOfNone.faddItem("1", forceIntKey = false)
        aux2.equals(strKKSetOfOne)  shouldBe true
    }
})
