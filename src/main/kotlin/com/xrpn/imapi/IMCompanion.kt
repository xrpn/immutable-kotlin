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

    fun <A: Any> IMList<A>.prepend(item: A): IMList<A>
    fun <A: Any> IMList<A>.prependAll(elements: Collection<A>): IMList<A>
    fun <A: Any> IMList<A>.append(item: A): IMList<A>
    fun <A: Any> IMList<A>.appendAll(elements: Collection<A>): IMList<A>
    fun <A: Any> IMList<A>.remove(item: A): IMList<A>
    fun <A: Any> IMList<A>.removeAll(elements: Collection<A>): IMList<A>

    fun <A: Any> IMList<A>.hasSubsequence(sub: IMList<A>): Boolean

    fun <A: Any> fappend(lead: FList<A>, after: FList<A>): FList<A>
    fun <A: Any> fhasSubsequence(xsa: FList<A>, sub: FList<A>): Boolean
    fun <A: Any> fsetHead(x: A, xs: FList<A>): FList<A>
    fun <A: Any> fsetLast(lead: FList<A>, after: A): FList<A>

    operator fun <A: Any> IMList<A>.plus(rhs: IMList<A>): IMList<A> = this.appendAll(rhs.toFList())
    operator fun <A: Any> IMList<A>.minus(rhs: IMList<A>): IMList<A> = this.removeAll(rhs.toFList())

    fun <A: Any> IMList<A>.equal(rhs: IMList<A>): Boolean
    fun <A: Any> Collection<A>.toIMList():IMList<A>
}

interface IMSetCompanion {

    fun <A: Any> emptyIMSet(): IMSet<A> = FSetBody.empty

    fun <A: Any> of(vararg items: A): IMSet<A>
    fun <A: Any> of(items: Iterator<A>): IMSet<A>
    fun <B, A: Any> ofMap(items: Iterator<B>, f: (B) -> A): IMSet<A>
    fun <A: Any> of(items: IMList<A>): IMSet<A>
    fun <A: Any, B> ofMap(items: List<B>, f: (B) -> A): IMSet<A>

    fun <A: Any> IMSet<A>.add(item: A): IMSet<A>
    fun <A: Any> IMSet<A>.addAll(elements: Collection<A>): IMSet<A>
    fun <A: Any> IMSet<A>.remove(item: A): IMSet<A>
    fun <A: Any> IMSet<A>.removeAll(elements: Collection<A>): IMSet<A>
    fun <A: Any> IMSet<A>.holds(item: A): Boolean

    fun <A: Any> IMSet<A>.isSubsetOf(rhs: IMSet<A>): Boolean
    fun <A: Any> IMSet<A>.retainsOnly(elements: Collection<A>): IMSet<A>
    fun <A: Any> IMSet<A>.symmetricDifference(elements: Collection<A>): IMSet<A>

    fun <A: Any> finsertOrReplace(src: IMSet<A>, item: A): IMSet<A>
    fun <A: Any> finsertsOrReplace(src: IMSet<A>, items: IMSet<A>): IMSet<A>
    fun <A: Any> fdelete(src: IMSet<A>, item: A): IMSet<A>
    fun <A: Any> fdeletes(src: IMSet<A>, items: IMSet<A>): IMSet<A>
    fun <A: Any> fretain(src: IMSet<A>, items: IMSet<A>): IMSet<A>
    fun <A: Any> fxordiff(src1: IMSet<A>, src2: IMSet<A>,): IMSet<A>

    infix fun <A: Any> IMSet<A>.or(rhs: IMSet<A>): IMSet<A> = this.addAll(rhs as FSet<A>)
    infix fun <A: Any> IMSet<A>.and(rhs: IMSet<A>): IMSet<A> = this.retainsOnly(rhs as FSet<A>)
    infix fun <A: Any> IMSet<A>.xor(rhs: IMSet<A>): IMSet<A> = this.symmetricDifference(rhs as FSet<A>)
    operator fun <A: Any> IMSet<A>.plus(rhs: IMSet<A>): IMSet<A> = this.addAll(rhs as FSet<A>)
    operator fun <A: Any> IMSet<A>.plus(rhs: A): IMSet<A> = this.add(rhs)
    operator fun <A: Any> IMSet<A>.minus(rhs: IMSet<A>): IMSet<A> = this.removeAll(rhs as FSet<A>)
    operator fun <A: Any> IMSet<A>.minus(rhs: A): IMSet<A> = this.remove(rhs)

    fun <A: Any> IMSet<A>.equal(rhs: IMSet<A>): Boolean
    fun <A: Any> Collection<A>.toIMSet(): IMSet<A>
}