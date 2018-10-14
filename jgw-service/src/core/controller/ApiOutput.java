/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.Serializable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author 
 */
public class ApiOutput implements Serializable {

    public int returnCode;
    public String returnMessage;
    public Object data;

    public ApiOutput(int returnCode, String returnMessage, Object data) {
        this.returnCode = returnCode;
        this.returnMessage = returnMessage;
        this.data = setData(data);
    }

    private Object setData(Object data) {
        try {
            if (data != null) {
                JSONObject ret = null;
                try {

                    if (data instanceof String) {
                        ret = (JSONObject) new JSONParser().parse((String) data);
                    } else {
                        String dataString = new GsonBuilder().create().toJson(data);
                        ret = (JSONObject) new JSONParser().parse(dataString);
                    }

                } catch (ParseException e) {
                }
                if (ret != null) {
                    data = ret;
                }
            }
        } catch (Exception ex) {
        }
        return data;
    }

    public String toJString() {
//        Gson gson = new GsonBuilder().setPrettyPrinting().create();
//        return gson.toJson(this);
        return new Gson().toJson(this);
    }
}
