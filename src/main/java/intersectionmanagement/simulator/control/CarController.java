package intersectionmanagement.simulator.control;

import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.networks.BasicNetwork;

public interface CarController {
    /*
    Must return a value between 0 and 1, 1 being full speed and 0 being stationary
     */
    float getTargetSpeed(double[] sensors);

    // Hack to support deserialization of both types of neural networks
    NEATNetwork getNEATNetwork();

    BasicNetwork getBasicNetwork();
}
