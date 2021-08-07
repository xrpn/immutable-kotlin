package com.xrpn.kapi

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.xrpn.flistAsKList

class FListKListPropertyTest : FunSpec({

  beforeTest {}

  val repeats = 10
  fun <Z: Comparable<Z>> matchEqual(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle == aut }
  fun <Z: Comparable<Z>> matchLessThan(oracle: Z): (Z) -> Boolean = { aut: Z -> oracle < aut }
  fun middle(l: List<Int>): Int {
    val max = l.maxOrNull()
    val min = l.minOrNull()
    return (max!! - min!!) / 2
  }

  test("equals") {
    Arb.flistAsKList<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      (fl.size >= 0) shouldBe true
      (fl == fl) shouldBe true
      (l == fl) shouldBe true
      (fl == l) shouldBe true
    }
    Arb.flistAsKList<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      (fl.size >= 0) shouldBe true
      (fl == fl) shouldBe true
      (l == fl) shouldBe true
      (fl == l) shouldBe true
    }
    Arb.flistAsKList<String, String>(Arb.string(0..10)).checkAll(repeats) { fl ->
      val l = fl.toList()
      (fl.size >= 0) shouldBe true
      (fl == fl) shouldBe true
      (l == fl) shouldBe true
      (fl == l) shouldBe true
    }
  }

  test("get by index") {
    Arb.flistAsKList<String, String>(Arb.string(0..10)).checkAll(repeats) { fl ->
      val l = fl.toList()
      for (ix in fl.indices) fl[ix] shouldBe l[ix]
    }
  }

  test("indexOf") {
    Arb.flistAsKList<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val selection = l[ix]
      fl.indexOf(selection) shouldBe l.indexOfFirst(matchEqual(selection))
    }
  }

  test("lastIndexOf") {
    Arb.flistAsKList<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val selection = l[ix]
      fl.lastIndexOf(selection) shouldBe l.indexOfLast(matchEqual(selection))
    }
  }

  test("subList") {
    Arb.flistAsKList<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val startIx = kotlin.random.Random.Default.nextInt(fl.size)
      val endIx = startIx + kotlin.random.Random.Default.nextInt(fl.size-startIx)
      fl.subList(startIx, endIx) shouldBe l.subList(startIx, endIx)
    }
  }

  test("elementAt") {
    Arb.flistAsKList<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      fl.elementAt(ix) shouldBe l.elementAt(ix)
    }
  }

  test("elementAtOrElse") {

    fun elseValue(ix: Int): Int = Int.MAX_VALUE - ix

    Arb.flistAsKList<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      for (ix in 0..fl.size) fl.elementAtOrElse(ix, ::elseValue) shouldBe l.elementAtOrElse(ix, ::elseValue)
    }
  }

  test("findLast") {
    Arb.flistAsKList<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val selection = l[ix]
      fl.findLast(matchEqual(selection)) shouldBe selection
    }
  }

  test("first") {
    Arb.flistAsKList<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.first() shouldBe l.first()
      fl.firstOrNull() shouldBe l.firstOrNull()
    }
  }

  test("getOrElse") {

    fun elseValue(ix: Int): Int = Int.MAX_VALUE - ix

    Arb.flistAsKList<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      for (ix in 0..fl.size) fl.getOrElse(ix, ::elseValue) shouldBe l.getOrElse(ix, ::elseValue)
    }
  }

  test("getOrNull") {
    Arb.flistAsKList<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      for (ix in 0..fl.size) fl.getOrNull(ix) shouldBe l.getOrNull(ix)
    }
  }

  test("indexOfirst") {
    Arb.flistAsKList<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val selection = l[ix]
      fl.indexOfFirst(matchEqual(selection)) shouldBe l.indexOfFirst(matchEqual(selection))
    }
  }

  test("indexOfLast") {
    Arb.flistAsKList<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val selection = l[ix]
      fl.indexOfLast(matchEqual(selection)) shouldBe l.indexOfLast(matchEqual(selection))
    }
  }

  test("last") {
    Arb.flistAsKList<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.last() shouldBe l.last()
      fl.lastOrNull() shouldBe l.lastOrNull()
    }
  }

  test("last (find)") {
    Arb.flistAsKList<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ix = kotlin.random.Random.Default.nextInt(fl.size)
      val selection = l[ix]
      fl.last(matchEqual(selection)) shouldBe selection
    }
  }

  test("dropLast") {
    Arb.flistAsKList<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val n = kotlin.random.Random.Default.nextInt(fl.size)
      fl.dropLast(n) shouldBe l.dropLast(n)
    }
  }

  test("dropLastWhile") {
    Arb.flistAsKList<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ora = middle(l)
      fl.dropLastWhile(matchLessThan(ora)) shouldBe l.dropLastWhile(matchLessThan(ora))
    }
  }

  test("slice of range") {
    Arb.flistAsKList<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val n = kotlin.random.Random.Default.nextInt(fl.size)
      val m = kotlin.random.Random.Default.nextInt(fl.size)
      val range = if (n < m) n..m else m..n
      fl.slice(range) shouldBe l.slice(range)
    }
  }

  test("slice of iterable") {
    Arb.flistAsKList<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val n = kotlin.random.Random.Default.nextInt(fl.size)
      val m = kotlin.random.Random.Default.nextInt(fl.size)
      val range = if (n < m) n..m else m..n
      val ml = range.toMutableList()
      val headIx = ml[0]
      var lastIx = ml[ml.size - 1]
      ml[0] = lastIx
      ml[ml.size - 1] = headIx
      fl.slice(ml) shouldBe l.slice(ml)
    }
  }

  test("takeLast") {
    Arb.flistAsKList<Char, Char>(Arb.char()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val n = kotlin.random.Random.Default.nextInt(fl.size)
      fl.takeLast(n) shouldBe l.takeLast(n)
    }
  }

  test("takeLastWhile") {
    Arb.flistAsKList<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      val ora = middle(l)
      fl.takeLastWhile(matchLessThan(ora)) shouldBe l.takeLastWhile(matchLessThan(ora))
    }
  }

  test("foldRight sum") {
    val f = { acc: Int, b: Int -> ((acc.toLong() + b.toLong()) % Int.MAX_VALUE.toLong()).toInt() }

    Arb.flistAsKList<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.foldRight(1, f) shouldBe l.foldRight(1, f)
    }
  }

  test("foldRight product") {
    val f = { acc: Int, b: Int -> ((acc.toLong() * b.toLong()) % Int.MAX_VALUE.toLong()).toInt() }

    Arb.flistAsKList<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.foldRight(1, f) shouldBe l.foldRight(1, f)
    }
  }

  test("reduceRight") {

    val f = { b: Int, a: Int -> ((a.toLong() - b.toLong()) % Int.MAX_VALUE.toLong()).toInt() }

    Arb.flistAsKList<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
      val l = fl.toList()
      fl.reduceRight(f) shouldBe l.reduceRight(f)
    }
  }

