package com.xrpn.immutable.fkksettest

import com.xrpn.imapi.*
import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.FKSet.Companion.ofk
import com.xrpn.immutable.FKSet.Companion.ofs
import com.xrpn.immutable.FKSet
import com.xrpn.immutable.FList
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

private val intKKSetOfNone = ofk(*emptyArrayOfInt)
private val intKKSetOfOne: IMCVSetNotEmpty<Int> = ofk(1).necvs()!!
private val intKKSetOfTwo: IMCVSetNotEmpty<Int> = ofk(1, 2).necvs()!!
private val intKKSetOfThree = ofk(1, 2, 3).necvs<Int>()!!
private val intList12 = FList.of(1,2)
private val intList23 = FList.of(2,3)

private val intSSetOfOne: IMVSetNotEmpty<Int> = ofs(1).nevs()!!
private val intSSetOfTwo: IMVSetNotEmpty<Int> = ofs(1, 2).nevs()!!

private val strKKSetOfOne: IMVSetNotEmpty<String> = ofk("1").necvs()!!

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

//        shouldThrow<ClassCastException> {
//            (object : IMUniversal{}).errLog(System.err, FKKSetAlteringTest::class).emitUnconditionally("EXPECTED FAILURE")
//            // this should not compile, but it does
//            intKKSetOfOne.tibSet<Int>()!!.faddUniq("1", intKKSetOfOne).first shouldBe false
//            strKKSetOfOne.tibSet<String>()!!.faddUniq("1", intKKSetOfOne).first shouldBe false
//        }

        intKKSetOfOne.tibSet<Int>()!!.faddUniq(1, intKKSetOfOne).first shouldBe false
        (intKKSetOfOne.tibSet<Int>()!!.faddUniq(1, intKKSetOfOne).second === intKKSetOfOne) shouldBe true

        intKKSetOfOne.tibSet<Int>()!!.faddUniq(2, intKKSetOfOne).first shouldBe true

        intKKSetOfOne.tibSet<Int>()!!.faddUniq(2, intKKSetOfOne).first shouldBe true
        val (count1, res1) = intKKSetOfOne.tibSet<Int>()!!.faddUniqs(intList12, intKKSetOfOne)
        count1 shouldBe 1
        res1!!.equal(intKKSetOfTwo) shouldBe true
        val (count2, res2) = intKKSetOfOne.tibSet<Int>()!!.faddUniqs(intList23, intKKSetOfOne)
        count2 shouldBe 2
        res2!!.equal(intKKSetOfThree) shouldBe true

        strKKSetOfOne.tibSet<String>()!!.faddUniq("2", strKKSetOfOne).first shouldBe true
        (@Suppress("UNCHECKED_CAST") (intKKSetOfOne.tibSet<Int>()!!.faddUniq(2, intKKSetOfOne).second.vcvdj().right() as IMKKSetNotEmpty<Int>)).equal(intKKSetOfTwo.asIMSet()) shouldBe true
        intKKSetOfOne.tibSet<Int>()!!.faddUniq(3, intKKSetOfTwo).first shouldBe true
        (@Suppress("UNCHECKED_CAST") (intKKSetOfOne.tibSet<Int>()!!.faddUniq(3, intKKSetOfTwo).second.vcvdj().right() as IMKKSetNotEmpty<Int>)).equal(intKKSetOfThree.asIMSet()) shouldBe true
        intKKSetOfOne.tibSet<Int>()!!.faddUniq(2, intKKSetOfOne).second.equal(intKKSetOfTwo) shouldBe true
        intKKSetOfOne.tibSet<Int>()!!.faddUniq(3, intKKSetOfTwo).second.equal(intKKSetOfThree) shouldBe true
        intKKSetOfOne.tibSet<Int>()!!.faddUniq(4, intKKSetOfThree).second.fsize() shouldBe 4

        intKKSetOfOne.tibSet<Int>()!!.faddUniq(2, intSSetOfOne).second.equal(intKKSetOfTwo) shouldBe true
        intKKSetOfOne.tibSet<Int>()!!.faddUniq(3, intSSetOfTwo).second.equal(intKKSetOfThree) shouldBe true
    }


    test("faddUniq on empty") {

        intKKSetOfNone.tibSet<Int>()!!.faddUniq(1, intKKSetOfNone).first shouldBe true
        intKKSetOfNone.tibSet<Int>()!!.faddUniq(1, intKKSetOfNone).second.equal(intKKSetOfOne) shouldBe true


        val aux1: FKSet<Int, Int> = intKKSetOfNone.faddUniq(1)
        aux1.equal(intKKSetOfOne)  shouldBe true
        (aux1 === intKKSetOfOne) shouldBe false
        aux1.nes() shouldNotBe null
        aux1.nevs() shouldNotBe null
        aux1.necvs<Int>() shouldNotBe null
    }
    
})
