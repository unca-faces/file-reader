package edu.unca.faces.files.annotations

/**
 * This annotation represents the expected size of the array in the serialized data which is based on the value of
 * some other field
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class BoundSize2D(val value: Array<String>)