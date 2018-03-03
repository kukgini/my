package my;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.stream.Stream;

public class Task
{
    private Function<String, Boolean> condition;
    private Function<String, String> mapper;
    private Function<String, String> task;
    private BlockingQueue<String> input = new LinkedBlockingQueue<>();

    public Task(Function<String, Boolean> condition, Function<String, String> mapper, Function<String, String> task) {
        this.condition = condition;
        this.mapper = mapper;
        this.task = task;
        runInBackground(input, task);
    }
    public boolean isMatch(String s) {
        return condition.apply(s);
    }

    public void offer(String s) {
        input.offer(mapper.apply(s));
    }

    private void runInBackground(BlockingQueue<String> q, Function<String,String> f) {
        Executors.newSingleThreadExecutor().execute(() ->
            Stream.generate(q::poll).filter(x -> x != null).forEach(f::apply)
        );
    }
}
