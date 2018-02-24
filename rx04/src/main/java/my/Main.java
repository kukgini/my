package my;

import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;

public class Main {
    
    private static final InfiniteStream<Integer> stream = new InfiniteStream<>();
    
    public static void main(String[] args) {
        
        System.err.println("now i will make a provider.");
        new Thread(Main::provide).start();
        
        System.err.println("now i will make a consumer.");
        stream
            .filter(x -> x != null)
            .forEach(System.out::println);
    }
    
    private static void provide() {
        for (int i = 0; i < 10; i++) {
            try {
                stream.accept(i);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                
            }
        }        
    }
}