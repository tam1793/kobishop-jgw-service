/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.admin.controller;

import app.entity.EnApiOutput;
import app.guest.service.VerifyUserNameService;
import app.user.service.PermissionService;
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
public abstract class AbstractAdminController extends ApiServlet {

    private final Logger logger = Logger.getLogger(AbstractAdminController.class);
    VerifyUserNameService verifyInstance = VerifyUserNameService.getInstance(ConfigApp.LOGIN_SECRET_KEY);

    @Override
    protected Object execute(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (!checkValidParam(req, new String[]{"token"})
                    || !CommonUtil.isValidString(req.getParameter("token"))) {
                return new EnApiOutput(null, EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }
            String token = req.getParameter("token");
            EnUserPermission verifiedUserName = verifyInstance.verifiedUserName(token);
            if (verifiedUserName == null) {
                logger.info("TOKEN_INVALID" + resp);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.LOGIN_TOKEN_INVALID);
            }
            if (!verifiedUserName.permission.equals("admin")) {
                logger.info("Permission denied" + resp);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.PERMISSION_DENY);
            }
            return doProcess(req, resp);

        } catch (Exception ex) {
            logger.error("AbstractAdminController: " + ex.getMessage(), ex);
        }
        return new EnApiOutput(null, EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    protected abstract EnApiOutput doProcess(HttpServletRequest req, HttpServletResponse resp);
    //To change body of generated methods, choose Tools | Templates.

}
