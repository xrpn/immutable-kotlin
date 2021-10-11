package com.xrpn.imapi

import com.xrpn.immutable.EDA
import com.xrpn.immutable.EDB
import com.xrpn.immutable.EDJ2

interface IMList<out A:Any>:
    IMListFiltering<A>,
    IMListGrouping<A>,
    IMListTransforming<A>,
    IMListAltering<A>,
    IMListUtility<A>,
    IMListExtras<A>

interface IMSet<out A: Any>:
    IMSetFiltering<A>,
    IMSetGrouping<A>,
    IMSetTransforming<A>,
    IMSetAltering<A>,
    IMSetUtility<A>,
    IMSetExtras<A> {
    fun asIMRSetNotEmpty(): IMRSetNotEmpty<A>?
    fun asIMRRSetNotEmpty(): IMRRSetNotEmpty<A>?
    fun asIMSetNotEmpty(): IMSetNotEmpty<A>? = null
}

interface IMSetNotEmpty<out A: Any> {
    fun edj(): EDJ2<IMRSetNotEmpty<@UnsafeVariance A>, IMRRSetNotEmpty<@UnsafeVariance A>>
    fun equal(rhs: IMSetNotEmpty<@UnsafeVariance A>): Boolean
    fun equal(rhs: Set<@UnsafeVariance A>): Boolean
}

interface IMRSetNotEmpty<out A:Any>:
    IMSet<A>,
    IMSetNotEmpty<A>,
    IMRSetAltering<A> {
    override fun asIMRSetNotEmpty(): IMRSetNotEmpty<A>? = this
    override fun asIMRRSetNotEmpty(): IMRRSetNotEmpty<A>? = null
    override fun asIMSetNotEmpty(): IMSetNotEmpty<A>? = this
}

interface IMRRSetNotEmpty<out A:Any>:
    IMSet<A>,
    IMSetNotEmpty<A>,
    IMRRSetTransforming<A>,
    IMRRSetAltering<A> {
    override fun asIMRSetNotEmpty(): IMRSetNotEmpty<A>? = null
    override fun asIMRRSetNotEmpty(): IMRRSetNotEmpty<A>? = this
    override fun asIMSetNotEmpty(): IMSetNotEmpty<A>? = this
}

interface IMMap<out K, out V: Any>:
    IMMapFiltering<K, V>,
    IMMapGrouping<K, V>,
    IMMapTransforming<K, V>,
    IMMapUtility<K, V>,
    IMMapAltering<K, V>,
    IMMapExtras<K, V>
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

// ============ INTERNAL

internal interface IMKSet<out K, out A:Any>:
    IMSet<A>,
    IMKSetGrouping<K, A>,
    IMKSetTransforming<K, A>,
    IMKSetUtility<K, A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    fun asIMKSetNotEmpty(): IMKSetNotEmpty<K, A>? = null
    fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, A>?
    fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>?
}

internal interface IMKSetNotEmpty<out K, out A:Any>:
    IMKSet<K, A>,
    IMKSetFiltering<K, A>,
    IMSetNotEmpty<A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    override fun fkeyType(): RestrictedKeyType<K> {
        val aux: RestrictedKeyType<Any> = this.edj().getIfA()?.let {
            (it as IMKSetNotEmpty<*, *>).fkeyType()
        } ?: (this.edj().getIfB()!! as IMKSetNotEmpty<*,*>).fkeyType()
        @Suppress("UNCHECKED_CAST") (aux as RestrictedKeyType<K>)
        return aux
    }
    fun toSetKey(a: @UnsafeVariance A): K
    override fun asIMKSetNotEmpty(): IMKSetNotEmpty<K, A>? = this
    fun <KK> forceKey(): IMKSetNotEmpty<KK, A>? where KK: Any, KK: Comparable<KK> =
        @Suppress("UNCHECKED_CAST") (this as IMKSetNotEmpty<KK, A>)
}

internal interface IMKASetNotEmpty<out K, out A:Any>:
    IMKSetNotEmpty<K, A>,
    IMRSetNotEmpty<A>,
    IMKASetAltering<K, A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    override fun edj() = EDA(this)
    override fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, A>? = this
    override fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>? = null
}

internal interface IMKKSetNotEmpty<out K>:
    IMKSetNotEmpty<K, K>,
    IMKKSetTransforming<K>,
    IMRRSetNotEmpty<K>,
    IMKKSetAltering<K>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    override fun edj() = EDB(this)
    override fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, K>? = null
    override fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>? = this
}
