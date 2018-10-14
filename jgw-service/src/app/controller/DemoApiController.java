/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.controller;

import app.admin.controller.AbstractAdminController;
import app.entity.EnApiOutput;
import app.entity.EnApiOutput.ERROR_CODE_API;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import core.controller.ApiOutput;
import core.controller.ApiServlet;
import core.utilities.CommonUtil;
import app.service.DemoService;
import org.apache.log4j.Logger;

/**
 *
 * @author tamnnq
 */
public class DemoApiController extends ApiServlet {

    private final Logger logger = Logger.getLogger(AbstractAdminController.class);

    @Override
    protected ApiOutput execute(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String action = req.getParameter("action");
            if (!CommonUtil.isValidString(action)) {
                logger.info("TEST LOG");
                logger.info("TEST LOG");
                logger.info("TEST LOG");
                logger.info("TEST LOG");

                return new EnApiOutput(ERROR_CODE_API.ACTION_INVALID);
            }
            switch (action) {
                case "plus":
                    return plusService(req, resp);
                case "mult":
                    return multService(req, resp);
                default:
                    return new EnApiOutput(ERROR_CODE_API.UNSUPPORTED_ERROR);
            }
        } catch (Exception e) {
            logger.info("TEST LOG");

            return new EnApiOutput(ERROR_CODE_API.SERVER_ERROR);
        }
    }

    private ApiOutput plusService(HttpServletRequest req, HttpServletResponse resp) {
        if (!checkValidParam(req, new String[]{"number1", "number2"})
                || !CommonUtil.isInteger(req.getParameter("number1"))
                || !CommonUtil.isInteger(req.getParameter("number2"))) {
            return new EnApiOutput(ERROR_CODE_API.INVALID_DATA_INPUT);// nếu k có sẽ return lại INVALID_DATA_INPUT
        }
        int number1 = Integer.parseInt(req.getParameter("number1"));
        int number2 = Integer.parseInt(req.getParameter("number2"));

        int result = DemoService.getInstance().PlusService(number1, number2);
//        int resulasd = DemoService.getInstance().
        return new EnApiOutput(ERROR_CODE_API.SUCCESS, result);
    }

    private ApiOutput multService(HttpServletRequest req, HttpServletResponse resp) {
        if (!checkValidParam(req, new String[]{"number1", "number2"})
                || !CommonUtil.isInteger(req.getParameter("number1"))
                || !CommonUtil.isInteger(req.getParameter("number2"))) {
            return new EnApiOutput(ERROR_CODE_API.INVALID_DATA_INPUT);
        }
        int number1 = Integer.parseInt(req.getParameter("number1"));
        int number2 = Integer.parseInt(req.getParameter("number2"));

        int result = DemoService.getInstance().MultService(number1, number2);
        return new EnApiOutput(ERROR_CODE_API.SUCCESS, result);
    }
}
