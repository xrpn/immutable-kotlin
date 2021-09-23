package com.xrpn.hash

class HashFast {

    // after https://lemire.me/blog/2018/08/15/fast-strongly-universal-64-bit-hashing-everywhere/

    fun murmur(): Long {
        var answer: Long = 0
        for (x in 0..99999) {
            answer += murmur64(x.toLong())
        }
        return answer
    }

    fun murmur_32(): Int {
        var answer1 = 0
        var answer2 = 0
        for (x in 0..99999) {
            val h = murmur64(x.toLong())
            answer1 += h.toInt()
            answer2 += (h ushr 32).toInt()
        }
        return answer1 + answer2
    }

    fun fast2_32(): Int {
        var answer1 = 0
        var answer2 = 0
        for (x in 0..99999) {
            answer1 += hash32_1(x.toLong())
            answer2 += hash32_2(x.toLong())
        }
        return answer1 + answer2
    }

    fun fast64(): Long {
        var answer: Long = 0
        for (x in 0..99999) {
            answer += hash64(x.toLong())
        }
        return answer
    }

    companion object {
        var a1 = 0x65d200ce55b19ad8L
        var b1 = 0x4f2162926e40c299L
        var c1 = 0x162dd799029970f8L
        var a2 = 0x68b665e6872bd1f4L
        var b2 = -0x49303062864ae24eL
        var c2 = 0x7a2b92ae912898c2L

        fun hash32_1(x: Long): Int {
            val low = x.toInt()
            val high = (x ushr 32).toInt()
            return (a1 * low + b1 * high + c1 ushr 32).toInt()
        }

        fun hash32_2(x: Long): Int {
            val low = x.toInt()
            val high = (x ushr 32).toInt()
            return (a2 * low + b2 * high + c2 ushr 32).toInt()
        }

        fun hash64(x: Long): Long {
            val low = x.toInt()
            val high = (x ushr 32).toInt()
            return ((a1 * low + b1 * high + c1 ushr 32) or (a2 * low + b2 * high + c2 and -0x100000000L))
        }

        fun murmur64(hIn: Long): Long = DigestHash.mrmr64(hIn)

        @JvmStatic
        fun main(args: Array<String>) {

        }
    }
}

interface MockMap<K, V>
interface MockMMap<K, V>

/* Notepad, TODO wipe when done
internal fun <K, V> MockMap<K, V>.getOrImplicitDefault(key: K): V = TODO()

fun <K, V> MockMap<K, V>.withDefault(defaultValue: (K) -> V): MockMap<K, V> = TODO()

fun <K, V> MockMMap<K, V>.withDefault(defaultValue: (K) -> V): MockMMap<K, V> = TODO()

// private const val INT_MAX_POWER_OF_TWO: kotlin.Int /* compiled code */

fun <K, V> sortedMapOf(comparator: java.util.Comparator<in K>, vararg pairs: kotlin.Pair<K, V>): java.util.SortedMap<K, V> = TODO()

fun <K : kotlin.Comparable<K>, V> sortedMapOf(vararg pairs: kotlin.Pair<K, V>): java.util.SortedMap<K, V> = TODO()

inline fun <K, V> java.util.concurrent.ConcurrentMap<K, V>.getOrPut(key: K, defaultValue: () -> V): V = TODO()

fun kotlin.collections.Map<kotlin.String, kotlin.String>.toProperties(): java.util.Properties = TODO()

internal fun <K, V> kotlin.collections.Map<out K, V>.toSingletonMap(): MockMap<K, V> = TODO()

internal inline fun <K, V> MockMap<K, V>.toSingletonMapOrSelf(): MockMap<K, V> = TODO()

fun <K : kotlin.Comparable<K>, V> kotlin.collections.Map<out K, V>.toSortedMap(): java.util.SortedMap<K, V> = TODO()

fun <K, V> kotlin.collections.Map<out K, V>.toSortedMap(comparator: java.util.Comparator<in K>): java.util.SortedMap<K, V> = TODO()

operator fun <K, V> kotlin.collections.Map.Entry<K, V>.component1(): K = TODO()

operator fun <K, V> kotlin.collections.Map.Entry<K, V>.component2(): V = TODO()

// ====== operator fun <K, V> kotlin.collections.Map<out K, V>.contains(key: K): kotlin.Boolean = TODO()

