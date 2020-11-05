package intersectionmanagement.simulator.control;

import org.apache.commons.lang3.SerializationUtils;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.networks.BasicNetwork;

public class NeuralNetworkController implements CarController {

    private NEATNetwork neatNetwork;
    private BasicNetwork basicNetwork;
    private MLRegression neuralNetwork;

    public NeuralNetworkController(byte[] serializedNetwork) {
        // Hack to deserialize the network and set it depending on what type it is
        try {
            neatNetwork = SerializationUtils.deserialize(serializedNetwork);
            neuralNetwork = neatNetwork;
        } catch (ClassCastException e) {
            //System.out.println("Not NEAT");
        }
        try {
            basicNetwork = SerializationUtils.deserialize(serializedNetwork);
            neuralNetwork = basicNetwork;
        } catch (ClassCastException e) {
            //System.out.println("Not basic");
        }
    }

    @Override
    public float getTargetSpeed(double[] sensors) {
        MLData inputData = new BasicMLData(sensors.length);
        inputData.setData(sensors);
        MLData outputData = neuralNetwork.compute(inputData);
        return (float) (outputData.getData(0));
    }

    @Override
    public NEATNetwork getNEATNetwork() {
        return neatNetwork;
    }

    @Override
    public BasicNetwork getBasicNetwork() {
        return basicNetwork;
    }
}
