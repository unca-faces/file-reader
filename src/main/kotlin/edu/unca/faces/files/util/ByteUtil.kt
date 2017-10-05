package edu.unca.faces.files.util

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.ReadableByteChannel

object ByteUtil {

    /**
     * Reads a string of a given length.
     */
    @JvmStatic
    fun readString(chan: ReadableByteChannel, length: Int): String {
        val buff = ByteBuffer.allocate(length)
        chan.read(buff)
        return readString(buff);
    }

    /**
     * Reads a string where the size is included in the byte channel as an int that precedes the string.
     */
    @JvmStatic
    fun readString(chan: ReadableByteChannel): String {
        val length = readInt(chan)
        val result = readString(chan, length)
        return result
    }

    fun readNullTerminatedString(chan: ReadableByteChannel): String {
        var buff = ByteBuffer.allocate(1)
        val result = StringBuilder()

        var currentChar: Char = ' '
        while (currentChar != '\u0000') {
            chan.read(buff)
            currentChar = buff[0].toChar()
            buff = ByteBuffer.allocate(1)
            if (currentChar != '\u0000') {
                result.append(currentChar)
            }
        }

        return result.toString()
    }

    @JvmStatic
    private fun readString(buff: ByteBuffer): String {
        val result = CharArray(buff.capacity(), { '\u0000' })
        for (i in 0 until buff.capacity()) {
            result[i] = buff[i].toChar()
        }
        return String(result)
    }

    @JvmStatic
    fun readInt(chan: ReadableByteChannel): Int = readNum(chan, ByteBuffer.allocate(4)).int

    @JvmStatic
    fun readFloat(chan: ReadableByteChannel): Float = readNum(chan, ByteBuffer.allocate(4)).float

    @JvmStatic
    fun readShort(chan: ReadableByteChannel): Short = readNum(chan, ByteBuffer.allocate(2)).short

    @JvmStatic
    fun readDouble(chan: ReadableByteChannel): Double = readNum(chan, ByteBuffer.allocate(8)).double

    @JvmStatic
    fun readLong(chan: ReadableByteChannel): Long = readNum(chan, ByteBuffer.allocate(8)).long

    @JvmStatic
    fun readByte(chan: ReadableByteChannel): Byte = readNum(chan, ByteBuffer.allocate(1)).get()

    @JvmStatic
    fun readChar(chan: ReadableByteChannel): Char = readNum(chan, ByteBuffer.allocate(1))[0].toChar()

    private fun readNum(chan: ReadableByteChannel, buff: ByteBuffer): ByteBuffer {
        buff.order(ByteOrder.LITTLE_ENDIAN)
        chan.read(buff)
        buff.position(0)
        return buff
    }
}