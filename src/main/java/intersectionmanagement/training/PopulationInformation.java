package intersectionmanagement.training;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CyclicBarrier;

public class PopulationInformation {
    public static int populationSize = TrainingConfig.POPULATION_SIZE;

    public static ControllerInformation historicalBest;
    public static ControllerInformation[] historicalTop20 = new ControllerInformation[20];
    public static ArrayList<ControllerInformation> currentPopulationValues = new ArrayList<>(populationSize);
    public static ArrayList<ControllerInformation> noveltyArchive = new ArrayList<>();
    public static boolean hasArchived = false;
    public static CyclicBarrier cyclicBarrier = new CyclicBarrier(populationSize);

    public static double currentHighestFitness = Integer.MIN_VALUE;
    public static double currentLowestFitness = Integer.MAX_VALUE;

    public static double currentHighestNovelty = Integer.MIN_VALUE;
    public static double currentLowestNovelty = Integer.MAX_VALUE;

    public static final int NEAREST_K = 20;
    public static final int ARCHIVE_NUMBER = 15;


    public synchronized static void addInformation (ControllerInformation controllerInformation) {
        currentPopulationValues.add(controllerInformation);
        if (historicalBest == null || controllerInformation.getFitness() >= historicalBest.getFitness()) {
            historicalBest = controllerInformation;
        }
        updateFitness(controllerInformation);

        if (controllerInformation.getFitness() > currentHighestFitness) {
            currentHighestFitness = controllerInformation.getFitness();
        }

        if (controllerInformation.getFitness() < currentLowestFitness) {
            currentLowestFitness = controllerInformation.getFitness();
        }
    }

    public synchronized static void updateFitness (ControllerInformation controllerInformation) {
        if (historicalTop20[0] == null) {
            for (int i = 0; i<20; i++) {
                historicalTop20[i] = new ControllerInformation(null, Integer.MIN_VALUE, null, 0);
            }
        }
        Arrays.sort(historicalTop20, new SortByFitness());
        if (controllerInformation.getFitness() >= historicalTop20[0].getFitness()) {
            historicalTop20[0] = controllerInformation;
        }
    }

    public synchronized static void updateNovelty (double noveltyScore) {
        if (noveltyScore > currentHighestNovelty) {
            currentHighestNovelty = noveltyScore;
        }

        if (noveltyScore < currentLowestNovelty) {
            currentLowestNovelty = noveltyScore;
        }
    }

    public static double calculateNovelty (float[] noveltyData) {
        double averageDistance = 0;
        double[] distances = new double[noveltyArchive.size() + populationSize];

        for (int i = 0; i < populationSize; i++) {
            distances[i] = calculateDistance(noveltyData, currentPopulationValues.get(i).getNoveltyData());
        }
        for (int i = 0; i < noveltyArchive.size(); i++) {
            distances[i + populationSize] = calculateDistance(noveltyData, noveltyArchive.get(i).getNoveltyData());
        }
        // sort in ascending order
        Arrays.sort(distances);
        // take average sum of first k
        for (int i = 0; i<NEAREST_K; i++) {
            averageDistance += distances[i];
        }

        return averageDistance/NEAREST_K;
    }

    private static double calculateDistance (float[] data1, float[] data2) {
        float distance = 0;
        for (int i = 0; i<data1.length; i++) {
            distance += Math.pow(data1[i] - data2[i], 2);
        }
        return Math.sqrt(distance);
    }

    public synchronized static void archive () {
        if (!hasArchived) {
            Collections.sort(currentPopulationValues);
            for (int i = 0; i<ARCHIVE_NUMBER; i++) {
                noveltyArchive.add(currentPopulationValues.get(i));
            }
            hasArchived = true;
            currentPopulationValues.clear();
        }
    }

    public static double normaliseFitness (double fitness) {
        return (fitness - currentLowestFitness)/(currentHighestFitness-currentLowestFitness);
    }

    public static double normaliseNovelty (double noveltyScore) {
        return (noveltyScore - currentLowestNovelty)/(currentHighestNovelty-currentLowestNovelty);
    }

    public static void reset () {
        hasArchived = false;
        currentHighestFitness = Integer.MIN_VALUE;
        currentLowestFitness = Integer.MAX_VALUE;
        currentHighestNovelty = Integer.MIN_VALUE;
        currentLowestNovelty = Integer.MAX_VALUE;
    }

    private static class SortByFitness implements Comparator<ControllerInformation> {
        public int compare(ControllerInformation a, ControllerInformation b) {
            if (a.getFitness() == b.getFitness()) {
                return 0;
            }else if (a.getFitness() > b.getFitness()) {
                return 1;
            }
            return -1;
        }
    }
}
