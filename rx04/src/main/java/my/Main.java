package my;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.concurrent.*;

public class Main {
    
    private static final LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
    private static Supplier<Stream<Integer>> supplier = () -> Stream.generate(queue::poll);
    
    public static void main(String[] args) {
        System.err.println("now i will make a provider.");
        new Thread(Main::provide).start();
        
        System.err.println("now i will make multiple consumers.");
        int numOfConsumerThreads = 2;
        ExecutorService executors = Executors.newFixedThreadPool(numOfConsumerThreads);
    	for (int i = 0; i < numOfConsumerThreads; i++) {
    		executors.execute(consume(i));
    	}
    }
    
    private static void provide() {
        for (int i = 0; i < 10; i++) {
            try {
                queue.offer(i);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                
            }
        }        
    }
    
    private static Runnable consume(final int i) {
        return new Runnable() {
            public void run() {
                supplier.get()
                    .filter(x -> x != null)
                    .map(x -> String.format("consumer %s = %d",i, x))
                    .forEach(System.out::println);
            }
        };
    }
}