/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.user.service;

import app.config.ConfigApp;
import core.utilities.DBConnector;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import static kobishop.tables.User.USER;
import kobishop.tables.records.UserRecord;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 *
 * @author tam
 */
public class PermissionService {

    private static final Logger logger = Logger.getLogger(PermissionService.class.getName());
    private static final Lock createLock = new ReentrantLock();
    private static final Map<String, PermissionService> instances = new HashMap();

    private static String loginSecret;
    private final DBConnector dbConnector;

    private PermissionService(String loginSecret) {
        this.loginSecret = loginSecret;
        this.dbConnector = DBConnector.getInstance(ConfigApp.MYSQL_HOST,
                ConfigApp.MYSQL_PORT,
                ConfigApp.MYSQL_DBNAME,
                ConfigApp.MYSQL_USER,
                ConfigApp.MYSQL_PASSWORD);
    }

    public static PermissionService getInstance(String loginSecret) {
        String key = loginSecret;
        if (!instances.containsKey(key)) {
            createLock.lock();
            try {
                if (!instances.containsKey(key)) {
                    instances.put(key, new PermissionService(loginSecret));
                }
            } finally {
                createLock.unlock();
            }
        }
        return instances.get(key);
    }
    
    public String getPermissionUser(String userName){
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
            UserRecord user = create.fetchOne(USER, USER.USERNAME.eq(userName));
            if(user == null){
                logger.info("userName not exist");
                return null;
            }
            return user.getRole();            
            
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}
