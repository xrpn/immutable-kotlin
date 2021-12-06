package com.xrpn.immutable

import com.xrpn.imapi.*
import mu.KLogger
import mu.KotlinLogging
import java.io.PrintStream
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.Logger.getLogger
import kotlin.reflect.KClass

internal object FT {

    infix fun <B, C, A> ((B) -> C).kompose(f: (A) -> B): (A) -> C = { a: A -> this(f(a)) }

    fun <A : Any> isContainer(c: A): Boolean = when (c) {
        is TSDJ<*,*> -> c.left()?.let { isContainer(it) } ?: isContainer(c.right()!!)
        is TKVEntry<*,*> -> isContainer(c.getv())
        is IMCommon<*> -> true
        is Collection<*> -> true
        is Map<*, *> -> true
        is Array<*> -> true
        is UCon<*, *, *, *, *, *, *> -> throw RuntimeException("internal error, UniContainer $c tested for containment")
        else -> false
    }

    fun <A : Any> isNested(c: A): Boolean? = when (c) {
        is IMCommon<*> -> c.fisNested()
        is Collection<*> -> isNestedCollection(c)
        is Map<*, *> -> {
            val aux: Map<Nothing, Any> = @Suppress("UNCHECKED_CAST") (c as Map<Nothing, Any>)
            isNestedMap(aux)
        }
        is Array<*> -> isNestedArray(c)
        is TKVEntry<*,*> -> isContainer(c.getv())
        is UCon<*, *, *, *, *, *, *> -> throw RuntimeException("internal error, UniContainer $c tested for nesting")
        else -> false
    }

    fun <A> isEmpty(item: A): Boolean? = item?.let {
        if (!isContainer(it)) null else item.toUCon()?.let { it.isEmpty() } ?:
        throw RuntimeException("internal error: unknown container $it:[${it::class}]")
    }

    internal fun <C : Any> filterNotEmpty(item: UniContainer<C>?): UniContainer<C>? = item?.let {
        if (it.imcoll()?.fempty() == false) {
            val aux: IMCommon<C> = it.imcoll()!!.ffilterNot { maybe: C -> UCon.of(maybe)?.isEmpty() ?: false }
            UCIMC(aux).asUC()
        } else if (it.kcoll()?.isEmpty() == false) {
            val aux: List<C> = it.kcoll()!!.filterNotNullTo(ArrayList(it.kcoll()!!.size))
            aux.filterNotTo(ArrayList(aux.size)) { maybe: C -> UCon.of(maybe)?.isEmpty() ?: false }
            UCKC(aux).asUC()
        } else if (it.kmap()?.isEmpty() == false) {
            val aux: Map<Nothing, C> =
                it.kmap()!!.filterNot { maybe: Map.Entry<Nothing, C> -> UCon.of(maybe.value)?.isEmpty() ?: false }
            UCKMAP(aux).asUC()
        } else if (it.karyl()?.isEmpty() == false) {
            val aux: ArrayList<C> = it.karyl()!!.filterNotNullTo(ArrayList(it.karyl()!!.size))
            aux.filterNotTo(ArrayList()) { maybe: C -> UCon.of(maybe)?.isEmpty() ?: false }
            UCARYL(aux).asUC()
        } else null
    }

    internal fun <C : Any> filterNotEmptyTerminally(item: UniContainer<C>?): UniContainer<C>? = item?.let {
        if (it.imcoll()?.fempty() == false) {
            val aux: IMCommon<C> = it.imcoll()!!.ffilterNot { maybe: C ->
                UCon.of(maybe)?.let { known ->
                    known.isEmpty() || filterNotEmptyTerminally(known)?.isEmpty() ?: false
                } ?: false
            }
            UCIMC(aux).asUC()
        } else if (it.kcoll()?.isEmpty() == false) {
            val aux: List<C> = it.kcoll()!!.filterNotNullTo(ArrayList(it.kcoll()!!.size))
            aux.filterNotTo(ArrayList(aux.size)) { maybe: C ->
                UCon.of(maybe)?.let { known ->
                    known.isEmpty() || filterNotEmptyTerminally(known)?.isEmpty() ?: false
                } ?: false
            }
            UCKC(aux).asUC()
        } else if (it.kmap()?.isEmpty() == false) {
            val aux: Map<Nothing, C> = it.kmap()!!.filterNot { maybe: Map.Entry<Nothing, C> ->
                UCon.of(maybe.value)?.let { known ->
                    known.isEmpty() || filterNotEmptyTerminally(known)?.isEmpty() ?: false
                } ?: false
            }
            UCKMAP(aux).asUC()
        } else if (it.karyl()?.isEmpty() == false) {
            val aux: ArrayList<C> = it.karyl()!!.filterNotNullTo(ArrayList(it.karyl()!!.size))
            aux.filterNotTo(ArrayList()) { maybe: C ->
                UCon.of(maybe)?.let { known ->
                    known.isEmpty() || filterNotEmptyTerminally(known)?.isEmpty() ?: false
                } ?: false
            }
            UCARYL(aux).asUC()
        } else null
    }


