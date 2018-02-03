package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public final class FileUtil {
    public static void writeFile(String fileName, boolean isAddToTail, List<String> contents) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, isAddToTail));
            for(String string : contents){
                writer.write(string);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String fileName, boolean isAddToTail, HashSet<String> contents) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, isAddToTail));
            for(String string : contents){
                writer.write(string);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String fileName, boolean isAddToTail, String content) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, isAddToTail));
            writer.write(content);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearFile(String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));
            writer.write("");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