//fun <K> kotlin.collections.Map<out K, *>.containsKey(key: K): kotlin.Boolean = TODO()

//fun <K, V> MockMap<K, V>.containsValue(value: V): kotlin.Boolean = TODO()

//fun <K, V> kotlin.collections.Map<out K, V>.filter(predicate: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Boolean): MockMap<K, V> = TODO()

//fun <K, V> kotlin.collections.Map<out K, V>.filterKeys(predicate: (K) -> kotlin.Boolean): MockMap<K, V> = TODO()

//fun <K, V> kotlin.collections.Map<out K, V>.filterNot(predicate: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Boolean): MockMap<K, V> = TODO()

fun <K, V, M : kotlin.collections.MutableMap<in K, in V>> kotlin.collections.Map<out K, V>.filterNotTo(destination: M, predicate: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Boolean): M = TODO()

fun <K, V, M : kotlin.collections.MutableMap<in K, in V>> kotlin.collections.Map<out K, V>.filterTo(destination: M, predicate: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Boolean): M = TODO()

fun <K, V> kotlin.collections.Map<out K, V>.filterValues(predicate: (V) -> kotlin.Boolean): MockMap<K, V> = TODO()

//operator fun <K, V> kotlin.collections.Map<out K, V>.get(key: K): V? = TODO()

//fun <K, V> MockMap<K, V>.getOrElse(key: K, defaultValue: () -> V): V = TODO()

//internal inline fun <K, V> MockMap<K, V>.getOrElseNullable(key: K, defaultValue: () -> V): V = TODO()

//fun <K, V> MockMMap<K, V>.getOrPut(key: K, defaultValue: () -> V): V = TODO()

//fun <K, V> MockMap<K, V>.getValue(key: K): V = TODO()

// fun <M, R> M.ifEmpty(defaultValue: () -> R): R where M : kotlin.collections.Map<*, *>, M : R = TODO()

//fun <K, V> kotlin.collections.Map<out K, V>.isNotEmpty(): kotlin.Boolean = TODO()

//fun <K, V> kotlin.collections.Map<out K, V>?.isNullOrEmpty(): kotlin.Boolean = TODO()

// operator fun <K, V> kotlin.collections.Map<out K, V>.iterator(): kotlin.collections.Iterator<kotlin.collections.Map.Entry<K, V>> = TODO()

// operator fun <K, V> MockMMap<K, V>.iterator(): kotlin.collections.MutableIterator<kotlin.collections.MutableMap.MutableEntry<K, V>> = TODO()

//fun <K, V, R> kotlin.collections.Map<out K, V>.mapKeys(transform: (kotlin.collections.Map.Entry<K, V>) -> R): kotlin.collections.Map<R, V> = TODO()

//fun <K, V, R, M : kotlin.collections.MutableMap<in R, in V>> kotlin.collections.Map<out K, V>.mapKeysTo(destination: M, transform: (kotlin.collections.Map.Entry<K, V>) -> R): M = TODO()

//fun <K, V, R> kotlin.collections.Map<out K, V>.mapValues(transform: (kotlin.collections.Map.Entry<K, V>) -> R): kotlin.collections.Map<K, R> = TODO()

//fun <K, V, R, M : kotlin.collections.MutableMap<in K, in R>> kotlin.collections.Map<out K, V>.mapValuesTo(destination: M, transform: (kotlin.collections.Map.Entry<K, V>) -> R): M = TODO()

//public operator fun <K, V> kotlin.collections.Map<out K, V>.minus(key: K): MockMap<K, V> = TODO()
//
//public operator fun <K, V> kotlin.collections.Map<out K, V>.minus(keys: kotlin.Array<out K>): MockMap<K, V> = TODO()
//
//public operator fun <K, V> kotlin.collections.Map<out K, V>.minus(keys: kotlin.collections.Iterable<K>): MockMap<K, V> = TODO()
//
//public operator fun <K, V> kotlin.collections.Map<out K, V>.minus(keys: kotlin.sequences.Sequence<K>): MockMap<K, V> = TODO()

