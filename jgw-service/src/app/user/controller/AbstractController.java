/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.user.controller;

import app.entity.EnApiOutput;
import app.guest.service.VerifyUserService;
import app.config.ConfigApp;
import app.entity.EnApp.*;
import core.controller.ApiServlet;
import core.utilities.CommonUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author tam
 */
public abstract class AbstractController extends ApiServlet {

    private final Logger logger = Logger.getLogger(AbstractController.class);
    VerifyUserService verifyInstance = VerifyUserService.getInstance(ConfigApp.LOGIN_SECRET_KEY);

    @Override
    protected Object execute(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (!checkValidParam(req, new String[]{"token"})
                    || !CommonUtil.isValidString(req.getParameter("token"))) {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }
            String token = req.getParameter("token");
            EnUserPermission verifiedUser = verifyInstance.verifiedUser(token);
            if (verifiedUser == null) {
                logger.info("TOKEN_INVALID" + resp);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.LOGIN_TOKEN_INVALID);
            }
            if (!verifiedUser.permission.equals("user")) {
                logger.info("Permission denied" + resp);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.PERMISSION_DENY);
            }
            return doProcess(verifiedUser, req, resp);
        } catch (Exception ex) {
            logger.error("AbstractController: " + ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    protected abstract EnApiOutput doProcess(EnUserPermission verifiedUserName, HttpServletRequest req, HttpServletResponse resp);
    //To change body of generated methods, choose Tools | Templates.

}
