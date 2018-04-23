package my;

import java.io.*;
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
        cleanDirThenRun(outputDir, () -> run());
    }

    public static void run() {
        executors.submit(() -> stream
                .filter(errAlerm)
                .filter(errProcessor)
                .filter(oppProcessor)
                .forEach(e -> System.out.format("Unknown Format:%s%n", e)));

        try (RandomAccessFile file = new RandomAccessFile(inputFilename, "r");
             FileChannel fileChannel = file.getChannel();
        ){
            BufferedReader r1 = stdInToBufferedReader.get();
            executors.submit(() -> doUntil(needQuit, () -> streamToQueue(r1), 10));

            //file.seek(file.length()); // move to end of file.
            BufferedReader r2 = fileChannelToBufferedReader.apply(file.getChannel());
            executors.submit(() -> doUntil(needQuit, () -> streamToQueue(r2), 10));

            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }

        waitUntil(needQuit,10);
        waitUntil(queueDrained, 10);

        oppWriter.close();
        errWriter.close();
        stream.close();
        executors.shutdownNow();

        System.out.println("system exit.");
    }

    public static final void cleanDirThenRun(String dir, Runnable run) {
        try {
            String current = new java.io.File(dir).getCanonicalPath();
            System.out.println("Cleaning dir:" + current);
            Files.walk(Paths.get(dir))
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .forEach(File::delete);
            run.run();
        } catch (IOException e) {
            System.out.println("outputDir cleaning failed.");
        }
    };
    private static final String workingDir = "rx08";
    private static final String inputDir = workingDir + "/INPUT";
    private static final String outputDir = workingDir + "/OUTPUT";
    private static final String errFilenamePattern = outputDir + "/ERROUT-%02d.txt";
    private static final String oppFilenamePattern = outputDir + "/OPPOUT-%02d.txt";
    private static final String inputFilename = inputDir + "/INPUT.txt";
    public static Supplier<BufferedReader> stdInToBufferedReader = () -> new BufferedReader(new InputStreamReader(System.in));
    public static Function<InputStream, BufferedReader> inputStreamToBufferedReader = (x) -> new BufferedReader(new InputStreamReader(x));
    public static Function<FileChannel, BufferedReader> fileChannelToBufferedReader = (x) -> new BufferedReader(new InputStreamReader(Channels.newInputStream(x)));

    // LinkedBlockingQueue is effective when privicer:consumer = n:1
    // https://sungjk.github.io/2016/11/02/Queue.html
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private static final Stream<String> stream = StreamSupport.stream(new QueueSpliterator(queue), false);
    private static boolean quit = false;
    private static CountDownLatch latch = new CountDownLatch(1);
    public static Supplier<Boolean> needQuit = () -> quit == true;
    public static Runnable sendQuitSignal = () -> {quit = true; latch.countDown();};
    public static Supplier<Boolean> queueDrained = () -> queue.size() == 0;
    public static Predicate<String> quitSignalReceived = (s) -> {
        if ("Q".equals(s)) {
            sendQuitSignal.run();
            return true;
        } else {
            return false;
        }
    };
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
    public static int errCount = 0;
    public static Predicate<String> errAlerm = (s) -> {
        if (s.startsWith(ERR_PREFIX) == true) {
            errCount++;
        } else if (s.startsWith(OPP_PREFIX) == true) {
            errCount = 0;
            System.out.format("정상 로그가 들어왔습니다. Alert 이 꺼집니다.%n", errCount);
        }
        if (errCount > 3) {
            System.out.format("Alert : error count 가 %s 회를 넘었습니다.%n", errCount);
        }
        return true;
    };
    private static final ExecutorService executors = Executors.newCachedThreadPool();

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

    public static void streamToQueue(BufferedReader reader) {
        try {
            String s = reader.readLine();
            if (quitSignalReceived.test(s) == false) {
                queue.offer(s);
            }
        }
        catch (IOException e) {}
    };
}