package edu.unca.faces.files

import edu.unca.faces.files.annotations.*
import edu.unca.faces.files.types.KnownType
import edu.unca.faces.files.util.*
import java.io.File
import java.io.IOException
import java.lang.annotation.AnnotationFormatError
import java.lang.reflect.Field
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel
import java.nio.file.StandardOpenOption
import java.util.function.Predicate

class ObjectReader internal constructor (private val input: ReadableByteChannel,
                                        objectProvider: (ReadableByteChannel) -> Any,
                                        private val integerFields: MutableMap<String, Field> = mutableMapOf(),
                                        private val parentReader: ObjectReader? = null) {

    private constructor(file: File) : this(FileChannel.open(file.toPath(), StandardOpenOption.READ),
            {input -> KnownType.getType(ByteUtil.readString(input, 8))?.createReadableType()
                    ?: throw IOException("Unknown file format")})

    val obj: Any = objectProvider(input)

    private val fields: List<Field> = ReflectionUtil.getIndexOrderedFields(obj::class.java)

    init {
        // Find any integer fields that may be the bound size for an array
        for (field in fields) {
            if (field.type == Int::class.java) {
                integerFields[field.name] = field
            }
        }
    }

    init {
        for (field in fields) {
            println(field)
            field.isAccessible = true
            //if (field.meetsConditions({c, f -> getIntegerField(c, f)})) {
            if (field.meetsConditions(this::getIntegerField)) {
                if (field.type.isArray) {
                    handleArray(field)
                } else {
                    handleNonArray(field)
                }
            }
        }
    }



    private fun handleNonArray(field: Field) {
        field.set(obj, extractValue(field.type, field))
    }

    private fun extractValue(type: Class<*>, field: Field): Any = when (type) {
        Int::class.java -> ByteUtil.readInt(input)
        Float::class.java -> ByteUtil.readFloat(input)
        Short::class.java -> ByteUtil.readShort(input)
        Double::class.java -> ByteUtil.readDouble(input)
        Long::class.java -> ByteUtil.readLong(input)
        Byte::class.java -> ByteUtil.readByte(input)
        Char::class.java -> ByteUtil.readChar(input)
        String::class.java -> {
            if (field.getAnnotation(NullTerminated::class.java) != null) {
                ByteUtil.readNullTerminatedString(input)
            } else {
                ByteUtil.readString(input)
            }
        }
        else -> {
            val o = try {
                type.newInstance()
            } catch (e: Exception) {
                throw IllegalArgumentException("The type ${type.name} is not supported in field ${field.name}")
            }
            ObjectReader(input, { o }, integerFields, this).obj
        }
    }

    private fun getIntegerField(name: String, field: Field): Int {
        val intField = integerFields[name] ?: throw AnnotationFormatError("There is no field "
                + "$name to bind which is required by $field")
        // The int field may belong to a parent object so we must find the parent that has it
        var o: Any = obj
        var nextParent: ObjectReader? = parentReader
        while (nextParent != null) {
            if (nextParent.obj::class.java.hasDeclaredField(name)) {
                o = nextParent.obj
                break
            }
            nextParent = nextParent.parentReader
        }
        return (intField.get(o) as Number).toInt()
    }

    private fun handleArray(field: Field) {
        val arraySizeAnnotation: ArraySize? = field.getAnnotation(ArraySize::class.java)
        val arraySize = arraySizeAnnotation?.value ?: -1

        val boundSizeAnnotation: BoundSize? = field.getAnnotation(BoundSize::class.java)
        val boundSize: Int = if (boundSizeAnnotation != null) {
            var size = 0
            for (boundFieldName in boundSizeAnnotation.value) {
                size += getIntegerField(boundFieldName, field)
            }
            size
        } else {
            -1
        }

        // Missing both annotations
        if (boundSize == -1 && arraySize == -1) {
            throw AnnotationFormatError("Array field ${field.name} must be annotated with ArraySize or BoundSize")
        }

        val twoD = field.type.componentType.isArray

        val arrayType: Class<*> = if (twoD) {
            // A 2D array
            field.type.componentType.componentType
        } else {
            field.type.componentType
        }

        if (twoD) {
            when (arrayType) {
                Int::class.java -> {
                    val array = ArrayUtil.create2DIntArray(boundSize, arraySize)
                    for (i in 0 until boundSize) {
                        for (j in 0 until arraySize) {
                            array[i][j] = extractValue(Int::class.java, field) as Int
                        }
                    }
                    field.set(obj, array)
                }
                Float::class.java -> {
                    val array = ArrayUtil.create2DFloatArray(boundSize, arraySize)
                    for (i in 0 until boundSize) {
                        for (j in 0 until arraySize) {
                            array[i][j] = extractValue(Float::class.java, field) as Float
                        }
                    }
                    field.set(obj, array)
                }
                Short::class.java -> {
                    val array = ArrayUtil.create2DShortArray(boundSize, arraySize)
                    for (i in 0 until boundSize) {
                        for (j in 0 until arraySize) {
                            array[i][j] = extractValue(Short::class.java, field) as Short
                        }
                    }
                    field.set(obj, array)
                }
                Char::class.java -> {
                    val array = ArrayUtil.create2DCharArray(boundSize, arraySize)
                    for (i in 0 until boundSize) {
                        for (j in 0 until arraySize) {
                            array[i][j] = extractValue(Char::class.java, field) as Char
                        }
                    }
                    field.set(obj, array)
                }
                else -> {
                    val array = ArrayUtil.create2DTypedArray(arrayType, boundSize, arraySize)
                    for (i in 0 until boundSize) {
                        for (j in 0 until arraySize) {
                            array[i][j] = extractValue(Char::class.java, field)
                        }
                    }
                    field.set(obj, array)
                }
            }
        } else {
            when (arrayType) {
                Int::class.java -> {
                    val array = ArrayUtil.createIntArray(boundSize, arraySize)
                    for (i in 0 until if (boundSize >= 0) boundSize else arraySize) {
                        array[i] = extractValue(Int::class.java, field) as Int
                    }
                    field.set(obj, array)
                }
                Float::class.java -> {
                    val array = ArrayUtil.createFloatArray(boundSize, arraySize)
                    for (i in 0 until if (boundSize >= 0) boundSize else arraySize) {
                        array[i] = extractValue(Float::class.java, field) as Float
                    }
                    field.set(obj, array)
                }
                Short::class.java -> {
                    val array = ArrayUtil.createShortArray(boundSize, arraySize)
                    for (i in 0 until if (boundSize >= 0) boundSize else arraySize) {
                        array[i] = extractValue(Short::class.java, field) as Short
                    }
                    field.set(obj, array)
                }
                Char::class.java -> {
                    val array = ArrayUtil.createCharArray(boundSize, arraySize)

                    for (i in 0 until if (boundSize >= 0) boundSize else arraySize) {
                        array[i] = extractValue(Char::class.java, field) as Char
                    }
                    field.set(obj, array)
                }
                String::class.java -> {
                    val array = ArrayUtil.createStringArray(boundSize, arraySize)
                    for (i in 0 until if (boundSize >= 0) boundSize else arraySize) {
                        array[i] = extractValue(String::class.java, field) as String
                    }
                    field.set(obj, array)
                }
                else -> {
                    val array = ArrayUtil.createTypedArray(arrayType, boundSize, arraySize)
                    for (i in 0 until if (boundSize >= 0) boundSize else arraySize) {
                        array[i] = extractValue(arrayType, field)
                    }
                    println(array)
                    field.set(obj, array)
                }
            }
        }
    }

    companion object {
        fun readFileToObject(file: File): Any {
            val reader = ObjectReader(file)
            return reader.obj
        }
    }
}