    internal fun <A:Any> itemStrictness(
        item: A,
        vKc: KClass<out A>,
        ucKc: SingleInit<KeyedTypeSample< /* key */ KClass<Any>?, /* value */ KClass<Any>>>
    ): Boolean {
        val strict = item.isStrictly(vKc)
        val stricture = strict && if (isContainer(item)) {
            item.toUCon()?.let { uc -> uc.stricture(ucKc) } ?:
            throw RuntimeException("internal error: unknown nested $item:[${item::class}]")
        } else true
        return stricture
    }

    internal fun <A, B:Any> entryStrictness(
        tkv: TKVEntry<A, B>,
        tkvClass: KClass<*>,
        vKc: KClass<out B>,
        ucKc: SingleInit<KeyedTypeSample< /* key */ KClass<Any>?, /* value */ KClass<Any>>>
    ): Boolean where A: Any, A: Comparable<A> {
        val strictTkv = tkv.isStrictly(tkvClass)
        val strictv = strictTkv && tkv.getvKc().isStrictly(vKc)
        val stricture = strictv && if (tkv.fisNested()!!) {
            tkv.toUCon()?.let { uc -> uc.stricture(ucKc) } ?:
            throw RuntimeException("internal error: unknown nested $tkv:[${tkv.getv()::class}]")
        } else tkv.fisStrict()
        return stricture
    }

    internal fun <A> strictly(item: A, kc: KClass<*>): Boolean = item?.let {
        val predicate = when (it) {
            is IMKSet<*,*> -> it.fempty() || (it.isStrictly(kc) && it.fisStrict())
            is IMCommon<*> -> it.fempty() || (it.isStrictly(kc) && it.fisStrict())
            is Collection<*> -> it.isEmpty() || (it.isStrictly(kc) && isStrictCollection(it))
            is Map<*, *> -> it.isEmpty() || (it.isStrictly(kc) && isStrictMap(it))
            is Array<*> -> it.isEmpty() || (it.isStrictly(kc) && isStrictArray(it))
            is UCon<*, *, *, *, *, *, *> -> throw RuntimeException("internal error")
            else -> it.isStrictly(kc)
        }
        return predicate
    } ?: false

    fun <A> isStrictCollection(c: Collection<A>): Boolean = if (c.isEmpty()) true else run {
        val kc: KClass<*> = c.firstNotNullOfOrNull { it }?.let { it::class } ?: return false
        val ucKc = SingleInit<KeyedTypeSample< /* key */ KClass<Any>?, /* value */ KClass<Any>>>()
        return c.all { it?.let { item ->
            val strict = strictly(item, kc)
            strict && (!isContainer(item as Any) ||
                (item.toUCon()?.let { uc -> uc.stricture(ucKc) } ?:
                throw RuntimeException("internal error: unknown nested $item:[$kc]"))
            )
        } ?: false }
    }

    fun <A, B:Any> isStrictCollectionOf(c: Collection<A>, kc: KClass<B>): Boolean = if (c.isEmpty()) true else run {
        val cKc: KClass<*> = c.firstNotNullOfOrNull { it }?.let { it::class } ?: return false
        val ucKc = SingleInit<KeyedTypeSample< /* key */ KClass<Any>?, /* value */ KClass<Any>>>()
        return (cKc == kc) && c.all { it?.let { item: A ->
            val strict = strictly(item, cKc)
            strict && (!isContainer(item as Any) ||
               (item.toUCon()?.let { uc -> uc.stricture(ucKc) } ?:
                throw RuntimeException("internal error: unknown nested $item:[$kc]"))
            )
        } ?: false }
    }

    fun <K, V> isStrictMap(m: Map<K, V>): Boolean = if (m.isEmpty()) true else run {
        val maybeKvc: Pair<KClass<*>?, KClass<*>?> =
            m.firstNotNullOfOrNull { it }?.let { kv -> Pair(kv.key?.let { it::class }, kv.value?.let { it::class }) }
            ?: return false
        val kvc: Pair<KClass<*>, KClass<*>> =
            maybeKvc.first?.let { frst -> maybeKvc.second?.let { scnd -> Pair(frst, scnd) } ?: return false }
            ?: return false
        val ucKc = SingleInit<KeyedTypeSample< /* key */ KClass<Any>?, /* value */ KClass<Any>>>()
        return m.all { item: Map.Entry<K, V> ->
            val strict = strictly(item.key, kvc.first) && strictly(item.value, kvc.second)
            val v = @Suppress("UNCHECKED_CAST") (item.value as Any)
            strict && (!isContainer(v) || (v.toUCon()?.let {
                    uc -> uc.stricture(ucKc)
            } ?: throw RuntimeException("internal error: unknown nested $item:[$kvc]")))
        }
    }

