package com.xrpn.imapi

interface StrictEq<T> {
    fun equal(lhs: T, rhs: T): Boolean
}

interface IMCommonEquality<T: Any>: StrictEq<IMCommon<T>> {
    fun softEqual(lhs: IMCommon<T>, rhs: Any?): Boolean
}

interface IMOrderedEquality<A: Any>: StrictEq<IMOrdered<A>> {
    fun softEqual(lhs: IMOrdered<A>, rhs: Any?): Boolean
    fun unorderedEqual(lhs: IMOrdered<A>, rhs: IMOrdered<A>): Boolean
    fun unorderedEqual(lhs: IMOrdered<A>, rhs: Iterable<A>): Boolean
}

interface IMKeyedValueEquality<A,B:Any>: StrictEq<IMKeyedValue<A,B>> where A:Any, A: Comparable<A>  {
    fun softEqual(lhs: IMKeyedValue<A,B>, rhs: Any?): Boolean
}

interface IMSetEquality<T: Any>: StrictEq<IMSet<T>> {
    fun softEqual(lhs: IMSet<T>, rhs: Any?): Boolean
}

interface IMBTreeEquality<A,B:Any>: StrictEq<IMBTree<A,B>> where A:Any, A: Comparable<A> {
    fun softEqual(lhs: IMBTree<A, B>, rhs: Any?): Boolean
}