//  afterTest { (testCase, result) ->
//  }

//  test("head") {
//    intListOfNone.head() shouldBe null
//    intListOfOne.head() shouldBe 1
//    intListOfTwo.head() shouldBe 1
//  }
//
//  test("tail") {
//    intListOfNone.tail() shouldBe FLNil
//    intListOfOne.tail() shouldBe FLNil
//    intListOfTwo.tail() shouldBe FLCons(2,FLNil)
//    intListOfThree.tail() shouldBe FLCons(2,FLCons(3,FLNil))
//  }
//
//  test("init") {
//    intListOfNone.init() shouldBe FLNil
//    intListOfOne.init() shouldBe FLNil
//    intListOfTwo.init() shouldBe FLCons(1,FLNil)
//    intListOfThree.init() shouldBe FLCons(1,FLCons(2,FLNil))
//  }

//  test("findFirst") {
//    intListOfNone.findFirst { it > 1 } shouldBe Triple(FLNil, null, FLNil)
//    intListOfOne.findFirst { it > 1 }  shouldBe Triple(FLCons(1,FLNil), null, FLNil)
//    FList.of(*arrayOf<Int>(2,1)).findFirst { it > 1 }  shouldBe Triple(FLNil, 2, FLCons(1,FLNil))
//    FList.of(*arrayOf<Int>(3,2,1)).findFirst { it > 1 }  shouldBe Triple(FLNil, 3, FLCons(2, FLCons(1,FLNil)))
//    FList.of(*arrayOf<Int>(3,2,1,0)).findFirst { it == 2 } shouldBe Triple(FLCons(3, FLNil), 2, FLCons(1,FLCons(0,FLNil)))
//    FList.of(*arrayOf<Int>(3,2,1,0)).findFirst { it < 3 } shouldBe Triple(FLCons(3, FLNil), 2, FLCons(1,FLCons(0,FLNil)))
//  }

