package io.github.jihch.preparedstatement.crud;

import io.github.jihch.util.JDBCUtils;
import org.junit.Test;

import java.sql.*;

/**
 * 测试数据表没有数据的情况下，返回的结果集元数据是否包含 columnName、columnLabel
 */
public class TestForResultSetMetaData {

    @Test
    public void test() {

        String sql = "select * from test_for_resultset_meta_data";

        orderForQuery(sql);

    }

    public void orderForQuery(String sql, Object... args) {

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

            for (int i = 0; i < columnCount; i++) {
                //获取每个列的列值：通过ResultSet

                //获取每个列的列名：通过ResultSetMetaData
                //获取列的列名：getColumnName() --不推荐使用
                //获取列的别名：getColumnLabel()
                String columnName = rsmd.getColumnName(i + 1);
                String columnLabel = rsmd.getColumnLabel(i + 1);
                System.out.printf("columnName:%s, columnLabel:%s\n", columnName, columnLabel);

            }
            //获取每个列的列名

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            JDBCUtils.closeResource(conn, ps, rs);

        }

    }

}
