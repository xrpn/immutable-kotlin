package com.xrpn.imapi

import com.xrpn.immutable.*
import com.xrpn.immutable.FList.Companion.toFList
import com.xrpn.immutable.FSetBody

interface IMListCompanion {
    
    fun <A: Any> emptyIMList(): IMList<A>

    fun <A: Any> of(vararg items: A): IMList<A>
    fun <A: Any> of(items: Iterator<A>): IMList<A>
    fun <B, A: Any> ofMap(items: Iterator<B>, f: (B) -> A): IMList<A>
    fun <A: Any> of(items: List<A>): IMList<A>
    fun <A: Any, B> ofMap(items: List<B>, f: (B) -> A): IMList<A>

    fun <A: Any> IMList<A>.fprepend(item: A): IMList<A>
    fun <A: Any> IMList<A>.fprependAll(elements: Collection<A>): IMList<A>
    fun <A: Any> IMList<A>.fappend(item: A): IMList<A>
    fun <A: Any> IMList<A>.fappendAll(elements: Collection<A>): IMList<A>
    fun <A: Any> IMList<A>.fremove(item: A): IMList<A>
    fun <A: Any> IMList<A>.fremoveAll(elements: Collection<A>): IMList<A>

    fun <A: Any> IMList<A>.fhasSubsequence(sub: IMList<A>): Boolean

    fun <A: Any> fappend(lead: FList<A>, after: FList<A>): FList<A>
    fun <A: Any> fhasSubsequence(xsa: FList<A>, sub: FList<A>): Boolean
    fun <A: Any> fsetHead(x: A, xs: FList<A>): FList<A>
    fun <A: Any> fsetLast(lead: FList<A>, after: A): FList<A>

    operator fun <A: Any> IMList<A>.plus(rhs: IMList<A>): IMList<A> = this.fappendAll(rhs.toFList())
    operator fun <A: Any> IMList<A>.minus(rhs: IMList<A>): IMList<A> = this.fremoveAll(rhs.toFList())

    fun <A: Any> IMList<A>.equal(rhs: IMList<A>): Boolean
    fun <A: Any> Collection<A>.toIMList():IMList<A>
}

interface IMSetCompanion {

    fun <A: Any> emptyIMSet(): IMSet<A> = FSetBody.empty

    fun <A: Any> of(vararg items: A): IMSet<A>
    fun <A: Any> of(items: Iterator<A>): IMSet<A>
    fun <B, A: Any> ofMap(items: Iterator<B>, f: (B) -> A): IMSet<A>
    fun <A: Any> of(items: IMList<A>): IMSet<A>
    fun <B: Any, A: Any> ofMap(items: IMList<B>, f: (B) -> A): FSet<A>
    fun <B, A: Any> ofMap(items: List<B>, f: (B) -> A): FSet<A>

    fun <A: Any> IMSet<A>.fadd(item: A): IMSet<A>
    fun <A: Any> IMSet<A>.faddAll(elements: Collection<A>): IMSet<A>
    fun <A: Any> IMSet<A>.fremove(item: A): IMSet<A>
    fun <A: Any> IMSet<A>.fremoveAll(elements: Collection<A>): IMSet<A>
    fun <A: Any> IMSet<A>.fholds(item: A): Boolean

    fun <A: Any> IMSet<A>.fisSubsetOf(rhs: IMSet<A>): Boolean
    fun <A: Any> IMSet<A>.fretainsOnly(elements: Collection<A>): IMSet<A>
    fun <A: Any> IMSet<A>.fsymmetricDifference(elements: Collection<A>): IMSet<A>

    fun <A: Any> finsertOrReplace(src: IMSet<A>, item: A): IMSet<A>
    fun <A: Any> finsertsOrReplace(src: IMSet<A>, items: IMSet<A>): IMSet<A>
    fun <A: Any> fdelete(src: IMSet<A>, item: A): IMSet<A>
    fun <A: Any> fdeletes(src: IMSet<A>, items: IMSet<A>): IMSet<A>
    fun <A: Any> fretain(src: IMSet<A>, items: IMSet<A>): IMSet<A>
    fun <A: Any> fxordiff(src1: IMSet<A>, src2: IMSet<A>,): IMSet<A>

    infix fun <A: Any> IMSet<A>.or(rhs: IMSet<A>): IMSet<A> = this.faddAll(rhs as FSet<A>)
    infix fun <A: Any> IMSet<A>.and(rhs: IMSet<A>): IMSet<A> = this.fretainsOnly(rhs as FSet<A>)
    infix fun <A: Any> IMSet<A>.xor(rhs: IMSet<A>): IMSet<A> = this.fsymmetricDifference(rhs as FSet<A>)
    operator fun <A: Any> IMSet<A>.plus(rhs: IMSet<A>): IMSet<A> = this.faddAll(rhs as FSet<A>)
    operator fun <A: Any> IMSet<A>.plus(rhs: A): IMSet<A> = this.fadd(rhs)
    operator fun <A: Any> IMSet<A>.minus(rhs: IMSet<A>): IMSet<A> = this.fremoveAll(rhs as FSet<A>)
    operator fun <A: Any> IMSet<A>.minus(rhs: A): IMSet<A> = this.fremove(rhs)

