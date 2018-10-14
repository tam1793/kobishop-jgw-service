/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package core.utilities;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.NameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CommonUtil {

    private static Logger logger = Logger.getLogger(CommonUtil.class);
    private static int timeout = 2000;

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String genUniqueId() {
        try {
            long curr = System.currentTimeMillis();
            int r = rand(1000, 9999);
            String id = md5(curr + "" + r);
            return id;
        } catch (Exception e) {
            return null;
        }
    }

    public static int rand(int min, int max) {
        try {
            Random rn = new Random();
            int range = max - min + 1;
            int randomNum = min + rn.nextInt(range);
            return randomNum;
        } catch (Exception e) {
            logger.error("Exception at random method. " + e.getMessage(), e);
            return -1;
        }
    }

    public static String getClientIp(HttpServletRequest req) {
        String clientip = "";
        try {
            if (req.getHeader("HTTP_X_FORWARDED_FOR") != null) {
                clientip = req.getHeader("HTTP_X_FORWARDED_FOR");
            } else if (req.getHeader("X-Forwarded-For") != null) {
                clientip = req.getHeader("X-Forwarded-For");
            } else if (req.getHeader("REMOTE_ADDR") != null) {
                clientip = req.getHeader("REMOTE_ADDR");
            }

            if ("".equals(clientip)) {
                clientip = req.getRemoteAddr();
            }
        } catch (Exception e) {
            logger.error("Exception at getClientIP method. " + e.getMessage(), e);
        }
        return clientip;
    }

    public static String formatNumber(int number) {
        if (number < 1000) {
            return String.valueOf(number);
        }
        try {
            NumberFormat formatter = new DecimalFormat("###,###");
            String resp = formatter.format(number);
            resp = resp.replaceAll(",", ".");
            return resp;
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatDate(Date d, String format) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            return formatter.format(d);
        } catch (Exception e) {
            logger.error("Exception at dateToString method. " + e.getMessage(), e);
            return null;
        }
    }

    public static java.sql.Date getSQLDateFromString(String dateString, String format) {
        try {
            Date date = new SimpleDateFormat(format).parse(dateString);
            return new java.sql.Date(date.getTime());
        } catch (Exception e) {
            logger.error("getDateFromString failed:" + dateString + " - format:" + format);
        }
        return null;
    }

    public static String getEncodedUserAgent(HttpServletRequest req) {
        String userAgent = "ZME";
        if (req.getHeader("User-Agent") != null) {
            userAgent = req.getHeader("User-Agent");
        }
        userAgent = md5(userAgent);
        userAgent = userAgent.toUpperCase();
        return userAgent;
    }

    public static JSONObject parseJSONObject(String value) {
        JSONObject ret = null;
        try {
            JSONParser parser = new JSONParser();
            ret = (JSONObject) parser.parse(value);
        } catch (ParseException e) {
            logger.warn("Error at parseJSONObject. " + e.getMessage() + " : " + value);
        }
        return ret;
    }

    public static JSONArray parseJSONArray(String value) {
        JSONArray ret = null;
        try {
            JSONParser parser = new JSONParser();
            ret = (JSONArray) parser.parse(value);
        } catch (ParseException e) {
            logger.warn("Error at parseJSONArray. " + e.getMessage() + " : " + value, e);
        }
        return ret;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String toYesterday() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isValidString(String s) {
        try {
            if (s != null && !s.isEmpty()) {
                return true;
            }
        } catch (Exception ex) {
        }
        return false;
    }

    public static boolean isJsonObject(String s) {
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(s);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static <T> T jsonToObject(String json, Class<T> cls) {
        try {
            Object fromJson = new GsonBuilder().serializeNulls().create().fromJson(json, cls);
            if (cls.isInstance(fromJson)) {
                return (T) fromJson;
            }
        } catch (JsonSyntaxException ex) {
            logger.warn(ex.getMessage(), ex);
        }
        return null;
    }

    public static JSONObject toJson(Object object) {
        return parseJSONObject(new Gson().toJson(object));
    }

    public static org.json.JSONObject convertNVPToJson(List<NameValuePair> nVPL) throws JSONException {
        org.json.JSONObject json = new org.json.JSONObject();
        for (NameValuePair pair : nVPL) {
            json.put(pair.getName(), pair.getValue());
        }
        return json;
    }

    public static boolean checkValidStringParams(String[] params) {
        try {
            if (params == null) {
                return false;
            }
            for (String param : params) {
                if (Strings.isNullOrEmpty(param)) {
                    return false;
                }
            }
            return true;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return false;
    }

    public static String objectToString(Object obj) {
        try {
            return new GsonBuilder().serializeNulls().create().toJson(obj);
        } catch (Exception ex) {
            return null;
        }
    }

    public static boolean checkValidParam(HttpServletRequest request, String[] params) {
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

        for (int i = 0; i < params.length; i++) {
            if (!listParam.contains(params[i])) {
                return false;
            }
        }
        return true;
    }

    public static String curl(String urlStr, String method, String param) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlStr);
            String proxyHost = System.getProperty("http.proxyHost");
            String proxyPort = System.getProperty("http.proxyPort");

            if (proxyHost != null && proxyPort != null) {
                try {
                    int port = Integer.parseInt(proxyPort);
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, port));
                    conn = (HttpURLConnection) url.openConnection(proxy);
                } catch (Exception e) {
                    logger.error("Proxy " + proxyHost + " didn't works!. " + e.getMessage(), e);
                    conn = (HttpURLConnection) url.openConnection();
                }
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }

            conn.setReadTimeout(15000); // Milliseconds
            conn.setConnectTimeout(15000); // Milliseconds
            conn.setRequestMethod(method);
            conn.setDoOutput(true); // Triggers POST.

            if (method.equals("POST")) {
                try (OutputStream output = conn.getOutputStream()) {
                    output.write(param.getBytes());
                }
            }

            int code = conn.getResponseCode();
            StringBuilder sb;
            try (BufferedReader rd = new BufferedReader(
                    new InputStreamReader(conn.getResponseCode() / 100 == 2
                            ? conn.getInputStream() : conn.getErrorStream()))) {
                sb = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    if (!line.isEmpty()) {
                        sb.append(line);
                    }
                }
            }

            logger.info("Curl: " + url + " - Param: " + param + " - Code: " + code + " - Result: " + sb.toString());
            return sb.toString();

        } catch (Exception e) {
            logger.error("Error at sendPostRequest. " + urlStr + " - Param: " + param + " - " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

}
