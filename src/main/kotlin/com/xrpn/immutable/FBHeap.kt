package com.xrpn.immutable

import kotlin.math.max

sealed class FBHeap<out A, out B : Any> where A: Any, A: Comparable<@UnsafeVariance A> {

    /*
        Binary heaps are commonly viewed as binary trees which satisfy two invariants. The shape invariant:
        the tree is a complete binary tree. The min [max] heap invariant: each node is less than or equal to
        [greater than or equal to] each of its children.

        A perfect binary tree contains 2^(h+1) − 1 nodes, where h is the height of the tree.  A tree is perfect
        if its children are perfect trees of identical height.  A recursive implementation will therefore be
        adequately stack safe.

        This is a mostly recursive implementation of a binary heap tree, freely adapted from the paper "A
        Functional Approach to Standard Binary Heaps" by Vladimir Kostyukov

        that paper retrieved from https://arxiv.org/pdf/1312.4666.pdf (December 2020)
     */

    fun top(): TKVEntry<A, B>? = when (this) {
        is FBHMinNil, is FBHMaxNil -> null
        is FBHBranch -> this.entry
    }

    internal fun left(): FBHeap<A, B> = when (this) {
        is FBHMinNil, is FBHMaxNil -> this
        is FBHBranch -> this.bLeft
    }

    internal fun right(): FBHeap<A, B> = when (this) {
        is FBHMinNil, is FBHMaxNil -> this
        is FBHBranch -> this.bRight
    }

    fun size(): Int = when (this) {
        is FBHMinNil, is FBHMaxNil -> 0
        is FBHBranch -> this.size
    }

    internal fun height(): Int = when (this) {
        is FBHMinNil, is FBHMaxNil -> 0
        is FBHBranch -> this.height
    }

    fun isEmpty(): Boolean = this is FBHMinNil || this is FBHMaxNil

    internal fun isSizeSane(): Boolean {
        val size = this.size()
        val perfectSize = 2 shl (this.height()) - 1
        val leavesCount = 2 shl (this.height() - 1)
        val previous = perfectSize - leavesCount
        val p1 = previous < size
        val p2 = size <= perfectSize
        val sane = p1 && p2
        return sane
    }

    override fun equals(other: Any?): Boolean = when (this) {
        is FBHMinNil -> other is FBHMinNil
        is FBHMaxNil -> other is FBHMaxNil
        is FBHBranch -> when (other) {
            is FBHBranch<*, *> -> other.equals(this)
            else -> false
        }
    }

    override fun hashCode(): Int = when (this) {
        is FBHMinNil -> FBHMinNil.hashCode()
        is FBHMaxNil -> FBHMaxNil.hashCode()
        is FBHBranch -> FBHBranch.Companion.hashCode(
            this
        )
    }

