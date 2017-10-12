package edu.unca.faces.files.util

import edu.unca.faces.files.annotations.ConditionalField
import edu.unca.faces.files.annotations.Conditions
import edu.unca.faces.files.annotations.Index
import java.lang.annotation.AnnotationFormatError
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.function.Predicate
import java.util.Arrays
import java.util.ArrayList



internal fun Field.meetsConditions(intFieldGetter: (String, Field) -> Int): Boolean {
    val conditions = this.getAnnotation(Conditions::class.java)
    if (conditions != null) {
        for (c in conditions.value) {
            val conditionalFieldName = c.java.getAnnotation(ConditionalField::class.java)?.value
                    ?: throw AnnotationFormatError("Condition class $c must be annotated with a ConditionalField")
            val conditionalValue = intFieldGetter(conditionalFieldName, this)

            val constructor = try {
                c.java.getDeclaredConstructor()
            } catch (e: NoSuchMethodException) {
                throw IllegalArgumentException("Condition classes must have no arg constructor", e)
            }
            constructor.isAccessible = true

            val predicate = constructor.newInstance() as Predicate<Int>
            if (!predicate.test(conditionalValue)) return false
        }
    }
    return true
}

fun Class<*>.hasDeclaredField(name: String): Boolean {
    val fields = this.getDeclaredFields()
    for (field in fields) {
        if (field.name == name) {
            return true
        }
    }
    return false
}

internal object ReflectionUtil {

    @JvmStatic
    fun getIndexOrderedFields(clazz: Class<*>): List<Field> {
        val fieldList = mutableListOf<Field>()
        for (field in getAllFields(clazz)) {
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

    fun getAllFields(clazz: Class<*>): List<Field> {
        val fields = ArrayList<Field>()
        var c: Class<*>? = clazz
        while (c != null) {
            fields.addAll(Arrays.asList(*c.declaredFields))
            c = c.superclass
        }
        return fields
    }
}