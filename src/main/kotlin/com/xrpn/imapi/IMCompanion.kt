package com.xrpn.imapi

import com.xrpn.immutable.FList
import com.xrpn.immutable.TKVEntry
import kotlin.reflect.KClass

// TODO remove in time
private fun checkf(res: Boolean, lhs: Any, rhs: Any): Boolean {
    val aux = if(res) lhs.hashCode() == rhs.hashCode() else lhs.hashCode() != rhs.hashCode()
    if (!aux) {
        println("${lhs.hashCode()}\tlhs: $lhs")
        println("${rhs.hashCode()}\trhs: $rhs")
    }
    return aux
}

internal fun <A: Any> IMListEqual2(lhs: IMList<A>, rhs: IMList<A>): Boolean {

    val res = when {
        lhs === rhs -> true
        rhs.fempty() && lhs.fempty() -> true
        rhs.fempty() || lhs.fempty() -> false
        rhs.fsize() != lhs.fsize() -> false
        // same elements in same order
        lhs.fzipWhile(rhs) { l, r ->  l.equals(r) }.fsize() == lhs.fsize() -> true
        else -> false
    }

    // TODO remove in time
    check(checkf(res, lhs, rhs))
    return res
}

interface IMListCompanion {

    fun <A: Any> emptyIMList(): IMList<A>
    fun <A: Any> of(vararg items: A): IMList<A>
    fun <A: Any> of(items: Iterator<A>): IMList<A>
    fun <A: Any> of(items: List<A>): IMList<A>
    fun <A: Any> of(items: IMList<A>): IMList<A>
    fun <B, A: Any> ofMap(items: Iterator<B>, f: (B) -> A): IMList<A>
    fun <A: Any, B> ofMap(items: List<B>, f: (B) -> A): IMList<A>

    fun <A: Any> Collection<A>.toIMList():IMList<A>
}

// because of type erasure, this is not entirely type safe, hence "internal"
internal fun <A: Any> IMRSetEqual2(lhs: IMRSet<A>, rhs: IMRSet<A>): Boolean {

    val res = when {
        lhs === rhs -> true
        lhs.fempty() && rhs.fempty() -> true
        lhs.fempty() || rhs.fempty() -> false
        lhs.fsize() != rhs.fsize() -> false
        else -> {
            val lhsInRhs = rhs.fcount(lhs::fcontains)
            val rhsInLhs = lhs.fcount(rhs::fcontains)
            lhsInRhs == rhsInLhs && lhsInRhs == rhs.fsize()
        }
    }

    return res
}

// because of type erasure, this is not entirely type safe, hence "internal"
internal fun <K, A: Any> IMSetEqual2(lhs: IMSet<K, A>, rhs: IMSet<K, A>): Boolean  where K: Any, K: Comparable<K> {

    val res = when {
        lhs === rhs -> true
        else -> IMBTreeEqual2(lhs.toIMBTree(), rhs.toIMBTree())
    }

    return res
}

interface IMSetCompanion {

    fun <A: Any> emptyIMRSet(): IMRSet<A>
    fun <A: Any> ofi(vararg items: A): IMRSet<A>
    fun <A: Any> ofi(items: Iterator<A>): IMRSet<A>
    fun <A: Any> ofi(items: IMBTree<Int, A>): IMRSet<A>
    fun <A: Any> ofi(items: IMList<A>): IMRSet<A>
    fun <B, A: Any> ofiMap(items: Iterator<B>, f: (B) -> A): IMRSet<A>
    fun <B: Any, A: Any> ofiMap(items: IMList<B>, f: (B) -> A): IMRSet<A>
    fun <B, A: Any> ofiMap(items: List<B>, f: (B) -> A): IMRSet<A>

    fun <A: Any> ofs(vararg items: A): IMRSet<A>
    fun <A: Any> ofs(items: Iterator<A>): IMRSet<A>
    fun <A: Any> ofs(items: IMBTree<String, A>): IMRSet<A>
    fun <A: Any> ofs(items: IMList<A>): IMRSet<A>
    fun <B, A: Any> ofsMap(items: Iterator<B>, f: (B) -> A): IMRSet<A>
    fun <B: Any, A: Any> ofsMap(items: IMList<B>, f: (B) -> A): IMRSet<A>
    fun <B, A: Any> ofsMap(items: List<B>, f: (B) -> A): IMRSet<A>

    fun <K, B : Any> toTKVEntry(s: IMRSet<B>, v: B): TKVEntry<K, B>? where K: Any, K: Comparable<K>

    fun <K, A: Any> Collection<A>.toIMSet(kType: RestrictedKeyType<K>): IMRSet<A> where K: Any, K: Comparable<K>
    fun <A: Any> Collection<A>.toIMISet(): IMRSet<A>
    fun <A: Any> Collection<A>.toIMSSet(): IMRSet<A>
}


