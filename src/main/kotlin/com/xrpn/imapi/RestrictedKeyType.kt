package com.xrpn.imapi

import kotlin.reflect.KClass

sealed class RestrictedKeyType<out K: Any> constructor (open val kc: KClass<@UnsafeVariance K>)

object IntKeyType: RestrictedKeyType<Int>(Int::class) {
    fun <A: Any> asRestrictedKeyType(): RestrictedKeyType<A>? = @Suppress("UNCHECKED_CAST") (this as? RestrictedKeyType<A>)
}

object StrKeyType: RestrictedKeyType<String>(String::class) {
    fun <A: Any> asRestrictedKeyType(): RestrictedKeyType<A>? = @Suppress("UNCHECKED_CAST") (this as? RestrictedKeyType<A>)
}

data class SymKeyType<out T>(override val kc: KClass<@UnsafeVariance T>): RestrictedKeyType<T>(kc) where T: Any, T: Comparable<@UnsafeVariance T>

internal data class DeratedCustomKeyType<out T: Any>(override val kc: KClass<@UnsafeVariance T>): RestrictedKeyType<T>(kc) {
    // type erasure can lose Comparable<*>
    fun <A> sameAs(rkt: RestrictedKeyType<A>): Boolean where A: Any, A: Comparable<A> = kc == rkt.kc
    fun <KK> specialize(): RestrictedKeyType<KK>? where KK: Comparable<KK> = Companion.specialize(this)
    companion object {
        fun <KK, T: Any> specialize(dkt: DeratedCustomKeyType<T>): RestrictedKeyType<KK>? where KK: Comparable<KK> = when {
            dkt.sameAs(IntKeyType) -> IntKeyType.asRestrictedKeyType()
            dkt.sameAs(StrKeyType) -> StrKeyType.asRestrictedKeyType()
            else -> if (Comparable::class.isInstance(dkt::kc)) @Suppress("UNCHECKED_CAST") (SymKeyType(dkt.kc as KClass<KK>))
            else null
        }
    }
}