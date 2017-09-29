package edu.unca.faces.files

import edu.unca.faces.files.annotations.ArraySize
import edu.unca.faces.files.annotations.Index

abstract class ReadableType(@JvmField @Index(0) @ArraySize(8) val magicNumber: Array<Char>)