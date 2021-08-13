package com.xrpn.imapi

interface IMList<out A:Any>: IMListFiltering<A>, IMListGrouping<A>, IMListTransforming<A>
interface IMSet<out A:Any>: IMSetFiltering<A>, IMSetGrouping<A>, IMSetTransforming<A>
interface IMBTree<out A, out B: Any>: IMTraversable<A, B>, IMBTreeFiltering<A, B>, IMBTreeGrouping<A, B> where A: Any, A: Comparable<@UnsafeVariance A>