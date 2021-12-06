package com.xrpn.immutable.flisttest

import com.xrpn.imapi.IMListTransforming
import com.xrpn.immutable.FLCons
import com.xrpn.immutable.FLNil
import com.xrpn.immutable.FList
import com.xrpn.immutable.emptyArrayOfInt
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intListOfNone: IMListTransforming<Int> = FList.of(*emptyArrayOfInt)
private val intListOfOne: IMListTransforming<Int> = FList.of(*arrayOf<Int>(1))
private val intListOfTwoA: IMListTransforming<Int> = FList.of(*arrayOf<Int>(1,3))
private val intListOfTwo: IMListTransforming<Int> = FList.of(*arrayOf<Int>(1,2))
private val intListOfTwoC: IMListTransforming<Int> = FList.of(*arrayOf<Int>(1,4))
private val intListOfThree: IMListTransforming<Int> = FList.of(*arrayOf<Int>(1,2,3))
private val intListOfThreeA: IMListTransforming<Int> = FList.of(*arrayOf<Int>(1,2,5))

class FListTransformingTest : FunSpec({

  beforeTest {}

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

  test("fold diff") {

    val d = { acc: Int, b: Int -> acc - b }

    (intListOfNone as FList<Int>).ffold(1, d) shouldBe 1
    (intListOfOne as FList<Int>).ffold(1, d) shouldBe 0
    (intListOfTwo as FList<Int>).ffold(1, d) shouldBe -2
    (intListOfTwoA as FList<Int>).ffold(1, d) shouldBe -3
    (intListOfThree as FList<Int>).ffold(1, d) shouldBe -5
    (intListOfThreeA as FList<Int>).ffold(1, d) shouldBe -7
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

})
