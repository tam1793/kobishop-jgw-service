/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.admin.service;

import app.config.ConfigApp;
import core.utilities.DBConnector;
import java.sql.Connection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import static kobishop.Tables.PRODUCT;
import org.apache.log4j.Logger;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

/**
 *
 * @author Lenovo
 */
public class ProductService {
    private static final Logger logger = Logger.getLogger(ProductService.class.getName());
    private static final Lock LOCK = new ReentrantLock();
    private static ProductService instance = null;

    private final DBConnector dbConnector;

    private ProductService() {
        this.dbConnector = DBConnector.getInstance(ConfigApp.MYSQL_HOST,
                ConfigApp.MYSQL_PORT,
                ConfigApp.MYSQL_DBNAME,
                ConfigApp.MYSQL_USER,
                ConfigApp.MYSQL_PASSWORD);
    }

    public static ProductService getInstance() {
        if (instance == null) {
            LOCK.lock();
            try {
                if (instance == null) {
                    instance = new ProductService();
                }
            } finally {
                LOCK.unlock();
            }
        }
        return instance;
    }

    public int addProduct(int typeId, int brandId, String name, String description,int price, int soldItems, int leftItems, String specs) {
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);

            int result = create.insertInto(PRODUCT)
                    .set(PRODUCT.TYPEID, typeId)
                    .set(PRODUCT.BRANDID, brandId)
                    .set(PRODUCT.NAME, name)
                    .set(PRODUCT.DESCRIPTION, description)
                    .set(PRODUCT.PRICE, price)
                    .set(PRODUCT.SOLDITEMS, soldItems)
                    .set(PRODUCT.LEFTITEMS, leftItems)
                    .set(PRODUCT.SPECS, specs)
                    .set(PRODUCT.ISDELETED, Byte.valueOf("0"))
                    .execute();
            if (result > 0) {
                logger.info("add product success");
                return 1;
            }
            logger.info("add product fail");
            return 0;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }finally{
            if(conn != null){
                dbConnector.close(conn);
            }
        }
        logger.error("Database Error");
        return -1;
    }
    
    public int modifyProduct(int productId,int typeId, int brandId, String name, String description,int price, int leftItems, String specs, int isDeleted) {
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MYSQL);

            int result = create.update(PRODUCT)
                    .set(PRODUCT.TYPEID, typeId)
                    .set(PRODUCT.BRANDID, brandId)
                    .set(PRODUCT.NAME, name)
                    .set(PRODUCT.DESCRIPTION, description)
                    .set(PRODUCT.PRICE, price)
                    .set(PRODUCT.LEFTITEMS, leftItems)
                    .set(PRODUCT.SPECS, specs)
                    .set(PRODUCT.ISDELETED, Byte.valueOf(String.valueOf(isDeleted)))
                    .where(PRODUCT.ID.eq(productId))
                    .execute();
            if (result > 0) {
                logger.info("modify product success with Product Id: " + productId);
                return 1;
            }
            logger.info("modify product fail with Product Id: " + productId);
            return 0;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }finally{
            if(conn != null){
                dbConnector.close(conn);
            }
        }
        logger.error("Database Error");
        return -1;
    }
}
