package se.facilia.math.klu;

public class KLU {
    static {
        System.loadLibrary("klu");
    }

    @Override
    public void finalize() {
        free();
    }

    private long[] dataPointers;

    public KLU() {
        dataPointers = new long[3];
    }

    private native void factorizeComplexCCS(int n, int[] Ap, int[] Ai, double[] Ax, long[] dataPointers);

    private native void solveComplex(double[] b, long[] dataPointers);

    private native void free(long[] dataPointers);

    public void factorizeComplex(int n, int[] columnPointers, int[] rowIndices, double[] complexValues)
            throws Exception {
        if (dataPointers[1] != 0) {
            free(dataPointers);
        }
        factorizeComplexCCS(n, columnPointers, rowIndices, complexValues, dataPointers);
        if (dataPointers[1] == 0) {
            throw new Exception("se.facilia.math.klu.KLU failed to factorize");
        }
    }

    public void solveComplex(double[] b) {
        solveComplex(b, dataPointers);
    }

    public void solveComplex(double[] real, double[] imag) {
        double[] complex = new double[real.length * 2];
        for (int i = 0; i < real.length; i++) {
            complex[i * 2] = real[i];
            complex[i * 2 + 1] = imag[i];
        }

        solveComplex(complex);

        for (int i = 0; i < real.length; i++) {
            real[i] = complex[i * 2];
            imag[i] = complex[i * 2 + 1];
        }
    }

    public void free() {
        if (dataPointers != null) {
            free(dataPointers);
            dataPointers = null;
        }
    }

}