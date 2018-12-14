/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.user.service;

import app.config.ConfigApp;
import app.entity.EnApp;
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
public class AccountService {
    private static final Logger logger = Logger.getLogger(AccountService.class.getName());
    private static final Lock LOCK = new ReentrantLock();
    private static AccountService instance = null;

    private final DBConnector dbConnector;

    private AccountService() {
        this.dbConnector = DBConnector.getInstance(ConfigApp.MYSQL_HOST,
                ConfigApp.MYSQL_PORT,
                ConfigApp.MYSQL_DBNAME,
                ConfigApp.MYSQL_USER,
                ConfigApp.MYSQL_PASSWORD);
    }

    public static AccountService getInstance() {
        if (instance == null) {
            LOCK.lock();
            try {
                if (instance == null) {
                    instance = new AccountService();
                }
            } finally {
                LOCK.unlock();
            }
        }
        return instance;
    }
    
    public List<EnApp.EnAccountInfo> getInfo(int userId) {
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MARIADB);
            return create.fetch(Tables.ACCOUNT, Tables.ACCOUNT.ID.eq(userId)).into(EnApp.EnAccountInfo.class);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}
