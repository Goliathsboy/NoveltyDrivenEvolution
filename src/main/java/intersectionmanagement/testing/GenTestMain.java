package intersectionmanagement.testing;

import intersectionmanagement.training.ControllerInformation;
import intersectionmanagement.training.TestResult;
import intersectionmanagement.trial.PerformanceValues;
import intersectionmanagement.trial.Trial;
import org.apache.commons.lang3.SerializationUtils;
import org.encog.neural.neat.NEATNetwork;
import org.json.JSONObject;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class GenTestMain {
//    private static String pathToFolder = "C:\\Users\\golia\\Documents\\CHPC\\TrainingRuns";
    private static String pathToFolder = "Second_Tests";
    private static String[] methods = {"Fitness", "Hybrid", "Novelty"};
    private static String[] tracks = {"four-way-2-lane-no-pedestrian", "four-way-no-pedestrian", "four-way-pedestrian","four-way-2-lane-pedestrian"}; //"four-way-2-lane-pedestrian",

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        JSONObject jsonParameters = new JSONObject(readParameters("experiment_parameters.json"));

        for (String method : methods) {
            for (String track : tracks) {
                String pathToResults = pathToFolder+"/"+method+"/"+track+"/results/";

                String generalisationTrack = "four-way-3-lane";
                switch (track) {
                    case "four-way-pedestrian":
                        generalisationTrack = "four-way-pedestrian-gen";
                        break;
                    case "four-way-2-lane-pedestrian":
                        generalisationTrack = "four-way-2-lane-pedestrian-gen";
                        break;
                }

                GenTestThread threadTest = new GenTestThread("tracks/"+generalisationTrack+".json", pathToResults, 20000, jsonParameters.getJSONObject("spawner"));
                threadTest.start();
            }
        }
    }

    private static String readParameters(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        reader.close();
        return stringBuilder.toString();
    }

    private static class GenTestThread extends Thread {
        private String trackFile;
        private String pathToResults;
        private int simulationSteps;
        private JSONObject spawner;

        public GenTestThread (String trackFile, String pathToResults, int simulationSteps, JSONObject spawner) {
            this.trackFile = trackFile;
            this.pathToResults = pathToResults;
            this.simulationSteps = simulationSteps;
            this.spawner = spawner;
        }

        public void run() {
            try{
                runGeneralisationTest();
            }
            catch (Exception e){
                System.out.println(Arrays.toString(e.getStackTrace()));
                System.out.println(e.getCause());
            }
        }

        private void runGeneralisationTest() throws IOException, ClassNotFoundException {
            System.out.println(pathToResults+"sampled20Networks.txt");
            ControllerInformation[] networks = readNetworks(new File(pathToResults+"sampled20Networks.txt"));


            TestResult[] results20k = new TestResult[networks.length];

            for (int i = 0; i<networks.length; i++) {
                results20k[i] = runTest(trackFile, networks[i].getNetwork(), simulationSteps, spawner);
            }

            saveTestResults(results20k, new File(pathToResults+"generalisationTestResults20k.txt"));
        }

        private TestResult runTest (String trackFile, NEATNetwork network, int simulationSteps, JSONObject spawner) throws IOException {
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

        private void saveTestResults (TestResult[] results, File file) throws IOException {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(fos);

            out.writeObject(results);

            out.close();
            fos.close();
        }

        public static ControllerInformation[] readNetworks (File file) throws IOException, ClassNotFoundException {
            System.out.println(file.exists());
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(fis);

            ControllerInformation[] networks = (ControllerInformation[])in.readObject();

            in.close();
            fis.close();

            return networks;
        }
    }
}


