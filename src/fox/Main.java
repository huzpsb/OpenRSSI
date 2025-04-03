package fox;

import fox.cloud.PCloudAnswer;
import fox.cloud.PCloudQuestion;
import fox.solve.PhiSolver;

public class Main {
    public static void main(String[] args) {
        final int trails = 1000;
        long startTime = System.currentTimeMillis();

        int cnt = 0;
        double loss = 0;

        for (int i = 0; i < trails; i++) {
            PCloudAnswer answer = new PCloudAnswer();
            PCloudQuestion question = answer.asQuestion(5);
            PCloudAnswer solution = PhiSolver.solve(question);
            for (int w = 3; w < solution.points.length; w++) {
                cnt++;
                loss += solution.points[w].dist(answer.points[w]);
            }
        }

        System.out.println("Average loss: " + (loss / cnt));
        System.out.println("fps: " + (1.0 * trails / ((System.currentTimeMillis() - startTime)) * 1000));
    }
}
