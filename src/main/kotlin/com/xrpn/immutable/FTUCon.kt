package com.xrpn.immutable

import com.xrpn.imapi.*
import com.xrpn.imapi.IMKSet
import java.util.RandomAccess
import kotlin.reflect.KClass

/* This, alas, is a form of boxing.  The API must be strictly limited to whatever contract is
 * supported by all boxed entities.  For instance, <boxed>.first() is NOT ok since ordering has meaning
 * for a list or a stack or a queue or a heap, but not for a set or a tree or a map; <boxed>.count()
 * is ok since all containers can count how many items they contain. Were it always that simple... */

internal typealias UniContainer<V> = UCon<Nothing,Nothing,Nothing,Nothing,Nothing,Nothing,V>

internal sealed class /* UniContainer */ UCon<out A, out B, out C, out D, out E, K, out V: Any>() where K: Any, K: Comparable<K>, A: Collection<V>, B: Map<K,V>, C: IMCommon<V>, D: ArrayList<@UnsafeVariance V> {

    /* A UniContainer MUST NOT contain another UniContainer, directly ir indirectly,
       This implies, among the rest, that no container-at-large is allowed to hold
       a UniContainer.  Whence, internal.
     */

    fun imset(): IMKSet<K, V>? = when (this) {
        is UCIMSET -> im
        else -> null
    }

    fun imcoll(): IMCommon<V>? = when (this) {
        is UCIMC -> im
        else -> null
    }

    fun kcoll(): Collection<V>? = when (this) {
        is UCKC -> c
        else -> null
    }

    fun kmap(): Map<K, V>? = when (this) {
        is UCKMAP -> m
        else -> null
    }

    fun karyl(): ArrayList<@UnsafeVariance V>? = when (this) {
        is UCARYL -> arl
        else -> null
    }

    fun all(predicate: (V) -> Boolean): Boolean = isEmpty() || count(predicate) == length()
    fun filterNotEmpty(): UniContainer<V>? = FT.filterNotEmpty(@Suppress("UNCHECKED_CAST") (this as UniContainer<V>))

    abstract fun asUC(): UniContainer<V>
    abstract fun count(isMatch: (V) -> Boolean): Int
    abstract fun isEmpty(): Boolean
    abstract fun isNested(): Boolean?
    abstract fun length(): Int
    abstract fun pick(): V?

    fun vKc(): KClass<out V>? = pick()?.let { it::class }
    fun isOfValue(kc: KClass<*>): Boolean? = vKc()?.let { it == kc }
    fun kKc(): KClass<out K>? = when (this) {
        is UCKMAP -> pickEntry()?.key!!::class
        is UCIMSET -> pickEntry()?.getkKc()
        else -> null
    }
    fun isOfKey(kc: KClass<*>): Boolean? = kKc()?.let { it == kc }
    fun isOf(sample: KeyedTypeSample<KClass<Any>?, KClass<Any>>): Boolean? = vKc()?.let { sample.isLike(kKc(), it) }
    fun typeSample(): KeyedTypeSample<KClass<Any>?, KClass<Any>> = KeyedTypeSample(kKc()?.let { @Suppress("UNCHECKED_CAST") (it as KClass<Any>) }, @Suppress("UNCHECKED_CAST") (vKc()!! as KClass<Any>))
    fun isStrictlyLike(sample: KeyedTypeSample<KClass<Any>?, KClass<Any>>): Boolean? =
        isOf(sample)?.let { it && strictlyLike(sample) } // NOT recursive if nested
    abstract fun isStrictInternally(): Boolean
    fun isStrictInternallyOf(sample: KeyedTypeSample<KClass<Any>?, KClass<Any>>): Boolean? =
        isOf(sample)?.let { it && strictlyLike(sample) && isStrictInternally() } // checks strictness
    fun stricture(register: SingleInit<KeyedTypeSample< /* key */ KClass<Any>?, /* value */ KClass<Any>>>): Boolean {
        return if (this.isEmpty()) true else {
            register.init(typeSample())
            val isLikeObs = isStrictlyLike(register.get()!!)!!
            isLikeObs && isStrictInternally()
        }
    }
    fun isStrict(): Boolean = if (isEmpty()) true else when {
        isNested()!! -> isStrictlyLike(KeyedTypeSample(kKc()?.let { @Suppress("UNCHECKED_CAST")(it as KClass<Any>) }, @Suppress("UNCHECKED_CAST")(vKc()!! as KClass<Any>))) ?: false
        isStrictInternally() -> true
        else -> false
    }


