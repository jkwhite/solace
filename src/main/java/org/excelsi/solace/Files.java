package org.excelsi.solace;


import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Collections;
import java.util.stream.Collectors;


public class Files {
    private Files() {}


    public static Iterable<String> textLines(String filename) {
        try {
            return java.nio.file.Files.lines(Paths.get(filename)).collect(Collectors.toList());
        }
        catch(IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
