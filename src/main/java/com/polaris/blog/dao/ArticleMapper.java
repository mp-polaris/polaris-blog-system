package com.polaris.blog.dao;

import com.polaris.blog.pojo.Article;
import java.util.List;

public interface ArticleMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_article
     *
     * @mbg.generated Sun Oct 18 15:54:40 CST 2020
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_article
     *
     * @mbg.generated Sun Oct 18 15:54:40 CST 2020
     */
    int insert(Article record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_article
     *
     * @mbg.generated Sun Oct 18 15:54:40 CST 2020
     */
    Article selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_article
     *
     * @mbg.generated Sun Oct 18 15:54:40 CST 2020
     */
    List<Article> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_article
     *
     * @mbg.generated Sun Oct 18 15:54:40 CST 2020
     */
    int updateByPrimaryKey(Article record);

    Article selectArticleAndBlogUserByArticleId(String articleId);

    List<Article> selectAllByCondition(String state, String keyword, String categoryId);

    int updateStateById(String articleId, String state);

    List<Article> selectAllByLabel(String targetLabel, String currentArticleId);

    List<Article> getLastArticleListByDxSize(int dxSize, String currentArticleId);

    int selectArticleCount();
}