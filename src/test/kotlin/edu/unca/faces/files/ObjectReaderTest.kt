package edu.unca.faces.files

import edu.unca.faces.files.util.StaticByteChannel

import org.junit.Assert.*
import org.junit.Test

class ObjectReaderTest {

    @Test
    fun testRead() {
        val obj = ObjectReader(StaticByteChannel(SampleFile.bytes), {SampleFile()}).obj as SampleFile
        assertEquals(SampleFile.KExpected, obj.K)
        assertEquals(SampleFile.VExpected, obj.V)
        assertEquals(String(SampleFile.idExpected), String(obj.id))
        assertTrue(SampleFile.sArray1Expected.contentEquals(obj.sArray1))
        assertTrue(SampleFile.sArray2Expected.contentEquals(obj.sArray2))
        assertEquals(SampleFile.sNullTermExpected, obj.sNullTerm)
        assertTrue(SampleFile.floats2dExpected.contentDeepEquals(obj.floats2d))
        assertEquals(SampleFile.childrenExpected, obj.children)
    }
}