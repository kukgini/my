package my;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BackgroundLogReader {

    public static void main(String[] args) {
        try
        {
            clearOutput();
            new BackgroundLogReader().run();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    // clear previously created output for new execution.
    private static void clearOutput() throws IOException {
        Files.walk(Paths.get("OUTPUT"))
            .map(Path::toFile)
            .filter(File::isFile)
            .forEach(File::delete);        
    }

    private static final String errFilenamePattern = "OUTPUT/ERROUT-%02d.txt";
    private static final String oppFilenamePattern = "OUTPUT/OPPOUT-%02d.txt";
    private static final String inputFilename = "INPUT/INPUT.txt";

    // LinkedBlockingQueue is effective when privicer:consumer = n:1
    // https://sungjk.github.io/2016/11/02/Queue.html
    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public void run() throws Exception {

        InputAggregator input = new InputAggregator();
        input.inputFromSystemIn();
        input.inputFromFile(inputFilename);

        delay(3000);

        OutputWriter errWriter = new OutputWriter(errFilenamePattern);
        OutputWriter oppWriter = new OutputWriter(oppFilenamePattern);

        QueueSpliter spliter = new QueueSpliter(input.output());
        spliter.addTask((x) -> x.startsWith("ERR#"), (x) -> x.substring("ERR#".length()), errWriter::write);
        spliter.addTask((x) -> x.startsWith("OPP#"), (x) -> x.substring("OPP#".length()), oppWriter::write);
        spliter.run();
    }

    private static boolean delay(int i) {
        try {Thread.sleep(i);} catch (InterruptedException e) {}
        return true;
    }

}