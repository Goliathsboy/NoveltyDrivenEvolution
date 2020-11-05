package intersectionmanagement.trial;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;


public class Main {
    public static void main(String[] args) throws IOException {
        String parameters = new String(Files.readAllBytes(Paths.get(args[0])), StandardCharsets.UTF_8);

        try {
            Trial trial = new Trial(parameters);
            trial.runSimulationRendered();
//            trial.runSimulation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
