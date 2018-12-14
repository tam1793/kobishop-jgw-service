/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.guest.service;

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
public class TypeService {
    private static final Logger logger = Logger.getLogger(TypeService.class);
    private static final Lock createLock = new ReentrantLock();
    private static TypeService instance = null;
    
    private final DBConnector dbConnector;
    
    private TypeService() {
        this.dbConnector = DBConnector.getInstance(ConfigApp.MYSQL_HOST,
                ConfigApp.MYSQL_PORT,
                ConfigApp.MYSQL_DBNAME,
                ConfigApp.MYSQL_USER,
                ConfigApp.MYSQL_PASSWORD);
    }
    
    public static TypeService getInstance() {
        if (instance == null) {
            createLock.lock();
            try {
                if (instance == null) {
                    instance = new TypeService();
                }
            } finally {
                createLock.unlock();
            }
        }
        return instance;
    }
    
    public List<EnApp.EnType> getTypeList() {
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MARIADB);
            return create.selectFrom(Tables.TYPE).fetchInto(EnApp.EnType.class);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}
