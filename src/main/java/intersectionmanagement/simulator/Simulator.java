package intersectionmanagement.simulator;

import intersectionmanagement.simulator.car.Car;
import intersectionmanagement.simulator.pedestrian.Pedestrian;

import java.util.*;

public class Simulator {
    private ArrayList<Actor> actorArray;
    private ArrayList<Car> carArray;
    private ArrayList<Pedestrian> pedestrianArray;
    private ArrayList<Actor> newActorBuffer;

    private ArrayList<Car> allCarsArray; //To be used in total average speed calculation

    private int collisions = 0;
    private int throughput = 0;

    private Random rng;

    public Simulator(long rngSeed) {
        rng = new Random(rngSeed);
        actorArray = new ArrayList<>();
        carArray = new ArrayList<>();
        pedestrianArray = new ArrayList<>();
        newActorBuffer = new ArrayList<>();

        allCarsArray = new ArrayList<>(); //To be used in total average speed calculation
    }

    // Main loop that runs through the simulation
    public ArrayList<Actor> step() {
        ArrayList<Actor> removedActorArray = new ArrayList<>();

        for (Actor actor : actorArray) {
            actor.step(actorArray);
            actor.moveTowardsTarget();
            if (actor.isFinished()) {
                removedActorArray.add(actor);
                throughput++;
            }
        }

        for (Actor actor : actorArray) {
            if (detectCollision(actor)) {
                removedActorArray.add(actor);
                collisions++;
            }
        }

        // Some actors add new actors, so we have to use a buffer to avoid modifying the actor array while we're looping through it to step
        actorArray.addAll(newActorBuffer);
        newActorBuffer.clear();
        actorArray.removeAll(removedActorArray);
        carArray.removeAll(removedActorArray);
        pedestrianArray.removeAll(removedActorArray);

        return actorArray;
    }

    public ArrayList<Car> getCars() {
        return carArray;
    }

    public ArrayList<Car> getAllCars() {
        return allCarsArray;
    } //To be used in total average speed calculation

    public ArrayList<Pedestrian> getPedestrians() {
        return pedestrianArray;
    }

    private boolean detectCollision(Actor actor) {
        for (Actor otherActor : actorArray) {
            if (actor == otherActor) {
                continue;
            }

            if (!actor.isSolid() || !otherActor.isSolid()) {
                continue;
            }

            if (Utility.distance(actor.getX(), actor.getY(), otherActor.getX(), otherActor.getY()) < actor.getRadius() + otherActor.getRadius()) {
                return true;
            }
        }

        return false;
    }

    public void addActor(Actor actor) {
        newActorBuffer.add(actor);
    }

    public void addCar(Car car) {
        newActorBuffer.add(car);
        carArray.add(car);

        allCarsArray.add(car); //To be used in total average speed calculation
    }

    public void addPedestrian(Pedestrian pedestrian) {
        newActorBuffer.add(pedestrian);
        pedestrianArray.add(pedestrian);
    }

    public int getCollisions() {
        return collisions/2;  // there are two actors involved in every collision so collisions ends up doubled, divide by two to get the true number of collisions
    }

    public int getThroughput() {
        return throughput;
    }

    public Random getRNG() {
        return rng;
    }
}
