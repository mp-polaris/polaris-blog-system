package com.polaris.blog.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.polaris.blog.dao.CategoryMapper;
import com.polaris.blog.pojo.BlogUser;
import com.polaris.blog.pojo.Category;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.CategoryService;
import com.polaris.blog.services.UserService;
import com.polaris.blog.utils.Constants;
import com.polaris.blog.utils.MapperUtil;
import com.polaris.blog.utils.SnowflakeIdWorker;
import com.polaris.blog.utils.TextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl extends BaseService implements CategoryService {
    @Autowired
    private SnowflakeIdWorker idWorker;
    @Autowired
    private UserService userService;

    @Override
    public ResponseResult addCategory(Category category) {
        //检查数据（必须有分类名称，分类的拼英，描述）
        if (TextUtil.isEmpty(category.getName())) return ResponseResult.FAILED("分类名称不可以为空");
        if (TextUtil.isEmpty(category.getPinyin())) return ResponseResult.FAILED("分类名称的拼英不可以为空");
        if (TextUtil.isEmpty(category.getDescription())) return ResponseResult.FAILED("分类描述不可以为空");
        //补全数据
        category.setId(idWorker.nextId() + "");
        category.setStatus("1");
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        //保存数据
        MapperUtil.getMapper(CategoryMapper.class).insert(category);
        //返回结果
        return ResponseResult.SUCCESS("分类添加成功");
    }

    @Override
    public ResponseResult getCategory(String categoryId) {
        Category category = MapperUtil.getMapper(CategoryMapper.class).selectByPrimaryKey(categoryId);
        if (category == null) return ResponseResult.FAILED("分类不存在");
        return ResponseResult.SUCCESS("分类获取成功").setData(category);
    }

    @Override
    public ResponseResult getCategoryList() {
        List<Category> list = null;
        BlogUser blogUser = userService.checkBlogUser();
        CategoryMapper categoryMapper = MapperUtil.getMapper(CategoryMapper.class);
        //管理员账号：能获取到所有category
        if (blogUser == null || !Constants.User.ROLE_ADMIN.equals(blogUser.getRoles())) {
            //普通用户或则未登录不能获取删除状态的分类
            list = categoryMapper.selectAllByStatus();
        } else {
            list = categoryMapper.selectAll();
        }
        return ResponseResult.SUCCESS("分类列表查询成功").setData(list);
    }

    @Override
    public ResponseResult updateCategory(Category category, String categoryId) {
        CategoryMapper categoryMapper = MapperUtil.getMapper(CategoryMapper.class);
        Category categoryFromDB = categoryMapper.selectByPrimaryKey(categoryId);
        if (categoryFromDB == null) return ResponseResult.FAILED("分类不存在");
        if (!TextUtil.isEmpty(category.getName())) categoryFromDB.setName(category.getName());
        if (!TextUtil.isEmpty(category.getPinyin())) categoryFromDB.setPinyin(category.getPinyin());
        if (!TextUtil.isEmpty(category.getDescription())) categoryFromDB.setDescription(category.getDescription());
        categoryFromDB.setOrder(category.getOrder());
        categoryFromDB.setStatus(category.getStatus());
        categoryFromDB.setUpdateTime(new Date());
        categoryMapper.updateByPrimaryKey(categoryFromDB);
        return ResponseResult.SUCCESS("分类修改成功");
    }

    @Override
    public ResponseResult deleteCategory(String categoryId) {
        int result = MapperUtil.getMapper(CategoryMapper.class).updateStatusById(categoryId);
        return result > 0 ? ResponseResult.SUCCESS("分类删除成功") : ResponseResult.FAILED("该分类不存在");
    }
}