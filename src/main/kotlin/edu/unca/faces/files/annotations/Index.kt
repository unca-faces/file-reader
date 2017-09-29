package edu.unca.faces.files.annotations

/**
 * This annotation represents the orderable index that a field should exist within the associated types file.
 *
 * Fields of a [ReadableType] must be annotated with this. The given index value is the order at which the fields
 * appear in the file. In a [ReadableType] implementation the index must start at 1 since the order of the magic number
 * is index 0.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Index(val value: Int)