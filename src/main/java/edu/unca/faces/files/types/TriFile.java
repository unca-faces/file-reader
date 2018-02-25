package edu.unca.faces.files.types;

import edu.unca.faces.files.annotations.ArraySize;
import edu.unca.faces.files.annotations.BoundSize;
import edu.unca.faces.files.annotations.ConditionalField;
import edu.unca.faces.files.annotations.Conditions;
import edu.unca.faces.files.annotations.Index;
import edu.unca.faces.files.annotations.NullTerminated;
import edu.unca.faces.files.annotations.Reserved;

import java.util.function.Predicate;

public class TriFile {

    @Index(0)
    @ArraySize(8)
    private char[] magicNumber;

    /** Number of vertices */
    @Index(1)
    int numVertices;

    /** Number of triangles */
    @Index(2)
    int numTriangles;

    /** Number of quads */
    @Index(3)
    int numQuads;

    /** Number of labelled vertices */
    @Index(4)
    int numLabelledVertices;

    /** Number of labelled surface points */
    @Index(5)
    int numLabelledSurfacePoints;

    /** Number of texture coordinates */
    @Index(6)
    int numTextureCoordinates;

    /**
     * Extension Info
     * <br>
     * ext & 0x01     True if there are texture coordinates.
     * <br>
     * ext & 0x02     True if surface point labels are 16bit chars.
     */
    @Index(7)
    int extensionInfo;

    /** Number of labelled difference morphs */
    @Index(8)
    int numLabelledDifferenceMorphs;

    /** Number of labelled stat morphs */
    @Index(9)
    int numLabelledStatMorphs;

    /** Number of stat morph vertices */
    @Index(10)
    int numStatMorphVertices;

    /** Reserved for future use. Set to 0 when saving. */
    @Index(11)
    @Reserved(16)
    char[] reserved;

    /** float, float, float, in order X,Y,Z (coordinate system varies) */
    @Index(12)
    @BoundSize({"numVertices", "numStatMorphVertices"})
    @ArraySize(3)
    float[][] vertices;

    /** int,int,int, the vertex indices of the tri facets. */
    @Index(13)
    @BoundSize("numTriangles")
    @ArraySize(3)
    int[][] triangles;

    /** int,int,int,int, the vertex indices of the quad facets. */
    @Index(14)
    @BoundSize("numQuads")
    @ArraySize(4)
    int[][] quads;

    @Index(15)
    @BoundSize("numLabelledVertices")
    VertexLabel[] vertexLabels;

    @Index(16)
    @BoundSize("numLabelledSurfacePoints")
    SurfacePointLabel[] surfacePointLabels;

    // ... If per-vertex texture coordinates (ie X == 0 and [<ext> & 0x01] == true): ...
    /** float, float in OpenGL texture coordinate system. */
    @Index(17)
    @Conditions({PerVertexCondition.class, ExtCondition1.class})
    @BoundSize("numVertices")
    @ArraySize(2)
    float[][] vertexTextureCoordinates;

    // ... Else if per-facet texture coordinates (ie X > 0 and [<ext> & 0x01] == true): ...
    /** float, float in OpenGL texture coordinate system. */
    @Index(18)
    @Conditions({PerFacetCondition.class, ExtCondition1.class})
    @BoundSize("numTextureCoordinates")
    @ArraySize(2)
    float[][] facetTextureCoordinates;

    /** int,int,int Indexes into the texture point list. */
    @Index(19)
    @Conditions({PerFacetCondition.class, ExtCondition1.class})
    @BoundSize("numTriangles")
    @ArraySize(3)
    int[][] trianglesTexturePointIndices;

    /** int,int,int,int Indexes into the texture point list. */
    @Index(20)
    @Conditions({PerFacetCondition.class, ExtCondition1.class})
    @BoundSize("numQuads")
    @ArraySize(4)
    int[][] quadsTexturePointIndices;

    // ... And if Md > 0: ...
    @Index(21)
    @Conditions(DifferenceMorphsCondition.class)
    @BoundSize("numLabelledDifferenceMorphs")
    DifferenceMorph[] differenceMorphs;

    // ... And if Ms > 0: ...
    @Index(22)
    @Conditions(StatMorphsCondition.class)
    @BoundSize("numLabelledStatMorphs")
    StatMorph[] statMorphs;

    public static class VertexLabel {

        @Index(0)
        int index;

        @Index(1)
        String label;
    }

    public static class SurfacePointLabel {

        @Index(0)
        int index;

        @Index(1)
        float x;

        @Index(2)
        float y;

        @Index(3)
        float z;

        @Index(4)
        String label;
    }

    public static class DifferenceMorph {

        @Index(0)
        @NullTerminated
        String label;

        @Index(1)
        Delta deltas;

        public static class Delta {

            @Index(0)
            float scale;

            @Index(1)
            @BoundSize("numVertices")
            @ArraySize(3)
            short[][] delta;
        }
    }

    public static class StatMorph {

        @Index(0)
        @NullTerminated
        String label;

        /** int. Number of model vertices affected */
        @Index(1)
        int L;

        @Index(2)
        @BoundSize("L")
        int[] vertexIndexList;
    }

    public static @ConditionalField("numTextureCoordinates") class PerVertexCondition implements Predicate<Integer> {
        @Override
        public boolean test(Integer X) {
            return X == 0;
        }
    }
    public static @ConditionalField("extensionInfo") class ExtCondition1 implements Predicate<Integer> {
        @Override
        public boolean test(Integer ext) {
            return (ext & 0x01) == 0x01;
        }
    }
    public static @ConditionalField("numTextureCoordinates") class PerFacetCondition implements Predicate<Integer> {
        @Override
        public boolean test(Integer X) {
            return X > 0;
        }
    }
    public static @ConditionalField("numLabelledDifferenceMorphs") class DifferenceMorphsCondition implements Predicate<Integer> {
        @Override
        public boolean test(Integer Md) {
            return Md > 0;
        }
    }
    public static @ConditionalField("numLabelledStatMorphs") class StatMorphsCondition implements Predicate<Integer> {
        @Override
        public boolean test(Integer Ms) {
            return Ms > 0;
        }
    }
}
