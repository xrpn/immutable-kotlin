package com.xrpn.immutable

import com.xrpn.imapi.IMListTransforming
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intListOfNone: IMListTransforming<Int> = FList.of(*arrayOf<Int>())
private val intListOfOne: IMListTransforming<Int> = FList.of(*arrayOf<Int>(1))
private val intListOfTwoA: IMListTransforming<Int> = FList.of(*arrayOf<Int>(1,3))
private val intListOfTwo: IMListTransforming<Int> = FList.of(*arrayOf<Int>(1,2))
private val intListOfTwoC: IMListTransforming<Int> = FList.of(*arrayOf<Int>(1,4))
private val intListOfThree: IMListTransforming<Int> = FList.of(*arrayOf<Int>(1,2,3))
private val intListOfThreeA: IMListTransforming<Int> = FList.of(*arrayOf<Int>(1,2,5))

class FListTransformingTest : FunSpec({

  beforeTest {}

//  afterTest { (testCase, result) ->
//  }

  test("fflatMap") {
    intListOfNone.fflatMap {it -> FLCons(it, FLNil)} shouldBe FLNil
    intListOfOne.fflatMap {it -> FLCons(it, FLNil)} shouldBe FLCons(1,FLNil)
    fun arrayBuilderConst(arg: Int) = Array<Int>(arg) { _ -> arg }
    intListOfTwo.fflatMap {FList.of(*arrayBuilderConst(it))} shouldBe FLCons(1,FLCons(2,FLCons(2,FLNil)))
    fun arrayBuilderIncrement(arg: Int) = Array<Int>(arg) { i -> arg + i }
    intListOfTwo.fflatMap {FList.of(*arrayBuilderIncrement(it))} shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
    intListOfThree.fflatMap {FList.of(*arrayBuilderIncrement(it))} shouldBe
            FLCons(1,FLCons(2,FLCons(3,FLCons(3,FLCons(4,FLCons(5,FLNil))))))
    intListOfThree.fflatMap { i -> FList.of(i, i) } shouldBe
            FLCons(1,FLCons(1,FLCons(2,FLCons(2,FLCons(3,FLCons(3,FLNil))))))
  }

  test("foldLeft sum") {

    val s = { acc: Int, b: Int -> acc + b}
      
    intListOfNone.ffoldLeft(0, s) shouldBe 0
    intListOfOne.ffoldLeft(0, s)  shouldBe 1
    FList.of(*arrayOf<Int>(2,1)).ffoldLeft(0, s)  shouldBe 3
    FList.of(*arrayOf<Int>(3,2,1)).ffoldLeft(0, s)  shouldBe 6
    FList.of(*arrayOf<Int>(3,2,1,0)).ffoldLeft(0, s) shouldBe 6
  }

  test("foldLeft diff") {

    val d = { acc: Int, b: Int -> acc - b }

    intListOfNone.ffoldLeft(1, d) shouldBe 1
    intListOfOne.ffoldLeft(1, d) shouldBe 0
    intListOfTwo.ffoldLeft(1, d) shouldBe -2
    intListOfTwoA.ffoldLeft(1, d) shouldBe -3
    intListOfThree.ffoldLeft(1, d) shouldBe -5
    intListOfThreeA.ffoldLeft(1, d) shouldBe -7
  }

  test("foldLeft product") {

    val s = { acc: Int, b: Int -> acc * b}

    intListOfNone.ffoldLeft(1, s) shouldBe 1
    intListOfOne.ffoldLeft(1, s)  shouldBe 1
    FList.of(*arrayOf<Int>(2,1)).ffoldLeft(1, s)  shouldBe 2
    FList.of(*arrayOf<Int>(3,2,1)).ffoldLeft(1, s)  shouldBe 6
    FList.of(*arrayOf<Int>(3,2,1,0)).ffoldLeft(1, s) shouldBe 0
  }

  test("ffoldRight diff") {

    val d = { a: Int, acc: Int -> a - acc}

    intListOfNone.ffoldRight(0, d) shouldBe 0
    intListOfOne.ffoldRight(0, d) shouldBe 1
    FList.of(*arrayOf<Int>(2,1)).ffoldRight(0, d)  shouldBe 1
    FList.of(*arrayOf<Int>(3,2,1)).ffoldRight(0, d)  shouldBe 2
    FList.of(*arrayOf<Int>(3,2,1,0)).ffoldRight(0, d) shouldBe 2
  }

  test("foldRight sum") {

    val s = { a: Int, acc: Int -> a + acc}

    intListOfNone.ffoldRight(0, s) shouldBe 0
    intListOfOne.ffoldRight(0, s)  shouldBe 1
    FList.of(*arrayOf<Int>(2,1)).ffoldRight(0, s)  shouldBe 3
    FList.of(*arrayOf<Int>(3,2,1)).ffoldRight(0, s)  shouldBe 6
    FList.of(*arrayOf<Int>(3,2,1,0)).ffoldRight(0, s) shouldBe 6
  }

  test("foldRight product") {

    val ss = { a: Int, acc: Int -> a * acc}

    intListOfNone.ffoldRight(1, ss) shouldBe 1
    intListOfOne.ffoldRight(1, ss)  shouldBe 1
    FList.of(*arrayOf<Int>(2,1)).ffoldRight(1, ss)  shouldBe 2
    FList.of(*arrayOf<Int>(3,2,1)).ffoldRight(1, ss)  shouldBe 6
    FList.of(*arrayOf<Int>(3,2,1,0)).ffoldRight(1, ss) shouldBe 0
  }

  test("fmap") {
    intListOfNone.fmap { it + 1} shouldBe FLNil
    intListOfOne.fmap { it + 1} shouldBe FLCons(2,FLNil)
    intListOfTwo.fmap { it + 1} shouldBe FLCons(2,FLCons(3,FLNil))
  }

  test("freduceLeft") {

    val ss = { acc: Int, b: Int -> b - acc }

    intListOfNone.freduceLeft(ss) shouldBe null
    intListOfOne.freduceLeft(ss) shouldBe 1
    intListOfTwo.freduceLeft(ss) shouldBe 1
    intListOfTwoA.freduceLeft(ss) shouldBe 2
    intListOfTwoC.freduceLeft(ss) shouldBe 3
    intListOfThree.freduceLeft(ss) shouldBe 2
    intListOfThreeA.freduceLeft(ss) shouldBe 4
  }

  test("freduceRight") {

    val ss = { b: Int, acc: Int -> acc - b }

    intListOfNone.freduceRight(ss) shouldBe null
    intListOfOne.freduceRight(ss) shouldBe 1
    intListOfTwo.freduceRight(ss) shouldBe 1
    intListOfTwoA.freduceRight(ss) shouldBe 2
    intListOfTwoC.freduceRight(ss) shouldBe 3
    intListOfThree.freduceRight(ss) shouldBe 0
    intListOfThreeA.freduceRight(ss) shouldBe 2
  }

  test("frotl") {
    intListOfNone.frotl() shouldBe intListOfNone
    intListOfOne.frotl() shouldBe intListOfOne
    intListOfTwo.frotl() shouldBe FLCons(2, FLCons(1, FLNil))
    intListOfThree.frotl() shouldBe FLCons(2, FLCons(3, FLCons(1, FLNil)))
  }

  test("frotr") {
    intListOfNone.frotr() shouldBe intListOfNone
    intListOfOne.frotr() shouldBe intListOfOne
    intListOfTwo.frotr() shouldBe FLCons(2, FLCons(1, FLNil))
    intListOfThree.frotr() shouldBe FLCons(3, FLCons(1, FLCons(2, FLNil)))
  }

  test("fswaph") {
    intListOfNone.fswaph() shouldBe intListOfNone
    intListOfOne.fswaph() shouldBe intListOfOne
    intListOfTwo.fswaph() shouldBe FLCons(2, FLCons(1, FLNil))
    intListOfThree.fswaph() shouldBe FLCons(2, FLCons(1, FLCons(3, FLNil)))
  }

})
