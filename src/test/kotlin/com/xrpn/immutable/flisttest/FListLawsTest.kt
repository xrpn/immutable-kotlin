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
private val strListOfNone = FList.of(*emptyArrayOfStr)
private val boolListOfNone = FList.of(*arrayOf<Boolean>())
private val intListOfOne = FList.of(*arrayOf<Int>(1))
private val longListOfOne = FList.of(*arrayOf<Long>(10L))
private val strListOfOne = FList.of(*arrayOf<String>("A"))
private val boolListOfOne = FList.of(*arrayOf<Boolean>(true))
private val intListOfTwo = FList.of(*arrayOf<Int>(1,2))
private val longListOfTwo = FList.of(*arrayOf<Long>(10L, 20L))
private val strListOfTwo = FList.of(*arrayOf<String>("A", "B"))
private val boolListOfTwo = FList.of(*arrayOf<Boolean>(true, true))
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

    test("flist cartesian law") {}

})