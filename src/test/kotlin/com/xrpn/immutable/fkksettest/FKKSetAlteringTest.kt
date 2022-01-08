package com.xrpn.immutable.fkksettest

import com.xrpn.imapi.*
import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.FKSet.Companion.ofk
import com.xrpn.immutable.FKSet.Companion.ofs
import com.xrpn.imapi.IMSet.Companion.faddUniq
import com.xrpn.immutable.FKSet
import com.xrpn.immutable.fiksettest.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

private val intKKSetOfNone = ofk(*emptyArrayOfInt)
private val intKKSetOfOne: IMCVSetNotEmpty<Int> = ofk(1).necvs()!!
private val intKKSetOfTwo: IMCVSetNotEmpty<Int> = ofk(1, 2).necvs()!!
private val intKKSetOfThree = ofk(1, 2, 3).necvs<Int>()!!

private val intSSetOfOne: IMVSetNotEmpty<Int> = ofs(1).nevs()!!
private val intSSetOfTwo: IMVSetNotEmpty<Int> = ofs(1, 2).nevs()!!

class FKKSetAlteringTest : FunSpec({

    beforeTest {}

//    test("faddItem") {
//        shouldThrow<ClassCastException> {
//            @Suppress("UNCHECKED_CAST") (intKKSetOfNone as IMKASetNotEmpty<Int, Int>)
//        }
//        (@Suppress("UNCHECKED_CAST") (intKKSetOfOne.faddUniq(2) as IMKKSetNotEmpty<Int>)).equal(intKKSetOfTwo) shouldBe true
//        (@Suppress("UNCHECKED_CAST") (intKKSetOfTwo.faddUniq(3) as IMKKSetNotEmpty<Int>)).equal(intKKSetOfThree) shouldBe true
//        intKKSetOfOne.faddUniq(2).equals(intKKSetOfTwo) shouldBe true
//        intKKSetOfTwo.faddUniq(3).equals(intKKSetOfThree) shouldBe true
//        intKKSetOfThree.faddUniq(4).fsize() shouldBe 4
//
//        intSSetOfOne.faddUniq(2).equals(intKKSetOfTwo) shouldBe true
//        intSSetOfOne.faddUniq(2).equals(intSSetOfTwo) shouldBe true
//    }
//
//    test("faddItem on empty") {
//        val aux1 = intKKSetOfNone.faddUniq(1)
//        aux1.equals(intKKSetOfOne)  shouldBe true
//        (aux1 === intKKSetOfOne) shouldBe false
//        aux1.nes() shouldNotBe null
//        aux1.nevs() shouldBe null
//        aux1.necvs<String>() shouldNotBe null
//    }
    
    test("faddUniq") {
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (intKKSetOfNone as IMKASetNotEmpty<Int, String>)
        }

        shouldThrow<ClassCastException> {
            (object : IMUniversal{}).errLog(System.err, FKKSetAlteringTest::class).emitUnconditionally("EXPECTED FAILURE")
            // this should not compile, but it does
            faddUniq("1", intKKSetOfOne).first shouldBe false
        }

        faddUniq(1, intKKSetOfOne).first shouldBe false
        (faddUniq(1, intKKSetOfOne).second === intKKSetOfOne) shouldBe true

        faddUniq(2, intKKSetOfOne).first shouldBe true
        (@Suppress("UNCHECKED_CAST") (faddUniq(2, intKKSetOfOne).second.vcvdj().right() as IMKKSetNotEmpty<Int>)).equal(intKKSetOfTwo.asIMSet()) shouldBe true
        faddUniq(3, intKKSetOfTwo).first shouldBe true
        (@Suppress("UNCHECKED_CAST") (faddUniq(3, intKKSetOfTwo).second.vcvdj().right() as IMKKSetNotEmpty<Int>)).equal(intKKSetOfThree.asIMSet()) shouldBe true
        faddUniq(2, intKKSetOfOne).second.equal(intKKSetOfTwo) shouldBe true
        faddUniq(3, intKKSetOfTwo).second.equal(intKKSetOfThree) shouldBe true
        faddUniq(4, intKKSetOfThree).second.fsize() shouldBe 4

        faddUniq(2, intSSetOfOne).second.equal(intKKSetOfTwo) shouldBe true
        faddUniq(3, intSSetOfTwo).second.equal(intKKSetOfThree) shouldBe true
    }


    test("faddUniq on empty") {

        faddUniq(1, intKKSetOfNone).first shouldBe true
        faddUniq(1, intKKSetOfNone).second.equal(intKKSetOfOne) shouldBe true


        val aux1: FKSet<Int, Int> = intKKSetOfNone.faddUniq(1)
        aux1.equal(intKKSetOfOne)  shouldBe true
        (aux1 === intKKSetOfOne) shouldBe false
        aux1.nes() shouldNotBe null
        aux1.nevs() shouldNotBe null
        aux1.necvs<Int>() shouldNotBe null
    }
    
})
