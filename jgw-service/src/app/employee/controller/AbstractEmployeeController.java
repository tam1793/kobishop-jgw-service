/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.employee.controller;

import app.config.ConfigApp;
import app.entity.EnApiOutput;
import app.entity.EnApp;
import app.guest.service.VerifyUserNameService;
import core.controller.ApiServlet;
import core.utilities.CommonUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Lenovo
 */
public abstract class AbstractEmployeeController extends ApiServlet{
    private final Logger logger = Logger.getLogger(AbstractEmployeeController.class);
    VerifyUserNameService verifyInstance = VerifyUserNameService.getInstance(ConfigApp.LOGIN_SECRET_KEY);

    @Override
    protected Object execute(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (!checkValidParam(req, new String[]{"token"})
                    || !CommonUtil.isValidString(req.getParameter("token"))) {
                return new EnApiOutput(null, EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }
            String token = req.getParameter("token");
            EnApp.EnUserPermission verifiedUserName = verifyInstance.verifiedUserName(token);
            if (verifiedUserName == null) {
                logger.info("TOKEN_INVALID" + resp);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.LOGIN_TOKEN_INVALID);
            }
            return doProcess(verifiedUserName,req, resp);

        } catch (Exception ex) {
            logger.error("AbstractEmployeeController: " + ex.getMessage(), ex);
        }
        return new EnApiOutput(null, EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    protected abstract EnApiOutput doProcess(EnApp.EnUserPermission verifiedUserName,HttpServletRequest req, HttpServletResponse resp);
    //To change body of generated methods, choose Tools | Templates.
    
}
