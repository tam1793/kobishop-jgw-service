/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.guest.controller;

import app.entity.EnApiOutput;
import app.entity.EnApp;
import app.guest.service.BrandService;
import core.controller.ApiServlet;
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
public class BrandController extends ApiServlet {

    private final Logger logger = Logger.getLogger(BrandController.class);

    @Override
    protected EnApiOutput execute(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();

            switch (pathInfo) {
                case "":
                    return getAllBrand();
                default:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput getAllBrand() {
        List<EnApp.EnBrand> rs = BrandService.getInstance().getBrandList();
        HashMap<String, Object> result = new HashMap<String, Object>();
        if (rs!=null && !rs.isEmpty()) {
            result.put("listBrands", rs);
            return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, result);
        } else {
            return new EnApiOutput(EnApiOutput.ERROR_CODE_API.BRAND_NOT_FOUND);
        }
    }

}
