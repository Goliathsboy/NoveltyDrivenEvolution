package intersectionmanagement.simulator;

import intersectionmanagement.simulator.track.Node;

import java.util.ArrayList;

public abstract class Actor {
    private float x;
    private float y;
    private float direction;
    private boolean finished;

    protected Simulator simulator;
    protected Node target;
    protected Node currentNode;
    protected boolean solid;

    protected float speed;
    protected float radius;

    protected float targetSpeed;

    protected Actor(Simulator simulator, Node firstTarget) {
        this.simulator = simulator;
        target = firstTarget;
        currentNode = firstTarget;
        x = target.getX();
        y = target.getY();
        finished = false;
        targetSpeed = 0;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getDirection() {
        return direction;
    }

    public float getSpeed() {
        return speed;
    }

    public float getRadius()
    {
        return radius;
    }

    public float getTargetSpeed() {
        return targetSpeed;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isSolid() {
        return solid;
    }

    // Critical physics engine that actually makes things move with time
    void moveTowardsTarget() {
        float move_x = target.getX() - x;
        float move_y = target.getY() - y;

        direction = (float) Math.atan2(move_y, move_x);

        float distance = distanceToTarget();

        move_x = (move_x / distance) * speed;
        move_y = (move_y / distance) * speed;

        if (distance > 0) {
            x += move_x;
            y += move_y;
        }

        // Logic for aiming for waypoints on the track, when the actor gets close enough it gets the next target and starts moving towards that
        if (distanceToTarget() < radius) {
            if (target.getNextNodes().size() > 0) {
                int nextNode = simulator.getRNG().nextInt(target.getNextNodes().size());
                currentNode = target;
                target = target.getNextNodes().get(nextNode);
            } else {
                finished = true;
            }
        }
    }

    protected void step(ArrayList<Actor> actorArray) {}

    private float distanceToTarget() {
        return Utility.distance(x, y, target.getX(), target.getY());
    }

}
