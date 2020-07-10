package compile;

//编译执行代码的结果
public class Answer {
    //通过error表示当前的错误类型
    //约定为 0：没有错误 1：编译出错 2：运行出错
    private int error;
    //表示具体出错原因
    private String reason;
    //执行时标准输出对应的内容
    private String stdout;
    //执行时标准错误对应的内容
    private String stderr;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "error=" + error +
                ", reason='" + reason + '\'' +
                ", stdout='" + stdout + '\'' +
                ", stderr='" + stderr + '\'' +
                '}';
    }
}
