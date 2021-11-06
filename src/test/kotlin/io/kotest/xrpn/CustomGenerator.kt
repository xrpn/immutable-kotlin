package io.kotest.xrpn

import com.xrpn.immutable.*
import com.xrpn.immutable.FBSTree.Companion.fbtDeepInvariant
import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.take

fun <A: Any, B> Arb.Companion.flist(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a }
): Arb<FList<A>> where B:A {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.list(arbB, range).map { bs -> FList.ofMap(bs, f) }
}

fun <A: Any, B> Arb.Companion.flistAsCollection(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a }
): Arb<Collection<A>> where B:A = flist(arbB, range, f)

fun <A: Any, B> Arb.Companion.flistAsKList(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a }
): Arb<List<A>> where B:A = flist(arbB, range, f)

fun <A: Any, B> Arb.Companion.fset(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a }
): Arb<FKSet<Int, A>> where B:A {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.set(arbB, range).map { bs: Set<B> ->
        val aux: FKSet<Int, A> = FKSet.ofiMap(bs.iterator(), f)
        aux
    }
}

fun <A: Any, B> Arb.Companion.fsset(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a }
): Arb<FKSet<String, A>> where B:A {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.set(arbB, range).map { bs: Set<B> ->
        val aux: FKSet<String, A> = FKSet.ofsMap(bs.iterator(), f)
        aux
    }
}

fun <A: Any, B> Arb.Companion.fsetAsCollection(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a }
): Arb<Collection<A>> where B:A = fset(arbB, range, f)

fun <A: Any, B> Arb.Companion.fssetAsCollection(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a }
): Arb<Collection<A>> where B:A = fsset(arbB, range, f)

fun <A: Any, B> Arb.Companion.frbtree(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a }
): Arb<FRBTree<Int, A>> where B:A {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.set(arbB, range).map { bs -> bs.map(f) }.map{ cs -> FRBTree.ofvi(cs.iterator()) }
}

fun <A: Any, B> Arb.Companion.frbStree(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a }
): Arb<FRBTree<String, A>> where B:A {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.set(arbB, range).map { bs -> bs.map(f) }.map{ cs -> FRBTree.ofvs(cs.iterator()) }
}

fun <A: Any, B> Arb.Companion.frbtreeAsCollection(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a }
): Arb<Collection<TKVEntry<Int, A>>> where B:A = frbtree(arbB, range, f)

fun <A: Any, B> Arb.Companion.fbstree(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a }
): Arb<FBSTree<Int, A>> where B:A {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.set(arbB, range).map { bs -> bs.map(f) }.map{ cs -> FBSTree.ofvi(cs.iterator()) }
}

fun <A: Any, B> Arb.Companion.fbsStree(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a }
): Arb<FBSTree<String, A>> where B:A {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.set(arbB, range).map { bs -> bs.map(f) }.map{ cs -> FBSTree.ofvs(cs.iterator()) }
}

fun <A: Any, B> Arb.Companion.fbstreeWithDups(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a }
): Arb<FBSTree<Int, A>> where B:A {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.list(arbB, range).map { bs -> bs.map(f) }.map{ cs: List<A> ->
        var count = 0
        val dups: List<A> = cs.flatMap { a ->
            if (0 == count % 7)
                listOf(a, a)
            else
                listOf(a)
        }
        FBSTree.ofvi(dups.iterator(), allowDups = true)
    }
}

fun <A: Any, B> Arb.Companion.fbstreeAsCollection(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a }
): Arb<Collection<TKVEntry<Int, A>>> where B:A = fbstree(arbB, range, f)

fun <A: Any, B> Arb.Companion.fqueue(
    arbB: Arb<B>,
    range: IntRange = 1..50,
    @Suppress("UNCHECKED_CAST") f: (B) -> A = { a -> a }
): Arb<FQueue<A>> where B:A {
    check(!range.isEmpty()) { "range must not be empty" }
    check(range.first >= 1) { "start of range must not be less than 1" }
    return Arb.list(arbB, range).map { bs: List<B> ->
        when  {
            0 == bs.size % 3 -> FQueue.ofMap(bs.iterator(), true, f)
            1 == bs.size % 3 -> FQueue.ofMap(bs.iterator(), false, f)
            2 == bs.size % 3 -> {
                var count = 0
                val (a: List<B>, b: List<B>) = bs.partition { count += 1; 0 == count % 2 }
                val front = FList.ofMap(a.iterator(), f)
                val back = FList.ofMap(b.iterator(), f)
                FQueueBody.of(front, back)
            }
            else -> throw RuntimeException("internal error")
        }
    }
}

