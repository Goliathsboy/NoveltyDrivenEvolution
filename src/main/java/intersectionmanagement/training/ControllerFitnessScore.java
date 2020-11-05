package intersectionmanagement.training;

import intersectionmanagement.trial.PerformanceValues;
import intersectionmanagement.trial.Trial;
import org.apache.commons.lang3.SerializationUtils;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.neural.neat.NEATNetwork;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

public class ControllerFitnessScore implements CalculateScore {

    private String parameters;

    public ControllerFitnessScore (String parameters) {
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
        try {
            for (int i = 0; i<5; i++) {
                int seed = r.nextInt(1000000);

                Trial trial = new Trial(seed, trackFile, simulationSteps, serializedNetwork, spawner);
                PerformanceValues values = trial.runSimulation();

                fitness += values.getCollisions();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        fitness /= -5;

        PopulationInformation.updateFitness(new ControllerInformation(network, fitness, null, 0));

        return fitness;
    }

    public boolean shouldMinimize() {
        return false;
    }

    @Override
    public boolean requireSingleThreaded() {
        return false;
    }
}
