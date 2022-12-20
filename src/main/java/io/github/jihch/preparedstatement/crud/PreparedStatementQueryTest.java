package io.github.jihch.preparedstatement.crud;

import io.github.jihch.bean.Customer;
import io.github.jihch.bean.Order;
import io.github.jihch.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 使用 PreparedStatement 实现针对于不同表的通用的查询操作
 */
public class PreparedStatementQueryTest {

    @Test
    public void testGetForList3() {
        String sql = "SELECT * FROM jdbc_test.customers";
        List<Customer> list = getForList(Customer.class, sql);
        list.forEach(System.out::println);

        sql = "SELECT * FROM `order`";
        List<Order> orderList = getForList(Order.class, sql);
        orderList.forEach(System.out::println);
    }

    public <T> List<T> getForList(Class<T> clazz, String sql, Object... args) {

        List<T> list = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

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
            Map<String, Field> fieldMap = Arrays.asList(clazz.getDeclaredFields()).stream().collect(Collectors.toMap(f -> f.getName(), f->f));

            while (rs.next()) {
                T t = clazz.newInstance();

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
                    if (fieldMap.containsKey(columnLabel)) {
                        Field field = clazz.getDeclaredField(columnLabel);
                        field.setAccessible(true);
                        field.set(t, value);
                    }
                }
                //获取每个列的列名

                list.add(t);

            }//end if


        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            JDBCUtils.closeResource(conn, ps, rs);

        }

        return list;
    }

    @Test
    public void testGetInstance2() {
        String sql = "SELECT * FROM jdbc_test.customers";
        Customer customer = getInstance2(Customer.class, sql);
        System.out.println(customer);
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

    @Test
    public void testGetInstance() {
        String sql = "select id, name, email from customers where id = ?";
        Customer customer = getInstance(Customer.class, sql, 2);
        System.out.println(customer);

        sql = "select order_id orderId, order_name orderName from `order` where order_id = ?";
        Order order = getInstance(Order.class, sql, 1);
        System.out.println(order);
    }

    public <T> T getInstance(Class<T> clazz, String sql, Object... args) {

        T t = null;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

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
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t, value);
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
