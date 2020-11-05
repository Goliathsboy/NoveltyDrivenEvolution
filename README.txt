The code for handling controller training can be found in `src\main\java\intersectionmanagement\training`
and the code for running tests can be found in `src\main\java\intersectionmanagement\testing`.

Changes were made to both the simulator and Encog's code. The customised version of Encog is in the `org` package.

To compile the project for training, use the following:

* install Maven
* `cd IntersectionTraining`
* `mvn clean package`

A compiled jar file will then be present in `target`, a precompiled one can be found there currently.

To start a training run: 

* `cd IntersectionTraining`
* `java -jar target/IntersectionTraining-1.0-SNAPSHOT-jar-with-dependencies.jar <parameters_file> <method>`

Where <parameters_file> is generally "training_parameters.json" and <method> can be any of ["fitness", "hybrid", "novelty"].

The generation count and population size settings can be changed in the TrainingConfig class.

To run the visualiser, the Main.java class in `src\main\java\intersectionmanagement\trial` must be run
with the `<parameters_file>` argument, which is generally "rendered_parameters.json".