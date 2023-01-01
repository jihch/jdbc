package io.github.jihch.exer;

import io.github.jihch.bean.ExamStudent;
import io.github.jihch.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
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

    //问题2：根据身份证号或者准考证号查询学生成绩信息
    @Test
    public void queryWithIDCardOrExamCard() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("请选择您要输入的类型：");
        System.out.println("a.准考证号");
        System.out.println("b.身份证号");

        String selection = scanner.next();
        if ("a".equalsIgnoreCase(selection)) { //这种写法可以有效避免空指针异常
            System.out.println("请输入准考证号：");
            String examCard = scanner.next();

            String sql = "SELECT FlowID flowId, Type type, IDCard idCard, ExamCard examCard, StudentName studentName, Location location, Grade grade FROM examstudent where ExamCard = ?";

            ExamStudent examStudent = getInstance2(ExamStudent.class, sql, examCard);
            if(examStudent != null){
                System.out.println("=========查询结果=========");
                System.out.println(examStudent.info());
            } else {
                System.out.println("输入的准考证号有误！");
            }

        } else if ("b".equalsIgnoreCase(selection)) {
            System.out.println("请输入身份证号：");
            String idCard = scanner.next();

            String sql = "SELECT FlowID flowId, Type type, IDCard idCard, ExamCard examCard, StudentName studentName, Location location, Grade grade FROM examstudent where idCard = ?";

            ExamStudent examStudent = getInstance2(ExamStudent.class, sql, idCard);
            if(examStudent != null){
                System.out.println("=========查询结果=========");
                System.out.println(examStudent.info());
            } else {
                System.out.println("输入的身份证号有误！");
            }


        } else {
            System.out.println("您的输入有误，请重新进入程序。");
        }
        Integer type = scanner.nextInt();
    }

    public <T> T getInstance2(Class<T> clazz, String sql, Object... args) {
        T t = null;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        Map<String, Field> fieldMap = new HashMap<>();
        for (Field declaredField : clazz.getDeclaredFields()) {
            fieldMap.put(declaredField.getName(), declaredField);
        }

        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            //执行获取结果集
            rs = ps.executeQuery();

            //获取结果集的元数据
            ResultSetMetaData rsmd = rs.getMetaData();

            int columnCount = rsmd.getColumnCount();

            if (rs.next()) {

                t = clazz.newInstance();

                for (int i = 0; i < columnCount; i++) {
                    //获取每个列的列值：通过ResultSet
                    Object value = rs.getObject(i + 1);

                    //获取每个列的列名：通过ResultSetMetaData
                    //获取列的列名：getColumnName() --不推荐使用
                    //获取列的别名：getColumnLabel()
                    String columnName = rsmd.getColumnName(i + 1);
                    String columnLabel = rsmd.getColumnLabel(i + 1);
//                    System.out.printf("columnName:%s, columnLabel:%s\n", columnName, columnLabel);

                    //通过反射，将对象指定名 columnName 的属性赋值为指定的值 columnValue
//                    Field field = Order.class.getDeclaredField(columnName);

                    if (fieldMap.containsKey(columnLabel)) {
                        Field field = clazz.getDeclaredField(columnLabel);
                        field.setAccessible(true);
                        field.set(t, value);
                    }
                }
                //获取每个列的列名

            }//end if

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            JDBCUtils.closeResource(conn, ps, rs);

        }

        return t;

    }

    //问题3：删除指定的学生信息
    @Test
    public void testDeleteByExamCard() {
        System.out.println("请输入学生的考号：");
        Scanner scanner = new Scanner(System.in);
        String examCard = scanner.next();
        //查询指定准考证号的学生
        String sql = "SELECT FlowID flowId, Type type, IDCard idCard, ExamCard examCard, StudentName studentName, Location location, Grade grade FROM examstudent where ExamCard = ?";
        ExamStudent student = getInstance2(ExamStudent.class, sql, examCard);
        if (student == null) {
            System.out.println("查无此人，请重新输入");

        } else {
            String sql1 = "delete from examstudent where ExamCard = ?";
            int deleteCount = update(sql1, examCard);
            if (deleteCount > 0) {
                System.out.println("删除成功");
            }
        }

    }

    @Test
    public void testDeleteByExamCard1() {
        System.out.println("请输入学生的考号：");
        Scanner scanner = new Scanner(System.in);
        String examCard = scanner.next();
        String sql1 = "delete from examstudent where ExamCard = ?";
        int deleteCount = update(sql1, examCard);
        if (deleteCount > 0) {
            System.out.println("删除成功");
        } else {
            System.out.println("查无此人，请重新输入");
        }
    }

}
