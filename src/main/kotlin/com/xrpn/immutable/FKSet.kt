package com.xrpn.immutable

import com.xrpn.bridge.FKSetIterator
import com.xrpn.hash.JohnsonTrotter.jtPermutations
import com.xrpn.hash.JohnsonTrotter.smallFact
import com.xrpn.imapi.*
import com.xrpn.immutable.FList.Companion.emptyIMList
import com.xrpn.immutable.FRBTree.Companion.finsertIK
import com.xrpn.immutable.FRBTree.Companion.finsertSK
import com.xrpn.immutable.FRBTree.Companion.nul
import com.xrpn.immutable.FRBTree.Companion.rbtInsert
import com.xrpn.immutable.FRBTree.Companion.toIMBTree
import com.xrpn.immutable.TKVEntry.Companion.toIAEntry
import com.xrpn.immutable.TKVEntry.Companion.toSAEntry
import kotlin.reflect.KClass

sealed class FKSet<out K, out A: Any> constructor (protected val body: FRBTree<K, A>): Set<A>, IMSet<K, A> where K: Any, K: Comparable<@UnsafeVariance K> {

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

    // from Collection<A> when set is not empty

    override fun contains(element: @UnsafeVariance A): Boolean = this.fcontains(element)

    override fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean {
        elements.forEach { if (!this.fcontains(it)) return false }
        return true
    }

    // from Collection<A>

    override fun iterator(): Iterator<A> = FKSetIterator(this)

    // utility

    override fun equal(rhs: IMRSet<@UnsafeVariance A>): Boolean =
        IMRSetEqual2(this, rhs)

    override fun strongEqual(rhs: IMSet<@UnsafeVariance K, @UnsafeVariance A>): Boolean =
        this.equals(rhs)

    override fun fforEach (f: (A) -> Unit) = when(this) {
        is FKSetEmpty -> Unit
        else -> body.fforEach { tkv -> f(tkv.getv()) }
    }

    override fun toIMBTree(): IMBTree<K, A> = when(this) {
        is FKSetEmpty -> nul()
        else -> body
    }

    override fun copy(): FKSet<K, A> = when (this) {
        is FKSetEmpty -> this
        else -> body.ffold(nul<K, A>()) { acc, tkv -> acc.finsert(tkv) }.toIMSet(fsKeyKClass())
    }

    override fun copyToMutableSet(): MutableSet<@UnsafeVariance A> = when (this) {
        is FKSetEmpty -> mutableSetOf()
        else -> body.ffold(mutableSetOf()) { acc, tkv -> acc.add(tkv.getv()); acc }
    }

    // filtering

    override fun fcontains(item: @UnsafeVariance A): Boolean = when (this) {
        is FKSetEmpty -> false
        else -> body.fcontainsKey(toKey(item))
    }

    override fun fcontainsAny(items: IMSet<@UnsafeVariance K, @UnsafeVariance A>): Boolean = when (this) {
        is FKSetEmpty -> !this.fempty()
        else -> if (items.fempty()) true else try {
            val outer = if (this.toIMBTree().fsize() < items.toIMBTree().fsize()) this.toIMBTree() else items.toIMBTree()
            val mark = if (this.toIMBTree().fsize() < items.toIMBTree().fsize()) items.toIMBTree() else this.toIMBTree()
            // TODO gross, need to think something better...
            outer.ffold(0) { acc, tkv -> if (mark.fcontains(tkv)) throw BreakoutException() else acc + 1 }
            false
        } catch (ex: BreakoutException) {
            true
        }
    }

    override fun fdropItem(item: @UnsafeVariance A): FKSet<K, A> = when (this) {
        is FKSetEmpty -> this
        else -> body.fdropItem(toTKVEntry(item)).toIMSet(fsKeyKClass())
    }

    override fun fdropAll(items: IMSet<@UnsafeVariance K, @UnsafeVariance A>): FKSet<K, A> = when (this) {
        is FKSetEmpty -> this
        else -> body.fdropAlt(items.toIMBTree()).toIMSet(fsKeyKClass()) as FKSet<K, A>
    }

    override fun ffilter(isMatch: (A) -> Boolean): FKSet<K, A> =when (this) {
        is FKSetEmpty -> this
        else -> body.ffilter { tkv -> isMatch(tkv.getv()) }.toIMSet(fsKeyKClass())
    }

