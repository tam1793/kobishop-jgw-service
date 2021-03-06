/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.admin.controller;

import app.admin.service.AccountService;
import app.entity.EnApiOutput;
import app.guest.service.LoginService;
import app.config.ConfigApp;
import app.entity.EnApp;
import core.utilities.CommonUtil;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author tam
 */
public class AccountController extends AbstractAdminController {

    private final Logger logger = Logger.getLogger(AccountController.class);

    @Override
    protected EnApiOutput doProcess(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();

            switch (pathInfo) {
                case "/insertAdmin":
                    return insertAdmin(req, resp);
                case "/getAccounts":
                    return getAccounts(req);
                case "/modifyAccountRole":
                    return modifyAccountRole(req,resp);
                default:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput insertAdmin(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (!checkValidParam(req, new String[]{"username", "password", "name", "role", "email"})
                    || !CommonUtil.isValidString(req.getParameter("username"))
                    || !CommonUtil.isValidString(req.getParameter("password"))
                    || !CommonUtil.isValidString(req.getParameter("name"))
                    || !CommonUtil.isValidString(req.getParameter("role"))
                    || !CommonUtil.isValidString(req.getParameter("email"))) {
                logger.info("insertAdmin fail: " + req);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }

            String userName = req.getParameter("username");
            boolean checkUserName = LoginService.getInstance(ConfigApp.LOGIN_SECRET_KEY).checkUser(userName);
            if (checkUserName == true) {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.USER_EXIST);
            }

            byte[] password = req.getParameter("password").getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] encrypt = md5.digest(password);

            String name = req.getParameter("name");
            String role = req.getParameter("role");
            String email = req.getParameter("email");

            int resultUser = AccountService.getInstance().insertByAdmin(userName, encrypt, name, role, email);
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
    
    private EnApiOutput getAccounts(HttpServletRequest req) {
        try {
            if (!CommonUtil.checkValidParam(req, new String[]{"page", "accountsPerPage"})
                    || !CommonUtil.isInteger(req.getParameter("page"))
                    || !CommonUtil.isInteger(req.getParameter("accountsPerPage"))) {
                logger.error("getAccount - params invalid - page: " + req.getParameter("page") + " - accountsPerPage: " + req.getParameter("accountsPerPage"));
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }
            
            int page = Integer.parseInt(req.getParameter("page"));
            int accountsPerPage = Integer.parseInt(req.getParameter("accountsPerPage"));
            String username = req.getParameter("username");
            if (page < 1 || accountsPerPage < 1) {
                logger.error("check param page & accountsPerPage invalid - page: " + page + " - accountsPerPage: " + accountsPerPage);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }
            
            HashMap<String, Object> resultAccount = AccountService.getInstance().getAccount(page,accountsPerPage,username);
            
            if (resultAccount!=null) {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, resultAccount);
            } else {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);

    }
    
    private EnApiOutput modifyAccountRole(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (!checkValidParam(req, new String[]{"userId","role"})
                    || !CommonUtil.isInteger(req.getParameter("userId"))
                    || !CommonUtil.isValidString(req.getParameter("role"))) {
                logger.info("modifyAccount fail: " + req);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }

            String userId = req.getParameter("userId");
            String role = req.getParameter("role");
            boolean updated = AccountService.getInstance().modifyAccountRole(Integer.parseInt(userId), role);

            if (updated) {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS);
            } else {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }


}
