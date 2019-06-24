import java.util.ArrayList;
import java.util.List;

public class CombinationGenerator {


    static List<Integer[]> generateCombinations(int[] array, int allFeatures, int chosenNumberOfFeatures) {
        List<Integer[]> combinations = new ArrayList<>();
        int[] data = new int[chosenNumberOfFeatures];

        generator(array, data, 0, allFeatures - 1, combinations, 0, chosenNumberOfFeatures);

        return combinations;
    }

    static void generator(int[] array, int[] data, int start, int end, List<Integer[]> combinations, int index, int r) {
        if (index == r) {
            Integer[] temp = new Integer[r];
            for (int i = 0; i < r; i++) {
                temp[i] = data[i];
            }
            combinations.add(temp);
            return;
        }

        for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
            data[index] = array[i];
            generator(array, data, i + 1, end, combinations, index + 1, r);
        }
    }

}
