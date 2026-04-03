import java.util.Arrays;

import static java.lang.Math.*;

public class Main {
    public static void main(String[] args) {
        int[] n = new int[14];
        for (var i = 2; i <= 15; i++) {
            n[i - 2] = i;
        }
//        System.out.println(Arrays.toString(n));
        float[] x = new float[15];
        for (int i = 0; i < 15; i++) {
            float tmp;
            do {
                tmp = (float) (random() * 9 - 6);
            } while (tmp == -6);
            x[i] = tmp;
        }
//        System.out.println(Arrays.toString(x));
        double[][] w = new double[14][15];
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 15; j++) {
                switch (n[i]) {
                    case 2:
                        w[i][j] = log(pow(3 * (abs(x[j]) + 1), 2));
                        break;
                    case 4, 5, 7, 11, 12, 14, 15:
                        w[i][j] = asin(1 / pow(E, pow(E, atan(1 / pow(E, abs(x[j]))))));
                        break;
                    default:
                        w[i][j] = tan(3 / x[j] * (0.25 - tan(x[j]))) / 2;
                }
            }
        }
//        System.out.println(Arrays.deepToString(w));
        String[][] w_4 = new String[14][15];
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 15; j++) {
                w_4[i][j] = String.format("%.4f", w[i][j]);
            }
        }
//        Вывод построчно
        for (int i = 0; i < 14; i++) {
            System.out.println(i + ": " + Arrays.toString(w_4[i]));
        }
//        Или можно вывод в одну строку
//        System.out.println(Arrays.deepToString(w_4));
    }
}
