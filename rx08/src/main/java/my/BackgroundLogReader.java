package my;

import java.io.*;
import java.nio.Buffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BackgroundLogReader {

    public static void main(String[] args) {
        try
        {
            clearOutput();
            run();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private static final String workingDir = "rx08";
    private static final String inputDir = workingDir + "/INPUT";
    private static final String outputDir = workingDir + "/OUTPUT";
    private static final String errFilenamePattern = outputDir + "/ERROUT-%02d.txt";
    private static final String oppFilenamePattern = outputDir + "/OPPOUT-%02d.txt";
    private static final String inputFilename = inputDir + "/INPUT.txt";

    // LinkedBlockingQueue is effective when privicer:consumer = n:1
    // https://sungjk.github.io/2016/11/02/Queue.html
    private  static final int TIME_OUT = 60000; // 60 sec
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private static final Stream<String> stream = StreamSupport.stream(new QueueSpliterator(queue, TIME_OUT), false);
    private static boolean quit = false;
    private static CountDownLatch latch = new CountDownLatch(1);
    public static Supplier<Boolean> quitSignalReceived = () -> quit == true;
    public static Runnable sendQuitSignal = () -> {quit = true; latch.countDown();};
    public static Supplier<Boolean> queueDrained = () -> queue.size() == 0;
    public static Function<InputStream, BufferedReader> inputStreamToBufferedReader = (x) -> new BufferedReader(new InputStreamReader(x));
    public static Function<FileChannel, BufferedReader> fileChannelToBufferedReader = (x) -> new BufferedReader(new InputStreamReader(Channels.newInputStream(x)));
    public static final String ERR_PREFIX = "ERR#";
    public static final String OPP_PREFIX = "OPP#";
    public static OutputWriter errWriter = new OutputWriter(errFilenamePattern);
    public static OutputWriter oppWriter = new OutputWriter(oppFilenamePattern);
    public static Predicate<String> errProcessor = (s) -> {
        if (s.startsWith(ERR_PREFIX) == false) {
            return true;
        } else {
            errWriter.write(s.substring(ERR_PREFIX.length()));
            return false;
        }
    };
    public static Predicate<String> oppProcessor = (s) -> {
        if (s.startsWith(OPP_PREFIX) == false) {
            return true;
        } else {
            oppWriter.write(s.substring(OPP_PREFIX.length()));
            return false;
        }
    };
    private static final ExecutorService executors = Executors.newCachedThreadPool();

    public static void run() {
        executors.submit(() -> stream
                .filter(errProcessor)
                .filter(oppProcessor)
                .forEach(e -> System.out.format("Unknown Format:%s%n", e)));

        BufferedReader r1 = inputStreamToBufferedReader.apply(System.in);
        executors.submit(() -> doUntil(quitSignalReceived, () -> streamToQueue(r1, queue), 10));

        try (RandomAccessFile file = new RandomAccessFile(inputFilename, "r");
             BufferedReader r2 = fileChannelToBufferedReader.apply(file.getChannel());
        ){
            //file.seek(file.length()); // move to end of file.
            executors.submit(() -> doUntil(quitSignalReceived, () ->streamToQueue(r2, queue), 10));
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        waitUntil(quitSignalReceived,10);
        waitUntil(queueDrained, 10);

        stream.close();
        executors.shutdownNow();

        System.out.println("system exit.");



    }

    // clear previously created output for new execution.
    private static void clearOutput() throws IOException {
        String current = new java.io.File( outputDir ).getCanonicalPath();
        System.out.println("Current dir:"+current);
        Files.walk(Paths.get(outputDir))
            .map(Path::toFile)
            .filter(File::isFile)
            .forEach(File::delete);
    }

    private static boolean delay(int i) {
        try {Thread.sleep(i);} catch (InterruptedException e) {}
        return true;
    }

    public static void doWhile(Supplier<Boolean> condition, Runnable r, int interval) {
        while(condition.get()) {
            r.run();
            sleep(interval);
        }
    }

    public static void doUntil(Supplier<Boolean> condition, Runnable r, int interval) {
        while(condition.get() == false) {
            r.run();
            sleep(interval);
        }
    }

    public static void doAfter(int delay, Runnable r) {
        sleep(delay);
        r.run();
    }

    public static void waitUntil(Supplier<Boolean> condition, int interval) {
        while(condition.get() == false) {
            sleep(interval);
        }
    }

    public static void sleep(int millisec) {
        try { Thread.sleep(millisec); } catch (InterruptedException e) {}
    }

    public static void streamToQueue(BufferedReader reader, BlockingQueue<String> queue) {
        if (quitSignalReceived.get() == false) {
            try {
                String s = reader.readLine();
                if ("Q".equals(s)) {
                    sendQuitSignal.run();
                } else if (s != null){
                    queue.offer(s);
                }
            }
            catch (IOException e) {}
        }
    }

    private static String readLine(BufferedReader reader) {
        String result = null;
        try {
            result = reader.readLine();
        }
        catch (IOException e){e.printStackTrace();}
        return result;
    }
}