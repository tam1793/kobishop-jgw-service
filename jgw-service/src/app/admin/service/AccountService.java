/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.admin.service;

import app.config.ConfigApp;
import app.entity.EnApp;
import core.utilities.DBConnector;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import kobishop.Tables;
import static kobishop.tables.Account.ACCOUNT;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 *
 * @author tam
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

    public int insertByAdmin(String userName, byte[] password, String name, String role, String email) {
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);

            int result = create.insertInto(ACCOUNT)
                    .set(ACCOUNT.USERNAME, userName)
                    .set(ACCOUNT.PASSWORD, password)
                    .set(ACCOUNT.NAME, name)
                    .set(ACCOUNT.ROLE, role)
                    .set(ACCOUNT.EMAIL, email)
                    .execute();
            if (result > 0) {
                logger.info("create admin success:" + userName + "--" + password);
                return 1;
            }
            logger.info("create admin fail:" + userName + "--" + password);
            return 0;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        logger.error("Database Error");

        return -1;
    }
    
    public HashMap<String, Object> getAccount(int page, int accountsPerPage) {
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MARIADB);
            HashMap<String, Object> map = new HashMap<String, Object>();
            List<EnApp.EnAccountInfoNoPass> list = create.selectFrom(Tables.ACCOUNT).fetchInto(EnApp.EnAccountInfoNoPass.class);
            int sizeList = list.size();
            List<EnApp.EnAccountInfoNoPass> sub = list.subList((page - 1) * accountsPerPage, page * accountsPerPage <= sizeList ? page * accountsPerPage : sizeList);
            map.put("numberOfPage", sizeList % accountsPerPage != 0 ? sizeList / accountsPerPage + 1 : sizeList / accountsPerPage);
            map.put("listAccounts", sub);
            return map;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
    
    public boolean modifyAccountRole(int userId, String role) {
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MARIADB);
            int result = create.update(Tables.ACCOUNT)
                                .set(Tables.ACCOUNT.ROLE,role)
                                .where(Tables.ACCOUNT.ID.eq(userId))
                                .execute();
            if (result > 0) {
                logger.info("modify account role success with UserId: " + Integer.toString(userId) + "Role: " + role );
                return true;
            }
            logger.info("modify account role fail with UserId: " + Integer.toString(userId) + "Role: " + role );
            return false;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        logger.error("Database Error");
        return false;
    }

}
