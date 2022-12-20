package io.github.jihch.preparedstatement.crud;

import io.github.jihch.bean.Order;
import io.github.jihch.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * 针对于 Order 表的通用的查询操作
 */
public class OrderForQuery {

    /**
     * 针对于表的字段名与类的属性名不相同的情况
     * 1.必须声明 sql 时，使用类的属性名来命名字段的别名
     * 2。使用 ResultSetMetaData 时，需要使用 getColumnLabel() 来替换 getColumnName() 获取列的别名
     *  说明：如果 sql 中没有给字段起别名，getColumnLabel() 获取的就是列名
     */
    @Test
    public void testOrderForQuery() {
        String sql = "select order_id orderId, order_name orderName, order_date orderDate from `order` where order_id = ?";
        Order order = orderForQuery(sql, 1);
        System.out.println(order);
    }

    /**
     * 针对于 Order 表的通用查询操作
     * @return
     */
    public Order orderForQuery(String sql, Object... args) {
        Order order = null;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i+1, args[i]);
            }

            //执行获取结果集
            rs = ps.executeQuery();

            //获取结果集的元数据
            ResultSetMetaData rsmd = rs.getMetaData();

            int columnCount = rsmd.getColumnCount();

            if (rs.next()) {
                order = new Order();

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
                    Field field = Order.class.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(order, value);
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

        return order;
    }


    @Test
    public void testQuery1() {
        String sql = "select order_id, order_name, order_date from `order` where order_id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            ps.setObject(1, 1);
            rs = ps.executeQuery();
            if (rs.next()) {
                int id = (int) rs.getObject(1);
                String name = (String) rs.getObject(2);
                Date date = (Date) rs.getObject(3);
                Order order = new Order(id, name, date);
                System.out.println(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }
    }
}
