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

fun shortToByteArray(value: Short): ByteArray {
    val bytes = ByteArray(2)
    val aux = intToByteArray(value.toInt())
    bytes[1] = aux[3]
    bytes[0] = aux[2]
    return bytes
}

fun Short.to2ByteArray() = shortToByteArray(this)

fun intToByteArray(value: Int): ByteArray {
    val bytes = ByteArray(4)
    bytes[3] = (value and 0xFF).toByte()
    bytes[2] = ((value ushr 8) and 0xFF).toByte()
    bytes[1] = ((value ushr 16) and 0xFF).toByte()
    bytes[0] = ((value ushr 24) and 0xFF).toByte()
    return bytes
}

fun Int.to4ByteArray() = intToByteArray(this)

fun longToByteArray(value: Long): ByteArray {
    val bytes = ByteArray(8)
    bytes[7] = (value and 0xFF).toByte()
    bytes[6] = ((value ushr 8) and 0xFF).toByte()
    bytes[5] = ((value ushr 16) and 0xFF).toByte()
    bytes[4] = ((value ushr 24) and 0xFF).toByte()
    bytes[3] = ((value ushr 32) and 0xFF).toByte()
    bytes[2] = ((value ushr 40) and 0xFF).toByte()
    bytes[1] = ((value ushr 48) and 0xFF).toByte()
    bytes[0] = ((value ushr 56) and 0xFF).toByte()
    return bytes
}

fun Long.to8ByteArray() = longToByteArray(this)