//operator fun <K, V> MockMMap<K, V>.minusAssign(key: K): kotlin.Unit = TODO()
//
//operator fun <K, V> MockMMap<K, V>.minusAssign(keys: kotlin.Array<out K>): kotlin.Unit = TODO()
//
//operator fun <K, V> MockMMap<K, V>.minusAssign(keys: kotlin.collections.Iterable<K>): kotlin.Unit = TODO()
//
//operator fun <K, V> MockMMap<K, V>.minusAssign(keys: kotlin.sequences.Sequence<K>): kotlin.Unit = TODO()

internal fun <K, V> MockMap<K, V>.optimizeReadOnlyMap(): MockMap<K, V> = TODO()

fun <K, V> MockMap<K, V>?.orEmpty(): MockMap<K, V> = TODO()

//public operator fun <K, V> kotlin.collections.Map<out K, V>.plus(pairs: kotlin.Array<out kotlin.Pair<K, V>>): MockMap<K, V> = TODO()
//
//public operator fun <K, V> kotlin.collections.Map<out K, V>.plus(pair: kotlin.Pair<K, V>): MockMap<K, V> = TODO()
//
//public operator fun <K, V> kotlin.collections.Map<out K, V>.plus(pairs: kotlin.collections.Iterable<kotlin.Pair<K, V>>): MockMap<K, V> = TODO()
//
//public operator fun <K, V> kotlin.collections.Map<out K, V>.plus(map: kotlin.collections.Map<out K, V>): MockMap<K, V> = TODO()
//
//public operator fun <K, V> kotlin.collections.Map<out K, V>.plus(pairs: kotlin.sequences.Sequence<kotlin.Pair<K, V>>): MockMap<K, V> = TODO()
//
//operator fun <K, V> kotlin.collections.MutableMap<in K, in V>.plusAssign(pairs: kotlin.Array<out kotlin.Pair<K, V>>): kotlin.Unit = TODO()
//
//operator fun <K, V> kotlin.collections.MutableMap<in K, in V>.plusAssign(pair: kotlin.Pair<K, V>): kotlin.Unit = TODO()
//
//operator fun <K, V> kotlin.collections.MutableMap<in K, in V>.plusAssign(pairs: kotlin.collections.Iterable<kotlin.Pair<K, V>>): kotlin.Unit = TODO()
//
//operator fun <K, V> kotlin.collections.MutableMap<in K, in V>.plusAssign(map: MockMap<K, V>): kotlin.Unit = TODO()
//
//operator fun <K, V> kotlin.collections.MutableMap<in K, in V>.plusAssign(pairs: kotlin.sequences.Sequence<kotlin.Pair<K, V>>): kotlin.Unit = TODO()

//fun <K, V> kotlin.collections.MutableMap<in K, in V>.putAll(pairs: kotlin.Array<out kotlin.Pair<K, V>>): kotlin.Unit = TODO()
//
//fun <K, V> kotlin.collections.MutableMap<in K, in V>.putAll(pairs: kotlin.collections.Iterable<kotlin.Pair<K, V>>): kotlin.Unit = TODO()
//
//fun <K, V> kotlin.collections.MutableMap<in K, in V>.putAll(pairs: kotlin.sequences.Sequence<kotlin.Pair<K, V>>): kotlin.Unit = TODO()

//fun <K, V> kotlin.collections.MutableMap<out K, V>.remove(key: K): V? = TODO()
//
//operator fun <K, V> MockMMap<K, V>.set(key: K, value: V): kotlin.Unit = TODO()

//fun <K, V> kotlin.Array<out kotlin.Pair<K, V>>.toMap(): MockMap<K, V> = TODO()

//fun <K, V, M : kotlin.collections.MutableMap<in K, in V>> kotlin.Array<out kotlin.Pair<K, V>>.toMap(destination: M): M = TODO()
//
//fun <K, V> kotlin.collections.Iterable<kotlin.Pair<K, V>>.toMap(): MockMap<K, V> = TODO()
//
//fun <K, V, M : kotlin.collections.MutableMap<in K, in V>> kotlin.collections.Iterable<kotlin.Pair<K, V>>.toMap(destination: M): M = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.toMap(): MockMap<K, V> = TODO()
//
//fun <K, V, M : kotlin.collections.MutableMap<in K, in V>> kotlin.collections.Map<out K, V>.toMap(destination: M): M = TODO()
//
//fun <K, V> kotlin.sequences.Sequence<kotlin.Pair<K, V>>.toMap(): MockMap<K, V> = TODO()
//
//fun <K, V, M : kotlin.collections.MutableMap<in K, in V>> kotlin.sequences.Sequence<kotlin.Pair<K, V>>.toMap(destination: M): M = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.toMutableMap(): MockMMap<K, V> = TODO()
//
//fun <K, V> kotlin.collections.Map.Entry<K, V>.toPair(): kotlin.Pair<K, V> = TODO()

