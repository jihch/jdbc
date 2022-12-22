package io.github.jihch.exer;

import io.github.jihch.util.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class Exer2Test {


    @Test
    public void testInsert() {

        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入四级/六级：");
        Integer type = scanner.nextInt();

        System.out.print("请输入身份证号码：");
        String idCard = scanner.next();

        System.out.print("请输入准考证号码：");
        String examCard = scanner.next();

        System.out.print("请输入学生姓名：");
        String studentName = scanner.next();

        System.out.print("请输入区域：");
        String location = scanner.next();

        System.out.print("请输入成绩：");
        Integer grade = scanner.nextInt();

        String sql = "INSERT INTO examstudent (Type, IDCard, ExamCard, StudentName, Location, Grade) values (?, ?, ?, ?, ?, ?)";
        int insertCount = update(sql, type, idCard, examCard, studentName, location, grade);

        if (insertCount > 0) {
            System.out.println("添加成功");
        } else {
            System.out.println("添加失败");
        }

    }

    //通用的增删改操作
    public int update(String sql, Object ...args) {//sql 中占位符的个数与可变形参的长度相同

        Connection conn = null;
        PreparedStatement ps = null;

        try {
            //1.获取数据库的连接
            conn = JDBCUtils.getConnection();

            //2.预编译sql语句，返回PreparedStatement的实例
            ps = conn.prepareStatement(sql);

            //3.填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            //4.执行
            /*
             * ps.execute();
             * 如果执行的查询操作，有返回结果，则此方法返回true;
             * 如果执行的是增删改操作，没有返回结果，则此方法返回false
             */
            //方式一：
//          return  ps.execute();
            //方式二：
            return ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, ps);
        }

        return 0;

    }

}
