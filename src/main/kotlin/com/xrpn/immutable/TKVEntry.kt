package com.xrpn.immutable

import com.xrpn.imapi.*
import kotlin.reflect.KClass


internal fun <A, B:Any> TKVEntry<A, B>.fitKeyOnly(key: A): FBTFIT where A: Any, A: Comparable<A> = fitKeyToEntry(key,this)

internal fun <A, B:Any> fitKeyToEntry(key: A, entry: TKVEntry<A, B>): FBTFIT where A: Any, A: Comparable<A> {
    val outcome = entry.vc?.compare(key, entry.getk()) ?: key.compareTo(entry.getk())
    return when {
        outcome == 0 -> FBTFIT.EQ
        outcome < 0 -> FBTFIT.LEFT
        else -> FBTFIT.RIGHT
    }
}

interface TKVEntry<out A, out B: Any>: Comparable<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>, Map.Entry<A, B>, IMCollection<TKVEntry<@UnsafeVariance A, @UnsafeVariance B>>
where A: Any, A: Comparable<@UnsafeVariance A> {

    val vc: Comparator<@UnsafeVariance A>?
        get() = null

    // the natural sorting order is Comparable<A>, unless vc (a comparator for A) is given.
    // To sort by B, create a TKVEntryK<B, B> giving it a Comparator<B>.
    // It is trivial to make B Comparable if a Comparator exists. (And vice-versa)
    // The current setup does not require B to be comparable -- it only requires B to have a (comparable) key.
    override operator fun compareTo(other: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>): Int {
        val localOutcome: Int = vc?.compare(this.getk(), other.getk()) ?: this.getk().compareTo(other.getk())
        return if (vc === other.vc) localOutcome else {
            val remoteOutcome = other.vc?.compare(other.getk(), this.getk()) ?: other.getk().compareTo(this.getk())
            when {
                localOutcome == 0 && remoteOutcome == 0 -> 0
                localOutcome < 0 && 0 < remoteOutcome -> localOutcome
                0 < localOutcome && remoteOutcome < 0 -> localOutcome
                else -> // comparators disagree: broken symmetric, reflexive, transitive relation
                    throw IllegalStateException(TKVEntryType.CANNOT_COMPARE)
            }
        }
    }

    fun getk(): A
    fun getkKc(): KClass<out A>
    fun getv(): B
    fun getvKc(): KClass<out B>
    fun copy(): TKVEntry<A,B>
    fun toPair(): Pair<A,B> = Pair(getk(),getv())
    fun toSample(): Pair<KClass<out A>, KClass<out B>> = Pair(getkKc(),getvKc())
    fun equal(other: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>?): Boolean
    fun strictly(other: TKVEntry<@UnsafeVariance A, @UnsafeVariance B>?): Boolean
    fun isStrict(): Boolean
    fun strictlyLike(sample: KeyedTypeSample<KClass<@UnsafeVariance A>?, KClass<@UnsafeVariance B>>?): Boolean

    companion object {
        fun <A: Comparable<A>, B: Any> ofkv (key:A, value: B): TKVEntry<A, B> = when (key) {
            is Int -> if (value is Int) (@Suppress("UNCHECKED_CAST") (RKTKVEntry(key, value) as TKVEntry<A, B>))
                else (@Suppress("UNCHECKED_CAST") (RITKVEntry(key, value) as TKVEntry<A, B>))
            is String -> if (value is String) (@Suppress("UNCHECKED_CAST") (RKTKVEntry(key, value) as TKVEntry<A, B>))
                else (@Suppress("UNCHECKED_CAST") (RSTKVEntry(key, value) as TKVEntry<A, B>))
            else -> if (key::class == value::class) {
                @Suppress("UNCHECKED_CAST") (value as A)
                val res = @Suppress("UNCHECKED_CAST") (ofkk(key, value) as TKVEntry<A, B>)
                res
            } else TKVEntryK(key, value)
        }
        fun <A: Comparable<A>, B: Any> ofkvc (key:A, value: B, cc: Comparator<A>): TKVEntry<A, B> = when (key) {
            is Int -> if (value is Int) (@Suppress("UNCHECKED_CAST") (RKTKVEntry(key, value, cc as Comparator<Int>) as TKVEntry<A, B>))
                else (@Suppress("UNCHECKED_CAST") (RITKVEntry(key, value, cc as Comparator<Int>) as TKVEntry<A, B>))
            is String ->if (value is String) (@Suppress("UNCHECKED_CAST") (RKTKVEntry(key, value, cc as Comparator<String>) as TKVEntry<A, B>))
                else (@Suppress("UNCHECKED_CAST") (RSTKVEntry(key, value, cc as Comparator<String>) as TKVEntry<A, B>))
            else -> if (key::class == value::class) {
                @Suppress("UNCHECKED_CAST") (value as A)
                val res = @Suppress("UNCHECKED_CAST") (ofkkc(key, value, cc) as TKVEntry<A, B>)
                res
            } else TKVEntryK(key, value, cc)
        }

        fun <A> ofkk (key:A, value: A): TKVEntry<A, A> where A: Any, A: Comparable<A> = RKTKVEntry(key, value)
        fun <A> ofkkc (key:A, value: A, cc: Comparator<A>): TKVEntry<A, A> where A: Any, A: Comparable<A> = RKTKVEntry(key, value, cc)

        fun <A: Comparable<A>, B: Any> ofp (p: Pair<A, B>): TKVEntry<A, B> = p.toTKVEntry()
        fun <A: Comparable<A>, B: Any> ofpc (p: Pair<A, B>, cc: Comparator<A>): TKVEntry<A, B> = p.toTKVEntry(cc)

        fun <A: Comparable<A>, B: Any> ofme (me: Map.Entry<A, B>): TKVEntry<A, B> = if(me is TKVEntry<A,B>) me else ofkv(me.key, me.value)
        fun <A: Comparable<A>, B: Any> ofmec (me: Map.Entry<A, B>, cc: Comparator<A>): TKVEntry<A, B> = ofkvc(me.key, me.value, cc)

        fun <B: Any> ofIntKey (item: B): RTKVEntry<Int, B> = when (item) {
            is Int -> @Suppress("UNCHECKED_CAST") (RKTKVEntry( /* weird, but it placates the compiler */ item as Int, item) as RTKVEntry<Int, B>)
            else -> RITKVEntry(item.hashCode(), item)
        }
        fun <B: Any> ofStrKey (item: B): RTKVEntry<String, B> = when (item) {
            is String -> RKTKVEntry(item, item)
            else -> RSTKVEntry(item.toString(), item)
        }

        fun <B> ofk (item: B): RTKVEntry<B, B> where B: Any, B: Comparable<B> = RKTKVEntry(item, item)
        fun <T: Any> T.toIAEntry(): RTKVEntry<Int, T> = ofIntKey(this)
        fun <T: Any> T.toSAEntry(): RTKVEntry<String, T> = ofStrKey(this)
        fun <T> T.toKKEntry(): RTKVEntry<T, T> where T: Any, T: Comparable<T> = ofk(this)
        fun <T: Any> T.toEntry(rkt: RestrictedKeyType<T>): TKVEntry<*, T>? = when (rkt) {
            is IntKeyType -> this.toIAEntry()
            is StrKeyType -> this.toSAEntry()
            is SymKeyType -> {
                @Suppress("UNCHECKED_CAST") (this as Comparable<T>)
                this.toKKEntry()
            }
            is DeratedCustomKeyType -> null
        }
        fun <B: Any> intKeyOf(item: B): Int = item.hashCode()
        fun <B: Any> strKeyOf(item: B): String = item.toString()
        fun <B: Any> IMList<B>.toIAEntries(): IMList<RTKVEntry<Int, B>> = this.fmap { a -> ofIntKey(a) }
        fun <B: Any> IMList<B>.toSAEntries(): IMList<RTKVEntry<String, B>> = this.fmap { a -> ofStrKey(a) }
        fun <B> IMList<B>.toKKEntries(): IMList<RTKVEntry<B, B>> where B: Any, B: Comparable<B> = this.fmap { a -> ofk(a) }
    }
}

