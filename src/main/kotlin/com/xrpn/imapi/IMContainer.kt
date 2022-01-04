package com.xrpn.imapi

import com.xrpn.bridge.FTreeIterator
import com.xrpn.imapi.FCartesian.Companion.emptyZipMap
import com.xrpn.imapi.FCartesian.Companion.reportException
import com.xrpn.immutable.*
import com.xrpn.immutable.FT
import com.xrpn.immutable.toUCon
import java.io.OutputStream
import java.io.PrintStream
import java.util.*
import kotlin.collections.LinkedHashSet
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

interface IMUniversalCommon {
    fun errLog(dest: OutputStream = System.err): IMLogging = object : IMLogging {
        override val logStream: PrintStream by lazy { PrintStream(dest) }
        override val classId: String by lazy { "${this::class}" }
//        {"${this::class.takeIf {
//            !it.isCompanion
//        } ?: this.javaClass.enclosingClass.kotlin}" }
    }
    fun reportException(ex: Exception, item: Any? = null, dest: OutputStream = System.err) {
        val aux = errLog(dest)
        val itemMsg = item?.let { " for ${it::class} as $it" }
        val msg = "failure in ${aux.classId}$itemMsg"
        aux.emitUnconditionally(msg)
        aux.emitUnconditionally(ex)
    }
}

fun <T: Any> IMUniversalCommon.asIMCommon(): IMCommon<T>? = @Suppress("UNCHECKED_CAST") (this as? IMCommon<T>) ?: run {
        val aux = this.errLog()
        aux.emitUnconditionally("is-not-a ${IMCommon::class.simpleName}")
        null
    }

interface IMSealed<out A:IMUniversalCommon>: IMUniversalCommon {
    val seal: IMSC
    fun softEqual(rhs: Any?): Boolean
}

interface IMReducible<out A: Any>: IMUniversalCommon {
    fun freduce(f: (acc: A, A) -> @UnsafeVariance A): A?
}

// One or more A
interface IMCommon<out A: Any>: IMSealed<IMUniversalCommon> {
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
        private fun <A: Any> cointainmentEquals(lhs: IMCommon<A>, rhs: Iterable<A>): Boolean {
            val rhsIter = rhs.iterator()
            val rhsEmpty = ! rhsIter.hasNext()
            return when {
                lhs.fempty() -> rhsEmpty
                rhsEmpty -> false
                !(lhs.fpick()!!.equals(rhs.first())) -> false
                else -> {
                    var match: Boolean = false
                    for(item in rhs) {
                        match = lhs.fcontains(item)
                        if (!match) break
                    }
                    match
                }
            }
        }

        fun <T: Any> equal(lhs: IMCommon<T>, rhs: IMCommon<T>): Boolean = lhs === rhs || lhs.equals(rhs) || when {
            lhs === rhs -> true
            lhs.fempty() -> rhs.fempty()
            rhs.fempty() -> false
            lhs.fsize() != rhs.fsize() -> false
            lhs.isStrictly(rhs) -> /* TODO consider taint from mutable content */ lhs.fisStrict() && rhs.fisStrict() && lhs.equals(rhs)
            else -> ! lhs.fany { lIt: T -> rhs.fany { rIt: T -> ! lIt.equals(rIt)  } }
        }

        fun <T: Any> softEqual(lhs: IMCommon<T>, rhs: Any?): Boolean = lhs === rhs || lhs.equals(rhs) || when (rhs) {
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
                                cointainmentEquals(lhs, it)
                            } ?: false
                        }
                        else -> (@Suppress("UNCHECKED_CAST") (rhs as? Iterable<T>))?.let {
                            cointainmentEquals(lhs, it)
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

interface IMKeyed<out K>: IMUniversalCommon where K: Any, K: Comparable<@UnsafeVariance K> {
    fun fcontainsKey(key: @UnsafeVariance K): Boolean
    fun fcountKey(isMatch: (K) -> Boolean): Int // count the values that match the predicate
    fun fdropKeys(keys: IMSet<@UnsafeVariance K>): IMKeyed<K>
    fun ffilterKey(isMatch: (K) -> Boolean): IMKeyed<K>
    fun ffilterKeyNot(isMatch: (K) -> Boolean): IMKeyed<K>
    fun fpickKey(): K?  // peekk at one random key
    fun fisStrictlyLike(sample: KeyedTypeSample<KClass<Any>?,KClass<Any>>?): Boolean?
}

interface IMKeyedValue<out K, out A: Any>: IMKeyed<K> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun asIMBTree(): IMBTree<K,A>
    fun asIMMap(): IMMap<K,A>
    fun fcontainsValue(value: @UnsafeVariance A): Boolean
    fun fcountValue(isMatch: (A) -> Boolean): Int // count the values that match the predicate
    fun ffilterValue(isMatch: (A) -> Boolean): IMKeyedValue<K,A>
    fun ffilterValueNot(isMatch: (A) -> Boolean): IMKeyedValue<K,A>
    fun ffindAnyValue(isMatch: (A) -> Boolean): A?
    fun fget(key: @UnsafeVariance K): A?
    fun fgetOrElse(key: @UnsafeVariance K, default: () -> @UnsafeVariance A): A = fget(key) ?: default()
    fun ftypeSample(): KeyedTypeSample<KClass<Any>?,KClass<Any>>? = fpickValue()?.let { value ->
        (@Suppress("UNCHECKED_CAST") (KeyedTypeSample(fpickKey()!!::class, value::class) as? KeyedTypeSample<KClass<Any>?,KClass<Any>>))
    }
    fun fpickValue(): A?  // peek at one random value

    fun fAND(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): IMKeyedValue<K, A>
    fun fNOT(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): IMKeyedValue<K, A>
    fun fOR(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): IMKeyedValue<K, A>
    fun fXOR(items: IMKeyedValue<@UnsafeVariance K, @UnsafeVariance A>): IMKeyedValue<K, A>

    companion object: IMKeyedValueAltering {

        override fun <K, B : Any> fadd(src: TKVEntry<K, B>, dest: IMKeyedValue<K, B>): IMKeyedValue<K, B>? where K: Any, K:Comparable<K> = when (dest) {
            is FBSTree<K,B> -> TODO()
            is FRBTree<K,B> -> TODO()
            is FKMap<K,B> -> TODO()
            is FKSet<K,B> -> TODO()
            else -> throw RuntimeException("internal error, unknown ${IMKeyedValue::class.simpleName}: ${dest::class.simpleName ?: dest::class}")
        }
    }
}

