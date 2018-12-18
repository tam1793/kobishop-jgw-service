/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.admin.controller;

import app.admin.service.ProductService;
import app.entity.EnApiOutput;
import core.utilities.CommonUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Lenovo
 */
public class ProductController extends AbstractAdminController {

    private final Logger logger = Logger.getLogger(ProductController.class);

    @Override
    protected EnApiOutput doProcess(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();

            switch (pathInfo) {
                case "/addProduct":
                    return addProduct(req, resp);
                case "/modifyProduct":
                    return modifyProduct(req,resp);
                default:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }
    
    private EnApiOutput addProduct(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (!checkValidParam(req, new String[]{"typeId", "brandId", "name", "description", "price","soldItems","leftItems","specs"})
                    || !CommonUtil.isInteger((req.getParameter("typeId")))
                    || !CommonUtil.isInteger(req.getParameter("brandId"))
                    || !CommonUtil.isValidString(req.getParameter("name"))
                    || !CommonUtil.isValidString(req.getParameter("description"))
                    || !CommonUtil.isInteger((req.getParameter("price")))
                    || !CommonUtil.isInteger(req.getParameter("soldItems"))
                    || !CommonUtil.isInteger((req.getParameter("leftItems")))
                    || !CommonUtil.isValidString(req.getParameter("specs"))) {
                logger.info("addProduct fail: " + req);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }

            String typeId = req.getParameter("typeId");
            String brandId = req.getParameter("brandId");
            String name = req.getParameter("name");
            String description = req.getParameter("description");
            String price = req.getParameter("price");
            String soldItems = req.getParameter("soldItems");
            String leftItems = req.getParameter("leftItems");
            String specs = req.getParameter("specs"); 

            int result = ProductService.getInstance().addProduct(Integer.parseInt(typeId),Integer.parseInt(brandId),name, description, Integer.parseInt(price), Integer.parseInt(soldItems), Integer.parseInt(leftItems),specs);
            switch (result) {
                case 1:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS);
                case 0:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
                case -1:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }
    
    private EnApiOutput modifyProduct(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (!checkValidParam(req, new String[]{"productId","typeId", "brandId", "name", "description", "price","soldItems","leftItems","specs","isDeleted"})
                    || !CommonUtil.isInteger((req.getParameter("productId")))
                    || !CommonUtil.isInteger((req.getParameter("typeId")))
                    || !CommonUtil.isInteger(req.getParameter("brandId"))
                    || !CommonUtil.isValidString(req.getParameter("name"))
                    || !CommonUtil.isValidString(req.getParameter("description"))
                    || !CommonUtil.isInteger((req.getParameter("price")))
                    || !CommonUtil.isInteger(req.getParameter("soldItems"))
                    || !CommonUtil.isInteger((req.getParameter("leftItems")))
                    || !CommonUtil.isInteger((req.getParameter("isDeleted")))
                    || !CommonUtil.isValidString(req.getParameter("specs"))) {
                logger.info("modifyProduct fail: " + req);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }

            int productId = Integer.parseInt(req.getParameter("productId"));
            int typeId = Integer.parseInt(req.getParameter("typeId"));
            int brandId = Integer.parseInt(req.getParameter("brandId"));
            String name = req.getParameter("name");
            String description = req.getParameter("description");
            int price = Integer.parseInt(req.getParameter("price"));
            int soldItems = Integer.parseInt(req.getParameter("soldItems"));
            int leftItems = Integer.parseInt(req.getParameter("leftItems"));
            String specs = req.getParameter("specs"); 
            int isDeleted = Integer.parseInt(req.getParameter("isDeleted"));

            int result = ProductService.getInstance().modifyProduct(productId,typeId,brandId,name, description, price, soldItems, leftItems,specs,isDeleted);
            switch (result) {
                case 1:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS);
                case 0:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
                case -1:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }
}
