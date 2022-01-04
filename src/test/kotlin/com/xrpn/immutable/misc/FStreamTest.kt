package com.xrpn.immutable.misc

import com.xrpn.immutable.*
import com.xrpn.immutable.FStream.Companion.append
import com.xrpn.immutable.FStream.Companion.arithmeticSequence
import com.xrpn.immutable.FStream.Companion.constant
import com.xrpn.immutable.FStream.Companion.emptyFStream
import com.xrpn.immutable.FStream.Companion.fsCons
import com.xrpn.immutable.FStream.Companion.geometricSequence
import com.xrpn.immutable.FStream.Companion.head
import com.xrpn.immutable.FStream.Companion.map_dr
import com.xrpn.immutable.FStream.Companion.prepend
import com.xrpn.immutable.FStream.Companion.tail
import com.xrpn.immutable.FStream.Companion.unfold
import com.xrpn.immutable.FStream.Companion.unfold_dr
import com.xrpn.immutable.FStream.Companion.zipWith_a
import com.xrpn.immutable.emptyArrayOfInt
import com.xrpn.immutable.emptyArrayOfStr
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

fun intStreamOfNone(): FStream<Int> = FStream.of(*emptyArrayOfInt)
fun intStreamOfOne(): FStream<Int> = FStream.of(*arrayOf<Int>(1))
fun intStreamOfTwo(): FStream<Int> = FStream.of(*arrayOf<Int>(1, 2))
fun intStreamOfThree(): FStream<Int> = FStream.of(*arrayOf<Int>(1, 2, 3))
fun strStreamOfNone(): FStream<String> = FStream.of(*emptyArrayOfStr)
fun strStreamOfOne(): FStream<String> = FStream.of(*arrayOf<String>("a"))
fun strStreamOfTwo(): FStream<String> = FStream.of(*arrayOf<String>("a", "b"))
fun strStreamOfThree(): FStream<String> = FStream.of(*arrayOf<String>("a", "b", "c"))
val tailInt: () -> FStream<Int> = { emptyFStream() }
val hdTwo: () -> Int = { 2 }
val hdThree: () -> Int = { 3 }
val tailStr: () -> FStream<String> = { emptyFStream() }
val hdB: () -> String = { "b" }

private val intListOfNone = FList.of(*emptyArrayOfInt)
private val intListOfOneA = FList.of(*arrayOf<Int>(0))
private val intListOfOne = FList.of(*arrayOf<Int>(1))
private val intListOfOneB = FList.of(*arrayOf<Int>(2))
private val intListOfOneC = FList.of(*arrayOf<Int>(3))
private val intListOfOneD = FList.of(*arrayOf<Int>(4))
private val intListOfTwoA = FList.of(*arrayOf<Int>(1, 3))
private val intListOfTwo = FList.of(*arrayOf<Int>(1, 2))
private val intListOfTwoB = FList.of(*arrayOf<Int>(0, 2))
private val intListOfTwoC = FList.of(*arrayOf<Int>(1, 4))
private val intListOfThree = FList.of(*arrayOf<Int>(1, 2, 3))
private val intListOfFive = FList.of(*arrayOf<Int>(1, 2, 3, 2, 1))
private val intListOfSix = FList.of(*arrayOf<Int>(1, 2, 3, 3, 2, 1))
private val strListOfNone = FList.of(*emptyArrayOfStr)
private val strListOfOne = FList.of(*arrayOf<String>("a"))
private val strListOfTwo = FList.of(*arrayOf<String>("a", "b"))
private val strListOfThree = FList.of(*arrayOf<String>("a", "b", "c"))

