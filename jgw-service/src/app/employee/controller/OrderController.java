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
                    return getOrders(req);
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

    private EnApiOutput getOrders(HttpServletRequest req) {
        try {
            if (!CommonUtil.checkValidParam(req, new String[]{"page", "ordersPerPage"})
                    || !CommonUtil.isInteger(req.getParameter("page"))
                    || !CommonUtil.isInteger(req.getParameter("ordersPerPage"))) {
                logger.error("getOrders - params invalid - page: " + req.getParameter("page") + " - ordersPerPage: " + req.getParameter("ordersPerPage"));
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }
            
            String from = req.getParameter("from");
            String to = req.getParameter("to");
            String orderId = req.getParameter("orderId");
            int page = Integer.parseInt(req.getParameter("page"));
            int ordersPerPage = Integer.parseInt(req.getParameter("ordersPerPage"));
            if (page < 1 || ordersPerPage < 1) {
                logger.error("check param page & ordersPerPage invalid - page: " + page + " - ordersPerPage: " + ordersPerPage);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }
            
            HashMap<String, Object> resultOrder = OrderService.getInstance().getOrders(page,ordersPerPage,orderId,from,to);
            
            if (resultOrder!=null) {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, resultOrder);
            } else {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
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
            boolean updated = OrderService.getInstance().modifyOrder(Integer.parseInt(orderId), orderStatus);

            if (updated) {
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
