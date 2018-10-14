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
import core.utilities.DBConnector;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import static kobishop.Tables.USER;
import kobishop.tables.records.UserRecord;
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
            UserRecord user = create.fetchOne(USER, USER.USERNAME.eq(userName).and(USER.PASSWORD.eq(password)));
            if (user == null) {
                logger.info("login false: USERNAME_OR_PASSWORD_INVALID");
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.USERNAME_OR_PASSWORD_INVALID);
            }
            String token = createToken(user.getUsername());
            EnUserSession rs = new EnUserSession(token, user.getUsername(), user.getRole());
            return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, rs);

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

            int result = create.insertInto(USER)
                    .set(USER.USERNAME, newUser.userName)
                    .set(USER.PASSWORD, newUser.password)
                    .set(USER.NAME, newUser.name)
                    .set(USER.BIRTHDAY, newUser.dob)
                    .set(USER.ADDRESS, newUser.address)
                    .set(USER.PHONE, newUser.phone)
                    .set(USER.ROLE, "user")
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

            UserRecord result = create.fetchOne(USER, USER.USERNAME.eq(userName));
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

    private String createToken(String userName) {
        try {
            String token = Jwts.builder()
                    .setSubject("Nab Id Coh")
                    .setExpiration(new Date(System.currentTimeMillis() + (6 * 3600 * 1000L)))
                    .claim("userName", userName)
                    .signWith(SignatureAlgorithm.HS512, loginSecret)
                    .compact();
            return token;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
}
