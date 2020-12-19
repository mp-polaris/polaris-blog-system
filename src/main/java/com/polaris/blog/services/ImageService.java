package com.polaris.blog.services;

import com.polaris.blog.response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;


public interface ImageService {
    ResponseResult uploadImage(MultipartFile file,String category);

    void getImage(String imageId);

    ResponseResult getImageList(int page, int size, String category);

    ResponseResult deleteImage(String imageUrl);

    void getQrCodeImage(String code);
}
