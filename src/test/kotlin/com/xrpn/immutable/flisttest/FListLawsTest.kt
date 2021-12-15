package com.xrpn.immutable.flisttest

import com.xrpn.imapi.*
import com.xrpn.immutable.*
import com.xrpn.immutable.FList.Companion.emptyIMList
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.*
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

    test("flist cartesian law") {
        val empty: ITMap<IMZPair<Any, Any>> = FCartesian.emptyZipMap()
        val zempty = IMCartesian.asZMap(empty)!!
        Arb.triple(
            Arb.flist(Arb.int(), repeats.second..repeats.third),
            Arb.flist(Arb.string(3..7), repeats.second..repeats.third),
            Arb.flist(Arb.double(), repeats.second..repeats.third)
        ).checkAll(repeats.first, PropTestConfig(seed = 6389489730542018457)) { (fil,fsl,fll) ->
            val zm1: ITMap<IMZPair<Int, String>> = fil mapWith fsl
            val zmap1: ITZMap<Int, String> = IMCartesian.asZMap(zm1)!!

            val zm2: ITMap<IMZPair<Int, String>> = fsl mapWith fil
            val zmap2 = IMCartesian.asZMap(zm2)!!

            flistCartesianLaw.zidentityLaw(zmap1) shouldBe true
            flistCartesianLaw.zidentitypLaw(zmap1) shouldBe true
            flistCartesianLaw.zidentityLaw(zmap2) shouldBe true
            flistCartesianLaw.zidentitypLaw(zmap2) shouldBe true

            flistCartesianLaw.kidentity(zmap1, zmap2) shouldBe true
            flistCartesianLaw.kidentityp(zmap1, zmap2) shouldBe true
            flistCartesianLaw.kidentity(zmap2, zmap1) shouldBe true
            flistCartesianLaw.kidentityp(zmap2, zmap1) shouldBe true
            flistCartesianLaw.kidentity(zempty, zmap2) shouldBe true
            flistCartesianLaw.kidentityp(zmap1, zempty) shouldBe true

            flistCartesianLaw.zassociativeLaw(fil,fsl,fll) shouldBe true
            flistCartesianLaw.zassociativeLaw(emptyIMList(),fsl,fll) shouldBe true
            flistCartesianLaw.zassociativeLaw(fil,emptyIMList(),fll) shouldBe true
            flistCartesianLaw.zassociativeLaw(fil,fsl,emptyIMList()) shouldBe true
            flistCartesianLaw.zassociativeLaw2(fil,fsl,fil,fll) shouldBe true
            flistCartesianLaw.zassociativeLaw2(emptyIMList(),fsl,fil,fll) shouldBe true
            flistCartesianLaw.zassociativeLaw2(fil,emptyIMList(),fil,fll) shouldBe true
            flistCartesianLaw.zassociativeLaw2(fil,fsl,emptyIMList(),fll) shouldBe true
            flistCartesianLaw.zassociativeLaw2(fil,fsl,fil,emptyIMList()) shouldBe true
            flistCartesianLaw.zassociativeLaw3(fil,fsl,fil,fll,fsl) shouldBe true
            flistCartesianLaw.zassociativeLaw3(emptyIMList(),fsl,fil,fll,fsl) shouldBe true
            flistCartesianLaw.zassociativeLaw3(fil,emptyIMList(),fil,fll,fsl) shouldBe true
            flistCartesianLaw.zassociativeLaw3(fil,fsl,emptyIMList(),fll,fsl) shouldBe true
            flistCartesianLaw.zassociativeLaw3(fil,fsl,fil,emptyIMList(),fsl) shouldBe true
            flistCartesianLaw.zassociativeLaw3(fil,fsl,fil,fll,emptyIMList()) shouldBe true
       }
       flistCartesianLaw.zidentityLaw(zempty) shouldBe true
       flistCartesianLaw.zidentitypLaw(zempty) shouldBe true
       flistCartesianLaw.kidentity(zempty, zempty) shouldBe true
       flistCartesianLaw.kidentityp(zempty, zempty) shouldBe true
    }

})