package my;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Main {

    private static final LinkedBlockingQueue<Integer> queue1 = new LinkedBlockingQueue<>();
    private static final LinkedBlockingQueue<Integer> queue2 = new LinkedBlockingQueue<>();
    private static Supplier<Stream<Integer>> supplier1 =
        () -> Stream.generate(queue1::poll).filter(x -> x != null);
    private static Supplier<Stream<Integer>> supplier2 =
        () -> Stream.generate(queue2::poll).filter(x -> x != null);

    public static void main(String[] args) {
        System.err.println("now i will make a provider.");
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(Main::provide);

        System.err.println("now i will make multiple consumers.");

        executor.execute(Main::consume1);
        executor.execute(() -> consume2());
        executor.execute(() -> consume1());
        executor.execute(() -> consume2());

        executor.shutdown();
    }

    private static void provide() {
        for (int i = 0; i < 50; i++) {
            try {
                System.out.format("provide %d%n", i);
                queue1.offer(i);
                Thread.sleep(1);
            } catch (InterruptedException e) {

            }
        }
    }

    private static void consume1() {
        try {Thread.sleep(5000);} catch (InterruptedException e) {}
        supplier1.get()
            .filter((x) -> {
                if (x % 2 == 0) {
                    return true;
                } else {
                    queue2.offer(x);
                    return false;
                }})
            .map((x) -> String.format("%d is even. thread[%d]",x, Thread.currentThread().getId()))
            .forEach(System.out::println);
    }

    private static void consume2() {
        try {Thread.sleep(3000);} catch (InterruptedException e) {}
        supplier2.get()
            .map((x) -> String.format("%d is odd. thread[%d]",x, Thread.currentThread().getId()))
            .forEach(System.out::println);
    }
}