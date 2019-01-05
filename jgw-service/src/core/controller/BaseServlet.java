/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;


public class BaseServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(BaseServlet.class);

    public BaseServlet() {
        super();
    }

    protected void out(String content, HttpServletRequest req, HttpServletResponse resp, String type) {
        try {
            resp.setCharacterEncoding("utf-8");
            resp.addHeader("Access-Control-Allow-Origin", "*");
            if ("json".equalsIgnoreCase(type)) {
                resp.addHeader("Content-Type", "application/json; charset=utf-8");
            } else if ("plain".equalsIgnoreCase(type)) {
                resp.addHeader("Content-Type", "text/plain; charset=utf-8");
            } else {
                resp.addHeader("Content-Type", "text/html; charset=utf-8");
            }

            logRequestWithResponse(req, content);

            try (PrintWriter os = resp.getWriter()) {
                os.write(content);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }

        } catch (Exception ex) {
            logger.error("BaseServlet.out", ex);
        }
    }

    protected void logRequestWithResponse(HttpServletRequest req, String resp) throws UnsupportedEncodingException, JSONException, IOException {
        JSONObject log = new JSONObject();
        JSONObject reqJson = new JSONObject();

        reqJson.put("URI", req.getRequestURI());
        reqJson.put("Query", req
                .getQueryString() != null ? "?" + req.getQueryString().replace("\\", "") : "");
        reqJson.put("Body", IOUtils.toString(req.getReader()));
        reqJson.put("IP", req.getRemoteAddr());
        reqJson.put("ForwardedIP", req.getHeader("X-FORWARDED-FOR"));
        reqJson.put("Agent", req.getHeader("User-Agent") != null ? req.getHeader("User-Agent").replace("\\", "") : "");

        log.put("ts", System.currentTimeMillis());
        log.put("request", reqJson);
        log.put("response", resp);

        logger.info("logRequestWithResponse: " + log.toString());
    }

    protected void logRequest(HttpServletRequest req) throws UnsupportedEncodingException, JSONException, IOException {
        JSONObject log = new JSONObject();
        JSONObject request = new JSONObject();
        request.put("URI", req.getRequestURI());
        request.put("Query", req
                .getQueryString() != null ? "?" + req.getQueryString().replace("\\", "") : "");
        request.put("Body", IOUtils.toString(req.getReader()));
        request.put("IP", req.getRemoteAddr());
        request.put("ForwardedIP", req.getHeader("X-FORWARDED-FOR"));
        request.put("Agent", req.getHeader("User-Agent") != null ? req.getHeader("User-Agent").replace("\\", "") : "");

        log.put("ts", System.currentTimeMillis());
        log.put("request", request);

        logger.info("logRequest: " + log.toString());
    }

    public class RequestWrapper extends HttpServletRequestWrapper {

        private String _body;
        private Map<String, String[]> allParameters = null;

        public RequestWrapper(HttpServletRequest request) throws IOException {
            super(request);

            // parametter
            allParameters = new HashMap<>();

            // body
            _body = "";
            BufferedReader bufferedReader = request.getReader();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                _body += line;
            }

            //
            try {
                List<NameValuePair> params = URLEncodedUtils.parse(_body, Charset.forName("UTF-8"));
                params.forEach((param) -> {
                    addParameter(param.getName(), param.getValue());
                });
            } catch (Exception ex) {
            }
        }

        public void addParameter(String name, String value) {

            String[] values = allParameters.get(name);
            if (values == null) {
                values = new String[0];
            }
            List<String> list = new ArrayList<>(values.length + 1);
            list.addAll(Arrays.asList(values));
            list.add(value);
            allParameters.put(name, (String[]) list.toArray(new String[0]));
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(_body.getBytes());
            return new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    return byteArrayInputStream.read();
                }

                @Override
                public boolean isFinished() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public boolean isReady() {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void setReadListener(ReadListener rl) {
                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }
            };
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(this.getInputStream()));
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            if (allParameters == null || allParameters.isEmpty()) {
                allParameters.putAll(super.getParameterMap());
            }

            return Collections.unmodifiableMap(allParameters);
        }

        @Override
        public String getParameter(final String name) {
            String[] strings = getParameterMap().get(name);
            if (strings != null) {
                return strings[0];
            }
            return null;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            if (allParameters == null || allParameters.isEmpty()) {
                allParameters.putAll(super.getParameterMap());
            }
            return Collections.enumeration(allParameters.keySet());
        }

        @Override
        public String[] getParameterValues(String name) {
            if (allParameters == null || allParameters.isEmpty()) {
                allParameters.putAll(super.getParameterMap());
            }
            return allParameters.get(name);
        }
    }
}
