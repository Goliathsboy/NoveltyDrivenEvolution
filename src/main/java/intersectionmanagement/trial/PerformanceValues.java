package intersectionmanagement.trial;

public class PerformanceValues {
    private int collisions;
    private int throughput;

    private float[] noveltyData;

    public PerformanceValues(int collisions, int throughput, float[] noveltyData) {
        this.collisions = collisions;
        this.throughput = throughput;
        this.noveltyData = noveltyData;
    }

    public int getCollisions() {
        return collisions;
    }

    public int getThroughput() {
        return throughput;
    }

    public float[] getNoveltyData() {
        return noveltyData;
    }
}


