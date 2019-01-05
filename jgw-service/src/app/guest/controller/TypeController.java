/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.guest.controller;

import app.entity.EnApiOutput;
import app.entity.EnApp;
import app.guest.service.TypeService;
import core.controller.ApiServlet;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Lenovo
 */
public class TypeController extends ApiServlet {

    private final Logger logger = Logger.getLogger(TypeController.class);

    @Override
    protected EnApiOutput execute(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();

            switch (pathInfo) {
                case "":
                    return getAllType();
                default:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput getAllType() {
        HashMap<String, Object> result = new HashMap<String, Object>();

        List<EnApp.EnType> rs = TypeService.getInstance().getTypeList();
        if (rs!=null && !rs.isEmpty()) {
            result.put("listType", rs);
            return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, result);
        } else {
            return new EnApiOutput(EnApiOutput.ERROR_CODE_API.TYPE_NOT_FOUND);
        }
    }

}
