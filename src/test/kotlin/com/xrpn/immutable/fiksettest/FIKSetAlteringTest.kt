package com.xrpn.immutable.fiksettest

import com.xrpn.imapi.IMKASetNotEmpty
import com.xrpn.imapi.IMXSetNotEmpty
import com.xrpn.immutable.emptyArrayOfStr
import com.xrpn.immutable.FKSet
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

private val strISetOfNone = FKSet.ofi(*emptyArrayOfStr)
private val strISetOfOne = FKSet.ofi("1").ne()!!
private val strISetOfTwo = FKSet.ofi("1", "2").ne()!!
private val strISetOfThree = FKSet.ofi("1", "2", "3").ne()!!

private val strKKSetOfOne: IMXSetNotEmpty<String> = FKSet.ofk("1").nex()!!
private val strKKSetOfTwo: IMXSetNotEmpty<String> = FKSet.ofk("1", "2").nex()!!

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
        val aux1 = strISetOfNone.faddItem("1")
        aux1.equals(strISetOfOne)  shouldBe true
        (aux1 === strISetOfOne) shouldBe false
        aux1.ner() shouldNotBe null
        aux1.ne() shouldNotBe null
        aux1.nex<String>() shouldBe null
    }
})
