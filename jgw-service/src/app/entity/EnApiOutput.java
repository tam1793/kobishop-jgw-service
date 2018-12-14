/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.entity;

import core.controller.ApiOutput;

/**
 *
 * @author tamnnq
 */
public class EnApiOutput extends ApiOutput {

    public static enum ERROR_CODE_API {
        SUCCESS(1, "SUCCESS"),
        INVALID_DATA_INPUT(-1, "INVALID_INPUT"),
        UNSUPPORTED_ERROR(-2, "UNSUPPORTED_ERROR"),
        ACTION_INVALID(-3, "ACTION_INVALID"),
        USERNAME_OR_PASSWORD_INVALID(-4, "USERNAME_OR_PASSWORD_INVALID"),
        TOKEN_INVALID(-5, "TOKEN_INVALID"),
        LOGIN_TOKEN_INVALID(-6, "LOGIN_TOKEN_INVALID"),
        USER_NOT_EXIST(-7, "USER_NOT_EXIST"),
        USER_EXIST(-8, "USER_EXIST"),
        PERMISSION_DENY(-999, "PERMISSION_DENY"),
        SERVER_ERROR(-1000, "SYSTEM_ERROR"),
        //PRODUCT
        PRODUCT_NOT_FOUND(-101, "PRODUCT_NOT_FOUND"),
        //BRAND
        BRAND_NOT_FOUND(-102, "BRAND_NOT_FOUND"),
        //TYPE
        TYPE_NOT_FOUND(-103, "TYPE_NOT_FOUND"),
        //ORDER
        ORDER_NOT_FOUND(-104,"ORDER_NOT_FOUND");
        public int code;
        public String message;

        private ERROR_CODE_API(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    public EnApiOutput(ERROR_CODE_API result) {
        super(result.code, result.message, null);
    }

    public EnApiOutput(int code, String message, Object data) {
        super(code, message, data);
    }

    public EnApiOutput(ERROR_CODE_API result, Object data) {
        super(result.code, result.message, data);
    }

    /*
        API OUTPUT DEFINE
     */
}
