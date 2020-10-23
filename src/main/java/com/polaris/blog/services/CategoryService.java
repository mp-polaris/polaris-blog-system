package com.polaris.blog.services;

import com.polaris.blog.pojo.Category;
import com.polaris.blog.response.ResponseResult;

public interface CategoryService {
    ResponseResult addCategory(Category category);

    ResponseResult getCategory(String categoryId);

    ResponseResult getCategoryList();

    ResponseResult updateCategory(Category category, String categoryId);

    ResponseResult deleteCategory(String categoryId);
}
