package com.polaris.blog.utils;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 管理CountDownLatch
 *  获取
 *  删除
 */
@Component
public class CountDownLatchManager {
    Map<String, CountDownLatch> latches = new HashMap<>();

    public void onPhoneDoLogin(String qrCode){
        CountDownLatch countDownLatch = latches.get(qrCode);
        if(countDownLatch != null){
            countDownLatch.countDown();
        }
    }

    public CountDownLatch getLatch(String qrCode){
        CountDownLatch countDownLatch = latches.get(qrCode);
        if(countDownLatch == null){
            countDownLatch = new CountDownLatch(1);
            latches.put(qrCode,countDownLatch);
        }
        return  countDownLatch;
    }

    public void deleteLatch(String qrCode){
        latches.remove(qrCode);
    }
}
