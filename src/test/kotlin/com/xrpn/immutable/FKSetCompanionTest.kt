package com.xrpn.immutable

import com.xrpn.imapi.IMList
import com.xrpn.imapi.IMSet
import com.xrpn.imapi.IMSetNotEmpty
import com.xrpn.immutable.FKSet.Companion.NOT_FOUND
import com.xrpn.immutable.FKSet.Companion.emptyIMSet
import com.xrpn.immutable.FKSet.Companion.ofi
import com.xrpn.immutable.FKSet.Companion.ofiMap
import com.xrpn.immutable.FKSet.Companion.ofs
import com.xrpn.immutable.FKSet.Companion.ofsMap
import com.xrpn.immutable.FKSet.Companion.toIMSet
import com.xrpn.immutable.FList.Companion.emptyIMList
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.xrpn.fset

private val intISetOfNone: FKSet<Int, Int> = FKSet.ofi(*arrayOf())
private val strISetOfNone: FKSet<Int, String> = FKSet.ofi(*arrayOf())
private val intISetOfOne = FKSet.ofi(1)
private val strISetOfOne = FKSet.ofi("a")
private val intISetOfTwo = FKSet.ofi(1, 2)
private val intISetOfTwoOfst1 = FKSet.ofi(2, 3)
private val intISetOfThree: FKSet<Int, Int> = FKSet.ofi(1, 2, 3)
private val strISetOfThree = FKSet.ofi("1", "2", "3")

private val intSSetOfNone: FKSet<String, Int> = FKSet.ofs(*arrayOf())
private val strSSetOfNone: FKSet<String, String> = FKSet.ofs(*arrayOf())
private val intSSetOfOne = FKSet.ofs(1)
private val strSSetOfOne = FKSet.ofs("a")
private val intSSetOfTwo = FKSet.ofs(1, 2)
private val intSSetOfTwoOfst1 = FKSet.ofs(2, 3)
private val intSSetOfThree: FKSet<String, Int> = FKSet.ofs(1, 2, 3)
private val strSSetOfThree = FKSet.ofs("1", "2", "3")

private val kSetOfNone: Set<Int> = setOf(*arrayOf())
private val ksSetOfNone: Set<String> = setOf(*arrayOf())
private val kSetOfOne = setOf(1)
private val ksSetOfOne = setOf("a")
private val kSetOfTwo = setOf(1, 2)
private val kSetOfTwoOfst1 = setOf(2, 3)
private val kSetOfThree = setOf(1, 2, 3)

inline fun <reified R, reified S> reifiedGenericIsSame(r: R, s: S): Boolean = (r is S) && (s is R)
interface FooBar<out A: Any, out B: Any>
typealias FBOI<A> = FooBar<Int,A>
typealias FBOS<A> = FooBar<String,A>

