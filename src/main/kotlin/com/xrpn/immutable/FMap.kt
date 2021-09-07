package com.xrpn.immutable

import com.xrpn.imapi.IMBTreeEqual2
import com.xrpn.immutable.FRBTree.Companion.rbtFindValueOFKey
import com.xrpn.immutable.FRBTree.Companion.rbtInsert
import com.xrpn.immutable.FRBTree.Companion.rbtInserts

//
// W       W  I  P P P
// W       W  I  P    P
// W   W   W  I  P P P
//  W W W W   I  P
//   W   W    I  P
//

sealed class FMap<out A, out B: Any> where A: Any, A: Comparable<@UnsafeVariance A> {

    fun isEmpty(): Boolean = this === FMapBody.empty
    fun size(): Int = (this as FMapBody).body.size
    fun keys(): FIKSet<A> = when {
        isEmpty() -> FIKSet.emptyIMSet()
        else -> {
            this as FMapBody
            FIKSet.of(this.body.preorder(reverse = true).fmap { it.getk() })
        }
    }
    fun values(): FList<B> = when {
        isEmpty() -> FLNil
        else -> {
            this as FMapBody
            this.body.preorder(reverse = true).fmap { it.getv() }
        }
    }
    fun entries(): FList<TKVEntry<A,B>> = when {
        isEmpty() -> FLNil
        else -> (this as FMapBody).body.preorder(reverse = true)
    }

    companion object {

        fun<A: Comparable<A>, B: Any> emptyFMap(): FMap<A, B> = FMapBody.empty

        fun<A: Comparable<A>, B: Any> equal2(lhs: FMap<A,B>, rhs: FMap<A,B>): Boolean = when(Pair(lhs.isEmpty(), rhs.isEmpty())) {
            Pair(false, false) -> if (lhs === rhs) true else (lhs as FMapBody) == rhs
            Pair(true, true) -> true
            else -> false
        }

        fun<A: Comparable<A>, B: Any> FMap<A,B>.equal(rhs: FMap<A,B>): Boolean = equal2(this, rhs)

        fun<A: Comparable<A>, B: Any> FMap<A,B>.get(key: A): B? = when {
            this.isEmpty() -> null
            else -> rbtFindValueOFKey((this as FMapBody).body, key)
        }

        operator fun<A: Comparable<A>, B: Any> FMap<A,B>.contains(key: A): Boolean = when {
            this.isEmpty() -> false
            else -> rbtFindValueOFKey((this as FMapBody).body, key) != null
        }

        fun<A: Comparable<A>, B: Any> FMap<A,B>.getOrElse(key: A, default:B): B = get(key) ?: default

        fun<A: Comparable<A>, B: Any> FMap<A,B>.add(key: A, value: B): FMap<A,B> = FMapBody(rbtInsert((this as FMapBody).body, TKVEntry.of(key, value)))

        fun<A: Comparable<A>, B: Any> FMap<A,B>.add(p: Pair<A, B>): FMap<A,B> = FMapBody(rbtInsert((this as FMapBody).body, TKVEntry.of(p)))

        fun<A: Comparable<A>, B: Any> FMap<A,B>.add(ps: FList<Pair<A, B>>): FMap<A,B> = FMapBody(rbtInserts((this as FMapBody).body, ps.fmap { TKVEntry.of(it) }))

        fun <A: Comparable<A>, B: Any> of(_body: FList<TKVEntry<A,B>>): FMap<A, B> =
            if (_body is FLNil) FMapBody.empty else FMapBody(FRBTree.of(_body))

        fun <A: Comparable<A>, B: Any> of(_body: Iterator<TKVEntry<A,B>>): FMap<A, B> = FMapBody(FRBTree.of(_body))

    }
}

internal class FMapBody<out A, out B: Any> internal constructor (
    val body: FRBTree<A, B>
): FMap<A, B>() where A: Any, A: Comparable<@UnsafeVariance A> {

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is FMapBody<*, *> -> when {
            this.isEmpty() && other.body.fempty() -> true
            this.body.fempty() || other.body.fempty() -> false
            this.body.froot()!!::class == other.body.froot()!!::class ->
                @Suppress("UNCHECKED_CAST") IMBTreeEqual2(this.body, other.body as FRBTree<A, B>)
            else -> false
        }
        else -> false
    }

    override fun hashCode(): Int = TODO("forbidden")

    override fun toString(): String {
        return if (this.isEmpty()) FMap::class.simpleName+"(EMPTY)"
        else this::class.simpleName+"("+body.toString()+")"
    }

    companion object {
        val empty = FMapBody(FRBTNil)
    }

}
