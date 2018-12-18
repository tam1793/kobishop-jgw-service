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

        public Integer userId;
        public String userName;
        public String permission;

        public EnUserPermission(Integer userId, String userName, String permission) {
            this.userId = userId;
            this.userName = userName;
            this.permission = permission;
        }

    }

    public static class EnUser {

        public String userName;
        public byte[] password;
        public String name;
        public String dob;
        public String address;
        public String phone;
        public String email;

        public EnUser(String userName, byte[] password, String name, String dob, String address, String phone, String email) {
            this.userName = userName;
            this.password = password;
            this.name = name;
            this.dob = dob;
            this.address = address;
            this.phone = phone;
            this.email = email;
        }

        public EnUser(String userName, byte[] password, String name, String email) {
            this.userName = userName;
            this.password = password;
            this.name = name;
            this.email = email;
        }

    }

    public static class EnProduct {

        public int id;
        public int typeId;
        public int brandId;
        public String name;
        public String description;
        public int price;
        public int soldItems;
        public int leftItems;
        public String specs;
        public Boolean isDeleted;
    }

    public static class EnOrder {

        public Date createDate;
        public int id;
        public String products;
        public String state;
        public int userId;
    }

    public static class EnAccountInfo {

        public String username;
        public String name;
        public String email;
        public String birthday;
        public String address;
        public String phone;
    }
    
    public static class EnAccountInfoNoPass {

        public int id;
        public String username;
        public String name;
        public String email;
        public String birthday;
        public String address;
        public String phone;
        public String role;
    }
    
    public static class EnBrand {

        public int id;
        public String name;
    }

    public static class EnType {

        public int id;
        public String name;
    }

    public static enum EnPriceOption {

        OPTION_1(0, 10000000),
        OPTION_2(10000000, 15000000),
        OPTION_3(15000000, 20000000),
        OPTION_4(20000000, Integer.MAX_VALUE);

        public Integer lowest;
        public Integer highest;

        EnPriceOption(Integer lowest, Integer highest) {
            this.lowest = lowest;
            this.highest = highest;
        }

        public static EnPriceOption getEnPriceOption(String option) {
            return EnPriceOption.valueOf(option);
        }

    }

}
