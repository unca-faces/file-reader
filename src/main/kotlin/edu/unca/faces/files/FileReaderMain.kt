package edu.unca.faces.files

import java.io.File
import kotlin.system.exitProcess

class FileReaderMain {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size < 1) {
                println("Must specify file to read")
                exitProcess(1)
            }
            val reader = ObjectReader(File(args[0]))
        }
    }
}