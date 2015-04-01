import rx.Observable;
import rx.functions.Action1;

public class JustInterestedInDataEvent {
    public static void main(String[] argv) {
        Observable
            .just(1, 2, 3)
            .subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    System.out.println("Got: " + integer);
                }
            });
    }
}
