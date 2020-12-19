package com.polaris.blog.services;

import com.polaris.blog.pojo.Article;
import com.polaris.blog.response.ResponseResult;

public interface SearchService {
    ResponseResult doSearch(String keyword, int page, int size, String categoryId, Integer sort);

    void addArticle(Article article);

    void deleteArticle(String articleId);

    void updateArticle(String articleId,Article article);
}
