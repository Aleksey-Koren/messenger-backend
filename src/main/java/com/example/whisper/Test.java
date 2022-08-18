package com.example.whisper;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {

    public static void main(String[] args) throws IOException {
        Path path = Paths.get("C:/test/test1");
        System.out.println(path);
        System.out.println(path.getFileName());
        System.out.println(path.toString().substring(path.toString().lastIndexOf(FileSystems.getDefault().getSeparator())));
        System.out.println(path.endsWith(path.toString().substring(path.toString().lastIndexOf(FileSystems.getDefault().getSeparator()) + 1)));
//        Path parent = path.getParent();
//        System.out.println(parent);
    }

}