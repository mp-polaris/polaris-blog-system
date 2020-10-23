package com.polaris.blog.controller.portal;

import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.ArticleService;
import com.polaris.blog.services.CategoryService;
import com.polaris.blog.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/portal/article")
public class ArticlePortalAPI {
    @Autowired
    private ArticleService articleService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 获取文章详情
     * 注意：普通用户只允许拿状态为置顶或发布的文章的详情
     *      管理员能拿所有
     * @param articleId
     * @return
     */
    @GetMapping("/{articleId}")
    public ResponseResult getArticleDetail(@PathVariable("articleId")String articleId){
        return articleService.getArticle(articleId);
    }

    /**
     * 获取文章列表
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list/{page}/{size}")
    public ResponseResult getArticleList(@PathVariable("page")int page,
                                        @PathVariable("size")int size){
        return articleService.getArticleList(page,size,Constants.Article.STATE_PUBLISH,null, null);
    }

    /**
     * 获取文章分类列表
     * @return
     */
    @GetMapping("/categories")
    public ResponseResult getCategoryList(){
        return categoryService.getCategoryList();
    }

    /**
     * 通过分类获取文章列表
     * @param categoryId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/list/{categoryId}/{page}/{size}")
    public ResponseResult getArticleListByCategoryId(
                                         @PathVariable("categoryId")String categoryId,
                                         @PathVariable("page")int page,
                                         @PathVariable("size")int size){
        return articleService.getArticleList(page,size,Constants.Article.STATE_PUBLISH,null,categoryId);
    }

    /**
     * 获取置顶文章
     * @return
     */
    @GetMapping("/top")
    public ResponseResult getTopArticle(){
        return articleService.AllTopArticle();
    }

    /**
     * 获取推荐的文章
     *      简单实现：通过标签来计算匹配度
     *      标签：每一篇文章都会有一个或则多个（不超过5个），从里面随机拿出一个标签当作参考
     *            即每次获取的推荐文章，就通过参考标签去查询类似的文章，
     *            如果没有查到就从数据库中获取最新的文章
     * @param articleId
     * @return
     */
    @GetMapping("/recommend/{articleId}/{size}")
    public ResponseResult getRecommendArticleList(@PathVariable("articleId")String articleId,
                                              @PathVariable("size")int size){
        return articleService.getRecommendArticleList(articleId,size);
    }

    /**
     * 获取标签列表
     * @param size
     * @return
     */
    @GetMapping("/label/{size}")
    public ResponseResult getLabelList(@PathVariable("size")int size){
        return articleService.getLabelList(size);
    }

    /**
     * 用户点击标签就会通过标签获取到对应的文章列表
     * @param size
     * @return
     */
    @GetMapping("/list/label/{label}/{page}/{size}")
    public ResponseResult getArticleListByLabel(
                            @PathVariable("label")String label,
                            @PathVariable("page")int page,
                            @PathVariable("size")int size){
        return articleService.getArticleListByLabel(label,page,size);
    }
}
