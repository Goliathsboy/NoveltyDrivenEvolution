package intersectionmanagement.simulator.spawner;

import intersectionmanagement.simulator.Actor;
import intersectionmanagement.simulator.Simulator;
import intersectionmanagement.simulator.pedestrian.Pedestrian;
import intersectionmanagement.simulator.track.Node;

import java.util.ArrayList;

public class PedestrianSpawner extends Actor {

    private int simulationSteps;
    private int steps;
    private int counter;
    private int pedestrianRate;
    private float pedestrianRandomness;

    public PedestrianSpawner(Simulator simulator, Node firstTarget, int simulationSteps, int pedestrianRate, float pedestrainRandomness) {
        super(simulator, firstTarget);
        speed = 0f;
        radius = 0f;

        this.simulationSteps = simulationSteps;
        this.pedestrianRate = pedestrianRate;
        this.pedestrianRandomness = pedestrainRandomness;
    }

    // Each step a counter decreases, when it reaches 0 it spawns a car and adjusts the rate based on a given function
    // For pedestrians it's a basic linear function with a randomness factor
    @Override
    protected void step(ArrayList<Actor> actorArray) {
        counter--;
        if (counter <= 0) {
            int rate = pedestrianRate;
            counter = rate + simulator.getRNG().nextInt((int) (rate*pedestrianRandomness));
            simulator.addPedestrian(new Pedestrian(simulator, target));
        }
        if (steps < simulationSteps) {
            steps++;
        }
    }
}
