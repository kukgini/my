package my;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OutputWriter {
    private int fileIndex = 0;
    private int count = 0;
    private final int capacity = 20;
    private String filenamePattern;
    private PrintWriter writer;

    public OutputWriter(String filenamePattern) {
        this.filenamePattern = filenamePattern;
    }

    private void openNewWriter() throws IOException
    {
        if (writer != null) {
            writer.flush();
            writer.close();
        }
        writer = new PrintWriter(Files.newBufferedWriter(Paths.get(String.format(filenamePattern, fileIndex))));
    }

    public String write(String s) {
        try
        {
            if (writer == null) {
                openNewWriter();
            }
            count++;
            if (count > capacity) {
                count = 1;
                fileIndex++;
                openNewWriter();
            }
            writer.println(s);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return s;
    }

    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
}