    override fun ffilterNot(isMatch: (A) -> Boolean): FKSet<K, A> {
        val notMatch: (a: A) -> Boolean = { a -> !isMatch(a) }
        return ffilter(notMatch)
    }

    override fun ffindDistinct(isMatch: (A) -> Boolean): A? =when (this) {
        is FKSetEmpty -> null
        else -> body.ffindDistinct { tkv -> isMatch(tkv.getv()) }?.getv()
    }

    override fun fisSubsetOf(rhs: IMSet<@UnsafeVariance K, @UnsafeVariance A>): Boolean {
        val superset = rhs.toIMBTree()
        val maybeSubset = this.toIMBTree()
        return this.fsize() == maybeSubset.fcount(superset::fcontains)
    }

    override fun fpick(): A? = body.froot()?.getv()

    override fun fpickKey(): K? = body.froot()?.getk()

    override fun fAND(items: IMSet<@UnsafeVariance K, @UnsafeVariance A>): FKSet<K, A> = when (this) {
        is FKSetEmpty -> this
        else -> treeWiseAND(items).toIMSet(fsKeyKClass())
    }

    override fun fOR(items: IMSet<@UnsafeVariance K, @UnsafeVariance A>): FKSet<K, A> = when (this) {
        is FKSetEmpty -> items as FKSet<K, A>
        else -> treeWiseOR(items).toIMSet(fsKeyKClass())
    }

    override fun fXOR(items: IMSet<@UnsafeVariance K, @UnsafeVariance A>): FKSet<K, A> =
        if (items.fempty() && fempty()) this // could be items...
        else if (items.fempty() && !fempty()) this
        else if (!items.fempty() && fempty()) items as FKSet<K, A>
        else {
            val bothHave: FRBTree<K, A> = treeWiseAND(items)
            val thisOnly: IMBTree<K, A> = body.fdropAlt(bothHave)
            val itemsOnly: IMBTree<K, A> = items.toIMBTree().fdropAlt(bothHave)
            val t = thisOnly.finsertt(itemsOnly) as FRBTree<K, A>
            if (t.isEmpty()) FKSetEmpty.empty() else t.toIMSet(t.frbKeyKClass())
        }

    // grouping

    override fun <B: Any> fcartesian(rhs: IMSet<@UnsafeVariance K, B>): FKSet<K, Pair<A, B>> {

        tailrec fun go(shrink: FKSet<K, A>, stay: IMSet<K, B>, acc: IMBTree<K, Pair<A, B>>): FKSet<K, Pair<A, B>> =
            if (shrink.fempty()) acc.toIMSet(fsKeyKClass()) as FKSet<K, Pair<A, B>> else {
                val (pop, reminder) = shrink.fpopAndReminder()
                val newAcc: IMBTree<K, Pair<A, B>> = pop?.let{ acc.finsertt( stay.ffold(nul()) { frb, a -> frb.finsert(toTKVEntry(Pair(it, a))) }) } ?: acc
                go(reminder, stay, newAcc)
            }

        return if (fempty()) emptyIMSet<K, Pair<A, B>>() as FKSet<K, Pair<A, B>>else go(this, rhs, nul())
    }

    override fun fcombinations(maxSize: Int): FKSet<K, FKSet<K, A>> {

        // all unique subsets up to "size" members from this set; order does not matter

        tailrec fun gogo(item: IMSet<@UnsafeVariance K, @UnsafeVariance A>, fat: IMBTree<K, FKSet<K, A>>, acc: IMBTree<K, FKSet<K, A>>): IMBTree<K, FKSet<K, A>> {
            val (pop: TKVEntry<K, FKSet<K, A>>?, reminder: IMBTree<K, FKSet<K, A>>) = fat.fpopAndReminder()
            return if (pop == null) acc else {
                if (pop.getv().size < maxSize) {
                    val aux: FKSet<K, A> = pop.getv().fOR(item)
                    val newAcc: IMBTree<K, FKSet<K, A>> = acc.finsert(toTKVEntry(aux))
                    gogo(item, reminder, newAcc)
                } else gogo(item, reminder, acc)
            }
        }

        tailrec fun go(shrink: FKSet<K, A>, acc: IMBTree<K, FKSet<K, A>>): IMBTree<K, FKSet<K, A>> {
            val (pop: A?, reminder: FKSet<K, A>) = shrink.fpopAndReminder()
            return if(pop == null) acc else {
                val newAcc: IMBTree<K, FKSet<K, A>> = pop.let {
                    val setOfIt = FRBTree.of(toTKVEntry(it)).toIMSet(fsKeyKClass())
                    val outer: IMBTree<K, FKSet<K, A>> = acc.finsert(toTKVEntry(setOfIt))
                    gogo(setOfIt, outer, outer)
                }
                go(reminder, newAcc)
            }
        }

        return if ((maxSize < 1) || fempty()) FKSetEmpty.empty()
               else go(this, nul()).toIMSet(fsKeyKClass()) as FKSet<K, FKSet<K, A>>
    }

