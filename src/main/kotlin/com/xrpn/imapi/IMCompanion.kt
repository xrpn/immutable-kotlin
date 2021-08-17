package com.xrpn.imapi

import com.xrpn.immutable.FSetOfOne
import com.xrpn.immutable.TKVEntry

interface IMListCompanion {

    fun <A: Any> emptyIMList(): IMList<A>
    fun <A: Any> equal2(lhs: IMList<A>, rhs: IMList<A>): Boolean = when {
        lhs === rhs -> true
        lhs.fsize() != rhs.fsize() -> false
        0 == lhs.fsize() -> true // i.e. they are both empty
        lhs.fzipWhile(rhs) { l, r ->  l == r }.fsize() == lhs.fsize() -> true
        else -> false
    }

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

interface IMSetCompanion {

    fun <A: Any> emptyIMSet(): IMSet<A>
    fun <A: Any> equal2(lhs: IMSet<A>, rhs: IMSet<A>): Boolean = when {
        lhs === rhs -> true
        lhs.fsize() != rhs.fsize() -> false
        0 == lhs.fsize() -> true // i.e. they are both empty
        else -> lhs.toIMBTree().equal(rhs.toIMBTree())
    }

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

interface IMBTreeCompanion {

    fun <A, B: Any> emptyIMBTree(): IMBTree<A, B> where A: Any, A: Comparable<@UnsafeVariance A>
    fun <A, B: Any> equal2(rhs: IMBTree<A, B>, lhs: IMBTree<A, B>) : Boolean where A: Any, A: Comparable<A> = when {
        rhs === lhs -> true
        rhs.fsize() != lhs.fsize() -> false
        0 == rhs.fsize() -> true
        else -> {
            val lhsInRhs = rhs.fcount(lhs::fcontains)
            val rhsInLhs = lhs.fcount(rhs::fcontains)
            lhsInRhs == rhsInLhs && lhsInRhs == rhs.fsize()
        }
    }

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