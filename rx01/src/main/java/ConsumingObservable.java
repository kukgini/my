import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;

public class ConsumingObservable {
    public static void main(String[] argv) {
        Observable
            .just(1, 2, 3)
            .doOnNext(new Action1<Integer>() {
                @Override
                public void call(Integer integer) {
                    if (integer.equals(2)) {
                        throw new RuntimeException("I don't like 2");
                    }
                }
            })
            .subscribe(new Subscriber<Integer>() {
                @Override
                public void onCompleted() {
                    System.out.println("Completed Observable.");
                }

                @Override
                public void onError(Throwable throwable) {
                    System.err.println("Whoops: " + throwable.getMessage());
                }

                @Override
                public void onNext(Integer integer) {
                    System.out.println("Got: " + integer);
                }
            });
    }
}
