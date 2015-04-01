import rx.Observable;
import rx.functions.Action1;
import rx.observables.BlockingObservable;

import java.util.concurrent.TimeUnit;

public class BlocksAndCallsEveryEmittedItems {
    public static void main(String... args) {
        BlockingObservable<Long> observable = Observable
                .interval(1, TimeUnit.SECONDS)
                .toBlocking();

        observable.forEach(new Action1<Long>() {
            @Override
            public void call(Long counter) {
                System.out.println("Got: " + counter);
            }
        });
    }
}
