package com.xrpn.imapi

import com.xrpn.immutable.*
import com.xrpn.immutable.FT
import java.io.OutputStream
import java.io.PrintStream
import java.util.*
import kotlin.reflect.KClass

enum class IMSC {
    IMLIST,
    IMSET,
    IMMAP,
    IMTREE,
    IMSTACK,
    IMQUEUE,
    IMENTRY,
    IMTSDJ,
    IMDIW,
    IMDMW,
    IMDAW,
    IMKART
}

interface IMUniversal {
    fun errLog(dest: OutputStream = System.err, emitter: Any? = null): IMLogging = object : IMLogging {
        override val logStream: PrintStream by lazy { PrintStream(dest) }
        override val emitterClass: String by lazy { emitter?.let { "${it::class}" } ?: "${this::class}" }
        override val emitterString: String? by lazy { emitter?.let { "$it" } }
//        {"${this::class.takeIf {
//            !it.isCompanion
//        } ?: this.javaClass.enclosingClass.kotlin}" }
    }
    fun reportException(ex: Exception, item: Any? = null, emitter: Any? = null, dest: OutputStream = System.err) {
        val aux = errLog(dest, emitter)
        val itemMsg = item?.let { " at ${it::class} as $it" }
        val msg = "failure$itemMsg"
        aux.emitUnconditionally(msg)
        aux.emitUnconditionally(ex)
    }
}

fun <T: Any> IMUniversal.asIMCommon(): IMCommon<T>? =
    @Suppress("UNCHECKED_CAST") (this as? IMCommon<T>) ?: run {
        val aux = this.errLog()
        aux.emitUnconditionally("is-not-a ${IMCommon::class.simpleName}")
        null
    }

fun <K, T: Any> IMUniversal.asIMKCommon(): IMCommon<TKVEntry<K,T>>? where K: Any, K: Comparable<K> =
    @Suppress("UNCHECKED_CAST") (this as? IMCommon<TKVEntry<K,T>>) ?: run {
        val aux = this.errLog()
        aux.emitUnconditionally("is-not-a ${IMCommon::class.simpleName}")
        null
    }

interface IMSealed<out A:IMUniversal>: IMUniversal {
    val seal: IMSC
    fun softEqual(rhs: Any?): Boolean
}

interface IMReducible<out A: Any>: IMUniversal {
    fun freduce(f: (acc: A, A) -> @UnsafeVariance A): A?
}

// One or more A
interface IMCommon<out A: Any>: IMSealed<IMUniversal> {
    fun fall(predicate: (A) -> Boolean): Boolean = fempty() || run {
        val negated: (A) -> Boolean = { a: A -> ! predicate(a) }
        return ffindAny(negated) == null
    }
    fun fany(predicate: (A) -> Boolean): Boolean = fempty() || ffindAny(predicate) != null
    fun fcontains(item: @UnsafeVariance A?): Boolean
    fun fcount(isMatch: (A) -> Boolean): Int // count the element that match the predicate
    fun fdropAll(items: IMCommon<@UnsafeVariance A>): IMCommon<A>
    fun fdropItem(item: @UnsafeVariance A): IMCommon<A>
    fun fdropWhen(isMatch: (A) -> Boolean): IMCommon<A>
    fun fempty(): Boolean = fpick() == null
    fun ffilter(isMatch: (A) -> Boolean): IMCommon<A> // return all elements that match the predicate p
    fun ffilterNot(isMatch: (A) -> Boolean): IMCommon<A> // Return all elements that do not match the predicate p
    fun ffindAny(isMatch: (A) -> Boolean): A? // Return some element, if any, that matches the predicate p
    // “Fold” element(s) using the binary operator f and using the initial seed z
    // The order of traversal is deterministic only if ordering is an intrinsic
    // property; if so, the traversal is from first to last.  Else, the order
    // of traversal is undetermined.  In the latter case, f MUST be commutative
    // or the outcome will also be undetermined and not necessarily repeatable.
    fun <R> ffold(z: R, f: (acc: R, A) -> R): R
    fun fisNested(): Boolean? = if (fempty()) null else FT.isContainer(fpick()!!)
    fun fisStrict(): Boolean
    fun fnone(predicate: (A) -> Boolean): Boolean = fempty() || ffindAny(predicate) == null
    fun fpick(): A? // peek at one random, easy-to-get i.e. cheap to get element
    fun fpickNotEmpty(): A? = when {
        this.fempty() -> null
        !FT.isContainer(fpick()!!) -> fpick()
        else -> ffindAny {
            it.toUCon()?.let { uc -> !(uc.isEmpty()) } ?:
            throw RuntimeException("internal error, $it:[${it::class}] unknown nested")
        }
    }
    fun fpopAndRemainder(): Pair<A?, IMCommon<A>>
    fun fsize(): Int
    fun toEmpty(): IMCommon<A>

