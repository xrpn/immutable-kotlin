package com.xrpn.imapi

import com.xrpn.immutable.TKVEntry

interface IMListUtility<out A: Any> {
    fun equal(rhs: IMList<@UnsafeVariance A>): Boolean
    fun fforEach (f: (A) -> Unit): Unit
    fun copy(): IMList<A>
    fun copyToMutableList(): MutableList<@UnsafeVariance A>
}

interface IMSetUtility<out A: Any> {
    fun equal(rhs: IMSet<@UnsafeVariance A>): Boolean
    fun fforEach (f: (A) -> Unit): Unit
    fun toIMBTree(): IMBTree<Int, A>
    fun copy(): IMSet<A>
    fun copyToMutableSet(): MutableSet<@UnsafeVariance A>
}

interface IMBTreeUtility<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun equal(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): Boolean = when {
        (this as IMBTree<A,B>).fempty() -> rhs.fempty()
        else -> when {
            this === rhs -> true
            rhs.fempty() -> false
            else -> IMBTreeEqual2(this, rhs)
        }
    }

    fun fforEach(f: (TKVEntry<A, B>) -> Unit): Unit =
        if ((this as IMBTree<A,B>).fempty()) Unit else { this.ffold(this.fpick()) { _, tkv -> f(tkv); tkv }; Unit }
    fun toIMSet(): IMSet<B>
    fun copy(): IMBTree<A, B>
    fun copyToMutableMap(): MutableMap<@UnsafeVariance A, @UnsafeVariance B> = (
        this as IMBTree<A,B>).ffold(mutableMapOf()) { acc, tkv -> acc[tkv.getk()] = tkv.getv(); acc }

    companion object {
        fun <A, B: Any> strongEqual(rhs: IMBTree<A, B>, lhs: IMBTree<A, B>): Boolean where A: Any, A: Comparable<A> =
            rhs.inorder() == lhs.inorder() && rhs.preorder() == lhs.preorder() && rhs.postorder() == lhs.postorder()
    }
}