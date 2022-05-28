package pt.up.fe.mobilecardriving.util;

public class VectorOperations {
    public static float[] add(final float[] v1, final float[] v2) {
        assert v1.length == v2.length;
        float[] result = new float[v1.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = v1[i] + v2[i];
        }
        return result;
    }
}