    companion object {

        // O(n^2)
        internal fun <A: Any> containmentEquals(lhs: IMCommon<A>, rhs: Iterable<A>): Boolean {
            val rhsIter = rhs.iterator()
            val rhsEmpty = ! rhsIter.hasNext()
            return when {
                lhs.fempty() -> rhsEmpty
                rhsEmpty -> false
                !(lhs.fpick()!!.isStrictly(rhs.first())) -> false
                else -> {
                    var match = false
                    for(item in rhs) {
                        match = lhs.fcontains(item)
                        if (!match) break
                    }
                    match
                }
            }
        }

        // O(n^2)
        internal fun <A: Any> containmentEquals(lhs: IMCommon<A>, rhs: IMCommon<A>): Boolean = lhs.fall { lIt: A ->
            rhs.fany { rIt: A ->
                val eq = lIt.equals(rIt)
                eq
            }
        }

        fun <T: Any> equal(lhs: IMCommon<T>, rhs: IMCommon<T>): Boolean = lhs === rhs || (lhs.fempty() && rhs.fempty()) || when {
            lhs.fempty() || rhs.fempty() -> false
            lhs.fsize() != rhs.fsize() -> false
            lhs.isStrictly(rhs) -> /* TODO consider taint from mutable content */ lhs.fisStrict() && rhs.fisStrict() && lhs.equals(rhs)
            else -> containmentEquals(lhs, rhs)
        }

        fun <T: Any> softEqual(lhs: IMCommon<T>, rhs: Any?): Boolean = lhs.equals(rhs) || when (rhs) {
            is IMCommon<*> ->(@Suppress("UNCHECKED_CAST") (rhs as? IMCommon<T>))?.let { equal(lhs, it) } ?: false
            is Iterable<*> -> {
                val rhsIter = rhs.iterator()
                val rhsEmpty = ! rhsIter.hasNext()
                when {
                    lhs.fempty() -> rhsEmpty
                    rhsEmpty -> false
                    lhs.fpick()!!.isStrictlyNot(rhs.first()!!) -> false
                    else -> when (rhs) {
                        is Collection<*> -> if(lhs.fsize() != rhs.size) false else {
                            (@Suppress("UNCHECKED_CAST") (rhs as? Collection<T>))?.let {
                                containmentEquals(lhs, it)
                            } ?: false
                        }
                        else -> (@Suppress("UNCHECKED_CAST") (rhs as? Iterable<T>))?.let {
                            containmentEquals(lhs, it)
                        } ?: false
                    }
                }
            }
            else -> false
        }

//        internal open class IMCommonEquality: EqualsProxy, HashCodeProxy {
//            override fun equals(other: Any?): Boolean = other?.let { when(it) {
//                is IMCommon<*> -> equal(it)
//                is Collection<*> -> it.isEmpty()
//                else -> false
//            }} ?: false
//            override fun hashCode(): Int = javaClass.hashCode()
//        }

    }
}

