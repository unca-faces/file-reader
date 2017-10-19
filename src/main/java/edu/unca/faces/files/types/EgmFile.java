package edu.unca.faces.files.types;

import edu.unca.faces.files.annotations.ArraySize;
import edu.unca.faces.files.annotations.BoundSize;
import edu.unca.faces.files.annotations.Index;
import edu.unca.faces.files.annotations.Reserved;

public class EgmFile {

    @Index(0) @ArraySize(8) private char[] magicNumber;

    @Index(1) int V;
    @Index(2) int S;
    @Index(3) int A;
    @Index(4) int geometryBasisVersion;
    @Index(5) @Reserved(40) char[] reserved;

    @Index(6) @BoundSize("S") MorphMode[] symmetricMorphModes;
    @Index(7) @BoundSize("A") MorphMode[] asymmetricMorphModes;

    public static class MorphMode {
        @Index(0) float x;
        @Index(1) @BoundSize("V") @ArraySize(3) short[][] values;
    }
}