    fun <T: Any> getIfIs(containerKc: KClass<T>): T? = when (this) {
        is UCIMSET -> if (imset()!!.isStrictly(containerKc)) @Suppress("UNCHECKED_CAST") (imset()!! as T) else null
        is UCIMC -> if (imcoll()!!.isStrictly(containerKc)) @Suppress("UNCHECKED_CAST") (imcoll()!! as T) else null
        is UCKC -> if (kcoll()!!.isStrictly(containerKc)) @Suppress("UNCHECKED_CAST") (kcoll()!! as T) else null
        is UCKMAP -> if (kmap()!!.isStrictly(containerKc)) @Suppress("UNCHECKED_CAST") (kmap()!! as T) else null
        is UCARYL -> if (karyl()!!.isStrictly(containerKc)) @Suppress("UNCHECKED_CAST") (karyl()!! as T) else null
    }

    protected abstract fun strictlyLike(sample: KeyedTypeSample<KClass<Any>?, KClass<Any>>): Boolean

    companion object {
        fun <K, A: Any> ofIMKSet(c: IMKSet<K, A>): UniContainer<A> where K: Any, K: Comparable<K> = UCIMSET(c).asUC()
        fun <A: Any> ofIMCollection(c: IMCommon<A>): UniContainer<A> = UCIMC(c).asUC()
        fun <A: Any> ofCollection(c: Collection<A>): UniContainer<A> = UCKC(c).asUC()
        fun <A: Any> ofArray(c: Array<A>): UniContainer<A> = ofArrayList(c.asList() as ArrayList<A>)
        fun <A: Any> ofArrayList(c: ArrayList<A>): UniContainer<A> = UCARYL(c).asUC()
        fun <K, A: Any> ofMap(c: Map<K, A>): UniContainer<A> where K: Any, K: Comparable<K> = UCKMAP(c).asUC()
        fun of(item: Any): UniContainer<*>? = if (!FT.isContainer(item)) null else when (item) {
            is TKVEntry<*,*> -> of(item.getv())
            is TSDJ<*,*> -> item.left()?.let { of(it) } ?: of(item.right()!!)
            is IMKSet<*, *> -> ofIMKSet(item).asUC()
            is IMCommon<*> -> ofIMCollection(item).asUC()
            is ArrayList<*> -> ofArrayList(item).asUC()
            is Collection<*> -> @Suppress("UNCHECKED_CAST") (UCKC(item as Collection<Any>) as UniContainer<*>)
            is Map<*, *> -> @Suppress("UNCHECKED_CAST") (UCKMAP(item as Map<Nothing, Any>) as UniContainer<*>)
            is Array<*> -> ofArrayList(item.asList() as ArrayList<*>)
            else -> null
        }
    }
}

internal fun <A: Any> A.toUCon(): UniContainer<*>? = UCon.of(this)

internal data class UCIMSET<K, out V: Any>(val im: IMKSet<K, V>): UCon<Nothing, Nothing, Nothing, Nothing, IMKSet<K, V>, K, V>() where K: Any, K: Comparable<K> {
    override fun asUC(): UniContainer<V> = @Suppress("UNCHECKED_CAST") (this as UniContainer<V>)
    override fun count(isMatch: (V) -> Boolean): Int = im.fcountValue(isMatch)
    override fun isEmpty(): Boolean = im.fempty()
    override fun isNested(): Boolean? = if (im.fempty()) null else FT.isNested(im)
    override fun isStrictInternally(): Boolean = im.fempty() || im.fisStrict()
    override fun length(): Int = im.fsize()
    override fun pick(): V? = im.fpickNotEmpty()
    fun pickEntry(): TKVEntry<K, V>? = if (im.fempty()) null else im.asIMBTree().fpick()
    override fun strictlyLike(sample: KeyedTypeSample<KClass<Any>?, KClass<Any>>): Boolean {
        check(!isEmpty())
        return sample.hasKey() &&
        sample.isLikeKey(kKc()) &&
        run {
            val simpleStrict = im.fisStrict()
            return if (simpleStrict) {
                val ne = im.fpickNotEmpty()
                ne?.let { sample.isLikeValue(it::class) } ?: true
            } else false
       }
    }
}

