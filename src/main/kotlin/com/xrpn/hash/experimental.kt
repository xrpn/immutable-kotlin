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