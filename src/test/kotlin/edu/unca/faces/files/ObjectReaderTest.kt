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
        assertArrayEquals(SampleFile.sArray1Expected, obj.sArray1)
        assertArrayEquals(SampleFile.sArray2Expected, obj.sArray2)
        assertEquals(SampleFile.sNullTermExpected, obj.sNullTerm)
        assertArrayEquals(SampleFile.floats2dExpected, obj.floats2d)
        assertArrayEquals(SampleFile.childrenExpected, obj.children)
        assertArrayEquals(SampleFile.failedCondCharsExpected, obj.failedCondChars)
        assertArrayEquals(SampleFile.passedCondCharsExpected, obj.passedCondChars)
    }
}