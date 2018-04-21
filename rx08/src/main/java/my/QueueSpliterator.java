package my;

import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class QueueSpliterator<T> implements Spliterator<T> {
    private final BlockingQueue<T> queue;

    public QueueSpliterator(final BlockingQueue<T> queue) {
        this.queue = queue;
    }

    @Override
    public int characteristics() {
        return Spliterator.CONCURRENT | Spliterator.NONNULL | Spliterator.ORDERED;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public boolean tryAdvance(final Consumer<? super T> action) {
        try {
            final T next = this.queue.take();
            if (next == null) {
                return false;
            }
            action.accept(next);
            return true;
        } catch (final InterruptedException e) {
            throw new RuntimeException("interrupted", e);
        }
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }
}