//  test("dropFirst") {
//    intListOfNone.dropFirst { it > 1 } shouldBe FLNil
//    intListOfOne.dropFirst { it > 1 }  shouldBe FLCons(1,FLNil)
//    FList.of(*arrayOf<Int>(2,1)).dropFirst { it > 1 }  shouldBe FLCons(1,FLNil)
//    FList.of(*arrayOf<Int>(3,2,1)).dropFirst { it > 1 }  shouldBe FLCons(2, FLCons(1,FLNil))
//    FList.of(*arrayOf<Int>(3,2,1,0)).dropFirst { it == 2 } shouldBe FLCons(3, FLCons(1,FLCons(0,FLNil)))
//    FList.of(*arrayOf<Int>(3,2,1,0)).dropFirst { it < 3 } shouldBe FLCons(3, FLCons(1,FLCons(0,FLNil)))
//  }


//  test("copy") {
//    intListOfNone.copy() shouldBe FLNil
//    intListOfOne.copy() shouldBe FLCons(1,FLNil)
//    intListOfTwo.copy() shouldBe FLCons(1,FLCons(2,FLNil))
//  }
//
//  test("reverse") {
//    intListOfNone.freverse() shouldBe FLNil
//    intListOfOne.freverse() shouldBe FLCons(1,FLNil)
//    intListOfTwo.freverse() shouldBe FLCons(2,FLCons(1,FLNil))
//  }
//
//  test("foldLeft sum") {
//    intListOfNone.foldLeft(0, { a, b -> a + b}) shouldBe 0
//    intListOfOne.foldLeft(0, { a, b -> a + b})  shouldBe 1
//    FList.of(*arrayOf<Int>(2,1)).ffoldLeft(0, { a, b -> a+b})  shouldBe 3
//    FList.of(*arrayOf<Int>(3,2,1)).ffoldLeft(0, { a, b -> a+b})  shouldBe 6
//    FList.of(*arrayOf<Int>(3,2,1,0)).ffoldLeft(0, { a, b -> a+b}) shouldBe 6
//  }
//
//  test("foldLeft product") {
//    intListOfNone.foldLeft(1, { a, b -> a * b}) shouldBe 1
//    intListOfOne.foldLeft(1, { a, b -> a * b})  shouldBe 1
//    FList.of(*arrayOf<Int>(2,1)).ffoldLeft(1, { a, b -> a*b})  shouldBe 2
//    FList.of(*arrayOf<Int>(3,2,1)).ffoldLeft(1, { a, b -> a*b})  shouldBe 6
//    FList.of(*arrayOf<Int>(3,2,1,0)).ffoldLeft(1, { a, b -> a*b}) shouldBe 0
//  }
//
//  test("len") {
//    intListOfNone.len() shouldBe 0
//    intListOfOne.len() shouldBe 1
//    intListOfTwo.len() shouldBe 2
//  }


