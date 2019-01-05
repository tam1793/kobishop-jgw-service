/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.guest.controller;

import app.entity.EnApiOutput;
import app.entity.EnApp.EnUser;
import app.guest.service.LoginService;
import app.guest.service.VerifyUserService;
import app.config.ConfigApp;
import app.entity.EnApp.*;
import core.controller.ApiServlet;
import core.utilities.CommonUtil;
import java.security.MessageDigest;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author tam
 */
public class LoginController extends ApiServlet {

    private final Logger logger = Logger.getLogger(LoginController.class);
    VerifyUserService verifyInstance = VerifyUserService.getInstance(ConfigApp.LOGIN_SECRET_KEY);

    @Override
    protected EnApiOutput execute(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();

            switch (pathInfo) {
                case "/login":
                    return login(req, resp);
                case "/checkPermission":
                    return checkPermission(req, resp);
                case "/checkUser":
                    return checkUser(req, resp);
                case "/register":
                    return createUser(req, resp);
                default:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput login(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (!checkValidParam(req, new String[]{"username", "password"})
                    || !CommonUtil.isValidString(req.getParameter("username"))
                    || !CommonUtil.isValidString(req.getParameter("password"))) {
                logger.info("login fail: " + req);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }

            String userName = req.getParameter("username");
            byte[] password = req.getParameter("password").getBytes("UTF-8");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] encrypt = md5.digest(password);

            return LoginService.getInstance(ConfigApp.LOGIN_SECRET_KEY).createSession(userName, encrypt);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput checkPermission(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (!checkValidParam(req, new String[]{"token"})
                    || !CommonUtil.isValidString(req.getParameter("token"))) {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }
            HashMap<String, Object> result = new HashMap<String, Object>();

            String token = req.getParameter("token");
            EnUserPermission verifiedUser = verifyInstance.verifiedUser(token);
            if (verifiedUser == null) {
                logger.info("LOGIN_TOKEN_INVALID" + resp);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.LOGIN_TOKEN_INVALID);
            }
            result.put("user", verifiedUser);
            return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, result);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput createUser(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (!checkValidParam(req, new String[]{"username", "password", "name", "email"})
                    || !CommonUtil.isValidString(req.getParameter("username"))
                    || !CommonUtil.isValidString(req.getParameter("password"))
                    || !CommonUtil.isValidString(req.getParameter("name"))
                    || !CommonUtil.isValidString(req.getParameter("email"))) {
                logger.info("createUser fail: " + req);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }

            String userName = req.getParameter("username");
            boolean checkUserName = LoginService.getInstance(ConfigApp.LOGIN_SECRET_KEY).checkUser(userName);
            if (checkUserName == true) {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.USER_EXIST);
            }
            byte[] password = req.getParameter("password").getBytes("UTF-8");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] encrypt = md5.digest(password);

            String name = req.getParameter("name");
            String email = req.getParameter("email");

            EnUser newUser = new EnUser(userName, encrypt, name, email);
            int resultUser = LoginService.getInstance(ConfigApp.LOGIN_SECRET_KEY).createUser(newUser);
            if (resultUser == 0) {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            } else if (resultUser == -1) {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
            }

            return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput checkUser(HttpServletRequest req, HttpServletResponse resp) {
        try {
            if (!checkValidParam(req, new String[]{"username"})
                    || !CommonUtil.isValidString(req.getParameter("username"))) {
                logger.info("check User fail: " + req);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.INVALID_DATA_INPUT);
            }
            HashMap<String, Object> result = new HashMap<String, Object>();

            String userName = req.getParameter("username");
            boolean check = LoginService.getInstance(ConfigApp.LOGIN_SECRET_KEY).checkUser(userName);
            result.put("checkUser", check);
            return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, result);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }
}
