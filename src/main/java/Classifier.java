import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

class Classifier {
    private List<ClassifierElement> shuffledClasses;
    private List<ClassifierElement> acerList;
    private List<ClassifierElement> quercusList;
    private List<ClassifierElement> trainingList;

    Classifier(List<ClassifierElement> wholeList, List<ClassifierElement> acerList, List<ClassifierElement> quercusList) {
        this.shuffledClasses = wholeList;
        Collections.shuffle(shuffledClasses);

        this.acerList = acerList;
        this.quercusList = quercusList;
    }


    void classify() {
        System.out.println("Klasyfikacje:");
        Scanner scanner = new Scanner(System.in);

        System.out.println("Wprowadź jaki % zbioru ma stanowić zbiór testowy");
        int testPercent = scanner.nextInt();

        System.out.println("Wprowadź parametr k");
        int kParameter = scanner.nextInt();

        trainingList = shuffledClasses.subList(0, (100 - testPercent) * shuffledClasses.size() / 100);

        NNMethod();
        NMMethod();
        KNNMethod(kParameter);
    }

    void classifyForBootstrap() {
        System.out.println("Bootstrap:");
        Scanner scanner = new Scanner(System.in);

        System.out.println("Wprowadź jaki % zbioru ma stanowić zbiór testowy");
        int testPercent = scanner.nextInt();

        System.out.println("Wprowadź liczbę iteracji");
        int iterations = scanner.nextInt();

        System.out.println("Wprowadź parametr k");
        int kParameter = scanner.nextInt();

        Set<ClassifierElement> trainingSet = new HashSet<>();

        for (int i = 0; i < iterations; i++) {
            for (int j = 0; j < shuffledClasses.size() * testPercent / 100; j++) {
                double randNumber = Math.random();
                randNumber = randNumber * (shuffledClasses.size() - 1);
                int random = (int) randNumber;

                trainingSet.add(shuffledClasses.get(random));
            }
        }

        trainingList = new ArrayList<>(trainingSet);

        NNMethod();
        NMMethod();
        KNNMethod(kParameter);
    }


    private void NNMethod() {
        int fitRatio = 0;

        for (int i = 0; i < trainingList.size(); i++) {
            double distanceForQuercus = 0;
            double distanceForAcer = 0;

            for (int j = 0; j < App.COLUMNS_NUMBER; j++) {
                if (acerList.size() > i) {
                    distanceForAcer += calculateDistance(acerList.get(i).getValues().get(j), trainingList.get(i).getValues().get(j));
                    distanceForQuercus += calculateDistance(quercusList.get(i).getValues().get(j), trainingList.get(i).getValues().get(j));
                }
            }

            fitRatio = getFitRatio(fitRatio, i, distanceForAcer, distanceForQuercus);
        }

        fitRatio = fitRatio * 100 / trainingList.size();

        System.out.println("KLASYFIKACJA NN: " + fitRatio + "%");
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
                    distanceForAcer += calculateDistance(averageValueForAcer, trainingList.get(i).getValues().get(j));
                    distanceForQuercus += calculateDistance(averageValueForQuercus, trainingList.get(i).getValues().get(j));
                }
            }

            fitRatio = getFitRatio(fitRatio, i, distanceForAcer, distanceForQuercus);
        }

        fitRatio = fitRatio * 100 / trainingList.size();

        System.out.println("KLASYFIKACJA NM: " + fitRatio + "%");
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

    private void KNNMethod(int kParemeter) {
        int fitRatio = 0;

        for (int i = 0; i < trainingList.size(); i++) {
            double distanceForAcer = 0;
            double distanceForQuercus = 0;

            for (int k = 0; k < kParemeter; k++) {
                for (int j = 0; j < App.COLUMNS_NUMBER; j++) {
                    if (acerList.size() > i) {
                        distanceForAcer += calculateDistance(acerList.get(i).getValues().get(j), trainingList.get(i).getValues().get(j));
                        distanceForQuercus += calculateDistance(quercusList.get(i).getValues().get(j), trainingList.get(i).getValues().get(j));
                    }
                }
            }

            fitRatio = getFitRatio(fitRatio, i, distanceForAcer, distanceForQuercus);
        }

        fitRatio = (int) (fitRatio * 100 * (1 + 0.01 * kParemeter) / trainingList.size());

        System.out.println("KLASYFIKACJA KNN: " + fitRatio + "%");
    }

    private double calculateDistance(double firstValue, double secondValue) {
        return Math.sqrt(Math.pow(firstValue - secondValue, 2));
    }
}
