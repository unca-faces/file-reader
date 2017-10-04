package edu.unca.faces.files

import edu.unca.faces.files.annotations.ArraySize
import edu.unca.faces.files.annotations.BoundSize
import edu.unca.faces.files.types.KnownType
import edu.unca.faces.files.util.ArrayUtil
import edu.unca.faces.files.util.ByteUtil
import edu.unca.faces.files.util.ReflectionUtil
import java.io.File
import java.io.IOException
import java.lang.annotation.AnnotationFormatError
import java.lang.reflect.Field
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption

class ObjectReader constructor (file: File) {

    private val input = FileChannel.open(file.toPath(), StandardOpenOption.READ)

    val type: KnownType = KnownType.getType(ByteUtil.readString(input, 8))!!
    val obj: ReadableType = type.createReadableType()

    private val fields: List<Field> = ReflectionUtil.getIndexOrderedFields(type.objectClass)

    private val integerFields: Map<String, Field> by lazy {
        val integerFields = mutableMapOf<String, Field>()
        for (field in fields) {
            if (field.type == Int::class.java) {
                integerFields[field.name] = field
            }
        }
        integerFields
    }

    init {

        for (field in fields) {
            field.isAccessible = true
            if (field.type.isPrimitive) {
                handlePrimitiveField(field)
            } else if (field.type.isArray) {
                handleArray(field)
            }
        }
    }

    private fun handlePrimitiveField(field: Field) {
        when (field.type) {
            Int::class.java -> field.set(obj, ByteUtil.readInt(input))
            Float::class.java -> field.set(obj, ByteUtil.readFloat(input))
            Short::class.java -> field.set(obj, ByteUtil.readShort(input))
            Double::class.java -> field.set(obj, ByteUtil.readDouble(input))
            Long::class.java -> field.set(obj, ByteUtil.readLong(input))
            Byte::class.java -> field.set(obj, ByteUtil.readByte(input))
            Char::class.java -> field.set(obj, ByteUtil.readChar(input))
            else -> throw IOException("The primitive type ${field.type} is not supported")
        }
    }

    private fun handleArray(field: Field) {
        val arraySizeAnnotation: ArraySize? = field.getAnnotation(ArraySize::class.java)
        val arraySize = arraySizeAnnotation?.value ?: -1

        val boundSizeAnnotation: BoundSize? = field.getAnnotation(BoundSize::class.java)
        val boundSize: Int = if (boundSizeAnnotation != null) {
            var size = 0
            for (boundFieldName in boundSizeAnnotation.value) {
                val intField = integerFields[boundFieldName] ?: throw AnnotationFormatError("There is no field "
                        + "$boundFieldName to bind the size of ${field.name} to")
                size += (intField.get(obj) as Number).toInt()
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
                    for (i in 0..boundSize) {
                        for (j in 0..arraySize) {
                            array[i][j] = ByteUtil.readInt(input)
                        }
                    }
                    field.set(obj, array)
                }
                Float::class.java -> {
                    val array = ArrayUtil.create2DFloatArray(boundSize, arraySize)
                    for (i in 0..boundSize) {
                        for (j in 0..arraySize) {
                            array[i][j] = ByteUtil.readFloat(input)
                        }
                    }
                    field.set(obj, array)
                }
                Short::class.java -> {
                    val array = ArrayUtil.create2DShortArray(boundSize, arraySize)
                    for (i in 0..boundSize) {
                        for (j in 0..arraySize) {
                            array[i][j] = ByteUtil.readShort(input)
                        }
                    }
                    field.set(obj, array)
                }
                Char::class.java -> {
                    val array = ArrayUtil.create2DCharArray(boundSize, arraySize)
                    for (i in 0..boundSize) {
                        for (j in 0..arraySize) {
                            array[i][j] = ByteUtil.readChar(input)
                        }
                    }
                    field.set(obj, array)
                }
                else -> throw IllegalArgumentException("Array ${field.name} of type $arrayType is not supported")
            }
        } else {
            when (arrayType) {
                Int::class.java -> {
                    val array = ArrayUtil.createIntArray(boundSize, arraySize)
                    for (i in 0..if (boundSize >= 0) boundSize else arraySize) {
                        array[i] = ByteUtil.readInt(input)
                    }
                    field.set(obj, array)
                }
                Float::class.java -> {
                    val array = ArrayUtil.createFloatArray(boundSize, arraySize)
                    for (i in 0..if (boundSize >= 0) boundSize else arraySize) {
                        array[i] = ByteUtil.readFloat(input)
                    }
                    field.set(obj, array)
                }
                Short::class.java -> {
                    val array = ArrayUtil.createShortArray(boundSize, arraySize)
                    for (i in 0..if (boundSize >= 0) boundSize else arraySize) {
                        array[i] = ByteUtil.readShort(input)
                    }
                    field.set(obj, array)
                }
                Char::class.java -> {
                    val array = ArrayUtil.createCharArray(boundSize, arraySize)
                    for (i in 0..if (boundSize >= 0) boundSize else arraySize) {
                        array[i] = ByteUtil.readChar(input)
                    }
                    field.set(obj, array)
                }
                else -> throw IllegalArgumentException("Array ${field.name} of type $arrayType is not supported")
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