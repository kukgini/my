package my;

import java.io.*;
import java.nio.channels.Channels;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class InputAggregator
{
    private static BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public BlockingQueue<String> output() {
        return queue;
    }

    private void inputStreamToQueue(InputStream stream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            java.util.stream.Stream.generate(() -> readLine(reader))
                .filter(x -> x != null)
                .forEach(queue::offer);
        }
        catch (IOException e) {e.printStackTrace();}
        catch (RuntimeException e) {e.printStackTrace();}
    }

    public void inputFromSystemIn() {
        Executors.newSingleThreadExecutor().execute(() -> inputStreamToQueue(System.in));
    }

    public void inputFromFile(String inputFilename) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try (RandomAccessFile file = new RandomAccessFile(inputFilename, "r"))
            {
                //file.seek(file.length()); // move to end of file.
                InputStream is = Channels.newInputStream(file.getChannel());
                inputStreamToQueue(is);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    private String readLine(BufferedReader reader) {
        String result = null;
        try {
            result = reader.readLine();
        }
        catch (IOException e){e.printStackTrace();}
        return result;
    }
}