    companion object {

        internal enum class FIT {
            ABOVE, BELOW
        }

        internal enum class RULE {
            MIN, MAX
        }

        internal fun <A, B: Any> minHeapNul(): FBHeap<A, B> where A: Any, A: Comparable<A> =
            FBHMinNil

        internal fun <A, B: Any> maxHeapNul(): FBHeap<A, B> where A: Any, A: Comparable<A> =
            FBHMaxNil

        private fun <A, B: Any> rule(h: FBHeap<A, B>): RULE where A: Any, A: Comparable<A> = when (h) {
            is FBHMinNil -> FBHeap.Companion.RULE.MIN
            is FBHMaxNil -> FBHeap.Companion.RULE.MAX
            is FBHBranch -> when (Pair(h.bLeft is FBHBranch, h.bRight is FBHBranch)) {
                Pair(false, false), Pair(false, true) -> FBHeap.Companion.rule(
                    h.bLeft
                )
                Pair(false, false), Pair(true, false) -> FBHeap.Companion.rule(
                    h.bRight
                )
                else -> FBHeap.Companion.rule(h.bRight)
            }
        }

        private fun <A, B: Any> fit(item: TKVEntry<A, B>, b: FBHBranch<A, B>, r: RULE): FIT where A: Any, A: Comparable<A> =
            when (r) {
                Companion.RULE.MIN -> when {
                    item.getk() < b.entry.getk() -> Companion.FIT.ABOVE
                    else -> Companion.FIT.BELOW
                }
                Companion.RULE.MAX -> when {
                    b.entry.getk() < item.getk() -> FBHeap.Companion.FIT.ABOVE
                    else -> FBHeap.Companion.FIT.BELOW
                }
            }

        private fun <A, B: Any> isHeapBranch(b: FBHBranch<A, B>, R: RULE): Boolean where A: Any, A: Comparable<A> =
            when (Pair(b.bLeft is FBHBranch, b.bRight is FBHBranch)) {
                Pair(true, true) -> {
                    b.bLeft as FBHBranch
                    b.bRight as FBHBranch
                    FBHeap.Companion.fit(
                        b.bLeft.entry,
                        b,
                        R
                    ) == FBHeap.Companion.FIT.BELOW && FBHeap.Companion.fit(
                        b.bRight.entry,
                        b,
                        R
                    ) == FBHeap.Companion.FIT.BELOW
                }
                Pair(true, false) -> {
                    b.bLeft as FBHBranch
                    FBHeap.Companion.fit(
                        b.bLeft.entry,
                        b,
                        R
                    ) == FBHeap.Companion.FIT.BELOW
                }
                Pair(false, true) -> {
                    b.bRight as FBHBranch
                    FBHeap.Companion.fit(
                        b.bRight.entry,
                        b,
                        R
                    ) == FBHeap.Companion.FIT.BELOW
                }
                else -> b.bRight == FBHeap.Companion.asMatchingNil(R) && b.bLeft == b.bRight
            }

        internal fun <A, B: Any> heapSane(heap: FBHeap<A, B>): Boolean where A: Any, A: Comparable<A> {

            tailrec fun go(h: FBHeap<A, B>, sane: Boolean): Boolean {
                if (!sane) return false
                return when (h) {
                    is FBHMinNil, is FBHMaxNil -> sane
                    is FBHBranch -> {
                        val acc1 = sane && h.isSizeSane()
                        val acc2 = acc1 && FBHeap.Companion.isHeapBranch(
                            h,
                            FBHeap.Companion.rule(h)
                        )
                        val (newHeap, _) = FBHeap.Companion.pop(h)
                        go(newHeap, acc2)
                    }
                }
            }

            return go(heap, true)
        }

        private fun <A, B : Any> bubbleUp(
            item: TKVEntry<A, B>,
            l: FBHeap<A, B>,
            r: FBHeap<A, B>,
            R: FBHeap.Companion.RULE
        ): FBHeap<A, B> where A: Any, A: Comparable<A> =
            if (l is FBHBranch && FBHeap.Companion.FIT.BELOW == FBHeap.Companion.fit(
                    item,
                    l,
                    R
                )
            ) {
                FBHBranch(
                    l.entry,
                    FBHBranch(item, l.bLeft, l.bRight),
                    r
                )
            } else if (r is FBHBranch && FBHeap.Companion.FIT.BELOW == FBHeap.Companion.fit(
                    item,
                    r,
                    R
                )
            ) {
                FBHBranch(
                    r.entry,
                    l,
                    FBHBranch(item, r.bLeft, r.bRight)
                )
            } else FBHBranch(item, l, r)

        private fun asMatchingNil(R: FBHeap.Companion.RULE) = if (FBHeap.Companion.RULE.MAX == R) FBHMaxNil else FBHMinNil

        internal fun <A, B : Any> insert(
            dest: FBHeap<A, B>,
            item: TKVEntry<A, B>,
            R: FBHeap.Companion.RULE
        ): FBHeap<A, B> where A: Any, A: Comparable<A> {
            if (dest.isEmpty() && dest != FBHeap.Companion.asMatchingNil(R)) throw RuntimeException("insert into $dest with opposite rule")
            return if (dest.isEmpty()) FBHBranch(
                item,
                FBHeap.Companion.asMatchingNil(R),
                FBHeap.Companion.asMatchingNil(R)
            )
            else if (dest.right().height() < dest.left().height())
                FBHeap.Companion.bubbleUp(
                    dest.top()!!,
                    dest.left(),
                    FBHeap.Companion.insert(dest.right(), item, R),
                    R
                )
            else if (dest.right().height() > dest.left().height())
                FBHeap.Companion.bubbleUp(
                    dest.top()!!,
                    FBHeap.Companion.insert(dest.left(), item, R),
                    dest.right(),
                    R
                )
            else {
                val rightSize = dest.right().size()
                val perfectRightSize = 2 shl (dest.right().height()) - 1
                if (rightSize < perfectRightSize) FBHeap.Companion.bubbleUp(
                    dest.top()!!,
                    dest.left(),
                    FBHeap.Companion.insert(dest.right(), item, R),
                    R
                )
                else FBHeap.Companion.bubbleUp(
                    dest.top()!!,
                    FBHeap.Companion.insert(dest.left(), item, R),
                    dest.right(),
                    R
                )
            }
        }

        private fun <A, B : Any> bubbleDown(
            item: TKVEntry<A, B>,
            l: FBHeap<A, B>,
            r: FBHeap<A, B>,
            R: FBHeap.Companion.RULE
        ): FBHeap<A, B> where A: Any, A: Comparable<A> =
            if (l is FBHBranch && r is FBHBranch && (FBHeap.Companion.FIT.ABOVE == FBHeap.Companion.fit(
                    r.entry,
                    l,
                    R
                ) && FBHeap.Companion.FIT.BELOW == FBHeap.Companion.fit(
                    item,
                    r,
                    R
                ))
            ) {
                FBHBranch(
                    r.entry,
                    l,
                    FBHeap.Companion.bubbleDown(item, r.bLeft, r.bRight, R)
                )
            } else if (l is FBHBranch && FBHeap.Companion.FIT.BELOW == FBHeap.Companion.fit(
                    item,
                    l,
                    R
                )
            ) {
                FBHBranch(
                    l.entry,
                    FBHBranch(item, l.bLeft, l.bRight),
                    r
                )
            } else FBHBranch(item, l, r)

        private fun <A, B : Any> bubbleRootDown(h: FBHeap<A, B>, R: FBHeap.Companion.RULE): FBHeap<A, B> where A: Any, A: Comparable<A> =
            if (h.isEmpty()) h else FBHeap.Companion.bubbleDown(
                h.top()!!,
                h.left(),
                h.right(),
                R
            )

        private fun <A, B : Any> floatLeft(
            item: TKVEntry<A, B>,
            l: FBHeap<A, B>,
            r: FBHeap<A, B>
        ): FBHeap<A, B> where A: Any, A: Comparable<A> =
            when (l) {
                is FBHBranch -> FBHBranch(
                    l.entry,
                    FBHBranch(item, l.left(), l.right()),
                    r
                )
                else -> FBHBranch(item, l, r)
            }

        private fun <A, B : Any> floatRight(
            item: TKVEntry<A, B>,
            l: FBHeap<A, B>,
            r: FBHeap<A, B>
        ): FBHeap<A, B> where A: Any, A: Comparable<A> =
            when (r) {
                is FBHBranch -> FBHBranch(
                    r.entry,
                    l,
                    FBHBranch(item, r.left(), r.right())
                )
                else -> FBHBranch(item, l, r)
            }

        private fun <A, B : Any> mergeChildren(l: FBHeap<A, B>, r: FBHeap<A, B>): FBHeap<A, B> where A: Any, A: Comparable<A> =
            if (l.isEmpty() && r.isEmpty()) l // or, identically, r
            else if (r.height() < l.height())
                FBHeap.Companion.floatLeft(
                    l.top()!!,
                    FBHeap.Companion.mergeChildren(l.left(), l.right()),
                    r
                )
            else if (r.height() > l.height())
                FBHeap.Companion.floatRight(
                    r.top()!!,
                    l,
                    FBHeap.Companion.mergeChildren(r.left(), r.right())
                )
            else {
                val rightSize = r.size()
                val perfectRightSize = 2 shl (r.height()) - 1
                if (rightSize < perfectRightSize)
                    FBHeap.Companion.floatRight(
                        r.top()!!,
                        l,
                        FBHeap.Companion.mergeChildren(r.left(), r.right())
                    )
                else
                    FBHeap.Companion.floatLeft(
                        l.top()!!,
                        FBHeap.Companion.mergeChildren(l.left(), l.right()),
                        r
                    )
            }

        private fun <A, B : Any> remove(src: FBHeap<A, B>, R: FBHeap.Companion.RULE): FBHeap<A, B> where A: Any, A: Comparable<A> =
            if (src.isEmpty()) src
            else FBHeap.Companion.bubbleRootDown(
                FBHeap.Companion.mergeChildren(
                    src.left(),
                    src.right()
                ), R
            )

        fun <A : Comparable<A>, B : Any> add(dest: FBHeap<A, B>, item: TKVEntry<A, B>): FBHeap<A, B> =
            FBHeap.Companion.insert(
                dest,
                item,
                FBHeap.Companion.rule(dest)
            )

        fun <A, B : Any> pop(src: FBHeap<A, B>): Pair<FBHeap<A, B>, TKVEntry<A, B>?> where A: Any, A: Comparable<A> =
            when (src) {
                is FBHMinNil, is FBHMaxNil -> Pair(src, null)
                is FBHBranch -> Pair(
                    FBHeap.Companion.remove(
                        src,
                        FBHeap.Companion.rule(src)
                    ), src.entry
                )
            }

        fun <A, B : Any> enumerate(heap: FBHeap<A, B>): FList<TKVEntry<A, B>> where A: Any, A: Comparable<A> {

            tailrec fun go(h: FBHeap<A, B>, acc: FList<TKVEntry<A, B>>): FList<TKVEntry<A, B>> = when (h) {
                is FBHMinNil, is FBHMaxNil -> acc
                is FBHBranch -> {
                    val (newHeap, entry) = FBHeap.Companion.pop(h)
                    go(newHeap, FLCons(entry!!, acc))
                }
            }

            return go(heap, FLNil)
        }

        fun <A, B : Any> equal2(rhs: FBHeap<A, B>, lhs: FBHeap<A, B>): Boolean where A: Any, A: Comparable<A> =
            when (Pair(lhs.isEmpty(), rhs.isEmpty())) {
                Pair(false, false) -> if (rhs === lhs) true else {
                    val lh = lhs.height()
                    val rh = rhs.height()
                    val ls = lhs.size()
                    val rs = rhs.size()
                    when {
                        lh != rh || ls != rs -> false
                        ls == 0 -> true
                        else -> {
                            val lr = FBHeap.Companion.rule(lhs)
                            val rr = FBHeap.Companion.rule(rhs)
                            lr == rr && FBHeap.Companion.enumerate(lhs) == FBHeap.Companion.enumerate(
                                rhs
                            )
                        }
                    }
                }
                Pair(true, true) -> true
                else -> false
            }

        fun <A, B : Any> FBHeap<A, B>.equal(rhs: FBHeap<A, B>): Boolean where A: Any, A: Comparable<A> =
            FBHeap.Companion.equal2(this, rhs)

        internal fun <A, B : Any> of(items: Array<TKVEntry<A, B>>, R: FBHeap.Companion.RULE): FBHeap<A, B> where A: Any, A: Comparable<A> {
            fun loop(i: Int): FBHeap<A, B> =
                if (i < items.size) FBHeap.Companion.bubbleDown(
                    items[i],
                    loop(2 * i + 1),
                    loop(2 * i + 2),
                    R
                ) else if (FBHeap.Companion.RULE.MIN == R) FBHMinNil else FBHMaxNil
            return loop(0)
        }

        fun <A, B : Any> minHeapOf(items: Array<TKVEntry<A, B>>): FBHeap<A, B> where A: Any, A: Comparable<A> =
            FBHeap.Companion.of(
                items,
                FBHeap.Companion.RULE.MIN
            )

        fun <A, B : Any> maxHeapOf(items: Array<TKVEntry<A, B>>): FBHeap<A, B> where A: Any, A: Comparable<A> =
            FBHeap.Companion.of(
                items,
                FBHeap.Companion.RULE.MAX
            )

        internal fun <A, B : Any> of(fl: FList<TKVEntry<A, B>>, R: FBHeap.Companion.RULE): FBHeap<A, B> where A: Any, A: Comparable<A> {
            return FBHeap.Companion.of(
                FList.Companion.toArray(
                    fl
                ), R
            )
        }

        fun <A, B : Any> minHeapOf(fl: FList<TKVEntry<A, B>>): FBHeap<A, B> where A: Any, A: Comparable<A> =
            FBHeap.Companion.of(
                fl,
                FBHeap.Companion.RULE.MIN
            )

        fun <A, B : Any> maxHeapOf(fl: FList<TKVEntry<A, B>>): FBHeap<A, B> where A: Any, A: Comparable<A> =
            FBHeap.Companion.of(
                fl,
                FBHeap.Companion.RULE.MAX
            )

        internal fun <A, B : Any> of(iter: Iterator<TKVEntry<A, B>>, R: FBHeap.Companion.RULE): FBHeap<A, B> where A: Any, A: Comparable<A> {
            return FBHeap.Companion.of(
                FList.Companion.toArray(
                    FList.Companion.of(iter)
                ), R
            )
        }

        fun <A, B : Any> minHeapOf(iter: Iterator<TKVEntry<A, B>>): FBHeap<A, B> where A: Any, A: Comparable<A> =
            FBHeap.Companion.of(
                iter,
                FBHeap.Companion.RULE.MIN
            )

        fun <A, B : Any> maxHeapOf(iter: Iterator<TKVEntry<A, B>>): FBHeap<A, B> where A: Any, A: Comparable<A> =
            FBHeap.Companion.of(
                iter,
                FBHeap.Companion.RULE.MAX
            )

        fun <A, B : Any> emptyMaxHeap(): FBHeap<A, B> where A: Any, A: Comparable<A> =
            FBHeap.Companion.maxHeapNul()

        fun <A, B : Any> emptyMinHeap(): FBHeap<A, B> where A: Any, A: Comparable<A> =
            FBHeap.Companion.minHeapNul()
    }
}

