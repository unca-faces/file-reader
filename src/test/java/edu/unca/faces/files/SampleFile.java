package edu.unca.faces.files;

import edu.unca.faces.files.annotations.ArraySize;
import edu.unca.faces.files.annotations.BoundSize;
import edu.unca.faces.files.annotations.Index;
import edu.unca.faces.files.annotations.NullTerminated;

public class SampleFile {

    static byte[] bytes = {
            0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, // id
            0x03, 0x00, 0x00, 0x00, // V
            0x01, 0x00, 0x00, 0x00, // K
            0x31, 0x00, 0x32, 0x00, 0x33, 0x00, 0x41, 0x42, 0x43, 0x00, // sArray1
            0x03, 0x00, 0x00, 0x00, 0x41, 0x42, 0x43, // sArray2
            0x41, 0x42, 0x43, 0x00, // sNullTerm
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
}
