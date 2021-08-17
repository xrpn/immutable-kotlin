package com.xrpn.imapi

import com.xrpn.immutable.FRBTree
import com.xrpn.immutable.FSet.Companion.toFRBTree
import com.xrpn.immutable.FSet.Companion.toFSet
import com.xrpn.immutable.FSetBody
import com.xrpn.immutable.TKVEntry

interface IMListTraversing<out A: Any> {
    fun equal(rhs: IMList<@UnsafeVariance A>): Boolean
    fun fforEach (f: (A) -> Unit): Unit
    fun copy(): IMList<A>
    fun copyToMutableList(): MutableList<@UnsafeVariance A>
}

interface IMSetTraversing<out A: Any> {
    fun equal(rhs: IMSet<@UnsafeVariance A>): Boolean
    fun fforEach (f: (A) -> Unit): Unit
    fun toIMBTree(): IMBTree<Int, A>
}

interface IMBTreeTraversing<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun toIMSet (): IMSet<B>
    fun equal(rhs: IMBTree<@UnsafeVariance A, @UnsafeVariance B>): Boolean
    fun fforEach (f: (TKVEntry<A, B>) -> Unit): Unit
    fun preorder(reverse: Boolean = false): IMList<TKVEntry<A, B>>
    fun postorder(reverse: Boolean = false): IMList<TKVEntry<A, B>>
    fun inorder(reverse: Boolean = false): IMList<TKVEntry<A, B>>
    fun breadthFirst(reverse: Boolean = false): IMList<TKVEntry<A, B>>

    fun preorderValues(reverse: Boolean = false): IMList<B> = preorder(reverse).fmap { it.getv() }
    fun postorderValues(reverse: Boolean = false): IMList<B> = postorder(reverse).fmap { it.getv() }
    fun inorderValues(reverse: Boolean = false): IMList<B> = inorder(reverse).fmap { it.getv() }
    fun breadthFirstValues(reverse: Boolean = false): IMList<B> = breadthFirst(reverse).fmap { it.getv() }

    fun preorderAsMutableList(reverse: Boolean = false): MutableList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>> =
        preorder(reverse).copyToMutableList()
    fun postorderAsMutableList(reverse: Boolean = false): MutableList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>> =
        postorder(reverse).copyToMutableList()
    fun inorderAsMutableList(reverse: Boolean = false): MutableList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>> =
        inorder(reverse).copyToMutableList()
    fun breadthFirstAsMutableList(reverse: Boolean = false): MutableList<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>> =
        breadthFirst(reverse).copyToMutableList()

    companion object {
        fun <A, B: Any> strongEqual(rhs: IMBTree<A, B>, lhs: IMBTree<A, B>): Boolean where A: Any, A: Comparable<A> =
            rhs.inorder() == lhs.inorder() && rhs.preorder() == lhs.preorder() && rhs.postorder() == lhs.postorder()
    }
}