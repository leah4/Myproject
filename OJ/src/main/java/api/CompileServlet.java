package api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import compile.Answer;
import compile.Question;
import compile.Task;
import problem.Problem;
import problem.ProblemDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class CompileServlet extends HttpServlet {
    private Gson gson = new GsonBuilder().create();

    // 创建两个辅助的类, 用来完成请求解析和响应构建
    // 这个类用于辅助解析 body 中的数据请求
    static class CompileRequest {
        private int id;
        //用户输入代码
        private String code;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    // 这个类用于辅助构造最终响应的 body 数据
    static class CompileResponse {
        //代码编译运行是否成功
        private int ok;
        private String reason;
        private String stdout;

        public int getOk() {
            return ok;
        }

        public void setOk(int ok) {
            this.ok = ok;
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
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 读取 请求 的 body 的所有数据
        String body = readBody(req);
        // 2. 按照 API 约定的格式来解析 JSON 数据, 得到 CompileRequest 对象
        CompileRequest compileRequest = gson.fromJson(body, CompileRequest.class);
        // 3. 按照 id 从数据库中读取出对应的测试用例代码.
        ProblemDAO problemDAO = new ProblemDAO();
        Problem problem = problemDAO.selectOne(compileRequest.getId());
        String testCode = problem.getTestCode(); // 得到该题目的测试代码
        String requestCode = compileRequest.getCode(); // 得到该题目的用户输入的代码
        // 4. 把用户输入的代码和测试用例代码进行组装, 组装成一个完整的可以编译运行的代码.
        String finalCode = mergeCode(requestCode, testCode);
        // 5. 创建 Task 对象对刚才组装好的代码进行编译运行.
        Question question = new Question();
        question.setCode(finalCode);
        question.setStdin("");
        Task task = new Task();
        Answer answer = null;
        try {
            answer = task.compileAndRun(question);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 6. 把运行结果构造成响应数据, 并写回给客户端.
        CompileResponse compileResponse = new CompileResponse();
        compileResponse.setOk(answer.getError());
        compileResponse.setReason(answer.getReason());
        compileResponse.setStdout(answer.getStdout());
        String jsonString = gson.toJson(compileResponse);
        resp.setContentType("application/json; charset=utf-8");
        resp.getWriter().write(jsonString);
    }

    private String mergeCode(String requestCode, String testCode) {
        // 合并之前需要先考虑清楚这两个部分的代码都是啥样的.
        // 把 testCode 中的 main 方法内容嵌入到 requestCode 中.
        // 1. 先找到 requestCode 中的最后 一个 }
        // 2. 把最后一个 } 去掉之后, 和 testCode 进行字符串拼接.
        // 3. 拼接完毕之后, 在最后再补上一个 }
        int pos = requestCode.lastIndexOf("}");
        if (pos == -1) {
            // 此时 requestCode
            return null;
        }
        // 此处取的子串中不包含 pos 位置的元素  前闭后开区间
        return requestCode.substring(0, pos) + testCode + "\n}";
    }

    private String readBody(HttpServletRequest req) {
        // body 的长度在 header 中的一个 Content-Length 字段中.
        // contentLength 单位就是字节.
        int contentLength = req.getContentLength();
        byte[] buf = new byte[contentLength];
        try (InputStream inputStream = req.getInputStream()) {
            inputStream.read(buf, 0, contentLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buf);
    }
}
