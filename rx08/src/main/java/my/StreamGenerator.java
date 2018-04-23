package my;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamGenerator {
    private final ExecutorService executors = Executors.newCachedThreadPool();

    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    private final Stream<String>  stream = StreamSupport.stream(new QueueSpliterator<>(queue), false);
    private List<BufferedReader> readers = new ArrayList<>();

    public StreamGenerator addInput(InputStream input) {
        readers.add(inputStreamToBufferedReader.apply(input));
        return this;
    }
    public StreamGenerator addInput(FileChannel input) {
        readers.add(fileChannelToBufferedReader.apply(input));
        return this;
    }
    public Stream<String> build(Supplier<Boolean> exitCondition, Function<String, String> streamProcessor, Runnable drainProcess) {
        for (BufferedReader reader: readers) {
            executors.submit(() -> doUntil(exitCondition, () -> streamToQueue(reader, streamProcessor), 10, drainProcess));
        }
        return stream;
    }

    private void streamToQueue (BufferedReader reader, Function<String, String> streamProcessor) {
        try {
                String s = reader.readLine();
                s = streamProcessor.apply(s);
                if (s != null) {
                    queue.offer(s);
                }
            }
            catch (IOException e) {}
    }

    private void doUntil(Supplier<Boolean> condition, Runnable r, int interval, Runnable drainProcess) {
        while(condition.get() == false) {
            r.run();
            sleep(interval);
        }
        drainProcess.run();
        stream.close();
        executors.shutdownNow();
    }

    private static Function<InputStream, BufferedReader> inputStreamToBufferedReader = (x) -> new BufferedReader(new InputStreamReader(x));
    private static Function<FileChannel, BufferedReader> fileChannelToBufferedReader = (x) -> new BufferedReader(new InputStreamReader(Channels.newInputStream(x)));
    private static void sleep(int millisec) {
        try { Thread.sleep(millisec); } catch (InterruptedException e) {}
    }

}
