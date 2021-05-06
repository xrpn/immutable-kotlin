package com.xrpn.immutable

import com.xrpn.immutable.FRBTree.Companion.insertIntKey

sealed class FSet<out A: Any> {

    fun isEmpty(): Boolean = this === FSetBody.empty
    fun size(): Int = (this as FSetBody).body.size()
    fun enumerate(): FList<A> = when {
        isEmpty() -> FLNil
        else -> (this as FSetBody).body.preorder(reverse = true).map { it.getv() }
    }

    companion object {
        fun<A: Any> emptyFSet(): FSet<A> = FSetBody.empty

        fun<A: Any> equal2(lhs: FSet<A>, rhs: FSet<A>): Boolean = when(Pair(lhs.isEmpty(), rhs.isEmpty())) {
            Pair(false, false) -> if (lhs === rhs) true else (lhs as FSetBody) == rhs
            Pair(true, true) -> true
            else -> false
        }

        fun<A: Any> FSet<A>.equal(rhs: FSet<A>): Boolean = equal2(this, rhs)

        operator fun<A: Any> FSet<A>.contains(value: A): Boolean = FRBTree.containsIntKey((this as FSetBody).body, value)
// TODO
//        fun <A: Any> union(src1: FSet<A>, src2: FSet<A>): FSet<A> = TODO()
//        fun <A: Any> intersection(src1: FSet<A>, src2: FSet<A>): FSet<A> = TODO()
//        fun <A: Any> difference(src1: FSet<A>, src2: FSet<A>): FSet<A> = TODO()
//        fun <A: Any> isSubset(src1: FSet<A>, src2: FSet<A>): FSet<A> = TODO()
//
//        fun <A: Any> add(dest: FSet<A>, item: A): FSet<A> = TODO()
//        fun <A: Any> remove(dest: FSet<A>, item: A): FSet<A> = TODO()
//
        fun <A: Any> of(fl: FList<A>): FSet<A> = FSetBody(fl.foldLeft(FRBTree.nul(), ::insertIntKey))
        fun <A: Any> of(iter: Iterator<A>): FSet<A> = of(FList.of(iter))

    }

    /* TODO
    pop(S): returns an arbitrary element of S, deleting it from S.[1]
pick(S): returns an arbitrary element of S.[2][3][4] Functionally, the mutator pop can be interpreted as the pair of selectors (pick, rest), where rest returns the set consisting of all elements except for the arbitrary element.[5] Can be interpreted in terms of iterate.[a]
map(F,S): returns the set of distinct values resulting from applying function F to each element of S.
filter(P,S): returns the subset containing all elements of S that satisfy a given predicate P.
fold(A0,F,S): returns the value A|S| after applying Ai+1 := F(Ai, e) for each element e of S, for some binary operation F. F must be associative and commutative for this to be well-defined.
clear(S): delete all elements of S.
equal(S1', S2'): checks whether the two given sets are equal (i.e. contain all and only the same elements).
hash(S): returns a hash value for the static set S such that if equal(S1, S2) then hash(S1) = hash(S2)
Other operations can be defined for sets with elements of a special type:

sum(S): returns the sum of all elements of S for some definition of "sum". For example, over integers or reals, it may be defined as fold(0, add, S).
collapse(S): given a set of sets, return the union.[6] For example, collapse({{1}, {2, 3}}) == {1, 2, 3}. May be considered a kind of sum.
flatten(S): given a set consisting of sets and atomic elements (elements that are not sets), returns a set whose elements are the atomic elements of the original top-level set or elements of the sets it contains. In other words, remove a level of nesting – like collapse, but allow atoms. This can be done a single time, or recursively flattening to obtain a set of only atomic elements.[7] For example, flatten({1, {2, 3}}) == {1, 2, 3}.
nearest(S,x): returns the element of S that is closest in value to x (by some metric).
min(S), max(S): returns the minimum/maximum element of S.
     */
}

internal class FSetBody<out A: Any> internal constructor (
    val body: FRBTree<Int, A>
): FSet<A>() {

    override fun equals(other: Any?): Boolean =
        if (this === other) true
        else if (other == null) false
        else if (other is FSetBody<*>) {
            if (this.isEmpty() && other.body.isEmpty()) true
            else if (this.body.isEmpty() || other.body.isEmpty()) false
            else (this.body.root()!!::class == other.body.root()!!::class) && BTreeTraversable.equal(
                this.body,
                other.body
            )
        } else false

    override fun hashCode(): Int = TODO("forbidden")

    override fun toString(): String {
        return if (this.isEmpty()) FSet::class.simpleName+"(EMPTY)"
        else this::class.simpleName+"("+body.toString()+")"
    }

    companion object {
        val empty = FSetBody(FRBTNil)
    }

}
