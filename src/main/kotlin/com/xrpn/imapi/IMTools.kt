package com.xrpn.imapi

import mu.KLogger
import mu.KotlinLogging
import java.io.PrintStream
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.Logger.getLogger
import kotlin.reflect.KClass

private fun <A, B> isSameType(a: A, b: B): Boolean = a?.let{ outer: A -> outer!!::class == b?.let{ it::class } } ?: false
private fun <A, B: Any> isSameTypeLike(a: A, b: KClass<B>): Boolean = a?.let{ outer: A -> outer!!::class == b } ?: false
private fun <A: Any, B> isLikeSameType(a: KClass<A>, b: B): Boolean = a == b?.let{ it::class }
private fun <A: Any, B: KClass<Any>> isLikeSameTypeLike(a: KClass<A>, b: B): Boolean = a == b

fun <A, B> A.isStrictly(b: B): Boolean = when(this) {
    is KClass<*> -> when(b) {
        is KClass<*> -> isLikeSameTypeLike(this, @Suppress("UNCHECKED_CAST") (b as KClass<Any>))
        else -> isLikeSameType(this, b)
    }
    else -> when(b) {
        is KClass<*> -> isSameTypeLike(this, b)
        else -> isSameType(this, b)
    }
}

fun <A, B> A.isStrictlyNot(b: B): Boolean = when(this) {
    is KClass<*> -> when(b) {
        is KClass<*> -> !isLikeSameTypeLike(this, @Suppress("UNCHECKED_CAST") (b as KClass<Any>))
        else -> !isLikeSameType(this, b)
    }
    else -> when(b) {
        is KClass<*> -> {
            val foo = !isSameTypeLike(this, b)
            foo }
        else -> !isSameType(this, b)
    }
}

data class KeyedTypeSample<K: KClass<*>?, V: KClass<*>>(val kKc: K, val vKc: V) {
    fun hasKey(): Boolean = null != kKc
    fun isSymRkc(): Boolean = hasKey() && kKc == vKc
    fun isIntRkc(): Boolean = hasKey() && kKc == Int::class
    fun isStrRkc(): Boolean = hasKey() && kKc == String::class
    fun isLikeIfLooselyKey(kClass: KClass<*>?, vClass: KClass<*>): Boolean =( !hasKey() || kKc == kClass ) && vKc == vClass
    fun isLike(kClass: KClass<*>?, vClass: KClass<*>): Boolean = kKc == kClass && vKc == vClass
    fun <KK: KClass<*>?, VV: KClass<*>> isStrictly(other: KeyedTypeSample<KK, VV>): Boolean = (kKc == other.kKc && vKc == other.vKc)
    fun isLikeKey(kClass: KClass<*>?): Boolean = kKc == kClass
    fun isLikeValue(vClass: KClass<*>): Boolean = vKc == vClass
}

fun <T: Any> IMCommon<T>?.toIMMapplicable(): FMapp<T>? =
    this?.let { IMMappOp.flift2mapp(it) }

object IM {

    fun <S: Any, T: Any> fmapOfCommon(
        u: FMap<S>,
    ): (IMCommon<(S) -> T>) -> FMap<T> = { g: IMCommon<(S) -> T> -> u.fmap(g.fpick()!!) }

    fun <B: Any> liftToIMMappable(item: IMCommon<B>): IMMapOp<B, IMCommon<B>>? = when(item) {
        is IMList -> item
        is IMSet -> item.asIMRSetNotEmpty()?.let { it -> it.sxdj().bireduce(
            { id -> id },
            { id -> @Suppress("UNCHECKED_CAST") (id as IMSet<B>) })
        } ?: item
        is IMStack -> item
        is IMQueue -> item
        is IMBTree<*, *> -> TODO()
        is IMMap<*, *> -> TODO()
        is IMHeap -> item
        is IMDisw<B, IMCommon<B>> -> DMW.of(item)
        is IMDimw<B, IMCommon<B>> -> item
        is IMSdj<*,*> -> @Suppress("UNCHECKED_CAST") (item as IMMapOp<B, IMCommon<B>>)
        else -> if (!(IMMapOp::class.isInstance(item))) null
                else throw RuntimeException("internal error, unknown IMCommon:'${item::class}'")

    }

    fun <A: Any> liftToIMMapplicable(item: IMMapOp<A, IMCommon<A>>): IMMappOp<A, IMMapOp<A, IMCommon<A>>>? = when(item) {
        is IMList -> item
        is IMSet -> item
        is IMStack -> item
        is IMQueue -> item
        is IMHeap -> item
        is IMBTree<*, *> -> TODO()
        is IMMap<*, *> -> TODO()
        is IMDimw<A, IMCommon<A>> -> DAW.of(item)
        is IMSdj<*,*> -> @Suppress("UNCHECKED_CAST") (item as IMMappOp<A, IMMapOp<A, IMCommon<A>>>)
        else -> if (!(IMMappOp::class.isInstance(item))) null
                else throw RuntimeException("internal error, unknown IMMappable:'${item::class}'")
    }
}

interface IMLogging<out A>  {
    val logger: A
    fun emitUnconditionally(msg: String)
    fun emitUnconditionally(e: Exception)
}

data class IMSimpleLogging(val forClass: KClass<*>): IMLogging<Logger> {
    override fun emitUnconditionally(msg: String) = if(logger.isLoggable(Level.SEVERE)) logger.severe(msg) else System.err.println(msg)
    override fun emitUnconditionally(e: Exception) = if(logger.isLoggable(Level.SEVERE)) logger.log(Level.SEVERE, "emitUnconditionally", e) else e.printStackTrace(System.err)
    override val logger: Logger by lazy { getEngine() }
    private fun getEngine(): Logger = getLogger(forClass.qualifiedName ?: "<local or anonymous>")
}

data class IMKLogging(val forClass: KClass<*>): IMLogging<KLogger> {
    override fun emitUnconditionally(msg: String) = if(logger.isErrorEnabled) logger.error(msg) else System.err.println(msg)
    override fun emitUnconditionally(e: Exception) = if(logger.isErrorEnabled) logger.error("emitUnconditionally", e) else e.printStackTrace(System.err)
    override val logger: KLogger by lazy { getEngine() }
    private fun getEngine(): KLogger = KotlinLogging.logger(forClass.qualifiedName ?: "<local or anonymous>")
}

data class IMSystemErrLogging(val forClass: KClass<*>): IMLogging<PrintStream> {
    override fun emitUnconditionally(msg: String) = System.err.println(msg)
    override fun emitUnconditionally(e: Exception) = e.printStackTrace(System.err)
    override val logger: PrintStream by lazy { getEngine() }
    private fun getEngine(): PrintStream {
        System.err.println("LOGGING FOR ${forClass.qualifiedName ?: "<local or anonymous>"} WILL GO TO STANDARD ERROR STREAM")
        return System.err
    }
}
