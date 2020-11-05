package intersectionmanagement.training;

import intersectionmanagement.trial.PerformanceValues;
import intersectionmanagement.trial.Trial;
import org.apache.commons.lang3.SerializationUtils;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.neural.neat.NEATNetwork;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class ControllerHybridScore implements CalculateScore {
    private String parameters;

    public ControllerHybridScore(String parameters) {
        this.parameters = parameters;
    }

    public double calculateScore(final MLMethod method) {
        NEATNetwork network = (NEATNetwork) method;

        JSONObject jsonParameters = new JSONObject(parameters);

        Random r = new Random();
        String trackFile = jsonParameters.getString("track");
        int simulationSteps = jsonParameters.getInt("steps");
        byte[] serializedNetwork = SerializationUtils.serialize(network);
        JSONObject spawner = jsonParameters.getJSONObject("spawner");

        double fitness = 0;
        float[] averageNoveltyData = new float[16];
        PerformanceValues values = null;
        try {
            for (int i = 0; i<5; i++) {
                int seed = r.nextInt(1000000);
                Trial trial = new Trial(seed, trackFile, simulationSteps, serializedNetwork, spawner);
                values = trial.runSimulation();

                fitness += values.getCollisions();
                for (int j = 0; j<averageNoveltyData.length; j++) {
                    averageNoveltyData[j] += values.getNoveltyData()[j];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        fitness /= -5;
        for (int i = 0; i<averageNoveltyData.length; i++) {
            averageNoveltyData[i] /= 5;
        }

        ControllerInformation controllerInformation = new ControllerInformation(network, fitness, averageNoveltyData, 0);
        PopulationInformation.addInformation(controllerInformation);

        try {
            PopulationInformation.cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        double noveltyScore = PopulationInformation.calculateNovelty(averageNoveltyData);

        PopulationInformation.updateNovelty(noveltyScore);

        try {
            PopulationInformation.cyclicBarrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        PopulationInformation.archive();

        double hybridScore = 0.5*PopulationInformation.normaliseFitness(fitness) + 0.5*PopulationInformation.normaliseNovelty(noveltyScore);

        return hybridScore;
    }

    public boolean shouldMinimize() {
        return false;
    }

    @Override
    public boolean requireSingleThreaded() {
        return false;
    }
}
