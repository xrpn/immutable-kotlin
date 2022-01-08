package com.xrpn.immutable.fsksettest

import com.xrpn.imapi.IMKASetNotEmpty
import com.xrpn.imapi.IMCVSetNotEmpty
import com.xrpn.immutable.FKSet
import com.xrpn.imapi.IMSet.Companion.faddUniq
import com.xrpn.imapi.IMVSetNotEmpty
import com.xrpn.immutable.emptyArrayOfInt
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

private val intSSetOfNone = FKSet.ofs(*emptyArrayOfInt)
private val intSSetOfOne: IMVSetNotEmpty<Int> = FKSet.ofs(1).nevs()!!
private val intSSetOfTwo: IMVSetNotEmpty<Int> = FKSet.ofs(1, 2).nevs()!!
private val intSSetOfThree: IMVSetNotEmpty<Int> = FKSet.ofs(1, 2, 3).nevs()!!

private val intKKSetOfOne: IMCVSetNotEmpty<Int> = FKSet.ofk(1).necvs()!!
private val intKKSetOfTwo: IMCVSetNotEmpty<Int> = FKSet.ofk(1, 2).necvs()!!

class FSKSetAlteringTest : FunSpec({

    beforeTest {}

    test("faddUniq") {
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (intSSetOfNone as IMKASetNotEmpty<String, Int>)
        }

        faddUniq(1, intSSetOfOne).first shouldBe false
        (faddUniq(1, intSSetOfOne).second === intSSetOfOne) shouldBe true

        shouldThrow<IllegalStateException> {
            // this should not compile, but it does
            faddUniq("2", intSSetOfOne).first shouldBe true
        }

        faddUniq(2, intSSetOfOne).first shouldBe true
        (@Suppress("UNCHECKED_CAST") (faddUniq(2, intSSetOfOne).second.vcvdj().left() as IMKASetNotEmpty<String, Int>)).equal(intSSetOfTwo.asIMSet()) shouldBe true
        faddUniq(3, intSSetOfTwo).first shouldBe true
        (@Suppress("UNCHECKED_CAST") (faddUniq(3, intSSetOfTwo).second.vcvdj().left() as IMKASetNotEmpty<String, Int>)).equal(intSSetOfThree.asIMSet()) shouldBe true
        faddUniq(2, intSSetOfOne).second.equal(intSSetOfTwo) shouldBe true
        faddUniq(3, intSSetOfTwo).second.equal(intSSetOfThree) shouldBe true
        faddUniq(4, intSSetOfThree).second.fsize() shouldBe 4

        faddUniq(2, intKKSetOfOne).second.equal(intSSetOfTwo) shouldBe true
        faddUniq(3, intKKSetOfTwo).second.equal(intSSetOfThree) shouldBe true
    }

    test("faddcUniq") {
        TODO()
    }

    test("faddUniq on empty") {

        faddUniq(1, intSSetOfNone).first shouldBe true
        faddUniq(1, intSSetOfNone).second.equal(intSSetOfOne) shouldBe true


        val aux1: FKSet<String, Int> = intSSetOfNone.faddUniq(1)
        aux1.equal(intSSetOfOne)  shouldBe true
        (aux1 === intSSetOfOne) shouldBe false
        aux1.nes() shouldNotBe null
        aux1.nevs() shouldNotBe null
        aux1.necvs<String>() shouldBe null
    }
    
})
