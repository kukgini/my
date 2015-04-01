import java.sql.Time;

/**
 * Created by soomin on 2015. 3. 26..
 */
public class Debug {
    public static void print(String message) {
        System.out.println("[" + new Time(System.currentTimeMillis()) + "][" + Thread.currentThread().getName() + ":" + Thread.currentThread().getId() +"] " + message);
    }
}
