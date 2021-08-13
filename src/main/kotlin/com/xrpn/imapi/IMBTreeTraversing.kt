package com.xrpn.imapi

import com.xrpn.bridge.FListIteratorFwd
import com.xrpn.immutable.FList
import com.xrpn.immutable.TKVEntry

interface IMTraversable<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun forEach (f: (TKVEntry<A, B>) -> Unit): Unit
    fun preorder(reverse: Boolean = false): FList<TKVEntry<A, B>>
    fun postorder(reverse: Boolean = false): FList<TKVEntry<A, B>>
    fun inorder(reverse: Boolean = false): FList<TKVEntry<A, B>>
    fun breadthFirst(reverse: Boolean = false): FList<TKVEntry<A, B>>

    companion object {
        fun <A, B: Any> equal(rhs: IMTraversable<A, B>, lhs: IMTraversable<A, B>) : Boolean where A: Any, A: Comparable<A> =
            rhs.inorder() == lhs.inorder()
        fun <A, B: Any> strongEqual(rhs: IMTraversable<A, B>, lhs: IMTraversable<A, B>): Boolean where A: Any, A: Comparable<A> =
            rhs.preorder() == lhs.preorder() &&
                    rhs.inorder() == lhs.inorder() &&
                    rhs.postorder() == lhs.postorder()
    }

    fun preorderValues(reverse: Boolean = false): FList<B> = preorder(reverse).fmap { it.getv() }
    fun postorderValues(reverse: Boolean = false): FList<B> = postorder(reverse).fmap { it.getv() }
    fun inorderValues(reverse: Boolean = false): FList<B> = inorder(reverse).fmap { it.getv() }
    fun breadthFirstValues(reverse: Boolean = false): FList<B> = breadthFirst(reverse).fmap { it.getv() }

    fun preorderIterator(reverse: Boolean = false): FListIteratorFwd<TKVEntry<A, B>> = FListIteratorFwd(preorder(reverse))
    fun inorderIterator(reverse: Boolean = false): FListIteratorFwd<TKVEntry<A, B>> = FListIteratorFwd(inorder(reverse))
    fun postorderIterator(reverse: Boolean = false): FListIteratorFwd<TKVEntry<A, B>> = FListIteratorFwd(postorder(reverse))
    fun breadthFirstIterator(reverse: Boolean = false): FListIteratorFwd<TKVEntry<A, B>> = FListIteratorFwd(breadthFirst(reverse))

    fun preorderValueIterator(reverse: Boolean = false): FListIteratorFwd<B> = FListIteratorFwd(preorderValues(reverse))
    fun inorderValueIterator(reverse: Boolean = false): FListIteratorFwd<B> = FListIteratorFwd(inorderValues(reverse))
    fun postorderValueIterator(reverse: Boolean = false): FListIteratorFwd<B> = FListIteratorFwd(postorderValues(reverse))
    fun breadthFirstValueIterator(reverse: Boolean = false): FListIteratorFwd<B> = FListIteratorFwd(breadthFirstValues(reverse))
}