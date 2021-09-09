package com.xrpn.immutable

import com.xrpn.bridge.FIKSetIterator
import com.xrpn.hash.JohnsonTrotter.jtPermutations
import com.xrpn.hash.JohnsonTrotter.smallFact
import com.xrpn.imapi.*
import com.xrpn.immutable.FIKSetOfOne.Companion.toSoO
import com.xrpn.immutable.FList.Companion.emptyIMList
import com.xrpn.immutable.FRBTree.Companion.finsertIK
import com.xrpn.immutable.FRBTree.Companion.nul
import com.xrpn.immutable.FRBTree.Companion.rbtInsert
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry

sealed class FIKSet<out A: Any>: Set<A>, IMSet<A> {

    @Deprecated("Set has no ordering.", ReplaceWith("ffilterNot"))
    fun dropWhile(predicate: (A) -> Boolean): List<A> = throw RuntimeException(predicate.toString())
    @Deprecated("Set has no ordering.", ReplaceWith("ffilter()"))
    fun takeWhile(predicate: (A) -> Boolean): List<A> = throw RuntimeException(predicate.toString())
    @Deprecated("Set has no ordering.", ReplaceWith("fpermutations() or fcombinations()"))
    fun windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): List<List<A>> = throw RuntimeException("$size $step $partialWindows")
    @Deprecated("Set has no ordering.", ReplaceWith("(no replacement)"))
    fun <B> runningFold(initial: B, operation: (acc: B, A) -> B): List<B> = throw RuntimeException("$initial $operation")
    @Deprecated("Set has no ordering.", ReplaceWith("(no replacement)"))
    fun <B> runningFoldIndexed(initial: B, operation: (index: Int, acc: B, A) -> B): List<B> = throw RuntimeException("$initial $operation")
    @Deprecated("Set has no ordering.", ReplaceWith("(no replacement)"))
    fun runningReduce(operation: (acc: A, A) -> @UnsafeVariance A): List<A> = throw RuntimeException("$operation")
    @Deprecated("Set has no ordering.", ReplaceWith("(no replacement)"))
    fun runningReduceIndexed(operation: (index: Int, acc: A, A) -> @UnsafeVariance A): List<A> = throw RuntimeException("$operation")
    @Deprecated("Set has no ordering.", ReplaceWith("withIndex()"))
    fun <B, C> zip(other: Array<out B>, transform: (a: A, b: B) -> C): List<C> = throw RuntimeException("$other, $transform")
    @Deprecated("Set has no ordering.", ReplaceWith("withIndex()"))
    fun <B> zip(other: Iterable<B>): List<Pair<A, B>> = throw RuntimeException("$other")
    @Deprecated("Set has no ordering.", ReplaceWith("fpermutations() or fcombinations()"))
    fun zipWithNext(): List<Pair<A, A>> = throw RuntimeException()
    @Deprecated("Set has no ordering.", ReplaceWith("fpermutations().fmap() or fcombinations().fmap()"))
    fun <B> zipWithNext(transform: (a: A, b: A) -> B): List<B>  = throw RuntimeException("$transform")

    // from Collection<A>

    override val size: Int by lazy { this.toFRBTree().size }

    override fun contains(element: @UnsafeVariance A): Boolean = this.fcontains(FIKSetOfOne(element))

    override fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean {
        elements.forEach { if (!this.fcontains(FIKSetOfOne(it))) return false }
        return true
    }

    override fun iterator(): Iterator<A> = FIKSetIterator(this)

    // utility

    override fun equal(rhs: IMSet<@UnsafeVariance A>): Boolean =
        this.equals(rhs)

    override fun fforEach (f: (A) -> Unit) =
        this.toFRBTree().fforEach { tkv -> f(tkv.getv()) }

    override fun toIMBTree(): IMBTree<Int, A> =
        this.toFRBTree()

    override fun copy(): IMSet<A> =
        FIKSetBody.of(this.toFRBTree().ffold(nul()) { acc, tkv -> acc.finsert(tkv) })

    override fun copyToMutableSet(): MutableSet<@UnsafeVariance A> = this.toFRBTree()
        .ffold(mutableSetOf()) { acc, tkv -> acc.add(tkv.getv()); acc }

    // filtering

    override fun fcontains(item: FIKSetOfOne<@UnsafeVariance A>): Boolean =
        this.toFRBTree().fcontainsKey(TKVEntry.intKeyOf(item.one))

    override fun fcontainsAny(items: IMSet<@UnsafeVariance A>): Boolean = if (items.fempty()) !this.fempty() else try {
        val outer = if (this.toIMBTree().fsize() < items.toIMBTree().fsize()) this.toIMBTree() else items.toIMBTree()
        val mark = if (this.toIMBTree().fsize() < items.toIMBTree().fsize()) items.toIMBTree() else this.toIMBTree()
        // TODO gross, need to think something better...
        outer.ffold(0) { acc, tkv -> if(mark.fcontains(tkv)) throw BreakoutException() else acc+1 }
        false
    } catch (ex: BreakoutException) {
        true
    }

    override fun fdropItem(item: FIKSetOfOne<@UnsafeVariance A>): FIKSet<A> {
        val aux: FRBTree<Int, A> = this.toFRBTree().fdropItem(item.one.toIAEntry())
        return FIKSetBody.of(aux)
    }

    override fun fdropAll(items: IMSet<@UnsafeVariance A>): FIKSet<A> {
        val aux = this.toFRBTree().fdropAlt(items.toIMBTree()) as FRBTree<Int, A>
        return FIKSetBody.of(aux)
    }

    override fun ffilter(isMatch: (A) -> Boolean): FIKSet<A> =
        FIKSetBody.of(this.toFRBTree().ffilter { tkv -> isMatch(tkv.getv()) })

    override fun ffilterNot(isMatch: (A) -> Boolean): FIKSet<A> {
        val notMatch: (a: A) -> Boolean = { a -> !isMatch(a) }
        return ffilter(notMatch)
    }

    override fun ffindDistinct(isMatch: (A) -> Boolean): A? =
        this.toFRBTree().ffindDistinct{ tkv -> isMatch(tkv.getv()) }?.getv()

    override fun fisSubsetOf(rhs: IMSet<@UnsafeVariance A>): Boolean {
        val superset = rhs.toIMBTree()
        val maybeSubset = this.toIMBTree()
        return this.fsize() == maybeSubset.fcount(superset::fcontains)
    }

    override fun fpick(): A? = this.toIMBTree().fpick()?.getv()

    override fun fAND(items: IMSet<@UnsafeVariance A>): FIKSet<A> =
        FIKSetBody.of(this.toFRBTree().ffold(nul()) {
            stub, tkv -> if (items.fcontains(FIKSetOfOne(tkv.getv()))) stub.finsert(tkv) else stub
        })

    override fun fOR(items: IMSet<@UnsafeVariance A>): FIKSet<A> {
        val aux = this.toFRBTree().finsertt(items.toIMBTree()) as FRBTree<Int, A>
        return FIKSetBody.of(aux)
    }

    override fun fXOR(items: IMSet<@UnsafeVariance A>): FIKSet<A> {
        val bothHave: FRBTree<Int, A> = this.fAND(items).toFRBTree()
        val thisOnly: FRBTree<Int, A> = this.toFRBTree().fdropAlt(bothHave) as FRBTree<Int, A>
        val itemsOnly = when(items) {
            is FIKSet -> items.toFRBTree().fdropAlt(bothHave) as FRBTree<Int, A>
            else -> items.fdropAll(FIKSetBody.of(bothHave)).toIMBTree() as FRBTree<Int, A>
        }
        return FIKSetBody.of(thisOnly.finsertt(itemsOnly) as FRBTree<Int, A>)

    }

    // grouping

    override fun <B: Any> fcartesian(rhs: IMSet<B>): FIKSet<Pair<A, B>> {

        tailrec fun go(shrink: FIKSet<A>, stay: IMSet<B>, acc: FIKSet<Pair<A, B>>): FIKSet<Pair<A, B>> = if (shrink.fempty()) acc else {
            val (pop, reminder) = shrink.fpopAndReminder()
            val newAcc = pop?.let{
                acc.fOR( stay.ffold(emptyIMSet()) {
                    set, a -> set.fadd(FIKSetOfOne(Pair(it, a)))
                }
            )} ?: acc
            go(reminder, stay, newAcc)
        }

        return go(this, rhs, emptyIMSet())
    }

    override fun fcombinations(size: Int): FIKSet<FIKSet<A>> {

        // all unique subsets up to "size" members from this set; order does not matter

        tailrec fun gogo(item: A, fatSet: FIKSet<FIKSet<A>>, acc: FIKSet<FIKSet<A>>) : FIKSet<FIKSet<A>> {
            val (pop: FIKSet<A>?, reminder: FIKSet<FIKSet<A>>) = fatSet.fpopAndReminder()
            return if (pop == null) acc else {
                if (pop.size < size) {
                    val aux: FIKSet<A> = pop.fadd(item.toSoO())
                    val newAcc: FIKSet<FIKSet<A>> = acc.fadd(aux.toSoO())
                    gogo(item, reminder, newAcc)
                } else gogo(item, reminder, acc)
            }
        }

        tailrec fun go(shrink: FIKSet<A>, acc: FIKSet<FIKSet<A>>): FIKSet<FIKSet<A>> = if (shrink.fempty()) acc else {
            val (pop, reminder) = shrink.fpopAndReminder()
            val newAcc = pop?.let {
                val outer: FIKSet<FIKSet<A>> = acc.fadd(of(it).toSoO())
                gogo(it, outer, outer)
            } ?: acc
            go(reminder, newAcc)
        }

        return if (size < 1) emptyIMSet() else go(this, emptyIMSet())
    }

    override fun fcount(isMatch: (A) -> Boolean): Int =
        this.toFRBTree().fcount { tkv -> isMatch(tkv.getv()) }

    override fun <B> fgroupBy(f: (A) -> B): IMMap<B, FIKSet<A>> where B: Any, B: Comparable<B> {
        TODO("Not yet implemented")
    }

    override fun findexed(offset: Int): FIKSet<Pair<A, Int>> {
        var index = offset - 1
        return FIKSetBody.of(this.toFRBTree().fmap{ tkv -> index+=1; Pair(tkv.getv(), index).toIAEntry() })
    }

    override fun fpartition(isMatch: (A) -> Boolean): Pair<FIKSet<A>, FIKSet<A>> {
        val (t: FRBTree<Int, A>, f: FRBTree<Int, A>) = this.toFRBTree().fpartition { tkv -> isMatch(tkv.getv()) }
        return Pair(FIKSetBody.of(t), FIKSetBody.of(f))
    }

    override fun fpermutations(size: Int): Collection<FList<A>> {

        // todo consider memoization

        tailrec fun goSmall(shrink: FIKSet<FIKSet<A>>, acc: FIKSet<FList<A>>): FIKSet<FList<A>> = if (shrink.fempty()) acc else {
            val (pop, reminder) = shrink.fpopAndReminder()
            val newAcc: FIKSet<FList<A>> = pop?.let { acc.fOR(it.fpermute() as FIKSet<FList<A>>) } ?: acc
            goSmall(reminder, newAcc)
        }

        tailrec fun goLarge(shrink: FIKSet<FIKSet<A>>, acc: FList<FList<A>>): FList<FList<A>> = if (shrink.fempty()) acc else {
            val (pop, reminder) = shrink.fpopAndReminder()
            val newAcc: FList<FList<A>> = if (pop == null) acc else {
                val perms = pop.fpermute() as FList<FList<A>>
                perms.ffoldLeft(acc) { pacc, l -> FLCons(l, pacc) }
            }
            goLarge(reminder, newAcc)
        }

        return if (size < 1 || this.size < size) emptyIMSet() else {
            val sizedCmbs: FIKSet<FIKSet<A>> = this.fcombinations(size).ffilter { it.size == size }
            if (this.size < PERMUTATIONCARDLIMIT) goSmall(sizedCmbs, emptyIMSet()) else goLarge(sizedCmbs, emptyIMList())
        }
    }

    // this not stack safe for "large" sets
    private fun permuteRecursively(): FIKSet<FList<A>> = when (this.size) {
        0 -> emptyIMSet()
        1 -> this.copyToFList().toSoO()
        else -> {
            val allItems: FIKSet<FList<A>> = this.fpermutations(1) as FIKSet<FList<A>>
            allItems.ffold(emptyIMSet()) { sol, listOf1 ->
                sol.fOR(this.fdropItem(listOf1.fhead()!!.toSoO()).permuteRecursively().ffold(emptyIMSet()) { psol, pl ->
                    psol.fadd(FLCons(listOf1.fhead()!!, pl).toSoO())
                })
            }
        }
    }

    val permutedFIKSet: Collection<FList<A>> by lazy {

        /*

            Permuting sets larger than, say, n=8 elements, is entangled in
            the mechanics of hashCode conflicts and key selection since it
            may require hashing _all_ possible distinct lists with n items,
            depending on how it is computed. If the hashCode is 32 bits,
            permutations of sets larger than 8 elements must, by necessity,
            be computed in a different way (e.g. other than selection by
            set membership) and cannot in any case be presented as a set
            (too many collisions).  A type disjunction (set | list) would be
            lovely, but Collection can do as a common parent class of either.
            Explanation provided in code comments with regards to execution.
            There is a hard implementation limit, at present: no permutations
            for size larger than 12 allowed.  Reason: 13! exceeds Int range.
            One may still run into OOM problems for size < 13 anyway...

        */

        val res = if (this.size < PERMUTATIONCARDLIMIT) {

            /*

                The probability that 2 out of n items will have the same hashcode if
                there are d available hashcode slots is (see also, for derivation,
                https://en.wikipedia.org/wiki/Birthday_problem#Approximations)

                                                -n(n-1)
                             P(n, d) ~ 1 - exp(---------)
                                                  2d

                A set of q elements has q! permutations; the probability of a collision
                for a 32-bit hashCode (for which d is 4,294,967,295) is therefore

                                                -(q!)((q!)-1)
                                P(q) ~ 1 - exp(----------------)
                                                 2*4294967295

                for q = 7 then that probability is ~0.003
                for q = 8 then that probability is ~0.17
                for q = 9 then that probability is ~0.9999997

                The following recursive solution computes, for a set of size s,
                all permutations of size (s-1) for each s elements.

             */

            // this is a FIKSet
            permuteRecursively() // .ffold(emptyIMList<FList<A>>()) { acc, perm -> FLCons(perm, acc) }

        } else {

            /*

                There are _many_ algorithms to compute permutations, see for
                instance Sedgewick, Robert (1977),"Permutation generation methods",
                ACM Comput. Surv., 9 (2): 137–164, doi:10.1145/356689.356692 or
                Knuth, "Art of Computer Programming", vol. 4A.  Overall they are
                all O(n!) with different coefficients (surprise...).  The iterative
                Johnson-Trotter is efficient and relatively simple even if not, at
                least in theory, the most efficient; but we are on a virtual
                machine anyway, so no point in clock cycle counting.

             */

            val aryls: ArrayList<TKVEntry<Int, A>> = ArrayList(this.toFRBTree())
            // this ends up being (_has_ to be, too many collisions for set) a FList
            jtPermutations(aryls).fold(emptyIMList()) { l, aryl ->
                FLCons(FList.ofMap(aryl) { tkv -> tkv.getv() }, l)
            }
        }

        val factorial = { n: Int -> smallFact(n) } // blows up at 13!
        check( this.isEmpty() || res.size == factorial(size))
        res
    }

    override fun fpermute(): Collection<FList<A>> = permutedFIKSet

    override fun fpopAndReminder(): Pair<A?, FIKSet<A>> {
        val pop: A? = this.fpick()
        val reminder = pop?.let { this.fdropItem(FIKSetOfOne(it)) } ?: FIKSetBody.empty
        return Pair(pop, reminder)
    }

    override fun fsize(): Int = size

    // transforming

    override fun <B : Any> fflatMap(f: (A) -> IMSet<B>): FIKSet<B> =
        FIKSetBody.of(this.toFRBTree().fflatMap { tkv -> f(tkv.getv()).toIMBTree() })

    override fun <C: Any> ffold(z: C, f: (acc: C, A) -> C): C =
        this.toFRBTree().ffoldv(z) { stub, tkv -> f(stub, tkv) }

    override fun <B : Any> fmap(f: (A) -> B): FIKSet<B> =
        FIKSetBody.of(this.toFRBTree().fmap { tkv -> f(tkv.getv()).toIAEntry() })

    override fun <B : Any> fmapToList(f: (A) -> B): FList<B> =
        this.toFRBTree().ffold(FList.emptyIMList()) { l, tkv -> FLCons(f(tkv.getv()), l) }

    override fun freduce(f: (acc: A, A) -> @UnsafeVariance A): A? =
        this.toFRBTree().freduce { acc, tkv -> f(acc.getv(), tkv.getv()).toIAEntry() }?.getv()

    // altering

    override fun fadd(item: FIKSetOfOne<@UnsafeVariance A>): FIKSet<A> {
        val aux = this.toFRBTree().finsert(item.one.toIAEntry())
        return FIKSetBody.of(aux)
    }

    //
    // =========
    //

    fun copyToFList(): FList<A> = when {
        isEmpty() -> FLNil
        else -> this.fmapToList { it }
    }

    protected abstract fun toFRBTree(): FRBTree<Int, A>

    // private

    companion object: IMSetCompanion {

        fun <A: Any> hashCode(s: FIKSet<A>) = s.hashCode()

        const val NOT_FOUND: Int = -1

        override fun<A: Any> emptyIMSet(): FIKSet<A> = FIKSetBody.empty

        override fun <A: Any> of(vararg items: A): FIKSet<A> = of(items.iterator())

        override fun <A: Any> of(items: Iterator<A>): FIKSet<A> {
            if (!items.hasNext()) return FIKSetBody.empty
            var acc: FRBTree<Int, A> = FRBTNil
            items.forEach {
                acc = rbtInsert(acc, it.toIAEntry())
            }
            return FIKSetBody.of(acc)
        }

        override fun <K, A: Any> of(items: IMBTree<K, A>): FIKSet<A> where K: Any, K: Comparable<K> = when {
            items.fempty() -> FIKSetBody.empty
            items.froot()!!.getk() is Int && items is FRBTree<K, A> -> @Suppress("UNCHECKED_CAST") FIKSetBody.of(items as FRBTree<Int, A>)
            else -> FIKSetBody.of(FRBTree.ofvi(items.preorderValues()))
        }

        override fun <A: Any> of(items: IMList<A>): FIKSet<A> =
            if (items.fempty()) FIKSetBody.empty else {
                val f: (IMBTree<Int, A>, A) -> IMBTree<Int, A> = { stub, item -> finsertIK(stub, item) }
                val aux: IMBTree<Int, A> = items.ffoldLeft(FRBTree.emptyIMBTree(), f)
                FIKSetBody.of(aux as FRBTree<Int, A>)
            }

        override fun <B, A: Any> ofMap(items: Iterator<B>, f: (B) -> A): FIKSet<A> {
            if (!items.hasNext()) return FIKSetBody.empty
            var acc: FRBTree<Int, A> = FRBTNil
            items.forEach {
                acc = rbtInsert(acc, f(it).toIAEntry())
            }
            return FIKSetBody.of(acc)
        }

        override fun <B: Any, A: Any> ofMap(items: IMList<B>, f: (B) -> A): FIKSet<A> = if (items.fempty()) FIKSetBody.empty else {
            val mapInsert: (IMBTree<Int, A>, B) -> IMBTree<Int, A> = { stub, it -> finsertIK(stub, f(it)) }
            val aux: IMBTree<Int, A> = items.ffoldLeft(FRBTree.nul(), mapInsert)
            FIKSetBody.of(aux as FRBTree<Int, A>)
        }

        override fun <B, A: Any> ofMap(items: List<B>, f: (B) -> A): FIKSet<A> = if (items.isEmpty()) FIKSetBody.empty else {
            val mapInsert: (IMBTree<Int, A>, B) -> IMBTree<Int, A> = { stub, it -> finsertIK(stub, f(it)) }
            val aux = items.fold(FRBTree.nul(), mapInsert)
            FIKSetBody.of(aux as FRBTree<Int, A>)
        }

        // ==========

        override fun <A : Any> Collection<A>.toIMSet(): IMSet<A> = when(this) {
            is FIKSet<A> -> this
            else -> of(this.iterator())
        }

        /* TODO maybe
        collapse(S): given a set of sets, return the union.[6] For example, collapse({{1}, {2, 3}}) == {1, 2, 3}. May be considered a kind of sum.
        flatten(S): given a set consisting of sets and atomic elements (elements that are not sets), returns a set whose elements are the atomic elements of the original top-level set or elements of the sets it contains. In other words, remove a level of nesting – like collapse, but allow atoms. This can be done a single time, or recursively flattening to obtain a set of only atomic elements.[7] For example, flatten({1, {2, 3}}) == {1, 2, 3}.
         */

        internal inline fun <reified A> toArray(fset: FIKSet<A>): Array<A> where A: Any, A: Comparable<A> =
            FIKSetIterator.toArray(fset.size, FIKSetIterator(fset))

        private class BreakoutException(): RuntimeException()

        private const val PERMUTATIONCARDLIMIT = 9
    }

}

