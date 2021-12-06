package com.xrpn.immutable

import com.xrpn.imapi.FKart
import com.xrpn.imapi.FMap
import com.xrpn.imapi.IMCartesian
import com.xrpn.imapi.IMOrdered
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

    fun <S: Any, T: Any> lifter(o: IMOrdered<S>): FKart<S, T> = IMCartesian.flift2kart(o)

    val isFKart0: FKart<Int, String> by lazy { lifter(intListOfNone) }
    val isFKart1: FKart<Int, String> by lazy { lifter(intListOfOne) }
    val isFKart2: FKart<Int, String> by lazy { lifter(intListOfTwo) }
    val isFKart3: FKart<Int, String> by lazy { lifter(intListOfThree) }
    val isFKart4: FKart<Int, String> by lazy { lifter(intListOfFour) }
    val isFKart4a: FKart<Int, String> by lazy { lifter(intListOfFourA) }
    val isFMap0: FMap<Pair<Int, String>> by lazy { isFKart0.mpro(strListOfNone)!! }
    val isFMap1: FMap<Pair<Int, String>> by lazy { isFKart1.mpro(strListOfOne)!! }
    val isFMap2: FMap<Pair<Int, String>> by lazy { isFKart2.mpro(strListOfTwo)!! }
    val isFMap3: FMap<Pair<Int, String>> by lazy { isFKart3.mpro(strListOfThree)!! }
    val isFMap4: FMap<Pair<Int, String>> by lazy { isFKart4.mpro(strListOfFour)!! }
    val isFMap4a: FMap<Pair<Int, String>> by lazy { isFKart4a.mpro(strListOfFourA)!! }

    test("sanity") {
        val aut0: FKart<Int, String> = lifter(intListOfNone)
        aut0 shouldNotBe null
        aut0.mpro(strListOfNone)?.fempty() shouldBe true
        aut0.opro(strListOfNone)?.fempty() shouldBe true
        aut0.mpro(strListOfOne)?.fempty() shouldBe true
        aut0.opro(strListOfOne)?.fempty() shouldBe true

        val aut1: FKart<Int, String> = lifter(intListOfOne)
        aut1 shouldNotBe null
        aut1.mpro(strListOfNone)?.fempty() shouldBe true
        aut1.opro(strListOfNone)?.fempty() shouldBe true
        aut1.mpro(strListOfOne)?.equals(FLCons(Pair(1,"1"), FLNil)) shouldBe true
        aut1.opro(strListOfOne)?.equals(FLCons(Pair(1,"1"), FLNil)) shouldBe true
        aut1.mpro(strListOfTwo) shouldBe null
        aut1.opro(strListOfTwo) shouldBe null

        val aut3: FKart<Int, String> = lifter(intListOfThree)
        aut3 shouldNotBe null
        aut3.mpro(strListOfNone)?.fempty() shouldBe true
        aut3.opro(strListOfNone)?.fempty() shouldBe true
        aut3.mpro(strListOfTwo) shouldBe null
        aut3.opro(strListOfTwo) shouldBe null
        aut3.mpro(strListOfThree)?.equals(FList.of(Pair(1,"1"), Pair(2,"2"), Pair(3,"3"))) shouldBe true
        aut3.opro(strListOfThree)?.equals(FList.of(Pair(1,"1"), Pair(2,"2"), Pair(3,"3"))) shouldBe true

        val aut3a: FKart<String, String> = lifter(strListOfThree)
        aut3a shouldNotBe null
        aut3a.mpro(strListOfNone)?.fempty() shouldBe true
        aut3a.opro(strListOfNone)?.fempty() shouldBe true
        aut3a.mpro(strListOfTwo) shouldBe null
        aut3a.opro(strListOfTwo) shouldBe null
        aut3a.mpro(strListOfThree)?.equals(FList.of(Pair("1","1"), Pair("2","2"), Pair("3","3"))) shouldBe true
        aut3a.opro(strListOfThree)?.equals(FList.of(Pair("1","1"), Pair("2","2"), Pair("3","3"))) shouldBe true
    }

    test("fall") {
        isFMap0.fall { true } shouldBe true
        isFMap0.fall { false } shouldBe true
        isFMap3.fall { it.first > 0 } shouldBe true
        isFMap3.fall { it.first > 1 } shouldBe false
        isFMap3.fall { it.second > "0" } shouldBe true
        isFMap3.fall { it.second > "1" } shouldBe false
    }

    test("fany") {
        isFMap0.fany { true } shouldBe true
        isFMap0.fany { false } shouldBe true
        isFMap3.fany { it.first > 3 } shouldBe false
        isFMap3.fany { it.first > 1 } shouldBe true
    }

    test("fcontains") {
        isFMap0.fcontains(Pair(1,"1")) shouldBe false

        isFMap1.fcontains(Pair(0,"0")) shouldBe false
        isFMap1.fcontains(Pair(1,"1")) shouldBe true
        isFMap2.fcontains(Pair(0,"0")) shouldBe false
        isFMap2.fcontains(Pair(1,"1")) shouldBe true
        isFMap2.fcontains(Pair(2,"2")) shouldBe true
        isFMap3.fcontains(Pair(0,"0")) shouldBe false
        isFMap3.fcontains(Pair(1,"1")) shouldBe true
        isFMap3.fcontains(Pair(3,"3")) shouldBe true
    }

    test("fcount") {
        isFMap0.fcount { _ -> true } shouldBe 0
        isFMap0.fcount { _ -> false } shouldBe 0
        isFMap1.fcount { _ -> true } shouldBe 1
        isFMap1.fcount { 0 < it.first } shouldBe 1
        isFMap1.fcount { it.first < 0 } shouldBe 0
        isFMap1.fcount { _ -> false } shouldBe 0
        isFMap2.fcount { _ -> true } shouldBe 2
        isFMap2.fcount { 0 < it.first } shouldBe 2
        isFMap2.fcount { 1 < it.first } shouldBe 1
        isFMap2.fcount { it.first < 0 } shouldBe 0
        isFMap2.fcount { _ -> false } shouldBe 0
    }

    test("fdropAll") {
        isFMap0.fdropAll(ispListOfNone) shouldBe FLNil
        isFMap1.fdropAll(ispListOfNone) shouldBe ispListOfOne
        isFMap1.fdropAll(ispListOfOne) shouldBe FLNil
        isFMap1.fdropAll(FLNil) shouldBe ispListOfOne
        isFMap1.fdropAll(ispListOfTwo) shouldBe FLNil
        isFMap4.fdropAll(intSet) shouldBe FList.of(*arrayOf(Pair(3,"3")))
        isFMap4.fdropAll(emptyIMKSet()) shouldBe ispListOfFour
        isFMap4a.fdropAll(intSet) shouldBe FList.of(*arrayOf(Pair(3,"3")))
        isFMap4.fdropAll(ispListOfTwo) shouldBe FList.of(*arrayOf(Pair(3,"3")))
        isFMap4a.fdropAll(ispListOfTwo) shouldBe FList.of(*arrayOf(Pair(3,"3")))
    }

    test("fdropItem") {
        isFMap0.fdropItem(Pair(0,"0")) shouldBe FLNil
        isFMap1.fdropItem(Pair(0,"0")) shouldBe ispListOfOne
        isFMap1.fdropItem(Pair(1,"1")) shouldBe FLNil
        isFMap1.fdropItem(Pair(2,"2")) shouldBe ispListOfOne
    }

    test("fdropWhen") {
        isFMap0.fdropWhen { it.first > 1 }.fempty() shouldBe true
        isFMap1.fdropWhen { it.first > 1 } shouldBe FLCons(Pair(1,"1"), FLNil)
        isFMap4.fdropWhen { it.first > 1 } shouldBe FLCons(Pair(1,"1"), FLCons(Pair(1,"1"), FLNil))
        isFMap4.fdropWhen { false } shouldBe ispListOfFour
        isFMap4.fdropWhen { true }.fempty() shouldBe true
        isFMap4a.fdropWhen { it.first < 2 } shouldBe FLCons(Pair(2,"2"), FLCons(Pair(2,"2"), FLCons(Pair(3,"3"), FLNil)))
        isFMap4a.fdropWhen { it.first < 3 } shouldBe FLCons(Pair(3,"3"), FLNil)
    }

    test("fempty") {
        isFMap0.fempty() shouldBe true
        isFMap1.fempty() shouldBe false
    }

    test("ffilter") {
        isFMap0.ffilter {0 == it.first % 2} shouldBe FLNil
        isFMap1.ffilter {0 == it.first % 2} shouldBe FLNil
        isFMap1.ffilter {true} shouldBe ispListOfOne
        isFMap2.ffilter {0 == it.first % 2} shouldBe FLCons(Pair(2,"2"),FLNil)
        isFMap2.ffilter {true} shouldBe ispListOfTwo
        isFMap2.ffilter {false}.fempty() shouldBe true
        isFMap3.ffilter {0 == it.first % 2} shouldBe FLCons(Pair(2,"2"),FLNil)
    }

    test("ffilterNot") {
        isFMap0.ffilterNot {0 == it.first % 2} shouldBe FLNil
        isFMap1.ffilterNot {0 == it.first % 2} shouldBe FLCons(Pair(1,"1"),FLNil)
        isFMap1.ffilterNot {false} shouldBe ispListOfOne
        isFMap1.ffilterNot {true}.fempty() shouldBe true
        isFMap2.ffilterNot {0 == it.first % 2} shouldBe FLCons(Pair(1,"1"),FLNil)
        isFMap3.ffilterNot {0 == it.first % 2} shouldBe FLCons(Pair(1,"1"),FLCons(Pair(3,"3"),FLNil))
    }

    test("ffindAny") {
        isFMap0.ffindAny { true } shouldBe null
        isFMap0.ffindAny { false } shouldBe null

        isFMap1.ffindAny { it.first == 0 } shouldBe null
        isFMap1.ffindAny { it.first == 1 } shouldBe Pair(1,"1")
        isFMap2.ffindAny { it.first == 0 } shouldBe null
        isFMap2.ffindAny { it.first == 1 } shouldBe Pair(1,"1")
        isFMap2.ffindAny { it.first == 2 } shouldBe Pair(2,"2")
        isFMap3.ffindAny { it.first == 0 } shouldBe null
        isFMap3.ffindAny { it.first == 1 } shouldBe Pair(1,"1")
        isFMap3.ffindAny { it.first == 3 } shouldBe Pair(3,"3")
    }

    test("fisStrict") {
        isFMap0.fisStrict() shouldBe true
        isFMap3.fisStrict() shouldBe true
    }

    test("fnone") {
        isFMap0.fnone { true } shouldBe true
        isFMap0.fnone { false } shouldBe true
        isFMap1.fnone { false } shouldBe true
        isFMap1.fnone { true } shouldBe false
        isFMap2.fnone { it.first == 1 } shouldBe false
        isFMap2.fnone { it.first > 10 } shouldBe true
    }

    test("fpick") {
        isFMap0.fpick() shouldBe null
        isFMap1.fpick()?.let { it::class } shouldBe Pair::class
    }

    test("fpickNotEmpty") {
        isFMap0.fpickNotEmpty() shouldBe null
        isFMap1.fpickNotEmpty()?.let { it::class } shouldBe Pair::class
    }

    test ("fpopAndReminder") {
        val (pop1, reminder1) = isFMap0.fpopAndRemainder()
        pop1 shouldBe null
        reminder1.fempty() shouldBe true
        val (pop2, reminder2) = isFMap1.fpopAndRemainder()
        pop2 shouldBe isFMap1.fpick()
        reminder2.fempty() shouldBe true
        val (pop3, reminder3) = isFMap2.fpopAndRemainder()
        pop3 shouldBe isFMap1.fpick()
        reminder3.equals(FLCons(Pair(2,"2"), FLNil)) shouldBe true
        val (pop4, reminder4) = isFMap3.fpopAndRemainder()
        pop4 shouldBe isFMap1.fpick()
        reminder4.equals(FLCons(Pair(2,"2"), FLCons(Pair(3,"3"), FLNil))) shouldBe true
    }

    test("fsize") {
        isFMap0.fsize() shouldBe 0
        isFMap1.fsize() shouldBe 1
        isFMap2.fsize() shouldBe 2
        isFMap3.fsize() shouldBe 3
    }

    test("fisNested") {
        isFMap0.fisNested() shouldBe null
        isFMap1.fisNested() shouldBe false
        isFMap2.fisNested() shouldBe false
    }

})