internal object FBHMinNil : FBHeap<Nothing, Nothing>() {
    override fun toString(): String = "-"
}

internal object FBHMaxNil : FBHeap<Nothing, Nothing>() {
    override fun toString(): String = "+"
}

internal data class FBHBranch<out A, out B : Any>(
    val entry: TKVEntry<A, B>,
    val bLeft: FBHeap<A, B>,
    val bRight: FBHeap<A, B>,
    val size: Int = bLeft.size() + bRight.size() + 1,
    val height: Int = max(bLeft.height(), bRight.height()) + 1
) : FBHeap<A, B>() where A : Any, A : Comparable<@UnsafeVariance A> {

    internal fun leaf() = (bLeft is FBHMinNil && bRight is FBHMinNil) || (bLeft is FBHMaxNil && bRight is FBHMaxNil)

    override fun toString(): String = "($entry@(${size()}#${height()}), <$bLeft, >$bRight)"

    private inline fun <reified Self : FBHBranch<@UnsafeVariance A, @UnsafeVariance B>> equalsImpl(other: Any?): Boolean =
        when {
            this === other -> true
            other == null -> false
            other is Self -> 0 == other.entry.getk().compareTo(this.entry.getk()) && FBHeap.Companion.equal2(
                this,
                other
            )
            else -> false
        }

    override fun equals(other: Any?): Boolean = equalsImpl<FBHBranch<A, B>>(other)

    override fun hashCode(): Int {
        var aux = entry.hashCode().toLong()
        aux = 3 * aux + FBHeap.Companion.enumerate(bLeft).hashCode()
        aux = 3 * aux + FBHeap.Companion.enumerate(bRight).hashCode()
        return if (Int.MIN_VALUE < aux && aux < Int.MAX_VALUE) aux.toInt()
        else /* may it even theoretically get here? */ TODO("must reduce range of FBHBranch.hashcode to Int")
    }

    companion object {
        fun hashCode(b: FBHBranch<*, *>) = b.hashCode()
    }
}