interface IMCommonEmpty<out A: Any>: IMCommon<A> {
    override fun fcount(isMatch: (A) -> Boolean): Int = 0
    override fun fcontains(item: @UnsafeVariance A?): Boolean = false
    override fun fdropAll(items: IMCommon<@UnsafeVariance A>): IMCommon<@UnsafeVariance A> = this
    override fun fdropItem(item: @UnsafeVariance A): IMCommon<@UnsafeVariance A> = this
    override fun fdropWhen(isMatch: (A) -> Boolean): IMCommon<A> = this
    override fun ffilter(isMatch: (A) -> Boolean): IMCommon<A> = this
    override fun ffilterNot(isMatch: (A) -> Boolean): IMCommon<A> = this
    override fun ffindAny(isMatch: (A) -> Boolean): A? = null
    override fun <R> ffold(z: R, f: (acc: R, A) -> R): R = z
    override fun fisStrict(): Boolean = true
    override fun fpick(): Nothing? = null
    override fun fpopAndRemainder(): Pair<A?, IMCommon<A>> = Pair(null, this)
    override fun fsize(): Int = 0
    override fun toEmpty(): IMCommon<A> = this
    override fun softEqual(rhs: Any?): Boolean = this === rhs || when (rhs) {
        is IMCommon<*> -> equal(rhs)
        is Collection<*> -> rhs.isEmpty()
        else -> false
    }

    companion object {
        fun equal(other: IMCommon<*>): Boolean = other.fempty()
        fun softEqual(other: Any?): Boolean = other?.let { when(it) {
            is IMCommon<*> -> it.fempty()
            is Iterable<*> -> ! it.iterator().hasNext()
            is Map<*,*> -> it.isEmpty()
            else -> false
        }} ?: false
        internal open class IMCommonEmptyEquality: EqualsProxy, HashCodeProxy {
            override fun equals(other: Any?): Boolean = other?.let { when(it) {
                is IMCommon<*> -> it.fempty()
                else -> false
            }} ?: false
            override fun hashCode(): Int = javaClass.hashCode()
        }
    }
}

interface IMKeyed<out K>: IMUniversal where K: Any, K: Comparable<@UnsafeVariance K> {
    fun fcontainsKey(key: @UnsafeVariance K): Boolean
    fun fcountKey(isMatch: (K) -> Boolean): Int // count the values that match the predicate
    fun fdropKeys(keys: IMSet<@UnsafeVariance K>): IMKeyed<K>
    fun ffilterKey(isMatch: (K) -> Boolean): IMKeyed<K>
    fun ffilterKeyNot(isMatch: (K) -> Boolean): IMKeyed<K>
    fun fpickKey(): K?  // peekk at one random key
    // fun fisStrictlyKeyed(rhs: IMKeyed<@UnsafeVariance K>?): Boolean?
    fun fisStrictlyKeyed(rhs: IMKeyed<@UnsafeVariance K>?): Boolean? = rhs?.let { it.fpickKey().isStrictly(fpickKey()) }
    fun fisStrictlyLike(sample: KeyedTypeSample<KClass<Any>?,KClass<Any>>?): Boolean?
}

interface IMKeyedValueInvariant<K, A: Any>:
    IMKeyedValueAltering<K,A>,
    IMKeyedValueLogic<K,A>
        where K: Any, K: Comparable<@UnsafeVariance K>

