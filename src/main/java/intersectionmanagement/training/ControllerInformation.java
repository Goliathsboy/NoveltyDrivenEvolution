package intersectionmanagement.training;

import org.encog.neural.neat.NEATLink;
import org.encog.neural.neat.NEATNetwork;

import java.io.Serializable;

public class ControllerInformation implements Comparable<ControllerInformation>, Serializable {
    private NEATNetwork network;
    private double fitness;
    private float[] noveltyData;
    private double noveltyScore;

    public ControllerInformation(NEATNetwork network, double fitness, float[] noveltyData, double noveltyScore) {
        this.network = network;
        this.fitness = fitness;
        this.noveltyData = noveltyData;
        this.noveltyScore = noveltyScore;
    }

    public NEATNetwork getNetwork() {
        return network;
    }

    public double getFitness() {
        return fitness;
    }

    public float[] getNoveltyData() {
        return noveltyData;
    }

    public double getNoveltyScore() {
        return noveltyScore;
    }

    public int compareTo(ControllerInformation other) {
        int result = 0;
        if (other.getNoveltyScore() > noveltyScore) {
            result = 1;
        }else if (other.getNoveltyScore() < noveltyScore) {
            result = -1;
        }
        return result;
    }
}
