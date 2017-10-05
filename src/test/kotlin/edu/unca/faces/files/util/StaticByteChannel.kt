package edu.unca.faces.files.util

import java.nio.BufferOverflowException
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel

class StaticByteChannel(val bytes: ByteArray) : ReadableByteChannel {

    var pos = 0;

    override fun isOpen() = true

    override fun close() {
    }

    override fun read(dst: ByteBuffer): Int {
        var i = 0
        while (dst.hasRemaining()) {
            if (pos >= bytes.size) throw BufferOverflowException()
            print(javax.xml.bind.DatatypeConverter.printHexBinary(byteArrayOf(bytes[pos])))
            dst.put(bytes[pos])
            i++
            pos++
        }
        println()
        return i
    }
}