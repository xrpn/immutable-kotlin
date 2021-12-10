package com.xrpn.imapi

import com.xrpn.immutable.TKVEntry

interface IMListUtility<out A: Any> {
    fun equal(rhs: IMList<@UnsafeVariance A>): Boolean
    fun fforEach (f: (A) -> Unit): Unit
    fun fforEachReverse (f: (A) -> Unit): Unit
    fun copy(): IMList<A>
    fun copyToMutableList(): MutableList<@UnsafeVariance A>
}

interface IMSetUtility<out A: Any> {
    fun equal(rhs: IMRSetNotEmpty<@UnsafeVariance A>): Boolean
    fun equal(rhs: Set<@UnsafeVariance A>): Boolean
    fun fforEach (f: (A) -> Unit): Unit
    fun copy(): IMSet<A>
    fun ner(): IMRSetNotEmpty<A>?
    fun ne(): IMSetNotEmpty<A>?
    fun <K> nex(): IMXSetNotEmpty<K>? where K: Any, K: Comparable<K> // TODO this makes it possible to lie to the compiler
    fun copyToMutableSet(): MutableSet<@UnsafeVariance A>
    fun <B: Any> toEmptyRetyped(): IMSet<B>
}

internal interface IMKSetUtility<out K, out A: Any>: IMSetUtility<A> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun strongEqual(rhs: IMSet<@UnsafeVariance A>): Boolean
    fun toIMBTree(): IMBTree<K, A>
    fun <KK> toIMKSetNotEmpty(kt: RestrictedKeyType<KK>): IMKSetNotEmpty<KK, A>? where KK: Any, KK: Comparable<@UnsafeVariance KK>
    fun <KK> toIMBTree(kt: RestrictedKeyType<KK>): IMBTree<KK, A>? where KK: Any, KK: Comparable<@UnsafeVariance KK> = when (kt) {
        is DeratedCustomKeyType -> throw RuntimeException("internal Error")
        else -> toIMKSetNotEmpty(kt)?.toIMBTree()
    }
    fun <KK> toIMList(kt: RestrictedKeyType<KK>): IMList<TKVEntry<KK, A>>? where KK: Any, KK: Comparable<@UnsafeVariance KK> = when (kt) {
        is DeratedCustomKeyType -> throw RuntimeException("internal Error")
        else -> toIMBTree(kt)?.breadthFirst()
    }

}

interface IMMapUtility<out K, out V: Any> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun equal(rhs: IMMap<@UnsafeVariance K, @UnsafeVariance V>): Boolean
    fun fforEach (f: (V) -> Unit): Unit
    fun toIMBTree(): IMBTree<K, V>
    fun copy(): IMMap<K, V>
    fun copyToMutableMap(): MutableMap<@UnsafeVariance K, @UnsafeVariance V>
}

interface IMBTreeUtility<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun equal(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): Boolean
    fun fforEach(f: (TKVEntry<A, B>) -> Unit): Unit =
        if ((this as IMBTree<A,B>).fempty()) Unit else { this.ffold(this.froot()) { _, tkv -> f(tkv); tkv }; Unit }
    fun toIMSet(kType: RestrictedKeyType<@UnsafeVariance A>?): IMSet<B>?
    fun <K> toIMBTree(kType: RestrictedKeyType<@UnsafeVariance K>): IMBTree<K, B>? where K: Any, K: Comparable<K>
    fun toIMMap(): IMMap<A, B>
    fun copy(): IMBTree<A, B>
    fun copyToMutableMap(): MutableMap<@UnsafeVariance A, @UnsafeVariance B> = (
        this as IMBTree<A,B>).ffold(mutableMapOf()) { acc, tkv -> acc[tkv.getk()] = tkv.getv(); acc }

    companion object {
        fun <A, B: Any> strongEqual(rhs: IMBTree<A, B>, lhs: IMBTree<A, B>): Boolean where A: Any, A: Comparable<A> {

            // TODO remove in time
            fun weakCheckf(res: Boolean): Boolean {
                // lhs.hashCode() == rhs.hashCode() is too much for different type IMBTrees
                val lhsSortedByKey = lhs.inorder()
                val rhsSortedByKey = rhs.inorder()
                val aux = if(res) lhsSortedByKey.hashCode() == rhsSortedByKey.hashCode() else lhsSortedByKey.hashCode() != rhsSortedByKey.hashCode()
                if (!aux) {
                    println("lhs: ${lhs.hashCode()}, $lhsSortedByKey")
                    println("rhs: ${rhs.hashCode()}, ${rhs.inorder()}")
                }
                return aux
            }

            // TODO remove in time
            fun strongCheckf(res: Boolean): Boolean {
                if (rhs::class != lhs::class) return weakCheckf(res)
                val aux = if(res) lhs.hashCode() == rhs.hashCode() else lhs.hashCode() != rhs.hashCode()
                if (!aux) {
                    println("lhs: ${lhs.hashCode()},\t${lhs.inorder()}")
                    println("rhs: ${rhs.hashCode()},\t${rhs.inorder()}")
                }
                return aux
            }

            val res = rhs.inorder() == lhs.inorder() && rhs.preorder() == lhs.preorder() && rhs.postorder() == lhs.postorder()
            // TODO remove in time
            check(strongCheckf(res))
            return res
        }
    }
}

interface IMStackUtility<out A: Any> {
    fun equal(rhs: IMStack<@UnsafeVariance A>): Boolean
    fun fforEach (f: (A) -> Unit): Unit
    fun copy(): IMStack<A>
    fun copyToMutableList(): MutableList<@UnsafeVariance A>
    fun toIMList(): IMList<A>
}

interface IMQueueUtility<out A: Any> {
    fun equal(rhs: IMQueue<@UnsafeVariance A>, strong: Boolean = false): Boolean
    fun fforEach (f: (A) -> Unit): Unit
    fun copy(): IMQueue<A>
    fun toIMList(): IMList<A>
    fun copyToMutableList(): MutableList<@UnsafeVariance A>
}
