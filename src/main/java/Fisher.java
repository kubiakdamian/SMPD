public class Fisher {

    public static double fisherMulti(int firstIndex, int secondIndex, double[][] vectorA, double[][] vectorB) {
        double[][] uA = Matrix.getVectorWithAverages(firstIndex, secondIndex, vectorA);
        double[][] uB = Matrix.getVectorWithAverages(firstIndex, secondIndex, vectorB);


        double[][] aMatrixTmp = createMatrixFromRows(firstIndex, secondIndex, vectorA);
        double[][] bMatrixTmp = createMatrixFromRows(firstIndex, secondIndex, vectorB);

        double[][] aMatrix = Matrix.subtraction(aMatrixTmp, uA);
        double[][] bMatrix = Matrix.subtraction(bMatrixTmp, uB);

        aMatrix = multiplyMatrixes(aMatrix, Matrix.transposeMatrix(aMatrix));
        bMatrix = multiplyMatrixes(bMatrix, Matrix.transposeMatrix(bMatrix));

        double detA = Matrix.calculateMatrixDet(aMatrix);
        double detB = Matrix.calculateMatrixDet(bMatrix);
        double fisherMultiValue = Matrix.euclidesDistance(uA, uB) / (detA + detB);

        return fisherMultiValue;
    }

    private static double[][] createMatrixFromRows(int row1, int row2, double[][] matrix) {
        double[][] matrixTmp = new double[2][2];
        matrixTmp[0] = matrix[row1];
        matrixTmp[1] = matrix[row2];

        return matrixTmp;
    }

    private static double[][] multiplyMatrixes(double[][] matrix1, double[][] matrix2) {
        double[][] result = new double[matrix1.length][matrix1.length];
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix2[0].length; j++) {
                double temp = 0;
                for (int w = 0; w < matrix2.length; w++) {
                    temp += matrix1[i][w] * matrix2[w][j];
                }
                result[i][j] = temp;
            }
        }

        return result;
    }
}
