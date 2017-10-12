package edu.unca.faces.files.util

import java.nio.BufferOverflowException
import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel

class TestingByteChannel(val bytes: ByteArray) : SeekableByteChannel {

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

    override fun position(): Long = pos.toLong()

    override fun position(newPosition: Long): SeekableByteChannel {
        pos = newPosition.toInt()
        return this
    }

    override fun size(): Long = bytes.size.toLong()

    override fun truncate(size: Long): SeekableByteChannel {
        throw UnsupportedOperationException()
    }
}