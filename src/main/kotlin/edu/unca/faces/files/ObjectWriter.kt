package edu.unca.faces.files

import edu.unca.faces.files.util.ReflectionUtil
import edu.unca.faces.files.util.hasDeclaredField
import edu.unca.faces.files.util.meetsConditions
import java.io.File
import java.lang.annotation.AnnotationFormatError
import java.lang.reflect.Field
import java.nio.channels.FileChannel
import java.nio.channels.WritableByteChannel
import java.nio.file.StandardOpenOption

class ObjectWriter internal constructor (private val output: WritableByteChannel,
                                         private val obj: Any,
                                         private val integerFields: MutableMap<String, Field> = mutableMapOf(),
                                         private val parentReader: ObjectWriter? = null) {

    private constructor(obj: Any, file: File) : this(FileChannel.open(file.toPath(), StandardOpenOption.WRITE), obj)

    private val fields: List<Field> = ReflectionUtil.getIndexOrderedFields(obj::class.java)

    init {
        // Find any integer fields that may be the bound size for an array
        for (field in fields) {
            if (field.type == Int::class.java) {
                integerFields[field.name] = field
            }
        }
    }

    fun writeObject() {
        for (field in fields) {
            println(field)
            field.isAccessible = true
            if (field.meetsConditions(this::getIntegerField)) {
                if (field.type.isArray) {
                    //handleArray(field)
                } else {
                    //handleNonArray(field)
                }
            }
        }
    }

    private fun getIntegerField(name: String, field: Field): Int {
        val intField = integerFields[name] ?: throw AnnotationFormatError("There is no field "
                + "$name to bind which is required by $field")
        // The int field may belong to a parent object so we must find the parent that has it
        var o: Any = obj
        var nextParent: ObjectWriter? = parentReader
        while (nextParent != null) {
            if (nextParent.obj::class.java.hasDeclaredField(name)) {
                o = nextParent.obj
                break
            }
            nextParent = nextParent.parentReader
        }
        return (intField.get(o) as Number).toInt()
    }
}