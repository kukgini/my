import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.concurrent.*;

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
        //int numOfConsumerThreads = 4;
    	//for (int i = 0; i < numOfConsumerThreads; i++) {
    	executor.execute(Main::consume1);
    	executor.execute(Main::consume2);
    	//}
    	executor.shutdown();
    }
    
    private static void provide() {
        for (int i = 0; i < 10000; i++) {
            try {
                System.out.format("provide %d%n", i);
                queue1.offer(i);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                
            }
        }        
    }
    
    private static void consume1() {
        supplier1.get()
            .filter((x) -> {
                if (x % 2 == 0) {
                    return true;
                } else {
                    queue2.offer(x);
                    return false;
                }}) 
            .map((x) -> String.format("\tthis is even : %d",x))
            .forEach(System.out::println);
    }
    
    private static void consume2() {
        supplier2.get()
            .map((x) -> String.format("\tthis is odd: %d",x))
            .forEach(System.out::println);
    }
}