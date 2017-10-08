package edu.unca.faces.files.types

enum class KnownType(magicNumber: CharSequence, val objectClass: Class<*>) {
    TRI("FRTRI003", TriFile::class.java);

    val magicNumber: String = magicNumber.toString()

    fun createObject(): Any = objectClass.getDeclaredConstructor().newInstance()

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