package io.github.jihch.connection;

import org.junit.Test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionTest2 {

    /**
     * 方式二：
     * 区别于第一种方式，这个方式不需要在当前类中 import 第三方 package
     * @throws SQLException
     */
    @Test
    public void testConnection2() throws SQLException, ClassNotFoundException, InstantiationException,
            IllegalAccessException {

        //1.获取 Driver 实现类对象：使用反射来实现
        Class<?> clazz = Class.forName("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) clazz.newInstance();

        //2.提供要连接的数据库
        String url = "jdbc:mysql://localhost:3306/guns";

        //将用户名和密码封装到 Properties 中
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "123456");

        Connection conn = driver.connect(url, info);

        System.out.println(conn);

    }

}
