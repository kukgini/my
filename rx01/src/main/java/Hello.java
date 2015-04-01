import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Hello {
    public static void main(String[] args) {
        new Hello().hello();
    }

    public static void hello() {

        List<String> names = new ArrayList<String>();
        names.add("1-1");
        names.add("1-2");
        names.add("1-3");

        //Observable<String> name = Observable.from(names);
        Observable<String> name1 = Observable.from(names).subscribeOn(Schedulers.io()).map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                try { Thread.sleep(200); } catch (Exception e) {}
                Debug.print("mapping1 : " + s);
                return "Hello " + s + "!";
            }
        });
        names.add("1-4");

        List<String> names2 = new ArrayList<String>();
        names2.add("2-1");
        names2.add("2-2");
        names2.add("2-3");
        Observable<String> name2 = Observable.from(names2).subscribeOn(Schedulers.io()).map(new Func1<String, String>() {
            @Override
            public String call(String s) {
                try { Thread.sleep(100);} catch (Exception e) {}
                Debug.print("mapping2 : " + s);
                return "Hello " + s + "!";
            }
        });
        names.add("1-5");

        final CountDownLatch latch = new CountDownLatch(names.size() *2);
        name1.forEach(new Action1<String>() {
            @Override
            public void call(String s) {
                latch.countDown();
                Debug.print(s);
            }
        });
        name1.forEach(new Action1<String>() {
            @Override
            public void call(String s) {
                latch.countDown();
                Debug.print(s);
            }
        });

        try {
            latch.await();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
