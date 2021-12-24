package com.xrpn.kapi

import com.xrpn.immutable.FLCons
import com.xrpn.immutable.FLNil
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.xrpn.flist

class FListKListPropertyExtendedTest : FunSpec({

//  val repeats = 10

  beforeTest {}

//  test("fflatMap") {
//    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
//      fl.fflatMap {it -> FLCons(it, FLNil) } shouldBe fl.flatMap {it -> FLCons(it, FLNil) }
//      fl.fflatMap {it -> FLCons(it, FLNil) } shouldBe fl.flatMap {it -> listOf(it) }
//    }
//  }
//
//  test("foldLeft sum") {
//
//    val s = { acc: Int, b: Int -> acc + b}
//
//    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
//      fl.ffoldLeft(0, s) shouldBe fl.fold(0, s)
//    }
//  }
//
//  test("foldLeft diff") {
//
//    val d = { acc: Int, b: Int -> acc - b }
//
//    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
//      fl.ffoldLeft(0, d) shouldBe fl.fold(0, d)
//    }
//  }
//
//  test("foldLeft product") {
//
//    val p = { acc: Int, b: Int -> acc * b}
//
//    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
//      fl.ffoldLeft(0, p) shouldBe fl.fold(0, p)
//    }
//  }
//
//  test("ffoldRight diff") {
//
//    val d = { a: Int, acc: Int -> a - acc}
//
//    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
//      fl.ffoldRight(0, d) shouldBe fl.foldRight(0, d)
//    }
//  }
//
//  test("foldRight sum") {
//
//    val s = { a: Int, acc: Int -> a + acc}
//
//    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
//      fl.ffoldRight(0, s) shouldBe fl.foldRight(0, s)
//    }
//  }
//
//  test("foldRight product") {
//
//    val p = { a: Int, acc: Int -> a * acc}
//
//    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
//      fl.ffoldRight(0, p) shouldBe fl.foldRight(0, p)
//    }
//  }
//
//  test("fmap") {
//    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
//      fl.fmap { it + 1} shouldBe fl.map { it + 1}
//    }
//  }
//
//  test("freduceLeft") {
//
//    val d = { acc: Int, b: Int -> b - acc }
//
//    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
//      fl.freduceLeft(d) shouldBe fl.reduce(d)
//    }
//  }
//
//  test("freduceRight") {
//
//    val d = { b: Int, acc: Int -> acc - b }
//
//    Arb.flist<Int, Int>(Arb.int()).checkAll(repeats) { fl ->
//      fl.freduceRight(d) shouldBe fl.reduceRight(d)
//    }
//  }
})
