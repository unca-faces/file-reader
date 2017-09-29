package edu.unca.faces.files.types

enum class KnownTypes(val magicNumber: String, val objectClass: Class<*>) {
    TRI("FRTRI003", TriFile::class.java);
}