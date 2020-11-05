package intersectionmanagement.simulator.control;

import intersectionmanagement.simulator.Utility;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.networks.BasicNetwork;

public class HeuristicController implements CarController {
    @Override
    public float getTargetSpeed(double[] sensors) {
        // If there is a car in front, slow down depending on how fast they are going
        if (sensors[0] > 0.9) {
            return (float) (sensors[1]/Utility.CAR_SPEED_MAX);
        } else {
            return 1;
        }
    }

    @Override
    public NEATNetwork getNEATNetwork() {
        return null;
    }

    @Override
    public BasicNetwork getBasicNetwork() {
        return null;
    }
}
