import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by soomin on 2015. 4. 1..
 */
public class MoreOnMap {
    public static void main(String[] args) {
        Observable.just("Hello. world!")
            .map(new Func1<String, Integer>() {
                @Override
                public Integer call(String s) {
                    return s.hashCode();
                }
            })
            .subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer i) {
                    System.out.println(Integer.toString(i));
                }
            });
    }
}
