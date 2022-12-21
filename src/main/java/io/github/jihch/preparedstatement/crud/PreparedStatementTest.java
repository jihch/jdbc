package io.github.jihch.preparedstatement.crud;

import io.github.jihch.statement.crud.User;
import io.github.jihch.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 演示使用PreparedStatement 替换 Statement 解决SQL 注入问题
 *
 * 除了解决 Statement 的拼串、sql问题之外，PreparedStatement 还有哪些好处呢？
 * 1.PreparedStatement 操作 Blob 的数据，而Statement做不到。
 * 2.PreparedStatement 可以实现更高效地批量操作
 */
public class PreparedStatementTest {

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

        String sql = "SELECT user, password from user_table where user = ? and password = ?";

        User user1 = getInstance2(User.class, sql, user, password);

        System.out.println(user1);

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
                    System.out.printf("columnName:%s, columnLabel:%s\n", columnName, columnLabel);

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

}
