package com.xrpn.immutable.flisttest

import com.xrpn.imapi.*
import com.xrpn.imapi.IMZipMap.Companion.fmap2
import com.xrpn.imapi.IMZipMap.Companion.fmap2p
import com.xrpn.imapi.IMZipMap.Companion.fmap3
import com.xrpn.imapi.IMZipMap.Companion.fmap3p
import com.xrpn.imapi.IMZipMap.Companion.fmap4
import com.xrpn.imapi.IMZipMap.Companion.fmap4p
import com.xrpn.immutable.*
import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.emptyArrayOfLong
import com.xrpn.immutable.emptyArrayOfStr
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

private val intListOfNone = FList.of(*emptyArrayOfInt)
private val intListOfOne = FList.of(*arrayOf<Int>(1))
private val intListOfTwoA = FList.of(*arrayOf<Int>(1,3))
private val intListOfTwo = FList.of(*arrayOf<Int>(1,2))
private val intListOfTwoC = FList.of(*arrayOf<Int>(1,4))
private val intListOfThree = FList.of(*arrayOf<Int>(1,2,3))
private val intListOfThreeA = FList.of(*arrayOf<Int>(1,2,5))

private val longListOfNone = FList.of(*emptyArrayOfLong)
private val strListOfNone = FList.of(*emptyArrayOfStr)
private val boolListOfNone = FList.of(*arrayOf<Boolean>())
private val longListOfOne = FList.of(*arrayOf<Long>(10L))
private val strListOfOne = FList.of(*arrayOf<String>("A"))
private val boolListOfOne = FList.of(*arrayOf<Boolean>(true))
private val longListOfTwo = FList.of(*arrayOf<Long>(10L, 20L))
private const val strItem1 = "A"
private const val strItem2 = "B"
private val strListOfTwo = FList.of(*arrayOf<String>(strItem1, strItem2))
private const val boolItem1 = true
private const val boolItem2 = false
private val boolListOfTwo = FList.of(*arrayOf<Boolean>(boolItem1, boolItem2))
private val strListOfThree = FList.of(*arrayOf<String>(strItem1, strItem2, "C"))
private val longListOfThree = FList.of(*arrayOf<Long>(10L, 20L, 30L))
private val boolListOfThree = FList.of(*arrayOf<Boolean>(boolItem1, boolItem2, false))

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

  test("flist zmap sanity") {

    val seed = 5.0

    val calc4C: (String) -> (Int) -> (Long) -> (Boolean) -> Double = { s: String -> { i: Int -> { l: Long  -> { b: Boolean ->
      seed+s[0].code.toDouble()+i.toDouble()+l.toDouble()+(if(b) 1.0 else -1.0)
    }}}}
    val calc3C: (String) -> (Int) -> (Long) -> Double = { _: String -> { _: Int -> { l: Long  ->  seed+l.toDouble()}}}
    val calc2C: (String) -> (Int) -> Double = { _: String -> { i: Int -> seed+i.toDouble() }}

    val calc4: (String, Int, Long, Boolean) -> Double = { s: String, i: Int,  l: Long,  b: Boolean ->
      seed+s[0].code.toDouble()+i.toDouble()+l.toDouble()+(if(b) 1.0 else -1.0)
    }
    val calc3: (String, Int, Long) -> Double = { _: String, _: Int, l: Long  ->  seed+l.toDouble() }
    val calc2: (String, Int) -> Double = { _: String, i: Int ->  seed+i.toDouble() }

    val aut2C: ITMap<Double> = (strListOfTwo mapWith intListOfTwo).fmap2(calc2C)
    aut2C.fsize() shouldBe 2
    aut2C.fmap { it }.fany { it == seed+1.0 } shouldBe true
    aut2C.fmap { it }.fany { it == seed+2.0 } shouldBe true
    val aut2: ITMap<Double> = (strListOfTwo mapWith intListOfTwo).fmap2p(calc2)
    aut2.fsize() shouldBe 2
    aut2C.fmap { it }.fany { it == seed+1.0 } shouldBe true
    aut2C.fmap { it }.fany { it == seed+2.0 } shouldBe true

    val aut3C: ITMap<Double> = (strListOfTwo mapWith intListOfTwo mapWith longListOfTwo).fmap3(calc3C)
    aut3C.fsize() shouldBe 2
    aut3C.fmap { it }.fany { it == seed+10.0 } shouldBe true
    aut3C.fmap { it }.fany { it == seed+20.0 } shouldBe true
    val aut3: ITMap<Double> = (strListOfTwo mapWith intListOfTwo mapWith longListOfTwo).fmap3p(calc3)
    aut3.fsize() shouldBe 2
    aut3.fmap { it }.fany { it == seed+10.0 } shouldBe true
    aut3.fmap { it }.fany { it == seed+20.0 } shouldBe true

    val aut4C: ITMap<Double> = (strListOfTwo mapWith intListOfTwo mapWith longListOfTwo mapWith boolListOfTwo).fmap4(calc4C)
    aut4C.fsize() shouldBe 2
    aut4C.fmap { it }.fany { it == seed+11.0+strItem1[0].code.toDouble()+(if(boolItem1) 1.0 else -1.0) } shouldBe true
    aut4C.fmap { it }.fany { it == seed+22.0+strItem2[0].code.toDouble()+(if(boolItem2) 1.0 else -1.0) } shouldBe true
    val aut4: ITMap<Double> = (strListOfTwo mapWith intListOfTwo mapWith longListOfTwo mapWith boolListOfTwo).fmap4p(calc4)
    aut4.fsize() shouldBe 2
    aut4.fmap { it }.fany { it == seed+11.0+strItem1[0].code.toDouble()+(if(boolItem1) 1.0 else -1.0) } shouldBe true
    aut4.fmap { it }.fany { it == seed+22.0+strItem2[0].code.toDouble()+(if(boolItem2) 1.0 else -1.0) } shouldBe true

    val aut4Cx: ITMap<Double> = (strListOfTwo mapWith intListOfThree mapWith longListOfTwo mapWith boolListOfTwo).fmap4(calc4C)
    aut4Cx.fsize() shouldBe 2
    aut4Cx.fmap { it }.fany { it == seed+11.0+strItem1[0].code.toDouble()+(if(boolItem1) 1.0 else -1.0) } shouldBe true
    aut4Cx.fmap { it }.fany { it == seed+22.0+strItem2[0].code.toDouble()+(if(boolItem2) 1.0 else -1.0) } shouldBe true
    val aut4x: ITMap<Double> = (strListOfThree mapWith intListOfTwo mapWith longListOfTwo mapWith boolListOfTwo).fmap4p(calc4)
    aut4x.fsize() shouldBe 2
    aut4x.fmap { it }.fany { it == seed+11.0+strItem1[0].code.toDouble()+(if(boolItem1) 1.0 else -1.0) } shouldBe true
    aut4x.fmap { it }.fany { it == seed+22.0+strItem2[0].code.toDouble()+(if(boolItem2) 1.0 else -1.0) } shouldBe true
    val aut4Cy: ITMap<Double> = (strListOfTwo mapWith intListOfThree mapWith longListOfThree mapWith boolListOfTwo).fmap4(calc4C)
    aut4Cy.fsize() shouldBe 2
    aut4Cy.fmap { it }.fany { it == seed+11.0+strItem1[0].code.toDouble()+(if(boolItem1) 1.0 else -1.0) } shouldBe true
    aut4Cy.fmap { it }.fany { it == seed+22.0+strItem2[0].code.toDouble()+(if(boolItem2) 1.0 else -1.0) } shouldBe true
    val aut4y: ITMap<Double> = (strListOfThree mapWith intListOfTwo mapWith longListOfTwo mapWith boolListOfThree).fmap4p(calc4)
    aut4y.fsize() shouldBe 2
    aut4y.fmap { it }.fany { it == seed+11.0+strItem1[0].code.toDouble()+(if(boolItem1) 1.0 else -1.0) } shouldBe true
    aut4y.fmap { it }.fany { it == seed+22.0+strItem2[0].code.toDouble()+(if(boolItem2) 1.0 else -1.0) } shouldBe true

    val aut4Ca: ITMap<Double> = (strListOfNone mapWith intListOfTwo mapWith longListOfTwo mapWith boolListOfTwo).fmap4(calc4C)
    aut4Ca.fsize() shouldBe 0
    val aut4Cb: ITMap<Double> = (strListOfTwo mapWith intListOfNone mapWith longListOfTwo mapWith boolListOfTwo).fmap4(calc4C)
    aut4Cb.fsize() shouldBe 0
    val aut4Cc: ITMap<Double> = (strListOfTwo mapWith intListOfTwo mapWith longListOfNone mapWith boolListOfTwo).fmap4(calc4C)
    aut4Cc.fsize() shouldBe 0
    val aut4Cd: ITMap<Double> = (strListOfTwo mapWith intListOfTwo mapWith longListOfTwo mapWith boolListOfNone).fmap4(calc4C)
    aut4Cd.fsize() shouldBe 0
    val aut4a: ITMap<Double> = (strListOfNone mapWith intListOfNone mapWith longListOfNone mapWith boolListOfNone).fmap4p(calc4)
    aut4a.fsize() shouldBe 0
  }
})