interface IMOrdered<out A: Any>: IMCommon<A> {
    fun fdrop(n: Int): IMOrdered<A> // Return all elements after the first n elements
    fun fnext(): A?
    fun freverse(): IMOrdered<A>
    fun frotl(): IMOrdered<A> // rotate left (A, B, C).frotl() becomes (B, C, A)
    fun frotr(): IMOrdered<A> // rotate right (A, B, C).frotr() becomes (C, A, B)
    fun fswaph(): IMOrdered<A> // swap head  (A, B, C).fswaph() becomes (B, A, C)
    fun <B: Any> fzip(items: IMOrdered<B>): IMOrdered<Pair<A,B>>
    // return value retyped
    override fun fdropAll(items: IMCommon<@UnsafeVariance A>): IMOrdered<A>
    override fun fdropItem(item: @UnsafeVariance A): IMOrdered<A>
    override fun fdropWhen(isMatch: (A) -> Boolean): IMOrdered<A>
    override fun fpopAndRemainder(): Pair<A?, IMOrdered<A>>
    override fun ffilter(isMatch: (A) -> Boolean): IMOrdered<A> // return all elements that match the predicate p
    override fun ffilterNot(isMatch: (A) -> Boolean): IMOrdered<A> // Return all elements that do not match the predicate p

