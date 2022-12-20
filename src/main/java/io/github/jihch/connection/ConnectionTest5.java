package io.github.jihch.connection;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionTest5 {

    @Test
    public void testConnection() throws SQLException, ClassNotFoundException, IOException {

        //1.读取配置文件中的4个基本信息
        InputStream is = ConnectionTest5.class.getClassLoader().getResourceAsStream("jdbc.properties");

        Properties pros = new Properties();

        pros.load(is);

        String user = pros.getProperty("user");

        String password = pros.getProperty("password");

        String url = pros.getProperty("url");

        String driverClass = pros.getProperty("driverClass");

        System.out.println(user);

        System.out.println(password);

        System.out.println(url);

        System.out.println(driverClass);

        //2.加载驱动
        Class.forName(driverClass);

        //3.获取连接
        Connection conn = DriverManager.getConnection(url, user, password);

        System.out.println(conn);

    }

}
