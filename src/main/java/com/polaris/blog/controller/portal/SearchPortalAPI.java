package com.polaris.blog.controller.portal;

import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/search")
public class SearchPortalAPI {
    @Autowired
    private SearchService searchService;

    /**
     * 指定搜索
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    @GetMapping
    public ResponseResult doSearch(@RequestParam("keyword")String keyword,
                                   @RequestParam("page")int page,
                                   @RequestParam("size")int size,
                                   @RequestParam(value="categoryId",required=false)String categoryId,
                                   @RequestParam(value="sort",required=false)Integer sort){
        return searchService.doSearch(keyword,page,size,categoryId,sort);
    }
}
