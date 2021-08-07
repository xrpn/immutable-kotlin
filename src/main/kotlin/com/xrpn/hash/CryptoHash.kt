package com.xrpn.hash

import java.security.MessageDigest

object CryptoHash {

    fun SHA3_512(input: ByteArray) = hashByteAry("SHA3-512", input)
    fun SHA_384(input: ByteArray) = hashByteAry("SHA-384", input)
    fun SHA(input: ByteArray) = hashByteAry("SHA", input)
    fun SHA3_384(input: ByteArray) = hashByteAry("SHA3-384", input)
    fun SHA_224(input: ByteArray) = hashByteAry("SHA-224", input)
    fun SHA_512_256(input: ByteArray) = hashByteAry("SHA-512/256", input)
    fun SHA_256(input: ByteArray) = hashByteAry("SHA-256", input)
    fun MD2(input: ByteArray) = hashByteAry("MD2", input)
    fun SHA_512_224(input: ByteArray) = hashByteAry("SHA-512/224", input)
    fun SHA3_256(input: ByteArray) = hashByteAry("SHA3-256", input)
    fun SHA_512(input: ByteArray) = hashByteAry("SHA-512", input)
    fun MD5(input: ByteArray) = hashByteAry("MD5", input)
    fun SHA3_224(input: ByteArray) = hashByteAry("SHA3-224", input)

    fun SHA3_512_hex(input: ByteArray) = hashHex("SHA3-512", input)
    fun SHA_384_hex(input: ByteArray) = hashHex("SHA-384", input)
    fun SHA_hex(input: ByteArray) = hashHex("SHA", input)
    fun SHA3_384_hex(input: ByteArray) = hashHex("SHA3-384", input)
    fun SHA_224_hex(input: ByteArray) = hashHex("SHA-224", input)
    fun SHA_512_256_hex(input: ByteArray) = hashHex("SHA-512/256", input)
    fun SHA_256_hex(input: ByteArray) = hashHex("SHA-256", input)
    fun MD2_hex(input: ByteArray) = hashHex("MD2", input)
    fun SHA_512_224_hex(input: ByteArray) = hashHex("SHA-512/224", input)
    fun SHA3_256_hex(input: ByteArray) = hashHex("SHA3-256", input)
    fun SHA_512_hex(input: ByteArray) = hashHex("SHA-512", input)
    fun MD5_hex(input: ByteArray) = hashHex("MD5", input)
    fun SHA3_224_hex(input: ByteArray) = hashHex("SHA3-224", input)

    fun SHA3_512_num(input: ByteArray): Number = hashNumber("SHA3-512", input)
    fun SHA_384_num(input: ByteArray) = hashNumber("SHA-384", input)
    fun SHA_num(input: ByteArray) = hashNumber("SHA", input)
    fun SHA3_384_num(input: ByteArray) = hashNumber("SHA3-384", input)
    fun SHA_224_num(input: ByteArray) = hashNumber("SHA-224", input)
    fun SHA_512_256_num(input: ByteArray) = hashNumber("SHA-512/256", input)
    fun SHA_256_num(input: ByteArray) = hashNumber("SHA-256", input)
    fun MD2_num(input: ByteArray) = hashNumber("MD2", input)
    fun SHA_512_224_num(input: ByteArray) = hashNumber("SHA-512/224", input)
    fun SHA3_256_num(input: ByteArray) = hashNumber("SHA3-256", input)
    fun SHA_512_num(input: ByteArray) = hashNumber("SHA-512", input)
    fun MD5_num(input: ByteArray) = hashNumber("MD5", input)
    fun SHA3_224_num(input: ByteArray) = hashNumber("SHA3-224", input)

    private fun hashByteAry(type: String, input: ByteArray): ByteArray = MessageDigest
        .getInstance(type).digest(input)
    private fun hashHex(type: String, input: ByteArray): String = hashByteAry(type, input)
        .joinToString(separator = "") { byte -> "%02x".format(byte) }
    private fun hashNumber(type: String, input: ByteArray): Number = hashByteAry(type, input)
        .toNumber()

}