interface IMKeyedValue<out K, out A: Any>: IMKeyed<K> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun asIMBTree(): IMBTree<K,A>
    fun asIMMap(): IMMap<K,A>
    fun fcontainsValue(value: @UnsafeVariance A): Boolean
    fun fcountValue(isMatch: (A) -> Boolean): Int // count the values that match the predicate
    fun ffilterValue(isMatch: (A) -> Boolean): IMKeyedValue<K,A>?
    fun ffilterValueNot(isMatch: (A) -> Boolean): IMKeyedValue<K,A>?
    fun ffindAnyValue(isMatch: (A) -> Boolean): A?
    fun fget(key: @UnsafeVariance K): A?
    fun fgetOrElse(key: @UnsafeVariance K, default: () -> @UnsafeVariance A): A = fget(key) ?: default()
    fun ftypeSample(): KeyedTypeSample<KClass<Any>?,KClass<Any>>? = fpickValue()?.let { value ->
        (@Suppress("UNCHECKED_CAST") (KeyedTypeSample(fpickKey()!!::class, value::class) as? KeyedTypeSample<KClass<Any>?,KClass<Any>>))
    }
    fun fpickValue(): A?  // peek at one random value
    fun <KK, AA: Any> tibKv(): IMKeyedValueInvariant<KK,AA>? where KK: Any, KK: Comparable<@UnsafeVariance KK> = TODO()

    companion object {

        internal fun <K, A: Any> typeInvariantBuilder(): IMKeyedValueInvariant<K,A> where K: Any, K:Comparable<K> = object : IMKeyedValueInvariant<K,A> {

            override fun fadd(src: TKVEntry<K, A>, dest: IMKeyedValue<K, A>): IMKeyedValue<K, A> = when (dest) {
                is FBSTree<K, A> -> dest.finsertTkv(src)
                is FRBTree<K, A> -> dest.finsertTkv(src)
                is FKMap<K, A> -> dest.fadd(src)
                is FKSet<K, A> -> dest.faddUniqTkv(src)
                else -> throw RuntimeException("internal error, unknown ${IMKeyedValue::class.simpleName}: ${dest::class.simpleName ?: dest::class}")
            }

            override fun fAND(src: IMKeyedValue<K, A>, origin: IMKeyedValue<K, A>): IMKeyedValue<K, A> = when (origin) {
                is FBSTree<K, A> -> origin.fAND(src)
                is FRBTree<K, A> -> origin.fAND(src)
                is FKMap<K, A> -> origin.fAND(src)
                is FKSet<K, A> -> origin.fAND(src)
                else -> throw RuntimeException("internal error, unknown ${IMKeyedValue::class.simpleName}: ${origin::class.simpleName ?: origin::class}")
            }

            override fun fNOT(src: IMKeyedValue<K, A>, origin: IMKeyedValue<K, A>): IMKeyedValue<K, A> = when (origin) {
                is FBSTree<K, A> -> origin.fNOT(src)
                is FRBTree<K, A> -> origin.fNOT(src)
                is FKMap<K, A> -> origin.fNOT(src)
                is FKSet<K, A> -> origin.fNOT(src)
                else -> throw RuntimeException("internal error, unknown ${IMKeyedValue::class.simpleName}: ${origin::class.simpleName ?: origin::class}")
            }

            override fun fOR(src: IMKeyedValue<K, A>, origin: IMKeyedValue<K, A>): IMKeyedValue<K, A> = when (origin) {
                is FBSTree<K, A> -> origin.fOR(src)
                is FRBTree<K, A> -> origin.fOR(src)
                is FKMap<K, A> -> origin.fOR(src)
                is FKSet<K, A> -> origin.fOR(src)
                else -> throw RuntimeException("internal error, unknown ${IMKeyedValue::class.simpleName}: ${origin::class.simpleName ?: origin::class}")
            }

            override fun fXOR(src: IMKeyedValue<K, A>, origin: IMKeyedValue<K, A>): IMKeyedValue<K, A> = when (origin) {
                is FBSTree<K, A> -> origin.fXOR(src)
                is FRBTree<K, A> -> origin.fXOR(src)
                is FKMap<K, A> -> origin.fXOR(src)
                is FKSet<K, A> -> origin.fXOR(src)
                else -> throw RuntimeException("internal error, unknown ${IMKeyedValue::class.simpleName}: ${origin::class.simpleName ?: origin::class}")
            }
        }
    }
}

interface IMOrderedInvariant<A: Any>: IMOrderedAltering<A>, IMOrderedEquality<A>

