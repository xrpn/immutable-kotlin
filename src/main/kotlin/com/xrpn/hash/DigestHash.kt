package com.xrpn.hash

import java.util.zip.Adler32
import java.util.zip.CRC32
import java.util.zip.CRC32C

object DigestHash {

    fun crc32(input: ByteArray): UInt {
        val crc32 = CRC32()
        crc32.update(input)
        val v = crc32.value
        if (v < UInt.MIN_VALUE.toLong() || UInt.MAX_VALUE.toLong() < v) throw RuntimeException(v.toString())
        return v.toUInt()
    }

    fun crc32i(input: ByteArray): Int {
        val aux: UInt = crc32(input)
        val v = (aux.toLong() - Int.MAX_VALUE)
        if (v < Int.MIN_VALUE.toLong() || Int.MAX_VALUE.toLong() < v) throw RuntimeException(v.toString())
        return v.toInt()
    }

    fun crc32c(input: ByteArray): UInt {
        val crc32c = CRC32C()
        crc32c.update(input)
        val v = crc32c.value
        if (v < UInt.MIN_VALUE.toLong() || UInt.MAX_VALUE.toLong() < v) throw RuntimeException(v.toString())
        return v.toUInt()
    }

    fun crc32ci(input: ByteArray): Int {
        val aux: UInt = crc32c(input)
        val v = (aux.toLong() - Int.MAX_VALUE)
        if (v < Int.MIN_VALUE.toLong() || Int.MAX_VALUE.toLong() < v) throw RuntimeException(v.toString())
        return v.toInt()
    }

    fun adler32(input: ByteArray): UInt {
        val adler32 = Adler32()
        adler32.update(input)
        val v = adler32.value
        if (v < UInt.MIN_VALUE.toLong() || UInt.MAX_VALUE.toLong() < v) throw RuntimeException(v.toString())
        return v.toUInt()
    }

    fun adler32i(input: ByteArray): Int {
        val aux: UInt = adler32(input)
        val v = (aux.toLong() - Int.MAX_VALUE)
        if (v < Int.MIN_VALUE.toLong() || Int.MAX_VALUE.toLong() < v) throw RuntimeException(v.toString())
        return v.toInt()
    }
}