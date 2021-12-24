package com.xrpn.bridge

import com.xrpn.immutable.*
import com.xrpn.immutable.FListRetrieval
import java.util.concurrent.atomic.AtomicInteger

class FListIteratorBidi<out A: Any> internal constructor (val seed: FList<A>, nextIndex: Int = IX_START): ListIterator<A>, Iterable<A> {

    enum class BIDI { FWD, BACK }

    init {
        if (seed.size < nextIndex) throw IndexOutOfBoundsException()
    }

    private val nextIx = AtomicInteger(nextIndex)

    override fun hasPrevious(): Boolean = nextIndex() > IX_START

    override fun previousIndex(): Int =
        if (hasPrevious()) nextIndex() - 1
        else throw NoSuchElementException(MSG_BEFORE_START)

    override fun previous(): A =
        if (hasPrevious()) getAt(previousIndex(), BIDI.BACK)
        else throw NoSuchElementException(MSG_BEFORE_START)

    override fun hasNext(): Boolean = nextIndex() < seed.size

    override fun nextIndex(): Int = nextIx.get()

    override fun iterator(): ListIterator<A> = this

    internal val retriever: FListRetrieval<A> by lazy { object : FListRetrieval<A> {
        override fun original(): FList<A> = seed
    }}

    override fun next(): A =
        if (hasNext()) getAt(nextIndex(), BIDI.FWD)
        else throw NoSuchElementException(MSG_AFTER_END)

    private fun getAt(ix: Int, bidi: BIDI): A {
        val b: FLCons<A> = when(seed) {
            is FLNil -> throw NoSuchElementException(FListIteratorFwd.MSG_EMPTY_ITERATOR)
            is FLCons -> seed
        }
        val aux: FList<A> = b.fdrop(ix)
        when(bidi) {
            BIDI.FWD -> if (ix == nextIx.get()) nextIx.set(ix+1) else throw RuntimeException()
            BIDI.BACK -> if (ix + 1 == nextIx.get()) nextIx.set(ix) else throw RuntimeException()
        }
        return aux.fhead()!!
    }

    companion object {
        internal const val IX_START: Int = 0
        internal const val MSG_BEFORE_START = "before start requested"
        internal const val MSG_AFTER_END = "after end requested"
    }
}