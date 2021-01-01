package com.polaris.blog.controller.admin;

import com.polaris.blog.interceptor.CheckTooFrequentCommit;
import com.polaris.blog.pojo.Article;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理中心 -> 文章API
 */
@RestController
@RequestMapping("/admin/article")
public class ArticleAdminAPI {
    @Autowired
    private ArticleService articleService;

    /**
     * 添加文章
     * @return
     */
    @CheckTooFrequentCommit
    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult addArticle(@RequestBody Article article){
        return articleService.addArticle(article);
    }

    /**
     * 获取文章
     * @param articleId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{articleId}")
    public ResponseResult getArticle(@PathVariable("articleId")String articleId){
        return articleService.getArticleForAdmin(articleId);
    }

    /**
     * 获取文章列表
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list/{page}/{size}")
    public ResponseResult getArticleList(@PathVariable("page")int page,
                                         @PathVariable("size")int size,
                                         @RequestParam(value="state",required=false)String state,
                                         @RequestParam(value="keyword",required=false)String keyword,
                                         @RequestParam(value="categoryId",required=false)String categoryId){
        System.out.println("page==> " + page);
        System.out.println("size==> " + size);
        System.out.println("state==> " + state);
        System.out.println("keyword==> " + keyword);
        System.out.println("categoryId==> " + categoryId);
        return articleService.getArticleList(page,size,state,keyword,categoryId);
    }

    /**
     * 修改文章
     * @param articleId
     * @param article
     * @return
     */
    @CheckTooFrequentCommit
    @PreAuthorize("@permission.admin()")
    @PutMapping("/{articleId}")
    public ResponseResult updateArticle(@PathVariable("articleId")String articleId,
                                        @RequestBody Article article){
        return articleService.updateArticle(articleId,article);
    }

    /**
     * 文章置顶
     * @param articleId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @PutMapping("/top/{articleId}")
    public ResponseResult TopArticle(@PathVariable("articleId")String articleId){
        return articleService.TopArticle(articleId);
    }

    /**
     * 通过修改文章状态来删除文章
     * @param articleId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/state/{articleId}")
    public ResponseResult deleteArticleByUpdateState(@PathVariable("articleId")String articleId){
        return articleService.deleteArticleByUpdateState(articleId);
    }

    /**
     * 删除文章
     * @param articleId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{articleId}")
    public ResponseResult deleteArticle(@PathVariable("articleId")String articleId){
        return articleService.deleteArticle(articleId);
    }

    /**
     * 获取文章总数
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/count")
    public ResponseResult getArticleCount(){
        return articleService.getArticleCount();
    }

}