    override fun fcount(isMatch: (A) -> Boolean): Int = when (this) {
        is FKSetEmpty -> 0
        else -> body.fcount { tkv -> isMatch(tkv.getv()) }
    }

    override fun <B> fgroupBy(f: (A) -> B): IMMap<B, FKSet<K, A>> where B: Any, B: Comparable<B> {
        TODO("Not yet implemented")
    }

    override fun findexed(offset: Int): FKSet<K, Pair<A, Int>> = when (this) {
        is FKSetEmpty -> FKSetEmpty.empty()
        else -> {
            var index = offset - 1
            val t: FRBTree<K, Pair<A, Int>> = body.fmap{ tkv ->
                    index+=1
                    toTKVEntry(Pair(tkv.getv(), index))
                }
            t.toIMSet(t.frbKeyKClass())
        }
    }

    override fun fpartition(isMatch: (A) -> Boolean): Pair<FKSet<K, A>, FKSet<K, A>> = when (this) {
        is FKSetEmpty -> Pair(FKSetEmpty.empty(), FKSetEmpty.empty())
        else -> {
            val (t: FRBTree<K, A>, f: FRBTree<K, A>) = body.fpartition { tkv -> isMatch(tkv.getv()) }
            Pair(t.toIMSet(fsKeyKClass()), f.toIMSet(fsKeyKClass()))
        }
    }

    override fun fpermutations(maxSize: Int): Collection<FList<A>> {

        // TODO consider memoization

        tailrec fun goSmall(shrink: FKSet<K, FKSet<K, A>>, acc: IMBTree<K, FList<A>>): IMBTree<K, FList<A>> = if (shrink.fempty()) acc else {
            val (pop: FKSet<K, A>?, reminder: FKSet<K, FKSet<K, A>>) = shrink.fpopAndReminder()
            val newAcc: IMBTree<K, FList<A>> = pop?.let { acc.finsertt(it.fpermute().map { p -> toTKVEntry(p) }.toIMBTree()) } ?: acc
            goSmall(reminder, newAcc)
        }

        tailrec fun goLarge(shrink: FKSet<K, FKSet<K, A>>, acc: FList<FList<A>>): FList<FList<A>> = if (shrink.fempty()) acc else {
            val (pop, reminder) = shrink.fpopAndReminder()
            val newAcc: FList<FList<A>> = if (pop == null) acc else {
                val perms = pop.fpermute() as FList<FList<A>>
                perms.ffoldLeft(acc) { pacc, l -> FLCons(l, pacc) }
            }
            goLarge(reminder, newAcc)
        }

        val res: Collection<FList<A>> = if (maxSize < 1 || this.size < maxSize) @Suppress("UNCHECKED_CAST") (emptyIMSet<K, FList<A>>() as FKSet<K, FList<A>>) else {
            val sizedCmbs: FKSet<K, FKSet<K, A>> = this.fcombinations(maxSize).ffilter { it.size == maxSize }
            if (this.size < PERMUTATIONCARDLIMIT) goSmall(sizedCmbs, nul()).toIMSet(fsKeyKClass()) as FKSet<K, FList<A>> else goLarge(sizedCmbs, emptyIMList())
        }
        
        return res
    }

