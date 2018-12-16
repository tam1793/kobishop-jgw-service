/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.guest.controller;

import app.entity.EnApiOutput;
import app.entity.EnApp;
import app.guest.service.ProductService;
import core.controller.ApiServlet;
import core.utilities.CommonUtil;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author tamnnq
 */
public class ProductController extends ApiServlet {

    private final Logger logger = Logger.getLogger(ProductController.class);
    private final String PATTERNT = "/(?<id>\\d+)";

    @Override
    protected EnApiOutput execute(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();

            switch (pathInfo) {
                case "":
                    return getProducts(req);
                case "/newest":
                    return getNewestProducts(req);
                default:
                    Pattern pattern = Pattern.compile(PATTERNT);
                    Matcher matcher = pattern.matcher(pathInfo);
                    String id = null;
                    if (matcher.find()) {
                        id = matcher.group("id").trim();
                    }
                    if (CommonUtil.isInteger(id)) {
                        return getProduct(Integer.parseInt(id));
                    }
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput getProducts(HttpServletRequest req) {
        try {
            if (!CommonUtil.checkValidParam(req, new String[]{"page", "productsPerPage"})
                    || !CommonUtil.isInteger(req.getParameter("page"))
                    || !CommonUtil.isInteger(req.getParameter("productsPerPage"))) {
                logger.error("getProducts - params invalid - page: " + req.getParameter("page") + " - productsPerPage: " + req.getParameter("productsPerPage"));
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }

            String productName = req.getParameter("productName");
            String brandId = req.getParameter("brandId");
            String typeId = req.getParameter("typeId");
            String priceOption = req.getParameter("priceOption");
            int page = Integer.parseInt(req.getParameter("page"));
            int productsPerPage = Integer.parseInt(req.getParameter("productsPerPage"));

            HashMap<String, Object> result = ProductService.getInstance().getListProduct(productName, brandId, typeId, priceOption, page, productsPerPage);

            if (result != null) {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, result);
            } else {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.PRODUCT_NOT_FOUND);
            }
//            return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, ProductService.getInstance().getListProduct(productName, brandId, typeId, priceOption, page, productsPerPage));
//            List<EnApp.EnProduct> rs = ProductService.getInstance().getListProduct(productName, brandId, typeId, priceOption, page, productsPerPage);
//
//            if (rs != null && !rs.isEmpty()) {
//                result.put("listProducts", rs);
//                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, result);
//            } else {
//                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.PRODUCT_NOT_FOUND);
//            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput getNewestProducts(HttpServletRequest req) {
        try {
            HashMap<String, Object> result = new HashMap<String, Object>();

            List<EnApp.EnProduct> rs = ProductService.getInstance().getNewestProducts();

            if (rs != null && !rs.isEmpty()) {
                result.put("listProducts", rs);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, result);
            } else {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.PRODUCT_NOT_FOUND);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput getProduct(Integer id) {
        try {
            HashMap<String, Object> result = new HashMap<String, Object>();

            EnApp.EnProduct rs = ProductService.getInstance().getProductbyId(id);

            if (rs != null) {
                result.put("product", rs);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, result);
            } else {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.PRODUCT_NOT_FOUND);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);

    }

}
