/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.employee.service;

import app.config.ConfigApp;
import app.entity.EnApp;
import core.utilities.CommonUtil;
import core.utilities.DBConnector;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import kobishop.Tables;
import org.apache.log4j.Logger;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import static org.jooq.impl.DSL.trueCondition;

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
    
    public HashMap<String, Object> getOrders(int page,int ordersPerPage,String orderId,String from, String to) {
        Connection conn = null;
        try {
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MARIADB);
            Condition condition = trueCondition();
            if (CommonUtil.isInteger(orderId)) {
                condition = condition.and(Tables.ORDER.ID.eq(Integer.parseInt(orderId)));
            }
            if (CommonUtil.isValidString(from) && CommonUtil.isValidString(to) ) {
                Date date = formatter.parse(from);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                Date dtmp = cal.getTime();
                Timestamp tsFrom = new Timestamp(dtmp.getTime());
                date = formatter.parse(to);
                cal.setTime(date);
                cal.set(Calendar.HOUR, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                dtmp = cal.getTime();
                Timestamp tsTo = new Timestamp(dtmp.getTime());
                condition = condition.and(Tables.ORDER.CREATEDATE.between(tsFrom).and(tsTo));    
            }
            
            HashMap<String, Object> map = new HashMap<String, Object>();
            List<EnApp.EnOrder> list = create.selectFrom(Tables.ORDER).where(condition).orderBy(Tables.ORDER.CREATEDATE.desc()).fetchInto(EnApp.EnOrder.class);
            int sizeList = list.size();
            List<EnApp.EnOrder> sub = list.subList((page - 1) * ordersPerPage, page * ordersPerPage <= sizeList ? page * ordersPerPage : sizeList);
            map.put("numberOfPage", sizeList % ordersPerPage != 0 ? sizeList / ordersPerPage + 1 : sizeList / ordersPerPage);
            map.put("listOrders", sub);
            return map;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }
    
    public boolean modifyOrder(int orderId, String orderStatus) {
        Connection conn = null;
        try {
            conn = dbConnector.getMySqlConnection();
            DSLContext create = DSL.using(conn, SQLDialect.MARIADB);
            int result = create.update(Tables.ORDER)
                                .set(Tables.ORDER.STATE,orderStatus)
                                .where(Tables.ORDER.ID.eq(orderId))
                                .execute();
            if (result > 0) {
                logger.info("modify order success with OrderId: " + Integer.toString(orderId) + "OrderStatus: " + orderStatus );
                return true;
            }
            logger.info("modify order fail with OrderId: " + Integer.toString(orderId) + "OrderStatus: " + orderStatus );
            return false;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        logger.error("Database Error");
        return false;
    }
}
