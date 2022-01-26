package com.xrpn.imapi

interface IMCommonEquality<T: Any> {
    fun equal(lhs: IMCommon<T>, rhs: IMCommon<T>): Boolean
    fun softEqual(lhs: IMCommon<T>, rhs: Any?): Boolean
}

interface IMOrderedEquality<A: Any> {
    fun equal(lhs: IMOrdered<A>, rhs: IMOrdered<A>): Boolean
    fun softEqual(lhs: IMOrdered<A>, rhs: Any?): Boolean
    fun unorderedEqual(lhs: IMOrdered<A>, rhs: IMOrdered<A>): Boolean
    fun unorderedEqual(lhs: IMOrdered<A>, rhs: Iterable<A>): Boolean
}

interface IMKeyedValueEquality<A,B:Any> where A:Any, A: Comparable<A>  {
    fun equal(lhs: IMKeyedValue<A,B>, rhs: IMKeyedValue<A,B>): Boolean
    fun softEqual(lhs: IMKeyedValue<A,B>, rhs: Any?): Boolean
}

interface IMSetEquality<T: Any> {
    fun equal(lhs: IMSet<T>, rhs: IMSet<T>): Boolean
    fun softEqual(lhs: IMSet<T>, rhs: Any?): Boolean
}

interface IMBTreeEquality<A,B:Any> where A:Any, A: Comparable<A> {
    fun equal(lhs: IMBTree<A, B>, rhs: IMBTree<A, B>): Boolean
    fun softEqual(lhs: IMBTree<A, B>, rhs: Any?): Boolean
}