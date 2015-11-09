package org.excelsi.solace;


import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class FileSink implements Sink<String> {
    private final FileWriter _w;


    public FileSink(String filename) throws IOException {
        File h = new File(filename);
        if(!h.exists()) {
            _w = new FileWriter(h);
        }
        else {
            _w = new FileWriter(h, true);
        }
    }

    @Override public void write(String s) {
        try {
            _w.write(s);
            _w.write("\n");
            _w.flush();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override public void close() {
        try {
            _w.close();
        }
        catch(IOException e) {
        }
    }
}
