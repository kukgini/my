import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by soomin on 2015. 4. 2..
 */
public class ParalleSort {
    public static void main(String[] args) {

        Observable<List<Integer>> num1 = createNumbers(11,20);
        Observable<List<Integer>> num2 = createNumbers(6,10);
        Observable<List<Integer>> num3 = createNumbers(1, 5);

        num1.subscribe(new Action1<List<Integer>>() {
            @Override
            public void call(List<Integer> integers) {
                Debug.print(integers.toString());
            }
        });
        num2.subscribe(new Action1<List<Integer>>() {
            @Override
            public void call(List<Integer> integers) {
                Debug.print(integers.toString());
            }
        });
        num3.subscribe(new Action1<List<Integer>>() {
            @Override
            public void call(List<Integer> integers) {
                Debug.print(integers.toString());
            }
        });

//        Observable.zip(num1, num2, num3, new Func3<Integer, Integer, Integer>() {
//            @Override
//            public Object call(Object o, Object o2, Object o3) {
//                return null;
//            }
//        });
        waitASeconds(5);
    }

    public static Observable<List<Integer>> createNumbers(int from, int to) {
        List<Integer> l = new ArrayList<Integer>();
        for(int i = to; i >= from; i--){
            l.add(i);
        }
        return Observable.from(l)
            .subscribeOn(Schedulers.computation())
            .toSortedList();
    }

    public static void waitASeconds(int sec) {
        try {
            Thread.sleep(sec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
