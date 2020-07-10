package problem;

import common.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//数据访问层
public class ProblemDAO {
    //获取所有题目信息
    public List<Problem> selectAll(){
        List<Problem> result = new ArrayList<>();
        //1.获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2.构造sql语句
        String sql = "select * from oj_table";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            //3.执行sql语句
            resultSet = statement.executeQuery();
            //4.遍历结果集
            while(resultSet.next()){
                Problem problem = new Problem();
                problem.setId(resultSet.getInt("id"));
                problem.setTitle(resultSet.getString("title"));
                problem.setLevel(resultSet.getString("level"));
                //暂时不不需要
//                problem.setDescription(resultSet.getString("description"));
//                problem.setTemplateCode(resultSet.getString("templateCode"));
//                problem.setTestCode(resultSet.getString("testCode"));
                result.add(problem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //5.关闭释放资源
            DBUtil.close(connection,statement,resultSet);
        }
        return result;
    }

    //获取指定id的题目信息
    public Problem selectOne(int id){
        //1.建立数据库连接
        Connection connection = DBUtil.getConnection();
        //2.编写sql语句
        String sql = "select * from oj_table where id = ?";
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,id);
            //3.执行sql语句
            resultSet = statement.executeQuery();
            //4.遍历结果集
            if (resultSet.next()){
                Problem problem = new Problem();
                problem.setId(resultSet.getInt("id"));
                problem.setTitle(resultSet.getString("title"));
                problem.setLevel(resultSet.getString("level"));
                problem.setDescription(resultSet.getString("description"));
                problem.setTemplateCode(resultSet.getString("templateCode"));
                problem.setTestCode(resultSet.getString("testCode"));
                return problem;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //5.释放关闭资源
            DBUtil.close(connection,statement,resultSet);
        }
        return null;
    }

    //新增题目到数据库中
    public void insert(Problem problem){
        //1.回去数据库连接
        Connection connection = DBUtil.getConnection();
        //2.构造sql语句
        String sql = "insert into oj_table values(null, ?, ?, ?, ?, ?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,problem.getTitle());
            statement.setString(2,problem.getLevel());
            statement.setString(3,problem.getDescription());
            statement.setString(4,problem.getTemplateCode());
            statement.setString(5,problem.getTestCode());
            System.out.println("insert: " + statement);
            //3.执行sql语句
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //4.关闭释放相关资源
            DBUtil.close(connection,statement,null);
        }
    }

    //删除某一题目
    public void delete(int id){
        //1.获取数据库连接
        Connection connection = DBUtil.getConnection();
        //2.编写sql语句
        String sql = "delete from oj_table wheere id = ?";
        PreparedStatement statement = null;
        try {
            //3.执行sql语句
            statement = connection.prepareStatement(sql);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            //4.释放关闭资源
            DBUtil.close(connection,statement,null);
        }
    }

    public static void main(String[] args) {
        //1.验证insert
        Problem problem = new Problem();
        problem.setTitle("1.面试题 17.13. 恢复空格");
        problem.setLevel("中等");
        problem.setDescription("哦，不！你不小心把一个长篇文章中的空格、标点都删掉了，并且大写也弄成了小写。示例：\n" +
                "\n" +
                "输入：\n" +
                "dictionary = [\"looked\",\"just\",\"like\",\"her\",\"brother\"]\n" +
                "sentence = \"jesslookedjustliketimherbrother\"\n" +
                "输出： 7\n" +
                "解释： 断句后为\"jess looked just like tim her brother\"，共7个未识别字符。\n" +
                "提示：\n" +
                "\n" +
                "来源：力扣（LeetCode）\n" +
                "链接：https://leetcode-cn.com/problems/re-space-lcci\n" +
                "著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。");
        problem.setTemplateCode("public class Main {\n" +
                "    public int add(int n) {\n" +
                "\n" +
                "    }\n" +
                "}");
        problem.setTestCode(
                "    public static void main(String[] args) {\n" +
                "        Main s = new Main();\n" +
                "        if (s.add(2) == 1){\n" +
                "            System.out.println(\"test ok\");\n" +
                "        } else {\n" +
                "            System.out.println(\"test failed\");\n" +
                "        }\n" +
                "\n" +
                "        if (s.add(4) == 3){\n" +
                "            System.out.println(\"test ok\");\n" +
                "        } else {\n" +
                "            System.out.println(\"test failed\");\n" +
                "        }\n" +
                "    }\n" );

        ProblemDAO problemDAO = new ProblemDAO();
        problemDAO.insert(problem);
        System.out.println("insert okk");

//        //测试 selectAll
//        ProblemDAO problemDAO = new ProblemDAO();
//        List<Problem> problems = problemDAO.selectAll();
//        System.out.println("selectAll: " + problems);

        //测试selectOne
//        ProblemDAO problemDAO = new ProblemDAO();
//        Problem problem = problemDAO.selectOne(1);
//        System.out.println("selectOne: " + problem);
    }
}