    fun <A: Any> IMSet<A>.equal(rhs: IMSet<A>): Boolean
    fun <A: Any> Collection<A>.toIMSet(): IMSet<A>
}

interface IMBTreeCompanion {

    fun <A, B: Any> emptyIMBTree(): IMBTree<A, B> where A: Any, A: Comparable<@UnsafeVariance A>

    fun <A, B: Any> of(vararg items: TKVEntry<A,B>): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(vararg items: TKVEntry<A,B>, allowDups: Boolean): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(items: Iterator<TKVEntry<A, B>>): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(items: Iterator<TKVEntry<A, B>>, allowDups: Boolean): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(items: IMList<TKVEntry<A, B>>): IMBTree<A, B> where A: Any, A: Comparable<A>
    fun <A, B: Any> of(items: IMList<TKVEntry<A, B>>, allowDups: Boolean = false): IMBTree<A, B> where A: Any, A: Comparable<A>

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

    fun <A, B : Any, C, D : Any> ofMap(items: Iterator<TKVEntry<A, B>>, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): IMBTree<C, D> where A: Any, A: Comparable<A>, C: Any, C: Comparable<C>
    fun <A, B : Any, C, D : Any> ofMap(items: Iterator<TKVEntry<A, B>>, allowDups: Boolean, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): IMBTree<C, D> where A: Any, A: Comparable<A>, C: Any, C: Comparable<C>

    fun <B: Any, C: Any> ofviMap(items: Iterator<B>, f: (B) -> C): IMBTree<Int, C>
    fun <B: Any, C: Any> ofviMap(items: Iterator<B>, allowDups: Boolean, f: (B) -> C): IMBTree<Int, C>

    fun <B: Any, C: Any> ofvsMap(items: Iterator<B>, f: (B) -> C): IMBTree<String, C>
    fun <B: Any, C: Any> ofvsMap(items: Iterator<B>, allowDups: Boolean, f: (B) -> C): IMBTree<String, C>

    // -----------

//    fun <A, B: Any> fcontains2(treeStub: IMBTree<A, B>, item: TKVEntry<A, B>): Boolean where A: Any, A: Comparable<A>
//    fun <B: Any> fcontainsIntKey(treeStub: IMBTree<Int, B>, value: B): Boolean
//    fun <B: Any> fcontainsStrKey(treeStub: IMBTree<String, B>, value: B): Boolean
//    fun <A, B: Any> fdelete(treeStub: IMBTree<A, B>, item: TKVEntry<A, B>): FBSTree<A, B> where A: Any, A: Comparable<A>
//    fun <A, B: Any> fdelete(treeStub: IMBTree<A, B>, item: TKVEntry<A, B>, atMostOne: Boolean): FBSTree<A, B> where A: Any, A: Comparable<A>
//    fun <A, B: Any> fdeletes(treeStub: IMBTree<A, B>, items: FList<TKVEntry<A,B>>): IMBTree<A, B> where A: Any, A: Comparable<A>
//    fun <A, B: Any> ffind(treeStub: IMBTree<A, B>, item: TKVEntry<A, B>): IMBTree<A, B> where A: Any, A: Comparable<A>
//    fun <A, B: Any> ffindKey(treeStub: IMBTree<A, B>, key: A): B? where A: Any, A: Comparable<A>
//    fun <A, B: Any> ffindLast(treeStub: IMBTree<A, B>, item: TKVEntry<A, B>): IMBTree<A, B> where A: Any, A: Comparable<A>
//    fun <A, B: Any> finsert(treeStub: IMBTree<A, B>, item: TKVEntry<A, B>): IMBTree<A, B> where A: Any, A: Comparable<A>
//    fun <A, B: Any> finsert(treeStub: IMBTree<A, B>, item: TKVEntry<A, B>, allowDups: Boolean): IMBTree<A, B> where A: Any, A: Comparable<A>
//    fun <A, B: Any> finserts(treeStub: IMBTree<A, B>, items: FList<TKVEntry<A, B>>): IMBTree<A, B> where A: Any, A: Comparable<A>
//    fun <A, B: Any> finserts(treeStub: IMBTree<A, B>, items: FList<TKVEntry<A, B>>, allowDups: Boolean): IMBTree<A, B> where A: Any, A: Comparable<A>
//    fun <A, B: Any> fparent(treeStub: IMBTree<A, B>, childItem: TKVEntry<A, B>): IMBTree<A, B> where A: Any, A: Comparable<A>
}