/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.service;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

/**
 *
 * @author tamnnq
 */
public class DemoService {

    private static final Lock LOCK = new ReentrantLock();
    private static final Map<String, DemoService> _instances = new NonBlockingHashMap<String, DemoService>();
    

    public static DemoService getInstance() {

        String key = "";
        if (!_instances.containsKey(key)) {
            LOCK.lock();
            try {
                if (_instances.get(key) == null) {
                    _instances.put(key, new DemoService());
                }
            } finally {
                LOCK.unlock();
            }
        }
        return _instances.get(key);
        
    }

    public int PlusService(int number1, int number2) {
        return number1 + number2;
    }

    public int MultService(int number1, int number2) {
        return number1 * number2;
    }

}