interface RTKVEntry<out A, out B: Any>: TKVEntry<A, B> where A: Any, A: Comparable<@UnsafeVariance A> {
    fun getrk(): RestrictedKeyType<A>
}

internal sealed class TKVEntryType <A: Comparable<A>, B:Any> constructor (val k: A, val v: B, override val vc: Comparator<@UnsafeVariance A>? = null): TKVEntry<A,B> {

    override fun toString(): String = "[ $k:$v ]"

    override fun hashCode(): Int = k.hashCode()

    protected val kClass: KClass<out A> by lazy { k::class }
    protected val vClass: KClass<out B> by lazy { v::class }

    override fun equal(other: TKVEntry<A,B>?): Boolean = when {
        this === other -> true
        other == null -> false
        else -> 0 == other.compareTo(this)
    }

    override fun strictly(other: TKVEntry<A,B>?): Boolean = when {
        this === other -> true
        other == null -> false
        other !is TKVEntryType<*,*> -> false
        kClass != other.getkKc() -> false
        vClass != other.getvKc() -> false
        else -> true
    }

    override fun strictlyLike(sample: KeyedTypeSample<KClass<@UnsafeVariance A>?, KClass<@UnsafeVariance B>>?): Boolean = sample?.let {
        sample.isLikeIfLooselyKey(kClass, vClass)
    } ?: false

