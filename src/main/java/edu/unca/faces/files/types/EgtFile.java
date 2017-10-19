package edu.unca.faces.files.types;

import edu.unca.faces.files.annotations.*;

public class EgtFile {

    @Index(0) @ArraySize(8) private char[] magicNumber;

    @Index(1) int R;
    @Index(2) int C;
    @Index(3) int S;
    @Index(4) int A;
    @Index(5) int textureBasisVersion;
    @Index(6) @Reserved(36) char[] reserved;

    @Index(7) @BoundSize("S") TextureMode[] symmetricTextureModes;
    @Index(8) @BoundSize("A") TextureMode[] asymmetricTextureModes;

    public static class TextureMode {
        @Index(0) float s;
        @Index(1) Image r;
        @Index(2) Image g;
        @Index(3) Image b;

        public static class Image {
            @Index(0) @BoundSize("C") @BoundSize2D("R") char[][] value;
        }
    }
}