//fun <K, V> kotlin.collections.Map<out K, V>.all(predicate: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Boolean): kotlin.Boolean = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.any(): kotlin.Boolean = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.any(predicate: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Boolean): kotlin.Boolean = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.asIterable(): kotlin.collections.Iterable<kotlin.collections.Map.Entry<K, V>> = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.asSequence(): kotlin.sequences.Sequence<kotlin.collections.Map.Entry<K, V>> = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.count(): kotlin.Int = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.count(predicate: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Boolean): kotlin.Int = TODO()
//
//fun <K, V, R : kotlin.Any> kotlin.collections.Map<out K, V>.firstNotNullOf(transform: (kotlin.collections.Map.Entry<K, V>) -> R?): R = TODO()
//
//fun <K, V, R : kotlin.Any> kotlin.collections.Map<out K, V>.firstNotNullOfOrNull(transform: (kotlin.collections.Map.Entry<K, V>) -> R?): R? = TODO()

//fun <K, V, R> kotlin.collections.Map<out K, V>.flatMap(transform: (kotlin.collections.Map.Entry<K, V>) -> kotlin.collections.Iterable<R>): kotlin.collections.List<R> = TODO()
//
//fun <K, V, R> kotlin.collections.Map<out K, V>.flatMap(transform: (kotlin.collections.Map.Entry<K, V>) -> kotlin.sequences.Sequence<R>): kotlin.collections.List<R> = TODO()
//
//fun <K, V, R, C : kotlin.collections.MutableCollection<in R>> kotlin.collections.Map<out K, V>.flatMapTo(destination: C, transform: (kotlin.collections.Map.Entry<K, V>) -> kotlin.collections.Iterable<R>): C = TODO()
//
//fun <K, V, R, C : kotlin.collections.MutableCollection<in R>> kotlin.collections.Map<out K, V>.flatMapTo(destination: C, transform: (kotlin.collections.Map.Entry<K, V>) -> kotlin.sequences.Sequence<R>): C = TODO()

// fun <K, V> kotlin.collections.Map<out K, V>.forEach(action: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Unit): kotlin.Unit = TODO()

