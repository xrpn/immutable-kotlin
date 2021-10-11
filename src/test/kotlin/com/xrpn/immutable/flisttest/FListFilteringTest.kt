package com.xrpn.immutable.flisttest

import com.xrpn.imapi.IMList
import com.xrpn.imapi.IMListFiltering
import com.xrpn.immutable.FLCons
import com.xrpn.immutable.FLNil
import com.xrpn.immutable.FList
import com.xrpn.immutable.emptyArrayOfInt
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intListOfNone: IMListFiltering<Int> = FList.of(*emptyArrayOfInt)
private val intListOfOne: IMListFiltering<Int> = FList.of(*arrayOf<Int>(1))
private val intListOfTwo: IMListFiltering<Int> = FList.of(*arrayOf<Int>(1,2))
private val intListOfThree: IMListFiltering<Int> = FList.of(*arrayOf<Int>(1,2,3))
private val intListOfFour: IMListFiltering<Int> = FList.of(*arrayOf<Int>(1,2,1,3))
private val intListOfFourA: IMListFiltering<Int> = FList.of(*arrayOf<Int>(1,2,2,3))
private val intListOfFourB: IMListFiltering<Int> = FList.of(*arrayOf<Int>(1,2,3,2))
private val intListOfSix = FList.of(*arrayOf<Int>(1,2,3,3,2,1))

