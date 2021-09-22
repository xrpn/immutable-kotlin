package com.xrpn.imapi

interface IMList<out A:Any>:
    IMListFiltering<A>,
    IMListGrouping<A>,
    IMListTransforming<A>,
    IMListAltering<A>,
    IMListUtility<A>,
    IMListExtras<A>

interface IMRSet<out A: Any>:
    IMRSetFiltering<A>,
    IMRSetGrouping<A>,
    IMRSetUtility<A>

interface IMSet<out K, out A:Any>:
    IMSetFiltering<K, A>,
    IMSetGrouping<K, A>,
    IMSetTransforming<K, A>,
    IMSetUtility<K, A>,
    IMSetExtras<K, A>,
    IMRSet<A>
        where K: Any, K: Comparable<@UnsafeVariance K>

interface IMSetNotEmpty<out K, out A:Any>:
    IMSet<K, A>,
    IMSetAltering<K, A>
        where K: Any, K: Comparable<@UnsafeVariance K>

interface IMMap<out K, out V: Any>:
    IMMapFiltering<K, V>,
    IMMapGrouping<K, V>,
    IMMapAltering<K, V>,
    IMMapUtility<K, V>
        where K: Any, K: Comparable<@UnsafeVariance K>

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

interface IMQueue<out A:Any>:
    IMQueueFiltering<A>,
    IMQueueGrouping<A>,
    IMQueueTransforming<A>,
    IMQueueAltering<A>,
    IMQueueUtility<A>