    val strictness: Boolean by lazy { if (!FT.isContainer(getv())) true else {
        getv().toUCon()!!.let { uc -> if (uc.isEmpty()) true else when {
            uc.isNested()!! -> uc.isStrictLike(KeyedTypeSample(uc.kKc()?.let { @Suppress("UNCHECKED_CAST")(it as KClass<Any>) }, @Suppress("UNCHECKED_CAST")(uc.vKc()!! as KClass<Any>))) ?: false
            uc.isStrictInternally() -> true
            else -> false
        }}
    }}

    override fun isStrict(): Boolean = strictness

    private fun equalsImpl(other: Any?): Boolean =
        when {
            this === other -> true
            other == null -> false
            other !is TKVEntryType<*,*> -> false
            kClass != other.kClass -> false
            vClass != other.vClass -> false
            else -> 0 == @Suppress("UNCHECKED_CAST")(other as TKVEntryType<A, B>).compareTo(this)
        }

    override fun equals(other: Any?): Boolean = equalsImpl(other)

    override fun getk(): A = k
    override fun getkKc(): KClass<out A> = kClass
    override fun getv(): B = v
    override fun getvKc(): KClass<out B> = vClass
    override fun copy(): TKVEntry<A, B> = when(this) {
        is RITKVEntry -> @Suppress("UNCHECKED_CAST") (RITKVEntry(k, v, vc) as TKVEntry<A, B>)
        is RSTKVEntry -> @Suppress("UNCHECKED_CAST") (RSTKVEntry(k, v, vc) as TKVEntry<A, B>)
        is RKTKVEntry<*, *> -> {
            check(k == v)
            @Suppress("UNCHECKED_CAST") (RKTKVEntry(k, k, vc) as TKVEntry<A, B>)
        }
        else -> TKVEntryK(k, v, vc)
    }
    override val key: A
        get() = k
    override val value: B
        get() = v

    // IMCollection

    override val seal: IMSC = IMSC.IMENTRY

    override fun fcount(isMatch: (TKVEntry<A, B>) -> Boolean): Int = if (isMatch(this)) 1 else 0

    override fun fcontains(item: TKVEntry<A, B>): Boolean = this == item

    override fun ffilter(isMatch: (TKVEntry<A, B>) -> Boolean): IMCollection<TKVEntry<A, B>> = TODO()

    override fun ffilterNot(isMatch: (TKVEntry<A, B>) -> Boolean): IMCollection<TKVEntry<A, B>> = TODO()

    override fun ffindAny(isMatch: (TKVEntry<A, B>) -> Boolean): TKVEntry<A, B>? = if (isMatch(this)) this else null

    override fun fisStrict(): Boolean = strictness

    override fun fpick(): TKVEntry<A, B>? = this

    override fun fsize(): Int = 1

    companion object {
        const val CANNOT_COMPARE = "incompatible comparators"
    }
}


internal open class TKVEntryK<A: Comparable<A>, B:Any> constructor (
    k: A, v: B, vc: Comparator<@UnsafeVariance A>? = null
): TKVEntryType<A,B>(k, v, vc) {
    init {
        check(k::class != v::class)
    }
}

internal class RITKVEntry<A: Any> constructor (
    rk: Int, v: A, vc: Comparator<@UnsafeVariance Int>? = null
): TKVEntryType<Int,A>(rk,v, vc), RTKVEntry<Int, A>{
    init {
        check(v !is Int)
        check(k::class != v::class)
    }
    override fun getrk() = IntKeyType
}

internal class RSTKVEntry<A: Any> constructor (
    rk: String, v: A, vc: Comparator<@UnsafeVariance String>? = null
): TKVEntryType<String,A>(rk,v, vc), RTKVEntry<String, A>{
    init {
        check(v !is String)
        check(k::class != v::class)
    }
    override fun getrk() = StrKeyType
}

// DUMMY is workaround for https://discuss.kotlinlang.org/t/problem-with-generic-capturedtype/17069
internal class RKTKVEntry<A, DUMMY> constructor (
    rk: A, v: A, vc: Comparator<@UnsafeVariance A>? = null
): TKVEntryType<A,A>(rk, v, vc), RTKVEntry<A,A> where A: Any, A: Comparable<A>, DUMMY: Any, DUMMY: Comparable<A> {
    init {
        check(k::class == v::class)
    }
    val rkt = SymKeyType(kClass)
    override fun getrk() = rkt
}