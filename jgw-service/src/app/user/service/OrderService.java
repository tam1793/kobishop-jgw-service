/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.user.service;

import app.config.ConfigApp;
import app.entity.EnApp.*;
import core.utilities.DBConnector;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import kobishop.Tables;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 *
 * @author Lenovo
 */
public class OrderService {
    private static final Logger logger = Logger.getLogger(OrderService.class.getName());
    private static final Lock LOCK = new ReentrantLock();
    private static OrderService instance = null;

    private final DBConnector dbConnector;

    private OrderService() {
        this.dbConnector = DBConnector.getInstance(ConfigApp.MYSQL_HOST,
                ConfigApp.MYSQL_PORT,
                ConfigApp.MYSQL_DBNAME,
                ConfigApp.MYSQL_USER,
                ConfigApp.MYSQL_PASSWORD);
    }

    public static OrderService getInstance() {
        if (instance == null) {
            LOCK.lock();
            try {
                if (instance == null) {
                    instance = new OrderService();
                }
            } finally {
                LOCK.unlock();
            }
        }
        return instance;
    }
    
    public List<EnOrder> getOrders(int orderId) {
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MARIADB);
//            Result<ProductRecord> record =
            return create.fetch(Tables.ORDER, Tables.ORDER.ID.eq(orderId)).into(EnOrder.class);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}
