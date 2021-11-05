package com.xrpn.immutable

import com.xrpn.imapi.*
import com.xrpn.immutable.FKSet.Companion.NOT_FOUND
import com.xrpn.immutable.FKSet.Companion.emptyIMKSet
import com.xrpn.immutable.FKSet.Companion.ofi
import com.xrpn.immutable.FKSet.Companion.ofiMap
import com.xrpn.immutable.FKSet.Companion.ofk
import com.xrpn.immutable.FKSet.Companion.ofkMap
import com.xrpn.immutable.FKSet.Companion.ofs
import com.xrpn.immutable.FKSet.Companion.ofsMap
import com.xrpn.immutable.FKSet.Companion.toIMKSet
import com.xrpn.immutable.FList.Companion.emptyIMList
import com.xrpn.immutable.TKVEntry.Companion.toKKEntry
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.kotest.xrpn.fset

private val intKKSetOfNone: FKSet<Int, Int> = FKSet.ofk(*emptyArrayOfInt)
private val intKKSetOfOne = FKSet.ofk(1)
private val intKKSetOfTwo = FKSet.ofk(1, 2)
private val intKKSetOfTwoOfst1 =  /* deliberate ofi */ FKSet.ofi(2, 3)
private val intKKSetOfThree: FKSet<Int, Int> = /* deliberate ofi */ FKSet.ofi(1, 2, 3)

private val strKKSetOfNone: FKSet<String, String> = FKSet.ofk(*emptyArrayOfStr)
private val strKKSetOfOne = FKSet.ofs("1")
private val strKKSetOfTwo = FKSet.ofk("1", "2")
private val strKKSetOfTwoOfst1 =  /* deliberate ofs */ FKSet.ofi("2", "3")
private val strKKSetOfThree: FKSet<String, String> = /* deliberate ofi */ FKSet.ofs("1", "2", "3")

private val longKKSetOfNone: FKSet<Long, Long> = FKSet.ofk(*emptyArrayOfLong)
private val longKKSetOfOne = FKSet.ofk(1L)
private val longKKSetOfTwo = FKSet.ofk(1L, 2L)
private val longKKSetOfTwoOfst1 =  /* deliberate ofi */ FKSet.ofk(2L, 3L)
private val longKKSetOfThree: FKSet<Long, Long> = FKSet.ofk(1L, 2L, 3L)

private val intSSetOfNone: FKSet<String, Int> = FKSet.ofs(*arrayOf())
private val intSSetOfOne = FKSet.ofs(1)
private val intSSetOfTwo = FKSet.ofs(1, 2)
private val intSSetOfTwoOfst1 = FKSet.ofs(2, 3)
private val intSSetOfThree: FKSet<String, Int> = FKSet.ofs(1, 2, 3)

private val strISetOfNone: FKSet<Int, String> = FKSet.ofi(*arrayOf())
private val strISetOfOne = FKSet.ofi("1")
private val strISetOfTwo = FKSet.ofi("1", "2")
private val strISetOfTwoOfst1 = FKSet.ofi("2", "3")
private val strISetOfThree = FKSet.ofi("1", "2", "3")

private val knSetOfNone: Set<Int> = setOf(*arrayOf())
private val ksSetOfNone: Set<String> = setOf(*arrayOf())
private val kiSetOfOne = setOf(1)
private val ksSetOfOne = setOf("1")
private val kiSetOfTwo = setOf(1, 2)
private val ksSetOfTwo = setOf("1", "2")
private val kiSetOfTwoOfst1 = setOf(2, 3)
private val ksSetOfTwoOfst1 = setOf("2", "3")
private val kiSetOfThree = setOf(1, 2, 3)
private val ksSetOfThree = setOf("1", "2", "3")

