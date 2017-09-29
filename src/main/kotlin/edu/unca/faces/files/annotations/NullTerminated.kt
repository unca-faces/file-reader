package edu.unca.faces.files.annotations

/**
 * Indicates this field should be terminated by null.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class NullTerminated