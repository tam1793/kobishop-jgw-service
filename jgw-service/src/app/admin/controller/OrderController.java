/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.admin.controller;

import app.admin.service.OrderService;
import app.entity.EnApiOutput;
import core.utilities.CommonUtil;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Lenovo
 */
public class OrderController extends AbstractAdminController {

    private final Logger logger = Logger.getLogger(OrderController.class);

    @Override
    protected EnApiOutput doProcess(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();

            switch (pathInfo) {
                case "/getOrdersByRange":
                    return getOrdersByRange(req);
                default:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }
    
    private EnApiOutput getOrdersByRange(HttpServletRequest req) {
        try {
            if (!CommonUtil.checkValidParam(req, new String[]{"page","ordersPerPage","from", "to"})
                    || !CommonUtil.isValidString(req.getParameter("from"))
                    || !CommonUtil.isValidString(req.getParameter("page"))
                    || !CommonUtil.isInteger(req.getParameter("page"))
                    || !CommonUtil.isInteger(req.getParameter("ordersPerPage"))) {
                logger.error("getOrdersByRange - params invalid");
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }
            int page = Integer.parseInt(req.getParameter("page"));
            int ordersPerPage = Integer.parseInt(req.getParameter("ordersPerPage"));
            if (page < 1 || ordersPerPage < 1) {
                logger.error("check param page & ordersPerPage invalid - page: " + page + " - ordersPerPage: " + ordersPerPage);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }
            String from = req.getParameter("from");
            String to = req.getParameter("to");
            
            HashMap<String, Object> resultOrder = OrderService.getInstance().getOrdersByRange(page,ordersPerPage,from,to);
            
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
}
