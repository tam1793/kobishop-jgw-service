/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.admin.controller;

import app.admin.service.AccountService;
import app.entity.EnApiOutput;
import app.guest.controller.LoginController;
import app.guest.service.LoginService;
import app.config.ConfigApp;
import core.utilities.CommonUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author tam
 */
public class AccountController extends AbstractAdminController {

    private final Logger logger = Logger.getLogger(LoginController.class);

    @Override
    protected EnApiOutput doProcess(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();

            switch (pathInfo) {
                case "/insertAdmin":
                    return insertAdmin(req, resp);
                default:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new EnApiOutput(null, EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput insertAdmin(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (!checkValidParam(req, new String[]{"username", "password", "name", "role"})
                    || !CommonUtil.isValidString(req.getParameter("username"))
                    || !CommonUtil.isValidString(req.getParameter("password"))
                    || !CommonUtil.isValidString(req.getParameter("name"))
                    || !CommonUtil.isValidString(req.getParameter("role"))) {
                logger.info("insertAdmin fail: " + req);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }

            String userName = req.getParameter("username");
            boolean checkUserName = LoginService.getInstance(ConfigApp.LOGIN_SECRET_KEY).checkUser(userName);
            if (checkUserName == true) {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.USER_EXIST);
            }

            byte[] password = req.getParameter("password").getBytes();

            String name = req.getParameter("name");
            String role = req.getParameter("role");

            int resultUser = AccountService.getInstance().insertByAdmin(userName, password, name, role);
            switch (resultUser) {
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
