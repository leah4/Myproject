package compile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//借助这个类 让Java代码能够执行具体指令 如javac Test.java
public class CommandUtil {
    /**
     *
     ** @param cmd 要执行的命令
     * @param stdoutFile 标准输出结果重定向到那个文件,如果为null 表示不需要重定向
     * @param stderrFile  标准错误结果重定向到哪个文件
     */
    public static int run(String cmd, String stdoutFile, String stderrFile) throws IOException, InterruptedException {
        //1.获取Runtime对象  Runtime对象hi一个单例的 不能通过new来获取
        Runtime runtime = Runtime.getRuntime();
        //2.通过runtime对象中的exec方法执行命令
        //相当于在命令行中输入命令
        Process process = runtime.exec(cmd);
        //3.针对标准输出重定向
        if(stdoutFile != null){
            //进程标准输出中的结果可以通过这个InputStream 获取到
            InputStream stdoutFrom = process.getInputStream();
            OutputStream stdoutTo = new FileOutputStream(stdoutFile);
            int ch = -1;
            while((ch = stdoutFrom.read()) != -1){
                stdoutTo.write(ch);
            }
            stdoutFrom.close();
            stdoutTo.close();
        }
        //4.针对标准错误重定向
        if (stderrFile != null){
            InputStream stderrFrom = process.getErrorStream();
            OutputStream stderrTo = new FileOutputStream(stderrFile);
            int ch = -1;
            while((ch = stderrFrom.read()) != -1){
                stderrTo.write(ch);
            }
            stderrFrom.close();
            stderrTo.close();
        }
        //5.为了确保子进程先运行完，加入进程等待  父进程会在这里阻塞 直到子进程执行结束 再往下继续
        int exitCode = process.waitFor();
        //进程的退出码
        return exitCode;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        run("javac", "d:/stdout.txt", "d:/stderr.txt");
    }
}
