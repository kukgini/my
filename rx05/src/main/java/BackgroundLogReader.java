import java.io.*;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
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
    private static clearOutput() {
        Files.walk(Paths.get("OUTPUT"))
            .map(Path::toFile)
            .filter(File::isFile)
            .forEach(File::delete);        
    }
    
    private static final String errFilenamePattern = "OUTPUT/ERROUT-%d.txt";
    private static final String oppFilenamePattern = "OUTPUT/OPPOUT-%d.txt";

    private static final String inputFilename = "INPUT/INPUT.txt";

    private static final String errPrefix = "ERR#";
    private static final String oppPrefix = "OPP#";

    // LinkedBlockingQueue is effective when privicer:consumer = n:1
    // https://sungjk.github.io/2016/11/02/Queue.html
    private static final BlockingQueue<Optional<String>> queue = new LinkedBlockingQueue<>();

    public void run() throws Exception {
        OutputWriter err = new OutputWriter(errFilenamePattern, errPrefix).build();
        OutputWriter opp = new OutputWriter(oppFilenamePattern, oppPrefix).build();
        OutputWriter[] writers = new OutputWriter[]{err, opp};

        Executors.newSingleThreadExecutor().execute(this::readStdin);
        Executors.newSingleThreadExecutor().execute(this::readFile);

        Stream.generate(() -> queue.poll())
            .filter(x -> x.isPresent())
            .map(x -> x.get())
            .filter(x -> exitWhenQuitSignalReceived(x, writers))
            .filter(err::write)
            .filter(opp::write)
            .forEach(x -> System.err.format("Unknown format: %s%n", x));
    }

    public boolean exitWhenQuitSignalReceived(String s, OutputWriter[] ws) {
        if ("Q".equals(s)) {
            System.out.print("[Q]uit signal received. Exiting...");
            for(OutputWriter w : ws) {
                w.close();
            }
            System.out.println("OK");
            System.exit(0);
        }
        return true;
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
        java.util.stream.Stream.generate(() -> readLine(reader))
            .forEach(queue::offer);
    }

    private Optional<String> readLine(BufferedReader reader) {
        String result = null;
        try {
            result = reader.readLine();
        } 
        catch (IOException e){}
        return Optional.ofNullable(result);
    }
    
    public class OutputWriter {
        private int fileIndex = 0;
        private int count = 0;
        private String filenamePattern;
        private String prefix;
        private PrintWriter writer;

        public OutputWriter(String filenamePattern, String prefix) {
            this.filenamePattern = filenamePattern;
            this.prefix = prefix;
        }

        public OutputWriter build() throws IOException
        {
            openNewWriter();
            return this;
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
                if (s != null && s.startsWith(prefix))
                {
                    count++;
                    if (count > 20) {
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