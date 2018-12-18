/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.user.controller;

import app.entity.EnApiOutput;
import app.entity.EnApp;
import app.user.service.OrderService;
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
public class OrderController extends AbstractController {

    private final Logger logger = Logger.getLogger(OrderController.class);

    @Override
    protected EnApiOutput doProcess(EnApp.EnUserPermission verifiedUserName, HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();

            switch (pathInfo) {
                case "/getOrders":
                    return getOrders(verifiedUserName.userId,req);
                case "/addOrder":
                    return addOrder(verifiedUserName.userId, req, resp);
                default:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput getOrders(int userId,HttpServletRequest req) {
        try {
            if (!CommonUtil.checkValidParam(req, new String[]{"page", "ordersPerPage"})
                    || !CommonUtil.isInteger(req.getParameter("page"))
                    || !CommonUtil.isInteger(req.getParameter("ordersPerPage"))) {
                logger.error("getOrders - params invalid - page: " + req.getParameter("page") + " - ordersPerPage: " + req.getParameter("ordersPerPage"));
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }
            int page = Integer.parseInt(req.getParameter("page"));
            int ordersPerPage = Integer.parseInt(req.getParameter("ordersPerPage"));
            if (page < 1 || ordersPerPage < 1) {
                logger.error("check param page & ordersPerPage invalid - page: " + page + " - ordersPerPage: " + ordersPerPage);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }
            
            HashMap<String, Object> result = OrderService.getInstance().getOrders(userId,page,ordersPerPage);
            if (result!=null) {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, result);
            } else {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.ORDER_NOT_FOUND);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);

    }

    private EnApiOutput addOrder(int userId, HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (!checkValidParam(req, new String[]{"items"})
                    || !CommonUtil.isValidString(req.getParameter("items"))) {
                logger.info("addOrder fail: " + req);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }

            String items = req.getParameter("items");
            boolean added = OrderService.getInstance().addOrder(userId, items);

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
