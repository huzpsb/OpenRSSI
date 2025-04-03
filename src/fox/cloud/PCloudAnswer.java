package fox.cloud;

import fox.num.Num3d;

public class PCloudAnswer {
    public final Num3d[] points;
    public final int stations;

    public PCloudAnswer(Num3d[] points, int stations) {
        this.points = points;
        this.stations = stations;
    }

    public PCloudAnswer() {
        this.stations = 3;
        // 随机问题：地板上有三个基站，它们间隔1m，构成了等边直角三角形
        int numPoints = 10;
        points = new Num3d[numPoints];
        points[0] = new Num3d(0, 0, 100);
        points[1] = new Num3d(100, 0, 0);
        points[2] = new Num3d(0, 0, 0);
        // 人身上有7个动捕节点，水平间隔3m乱飞，最高2m，最低10cm
        for (int i = 3; i < numPoints; i++) {
            points[i] = new Num3d((Math.random() * 300) - 100, Math.random() * 200 + 10, (Math.random() * 300) - 100);
        }
    }

    public PCloudQuestion asQuestion(double tamper) {
        int numPoints = points.length;
        Num3d[] stationsArray = new Num3d[this.stations];
        System.arraycopy(points, 0, stationsArray, 0, stations);
        PCloudQuestion question = new PCloudQuestion(numPoints, stationsArray);
        for (int i = 0; i < numPoints; i++) {
            for (int j = i + 1; j < numPoints; j++) {
                question.distances[question.offset(i, j)] = points[i].dist(points[j]);
            }
        }
        question.tamper(tamper);
        return question;
    }
}