    companion object: IMOrderedAltering {

        override fun <A : Any> fadd(src: A, dest: IMOrdered<A>): IMOrdered<A>? = when (dest) {
            is FList<A> -> TODO()
            is FQueue<A> -> TODO()
            is FStack<A> -> TODO()
            else -> throw RuntimeException("internal error, unknown ${IMOrdered::class.simpleName}: ${dest::class.simpleName ?: dest::class}")
        }

        // O(n)
        private tailrec fun <A: Any> pairwiseEquals(lhs: IMOrdered<A>, rhs: IMOrdered<A>): Boolean = when {
            lhs.fempty() -> rhs.fempty()
            rhs.fempty() -> false
            !(lhs.fnext()!!.equals(rhs.fnext())) -> false
            else -> pairwiseEquals(lhs.fdrop(1), rhs.fdrop(1))
        }

        // O(n)
        private fun <A: Any> pairwiseEquals(lhs: IMOrdered<A>, rhs: Iterable<A>): Boolean {
            val rhsIter = rhs.iterator()
            val rhsEmpty = ! rhsIter.hasNext()
            return when {
                lhs.fempty() -> rhsEmpty
                rhsEmpty -> false
                !(lhs.fnext()!!.equals(rhs.first())) -> false
                else -> {
                    tailrec fun go(l: IMOrdered<A>, r: A): Boolean {
                        val lItem = l.fnext()
                        val lNext = l.fdrop(1)
                        return when {
                            lItem == null -> ! rhsIter.hasNext()
                            ! lItem.equals(r) -> false
                            (! rhsIter.hasNext()) && lNext.fempty() -> true
                            else ->go(lNext, rhsIter.next())
                        }
                    }
                    go(lhs, rhsIter.next())
                }
            }
        }

        private fun untypedScreen(lhs: IMOrdered<*>, rhs: Collection<*>?) : Boolean = rhs?.let { when {
            lhs.fempty() -> rhs.isEmpty()
            lhs.fsize() != rhs.size -> false
            lhs.fpick()!!.isStrictlyNot(rhs.first()!!) -> false
            else -> true
        } } ?: false

        private fun <A: Any> fullScreen(lhs: IMOrdered<A>, rhs: Collection<*>?) : Boolean = untypedScreen(lhs,rhs) && (
            (@Suppress("UNCHECKED_CAST") (rhs as? Iterable<A>))?.let {
                pairwiseEquals(lhs, it)
            } ?: false
        )

        fun <A: Any> equal(lhs: IMOrdered<A>, rhs: IMOrdered<A>): Boolean = when {
            lhs.fempty() -> rhs.fempty()
            rhs.fempty() -> false
            lhs.fsize() != rhs.fsize() -> false
            else -> pairwiseEquals(lhs,rhs)
        }

        fun <A: Any> softEqual(lhs: IMOrdered<A>, rhs: Any?): Boolean = lhs.equals(rhs) || when (rhs) {
            null -> false
            is IMOrdered<*> -> (@Suppress("UNCHECKED_CAST") (rhs as? IMOrdered<A>)?. let {
                    equal(lhs, it)
                } ?: false)
            is LinkedHashSet<*>, is SortedSet<*> -> {
                rhs as Collection<*>
                fullScreen(lhs,rhs)
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
            is Queue<*> -> fullScreen(lhs,rhs)
            is List<*> -> fullScreen(lhs,rhs)
            // is IMCommon<*> -> IMCommon.softEqual(lhs, rhs)
            else -> false
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

typealias ITKart<S, T> = IMCartesian<S, ITMap<S>, T, IMZPair<S,T>>

interface IMCartesian<out S: Any, out U: ITMap<S>, out T: Any, out W:IMZPair<S,T>> {

    infix fun mpro(t: ITMap<@UnsafeVariance T>): ITMap<W>?
    infix fun opro(t: IMOrdered<@UnsafeVariance T>): ITMap<W>?

    companion object {
        fun <S: Any, T: Any, W:IMZPair<S,T>> asZMap(k: ITMap<W>?): ITZMap<S,T>? = try {
            k?.let { try {
                FCartesian.asZMap(it as ITMap<IMZPair<S, T>>) as ITZMap<S, T>
            } catch (ex: NullPointerException) {
                reportException(ex)
                null
            }}
        } catch (ex: ClassCastException) {
            reportException(ex,k)
            null
        }
        fun <S: Any, T: Any> flift2kart(item: IMOrdered<S>): ITKart<S,T> = FCartesian.of(item)
    }
}

interface IMZPair<out A: Any, out B: Any> {
    fun _1():A
    fun _2():B
    fun toPair(): Pair<A,B>
    fun equal(rhs: Pair<@UnsafeVariance A, @UnsafeVariance B>) = (_1().equals(rhs.first) && _2().equals(rhs.second))
    fun equal(rhs: IMZPair<@UnsafeVariance A, @UnsafeVariance B>) = (_1().equals(rhs._1()) && _2().equals(rhs._2()))
}

typealias ITZMap<S,T> = IMZipMap<S,T,IMZPair<S,T>>

interface IMZipWrap<out S: Any, out T: Any> {
    fun asZMap(): ITZMap<S,T>
}

interface IMZipMap<out S: Any, out T: Any, out W: IMZPair<S,T>>: IMZipWrap<S,T> {

    fun <X: Any> fzipMap(f: (S) -> (T) -> X): ITMap<X>
    fun <X: Any> fzippMap(f: (S, T) -> X): ITMap<X> {
        val pf: (S) -> (T) -> X = { s: S -> { t: T -> f(s, t) } }
        return fzipMap(pf)
    }
    fun <X: Any> fkartMap(f: (S) -> (T) -> X ): ITMap<ITMap<X>>
    fun <X: Any> fkartpMap(f: (S, T) -> X ): ITMap<ITMap<X>> {
        val pf: (S) -> (T) -> X = { s: S -> { t: T -> f(s, t) } }
        return fkartMap(pf)
    }
    fun asMap(): ITMap<W>
    fun asIMOrdered(): IMOrdered<W>
    fun equal(rhs: ITZMap<@UnsafeVariance S, @UnsafeVariance T>): Boolean = equals(rhs)
    fun softEqual(rhs: Any?): Boolean = equals(rhs) || softEqual(this, rhs)

    companion object {
        fun <S: Any, T: Any, X: Any, W:IMZPair<S,T>> ITMap<W>.fmap2p(f: (S, T) -> X): ITMap<X> =
            IMCartesian.asZMap(this)!!.fzippMap(f)
        fun <S: Any, T: Any, X: Any, W:IMZPair<S,T>> ITMap<W>.fmap2(f: (S) -> (T) -> X): ITMap<X> =
            IMCartesian.asZMap(this)!!.fzipMap(f)
        fun <S: Any, T: Any, U: Any, X: Any> ITMap<IMZPair<IMZPair<S,T>,U>>.fmap3p(f: (S, T, U) -> X): ITMap<X> =
            this.fmap { f(it._1()._1(), it._1()._2(), it._2()) }
        fun <S: Any, T: Any, U: Any, X: Any> ITMap<IMZPair<IMZPair<S,T>,U>>.fmap3(f: (S) -> (T) -> (U) -> X): ITMap<X> =
            this.fmap {  it.partial3map(f) }
        fun <S: Any, T: Any, U: Any, V: Any, X: Any> ITMap<IMZPair<IMZPair<IMZPair<S,T>,U>,V>>.fmap4p(f: (S, T, U, V) -> X): ITMap<X> =
            this.fmap { f(it._1()._1()._1(), it._1()._1()._2(), it._1()._2(), it._2()) }
        fun <S: Any, T: Any, U: Any, V: Any, X: Any> ITMap<IMZPair<IMZPair<IMZPair<S,T>,U>,V>>.fmap4(f: (S) -> (T) -> (U) -> (V) -> X): ITMap<X> =
            this.fmap { it.partial4map(f) }
        // TODO: more?

        fun <S: Any, T: Any> softEqual(lhs: ITZMap<S,T>, rhs: Any?): Boolean = lhs.equals(rhs) || when (rhs) {
            is IMOrdered<*> -> when {
                lhs.asMap().fempty() -> rhs.fempty()
                rhs.fempty() -> lhs.asMap().fempty()
                rhs.fsize() != lhs.asMap().fsize() -> false
                rhs.fpick() is Pair<*,*> -> {

                    @Suppress("UNCHECKED_CAST") (rhs as IMOrdered<Pair<Any,Any>>)

                    tailrec fun goPairwise(lhs: IMOrdered<IMZPair<Any,Any>>, rhs: IMOrdered<Pair<Any,Any>>): Boolean = when {
                        lhs.fempty() -> true
                        !(lhs.fnext()!!.equal(rhs.fnext()!!)) -> false
                        else -> goPairwise(lhs.fdrop(1), rhs.fdrop(1))
                    }

                    goPairwise(lhs.asIMOrdered(),rhs)

                }
                rhs.fpick() is IMZPair<*,*> -> IMOrdered.equal(lhs.asIMOrdered(), rhs)
                else -> false
            }
            is IMCommon<*> -> lhs.asMap().fempty() && rhs.fempty()
            else -> false
        }
    }
}


// ITMappable, really.  ITMap for brevity
typealias ITMap<S> = IMMapOp<S, IMCommon<S>>

interface IMMapOp<out S: Any, out U: IMCommon<S>>: IMCommon<S> {

    fun <T: Any> fmap(f: (S) -> T): ITMap<T>

    infix fun <T: Any, V: ITMap<T>, W: IMZPair<@UnsafeVariance S,T>> mapWith(tmap: V): ITMap<W> = when {
        this.fempty() -> @Suppress("UNCHECKED_CAST") (this.toEmpty() as ITMap<W>)
        tmap.fempty() -> @Suppress("UNCHECKED_CAST") (tmap.toEmpty() as ITMap<W>)
        this is IMZipWrap<*,*> -> { // S is at least a Pair<*,*>
            (@Suppress("UNCHECKED_CAST") (this as ITMap<IMZPair<Any,Any>>))
            val zm: ITZMap<Any, Any> = FCartesian.asZMap(this)!!
            zipMaps(zm.asMap(), tmap)
        }
        tmap is IMZipWrap<*,*> -> { // T is at least a Pair<*,*>
            (@Suppress("UNCHECKED_CAST") (tmap as ITMap<IMZPair<Any,Any>>))
            val zm: ITZMap<Any, Any> = FCartesian.asZMap(tmap)!!
            zipMaps(this, zm.asMap())
        }
        this !is IMOrdered<*> && tmap !is IMOrdered<*> -> throw RuntimeException("internal error")
        tmap !is IMOrdered<*> -> emptyZipMap()
        this !is IMOrdered<*> -> emptyZipMap()
        else -> zipMaps(this, tmap)
    }


    companion object {

        fun <T: Any> softEqual(lhs: ITMap<T>, rhs: Any?): Boolean = lhs.equals(rhs) || when (lhs) {
            is ITZMap<*,*> -> IMZipMap.softEqual(lhs, rhs)
            is IMOrdered<*> -> when (rhs) {
                is IMOrdered<*> -> IMOrdered.equal(lhs,rhs)
                else -> TODO()
            }
            else -> TODO()
        }

        fun <T: Any> flift2map(item: IMCommon<T>): ITMap<T>? = IM.liftToIMMappable(item)
        fun <T: Any> flift2map(item: T): ITMap<T> {
            check(item !is IMCommon<*>)
            return DWFMap.of(item)
        }
    }
}

interface IMKMappable<out K, out V: Any, out U: IMCommon<TKVEntry<K,V>>> where K: Any, K: Comparable<@UnsafeVariance K> {
    fun <L, T: Any> fmap(f: (TKVEntry<K,V>) -> TKVEntry<L,T>): IMKMappable<L,T,IMCommon<TKVEntry<L,T>>> where L: Any, L: Comparable<@UnsafeVariance L>
}

typealias ITMapp<S> = IMMappOp<S, IMMapOp<S, IMCommon<S>>>

interface IMMappOp<out S: Any, out U: ITMap<S>>: IMCommon<S> {

    fun asITMap(): ITMap<S> = (@Suppress("UNCHECKED_CAST") (this as ITMap<S>))

    // maintains the container of 'this'
    fun <T: Any> fmapp(f: (S) -> T): ITMapp<T> =
        flift2mapp(asITMap().fmap(f))!!

    // drops the container of 'this' and replaces with generic container
    fun <T: Any> fmappx(f: (S) -> T): ITMapp<T> {
        fun fLifted(u: ITMapp<S>): (IMCommon<(S) -> T>) -> ITMap<T> = { g: IMCommon<(S) -> T> ->
            check(1 == g.fsize())
            u.asITMap().fmap(g.fpick()!!)
        }
        return flift2mapp(DWCommon.of(f))!!.fapp(fLifted(this))
      }

    fun <T: Any> fapp(op: (U) -> ITMap<T>): ITMapp<T>

    fun <T: Any, V: ITMap<T>> fmaprod(f: (U) -> V): ITMap<Pair<U,V>> {
        fun fLifted(u: ITMapp<S>): ((ITMap<S>) -> ITMap<T>) -> Pair<ITMap<S>,ITMap<T>> = { g: (ITMap<S>) -> ITMap<T> ->
            val aux: ITMap<T> = u.fapp(g).asITMap()
            Pair(u.asITMap(), aux)
        }
        val fmapp: ITMapp<S> = flift2mapp(this)!!
        val faux: ((U) -> V) -> ITMap<Pair<U,V>> = { _: (U) -> V ->
            val aux = @Suppress("UNCHECKED_CAST") (fLifted(fmapp) as ((U) -> V) -> ITMap<Pair<U,V>>)
            IMMapOp.flift2map(aux(f))!!
        }
        return faux(f)
    }

    // will return: success, if all succeed; xor: only errors (all errors), if any
    fun <R: Any> ftraverse(op: (S) -> R): IMSdj<IMCommon<String>, IMCommon<R>> {
        val fail: (S?) -> String = { v:S? -> "op error during traversal, ${v?.let{ it::class }} item $v" }
        val impl: TraversalImpl<S, String> = TraversalImpl(fail, this)
        return impl.traverse<R, String>(op)
    }

    // will return any and all success, and any and all failures
    fun <R: Any> fgrossTraverse(op: (S) -> R): Pair<IMCommon<String>, IMCommon<R>> {
        val fail: (S?) -> String = { v:S? -> "op error during traversal, ${v?.let{ it::class }} item $v" }
        val impl: TraversalImpl<S, String> = TraversalImpl(fail, this)
        return impl.grossTraverse<R, String>(op)
    }

    fun <R: Any, E: Any> ftraverseWithError(op: (S) -> R, toError: ((String) -> E)): TSDJ<IMCommon<E>, IMCommon<R>> {
        val fail: (S?) -> E = { v:S? -> toError("for:${v?.let{ it::class }} item $v") }
        val impl: TraversalImpl<S, E> = TraversalImpl(fail, this)
        return impl.traverse<R,E>(op)
    }

//    infix fun <T: Any, V: FMapp<T>, W: Pair<@UnsafeVariance S,T>> kmapp(tmapp: V): FMapp<W> =
//        if (fempty() || tmapp.fempty()) DWFMapp.empty() else when(this) {
//            is IMZipMap<*,*> -> {
//                val bbb = this
//                print(bbb)
//                this.fapp
//                TODO()
//            }
//            !is IMOrdered<*> -> DWFMapp.empty()
//            else -> {
//                @Suppress("UNCHECKED_CAST") (this as IMOrdered<S>)
//                val fkart: FMap<Pair<S, T>>? = IMCartesian.flift2kart<S, T>(this).mpro(tmap)
//                fkart?.let { @Suppress("UNCHECKED_CAST") (it as FMap<W>) } ?: DWFMap.empty()
//                TODO()
//            }
//        }

    companion object {
        // fun <T: Any> equal(rhs: FMapp<T>, lhs: FMapp<T>): Boolean = TODO()

        fun <T: Any> flift2mapp(item: ITMap<T>) = IM.liftToIMMapplicable(item)
        fun <T: Any> flift2mapp(item: IMCommon<T>): ITMapp<T>? = IMMapOp.flift2map(item)?.let { mappable -> flift2mapp(mappable) }
        fun <T: Any> flift2mapp(item: T): ITMapp<T>? {
            check(item !is IMCommon<*>)
            return IMMapOp.flift2map(item).let { mapOp: IMMapOp<T, IMCommon<T>> -> flift2mapp(mapOp) }
        }
        /*
         infix fun <S: Any, T: Any, W: Any> FMap<S>.kmap(tmap: FMap<T>): ((S) -> T) -> W = { s2t: (S) ->T -> { t2w: (T) -> W ->
            @Suppress("UNCHECKED_CAST") (this as IMOrdered<S>)
            val aux: FMap<Pair<S, T>>? = IMCartesian.flift2kart<S,T>(this).mpro(tmap)
            val foo = aux.fmap<W> { it: Pair<S, T> -> }
            TODO()

        }}

        infix fun <A: Any, B: Any, C: Any> ITMapp<A>.kmapp(bk: ITMapp<B>): ITMapp<C> = if (fempty() || bk.fempty()) flift2mapp(DWFMap.empty())!! else {
            @Suppress("UNCHECKED_CAST") (this as IMOrdered<A>)
            IMCartesian.flift2kart<A,B>(this).mpro(bk.asITMap())
            TODO()
        }

         */
    }
}

/*
interface IMCartesian<out S: Any, out U: FMap<S>, out T: Any, out V: FMap<T>, out W:Pair<U,V>> {

    infix fun mpro(t: FMap<@UnsafeVariance T>): FMap<W>?
    infix fun opro(t: IMOrdered<@UnsafeVariance T>): FMap<W>?

    companion object {
        fun <S: Any, T: Any> flift2kart(item: IMOrdered<S>): FKart<S,T> = FCartesian.of(item)

    }
}

 */

// simple disjunction
interface IMDj<out L, out R>: IMOrdered<IMDj<L,R>>,
    IMDjFiltering<L, R> {
    companion object {
        fun <A, B, L:Any, R: Any> bifold(djs: IMCommon<IMDj<A,B>>, acc:Pair<IMList<L>, IMList<R>>? = null): ((A) -> L, (B) -> R) -> Pair<IMList<L>, IMList<R>> = { fl: (A) -> L, fr: (B) -> R ->
            val biAcc = acc ?: Pair(FList.emptyIMList(),FList.emptyIMList())
            fun apportion (acc:Pair<IMList<L>, IMList<R>>, item:IMDj<A,B>): Pair<IMList<L>, IMList<R>> =
                item.right()?.let { dr -> Pair(acc.first, IMList.fprepend(fr(dr), acc.second)!!) } ?: Pair( IMList.fprepend(fl(item.left()!!),acc.first)!!, acc.second)
            djs.ffold(biAcc, ::apportion)
        }
    }
}

// higher kind disjunction
interface IMSdj<out L, out R>: IMDj<L,R>,
    ITMap<IMDj<L,R>>,
    ITMapp<IMDj<L,R>> {
}

typealias ITDsw<A> = IMDisw<A, IMCommon<A>>

// Di-sposable wrappers

interface IMDisw<out A: Any, out B: IMCommon<A>>:
    IMCommon<A>,
    IMOrdered<A>

typealias ITDmw<A> = IMDimw<A, IMCommon<A>>

interface IMDimw<out A: Any, out B: IMCommon<A>>:
    IMMapOp<A, IMDimw<A,B>>,
    IMOrdered<A>

typealias ITDaw<A> = IMDiaw<A, IMCommon<A>>

interface IMDiaw<out A: Any, out B: IMCommon<A>>:
    IMMapOp<A, IMDiaw<A,B>>,
    IMMappOp<A, ITMap<A>>,
    IMOrdered<A>

// collections

interface IMList<out A:Any>: IMOrdered<A>,
    IMListFiltering<A>,
    IMListGrouping<A>,
    IMListTransforming<A>,
    IMListUtility<A>,
    IMListExtras<A>,
    IMReducible<A>,
    IMMapOp<A, IMList<A>>,
    IMMappOp<A, IMList<A>>,
    IMListTyping<A> {
    override fun <R> ffold(z: R, f: (acc: R, A) -> R): R = ffoldLeft(z, f)
    override fun freduce(f: (acc: A, A) -> @UnsafeVariance A): A? = freduceLeft(f)

    companion object: IMListWritable by FList.Companion {}
}

interface IMStack<out A:Any>: IMOrdered<A>,
    IMStackFiltering<A>,
    IMStackGrouping<A>,
    IMStackTransforming<A>,
    IMStackAltering<A>,
    IMStackUtility<A>,
    IMMapOp<A, IMStack<Nothing>>,
    IMMappOp<A, IMStack<A>>,
    IMStackTyping<A> {

    companion object: IMStackWritable by FStack.Companion {}

}

interface IMQueue<out A:Any>: IMOrdered<A>,
    IMQueueFiltering<A>,
    IMQueueGrouping<A>,
    IMQueueTransforming<A>,
    IMQueueAltering<A>,
    IMQueueUtility<A>,
    IMMapOp<A, IMQueue<Nothing>>,
    IMMappOp<A, IMQueue<A>>,
    IMQueueTyping<A> {

    companion object: IMQueueWritable by FQueue.Companion {}
}

interface IMSet<out A: Any>: IMCommon<A>,
    IMRSetAltering<A>,
    IMSetFiltering<A>,
    IMSetGrouping<A>,
    IMSetTransforming<A>,
    IMSetUtility<A>,
    IMSetExtras<A>,
    IMReducible<A>,
    IMMapOp<A, IMSet<Nothing>>,
    IMMappOp<A, IMSet<A>>,
    IMSetTyping<A> {
    fun asIMSetNotEmpty(): IMSetNotEmpty<A>?
    fun <K> asIMXSetNotEmpty(): IMXSetNotEmpty<K>? where K: Any, K: Comparable<K>
    fun asIMRSetNotEmpty(): IMRSetNotEmpty<A>? = null
}

interface IMHeap<out A: Any>: IMCommon<A>,
    IMMapOp<A, IMHeap<Nothing>>,
    IMMappOp<A, IMHeap<A>>,
    IMHeapTyping<A>

interface IMMap<out K, out V: Any>: IMCommon<TKVEntry<K,V>>,
    IMMapFiltering<K, V>,
    IMMapGrouping<K, V>,
    IMMapTransforming<K, V>,
    IMMapUtility<K, V>,
    IMMapAltering<K, V>,
    IMMapExtras<K, V>,
    IMKeyed<K>,
    IMKeyedValue<K, V>,
    IMReducible<V>,
//    IMFoldable<TKVEntry<K,V>>,
    IMKMappable<K, V, IMMap<Nothing,Nothing>>,
    IMMapTyping<K, V>
        where K: Any, K: Comparable<@UnsafeVariance K> {

    // IMCommon
    override fun fcount(isMatch: (TKVEntry<K,V>) -> Boolean): Int = // count the element that match the predicate
        asIMBTree().fcount(isMatch)
    override fun fisNested(): Boolean? = if (fempty()) null else FT.isContainer(fpick()!!.getv())
    // IMKeyed
    override fun fisStrictlyLike(sample: KeyedTypeSample<KClass<Any>?, KClass<Any>>?): Boolean? = sample?.let {
        if (fempty()) null else null == asIMBTree().ffindAny { tkv -> !(tkv.strictlyLike(sample)) }
    }
    // IMKeyedValue
    override fun asIMMap(): IMMap<K,V> = this

    // derivatives

    // since order is an ambiguous property of Tree, f MUST be commutative
    fun <C> ffoldv(z: C, f: (acc: C, V) -> C): C = // 	“Fold” the value of the tree using the binary operator o, using an initial seed s, going from left to right (see also reduceLeft)
        ffold(z) { acc, tkv -> f(acc, tkv.getv()) }
    fun <T: Any> fmapToList(f: (TKVEntry<K, V>) -> T): IMList<T> = // 	Return a new sequence by applying the function f to each element in the List
        ffold(FList.emptyIMList()) { acc, tkv -> acc.fprepend(f(tkv)) }
    fun <W: Any> fmapvToList(f: (V) -> W): IMList<W> = // 	Return a new sequence by applying the function f to each element in the List
        ffold(FList.emptyIMList()) { acc, tkv -> acc.fprepend(f(tkv.getv())) }
}

interface IMBTree<out A, out B: Any>: IMCommon<TKVEntry<A,B>>,
    IMBTreeUtility<A, B>,
    IMBTreeTraversing<A, B>,
    IMBTreeFiltering<A, B>,
    IMBTreeGrouping<A, B>,
    IMBTreeTransforming<A,B>,
    IMBTreeExtras<A, B>,
    IMKeyed<A>,
    IMKeyedValue<A, B>,
    IMReducible<TKVEntry<A,B>>,
    IMKMappable<A, B, IMBTree<Nothing,Nothing>>,
    IMBTreeTyping<A, B>
        where A: Any, A: Comparable<@UnsafeVariance A> {

    override fun fcount(isMatch: (TKVEntry<A, B>) -> Boolean): Int =
        ffold(0) { acc, item -> if(isMatch(item)) acc + 1 else acc }
    override fun fisNested(): Boolean?  = if (fempty()) null else FT.isContainer(fpick()!!.getv())
    // IMKeyed
    override fun fisStrictlyLike(sample: KeyedTypeSample<KClass<Any>?,KClass<Any>>?): Boolean? = sample?.let {
        if (fempty()) null else null == this.ffindAny { tkv -> !(tkv.strictlyLike(it)) }
    }
    // IMKeyedValue
    override fun asIMBTree(): IMBTree<A,B> = this
    override fun fcontainsValue(value: @UnsafeVariance B): Boolean = if (this.fempty()) false else {
        fun isMatch(entry: TKVEntry<A, B>): Boolean = entry.getv() == value
        ffindAny { isMatch(it) } != null
    }
    override fun fcountValue(isMatch: (B) -> Boolean): Int =  if (this.fempty()) 0 else {
        ffold(0) { acc, tkv -> if(isMatch(tkv.getv())) acc + 1 else acc }
    }

    // derivatives

    // since order is an ambiguous property of Tree, f MUST be commutative
    fun <C> ffoldv(z: C, f: (acc: C, B) -> C): C = // 	“Fold” the value of the tree using the binary operator o, using an initial seed s, going from left to right (see also reduceLeft)
        ffold(z) { acc, tkv -> f(acc, tkv.getv()) }
    fun <T: Any> fmapToList(f: (TKVEntry<A, B>) -> T): IMList<T> = // 	Return a new sequence by applying the function f to each element in the List
        ffold(FList.emptyIMList()) { acc, tkv -> acc.fprepend(f(tkv)) }
    fun <C: Any> fmapvToList(f: (B) -> C): IMList<C> = // 	Return a new sequence by applying the function f to each element in the List
        ffold(FList.emptyIMList()) { acc, tkv -> acc.fprepend(f(tkv.getv())) }

    companion object: IMBTreeAltering {

        fun<A, B: Any> softEqual(lhs: IMBTree<A,B>, rhs: Any?): Boolean
        where A: Any, A: Comparable<@UnsafeVariance A> = lhs.equals(rhs) || when (rhs) {
            is IMBTree<*, *> -> when {
                lhs.fempty() -> rhs.fempty()
                rhs.fempty() -> false
                rhs.fsize() != lhs.fsize() -> false
                rhs.froot()!!.strictlyNot(lhs.froot()!!.untype()) -> false
                rhs.froot()!!.getvKc().isStrictlyNot(lhs.froot()!!.untype().getvKc()) -> false
                rhs.froot()!!.getkKc().isStrictlyNot(lhs.froot()!!.untype().getkKc()) -> false
                else -> (@Suppress("UNCHECKED_CAST") (rhs as? IMBTree<A, B>))?.let {
                    IMBTreeEqual2 (lhs, it)
                } ?: false
            }
            is Collection<*> -> when(val iter = rhs.iterator()) {
                is FTreeIterator<*, *> -> softEqual(lhs, iter.retriever.original())
                else -> false
            }
            else -> false
        }

        override fun <A, B : Any> fadd(src: TKVEntry<A, B>, dest: IMKeyedValue<A, B>): IMBTree<A, B>?
        where A: Any, A:Comparable<A> = try {
            IMKeyedValue.fadd(src, dest)!!.asIMBTree()
        } catch (ex: Exception) {
            dest.reportException(ex, this)
            null
        }

        override fun <A, B : Any> fadd(src: TKVEntry<A, B>, dest: IMBTree<A, B>): IMBTree<A, B>
        where A: Any, A:Comparable<A> = when (dest) {
            is FRBTree ->  dest.finsertTkv(src)
            is FBSTree ->  dest.finsertTkv(src)
            else -> throw RuntimeException("internal error, unknown ${IMBTree::class.simpleName}: ${dest::class.simpleName ?: dest::class}")
        }

        override fun <A, B : Any> faddAll(src: IMCommon<TKVEntry<A, B>>, dest: IMBTree<A, B>): IMBTree<A, B>
        where A: Any, A:Comparable<A> =
            IMKeyedValue.faddAll(src,dest).asIMBTree()

        override fun <A, B : Any> finserts(src: IMKeyedValue<A, B>, dest: IMBTree<A, B>): IMBTree<A, B>
        where A: Any, A:Comparable<A> = when(dest) {
            is FRBTree<A,B> -> src.asIMCommon<TKVEntry<A,B>>()!!.ffold(dest){frbt, tkv -> fadd(tkv, frbt) as FRBTree<A,B> }
            is FBSTree<A,B> -> src.asIMCommon<TKVEntry<A,B>>()!!.ffold(dest){fbst, tkv -> fadd(tkv, fbst) as FBSTree<A,B> }
            else -> throw RuntimeException("internal error, unknown ${IMBTree::class.simpleName}: ${dest::class.simpleName ?: dest::class}")
        }
    }
}

interface IMBTreeNotEmpty<out A, out B: Any>: IMBTree<A,B> where A: Any, A: Comparable<@UnsafeVariance A>

// Set derivatives

interface IMRSetNotEmpty<out A: Any>: IMCommon<A>,
    IMRSetAltering<A>,
    IMSetFiltering<A>,
    IMSetGrouping<A>,
    IMSetUtility<A>,
    IMSetExtras<A>,
    IMSetTyping<A> {
    fun <KK> xdj(): TSDJ<ErrorMsgTrap, IMXSetNotEmpty<KK>> where KK: Any, KK: Comparable<KK>
    fun sdj(): TSDJ<IMSetNotEmpty<@UnsafeVariance A>, IMXSetNotEmpty<*>>
}

interface IMSetNotEmpty<out A:Any>: IMSet<A>, IMRSetNotEmpty<A>,
    IMSetAltering<A>,
    IMSetTransforming<A> {
    override fun asIMSetNotEmpty(): IMSetNotEmpty<A>? = sdj().left()
    override fun <K> asIMXSetNotEmpty(): IMXSetNotEmpty<K>? where K: Any, K: Comparable<K> = xdj<K>().right()
    override fun asIMRSetNotEmpty(): IMRSetNotEmpty<A> = this
}

interface IMXSetNotEmpty<out A>: IMSet<A>, IMRSetNotEmpty<A>,
    IMXSetAltering<A>,
    IMXSetTransforming<A>
        where A: Any, A: Comparable<@UnsafeVariance A> {
    override fun asIMSetNotEmpty(): IMSetNotEmpty<A>? = null
    override fun <K> asIMXSetNotEmpty(): IMXSetNotEmpty<K>? where K: Any, K: Comparable<K> =
        @Suppress("UNCHECKED_CAST") (this as? IMXSetNotEmpty<K>)
    override fun asIMRSetNotEmpty(): IMRSetNotEmpty<A> = this
}

// ============ INTERNAL

internal interface IMKSet<out K, out A:Any>: IMSet<A>,
    IMKeyed<K>,
    IMKeyedValue<K, A>,
    IMKSetGrouping<K, A>,
    IMKSetTransforming<K, A>,
    IMKSetExtras<K, A>,
    IMKSetUtility<K, A>,
    IMKSetTyping<K, A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    fun isKeyedAlike(rhs: IMSet<@UnsafeVariance A>): Boolean? = if (fempty()) null else {
        rhs as IMKSet<*, A>
        if (rhs.fempty()) null else fpickKey().isStrictly(rhs.fpickKey())
    }
    fun asIMKSetNotEmpty(): IMKSetNotEmpty<K, A>? = null
    fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, A>?
    fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>?

    override fun fisStrictlyLike(sample: KeyedTypeSample<KClass<Any>?, KClass<Any>>?): Boolean? = sample?.let {
        this.asIMKSetNotEmpty()?.let { null == it.toIMBTree().ffindAny { tkv -> tkv.strictlyLike(sample) } }
    }
 }

internal interface IMKSetNotEmpty<out K, out A:Any>: IMKSet<K,A>,
    IMKSetFiltering<K,A>,
    IMRSetNotEmpty<A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    fun toSetKey(a: @UnsafeVariance A): K
    override fun asIMKSetNotEmpty(): IMKSetNotEmpty<K, A> = this
}

internal interface IMKASetNotEmpty<out K, out A:Any>: IMKSetNotEmpty<K, A>,
    IMSetNotEmpty<A>,
    IMKASetAltering<K, A>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    override fun sdj() = IMSetDJL(this)
    override fun <J> xdj(): TSDJ<ErrorMsgTrap, IMXSetNotEmpty<J>> where J: Any, J:Comparable<J> =
        @Suppress("UNCHECKED_CAST") (ErrorMsgTrap("failure: ${this::class.simpleName}") as TSDJ<ErrorMsgTrap, IMXSetNotEmpty<J>>)
    override fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, A> = this
    override fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K>? = null
}

internal interface IMKKSetNotEmpty<out K>: IMKSetNotEmpty<K, K>,
    IMXSetNotEmpty<K>,
    IMKKSetTransforming<K>,
    IMKKSetAltering<K>
        where K: Any, K: Comparable<@UnsafeVariance K> {
    override fun sdj() = IMXSetDJR(this)
    override fun <J> xdj(): TSDJ<ErrorMsgTrap, IMXSetNotEmpty<J>> where J:Any, J:Comparable<J> =
        @Suppress("UNCHECKED_CAST") (IMXSetDJR(this) as TSDJ<ErrorMsgTrap, IMXSetNotEmpty<J>>)
    override fun asIMKASetNotEmpty(): IMKASetNotEmpty<K, K>? = null
    override fun asIMKKSetNotEmpty(): IMKKSetNotEmpty<K> = this
}
