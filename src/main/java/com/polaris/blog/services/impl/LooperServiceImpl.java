package com.polaris.blog.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.polaris.blog.dao.FriendLinkMapper;
import com.polaris.blog.dao.LooperMapper;
import com.polaris.blog.pojo.BlogUser;
import com.polaris.blog.pojo.FriendLink;
import com.polaris.blog.pojo.Looper;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.LooperService;
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
public class LooperServiceImpl extends BaseService implements LooperService {
    @Autowired
    private SnowflakeIdWorker idWorker;
    @Autowired
    private UserService userService;

    @Override
    public ResponseResult addLooper(Looper looper) {
        //检查数据：title,target_url,image_url
        if (TextUtil.isEmpty(looper.getTitle())) return ResponseResult.FAILED("轮播图名字不可以为空");
        if (TextUtil.isEmpty(looper.getTargetUrl())) return ResponseResult.FAILED("跳转链接不可以为空");
        if (TextUtil.isEmpty(looper.getImageUrl())) return ResponseResult.FAILED("图片地址不可以为空");
        //补充数据
        looper.setId(idWorker.nextId() + "");
        looper.setCreateTime(new Date());
        looper.setUpdateTime(new Date());
        //保存数据
        MapperUtil.getMapper(LooperMapper.class).insert(looper);
        //返回结果
        return ResponseResult.SUCCESS("轮播图添加成功");
    }

    @Override
    public ResponseResult getLooper(String looperId) {
        Looper looper = MapperUtil.getMapper(LooperMapper.class).selectByPrimaryKey(looperId);
        if (looper == null) return ResponseResult.FAILED("轮播图不存在");
        return ResponseResult.SUCCESS("轮播图获取成功").setData(looper);
    }

    @Override
    public ResponseResult getLooperList() {
        List<Looper> list = null;
        BlogUser blogUser = userService.checkBlogUser();
        LooperMapper looperMapper = MapperUtil.getMapper(LooperMapper.class);
        //管理员账号：能获取到所有category
        if (blogUser == null || !Constants.User.ROLE_ADMIN.equals(blogUser.getRoles())) {
            //普通用户或则未登录不能获取删除状态的分类
            list = looperMapper.selectAllByStatus();
        } else {
            list = looperMapper.selectAll();
        }
        return ResponseResult.SUCCESS("轮播图列表查询成功").setData(list);
    }

    @Override
    public ResponseResult updateLooper(String looperId, Looper looper) {
        LooperMapper looperMapper = MapperUtil.getMapper(LooperMapper.class);
        Looper looperFromDB = looperMapper.selectByPrimaryKey(looperId);
        if (looperFromDB == null) return ResponseResult.FAILED("该轮播图不存在");
        if (!TextUtil.isEmpty(looper.getTitle())) looperFromDB.setTitle(looper.getTitle());
        if (!TextUtil.isEmpty(looper.getTargetUrl())) looperFromDB.setTargetUrl(looper.getTargetUrl());
        if (!TextUtil.isEmpty(looper.getImageUrl())) looperFromDB.setImageUrl(looper.getImageUrl());
        looperFromDB.setOrder(looper.getOrder());
        looperFromDB.setUpdateTime(new Date());
        looperMapper.updateByPrimaryKey(looperFromDB);
        return ResponseResult.SUCCESS("轮播图修改成功");
    }

    @Override
    public ResponseResult deleteLooper(String looperId) {
        int result = MapperUtil.getMapper(LooperMapper.class).deleteByPrimaryKey(looperId);
        return result > 0 ? ResponseResult.SUCCESS("轮播图删除成功") : ResponseResult.FAILED("该轮播图不存在");
    }
}
