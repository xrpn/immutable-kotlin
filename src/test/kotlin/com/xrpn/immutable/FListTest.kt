package com.xrpn.immutable

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

val intListOfNone = FList.of(*arrayOf<Int>())
val intListOfOne = FList.of(*arrayOf<Int>(1))
val intListOfTwo = FList.of(*arrayOf<Int>(1,2))
val intListOfThree = FList.of(*arrayOf<Int>(1,2,3))
val strListOfNone = FList.of(*arrayOf<String>())
val strListOfOne = FList.of(*arrayOf<String>("a"))
val strListOfTwo = FList.of(*arrayOf<String>("a","b"))
val strListOfThree = FList.of(*arrayOf<String>("a","b","c"))

class FListTest : FunSpec({

  beforeTest {
  }

//  afterTest { (testCase, result) ->
//  }

  test("head") {
    intListOfNone.head() shouldBe null
    intListOfOne.head() shouldBe 1
    intListOfTwo.head() shouldBe 1
  }

  test("tail") {
    intListOfNone.tail() shouldBe FLNil
    intListOfOne.tail() shouldBe FLNil
    intListOfTwo.tail() shouldBe FLCons(2,FLNil)
    intListOfThree.tail() shouldBe FLCons(2,FLCons(3,FLNil))
  }

  test("init") {
    intListOfNone.init() shouldBe FLNil
    intListOfOne.init() shouldBe FLNil
    intListOfTwo.init() shouldBe FLCons(1,FLNil)
    intListOfThree.init() shouldBe FLCons(1,FLCons(2,FLNil))
  }

  test("last") {
    intListOfNone.last() shouldBe null
    intListOfOne.last() shouldBe 1
    intListOfTwo.last() shouldBe 2
    intListOfThree.last() shouldBe 3
  }

  test("drop 1") {
    intListOfNone.drop(1) shouldBe FLNil
    intListOfOne.drop(1) shouldBe FLNil
    intListOfTwo.drop(1) shouldBe FLCons(2,FLNil)
    intListOfThree.drop(1) shouldBe FLCons(2,FLCons(3,FLNil))
  }

  test("drop 2") {
    intListOfNone.drop(2) shouldBe FLNil
    intListOfOne.drop(2) shouldBe FLNil
    intListOfTwo.drop(2) shouldBe FLNil
    intListOfThree.drop(2) shouldBe FLCons(3,FLNil)
    FList.of(*arrayOf<Int>(1,2,3,4)).drop(2) shouldBe FLCons(3,FLCons(4,FLNil))
  }

  test("drop 3") {
    intListOfNone.drop(3) shouldBe FLNil
    intListOfOne.drop(3) shouldBe FLNil
    intListOfTwo.drop(3) shouldBe FLNil
    intListOfThree.drop(3) shouldBe FLNil
    FList.of(*arrayOf<Int>(1,2,3,4)).drop(3) shouldBe FLCons(4,FLNil)
  }

  test("findFirst") {
    intListOfNone.findFirst { it > 1 } shouldBe Triple(FLNil, null, FLNil)
    intListOfOne.findFirst { it > 1 }  shouldBe Triple(FLCons(1,FLNil), null, FLNil)
    FList.of(*arrayOf<Int>(2,1)).findFirst { it > 1 }  shouldBe Triple(FLNil, 2, FLCons(1,FLNil))
    FList.of(*arrayOf<Int>(3,2,1)).findFirst { it > 1 }  shouldBe Triple(FLNil, 3, FLCons(2, FLCons(1,FLNil)))
    FList.of(*arrayOf<Int>(3,2,1,0)).findFirst { it == 2 } shouldBe Triple(FLCons(3, FLNil), 2, FLCons(1,FLCons(0,FLNil)))
    FList.of(*arrayOf<Int>(3,2,1,0)).findFirst { it < 3 } shouldBe Triple(FLCons(3, FLNil), 2, FLCons(1,FLCons(0,FLNil)))
  }

  test("dropWhile") {
    intListOfNone.dropWhile { it > 1 } shouldBe FLNil
    intListOfOne.dropWhile { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(2,1)).dropWhile { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(3,2,1)).dropWhile { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(3,2,1,0)).dropWhile { it > 1 } shouldBe FLCons(1,FLCons(0,FLNil))
  }

  test("dropFirst") {
    intListOfNone.dropFirst { it > 1 } shouldBe FLNil
    intListOfOne.dropFirst { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(2,1)).dropFirst { it > 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(3,2,1)).dropFirst { it > 1 }  shouldBe FLCons(2, FLCons(1,FLNil))
    FList.of(*arrayOf<Int>(3,2,1,0)).dropFirst { it == 2 } shouldBe FLCons(3, FLCons(1,FLCons(0,FLNil)))
    FList.of(*arrayOf<Int>(3,2,1,0)).dropFirst { it < 3 } shouldBe FLCons(3, FLCons(1,FLCons(0,FLNil)))
  }

  test("take 1") {
    intListOfNone.take(1) shouldBe FLNil
    intListOfOne.take(1) shouldBe intListOfOne
    intListOfTwo.take(1) shouldBe intListOfOne
    intListOfThree.take(1) shouldBe intListOfOne
  }

  test("take 2") {
    intListOfNone.take(2) shouldBe FLNil
    intListOfOne.take(2) shouldBe intListOfOne
    intListOfTwo.take(2) shouldBe intListOfTwo
    intListOfThree.take(2) shouldBe intListOfTwo
    FList.of(*arrayOf<Int>(1,2,3,4)).take(2) shouldBe intListOfTwo
  }

  test("take 3") {
    intListOfNone.take(3) shouldBe FLNil
    intListOfOne.take(3) shouldBe intListOfOne
    intListOfTwo.take(3) shouldBe intListOfTwo
    intListOfThree.take(3) shouldBe intListOfThree
    FList.of(*arrayOf<Int>(1,2,3,4)).take(3) shouldBe intListOfThree
  }

  test("takeWhile") {
    intListOfNone.takeWhile { it > 1 } shouldBe FLNil
    intListOfOne.takeWhile { it == 1 }  shouldBe FLCons(1,FLNil)
    FList.of(*arrayOf<Int>(2,1)).takeWhile { it > 1 }  shouldBe FLCons(2,FLNil)
    FList.of(*arrayOf<Int>(3,2,1)).takeWhile { it > 1 }  shouldBe FLCons(3,FLCons(2,FLNil))
    FList.of(*arrayOf<Int>(3,2,1,0)).takeWhile { it != 1 } shouldBe FLCons(3,FLCons(2,FLNil))
  }

  test("foldRight sum") {
    intListOfNone.foldRight(0, { a, b -> a + b}) shouldBe 0
    intListOfOne.foldRight(0, { a, b -> a + b})  shouldBe 1
    FList.of(*arrayOf<Int>(2,1)).foldRight(0, {a,b -> a+b})  shouldBe 3
    FList.of(*arrayOf<Int>(3,2,1)).foldRight(0, {a,b -> a+b})  shouldBe 6
    FList.of(*arrayOf<Int>(3,2,1,0)).foldRight(0, {a,b -> a+b}) shouldBe 6
  }

  test("foldRight product") {
    intListOfNone.foldRight(1, { a, b -> a * b}) shouldBe 1
    intListOfOne.foldRight(1, { a, b -> a * b})  shouldBe 1
    FList.of(*arrayOf<Int>(2,1)).foldRight(1, {a,b -> a*b})  shouldBe 2
    FList.of(*arrayOf<Int>(3,2,1)).foldRight(1, {a,b -> a*b})  shouldBe 6
    FList.of(*arrayOf<Int>(3,2,1,0)).foldRight(1, {a,b -> a*b}) shouldBe 0
  }

  test("copy") {
    intListOfNone.copy() shouldBe FLNil
    intListOfOne.copy() shouldBe FLCons(1,FLNil)
    intListOfTwo.copy() shouldBe FLCons(1,FLCons(2,FLNil))
  }

  test("reverse") {
    intListOfNone.reverse() shouldBe FLNil
    intListOfOne.reverse() shouldBe FLCons(1,FLNil)
    intListOfTwo.reverse() shouldBe FLCons(2,FLCons(1,FLNil))
  }

  test("foldLeft sum") {
    intListOfNone.foldLeft(0, { a, b -> a + b}) shouldBe 0
    intListOfOne.foldLeft(0, { a, b -> a + b})  shouldBe 1
    FList.of(*arrayOf<Int>(2,1)).foldLeft(0, {a,b -> a+b})  shouldBe 3
    FList.of(*arrayOf<Int>(3,2,1)).foldLeft(0, {a,b -> a+b})  shouldBe 6
    FList.of(*arrayOf<Int>(3,2,1,0)).foldLeft(0, {a,b -> a+b}) shouldBe 6
  }

  test("foldLeft product") {
    intListOfNone.foldLeft(1, { a, b -> a * b}) shouldBe 1
    intListOfOne.foldLeft(1, { a, b -> a * b})  shouldBe 1
    FList.of(*arrayOf<Int>(2,1)).foldLeft(1, {a,b -> a*b})  shouldBe 2
    FList.of(*arrayOf<Int>(3,2,1)).foldLeft(1, {a,b -> a*b})  shouldBe 6
    FList.of(*arrayOf<Int>(3,2,1,0)).foldLeft(1, {a,b -> a*b}) shouldBe 0
  }

  test("len") {
    intListOfNone.len() shouldBe 0
    intListOfOne.len() shouldBe 1
    intListOfTwo.len() shouldBe 2
  }

  test("map") {
    intListOfNone.map { it + 1} shouldBe FLNil
    intListOfOne.map { it + 1} shouldBe FLCons(2,FLNil)
    intListOfTwo.map { it + 1} shouldBe FLCons(2,FLCons(3,FLNil))
  }

  test("flatMap") {
    intListOfNone.flatMap {it -> FLCons(it, FLNil)} shouldBe FLNil
    intListOfOne.flatMap {it -> FLCons(it, FLNil)} shouldBe FLCons(1,FLNil)
    fun arrayBuilderConst(arg: Int) = Array<Int>(arg) { _ -> arg }
    intListOfTwo.flatMap {FList.of(*arrayBuilderConst(it))} shouldBe FLCons(1,FLCons(2,FLCons(2,FLNil)))
    fun arrayBuilderIncrement(arg: Int) = Array<Int>(arg) { i -> arg + i }
    intListOfTwo.flatMap {FList.of(*arrayBuilderIncrement(it))} shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
    intListOfThree.flatMap {FList.of(*arrayBuilderIncrement(it))} shouldBe
        FLCons(1,FLCons(2,FLCons(3,FLCons(3,FLCons(4,FLCons(5,FLNil))))))
    intListOfThree.flatMap { i -> FList.of(i, i) } shouldBe
        FLCons(1,FLCons(1,FLCons(2,FLCons(2,FLCons(3,FLCons(3,FLNil))))))
  }

  test("filter") {
    intListOfNone.filter {0 == it % 2} shouldBe FLNil
    intListOfOne.filter {0 == it % 2} shouldBe FLNil
    intListOfTwo.filter {0 == it % 2} shouldBe FLCons(2,FLNil)
    intListOfThree.filter {0 == it % 2} shouldBe FLCons(2,FLNil)
    FList.of(*arrayOf<Int>(1,2,3,4)).filter {0 == it % 2} shouldBe FLCons(2,FLCons(4,FLNil))
  }

  test("filterNot") {
    intListOfNone.filterNot {0 == it % 2} shouldBe FLNil
    intListOfOne.filterNot {0 == it % 2} shouldBe FLCons(1,FLNil)
    intListOfTwo.filterNot {0 == it % 2} shouldBe FLCons(1,FLNil)
    intListOfThree.filterNot {0 == it % 2} shouldBe FLCons(1,FLCons(3,FLNil))
    FList.of(*arrayOf<Int>(1,2,3,4)).filterNot {0 == it % 2} shouldBe FLCons(1,FLCons(3,FLNil))
  }

  test("zipWith") {
    intListOfNone.zipWith(FList.of(*arrayOf<String>())){a, b -> Pair(a,b)} shouldBe FLNil
    intListOfOne.zipWith(FList.of(*arrayOf<String>())){a, b -> Pair(a,b)} shouldBe FLNil
    intListOfNone.zipWith(FList.of(*arrayOf<String>("a"))){a, b -> Pair(a,b)} shouldBe FLNil
    intListOfOne.zipWith(FList.of(*arrayOf<String>("a"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLNil)
    intListOfTwo.zipWith(FList.of(*arrayOf<String>("a"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLNil)
    intListOfOne.zipWith(FList.of(*arrayOf<String>("a","b"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLNil)
    intListOfTwo.zipWith(FList.of(*arrayOf<String>("a", "b"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
    intListOfThree.zipWith(FList.of(*arrayOf<String>("a", "b"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
    intListOfTwo.zipWith(FList.of(*arrayOf<String>("a", "b", "c"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
  }

  test("zipWith iterable") {
    intListOfNone.zipWith(arrayOf<String>().iterator()) shouldBe FLNil
    intListOfOne.zipWith(arrayOf<String>().iterator()) shouldBe FLNil
    intListOfNone.zipWith(arrayOf("a").iterator()) shouldBe FLNil
    intListOfOne.zipWith(arrayOf("a").iterator()) shouldBe FLCons(Pair(1,"a"),FLNil)
    intListOfTwo.zipWith(arrayOf("a").iterator()) shouldBe FLCons(Pair(1,"a"),FLNil)
    intListOfOne.zipWith(arrayOf("a","b").iterator()) shouldBe FLCons(Pair(1,"a"),FLNil)
    intListOfTwo.zipWith(arrayOf("a","b").iterator()) shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
    intListOfThree.zipWith(arrayOf("a","b").iterator()) shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
    intListOfTwo.zipWith(arrayOf("a","b","c").iterator()) shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
  }

  test("enumerate") {
    FList.of(*arrayOf<String>()).enumerate() shouldBe FLNil
    strListOfOne.enumerate() shouldBe FLCons(Pair("a",0),FLNil)
    strListOfTwo.enumerate() shouldBe FLCons(Pair("a",0),FLCons(Pair("b",1),FLNil))
    strListOfThree.enumerate() shouldBe FLCons(Pair("a",0),FLCons(Pair("b",1),FLCons(Pair("c",2),FLNil)))
  }

  test("enumerate offset") {
    FList.of(*arrayOf<String>()).enumerate(10) shouldBe FLNil
    strListOfOne.enumerate(10) shouldBe FLCons(Pair("a",10),FLNil)
    strListOfTwo.enumerate(10) shouldBe FLCons(Pair("a",10),FLCons(Pair("b",11),FLNil))
    strListOfThree.enumerate(10) shouldBe FLCons(Pair("a",10),FLCons(Pair("b",11),FLCons(Pair("c",12),FLNil)))
  }

  //
  // ================ companion object
  //

  test("co.of varargs") {
    intListOfNone shouldBe FLNil
    intListOfOne shouldBe FLCons(1,FLNil)
    intListOfTwo shouldBe FLCons(1,FLCons(2,FLNil))
    intListOfThree shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
  }

  test("co.of iterator") {
    FList.of(arrayOf<Int>().iterator()) shouldBe FLNil
    FList.of(arrayOf<Int>(1).iterator()) shouldBe FLCons(1,FLNil)
    FList.of(arrayOf<Int>(1,2).iterator()) shouldBe FLCons(1,FLCons(2,FLNil))
    FList.of(arrayOf<Int>(1,2,3).iterator()) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
  }

  test("co.equal") {
    FList.equal2(intListOfNone, FList.of(*arrayOf<Int>())) shouldBe true
    FList.equal2(FList.of(*arrayOf(1)), FList.of(*arrayOf<Int>())) shouldBe false
    FList.equal2(intListOfNone, FList.of(*arrayOf(1))) shouldBe false
    FList.equal2(intListOfOne, FList.of(*arrayOf<Int>(1))) shouldBe true
    FList.equal2(FList.of(*arrayOf(1)), FList.of(*arrayOf<Int>(1, 2))) shouldBe false
    FList.equal2(FList.of(*arrayOf<Int>(1, 2)), FList.of(*arrayOf(1))) shouldBe false
    FList.equal2(FList.of(*arrayOf<Int>(1, 2)), FList.of(*arrayOf(1, 2))) shouldBe true
  }

//  test("co.reduceLeft") {
//    FList.reduceLeft(intListOfNone) { b, a -> a - b } shouldBe null
//    FList.reduceLeft(FList.of(*arrayOf(1))) { b, a -> a - b } shouldBe null
//    FList.reduceLeft(FList.of(*arrayOf<Int>(2, 1))) { b, a -> a - b } shouldBe 1
//    FList.reduceLeft(FList.of(*arrayOf<Int>(3, 1, 1))) { b, a -> a - b } shouldBe 1
//    FList.reduceLeft(FList.of(*arrayOf<Int>(4, 1, 2))) { b, a -> a - b } shouldBe 1
//  }

  test("co.setHead") {
    FList.setHead(1, FList.of(*arrayOf<Int>())) shouldBe FLCons(1,FLNil)
    FList.setHead(1, FList.of(*arrayOf<Int>(2))) shouldBe FLCons(1,FLCons(2,FLNil))
    FList.setHead(1, FList.of(*arrayOf<Int>(2, 3))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
  }

  test("co.setLast") {
    FList.setLast(intListOfNone,0) shouldBe FLCons(0,FLNil)
    FList.setLast(intListOfOne,0) shouldBe FLCons(1,FLCons(0,FLNil))
    FList.setLast(FList.of(*arrayOf<Int>(2,1)),0) shouldBe FLCons(2, FLCons(1,FLCons(0,FLNil)))
    FList.setLast(FList.of(*arrayOf<Int>(3,2,1)), 0) shouldBe
            FLCons(3,FLCons(2,FLCons(1,FLCons(0,FLNil))))
  }

  test("co.append") {
    FList.append(intListOfNone, FList.of(*arrayOf<Int>())) shouldBe FLNil
    FList.append(intListOfNone, FList.of(*arrayOf<Int>(2))) shouldBe FLCons(2,FLNil)
    FList.append(intListOfOne, FList.of(*arrayOf<Int>())) shouldBe FLCons(1,FLNil)
    FList.append(intListOfOne, FList.of(*arrayOf<Int>(2))) shouldBe FLCons(1,FLCons(2,FLNil))
    FList.append(intListOfTwo, FList.of(*arrayOf<Int>(3,4))) shouldBe
            FLCons(1,FLCons(2,FLCons(3,FLCons(4,FLNil))))
  }

  test("co.flatten") {
    FList.appendNested(FList.of(*arrayOf(FList.of(*arrayOf<Int>())))) shouldBe FLNil
    FList.appendNested(FList.of(*arrayOf(FList.of(*arrayOf<Int>(1))))) shouldBe FLCons(1,FLNil)
    FList.appendNested(FList.of(*arrayOf(intListOfNone, FList.of(*arrayOf<Int>())))) shouldBe FLNil
    FList.appendNested(FList.of(*arrayOf(intListOfNone, FList.of(*arrayOf<Int>(1))))) shouldBe FLCons(1,FLNil)
    FList.appendNested(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>())))) shouldBe FLCons(1,FLNil)
    FList.appendNested(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>(2))))) shouldBe FLCons(1,FLCons(2,FLNil))
    FList.appendNested(FList.of(*arrayOf(intListOfTwo, FList.of(*arrayOf<Int>(3))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
    FList.appendNested(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>(2,3))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
    FList.appendNested(FList.of(*arrayOf(intListOfTwo, FList.of(*arrayOf<Int>(3,4))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLCons(4,FLNil))))
  }

  test("co.hasSubsequence") {
    FList.hasSubsequence(intListOfNone, FList.of(*arrayOf<Int>())) shouldBe true
  }
})
