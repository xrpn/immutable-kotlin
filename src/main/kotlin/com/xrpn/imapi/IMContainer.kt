package com.xrpn.imapi

interface IMList<out A:Any>:
    IMListFiltering<A>,
    IMListGrouping<A>,
    IMListTransforming<A>,
    IMListAltering<A>,
    IMListUtility<A>,
    IMListExtras<A>

interface IMMap<out A, out B: Any>
        where A: Any, A: Comparable<@UnsafeVariance A>

interface IMSet<out K, out A:Any>:
    IMSetFiltering<K, A>,
    IMSetGrouping<K, A>,
    IMSetTransforming<K, A>,
    IMSetAltering<K, A>,
    IMSetUtility<K, A>,
    IMSetExtras<K, A>
        where K: Any, K: Comparable<@UnsafeVariance K>

interface IMSetOfOne<out K, out A:Any>:
    IMSet<K, A>,
    IMSetNEAltering<K, A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    fun fitem(): A
}

interface IMSetNotEmpty<out K, out A:Any>:
    IMSet<K, A>,
    IMSetNEAltering<K, A>
        where K: Any, K: Comparable<@UnsafeVariance K>

//typealias IMISet<A> = IMSet<Int, A>
//typealias IMISetOfOne<A> = IMSetOfOne<Int, A>
//typealias IMISetNotEmpty<A> = IMSetNotEmpty<Int, A>
//
//typealias IMSSet<A> = IMSet<String, A>
//typealias IMSSetOfOne<A> = IMSetOfOne<String, A>
//typealias IMSSetNotEmpty<A> = IMSetNotEmpty<String, A>

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
