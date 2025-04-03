package fox.cloud;

import fox.num.Num3d;

public class PCloudQuestion {
    public final int numPoints;
    public final double[] distances;
    public final Num3d[] stations;

    public PCloudQuestion(int numPoints, Num3d[] stations) {
        this.numPoints = numPoints;
        this.distances = new double[sLen(numPoints)];
        this.stations = stations;
    }

    public static int sLen(int numPoints) {
        return numPoints * (numPoints - 1) / 2;
    }

    public int offset(int from, int to) {
        if (from > to) {
            return offset(to, from);
        }
        if (from == to) {
            return 0;
        }
        return (from * (2 * numPoints - from - 1)) / 2 + (to - from - 1);
    }

    public void tamper(double maxL) {
        for (int i = 0; i < distances.length; i++) {
            double diff = Math.random() * maxL * 2;
            diff = diff - maxL;
            distances[i] += diff;
        }
    }
}
