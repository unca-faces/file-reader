package edu.unca.faces.files.util

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.ReadableByteChannel
import java.nio.channels.SeekableByteChannel
import java.nio.channels.WritableByteChannel

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

    fun readStringAndReset(chan: SeekableByteChannel, length: Int): String {
        val pos = chan.position()
        val result = readString(chan, length)
        chan.position(pos)
        return result
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

    @JvmStatic
    fun writeString(chan: WritableByteChannel, value: String) {
        val length = value.length
        writeInt(chan, length)
        val buff = ByteBuffer.allocate(length)
        for (c in value.toCharArray()) {
            writeCharToBuffer(buff, c)
        }
        buff.position(0)
        chan.write(buff)
    }

    @JvmStatic
    fun writeNullTerminatedString(chan: WritableByteChannel, value: String) {
        val buff = ByteBuffer.allocate(value.length + 1)
        for (c in value.toCharArray()) {
            writeCharToBuffer(buff, c)
        }
        writeCharToBuffer(buff, '\u0000')
        buff.position(0)
        chan.write(buff)
    }

    @JvmStatic
    fun writeInt(chan: WritableByteChannel, value: Int) {
        val buff = ByteBuffer.allocate(4)
        buff.order(ByteOrder.LITTLE_ENDIAN)
        buff.putInt(value)
        buff.flip()
        chan.write(buff)
    }

    @JvmStatic
    fun writeFloat(chan: WritableByteChannel, value: Float) {
        val buff = ByteBuffer.allocate(4)
        buff.order(ByteOrder.LITTLE_ENDIAN)
        buff.putFloat(value)
        buff.flip()
        chan.write(buff)
    }

    @JvmStatic
    fun writeShort(chan: WritableByteChannel, value: Short) {
        val buff = ByteBuffer.allocate(2)
        buff.order(ByteOrder.LITTLE_ENDIAN)
        buff.putShort(value)
        buff.flip()
        chan.write(buff)
    }

    @JvmStatic
    fun writeDouble(chan: WritableByteChannel, value: Double) {
        val buff = ByteBuffer.allocate(8)
        buff.order(ByteOrder.LITTLE_ENDIAN)
        buff.putDouble(value)
        buff.flip()
        chan.write(buff)
    }

    @JvmStatic
    fun writeLong(chan: WritableByteChannel, value: Long) {
        val buff = ByteBuffer.allocate(8)
        buff.order(ByteOrder.LITTLE_ENDIAN)
        buff.putLong(value)
        buff.flip()
        chan.write(buff)
    }

    @JvmStatic
    fun writeByte(chan: WritableByteChannel, value: Byte) {
        val buff = ByteBuffer.allocate(1)
        buff.put(value)
        chan.write(buff)
    }

    @JvmStatic
    fun writeChar(chan: WritableByteChannel, value: Char) {
        val buff = ByteBuffer.allocate(1)
        writeCharToBuffer(buff, value)
        buff.flip()
        chan.write(buff)
    }

    private fun writeCharToBuffer(buff: ByteBuffer, value: Char) {
        val asByte = (0xFF and value.toInt()).toByte()
        buff.put(asByte)
    }

    fun writeNulls(chan: WritableByteChannel, length: Int) {
        val buff = ByteBuffer.allocate(length)
        for (i in 0 until length) {
            buff.put(0x00)
        }
        buff.position(0)
        chan.write(buff)
    }
}