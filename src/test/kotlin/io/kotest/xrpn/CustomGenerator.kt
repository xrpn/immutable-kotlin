package io.kotest.xrpn

import com.xrpn.immutable.FList
import com.xrpn.immutable.FRBTree
import com.xrpn.immutable.FSet
import com.xrpn.immutable.TKVEntry
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.set

fun <A: Any, B> Arb.Companion.flist(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a as A }
): Arb<FList<A>> {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.list(arbB, range).map { bs -> FList.ofMap(bs, f) }
}

fun <A: Any, B> Arb.Companion.flistAsCollection(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a as A }
): Arb<Collection<A>> = flist(arbB, range, f)

fun <A: Any, B> Arb.Companion.flistAsKList(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a as A }
): Arb<List<A>> = flist(arbB, range, f)

fun <A: Any, B> Arb.Companion.fset(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a as A }
): Arb<FSet<A>> {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.set(arbB, range).map { bs -> FSet.ofMap(bs.iterator(), f) }
}

fun <A: Any, B> Arb.Companion.fsetAsSet(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a as A }
): Arb<Set<A>> = fset(arbB, range, f)

fun <A: Any, B> Arb.Companion.fsetAsCollection(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a as A }
): Arb<Collection<A>> = fset(arbB, range, f)

fun <A: Any, B> Arb.Companion.frbtree(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a as A }
): Arb<FRBTree<Int, A>> {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.set(arbB, range).map { bs -> bs.map(f) }.map{ cs -> FRBTree.ofvi(cs.iterator()) }
}

fun <A: Any, B> Arb.Companion.frbtreeAsCollection(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a as A }
): Arb<Collection<TKVEntry<Int, A>>> = frbtree(arbB, range, f)