/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.employee.controller;

import app.employee.service.OrderService;
import app.entity.EnApiOutput;
import app.entity.EnApp;
import core.utilities.CommonUtil;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Lenovo
 */
public class OrderController extends AbstractEmployeeController {

    private final Logger logger = Logger.getLogger(app.user.controller.OrderController.class);

    @Override
    protected EnApiOutput doProcess(EnApp.EnUserPermission verifiedUserName, HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();

            switch (pathInfo) {
                case "/getOrders":
                    return getOrders();
                case "/modifyOrder":
                    return modifyOrder(req, resp);
                default:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput getOrders() {
        try {
            List<EnApp.EnOrder> resultOrders = OrderService.getInstance().getOrders();
            HashMap<String, Object> result = new HashMap<String, Object>();
            if (!resultOrders.isEmpty()) {
                result.put("listOrders", resultOrders);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, result);
            } else {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.ORDER_NOT_FOUND);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);

    }

    private EnApiOutput modifyOrder(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (!checkValidParam(req, new String[]{"orderId", "orderStatus"})
                    || !CommonUtil.isValidString(req.getParameter("orderId"))
                    || !CommonUtil.isValidString(req.getParameter("orderStatus"))) {
                logger.info("modifyOrder fail: " + req);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }

            String orderId = req.getParameter("orderId");
            String orderStatus = req.getParameter("orderStatus");
            boolean added = OrderService.getInstance().modifyOrder(Integer.parseInt(orderId), orderStatus);

            if (added) {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS);
            } else {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

}
