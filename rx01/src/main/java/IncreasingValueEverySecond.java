import rx.Observable;
import rx.functions.Action1;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class IncreasingValueEverySecond {
    public static void main(String... args) throws Exception {
        final CountDownLatch latch = new CountDownLatch(5);
        Observable
            .interval(1, TimeUnit.SECONDS)
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long counter) {
                    latch.countDown();
                    System.out.println("Got: " + counter);
                }
            });
        latch.await();
    }
}
