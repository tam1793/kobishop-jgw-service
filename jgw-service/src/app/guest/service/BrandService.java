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
public class BrandService {
    private static final Logger logger = Logger.getLogger(BrandService.class);
    private static final Lock createLock = new ReentrantLock();
    private static BrandService instance = null;
    
    private final DBConnector dbConnector;
    
    private BrandService() {
        this.dbConnector = DBConnector.getInstance(ConfigApp.MYSQL_HOST,
                ConfigApp.MYSQL_PORT,
                ConfigApp.MYSQL_DBNAME,
                ConfigApp.MYSQL_USER,
                ConfigApp.MYSQL_PASSWORD);
    }
    
    public static BrandService getInstance() {
        if (instance == null) {
            createLock.lock();
            try {
                if (instance == null) {
                    instance = new BrandService();
                }
            } finally {
                createLock.unlock();
            }
        }
        return instance;
    }
    
    public List<EnApp.EnBrand> getBrandList() {
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MARIADB);
            return create.selectFrom(Tables.BRAND).fetchInto(EnApp.EnBrand.class);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }finally{
            if(conn != null){
                dbConnector.close(conn);
            }
        }
    }
}
