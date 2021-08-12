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