interface IMOrdered<out A: Any>: IMCommon<A> {
    fun fdrop(n: Int): IMOrdered<A> // Return all elements after the first n elements
    fun fnext(): A?
    fun freverse(): IMOrdered<A>
    fun frotl(): IMOrdered<A> // rotate left (A, B, C).frotl() becomes (B, C, A)
    fun frotr(): IMOrdered<A> // rotate right (A, B, C).frotr() becomes (C, A, B)
    fun fswaph(): IMOrdered<A> // swap head  (A, B, C).fswaph() becomes (B, A, C)
    fun <B: Any> fzip(items: IMOrdered<B>): IMOrdered<Pair<A,B>>
    fun <B: Any> tibOrdered(): IMOrderedInvariant<B>? = TODO()
    // return value retyped
    override fun fdropAll(items: IMCommon<@UnsafeVariance A>): IMOrdered<A>
    override fun fdropItem(item: @UnsafeVariance A): IMOrdered<A>
    override fun fdropWhen(isMatch: (A) -> Boolean): IMOrdered<A>
    override fun fpopAndRemainder(): Pair<A?, IMOrdered<A>>
    override fun ffilter(isMatch: (A) -> Boolean): IMOrdered<A> // return all elements that match the predicate p
    override fun ffilterNot(isMatch: (A) -> Boolean): IMOrdered<A> // Return all elements that do not match the predicate p

