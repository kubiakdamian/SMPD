import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class FeatureFinder {
    private static final int ALL_FEATURES_NUMBER = 64;

    private int acerCounter;
    private int quercusCounter;
    private double[][] acerTable;
    private double[][] quercusTable;

    public FeatureFinder() throws IOException {
        String[] fileData = loadFile();
        tableMatrixInit(fileData);
    }

    public void findBestWithoutSFS() {
        Scanner scanner = new Scanner(System.in);
        int chosenFeaturesNumber;
        long start;
        long end;

        System.out.println("Wprowadz n ( liczba cech)");
        chosenFeaturesNumber = Integer.valueOf(scanner.nextLine());

        start = System.nanoTime();

        Set<Element> elements = new TreeSet<>();

        int[] array = new int[ALL_FEATURES_NUMBER];

        for (int i = 0; i < ALL_FEATURES_NUMBER; i++) {
            array[i] = i;
        }

        List<Integer[]> combinations = CombinationGenerator.generateCombinations(array, ALL_FEATURES_NUMBER, chosenFeaturesNumber);

        for (Integer[] combination : combinations) {
            for (Integer index : combination) {
                double fisherResult = fisher(acerTable[index], quercusTable[index]);

                Element e = new Element(fisherResult, index + 1);
                elements.add(e);
            }
        }

        List<Element> bestElements = bestElements(chosenFeaturesNumber, elements);

        printBest(bestElements);

        end = System.nanoTime();
        double duration = (end - start) / 1000000000.0;
        System.out.println("CZAS WYKONANIA BEZ SFS: " + duration);
    }

    public void findBestWithSFS() {
        Scanner scanner = new Scanner(System.in);
        int n;
        long start;
        long end;

        System.out.println("Wprowadz n ( liczba cech)");
        n = Integer.valueOf(scanner.nextLine());

        start = System.nanoTime();

        SFSObject sfsObject = fisherOneOnly();
        for (int i = 1; i < n; i++) {
            sfsObject = fisherMulti(sfsObject);
        }

        printBest(sfsObject.getBestElements());

        end = System.nanoTime();
        double duration = (end - start) / 1000000000.0;
        System.out.println("CZAS WYKONANIA Z SFS: " + duration);
    }

    private double fisher(double[] vectorAcer, double[] vectorQuercus) {
        double uA = 0;
        double uB = 0;
        double sA = 0;
        double sB = 0;

        int elementsNumber = vectorAcer.length;

        for (int i = 0; i < elementsNumber; i++) {
            uA += vectorAcer[i];
            uB += vectorQuercus[i];
        }

        uA /= elementsNumber;
        uB /= elementsNumber;

        for (int i = 0; i < elementsNumber; i++) {
            sA += (vectorAcer[i] - uA) * (vectorAcer[i] - uA);
            sB += (vectorQuercus[i] - uB) * (vectorQuercus[i] - uB);
        }

        sA = Math.sqrt(sA / elementsNumber);
        sB = Math.sqrt(sB / elementsNumber);

        return Math.abs(uA - uB) / (sA + sB);
    }

    private List<Element> bestElements(int chosenNumber, Set<Element> elements) {
        List<Element> bestElements = new ArrayList<>();
        List<Element> temp = new ArrayList<>(elements);

        for (int i = 0; i < chosenNumber; i++) {
            Element element = findBest(temp);
            bestElements.add(element);
            temp.remove(element);
        }

        return bestElements;
    }

    private Element findBest(List<Element> elements) {
        Element bestElement = null;
        int i = 0;

        for (Element element : elements) {
            if (element != null) {
                if (i == 0) {
                    bestElement = element;
                } else {
                    if (bestElement != null) {
                        if (bestElement.getValue() < element.getValue()) {
                            bestElement = element;
                        }
                    }
                }
            }

            i++;
        }

        return bestElement;
    }

    private void printBest(List<Element> elements) {
        for (Element element : elements) {
            if (element != null) {
                System.out.println("Cecha : " + " " + element.getValue() + " " + element.getIndex());
            }
        }
    }

    private void tableMatrixInit(String[] fileData) {
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

    private String[] loadFile() throws IOException {
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

    private SFSObject fisherOneOnly() {
        SFSObject sfsObject = new SFSObject();

        double max = fisher(acerTable[0], quercusTable[0]);
        int maxIndex = 0;

        for (int i = 1; i < ALL_FEATURES_NUMBER; i++) {
            double fisherResult = fisher(acerTable[i], quercusTable[i]);
            if (fisherResult > max) {
                max = fisherResult;
                maxIndex = i;
            }
        }

        Element maxElement = new Element(max, maxIndex);
        sfsObject.setActiveBestElements(maxElement);
        sfsObject.getBestElements().add(maxElement);
        sfsObject.getElementsIndexes().add(maxIndex);

        return sfsObject;
    }

    private SFSObject fisherMulti(SFSObject sfsObject) {
        int elementIndexToCombine = sfsObject.getActiveBestElements().getIndex();
        List<Integer> elementIndexesUsed = sfsObject.getElementsIndexes();
        List<Integer> getIndexesForSfs = getIndexesFprSFS(elementIndexesUsed);
        List<ExtendedElement> extendedElements = new ArrayList<>();

        for (Integer index : getIndexesForSfs) {
            double fisherMultiValue;

            if (index < elementIndexToCombine) {
                fisherMultiValue = Fisher.fisherMulti(index, elementIndexToCombine, acerTable, quercusTable);
            } else {
                fisherMultiValue = Fisher.fisherMulti(index, elementIndexToCombine, acerTable, quercusTable);
            }

            ExtendedElement extendedElement = new ExtendedElement(fisherMultiValue, elementIndexToCombine, index);
            extendedElements.add(extendedElement);
        }

        ExtendedElement bestExtendedElement = findBestFeatureFromFeaturesFisher2D(extendedElements);
        Element bestElement = new Element(bestExtendedElement.getValue(), bestExtendedElement.getFollowIndex());
        sfsObject.getBestElements().add(bestElement);
        sfsObject.setActiveBestElements(bestElement);
        sfsObject.getElementsIndexes().add(bestExtendedElement.getFollowIndex());

        return sfsObject;
    }

    private ExtendedElement findBestFeatureFromFeaturesFisher2D(List<ExtendedElement> featuresList) {
        ExtendedElement bestFeature = null;
        int i = 0;

        for (ExtendedElement feature : featuresList) {
            if (feature != null) {
                if (i == 0) {
                    bestFeature = feature;
                } else {
                    if (bestFeature != null) {
                        if (bestFeature.getValue() < feature.getValue()) {
                            bestFeature = feature;
                        }
                    }
                }
            }
            i++;
        }

        return bestFeature;
    }

    private List<Integer> getIndexesFprSFS(List<Integer> indexesAlreadyUsed) {
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            if (!indexesAlreadyUsed.contains(i)) {
                indexes.add(i);
            }
        }

        return indexes;
    }
}
