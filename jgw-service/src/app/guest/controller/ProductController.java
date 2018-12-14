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
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author tamnnq
 */
public class ProductController extends ApiServlet {

    private final Logger logger = Logger.getLogger(ProductController.class);

    @Override
    protected EnApiOutput execute(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();

            switch (pathInfo) {
                case "":
                    return getAllProduct(req);
                default:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput getAllProduct(HttpServletRequest req) {
//        List<EnApp.EnProduct> rs = ProductService.getInstance().getListProduct();
        if (!CommonUtil.checkValidParam(req, new String[]{"page", "productsPerPage"})
                || !CommonUtil.isInteger(req.getParameter("page"))
                || !CommonUtil.isInteger("productsPerPage")) {
            return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
        }
        String productName = req.getParameter("productName");
        String brandId = req.getParameter("brandId");
        String typeId = req.getParameter("typeId");
        String priceOption = req.getParameter("priceOption");
        int page = Integer.parseInt(req.getParameter("page"));
        int productsPerPage = Integer.parseInt(req.getParameter("productsPerPage"));

        List<EnApp.EnProduct> rs = ProductService.getInstance().getListProduct(productName, brandId, typeId, priceOption, page, productsPerPage);

        if (!rs.isEmpty()) {
            return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, rs);
        } else {
            return new EnApiOutput(EnApiOutput.ERROR_CODE_API.PRODUCT_NOT_FOUND);
        }
    }

}
