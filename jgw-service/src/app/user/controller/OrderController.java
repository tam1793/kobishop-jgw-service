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
                    return getOrders(req, resp);
                default:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new EnApiOutput(null, EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput getOrders(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (!checkValidParam(req, new String[]{"orderId"})
                    || !CommonUtil.isInteger(req.getParameter("orderId"))) {
                logger.info("getOrders fail: " + req.getParameter("orderId"));
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }

            String orderId = req.getParameter("orderId");

            List<EnApp.EnOrder> resultOrders = OrderService.getInstance().getOrders(Integer.parseInt(orderId));
            if (!resultOrders.isEmpty()) {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, resultOrders);
            } else {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.ORDER_NOT_FOUND);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);

    }

}
