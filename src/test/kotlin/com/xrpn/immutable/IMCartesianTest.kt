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
private val intSet = FKSet.ofs(Pair(1,"1"),Pair(2,"2"))

private val ispListOfNone = FList.of(*arrayOf<Pair<Int,String>>())
private val ispListOfOne = FList.of(*arrayOf<Pair<Int,String>>(Pair(1,"1")))
private val ispListOfTwo = FList.of(*arrayOf<Pair<Int,String>>(Pair(1,"1"),Pair(2,"2")))
private val ispListOfFour = FList.of(*arrayOf<Pair<Int,String>>(Pair(1,"1"),Pair(2,"2"),Pair(1,"1"),Pair(3,"3")))

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
    val isITMap0: ITMap<Pair<Int, String>> by lazy { isITKart0.mpro(strListOfNone)!! }
    val isITMap1: ITMap<Pair<Int, String>> by lazy { isITKart1.mpro(strListOfOne)!! }
    val isITMap2: ITMap<Pair<Int, String>> by lazy { isITKart2.mpro(strListOfTwo)!! }
    val isITMap3: ITMap<Pair<Int, String>> by lazy { isITKart3.mpro(strListOfThree)!! }
    val isITMap4: ITMap<Pair<Int, String>> by lazy { isITKart4.mpro(strListOfFour)!! }
    val isITMap4a: ITMap<Pair<Int, String>> by lazy { isITKart4a.mpro(strListOfFourA)!! }

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
        aut1.mpro(strListOfOne)?.equals(FLCons(Pair(1,"1"), FLNil)) shouldBe true
        aut1.opro(strListOfOne)?.equals(FLCons(Pair(1,"1"), FLNil)) shouldBe true
        aut1.mpro(strListOfTwo)?.equals(FLCons(Pair(1,"1"), FLNil)) shouldBe true
        aut1.opro(strListOfTwo)?.equals(FLCons(Pair(1,"1"), FLNil)) shouldBe true

        val aut3: ITKart<Int, String> = lifter(intListOfThree)
        aut3 shouldNotBe null
        aut3.mpro(strListOfNone)?.fempty() shouldBe true
        aut3.opro(strListOfNone)?.fempty() shouldBe true
        aut3.mpro(strListOfTwo)?.equals(FList.of(Pair(1,"1"), Pair(2,"2"))) shouldBe true
        aut3.opro(strListOfTwo)?.equals(FList.of(Pair(1,"1"), Pair(2,"2"))) shouldBe true
        aut3.mpro(strListOfThree)?.equals(FList.of(Pair(1,"1"), Pair(2,"2"), Pair(3,"3"))) shouldBe true
        aut3.opro(strListOfThree)?.equals(FList.of(Pair(1,"1"), Pair(2,"2"), Pair(3,"3"))) shouldBe true

        val aut3a: ITKart<String, String> = lifter(strListOfThree)
        aut3a shouldNotBe null
        aut3a.mpro(strListOfNone)?.fempty() shouldBe true
        aut3a.opro(strListOfNone)?.fempty() shouldBe true
        aut3a.mpro(strListOfTwo)?.equals(FList.of(Pair("1","1"), Pair("2","2"))) shouldBe true
        aut3a.opro(strListOfTwo)?.equals(FList.of(Pair("1","1"), Pair("2","2"))) shouldBe true
        aut3a.mpro(strListOfThree)?.equals(FList.of(Pair("1","1"), Pair("2","2"), Pair("3","3"))) shouldBe true
        aut3a.opro(strListOfThree)?.equals(FList.of(Pair("1","1"), Pair("2","2"), Pair("3","3"))) shouldBe true
    }

    test("fall") {
        isITMap0.fall { true } shouldBe true
        isITMap0.fall { false } shouldBe true
        isITMap3.fall { it.first > 0 } shouldBe true
        isITMap3.fall { it.first > 1 } shouldBe false
        isITMap3.fall { it.second > "0" } shouldBe true
        isITMap3.fall { it.second > "1" } shouldBe false
    }

    test("fany") {
        isITMap0.fany { true } shouldBe true
        isITMap0.fany { false } shouldBe true
        isITMap3.fany { it.first > 3 } shouldBe false
        isITMap3.fany { it.first > 1 } shouldBe true
    }

    test("fcontains") {
        isITMap0.fcontains(Pair(1,"1")) shouldBe false

        isITMap1.fcontains(Pair(0,"0")) shouldBe false
        isITMap1.fcontains(Pair(1,"1")) shouldBe true
        isITMap2.fcontains(Pair(0,"0")) shouldBe false
        isITMap2.fcontains(Pair(1,"1")) shouldBe true
        isITMap2.fcontains(Pair(2,"2")) shouldBe true
        isITMap3.fcontains(Pair(0,"0")) shouldBe false
        isITMap3.fcontains(Pair(1,"1")) shouldBe true
        isITMap3.fcontains(Pair(3,"3")) shouldBe true
    }

    test("fcount") {
        isITMap0.fcount { _ -> true } shouldBe 0
        isITMap0.fcount { _ -> false } shouldBe 0
        isITMap1.fcount { _ -> true } shouldBe 1
        isITMap1.fcount { 0 < it.first } shouldBe 1
        isITMap1.fcount { it.first < 0 } shouldBe 0
        isITMap1.fcount { _ -> false } shouldBe 0
        isITMap2.fcount { _ -> true } shouldBe 2
        isITMap2.fcount { 0 < it.first } shouldBe 2
        isITMap2.fcount { 1 < it.first } shouldBe 1
        isITMap2.fcount { it.first < 0 } shouldBe 0
        isITMap2.fcount { _ -> false } shouldBe 0
    }

    test("fdropAll") {
        isITMap0.fdropAll(ispListOfNone) shouldBe FLNil
        isITMap1.fdropAll(ispListOfNone) shouldBe ispListOfOne
        isITMap1.fdropAll(ispListOfOne) shouldBe FLNil
        isITMap1.fdropAll(FLNil) shouldBe ispListOfOne
        isITMap1.fdropAll(ispListOfTwo) shouldBe FLNil
        isITMap4.fdropAll(intSet) shouldBe FList.of(*arrayOf(Pair(3,"3")))
        isITMap4.fdropAll(emptyIMKSet()) shouldBe ispListOfFour
        isITMap4a.fdropAll(intSet) shouldBe FList.of(*arrayOf(Pair(3,"3")))
        isITMap4.fdropAll(ispListOfTwo) shouldBe FList.of(*arrayOf(Pair(3,"3")))
        isITMap4a.fdropAll(ispListOfTwo) shouldBe FList.of(*arrayOf(Pair(3,"3")))
    }

    test("fdropItem") {
        isITMap0.fdropItem(Pair(0,"0")) shouldBe FLNil
        isITMap1.fdropItem(Pair(0,"0")) shouldBe ispListOfOne
        isITMap1.fdropItem(Pair(1,"1")) shouldBe FLNil
        isITMap1.fdropItem(Pair(2,"2")) shouldBe ispListOfOne
    }

    test("fdropWhen") {
        isITMap0.fdropWhen { it.first > 1 }.fempty() shouldBe true
        isITMap1.fdropWhen { it.first > 1 } shouldBe FLCons(Pair(1,"1"), FLNil)
        isITMap4.fdropWhen { it.first > 1 } shouldBe FLCons(Pair(1,"1"), FLCons(Pair(1,"1"), FLNil))
        isITMap4.fdropWhen { false } shouldBe ispListOfFour
        isITMap4.fdropWhen { true }.fempty() shouldBe true
        isITMap4a.fdropWhen { it.first < 2 } shouldBe FLCons(Pair(2,"2"), FLCons(Pair(2,"2"), FLCons(Pair(3,"3"), FLNil)))
        isITMap4a.fdropWhen { it.first < 3 } shouldBe FLCons(Pair(3,"3"), FLNil)
    }

    test("fempty") {
        isITMap0.fempty() shouldBe true
        isITMap1.fempty() shouldBe false
    }

    test("ffilter") {
        isITMap0.ffilter {0 == it.first % 2} shouldBe FLNil
        isITMap1.ffilter {0 == it.first % 2} shouldBe FLNil
        isITMap1.ffilter {true} shouldBe ispListOfOne
        isITMap2.ffilter {0 == it.first % 2} shouldBe FLCons(Pair(2,"2"),FLNil)
        isITMap2.ffilter {true} shouldBe ispListOfTwo
        isITMap2.ffilter {false}.fempty() shouldBe true
        isITMap3.ffilter {0 == it.first % 2} shouldBe FLCons(Pair(2,"2"),FLNil)
    }

    test("ffilterNot") {
        isITMap0.ffilterNot {0 == it.first % 2} shouldBe FLNil
        isITMap1.ffilterNot {0 == it.first % 2} shouldBe FLCons(Pair(1,"1"),FLNil)
        isITMap1.ffilterNot {false} shouldBe ispListOfOne
        isITMap1.ffilterNot {true}.fempty() shouldBe true
        isITMap2.ffilterNot {0 == it.first % 2} shouldBe FLCons(Pair(1,"1"),FLNil)
        isITMap3.ffilterNot {0 == it.first % 2} shouldBe FLCons(Pair(1,"1"),FLCons(Pair(3,"3"),FLNil))
    }

    test("ffindAny") {
        isITMap0.ffindAny { true } shouldBe null
        isITMap0.ffindAny { false } shouldBe null

        isITMap1.ffindAny { it.first == 0 } shouldBe null
        isITMap1.ffindAny { it.first == 1 } shouldBe Pair(1,"1")
        isITMap2.ffindAny { it.first == 0 } shouldBe null
        isITMap2.ffindAny { it.first == 1 } shouldBe Pair(1,"1")
        isITMap2.ffindAny { it.first == 2 } shouldBe Pair(2,"2")
        isITMap3.ffindAny { it.first == 0 } shouldBe null
        isITMap3.ffindAny { it.first == 1 } shouldBe Pair(1,"1")
        isITMap3.ffindAny { it.first == 3 } shouldBe Pair(3,"3")
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
        isITMap2.fnone { it.first == 1 } shouldBe false
        isITMap2.fnone { it.first > 10 } shouldBe true
    }

    test("fpick") {
        isITMap0.fpick() shouldBe null
        isITMap1.fpick()?.let { it::class } shouldBe Pair::class
    }

    test("fpickNotEmpty") {
        isITMap0.fpickNotEmpty() shouldBe null
        isITMap1.fpickNotEmpty()?.let { it::class } shouldBe Pair::class
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
        reminder3.equals(FLCons(Pair(2,"2"), FLNil)) shouldBe true
        val (pop4, reminder4) = isITMap3.fpopAndRemainder()
        pop4 shouldBe isITMap1.fpick()
        reminder4.equals(FLCons(Pair(2,"2"), FLCons(Pair(3,"3"), FLNil))) shouldBe true
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