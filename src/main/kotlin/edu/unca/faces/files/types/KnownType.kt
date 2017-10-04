package edu.unca.faces.files.types

import edu.unca.faces.files.ReadableType

enum class KnownType(magicNumber: CharSequence, val objectClass: Class<out ReadableType>) {
    TRI("FRTRI003", TriFile::class.java);

    val magicNumber: String = magicNumber.toString()

    fun createReadableType(): ReadableType {
        val constructor = objectClass.getDeclaredConstructor(Array<Char>::class.java)
        return constructor.newInstance(magicNumber.toCharArray()) as ReadableType
    }

    companion object {

        private val magicNumberMap: Map<CharSequence, KnownType> by lazy {
            val result = mutableMapOf<CharSequence, KnownType>()
            for (type in KnownType.values()) {
                result[type.magicNumber] = type
            }
            result
        }

        @JvmStatic
        fun getType(magicNumber: CharSequence) = magicNumberMap[magicNumber]
    }
}