# IntersectionSimulator
Author: Matthew P. Cherry

## What is this?
This is a simulator for testing neuro-evolution controllers in intersections.

Cars in the simulation follow a set path through the intersection, making random choices when they get to crossroads, and despawing when they reach the end of a path.
If a car collides with another car at any point, both will despawn.
If a car would spawn where another car is, it counts as a collision.
The total number of collisions is recorded.

Certain parts of the road are *active*, usually near the actual intersection itself (they look slightly brighter in the GUI). In these sections the car's neural network takes over control, otherwise a basic heuristic is used.

Pedestrians can also spawn, they follow a simple set path and have no controller.
They do cause collisions though.

Cars and pedestrians spawn randomly throughout the simulation as guided by the parameters used to launch the simulation.

## How do I compile it?
Using [Maven](https://maven.apache.org/).

* install Maven
* `cd Simulator`
* `mvn clean package`

You will now have a compiled jar file in `target`.

There is a pre-compiled version included as well.

## How do I run it?
This is an easy way to run the GUI and see what's actually happening in the simulation.

* `cd Simulator`
* `java -jar -Djava.library.path=target/natives target/Simulator-1.0-SNAPSHOT-jar-with-dependencies.jar sample_parameters.json`

You can edit `sample_parameters.json` to test different configurations, tracks, controllers, etc.

You can pause by pressing space bar.
You can click on a car to see the range of its sensors and which are active and by how much.
(You won't see any sensors except the front one active until the car is on an *active* part of the track)
## How do I  use this to run an experiment?
Take a look at the `Main` and `Trial` classes in the `trial` package.
There you can see how trials are created and executed.
You can setup your own main method for running your code, then just point the `pom.xml` to compile for that (ideally setup a separate Maven module so you can compile the Simulator and your code seperately).

A trial is just a single run of the simulator for a given set of parameters (see `sample_parameters.json`).
Currently you can either run a trial with the renderer to see the GUI or you can run it headless.
Use headless for training.

There is a sample trial file in resources.
You will see that the only difference between it and `sample_parameters.json` is the neural network field, this is where you can insert the candidate neural network you are evaluating when you run experiments.

The trial expects serialized Encog neural networks (BasicNetwork, NEAT or HyperNEAT).
That looks something like this:
```
import org.apache.commons.lang3.SerializationUtils;
...
BasicNetwork nn = new BasicNetwork();
SerializationUtils.serialize(nn));
```

## How do these tracks work?
Sorry they're a real pain to edit, most of them were generated with code.

All track files are encoded in JSON. A track is made up of a series of bezier curves of degree up to 4.

The file must contain a dictionary with fields:

* name - Name of the track
* created _(optional)_ - Date of creation in format dd-mm-yyyy
* description _(optional)_ - Description of the track
* curves - Curve data

The _curves_ field must have a list of dictionaries, each with fields:

* degree - The degree of the curve, minimum 2 and maximum 4
* precision - The number of points to have along the curve, minimum 2, defaults to 2
* x\__n_ - The x position of the nth point of the bezier curve
* y\__n_ - The y position of the nth point of the bezier curve

Example:

```json
{
  "name": "Sample",
  "created": "30-12-2017",
  "description": "Sample track",
  "curves":
  [
    {"degree": 2, "x_0": 10, "y_0": 10, "x_1": 50, "y_1": 50},
    {"degree": 3, "precision": 10, "x_0": 10, "y_0": 10, "x_1": 50, "y_1": 50, "x_2": 100, "y_2": 200}
  ]
}
```

If a curve has a start point that is the same as the end point of another curve, the two curves will be linked.
If there is a set of curves that forms a circular pattern with no non-circular curve to feed into it, the entire 
circular pattern will be removed.

Pedestrian tracks are just curves that have a special pedestrian value set to true.