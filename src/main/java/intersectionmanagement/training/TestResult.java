package intersectionmanagement.training;

import org.encog.neural.neat.NEATNetwork;

import java.io.Serializable;

public class TestResult implements Serializable {
    private NEATNetwork network;
    private double collisions;
    private float[] noveltyData;
    private double throughput;

    public TestResult(NEATNetwork network, double collisions, double throughput) {
        this.network = network;
        this.collisions = collisions;
        this.throughput = throughput;
    }

    public NEATNetwork getNetwork() {
        return network;
    }

    public double getCollisions() {
        return collisions;
    }

    public double getThroughput() {
        return throughput;
    }
}
