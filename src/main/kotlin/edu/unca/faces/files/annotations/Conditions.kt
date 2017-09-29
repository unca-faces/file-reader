package edu.unca.faces.files.annotations

import java.util.function.Predicate
import kotlin.reflect.KClass

/**
 * This annotation represents the expected size of the array in the serialized data.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Conditions(val value: Array<KClass<out Predicate<*>>>)