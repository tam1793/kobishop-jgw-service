/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.entity;

import java.sql.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author tam
 */
public class EnApp {

    private static final Logger logger = Logger.getLogger(EnApp.class.getName());

    public static class EnUserSession {

        public String token;
        public String userName;
        public String permission;

        public EnUserSession(String token, String userName, String permission) {
            this.token = token;
            this.userName = userName;
            this.permission = permission;
        }
    }

    public static class EnUserPermission {

        String userName;
        String permission;

        public EnUserPermission(String userName, String permission) {
            this.userName = userName;
            this.permission = permission;
        }

    }

    public static class EnUser {

        public String userName;
        public byte[] password;
        public String name;
        public Date dob;
        public String address;
        public String phone;

        public EnUser(String userName, byte[] password, String name, Date dob, String address, String phone) {
            this.userName = userName;
            this.password = password;
            this.name = name;
            this.dob = dob;
            this.address = address;
            this.phone = phone;
        }

    }

}
