package com.xrpn.hash

import com.xrpn.imapi.IMList
import java.util.zip.Adler32
import java.util.zip.CRC32
import java.util.zip.CRC32C
import java.util.zip.Checksum

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