package common;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

//简单工具类，帮助我们更方便的读写文件
public class FileUtil {
    //读文件：一下文件的内容都读到String中
    public static String readFile(String filePath){
        //当前涉及到的编译错误 标准输出等都是文本文件
        //try() 括号中包含的对象 表示是一个可以被自动关闭的对象
        try(FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader)){
            StringBuilder stringBuilder = new StringBuilder();
            //按行读取文件内容
            String line = "";
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 写文件：一下吧String的内容全部写入指定文件中
     * @param filePath 要写入得指定文件
     * @param content  写入的文件内容
     */
    public static void writeFile(String filePath, String content){
        try (FileWriter fileWriter = new FileWriter(filePath)){
            fileWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
