package my;

import java.util.concurrent.Executors;

public class QuitSignal
{
    public static void close() {
        System.out.format("[Q]uit signal received.");
        closeInBackground();
    }

    public static void closeInBackground() {
        Executors.newSingleThreadExecutor().execute(() -> System.out.format("Begin shutting down...%n"));
    }
}
