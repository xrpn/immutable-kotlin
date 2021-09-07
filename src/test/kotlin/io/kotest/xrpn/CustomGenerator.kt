package io.kotest.xrpn

import com.xrpn.immutable.*
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
): Arb<FIKSet<A>> {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.set(arbB, range).map { bs -> FIKSet.ofMap(bs.iterator(), f) }
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

fun <A: Any, B> Arb.Companion.fbstree(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a as A }
): Arb<FBSTree<Int, A>> {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.set(arbB, range).map { bs -> bs.map(f) }.map{ cs -> FBSTree.ofvi(cs.iterator()) }
}

fun <A: Any, B> Arb.Companion.fbstreeAllowDups(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a as A }
): Arb<FBSTree<Int, A>> {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.list(arbB, range).map { bs -> bs.map(f) }.map{ cs -> FBSTree.ofvi(cs.iterator(), allowDups = true) }
}

fun <A: Any, B> Arb.Companion.fbstreeAsCollection(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a as A }
): Arb<Collection<TKVEntry<Int, A>>> = fbstree(arbB, range, f)