    companion object {

        internal fun <A: Any> typeInvariantBuilder(): IMOrderedInvariant<A> = object : IMOrderedInvariant<A> {

            override fun fadd(src: A, dest: IMOrdered<A>): IMOrdered<A> = when (dest) {
                is FList<A> -> dest.fprepend(src)
                is FQueue<A> -> dest.fenqueue(src)
                is FStack<A> -> dest.fpush(src)
                else -> throw RuntimeException("internal error, unknown ${IMOrdered::class.simpleName}: ${dest::class.simpleName ?: dest::class}")
            }

            // O(n)
            private tailrec fun <A : Any> pairwiseEquals(lhs: IMOrdered<A>, rhs: IMOrdered<A>): Boolean = when {
                lhs.fempty() -> rhs.fempty()
                rhs.fempty() -> false
                !(lhs.fnext()!!.equals(rhs.fnext())) -> false
                else -> pairwiseEquals(lhs.fdrop(1), rhs.fdrop(1))
            }

            // O(n)
            private fun <A : Any> pairwiseEquals(lhs: IMOrdered<A>, rhs: Iterable<A>): Boolean {
                val rhsIter = rhs.iterator()
                val rhsEmpty = !rhsIter.hasNext()
                return when {
                    lhs.fempty() -> rhsEmpty
                    rhsEmpty -> false
                    !(lhs.fnext()!!.equals(rhs.first())) -> false
                    else -> {
                        tailrec fun go(l: IMOrdered<A>, r: A): Boolean {
                            val lItem = l.fnext()
                            val lNext = l.fdrop(1)
                            return when {
                                lItem == null -> !rhsIter.hasNext()
                                !lItem.equals(r) -> false
                                (!rhsIter.hasNext()) && lNext.fempty() -> true
                                else -> go(lNext, rhsIter.next())
                            }
                        }
                        go(lhs, rhsIter.next())
                    }
                }
            }

            private fun untypedScreen(lhs: IMOrdered<*>, rhs: Collection<*>?): Boolean = rhs?.let {
                when {
                    lhs.fempty() -> rhs.isEmpty()
                    lhs.fsize() != rhs.size -> false
                    lhs.fpick()!!.isStrictlyNot(rhs.first()!!) -> false
                    else -> true
                }
            } ?: false

            private fun <A : Any> fullScreen(lhs: IMOrdered<A>, rhs: Collection<*>?): Boolean =
                untypedScreen(lhs, rhs) && (
                        (@Suppress("UNCHECKED_CAST") (rhs as? Iterable<A>))?.let {
                            pairwiseEquals(lhs, it)
                        } ?: false
                        )

            override fun equal(lhs: IMOrdered<A>, rhs: IMOrdered<A>): Boolean = when {
                lhs.fempty() -> rhs.fempty()
                rhs.fempty() -> false
                lhs.fsize() != rhs.fsize() -> false
                else -> pairwiseEquals(lhs, rhs)
            }

            override fun softEqual(lhs: IMOrdered<A>, rhs: Any?): Boolean = lhs.equals(rhs) || when (rhs) {
                null -> false
                is IMOrdered<*> -> (@Suppress("UNCHECKED_CAST") (rhs as? IMOrdered<A>)?.let {
                    equal(lhs, it)
                } ?: false)
                is LinkedHashSet<*>, is SortedSet<*> -> {
                    rhs as Collection<*>
                    fullScreen(lhs, rhs)
                }
                is Set<*> -> false /* not ordered */
                is Array<*> -> when {
                    lhs.fempty() -> 0 == rhs.size
                    lhs.fsize() != rhs.size -> false
                    lhs.fpick()!!.isStrictlyNot(rhs[0]) -> false
                    else -> (@Suppress("UNCHECKED_CAST") (rhs as? Iterable<A>))?.let {
                        pairwiseEquals(lhs, it)
                    } ?: false
                }
                is Queue<*> -> fullScreen(lhs, rhs)
                is List<*> -> fullScreen(lhs, rhs)
                // is IMCommon<*> -> IMCommon.softEqual(lhs, rhs)
                else -> false
            }

            override fun unorderedEqual(lhs: IMOrdered<A>, rhs: IMOrdered<A>): Boolean =
                lhs === rhs || (lhs.fempty() && rhs.fempty()) || lhs.equals(rhs) || when {
                    lhs.fempty() || rhs.fempty() -> false
                    lhs.fsize() != rhs.fsize() -> false
                    else -> IMCommon.containmentEquals(lhs, rhs)
                }

            override fun unorderedEqual(lhs: IMOrdered<A>, rhs: Iterable<A>): Boolean {
                val rhsIter = rhs.iterator()
                val rhsEmpty = !rhsIter.hasNext()
                return when {
                    lhs.fempty() -> rhsEmpty
                    rhsEmpty -> false
                    lhs.fpick()!!.isStrictlyNot(rhs.first()) -> false
                    else -> when (rhs) {
                        is Collection<*> -> if (lhs.fsize() != rhs.size) false else {
                            (@Suppress("UNCHECKED_CAST") (rhs as? Collection<A>))?.let {
                                IMCommon.containmentEquals(lhs, it)
                            } ?: false
                        }
                        else -> IMCommon.containmentEquals(lhs, rhs)
                    }
                }
            }

            fun executor(): IMOrderedInvariant<A> = this
        }
    }
}

interface IMOrderedEmpty<out A: Any>: IMCommonEmpty<A>, IMOrdered<A> {
    override fun fdrop(n: Int): IMOrdered<A> = this
    override fun fnext(): A? = null
    override fun freverse(): IMOrdered<A> = this
    override fun frotl(): IMOrdered<A> = this
    override fun frotr(): IMOrdered<A> = this
    override fun fswaph(): IMOrdered<A> = this
    override fun fdropAll(items: IMCommon<@UnsafeVariance A>): IMOrdered<A> = this
    override fun fdropItem(item: @UnsafeVariance A): IMOrdered<A> = this
    override fun fdropWhen(isMatch: (A) -> Boolean): IMOrdered<A> = this
    override fun fpopAndRemainder(): Pair<A?, IMOrdered<A>> = Pair(null, this)
    override fun ffilter(isMatch: (A) -> Boolean): IMOrdered<A> = this // return all elements that match the predicate p
    override fun ffilterNot(isMatch: (A) -> Boolean): IMOrdered<A> = this // Return all elements that do not match the predicate p
}

