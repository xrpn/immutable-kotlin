package com.xrpn.hash

import java.nio.ByteBuffer

class MrMr64: LChecksum {

    private var mister64: Long = initial

    override fun update(p0: Int) =
        update(p0.toLong())

    override fun update(p0: Long) {
        mister64 = mrmr64(p0 + mrmr64(mister64))
    }

    /*
        b - the byte array to update the checksum with
        off - the start offset of the data
        len - the number of bytes to use for the update
    */
    override tailrec fun update(b: ByteArray, off: Int, len: Int): Unit = when {
        b.isEmpty() || off >= len -> Unit
        else -> {
            val newOff: Int = if (off+8 <= len) off+8
            else if (off+7 <= len) off+7
            else if (off+6 <= len) off+6
            else if (off+5 <= len) off+5
            else if (off+4 <= len) off+4
            else if (off+3 <= len) off+3
            else if (off+2 <= len) off+2
            else off+1

            when (newOff % 8) {
                8 -> update(byteToInt64(b.copyOfRange(off, newOff)))
                7 -> update(byteToInt64(b.copyOfRange(off, newOff)))
                6 -> update(byteToInt64(b.copyOfRange(off, newOff)))
                5 -> update(byteToInt64(b.copyOfRange(off, newOff)))
                4 -> update(byteToInt32(b.copyOfRange(off, newOff)))
                3 -> update(byteToInt32(b.copyOfRange(off, newOff)))
                2 -> update(byteToInt16(b.copyOfRange(off, newOff)).toInt())
                1 -> update(b.get(off).toInt())
                0 -> update(byteToInt64(b.copyOfRange(off, newOff)))
            }
            update(b, newOff, len)
        }
    }

    override fun update(buffer: ByteBuffer) {
        val pos = buffer.position()
        val limit = buffer.limit()
        check(pos <= limit)
        val remaining = limit - pos
        if (remaining > 0) {
            val b = if (buffer.hasArray()) buffer.array() else ByteArray(Math.min(buffer.remaining(), 4096))
            while (buffer.hasRemaining()) {
                val length = buffer.remaining().coerceAtMost(b.size)
                buffer[b, 0, length]
                this.update(b, 0, length)
            }
            buffer.position(limit)
        }
    }

    override fun getValue(): Long = mister64

    override fun getIntValue(): Int {
        val l = mister64
        return (l xor (l ushr 32)).toInt()
    }

    override fun reset() { mister64 = initial }

    companion object {

        private const val initial: Long = 0L
        private const val c1: Long = -0xae502812aa7333L
        private const val c2: Long = -0x3b314601e57a13adL

        fun mrmr64(hIn: Long): Long {
            var h: Long = hIn
            h = h xor (h ushr 33)
            h *= c1
            h = h xor (h ushr 33)
            h *= c2
            h = h xor (h ushr 33)
            return h
        }
    }
}