//fun <K, V, R> kotlin.collections.Map<out K, V>.map(transform: (kotlin.collections.Map.Entry<K, V>) -> R): kotlin.collections.List<R> = TODO()
//
//fun <K, V, R : kotlin.Any> kotlin.collections.Map<out K, V>.mapNotNull(transform: (kotlin.collections.Map.Entry<K, V>) -> R?): kotlin.collections.List<R> = TODO()
//
//fun <K, V, R : kotlin.Any, C : kotlin.collections.MutableCollection<in R>> kotlin.collections.Map<out K, V>.mapNotNullTo(destination: C, transform: (kotlin.collections.Map.Entry<K, V>) -> R?): C = TODO()
//
//fun <K, V, R, C : kotlin.collections.MutableCollection<in R>> kotlin.collections.Map<out K, V>.mapTo(destination: C, transform: (kotlin.collections.Map.Entry<K, V>) -> R): C = TODO()
//
//fun <K, V, R : kotlin.Comparable<R>> kotlin.collections.Map<out K, V>.maxBy(selector: (kotlin.collections.Map.Entry<K, V>) -> R): kotlin.collections.Map.Entry<K, V>? = TODO()
//
//fun <K, V, R : kotlin.Comparable<R>> kotlin.collections.Map<out K, V>.maxByOrNull(selector: (kotlin.collections.Map.Entry<K, V>) -> R): kotlin.collections.Map.Entry<K, V>? = TODO()
//
//fun <K, V, R : kotlin.Comparable<R>> kotlin.collections.Map<out K, V>.maxOf(selector: (kotlin.collections.Map.Entry<K, V>) -> R): R = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.maxOf(selector: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Double): kotlin.Double = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.maxOf(selector: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Float): kotlin.Float = TODO()
//
//fun <K, V, R : kotlin.Comparable<R>> kotlin.collections.Map<out K, V>.maxOfOrNull(selector: (kotlin.collections.Map.Entry<K, V>) -> R): R? = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.maxOfOrNull(selector: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Double): kotlin.Double? = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.maxOfOrNull(selector: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Float): kotlin.Float? = TODO()
//
//fun <K, V, R> kotlin.collections.Map<out K, V>.maxOfWith(comparator: kotlin.Comparator<in R> /* = java.util.Comparator<in R> */, selector: (kotlin.collections.Map.Entry<K, V>) -> R): R = TODO()
//
//fun <K, V, R> kotlin.collections.Map<out K, V>.maxOfWithOrNull(comparator: kotlin.Comparator<in R> /* = java.util.Comparator<in R> */, selector: (kotlin.collections.Map.Entry<K, V>) -> R): R? = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.maxWith(comparator: kotlin.Comparator<in kotlin.collections.Map.Entry<K, V>> /* = java.util.Comparator<in kotlin.collections.Map.Entry<K, V>> */): kotlin.collections.Map.Entry<K, V>? = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.maxWithOrNull(comparator: kotlin.Comparator<in kotlin.collections.Map.Entry<K, V>> /* = java.util.Comparator<in kotlin.collections.Map.Entry<K, V>> */): kotlin.collections.Map.Entry<K, V>? = TODO()
//
//fun <K, V, R : kotlin.Comparable<R>> kotlin.collections.Map<out K, V>.minBy(selector: (kotlin.collections.Map.Entry<K, V>) -> R): kotlin.collections.Map.Entry<K, V>? = TODO()
//
//fun <K, V, R : kotlin.Comparable<R>> kotlin.collections.Map<out K, V>.minByOrNull(selector: (kotlin.collections.Map.Entry<K, V>) -> R): kotlin.collections.Map.Entry<K, V>? = TODO()
//
//fun <K, V, R : kotlin.Comparable<R>> kotlin.collections.Map<out K, V>.minOf(selector: (kotlin.collections.Map.Entry<K, V>) -> R): R = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.minOf(selector: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Double): kotlin.Double = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.minOf(selector: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Float): kotlin.Float = TODO()
//
//fun <K, V, R : kotlin.Comparable<R>> kotlin.collections.Map<out K, V>.minOfOrNull(selector: (kotlin.collections.Map.Entry<K, V>) -> R): R? = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.minOfOrNull(selector: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Double): kotlin.Double? = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.minOfOrNull(selector: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Float): kotlin.Float? = TODO()
//
//fun <K, V, R> kotlin.collections.Map<out K, V>.minOfWith(comparator: kotlin.Comparator<in R> /* = java.util.Comparator<in R> */, selector: (kotlin.collections.Map.Entry<K, V>) -> R): R = TODO()
//
//fun <K, V, R> kotlin.collections.Map<out K, V>.minOfWithOrNull(comparator: kotlin.Comparator<in R> /* = java.util.Comparator<in R> */, selector: (kotlin.collections.Map.Entry<K, V>) -> R): R? = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.minWith(comparator: kotlin.Comparator<in kotlin.collections.Map.Entry<K, V>> /* = java.util.Comparator<in kotlin.collections.Map.Entry<K, V>> */): kotlin.collections.Map.Entry<K, V>? = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.minWithOrNull(comparator: kotlin.Comparator<in kotlin.collections.Map.Entry<K, V>> /* = java.util.Comparator<in kotlin.collections.Map.Entry<K, V>> */): kotlin.collections.Map.Entry<K, V>? = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.none(): kotlin.Boolean = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.none(predicate: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Boolean): kotlin.Boolean = TODO()
//
//fun <K, V, M : kotlin.collections.Map<out K, V>> M.onEach(action: (kotlin.collections.Map.Entry<K, V>) -> kotlin.Unit): M = TODO()
//
//fun <K, V, M : kotlin.collections.Map<out K, V>> M.onEachIndexed(action: (kotlin.Int, kotlin.collections.Map.Entry<K, V>) -> kotlin.Unit): M = TODO()
//
//fun <K, V> kotlin.collections.Map<out K, V>.toList(): kotlin.collections.List<kotlin.Pair<K, V>> = TODO()
*/