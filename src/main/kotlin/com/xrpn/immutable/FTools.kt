package com.xrpn.immutable

data class TKVEntryK<A: Comparable<A>, B:Any> constructor (val k: A, val v: B):
        Comparable<TKVEntryK<A,B>>,
        TKVEntry<A,B> {

    override fun compareTo(other: TKVEntryK<A, B>): Int = k.compareTo(other.k)

    override fun toString(): String = "[ $k:$v ]"

    override fun hashCode(): Int = when {
        k is Int -> k
        else -> k.hashCode()
    }

    private inline fun <reified Self: TKVEntryK<@UnsafeVariance A, @UnsafeVariance B>> equalsImpl(other: Any?): Boolean =
        when {
            this === other -> true
            other == null -> false
            other is Self -> 0 == other.compareTo(this)
            else -> false
        }

    override fun equals(other: Any?): Boolean = equalsImpl<TKVEntryK<A,B>>(other)


    override fun getk(): A = k
    override fun getkc(): Comparable<A> = k
    override fun getv(): B = v
    override fun copy(): TKVEntry<A, B> = /* TODO */ this.copy(k=k, v=v)

}

interface TKVEntry<out A: Any, out B: Any> {
    fun getk(): A
    fun getkc(): Comparable<@UnsafeVariance A>
    fun getv(): B
    fun copy(): TKVEntry<A,B>

    companion object {
        fun <A: Comparable<A>, B: Any> of (key:A, value: B): TKVEntry<A, B> = TKVEntryK(key, value)
        fun <A: Comparable<A>, B: Any> of (p: Pair<A, B>): TKVEntry<A, B> = TKVEntryK(p.first, p.second)
        fun <B: Any> ofIntKey (item: B): TKVEntry<Int, B> = TKVEntryK(item.hashCode(), item)
        fun <B: Any> ofStrKey (item: B): TKVEntry<String, B> = TKVEntryK(item.toString(), item)
    }
}

interface BTree<out A: Any, out B: Any> {
    fun isEmpty(): Boolean
    fun root(): TKVEntry<A, B>?
    fun leftMost(): TKVEntry<A, B>?
    fun rightMost(): TKVEntry<A, B>?
    fun size(): Int
    fun minDepth(): Int
    fun maxDepth(): Int
}

interface BTreeTraversable<out A: Any, out B: Any> {
    fun preorder(reverse: Boolean = false): FList<TKVEntry<A, B>>
    fun postorder(reverse: Boolean = false): FList<TKVEntry<A, B>>
    fun inorder(reverse: Boolean = false): FList<TKVEntry<A, B>>
    fun breadthFirst(reverse: Boolean = false): FList<TKVEntry<A, B>>

    companion object {
        fun <A: Any, B: Any> equal(rhs: BTreeTraversable<A, B>, lhs: BTreeTraversable<A, B>): Boolean =
            rhs.inorder() == lhs.inorder()
        fun <A: Any, B: Any> strongEqual(rhs: BTreeTraversable<A, B>, lhs: BTreeTraversable<A, B>): Boolean =
            rhs.preorder() == lhs.preorder() &&
                rhs.inorder() == lhs.inorder() &&
                rhs.postorder() == lhs.postorder()
    }

    fun preorderValues(reverse: Boolean = false): FList<B> = preorder(reverse).map { it.getv() }
    fun postorderValues(reverse: Boolean = false): FList<B> = postorder(reverse).map { it.getv() }
    fun inorderValues(reverse: Boolean = false): FList<B> = inorder(reverse).map { it.getv() }
    fun breadthFirstValues(reverse: Boolean = false): FList<B> = breadthFirst(reverse).map { it.getv() }

    fun preorderIterator(reverse: Boolean = false): FListIterator<TKVEntry<A, B>> = FListIterator(preorder(reverse))
    fun inorderIterator(reverse: Boolean = false): FListIterator<TKVEntry<A, B>> = FListIterator(inorder(reverse))
    fun postorderIterator(reverse: Boolean = false): FListIterator<TKVEntry<A, B>> = FListIterator(postorder(reverse))
    fun breadthFirstIterator(reverse: Boolean = false): FListIterator<TKVEntry<A, B>> = FListIterator(breadthFirst(reverse))

    fun preorderValueIterator(reverse: Boolean = false): FListIterator<B> = FListIterator(preorderValues(reverse))
    fun inorderValueIterator(reverse: Boolean = false): FListIterator<B> = FListIterator(inorderValues(reverse))
    fun postorderValueIterator(reverse: Boolean = false): FListIterator<B> = FListIterator(postorderValues(reverse))
    fun breadthFirstValueIterator(reverse: Boolean = false): FListIterator<B> = FListIterator(breadthFirstValues(reverse))
}