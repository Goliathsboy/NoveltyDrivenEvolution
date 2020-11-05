package intersectionmanagement.testing;

import intersectionmanagement.training.ControllerInformation;
import intersectionmanagement.training.TestResult;
import intersectionmanagement.trial.PerformanceValues;
import intersectionmanagement.trial.Trial;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.encog.neural.neat.NEATNetwork;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class TestMain {
    private static String pathToFolder = "C:\\Users\\golia\\Documents\\CHPC\\IntersectionTraining\\test_archives\\Second_Tests";
    private static String[] methods = {"Fitness", "Hybrid", "Novelty"};
    private static String[] tracks = {"four-way-2-lane-no-pedestrian", "four-way-2-lane-pedestrian", "four-way-no-pedestrian", "four-way-pedestrian"};

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length == 1) {
            pathToFolder = args[0];
        }

        extractNetworks();
        runBaselineTests();
        runGeneralisationTests();
        convertTestResults("baselineTestResults20k.txt", "baseGraphData.txt");
        convertTestResults("generalisationTestResults20k.txt", "genGraphData.txt");
        getBest("baselineTestResults20k.txt", "baseBestNetwork.txt");
        getBest("generalisationTestResults20k.txt", "genBestNetwork.txt");
        collectBestNetworks();
    }

    public static void extractNetworks() throws IOException, ClassNotFoundException {
        for (String method : methods) {
            for (String track : tracks) {
                String pathToResults = pathToFolder+"\\"+method+"\\"+track+"\\results";
                File dir = new File(pathToResults+"\\trained_networks");

                ControllerInformation[] allNetworks = null;
                if (dir.listFiles() != null) {
                    for (File file : dir.listFiles()) {
                        ControllerInformation[] networks = readNetworks(file);
                        Arrays.sort(networks, new SortByFitness());
                        ControllerInformation[] top4Networks = new ControllerInformation[4];
                        System.arraycopy(networks, 0, top4Networks, 0, 4);

                        allNetworks = ArrayUtils.addAll(allNetworks, top4Networks);
                    }
                }
                if (allNetworks != null) {
                    Arrays.sort(allNetworks, new SortByFitness());
                }

                saveResults(allNetworks, new File(pathToResults+"\\sampled20Networks.txt"));
            }
        }
    }

    public static void runBaselineTests () throws IOException, ClassNotFoundException {
        long startTime = System.currentTimeMillis();
        JSONObject jsonParameters = new JSONObject(readParameters(pathToFolder+"\\experiment_parameters.json"));

        for (String method : methods) {
            for (String track : tracks) {
                System.out.println(method + " - " + track + " tests starting");
                String pathToResults = pathToFolder+"\\"+method+"\\"+track+"\\results\\";

                ControllerInformation[] networks = readNetworks(new File(pathToResults+"sampled20Networks.txt"));
                TestResult[] results20k = new TestResult[networks.length];

                for (int i = 0; i<networks.length; i++) {
                    System.out.println("Test " + i);
                    results20k[i] = runTest("tracks/"+track+".json", networks[i].getNetwork(), 20000, jsonParameters.getJSONObject("spawner"));
                }

                saveTestResults(results20k, new File(pathToResults+"baselineTestResults20k.txt"));
                System.out.println("Tests complete");
                System.out.println("Total time: " + (System.currentTimeMillis() - startTime)/(60000*1.0f)+ "min");
            }
        }
        System.out.println("Baseline tests complete");
        System.out.println("Total time: " + (System.currentTimeMillis() - startTime)/(60000*1.0f)+ "min");
    }

    public static void runGeneralisationTests () throws IOException, ClassNotFoundException {
        long startTime = System.currentTimeMillis();
        JSONObject jsonParameters = new JSONObject(readParameters("experiment_parameters.json"));

        for (String method : methods) {
            for (String track : tracks) {
                System.out.println(method + " - " + track + " tests starting");
                String pathToResults = pathToFolder+"\\"+method+"\\"+track+"\\results\\";
                ControllerInformation[] networks = readNetworks(new File(pathToResults+"sampled20Networks.txt"));

                String generalisationTrack = "four-way-3-lane";
                switch (track) {
                    case "four-way-pedestrian":
                        generalisationTrack = "four-way-pedestrian-gen";
                        break;
                    case "four-way-2-lane-pedestrian":
                        generalisationTrack = "four-way-2-lane-pedestrian-gen";
                        break;
                }
                TestResult[] results20k = new TestResult[networks.length];

                for (int i = 0; i<networks.length; i++) {
                    System.out.println("Test " + i);
                    results20k[i] = runTest("tracks/"+generalisationTrack+".json", networks[i].getNetwork(), 20000, jsonParameters.getJSONObject("spawner"));
                }

                saveTestResults(results20k, new File(pathToResults+"generalisationTestResults20k.txt"));
                System.out.println("Tests complete");
                System.out.println("Total time: " + (System.currentTimeMillis() - startTime)/(60000*1.0f)+ "min");
            }
        }
        System.out.println("Generalisation tests complete");
        System.out.println("Total time: " + (System.currentTimeMillis() - startTime)/(60000*1.0f)+ "min");
    }

    public static TestResult runTest (String trackFile, NEATNetwork network, int simulationSteps, JSONObject spawner) throws IOException {
        Random r = new Random();
        double collisions = 0;
        double throughput = 0;
        for (int i = 0; i<20; i++) {
            int seed = r.nextInt(1000000);

            Trial trial = new Trial(seed, trackFile, simulationSteps, SerializationUtils.serialize(network), spawner);
            PerformanceValues values = trial.runSimulation();

            collisions += values.getCollisions();
            throughput += values.getThroughput();
        }
        collisions /= 20;
        throughput /= 20;
        return new TestResult(network, collisions, throughput);
    }

    public static void convertTestResults (String filename, String targetFile) throws IOException, ClassNotFoundException {
        for (String method : methods) {
            for (String track : tracks) {
                String pathToResults = pathToFolder+"\\"+method+"\\"+track+"\\results\\";

                TestResult[] results = readTestResults(new File(pathToResults+filename));
                double[] collisionArray = new double[results.length];
                double[] throughputArray = new double[results.length];

                for (int i = 0; i<results.length; i++) {
                    collisionArray[i] = results[i].getCollisions();
                    throughputArray[i] = results[i].getThroughput();
                }

                JSONArray collisionJSON = new JSONArray(collisionArray);
                JSONArray throughputJSON = new JSONArray(throughputArray);
                JSONObject graphData = new JSONObject();
                graphData.put("collisions", collisionJSON);
                graphData.put("throughput", throughputJSON);


                File file = new File(pathToResults+targetFile);
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(graphData.toString());
                writer.close();
            }
        }
    }

    public static void getBest (String filename, String targetFile) throws IOException, ClassNotFoundException {
        for (String method : methods) {
            for (String track : tracks) {
                String pathToResults = pathToFolder+"\\"+method+"\\"+track+"\\results\\";

                TestResult[] results = readTestResults(new File(pathToResults+filename));
                ControllerInformation bestNetwork = null;

                for (TestResult result : results) {
                    if (bestNetwork == null || bestNetwork.getFitness() > result.getCollisions()) {
                        bestNetwork = new ControllerInformation(result.getNetwork(), result.getCollisions(), null, 0);
                        System.out.println(bestNetwork.getFitness());
                        System.out.println(result.getCollisions());
                    }
                }

                saveNetwork(bestNetwork, pathToResults+targetFile);
            }
        }
    }

    private static void collectBestNetworks() throws IOException, ClassNotFoundException {
        JSONObject bestNetworkData = new JSONObject();
        for (String method : methods) {
            JSONObject methodData = new JSONObject();
            for (String track : tracks) {
                JSONObject trackData = new JSONObject();

                String pathToResults = pathToFolder + "\\" + method + "\\" + track + "\\results\\";
                ControllerInformation baseNetwork = readNetwork(new File(pathToResults+"baseBestNetwork.txt"));
                ControllerInformation genNetwork = readNetwork(new File(pathToResults+"genBestNetwork.txt"));

                JSONObject baseData = new JSONObject();
                baseData.put("network", SerializationUtils.serialize(baseNetwork.getNetwork()));
                baseData.put("collisions", baseNetwork.getFitness());

                JSONObject genData = new JSONObject();
                genData.put("network", SerializationUtils.serialize(genNetwork.getNetwork()));
                genData.put("collisions", genNetwork.getFitness());

                trackData.put("base", baseData);
                trackData.put("gen", genData);

                methodData.put(track, trackData);
            }
            bestNetworkData.put(method, methodData);
        }

        File file = new File(pathToFolder+"\\bestNetworks.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(bestNetworkData.toString());
        writer.close();
    }

    public static ControllerInformation[] readNetworks (File file) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(fis);

        ControllerInformation[] networks = (ControllerInformation[])in.readObject();

        in.close();
        fis.close();

        return networks;
    }

    public static ControllerInformation readNetwork (File file) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(fis);

        ControllerInformation network = (ControllerInformation)in.readObject();

        in.close();
        fis.close();

        return network;
    }

    public static TestResult[] readTestResults (File file) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream in = new ObjectInputStream(fis);

        TestResult[] results = (TestResult[])in.readObject();

        in.close();
        fis.close();

        return results;
    }

    public static void saveResults (ControllerInformation[] networks, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fos);

        out.writeObject(networks);

        out.close();
        fos.close();
    }

    public static void saveTestResults (TestResult[] results, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fos);

        out.writeObject(results);

        out.close();
        fos.close();
    }

    private static String readParameters(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        reader.close();
        return stringBuilder.toString();
    }

    public static void saveNetwork(ControllerInformation network, String filename) throws IOException {
        File file = new File(filename);

        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream out = new ObjectOutputStream(fos);

        out.writeObject(network);

        out.close();
        fos.close();
    }

    private static class SortByFitness implements Comparator<ControllerInformation> {
        public int compare(ControllerInformation a, ControllerInformation b) {
            if (a.getFitness() == b.getFitness()) {
                return 0;
            }else if (a.getFitness() > b.getFitness()) {
                return -1;
            }
            return 1;
        }
    }
}
