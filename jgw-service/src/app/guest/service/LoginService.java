/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.guest.service;

import app.entity.EnApiOutput;
import app.entity.EnApp.EnUser;
import app.entity.EnApp.EnUserSession;
import app.config.ConfigApp;
import app.entity.EnApp.EnUserPermission;
import core.utilities.DBConnector;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import static kobishop.tables.Account.ACCOUNT;
import kobishop.tables.records.AccountRecord;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 *
 * @author tam
 */
public class LoginService {

    private static final Logger logger = Logger.getLogger(LoginService.class.getName());
    private static final Lock createLock = new ReentrantLock();
    private static final Map<String, LoginService> instances = new HashMap();

    private static String loginSecret;
    private final DBConnector dbConnector;

    private LoginService(String loginSecret) {
        this.loginSecret = loginSecret;
        this.dbConnector = DBConnector.getInstance(ConfigApp.MYSQL_HOST,
                ConfigApp.MYSQL_PORT,
                ConfigApp.MYSQL_DBNAME,
                ConfigApp.MYSQL_USER,
                ConfigApp.MYSQL_PASSWORD);
    }

    public static LoginService getInstance(String loginSecret) {
        String key = loginSecret;
        if (!instances.containsKey(key)) {
            createLock.lock();
            try {
                if (!instances.containsKey(key)) {
                    instances.put(key, new LoginService(loginSecret));
                }
            } finally {
                createLock.unlock();
            }
        }
        return instances.get(key);
    }

    public EnApiOutput createSession(String userName, byte[] password) {
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);
            AccountRecord user = create.fetchOne(ACCOUNT, ACCOUNT.USERNAME.eq(userName).and(ACCOUNT.PASSWORD.eq(password)));
            if (user == null) {
                logger.info("login false: USERNAME_OR_PASSWORD_INVALID");
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.USERNAME_OR_PASSWORD_INVALID);
            }
            HashMap<String, Object> result = new HashMap<String, Object>();

            String token = createToken(new EnUserPermission(user.getId(), user.getUsername(), user.getRole()));
            EnUserSession rs = new EnUserSession(token, user.getUsername(), user.getName(), user.getRole());
            result.put("session", rs);
            return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, result);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    public int createUser(EnUser newUser) {
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);

            int result = create.insertInto(ACCOUNT)
                    .set(ACCOUNT.USERNAME, newUser.userName)
                    .set(ACCOUNT.PASSWORD, newUser.password)
                    .set(ACCOUNT.NAME, newUser.name)
                    .set(ACCOUNT.ROLE, "user")
                    .set(ACCOUNT.EMAIL, newUser.email)
                    .execute();
            if (result > 0) {
                logger.info("create user success:" + newUser);
                return 1;
            }
            logger.info("create user fail:" + newUser);
            return 0;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        logger.error("Database Error");

        return -1;
    }

    public boolean checkUser(String userName) {
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);

            AccountRecord result = create.fetchOne(ACCOUNT, ACCOUNT.USERNAME.eq(userName));
            if (result != null) {
                logger.info("check user exist");
                return true;
            } else {
                logger.info("check user not exist");
                return false;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        logger.error("Database Error");
        return false;
    }

    private String createToken(EnUserPermission user) {
        try {
            String token = Jwts.builder()
                    .setSubject("Kobishop")
                    .setExpiration(new Date(System.currentTimeMillis() + (6 * 3600 * 1000L)))
                    .claim("user", user)
                    .signWith(SignatureAlgorithm.HS512, loginSecret)
                    .compact();
            return token;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}