    // this not stack safe for "large" sets, but it will (probably) blow op anyway for different reasons
    private fun permuteRecursively(): FKSet<K, FList<A>> = when (this.size) {
        0 -> emptyIMSet<K, FList<A>>() as FKSet<K, FList<A>>
        1 -> FRBTree.of(toTKVEntry(this.copyToFList())).toIMSet(fsKeyKClass())
        else -> {
            val allItems: FKSet<K, FList<A>> = @Suppress("UNCHECKED_CAST") (this.fpermutations(1) as FKSet<K, FList<A>>)
            allItems.ffold(emptyIMSet<K, FList<A>>() as FKSet<K, FList<A>>) { sol: FKSet<K, FList<A>>, listOf1: FList<A> ->
                sol.fOR(this.fdropItem(listOf1.fhead()!!).permuteRecursively().ffold(emptyIMSet()) { psol, pl ->
                    psol.fOR(FRBTree.of(toTKVEntry(FLCons(listOf1.fhead()!!, pl))).toIMSet(fsKeyKClass()) as FKSet<K, FList<A>>)
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
            lovely, but Collection must do as a common parent class of either.
            (Pun intended). Explanation are provided in code comments with
            regard to execution.

            There is a hard implementation limit, at present: no permutations
            for size larger than 12 allowed.  Reason: 13! exceeds Int range.
            One may still run into OOM problems for size < 13 anyway.

        */

        val res = if (this.size < PERMUTATIONCARDLIMIT) {

            /*

                The probability that 2 out of n items will have the same hashcode if
                there are d available hashcode slots is (see for example
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

            val fkset: FKSet<K, FList<A>> = permuteRecursively()
            fkset

        } else {

            /*

                There are _many_ algorithms to compute permutations, see for
                instance Sedgewick, Robert (1977),"Permutation generation methods",
                ACM Comput. Surv., 9 (2): 137–164, doi:10.1145/356689.356692 or
                Knuth, "Art of Computer Programming", vol. 4A.  Overall they are
                all O(n!) with different coefficients (DUH!).  The iterative
                Johnson-Trotter is efficient and relatively simple even if not, at
                least in theory, the most-est efficient; but we are on a virtual
                machine anyway, so no point in clock cycle counting.

             */

            val aryls: ArrayList<TKVEntry<K, A>> = ArrayList(body)
            // this ends up being (_has_ to be, too many collisions for set) a FList
            val flist: FList<FList<A>> = jtPermutations(aryls)
                .fold(emptyIMList()) { l: FList<FList<A>>, aryl: ArrayList<TKVEntry<K, A>> ->
                    FLCons(FList.ofMap(aryl) { tkv -> tkv.getv() }, l)
                }
            flist
        }

        val constrainedFactorial = { n: Int -> smallFact(n) } // blows up at 13!
        check( this.isEmpty() || res.size == constrainedFactorial(size))
        res
    }

    override fun fpermute(): Collection<FList<A>> = permutedFIKSet

    override fun fpopAndReminder(): Pair<A?, FKSet<K, A>> {
        val pop: A? = this.toIMBTree().fpick()?.getv()
        val reminder: FKSet<K, A> = pop?.let { this.fdropItem(it) } ?: emptyIMSet<K, A>() as FKSet<K, A>
        return Pair(pop, reminder)
    }

    override fun fsize(): Int = size

    // transforming

    override fun <B : Any> fflatMap(f: (A) -> IMSet<@UnsafeVariance K, B>): FKSet<K, B> = when (this) {
        is FKSetEmpty -> FKSetEmpty.empty()
        else -> body.fflatMap { tkv -> f(tkv.getv()).toIMBTree() }.toIMSet(fsKeyKClass())
    }

    override fun <C: Any> ffold(z: C, f: (acc: C, A) -> C): C = when (this) {
        is FKSetEmpty -> z
        else -> body.ffoldv(z) { stub, tkv -> f(stub, tkv) }
    }

    override fun <B : Any> fmap(f: (A) -> B): FKSet<K, B> =  when (this) {
        is FKSetEmpty -> FKSetEmpty.empty()
        else -> {
            val t = body.fmap { tkv -> toTKVEntry(f(tkv.getv())) }
            t.toIMSet(t.frbKeyKClass())
        }
    }

    override fun <B : Any> fmapToList(f: (A) -> B): FList<B> = when (this) {
        is FKSetEmpty -> emptyIMList()
        else -> body.ffold(emptyIMList()) { l, tkv -> FLCons(f(tkv.getv()), l) }
    }

    override fun freduce(f: (acc: A, A) -> @UnsafeVariance A): A? = when (this) {
        is FKSetEmpty -> null
        else -> body.freduce { acc, tkv -> toTKVEntry(f(acc.getv(), tkv.getv())) }?.getv()
    }

    //
    // ========= implementation
    //

    fun copyToFList(): FList<A> = when {
        isEmpty() -> FLNil
        else -> this.fmapToList { it }
    }

    internal fun fsKeyKClassOut(): KClass<out K> = body.frbKeyKClassOut()

    internal fun fsKeyKClass(): KClass<@UnsafeVariance K> = body.frbKeyKClass()

    protected abstract fun toFRBTree(k: KClass<@UnsafeVariance K>): FRBTree<K, A>

    protected abstract val toKey: (@UnsafeVariance A) -> K

    protected abstract fun <V: Any> toTKVEntry(v: @UnsafeVariance V): TKVEntry<K, V>

    // private

    private fun treeWiseAND(items: IMSet<@UnsafeVariance K, @UnsafeVariance A>): FRBTree<K, A> =
        if (items.fempty()) nul()
        else body.ffold(nul()) { stub, tkv -> if (items.fcontains(tkv.getv())) stub.finsert(tkv) else stub }

    private fun treeWiseOR(items: IMSet<@UnsafeVariance K, @UnsafeVariance A>): FRBTree<K, A> =
        if (items.fempty() && !fempty()) this.body else {
        val t = body.finsertt(items.toIMBTree()) as FRBTree<K, A>
        if (t.fempty()) items.toIMBTree() as FRBTree<K, A> else t
    }

    companion object: IMSetCompanion {

        internal const val unknownKeyType = "unknown set key type "

        fun <K, A: Any> hashCode(s: FKSet<K, A>) where K: Any, K: Comparable<K> = s.hashCode()

        const val NOT_FOUND: Int = -1

//        internal fun <K, A: Any> emptyIMSet(kType: KClass<out K>): IMSet<K, A> where K: Any, K: Comparable<K> = when(kType!!) {
//            Int::class -> { @Suppress("UNCHECKED_CAST") (FIKSetBody.empty<A>() as IMSet<K, A>) }
//            else -> throw RuntimeException("$unknownKeyType: $kType")
//        }

        override fun <K, A: Any> emptyIMSet(): IMSet<K, A> where K: Any, K: Comparable<K> = FKSetEmpty.empty()

        // ========== IMISet

        override fun <A: Any> ofi(vararg items: A): FKSet<Int, A> = ofi(items.iterator())

        override fun <A: Any> ofi(items: Iterator<A>): FKSet<Int, A> {
            if (!items.hasNext()) return FKSetEmpty.empty()
            var acc: FRBTree<Int, A> = nul()
            items.forEach {
                acc = rbtInsert(acc, it.toIAEntry())
            }
            return ofFIKSNotEmpty(acc as FRBTNode<Int, A>)
        }

        override fun <A: Any> ofi(items: IMBTree<Int, A>): FKSet<Int, A> = when {
            items.fempty() -> FKSetEmpty.empty()
            items is FRBTNode<Int, A> -> @Suppress("UNCHECKED_CAST") ofFIKSNotEmpty(items)
            else -> {
                val t = FRBTree.ofvi(items.preorderValues())
                ofFIKSNotEmpty(t as FRBTNode<Int, A>)
            }
        }

        override fun <A: Any> ofi(items: IMList<A>): FKSet<Int, A> =
            if (items.fempty()) FKSetEmpty.empty() else {
                val f: (FRBTree<Int, A>, A) -> FRBTree<Int, A> = { stub, item -> finsertIK(stub, item) as FRBTree<Int, A> }
                val aux: FRBTree<Int, A> = items.ffoldLeft(nul(), f)
                ofFIKSNotEmpty(aux as FRBTNode<Int, A>)
            }

        override fun <B, A: Any> ofiMap(items: Iterator<B>, f: (B) -> A): FKSet<Int, A> {
            if (!items.hasNext()) return FKSetEmpty.empty()
            var acc: FRBTree<Int, A> = nul()
            items.forEach {
                acc = rbtInsert(acc, f(it).toIAEntry())
            }
            return ofFIKSNotEmpty(acc as FRBTNode<Int, A>)
        }

        override fun <B: Any, A: Any> ofiMap(items: IMList<B>, f: (B) -> A): FKSet<Int, A> = if (items.fempty()) FKSetEmpty.empty() else {
            val mapInsert: (FRBTree<Int, A>, B) -> FRBTree<Int, A> = { stub, it -> finsertIK(stub, f(it)) as FRBTNode }
            val aux: FRBTree<Int, A> = items.ffoldLeft(nul(), mapInsert)
            ofFIKSNotEmpty(aux as FRBTNode<Int, A>)
        }

        override fun <B, A: Any> ofiMap(items: List<B>, f: (B) -> A): FKSet<Int, A> = if (items.isEmpty()) FKSetEmpty.empty() else {
            val mapInsert: (FRBTree<Int, A>, B) -> FRBTree<Int, A> = { stub, it -> finsertIK(stub, f(it)) as FRBTNode }
            val aux: FRBTree<Int, A> = items.fold(nul(), mapInsert)
            ofFIKSNotEmpty(aux as FRBTNode<Int, A>)
        }

        // ========== IMSSet

        override fun <A: Any> ofs(vararg items: A): FKSet<String, A> = ofs(items.iterator())

        override fun <A: Any> ofs(items: Iterator<A>): FKSet<String, A> {
            if (!items.hasNext()) return FKSetEmpty.empty()
            var acc: FRBTree<String, A> = nul()
            items.forEach {
                acc = rbtInsert(acc, it.toSAEntry())
            }
            return ofFSKSNotEmpty(acc as FRBTNode<String, A>)
        }

        override fun <A: Any> ofs(items: IMBTree<String, A>): FKSet<String, A> = when {
            items.fempty() -> FKSetEmpty.empty()
            items is FRBTNode<String, A> -> @Suppress("UNCHECKED_CAST") ofFSKSNotEmpty(items)
            else -> {
                val t = FRBTree.ofvs(items.preorderValues())
                ofFSKSNotEmpty(t as FRBTNode<String, A>)
            }
        }

        override fun <A: Any> ofs(items: IMList<A>): FKSet<String, A> =
            if (items.fempty()) FKSetEmpty.empty() else {
                val f: (FRBTree<String, A>, A) -> FRBTree<String, A> = { stub, item -> finsertSK(stub, item) as FRBTree<String, A> }
                val aux: FRBTree<String, A> = items.ffoldLeft(nul(), f)
                ofFSKSNotEmpty(aux as FRBTNode<String, A>)
            }

        override fun <B, A: Any> ofsMap(items: Iterator<B>, f: (B) -> A): FKSet<String, A> {
            if (!items.hasNext()) return FKSetEmpty.empty()
            var acc: FRBTree<String, A> = nul()
            items.forEach {
                acc = rbtInsert(acc, f(it).toSAEntry())
            }
            return ofFSKSNotEmpty(acc as FRBTNode<String, A>)
        }

        override fun <B: Any, A: Any> ofsMap(items: IMList<B>, f: (B) -> A): FKSet<String, A> = if (items.fempty()) FKSetEmpty.empty() else {
            val mapInsert: (FRBTree<String, A>, B) -> FRBTree<String, A> = { stub, it -> finsertSK(stub, f(it)) as FRBTNode }
            val aux: FRBTree<String, A> = items.ffoldLeft(nul(), mapInsert)
            ofFSKSNotEmpty(aux as FRBTNode<String, A>)
        }

        override fun <B, A: Any> ofsMap(items: List<B>, f: (B) -> A): FKSet<String, A> = if (items.isEmpty()) FKSetEmpty.empty() else {
            val mapInsert: (FRBTree<String, A>, B) -> FRBTree<String, A> = { stub, it -> finsertSK(stub, f(it)) as FRBTNode }
            val aux: FRBTree<String, A> = items.fold(nul(), mapInsert)
            ofFSKSNotEmpty(aux as FRBTNode<String, A>)
        }

        // ==========

        override fun <K, B : Any> toTKVEntry(s: IMSet<K, B>, v: B): TKVEntry<K, B>? where K: Any, K: Comparable<K> = if (null == s.fpickKey()) null else when (s.fpickKey()) {
            is Int -> @Suppress("UNCHECKED_CAST") ((@Suppress("UNCHECKED_CAST") (s as FKSet<Int, *>)).toTKVEntry(v) as TKVEntry<K, B>)
            is String -> @Suppress("UNCHECKED_CAST") ((@Suppress("UNCHECKED_CAST") (s as FKSet<String, *>)).toTKVEntry(v) as TKVEntry<K, B>)
            else -> throw RuntimeException("$unknownKeyType ${s.fpickKey()}")
        }

        // ==========

        override fun <K, A: Any> Collection<A>.toIMSet(kType: KClass<K>): IMSet<K, A> where K: Any, K: Comparable<K> = when(kType) {
            Int::class -> @Suppress("UNCHECKED_CAST") (this.toIMISet() as IMSet<K, A>)
            String::class -> @Suppress("UNCHECKED_CAST") (this.toIMSSet() as IMSet<K, A>)
            else -> throw RuntimeException()
        }

        override fun <A: Any> Collection<A>.toIMISet(): FKSet<Int, A> = when {
            this.isEmpty() -> FKSetEmpty.empty()
            this is FKSet<*, *> -> when {
                this.fpickKey()!! is Int -> @Suppress("UNCHECKED_CAST") (this as FKSet<Int, A>)
                else -> TODO()
            }
            this is IMBTree<*, *> -> when {
                this.froot()!!.getk() is Int -> this.toIMSet(Int::class) as FKSet<Int, A>
                else -> TODO()
            }
            else -> ofi(this.iterator())
        }

        override fun <A: Any> Collection<A>.toIMSSet(): FKSet<String, A> = when {
            this.isEmpty() -> FKSetEmpty.empty()
            this is FKSet<*, *> -> when {
                this.fpickKey()!! is String -> @Suppress("UNCHECKED_CAST") (this as FKSet<String, A>)
                else -> TODO()
            }
            this is IMBTree<*, *> -> when {
                this.froot()!!.getk() is String -> this.toIMSet(String::class) as FKSet<String, A>
                else -> TODO()
            }
            else -> ofs(this.iterator())
        }

        /* TODO maybe
        collapse(S): given a set of sets, return the union.[6] For example, collapse({{1}, {2, 3}}) == {1, 2, 3}. May be considered a kind of sum.
        flatten(S): given a set consisting of sets and atomic elements (elements that are not sets), returns a set whose elements are the atomic elements of the original top-level set or elements of the sets it contains. In other words, remove a level of nesting – like collapse, but allow atoms. This can be done a single time, or recursively flattening to obtain a set of only atomic elements.[7] For example, flatten({1, {2, 3}}) == {1, 2, 3}.
         */

        internal inline fun <reified K, reified A: Any> toArray(fset: FKSet<K, A>): Array<A> where K: Any, K: Comparable<K> =
            FKSetIterator.toArray(fset.size, FKSetIterator(fset))

        private class BreakoutException(): RuntimeException()

        private const val PERMUTATIONCARDLIMIT = 9
    }
}

internal class FKSetEmpty<K, A: Any> private constructor (
    b: FRBTree<K, A>
): FKSet<K, A>(b) where K: Any, K: Comparable<@UnsafeVariance K> {

    // Any

    override fun equals(other: Any?): Boolean = when {
        singletonEmpty === other -> true
        other == null -> false
        other is IMSet<*,*> -> other.fempty()
        other is Set<*> -> other.isEmpty()
        else -> false
    }
    val hash: Int by lazy { this::class.simpleName.hashCode() }
    override fun hashCode(): Int = hash
    val show: String by lazy { FKSet::class.simpleName + "(*)" }
    override fun toString(): String = show

    // FKSet

    override fun toFRBTree(k: KClass<K>): FRBTree<K, A> = nul()
    override val toKey: (A) -> K
        get() = TODO(msg)
    override fun <V : Any> toTKVEntry(v: V): TKVEntry<K, V> = TODO(msg)

    // Collections

    override fun isEmpty(): Boolean = true
    override val size = 0
    override fun contains(element: @UnsafeVariance A): Boolean = false
    override fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean = elements.isEmpty()

    companion object {
        const val msg = "never to be implemented (impossible path)"
        private val singletonEmpty = FKSetEmpty(FRBTNil)
        internal fun <K, A: Any> empty(): FKSet<K, A> where K: Any, K: Comparable<@UnsafeVariance K> = singletonEmpty
    }
}

// =====================================================================================================================
// FIKSet implementation
// =====================================================================================================================

typealias FIKSet<A> = FKSet<Int, A>

private class FIKSetNotEmpty<out A: Any> private constructor (
    b: FRBTNode<Int, @UnsafeVariance A>
): FKSet<Int, A>(b), IMSetNotEmpty<Int, A> {
    override fun isEmpty(): Boolean = false
    // short of type erasure, this must maintain reflexive, symmetric and transitive properties
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is IMSet<*,*> -> when {
            other.fempty() -> false
            this.fpick()!!::class != other.fpick()!!::class -> false
            other.fpickKey()!! !is Int -> false
            else -> @Suppress("UNCHECKED_CAST") IMSetEqual2(this, other as IMSet<Int, A>)
        }
        other is Set<*> -> when {
            other.isEmpty() -> false // type erasure boo-boo
            fsize() != other.size -> false
            this.fpick()!!::class != other.first()!!::class -> false
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
        val cn: String = this::class.simpleName!!.replace("Body","", ignoreCase = true).replace("NotEmpty","", ignoreCase = true)
        "$cn(${body.inorder().ffoldLeft("") { acc, tkv -> acc + spacerOpen + tkv.getv().toString() + spacerClose }.dropLast(1)})"
    }
    override fun toString(): String = show
    override val size: Int by lazy { body.size }
    override fun faddItem(item: @UnsafeVariance A) =
        @Suppress("UNCHECKED_CAST") (body.finsert(toTKVEntry(this, item)!!).toIMSet(body.frbKeyKClass()) as IMSetNotEmpty<Int, A>)
    override fun toFRBTree(k: KClass<Int>): FRBTNode<Int, @UnsafeVariance A> = body as FRBTNode<Int, A>
    override val toKey: (v: @UnsafeVariance A) -> Int
        get() = { v -> TKVEntry.intKeyOf(v) }
    override fun <V : Any> toTKVEntry(v: V) = TKVEntry.ofIntKey(v)
    companion object {
        internal fun <A: Any> of(b: FRBTNode<Int, A>): FKSet<Int, A> = FIKSetNotEmpty(b)
    }
}

// fun <A : Any> A.toISoO(): IMSetOfOne<Int, A> = FIKSetOfOne.toISoO(this)

internal fun <A: Any> ofFIKSBody(b: FRBTree<Int, A>): FKSet<Int, A> = when(b) {
    is FRBTNode -> FIKSetNotEmpty.of(b)
    else -> FKSetEmpty.empty()
}

internal fun <A: Any> ofFIKSNotEmpty(b: FRBTNode<Int, A>): FKSet<Int, A> = FIKSetNotEmpty.of(b)

// =====================================================================================================================
// FSKSet implementation
// =====================================================================================================================

typealias FSKSet<A> = FKSet<String, A>

private class FSKSetNotEmpty<out A: Any> private constructor (
    b: FRBTNode<String, @UnsafeVariance A>
): FKSet<String, A>(b), IMSetNotEmpty<String, A> {
    override fun isEmpty(): Boolean = false
    // short of type erasure, this must maintain reflexive, symmetric and transitive properties
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other == null -> false
        other is IMSet<*,*> -> when {
            other.fempty() -> false
            this.fpick()!!::class != other.fpick()!!::class -> false
            other.fpickKey()!! !is String -> false
            else -> @Suppress("UNCHECKED_CAST") IMSetEqual2(this, other as IMSet<String, A>)
        }
        other is Set<*> -> when {
            other.isEmpty() -> false // type erasure boo-boo
            fsize() != other.size -> false
            this.fpick()!!::class != other.first()!!::class -> false
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
        val cn: String = this::class.simpleName!!.replace("NotEmpty","", ignoreCase = true)
        "$cn(${body.inorder().ffoldLeft("") { acc, tkv -> acc + spacerOpen + tkv.getv().toString() + spacerClose }.dropLast(1)})"
    }
    override fun toString(): String = show
    override val size: Int by lazy { body.size }
    override fun faddItem(item: @UnsafeVariance A): IMSetNotEmpty<String, A> =
        @Suppress("UNCHECKED_CAST") (body.finsert(toTKVEntry(this, item)!!).toIMSet(body.frbKeyKClass()) as IMSetNotEmpty<String, A>)
    override fun toFRBTree(k: KClass<String>): FRBTNode<String, @UnsafeVariance A> = body as FRBTNode<String, A>
    override val toKey: (v: @UnsafeVariance A) -> String
        get() = { v -> TKVEntry.strKeyOf(v) }
    override fun <V : Any> toTKVEntry(v: V) = TKVEntry.ofStrKey(v)
    companion object {
        internal fun <A: Any> of(b: FRBTNode<String, A>): FKSet<String, A> = FSKSetNotEmpty(b)
    }
}

// fun <A : Any> A.toSSoO(): IMSetOfOne<String, A> = FSKSetOfOne.toISoO(this)

internal fun <A: Any> ofFSKSBody(b: FRBTree<String, A>): FKSet<String, A> = when(b) {
    is FRBTNode -> FSKSetNotEmpty.of(b)
    else -> FKSetEmpty.empty()
}

internal fun <A: Any> ofFSKSNotEmpty(b: FRBTNode<String, A>): FKSet<String, A> = FSKSetNotEmpty.of(b)

