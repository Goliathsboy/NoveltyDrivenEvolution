package intersectionmanagement.simulator.car;

import intersectionmanagement.simulator.Actor;
import intersectionmanagement.simulator.Utility;

import java.util.ArrayList;

public class Sensor {
    private float distance;
    private float angle;
    private float radius;
    private boolean lastActivated = false;
    public float lastSpeed = 0;

    // Sensor used to pass information to the neural network, each sensor is a single circle and they are arranged in rows
    Sensor(float distance, float angle, float radius) {
        this.distance = distance;
        this.angle = angle;
        this.radius = radius;
    }

    public float getX(Car parent) {
        return calculateX(parent.getX(), parent.getDirection());
    }

    public float getY(Car parent) {
        return calculateY(parent.getY(), parent.getDirection());
    }

    public float getRadius() {
        return radius;
    }

    public boolean activated(Car parent, ArrayList<Actor> actorArray) {
        float x = calculateX(parent.getX(), parent.getDirection());
        float y = calculateY(parent.getY(), parent.getDirection());

        for (Actor actor : actorArray) {
            if (actor == parent) {
                continue;
            }
            if (!actor.isSolid()) {
                continue;
            }
            if (Utility.distance(x, y, actor.getX(), actor.getY()) < this.radius + actor.getRadius()) {
                lastActivated = true;
                lastSpeed = actor.getSpeed();
                return true;
            }
        }
        lastActivated = false;
        return false;
    }

    // Used for the GUI, just gets whether the actor is active without going through all the collision detection again
    public boolean getLastActivated() {
        return lastActivated;
    }

    // Calculates the actual location of the sensor relative to the car
    private float calculateX(float anchorX, float anchorDirection) {
        return anchorX + (float) Math.cos(anchorDirection + angle) * distance;
    }

    private float calculateY(float anchorY, float anchorDirection) {
        return anchorY + (float) Math.sin(anchorDirection + angle) * distance;
    }
}
