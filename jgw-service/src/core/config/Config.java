/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.config;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.HierarchicalINIConfiguration;

/**
 *
 * @author tam
 */
public class Config {

    private static CompositeConfiguration configIni = null;
    private static ConcurrentHashMap<String, String> hashConfig = null;

    static {
        init();
    }

    private static synchronized void init() {

        try {
            String iniFile = System.getProperty("apppath") + "/conf/conf.ini";
            System.out.println(iniFile);
            configIni = new CompositeConfiguration();
            hashConfig = new ConcurrentHashMap();
            configIni.addConfiguration(new HierarchicalINIConfiguration(iniFile));
        } catch (Exception ex) {
            System.err.println("Please check file path or VM");
        }
    }

    public static String getParam(String section, String name) {
        String key = section + "." + name;
        if (hashConfig.contains(key)) {
            return (String) hashConfig.get(key);
        }
        String value = configIni.getString(section + "." + name);
        if (value != null) {
            hashConfig.put(key, value);
        }
        return value;
    }
}
