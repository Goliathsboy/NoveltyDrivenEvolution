package intersectionmanagement.simulator.spawner;

import intersectionmanagement.simulator.Actor;
import intersectionmanagement.simulator.Simulator;
import intersectionmanagement.simulator.car.Car;
import intersectionmanagement.simulator.control.NeuralNetworkController;
import intersectionmanagement.simulator.track.Node;

import java.util.ArrayList;

public class CarSpawner extends Actor {


    public enum Function {LINEAR, CONSTANT, SIN}

    private int simulationSteps;
    private int steps;
    private int counter;
    private byte[] serializedNetwork;
    private Function function;
    private double[] params;
    private double randomness;

    public CarSpawner(Simulator simulator, Node spawnLocation, byte[] serializedNetwork, int simulationSteps, Function function, double[] params, double randomness) {
        super(simulator, spawnLocation);
        speed = 0f;
        radius = 0f;

        this.simulationSteps = simulationSteps;
        this.steps = 0;
        this.function = function;
        this.params = params;
        counter = simulator.getRNG().nextInt(getRate());
        this.serializedNetwork = serializedNetwork;
        this.randomness = randomness;
    }

    // Each step a counter decreases, when it reaches 0 it spawns a car and adjusts the rate based on a given function
    @Override
    protected void step(ArrayList<Actor> actorArray) {
        counter--;
        if (counter <= 0) {
            int rate = getRate();
            counter = rate + simulator.getRNG().nextInt((int) (rate*randomness));
            simulator.addCar(new Car(simulator, target, new NeuralNetworkController(serializedNetwork)));
        }
        if (steps < simulationSteps) {
            steps++;
        }
    }

    private int getRate() {
        switch (function) {
            case LINEAR:
                return linear();
            case CONSTANT:
                return constant();
            case SIN:
                return sin();
            default:
                throw new RuntimeException("No function specified for spawner");
        }
    }

    private int sin() {
        // params
        // 0 - period multiplier
        // 1 - min period
        // 2 - max period
        return (int) (params[1] + (1 - (0.5 + 0.5*Math.sin(params[0]*Math.PI*2*(steps*1.0)/simulationSteps)))*(params[2]-params[1]));
    }

    private int constant() {
        // params
        // 0 - rate
        return (int) params[0];
    }

    private int linear() {
        // params
        // 0 - min period
        // 1 - max period
        return (int) (params[0] + (1 - (steps*1.0)/simulationSteps)*(params[1]-params[0]));
    }
}
