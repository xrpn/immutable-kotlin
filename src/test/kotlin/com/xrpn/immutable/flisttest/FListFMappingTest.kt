package com.xrpn.immutable.flisttest

import com.xrpn.imapi.*
import com.xrpn.immutable.FLCons
import com.xrpn.immutable.FLNil
import com.xrpn.immutable.FList
import com.xrpn.immutable.emptyArrayOfInt
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intListOfNone = FList.of(*emptyArrayOfInt)
private val intListOfOne = FList.of(*arrayOf<Int>(1))
private val intListOfTwoA = FList.of(*arrayOf<Int>(1,3))
private val intListOfTwo = FList.of(*arrayOf<Int>(1,2))
private val intListOfTwoC = FList.of(*arrayOf<Int>(1,4))
private val intListOfThree = FList.of(*arrayOf<Int>(1,2,3))
private val intListOfThreeA = FList.of(*arrayOf<Int>(1,2,5))

class FListFMappingTest : FunSpec({

  beforeTest {}

  test("fflatMap") {
    intListOfNone.fflatMap {it -> FLCons(it, FLNil) } shouldBe FLNil
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

  test("fmap") {
    intListOfNone.fmap { it + 1} shouldBe FLNil
    intListOfOne.fmap { it + 1} shouldBe FLCons(2,FLNil)
    intListOfTwo.fmap { it + 1} shouldBe FLCons(2,FLCons(3,FLNil))
    intListOfThree.fmap { it + 1} shouldBe FLCons(2,FLCons(3,FLCons(4,FLNil)))
  }

  test("fmapp") {
    intListOfNone.fmapp { it + 1} shouldBe FLNil
    intListOfOne.fmapp { it + 1} shouldBe FLCons(2,FLNil)
    intListOfTwo.fmapp { it + 1} shouldBe FLCons(2,FLCons(3,FLNil))
    intListOfThree.fmapp { it + 1} shouldBe FLCons(2,FLCons(3,FLCons(4,FLNil)))
  }

  test("fmapp fmapp") {
    (intListOfNone.fmap { it + 1} === intListOfNone.fmapp { it + 1}) shouldBe true
    (intListOfNone.fmap { it + 1}.equals(intListOfNone.fmapp { it + 1})) shouldBe true
    (intListOfOne.fmap { it + 1}.equals(intListOfOne.fmapp { it + 1})) shouldBe true
    (intListOfTwo.fmap { it + 1}.equals(intListOfTwo.fmapp { it + 1})) shouldBe true
    (intListOfThree.fmap { it + 1}.equals(intListOfThree.fmapp { it + 1})) shouldBe true
  }

  test("ftraverse succeed") {
    val aut1 = intListOfNone.ftraverse { it.toString() }
    aut1.left() shouldBe null
    aut1.right() shouldBe FLNil
    val aut2 = intListOfOne.ftraverse{ (it + 1).toString() }
    aut2.left() shouldBe null
    aut2.right()?.equals(FLCons("2",FLNil)) shouldBe true
    val aut3 = intListOfTwo.ftraverse{ (it + 1).toString() }
    aut3.left() shouldBe null
    aut3.right()?.equals(FLCons("2",FLCons("3",FLNil))) shouldBe true
    val aut4 = intListOfThree.ftraverse{ (it + 1).toString() }
    aut4.left() shouldBe null
    aut4.right()?.equals(FLCons("2", FLCons("3", FLCons("4",FLNil)))) shouldBe true
  }

  test("ftraverse fail") {
    val aut1 = intListOfNone.ftraverse { throw RuntimeException(it.toString()) }
    aut1.left() shouldBe null
    aut1.right() shouldBe FLNil
    val aut2 = intListOfOne.ftraverse{ throw RuntimeException(it.toString()) }
    aut2.left()?.fsize() shouldBe 1
    aut2.right() shouldBe null
    val aut3 = intListOfTwo.ftraverse{ throw RuntimeException(it.toString()) }
    aut3.left()?.fsize() shouldBe 2
    aut3.right() shouldBe null
    val aut4 = intListOfThree.ftraverse{ throw RuntimeException(it.toString()) }
    aut4.left()?.fsize() shouldBe 3
    aut4.right() shouldBe null
    val aut5 = intListOfThree.ftraverse{ if(0==(it%2)) throw RuntimeException(it.toString()) else it }
    aut5.left()?.fsize() shouldBe 1
    aut5.right() shouldBe null
  }

  test("fgrossTraverse") {
    val aut1 = intListOfNone.fgrossTraverse { throw RuntimeException(it.toString()) }
    aut1.first shouldBe FLNil
    aut1.second shouldBe FLNil
    val aut2 = intListOfThree.fgrossTraverse{ if(0!=(it%2)) throw RuntimeException(it.toString()) else it }
    aut2.first.fsize() shouldBe 2
    aut2.second.fsize() shouldBe 1
    val aut3 = intListOfThree.fgrossTraverse{ it.toString() }
    aut3.first shouldBe FLNil
    aut3.second.fsize() shouldBe 3
    val aut4 = intListOfThree.fgrossTraverse{ throw RuntimeException(it.toString()) }
    aut4.first.fsize() shouldBe 3
    aut4.second shouldBe FLNil
    val aut5 = intListOfThree.fgrossTraverse{ if(0==(it%2)) throw RuntimeException(it.toString()) else it }
    aut5.first.fsize() shouldBe 1
    aut5.second.fsize() shouldBe 2
  }

  test("ftraverseWithError") {
    val toError: (String) -> ErrExReport<String> = { s ->
      val ex = RuntimeException("where this happened")
      ErrExReport(" reported as $s", ex)
    }
    val aut1 = intListOfNone.ftraverseWithError({ throw RuntimeException(it.toString()) }, toError)
    aut1.left() shouldBe null
    aut1.right() shouldBe FLNil
    val aut2 = intListOfOne.ftraverseWithError({ throw RuntimeException(it.toString()) }, toError )
    aut2.left()?.fsize() shouldBe 1
    aut2.right() shouldBe null
    val aut3 = intListOfTwo.ftraverseWithError({ throw RuntimeException(it.toString()) }, toError )
    aut3.left()?.fsize() shouldBe 2
    aut3.right() shouldBe null
    val aut4 = intListOfThree.ftraverseWithError({ throw RuntimeException(it.toString()) }, toError )
    aut4.left()?.fsize() shouldBe 3
    aut4.right() shouldBe null
    val aut5 = intListOfThree.ftraverseWithError({ if(0==(it%2)) throw RuntimeException(it.toString()) else it }, toError)
    aut5.left()?.fsize() shouldBe 1
    aut5.right() shouldBe null
    aut5.equals(intListOfThree.ftraverse{ if(0==(it%2)) throw RuntimeException(it.toString()) else it }) shouldBe false
  }

  test("fapp") {
    intListOfNone.fmap { it + 1} shouldBe FLNil
    intListOfOne.fmap { it + 1} shouldBe FLCons(2,FLNil)
    intListOfTwo.fmap { it + 1} shouldBe FLCons(2,FLCons(3,FLNil))
  }



})
