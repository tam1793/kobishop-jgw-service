/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.guest.service;

import app.entity.EnApp.EnUserPermission;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;

/**
 *
 * @author tam
 */
public class VerifyUserService {

    private static final Logger logger = Logger.getLogger(VerifyUserService.class.getName());
    private static final Lock createLock = new ReentrantLock();
    private static final Map<String, VerifyUserService> instances = new HashMap();

    private static String loginSecret;

    public static VerifyUserService getInstance(String loginSecret) {
        String key = loginSecret;
        if (!instances.containsKey(key)) {
            createLock.lock();
            try {
                if (!instances.containsKey(key)) {
                    instances.put(key, new VerifyUserService(loginSecret));
                }
            } finally {
                createLock.unlock();
            }
        }
        return (VerifyUserService) instances.get(key);
    }

    private VerifyUserService(String loginSecret) {
        this.loginSecret = loginSecret;
    }

    public EnUserPermission verifiedUser(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(loginSecret).parseClaimsJws(token);
            Gson googleJson = new Gson();
            EnUserPermission user = googleJson.fromJson(claims.getBody().get("user").toString(), EnUserPermission.class);
            return user;
        } catch (Exception Ex) {
            logger.error(Ex.getMessage(), Ex);
        }
        return null;
    }
}
