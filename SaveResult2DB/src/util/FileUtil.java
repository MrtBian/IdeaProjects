package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class FileUtil {
    public static List<String> readFileByLine(String fileName){
        ArrayList<String> result = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String text;
            while ((text = reader.readLine())!=null) {
                result.add(text);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
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
