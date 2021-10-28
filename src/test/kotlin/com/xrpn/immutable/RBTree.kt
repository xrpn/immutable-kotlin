package com.xrpn.immutable

import com.xrpn.bridge.FListIteratorFwd
import com.xrpn.imapi.*

/*
    Mutable version of RBTree, verbatim from Java, to use as test oracle
 */

open class RBTree<A: Comparable<A>, B: Any>: IMBTree<A, B> {
    private var root: RBNode<A, B>? = null
    private var size: Int? = null

    override fun copy(): RBTree<A, B> {
        val res = RBTree<A, B>()
        res.root = root?.deepClone()
        res.size = size
        return res
    }

    fun search(key: A): B? {
        var x = root
        while (x != null) when {
            key == x.entry.getk() -> return x.entry.getv()
            key < x.entry.getk() -> x = x.bLeft
            x.entry.getk() < key -> x = x.bRight
        }
        return null
    }

    fun leftMost(at: RBTree<A,B>?): TKVEntry<A, B>? = when (at) {
            is RBNode -> at.bLeft?.leftMost(at.bLeft) ?: at.entry
            else -> null
        }

    private fun colorFlip(h: RBNode<A, B>) {
        h.color = !h.color
        h.bLeft?.let{ it.color = !it.color }
        h.bRight?.let{ it.color = !it.color }
    }

    private fun rotateLeft(h: RBNode<A, B>): RBNode<A, B> {
        val x = h.bRight!!
        h.bRight = x.bLeft
        x.bLeft = h
        x.color = h.color
        h.color = RED
        return x
    }

    private fun rotateRight(h: RBNode<A, B>): RBNode<A, B> {
        val x = h.bLeft!!
        h.bLeft = x.bRight
        x.bRight = h
        x.color = h.color
        h.color = RED
        return x
    }

