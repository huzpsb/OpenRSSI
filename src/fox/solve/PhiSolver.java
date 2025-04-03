package fox.solve;

import fox.cloud.PCloudAnswer;
import fox.cloud.PCloudQuestion;
import fox.num.Num3d;

public class PhiSolver {
    private final PCloudQuestion question;
    private final Num3d[] pointsNow;
    private final Num3d[] motionLast;
    private final Num3d[] motionNow;
    private final int numStations;
    private double lr = 1;

    private void clearMotion() {
        System.arraycopy(motionNow, 0, motionLast, 0, motionNow.length);
        for (int i = 0; i < motionNow.length; i++) {
            motionNow[i] = new Num3d(0, 0, 0);
        }
    }

    private void calcMotion() {
        for (int i = 0; i < pointsNow.length; i++) {
            for (int j = i + 1; j < pointsNow.length; j++) {
                // FIXME 在基站定位等问题中，基站多，被追踪对象少，此算法乃至数据结构便是低效的。知道就行 ;E 反正你不会优化...
                double want = getDist(i, j);
                double real = pointsNow[i].dist(pointsNow[j]);
                double diff = want - real;

                Num3d motionI = pointsNow[i].subtract(pointsNow[j]);
                motionI = motionI.ofScale(diff);

                motionI = motionI.scale(1 / Math.pow(want, 0.1));

                motionNow[i] = motionNow[i].add(motionI);
                motionNow[j] = motionNow[j].subtract(motionI);
            }
        }
    }

    private void updatePos() {
        for (int i = numStations; i < pointsNow.length; i++) {
            pointsNow[i] = pointsNow[i].add(motionNow[i].ofScale(lr));
            // FIXME 此处限制y必须在5以上是因为在动作捕捉中，节点的高度一定不低于5cm。这个假设在其他应用中不一定成立，需要灵活处理。
            if (pointsNow[i].y < 5) {
                pointsNow[i] = new Num3d(
                        pointsNow[i].x,
                        5,
                        pointsNow[i].z
                );
            }
        }
    }

    private double getDist(int from, int to) {
        return question.distances[question.offset(from, to)];
    }

    private void updateLr() {
        int neg = 0;
        for (int i = 0; i < motionNow.length; i++) {
            if (motionNow[i].dot(motionLast[i]) < 0) {
                neg++;
            }
        }
        if (neg > motionNow.length / 1.9) {
            lr *= 0.9;
        } else {
            lr *= 1.5;
        }
    }

    private void step() {
        clearMotion();
        calcMotion();
        updatePos();
        updateLr();
    }

    private void solve(int maxSteps) {
        for (int i = 0; i < maxSteps; i++) {
            step();
            if (lr < 1e-5) {
                break;
            }
        }
    }

    public PhiSolver(PCloudQuestion question) {
        this.question = question;
        this.pointsNow = new Num3d[question.numPoints];
        this.numStations = question.stations.length;

        System.arraycopy(question.stations, 0, pointsNow, 0, numStations);

        for (int i = numStations; i < question.numPoints; i++) {
            double height = 0;
            for (int j = 0; j < numStations; j++) {
                height += getDist(i, j);
            }
            height /= numStations;

            double x = 0;
            double z = 0;
            double weigh = 0;
            for (int j = 0; j < numStations; j++) {
                double w = getDist(i, j);
                w = 1 / (w * w);
                x += pointsNow[j].x * w;
                z += pointsNow[j].z * w;
                weigh += w;
            }
            pointsNow[i] = new Num3d(x / weigh, height, z / weigh);
        }

        motionLast = new Num3d[question.numPoints];
        motionNow = new Num3d[question.numPoints];
        clearMotion();
        calcMotion();
        updatePos();
        // 这里不能简化为step()，因为此时motionLast尚且为全null。
    }

    public static PCloudAnswer solve(PCloudQuestion q) {
        PhiSolver solver = new PhiSolver(q);
        solver.solve(2000);
        return new PCloudAnswer(solver.pointsNow, solver.numStations);
    }
}
