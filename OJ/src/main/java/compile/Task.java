package compile;

import common.FileUtil;

import java.io.File;
import java.io.IOException;

//描述一次编译运行的过程
public class Task {
    //约定编译运行过程中依赖的临时文件名字
    //临时文件放到tmp目录
    //为了方便后续调试  将临时文件记录执行过程中的中间结果
    private static final String WORK_DIR = "./tmp/";
    //代码的类名
    private static final String CLASS = "Main";
    //代码对应文件名 与类名一致
    private static final String CODE = WORK_DIR + "Main.java";
    //标准输入对应的文件
    private static final String STDIN = WORK_DIR + "stdin.txt";
    //标准输出对应的文件 编译执行代码的结果
    private static final String STDOUT = WORK_DIR + "stdout.txt";
    //标准错误对应的文件 编译执行代码的结果
    private static final String STDERR = WORK_DIR + "stderr.txt";
    //编译错误对应的文件  编译出错时的具体原因
    private static final String COMPILE_ERROR = WORK_DIR + "compile_error.txt";

    public Answer compileAndRun(Question question) throws IOException, InterruptedException {
        Answer answer = new Answer();
        //存放临时文件的目录
        File workDir = new File(WORK_DIR);
        if (!workDir.exists()){
            workDir.mkdir();
        }
        //1.根据Question对象构造需要的临时文件
        FileUtil.writeFile(CODE,question.getCode());
        FileUtil.writeFile(STDIN,question.getStdin());
        //2.构造编译命令，并执行
        //编译命令形如：javac -encoding utf8 ./tmp/Main.java -d ./tmp/
        //直接拼接命令容易出错  建议   %s 占位符
        String cmd = String.format("javac -encoding utf8 %s -d %s",CODE,WORK_DIR);
        System.out.println("编译命令" + cmd);
        CommandUtil.run(cmd,null,COMPILE_ERROR);
        //编译完需要判断是否编译出错  若出错则不会继续运行
        String compileError = FileUtil.readFile(COMPILE_ERROR);
        if (!"".equals(compileError)){
            //COMPILE_ERROR 不为空 则编译出错
            System.out.println("编译出错");
            answer.setError(1);
            answer.setReason(compileError);
            return answer;
        }
        //3.构造运行命令，并执行
        //为找到正确的类对应的.class 文件  需要指定加载路径  -classpath
        //运行命令 形如：java - classpath ./tmp/ Main
        cmd = String.format("java -classpath %s %s",WORK_DIR,CLASS);
        System.out.println("运行命令" + cmd);
        CommandUtil.run(cmd,STDOUT,STDERR);
        //判定运行是否异常
        String stdError = FileUtil.readFile(STDERR);
        if (!"".equals(stdError)){
            System.out.println("运行出错");
            answer.setError(2);
            answer.setReason(stdError);
            answer.setStderr(stdError);
            return answer;
        }
        //4.将最终结果包装到Answer中
        answer.setError(0);
        answer.setStdout(FileUtil.readFile(STDOUT));
        return answer;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //验证
        Question question = new Question();
        question.setCode(
                "public class Main{\n" +
                "    public static void main(String[] args) {\n" +
                "        System.out.println(\"hello\");\n" +
                "    }\n" +
                "}\n"
        );
        question.setStdin("");
        Task task = new Task();
        Answer answer = task.compileAndRun(question);
        System.out.println(answer);
    }
}


