package intersectionmanagement.simulator.car;

import intersectionmanagement.simulator.Actor;
import intersectionmanagement.simulator.Simulator;
import intersectionmanagement.simulator.Utility;
import intersectionmanagement.simulator.control.CarController;
import intersectionmanagement.simulator.control.HeuristicController;
import intersectionmanagement.simulator.track.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Car extends Actor {
    private CarController controller;
    private HeuristicController inactiveController;
    private boolean controllerActive;
    private List<List<Sensor>> sensorArray;

    private int activeStepsCount;

    private float lastSpeed;
    private float[] speedDeltaTotal;
    private boolean[] sensorDetection;
    private int[] detectionNumbers;


    public Car(Simulator simulator, Node firstTarget, CarController controller) {
        super(simulator, firstTarget);
        speed = Utility.CAR_SPEED_MAX;  // start out full speed so that cars don't just crash into each other
        radius = Utility.CAR_RADIUS;
        solid = true;

        this.controller = controller;
        this.inactiveController = new HeuristicController();
        controllerActive = false;
        sensorArray = new ArrayList<>();
        createSensorRow(0, 60, 32);
        createSensorRow((float) Math.PI, 30, 16);
        createSensorRow(-0.3f, 55, 16);
        createSensorRow(0.3f, 55, 16);
        createSensorRow(-0.65f, 50, 16);
        createSensorRow(0.65f, 50, 16);
        createSensorRow(-1.0f, 45, 16);
        createSensorRow(1.0f, 45, 16);

        activeStepsCount = 0;

        lastSpeed = 0;
//        speedDeltaTotal = new float[8];
//        sensorDetection = new boolean[8];
//        detectionNumbers = new int[8];
        speedDeltaTotal = new float[16];
        sensorDetection = new boolean[16];
        detectionNumbers = new int[16];
    }

    @Override
    protected void step(ArrayList<Actor> actorArray) {
        controllerActive = currentNode.isActive();

        if (controllerActive) {
            targetSpeed = controller.getTargetSpeed(getSensorValues(actorArray));
        } else {
            targetSpeed = inactiveController.getTargetSpeed(getSensorValues(actorArray, 1)); // use the front sensor just to get the speed of the car in front
        }

        float acceleration = limitAcceleration(targetSpeed*Utility.CAR_SPEED_MAX - speed);

        speed += acceleration;
        limitSpeed();

        // Custom
        if (controllerActive) {
            activeStepsCount++;
        }
        for (int i = 0; i < speedDeltaTotal.length; i++) {
            if (sensorDetection[i]) {
                speedDeltaTotal[i] += speed - lastSpeed;
                detectionNumbers[i]++;
            }
        }
        lastSpeed = speed;
    }

    private void limitSpeed() {
        if (speed > Utility.CAR_SPEED_MAX) {
            speed = Utility.CAR_SPEED_MAX;
        }

        if (speed < Utility.CAR_SPEED_MIN) {
            speed = Utility.CAR_SPEED_MIN;
        }
    }

    private float limitAcceleration(float acceleration) {
        if (acceleration > Utility.CAR_ACCELERATION) {
            acceleration = Utility.CAR_ACCELERATION;
        }

        if (acceleration < -1*Utility.CAR_ACCELERATION) {
            acceleration = -1*Utility.CAR_ACCELERATION;
        }

        return acceleration;
    }

    // Sensors are arranged as a set of circles in a row coming out of the car, for a row only one value is returned for distance
    private void createSensorRow(float angle, float distance, int quantity) {
        LinkedList<Sensor> sensorRow = new LinkedList<>();
        for (int i = 0; i < quantity; i++) {
            // Add one to i because we want sensors starting off the car and ending at final distance
            float sensorDistance = (distance / quantity) * (i + 1);
            sensorRow.add(new Sensor(sensorDistance, angle, sensorDistance*0.15f));
        }
        sensorArray.add(sensorRow);
    }

    public List<List<Sensor>> getSensors() {
        return sensorArray;
    }

    private double[] getSensorValues(ArrayList<Actor> actorArray) {
        return getSensorValues(actorArray, sensorArray.size());
    }

    private double[] getSensorValues(ArrayList<Actor> actorArray, int maxSensors) {
        double[] sensorValues;
        if (maxSensors == 1) {
            sensorValues = new double[2];
        } else {
            sensorValues = new double[maxSensors];
        }
        // For each sensor row, get the closest sensor in that row that is in contact with another actor
        for (int i = 0; i < maxSensors; i++) {
            List<Sensor> sensorRow = sensorArray.get(i);
            for (Sensor sensor : sensorRow) {
                if (sensor.activated(this, actorArray)) {
                    // Sensor row's value is 1 if the closest circle is in contact and 0 if none are in contact
                    float value = 1 - ((sensorRow.indexOf(sensor)*1.0f) / sensorRow.size());
                    sensorValues[i] = value;

                    // Little hack to allow the heuristic controller to use the car in front's speed
                    if (maxSensors == 1) {
                        sensorValues[1] = sensor.lastSpeed;
                        sensorDetection[i] = false;
                        sensorDetection[i+1] = false;
                    } else {
                        sensorDetection[i] = value > 0.5;
                        sensorDetection[i+1] = value <= 0.5;
                    }
                    break;
                }
            }
        }
        return sensorValues;
    }

    public int getActiveStepsCount() {
        return activeStepsCount;
    }

    public float[] getSpeedDeltaTotal() {
        return speedDeltaTotal;
    }

    public int[] getDetectionNumbers() {
        return detectionNumbers;
    }
}
