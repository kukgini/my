package my;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.stream.Stream;

public class QueueSpliter
{
    private BlockingQueue<String> input;
    private List<Task> tasks = new ArrayList<>();

    public QueueSpliter(BlockingQueue<String> input) {
        this.input = input;
    }

    public void run() {
        Stream.generate(input::poll).filter(s -> s != null)
            .forEach(s -> {
                if (s.equals("Q")) {
                    QuitSignal.close();
                }
                for(int i = 0; i < tasks.size(); i++) {
                    Task task = tasks.get(i);
                    if (task.isMatch(s))
                    {
                        task.offer(s);
                    }
                }
            });
    }

    public void addTask(Task task) {
        tasks.add(task);
    }
}
