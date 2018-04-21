package my;

import java.util.List;

public class SumOfK {

    public static Integer chooseBestSum(int t, int k, List<Integer> ls) {
        if (k > ls.size()) return null;
        int idx[] = new int[k];
        for (int i = 0; i < k; i++) idx[i] = i;
        int best = 0;
        while (true) {
            int next = value(idx, ls);
            if (next > best && next <= t) best = next;
            if (increment(idx, ls.size())) break;
        }
        return best == 0 ? null : best;
    }

    private static int value(int[] idx, List<Integer> vals) {
        int sum = 0;
        for (int i = 0; i < idx.length; i++) sum += vals.get(idx[i]);
        return sum;
    }

    //return true when no more combinations
    private static boolean increment(int[] idx, int range) {
        for (int i = idx.length - 1; i >= 0; i--) {
            idx[i]++;
            if (idx[i] <= range - idx.length + i) {
                for (int j = i + 1; j < idx.length; j++) idx[j] = idx[j-1] + 1;
                return false;
            }
        }
        return true;
    }
}