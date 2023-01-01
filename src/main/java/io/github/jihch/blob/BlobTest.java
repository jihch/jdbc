package io.github.jihch.blob;

import io.github.jihch.util.JDBCUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * 测试使用 PreparedStatement 操作 Blob 类型的数据
 */
public class BlobTest {

    //向数据表 customers 中插入 blob 类型的字段
    @Test
    public void testInsert() throws Exception {
        Connection connection = JDBCUtils.getConnection();
        String sql = "insert into customers(name, email, birth, photo) values (?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setObject(1, "张三丰");
        preparedStatement.setObject(2, "zhangsanfeng@126.com");
        preparedStatement.setObject(3, "1992-09-08");
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("zhangsanfeng.jpg");
        preparedStatement.setBlob(4, inputStream);
        preparedStatement.execute();
        JDBCUtils.closeResource(connection, preparedStatement);
    }

}
