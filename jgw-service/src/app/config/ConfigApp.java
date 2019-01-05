/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.config;

import core.config.Config;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author tam
 */
public class ConfigApp {

    //web_server
    public static String WEB_SERVER_HOST;
    public static Integer WEB_SERVER_PORT;
    public static String WEB_SERVER_CONTEXT_PATH;
    public static String LOGIN_SECRET_KEY;

    //database
    public static String MYSQL_HOST;
    public static String MYSQL_PORT;
    public static String MYSQL_DBNAME;
    public static String MYSQL_USER;
    public static String MYSQL_PASSWORD;

    public ConfigApp() {
        WEB_SERVER_HOST = Config.getParam("web_server", "host");
        WEB_SERVER_PORT = Integer.parseInt(Config.getParam("web_server", "port"));
        WEB_SERVER_CONTEXT_PATH = Config.getParam("web_server", "context_path");
        LOGIN_SECRET_KEY = Config.getParam("web_server", "secret_key");

        MYSQL_HOST = Config.getParam("mysql", "mysql_host");
        MYSQL_PORT = Config.getParam("mysql", "mysql_port");
        MYSQL_DBNAME = Config.getParam("mysql", "mysql_dbname");
        MYSQL_USER = Config.getParam("mysql", "mysql_user");
        MYSQL_PASSWORD = Config.getParam("mysql", "mysql_password");

    }

    public static void init() {
        String log4jFile = System.getProperty("apppath") + "/conf/log4j.ini";

        PropertyConfigurator.configure(log4jFile);
        new ConfigApp();
    }
}
