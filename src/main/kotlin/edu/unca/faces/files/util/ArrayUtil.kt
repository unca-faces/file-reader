package edu.unca.faces.files.util

object ArrayUtil {

    fun createIntArray(boundSize: Int, arraySize: Int) = IntArray(if (boundSize >= 0) { boundSize } else { arraySize })
    fun create2DIntArray(boundSize: Int, arraySize: Int) = Array(boundSize) { IntArray(arraySize) }

    fun createShortArray(boundSize: Int, arraySize: Int) = ShortArray(if (boundSize >= 0) { boundSize } else { arraySize })
    fun create2DShortArray(boundSize: Int, arraySize: Int) = Array(boundSize) { ShortArray(arraySize) }

    fun createFloatArray(boundSize: Int, arraySize: Int) = FloatArray(if (boundSize >= 0) { boundSize } else { arraySize })
    fun create2DFloatArray(boundSize: Int, arraySize: Int) = Array(boundSize) { FloatArray(arraySize) }

    fun createCharArray(boundSize: Int, arraySize: Int) = CharArray(if (boundSize >= 0) { boundSize } else { arraySize })
    fun create2DCharArray(boundSize: Int, arraySize: Int) = Array(boundSize) { CharArray(arraySize) }

    fun createStringArray(boundSize: Int, arraySize: Int): Array<String> = Array(if (boundSize >= 0) { boundSize } else { arraySize }) { "" }
}