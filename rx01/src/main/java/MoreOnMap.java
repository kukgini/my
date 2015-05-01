import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by soomin on 2015. 4. 1..
 */
public class MoreOnMap {
    public static void main(String[] args) {
        Observable.from(new String[]{"Hello","world!"}).subscribeOn(Schedulers.io())
            .map(new Func1<String, Integer>() {
                @Override
                public Integer call(String s) {
                    Debug.print(s + " -> " + s.hashCode());
                    return s.hashCode();
                }
            }).observeOn(Schedulers.computation())
            .subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer i) {
                    Debug.print(Integer.toString(i));
                }
            });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
