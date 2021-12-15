package com.xrpn.imapi

interface IMListExtras<out A: Any> {
    operator fun plus(rhs: IMList<@UnsafeVariance A>): IMList<A>
    operator fun minus(rhs: IMList<@UnsafeVariance A>): IMList<A>
}

interface IMSetExtras<out A: Any> {

    operator fun contains(element: @UnsafeVariance A): Boolean

    infix fun or(rhs: IMSet<@UnsafeVariance A>): IMSet<A> {
        this as IMSet<A>
        val f: (acc: IMSet<A>, item: A) -> IMSet<A> = { acc, item ->
            if(acc.fcontains(item)) acc
            else @Suppress("UNCHECKED_CAST") (acc.faddItem(item) as IMSet<A>) }
        return if (fempty()) rhs else rhs.ffold(this, f)
    }

    infix fun and(rhs: IMSet<@UnsafeVariance A>): IMSet<A> {
        this as IMSet<A>
        val f: (acc: IMSet<A>, item: A) -> IMSet<A> = { acc, item ->
            if(rhs.fcontains(item)) @Suppress("UNCHECKED_CAST") (acc.faddItem(item) as IMSet<A>)
            else acc
        }
        return if (rhs.fempty()) toEmpty() else ffold(toEmpty(), f)
    }

    infix fun xor(rhs: IMSet<@UnsafeVariance A>): IMSet<A> {
        this as IMKSet<*, A>
        rhs as IMKSet<*, A>
        return when {
            fempty() -> rhs
            rhs.fempty() -> this
            else -> isKeyedAlike(rhs)?.let { alike ->
                if (alike) @Suppress("UNCHECKED_CAST") fXOR(rhs as IMKeyedValue<Nothing, A>)
                else {
                    fun f(container: IMSet<A>): (acc: IMSet<A>, item: A) -> IMSet<A> = { acc, item ->
                        if(container.fcontains(item)) acc
                        else @Suppress("UNCHECKED_CAST") (acc.faddItem(item) as IMSet<A>)
                    }
                    val partial = if (fempty()) rhs else rhs.ffold(toEmpty(), f(this))
                    ffold(partial, f(rhs))
                }
            } ?: toEmpty()
        }
    }

    infix fun not(rhs: IMSet<@UnsafeVariance A>): IMSet<A> {
        this as IMSet<A>
        val f: (acc: IMSet<A>, item: A) -> IMSet<A> = { acc, item ->
            if(rhs.fcontains(item)) acc
            else @Suppress("UNCHECKED_CAST") (acc.faddItem(item) as IMSet<A>) }
        return if(fempty() || rhs.fempty()) this else ffold(toEmpty(), f)
    }
}

internal interface IMKSetExtras<out K, out A: Any>: IMSetExtras<A> where K:Any, K:Comparable<@UnsafeVariance K> {

    operator fun set(k: @UnsafeVariance K, v: @UnsafeVariance A): IMSet<A>

    infix fun or(rhs: IMKSet<@UnsafeVariance K, @UnsafeVariance A>): IMSet<A> = (this as IMKSet<K, A>).fOR(rhs)
    infix fun and(rhs: IMKSet<@UnsafeVariance K, @UnsafeVariance A>): IMSet<A> = (this as IMKSet<K, A>).fAND(rhs)
    infix fun xor(rhs: IMKSet<@UnsafeVariance K, @UnsafeVariance A>): IMSet<A> = (this as IMKSet<K, A>).fXOR(rhs)
    infix fun not(rhs: IMKSet<@UnsafeVariance K, @UnsafeVariance A>): IMSet<A> = (this as IMKSet<K, A>).fNOT(rhs)

}

interface IMMapExtras<out K, out V: Any> where K: Any, K: Comparable<@UnsafeVariance K> {

    operator fun contains(k: @UnsafeVariance K): Boolean
    operator fun set(k: @UnsafeVariance K, v: @UnsafeVariance V): IMMap<K, V>
    operator fun get(key: @UnsafeVariance K): V?

    infix fun or(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fOR(rhs)
    infix fun and(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fAND(rhs)
    infix fun xor(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fXOR(rhs)
    infix fun not(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): IMMap<K, V> = (this as IMMap<K, V>).fNOT(rhs)
}

interface IMBTreeExtras<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {

    // TODO operator fun contains(k: @UnsafeVariance K): Boolean = fcontainsKey(k)
    operator fun set(k: @UnsafeVariance A, v: @UnsafeVariance B): IMBTree<A, B>
    operator fun get(key: @UnsafeVariance A): B?

    infix fun or(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> = (this as IMBTree<A, B>).fOR(rhs)
    infix fun and(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> = (this as IMBTree<A, B>).fAND(rhs)
    infix fun xor(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> = (this as IMBTree<A, B>).fXOR(rhs)
    infix fun not(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): IMBTree<A, B> = (this as IMBTree<A, B>).fNOT(rhs)
}
