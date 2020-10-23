package com.polaris.blog.services;

import com.polaris.blog.response.ResponseResult;

public interface WebSizeInfoService {
    ResponseResult getWebSizeTitle();

    ResponseResult updateWebSizeTitle(String title);

    ResponseResult getSeoInfo();

    ResponseResult updateSeoInfo(String keywords, String description);

    ResponseResult getWebSizeViewCount();

    void updateViewCount();
}
