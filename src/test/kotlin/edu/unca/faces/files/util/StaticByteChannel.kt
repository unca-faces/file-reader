package edu.unca.faces.files.util

import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel

class StaticByteChannel(val bytes: ByteArray) : ReadableByteChannel {

    override fun isOpen() = true

    override fun close() {
    }

    override fun read(dst: ByteBuffer): Int {
        var i = 0
        for (byte in bytes) {
            dst.put(byte)
            i++
        }
        return i
    }
}