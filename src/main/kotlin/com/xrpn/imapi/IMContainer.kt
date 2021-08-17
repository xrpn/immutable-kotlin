package com.xrpn.imapi

interface IMList<out A:Any>: IMListTraversing<A>, IMListFiltering<A>, IMListGrouping<A>, IMListTransforming<A>, IMListAltering<A>
interface IMSet<out A:Any>: IMSetTraversing<A>, IMSetFiltering<A>, IMSetGrouping<A>, IMSetTransforming<A>, IMSetAltering<A>
interface IMBTree<out A, out B: Any>: IMBTreeTraversing<A, B>, IMBTreeFiltering<A, B>, IMBTreeGrouping<A, B>, IMBTreeTransforming<A,B>, IMBTreeAltering<A, B> where A: Any, A: Comparable<@UnsafeVariance A>