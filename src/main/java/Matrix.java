public class Matrix {

    public static double[][] getVectorWithAverages(int firstIndex, int secondIndex, double[][] vector) {
        double firstElement = getRowAverageValue(vector, firstIndex);
        double secondElement = getRowAverageValue(vector, secondIndex);
        double[][] vectorTmp = new double[2][1];
        vectorTmp[0][0] = firstElement;
        vectorTmp[1][0] = secondElement;
        return vectorTmp;
    }

    private static double getRowAverageValue(double[][] vector, int row) {
        double sum = 0;
        for (int j = 0; j < vector[row].length; j++) {
            sum = sum + vector[row][j];
        }
        return sum / vector[row].length;
    }

    public static double[][] subtraction(double[][] matrix1, double[][] matrix2) {

        double[][] resultMatrix = new double[matrix1.length][matrix1[0].length];
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1[i].length; j++) {
                resultMatrix[i][j] = matrix1[i][j] - matrix2[i][0];
            }
        }
        return resultMatrix;
    }


    //transponowanie macierzy
    public static double[][] transposeMatrix(double[][] matrix) {
        int r = matrix.length, c = matrix[1].length;
        double[][] matrixTransponded = new double[c][r];
        for (int w = 0; w < r; w++) {
            for (int k = 0; k < c; k++) {
                matrixTransponded[k][w] = matrix[w][k];
            }
        }
        return matrixTransponded;
    }


    public static double calculateMatrixDet(double[][] matrix) {
        double det = 0;

        if (matrix.length == 1 && matrix[0].length == 1) {
            det = matrix[0][0];
        } else if (matrix.length != matrix[0].length) {
            throw new RuntimeException("Matrix wrong sizes");
        } else if (matrix.length == 2 && matrix[0].length == 2) {
            det = (matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]);
        } else {
           det = getDetForBiggerMatrix(matrix);
        }
        return det;
    }

    private static double getDetForBiggerMatrix(double[][] matrix){
        double det = 0;
        double[][] nTab = new double[matrix.length + (matrix.length - 1)][matrix[0].length];

        for (int i = 0, j = 0; i < nTab.length; i++, j++) {
            for (int w = 0; w < matrix[0].length; w++) {
                if (j < matrix.length && w < matrix[0].length) {
                    nTab[i][w] = matrix[j][w];
                } else {
                    j = 0;
                    nTab[i][w] = matrix[j][w];
                }
            }
        }
        //iloczyn
        double product = 1;
        int k;

        for (int i = 0; i < matrix.length; i++) {
            k = i;
            for (int j = 0; j < matrix[0].length; j++) {
                product = product * nTab[k][j];
                k++;
            }
            det = det + product;
            product = 1;
        }

        product = 1;
        for (int i = 0; i < matrix.length; i++) {
            k = i;
            for (int j = matrix[0].length - 1; j >= 0; j--) {
                product = product * nTab[k][j];
                k++;
            }
            det = det - product;
            product = 1;
        }

        return det;
    }

    public static double euclidesDistance(double[][] vectorA, double[][] vectorB) {
        double element_1A = vectorA[0][0];
        double element_1B = vectorA[1][0];
        double element_2A = vectorB[0][0];
        double element_2B = vectorB[1][0];
        return Math.sqrt((element_1A - element_2A) * (element_1A - element_2A) + (element_1B - element_2B) * (element_1B - element_2B));
    }
}