//  test("zipWith") {
//    intListOfNone.zipWith(FList.of(*arrayOf<String>())){a, b -> Pair(a,b)} shouldBe FLNil
//    intListOfOne.zipWith(FList.of(*arrayOf<String>())){a, b -> Pair(a,b)} shouldBe FLNil
//    intListOfNone.zipWith(FList.of(*arrayOf<String>("a"))){a, b -> Pair(a,b)} shouldBe FLNil
//    intListOfOne.zipWith(FList.of(*arrayOf<String>("a"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLNil)
//    intListOfTwo.zipWith(FList.of(*arrayOf<String>("a"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLNil)
//    intListOfOne.zipWith(FList.of(*arrayOf<String>("a","b"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLNil)
//    intListOfTwo.zipWith(FList.of(*arrayOf<String>("a", "b"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
//    intListOfThree.zipWith(FList.of(*arrayOf<String>("a", "b"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
//    intListOfTwo.zipWith(FList.of(*arrayOf<String>("a", "b", "c"))){a, b -> Pair(a,b)} shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
//  }
//
//  test("zipWith iterable") {
//    intListOfNone.zipWith(arrayOf<String>().iterator()) shouldBe FLNil
//    intListOfOne.zipWith(arrayOf<String>().iterator()) shouldBe FLNil
//    intListOfNone.zipWith(arrayOf("a").iterator()) shouldBe FLNil
//    intListOfOne.zipWith(arrayOf("a").iterator()) shouldBe FLCons(Pair(1,"a"),FLNil)
//    intListOfTwo.zipWith(arrayOf("a").iterator()) shouldBe FLCons(Pair(1,"a"),FLNil)
//    intListOfOne.zipWith(arrayOf("a","b").iterator()) shouldBe FLCons(Pair(1,"a"),FLNil)
//    intListOfTwo.zipWith(arrayOf("a","b").iterator()) shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
//    intListOfThree.zipWith(arrayOf("a","b").iterator()) shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
//    intListOfTwo.zipWith(arrayOf("a","b","c").iterator()) shouldBe FLCons(Pair(1,"a"),FLCons(Pair(2,"b"),FLNil))
//  }
//
//  test("enumerate") {
//    FList.of(*arrayOf<String>()).enumerate() shouldBe FLNil
//    strListOfOne.enumerate() shouldBe FLCons(Pair("a",0),FLNil)
//    strListOfTwo.enumerate() shouldBe FLCons(Pair("a",0),FLCons(Pair("b",1),FLNil))
//    strListOfThree.enumerate() shouldBe FLCons(Pair("a",0),FLCons(Pair("b",1),FLCons(Pair("c",2),FLNil)))
//  }
//
//  test("enumerate offset") {
//    FList.of(*arrayOf<String>()).enumerate(10) shouldBe FLNil
//    strListOfOne.enumerate(10) shouldBe FLCons(Pair("a",10),FLNil)
//    strListOfTwo.enumerate(10) shouldBe FLCons(Pair("a",10),FLCons(Pair("b",11),FLNil))
//    strListOfThree.enumerate(10) shouldBe FLCons(Pair("a",10),FLCons(Pair("b",11),FLCons(Pair("c",12),FLNil)))
//  }

  //
  // ================ companion object
  //

//  test("co.of varargs") {
//    intListOfNone shouldBe FLNil
//    intListOfOne shouldBe FLCons(1,FLNil)
//    intListOfTwo shouldBe FLCons(1,FLCons(2,FLNil))
//    intListOfThree shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
//  }

//  test("co.of iterator") {
//    FList.of(arrayOf<Int>().iterator()) shouldBe FLNil
//    FList.of(arrayOf<Int>(1).iterator()) shouldBe FLCons(1,FLNil)
//    FList.of(arrayOf<Int>(1,2).iterator()) shouldBe FLCons(1,FLCons(2,FLNil))
//    FList.of(arrayOf<Int>(1,2,3).iterator()) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
//  }

