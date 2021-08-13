package com.xrpn.imapi

import com.xrpn.immutable.TKVEntry

interface IMListTraversing<out A: Any> {
    fun fforEach (f: (A) -> Unit): Unit
}

interface IMSetTraversing<out A: Any> {
    fun fforEach (f: (A) -> Unit): Unit
}

interface IMBTreeTraversing<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun fforEach (f: (TKVEntry<A, B>) -> Unit): Unit
    fun preorder(reverse: Boolean = false): IMList<TKVEntry<A, B>>
    fun postorder(reverse: Boolean = false): IMList<TKVEntry<A, B>>
    fun inorder(reverse: Boolean = false): IMList<TKVEntry<A, B>>
    fun breadthFirst(reverse: Boolean = false): IMList<TKVEntry<A, B>>

    fun preorderValues(reverse: Boolean = false): IMList<B> = preorder(reverse).fmap { it.getv() }
    fun postorderValues(reverse: Boolean = false): IMList<B> = postorder(reverse).fmap { it.getv() }
    fun inorderValues(reverse: Boolean = false): IMList<B> = inorder(reverse).fmap { it.getv() }
    fun breadthFirstValues(reverse: Boolean = false): IMList<B> = breadthFirst(reverse).fmap { it.getv() }

    companion object {
        fun <A, B: Any> equal(rhs: IMBTree<A, B>, lhs: IMBTree<A, B>) : Boolean where A: Any, A: Comparable<A> = when (Pair(lhs.fempty(), rhs.fempty())) {
            Pair(false, false) -> if (rhs === lhs) true else rhs.inorder() == lhs.inorder()
            Pair(true, true) -> true
            else -> false
        }
        fun <A, B: Any> strongEqual(rhs: IMBTree<A, B>, lhs: IMBTree<A, B>): Boolean where A: Any, A: Comparable<A> =
            equal(rhs, lhs) && rhs.preorder() == lhs.preorder() && rhs.postorder() == lhs.postorder()
    }

}