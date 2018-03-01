package my;

import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Main {
    private static final LinkedBlockingQueue<Integer> queue1 = new LinkedBlockingQueue<>();
    private static final LinkedBlockingQueue<Integer> queue2 = new LinkedBlockingQueue<>();
    private static final LinkedBlockingQueue<Integer> queue3 = new LinkedBlockingQueue<>();
    private static final LinkedBlockingQueue<Integer> quitsig = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();

        System.err.println("now i will make a provider.");
        executor.execute(() -> provide(queue1));

        System.err.println("now i will make consumers flow.");
        executor.execute(() -> split(queue1, queue2, queue3));
        executor.execute(() -> consume1(queue2));
        executor.execute(() -> consume2(queue3));
        executor.execute(() -> consumeQuit(quitsig));
    }

    private static void provide(LinkedBlockingQueue queue) {
        for (int i = 0; i < 10; i++)
        {
            System.out.format("provide %02d%n", i);
            queue.offer(i);
            delay(1000);
        }
        quitsig.offer(-1);
    }

    private static void consumeQuit(LinkedBlockingQueue<Integer> from) {
        Stream.generate(from::poll)
            .filter(x -> x != null)
            .forEach(x -> quit(x));
    }

    private static void split(LinkedBlockingQueue<Integer> from, LinkedBlockingQueue<Integer> n1, LinkedBlockingQueue<Integer> n2) {
        Stream.generate(from::poll)
            .parallel()
            .filter(x -> x != null)
            .forEach(x -> {
                if (x % 2 == 0) {
                    n1.offer(x);
                } else {
                    n2.offer(x);
                }
            });
    }

    private static void consume1(LinkedBlockingQueue<Integer> from) {
        Stream.generate(from::poll)
//            .parallel()
            .filter(x -> x != null)
            .filter(x -> delay(5000))
            .map((x) -> String.format("%02d is eve. thread[%02d]",x, Thread.currentThread().getId()))
            .forEach(System.out::println);
    }

    private static void consume2(LinkedBlockingQueue<Integer> from) {
        Stream.generate(from::poll)
 //           .parallel()
            .filter(x -> x != null)
            .filter(x -> delay(5000))
            .map((x) -> String.format("%02d is odd. thread[%02d]",x, Thread.currentThread().getId()))
            .forEach(System.out::println);
    }


    private static void quit(Integer i) {
        System.out.format("Quit signal received... waiting for all queue is processed. thread[%02d]%n", Thread.currentThread().getId());
        while (queue1.size() > 0 || queue2.size() > 0 || queue3.size() > 0) {
            delay(1000);
        }
        System.out.println("Quit now.");
        System.exit(0);
    }

    private static boolean delay(int i) {
        try {Thread.sleep(i);} catch (InterruptedException e) {}
        return true;
    }
}