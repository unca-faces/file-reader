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
                exitProcess(1)
            }
            if (args.size < 2) {
                println("Must specify file to write as second param")
                exitProcess(2)
            }

            val originalFile = Paths.get(args[0])
            val rewrittenFile = Paths.get(args[1])

            rewrittenFile.parent.toFile().mkdirs()

            val obj = ObjectReader.readFileToObject(originalFile)
            ObjectWriter.writeObjectToFile(obj, rewrittenFile)

            if (HashUtil.checksumMatches(originalFile, rewrittenFile)) {
                println("Checksums match")
            } else {
                println("Checksums do not match")
                exitProcess(3)
            }
        }
    }
}