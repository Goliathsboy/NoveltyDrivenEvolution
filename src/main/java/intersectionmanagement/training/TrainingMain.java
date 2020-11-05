package intersectionmanagement.training;

import org.apache.commons.lang3.SerializationUtils;
import org.encog.Encog;
import org.encog.ml.CalculateScore;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.NEATUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Random;

public class TrainingMain {

    public static void main(String[] args) throws IOException {

        // Initializing NEAT population
        NEATPopulation pop = new NEATPopulation(8,1, TrainingConfig.POPULATION_SIZE);
        pop.setInitialConnectionDensity(1.0);
        pop.reset();

        // Score class initialization
        String parameters = readParameters(args[0]);
        CalculateScore score = null;
        switch (args[1]) {
            case "novelty":
                score = new ControllerNoveltyScore(parameters);
                break;
            case "hybrid":
                score = new ControllerHybridScore(parameters);
                break;
            default:
                score = new ControllerFitnessScore(parameters);
        }

        final EvolutionaryAlgorithm train = NEATUtil.constructNEATTrainer(pop,score); // Initializing evolutionary framework

        long startTime = System.currentTimeMillis();
        long lastIterationTime = System.currentTimeMillis();

        int iteration = 0;
        do {
            train.iteration(); // Run the framework code to process the next generation

            System.out.println("Epoch #" + train.getIteration() + " Score:" + train.getError()+ ", Species:" + pop.getSpecies().size());
            System.out.println("Iteration time: " + (System.currentTimeMillis() - lastIterationTime)/(1000*1.0f) + "s");
            lastIterationTime = System.currentTimeMillis();
            System.out.println("Total time: " + (lastIterationTime - startTime)/(1000*1.0f)+ "s");

            NEATNetwork network = null;
            if ("fitness".equals(args[1])) {
                network = (NEATNetwork) train.getCODEC().decode(train.getBestGenome());
            } else {
                PopulationInformation.reset();
                network = PopulationInformation.historicalBest.getNetwork();
            }
            TrainingMain.saveNetwork(network); // Continuously saves best network in case run fails

            iteration++;
        } while(iteration < TrainingConfig.NUM_GENERATIONS);

        TrainingMain.saveResults(PopulationInformation.historicalTop20, args[1]); // Writing the top 20 networks to a file for testing purposes

        Encog.getInstance().shutdown();
    }

    public static String readParameters(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        // delete the last new line separator
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        reader.close();
        return stringBuilder.toString();
    }

    public static void saveNetwork(NEATNetwork network) throws IOException {
        byte[] serializedNetwork = SerializationUtils.serialize(network);

        int[] arr = new int[serializedNetwork.length];
        for (int i = 0; i<serializedNetwork.length; i++) {
            arr[i] = serializedNetwork[i];
        }

        JSONArray jsonArr = new JSONArray(arr);
        System.out.println(jsonArr.toString());

        File file = new File("network.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(jsonArr.toString());
        writer.close();
    }

    public static void saveResults(ControllerInformation[] networks, String method) throws IOException {
        Random r = new Random();

        File results = new File("results");
        results.mkdir();

        File file = null;
        do {
            int randomID = r.nextInt(1000000);
            file = new File("results/"+method+"_"+randomID+".txt");
        }while(!file.createNewFile());

        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fos);

        out.writeObject(networks);

        out.close();
        fos.close();
    }
}
