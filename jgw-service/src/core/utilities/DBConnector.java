/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.utilities;

import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import snaq.db.ConnectionPool;

public class DBConnector {

    private static final Logger LOGGER = Logger.getLogger(DBConnector.class);
    private String MYSQL_HOST;
    private String MYSQL_PORT;
    private String MYSQL_DBNAME;
    private String MYSQL_USER;
    private String MYSQL_PASS;

    private static final int MAX_RECREATE_POOL_ATTEMPTS = 1;

    private static final Lock LOCK = new ReentrantLock();
    private static final Map<String, DBConnector> _instances = new NonBlockingHashMap<>();

    private ConnectionPool mySqlPool = null;
    private static int attempts = 0;

    public static DBConnector getInstance(String host, String port, String dbname, String user, String password) {
        String key = host + port + dbname + user + password;

        if (!_instances.containsKey(key)) {
            LOCK.lock();
            try {
                if (_instances.get(key) == null) {
                    _instances.put(key, new DBConnector(host, port, dbname, user, password));
                }
            } finally {
                LOCK.unlock();
            }
        }
        return _instances.get(key);
    }

    private DBConnector(String host, String port, String dbname, String user, String password) {
        MYSQL_HOST = host;
        MYSQL_PORT = port;
        MYSQL_DBNAME = dbname;
        MYSQL_USER = user;
        MYSQL_PASS = password;
        initializeMySqlPool();
    }

    private synchronized void initializeMySqlPool() {
        if (null == mySqlPool) {
            try {
                Class<?> c = Class.forName("com.mysql.jdbc.Driver");
                Driver driver = (Driver) c.newInstance();
                DriverManager.registerDriver(driver);

                final String MYSQL_PATH = "jdbc:mysql://"
                        + MYSQL_HOST
                        + ":" + MYSQL_PORT
                        + "/" + MYSQL_DBNAME
                        + "?useUnicode=true&characterEncoding=utf-8";

                LOGGER.info("Mysql jdbc: " + MYSQL_PATH);
                LOGGER.info("user: "+MYSQL_USER);
                LOGGER.info("pass: "+MYSQL_PASS);
                mySqlPool = new ConnectionPool("gatool-mysql",
                        10, // min-pool default = 1 
                        40, // max-pool default = 5 
                        200, // max-size default 30 
                        500, // timeout (sec) 
                        MYSQL_PATH,
                        MYSQL_USER,
                        MYSQL_PASS);
                LOGGER.info("mySQLPool: finished init pool...");
                attempts = 0;
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                LOGGER.error("Exception when initializing MySQL connection");
                LOGGER.error("Exception:", e);
                ++attempts;
            } catch (SQLException ex) {
                LOGGER.error("Exception when initializing MySQL connection");
            }
        }
    }

    public synchronized Connection getMySqlConnection() throws SQLException {
        long timeout = 2000;
        Connection conn = null;

        try {
            if (mySqlPool != null) {
                conn = mySqlPool.getConnection(timeout);
            } else {
                LOGGER.error("MySql Pool is NULL");
                initializeMySqlPool();
                conn = mySqlPool.getConnection(timeout);
            }

            if (conn != null) {
                attempts = 0;
                return conn;
            } else if (attempts < MAX_RECREATE_POOL_ATTEMPTS) {
                if (!mySqlPool.isReleased() && mySqlPool != null) {
                    mySqlPool.release();
                    mySqlPool = null;
                    ++attempts;
                }
                initializeMySqlPool();
                conn = mySqlPool.getConnection(timeout);
                LOGGER.warn("MySQL Pool reallocated");
                if (null == conn) {
                    LOGGER.error("The created MySQL connection is null even AFTER Reinitializing the MySQL Pool!!! Giving up...");
                } else {
                    attempts = 0; //reset
                }
                return conn;
            }
        } catch (Exception ex) {
            LOGGER.error("Exception", ex);

            if (attempts < MAX_RECREATE_POOL_ATTEMPTS) {
                if (!mySqlPool.isReleased() && mySqlPool != null) {
                    mySqlPool.release();
                    mySqlPool = null;
                    ++attempts;
                }
                initializeMySqlPool();
                conn = mySqlPool.getConnection(timeout);
                if (conn != null) {
                    attempts = 0;  //reset 
                }
            }
        }
        return conn;
    }

    public void close(Connection con) {
        if (con == null) {
            return;
        }
        try {
            con.close();
            con = null;
        } catch (SQLException e) {
            LOGGER.error("Exception when returning MySQL connection", e);
        }
    }

    public void close(Statement statement) {
        if (statement == null) {
            return;
        }
        try {
            statement.close();
            statement = null;
        } catch (SQLException e) {
            LOGGER.error("Could not close statement", e);
        }
    }

    public void close(ResultSet resultset) {
        if (resultset == null) {
            return;
        }
        try {
            resultset.close();
            resultset = null;
        } catch (SQLException e) {
            LOGGER.error("Could not close resultSet", e);
        }
    }

    public String getPoolInfo() {
        String content = "";

        content += "<p>CheckedOut: " + mySqlPool.getCheckedOut() + "</p>";
        content += "<p>FreeCount: " + mySqlPool.getFreeCount() + "</p>";
        content += "<p>MaxPool: " + mySqlPool.getMaxPool() + "</p>";
        content += "<p>MinPool: " + mySqlPool.getMinPool() + "</p>";
        content += "<p>MaxSize: " + mySqlPool.getMaxSize() + "</p>";
        content += "<p>PoolHitRate: " + mySqlPool.getPoolHitRate() + "</p>";
        content += "<p>PoolMissRate: " + mySqlPool.getPoolMissRate() + "</p>";
        content += "<p>RequestCount: " + mySqlPool.getRequestCount() + "</p>";

        return content;
    }

}