    fun <K, V, KK: Any, VV: Any> isStrictMapOf(m: Map<K, V>, kKc: KClass<KK>, vKc: KClass<VV>): Boolean = if (m.isEmpty()) true else run {
        val maybeKvc: Pair<KClass<*>?, KClass<*>?> =
            m.firstNotNullOfOrNull { it }?.let { kv -> Pair(kv.key?.let { it::class }, kv.value?.let { it::class }) }
                ?: return false
        val kvc: Pair<KClass<*>, KClass<*>> = maybeKvc.first?.let { frst -> maybeKvc.second?.let { scnd -> Pair(frst, scnd) } ?: return false }
            ?: return false
        val ucKc = SingleInit<KeyedTypeSample< /* key */ KClass<Any>?, /* value */ KClass<Any>>>()
        return (kvc.first == kKc) && (kvc.second == vKc) && m.all { item: Map.Entry<K, V> ->
            val strict = strictly(item.key, kvc.first) && strictly(item.value, kvc.second)
            val v = @Suppress("UNCHECKED_CAST") (item.value as Any)
            strict && (!isContainer(v) || (v.toUCon()?.let {
                    uc -> uc.stricture(ucKc)
            } ?: throw RuntimeException("internal error: unknown nested $item:[$kvc]")))
        }
    }

    fun <A> isStrictArray(a: Array<A>): Boolean = isStrictCollection(a.asList())

    fun <A> isNestedCollection(c: Collection<A>): Boolean? = if (c.isEmpty()) null else
        c.all { it?.let { item -> isContainer(item) } ?: false }

    fun <A : Any> isNestedMap(c: Map<Nothing, A>): Boolean? = if (c.isEmpty()) null else
        c.all { it.let { item -> isContainer(item.value) } }

    fun <A> isNestedArray(a: Array<A>): Boolean? = isNestedCollection(a.asList())

}

fun <A, B> Pair<A, A>.pmap1(f: (A) -> B): Pair<B, B> = Pair(f(this.first), f(this.second))
fun <A, B, C, D> Pair<A, B>.pmap2(f: (A) -> C, g: (B) -> D): Pair<C, D> = Pair(f(this.first), g(this.second))
fun <A: Any> Pair<A, A>.toIMList() = FLCons(this.first, FLCons(this.second, FLNil))
fun <K, A: Any> Pair<K, A>.toTKVEntry(): TKVEntry<K, A> where K: Any, K: Comparable<K> = TKVEntry.ofkv(this.first, this.second)
fun <K, A: Any> Pair<K, A>.toTKVEntry(cc: Comparator<K>): TKVEntry<K, A> where K: Any, K: Comparable<K> = TKVEntry.ofkvc(this.first, this.second, cc)
fun <A, B> Pair<A, B>.fne() = this.first?.let { it } ?: this.second!!

fun <A, B> Triple<A, A, A>.tmap1(f: (A) -> B): Triple<B, B, B> = Triple(f(this.first), f(this.second), f(this.third))
fun <A, B, C, D, E, F> Triple<A, B, C>.tmap3(f: (A) -> D, g: (B) -> E, h: (C) -> F): Triple<D, E, F> = Triple(f(this.first), g(this.second), h(this.third))
fun <A: Any> Triple<A, A, A>.toIMList() = FLCons(this.first, FLCons(this.second, FLCons(this.third, FLNil)))

fun interface EqualsProxy {
    override fun equals(other: Any?): Boolean
}

fun interface HashCodeProxy {
    override fun hashCode(): Int
}

internal enum class FBTFIT {
    LEFT, RIGHT, EQ
}

class SingleInit<T: Any>() {
    private var inner: T? = null
    fun init(t: T): Boolean = inner?.let { false } ?: run {
        inner = t
        return true
    }
    fun initAndGet(t: T): T = inner ?: run{
        inner = t
        return inner!!
    }
    fun isInit(): Boolean = inner != null
    fun isNotInit(): Boolean = inner == null
    fun getOrThrow(): T = inner!!
    fun get(): T? = inner
    fun equal(rhs: SingleInit<T>) = inner == rhs.inner
    override fun toString() =
        "${this::class.simpleName}(${inner?.let{ it.toString() } ?: '?'.toString() })"
}

fun <C: Comparator<C>> toComparable(c: C): Comparable<C> = object : Comparable<C>  {
    val proxy = object : Comparable<C> {
        override fun compareTo(other: C): Int = c.compare(c, other)
    }
    override fun compareTo(other: C): Int = proxy.compareTo(other)
}

fun <C: Comparator<C>> C.compareTo(other: C): Int = toComparable(this).compareTo(other)


class TimingDynamicInvocationHandler<C: Any>(private val target: C) : InvocationHandler {
    private val methods: MutableMap<String, Method> = HashMap()
    private val logger: KLogger = KotlinLogging.logger("TimingProxy(${target::class.simpleName!!})")

    @Throws(Throwable::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any {
        val start = System.nanoTime()
        val result = methods[method.name]!!.invoke(target, *args)
        val elapsed = System.nanoTime() - start
        logger.info("Executing {} finished in {} ns", method.name, elapsed)
        return result
    }

    init {
        for (method in target.javaClass.declaredMethods) {
            methods[method.name] = method
        }
    }
}

fun <C: Any> timingProxy(c: C): C {
    val aux = TimingDynamicInvocationHandler(c)
    val res = Proxy.newProxyInstance(
        c::class.java.getClassLoader(),
        arrayOf<Class<*>>(c::class.java),
        aux
    )
    @Suppress("UNCHECKED_CAST") (res as C)
    return res
}

