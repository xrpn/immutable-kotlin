package com.xrpn.immutable.flisttest

import com.xrpn.imapi.IMList
import com.xrpn.imapi.IMListGrouping
import com.xrpn.immutable.*
import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.emptyArrayOfStr
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intListOfNone: IMListGrouping<Int> = FList.of(*emptyArrayOfInt)
private val intListOfOne: IMListGrouping<Int> = FList.of(*arrayOf<Int>(1))
private val intListOfTwo: IMListGrouping<Int> = FList.of(*arrayOf<Int>(1,2))
private val intListOfThree: IMListGrouping<Int> = FList.of(*arrayOf<Int>(1,2,3))
private val strListOfNone: IMListGrouping<String> = FList.of(*emptyArrayOfStr)
private val strListOfOne: IMListGrouping<String> = FList.of(*arrayOf<String>("a"))
private val strListOfTwo: IMListGrouping<String> = FList.of(*arrayOf<String>("a","b"))
private val strListOfThree: IMListGrouping<String> = FList.of(*arrayOf<String>("a","b","c"))

class FListGroupingTest : FunSpec({

  beforeTest {}

  test("ffindFirst") {
    intListOfNone.ffindFirst { it > 1 } shouldBe Triple(FLNil, null, FLNil)
    intListOfOne.ffindFirst { it > 1 }  shouldBe Triple(FLCons(1,FLNil), null, FLNil)
    FList.of(*arrayOf<Int>(2,1)).ffindFirst { it > 1 }  shouldBe Triple(FLNil, 2, FLCons(1,FLNil))
    FList.of(*arrayOf<Int>(3,2,1)).ffindFirst { it > 1 }  shouldBe Triple(FLNil, 3, FLCons(2, FLCons(1,FLNil)))
    FList.of(*arrayOf<Int>(3,2,1,0)).ffindFirst { it == 2 } shouldBe Triple(FLCons(3, FLNil), 2, FLCons(1,FLCons(0,FLNil)))
    FList.of(*arrayOf<Int>(3,2,1,0)).ffindFirst { it < 3 } shouldBe Triple(FLCons(3, FLNil), 2, FLCons(1,FLCons(0,FLNil)))
  }

  test("fgroupBy").config(enabled = false) {
    fail("need FMap done to make this happen")
    // intListOfNone.fgroupBy { a -> a.toString() } shouldBe emptyMap()
  }

  test("findexed") {
    FList.of(*emptyArrayOfStr).findexed() shouldBe FLNil
    strListOfOne.findexed() shouldBe FLCons(Pair("a",0),FLNil)
    strListOfTwo.findexed() shouldBe FLCons(Pair("a",0),FLCons(Pair("b",1),FLNil))
    strListOfThree.findexed() shouldBe FLCons(Pair("a",0),FLCons(Pair("b",1),FLCons(Pair("c",2),FLNil)))
  }

  test("findexed offset") {
    FList.of(*emptyArrayOfStr).findexed(10) shouldBe FLNil
    strListOfOne.findexed(10) shouldBe FLCons(Pair("a",10),FLNil)
    strListOfTwo.findexed(10) shouldBe FLCons(Pair("a",10),FLCons(Pair("b",11),FLNil))
    strListOfThree.findexed(10) shouldBe FLCons(Pair("a",10),FLCons(Pair("b",11),FLCons(Pair("c",12),FLNil)))
  }

  test("fpartition") {
    intListOfNone.fpartition { _ -> true } shouldBe Pair(FLNil, FLNil)
    intListOfNone.fpartition { _ -> false } shouldBe Pair(FLNil, FLNil)
    intListOfOne.fpartition { _ -> true } shouldBe Pair(intListOfOne, FLNil)
    intListOfOne.fpartition { _ -> false } shouldBe Pair(FLNil, intListOfOne)

    intListOfThree.fpartition { _ -> true } shouldBe Pair(intListOfThree, FLNil)
    intListOfThree.fpartition { 1 < it } shouldBe Pair(FLCons(2, FLCons(3, FLNil)), intListOfOne)
    intListOfThree.fpartition { 2 < it } shouldBe Pair(FLCons(3, FLNil), intListOfTwo)
    intListOfThree.fpartition { _ -> false } shouldBe Pair(FLNil, intListOfThree)
    intListOfThree.fpartition { it < 2 } shouldBe Pair(intListOfOne, FLCons(2, FLCons(3, FLNil)))
    intListOfThree.fpartition { it < 3 } shouldBe Pair(intListOfTwo, FLCons(3, FLNil))
  }

  test("fslidingWindow") {
    intListOfNone.fslidingWindow(0, 1) shouldBe FLNil
    intListOfNone.fslidingWindow(1, 0) shouldBe FLNil
    intListOfNone.fslidingWindow(1, 1) shouldBe FLNil
    intListOfOne.fslidingWindow(0, 1) shouldBe FLNil
    intListOfOne.fslidingWindow(1, 0) shouldBe FLNil
    intListOfOne.fslidingWindow(1, 1) shouldBe FLCons(intListOfOne, FLNil)

    intListOfTwo.fslidingWindow(0, 1) shouldBe FLNil
    intListOfTwo.fslidingWindow(1, 0) shouldBe FLNil
    intListOfTwo.fslidingWindow(1, 1) shouldBe FLCons(intListOfOne, FLCons( FLCons(2, FLNil), FLNil))
    intListOfTwo.fslidingWindow(2, 1) shouldBe FLCons(intListOfTwo, FLCons( FLCons(2, FLNil), FLNil))
    intListOfTwo.fslidingWindow(2, 2) shouldBe FLCons(intListOfTwo, FLNil)
    intListOfTwo.fslidingWindow(2, 3) shouldBe FLCons(intListOfTwo, FLNil)
    intListOfTwo.fslidingWindow(3, 4) shouldBe FLCons(intListOfTwo, FLNil)

    intListOfThree.fslidingWindow(1, 1) shouldBe
            FLCons(intListOfOne, FLCons(FLCons(2, FLNil), FLCons( FLCons(3, FLNil), FLNil)))
    intListOfThree.fslidingWindow(2, 1) shouldBe
            FLCons(intListOfTwo, FLCons( FLCons(2, FLCons(3, FLNil)), FLCons( FLCons(3, FLNil), FLNil)))
    intListOfThree.fslidingWindow(2, 2) shouldBe
            FLCons(intListOfTwo, FLCons( FLCons(3, FLNil), FLNil))
    intListOfThree.fslidingWindow(2, 3) shouldBe FLCons(intListOfTwo, FLNil)
    intListOfThree.fslidingWindow(3, 3) shouldBe FLCons(intListOfThree, FLNil)
  }

  test("fslidingFullWindow") {
    intListOfNone.fslidingFullWindow(0, 1) shouldBe FLNil
    intListOfNone.fslidingFullWindow(1, 0) shouldBe FLNil
    intListOfNone.fslidingFullWindow(1, 1) shouldBe FLNil
    intListOfOne.fslidingFullWindow(0, 1) shouldBe FLNil
    intListOfOne.fslidingFullWindow(1, 0) shouldBe FLNil
    intListOfOne.fslidingFullWindow(1, 1) shouldBe FLCons(intListOfOne, FLNil)

    intListOfTwo.fslidingFullWindow(0, 1) shouldBe FLNil
    intListOfTwo.fslidingFullWindow(1, 0) shouldBe FLNil
    intListOfTwo.fslidingFullWindow(1, 1) shouldBe FLCons(intListOfOne, FLCons( FLCons(2, FLNil), FLNil))
    intListOfTwo.fslidingFullWindow(2, 1) shouldBe FLCons(intListOfTwo, FLNil)
    intListOfTwo.fslidingFullWindow(2, 2) shouldBe FLCons(intListOfTwo, FLNil)
    intListOfTwo.fslidingFullWindow(2, 3) shouldBe FLCons(intListOfTwo, FLNil)
    intListOfTwo.fslidingFullWindow(3, 4) shouldBe FLNil

    intListOfThree.fslidingFullWindow(1, 1) shouldBe
            FLCons(intListOfOne, FLCons(FLCons(2, FLNil), FLCons( FLCons(3, FLNil), FLNil)))
    intListOfThree.fslidingFullWindow(2, 1) shouldBe
            FLCons(intListOfTwo, FLCons( FLCons(2, FLCons(3, FLNil)), FLNil))
    intListOfThree.fslidingFullWindow(2, 2) shouldBe FLCons(intListOfTwo, FLNil)
    intListOfThree.fslidingFullWindow(2, 3) shouldBe FLCons(intListOfTwo, FLNil)
    intListOfThree.fslidingFullWindow(3, 3) shouldBe FLCons(intListOfThree, FLNil)
  }

  test("fsplitAt") {
    intListOfNone.fsplitAt((intListOfNone as FList<Int>).indexOfFirst { it > 1 }) shouldBe Triple(FLNil, null, FLNil)
    intListOfOne.fsplitAt((intListOfOne as FList<Int>).indexOfFirst { it > 1 })  shouldBe Triple(FLCons(1,FLNil), null, FLNil)
    FList.of(*arrayOf<Int>(2,1)).fsplitAt(0)  shouldBe Triple(FLNil, 2, FLCons(1,FLNil))
    FList.of(*arrayOf<Int>(3,2,1)).fsplitAt(0)  shouldBe Triple(FLNil, 3, FLCons(2, FLCons(1,FLNil)))
    FList.of(*arrayOf<Int>(3,2,1,0)).fsplitAt(1) shouldBe Triple(FLCons(3, FLNil), 2, FLCons(1,FLCons(0,FLNil)))
    FList.of(*arrayOf<Int>(3,2,1,0)).fsplitAt(1) shouldBe Triple(FLCons(3, FLNil), 2, FLCons(1,FLCons(0,FLNil)))
  }

  test("funzip") {
    intListOfNone.funzip { a -> Pair(a, a) } shouldBe Pair(FLNil, FLNil)
    intListOfThree.fzipWith(strListOfThree as IMList<String>){ a, b -> Pair(a,b)}.funzip { pair -> pair } shouldBe
            Pair(FLCons(1,FLCons(2 ,FLCons(3, FLNil))), FLCons("a", FLCons("b", FLCons("c",FLNil))))
    intListOfThree.fzipWith(strListOfTwo as IMList<String>){a, b -> Pair(a,b)}.funzip { pair -> pair } shouldBe
            Pair(FLCons(1,FLCons(2 ,FLNil)), FLCons("a",FLCons("b",FLNil)))
    intListOfThree.fzipWith(strListOfOne as IMList<String>){a, b -> Pair(a,b)}.funzip { pair -> pair } shouldBe
            Pair(FLCons(1, FLNil), FLCons("a", FLNil))
    intListOfThree.fzipWith(strListOfNone as IMList<String>){a, b -> Pair(a,b)}.funzip { pair -> pair } shouldBe Pair(FLNil, FLNil)
  }

  test("zipWith") {
    intListOfNone.fzipWith(FList.of(*emptyArrayOfStr)){a, b -> Pair(a,b)} shouldBe FLNil
    intListOfOne.fzipWith(FList.of(*emptyArrayOfStr)){a, b -> Pair(a,b)} shouldBe FLNil
    intListOfNone.fzipWith(FList.of(*arrayOf<String>("a"))){a, b -> Pair(a,b)} shouldBe FLNil
    intListOfOne.fzipWith(FList.of(*arrayOf<String>("a"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLNil)
    intListOfTwo.fzipWith(FList.of(*arrayOf<String>("a"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLNil)
    intListOfOne.fzipWith(FList.of(*arrayOf<String>("a","b"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLNil)
    intListOfTwo.fzipWith(FList.of(*arrayOf<String>("a", "b"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
    intListOfThree.fzipWith(FList.of(*emptyArrayOfStr)){a, b -> Pair(a,b)} shouldBe FLNil
    intListOfThree.fzipWith(FList.of(*arrayOf<String>("a", "b"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
    intListOfTwo.fzipWith(FList.of(*arrayOf<String>("a", "b", "c"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
  }

  test("zipWith iterable") {
    intListOfNone.fzipWith(emptyArrayOfStr.iterator()) shouldBe FLNil
    intListOfOne.fzipWith(emptyArrayOfStr.iterator()) shouldBe FLNil
    intListOfNone.fzipWith(arrayOf("a").iterator()) shouldBe FLNil
    intListOfOne.fzipWith(arrayOf("a").iterator()) shouldBe FLCons(Pair(1,"a"),FLNil)
    intListOfTwo.fzipWith(arrayOf("a").iterator()) shouldBe FLCons(Pair(1,"a"),FLNil)
    intListOfOne.fzipWith(arrayOf("a","b").iterator()) shouldBe FLCons(Pair(1,"a"),FLNil)
    intListOfTwo.fzipWith(arrayOf("a","b").iterator()) shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
    intListOfThree.fzipWith(emptyList<String>().iterator()) shouldBe FLNil
    intListOfThree.fzipWith(arrayOf("a","b").iterator()) shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
    intListOfTwo.fzipWith(arrayOf("a","b","c").iterator()) shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
  }

  test("zipWhen") {
    intListOfNone.fzipWhen(FList.of(*emptyArrayOfInt)){a, b -> a == b} shouldBe FLNil
    intListOfNone.fzipWhen(FList.of(*arrayOf<Int>(1))){a, b -> a == b} shouldBe FLNil

    intListOfOne.fzipWhen(FList.of(*emptyArrayOfInt)){a, b -> a == b} shouldBe FLNil
    intListOfOne.fzipWhen(FList.of(*arrayOf<Int>(1))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLNil)
    intListOfOne.fzipWhen(FList.of(*arrayOf<Int>(2))){a, b -> a == b} shouldBe FLNil
    intListOfOne.fzipWhen(FList.of(*arrayOf<Int>(1, 3))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLNil)
    intListOfOne.fzipWhen(FList.of(*arrayOf<Int>(3, 1))){a, b -> a == b} shouldBe FLNil

    intListOfTwo.fzipWhen(FList.of(*emptyArrayOfInt)){a, b -> a == b} shouldBe FLNil
    intListOfTwo.fzipWhen(FList.of(*arrayOf<Int>(1))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLNil)
    intListOfTwo.fzipWhen(FList.of(*arrayOf<Int>(2))){a, b -> a == b} shouldBe FLNil
    intListOfTwo.fzipWhen(FList.of(*arrayOf<Int>(1, 3))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLNil)
    intListOfTwo.fzipWhen(FList.of(*arrayOf<Int>(3, 2))){a, b -> a == b} shouldBe FLCons(Pair(2,2),FLNil)
    intListOfTwo.fzipWhen(FList.of(*arrayOf<Int>(1, 2))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLCons(Pair(2,2),FLNil))
    intListOfTwo.fzipWhen(FList.of(*arrayOf<Int>(1, 2, 3))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLCons(Pair(2,2),FLNil))

    intListOfThree.fzipWhen(FList.of(*emptyArrayOfInt)){a, b -> a == b} shouldBe FLNil
    intListOfThree.fzipWhen(FList.of(*arrayOf<Int>(2, 3))){a, b -> a == b} shouldBe FLNil
    intListOfThree.fzipWhen(FList.of(*arrayOf<Int>(2, 3, 1))){a, b -> a == b} shouldBe FLNil
    intListOfThree.fzipWhen(FList.of(*arrayOf<Int>(1, 2))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLCons(Pair(2,2),FLNil))
    intListOfThree.fzipWhen(FList.of(*arrayOf<Int>(1, 5, 3))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLCons(Pair(3,3),FLNil))
    intListOfThree.fzipWhen(FList.of(*arrayOf<Int>(1, 3, 5))){a, b -> a == b} shouldBe FLCons(Pair(1,1), FLNil)
    intListOfThree.fzipWhen(FList.of(*arrayOf<Int>(1, 2, 3))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLCons(Pair(2,2),FLCons(Pair(3,3),FLNil)))
    intListOfThree.fzipWhen(FList.of(*arrayOf<Int>(1, 2, 3, 4))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLCons(Pair(2,2),FLCons(Pair(3,3),FLNil)))
  }

  test("zipWhile") {
    intListOfNone.fzipWhile(FList.of(*emptyArrayOfInt)){a, b -> a == b} shouldBe FLNil
    intListOfNone.fzipWhile(FList.of(*arrayOf<Int>(1))){a, b -> a == b} shouldBe FLNil

    intListOfOne.fzipWhile(FList.of(*emptyArrayOfInt)){a, b -> a == b} shouldBe FLNil
    intListOfOne.fzipWhile(FList.of(*arrayOf<Int>(1))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLNil)
    intListOfOne.fzipWhile(FList.of(*arrayOf<Int>(2))){a, b -> a == b} shouldBe FLNil
    intListOfOne.fzipWhile(FList.of(*arrayOf<Int>(1, 3))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLNil)
    intListOfOne.fzipWhile(FList.of(*arrayOf<Int>(3, 1))){a, b -> a == b} shouldBe FLNil

    intListOfTwo.fzipWhile(FList.of(*emptyArrayOfInt)){a, b -> a == b} shouldBe FLNil
    intListOfTwo.fzipWhile(FList.of(*arrayOf<Int>(1))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLNil)
    intListOfTwo.fzipWhile(FList.of(*arrayOf<Int>(2))){a, b -> a == b} shouldBe FLNil
    intListOfTwo.fzipWhile(FList.of(*arrayOf<Int>(1, 3))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLNil)
    intListOfTwo.fzipWhile(FList.of(*arrayOf<Int>(3, 2))){a, b -> a == b} shouldBe FLNil
    intListOfTwo.fzipWhile(FList.of(*arrayOf<Int>(1, 2))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLCons(Pair(2,2),FLNil))
    intListOfTwo.fzipWhile(FList.of(*arrayOf<Int>(1, 2, 3))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLCons(Pair(2,2),FLNil))

    intListOfThree.fzipWhile(FList.of(*emptyArrayOfInt)){a, b -> a == b} shouldBe FLNil
    intListOfThree.fzipWhile(FList.of(*arrayOf<Int>(2, 3))){a, b -> a == b} shouldBe FLNil
    intListOfThree.fzipWhile(FList.of(*arrayOf<Int>(2, 3, 1))){a, b -> a == b} shouldBe FLNil
    intListOfThree.fzipWhile(FList.of(*arrayOf<Int>(1, 2))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLCons(Pair(2,2),FLNil))
    intListOfThree.fzipWhile(FList.of(*arrayOf<Int>(1, 5, 3))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLNil)
    intListOfThree.fzipWhile(FList.of(*arrayOf<Int>(1, 3, 5))){a, b -> a == b} shouldBe FLCons(Pair(1,1), FLNil)
    intListOfThree.fzipWhile(FList.of(*arrayOf<Int>(1, 2, 3))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLCons(Pair(2,2),FLCons(Pair(3,3),FLNil)))
    intListOfThree.fzipWhile(FList.of(*arrayOf<Int>(1, 2, 3, 4))){a, b -> a == b} shouldBe FLCons(Pair(1,1),FLCons(Pair(2,2),FLCons(Pair(3,3),FLNil)))
  }

  test("zipWhile iterable") {}

  test("fzipWithIndex") {
    FList.of(*emptyArrayOfStr).fzipWithIndex() shouldBe FLNil
    strListOfOne.fzipWithIndex() shouldBe FLCons(Pair("a",0),FLNil)
    strListOfTwo.fzipWithIndex() shouldBe FLCons(Pair("a",0),FLCons(Pair("b",1),FLNil))
    strListOfThree.fzipWithIndex() shouldBe FLCons(Pair("a",0),FLCons(Pair("b",1),FLCons(Pair("c",2),FLNil)))
  }

  test("fzipWithIndex offset") {
    FList.of(*emptyArrayOfStr).fzipWithIndex(10) shouldBe FLNil
    strListOfOne.fzipWithIndex(1) shouldBe FLNil
    strListOfTwo.fzipWithIndex(1) shouldBe FLCons(Pair("b",0),FLNil)
    strListOfThree.fzipWithIndex(1) shouldBe FLCons(Pair("b",0),FLCons(Pair("c",1),FLNil))
    strListOfThree.fzipWithIndex(2) shouldBe FLCons(Pair("c",0),FLNil)
  }
})
