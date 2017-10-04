package edu.unca.faces.files

import edu.unca.faces.files.types.KnownType
import edu.unca.faces.files.util.ByteUtil
import edu.unca.faces.files.util.ReflectionUtil
import java.io.File
import java.lang.reflect.Field
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption

class ObjectReader constructor (file: File) {

    private val input = FileChannel.open(file.toPath(), StandardOpenOption.READ)

    val type: KnownType = KnownType.getType(ByteUtil.readString(input, 8))!!
    val obj: ReadableType = type.createReadableType()

    private val fields: List<Field> = ReflectionUtil.getIndexOrderedFields(type.objectClass)

    private val primitiveFields: Map<String, Field> by lazy {
        val primitiveFields = mutableMapOf<String, Field>()
        for (field in fields) {
            if (field.type.isPrimitive) {
                primitiveFields[field.name] = field
            }
        }
        primitiveFields
    }

    init {
        fields
                .filter { it.type.isPrimitive }
                .forEach {
                    when (it.type) {
                        Int::class.java -> it.set(obj, ByteUtil.readInt(input))
                        Float::class.java -> it.set(obj, ByteUtil.readFloat(input))
                        Short::class.java -> it.set(obj, ByteUtil.readShort(input))
                        Double::class.java -> it.set(obj, ByteUtil.readDouble(input))
                        Long::class.java -> it.set(obj, ByteUtil.readLong(input))
                        Byte::class.java -> it.set(obj, ByteUtil.readByte(input))
                        Char::class.java -> it.set(obj, ByteUtil.readChar(input))
                    }
                }
    }

    companion object {
        inline fun <reified T : ReadableType> readFileToObject(file: File): T {
            val reader = ObjectReader(file)
            if (reader.obj is T) {
                return reader.obj
            } else {
                throw IllegalArgumentException("Given file does not represent an known type")
            }
        }
    }
}