//  test("co.reduceLeft") {
//    FList.reduceLeft(intListOfNone) { b, a -> a - b } shouldBe null
//    FList.reduceLeft(FList.of(*arrayOf(1))) { b, a -> a - b } shouldBe null
//    FList.reduceLeft(FList.of(*arrayOf<Int>(2, 1))) { b, a -> a - b } shouldBe 1
//    FList.reduceLeft(FList.of(*arrayOf<Int>(3, 1, 1))) { b, a -> a - b } shouldBe 1
//    FList.reduceLeft(FList.of(*arrayOf<Int>(4, 1, 2))) { b, a -> a - b } shouldBe 1
//  }

//  test("co.setHead") {
//    FList.setHead(1, FList.of(*arrayOf<Int>())) shouldBe FLCons(1,FLNil)
//    FList.setHead(1, FList.of(*arrayOf<Int>(2))) shouldBe FLCons(1,FLCons(2,FLNil))
//    FList.setHead(1, FList.of(*arrayOf<Int>(2, 3))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
//  }
//
//  test("co.setLast") {
//    FList.setLast(intListOfNone,0) shouldBe FLCons(0,FLNil)
//    FList.setLast(intListOfOne,0) shouldBe FLCons(1,FLCons(0,FLNil))
//    FList.setLast(FList.of(*arrayOf<Int>(2,1)),0) shouldBe FLCons(2, FLCons(1,FLCons(0,FLNil)))
//    FList.setLast(FList.of(*arrayOf<Int>(3,2,1)), 0) shouldBe
//            FLCons(3,FLCons(2,FLCons(1,FLCons(0,FLNil))))
//  }
//
//  test("co.append") {
//    FList.append(intListOfNone, FList.of(*arrayOf<Int>())) shouldBe FLNil
//    FList.append(intListOfNone, FList.of(*arrayOf<Int>(2))) shouldBe FLCons(2,FLNil)
//    FList.append(intListOfOne, FList.of(*arrayOf<Int>())) shouldBe FLCons(1,FLNil)
//    FList.append(intListOfOne, FList.of(*arrayOf<Int>(2))) shouldBe FLCons(1,FLCons(2,FLNil))
//    FList.append(intListOfTwo, FList.of(*arrayOf<Int>(3,4))) shouldBe
//            FLCons(1,FLCons(2,FLCons(3,FLCons(4,FLNil))))
//  }
//
//  test("co.flatten") {
//    FList.appendNested(FList.of(*arrayOf(FList.of(*arrayOf<Int>())))) shouldBe FLNil
//    FList.appendNested(FList.of(*arrayOf(FList.of(*arrayOf<Int>(1))))) shouldBe FLCons(1,FLNil)
//    FList.appendNested(FList.of(*arrayOf(intListOfNone, FList.of(*arrayOf<Int>())))) shouldBe FLNil
//    FList.appendNested(FList.of(*arrayOf(intListOfNone, FList.of(*arrayOf<Int>(1))))) shouldBe FLCons(1,FLNil)
//    FList.appendNested(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>())))) shouldBe FLCons(1,FLNil)
//    FList.appendNested(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>(2))))) shouldBe FLCons(1,FLCons(2,FLNil))
//    FList.appendNested(FList.of(*arrayOf(intListOfTwo, FList.of(*arrayOf<Int>(3))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
//    FList.appendNested(FList.of(*arrayOf(intListOfOne, FList.of(*arrayOf<Int>(2,3))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLNil)))
//    FList.appendNested(FList.of(*arrayOf(intListOfTwo, FList.of(*arrayOf<Int>(3,4))))) shouldBe FLCons(1,FLCons(2,FLCons(3,FLCons(4,FLNil))))
//  }
//
//  test("co.hasSubsequence") {
//    FList.hasSubsequence(intListOfNone, FList.of(*arrayOf<Int>())) shouldBe true
//  }
})
