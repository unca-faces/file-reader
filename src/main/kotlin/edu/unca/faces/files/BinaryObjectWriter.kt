package edu.unca.faces.files

import edu.unca.faces.files.util.ByteUtil
import java.io.File
import java.lang.reflect.Field
import java.nio.channels.FileChannel
import java.nio.channels.WritableByteChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class BinaryObjectWriter internal constructor (private val output: WritableByteChannel,
                                               private val obj: Any,
                                               private val integerFields: MutableMap<String, Field> = mutableMapOf(),
                                               private val parentWriter: ObjectWriter? = null,
                                               enableDebug: Boolean = false)
    : ObjectWriter(output, obj, integerFields, parentWriter, enableDebug) {

    override fun newWriter(output: WritableByteChannel, obj: Any, integerFields: MutableMap<String, Field>, parentWriter: ObjectWriter?, enableDebug: Boolean): ObjectWriter {
        return BinaryObjectWriter(output, obj, integerFields, parentWriter, enableDebug)
    }

    private constructor(obj: Any, path: Path, enableDebug: Boolean = false)
            : this(FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE), obj, enableDebug = enableDebug)

    override fun writeInt(field: Field, value: Int) {
        ByteUtil.writeInt(output, value)
    }

    override fun writeFloat(field: Field, value: Float) {
        ByteUtil.writeFloat(output, value)
    }

    override fun writeShort(field: Field, value: Short) {
        ByteUtil.writeShort(output, value)
    }

    override fun writeLong(field: Field, value: Long) {
        ByteUtil.writeLong(output, value)
    }

    override fun writeDouble(field: Field, value: Double) {
        ByteUtil.writeDouble(output, value)
    }

    override fun writeChar(field: Field, value: Char) {
        ByteUtil.writeChar(output, value)
    }

    override fun writeByte(field: Field, value: Byte) {
        ByteUtil.writeByte(output, value)
    }

    companion object {
        fun writeObjectToFile(obj: Any, file: File) {
            val writer = BinaryObjectWriter(obj, file.toPath())
            writer.writeObject()
        }


        fun writeObjectToFile(obj: Any, path: Path, enableDebug: Boolean = false) {
            val writer = BinaryObjectWriter(obj, path, enableDebug = enableDebug)
            writer.writeObject()
        }
    }
}