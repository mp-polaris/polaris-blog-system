package com.polaris.blog.services;

import com.polaris.blog.pojo.Article;
import com.polaris.blog.response.ResponseResult;

public interface ArticleService {
    ResponseResult addArticle(Article article);

    ResponseResult getArticle(String articleId);

    ResponseResult getArticleList(int page, int size, String state,String keyword, String categoryId);

    ResponseResult updateArticle(String articleId, Article article);

    ResponseResult TopArticle(String articleId);

    ResponseResult deleteArticle(String articleId);

    ResponseResult deleteArticleByUpdateState(String articleId);

    ResponseResult AllTopArticle();

    ResponseResult getRecommendArticleList(String articleId, int size);

    ResponseResult getArticleListByLabel(String label, int page, int size);

    ResponseResult getLabelList(int size);

    ResponseResult getArticleCount();

    ResponseResult getArticleForAdmin(String articleId);
}