    override fun inorder(reverse: Boolean): FList<TKVEntry<A,B>> {

        fun traverse(t: RBNode<A, B>?, acc: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> {
            t?.let {
                return traverse(it.bRight, FLCons(it.entry, traverse(it.bLeft, acc)))
            }
            return acc
        }


        fun reverseTraverse(t: RBNode<A, B>?, acc: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> {
           t?.let {
               return reverseTraverse(it.bLeft, FLCons(it.entry, reverseTraverse(it.bRight, acc)))
            }
            return acc
        }

        return when(reverse) {
            // FList<TKVEntry<A,B>> is assembled in reverse order during the traversal, so
            // this apparently perverse construct is, by virtue of that mechanism, correct
            true -> traverse(root, FLNil)
            false -> reverseTraverse(root, FLNil)
        }
    }

    override fun preorder(reverse: Boolean): FList<TKVEntry<A,B>> {

        fun traverse(t: RBNode<A, B>?, acc: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> {
            t?.let {
                return traverse(it.bRight, traverse(it.bLeft, FLCons(it.entry, acc)))
            }
            return acc
        }

        return when(reverse) {
            true -> traverse(root, FLNil)
            false -> traverse(root, FLNil).freverse()
        }
    }

    override fun postorder(reverse: Boolean): FList<TKVEntry<A,B>> {

        fun traverse(t: RBNode<A, B>?, acc: FList<TKVEntry<A,B>>): FList<TKVEntry<A,B>> {
            t?.let {
                return FLCons(it.entry, traverse(it.bRight, traverse(it.bLeft, acc)))
            }
            return acc
        }

        return when(reverse) {
            true -> traverse(root, FLNil)
            false -> traverse(root, FLNil).freverse()
        }
    }

    fun insert(entry: TKVEntry<A, B>) {
        root = insert(root, entry)
        root!!.color = BLACK
        size = size?.plus(1) ?: 1
    }

    private fun lrf23(h: RBNode<A, B>): RBNode<A, B> {
        val hh = if ((h.redRight()) && !(h.redLeft())) rotateLeft(h) else h
        val hhh = if ((hh.redLeft()) && (hh.bLeft?.redLeft() == true)) rotateRight(hh) else hh
        if ((hhh.redLeft()) && (hhh.redRight())) colorFlip(hhh)
        return hhh
    }

    private fun insert(h: RBNode<A,B>?, item: TKVEntry<A, B>): RBNode<A, B> {
        if (h == null) return RBNode(item)
        when {
            item.getk() == h.entry.getk() -> h.entry = item
            item.getk() < h.entry.getk() -> h.bLeft = insert(h.bLeft, item)
            h.entry.getk() < item.getk() -> h.bRight = insert(h.bRight, item)
        }
        return lrf23(h)
    }

    private fun moveRedLeft(h: RBNode<A,B>): RBNode<A, B> {
        colorFlip(h)
        return if (h.bRight?.redLeft() == true) {
            h.bRight = if (null != h.bRight) rotateRight(h.bRight!!) else h.bRight
            val aux = rotateLeft(h)
            colorFlip(aux)
            aux
        } else h
    }

    private fun moveRedRight(h: RBNode<A,B>): RBNode<A, B> {
        colorFlip(h)
        return if (h.bLeft?.redLeft() == true) {
            val aux = rotateRight(h)
            colorFlip(aux)
            aux
        } else h
    }

    private fun deleteLeftmost(h: RBNode<A,B>?): RBNode<A, B>? =
        if (h?.bLeft != null) {
            val hh = if (!(h.redLeft()) && h.bLeft?.redLeft() != true) moveRedLeft(h)
                     else h
            hh.bLeft = deleteLeftmost(hh.bLeft)
            lrf23(hh)
        } else null

    fun deleteLeftmost() {
        root = deleteLeftmost(root)
        root?.color = BLACK
        // TODO no negative size
        size = size?.minus(1)
    }

    fun rbDelete(entry: TKVEntry<A, B>) {
        root = delete(root, entry)
        root?.color = BLACK
        // TODO no negative size
        size = size?.minus(1)
    }

    private fun delete(h: RBNode<A,B>?, item: TKVEntry<A, B>): RBNode<A, B>? {
        if (null == h) return null
        val unbalanced: RBNode<A, B> = if (item.getk() < h.entry.getk()) {
            val aux = if (!(h.redLeft()) && h.bLeft?.redLeft() != true) moveRedLeft(h)
                      else h
            aux.bLeft = delete(aux.bLeft, item)
            aux
        } else {
            val o1 = if (h.redLeft()) rotateRight(h)
                     else h
            if ((item.getk() == o1.entry.getk()) && (null == o1.bRight)) return null
            val o2 = if (!(o1.redRight()) && o1.bRight?.redLeft() != true) moveRedRight(o1)
                     else o1
            val o3 = if (item.getk() == o2.entry.getk()) {
                val replacement: TKVEntry<A, B> = o2.bRight?.leftMost(o2.bRight)!! // this will bomb is not found
                o2.entry = replacement
                o2.bRight = deleteLeftmost(o2.bRight)
                o2
            } else {
                o2.bRight = delete(o2.bRight, item)
                o2
            }
            o3
        }
        return lrf23(unbalanced)
    }

    override fun toString(): String {
        root as RBNode
        return "$root"
    }

    fun maxDepth(): Int = root?.MD() ?: 0

    fun minDepth(): Int = root?.md() ?: 0

    fun size(): Int {
        size?.let{ return it }
        return 0
    }

    internal fun isDepthInvariant(): Boolean {
        root?.let {
            val size = this.size()
            val minDepth = this.minDepth()
            val maxDepth = this.maxDepth()
            val maxAllowed = FRBTree.rbMaxDepth(size)
            val p2 = maxDepth <= maxAllowed
            if (!p2) FRBTree.printErr("size:$size, fail: $minDepth <= $maxDepth <= $maxAllowed")
            return p2
        }
        return true
    }

    internal fun is23(): Boolean {
        if (root == null) return true
        if (root?.redRight() == true) return false
        if (this !== root && (this as RBNode).red() && this.redLeft()) return false
        return root?.bLeft?.is23() ?: false && root?.bRight?.is23() ?: false
    }

    internal fun isRedInvariant(): Boolean {

        fun redInvariant(x: RBNode<A, B>?, twoConsecutive: Boolean): Boolean =
            if (twoConsecutive) false
            else if (x == null) true
            else {
                if (!x.red()) redInvariant(x.bLeft, false) && redInvariant(x.bRight, false)
                else redInvariant(x.bLeft, x.redLeft()) && redInvariant(x.bRight, x.redRight())
            }

        return if (root == null) true else redInvariant(root, root!!.red())
    }

    internal fun isBalanced(): Boolean {

        fun isBalanced(x: RBNode<A, B>?, countDown: Int): Boolean =
            if (x == null) countDown == 0
            else {
                val newCount = if (!x.red()) countDown - 1 else countDown
                isBalanced(x.bLeft, newCount) && isBalanced(x.bRight, newCount)
            }

        root?.let {
            val halfCount = generateSequence(root) { it.bLeft }.filter { !it.red() }.count()
            return isBalanced(root, halfCount)
        }
        return true
    }

    fun rbSane(): Boolean {
        root?.let {
            it.isRedInvariant() &&
            it.isBalanced() &&
            it.isDepthInvariant() &&
            it.is23()
        }
        // TODO
        return true
    }

    companion object {
        const val RED: Boolean = true
        const val BLACK: Boolean = false
        fun <A: Comparable<A>, B: Any> of(fl: FList<TKVEntry<A,B>>): RBTree<A,B> = of(FListIteratorFwd(fl))
        fun <A: Comparable<A>, B: Any> of(iter: Iterator<TKVEntry<A,B>>): RBTree<A, B> {
            val tree = RBTree<A, B>()
            while(iter.hasNext())
                tree.insert(iter.next())
            return tree
        }
    }

    override fun breadthFirst(reverse: Boolean): FList<TKVEntry<A, B>> {
        TODO("Not yet implemented")
    }

    override fun copyToMutableMap(): MutableMap<@UnsafeVariance A, @UnsafeVariance B> {
        TODO("Not yet implemented")
    }

    override fun equal(rhs: IMBTree<A, B>): Boolean {
        TODO("Not yet implemented")
    }

    override fun fforEach(f: (TKVEntry<A, B>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun ffilter(isMatch: (TKVEntry<A, B>) -> Boolean): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun ffilterNot(isMatch: (TKVEntry<A, B>) -> Boolean): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun ffind(isMatch: (TKVEntry<A, B>) -> Boolean): IMList<TKVEntry<A, B>> {
        TODO("Not yet implemented")
    }

    override fun fleftMost(): TKVEntry<A, B>? {
        TODO("Not yet implemented")
    }

    override fun fpick(): TKVEntry<A, B>? {
        TODO("Not yet implemented")
    }

    override fun frightMost(): TKVEntry<A, B>? {
        TODO("Not yet implemented")
    }

    override fun froot(): TKVEntry<A, B>? = root?.entry

    override fun fsize(): Int {
        TODO("Not yet implemented")
    }

    override fun fpopAndRemainder(): Pair<TKVEntry<A, B>?, IMBTree<A, B>> {
        TODO("Not yet implemented")
    }

    override fun fmaxDepth(): Int {
        TODO("Not yet implemented")
    }

    override fun fminDepth(): Int {
        TODO("Not yet implemented")
    }

    override fun <C> fgroupBy(f: (TKVEntry<A, B>) -> C): IMMap<C, RBTree<A, B>> where C: Any, C: Comparable<C> {
        TODO("Not yet implemented")
    }

    override fun fpartition(isMatch: (TKVEntry<A, B>) -> Boolean): Pair<IMBTree<A, B>, IMBTree<A, B>> {
        TODO("Not yet implemented")
    }

    override fun <C, D : Any> fflatMap(f: (TKVEntry<A, B>) -> IMBTree<C, D>): IMBTree<C, D> where C: Any, C : Comparable<C> {
        TODO("Not yet implemented")
    }

    override fun <C, D : Any> fflatMapDup(allowDups: Boolean, f: (TKVEntry<A, B>) -> IMBTree<C, D>): IMBTree<C, D> where C: Any, C : Comparable<C> {
        TODO("Not yet implemented")
    }

    override fun <C> ffold(z: C, f: (acc: C, TKVEntry<A, B>) -> C): C {
        TODO("Not yet implemented")
    }

    override fun <C> ffoldv(z: C, f: (acc: C, B) -> C): C {
        TODO("Not yet implemented")
    }

    override fun <C, D : Any> fmap(f: (TKVEntry<A, B>) -> TKVEntry<C, D>): IMBTree<C, D> where C: Any, C : Comparable<C> {
        TODO("Not yet implemented")
    }

    override fun <D : Any> fmapToList(f: (TKVEntry<A, B>) -> D): IMList<D> {
        TODO("Not yet implemented")
    }

    override fun <C : Any> fmapvToList(f: (B) -> C): IMList<C> {
        TODO("Not yet implemented")
    }

    override fun freduce(f: (acc: TKVEntry<A,B>, TKVEntry<A,B>) -> TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): TKVEntry<A,B>? {
        TODO("Not yet implemented")
    }

    override fun finsert(item: TKVEntry<A, B>): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun finsertDup(item: TKVEntry<A, B>, allowDups: Boolean): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun finserts(items: IMList<TKVEntry<A, B>>): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun finsertsDup(items: IMList<TKVEntry<A, B>>, allowDups: Boolean): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun fcontains(item: TKVEntry<A, B>): Boolean {
        TODO("Not yet implemented")
    }

    override fun fcontainsKey(key: A): Boolean {
        TODO("Not yet implemented")
    }

    override fun fcountKey(isMatch: (A) -> Boolean): Int {
        TODO("Not yet implemented")
    }

    override fun fdropItem(item: TKVEntry<A, B>): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun fdropAll(items: IMCollection<TKVEntry<A, B>>): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun ffindItem(item: TKVEntry<A, B>): IMBTree<A, B>? {
        TODO("Not yet implemented")
    }

    override fun ffindKey(key: A): IMBTree<A, B>? {
        TODO("Not yet implemented")
    }

    override fun ffindLastItem(item: TKVEntry<A, B>): IMBTree<A, B>? {
        TODO("Not yet implemented")
    }

    override fun ffindLastKey(key: A): IMBTree<A, B>? {
        TODO("Not yet implemented")
    }

    override fun ffindValueOfKey(key: A): B? {
        TODO("Not yet implemented")
    }

    override fun fparentOf(child: TKVEntry<A, B>): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun fdropItemAll(item: TKVEntry<A, B>): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun fisDup(item: TKVEntry<A, B>): Boolean {
        TODO("Not yet implemented")
    }

    override fun fhasDups(): Boolean {
        TODO("Not yet implemented")
    }

    override fun <C, D : Any> fmapDup(allowDups: Boolean, f: (TKVEntry<A, B>) -> TKVEntry<C, D>): IMBTree<C, D> where C: Any, C : Comparable<C> {
        TODO("Not yet implemented")
    }

    override fun toIMMap(): IMMap<A, B> {
        TODO("Not yet implemented")
    }

    override fun toIMRSet(kType: RestrictedKeyType<A>?): IMSet<B>? {
        TODO("Not yet implemented")
    }

    override fun fkeyType(): RestrictedKeyType<A>? {
        TODO("Not yet implemented")
    }

    override fun <K> toIMBTree(kType: RestrictedKeyType<K>): IMBTree<K, B>? where K: Any, K : Comparable<K> {
        TODO("Not yet implemented")
    }

    override fun ffindAny(isMatch: (TKVEntry<A, B>) -> Boolean): TKVEntry<A, B>? {
        TODO("Not yet implemented")
    }

    override val seal: IMSC
        get() = TODO("Not yet implemented")

    override fun fisStrict(): Boolean {
        TODO("Not yet implemented")
    }

    override fun fpeek(): TKVEntry<A, B>? {
        TODO("Not yet implemented")
    }

    override fun ffilterKey(isMatch: (A) -> Boolean): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun ffilterKeyNot(isMatch: (A) -> Boolean): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun ffilterValue(isMatch: (B) -> Boolean): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun ffilterValueNot(isMatch: (B) -> Boolean): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun fget(key: A): B? {
        TODO("Not yet implemented")
    }

    override fun ffindAnyValue(isMatch: (B) -> Boolean): B? {
        TODO("Not yet implemented")
    }

    override fun asIMMap(): IMMap<A, B> {
        TODO("Not yet implemented")
    }

    override fun set(k: A, v: B): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun fAND(items: IMBTree<A, B>): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun fOR(items: IMBTree<A, B>): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun fXOR(items: IMBTree<A, B>): IMBTree<A, B> {
        TODO("Not yet implemented")
    }

    override fun get(key: A): B? {
        TODO("Not yet implemented")
    }
}

internal data class RBNode<A: Comparable<A>, B: Any>(
    var entry: TKVEntry<A, B>,
    var bLeft: RBNode<A,B>? = null,
    var bRight: RBNode<A,B>? = null,
    var color: Boolean = RED
): RBTree<A, B>() {
    internal fun red(): Boolean = color == RED
    internal fun redLeft(): Boolean = bLeft?.red() ?: false
    internal fun redRight(): Boolean = bRight?.red() ?: false
    internal fun leaf(): Boolean = bLeft == null && bRight == null
    override fun toString(): String {
        val col = if (color) "r" else "b"
        val bls = bLeft?.toString() ?: "*"
        val brs = bRight?.toString() ?: "*"
        return if (leaf()) "($entry@$col)" else "($entry@$col, <$bls, >$brs)"
    }
    fun md(): Int = if (leaf()) 1 else 1 + Integer.min(bLeft?.md() ?: 0,bRight?.md() ?: 0)
    fun MD(): Int = if (leaf()) 1 else 1 + Integer.max(bLeft?.MD() ?: 0,bRight?.MD() ?: 0)
    fun deepClone(): RBNode<A, B> = RBNode(
        entry.copy(),
        bLeft?.deepClone(),
        bRight?.deepClone(),
        color)

}