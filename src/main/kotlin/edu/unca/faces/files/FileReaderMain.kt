package edu.unca.faces.files

import com.google.gson.GsonBuilder
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import edu.unca.faces.files.util.HashUtil
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class FileReaderMain {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = mainBody {
            val opts = ProgramOptions(ArgParser(args))

            val input = opts.input
            val output = if (opts.output != null) {
                opts.output!!
            } else {
                if (opts.json) {
                    Paths.get("$input.json")
                } else {
                    Paths.get("${opts.inputName}.rewrite${if (opts.inputExt.isNotEmpty()) ".${opts.inputExt}" else ""}")
                }
            }

            val parentDir = output.parent
            if (parentDir != null) {
                parentDir.toFile().mkdirs()
            }

            val obj = ObjectReader.readFileToObject(opts.input, opts.enableDebug)

            if (opts.json) {
                val gson = GsonBuilder()
                if (opts.prettyPrint) gson.setPrettyPrinting()

                Files.write(output, listOf(gson.create().toJson(obj)))
                println("Object file converted to JSON")
            } else {
                BinaryObjectWriter.writeObjectToFile(obj, output)
                if (HashUtil.checksumMatches(input, output)) {
                    println("Checksums match")
                } else {
                    println("Checksums do not match")
                }
            }
        }
    }

    class ProgramOptions(parser: ArgParser) {
        val input by parser.positional("INPUT", help = "name of input file") { Paths.get(this) }
        val output by parser.storing("-o", "--output", help = "name of the output file") { Paths.get(this) }.default({ null })
        val json by parser.flagging("-j", "--json", help = "produce a json output file")
        val prettyPrint by parser.flagging("-p", "--pretty-print", help = "pretty print json")
        val enableDebug by parser.flagging("-d", "--enable-debug", help = "enable debug output")

        val inputName by lazy {
            val path = input.toString()
            val dotIndex = path.lastIndexOf(".")
            if (dotIndex != -1) {
                path.substring(0, dotIndex)
            } else {
                path
            }
        }

        val inputExt by lazy {
            val path = input.toString()
            val dotIndex = path.lastIndexOf(".")
            if (dotIndex != -1 && dotIndex < path.length - 1) {
                path.substring(dotIndex + 1, path.length)
            } else {
                ""
            }
        }
    }
}