package com.xrpn.immutable.misc

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ArraySet(private val orders: Array<String>) : Set<String> {

    override val size get() = orders.size
    override fun iterator() = orders.iterator()
    override fun contains(element: String) = orders.contains(element)
    override fun containsAll(elements: Collection<String>) = orders.toList().containsAll(elements)
    override fun isEmpty() = orders.isEmpty()

    override fun equals(other: Any?): Boolean = false

    override fun hashCode(): Int {
        return orders.sumOf { it.hashCode() }
    }

    companion object {
        fun of(vararg orders: String): ArraySet {
            return ArraySet(orders.distinct().toTypedArray())
        }
    }
}

class ArraySetTest : FunSpec({
    val o1 = ArraySet.of("foo", "bar")
    val o2 = ArraySet.of("bar", "foo")
    val o3 = ArraySet.of("foo", "bar")
    val o4 = ArraySet.of("foo, bar, baz")

    test("1. permutation") {
        o1 shouldBe o2
    }
    test("1. permutation (iter)") {
        (o1 as Iterable<*>) shouldBe (o2 as Iterable<*>)
    }
    test("2. completely equal") {
        o1 shouldBe o3
    }
    test("3. different elements") {
        o1 shouldNotBe o4
    }
})