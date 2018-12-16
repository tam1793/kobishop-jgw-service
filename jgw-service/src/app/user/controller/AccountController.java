/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.user.controller;

import app.entity.EnApiOutput;
import app.entity.EnApp;
import app.user.service.AccountService;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

/**
 *
 * @author Lenovo
 */
public class AccountController extends AbstractController {

    private final Logger logger = Logger.getLogger(AccountController.class);

    @Override
    protected EnApiOutput doProcess(EnApp.EnUserPermission verifiedUserName, HttpServletRequest req, HttpServletResponse resp) {
        try {
            String pathInfo = req.getPathInfo() == null ? "" : req.getPathInfo();

            switch (pathInfo) {
                case "/getInfo":
                    return getInfo(verifiedUserName.userId);
                default:
                    return new EnApiOutput(EnApiOutput.ERROR_CODE_API.UNSUPPORTED_ERROR);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);
    }

    private EnApiOutput getInfo(int userId) {
        try {
            HashMap<String, Object> result = new HashMap<String, Object>();

            List<EnApp.EnAccountInfo> resultInfo = AccountService.getInstance().getInfo(userId);
            if (resultInfo!=null && !resultInfo.isEmpty()) {
                result.put("info", resultInfo);
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SUCCESS, result);
            } else {
                return new EnApiOutput(EnApiOutput.ERROR_CODE_API.ORDER_NOT_FOUND);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new EnApiOutput(EnApiOutput.ERROR_CODE_API.SERVER_ERROR);

    }

}
