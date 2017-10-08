package edu.unca.faces.files.util

import java.nio.BufferOverflowException
import java.nio.ByteBuffer
import java.nio.channels.ByteChannel

class TestingByteChannel(val bytes: ByteArray) : ByteChannel {

    var pos = 0;

    override fun isOpen() = true

    override fun close() {
    }

    override fun read(dst: ByteBuffer): Int {
        var i = 0
        while (dst.hasRemaining()) {
            if (pos >= bytes.size) throw BufferOverflowException()
            print(String.format("%02X ", bytes[pos]))
            dst.put(bytes[pos])
            i++
            pos++
        }
        println()
        return i
    }

    override fun write(src: ByteBuffer): Int {
        var i = 0
        while (src.hasRemaining()) {
            if (pos >= bytes.size) throw BufferOverflowException()
            val byte = src.get()
            print(String.format("%02X ", byte))
            bytes[pos] = byte
            i++
            pos++
        }
        println()
        return i
    }
}