package com.xrpn.immutable

import com.xrpn.imapi.IMListFiltering
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intListOfNone: IMListFiltering<Int> = FList.of(*arrayOf<Int>())
private val intListOfOne: IMListFiltering<Int> = FList.of(*arrayOf<Int>(1))
private val intListOfTwo: IMListFiltering<Int> = FList.of(*arrayOf<Int>(1,2))
private val intListOfThree: IMListFiltering<Int> = FList.of(*arrayOf<Int>(1,2,3))

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

  test("fdropWhile") {
    intListOfNone.fdropWhile { it > 1 } shouldBe FLNil
    intListOfOne.fdropWhile { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(2,1)).fdropWhile { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(3,2,1)).fdropWhile { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(3,2,1,0)).fdropWhile { it > 1 } shouldBe FLCons(1,FLCons(0,FLNil))
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

  test("ffindFromLeft") {
    intListOfNone.ffindFromLeft { _ -> true } shouldBe null
    intListOfNone.ffindFromLeft { _ -> false } shouldBe null
    intListOfOne.ffindFromLeft { 0 < it } shouldBe 1
    intListOfOne.ffindFromLeft { _ -> false } shouldBe null
    intListOfThree.ffindFromLeft { 0 < it } shouldBe 1
    intListOfThree.ffindFromLeft { 1 < it } shouldBe 2
    intListOfThree.ffindFromLeft { 2 < it } shouldBe 3
    intListOfThree.ffindFromLeft { 3 < it } shouldBe null
    intListOfThree.ffindFromLeft { it < 3 } shouldBe 1
    intListOfThree.ffindFromLeft { it < 2 } shouldBe 1
  }

  test("ffindFromRight") {
    intListOfNone.ffindFromRight { _ -> true } shouldBe null
    intListOfNone.ffindFromRight { _ -> false } shouldBe null
    intListOfOne.ffindFromRight { 0 < it } shouldBe 1
    intListOfOne.ffindFromRight { _ -> false } shouldBe null
    intListOfThree.ffindFromRight { 0 < it } shouldBe 3
    intListOfThree.ffindFromRight { 1 < it } shouldBe 3
    intListOfThree.ffindFromRight { 2 < it } shouldBe 3
    intListOfThree.ffindFromRight { 3 < it } shouldBe null
    intListOfThree.ffindFromRight { it < 3 } shouldBe 2
    intListOfThree.ffindFromRight { it < 2 } shouldBe 1
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

  test("fslice (list)") {
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
