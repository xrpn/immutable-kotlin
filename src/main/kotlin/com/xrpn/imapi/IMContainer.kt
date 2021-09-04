package com.xrpn.imapi

interface IMList<out A:Any>:
    IMListFiltering<A>,
    IMListGrouping<A>,
    IMListTransforming<A>,
    IMListAltering<A>,
    IMListUtility<A>

interface IMMap<out A, out B: Any>
        where A: Any, A: Comparable<@UnsafeVariance A>

interface IMSet<out A:Any>:
    IMSetFiltering<A>,
    IMSetGrouping<A>,
    IMSetTransforming<A>,
    IMSetAltering<A>,
    IMSetUtility<A>

interface IMBTree<out A, out B: Any>:
    IMBTreeTraversing<A, B>,
    IMBTreeFiltering<A, B>,
    IMBTreeGrouping<A, B>,
    IMBTreeTransforming<A,B>,
    IMBTreeAltering<A, B>,
    IMBTreeUtility<A, B>
        where A: Any, A: Comparable<@UnsafeVariance A>

interface IMStack<out A:Any>:
    IMStackFiltering<A>,
    IMStackGrouping<A>,
    IMStackTransforming<A>,
    IMStackAltering<A>,
    IMStackUtility<A>
