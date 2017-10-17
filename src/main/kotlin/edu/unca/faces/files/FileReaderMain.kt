package edu.unca.faces.files

import edu.unca.faces.files.util.HashUtil
import java.nio.file.Paths
import kotlin.system.exitProcess

class FileReaderMain {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size < 1) {
                println("Must specify file to read as first param")
                return
            }
            if (args.size < 2) {
                println("Must specify file to write as second param")
                return
            }

            val originalFile = Paths.get(args[0])
            val rewrittenFile = Paths.get(args[1])

            val parentDir = rewrittenFile.parent
            if (parentDir != null) {
                parentDir.toFile().mkdirs()
            }

            val obj = ObjectReader.readFileToObject(originalFile)
            ObjectWriter.writeObjectToFile(obj, rewrittenFile)

            if (HashUtil.checksumMatches(originalFile, rewrittenFile)) {
                println("Checksums match")
            } else {
                println("Checksums do not match")
            }
        }
    }
}