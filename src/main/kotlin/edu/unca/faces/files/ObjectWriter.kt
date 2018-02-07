package edu.unca.faces.files

import edu.unca.faces.files.annotations.NullTerminated
import edu.unca.faces.files.annotations.Reserved
import edu.unca.faces.files.util.ByteUtil
import edu.unca.faces.files.util.ReflectionUtil
import edu.unca.faces.files.util.hasDeclaredField
import edu.unca.faces.files.util.meetsConditions
import java.io.File
import java.lang.annotation.AnnotationFormatError
import java.lang.reflect.Field
import java.nio.channels.FileChannel
import java.nio.channels.WritableByteChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption

abstract class ObjectWriter internal constructor (private val output: WritableByteChannel,
                                                  private val obj: Any,
                                                  private val integerFields: MutableMap<String, Field> = mutableMapOf(),
                                                  private val parentWriter: ObjectWriter? = null,
                                                  private val enableDebug: Boolean = false) {

    private constructor(obj: Any, path: Path, enableDebug: Boolean = false)
            : this(FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE), obj, enableDebug = enableDebug)

    private val fields: List<Field> = ReflectionUtil.getIndexOrderedFields(obj::class.java)

    init {
        // Find any integer fields that may be the bound size for an array
        for (field in fields) {
            if (field.type == Int::class.java) {
                integerFields[field.name] = field
            }
        }
    }

    protected fun debug(messageCall: () -> Any) {
        if (enableDebug) println(messageCall())
    }

    abstract fun newWriter(output: WritableByteChannel, obj: Any,
                           integerFields: MutableMap<String, Field> = mutableMapOf(),
                           parentWriter: ObjectWriter? = null,
                           enableDebug: Boolean = false): ObjectWriter

    fun writeObject() {
        for (field in fields) {
            debug { field }
            field.isAccessible = true
            if (field.meetsConditions(this::getIntegerField)) {
                val reservedAmount = field.getAnnotation(Reserved::class.java)?.value
                if (reservedAmount != null) {
                    if (field.type.isArray && field.type.componentType == Char::class.java) {
                        writeReserved(field, reservedAmount)
                    } else {
                        throw AnnotationFormatError("Reserved annotation can only be use on char[] field")
                    }
                } else if (field.type.isArray) {
                    handleArray(field)
                } else {
                    handleNonArray(field)
                }
            }
        }
        if (parentWriter == null) output.close()
    }

    fun writeReserved(field: Field, reservedAmount: Int) {
        ByteUtil.writeNulls(output, reservedAmount)
    }

    private fun getIntegerField(name: String, field: Field): Int {
        val intField = integerFields[name] ?: throw AnnotationFormatError("There is no field "
                + "$name to bind which is required by $field")
        // The int field may belong to a parent object so we must find the parent that has it
        var o: Any = obj
        var nextParent: ObjectWriter? = parentWriter
        while (nextParent != null) {
            if (nextParent.obj::class.java.hasDeclaredField(name)) {
                o = nextParent.obj
                break
            }
            nextParent = nextParent.parentWriter
        }
        return (intField.get(o) as Number).toInt()
    }

    private fun handleArray(field: Field) {
        val componentType = if (field.type.componentType.isArray) field.type.componentType.componentType else field.type.componentType
        val twoD = field.type.componentType.isArray

        val value = field.get(obj)

        if (twoD) {
            val array = value as Array<*>
            for (e in array) {
                if (e != null) handle1DArray(field, e, componentType)
            }
        } else {
            handle1DArray(field, value, componentType)
        }
    }

    private fun handle1DArray(field: Field, arrayObj: Any, componentType: Class<*>) {
        val array = when (arrayObj::class.java) {
            IntArray::class.java -> (arrayObj as IntArray).toTypedArray()
            FloatArray::class.java -> (arrayObj as FloatArray).toTypedArray()
            ShortArray::class.java -> (arrayObj as ShortArray).toTypedArray()
            DoubleArray::class.java -> (arrayObj as DoubleArray).toTypedArray()
            LongArray::class.java -> (arrayObj as LongArray).toTypedArray()
            CharArray::class.java -> (arrayObj as CharArray).toTypedArray()
            ByteArray::class.java -> (arrayObj as ByteArray).toTypedArray()
            else -> arrayObj as Array<*>
        }
        for (e in array) {
            if (e != null) writeValue(field, componentType, e)
        }
    }

    private fun handleNonArray(field: Field) {
        field.isAccessible = true
        val value = field.get(obj)

        if (value != null) {
            writeValue(field, field.type, value)
        }
    }

    private fun writeValue(field: Field, type: Class<*>, value: Any) {
        when (type) {
            Int::class.java -> writeInt(field, value as Int)
            Float::class.java -> ByteUtil.writeFloat(output, value as Float)
            Short::class.java -> ByteUtil.writeShort(output, value as Short)
            Long::class.java -> ByteUtil.writeLong(output, value as Long)
            Double::class.java -> ByteUtil.writeDouble(output, value as Double)
            Char::class.java -> ByteUtil.writeChar(output, value as Char)
            Byte::class.java -> ByteUtil.writeByte(output, value as Byte)
            String::class.java -> {
                if (field.getAnnotation(NullTerminated::class.java) != null) {
                    writeNullTerminatedString(field, value as String)
                } else {
                    ByteUtil.writeString(output, value as String)
                }
            }
            else -> {
//                val o = try {
//                    type.getDeclaredConstructor().newInstance()
//                } catch (e: Exception) {
//                    throw IllegalArgumentException("The type ${type.name} is not supported in field ${field.name}")
//                }
                newWriter(output, value, integerFields, this).writeObject()
            }
        }
    }

    internal inline fun <reified T> writePrimitive(field: Field, value: T) {
        when (T::class.java) {
            Float::class.java -> ByteUtil.writeFloat(output, value as Float)
            Short::class.java -> ByteUtil.writeShort(output, value as Short)
            Long::class.java -> ByteUtil.writeLong(output, value as Long)
            Double::class.java -> ByteUtil.writeDouble(output, value as Double)
            Char::class.java -> ByteUtil.writeChar(output, value as Char)
            Byte::class.java -> ByteUtil.writeByte(output, value as Byte)
            else -> throw IllegalArgumentException("$field is primitive but not parseable.")
        }
    }

    abstract fun writeInt(field: Field, value: Int)

    abstract fun writeFloat(field: Field, value: Float)

    abstract fun writeShort(field: Field, value: Short)

    abstract fun writeLong(field: Field, value: Long)

    abstract fun writeDouble(field: Field, value: Double)

    abstract fun writeChar(field: Field, value: Char)

    abstract fun writeByte(field: Field, value: Byte)

    fun writeNullTerminatedString(field: Field, value: String) {
        ByteUtil.writeNullTerminatedString(output, value)
    }


}