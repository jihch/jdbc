package io.github.jihch.batch;

import io.github.jihch.util.JDBCUtils;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 使用 PreparedStatement 实现批量数据的操作
 *
 * update、delete 本身就具有批量操作的效果
 * 此时的批量操作，主要指的是批量插入。使用 PreparedStatement 如何实现更高效地批量插入
 *
 * 题目：向 goods 表中插入 20000 条数据
 *
 * 方式一：使用 Statement
 * Connection connection = JDBCUtils.getConnection();
 * Statement statement = connection.createStatement();
 *
 * for (int i = 1; i <= 20000; i++) {
 *     String sql = "insert into goods(name) values('name_" + i + "')";
 *     statement.execute(sql);
 * }
 */
public class InsertTest {

    //批量插入的方式二：使用 PreparedStatement
    @Test
    public void testInsert1() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            long start = System.currentTimeMillis();
            connection = JDBCUtils.getConnection();
            String sql = "insert into goods(name) values(?)";
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 1; i <= 20000; i++) {
                preparedStatement.setObject(1, "name_" + i);
                preparedStatement.execute();
            }
            long end = System.currentTimeMillis();
            System.out.printf("花费的时间为：%d\n", end - start);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtils.closeResource(connection, preparedStatement);
        }
    }

    /**
     * 批量插入的方式三：
     * 1.addBatch()、executeBatch()、clearBatch()
     * 2.MySQL 服务器默认是关闭批处理的，我们需要通过一个参数，让 MySQL 开启批处理的支持。
     *  ?rewriteBatchedStatements=true 写在配置文件的 URL 后面
     * 3.使用更新的 MySQL 驱动：>= mysql-connector-java-5.1.37-bin.jar
     */
    @Test
    public void testInsert2() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            long start = System.currentTimeMillis();
            connection = JDBCUtils.getConnection();
            String sql = "insert into goods(name) values(?)";
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 1; i <= 20000; i++) {
                preparedStatement.setObject(1, "name_" + i);

                //1.“攒” SQL
                preparedStatement.addBatch();

                if (i % 500 == 0) {
                    //2.执行
                    preparedStatement.executeBatch();

                    //3.清空batch
                    preparedStatement.clearBatch();
                }
            }
            long end = System.currentTimeMillis();
            System.out.printf("花费的时间为：%d\n", end - start);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtils.closeResource(connection, preparedStatement);
        }
    }

    @Test
    public void testInsert4() {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            long start = System.currentTimeMillis();
            connection = JDBCUtils.getConnection();

            //设置不允许自动提交
            connection.setAutoCommit(false);

            String sql = "insert into goods(name) values(?)";
            preparedStatement = connection.prepareStatement(sql);
            for (int i = 1; i <= 1000000; i++) {
                preparedStatement.setObject(1, "name_" + i);

                //1.“攒” SQL
                preparedStatement.addBatch();

                if (i % 500 == 0) {
                    //2.执行
                    preparedStatement.executeBatch();

                    //3.清空batch
                    preparedStatement.clearBatch();
                }
            }

            //提交数据
            connection.commit();

            long end = System.currentTimeMillis();
            System.out.printf("花费的时间为：%d\n", end - start);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            JDBCUtils.closeResource(connection, preparedStatement);
        }
    }

}
