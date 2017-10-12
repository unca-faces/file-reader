package edu.unca.faces.files.util

import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

object HashUtil {

    @JvmStatic
    fun getMD5Checksum(path: Path): String {
        val md = MessageDigest.getInstance("MD5")
        md.update(Files.readAllBytes(path))
        return bytesToHexString(md.digest())
    }

    @JvmStatic
    fun bytesToHexString(bytes: ByteArray): String {
        val b = StringBuilder()
        for (byte in bytes) {
            b.append(String.format("%02x ", byte))
        }
        return b.toString()
    }

    @JvmStatic
    fun checksumMatches(path1: Path, path2: Path): Boolean {
        return getMD5Checksum(path1).equals(getMD5Checksum(path2))
    }
}