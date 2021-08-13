package com.xrpn.imapi

interface IMList<out A:Any>: IMListTraversing<A>, IMListFiltering<A>, IMListGrouping<A>, IMListTransforming<A>
interface IMSet<out A:Any>: IMSetTraversing<A>, IMSetFiltering<A>, IMSetGrouping<A>, IMSetTransforming<A>
interface IMBTree<out A, out B: Any>: IMBTreeTraversing<A, B>, IMBTreeFiltering<A, B>, IMBTreeGrouping<A, B> where A: Any, A: Comparable<@UnsafeVariance A>