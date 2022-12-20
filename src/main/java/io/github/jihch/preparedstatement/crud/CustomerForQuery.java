package io.github.jihch.preparedstatement.crud;

import io.github.jihch.bean.Customer;
import io.github.jihch.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * 针对于 Customers 表的查询操作
 */
public class CustomerForQuery {

    @Test
    public void testQueryForCustomers() {
        String sql = "select id, name, email, birth from customers where id = ?";
        Customer customer = queryForCustomers(sql, 2);
        System.out.println(customer);

        sql = "select name, email from customers where id = ?";
        customer = queryForCustomers(sql, 2);
        System.out.println(customer);

    }

    /**
     * 针对 customers表的通用的查询操作
     * @param sql
     * @param args
     * @throws Exception
     */
    public Customer queryForCustomers(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Customer customer = null;

        try {
            conn = JDBCUtils.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i+1, args[i]);
            }
            rs = ps.executeQuery();
            //获取结果集的元数据
            ResultSetMetaData rsmd = rs.getMetaData();

            //通过 ResultSetMetaData 获取结果集中的列数
            int columnCount = rsmd.getColumnCount();

            if (rs.next()) {

                customer = new Customer();

                //处理结果集一行数据中的每一列
                for (int i = 0; i < columnCount; i++) {

                    //获取每个列的列名
                    String columnName = rsmd.getColumnName(i+1);

                    Object columnValue = rs.getObject(i + 1);

                    //给 customer 对象指定的某个属性，赋值为 value
                    Field field = Customer.class.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(customer, columnValue);

                }

            }//end if

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            JDBCUtils.closeResource(conn, ps, rs);
        }

        return customer;
    }

    @Test
    public void testQuery1() {

        String sql = "select id, name, email, birth from customers where id = ?";
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            conn = JDBCUtils.getConnection();

            ps = conn.prepareStatement(sql);
            ps.setObject(1, 2);

            //执行，并返回结果集
            resultSet = ps.executeQuery();

            //处理结果集
            if (resultSet.next()) { //next():判断结果集的下一条是否有数据，如果有数据返回 true，并指针下移；如果返回 false，指针不会下移

                //获取当前这条数据的各个字段值
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                String email = resultSet.getString(3);
                Date birth = resultSet.getDate(4);

                //方式一：
//            System.out.println("id = " + id+", name = " + name + ", email = " + email + ", birth = " + birth);

                //方式二：
//            Object[] data = new Object[]{id, name, email, birth};

                //方式三：将数据封装为一个对象（推荐）
                Customer customer = new Customer(id, name, email, birth);
                System.out.println(customer);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtils.closeResource(conn, ps, resultSet);
        }

    }//end testQuery1

}
