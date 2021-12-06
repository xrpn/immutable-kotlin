package com.xrpn.immutable.flisttest

import com.xrpn.imapi.*
import com.xrpn.immutable.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.flist

private val intListOfNone = FList.of(*emptyArrayOfInt)
private val longListOfNone = FList.of(*emptyArrayOfLong)
private val intListOfOne = FList.of(*arrayOf<Int>(1))
private val strListOfOne = FList.of(*arrayOf<String>("A"))
private val boolListOfOne = FList.of(*arrayOf<Boolean>(true))
private val intListOfTwo = FList.of(*arrayOf<Int>(1,2))
private val intListOfThree = FList.of(*arrayOf<Int>(1,2,3))

class FListLawsTest: FunSpec({

    val repeats = Triple(10, 10, 30)

    beforeTest {}

    test("flist functor law") {
        Arb.flist(Arb.int(), repeats.second..repeats.third).checkAll(repeats.first) { fl: IMList<Int> ->
            flistFunctorLaw.identityLaw(fl) shouldBe true
            flistFunctorLaw.associativeLaw(fl, mapInt2String_I, mapString2StrangeDouble, mapDouble2StrangeLong) shouldBe true
        }
        flistFunctorLaw.identityLaw(intListOfNone) shouldBe true
        flistFunctorLaw.associativeLaw(intListOfNone, mapInt2String_I, mapString2StrangeDouble, mapDouble2StrangeLong) shouldBe true

    }

    test("flist applicative law") {
        Arb.flist(Arb.int(), repeats.second..repeats.third).checkAll(repeats.first) { fl: IMList<Int> ->
            val flL: IMList<Long> = fl.fmap { xi -> (xi*4).toLong() / 3L }
            flistApplicativeLaw.identityLaw(fl) shouldBe true
            flistApplicativeLaw.homomorphismLaw(fl, fmapInt2String_I) shouldBe true
            flistApplicativeLaw.liftSymmetryLaw(fl, flL, fmapLong2StrangeInt ) shouldBe true
            flistApplicativeLaw.functorialLaw(fl, mapInt2String_I) shouldBe true
            flistApplicativeLaw.compositionLaw(fl, fmapInt2String_I, flL, fmapLong2StrangeInt ) shouldBe true
        }
        flistApplicativeLaw.identityLaw(intListOfNone) shouldBe true
        flistApplicativeLaw.homomorphismLaw(intListOfNone, fmapInt2String_I) shouldBe true
        flistApplicativeLaw.liftSymmetryLaw(intListOfNone, longListOfNone, fmapLong2StrangeInt ) shouldBe true
        flistApplicativeLaw.functorialLaw(intListOfNone, mapInt2String_I) shouldBe true
        flistApplicativeLaw.compositionLaw(intListOfNone, fmapInt2String_I, longListOfNone, fmapLong2StrangeInt ) shouldBe true
    }

    test("flist applicative composition") {

//        val calc: (Int) -> ( (String) -> ( (Boolean) -> Double ) ) = { i: Int -> { s: String -> { b: Boolean ->  5.0 }}}
//        val appCalc: FMapp<(Int) -> (String) -> (Boolean) -> Double> = IMMappOp.flift2mapp(calc)!!
//        val foo: FMapp<Long> = intListOfOne.appKompose<String, FMap<String>, Boolean>(strListOfOne::fapp).appKompose<Boolean, FMap<Boolean>, Long>(boolListOfOne::fapp)
//        val bar: FMapp<Long> = intListOfOne.fmapply<String,Boolean>(strListOfOne).fmapply(boolListOfOne)
//        val baz: FMapp<FMapp<Boolean>> = intListOfOne.fmApply(strListOfOne).fmApply(boolListOfOne)
//        // bar.fapp { it: IMMapOp<Long, IMCommon<Long>> ->  }
//        // baz.fapp { it: FMap<FMapp<Boolean>> -> TODO() }
//
//                // il1: IMList<Int> -> {  strListOfOne.fapp { sl1: IMList<String> -> boolListOfOne.fapp { bl1 -> appCalc.fapp { it: IMMapOp<(Int) -> (String) -> (Boolean) -> Double, IMCommon<(Int) -> (String) -> (Boolean) -> Double>> ->  } } }}}
//
//        foo.fempty() shouldBe false

//        IMCartesian.flift2kart(intListOfOne)
    }


})