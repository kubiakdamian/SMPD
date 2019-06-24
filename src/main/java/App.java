import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
    static final int COLUMNS_NUMBER = 64;

    //Feature finder data
    private static int acerCounter;
    private static int quercusCounter;
    private static double[][] acerTable;
    private static double[][] quercusTable;

    //Classifier data
    private static List<ClassifierElement> wholeList = new ArrayList<>();
    private static List<ClassifierElement> acerList = new ArrayList<>();
    private static List<ClassifierElement> quercusList = new ArrayList<>();

    static {
        String[] fileData = new String[0];
        try {
            fileData = loadFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tableMatrixInit(fileData);
        classifierInit(fileData);
    }

    public static void main(String[] args) {
        FeatureFinder featureFinder = new FeatureFinder(acerTable, quercusTable);

        featureFinder.findBestWithoutSFS();
        featureFinder.findBestWithSFS();

        Classifier classifier = new Classifier(wholeList, acerList, quercusList);

        classifier.classify();
        classifier.classifyForBootstrap();
    }

    private static String[] loadFile() throws IOException {
        FileReader fileReader = new FileReader("Maple_Oak.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> rows = new ArrayList<>();
        String row;

        while ((row = bufferedReader.readLine()) != null) {
            rows.add(row);
            if (row.contains("Acer")) {
                acerCounter++;
            } else if (row.contains("Quercus")) {
                quercusCounter++;
            }
        }
        bufferedReader.close();

        rows.remove(0);

        String[] array = new String[rows.size()];

        return rows.toArray(array);
    }


    private static void tableMatrixInit(String[] fileData) {
        acerTable = new double[64][acerCounter];
        quercusTable = new double[64][quercusCounter];
        int acerRowCounter = 0;
        int quercusRowCounter = 0;

        for (String row : fileData) {
            int columnCounter = 0;
            List<String> val = Arrays.asList(row.split(","));
            List<String> values = new ArrayList<>(val);
            String name = values.get(0);
            values.remove(0);

            for (String value : values) {
                double parsedValue = Double.parseDouble(value);
                if (name.contains("Acer")) {
                    acerTable[columnCounter][acerRowCounter] = parsedValue;
                } else if (name.contains("Quercus")) {
                    quercusTable[columnCounter][quercusRowCounter] = parsedValue;
                }
                columnCounter++;
            }

            if (name.contains("Acer")) {
                acerRowCounter++;
            } else if (name.contains("Quercus")) {
                quercusRowCounter++;
            }
        }
    }

    private static void classifierInit(String[] fileData) {
        int index = 1;
        for (String fileLine : fileData) {
            String[] values = fileLine.split(",");
            ClassifierElement element = new ClassifierElement(index, values[0]);

            for (String value : values) {
                if (checkIfValueIsParsable(value)) {
                    element.getValues().add(Double.parseDouble(value));
                }
            }
            wholeList.add(element);

            if (element.getName().contains("Acer")) {
                acerList.add(element);
            } else if (element.getName().contains("Quercus")) {
                quercusList.add(element);
            }

            index++;
        }
    }

    private static boolean checkIfValueIsParsable(String value) {
        try {
            Double.parseDouble(value);

            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