internal data class UCIMC<out V: Any>(val im: IMCommon<V>): UCon<Nothing, Nothing, IMCommon<V>, Nothing, Nothing, Nothing, V>() {
    init {
        check(im !is IMSet<V>)
    }
    override fun asUC(): UniContainer<V> = @Suppress("UNCHECKED_CAST") (this as UniContainer<V>)
    override fun count(isMatch: (V) -> Boolean): Int = im.fcount(isMatch)
    override fun isEmpty(): Boolean = im.fempty()
    override fun isNested(): Boolean? = if (im.fempty()) null else FT.isNested(im)
    override fun isStrictInternally(): Boolean = im.fempty() || im.fisStrict()
    override fun length(): Int = im.fsize()
    override fun pick(): V? = im.fpick()
    override fun strictlyLike(sample: KeyedTypeSample<KClass<Any>?, KClass<Any>>): Boolean {
        check(!isEmpty())
        return when (im) {
            is IMKeyedValue<*, *> ->im.fisStrictlyLike(sample)!!
            else -> (!sample.hasKey()) && sample.isLikeValue(im.fpickNotEmpty()!!::class)
        }
    }
}

internal data class UCKC<out V: Any>(val c: Collection<V>): UCon<Collection<V>, Nothing, Nothing, Nothing, Nothing, Nothing,V>() {
    override fun asUC(): UniContainer<V> = @Suppress("UNCHECKED_CAST") (this as UniContainer<V>)
    override fun count(isMatch: (V) -> Boolean): Int = c.count(isMatch)
    override fun isEmpty(): Boolean = c.isEmpty()
    override fun isNested(): Boolean? = if (c.isEmpty()) null else FT.isNested(c)
    override fun isStrictInternally(): Boolean = c.isEmpty() || vKc()?.let { FT.isStrictCollectionOf(c, it) } ?: false /* has nulls */
    override fun length(): Int = c.size
    override fun pick(): V? = c.firstNotNullOfOrNull { it }
    override fun strictlyLike(sample: KeyedTypeSample<KClass<Any>?, KClass<Any>>): Boolean {
        // check(!isEmpty())
        return (!sample.hasKey()) && pick()?.let {
            sample.isLikeValue(it::class) &&
            isStrictInternally()
        } ?: throw RuntimeException("nternal error, strictlyLike on empty UCon(${c::class})")
    }
}

internal data class UCKMAP<K, out V: Any>(val m: Map<K,V>): UCon<Nothing, Map<K,V>, Nothing, Nothing, Nothing, K, V>() where K: Any, K: Comparable<K> {
    override fun asUC(): UniContainer<V> = @Suppress("UNCHECKED_CAST") (this as UniContainer<V>)
    override fun count(isMatch: (V) -> Boolean): Int = m.count { entry -> isMatch(entry.value) }
    override fun isEmpty(): Boolean = m.isEmpty()
    override fun isNested(): Boolean? = if (m.isEmpty()) null else FT.isNested(m)
    override fun isStrictInternally(): Boolean = m.isEmpty() || pickEntry()?.let { FT.isStrictMapOf(m, it.key::class, it.value::class) } ?: false
    override fun length(): Int = m.size
    override fun pick(): V? = pickEntry()?.value
    fun pickEntry(): Map.Entry<K, V>? = m.entries.firstOrNull()
    override fun strictlyLike(sample: KeyedTypeSample<KClass<Any>?, KClass<Any>>): Boolean {
        check(!isEmpty())
        return sample.hasKey() && pickEntry()?.let {
            sample.isLike(it.key::class, it.value::class) &&
            isStrictInternally()
        } ?: throw RuntimeException("nternal error, strictlyLike on empty UCon(${m::class})")
    }
}

internal data class UCARYL<out V: Any>(val arl: ArrayList<@UnsafeVariance V>): RandomAccess, UCon<Nothing, Nothing, Nothing, ArrayList<@UnsafeVariance V>, Nothing, Nothing, V>() {
    override fun asUC(): UniContainer<V> = @Suppress("UNCHECKED_CAST") (this as UniContainer<V>)
    override fun count(isMatch: (V) -> Boolean): Int = arl.count(isMatch)
    override fun isEmpty(): Boolean = arl.isEmpty()
    override fun isNested(): Boolean? = if (arl.isEmpty()) null else FT.isNested(arl)
    override fun isStrictInternally(): Boolean = arl.isEmpty() || vKc()?.let { FT.isStrictCollectionOf(arl, it) } ?: false
    override fun length(): Int = arl.size
    override fun pick(): V? = arl.firstNotNullOfOrNull { it }
    override fun strictlyLike(sample: KeyedTypeSample<KClass<Any>?, KClass<Any>>): Boolean {
        // check(!isEmpty())
        return (!sample.hasKey()) && pick()?.let {
            sample.isLikeValue(it::class) &&
            isStrictInternally()
        } ?: throw RuntimeException("nternal error, strictlyLike on empty UCon(${arl::class})")
    }
}