class FListFilteringTest : FunSpec({

  beforeTest {}

  test("fdrop 0") {
    intListOfNone.fdrop(0) shouldBe FLNil
    intListOfOne.fdrop(0) shouldBe intListOfOne
    intListOfTwo.fdrop(0) shouldBe intListOfTwo
    intListOfThree.fdrop(0) shouldBe intListOfThree
  }

  test("fdrop 1") {
    intListOfNone.fdrop(1) shouldBe FLNil
    intListOfOne.fdrop(1) shouldBe FLNil
    intListOfTwo.fdrop(1) shouldBe FLCons(2,FLNil)
    intListOfThree.fdrop(1) shouldBe FLCons(2,FLCons(3,FLNil))
  }

  test("fdrop 2") {
    intListOfNone.fdrop(2) shouldBe FLNil
    intListOfOne.fdrop(2) shouldBe FLNil
    intListOfTwo.fdrop(2) shouldBe FLNil
    intListOfThree.fdrop(2) shouldBe FLCons(3,FLNil)
    FList.of(*arrayOf<Int>(1,2,3,4)).fdrop(2) shouldBe FLCons(3,FLCons(4,FLNil))
  }

  test("fdrop 3") {
    intListOfNone.fdrop(3) shouldBe FLNil
    intListOfOne.fdrop(3) shouldBe FLNil
    intListOfTwo.fdrop(3) shouldBe FLNil
    intListOfThree.fdrop(3) shouldBe FLNil
    FList.of(*arrayOf<Int>(1,2,3,4)).fdrop(3) shouldBe FLCons(4,FLNil)
  }

  test("fdrop negative") {
    intListOfNone.fdrop(-1) shouldBe FLNil
    intListOfOne.fdrop(-1) shouldBe FLNil
    intListOfTwo.fdrop(-1) shouldBe FLNil
    intListOfThree.fdrop(-1) shouldBe FLNil
  }

  test("fdropAll") {
    intListOfNone.fdropAll(intListOfNone as IMList<Int>) shouldBe FLNil
    intListOfOne.fdropAll(intListOfNone) shouldBe intListOfOne
    intListOfOne.fdropAll(intListOfOne as IMList<Int>) shouldBe FLNil
    intListOfOne.fdropAll(intListOfTwo as IMList<Int>) shouldBe FLNil
    FList.of(*arrayOf<Int>(2,1)).fdropAll(intListOfThree as IMList<Int>) shouldBe FLNil
    FList.of(*arrayOf<Int>(3,2,1)).fdropAll(intListOfTwo) shouldBe FLCons(3, FLNil)
  }

  test("fdropItem") {
    intListOfNone.fdropItem(0) shouldBe FLNil
    intListOfOne.fdropItem(0) shouldBe intListOfOne
    intListOfOne.fdropItem(1) shouldBe FLNil
    intListOfOne.fdropItem(2) shouldBe intListOfOne
    FList.of(*arrayOf<Int>(2,1)).fdropItem(2) shouldBe intListOfOne
    FList.of(*arrayOf<Int>(2,1,2)).fdropItem(2) shouldBe intListOfOne
    FList.of(*arrayOf<Int>(1, 2, 1, 2)).fdropItem(2) shouldBe FLCons(1, intListOfOne as FList<Int>)
    intListOfSix.fdropItem(3) shouldBe FList.of(*arrayOf<Int>(1, 2, 2, 1))
    intListOfSix.fdropItem(2) shouldBe FList.of(*arrayOf<Int>(1, 3, 3, 1))
    intListOfSix.fdropItem(1) shouldBe FList.of(*arrayOf<Int>(2, 3, 3, 2))
  }

  test("fdropFirst") {
    intListOfNone.fdropFirst { it > 1 } shouldBe FLNil
    intListOfOne.fdropFirst { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(2,1)).fdropFirst { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(3,2,1)).fdropFirst { it > 1 }  shouldBe FLCons(2, FLCons(1,FLNil))
    FList.of(*arrayOf<Int>(3,2,1,0)).fdropFirst { it == 2 } shouldBe FLCons(3, FLCons(1,FLCons(0,FLNil)))
    FList.of(*arrayOf<Int>(3,2,1,0)).fdropFirst { it < 3 } shouldBe FLCons(3, FLCons(1,FLCons(0,FLNil)))
  }

  test("fdropRight") {
    intListOfNone.fdropRight(1) shouldBe intListOfNone
    intListOfOne.fdropRight(0) shouldBe intListOfOne
    intListOfThree.fdropRight(1) shouldBe intListOfTwo
    intListOfThree.fdropRight(2) shouldBe intListOfOne
    intListOfThree.fdropRight(3) shouldBe intListOfNone
  }

  test("fdropRight negative") {
    intListOfNone.fdropRight(-1) shouldBe FLNil
    intListOfThree.fdropRight(-1) shouldBe FLNil
    intListOfThree.fdropRight(-2) shouldBe FLNil
    intListOfThree.fdropRight(-3) shouldBe FLNil
  }

  test("fdropWhen") {
    intListOfNone.fdropWhen { it > 1 } shouldBe FLNil
    intListOfOne.fdropWhen { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(2,1)).fdropWhen { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(3,2,1)).fdropWhen { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(3,2,1,0)).fdropWhen { it > 1 } shouldBe FLCons(1,FLCons(0,FLNil))
    intListOfFour.fdropWhen { it > 1 } shouldBe FLCons(1, FLCons(1, FLNil))
    intListOfFourA.fdropWhen { it < 2 } shouldBe FLCons(2, FLCons(2, FLCons(3, FLNil)))
    intListOfFourA.fdropWhen { it < 3 } shouldBe FLCons(3, FLNil)
    intListOfFourB.fdropWhen { it < 3 } shouldBe FLCons(3, FLNil)
  }

  test("fdropWhile") {
    intListOfNone.fdropWhile { it > 1 } shouldBe FLNil
    intListOfOne.fdropWhile { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(2,1)).fdropWhile { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(3,2,1)).fdropWhile { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(3,2,1,0)).fdropWhile { it > 1 } shouldBe FLCons(1,FLCons(0,FLNil))
    intListOfFour.fdropWhile { it > 1 } shouldBe intListOfFour
    intListOfFourA.fdropWhile { it < 2 } shouldBe FLCons(2, FLCons(2, FLCons(3, FLNil)))
    intListOfFourA.fdropWhile { it < 3 } shouldBe FLCons(3, FLNil)
    intListOfFourB.fdropWhile { it < 3 } shouldBe FLCons(3, FLCons(2, FLNil))
  }

  test("ffilter") {
    intListOfNone.ffilter {0 == it % 2} shouldBe FLNil
    intListOfOne.ffilter {0 == it % 2} shouldBe FLNil
    intListOfTwo.ffilter {0 == it % 2} shouldBe FLCons(2,FLNil)
    intListOfThree.ffilter {0 == it % 2} shouldBe FLCons(2,FLNil)
    FList.of(*arrayOf<Int>(1,2,3,4)).ffilter {0 == it % 2} shouldBe FLCons(2,FLCons(4,FLNil))
  }

  test("ffilterNot") {
    intListOfNone.ffilterNot {0 == it % 2} shouldBe FLNil
    intListOfOne.ffilterNot {0 == it % 2} shouldBe FLCons(1,FLNil)
    intListOfTwo.ffilterNot {0 == it % 2} shouldBe FLCons(1,FLNil)
    intListOfThree.ffilterNot {0 == it % 2} shouldBe FLCons(1,FLCons(3,FLNil))
    FList.of(*arrayOf<Int>(1,2,3,4)).ffilterNot {0 == it % 2} shouldBe FLCons(1,FLCons(3,FLNil))
  }

  test("ffind") {
    intListOfNone.ffind { _ -> true } shouldBe null
    intListOfNone.ffind { _ -> false } shouldBe null
    intListOfOne.ffind { 0 < it } shouldBe 1
    intListOfOne.ffind { _ -> false } shouldBe null
    intListOfThree.ffind { 0 < it } shouldBe 1
    intListOfThree.ffind { 1 < it } shouldBe 2
    intListOfThree.ffind { 2 < it } shouldBe 3
    intListOfThree.ffind { 3 < it } shouldBe null
    intListOfThree.ffind { it < 3 } shouldBe 1
    intListOfThree.ffind { it < 2 } shouldBe 1
  }

  test("ffindLast") {
    intListOfNone.ffindLast { _ -> true } shouldBe null
    intListOfNone.ffindLast { _ -> false } shouldBe null
    intListOfOne.ffindLast { 0 < it } shouldBe 1
    intListOfOne.ffindLast { _ -> false } shouldBe null
    intListOfThree.ffindLast { 0 < it } shouldBe 3
    intListOfThree.ffindLast { 1 < it } shouldBe 3
    intListOfThree.ffindLast { 2 < it } shouldBe 3
    intListOfThree.ffindLast { 3 < it } shouldBe null
    intListOfThree.ffindLast { it < 3 } shouldBe 2
    intListOfThree.ffindLast { it < 2 } shouldBe 1
  }

  test("fgetOrNull") {
    intListOfNone.fgetOrNull(0) shouldBe null
    intListOfOne.fgetOrNull(-1) shouldBe null
    intListOfOne.fgetOrNull(0) shouldBe 1
    intListOfOne.fgetOrNull(1) shouldBe null
    intListOfThree.fgetOrNull(-1) shouldBe null
    intListOfThree.fgetOrNull(0) shouldBe 1
    intListOfThree.fgetOrNull(1) shouldBe 2
    intListOfThree.fgetOrNull(2) shouldBe 3
    intListOfThree.fgetOrNull(3) shouldBe null
  }

  test("fhasSubsequence") {
    intListOfNone.fhasSubsequence(FList.of(*emptyArrayOfInt)) shouldBe true
    intListOfNone.fhasSubsequence(intListOfOne as IMList<Int>) shouldBe false

    intListOfOne.fhasSubsequence(intListOfNone as IMList<Int>) shouldBe true
    intListOfOne.fhasSubsequence(intListOfOne) shouldBe true
    intListOfOne.fhasSubsequence(intListOfTwo as IMList<Int>) shouldBe false

    intListOfTwo.fhasSubsequence(intListOfNone) shouldBe true
    intListOfTwo.fhasSubsequence(intListOfOne) shouldBe true
    intListOfTwo.fhasSubsequence(FLCons(2, FLNil) as IMList<Int>) shouldBe true
    intListOfTwo.fhasSubsequence(intListOfTwo) shouldBe true
    intListOfTwo.fhasSubsequence(intListOfTwo.freverse()) shouldBe false
    intListOfTwo.fhasSubsequence(intListOfThree as IMList<Int>) shouldBe false

    intListOfThree.fhasSubsequence(intListOfNone) shouldBe true
    intListOfThree.fhasSubsequence(intListOfOne) shouldBe true
    intListOfThree.fhasSubsequence(FLCons(2, FLNil) as IMList<Int>) shouldBe true
    intListOfThree.fhasSubsequence(FLCons(3, FLNil) as IMList<Int>) shouldBe true
    intListOfThree.fhasSubsequence(intListOfTwo) shouldBe true
    intListOfThree.fhasSubsequence(FLCons(2, FLCons(3, FLNil)) as IMList<Int>) shouldBe true
    intListOfThree.fhasSubsequence(FLCons(1, FLCons(3, FLNil)) as IMList<Int>) shouldBe false
    intListOfThree.fhasSubsequence(intListOfTwo.freverse()) shouldBe false
    intListOfThree.fhasSubsequence(intListOfThree.freverse()) shouldBe false
    intListOfThree.fhasSubsequence(intListOfThree) shouldBe true
  }

  test("fhead") {
    intListOfNone.fhead() shouldBe null
    intListOfOne.fhead() shouldBe 1
    intListOfTwo.fhead() shouldBe 1
  }

  test("finit") {
    intListOfNone.finit() shouldBe FLNil
    intListOfOne.finit() shouldBe FLNil
    intListOfTwo.finit() shouldBe FLCons(1,FLNil)
    intListOfThree.finit() shouldBe FLCons(1,FLCons(2,FLNil))
  }

  test("flast") {
    intListOfNone.flast() shouldBe null
    intListOfOne.flast() shouldBe 1
    intListOfTwo.flast() shouldBe 2
    intListOfThree.flast() shouldBe 3
  }

  test("fslice (ix, ix)") {
    intListOfNone.fslice(0, 0) shouldBe FLNil
    intListOfOne.fslice(0, 0) shouldBe FLNil
    intListOfOne.fslice(1, 0) shouldBe FLNil
    intListOfOne.fslice(0, 1) shouldBe intListOfOne
    intListOfTwo.fslice(1, 0) shouldBe FLNil
    intListOfTwo.fslice(0, 1) shouldBe intListOfOne
    intListOfTwo.fslice(0, 2) shouldBe intListOfTwo

    intListOfThree.fslice(0, 0) shouldBe FLNil
    intListOfThree.fslice(1, 1) shouldBe FLNil
    intListOfThree.fslice(2, 2) shouldBe FLNil
    intListOfThree.fslice(3, 3) shouldBe FLNil

    intListOfThree.fslice(0, 1) shouldBe intListOfOne
    intListOfThree.fslice(0, 2) shouldBe intListOfTwo
    intListOfThree.fslice(0, 3) shouldBe intListOfThree
    intListOfThree.fslice(1, 2) shouldBe FLCons(2, FLNil)
    intListOfThree.fslice(1, 3) shouldBe FLCons(2, FLCons(3, FLNil))
    intListOfThree.fslice(2, 3) shouldBe FLCons(3, FLNil)
  }

  test("fslice (IMList)") {
    intListOfThree.fslice(FLNil) shouldBe FLNil
    intListOfThree.fslice(FList.of(-1)) shouldBe FLNil
    intListOfThree.fslice(FList.of(0)) shouldBe intListOfOne
    intListOfThree.fslice(FList.of(1)) shouldBe FLCons(2, FLNil)
    intListOfThree.fslice(FList.of(2)) shouldBe FLCons(3, FLNil)
    intListOfThree.fslice(FList.of(3)) shouldBe FLNil
    intListOfThree.fslice(FList.of(1, 2)) shouldBe FLCons(2, FLCons(3, FLNil))
    intListOfThree.fslice(FList.of(-1, 1, 2, 5)) shouldBe FLCons(2, FLCons(3, FLNil))
    intListOfThree.fslice(FList.of(2, -1, 1, 5)) shouldBe FLCons(3, FLCons(2, FLNil))
  }

  test("ftail") {
    intListOfNone.ftail() shouldBe FLNil
    intListOfOne.ftail() shouldBe FLNil
    intListOfTwo.ftail() shouldBe FLCons(2,FLNil)
    intListOfThree.ftail() shouldBe FLCons(2,FLCons(3,FLNil))
  }

  test("ftake 0") {
    intListOfNone.ftake(0) shouldBe FLNil
    intListOfOne.ftake(0) shouldBe FLNil
    intListOfTwo.ftake(0) shouldBe FLNil
    intListOfThree.ftake(0) shouldBe FLNil
  }

  test("ftake 1") {
    intListOfNone.ftake(1) shouldBe FLNil
    intListOfOne.ftake(1) shouldBe intListOfOne
    intListOfTwo.ftake(1) shouldBe intListOfOne
    intListOfThree.ftake(1) shouldBe intListOfOne
  }

  test("ftake 2") {
    intListOfNone.ftake(2) shouldBe FLNil
    intListOfOne.ftake(2) shouldBe intListOfOne
    intListOfTwo.ftake(2) shouldBe intListOfTwo
    intListOfThree.ftake(2) shouldBe intListOfTwo
    FList.of(*arrayOf<Int>(1,2,3,4)).ftake(2) shouldBe intListOfTwo
  }

  test("ftake 3") {
    intListOfNone.ftake(3) shouldBe FLNil
    intListOfOne.ftake(3) shouldBe intListOfOne
    intListOfTwo.ftake(3) shouldBe intListOfTwo
    intListOfThree.ftake(3) shouldBe intListOfThree
    FList.of(*arrayOf<Int>(1,2,3,4)).ftake(3) shouldBe intListOfThree
  }

  test("ftake (negative)") {
    intListOfOne.ftake(-1) shouldBe FLNil
    intListOfTwo.ftake(-1) shouldBe FLNil
    intListOfTwo.ftake(-2) shouldBe FLNil
    intListOfTwo.ftake(-3) shouldBe FLNil
    intListOfThree.ftake(-1) shouldBe FLNil
    intListOfThree.ftake(-2) shouldBe FLNil
    intListOfThree.ftake(-3) shouldBe FLNil
  }

  test("ftakeRight") {
    intListOfNone.ftakeRight(0) shouldBe intListOfNone
    intListOfOne.ftakeRight(0) shouldBe intListOfNone
    intListOfOne.ftakeRight(1) shouldBe intListOfOne
    intListOfTwo.ftakeRight(0) shouldBe intListOfNone
    intListOfTwo.ftakeRight(1) shouldBe FLCons(2, FLNil)
    intListOfTwo.ftakeRight(2) shouldBe intListOfTwo
    intListOfTwo.ftakeRight(3) shouldBe intListOfTwo
    intListOfThree.ftakeRight(0) shouldBe intListOfNone
    intListOfThree.ftakeRight(1) shouldBe FLCons(3, FLNil)
    intListOfThree.ftakeRight(2) shouldBe FLCons(2, FLCons(3, FLNil))
    intListOfThree.ftakeRight(3) shouldBe intListOfThree
  }

  test("ftakeRight (negative)") {
    intListOfOne.ftakeRight(-1) shouldBe FLNil
    intListOfTwo.ftakeRight(-1) shouldBe FLNil
    intListOfTwo.ftakeRight(-2) shouldBe FLNil
    intListOfTwo.ftakeRight(-3) shouldBe FLNil
    intListOfThree.ftakeRight(-1) shouldBe FLNil
    intListOfThree.ftakeRight(-2) shouldBe FLNil
    intListOfThree.ftakeRight(-3) shouldBe FLNil
  }

  test("ftakeWhile") {
    intListOfNone.ftakeWhile { it > 1 } shouldBe FLNil
    intListOfOne.ftakeWhile { it == 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(2,1)).ftakeWhile { it > 1 }  shouldBe FLCons(2,FLNil)
    FList.of(*arrayOf<Int>(3,2,1)).ftakeWhile { it > 1 }  shouldBe FLCons(3,FLCons(2,FLNil))
    FList.of(*arrayOf<Int>(3,2,1,0)).ftakeWhile { it != 1 } shouldBe FLCons(3,FLCons(2,FLNil))
  }
})
