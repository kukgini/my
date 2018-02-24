package my;

import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;

public class Main {
    
    private static final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
    
    public static void main(String[] args) {
        
        System.err.println("now i will make a provider.");
        new Thread(Main::enqueueSomething).start();
        
        System.err.println("now i will make a consumer.");
        Stream.generate(() -> queue.poll())
            .filter(x -> x != null)
            .forEach(System.out::println);
    }
    
    private static void enqueueSomething() {
        for (int i = 0; i < 10; i++) {
            try {
                queue.put(i);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                
            }
        }        
    }
}