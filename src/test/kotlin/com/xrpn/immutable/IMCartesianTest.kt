package com.xrpn.immutable

import com.xrpn.imapi.*
import com.xrpn.immutable.FKSet.Companion.emptyIMKSet
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

private val intListOfNone = FList.of(*emptyArrayOfInt)
private val intListOfOne = FList.of(*arrayOf<Int>(1))
private val intListOfTwo = FList.of(*arrayOf<Int>(1,2))
private val intListOfThree = FList.of(*arrayOf<Int>(1,2,3))
private val intListOfFour = FList.of(*arrayOf<Int>(1,2,1,3))
private val intListOfFourA = FList.of(*arrayOf<Int>(1,2,2,3))

private val isZp0 = ZW(0,"0")
private val isZp1 = ZW(1,"1")
private val isZp2 = ZW(2,"2")
private val isZp3 = ZW(3,"3")
private val ssZp1 = ZW("1","1")
private val ssZp2 = ZW("2","2")
private val ssZp3 = ZW("3","3")

private val ispListOfNone = FList.of(*arrayOf<IMZPair<Int,String>>())
private val ispListOfOne = FList.of(*arrayOf<IMZPair<Int,String>>(isZp1))
private val ispListOfTwo = FList.of(*arrayOf<IMZPair<Int,String>>(isZp1, isZp2))
private val ispListOfFour = FList.of(*arrayOf<IMZPair<Int,String>>(isZp1,isZp2,isZp1,isZp3))

private val ispStrSet = FKSet.ofs(isZp1,isZp2)

private val strListOfNone = FList.of(*emptyArrayOfStr)
private val strListOfOne = FList.of(*arrayOf<String>("1"))
private val strListOfTwo = FList.of(*arrayOf<String>("1","2"))
private val strListOfThree = FList.of(*arrayOf<String>("1","2","3"))
private val strListOfFour = FList.of(*arrayOf<String>("1","2","1","3"))
private val strListOfFourA = FList.of(*arrayOf<String>("1","2","2","3"))


