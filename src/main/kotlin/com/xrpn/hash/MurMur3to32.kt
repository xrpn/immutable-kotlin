package com.xrpn.hash

import java.nio.ByteBuffer

class MurMur3to32(val seed: UInt = 1u): LChecksum {

    /*

        Freely adapted with thanks from Austin Appleby at https://github.com/aappleby/smhasher and derivative
        https://github.com/google/guava/blob/master/guava/src/com/google/common/hash/Murmur3_32HashFunction.java
        with all credits given to whom the credits pertain.  Please check my adaptation for errors!

     */

    var murmur: UInt = initial

    override fun update(p0: Long) {
        val low: UInt = p0.toUInt()
        val high: UInt = (p0 ushr 32).toUInt()
        var k1: UInt = mixK1(low)
        var h1: UInt = mixH1(seed, k1)
        k1 = mixK1(high)
        h1 = mixH1(h1, k1)
        val mr = fmix(h1, Long.SIZE_BYTES);
        murmur = mixH1(murmur, mixK1(mr))
    }

    override fun update(p0: Int) {
        val k1: UInt = mixK1(p0.toUInt())
        val h1: UInt = mixH1(seed, k1)
        val mr: UInt = fmix(h1, Int.SIZE_BYTES)
        murmur = mixH1(murmur, mixK1(mr))
    }

    override fun update(b: ByteArray, off: Int, len: Int) {
        validate(off, off + len, b.size)
        var h1 = seed
        var ix = 0
        while (ix + SIZE <= len) {
            val start = off + ix
            val k1 = mixK1(byteToInt32(b.copyOfRange(start,start+SIZE)).toUInt())
            h1 = mixH1(h1, k1)
            ix += SIZE
        }

        var k1: UInt = 0u
        var shift = 0
        while (ix < len) {
            k1 = k1 xor (b[off + ix].toUInt() shl shift)
            ix += 1
            shift += 8
        }
        h1 = h1 xor mixK1(k1)
        val mr: UInt = fmix(h1, len)
        murmur = mixH1(murmur, mixK1(mr))
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

    override fun getIntValue(): Int {
        val v = (murmur.toLong() - Int.MAX_VALUE)
        check(!(v < Int.MIN_VALUE.toLong() || Int.MAX_VALUE.toLong() < v))
        return v.toInt()
    }

    override fun getValue(): Long = murmur.toLong()

    override fun reset() { murmur = initial }

    companion object {

        private const val C1: UInt = 3432918353u
        private const val C2: UInt = 461845907u
        private const val C3: UInt = 3864292196u
        private const val C4: UInt = 2246822507u
        private const val C5: UInt = 2246822507u
        private const val C6: UInt = 3266489909u
        private const val SIZE = 4
        private const val initial: UInt = 0u

        private fun validate(start: Int, end: Int, size: Int) {
            if (start < 0 || end < start || end > size) throw IndexOutOfBoundsException()
        }

        @OptIn(ExperimentalStdlibApi::class)
        private fun mixK1(kIn: UInt): UInt {
            var k1 = kIn
            k1 *= C1;
            k1 = k1.rotateLeft(15)
            k1 *= C2;
            return k1;
        }

        @OptIn(ExperimentalStdlibApi::class)
        private fun mixH1(hIn: UInt, kIn: UInt): UInt {
            var h1 = hIn
            var k1 = kIn
            h1 = h1 xor k1
            h1 = h1.rotateLeft(13)
            h1 = h1 * 5u + C3
            return h1
        }

        private fun fmix(hIn: UInt, length: Int): UInt {
            var h1 = hIn
            h1 = h1 xor length.toUInt()
            h1 = h1 xor ( h1 shr 16 )
            h1 *= C5
            h1 = h1 xor ( h1 shr 13 )
            h1 *= C6
            h1 = h1 xor ( h1 shr 16 )
            return h1
        }
    }
}