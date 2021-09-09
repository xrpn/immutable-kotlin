package com.xrpn.hash

import java.util.zip.Checksum

interface LChecksum: Checksum {
    fun update(p0: Long)
    fun getIntValue() : Int
}