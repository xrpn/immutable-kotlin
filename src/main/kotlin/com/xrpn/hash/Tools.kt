package com.xrpn.hash

import java.math.BigInteger

fun byteToInt16(bytes: ByteArray): Short = when (bytes.size) {
    // Big Endian: bytes[0] is signed
    1 -> bytes[0].toShort()
    2 -> {
        val b0 = bytes[0].toInt()
        val b1 = bytes[1].toInt() and 0xFF
        val res = (b0 shl 8) or b1
        if (res < Short.MIN_VALUE || Short.MAX_VALUE < res) throw IllegalArgumentException()
        res.toShort()
    }
    else -> throw IllegalArgumentException()
}

fun byteToInt32(bytes: ByteArray): Int = when(bytes.size) {
    // Big Endian: bytes[0] is signed
    1, 2 -> byteToInt16(bytes).toInt()
    3 -> {
        val b0 = bytes[0].toInt()
        val b1 = bytes[1].toInt() and 0xFF
        val b2 = bytes[2].toInt() and 0xFF
        (b0 shl 16) or (b1 shl 8) or b2
    }
    4 -> {
        val b0 = bytes[0].toInt()
        val b1 = bytes[1].toInt() and 0xFF
        val b2 = bytes[2].toInt() and 0xFF
        val b3 = bytes[3].toInt() and 0xFF
        (b0 shl 24) or (b1 shl 16) or (b2 shl 8) or b3
    }
    else -> throw IllegalArgumentException()
}

fun byteToInt64(bytes: ByteArray): Long = when(val sz = bytes.size) {
    1, 2 -> byteToInt16(bytes).toLong()
    3, 4 -> byteToInt32(bytes).toLong()
    5, 6, 7, 8 -> {
        var res = bytes[0].toLong() shl (8 * (sz - 1))
        var offset = (8 * (sz - 2))
        for (byte in bytes.copyOfRange(1, sz)) {
            res = res or ((byte.toLong() and 0xFF) shl offset)
            offset -= 8
        }
        res
    }
    else -> throw IllegalArgumentException()
}

fun ByteArray.toNumber(): Number = when {
    this.size <= 2 -> byteToInt16(this)
    this.size <= 4 -> byteToInt32(this)
    this.size <= 8 -> byteToInt64(this)
    else -> BigInteger(this)
}