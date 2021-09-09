package com.xrpn.hash

import com.xrpn.imapi.IMList
import java.util.zip.Adler32
import java.util.zip.CRC32
import java.util.zip.CRC32C
import java.util.zip.Checksum

interface LChecksum: Checksum {
    fun update(p0: Long)
    fun getIntValue() : Int
}

object DigestHash {

    fun crc32(input: ByteArray): UInt {
        val crc32 = CRC32()
        crc32.update(input)
        val v: Long = crc32.value
        check(!(v < UInt.MIN_VALUE.toLong() || UInt.MAX_VALUE.toLong() < v))
        return v.toUInt()
    }

    fun crc32i(input: ByteArray): Int {
        val aux: UInt = crc32(input)
        val v: Long = (aux.toLong() - Int.MAX_VALUE)
        check(!(v < Int.MIN_VALUE.toLong() || Int.MAX_VALUE.toLong() < v))
        return v.toInt()
    }

    fun crc32c(input: ByteArray): UInt {
        val crc32c = CRC32C()
        crc32c.update(input)
        val v: Long = crc32c.value
        check(!(v < UInt.MIN_VALUE.toLong() || UInt.MAX_VALUE.toLong() < v))
        return v.toUInt()
    }

    fun crc32ci(input: ByteArray): Int {
        val aux: UInt = crc32c(input)
        val v: Long = (aux.toLong() - Int.MAX_VALUE)
        check(!(v < Int.MIN_VALUE.toLong() || Int.MAX_VALUE.toLong() < v))
        return v.toInt()
    }

    fun adler32(input: ByteArray): UInt {
        val adler32 = Adler32()
        adler32.update(input)
        val v: Long = adler32.value
        check(!(v < UInt.MIN_VALUE.toLong() || UInt.MAX_VALUE.toLong() < v))
        return v.toUInt()
    }

    fun adler32i(input: ByteArray): Int {
        val aux: UInt = adler32(input)
        val v: Long = (aux.toLong() - Int.MAX_VALUE)
        check(!(v < Int.MIN_VALUE.toLong() || Int.MAX_VALUE.toLong() < v))
        return v.toInt()
    }

    fun mrmr64(input: Long): Long = MrMr64.mrmr64(input)

    fun mrmr32(input: Long): Int = MrMr64.mrmr64(input).collapse()

    fun collapseToInt(l: Long): Int = (l xor (l ushr 32)).toInt()

    fun Long.collapse() = collapseToInt(this)

    fun <T: Any> lChecksumHashCode(cs: LChecksum, t: IMList<T>, f: (T) -> Long): Int {
        if (t.fempty()) return t.hashCode()
        t.fforEach { tItem -> cs.update(f(tItem)) }
        return cs.getIntValue()
    }

    fun <T: Any> lChecksumHashCodeReverse(cs: LChecksum, t: IMList<T>, f: (T) -> Long): Int {
        if (t.fempty()) return t.hashCode()
        t.fforEachReverse { tItem -> cs.update(f(tItem)) }
        return cs.getIntValue()
    }

    fun <T: Any, Q> uIntChecksumHashCode(cs: Checksum, t: IMList<T>, f: (T) -> Q): Int {
        if (t.fempty()) return t.hashCode()
        check(cs is CRC32C || cs is CRC32 || cs is Adler32)
        when (f(t.fhead()!!)) {
            is Int -> t.fforEach { tItem -> cs.update(f(tItem) as Int) }
            is ByteArray -> t.fforEach { tItem -> cs.update(f(tItem) as ByteArray) }
            else -> throw RuntimeException()
        }
        val aux: UInt = cs.value.toUInt()
        return (aux.toLong() - Int.MAX_VALUE).toInt()
    }

}

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