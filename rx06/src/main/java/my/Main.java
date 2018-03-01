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

    public static void main(String[] args) {

        System.err.println("now i will make a provider.");
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(() -> provide(queue1));

        System.err.println("now i will make consumers flow.");
        executor.execute(() -> consumeQuit(queue1, queue2));
        executor.execute(() -> consume1(queue2, queue3));
        executor.execute(() -> consume2(queue3));
    }

    private static void provide(LinkedBlockingQueue queue) {
        for (int i = 0; i < 20; i++)
        {
            System.out.format("provide %d%n", i);
            queue.offer(i);
            delay(1);
        }
        queue.offer(-1);
    }

    private static void consumeQuit(LinkedBlockingQueue<Integer> from, LinkedBlockingQueue<Integer> next) {
        Stream.generate(from::poll)
            .filter(x -> x != null)
            .filter(x -> {
                if (x == -1) {
                    return true;
                } else {
                    next.offer(x);
                    System.out.format("offering to consume1. queue2 size is now: %d%n", next.size());
                    return false;
                }
            })
            .forEach(x -> quit(x));
    }

    private static void consume1(LinkedBlockingQueue<Integer> from, LinkedBlockingQueue<Integer> next) {
        Stream.generate(from::poll)
            .parallel()
            .filter(x -> x != null)
            .filter(x -> delay(2000))
            .filter((x) -> {
                if (x % 2 == 0) {
                    return true;
                } else {
                    next.offer(x);
                    return false;
                }})
            .map((x) -> String.format("%d is even. thread[%d]",x, Thread.currentThread().getId()))
            .forEach(System.out::println);
    }

    private static void consume2(LinkedBlockingQueue<Integer> from) {
        Stream.generate(from::poll)
            .parallel()
            .filter(x -> x != null)
            .filter(x -> delay(3000))
            .map((x) -> String.format("%d is odd. thread[%d]",x, Thread.currentThread().getId()))
            .forEach(System.out::println);
    }


    private static void quit(Integer i) {
        System.out.println("Quit signal received... waiting for all queue is processed");
        System.out.format("%d\t%d\t%d%n",queue1.size(),queue2.size(),queue3.size());
        while (queue1.size() > 0 || queue2.size() > 0 || queue3.size() > 0) {
            System.out.format("%d\t%d\t%d%n",queue1.size(),queue2.size(),queue3.size());
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