// because of type erasure, this is not entirely type safe, hence "internal"
internal fun <K, V: Any> IMMapEqual2(lhs: IMMap<K, V>, rhs: IMMap<K, V>): Boolean  where K: Any, K: Comparable<K> {

    val res = when {
        lhs === rhs -> true
        else -> IMBTreeEqual2(lhs.toIMBTree(), rhs.toIMBTree())
    }

    return res
}

interface IMMapCompanion {
    fun <K, V: Any> emptyIMMap(): IMMap<K, V> where K: Any, K: Comparable<K>
    fun <K, V: Any> of(vararg items: Pair<K, V>): IMMap<K, V> where K: Any, K: Comparable<K>
    fun <K, V: Any> of(items: Iterator<Pair<K, V>>): IMMap<K, V> where K: Any, K: Comparable<K>
    fun <K, V: Any> of(items: IMList<Pair<K, V>>): IMMap<K, V> where K: Any, K: Comparable<K>
    fun <K, V: Any> of(items: IMBTree<K, V>): IMMap<K, V> where K: Any, K: Comparable<K>

    fun <K, V: Any> Collection<V>.toIMMap(keyMaker: (V) -> K): IMMap<K, V> where K: Any, K: Comparable<K>
}

// because of type erasure, this is not entirely type safe, hence "internal"
// this is a "weak" equality test, concerned with element containment and disregarding tree shape
internal fun <A, B: Any> IMBTreeEqual2(rhs: IMBTree<A, B>, lhs: IMBTree<A, B>) : Boolean where A: Any, A: Comparable<A> {

    val res = when {
        rhs === lhs -> true
        rhs.fempty() && lhs.fempty() -> true
        rhs.fempty() || lhs.fempty() -> false
        rhs.fsize() != lhs.fsize() -> false
        else -> {
            val lhsInRhs = rhs.fcount(lhs::fcontains)
            val rhsInLhs = lhs.fcount(rhs::fcontains)
            lhsInRhs == rhsInLhs && lhsInRhs == rhs.fsize()
        }
    }

    // TODO remove in time
    // lhs.hashCode() == rhs.hashCode() is the same as strong equality (too much for weaker containment equality)
    val lhsSortedByKey = lhs.inorder()
    val rhsSortedByKey = rhs.inorder()
    check(checkf(res, lhsSortedByKey, rhsSortedByKey))
    return res

}

interface IMBTreeCompanion {

    fun <A, B: Any> emptyIMBTree(): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(vararg items: TKVEntry<A, B>): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(vararg items: TKVEntry<A,B>, allowDups: Boolean): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(items: Iterator<TKVEntry<A, B>>): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(items: Iterator<TKVEntry<A, B>>, allowDups: Boolean): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(items: IMList<TKVEntry<A, B>>): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(items: IMList<TKVEntry<A, B>>, allowDups: Boolean): IMBTree<A, B> where A: Any, A: Comparable<A>

    fun <A, B: Any> ofc(cc: Comparator<A>, vararg items: TKVEntry<A, B>): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> ofc(cc: Comparator<A>, vararg items: TKVEntry<A,B>, allowDups: Boolean): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> ofc(cc: Comparator<A>, items: Iterator<TKVEntry<A, B>>): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> ofc(cc: Comparator<A>, items: Iterator<TKVEntry<A, B>>, allowDups: Boolean): IMBTree<A, B> where A: Any, A: Comparable<A>

    fun <B: Any> ofvi(vararg items: B): IMBTree<Int, B>
    fun <B: Any> ofvi(vararg items: B, allowDups: Boolean): IMBTree<Int, B>
    fun <B: Any> ofvi(items: Iterator<B>): IMBTree<Int, B>
    fun <B: Any> ofvi(items: Iterator<B>, allowDups: Boolean): IMBTree<Int, B>
    fun <B: Any> ofvi(items: IMList<B>): IMBTree<Int, B>
    fun <B: Any> ofvi(items: IMList<B>, allowDups: Boolean): IMBTree<Int, B>

    fun <B: Any> ofvs(vararg items: B): IMBTree<String, B>
    fun <B: Any> ofvs(vararg items: B, allowDups: Boolean): IMBTree<String, B>
    fun <B: Any> ofvs(items: Iterator<B>): IMBTree<String, B>
    fun <B: Any> ofvs(items: Iterator<B>, allowDups: Boolean): IMBTree<String, B>
    fun <B: Any> ofvs(items: IMList<B>): IMBTree<String, B>
    fun <B: Any> ofvs(items: IMList<B>, allowDups: Boolean): IMBTree<String, B>

    fun <A, B : Any, C, D : Any> ofMap(items: Iterator<TKVEntry<A, B>>, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): IMBTree<C, D>
    where A: Any, A: Comparable<A>, C: Any, C: Comparable<C>
    fun <A, B : Any, C, D : Any> ofMap(items: Iterator<TKVEntry<A, B>>, allowDups: Boolean, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): IMBTree<C, D>
    where A: Any, A: Comparable<A>, C: Any, C: Comparable<C>

