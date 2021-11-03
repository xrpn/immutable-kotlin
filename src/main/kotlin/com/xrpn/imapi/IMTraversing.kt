package com.xrpn.imapi

import com.xrpn.immutable.TKVEntry

interface IMBTreeTraversing<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun inorder(reverse: Boolean = false): IMList<TKVEntry<A, B>>
    fun breadthFirst(reverse: Boolean = false): IMList<TKVEntry<A, B>>
    fun preorder(reverse: Boolean = false): IMList<TKVEntry<A, B>>
    fun postorder(reverse: Boolean = false): IMList<TKVEntry<A, B>>

    fun inorderValues(reverse: Boolean = false): IMList<B> = inorder(reverse).fmap { it.getv() }
    fun breadthFirstValues(reverse: Boolean = false): IMList<B> = breadthFirst(reverse).fmap { it.getv() }
    fun preorderValues(reverse: Boolean = false): IMList<B> = preorder(reverse).fmap { it.getv() }
    fun postorderValues(reverse: Boolean = false): IMList<B> = postorder(reverse).fmap { it.getv() }
}