package com.polaris.blog.services;

import com.polaris.blog.pojo.Looper;
import com.polaris.blog.response.ResponseResult;

public interface LooperService {

    ResponseResult addLooper(Looper looper);

    ResponseResult getLooper(String looperId);

    ResponseResult getLooperList();

    ResponseResult updateLooper(String looperId, Looper looper);

    ResponseResult deleteLooper(String looperId);
}
