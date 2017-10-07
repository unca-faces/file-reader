package edu.unca.faces.files.util

import org.junit.Assert.*
import org.junit.Test

class ByteUtilTest {

    @Test
    fun testReadString() {
        val bytes = byteArrayOf(0x41, 0x42, 0x43, 0x44)
        val result = ByteUtil.readString(TestingByteChannel(bytes), 4)
        assertEquals("ABCD", result)
    }

    @Test
    fun testReadInt() {
        val bytes = byteArrayOf(0xFF.toByte(), 0x00, 0x00, 0x00)
        val result = ByteUtil.readInt(TestingByteChannel(bytes))
        assertEquals(255, result)
    }

    @Test
    fun testReadFloat() {
        val bytes = byteArrayOf(0x00, 0x00, 0x00, 0x40)
        val result = ByteUtil.readFloat(TestingByteChannel(bytes))
        assertEquals(2.0F, result)
    }

    @Test
    fun testReadShort() {
        val bytes = byteArrayOf(0xFF.toByte(), 0x00)
        val result = ByteUtil.readShort(TestingByteChannel(bytes))
        assertEquals(255.toShort(), result)
    }

    @Test
    fun testWriteInt() {
        val bytes = byteArrayOf(0xFF.toByte(), 0x00, 0x00, 0x00)
        val chan = TestingByteChannel(ByteArray(4))
        ByteUtil.writeInt(chan, 255)
        assertArrayEquals(bytes, chan.bytes)
    }

    @Test
    fun testWriteFloat() {
        val bytes = byteArrayOf(0x00, 0x00, 0x00, 0x40)
        val chan = TestingByteChannel(ByteArray(4))
        ByteUtil.writeFloat(chan, 2.0F)
        assertArrayEquals(bytes, chan.bytes)
    }

    @Test
    fun testWriteShort() {
        val bytes = byteArrayOf(0xFF.toByte(), 0x00)
        val chan = TestingByteChannel(ByteArray(2))
        ByteUtil.writeShort(chan, 255)
        assertArrayEquals(bytes, chan.bytes)
    }

    @Test
    fun testWriteChar() {
        val bytes = byteArrayOf(0x41)
        val chan = TestingByteChannel(ByteArray(1))
        ByteUtil.writeChar(chan, 'A')
        assertArrayEquals(bytes, chan.bytes)
    }
}