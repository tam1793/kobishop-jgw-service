/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.controller;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import core.utilities.CommonUtil;

/**
 *
 * @author huuloc.tran89
 */
public abstract class ApiServlet extends BaseServlet {

    public static final Logger logger = Logger.getLogger(ApiServlet.class);

    protected abstract Object execute(HttpServletRequest req, HttpServletResponse resp);

    protected boolean checkSigKey(String secretKey, String sigKey, Object[] params) {
        try {
            StringBuilder s = new StringBuilder();
            s.append(secretKey);
            for (Object param : params) {
                s = s.append(param);
            }
            String raw = s.toString();
            String md5Sum = CommonUtil.md5(raw);
            logger.info("raw: " + s.toString() + " - md5: " + md5Sum + " - sig: " + sigKey);
            return md5Sum.equals(sigKey);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return false;
    }

    protected boolean checkSigKey(String secretKey, String sigKey, Object data) {
        try {
            String raw = secretKey + CommonUtil.objectToString(data);
            String md5Sum = CommonUtil.md5(raw);
            logger.info("raw: " + raw + " - md5: " + md5Sum + " - sig: " + sigKey);
            return md5Sum.equals(sigKey);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return false;
    }

    protected boolean checkValidParam(HttpServletRequest request, String[] params) {
        if (request == null || params == null) {
            return false;
        }

        if (params.length == 0) {
            return true;
        }

        List<String> listParam = new LinkedList<>();
        Enumeration<String> enumParams = request.getParameterNames();
        while (enumParams.hasMoreElements()) {
            String paramName = enumParams.nextElement();
            if (request.getParameter(paramName) != null && !request.getParameter(paramName).isEmpty()) {
                listParam.add(paramName);
            }
        }

        for (String param : params) {
            if (!listParam.contains(param)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handle(req, resp);
    }

    private void handle(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RequestWrapper wrappedRequest = new RequestWrapper(req);
        HttpServletRequest request = wrappedRequest;

        String pathInfo = (request.getPathInfo() == null) ? "" : request.getPathInfo();

        // log stat
        long start = System.currentTimeMillis();

        // do output
        Object object = execute(request, resp);
        this.out(new Gson().toJson(object), request, resp, "json");

    }

}
