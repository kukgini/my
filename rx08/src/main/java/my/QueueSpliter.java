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
    private static final String errPrefix = "ERR#";
    private static final String oppPrefix = "OPP#";

    private BlockingQueue<String> input;

    private List<Function<String, Boolean>> conditions = new ArrayList<>();
    private List<Function<String, String>> manifulators = new ArrayList<> ();
    private List<Function<String, String>> tasks = new ArrayList<>();
    private List<BlockingQueue<String>> outputs = new ArrayList<>();

    public QueueSpliter(BlockingQueue<String> input) {
        this.input = input;
    }

    public void run() {
        Stream.generate(input::poll).filter(s -> s != null)
            .forEach(s -> {
                if (s.equals("Q")) {
                    QuitSignal.close();
                }
                for(int i = 0; i < conditions.size(); i++) {
                    Function<String, Boolean> condition = conditions.get(i);
                    if (condition.apply(s)) {
                        if (s.startsWith("OPP#")) {
                            System.out.println();
                        }
                        Function<String, String> manifulator = manifulators.get(i);
                        outputs.get(i).offer(manifulator.apply(s));
                    }

                }
            });
    }

    public void addTask(Function<String, Boolean> condition, Function<String, String> manifulator, Function<String, String> task) {
        conditions.add(condition);
        manifulators.add(manifulator);
        tasks.add(task);
        BlockingQueue<String> output = new LinkedBlockingQueue<String>();
        runFunctionInBackground(output, task);
        outputs.add(output);
    }

    private void runFunctionInBackground(BlockingQueue<String> q, Function<String,String> f) {
        Executors.newSingleThreadExecutor().execute(() -> Stream.generate(q::poll).filter(x -> x != null).forEach(f::apply));
    }
}
