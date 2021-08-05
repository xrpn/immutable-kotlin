package com.xrpn.imapi

import com.xrpn.immutable.FLNil

interface IMListCompanion {
    
    fun <A: Any> emptyFList(): IMList<A> = FLNil

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