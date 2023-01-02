package io.github.jihch.blob;

import io.github.jihch.bean.Customer;
import io.github.jihch.util.JDBCUtils;
import org.junit.Test;

import java.io.*;
import java.sql.*;

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

    //查询数据表 customers 中 blob 类型的字段
    @Test
    public void testQuery() {
        Connection connection = null;
        String sql = "select id, name, email, birth, photo from customers where id = ?";
        PreparedStatement preparedStatement = null;
        InputStream binaryStream = null;
        FileOutputStream fileOutputStream = null;
        ResultSet resultSet = null;

        try {
            connection = JDBCUtils.getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, 5);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
//            方式一：
//            int id = resultSet.getInt(1);
//            String name = resultSet.getString(2);
//            String email = resultSet.getString(3);
//            Date birth = resultSet.getDate(4);

                //方式二：
                //一般来说方式二优于方式一，因为变更sql中查询字段顺序的时候不会影响到已经存在取结果集字段的代码
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                Date birth = resultSet.getDate("birth");

                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);

                //将 Blob 类型的字段下载下来，以文件的方式保存在本地
                Blob photo = resultSet.getBlob("photo");
                binaryStream = photo.getBinaryStream();
                fileOutputStream = new FileOutputStream(".\\zhangsanfeng_query.jpg");
                byte[] buffer = new byte[1024];
                int len;
                while ((len = binaryStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (binaryStream != null) {
                try {
                    binaryStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            JDBCUtils.closeResource(connection, preparedStatement, resultSet);
        }

    }

}
