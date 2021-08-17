package com.xrpn.immutable

import kotlinx.coroutines.*
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {
    override val coroutineContext: CoroutineContext = context
    override fun close() {
        coroutineContext.cancel()
    }
}

fun uncaughtHandler(msg: String): CoroutineExceptionHandler = CoroutineExceptionHandler { coroutineCtx, ex -> when (ex) {
    is CancellationException -> listOf(
            msg,
            "job ${coroutineCtx.job.key} was cancelled"
        ).joinToString { "\n" }
    else -> listOf(
            msg,
            "${coroutineCtx.job.key} failure: $ex",
            "${coroutineCtx.job.key} failure details ${ex.stackTraceToString()}"
        ).joinToString { "\n" }
}}

fun ioScope(logTag: String): CoroutineScope = CloseableCoroutineScope(
    SupervisorJob() + Dispatchers.IO + uncaughtHandler(logTag)
)

fun defaultScope(logTag: String): CoroutineScope = CloseableCoroutineScope(
    SupervisorJob() + Dispatchers.Default + uncaughtHandler(logTag)
)

fun uiScope(logTag: String): CoroutineScope = CloseableCoroutineScope(
    SupervisorJob() + Dispatchers.Main + uncaughtHandler(logTag)
)

//abstract class DeepRecursiveScope<T, R> {
//    abstract suspend fun callRecursive(value: T): R
//}
//
//class DeepRecursiveFunction<T, R>(
//    val block: suspend DeepRecursiveScope<T, R>.(T) -> R
//)


//typealias Result = Pair<KFunction<*>?, Any?>
//typealias Func = KFunction<Result>
//
//tailrec fun trampoline(f: Func, arg: Any?): Any? {
//    val (f2,arg2) = f.call(arg)
//    @Suppress("UNCHECKED_CAST")
//    return if (f2 == null) arg2
//    else trampoline(f2 as Func, arg2)
//}
//
//fun odd(n: Int): Result =
//    if (n == 0) null to false
//    else ::even to n-1
//
//fun even(n: Int): Result =
//    if (n == 0) null to true
//    else ::odd to n-1
//
//fun mutualrecursion() {
//    System.out.println(trampoline(::even, 9999999))
//}
