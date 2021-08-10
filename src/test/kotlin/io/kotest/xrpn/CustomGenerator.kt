package io.kotest.xrpn

import com.xrpn.immutable.FLCons
import com.xrpn.immutable.FLNil
import com.xrpn.immutable.FList
import com.xrpn.immutable.FSet
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive

//data class Person(val name: String, val age: Int)
//
//val personArb = arbitrary { rs ->
//    val name = Arb.string(10..12).next(rs)
//    val age = Arb.int(21, 150).next(rs)
//    Person(name, age)
//}

//val singleDigitPrimes = listOf(2,3,5,7).exhaustive()
//fun isPrime(aut: Int) = aut <= 7
//
//class PropertyExample: StringSpec({
//    "testing single digit primes" {
//        checkAll(singleDigitPrimes) { prime ->
//            isPrime(prime) shouldBe true
//            isPrime(prime * prime) shouldBe false
//        }
//    }
//})

fun <A: Any, B> Arb.Companion.flistAsCollection(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a as A }
): Arb<Collection<A>> {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.list(arbB, range).map { bs -> FList.ofMap(bs, f) }
}

fun <A: Any, B> Arb.Companion.flistAsKList(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a as A }
): Arb<List<A>> {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.list(arbB, range).map { bs -> FList.ofMap(bs, f) }
}

fun <A: Any, B> Arb.Companion.flist(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a as A }
): Arb<FList<A>> {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.list(arbB, range).map { bs -> FList.ofMap(bs, f) }
}

fun <A: Any, B> Arb.Companion.fsetAsCollection(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a as A }
): Arb<Collection<A>> {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.list(arbB, range).map { bs -> FSet.ofMap(bs, f) }
}


//fun <A, B, T: Any> let(genA: Gen<A>, genB: (A) -> Gen<B>, bindFn: (A, B) -> T): Arb<T> {
//    return arb { rs ->
//        val iterA = genA.generate(rs).iterator()
//
//        generateSequence {
//            val a = iterA.next()
//            val iterB = genB(a.value).generate(rs).iterator()
//            val b = iterB.next()
//            bindFn(a.value, b.value)
//        }
//    }
//}
//
//fun <A, B> let(genA: Gen<A>, genBFn: (A) -> Gen<B>): Arb<Pair<A, B>> {
//    return arb { rs ->
//        val iterA = genA.generate(rs).iterator()
//
//        generateSequence {
//            val a = iterA.next().value
//
//            // could combine the following to one line, but split for clarity
//            val genB = genBFn(a)
//            val iterB = genB.generate(rs).iterator()
//            Pair(a, iterB.next().value)
//        }
//    }
//}