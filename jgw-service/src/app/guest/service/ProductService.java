/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.guest.service;

import app.config.ConfigApp;
import app.entity.EnApp.*;
import core.utilities.CommonUtil;
import core.utilities.DBConnector;
import java.sql.Connection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import kobishop.Tables;
import static kobishop.tables.Product.PRODUCT;
import org.apache.log4j.Logger;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import static org.jooq.impl.DSL.trueCondition;

/**
 *
 * @author tamnnq
 */
public class ProductService {

    private static final Logger logger = Logger.getLogger(ProductService.class);
    private static final Lock createLock = new ReentrantLock();
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
            createLock.lock();
            try {
                if (instance == null) {
                    instance = new ProductService();
                }
            } finally {
                createLock.unlock();
            }
        }
        return instance;
    }

    public List<EnProduct> getListProduct() {
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MARIADB);
//            Result<ProductRecord> record =
            return create.fetch(Tables.PRODUCT, Tables.PRODUCT.ISDELETED.eq(Byte.valueOf("0"))).into(EnProduct.class);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    public List<EnProduct> getListProduct(String productName, String brandId, String typeId, String priceOption, int page, int productsPerPage) {
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MARIADB);

            Condition condition = trueCondition();
            if (CommonUtil.isValidString(productName)) {
                condition = condition.and(PRODUCT.NAME.eq(productName));
            }
            if (CommonUtil.isInteger(brandId)) {
                condition = condition.and(PRODUCT.BRANDID.eq(Integer.parseInt(brandId)));
            }
            if (CommonUtil.isInteger(typeId)) {
                condition = condition.and(PRODUCT.TYPEID.eq(Integer.parseInt(typeId)));
            }

            if (CommonUtil.isValidString(priceOption)) {
                EnPriceOption option = EnPriceOption.getEnPriceOption(priceOption);
                if (option != null) {
                   condition = condition.and(PRODUCT.PRICE.between(option.lowest, option.highest));
                }
            }

            return create.select().from(PRODUCT).where(condition).limit(productsPerPage).offset(page * productsPerPage).fetch().into(EnProduct.class);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    public static void main(String[] args) {
        ConfigApp.init();
        System.out.println(CommonUtil.objectToString(ProductService.getInstance().getListProduct("", "1", "", "OPTION_2", 0, 100)));
    }

}