class FKSetCompanionTest : FunSpec({

    val repeats = 50

    beforeTest {}

    test("reified types 1") {
        open class Base<out A: Any, out B: Any>(val a: A, val b: B): Set<B>, FooBar<A, B> {
            override val size: Int = 1
            override fun contains(element: @UnsafeVariance B): Boolean = element == b
            override fun containsAll(elements: Collection<@UnsafeVariance B>): Boolean = elements.size == 1 && elements.first() == b
            override fun isEmpty(): Boolean = false
            override fun iterator(): Iterator<B> = object : Iterator<B> {
                override fun hasNext(): Boolean = true
                override fun next(): B = b
            }
        }

        class SpecialOfInt<out B: Any>(b: B) : Base<Int, B>(1, b)
        class SpecialOfStr<out B: Any>(b: B) : Base<String, B>("1", b)

        val soitd: FBOI<Int> = SpecialOfInt(2)
        val sostd: FBOS<Int> = SpecialOfStr(2)
        val soi = SpecialOfInt(2)
        val sos = SpecialOfStr(2)

        reifiedGenericIsSame(soi, sos) shouldBe false
        reifiedGenericIsSame(soitd, sostd) shouldBe true

    }

    test("reified types 2") {
        open class Base<out A: Any, out B: Any>(val a: A, val b: B): FooBar<A, B>

        class SpecialOfInt<out B: Any>(b: B) : Base<Int, B>(1, b)
        class SpecialOfStr<out B: Any>(b: B) : Base<String, B>("1", b)

        val soi = SpecialOfInt(2)
        val sos = SpecialOfStr(2)
        val soitd: FBOI<Int> = soi
        val sostd: FBOS<Int> = sos

        reifiedGenericIsSame(soi, sos) shouldBe false
        // reifiedGenericIsSame(soi, sos) shouldBe true
        // reifiedGenericIsSame(soitd, sostd) shouldBe false
        reifiedGenericIsSame(soitd, sostd) shouldBe true

    }

    test("equals ISet") {
        intISetOfNone.equals(kSetOfNone) shouldBe true
        intISetOfNone.equals(kSetOfOne) shouldBe false
        intISetOfOne.equals(kSetOfNone) shouldBe false
        intISetOfOne.equals(kSetOfOne) shouldBe true
        intISetOfOne.equals(kSetOfTwo) shouldBe false
        intISetOfTwo.equals(kSetOfOne) shouldBe false
        intISetOfTwo.equals(kSetOfTwo) shouldBe true
        intISetOfTwo.equals(kSetOfTwoOfst1) shouldBe false
        intISetOfTwoOfst1.equals(kSetOfTwo) shouldBe false
        intISetOfTwo.equals(kSetOfThree) shouldBe false
        intISetOfTwoOfst1.equals(kSetOfThree) shouldBe false
        intISetOfThree.equals(kSetOfTwo) shouldBe false
        intISetOfThree.equals(kSetOfTwoOfst1) shouldBe false
        intISetOfThree.equals(kSetOfThree) shouldBe true

        kSetOfNone.equals(intISetOfNone) shouldBe true
        kSetOfNone.equals(intISetOfOne) shouldBe false
        kSetOfOne.equals(intISetOfNone) shouldBe false
        kSetOfOne.equals(intISetOfOne) shouldBe true
        kSetOfOne.equals(intISetOfTwo) shouldBe false
        kSetOfTwo.equals(intISetOfOne) shouldBe false
        kSetOfTwo.equals(intISetOfTwo) shouldBe true
        kSetOfTwo.equals(intISetOfTwoOfst1) shouldBe false
        kSetOfTwoOfst1.equals(intISetOfTwo) shouldBe false
        kSetOfTwo.equals(intISetOfThree) shouldBe false
        kSetOfTwoOfst1.equals(intISetOfThree) shouldBe false
        kSetOfThree.equals(intISetOfTwo) shouldBe false
        kSetOfThree.equals(intISetOfTwoOfst1) shouldBe false
        kSetOfThree.equals(intISetOfThree) shouldBe true

        kSetOfNone.equals(strISetOfNone) shouldBe true
        ksSetOfNone.equals(intISetOfNone) shouldBe true

        // this is why "equal" (vs. equals) is _always_ preferred
        intISetOfNone.equals(strISetOfNone) shouldBe true
        strISetOfNone.equals(intISetOfNone) shouldBe true
    }

    test("equals SSet") {
        intSSetOfNone.equals(kSetOfNone) shouldBe true
        intSSetOfNone.equals(kSetOfOne) shouldBe false
        intSSetOfOne.equals(kSetOfNone) shouldBe false
        intSSetOfOne.equals(kSetOfOne) shouldBe true
        intSSetOfOne.equals(kSetOfTwo) shouldBe false
        intSSetOfTwo.equals(kSetOfOne) shouldBe false
        intSSetOfTwo.equals(kSetOfTwo) shouldBe true
        intSSetOfTwo.equals(kSetOfTwoOfst1) shouldBe false
        intSSetOfTwoOfst1.equals(kSetOfTwo) shouldBe false
        intSSetOfTwo.equals(kSetOfThree) shouldBe false
        intSSetOfTwoOfst1.equals(kSetOfThree) shouldBe false
        intSSetOfThree.equals(kSetOfTwo) shouldBe false
        intSSetOfThree.equals(kSetOfTwoOfst1) shouldBe false
        intSSetOfThree.equals(kSetOfThree) shouldBe true

        kSetOfNone.equals(intSSetOfNone) shouldBe true
        kSetOfNone.equals(intSSetOfOne) shouldBe false
        kSetOfOne.equals(intSSetOfNone) shouldBe false
        kSetOfOne.equals(intSSetOfOne) shouldBe true
        kSetOfOne.equals(intSSetOfTwo) shouldBe false
        kSetOfTwo.equals(intSSetOfOne) shouldBe false
        kSetOfTwo.equals(intSSetOfTwo) shouldBe true
        kSetOfTwo.equals(intSSetOfTwoOfst1) shouldBe false
        kSetOfTwoOfst1.equals(intSSetOfTwo) shouldBe false
        kSetOfTwo.equals(intSSetOfThree) shouldBe false
        kSetOfTwoOfst1.equals(intSSetOfThree) shouldBe false
        kSetOfThree.equals(intSSetOfTwo) shouldBe false
        kSetOfThree.equals(intSSetOfTwoOfst1) shouldBe false
        kSetOfThree.equals(intSSetOfThree) shouldBe true

        kSetOfNone.equals(intSSetOfNone) shouldBe true
        kSetOfNone.equals(intSSetOfOne) shouldBe false
        kSetOfOne.equals(intSSetOfNone) shouldBe false
        kSetOfOne.equals(intSSetOfOne) shouldBe true
        kSetOfOne.equals(intSSetOfTwo) shouldBe false
        kSetOfTwo.equals(intSSetOfOne) shouldBe false
        kSetOfTwo.equals(intSSetOfTwo) shouldBe true
        kSetOfTwo.equals(intSSetOfTwoOfst1) shouldBe false
        kSetOfTwoOfst1.equals(intSSetOfTwo) shouldBe false
        kSetOfTwo.equals(intSSetOfThree) shouldBe false
        kSetOfTwoOfst1.equals(intSSetOfThree) shouldBe false
        kSetOfThree.equals(intSSetOfTwo) shouldBe false
        kSetOfThree.equals(intSSetOfTwoOfst1) shouldBe false
        kSetOfThree.equals(intSSetOfThree) shouldBe true

        kSetOfNone.equals(strSSetOfNone) shouldBe true
        ksSetOfNone.equals(intSSetOfNone) shouldBe true

        // this is why "equal" (vs. equals) is _always_ preferred
        intSSetOfNone.equals(strSSetOfNone) shouldBe true
        strSSetOfNone.equals(intSSetOfNone) shouldBe true
    }

    test("equals ISet SSet") {
        intISetOfNone.equals(intSSetOfNone) shouldBe true
        intISetOfNone.equals(intSSetOfOne) shouldBe false
        intISetOfOne.equals(intSSetOfNone) shouldBe false
        intISetOfOne.equals(intSSetOfOne) shouldBe false
        intISetOfOne.equals(intSSetOfTwo) shouldBe false
        intISetOfTwo.equals(intSSetOfOne) shouldBe false
        intISetOfTwo.equals(intSSetOfTwo) shouldBe false
        intISetOfTwo.equals(intSSetOfTwoOfst1) shouldBe false
        intISetOfTwoOfst1.equals(intSSetOfTwo) shouldBe false
        intISetOfTwo.equals(intSSetOfThree) shouldBe false
        intISetOfTwoOfst1.equals(intSSetOfThree) shouldBe false
        intISetOfThree.equals(intSSetOfTwo) shouldBe false
        intISetOfThree.equals(intSSetOfTwoOfst1) shouldBe false
        intISetOfThree.equals(intSSetOfThree) shouldBe false

        intSSetOfNone.equals(intISetOfNone) shouldBe true
        intSSetOfNone.equals(intISetOfOne) shouldBe false
        intSSetOfOne.equals(intISetOfNone) shouldBe false
        intSSetOfOne.equals(intISetOfOne) shouldBe false
        intSSetOfOne.equals(intISetOfTwo) shouldBe false
        intSSetOfTwo.equals(intISetOfOne) shouldBe false
        intSSetOfTwo.equals(intISetOfTwo) shouldBe false
        intSSetOfTwo.equals(intISetOfTwoOfst1) shouldBe false
        intSSetOfTwoOfst1.equals(intISetOfTwo) shouldBe false
        intSSetOfTwo.equals(intISetOfThree) shouldBe false
        intSSetOfTwoOfst1.equals(intISetOfThree) shouldBe false
        intSSetOfThree.equals(intISetOfTwo) shouldBe false
        intSSetOfThree.equals(intISetOfTwoOfst1) shouldBe false
        intSSetOfThree.equals(intISetOfThree) shouldBe false

        intSSetOfNone.equals(strISetOfNone) shouldBe true
        ksSetOfNone.equals(intISetOfNone) shouldBe true
    }

    test("equals fail ISet") {
        intISetOfNone.equals(null) shouldBe false

        intISetOfOne.equals(strISetOfOne) shouldBe false
        strISetOfOne.equals(intISetOfOne) shouldBe false
        intISetOfTwo.equals(strISetOfOne) shouldBe false
        strISetOfOne.equals(intISetOfTwo) shouldBe false

        kSetOfOne.equals(strISetOfOne) shouldBe false
        ksSetOfOne.equals(intISetOfOne) shouldBe false
        kSetOfTwo.equals(strISetOfOne) shouldBe false
        ksSetOfOne.equals(intISetOfTwo) shouldBe false

        strISetOfOne.equals(kSetOfOne) shouldBe false
        intISetOfOne.equals(ksSetOfOne) shouldBe false
        strISetOfOne.equals(kSetOfTwo) shouldBe false
        intISetOfTwo.equals(ksSetOfOne) shouldBe false

        intISetOfOne.equals(1) shouldBe false
        strISetOfOne.equals("a") shouldBe false
    }
    
    test("equals fail SSet") {
        intSSetOfNone.equals(null) shouldBe false

        intSSetOfOne.equals(strSSetOfOne) shouldBe false
        strSSetOfOne.equals(intSSetOfOne) shouldBe false
        intSSetOfTwo.equals(strSSetOfOne) shouldBe false
        strSSetOfOne.equals(intSSetOfTwo) shouldBe false

        kSetOfOne.equals(strSSetOfOne) shouldBe false
        ksSetOfOne.equals(intSSetOfOne) shouldBe false
        kSetOfTwo.equals(strSSetOfOne) shouldBe false
        ksSetOfOne.equals(intSSetOfTwo) shouldBe false

        strSSetOfOne.equals(kSetOfOne) shouldBe false
        intSSetOfOne.equals(ksSetOfOne) shouldBe false
        strSSetOfOne.equals(kSetOfTwo) shouldBe false
        intSSetOfTwo.equals(ksSetOfOne) shouldBe false

        intSSetOfOne.equals(1) shouldBe false
        strSSetOfOne.equals("a") shouldBe false
    }

    test("toString() hashCode()") {
        emptyIMSet<Int, Int>().toString() shouldBe "FKSet(*)"
    }

    test("toString() hashCode() ISet") {

        val aux = emptyIMSet<Int, Int>().hashCode()
        for (i in (1..100)) {
            aux shouldBe emptyIMSet<Int, Int>().hashCode()
        }
        intISetOfTwo.toString() shouldStartWith "FIKSet("
        val aux2 = intISetOfTwo.hashCode()
        for (i in (1..100)) {
            aux2 shouldBe intISetOfTwo.hashCode()
        }
        for (i in (1..100)) {
            FKSet.hashCode(intISetOfTwo) shouldBe intISetOfTwo.hashCode()
        }
    }

    test("toString() hashCode() SSet") {

        val aux = emptyIMSet<String, Int>().hashCode()
        for (i in (1..100)) {
            aux shouldBe emptyIMSet<String, Int>().hashCode()
        }
        intSSetOfTwo.toString() shouldStartWith "FSKSet("
        val aux2 = intSSetOfTwo.hashCode()
        for (i in (1..100)) {
            aux2 shouldBe intSSetOfTwo.hashCode()
        }
        for (i in (1..100)) {
            FKSet.hashCode(intSSetOfTwo) shouldBe intSSetOfTwo.hashCode()
        }
    }

    test("toString() hashCode() ISet SSet") {
        (emptyIMSet<Int, Int>().hashCode() == emptyIMSet<String, Int>().hashCode()) shouldBe true
        (intSSetOfTwo.hashCode() != intISetOfTwo.hashCode()) shouldBe true
    }

    // IMSetCompanion

    test("co.emptyIMSet"){
        emptyIMSet<Int, Int>() shouldBe FKSetEmpty.empty()
        emptyIMSet<String, Int>() shouldBe FKSetEmpty.empty()
        (emptyIMSet<Int, Int>() === FKSetEmpty.empty<Int, Int>()) shouldBe true
        (emptyIMSet<String, Int>() === FKSetEmpty.empty<String, Int>()) shouldBe true
        shouldThrow<ClassCastException> {
            emptyIMSet<Int, Int>() as IMSetNotEmpty<Int, Int>
        }
        shouldThrow<ClassCastException> {
            emptyIMSet<String, Int>() as IMSetNotEmpty<String, Int>
        }
    }

    // IMISet TODO needs more for delta btw ISet and SSet

    test("co.ofi vararg"){
        ofi(*arrayOf()) shouldBe emptyIMSet()
        (ofi(*arrayOf()) === emptyIMSet<Int, Int>()) shouldBe true
    }

    test("co.ofi Iterator"){
        ofi(arrayOf<Int>().iterator()) shouldBe emptyIMSet()
        (ofi(arrayOf<Int>().iterator()) === emptyIMSet<Int, Int>()) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofi(arrayOf(1, 2, 3).iterator()) as IMSetNotEmpty<Int, Int>)).equal(intISetOfThree) shouldBe true
    }

    test("co.ofi IMBTree<K, A>"){
        ofi(FRBTree.nul<Int, Int>()) shouldBe emptyIMSet()
        (ofi(FRBTree.nul<Int, Int>()) === emptyIMSet<Int, Int>()) shouldBe true
        ofi(FBSTree.nul<Int, Int>()) shouldBe emptyIMSet()
        (ofi(FBSTree.nul<Int, Int>()) === emptyIMSet<Int, Int>()) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofi(FRBTree.ofvi(1, 2, 3)) as IMSetNotEmpty<Int, Int>)) shouldBe intISetOfThree
        (@Suppress("UNCHECKED_CAST") (ofi(FBSTree.ofvi(1, 2, 3)) as IMSetNotEmpty<Int, Int>)) shouldBe intISetOfThree
    }

    test("co.ofi IMList"){
        ofi(emptyIMList()) shouldBe emptyIMSet()
        (ofi(emptyIMList()) === emptyIMSet<Int, Int>()) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofi(FLCons(2, FLCons(3, FLCons(1, FLNil)))) as IMSetNotEmpty<Int, Int>)) shouldBe intISetOfThree
    }

    test("co.ofiMap Iterator"){
        ofiMap(arrayOf<Int>().iterator()){ it.toString() } shouldBe emptyIMSet()
        (ofiMap(arrayOf<Int>().iterator()){ it.toString() } === emptyIMSet<Int, Int>()) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofiMap(arrayOf(1, 2, 3).iterator()){ it.toString() } as IMSetNotEmpty<Int, Int>)) shouldBe strISetOfThree
    }

    test("co.ofiMap IMList"){
        ofiMap(emptyIMList<Int>() as IMList<Int>){ it.toString() } shouldBe emptyIMSet()
        (ofiMap(emptyIMList<Int>() as IMList<Int>){ it.toString() } === emptyIMSet<Int, Int>()) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofiMap(FLCons(2, FLCons(3, FLCons(1, FLNil))) as IMList<Int>){ it.toString() } as IMSetNotEmpty<Int, Int>)) shouldBe strISetOfThree
    }

    test("co.ofiMap List"){
        ofiMap(emptyList<Int>()){ it.toString() } shouldBe emptyIMSet()
        (ofiMap(emptyList<Int>()){ it.toString() } === emptyIMSet<Int, Int>()) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofiMap(listOf(1, 2, 3)){ it.toString() } as IMSetNotEmpty<Int, Int>)) shouldBe strISetOfThree
    }

    // IMSSet TODO needs more for delta btw ISet and SSet

    test("co.ofs vararg"){
        ofs(*arrayOf()) shouldBe emptyIMSet()
        (ofs(*arrayOf()) === emptyIMSet<String, Int>()) shouldBe true
    }

    test("co.ofs Iterator"){
        ofs(arrayOf<Int>().iterator()) shouldBe emptyIMSet()
        (ofs(arrayOf<Int>().iterator()) === emptyIMSet<String, Int>()) shouldBe true
        val aut: IMSet<String, Int> = ofs(arrayOf(1, 2, 3).iterator())
        aut.equal(intSSetOfThree) shouldBe true
        aut.equals(intISetOfThree) shouldBe false
        // the following compares lhs and rhs as Iterable<A>, so the test is correct
        aut shouldBe intISetOfThree
        isSameType(aut, intISetOfThree) shouldBe false // DUH
        // this is a bad cast, but type erasure prevents the following from blowing up (as it should)
        (@Suppress("UNCHECKED_CAST") (aut as IMSetNotEmpty<Int, Int>)).equal(intISetOfThree) shouldBe false
        intISetOfThree.equal(@Suppress("UNCHECKED_CAST") (aut as IMSetNotEmpty<Int, Int>)) shouldBe false
    }

    test("co.ofs IMBTree<K, A>") {
        ofs(FRBTree.nul<String, Int>()) shouldBe emptyIMSet()
        (ofs(FRBTree.nul<String, Int>()) === emptyIMSet<String, Int>()) shouldBe true
        val autfrb: IMSet<String, Int> = ofs(FRBTree.ofvs(1, 2, 3))
        autfrb.equal(intSSetOfThree) shouldBe true
        autfrb.equals(intISetOfThree) shouldBe false
        autfrb shouldBe intISetOfThree
        isSameType(autfrb, intISetOfThree) shouldBe false // DUH
        (@Suppress("UNCHECKED_CAST") (autfrb as IMSetNotEmpty<Int, Int>)).equal(intISetOfThree) shouldBe false
        intISetOfThree.equal(@Suppress("UNCHECKED_CAST") (autfrb as IMSetNotEmpty<Int, Int>)) shouldBe false
    }

    test("co.ofs FBSTree<K, A>"){
        ofs(FBSTree.nul<String, Int>()) shouldBe emptyIMSet()
        (ofs(FBSTree.nul<String, Int>()) === emptyIMSet<String, Int>()) shouldBe true
        val autfbs: FKSet<String, Int> = ofs(FBSTree.ofvs(1, 2, 3))
        autfbs.equal(intSSetOfThree) shouldBe true
        autfbs.equals(intISetOfThree) shouldBe false
        autfbs shouldBe intISetOfThree
        isSameType(autfbs, intISetOfThree) shouldBe false // DUH
        (@Suppress("UNCHECKED_CAST") (autfbs as IMSetNotEmpty<Int, Int>)).equal(intISetOfThree) shouldBe false
        intISetOfThree.equal(@Suppress("UNCHECKED_CAST") (autfbs as IMSetNotEmpty<Int, Int>)) shouldBe false
    }

    test("co.ofs IMList"){
        ofs(emptyIMList()) shouldBe emptyIMSet()
        (ofs(emptyIMList()) === emptyIMSet<String, Int>()) shouldBe true
        val aut = ofs(FLCons(2, FLCons(3, FLCons(1, FLNil))))
        aut.equal(intSSetOfThree) shouldBe true
        aut.equals(intISetOfThree) shouldBe false
        aut shouldBe intISetOfThree
        isSameType(aut, intISetOfThree) shouldBe false // DUH
        (@Suppress("UNCHECKED_CAST") (aut as IMSetNotEmpty<Int, Int>)).equals(intISetOfThree) shouldBe false
        intISetOfThree.equals(@Suppress("UNCHECKED_CAST") (aut as IMSetNotEmpty<Int, Int>)) shouldBe false
    }

    test("co.ofsMap Iterator"){
        ofsMap(arrayOf<Int>().iterator()){ it.toString() } shouldBe emptyIMSet()
        (ofsMap(arrayOf<Int>().iterator()){ it.toString() } === emptyIMSet<String, Int>()) shouldBe true
        val aut = ofsMap(arrayOf(1, 2, 3).iterator()){ it.toString() }
        (@Suppress("UNCHECKED_CAST") ( aut as IMSetNotEmpty<Int, String>)) shouldBe strISetOfThree
        (@Suppress("UNCHECKED_CAST") ( aut as IMSetNotEmpty<Int, String>)).equal(strISetOfThree) shouldBe false
    }

    test("co.ofsMap IMList"){
        ofsMap(emptyIMList<Int>() as IMList<Int>){ it.toString() } shouldBe emptyIMSet()
        (ofsMap(emptyIMList<Int>() as IMList<Int>){ it.toString() } === emptyIMSet<String, Int>()) shouldBe true
        val aut = ofsMap(FLCons(2, FLCons(3, FLCons(1, FLNil))) as IMList<Int>){ it.toString() }
        (@Suppress("UNCHECKED_CAST") ( aut as IMSetNotEmpty<Int, String>)) shouldBe strISetOfThree
        (@Suppress("UNCHECKED_CAST") ( aut as IMSetNotEmpty<Int, String>)).equal(strISetOfThree) shouldBe false
    }

    test("co.ofsMap List"){
        ofsMap(emptyList<Int>()){ it.toString() } shouldBe emptyIMSet()
        (ofsMap(emptyList<Int>()){ it.toString() } === emptyIMSet<String, Int>()) shouldBe true
        val aut = ofsMap(listOf(1, 2, 3)){ it.toString() }
        (@Suppress("UNCHECKED_CAST") ( aut as IMSetNotEmpty<Int, String>)) shouldBe strISetOfThree
        (@Suppress("UNCHECKED_CAST") ( aut as IMSetNotEmpty<Int, String>)).equal(strISetOfThree) shouldBe false
    }

    test("co.toIMSet() IK"){
        Arb.list(Arb.int()).checkAll(repeats) { kl ->
            val ks = kl.toSet()
            val fs1 = kl.toIMSet(Int::class)
            val fs2 = ks.toIMSet(Int::class)
            val fs3 = if (ks.isNotEmpty()) {
                fs1 as IMSetNotEmpty<Int, Int>
            } else fs1
            fs3.equal(fs2) shouldBe true
            fs1.equals(ks) shouldBe true
            ks.equals(fs2) shouldBe true
        }
    }

    test("co.toIMSet() SK"){
        Arb.list(Arb.int()).checkAll(repeats) { kl ->
            val ks = kl.toSet()
            val fs1 = kl.toIMSet(String::class)
            val fs2 = ks.toIMSet(String::class)
            val fs3 = if (ks.isNotEmpty()) {
                fs1 as IMSetNotEmpty<String, Int>
            } else fs1
            fs3.equal(fs2) shouldBe true
            fs1.equals(ks) shouldBe true
            ks.equals(fs2) shouldBe true
        }
    }

    // implementation

    test("co.NOT_FOUND"){
        NOT_FOUND shouldBe -1
    }

    test("co.toArray"){
        Arb.fset<Int, Int>(Arb.int()).checkAll(repeats) { fs ->
            val ary: Array<Int> = FKSet.toArray(fs)
            fs.equal(FKSet.ofi(ary.iterator())) shouldBe true
            fs.equal(FKSet.ofi(*ary)) shouldBe true
        }
    }

})