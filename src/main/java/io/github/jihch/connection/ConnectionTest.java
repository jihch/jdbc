package io.github.jihch.connection;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionTest {

    //方式一：
    @Test
    public void testConnection1() throws SQLException {

        //获取Driver实现类对象
        Driver driver = new com.mysql.cj.jdbc.Driver();

        /*
        * jdbc:mysql  协议
        * localhost  ip地址
        * 3306 端口号
        * guns guns数据库
        * */
        String url = "jdbc:mysql://localhost:3306/guns";

        //将用户名和密码封装到 Properties 中
        Properties info = new Properties();
        info.setProperty("user", "root");
        info.setProperty("password", "123456");

        Connection conn = driver.connect(url, info);

        System.out.println(conn);

    }

}