    fun <B: Any, C: Any> ofviMap(items: Iterator<B>, f: (B) -> C): IMBTree<Int, C>
    fun <B: Any, C: Any> ofviMap(items: Iterator<B>, allowDups: Boolean, f: (B) -> C): IMBTree<Int, C>

    fun <B: Any, C: Any> ofvsMap(items: Iterator<B>, f: (B) -> C): IMBTree<String, C>
    fun <B: Any, C: Any> ofvsMap(items: Iterator<B>, allowDups: Boolean, f: (B) -> C): IMBTree<String, C>

    fun <A, B: Any> Collection<TKVEntry<A, B>>.toIMBTree(): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> Map<A, B>.toIMBTree(): IMBTree<A, B> where A: Any, A: Comparable<A>

    fun <B: Any> fcontainsIK(t: IMBTree<Int,B>, item: B): Boolean = t.ffindItem(TKVEntry.ofIntKey(item)) != null
    fun <B: Any> fdeleteIK(t: IMBTree<Int,B>, item: B): IMBTree<Int,B> = t.fdropItem(TKVEntry.ofIntKey(item))
    fun <B: Any> ffindIK(t: IMBTree<Int,B>, item: B): IMBTree<Int,B>? = t.ffindItem(TKVEntry.ofIntKey(item))
    fun <B: Any> ffindLastIK(t: IMBTree<Int,B>, item: B): IMBTree<Int,B>? = t.ffindLastItem(TKVEntry.ofIntKey(item))
    fun <B: Any> finsertIK(t: IMBTree<Int,B>, item: B): IMBTree<Int, B> = t.finsert(TKVEntry.ofIntKey(item))
    fun <B: Any> finsertDupIK(t: IMBTree<Int,B>, item: B, allowDups: Boolean): IMBTree<Int, B> = t.finsertDup(TKVEntry.ofIntKey(item), allowDups)

    fun <B: Any> fcontainsSK(t: IMBTree<String,B>, item: B): Boolean = t.ffindItem(TKVEntry.ofStrKey(item)) != null
    fun <B: Any> fdeleteSK(t: IMBTree<String,B>, item: B): IMBTree<String,B> = t.fdropItem(TKVEntry.ofStrKey(item))
    fun <B: Any> ffindSK(t: IMBTree<String,B>, item: B): IMBTree<String,B>? = t.ffindItem(TKVEntry.ofStrKey(item))
    fun <B: Any> ffindLastSK(t: IMBTree<String,B>, item: B): IMBTree<String,B>? = t.ffindLastItem(TKVEntry.ofStrKey(item))
    fun <B: Any> finsertSK(t: IMBTree<String,B>, item: B): IMBTree<String, B> = t.finsert(TKVEntry.ofStrKey(item))
    fun <B: Any> finsertDupSK(t: IMBTree<String,B>, item: B, allowDups: Boolean): IMBTree<String, B> = t.finsertDup(TKVEntry.ofStrKey(item), allowDups)
}

internal fun <A: Any> IMStackEqual2(lhs: IMStack<A>, rhs: IMStack<A>): Boolean {

    val res = when {
        lhs === rhs -> true
        else -> IMListEqual2(lhs.toIMList(), rhs.toIMList())
    }

    return res
}

interface IMStackCompanion {

    fun <A: Any> emptyIMStack(): IMStack<A>
    fun <A: Any> of(vararg items: A): IMStack<A>
    fun <A: Any> of(items: Iterator<A>): IMStack<A>
    fun <A: Any> of(items: List<A>): IMStack<A>
    fun <A: Any> of(items: IMList<A>): IMStack<A>
    fun <B, A: Any> ofMap(items: Iterator<B>, f: (B) -> A): IMStack<A>
    fun <A: Any, B> ofMap(items: List<B>, f: (B) -> A): IMStack<A>

    fun <A: Any> Collection<A>.toIMStack():IMStack<A>
}

interface IMQueueCompanion {

    fun <A: Any> emptyIMQueue(): IMQueue<A>
    fun <A: Any> of(vararg items: A, readyToDequeue: Boolean = false): IMQueue<A>
    fun <A: Any> of(items: Iterator<A>, readyToDequeue: Boolean = false): IMQueue<A>
    fun <A: Any> of(items: List<A>): IMQueue<A> // yes, ready to dequeue
    fun <A: Any> of(items: IMList<A>): IMQueue<A> // yes, ready to dequeue
    fun <B, A: Any> ofMap(items: Iterator<B>, readyToDequeue: Boolean, f: (B) -> A): IMQueue<A>
    fun <B, A: Any> ofMap(items: List<B>, f: (B) -> A): IMQueue<A>  // yes, ready to dequeue

    fun <A: Any> Collection<A>.toIMQueue():IMQueue<A>
}
