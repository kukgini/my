package my;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Implement a buffered consumer that feeds consumed items in to a stream
 *
 * @author rolf
 *
 * @param <V>
 *            the generic type of the data being streamed.
 */
public class ConsumerToStream<V> implements Consumer<V> {

    private static final class QSpliterator<T> implements Spliterator<T> {

        private final BlockingQueue<T> queue;

        public QSpliterator(BlockingQueue<T> queue) {
            this.queue = queue;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            try {
                action.accept(queue.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Take interrupted.", e);
            }
            return true;
        }

        @Override
        public Spliterator<T> trySplit() {
            try {
                final int size = queue.size();
                List<T> vals = new ArrayList<>(size + 1);
                vals.add(queue.take());
                queue.drainTo(vals);
                return vals.spliterator();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Thread interrupted during trySplit.", e);
            }
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return Spliterator.CONCURRENT;
        }

    }

    private final Stream<V> outstream;
    private final BlockingQueue<V> blockingQueue;

    private final Spliterator<V> splitter;

    /**
     * Construct an instance of the consumer buffer with the supplied maximum
     * capacity
     *
     * @param bufferSize
     *            the amount of space to set aside for buffered items.
     */
    public ConsumerToStream(int bufferSize) {
        this.blockingQueue = new LinkedBlockingQueue<>(bufferSize);
        this.splitter = new QSpliterator<>(blockingQueue);
        this.outstream = StreamSupport.stream(splitter, false);
    }

    /**
     * Get the stream this buffer outputs to.
     *
     * @return the output stream.
     */
    public Stream<V> stream() {
        return outstream;
    }

    @Override
    public void accept(V t) {
        try {
            blockingQueue.put(t);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted accepting a new value.", e);
        }
    }

    public static void main(String[] args) {
        ConsumerToStream<Integer> cts = new ConsumerToStream<>(10);
        CompletableFuture.runAsync(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                IntStream.range(0, 100).boxed().forEach(cts);
            }
        });
        cts.stream().parallel().forEach((x) -> System.out.format("%s thread = %s %n", x, Thread.currentThread().getId()));
    }
}