package edu.unca.faces.files.annotations

/**
 * This annotation represents the expected size of the array in the serialized data.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ArraySize(val value: Int)