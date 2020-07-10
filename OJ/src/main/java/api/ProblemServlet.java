package api;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import problem.Problem;
import problem.ProblemDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ProblemServlet extends HttpServlet {
    private Gson gson = new GsonBuilder().create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id == null ||"".equals(id)){
            //没有id 查找全部
            selectAll(resp);
        } else{
            //存在id 查找指定题目
            selectOne(Integer.parseInt(id),resp);
        }
    }

    private void selectOne(int problemId, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=utf-8");
        ProblemDAO problemDAO = new ProblemDAO();
        Problem problem = problemDAO.selectOne(problemId);
        //前端不应该显示出测试代码
        problem.setTestCode("");
        String jsonString = gson.toJson(problem);
        resp.getWriter().write(jsonString);
    }

    private void selectAll(HttpServletResponse resp) throws IOException {
        //ContentType描述了包内数据类型是什么
        //常见取值： HTML：text/html  图片: image/png  image/jpg
        //json：application/json
        //css：text/css
        //JavaScript： application/JavaScript
        resp.setContentType("application/json; charset=utf-8");
        ProblemDAO problemDAO = new ProblemDAO();
        List<Problem> problems = problemDAO.selectAll();
        //吧结果组织成json结构
        //需要把problems 中某些字段去掉  不需要全部的
        String jsonString =  gson.toJson(problems);
        resp.getWriter().write(jsonString);
    }
}
