package edu.unca.faces.files

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import edu.unca.faces.files.util.TestingByteChannel
import org.junit.Assert.*
import org.junit.Test

class ObjectWriterTest {

    @Test
    fun testWrite() {
        val chan = TestingByteChannel(ByteArray(SampleFile.bytes.size))
        BinaryObjectWriter(chan, SampleFile.createSampleFileObject()).writeObject()
        assertArrayEquals(SampleFile.bytes, chan.bytes)
    }

    @Test
    fun testGsonWrite() {
        val obj = ObjectReader(TestingByteChannel(SampleFile.bytes), {SampleFile()}).obj
        println(GsonBuilder().setPrettyPrinting().create().toJson(obj));
    }
}