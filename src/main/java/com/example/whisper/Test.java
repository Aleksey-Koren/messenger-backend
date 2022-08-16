package com.example.whisper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("C:/test/test1");
        System.out.println(path.getFileName());
        System.out.println(path.getRoot());
        Path parent = path.getParent();
        System.out.println(parent);
    }
}