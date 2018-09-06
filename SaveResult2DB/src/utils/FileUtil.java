package utils;

import java.io.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具类
 *
 * @version        1.0, 18/07/19
 * @author         Wing
 */
public final class FileUtil {

    /**
     * 清空文件
     *
     * @param fileName 文件路径
     */
    public static void clearFile(String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false));

            writer.write("");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按行读取文件
     *
     * @param fileName 文件路径
     * @return 行字符串列表
     */
    public static List<String> readFileByLine(String fileName) {
        ArrayList<String> result = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String         text;

            while ((text = reader.readLine()) != null) {
                result.add(text);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 将字符串列表写入文件
     *
     * @param fileName    文件路径
     * @param isAddToTail 是否续写文件
     * @param contents    写入内容列表
     */
    public static void writeFile(String fileName, boolean isAddToTail, List<String> contents) {
        try {
            File file = new File(fileName);

            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, isAddToTail));

            for (String string : contents) {
                writer.write(string);
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将字符串列表写入文件
     *
     * @param fileName    文件路径
     * @param isAddToTail 是否续写文件
     * @param content     写入内容
     */
    public static void writeFile(String fileName, boolean isAddToTail, String content) {
        try {
            File file = new File(fileName);

            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, isAddToTail));

            writer.write(content);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