class FStreamTest : FunSpec({

  beforeTest {
  }

//  afterTest { (testCase, result) ->
//  }

  test("toFList") {
    intStreamOfNone().toFList() shouldBe intListOfNone
    intStreamOfOne().toFList() shouldBe intListOfOne
    intStreamOfTwo().toFList() shouldBe intListOfTwo
    intStreamOfThree().toFList() shouldBe intListOfThree
    strStreamOfNone().toFList() shouldBe strListOfNone
    strStreamOfOne().toFList() shouldBe strListOfOne
    strStreamOfTwo().toFList() shouldBe strListOfTwo
    strStreamOfThree().toFList() shouldBe strListOfThree
  }

  test("head int") {
    intStreamOfNone().head() shouldBe null
    intStreamOfOne().head() shouldBe 1
    intStreamOfTwo().head() shouldBe 1
  }

  test("head string") {
    strStreamOfNone().head() shouldBe null
    strStreamOfOne().head() shouldBe "a"
    strStreamOfTwo().head() shouldBe "a"
  }

  test("tail int") {
    intStreamOfNone().tail().toFList() shouldBe FSNil.toFList()
    intStreamOfOne().tail().toFList() shouldBe FSNil.toFList()
    intStreamOfTwo().tail().toFList() shouldBe fsCons(hdTwo, tailInt).toFList()
    intStreamOfThree().tail().toFList() shouldBe FStream.of(2, 3).toFList()
  }

  test("tail str") {
    strStreamOfNone().tail().toFList() shouldBe FSNil.toFList()
    strStreamOfOne().tail().toFList() shouldBe FSNil.toFList()
    strStreamOfTwo().tail().toFList() shouldBe fsCons(hdB, tailStr).toFList()
    strStreamOfThree().tail().toFList() shouldBe FStream.of("b", "c").toFList()
  }

  test("drop 1") {
    intStreamOfNone().drop(1).toFList() shouldBe FSNil.toFList()
    intStreamOfOne().drop(1).toFList() shouldBe FSNil.toFList()
    intStreamOfTwo().drop(1).toFList() shouldBe fsCons(hdTwo, tailInt).toFList()
    intStreamOfThree().drop(1).toFList() shouldBe FStream.of(2, 3).toFList()
  }

  test("drop 2") {
    intStreamOfNone().drop(2).toFList() shouldBe FSNil.toFList()
    intStreamOfOne().drop(2).toFList() shouldBe FSNil.toFList()
    intStreamOfTwo().drop(2).toFList() shouldBe FSNil.toFList()
    intStreamOfThree().drop(2).toFList() shouldBe fsCons(hdThree, tailInt).toFList()
    FStream.of(*arrayOf<Int>(1, 2, 3, 4)).drop(2).toFList() shouldBe FStream.of(3, 4).toFList()
  }

  test("drop 3") {
    intStreamOfNone().drop(3).toFList() shouldBe FSNil.toFList()
    intStreamOfOne().drop(3).toFList() shouldBe FSNil.toFList()
    intStreamOfTwo().drop(3).toFList() shouldBe FSNil.toFList()
    intStreamOfThree().drop(3).toFList() shouldBe FSNil.toFList()
    FStream.of(*arrayOf<Int>(1, 2, 3, 4)).drop(3).toFList() shouldBe fsCons({ 4 }, tailInt).toFList()
  }

  test("take 1") {
    intStreamOfNone().take(1).toFList() shouldBe FSNil.toFList()
    intStreamOfOne().take(1).toFList() shouldBe intStreamOfOne().toFList()
    intStreamOfTwo().take(1).toFList() shouldBe intStreamOfOne().toFList()
    intStreamOfThree().take(1).toFList() shouldBe intStreamOfOne().toFList()
  }

  test("take 2") {
    intStreamOfNone().take(2).toFList() shouldBe FSNil.toFList()
    intStreamOfOne().take(2).toFList() shouldBe intStreamOfOne().toFList()
    intStreamOfTwo().take(2).toFList() shouldBe intStreamOfTwo().toFList()
    intStreamOfThree().take(2).toFList() shouldBe intStreamOfTwo().toFList()
    FStream.of(*arrayOf<Int>(1, 2, 3, 4)).take(2).toFList() shouldBe intStreamOfTwo().toFList()
  }

  test("take 3") {
    intStreamOfNone().take(3).toFList() shouldBe FSNil.toFList()
    intStreamOfOne().take(3).toFList() shouldBe intStreamOfOne().toFList()
    intStreamOfTwo().take(3).toFList() shouldBe intStreamOfTwo().toFList()
    intStreamOfThree().take(3).toFList() shouldBe intStreamOfThree().toFList()
    FStream.of(*arrayOf<Int>(1, 2, 3, 4)).take(3).toFList() shouldBe intStreamOfThree().toFList()
  }

  test("findFirst") {
    fun <A: Any> listed(src: Triple<FStream<A>, A?, FStream<A>>): Triple<FList<A>, A?, FList<A>> = Triple(src.first.toFList(),src.second, src.third.toFList())

    listed(intStreamOfNone().findFirst { it > 1 }) shouldBe listed(Triple(FSNil, null, FSNil))
    listed(intStreamOfOne().findFirst { it > 1 }) shouldBe listed(Triple(intStreamOfOne(), null, FSNil))
    listed(FStream.of(*arrayOf<Int>(2, 1)).findFirst { it > 1 })  shouldBe listed(Triple(FSNil, 2, intStreamOfOne()))
    listed(FStream.of(*arrayOf<Int>(3, 2, 1)).findFirst { it > 1 })  shouldBe listed(Triple(
        FSNil, 3,
        FStream.of(*arrayOf<Int>(2, 1))
    ))
    listed(FStream.of(*arrayOf<Int>(3, 2, 1, 0)).findFirst { it == 2 }) shouldBe listed(Triple(fsCons(hdThree, tailInt), 2,
        FStream.of(*arrayOf<Int>(1, 0))
    ))
    listed(FStream.of(*arrayOf<Int>(3, 2, 1, 0)).findFirst { it < 3 }) shouldBe listed(Triple(fsCons(hdThree, tailInt), 2,
        FStream.of(*arrayOf<Int>(1, 0))
    ))
    listed(intStreamOfThree().findFirst { it == 4 }) shouldBe listed(Triple(intStreamOfThree(), null, FSNil))
  }

  test("dropWhile") {
    intStreamOfNone().dropWhile { it > 1 }.toFList() shouldBe FSNil.toFList()
    intStreamOfOne().dropWhile { it > 1 }.toFList()  shouldBe intStreamOfOne().toFList()
    FStream.of(*arrayOf<Int>(2, 1)).dropWhile { it > 1 }.toFList()  shouldBe intStreamOfOne().toFList()
    FStream.of(*arrayOf<Int>(3, 2, 1)).dropWhile { it > 1 }.toFList()  shouldBe intStreamOfOne().toFList()
    FStream.of(*arrayOf<Int>(3, 2, 1, 0)).dropWhile { it > 1 }.toFList() shouldBe FStream.of(*arrayOf<Int>(1, 0)).toFList()
  }

  test("exists") {
    intStreamOfNone().exists { it > 1 } shouldBe false
    intStreamOfOne().exists { it > 1 }  shouldBe false
    FStream.of(*arrayOf<Int>(2, 1)).exists { it > 1 }  shouldBe true
    FStream.of(*arrayOf<Int>(3, 2, 1)).exists { it > 1 }  shouldBe true
    FStream.of(*arrayOf<Int>(3, 2, 1, 0)).exists { it == 2 } shouldBe true
    FStream.of(*arrayOf<Int>(3, 2, 1, 0)).exists { it < 1 } shouldBe true
  }

  test("takeWhile_a") {
    intStreamOfNone().takeWhile_a { it > 1 }.toFList() shouldBe FSNil.toFList()
    intStreamOfOne().takeWhile_a { it == 1 }.toFList() shouldBe intStreamOfOne().toFList()
    FStream.of(*arrayOf<Int>(2, 1)).takeWhile_a { it > 1 }.toFList()  shouldBe FStream.of(*arrayOf<Int>(2)).toFList()
    FStream.of(*arrayOf<Int>(3, 2, 1)).takeWhile_a { it > 1 }.toFList()  shouldBe FStream.of(*arrayOf<Int>(3, 2)).toFList()
    FStream.of(*arrayOf<Int>(3, 2, 1, 0)).takeWhile_a { it != 1 }.toFList() shouldBe FStream.of(*arrayOf<Int>(3, 2)).toFList()
  }

  test("takeWhile_b") {
    intStreamOfNone().takeWhile_b { it > 1 }.toFList() shouldBe FSNil.toFList()
    intStreamOfOne().takeWhile_b { it == 1 }.toFList() shouldBe intStreamOfOne().toFList()
    FStream.of(*arrayOf<Int>(2, 1)).takeWhile_b { it > 1 }.toFList()  shouldBe FStream.of(*arrayOf<Int>(2)).toFList()
    FStream.of(*arrayOf<Int>(3, 2, 1)).takeWhile_b { it > 1 }.toFList()  shouldBe FStream.of(*arrayOf<Int>(3, 2)).toFList()
    FStream.of(*arrayOf<Int>(3, 2, 1, 0)).takeWhile_b { it != 1 }.toFList() shouldBe FStream.of(*arrayOf<Int>(3, 2)).toFList()
  }

  test("foldRight sum") {
    intStreamOfNone().foldRight({0}, { a, b -> a + b()}) shouldBe 0
    intStreamOfOne().foldRight({0}, { a, b -> a + b()})  shouldBe 1
    FStream.of(*arrayOf<Int>(2, 1)).foldRight({0}, { a, b -> a+b()})  shouldBe 3
    FStream.of(*arrayOf<Int>(3, 2, 1)).foldRight({0}, { a, b -> a+b()})  shouldBe 6
    FStream.of(*arrayOf<Int>(3, 2, 1, 0)).foldRight({0}, { a, b -> a+b()}) shouldBe 6
  }

  test("foldRight product") {
    intStreamOfNone().foldRight({1}, { a, b -> a * b()}) shouldBe 1
    intStreamOfOne().foldRight({1}, { a, b -> a * b()})  shouldBe 1
    FStream.of(*arrayOf<Int>(2, 1)).foldRight({1}, { a, b -> a*b()})  shouldBe 2
    FStream.of(*arrayOf<Int>(3, 2, 1)).foldRight({1}, { a, b -> a*b()})  shouldBe 6
    FStream.of(*arrayOf<Int>(3, 2, 1, 0)).foldRight({1}, { a, b -> a*b()}) shouldBe 0
  }

  test("reverse") {
    intStreamOfNone().reverseX() shouldBe FSNil
    intStreamOfOne().reverseX().toFList() shouldBe FLCons(1, FLNil)
    intStreamOfTwo().reverseX().toFList() shouldBe FLCons(2, FLCons(1, FLNil))
  }

  test("foldLeft sum") {
    intStreamOfNone().foldLeftX({ 0 }, { a, b -> a + b()}) shouldBe 0
    intStreamOfOne().foldLeftX({ 0 }, { a, b -> a + b()})  shouldBe 1
    FStream.of(*arrayOf<Int>(2, 1)).foldLeftX({ 0 }, { a, b -> a+b()})  shouldBe 3
    FStream.of(*arrayOf<Int>(3, 2, 1)).foldLeftX({ 0 }, { a, b -> a+b()})  shouldBe 6
    FStream.of(*arrayOf<Int>(3, 2, 1, 0)).foldLeftX({ 0 }, { a, b -> a+b()}) shouldBe 6
  }

  test("foldLeft product") {
    intStreamOfNone().foldLeftX({ 1 }, { a, b -> a * b()}) shouldBe 1
    intStreamOfOne().foldLeftX({ 1 }, { a, b -> a * b()})  shouldBe 1
    FStream.of(*arrayOf<Int>(2, 1)).foldLeftX({ 1 }, { a, b -> a*b()})  shouldBe 2
    FStream.of(*arrayOf<Int>(3, 2, 1)).foldLeftX({ 1 }, { a, b -> a*b()})  shouldBe 6
    FStream.of(*arrayOf<Int>(3, 2, 1, 0)).foldLeftX({ 1 }, { a, b -> a*b()}) shouldBe 0
  }

  test("map_a") {
    intStreamOfNone().map_a { it + 1} shouldBe FSNil
    intStreamOfOne().map_a { it + 1}.toFList() shouldBe FLCons(2, FLNil)
    intStreamOfTwo().map_a { it + 1}.toFList() shouldBe FLCons(2, FLCons(3, FLNil))
  }

  test("map_b") {
    intStreamOfNone().map_b { it + 1} shouldBe FSNil
    intStreamOfOne().map_b { it + 1}.toFList() shouldBe FLCons(2, FLNil)
    intStreamOfTwo().map_b { it + 1}.toFList() shouldBe FLCons(2, FLCons(3, FLNil))
  }

  test("map_c") {
    intStreamOfNone().map_c { it + 1} shouldBe FSNil
    intStreamOfOne().map_c { it + 1}.toFList() shouldBe FLCons(2, FLNil)
    intStreamOfTwo().map_c { it + 1}.toFList() shouldBe FLCons(2, FLCons(3, FLNil))
  }

  test("map_dr") {
    @OptIn(ExperimentalStdlibApi::class)
    intStreamOfNone().map_dr { it + 1} shouldBe FSNil
    @OptIn(ExperimentalStdlibApi::class)
    intStreamOfOne().map_dr { it + 1}.toFList() shouldBe FLCons(2, FLNil)
    @OptIn(ExperimentalStdlibApi::class)
    intStreamOfTwo().map_dr { it + 1}.toFList() shouldBe FLCons(2, FLCons(3, FLNil))
  }

  test("flatMap") {
    intStreamOfNone().flatMap { it -> fsCons({ it }, { FSNil }) } shouldBe FSNil
    intStreamOfOne().flatMap { it -> fsCons({ it }, { FSNil }) }.toFList() shouldBe FSCons({ 1 }, { FSNil } ).toFList()
    fun arrayBuilderConst(arg: Int) = Array<Int>(arg) { _ -> arg }
    intStreamOfTwo().flatMap { FStream.of(*arrayBuilderConst(it)) }.toFList() shouldBe
        FLCons(1, FLCons(2, FLCons(2, FLNil)))
    fun arrayBuilderIncrement(arg: Int) = Array<Int>(arg) { i -> arg + i }
    intStreamOfTwo().flatMap { FStream.of(*arrayBuilderIncrement(it)) }.toFList() shouldBe
        FLCons(1, FLCons(2, FLCons(3, FLNil)))
    intStreamOfThree().flatMap { FStream.of(*arrayBuilderIncrement(it)) }.toFList() shouldBe
        FLCons(1, FLCons(2, FLCons(3, FLCons(3, FLCons(4, FLCons(5, FLNil))))))
    intStreamOfThree().flatMap { i -> FStream.of(i, i) }.toFList() shouldBe
        FLCons(1, FLCons(1, FLCons(2, FLCons(2, FLCons(3, FLCons(3, FLNil))))))
  }

  test("filter") {
    intStreamOfNone().filter {0 == it % 2} shouldBe FSNil
    intStreamOfOne().filter {0 == it % 2} shouldBe FSNil
    intStreamOfTwo().filter {0 == it % 2}.toFList() shouldBe fsCons(hdTwo, tailInt).toFList()
    intStreamOfThree().filter {0 == it % 2}.toFList() shouldBe fsCons(hdTwo, tailInt).toFList()
    FStream.of(*arrayOf<Int>(1, 2, 3, 4)).filter {0 == it % 2}.toFList() shouldBe FLCons(2, FLCons(4, FLNil))
  }

  test("filterNot") {
    intStreamOfNone().filterNot {0 == it % 2} shouldBe FSNil
    intStreamOfOne().filterNot {0 == it % 2}.toFList() shouldBe FLCons(1, FLNil)
    intStreamOfTwo().filterNot {0 == it % 2}.toFList() shouldBe FLCons(1, FLNil)
    intStreamOfThree().filterNot {0 == it % 2}.toFList() shouldBe FLCons(1, FLCons(3, FLNil))
    FStream.of(*arrayOf<Int>(1, 2, 3, 4)).filterNot {0 == it % 2}.toFList() shouldBe FLCons(1, FLCons(3, FLNil))
  }

  test("zipWith") {
    intStreamOfNone().zipWith_a(FStream.of(*emptyArrayOfStr)){ a, b -> Pair(a,b)} shouldBe FSNil
    intStreamOfOne().zipWith_a(FStream.of(*emptyArrayOfStr)){ a, b -> Pair(a,b)} shouldBe FSNil
    intStreamOfNone().zipWith_a(FStream.of(*arrayOf<String>("a"))){ a, b -> Pair(a,b)} shouldBe FSNil
    intStreamOfOne().zipWith_a(FStream.of(*arrayOf<String>("a"))){ a, b -> Pair(a,b)}.toFList() shouldBe FLCons(Pair(1,"a"),
        FLNil
    )
    intStreamOfTwo().zipWith_a(FStream.of(*arrayOf<String>("a"))){ a, b -> Pair(a,b)}.toFList() shouldBe FLCons(Pair(1,"a"),
        FLNil
    )
    intStreamOfOne().zipWith_a(FStream.of(*arrayOf<String>("a", "b"))){ a, b -> Pair(a,b)}.toFList() shouldBe FLCons(Pair(1,"a"),
        FLNil
    )
    intStreamOfTwo().zipWith_a(FStream.of(*arrayOf<String>("a", "b"))){ a, b -> Pair(a,b)}.toFList() shouldBe FLCons(Pair(1,"a"),
        FLCons(Pair(2,"b"), FLNil)
    )
    intStreamOfThree().zipWith_a(FStream.of(*arrayOf<String>("a", "b"))){ a, b -> Pair(a,b)}.toFList() shouldBe FLCons(Pair(1,"a"),
        FLCons(Pair(2,"b"), FLNil)
    )
    intStreamOfTwo().zipWith_a(FStream.of(*arrayOf<String>("a", "b", "c"))){ a, b -> Pair(a,b)}.toFList() shouldBe FLCons(Pair(1,"a"),
        FLCons(Pair(2,"b"), FLNil)
    )
  }

//  test("zipWith iterable") {
//    intStreamOfNone.zipWith(emptyArrayOfStr.iterator()) shouldBe FSNil
//    intStreamOfOne.zipWith(emptyArrayOfStr.iterator()) shouldBe FSNil
//    intStreamOfNone.zipWith(arrayOf("a").iterator()) shouldBe FSNil
//    intStreamOfOne.zipWith(arrayOf("a").iterator()) shouldBe cons(Pair(1,"a"),FSNil)
//    intStreamOfTwo.zipWith(arrayOf("a").iterator()) shouldBe cons(Pair(1,"a"),FSNil)
//    intStreamOfOne.zipWith(arrayOf("a","b").iterator()) shouldBe cons(Pair(1,"a"),FSNil)
//    intStreamOfTwo.zipWith(arrayOf("a","b").iterator()) shouldBe cons(Pair(1,"a"),cons(Pair(2,"b"),FSNil))
//    intStreamOfThree.zipWith(arrayOf("a","b").iterator()) shouldBe cons(Pair(1,"a"),cons(Pair(2,"b"),FSNil))
//    intStreamOfTwo.zipWith(arrayOf("a","b","c").iterator()) shouldBe cons(Pair(1,"a"),cons(Pair(2,"b"),FSNil))
//  }
//
//  test("enumerate") {
//    FList.of(*emptyArrayOfStr).enumerate() shouldBe FSNil
//    strStreamOfOne.enumerate() shouldBe cons(Pair("a",0),FSNil)
//    strStreamOfTwo.enumerate() shouldBe cons(Pair("a",0),cons(Pair("b",1),FSNil))
//    strStreamOfThree.enumerate() shouldBe cons(Pair("a",0),cons(Pair("b",1),cons(Pair("c",2),FSNil)))
//  }
//
//  test("enumerate offset") {
//    FList.of(*emptyArrayOfStr).enumerate(10) shouldBe FSNil
//    strStreamOfOne.enumerate(10) shouldBe cons(Pair("a",10),FSNil)
//    strStreamOfTwo.enumerate(10) shouldBe cons(Pair("a",10),cons(Pair("b",11),FSNil))
//    strStreamOfThree.enumerate(10) shouldBe cons(Pair("a",10),cons(Pair("b",11),cons(Pair("c",12),FSNil)))
//  }
//
//  //
//  // ================ companion object
//  //
//
  test("co.constant") {
    constant(1).take(1).toFList() shouldBe FLCons(1, FLNil)
    constant(1).take(2).toFList() shouldBe FLCons(1, FLCons(1, FLNil))
    constant(1).take(3).toFList() shouldBe FLCons(1, FLCons(1, FLCons(1, FLNil)))
  }

  test("co.unfold") {
    unfold(1){Pair(it, it + 1)}.take(1).toFList() shouldBe FLCons(1, FLNil)
    unfold(1){Pair(it, it + 1)}.take(2).toFList() shouldBe FLCons(1, FLCons(2, FLNil))
    unfold(1){Pair(it, it + 1)}.take(3).toFList() shouldBe FLCons(1, FLCons(2, FLCons(3, FLNil)))
    unfold(2){Pair(it, it + 1)}.take(2).toFList() shouldBe FLCons(2, FLCons(3, FLNil))
  }

  test("co.unfold_dr").config(enabled = false) {
    @OptIn(ExperimentalStdlibApi::class)
    unfold_dr(1){Pair(it, it + 1)}.take(1).toFList() shouldBe FLCons(1, FLNil)
    @OptIn(ExperimentalStdlibApi::class)
    unfold_dr(1){Pair(it, it + 1)}.take(2).toFList() shouldBe FLCons(1, FLCons(2, FLNil))
    @OptIn(ExperimentalStdlibApi::class)
    unfold_dr(1){Pair(it, it + 1)}.take(3).toFList() shouldBe FLCons(1, FLCons(2, FLCons(3, FLNil)))
    @OptIn(ExperimentalStdlibApi::class)
    unfold_dr(2){Pair(it, it + 1)}.take(2).toFList() shouldBe FLCons(2, FLCons(3, FLNil))
  }

  test("co.arithmeticSequence") {
    arithmeticSequence(1, 1).take(1).toFList() shouldBe FLCons(1, FLNil)
    arithmeticSequence(1, 1).take(2).toFList() shouldBe FLCons(1, FLCons(2, FLNil))
    arithmeticSequence(1, 1).take(3).toFList() shouldBe FLCons(1, FLCons(2, FLCons(3, FLNil)))
    arithmeticSequence(2, 3).take(2).toFList() shouldBe FLCons(2, FLCons(5, FLNil))
  }

  test("co.geometricSequence") {
    geometricSequence(2, 3).take(1).toFList() shouldBe FLCons(2, FLNil)
    geometricSequence(2, 3).take(2).toFList() shouldBe FLCons(2, FLCons(6, FLNil))
    geometricSequence(2, 3).take(3).toFList() shouldBe FLCons(2, FLCons(6, FLCons(18, FLNil)))
  }

  test("co.of varargs") {
    intStreamOfNone() shouldBe FSNil
    intStreamOfOne().toFList() shouldBe FLCons(1, FLNil)
    intStreamOfTwo().toFList() shouldBe FLCons(1, FLCons(2, FLNil))
    intStreamOfThree().toFList() shouldBe FLCons(1, FLCons(2, FLCons(3, FLNil)))
  }

//  test("co.of FListIterator").config(enabled = false) {
//    FStream.of(FList.of(emptyArrayOfInt.iterator()).asList().iterator()) shouldBe FSNil
//    FStream.of(FList.of(arrayOf<Int>(1).iterator()).asList().iterator()).toFList() shouldBe FLCons(1,FLNil)
//    FStream.of(FList.of(arrayOf<Int>(1,2).iterator()).asList().iterator()).toFList() shouldBe FLCons(1,FLCons(2,FLNil))
//    FStream.of(FList.of(arrayOf<Int>(1,2,3).iterator()).asList().iterator()).toFList() shouldBe FLCons(1, FLCons(2, FLCons(3,FLNil)))
//  }

  test("co.prepend") {
    FStream.of(*emptyArrayOfInt).prepend(1).toFList() shouldBe FLCons(1, FLNil)
    FStream.of(*arrayOf<Int>(2)).prepend(1).toFList() shouldBe FLCons(1, FLCons(2, FLNil))
    FStream.of(*arrayOf<Int>(2, 3)).prepend(1).toFList() shouldBe FLCons(1, FLCons(2, FLCons(3, FLNil)))
  }

  test("co.append") {
    intStreamOfNone().append(0).toFList() shouldBe FLCons(0, FLNil)
    intStreamOfOne().append(0).toFList() shouldBe FLCons(1, FLCons(0, FLNil))
    FStream.of(*arrayOf<Int>(2, 1)).append(0).toFList() shouldBe FLCons(2, FLCons(1, FLCons(0, FLNil)))
    FStream.of(*arrayOf<Int>(3, 2, 1)).append(0).toFList() shouldBe FLCons(3, FLCons(2, FLCons(1, FLCons(0, FLNil))))
  }

//  test("co.hasSubsequence") {
//    FList.hasSubsequence(intStreamOfNone, FList.of(*emptyArrayOfInt)) shouldBe true
//  }
})
