package edu.unca.faces.files

import edu.unca.faces.files.util.TestingByteChannel
import org.junit.Assert.*
import org.junit.Test

class ObjectWriterTest {

    @Test
    fun testWrite() {
        val chan = TestingByteChannel(ByteArray(SampleFile.bytes.size))
        ObjectWriter(chan, SampleFile.createSampleFileObject()).writeObject()
        assertArrayEquals(SampleFile.bytes, chan.bytes)
    }
}