class FIKSetOfOne<out A: Any> internal constructor (
    val one: A
): FIKSet<A>() {
    val body: FRBTree<Int, A> by lazy { FRBTree.ofvi(one) }
    override fun isEmpty(): Boolean = false
    override fun equals(other: Any?): Boolean = FIKSetBody.of(body).equals(other)
    val hash: Int by lazy { (131 * (this.body.inorder().hashCode() + 17)) / 127 }
    override fun hashCode(): Int = hash
    val show: String by lazy { FIKSetOfOne::class.simpleName+"($one)" }
    override fun toString(): String = show
    override val size: Int = 1
    override fun toFRBTree(): FRBTree<Int, A> = body

    companion object {
        fun <A: Any> A.toSoO() = FIKSetOfOne(this)
    }
}

internal class FIKSetBody<out A: Any> private constructor (
    val body: FRBTree<Int, A>
): FIKSet<A>() {

    override fun isEmpty(): Boolean = this === empty

    // short of type erasure, this must maintain reflexive, symmetric and transitive properties
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is IMSet<*> -> when {
            this.isEmpty() && other.fempty() -> true
            this.isEmpty() || other.fempty() -> false
            this.fpick()!!::class != other.fpick()!!::class -> false
            else -> @Suppress("UNCHECKED_CAST") IMSetEqual2(this, other as IMSet<A>)
        }
        other is Set<*> -> when {
            this.isEmpty() && other.isEmpty() -> true // type erasure boo-boo
            this.isEmpty() || other.isEmpty() -> false
            this.fpick()!!::class != other.first()!!::class -> false
            this.fsize() != other.size -> false
            else -> other.equals(this)
        }
        else -> false
    }

    val hash: Int by lazy {
        // hash of a FRBTree depends on the content AND on the shape of the tree;
        // for set hash, the shape of the tree is irrelevant, whence the following
        (131 * (this.body.inorder().hashCode() + 17)) / 127
    }

    override fun hashCode(): Int = hash

    val show: String by lazy {
        val spacerOpen = "{"
        val spacerClose = "},"
        if (this.isEmpty()) FIKSet::class.simpleName+"(EMPTY)"
        else this::class.simpleName+"("+body.inorder().ffoldLeft("") { acc, tkv -> acc + spacerOpen + tkv.getv().toString() + spacerClose }+")"
    }

    override fun toString(): String = show

    override val size: Int by lazy { body.size }

    override fun toFRBTree(): FRBTree<Int, A> = this.body

    companion object {
        internal fun <A: Any> of(b: FRBTree<Int, A>): FIKSetBody<A> = if (b.isEmpty()) empty else FIKSetBody(b)
        val empty = FIKSetBody(FRBTNil)
    }
}