package my;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

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
    
    private static Optional<Boolean> quitSignalReceived = Optional.of(false);
    
    private static final String errFilenamePattern = "OUTPUT/ERROUT-%d.txt";
    private static final String oppFilenamePattern = "OUTPUT/OPPOUT-%d.txt";

    private static final String inputFilename = "INPUT/INPUT.txt";

    private static final String errPrefix = "ERR#";
    private static final String oppPrefix = "OPP#";

    private static final OutputWriter err = new OutputWriter(errFilenamePattern, errPrefix);
    private static final OutputWriter opp = new OutputWriter(oppFilenamePattern, oppPrefix);
    private static final OutputWriter[] writers = new OutputWriter[]{err, opp};

    // LinkedBlockingQueue is effective when privicer:consumer = n:1
    // https://sungjk.github.io/2016/11/02/Queue.html
    private static final BlockingQueue<Optional<String>> queue = new LinkedBlockingQueue<>();

    public void run() throws Exception {

        Executors.newSingleThreadExecutor().execute(this::readStdin);
        Executors.newSingleThreadExecutor().execute(this::readFile);

        System.out.format("now i will eat queue: current size is: %s %n", queue.size());
        Stream.generate(() -> queue.poll())
            .map(Optional::get)
            .filter(err::write)
            .filter(opp::write)
            .forEach(this::warnWhenUnknownFormatReceived);
    }

    public void warnWhenUnknownFormatReceived(String x) {
        System.err.format("Unknown format: %s%n", x);
    }
    
    public void exitWhenQuitSignalReceived(String s) {
        if ("Q".equals(s)) {
            System.out.println("[Q]uit signal received. Waiting queue drained...");
            waitingQueueDrained();
            System.out.println("Done. Exiting...");
            for(OutputWriter w : writers) {
                w.close();
            }
            System.out.println("OK");
            System.exit(0);
        }
    }

    public void waitingQueueDrained() {
        while (queue.size() > 0) {
            try {
                System.out.format("remain queue size is : %d%n", queue.size());
                Thread.sleep(10);
            } 
            catch (InterruptedException e) {}
        }

    }
    
    public void readStdin() {
        readStream(System.in);
    }

    public void readFile() {
        try
        {
            RandomAccessFile file = new RandomAccessFile(inputFilename, "r");
            //file.seek(file.length()); // move to end of file.
            InputStream is = Channels.newInputStream(file.getChannel());
            readStream(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void readStream(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
        java.util.stream.Stream.generate(() -> readLine(reader))
            .filter(Optional::isPresent)
            .forEach(queue::offer);
        } 
        catch (RuntimeException e) {}
    }

    private Optional<String> readLine(BufferedReader reader) {
        String result = null;
        try {
            result = reader.readLine();
            exitWhenQuitSignalReceived(result);
        } 
        catch (IOException e){}
        return Optional.ofNullable(result);
    }
    
    static public class OutputWriter {
        private int fileIndex = 0;
        private int count = 0;
        private final int capacity = 20;
        private String filenamePattern;
        private String prefix;
        private PrintWriter writer;

        public OutputWriter(String filenamePattern, String prefix) {
            this.filenamePattern = filenamePattern;
            this.prefix = prefix;
        }

        private void openNewWriter() throws IOException {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
            writer = new PrintWriter(Files.newBufferedWriter(Paths.get(String.format(filenamePattern, fileIndex))));
        }

        public boolean write(String s) {
            try
            {
                if (writer == null) {
                    openNewWriter();
                }
                if (s != null && s.startsWith(prefix))
                {
                    //System.out.format("writing %s, current queue size: %d %n", prefix, queue.size());
                    count++;
                    if (count > capacity) {
                        count = 1;
                        fileIndex++;
                        openNewWriter();
                    }
                    writer.println(s.substring(prefix.length()));
                    return false;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;
        }

        public void close() {
            if (writer != null) {
                writer.close();
            }
        }
    }
}