class IMCartesianTest : FunSpec({

    beforeTest {}

    fun <S: Any, T: Any> lifter(o: IMOrdered<S>): ITKart<S, T> = IMCartesian.flift2kart(o)

    val isITKart0: ITKart<Int, String> by lazy { lifter(intListOfNone) }
    val isITKart1: ITKart<Int, String> by lazy { lifter(intListOfOne) }
    val isITKart2: ITKart<Int, String> by lazy { lifter(intListOfTwo) }
    val isITKart3: ITKart<Int, String> by lazy { lifter(intListOfThree) }
    val isITKart4: ITKart<Int, String> by lazy { lifter(intListOfFour) }
    val isITKart4a: ITKart<Int, String> by lazy { lifter(intListOfFourA) }
    val isITMap0: ITMap<IMZPair<Int, String>> by lazy { isITKart0.mpro(strListOfNone)!! }
    val isITMap1: ITMap<IMZPair<Int, String>> by lazy { isITKart1.mpro(strListOfOne)!! }
    val isITMap2: ITMap<IMZPair<Int, String>> by lazy { isITKart2.mpro(strListOfTwo)!! }
    val isITMap3: ITMap<IMZPair<Int, String>> by lazy { isITKart3.mpro(strListOfThree)!! }
    val isITMap4: ITMap<IMZPair<Int, String>> by lazy { isITKart4.mpro(strListOfFour)!! }
    val isITMap4a: ITMap<IMZPair<Int, String>> by lazy { isITKart4a.mpro(strListOfFourA)!! }

    test("sanity") {
        val aut0: ITKart<Int, String> = lifter(intListOfNone)
        aut0 shouldNotBe null
        aut0.mpro(strListOfNone)?.fempty() shouldBe true
        aut0.opro(strListOfNone)?.fempty() shouldBe true
        aut0.mpro(strListOfOne)?.fempty() shouldBe true
        aut0.opro(strListOfOne)?.fempty() shouldBe true

        val aut1: ITKart<Int, String> = lifter(intListOfOne)
        aut1 shouldNotBe null
        aut1.mpro(strListOfNone)?.fempty() shouldBe true
        aut1.opro(strListOfNone)?.fempty() shouldBe true
        aut1.mpro(strListOfOne)?.softEqual(FLCons(isZp1, FLNil)) shouldBe true
        aut1.opro(strListOfOne)?.softEqual(FLCons(isZp1, FLNil)) shouldBe true
        aut1.mpro(strListOfTwo)?.softEqual(FLCons(isZp1, FLNil)) shouldBe true
        aut1.opro(strListOfTwo)?.softEqual(FLCons(isZp1, FLNil)) shouldBe true

        val aut3: ITKart<Int, String> = lifter(intListOfThree)
        aut3 shouldNotBe null
        aut3.mpro(strListOfNone)?.fempty() shouldBe true
        aut3.opro(strListOfNone)?.fempty() shouldBe true
        aut3.mpro(strListOfTwo)?.softEqual(FList.of(isZp1, isZp2)) shouldBe true
        aut3.opro(strListOfTwo)?.softEqual(FList.of(isZp1, isZp2)) shouldBe true
        aut3.mpro(strListOfThree)?.softEqual(FList.of(isZp1, isZp2, isZp3)) shouldBe true
        aut3.opro(strListOfThree)?.softEqual(FList.of(isZp1, isZp2, isZp3)) shouldBe true

        val aut3a: ITKart<String, String> = lifter(strListOfThree)
        aut3a shouldNotBe null
        aut3a.mpro(strListOfNone)?.fempty() shouldBe true
        aut3a.opro(strListOfNone)?.fempty() shouldBe true
        aut3a.mpro(strListOfTwo)?.softEqual(FList.of(ssZp1, ssZp2)) shouldBe true
        aut3a.opro(strListOfTwo)?.softEqual(FList.of(ssZp1, ssZp2)) shouldBe true
        aut3a.mpro(strListOfThree)?.softEqual(FList.of(ssZp1, ssZp2, ssZp3)) shouldBe true
        aut3a.opro(strListOfThree)?.softEqual(FList.of(ssZp1, ssZp2, ssZp3)) shouldBe true
    }

    test("fall") {
        isITMap0.fall { true } shouldBe true
        isITMap0.fall { false } shouldBe true
        isITMap3.fall { it._1() > 0 } shouldBe true
        isITMap3.fall { it._1() > 1 } shouldBe false
        isITMap3.fall { it._2() > "0" } shouldBe true
        isITMap3.fall { it._2() > "1" } shouldBe false
    }

    test("fany") {
        isITMap0.fany { true } shouldBe true
        isITMap0.fany { false } shouldBe true
        isITMap3.fany { it._1() > 3 } shouldBe false
        isITMap3.fany { it._1() > 1 } shouldBe true
    }

    test("fcontains") {
        isITMap0.fcontains(isZp1) shouldBe false

        isITMap1.fcontains(isZp0) shouldBe false
        isITMap1.fcontains(isZp1) shouldBe true
        isITMap2.fcontains(isZp0) shouldBe false
        isITMap2.fcontains(isZp1) shouldBe true
        isITMap2.fcontains(isZp2) shouldBe true
        isITMap3.fcontains(isZp0) shouldBe false
        isITMap3.fcontains(isZp1) shouldBe true
        isITMap3.fcontains(isZp3) shouldBe true
    }

    test("fcount") {
        isITMap0.fcount { _ -> true } shouldBe 0
        isITMap0.fcount { _ -> false } shouldBe 0
        isITMap1.fcount { _ -> true } shouldBe 1
        isITMap1.fcount { 0 < it._1() } shouldBe 1
        isITMap1.fcount { it._1() < 0 } shouldBe 0
        isITMap1.fcount { _ -> false } shouldBe 0
        isITMap2.fcount { _ -> true } shouldBe 2
        isITMap2.fcount { 0 < it._1() } shouldBe 2
        isITMap2.fcount { 1 < it._1() } shouldBe 1
        isITMap2.fcount { it._1() < 0 } shouldBe 0
        isITMap2.fcount { _ -> false } shouldBe 0
    }

    test("fdropAll") {
        isITMap0.fdropAll(ispListOfNone).fempty() shouldBe true
        (isITMap0.fdropAll(ispListOfNone) === FCartesian.emptyZMap<Int,String>().asMap()) shouldBe true
        isITMap1.fdropAll(ispListOfNone) shouldBe ispListOfOne
        isITMap0.fdropAll(ispListOfOne).fempty() shouldBe true
        (isITMap0.fdropAll(ispListOfOne) === FCartesian.emptyZMap<Int,String>().asMap()) shouldBe true
        isITMap1.fdropAll(FLNil) shouldBe ispListOfOne
        isITMap0.fdropAll(ispListOfTwo).fempty() shouldBe true
        (isITMap0.fdropAll(ispListOfTwo) === FCartesian.emptyZMap<Int,String>().asMap()) shouldBe true
        isITMap4.fdropAll(ispStrSet) shouldBe FList.of(*arrayOf(isZp3))
        isITMap4.fdropAll(emptyIMKSet()) shouldBe ispListOfFour
        isITMap4a.fdropAll(ispStrSet) shouldBe FList.of(*arrayOf(isZp3))
        isITMap4.fdropAll(ispListOfTwo) shouldBe FList.of(*arrayOf(isZp3))
        isITMap4a.fdropAll(ispListOfTwo) shouldBe FList.of(*arrayOf(isZp3))
    }

    test("fdropItem") {
        isITMap0.fdropItem(isZp0).fempty() shouldBe true
        (isITMap0.fdropItem(isZp0) === FCartesian.emptyZMap<Int,String>().asMap()) shouldBe true
        isITMap1.fdropItem(isZp0) shouldBe ispListOfOne
        isITMap1.fdropItem(isZp1).fempty() shouldBe true
        (isITMap1.fdropItem(isZp1) === FCartesian.emptyZMap<Int,String>().asMap()) shouldBe true
        isITMap1.fdropItem(isZp2) shouldBe ispListOfOne
    }

    test("fdropWhen") {
        isITMap0.fdropWhen { it._1() > 1 }.fempty() shouldBe true
        isITMap1.fdropWhen { it._1() > 1 } shouldBe FLCons(isZp1, FLNil)
        isITMap4.fdropWhen { it._1() > 1 } shouldBe FLCons(isZp1, FLCons(isZp1, FLNil))
        isITMap4.fdropWhen { false } shouldBe ispListOfFour
        isITMap4.fdropWhen { true }.fempty() shouldBe true
        isITMap4a.fdropWhen { it._1() < 2 } shouldBe FLCons(isZp2, FLCons(isZp2, FLCons(isZp3, FLNil)))
        isITMap4a.fdropWhen { it._1() < 3 } shouldBe FLCons(isZp3, FLNil)
    }

    test("fempty") {
        isITMap0.fempty() shouldBe true
        (isITMap0 === FCartesian.emptyZMap<Int,String>().asMap()) shouldBe true
        isITMap1.fempty() shouldBe false
    }

    test("ffilter") {
        isITMap0.ffilter {0 == it._1() % 2}.fempty() shouldBe true
        (isITMap0.ffilter {0 == it._1() % 2} === FCartesian.emptyZMap<Int,String>().asMap()) shouldBe true
        isITMap1.ffilter {0 == it._1() % 2}.fempty() shouldBe true
        (isITMap1.ffilter {0 == it._1() % 2} === FCartesian.emptyZMap<Int,String>().asMap()) shouldBe true
        isITMap1.ffilter {true} shouldBe ispListOfOne
        isITMap2.ffilter {0 == it._1() % 2} shouldBe FLCons(isZp2,FLNil)
        isITMap2.ffilter {true} shouldBe ispListOfTwo
        isITMap2.ffilter {false}.fempty() shouldBe true
        isITMap3.ffilter {0 == it._1() % 2} shouldBe FLCons(isZp2,FLNil)
    }

    test("ffilterNot") {
        isITMap0.ffilterNot {0 == it._1() % 2}.fempty() shouldBe true
        (isITMap0.ffilterNot {0 == it._1() % 2} === FCartesian.emptyZMap<Int,String>().asMap()) shouldBe true
        isITMap1.ffilterNot {0 == it._1() % 2} shouldBe FLCons(isZp1,FLNil)
        isITMap1.ffilterNot {false} shouldBe ispListOfOne
        isITMap1.ffilterNot {true}.fempty() shouldBe true
        isITMap2.ffilterNot {0 == it._1() % 2} shouldBe FLCons(isZp1,FLNil)
        isITMap3.ffilterNot {0 == it._1() % 2} shouldBe FLCons(isZp1,FLCons(isZp3,FLNil))
    }

    test("ffindAny") {
        isITMap0.ffindAny { true } shouldBe null
        isITMap0.ffindAny { false } shouldBe null

        isITMap1.ffindAny { it._1() == 0 } shouldBe null
        isITMap1.ffindAny { it._1() == 1 } shouldBe isZp1
        isITMap2.ffindAny { it._1() == 0 } shouldBe null
        isITMap2.ffindAny { it._1() == 1 } shouldBe isZp1
        isITMap2.ffindAny { it._1() == 2 } shouldBe isZp2
        isITMap3.ffindAny { it._1() == 0 } shouldBe null
        isITMap3.ffindAny { it._1() == 1 } shouldBe isZp1
        isITMap3.ffindAny { it._1() == 3 } shouldBe isZp3
    }

    test("fisStrict") {
        isITMap0.fisStrict() shouldBe true
        isITMap3.fisStrict() shouldBe true
    }

    test("fnone") {
        isITMap0.fnone { true } shouldBe true
        isITMap0.fnone { false } shouldBe true
        isITMap1.fnone { false } shouldBe true
        isITMap1.fnone { true } shouldBe false
        isITMap2.fnone { it._1() == 1 } shouldBe false
        isITMap2.fnone { it._1() > 10 } shouldBe true
    }

    test("fpick") {
        isITMap0.fpick() shouldBe null
        isITMap1.fpick()?.let { it::class } shouldBe ZW::class
        (IMZPair::class).isInstance(isITMap1.fpick()?.let { it }) shouldBe true
    }

    test("fpickNotEmpty") {
        isITMap0.fpickNotEmpty() shouldBe null
        isITMap1.fpickNotEmpty()?.let { it::class } shouldBe ZW::class
    }

    test ("fpopAndReminder") {
        val (pop1, reminder1) = isITMap0.fpopAndRemainder()
        pop1 shouldBe null
        reminder1.fempty() shouldBe true
        val (pop2, reminder2) = isITMap1.fpopAndRemainder()
        pop2 shouldBe isITMap1.fpick()
        reminder2.fempty() shouldBe true
        val (pop3, reminder3) = isITMap2.fpopAndRemainder()
        pop3 shouldBe isITMap1.fpick()
        reminder3.equals(FLCons(isZp2, FLNil)) shouldBe true
        val (pop4, reminder4) = isITMap3.fpopAndRemainder()
        pop4 shouldBe isITMap1.fpick()
        reminder4.equals(FLCons(isZp2, FLCons(isZp3, FLNil))) shouldBe true
    }

    test("fsize") {
        isITMap0.fsize() shouldBe 0
        isITMap1.fsize() shouldBe 1
        isITMap2.fsize() shouldBe 2
        isITMap3.fsize() shouldBe 3
    }

    test("fisNested") {
        isITMap0.fisNested() shouldBe null
        isITMap1.fisNested() shouldBe false
        isITMap2.fisNested() shouldBe false
    }

})