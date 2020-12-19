package com.polaris.blog.controller.admin;

import com.polaris.blog.interceptor.CheckTooFrequentCommit;
import com.polaris.blog.pojo.Category;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理中心 -> 分类API
 */
@RestController
@RequestMapping("/admin/category")
public class CategoryAdminAPI {
    @Autowired
    private CategoryService categoryService;

    /**
     * 添加分类
     *
     * @return
     */
    @CheckTooFrequentCommit
    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult addCategory(@RequestBody Category category) {
        return categoryService.addCategory(category);
    }

    /**
     * 获取一个分类
     *
     * @param categoryId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{categoryId}")
    public ResponseResult getCategory(@PathVariable("categoryId") String categoryId) {
        return categoryService.getCategory(categoryId);
    }

    /**
     * 获取分类列表
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult getCategoryList() {
        return categoryService.getCategoryList();
    }


    /**
     * 修改一个分类
     * @param category
     * @return
     */
    @CheckTooFrequentCommit
    @PreAuthorize("@permission.admin()")
    @PutMapping("/{category_id}")
    public ResponseResult updateCategory(@RequestBody Category category,
                                         @PathVariable("category_id")String categoryId) {
        return categoryService.updateCategory(category,categoryId);
    }

    /**
     * 删除一个分类（只是改变分类的状态）
     * @param categoryId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{categoryId}")
    public ResponseResult deleteCategory(@PathVariable("categoryId") String categoryId) {
        return categoryService.deleteCategory(categoryId);
    }


}
