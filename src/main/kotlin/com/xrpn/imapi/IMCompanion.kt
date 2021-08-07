package com.xrpn.imapi

import com.xrpn.immutable.*
import com.xrpn.immutable.FList.Companion.toFList
import com.xrpn.immutable.FRBTNil
import com.xrpn.immutable.FRBTree.Companion.contains
import com.xrpn.immutable.FSet.Companion.add
import com.xrpn.immutable.FSetBody

interface IMListCompanion {
    
    fun <A: Any> emptyIMList(): IMList<A>

    fun <A: Any> of(vararg items: A): IMList<A>
    fun <A: Any> of(items: Iterator<A>): IMList<A>
    fun <B, A: Any> ofMap(items: Iterator<B>, f: (B) -> A): IMList<A>
    fun <A: Any> of(items: List<A>): IMList<A>
    fun <A: Any, B> ofMap(items: List<B>, f: (B) -> A): IMList<A>

    fun <A: Any> append(lead: IMList<A>, after: IMList<A>): IMList<A>
    fun <A: Any> appendNested(rhs: IMList<IMList<A>>): IMList<A>
    fun <A: Any> hasSubsequence(xsa: IMList<A>, sub: IMList<A>): Boolean
    fun <A: Any> setHead(x: A, xs: IMList<A>): IMList<A>
    fun <A: Any> setLast(lead: IMList<A>, after: A): IMList<A>

    fun <A: Any> IMList<A>.equal(rhs: IMList<A>): Boolean
    fun <A: Any> Collection<A>.toIMList():IMList<A>
}

interface IMSetCompanion {

    fun <A: Any> emptyFSet(): IMSet<A> = FSetBody.empty

    fun <A: Any> of(vararg items: A): IMSet<A>
    fun <A: Any> of(items: Iterator<A>): IMSet<A>
    fun <B, A: Any> ofMap(items: Iterator<B>, f: (B) -> A): IMSet<A>
    fun <A: Any> of(items: IMList<A>): IMSet<A>
    fun <A: Any, B> ofMap(items: List<B>, f: (B) -> A): IMSet<A>

    fun <A: Any> IMSet<A>.add(item: A): IMSet<A>
    fun <A: Any> IMSet<A>.addAll(elements: Collection<A>): IMSet<A>
    fun <A: Any> IMSet<A>.clear(): IMSet<A>
    fun <A: Any> IMSet<A>.remove(item: A): IMSet<A>
    fun <A: Any> IMSet<A>.removeAll(elements: Collection<A>): IMSet<A>
    fun <A: Any> IMSet<A>.retainsOnly(elements: Collection<A>): IMSet<A>
    fun <A: Any> IMSet<A>.symmetricDifference(elements: Collection<A>): IMSet<A>

    infix fun <A: Any> IMSet<A>.or(rhs: IMSet<A>): IMSet<A> = this.addAll(rhs as FSet<A>)
    infix fun <A: Any> IMSet<A>.and(rhs: IMSet<A>): IMSet<A> = this.retainsOnly(rhs as FSet<A>)
    infix fun <A: Any> IMSet<A>.xor(rhs: IMSet<A>): IMSet<A> = this.symmetricDifference(rhs as FSet<A>)
    operator fun <A: Any> IMSet<A>.plus(rhs: IMSet<A>): IMSet<A> = this.addAll(rhs as FSet<A>)
    operator fun <A: Any> IMSet<A>.plus(rhs: A): IMSet<A> = this.add(rhs)
    operator fun <A: Any> IMSet<A>.minus(rhs: IMSet<A>): IMSet<A> = this.removeAll(rhs as FSet<A>)
    operator fun <A: Any> IMSet<A>.minus(rhs: A): IMSet<A> = this.remove(rhs)
    fun <A: Any>  IMSet<A>.isSubsetOf(rhs: IMSet<A>): Boolean


    fun <A: Any> FSet<A>.equal(rhs: FSet<A>): Boolean
    fun <A: Any> Collection<A>.toFSet(): IMSet<A>
}