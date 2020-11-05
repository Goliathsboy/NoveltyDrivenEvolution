package intersectionmanagement.trial;

import intersectionmanagement.simulator.Utility;
import intersectionmanagement.simulator.car.Car;
import intersectionmanagement.simulator.car.Sensor;
import intersectionmanagement.simulator.pedestrian.Pedestrian;
import intersectionmanagement.simulator.track.Node;
import org.apache.commons.lang3.SerializationUtils;
import org.encog.neural.neat.NEATLink;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Renderer {
    private static float scale;

    private static Trial trial;
    private static ArrayList<Car> selectedActors;

    private static final int OFFSET = 400;

    static void setupWindow(Trial trial, float scale, int width, int height) throws LWJGLException {
        Display.setDisplayMode(new DisplayMode(width, height));
        Display.create(new PixelFormat(8,0,0,8));
        Display.setTitle("Simulation");
        GL11.glLoadIdentity(); // Resets any previous projection matrices
        GL11.glOrtho(OFFSET, width+OFFSET, height+OFFSET, OFFSET, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Renderer.scale = scale;
        Renderer.trial = trial;
        selectedActors = new ArrayList<>();
    }

    static void drawActors(ArrayList<Car> cars, ArrayList<Pedestrian> pedestrians, List<Node> track) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(binToFloat(0),binToFloat(43),binToFloat(54),1);

        List<Node> seenNodes = new LinkedList<>();
        List<Node> nextNodes = new LinkedList<>();
        List<Node> currentNodes = new LinkedList<>(track);

        // Draw track, recursive stuff going on here
        while (!currentNodes.isEmpty()) {
            for (Node node : currentNodes) {
                for (Node nextNode : node.getNextNodes()) {
                    if (node.isActive()) {
                        drawLine(node.getX(), node.getY(), nextNode.getX(), nextNode.getY(), 2, binToFloat(131), binToFloat(148), binToFloat(150), 1);
                    }
                    else {
                        drawLine(node.getX(), node.getY(), nextNode.getX(), nextNode.getY(), 2, binToFloat(88), binToFloat(110), binToFloat(117), 1);
                    }
                    nextNodes.add(nextNode);
                }
            }
            currentNodes.clear();
            currentNodes.addAll(nextNodes);
            currentNodes.removeAll(seenNodes);
            seenNodes.addAll(nextNodes);
            nextNodes.clear();
        }

        // Draw cars
        for (Car car : cars) {
            drawCircle(20, car.getX(), car.getY(), car.getRadius(), binToFloat(133), binToFloat(153), binToFloat(0), 1);

            // Draw the sensors for selected cars
            if (selectedActors.contains(car)) {
                for (List<Sensor> sensorRow : car.getSensors()) {
                    for (Sensor sensor : sensorRow) {
                        if (sensor.getLastActivated()) {
                            drawCircle(20, sensor.getX(car), sensor.getY(car), sensor.getRadius(), binToFloat(211), binToFloat(54), binToFloat(130), 0.2f);
                            break;
                        } else {
                            drawCircle(20, sensor.getX(car), sensor.getY(car), sensor.getRadius(), binToFloat(42), binToFloat(161), binToFloat(152), 0.1f);
                        }
                    }

                }
            }
        }

        // Draw pedestrians
        for (Pedestrian pedestrian : pedestrians) {
            drawCircle(20, pedestrian.getX(), pedestrian.getY(), pedestrian.getRadius(), binToFloat(133), binToFloat(153), binToFloat(0), 1);
        }

        Display.update();
        Display.sync(60);
    }


    private static boolean spaceDown = false;
    private static boolean leftMouseDown = false;

    static void handleInput(ArrayList<Car> actors) {
        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            if (!spaceDown) {
                trial.toggleSimulating();
                spaceDown = true;
            }
        } else {
            spaceDown = false;
        }

        if (Mouse.isButtonDown(0)) {
            if (!leftMouseDown) {
                float x = Mouse.getX()+OFFSET;
                float y = Display.getHeight() - Mouse.getY()+OFFSET;
                for (Car actor : actors) {
                    if (Utility.distance(x, y, actor.getX()*scale, actor.getY()*scale) < actor.getRadius()*scale) {
                        if (selectedActors.contains(actor)) {
                            selectedActors.remove(actor);
                        } else {
                            selectedActors.add(actor);
                        }
                    }
                }
                leftMouseDown = true;
            }
        } else {
            leftMouseDown = false;
        }
    }

    private static void drawCircle(int slices, float x, float y, float radius, float r, float g, float b, float a) {
        GL11.glColor4f(r, g, b, a);
        GL11.glBegin(GL11.GL_POLYGON);

        for (int i = 0; i < slices; i++)
        {
            double rad = (i/(slices*1.0))*2*Math.PI;
            GL11.glVertex2f(scale*x + (float) Math.cos(rad)*radius*scale, scale*y + (float)Math.sin(rad)*radius*scale);
        }

        GL11.glEnd();
    }

    private static void drawLine(float x1, float y1, float x2, float y2, float width, float r, float g, float b, float a) {
        GL11.glColor4f(r, g, b, a);
        GL11.glLineWidth(width);
        GL11.glBegin(GL11.GL_LINES);

        GL11.glVertex2f(scale*x1, scale*y1);
        GL11.glVertex2f(scale*x2, scale*y2);

        GL11.glEnd();
    }

    private static float binToFloat(int binary) {
        return binary/255.0f;
    }
}
