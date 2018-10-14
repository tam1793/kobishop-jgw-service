/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.controller;

import core.config.Config;
/**
 *
 * @author tam
 */
public class TestConfig {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    String host = Config.getParam("web_server", "host");
        System.out.println(host);
        
        
    }
    
}
