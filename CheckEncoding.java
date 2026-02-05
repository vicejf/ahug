import java.io.*;
import java.nio.charset.*;

public class CheckEncoding {
    public static void main(String[] args) throws Exception {
        File file = new File("d:/Documents/workspace/hdc-mes/backend/output/src/client/nc/ui/ahu/aujz/AUJZController.java");

        // 读取文件的字节
        FileInputStream fis = new FileInputStream(file);
        byte[] bytes = new byte[500];
        int len = fis.read(bytes);
        fis.close();

        System.out.println("文件前200个字节:");
        for(int i = 0; i < 200 && i < len; i++) {
            System.out.printf("%02X ", bytes[i]);
        }
        System.out.println();

        // 尝试用GBK读取
        System.out.println("\n尝试用GBK读取文件内容:");
        BufferedReader gbkReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GBK"));
        String line;
        int lineCount = 0;
        while((line = gbkReader.readLine()) != null && lineCount < 20) {
            System.out.println(line);
            lineCount++;
        }
        gbkReader.close();

        // 尝试用UTF-8读取
        System.out.println("\n尝试用UTF-8读取文件内容:");
        BufferedReader utf8Reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
        lineCount = 0;
        while((line = utf8Reader.readLine()) != null && lineCount < 20) {
            System.out.println(line);
            lineCount++;
        }
        utf8Reader.close();
    }
}
