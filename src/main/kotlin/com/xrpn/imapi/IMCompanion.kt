package com.xrpn.imapi

import com.xrpn.immutable.FSetOfOne
import com.xrpn.immutable.TKVEntry

internal fun <A: Any> IMListEqual2(lhs: IMList<A>, rhs: IMList<A>): Boolean {

    // TODO remove in time
    fun checkf(res: Boolean): Boolean {
        val aux = if(res) lhs.hashCode() == rhs.hashCode() else lhs.hashCode() != rhs.hashCode()
        if (!aux) {
            println("lhs: $lhs")
            println("rhs: $rhs")
        }
        return aux
    }

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
    check(checkf(res))
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

    operator fun <A: Any> IMList<A>.plus(rhs: IMList<A>): IMList<A> = this.fappendAll(of(rhs))
    operator fun <A: Any> IMList<A>.minus(rhs: IMList<A>): IMList<A> = this.fdropAll(of(rhs))

    fun <A: Any> Collection<A>.toIMList():IMList<A>
}

// because of type erasure, this is not entirely type safe, hence "internal"
internal fun <A: Any> IMSetEqual2(lhs: IMSet<A>, rhs: IMSet<A>): Boolean = when {
    lhs === rhs -> true
    else -> IMBTreeEqual2(lhs.toIMBTree(), rhs.toIMBTree())
}

interface IMSetCompanion {

    fun <A: Any> emptyIMSet(): IMSet<A>
    fun <A: Any> of(vararg items: A): IMSet<A>
    fun <A: Any> of(items: Iterator<A>): IMSet<A>
    fun <K, A: Any> of(items: IMBTree<K, A>): IMSet<A> where K: Any, K: Comparable<K>
    fun <A: Any> of(items: IMList<A>): IMSet<A>
    fun <B, A: Any> ofMap(items: Iterator<B>, f: (B) -> A): IMSet<A>
    fun <B: Any, A: Any> ofMap(items: IMList<B>, f: (B) -> A): IMSet<A>
    fun <B, A: Any> ofMap(items: List<B>, f: (B) -> A): IMSet<A>

    infix fun <A: Any> IMSet<A>.or(rhs: IMSet<A>): IMSet<A> = this.fOR(rhs)
    infix fun <A: Any> IMSet<A>.and(rhs: IMSet<A>): IMSet<A> = this.fAND(rhs)
    infix fun <A: Any> IMSet<A>.xor(rhs: IMSet<A>): IMSet<A> = this.fXOR(rhs)
    operator fun <A: Any> IMSet<A>.plus(rhs: IMSet<A>): IMSet<A> = this.fOR(rhs)
    operator fun <A: Any> IMSet<A>.plus(rhs: FSetOfOne<A>): IMSet<A> = this.fadd(rhs)
    operator fun <A: Any> IMSet<A>.minus(rhs: IMSet<A>): IMSet<A> = this.fdropAll(rhs)
    operator fun <A: Any> IMSet<A>.minus(rhs: FSetOfOne<A>): IMSet<A> = this.fdropItem(rhs)

    fun <A: Any> Collection<A>.toIMSet(): IMSet<A>
}

// because of type erasure, this is not entirely type safe, hence "internal"
// this is a "weak" equality test, concerned with element containment and disregarding tree shape
internal fun <A, B: Any> IMBTreeEqual2(rhs: IMBTree<A, B>, lhs: IMBTree<A, B>) : Boolean where A: Any, A: Comparable<A> {

    // TODO remove in time
    fun checkf(res: Boolean): Boolean {
        // lhs.hashCode() == rhs.hashCode() is the same as strong equality (too much for weaker containment equality)
        val lhsSortedByKey = lhs.inorder()
        val rhsSortedByKey = rhs.inorder()
        val aux = if(res) lhsSortedByKey.hashCode() == rhsSortedByKey.hashCode() else lhsSortedByKey.hashCode() != rhsSortedByKey.hashCode()
        if (!aux) {
            println("lhs: ${lhs.hashCode()}, $lhsSortedByKey")
            println("rhs: ${rhs.hashCode()}, ${rhs.inorder()}")
        }
        return aux
    }

    return when {
    rhs === lhs -> true
    rhs.fempty() && lhs.fempty() -> true
    rhs.fempty() || lhs.fempty() -> false
    rhs.fsize() != lhs.fsize() -> false
    else -> {
        val lhsInRhs = rhs.fcount(lhs::fcontains)
        val rhsInLhs = lhs.fcount(rhs::fcontains)
        val res = lhsInRhs == rhsInLhs && lhsInRhs == rhs.fsize()
        // TODO remove in time
        check(checkf(res))
        res
    }
}}

interface IMBTreeCompanion {

    fun <A, B: Any> emptyIMBTree(): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(vararg items: TKVEntry<A, B>): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(vararg items: TKVEntry<A,B>, allowDups: Boolean): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(items: Iterator<TKVEntry<A, B>>): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(items: Iterator<TKVEntry<A, B>>, allowDups: Boolean): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(items: IMList<TKVEntry<A, B>>): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(items: IMList<TKVEntry<A, B>>, allowDups: Boolean): IMBTree<A, B> where A: Any, A: Comparable<A>

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