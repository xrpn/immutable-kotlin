package com.xrpn.immutable.flisttest

import com.xrpn.imapi.IMList
import com.xrpn.imapi.IMOrdered
import com.xrpn.immutable.*
import com.xrpn.immutable.FList.Companion.emptyIMList
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.flist

private val intListOfNone = FList.of(*emptyArrayOfInt)
private val intListOfOne = FList.of(*arrayOf<Int>(1))
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

//    test("flist functor copmosition") {
//        val i2d: FList<(Int) -> Double> = emptyIMList<Int>().fliftf(mapInt2String_I).fmap{ it: (Int) -> String -> mapString2StrangeDouble kompose it }
//        flistFunctorLaw.identityLaw(i2d) shouldBe true
//        val d2s: FList<(Double) -> String> = emptyIMList<Double>().fliftf(mapDouble2StrangeLong).fmap { it: (Double) -> Long -> mapLong2String_L kompose it }
//        flistFunctorLaw.identityLaw(d2s) shouldBe true
//        val s2l: FList<(String) -> Long> = emptyIMList<String>().fliftf(mapString2HashLong)
//        flistFunctorLaw.identityLaw(d2s) shouldBe true
//        val start: FList<(String) -> Int> = emptyIMList<String>().fliftf(mapString2StrangeInt)
//
//        flistFunctorLaw.associativeLaw(start, i2d , d2s, s2l) shouldBe true
//
//
//    }

})