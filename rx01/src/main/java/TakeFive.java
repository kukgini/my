import rx.Observable;
import rx.Subscriber;

public class TakeFive {
    public static void main(String[] argv) {
        Observable
            .just("1.The", "2.Dave", "3.Brubeck", "4.Quartet", "5.Time", "6.Out")
            .take(5)
            .subscribe(new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    System.out.println("Completed Observable.");
                }

                @Override
                public void onError(Throwable throwable) {
                    System.err.println("Whoops: " + throwable.getMessage());
                }

                @Override
                public void onNext(String name) {
                    System.out.println("Got: " + name);
                }
            });
    }
}
