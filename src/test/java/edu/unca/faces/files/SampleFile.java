package edu.unca.faces.files;

import edu.unca.faces.files.annotations.ArraySize;
import edu.unca.faces.files.annotations.BoundSize;
import edu.unca.faces.files.annotations.Index;
import edu.unca.faces.files.annotations.NullTerminated;

import java.util.Arrays;

public class SampleFile {

    static byte[] bytes = {
            0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, // id
            0x03, 0x00, 0x00, 0x00, // V
            0x01, 0x00, 0x00, 0x00, // K
            0x31, 0x00, 0x32, 0x00, 0x33, 0x00, 0x41, 0x42, 0x43, 0x00, // sArray1
            0x03, 0x00, 0x00, 0x00, 0x41, 0x42, 0x43, // sArray2
            0x41, 0x42, 0x43, 0x00, // sNullTerm
            0x00, 0x00, 0x00, 0x40, 0x00, 0x00, -0x80, 0x3f, 0x00, 0x00, -0x80, 0x3f, 0x00, 0x00, 0x00, 0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, // floats2d
            0x01, 0x00, // shorts
            0x02, 0x00, 0x00, 0x00, // X
            0x03, 0x00, 0x05, 0x00, // xShorts
            0x02, 0x00, // shorts x2
            0x02, 0x00, 0x00, 0x00, // X x2
            0x04, 0x00, 0x06, 0x00, // xShorts x2
    };

    static char[] idExpected = "ABCDEFGH".toCharArray();
    @Index(0) @ArraySize(8) char[] id;

    static int VExpected = 3;
    @Index(1) int V;

    static int KExpected = 1;
    @Index(2) int K;

    static String[] sArray1Expected = {"1", "2", "3", "ABC"};
    @Index(3) @BoundSize({"V", "K"}) @NullTerminated String[] sArray1;

    static String[] sArray2Expected = {"ABC"};
    @Index(4) @BoundSize({"K"}) String[] sArray2;

    static String sNullTermExpected = "ABC";
    @Index(5) @NullTerminated String sNullTerm;

    static float[][] floats2dExpected = {{2F, 1F}, {1F, 2F}, {0F, 0F}};
    @Index(6) @BoundSize("V") @ArraySize(2) float[][] floats2d;

    static SampleChild[] childrenExpected;
    static {
        childrenExpected = new SampleChild[2];
        SampleChild c = new SampleChild();
        c.shorts = new short[1];
        c.shorts[0] = 1;
        c.X = 2;
        c.xShorts = new short[2];
        c.xShorts[0] = 3;
        c.xShorts[1] = 5;
        childrenExpected[0] = c;

        c = new SampleChild();
        c.shorts = new short[1];
        c.shorts[0] = 2;
        c.X = 2;
        c.xShorts = new short[2];
        c.xShorts[0] = 4;
        c.xShorts[1] = 6;
        childrenExpected[1] = c;
    }
    @Index(7) @ArraySize(2) SampleChild[] children;

    static class SampleChild {
        @Index(0) @BoundSize("K") short[] shorts;

        @Index(1) int X;

        @Index(2) @BoundSize("X") short[] xShorts;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SampleChild that = (SampleChild) o;

            return X == that.X && Arrays.equals(shorts, that.shorts) && Arrays.equals(xShorts, that.xShorts);
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(shorts);
            result = 31 * result + X;
            result = 31 * result + Arrays.hashCode(xShorts);
            return result;
        }
    }
}
