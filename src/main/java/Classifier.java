import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

class Classifier {
    private static final int NN_METHOD_PARAMETER = 1;

    private List<ClassifierElement> wholeList;
    private List<ClassifierElement> acerList;
    private List<ClassifierElement> quercusList;
    private List<ClassifierElement> trainingList;
    private List<ClassifierElement> testList;

    Classifier(List<ClassifierElement> wholeList, List<ClassifierElement> acerList, List<ClassifierElement> quercusList) {
        this.wholeList = wholeList;
        this.trainingList = new ArrayList<>(wholeList);
        this.acerList = acerList;
        this.quercusList = quercusList;
    }

    void classify() {
        Collections.shuffle(wholeList);
        List<ClassifierElement> tempList = new ArrayList<>(wholeList);

        System.out.println("Klasyfikacje:");
        Scanner scanner = new Scanner(System.in);

        System.out.println("Wprowadź jaki % zbioru ma stanowić zbiór testowy");
        int testPercent = scanner.nextInt();

        System.out.println("Wprowadź parametr k");
        int kParameter = scanner.nextInt();

        testList = tempList.subList(0, (100 - (100 - testPercent)) * wholeList.size() / 100);
        tempList = new ArrayList<>(testList);
        trainingList.removeAll(tempList);

        KNNMethod(NN_METHOD_PARAMETER); //NN method
        NMMethod();
        KNNMethod(kParameter);
    }

    void classifyForBootstrap() {
        List<ClassifierElement> tempList;
        trainingList = new ArrayList<>(wholeList);

        System.out.println("Bootstrap:");
        Scanner scanner = new Scanner(System.in);

        System.out.println("Wprowadź jaki % zbioru ma stanowić zbiór testowy");
        int testPercent = scanner.nextInt();

        System.out.println("Wprowadź liczbę iteracji");
        int iterations = scanner.nextInt();

        System.out.println("Wprowadź parametr k");
        int kParameter = scanner.nextInt();

        Set<ClassifierElement> testSet = new HashSet<>();

        for (int i = 0; i < iterations; i++) {
            for (int j = 0; j < wholeList.size() * testPercent / 100; j++) {
                double randNumber = Math.random();
                randNumber = randNumber * (wholeList.size() - 1);
                int random = (int) randNumber;

                testSet.add(wholeList.get(random));
            }
        }

        testList = new ArrayList<>(testSet);
        tempList = new ArrayList<>(testList);
        trainingList.removeAll(tempList);

        KNNMethod(NN_METHOD_PARAMETER); //NN method
        NMMethod();
        KNNMethod(kParameter);
    }

    private void KNNMethod(int kParemeter) {
        List<Distance> distances = new ArrayList<>();
        int fitRatio = 0;
        double distance = 0;

        for (ClassifierElement trainingElement : trainingList) {
            for (ClassifierElement testElement : testList) {
                for (int k = 0; k < testElement.getValues().size(); k++) {
                    distance += calculateDistance(testElement.getValues().get(k), trainingElement.getValues().get(0));
                }

                distances.add(new Distance(testElement.getName(), distance));
                distance = 0;
            }

            String classifiedName = classifyElement(kParemeter, distances);
            distances.clear();

            if (trainingElement.getName().contains(classifiedName)) {
                fitRatio++;
            }
        }

        fitRatio = fitRatio * 100 / trainingList.size();

        if (kParemeter == NN_METHOD_PARAMETER) {
            System.out.println("KLASYFIKACJA NN: " + fitRatio + "%");
        } else {
            System.out.println("KLASYFIKACJA KNN: " + fitRatio + "%");
        }
    }

    private void NMMethod() {
        int fitRatio = 0;

        for (int i = 0; i < trainingList.size(); i++) {
            double distanceForAcer = 0;
            double distanceForQuercus = 0;
            double sumOfAcerValues = 0;
            double sumOfQuercusValues = 0;

            for (int j = 0; j < App.COLUMNS_NUMBER; j++) {
                if (acerList.size() > i) {
                    sumOfAcerValues += acerList.get(i).getValues().get(j);
                    sumOfQuercusValues += quercusList.get(i).getValues().get(j);
                }
            }

            double averageValueForAcer = sumOfAcerValues / App.COLUMNS_NUMBER;
            double averageValueForQuercus = sumOfQuercusValues / App.COLUMNS_NUMBER;

            for (int j = 0; j < App.COLUMNS_NUMBER; j++) {
                if (acerList.size() > i) {
                    distanceForAcer += calculateDistance(averageValueForAcer, trainingList.get(i).getValues().get(0));
                    distanceForQuercus += calculateDistance(averageValueForQuercus, trainingList.get(i).getValues().get(0));
                }
            }

            fitRatio = getFitRatio(fitRatio, i, distanceForAcer, distanceForQuercus);
        }

        fitRatio = fitRatio * 100 / trainingList.size();

        System.out.println("KLASYFIKACJA NM: " + fitRatio + "%");
    }

    private String classifyElement(int kParemeter, List<Distance> distances) {
        int acerCounter = 0;
        int quercusCounter = 0;

        distances.sort(new DistanceComparator());

        for (int i = 0; i < kParemeter; i++) {
            if (distances.get(i).getName().contains("Acer")) {
                acerCounter++;
            } else {
                quercusCounter++;
            }
        }

        if (acerCounter > quercusCounter) {
            return "Acer";
        } else if (quercusCounter > acerCounter) {
            return "Quercus";
        } else {
            return "";
        }
    }

    private int getFitRatio(int fitRatio, int i, double distanceForAcer, double distanceForQuercus) {
        if (distanceForAcer < distanceForQuercus) {
            if (trainingList.get(i).getName().contains("Acer")) {
                fitRatio++;
            }
        } else {
            if (trainingList.get(i).getName().contains("Quercus")) {
                fitRatio++;
            }
        }

        return fitRatio;
    }

    private double calculateDistance(double firstValue, double secondValue) {
        return Math.sqrt(Math.pow(firstValue - secondValue, 2));
    }
}
