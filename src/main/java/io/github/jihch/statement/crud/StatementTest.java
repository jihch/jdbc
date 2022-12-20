package io.github.jihch.statement.crud;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class StatementTest {

    // 使用 Statement 的弊端：需要拼写 SQL 语句，并且存在 SQL 注入的风险
    @Test
    public void testLogin() {

        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入用户名：");

        String user = scanner.nextLine();

        System.out.print("请输入密码：");

        /* 当输入密码的字符串是：  ' or '1' = '1
         * SQL 也会执行并返回数据
         * next() 和 nextLine() 的区别：空格也会被next()方法认为是结束输入的字符，所以这里要用 nextLine()
         */
        String password = scanner.nextLine();

        String sql = "SELECT user, password from user_table where user = '" + user + "' and password = '" + password + "'";

        System.out.printf("\nsql:%s\n", sql);

        User userInfo = get(sql, User.class);

        if (userInfo != null) {
            System.out.println("登录成功");
        } else {
            System.out.println("用户名不存在或密码错误");
        }
    }

    // 使用 Statement 实现对数据表的查询操作
    public <T> T get(String sql, Class<T> clazz) {
        T t = null;
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            //1.加载配置文件
            InputStream is = StatementTest.class.getClassLoader().getResourceAsStream("jdbc.properties");
            Properties pros = new Properties();
            pros.load(is);

            //2.读取配置信息
            String user = pros.getProperty("user");
            String password = pros.getProperty("password");
            String url = pros.getProperty("url");
            String driverClass = pros.getProperty("driverClass");

            //3.加载驱动
            Class.forName(driverClass);

            //4.获取连接
            conn = DriverManager.getConnection(url, user, password);
            st = conn.createStatement();
            rs = st.executeQuery(sql);

            //获取结果集的元数据
            ResultSetMetaData rsmd = rs.getMetaData();

            //获取结果集的列数
            int columnCount = rsmd.getColumnCount();

            if (rs.next()) {

                t = clazz.newInstance();

                for (int i = 0; i < columnCount; i++) {

                    //1.获取列的名称
                    String columnName = rsmd.getColumnLabel(i+1);

                    //2.根据列名获取对应数据表中的数据
                    Object columnVal = rs.getObject(columnName);

                    //3.将数据表中得到的数据，封装进对象
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(t, columnVal);

                }

                return t;

            }

        } catch (IOException e) {
            throw new RuntimeException(e);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (InstantiationException e) {
            throw new RuntimeException(e);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            if (st != null) {
                try {
                    st.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        return t;

    }



}
