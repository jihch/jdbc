package io.github.jihch.connection;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionTest4 {

    @Test
    public void testConnection() throws SQLException, ClassNotFoundException {

        //1.获取 Driver 实现类的对象
//        Class.forName("com.mysql.cj.jdbc.Driver");

        //2.提供另外三个连接的基本信息
        String url = "jdbc:mysql://localhost:3306/guns";
        String user = "root";
        String password = "123456";

        Connection conn = DriverManager.getConnection(url, user, password);

        System.out.println(conn);

    }

}
