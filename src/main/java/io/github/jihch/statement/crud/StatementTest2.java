package io.github.jihch.statement.crud;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

/**
 * try with resources autoclose 的写法
 */
public class StatementTest2 {

    // 使用 Statement 的弊端：需要拼写 SQL 语句，并且存在 SQL 注入的风险
    @Test
    public void testLogin() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入用户名：");

        String user = scanner.next();

        System.out.print("请输入密码：");

        String password = scanner.next();

        String sql = "SELECT USER, PASSWORD from user_table where user = '" + user + "' and password = '" + password + "'";

        User userInfo = get(sql, User.class);

        System.out.println(userInfo);

    }

    // 使用 Statement 实现对数据表的查询操作
    public <T> T get(String sql, Class<T> clazz) {
        T t = null;

        //1.加载配置文件
        Properties pros = new Properties();

        try (InputStream is = StatementTest.class.getClassLoader().getResourceAsStream("jdbc.properties")) {
            pros.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //2.读取配置信息
        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driverClass = pros.getProperty("driverClass");

        //3.加载驱动
        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try (//4.获取连接
             Connection conn = DriverManager.getConnection(url, user, password);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

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

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (InstantiationException e) {
            throw new RuntimeException(e);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        return t;

    }

}
