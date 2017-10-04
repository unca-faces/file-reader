package edu.unca.faces.files.util

import edu.unca.faces.files.ReadableType
import edu.unca.faces.files.annotations.Index
import java.lang.reflect.Field
import java.lang.reflect.Modifier

internal object ReflectionUtil {

    @JvmStatic
    fun getIndexOrderedFields(clazz: Class<*>): List<Field> {
        val fieldList = mutableListOf<Field>()
        for (field in clazz.declaredFields) {
            if (field.modifiers != Modifier.STATIC
                    && field.modifiers != Modifier.TRANSIENT) {
                if (field.getAnnotation(Index::class.java) == null) {
                    throw IllegalArgumentException("Field $field does not have required @Index annotation")
                }
                fieldList.add(field);
            }
        }
        // Sort the fields by index
        fieldList.sortWith(object : Comparator<Field> {
            override fun compare(f1: Field, f2: Field): Int {
                val i1 = f1.getAnnotation(Index::class.java).value
                val i2 = f2.getAnnotation(Index::class.java).value
                val result = i1 - i2
                return if (result != 0) result else throw IllegalArgumentException("Fields $f1 and $f2 have the "
                        + "same @Index value")
            }
        })

        return fieldList
    }
}