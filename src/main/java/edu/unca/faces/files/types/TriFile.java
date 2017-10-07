package edu.unca.faces.files.types;

import edu.unca.faces.files.ReadableType;
import edu.unca.faces.files.annotations.ArraySize;
import edu.unca.faces.files.annotations.BoundSize;
import edu.unca.faces.files.annotations.ConditionalField;
import edu.unca.faces.files.annotations.Conditions;
import edu.unca.faces.files.annotations.Index;
import edu.unca.faces.files.annotations.NullTerminated;
import edu.unca.faces.files.annotations.Reserved;

import java.util.function.Predicate;

public class TriFile extends ReadableType {

    TriFile(char[] magicNumber) {
        super(magicNumber);
    }

    /** Number of vertices */
    @Index(1) int V;
    /** Number of triangles */
    @Index(2) int T;
    /** Number of quads */
    @Index(3) int Q;
    /** Number of labelled vertices */
    @Index(4) int LV;
    /** Number of labelled surface points */
    @Index(5) int LS;
    /** Number of texture coordinates */
    @Index(6) int X;
    /**
     * Extension Info
     * <br>
     * ext & 0x01     True if there are texture coordinates.
     * <br>
     * ext & 0x02     True if surface point labels are 16bit chars.
     */
    @Index(7) int ext;
    /** Number of labelled difference morphs */
    @Index(8) int Md;
    /** Number of labelled stat morphs */
    @Index(9) int Ms;
    /** Number of stat morph vertices */
    @Index(10) int K;
    /** Reserved for future use. Set to 0 when saving. */
    @Index(11) @Reserved(16) char[] reserved;
    /** float, float, float, in order X,Y,Z (coordinate system varies) */
    @Index(12) @BoundSize({"V", "K"}) @ArraySize(3) float[][] vert;
    /** int,int,int, the vertex indices of the tri facets. */
    @Index(13) @BoundSize("T") @ArraySize(3) int[][] tri;
    /** int,int,int,int, the vertex indeices of the quad facets. */
    @Index(14) @BoundSize("Q") @ArraySize(4) int[][] quad;
    @Index(15) @BoundSize("LV") String[] vlabels;
    @Index(16) @BoundSize("LS") String[] slabels;

    // ... If per-vertex texture coordinates (ie X == 0 and [<ext> & 0x01] == true): ...
    /** float, float in OpenGL texture coordinate system. */
    @Index(17) @Conditions({PerVertexCondition.class, ExtCondition1.class})
    @BoundSize("V") @ArraySize(2) float[][] perVertexTex;

    // ... Else if per-facet texture coordinates (ie X > 0 and [<ext> & 0x01] == true): ...
    /** float, float in OpenGL texture coordinate system. */
    @Index(18) @Conditions({PerFacetCondition.class, ExtCondition1.class})
    @BoundSize("X") @ArraySize(2) float[][] perFacetTex;
    /** int,int,int Indexes into the texture point list. */
    @Index(19) @Conditions({PerFacetCondition.class, ExtCondition1.class})
    @BoundSize("T") @ArraySize(3) int[][] ttInd;
    /** int,int,int,int Indexes into the texture point list. */
    @Index(20) @Conditions({PerFacetCondition.class, ExtCondition1.class})
    @BoundSize("Q") @ArraySize(4) int[][] qtInd;

    // ... And if Md > 0: ...
    @Index(21) @Conditions(DifferenceMorphsCondition.class) @BoundSize("Md") DifferenceMorph[] differenceMorphs;

    // ... And if Ms > 0: ...
    @Index(22) @Conditions(StatMorphsCondition.class) @BoundSize("Ms") StatMorph[] statMorphs;

    public static class DifferenceMorph {
        @Index(0) @NullTerminated String label;
        @Index(1) Delta deltas;

        public static class Delta {
            @Index(0) float scale;
            @Index(1) @BoundSize("V") @ArraySize(3) short[][] delta;
        }
    }

    public static class StatMorph {
        @Index(0) @NullTerminated String label;
        /** int. Number of model vertices affected */
        @Index(1) int L;
        @Index(2) @BoundSize("L") int[] VtxIdxList;

        public static class Delta {
            @Index(0) float scale;
            @Index(1) @BoundSize("V") @ArraySize(3) short delta;
        }
    }

    public static @ConditionalField("X") class PerVertexCondition implements Predicate<Integer> {
        @Override
        public boolean test(Integer X) {
            return X == 0;
        }
    }
    public static @ConditionalField("ext") class ExtCondition1 implements Predicate<Integer> {
        @Override
        public boolean test(Integer ext) {
            return (ext & 0x01) == 0x01;
        }
    }
    public static @ConditionalField("X") class PerFacetCondition implements Predicate<Integer> {
        @Override
        public boolean test(Integer X) {
            return X > 0;
        }
    }
    public static @ConditionalField("Md") class DifferenceMorphsCondition implements Predicate<Integer> {
        @Override
        public boolean test(Integer Md) {
            return Md > 0;
        }
    }
    public static @ConditionalField("Ms") class StatMorphsCondition implements Predicate<Integer> {
        @Override
        public boolean test(Integer Ms) {
            return Ms > 0;
        }
    }
}
