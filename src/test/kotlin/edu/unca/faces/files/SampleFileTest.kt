package edu.unca.faces.files

import edu.unca.faces.files.util.HashUtil.bytesToHexString
import edu.unca.faces.files.util.HashUtil.getMD5Checksum
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Files.isRegularFile
import java.nio.file.Paths


class SampleFileTest {

    @Test
    fun testFaceTri() {
        val path = Paths.get("./testfiles/Face.tri")

        val obj = ObjectReader.readFileToObject(path)
        val originalChecksum = getMD5Checksum(path)
        val originalBytes = Files.readAllBytes(path)

        val tempFile = Files.createTempFile("rewritten", path.toFile().name)
        ObjectWriter.writeObjectToFile(obj, tempFile)
        val newChecksum = getMD5Checksum(tempFile)
        val newBytes = Files.readAllBytes(tempFile)


        assertByteArraysEqual(originalBytes, newBytes)
        assertEquals(originalChecksum, newChecksum)
    }

    @Test
    fun testSampleFiles() {
        Files.walk(Paths.get("./testfiles")).use { paths ->
            paths.filter({ isRegularFile(it) }).filter({ it.toFile().name.endsWith(".tri") }).forEach({
                println("File: $it")
                try {
                    val obj = ObjectReader.readFileToObject(it)
                    val originalChecksum = getMD5Checksum(it)
                    val originalBytes = Files.readAllBytes(it)

                    val tempFile = Files.createTempFile("rewritten", it.toFile().name)
                    ObjectWriter.writeObjectToFile(obj, tempFile)
                    val newChecksum = getMD5Checksum(tempFile)
                    val newBytes = Files.readAllBytes(tempFile)

                    assertByteArraysEqual(originalBytes, newBytes)
                    assertEquals(originalChecksum, newChecksum)
                } catch (e: Throwable) {
                    println("File: $it")
                    throw e
                }


            })
        }
    }

    private fun assertByteArraysEqual(b1: ByteArray, b2: ByteArray) {
        for (i in 0 until (Math.max(b1.size, b2.size))) {
            if (i >= b1.size) {
                throw AssertionError("Reached end of expected bytes but found actual bytes at index $i: ${bytesToHexString(getNext8Bytes(b2, i))}")
            } else if (i >= b2.size) {
                throw AssertionError("Reached end of actual bytes but found expected bytes at index $i: ${bytesToHexString(getNext8Bytes(b1, i))}")
            } else if (b1[i] != b2[i]) {
                throw AssertionError("Arrays do not match at index $i. Expected: ${bytesToHexString(getNext8Bytes(b1, i))}. Actual: ${bytesToHexString(getNext8Bytes(b2, i))}")
            }
        }
    }

    private fun getNext8Bytes(bytes: ByteArray, index: Int): ByteArray {
        val length = Math.min(8, bytes.size - index)
        var j = 0
        val result = ByteArray(length)
        for (i in index until index + length) {
            result[j] = bytes[i]
            j++
        }
        return result
    }
}