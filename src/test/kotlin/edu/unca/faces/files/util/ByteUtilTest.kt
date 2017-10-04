package edu.unca.faces.files.util

import org.junit.Assert.*
import org.junit.Test

class ByteUtilTest {

    @Test
    fun testReadString() {
        val bytes = byteArrayOf(0x41, 0x42, 0x43, 0x44)
        val result = ByteUtil.readString(StaticByteChannel(bytes), 4)
        assertEquals("ABCD", result)
    }

    @Test
    fun testReadInt() {
        val bytes = byteArrayOf(0xFF.toByte(), 0x00, 0x00, 0x00)
        val result = ByteUtil.readInt(StaticByteChannel(bytes))
        assertEquals(255, result)
    }

    @Test
    fun testReadFloat() {
        val bytes = byteArrayOf(0x00, 0x00, 0x00, 0x40)
        val result = ByteUtil.readFloat(StaticByteChannel(bytes))
        assertEquals(2.0F, result)
    }

    @Test
    fun testReadShort() {
        val bytes = byteArrayOf(0xFF.toByte(), 0x00)
        val result = ByteUtil.readShort(StaticByteChannel(bytes))
        assertEquals(255.toShort(), result)
    }
}