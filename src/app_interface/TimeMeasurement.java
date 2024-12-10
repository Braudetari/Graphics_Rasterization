package app_interface;

import java.util.Arrays;

public class TimeMeasurement {
    private int N;
    private long startTime;
    private double[] measurements;
    private double lastMeasurement;
    private int currentIndex;
    private int count; // To track how many measurements have been added

    public TimeMeasurement(int N) {
        this.N = N;
        this.measurements = new double[N];
        this.currentIndex = 0;
        this.count = 0;
    }

    int getN() {
        return N;
    }

    public void start() {
        startTime = System.nanoTime();
    }

    public void stop() {
        lastMeasurement = (System.nanoTime() - startTime) / 1_000_000.0;
        measurements[currentIndex] = lastMeasurement;
        currentIndex = (currentIndex + 1) % N; // Move in a circular manner
        count = Math.min(count + 1, N); // Ensure count doesn't exceed N
    }

    public double getLastMeasurement() {
        return lastMeasurement;
    }

    public double getMeanOfLastN() {
        return calculateMean();
    }

    public double getStdOfLastN() {
        return calculateStd(getMeanOfLastN());
    }

    public double getMaxOfLastN() {
        return count > 0 ? Arrays.stream(getLastNMeasurements()).max().orElse(0) : 0;
    }

    public double getMinOfLastN() {
        return count > 0 ? Arrays.stream(getLastNMeasurements()).min().orElse(0) : 0;
    }

    // toString method
    @Override
    public String toString() {
        return String.format(
            "Last %d Measurements:\n" +
            "Mean: %.3f ms\n" +
            "Std: %.3f ms\n" +
            "Max: %.3f ms\n" +
            "Min: %.3f ms\n",
            count, getMeanOfLastN(), getStdOfLastN(), getMaxOfLastN(), getMinOfLastN()
        );
    }

    // Retrieve the last N measurements from the array
    private double[] getLastNMeasurements() {
        double[] lastNMeasurements = new double[count];
        for (int i = 0; i < count; i++) {
            int index = (currentIndex - count + i + N) % N; // Circular indexing
            lastNMeasurements[i] = measurements[index];
        }
        return lastNMeasurements;
    }

    private double calculateMean() {
        if (count == 0) return 0;
        double sum = 0;
        for (double value : getLastNMeasurements()) {
            sum += value;
        }
        return sum / count;
    }

    private double calculateStd(double mean) {
        if (count == 0) return 0;
        double sum = 0;
        for (double value : getLastNMeasurements()) {
            sum += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sum / count);
    }

    // Main method for testing
    public static void main(String[] args) {
        TimeMeasurement tm = new TimeMeasurement(5);
        tm.start();
        try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
        tm.stop();

        tm.start();
        try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }
        tm.stop();

        tm.start();
        try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
        tm.stop();

        tm.start();
        try { Thread.sleep(400); } catch (InterruptedException e) { e.printStackTrace(); }
        tm.stop();

        tm.start();
        try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
        tm.stop();

        System.out.println(tm);

    }
}
