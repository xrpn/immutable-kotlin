package com.xrpn.immutable.fiksettest

import com.xrpn.imapi.IMKASetNotEmpty
import com.xrpn.immutable.emptyArrayOfStr
import com.xrpn.immutable.FKSet
import com.xrpn.imapi.IMSet.Companion.faddUniq
import com.xrpn.imapi.IMUniversal
import com.xrpn.immutable.fkksettest.FKKSetAlteringTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

private val strISetOfNone = FKSet.ofi(*emptyArrayOfStr)
private val strISetOfOne = FKSet.ofi("1")
private val strISetOfTwo = FKSet.ofi("1", "2")
private val strISetOfThree = FKSet.ofi("1", "2", "3")

private val strKKSetOfOne = FKSet.ofk("1")
private val strKKSetOfTwo = FKSet.ofk("1", "2")

class FIKSetAlteringTest : FunSpec({

    beforeTest {}

    test("faddUniq") {
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (strISetOfNone as IMKASetNotEmpty<Int, String>)
        }

        shouldThrow<IllegalStateException> {
            // this should not compile, but it does
            faddUniq(2, strISetOfOne).first shouldBe true
        }

        faddUniq("1",strISetOfOne).first shouldBe false
        (faddUniq("1",strISetOfOne).second === strISetOfOne) shouldBe true

        faddUniq("2",strISetOfOne).first shouldBe true
        (@Suppress("UNCHECKED_CAST") (faddUniq("2",strISetOfOne).second.vcvdj().left() as IMKASetNotEmpty<Int, String>)).equal(strISetOfTwo.asIMSet()) shouldBe true
        faddUniq("3",strISetOfTwo).first shouldBe true
        (@Suppress("UNCHECKED_CAST") (faddUniq("3",strISetOfTwo).second.vcvdj().left() as IMKASetNotEmpty<Int, String>)).equal(strISetOfThree.asIMSet()) shouldBe true
        faddUniq("2", strISetOfOne).second.equal(strISetOfTwo) shouldBe true
        faddUniq("3", strISetOfTwo).second.equal(strISetOfThree) shouldBe true
        faddUniq("4",strISetOfThree).second.fsize() shouldBe 4

        faddUniq("2",strKKSetOfOne).second.equal(strISetOfTwo) shouldBe true
        faddUniq("3",strKKSetOfTwo).second.equal(strISetOfThree) shouldBe true
    }

    test("faddcUniq") {
        TODO()
    }

    test("faddUniq on empty") {

        faddUniq("1",strISetOfNone).first shouldBe true
        faddUniq("1",strISetOfNone).second.equal(strISetOfOne) shouldBe true


        val aux1: FKSet<Int, String> = strISetOfNone.faddUniq("1")
        aux1.equal(strISetOfOne)  shouldBe true
        (aux1 === strISetOfOne) shouldBe false
        aux1.nes() shouldNotBe null
        aux1.nevs() shouldNotBe null
        aux1.necvs<String>() shouldBe null
    }
})