private val longISetOfThree: IMSetNotEmpty<Long> = strISetOfThree.fmap { it.toLong() }.ne()!!

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

    test("equals KSet") {
        intKKSetOfNone.equals(knSetOfNone) shouldBe true
        intKKSetOfNone.equals(kiSetOfOne) shouldBe false
        intKKSetOfOne.equals(knSetOfNone) shouldBe false
        intKKSetOfOne.equals(kiSetOfOne) shouldBe true
        intKKSetOfOne.equals(kiSetOfTwo) shouldBe false
        intKKSetOfTwo.equals(kiSetOfOne) shouldBe false
        intKKSetOfTwo.equals(kiSetOfTwo) shouldBe true
        intKKSetOfTwo.equals(kiSetOfTwoOfst1) shouldBe false
        intKKSetOfTwoOfst1.equals(kiSetOfTwo) shouldBe false
        intKKSetOfTwo.equals(kiSetOfThree) shouldBe false
        intKKSetOfTwoOfst1.equals(kiSetOfThree) shouldBe false
        intKKSetOfThree.equals(kiSetOfTwo) shouldBe false
        intKKSetOfThree.equals(kiSetOfTwoOfst1) shouldBe false
        intKKSetOfThree.equals(kiSetOfThree) shouldBe true

        knSetOfNone.equals(intKKSetOfNone) shouldBe true
        knSetOfNone.equals(intKKSetOfOne) shouldBe false
        kiSetOfOne.equals(intKKSetOfNone) shouldBe false
        kiSetOfOne.equals(intKKSetOfOne) shouldBe true
        kiSetOfOne.equals(intKKSetOfTwo) shouldBe false
        kiSetOfTwo.equals(intKKSetOfOne) shouldBe false
        kiSetOfTwo.equals(intKKSetOfTwo) shouldBe true
        kiSetOfTwo.equals(intKKSetOfTwoOfst1) shouldBe false
        kiSetOfTwoOfst1.equals(intKKSetOfTwo) shouldBe false
        kiSetOfTwo.equals(intKKSetOfThree) shouldBe false
        kiSetOfTwoOfst1.equals(intKKSetOfThree) shouldBe false
        kiSetOfThree.equals(intKKSetOfTwo) shouldBe false
        kiSetOfThree.equals(intKKSetOfTwoOfst1) shouldBe false
        kiSetOfThree.equals(intKKSetOfThree) shouldBe true

        knSetOfNone.equals(strISetOfNone) shouldBe true
        ksSetOfNone.equals(intKKSetOfNone) shouldBe true

        // this is why "equal" (vs. equals) is _always_ preferred
        intKKSetOfNone.equals(strISetOfNone) shouldBe true
        strISetOfNone.equals(intKKSetOfNone) shouldBe true
    }

    test("equals iSSet") {
        intSSetOfNone.equals(knSetOfNone) shouldBe true
        intSSetOfNone.equals(kiSetOfOne) shouldBe false
        intSSetOfOne.equals(knSetOfNone) shouldBe false
        intSSetOfOne.equals(kiSetOfOne) shouldBe true
        intSSetOfOne.equals(kiSetOfTwo) shouldBe false
        intSSetOfTwo.equals(kiSetOfOne) shouldBe false
        intSSetOfTwo.equals(kiSetOfTwo) shouldBe true
        intSSetOfTwo.equals(kiSetOfTwoOfst1) shouldBe false
        intSSetOfTwoOfst1.equals(kiSetOfTwo) shouldBe false
        intSSetOfTwo.equals(kiSetOfThree) shouldBe false
        intSSetOfTwoOfst1.equals(kiSetOfThree) shouldBe false
        intSSetOfThree.equals(kiSetOfTwo) shouldBe false
        intSSetOfThree.equals(kiSetOfTwoOfst1) shouldBe false
        intSSetOfThree.equals(kiSetOfThree) shouldBe true

        knSetOfNone.equals(intSSetOfNone) shouldBe true
        knSetOfNone.equals(intSSetOfOne) shouldBe false
        kiSetOfOne.equals(intSSetOfNone) shouldBe false
        kiSetOfOne.equals(intSSetOfOne) shouldBe true
        kiSetOfOne.equals(intSSetOfTwo) shouldBe false
        kiSetOfTwo.equals(intSSetOfOne) shouldBe false
        kiSetOfTwo.equals(intSSetOfTwo) shouldBe true
        kiSetOfTwo.equals(intSSetOfTwoOfst1) shouldBe false
        kiSetOfTwoOfst1.equals(intSSetOfTwo) shouldBe false
        kiSetOfTwo.equals(intSSetOfThree) shouldBe false
        kiSetOfTwoOfst1.equals(intSSetOfThree) shouldBe false
        kiSetOfThree.equals(intSSetOfTwo) shouldBe false
        kiSetOfThree.equals(intSSetOfTwoOfst1) shouldBe false
        kiSetOfThree.equals(intSSetOfThree) shouldBe true

        knSetOfNone.equals(intSSetOfNone) shouldBe true
        knSetOfNone.equals(intSSetOfOne) shouldBe false
        kiSetOfOne.equals(intSSetOfNone) shouldBe false
        kiSetOfOne.equals(intSSetOfOne) shouldBe true
        kiSetOfOne.equals(intSSetOfTwo) shouldBe false
        kiSetOfTwo.equals(intSSetOfOne) shouldBe false
        kiSetOfTwo.equals(intSSetOfTwo) shouldBe true
        kiSetOfTwo.equals(intSSetOfTwoOfst1) shouldBe false
        kiSetOfTwoOfst1.equals(intSSetOfTwo) shouldBe false
        kiSetOfTwo.equals(intSSetOfThree) shouldBe false
        kiSetOfTwoOfst1.equals(intSSetOfThree) shouldBe false
        kiSetOfThree.equals(intSSetOfTwo) shouldBe false
        kiSetOfThree.equals(intSSetOfTwoOfst1) shouldBe false
        kiSetOfThree.equals(intSSetOfThree) shouldBe true

        knSetOfNone.equals(strKKSetOfNone) shouldBe true
        ksSetOfNone.equals(intSSetOfNone) shouldBe true

        // this is why "equal" (vs. equals) is _always_ preferred
        intSSetOfNone.equals(strKKSetOfNone) shouldBe true
        strKKSetOfNone.equals(intSSetOfNone) shouldBe true
    }

    test("equals sISet") {
        strISetOfNone.equals(knSetOfNone) shouldBe true
        strISetOfNone.equals(ksSetOfOne) shouldBe false
        strISetOfOne.equals(knSetOfNone) shouldBe false
        strISetOfOne.equals(ksSetOfOne) shouldBe true
        strISetOfOne.equals(ksSetOfTwo) shouldBe false
        strISetOfTwo.equals(ksSetOfOne) shouldBe false
        strISetOfTwo.equals(ksSetOfTwo) shouldBe true
        strISetOfTwo.equals(ksSetOfTwoOfst1) shouldBe false
        strISetOfTwoOfst1.equals(ksSetOfTwo) shouldBe false
        strISetOfTwo.equals(ksSetOfThree) shouldBe false
        strISetOfTwoOfst1.equals(ksSetOfThree) shouldBe false
        strISetOfThree.equals(ksSetOfTwo) shouldBe false
        strISetOfThree.equals(ksSetOfTwoOfst1) shouldBe false
        strISetOfThree.equals(ksSetOfThree) shouldBe true

        knSetOfNone.equals(strISetOfNone) shouldBe true
        knSetOfNone.equals(strISetOfOne) shouldBe false
        ksSetOfOne.equals(strISetOfNone) shouldBe false
        ksSetOfOne.equals(strISetOfOne) shouldBe true
        ksSetOfOne.equals(strISetOfTwo) shouldBe false
        ksSetOfTwo.equals(strISetOfOne) shouldBe false
        ksSetOfTwo.equals(strISetOfTwo) shouldBe true
        ksSetOfTwo.equals(strISetOfTwoOfst1) shouldBe false
        ksSetOfTwoOfst1.equals(strISetOfTwo) shouldBe false
        ksSetOfTwo.equals(strISetOfThree) shouldBe false
        ksSetOfTwoOfst1.equals(strISetOfThree) shouldBe false
        ksSetOfThree.equals(strISetOfTwo) shouldBe false
        ksSetOfThree.equals(strISetOfTwoOfst1) shouldBe false
        ksSetOfThree.equals(strISetOfThree) shouldBe true

        knSetOfNone.equals(strISetOfNone) shouldBe true
        knSetOfNone.equals(strISetOfOne) shouldBe false
        ksSetOfOne.equals(strISetOfNone) shouldBe false
        ksSetOfOne.equals(strISetOfOne) shouldBe true
        ksSetOfOne.equals(strISetOfTwo) shouldBe false
        ksSetOfTwo.equals(strISetOfOne) shouldBe false
        ksSetOfTwo.equals(strISetOfTwo) shouldBe true
        ksSetOfTwo.equals(strISetOfTwoOfst1) shouldBe false
        ksSetOfTwoOfst1.equals(strISetOfTwo) shouldBe false
        ksSetOfTwo.equals(strISetOfThree) shouldBe false
        ksSetOfTwoOfst1.equals(strISetOfThree) shouldBe false
        ksSetOfThree.equals(strISetOfTwo) shouldBe false
        ksSetOfThree.equals(strISetOfTwoOfst1) shouldBe false
        ksSetOfThree.equals(strISetOfThree) shouldBe true

        knSetOfNone.equals(strKKSetOfNone) shouldBe true
        ksSetOfNone.equals(strISetOfNone) shouldBe true

        // this is why "equal" (vs. equals) is _always_ preferred
        strISetOfNone.equals(strKKSetOfNone) shouldBe true
        strKKSetOfNone.equals(strISetOfNone) shouldBe true
    }

    test("equals iKSet iSSet") {
        intKKSetOfNone.equals(intSSetOfNone) shouldBe true
        intKKSetOfNone.equals(intSSetOfOne) shouldBe false
        intKKSetOfOne.equals(intSSetOfNone) shouldBe false
        intKKSetOfOne.equals(intSSetOfOne) shouldBe false
        intKKSetOfOne.equals(intSSetOfTwo) shouldBe false
        intKKSetOfTwo.equals(intSSetOfOne) shouldBe false
        intKKSetOfTwo.equals(intSSetOfTwo) shouldBe false
        intKKSetOfTwo.equals(intSSetOfTwoOfst1) shouldBe false
        intKKSetOfTwoOfst1.equals(intSSetOfTwo) shouldBe false
        intKKSetOfTwo.equals(intSSetOfThree) shouldBe false
        intKKSetOfTwoOfst1.equals(intSSetOfThree) shouldBe false
        intKKSetOfThree.equals(intSSetOfTwo) shouldBe false
        intKKSetOfThree.equals(intSSetOfTwoOfst1) shouldBe false
        intKKSetOfThree.equals(intSSetOfThree) shouldBe false

        intSSetOfNone.equals(intKKSetOfNone) shouldBe true
        intSSetOfNone.equals(intKKSetOfOne) shouldBe false
        intSSetOfOne.equals(intKKSetOfNone) shouldBe false
        intSSetOfOne.equals(intKKSetOfOne) shouldBe false
        intSSetOfOne.equals(intKKSetOfTwo) shouldBe false
        intSSetOfTwo.equals(intKKSetOfOne) shouldBe false
        intSSetOfTwo.equals(intKKSetOfTwo) shouldBe false
        intSSetOfTwo.equals(intKKSetOfTwoOfst1) shouldBe false
        intSSetOfTwoOfst1.equals(intKKSetOfTwo) shouldBe false
        intSSetOfTwo.equals(intKKSetOfThree) shouldBe false
        intSSetOfTwoOfst1.equals(intKKSetOfThree) shouldBe false
        intSSetOfThree.equals(intKKSetOfTwo) shouldBe false
        intSSetOfThree.equals(intKKSetOfTwoOfst1) shouldBe false
        intSSetOfThree.equals(intKKSetOfThree) shouldBe false

        intSSetOfNone.equals(strISetOfNone) shouldBe true
        ksSetOfNone.equals(intKKSetOfNone) shouldBe true
    }

    test("equals sKSet sISet") {
        strKKSetOfNone.equals(strISetOfNone) shouldBe true
        strKKSetOfNone.equals(strISetOfOne) shouldBe false
        strKKSetOfOne.equals(strISetOfNone) shouldBe false
        strKKSetOfOne.equals(strISetOfOne) shouldBe false
        strKKSetOfOne.equals(strISetOfTwo) shouldBe false
        strKKSetOfTwo.equals(strISetOfOne) shouldBe false
        strKKSetOfTwo.equals(strISetOfTwo) shouldBe false
        strKKSetOfTwo.equals(strISetOfTwoOfst1) shouldBe false
        strKKSetOfTwoOfst1.equals(strISetOfTwo) shouldBe false
        strKKSetOfTwo.equals(strISetOfThree) shouldBe false
        strKKSetOfTwoOfst1.equals(strISetOfThree) shouldBe false
        strKKSetOfThree.equals(strISetOfTwo) shouldBe false
        strKKSetOfThree.equals(strISetOfTwoOfst1) shouldBe false
        strKKSetOfThree.equals(strISetOfThree) shouldBe false

        strISetOfNone.equals(strKKSetOfNone) shouldBe true
        strISetOfNone.equals(strKKSetOfOne) shouldBe false
        strISetOfOne.equals(strKKSetOfNone) shouldBe false
        strISetOfOne.equals(strKKSetOfOne) shouldBe false
        strISetOfOne.equals(strKKSetOfTwo) shouldBe false
        strISetOfTwo.equals(strKKSetOfOne) shouldBe false
        strISetOfTwo.equals(strKKSetOfTwo) shouldBe false
        strISetOfTwo.equals(strKKSetOfTwoOfst1) shouldBe false
        strISetOfTwoOfst1.equals(strKKSetOfTwo) shouldBe false
        strISetOfTwo.equals(strKKSetOfThree) shouldBe false
        strISetOfTwoOfst1.equals(strKKSetOfThree) shouldBe false
        strISetOfThree.equals(strKKSetOfTwo) shouldBe false
        strISetOfThree.equals(strKKSetOfTwoOfst1) shouldBe false
        strISetOfThree.equals(strKKSetOfThree) shouldBe false

        strISetOfNone.equals(strISetOfNone) shouldBe true
        ksSetOfNone.equals(strKKSetOfNone) shouldBe true
    }

    test("equals fail KKSet ISet") {
        intKKSetOfNone.equals(null) shouldBe false

        intKKSetOfOne.equals(strISetOfOne) shouldBe false
        strISetOfOne.equals(intKKSetOfOne) shouldBe false
        intKKSetOfTwo.equals(strISetOfOne) shouldBe false
        strISetOfOne.equals(intKKSetOfTwo) shouldBe false

        kiSetOfOne.equals(strISetOfOne) shouldBe false
        ksSetOfOne.equals(intKKSetOfOne) shouldBe false
        kiSetOfTwo.equals(strISetOfOne) shouldBe false
        ksSetOfOne.equals(intKKSetOfTwo) shouldBe false

        strISetOfOne.equals(kiSetOfOne) shouldBe false
        intKKSetOfOne.equals(ksSetOfOne) shouldBe false
        strISetOfOne.equals(kiSetOfTwo) shouldBe false
        intKKSetOfTwo.equals(ksSetOfOne) shouldBe false

        intKKSetOfOne.equals(1) shouldBe false
        strISetOfOne.equals("a") shouldBe false
    }

    test("equals fail KKSet SSet") {
        intSSetOfNone.equals(null) shouldBe false

        intSSetOfOne.equals(strKKSetOfOne) shouldBe false
        strKKSetOfOne.equals(intSSetOfOne) shouldBe false
        intSSetOfTwo.equals(strKKSetOfOne) shouldBe false
        strKKSetOfOne.equals(intSSetOfTwo) shouldBe false

        kiSetOfOne.equals(strKKSetOfOne) shouldBe false
        ksSetOfOne.equals(intSSetOfOne) shouldBe false
        kiSetOfTwo.equals(strKKSetOfOne) shouldBe false
        ksSetOfOne.equals(intSSetOfTwo) shouldBe false

        strKKSetOfOne.equals(kiSetOfOne) shouldBe false
        intSSetOfOne.equals(ksSetOfOne) shouldBe false
        strKKSetOfOne.equals(kiSetOfTwo) shouldBe false
        intSSetOfTwo.equals(ksSetOfOne) shouldBe false

        intSSetOfOne.equals(1) shouldBe false
        strKKSetOfOne.equals("a") shouldBe false
    }

    test("toString() hashCode()") {
        emptyIMKSet<Int, Int>().toString() shouldBe "FKSet(*)"
    }

    test("toString() hashCode() ISet") {

        val aux = emptyIMKSet<Int, Int>().hashCode()
        for (i in (1..100)) {
            aux shouldBe emptyIMKSet<Int, Int>().hashCode()
        }
        strISetOfThree.toString() shouldStartWith "FIKSet("
        val aux2 = strISetOfThree.hashCode()
        for (i in (1..100)) {
            aux2 shouldBe strISetOfThree.hashCode()
        }
        for (i in (1..100)) {
            FKSet.hashCode(strISetOfThree) shouldBe strISetOfThree.hashCode()
        }
    }

    test("toString() hashCode() SSet") {

        val aux = emptyIMKSet<String, Int>().hashCode()
        for (i in (1..100)) {
            aux shouldBe emptyIMKSet<String, Int>().hashCode()
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

    test("toString() hashCode() KSet") {

        val aux = emptyIMKSet<Int, Int>().hashCode()
        for (i in (1..100)) {
            aux shouldBe emptyIMKSet<Int, Int>().hashCode()
        }
        intKKSetOfTwo.toString() shouldStartWith "FKKSet("
        val aux2 = intKKSetOfTwo.hashCode()
        for (i in (1..100)) {
            aux2 shouldBe intKKSetOfTwo.hashCode()
        }
        for (i in (1..100)) {
            FKSet.hashCode(intKKSetOfTwo) shouldBe intKKSetOfTwo.hashCode()
        }
    }

    test("toString() hashCode() ISet SSet") {
        (emptyIMKSet<Int, Int>().hashCode() == emptyIMKSet<String, Int>().hashCode()) shouldBe true
        (intSSetOfTwo.hashCode() != intKKSetOfTwo.hashCode()) shouldBe true
    }

    // IMKSetCompanion

    test("co.emptyIMKSet"){
        emptyIMKSet<Int, Int>() shouldBe FKSetEmpty.empty()
        emptyIMKSet<String, Int>() shouldBe FKSetEmpty.empty()
        (emptyIMKSet<Int, Int>() === FKSetEmpty.empty<Int, Int>()) shouldBe true
        (emptyIMKSet<String, Int>() === FKSetEmpty.empty<String, Int>()) shouldBe true
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (emptyIMKSet<Int, Int>() as IMKKSetNotEmpty<Int>)
        }
        shouldThrow<ClassCastException> {
            @Suppress("UNCHECKED_CAST") (emptyIMKSet<String, Int>() as IMKASetNotEmpty<String, Int>)
        }
    }

    // IMSet TODO needs more for delta btw ISet and SSet and KSet

    test("co.ofi vararg"){
        ofi(*arrayOf()) shouldBe emptyIMKSet()
        (ofi(*arrayOf()) === emptyIMKSet<Int, Int>()) shouldBe true
    }

    test("co.ofi Iterator"){
        ofi(emptyArrayOfInt.iterator()) shouldBe emptyIMKSet()
        (ofi(emptyArrayOfInt.iterator()) === emptyIMKSet<Int, Int>()) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofi(arrayOf(1, 2, 3).iterator()) as IMKKSetNotEmpty<Int>)).strongEqual(intKKSetOfThree) shouldBe true
    }

    test("co.ofi FBSTree<K, A>"){
        ofi(FRBTree.nul<Int, Int>()) shouldBe emptyIMKSet()
        (ofi(FRBTree.nul<Int, Int>()) === emptyIMKSet<Int, Int>()) shouldBe true
        ofi(FBSTree.nul<Int, Int>()) shouldBe emptyIMKSet()
        (ofi(FBSTree.nul<Int, Int>()) === emptyIMKSet<Int, Int>()) shouldBe true
        ofi(FBSTree.nul<Int, Int>(true)) shouldBe emptyIMKSet()
        (ofi(FBSTree.nul<Int, Int>(true)) === emptyIMKSet<Int, Int>()) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofi(FRBTree.ofvi(1, 2, 3)) as IMKKSetNotEmpty<Int>)) shouldBe intKKSetOfThree
        (@Suppress("UNCHECKED_CAST") (ofi(FBSTree.ofvi(1, 2, 3)) as IMKKSetNotEmpty<Int>)) shouldBe intKKSetOfThree
        ofi(FBSTree.ofvi(1, 2, 3, allowDups = true)) shouldBe null
    }

    test("co.ofi IMList"){
        ofi(emptyIMList()) shouldBe emptyIMKSet()
        (ofi(emptyIMList()) === emptyIMKSet<Int, Int>()) shouldBe true
        ofi(FLCons(2, FLCons(3, FLCons(1, FLNil)))).equals(intKKSetOfThree) shouldBe true
        ofi(FLCons("2", FLCons("3", FLCons("1", FLNil)))).equals(strISetOfThree) shouldBe true
    }

    test("co.ofiMap Iterator"){
        ofiMap(emptyArrayOfInt.iterator()){ it.toString() } shouldBe emptyIMKSet()
        (ofiMap(emptyArrayOfInt.iterator()){ it.toString() } === emptyIMKSet<Int, Int>()) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofiMap(arrayOf(1, 2, 3).iterator()){ it.toString() } as IMKASetNotEmpty<Int, Int>)) shouldBe strISetOfThree
        ofiMap(arrayOf(1, 2, 3).iterator()){ it }.equals(intKKSetOfThree) shouldBe true
    }

    test("co.ofiMap IMList"){
        ofiMap(emptyIMList<Int>() as IMList<Int>){ it.toString() } shouldBe emptyIMKSet()
        (ofiMap(emptyIMList<Int>() as IMList<Int>){ it.toString() } === emptyIMKSet<Int, Int>()) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofiMap(FLCons(2, FLCons(3, FLCons(1, FLNil))) as IMList<Int>){ it.toString() } as IMKASetNotEmpty<Int, Int>)) shouldBe strISetOfThree
        ofiMap(FLCons(2, FLCons(3, FLCons(1, FLNil))) as IMList<Int>){ it }.equals(intKKSetOfThree) shouldBe true
    }

    // IMSet TODO needs more for delta btw ISet and SSet and KSet

    test("co.ofs vararg"){
        ofs(*arrayOf()) shouldBe emptyIMKSet()
        (ofs(*arrayOf()) === emptyIMKSet<String, Int>()) shouldBe true
    }

    test("co.ofs Iterator"){
        ofs(emptyArrayOfInt.iterator()) shouldBe emptyIMKSet()
        (ofs(emptyArrayOfInt.iterator()) === emptyIMKSet<String, Int>()) shouldBe true
        val aut: IMKSet<String, Int> = ofs(arrayOf(1, 2, 3).iterator())
        aut.strongEqual(intSSetOfThree) shouldBe true
        aut.equals(intKKSetOfThree) shouldBe false
        // the following compares lhs and rhs as Iterable<A>, so the test is correct
        aut shouldBe intKKSetOfThree
        aut.isStrictly(intKKSetOfThree) shouldBe false // DUH
        // this is a bad cast, but type erasure prevents the following from blowing up (as it should)
        (@Suppress("UNCHECKED_CAST") (aut as IMKASetNotEmpty<Int, Int>)).strongEqual(intKKSetOfThree) shouldBe false
        intKKSetOfThree.strongEqual(@Suppress("UNCHECKED_CAST") (aut as IMKASetNotEmpty<Int, Int>)) shouldBe false
    }

    test("co.ofs FRBTree<K, A>") {
        ofs(FRBTree.nul<String, Int>()) shouldBe emptyIMKSet()
        (ofs(FRBTree.nul<String, Int>()) === emptyIMKSet<String, Int>()) shouldBe true
        val autfrb: IMKSet<String, Int> = ofs(FRBTree.ofvs(1, 2, 3))!!
        autfrb.strongEqual(intSSetOfThree) shouldBe true
        autfrb.equals(intKKSetOfThree) shouldBe false
        autfrb shouldBe intKKSetOfThree
        autfrb.isStrictly(intKKSetOfThree) shouldBe false // DUH
        (@Suppress("UNCHECKED_CAST") (autfrb as IMKASetNotEmpty<Int, Int>)).strongEqual(intKKSetOfThree) shouldBe false
        intKKSetOfThree.strongEqual(@Suppress("UNCHECKED_CAST") (autfrb as IMKASetNotEmpty<Int, Int>)) shouldBe false
    }

    test("co.ofs FBSTree<K, A>"){
        ofs(FBSTree.nul<String, Int>()) shouldBe emptyIMKSet()
        (ofs(FBSTree.nul<String, Int>()) === emptyIMKSet<String, Int>()) shouldBe true
        (ofs(FBSTree.nul<String, Int>(true)) === emptyIMKSet<String, Int>()) shouldBe true
        ofs(FBSTree.ofvs(1, 2, 3, allowDups = true)) shouldBe null
        val autfbs: FKSet<String, Int> = ofs(FBSTree.ofvs(1, 2, 3))!!
        autfbs.strongEqual(intSSetOfThree) shouldBe true
        autfbs.equals(intKKSetOfThree) shouldBe false
        autfbs shouldBe intKKSetOfThree
        autfbs.isStrictly(intKKSetOfThree) shouldBe false // DUH
        (@Suppress("UNCHECKED_CAST") (autfbs as IMKASetNotEmpty<Int, Int>)).strongEqual(intKKSetOfThree) shouldBe false
        intKKSetOfThree.strongEqual(@Suppress("UNCHECKED_CAST") (autfbs as IMKASetNotEmpty<Int, Int>)) shouldBe false
    }

    test("co.ofs IMList"){
        ofs(emptyIMList()) shouldBe emptyIMKSet()
        (ofs(emptyIMList()) === emptyIMKSet<String, Int>()) shouldBe true
        val auti = ofs(FLCons(2, FLCons(3, FLCons(1, FLNil))))
        auti.strongEqual(intSSetOfThree) shouldBe true
        auti.equals(intKKSetOfThree) shouldBe false
        auti shouldBe intKKSetOfThree
        auti.isStrictly(intKKSetOfThree) shouldBe false // DUH
        (@Suppress("UNCHECKED_CAST") (auti as IMKASetNotEmpty<Int, Int>)).equals(intKKSetOfThree) shouldBe false
        intKKSetOfThree.equals(@Suppress("UNCHECKED_CAST") (auti as IMKASetNotEmpty<Int, Int>)) shouldBe false
        val auts = ofs(FLCons("1", FLCons("2", FLCons("3", FLNil))))
        auts.strongEqual(strKKSetOfThree) shouldBe true
        auts.equals(strISetOfThree) shouldBe false
        auts shouldBe strKKSetOfThree
    }

    test("co.ofsMap Iterator"){
        ofsMap(emptyArrayOfInt.iterator()){ it.toString() } shouldBe emptyIMKSet()
        (ofsMap(emptyArrayOfInt.iterator()){ it.toString() } === emptyIMKSet<String, Int>()) shouldBe true
        val auts = ofsMap(arrayOf(1, 2, 3).iterator()){ it.toString() }
        (@Suppress("UNCHECKED_CAST") ( auts as IMKKSetNotEmpty<String>)) shouldBe strISetOfThree
        (@Suppress("UNCHECKED_CAST") ( auts as IMKKSetNotEmpty<String>)).strongEqual(strISetOfThree) shouldBe false
        val autl: FKSet<String, Long> = ofsMap(arrayOf(1, 2, 3).iterator()){ it.toLong() }
        autl.equal(longISetOfThree) shouldBe true
    }

    test("co.ofsMap IMList"){
        ofsMap(emptyIMList<Int>() as IMList<Int>){ it.toString() } shouldBe emptyIMKSet()
        (ofsMap(emptyIMList<Int>() as IMList<Int>){ it.toString() } === emptyIMKSet<String, Int>()) shouldBe true
        val auts = ofsMap(FLCons(2, FLCons(3, FLCons(1, FLNil))) as IMList<Int>){ it.toString() }
        (@Suppress("UNCHECKED_CAST") ( auts as IMKKSetNotEmpty<String>)) shouldBe strISetOfThree
        (@Suppress("UNCHECKED_CAST") ( auts as IMKKSetNotEmpty<String>)).strongEqual(strISetOfThree) shouldBe false
        val autl: FKSet<String, Long> = ofsMap(FLCons(2, FLCons(3, FLCons(1, FLNil))) as IMList<Int>){ it.toLong() }
        autl.equal(longISetOfThree) shouldBe true
    }

    // IMSet TODO needs more for delta btw ISet and SSet and KSet

    test("co.ofk vararg"){
        (ofk(*emptyArrayOfLong) === emptyIMKSet<Long, Long>()) shouldBe true
    }

    test("co.ofk Iterator"){
        ofk(emptyArrayOfLong.iterator()) shouldBe emptyIMKSet()
        (ofk(emptyArrayOfLong.iterator()) === emptyIMKSet<Long, Long>()) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofk(arrayOf(1L, 2L, 3L).iterator()) as IMKKSetNotEmpty<Long>)).strongEqual(longKKSetOfThree) shouldBe true
    }

    test("co.ofk IMBTree<K, A>"){
        ofk(FRBTree.nul<Long, Long>()) shouldBe emptyIMKSet()
        (ofk(FRBTree.nul<Long, Long>()) === emptyIMKSet<Long, Long>()) shouldBe true
        ofk(FBSTree.nul<Long, Long>()) shouldBe emptyIMKSet()
        (ofk(FBSTree.nul<Long, Long>()) === emptyIMKSet<Long, Long>()) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofk(FRBTree.of(1L.toKKEntry() , 2L.toKKEntry(), 3L.toKKEntry())) as IMKKSetNotEmpty<Long>)) shouldBe longKKSetOfThree
        (@Suppress("UNCHECKED_CAST") (ofk(FBSTree.of(1L.toKKEntry(), 2L.toKKEntry(), 3L.toKKEntry())) as IMKKSetNotEmpty<Long>)) shouldBe longKKSetOfThree
    }

    test("co.ofk IMList"){
        ofk(emptyIMList<Long>()) shouldBe emptyIMKSet()
        (ofk(emptyIMList<Long>()) === emptyIMKSet<Long, Long>()) shouldBe true
        (@Suppress("UNCHECKED_CAST") (ofk(FLCons(2L, FLCons(3L, FLCons(1L, FLNil)))) as IMKKSetNotEmpty<Long>)) shouldBe longKKSetOfThree
    }

    test("co.ofkMap Iterator"){
        ofkMap(emptyArrayOfLong.iterator()){ it.toString() } shouldBe emptyIMKSet()
        (ofkMap(emptyArrayOfLong.iterator()){ it.toString() } === emptyIMKSet<Long, Long>()) shouldBe true
        ofkMap(arrayOf(1L, 2L, 3L).iterator()){ it.toString() }.equals(strKKSetOfThree) shouldBe true
    }

    test("co.ofkMap IMList"){
        ofkMap(emptyIMList<Long>() as IMList<Long>){ it.toString() } shouldBe emptyIMKSet()
        (ofkMap(emptyIMList<Long>() as IMList<Long>){ it.toString() } === emptyIMKSet<Long, Long>()) shouldBe true
        ofkMap(FLCons(2L, FLCons(3L, FLCons(1L, FLNil))) as IMList<Long>){ it.toString() }.equals(strKKSetOfThree) shouldBe true
    }

    test("co.toIMKSet() IK"){
        Arb.list(Arb.int()).checkAll(repeats) { kl ->
            val ks = kl.toSet()
            val fs1 = kl.toIMKSet(IntKeyType)
            val fs2 = ks.toIMKSet(IntKeyType)
            fs1?.equals(ks) shouldBe true
            ks.equals(fs2) shouldBe true
        }
    }

    test("co.toIMKSet() SK"){
        Arb.list(Arb.int()).checkAll(repeats) { kl ->
            val ks = kl.toSet()
            val fs1 = kl.toIMKSet(StrKeyType)
            val fs2 = ks.toIMKSet(StrKeyType)
            fs1?.equals(ks) shouldBe true
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
            fs.strongEqual(FKSet.ofi(ary.iterator())) shouldBe true
            fs.strongEqual(FKSet.ofi(*ary)) shouldBe true
        }
    }

})