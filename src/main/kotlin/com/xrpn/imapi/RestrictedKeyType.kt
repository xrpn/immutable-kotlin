package com.xrpn.imapi

import kotlin.reflect.KClass

sealed class RestrictedKeyType<out K>(kc: KClass<K>) where K: Any, K: Comparable<@UnsafeVariance K> {
    val show: String = kc::class.simpleName!!
}

object IntKeyType: RestrictedKeyType<Int>(Int::class)

object StrKeyType: RestrictedKeyType<String>(String::class)
