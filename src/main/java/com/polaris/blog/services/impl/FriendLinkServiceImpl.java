package com.polaris.blog.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.polaris.blog.dao.CategoryMapper;
import com.polaris.blog.dao.FriendLinkMapper;
import com.polaris.blog.pojo.BlogUser;
import com.polaris.blog.pojo.Category;
import com.polaris.blog.pojo.FriendLink;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.FriendLinkService;
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
public class FriendLinkServiceImpl extends BaseService implements FriendLinkService {
    @Autowired
    private SnowflakeIdWorker idWorker;
    @Autowired
    private UserService userService;

    @Override
    public ResponseResult addFriendLink(FriendLink friendLink) {
        if (TextUtil.isEmpty(friendLink.getUrl())) return ResponseResult.FAILED("友情链接Url不可以为空");
        if (TextUtil.isEmpty(friendLink.getLogo())) return ResponseResult.FAILED("logo不可以为空");
        if (TextUtil.isEmpty(friendLink.getName())) return ResponseResult.FAILED("友情链接名不可以为空");
        //补全数据
        friendLink.setId(idWorker.nextId() + "");
        friendLink.setUpdateTime(new Date());
        friendLink.setCreateTime(new Date());
        //保存数据
        MapperUtil.getMapper(FriendLinkMapper.class).insert(friendLink);
        return ResponseResult.SUCCESS("友情链接添加成功");
    }

    @Override
    public ResponseResult getFriendLink(String friendLinkId) {
        FriendLink friendLink = MapperUtil.getMapper(FriendLinkMapper.class).selectByPrimaryKey(friendLinkId);
        if (friendLink == null) return ResponseResult.FAILED("友情链接不存在");
        return ResponseResult.SUCCESS("友情链接获取成功").setData(friendLink);
    }

    @Override
    public ResponseResult getFriendLinkList() {
        List<FriendLink> list = null;
        BlogUser blogUser = userService.checkBlogUser();
        FriendLinkMapper friendLinkMapper = MapperUtil.getMapper(FriendLinkMapper.class);
        //管理员账号：能获取到所有category
        if (blogUser == null || !Constants.User.ROLE_ADMIN.equals(blogUser.getRoles())) {
            //普通用户或则未登录不能获取删除状态的分类
            list = friendLinkMapper.selectAllByStatus();
        } else {
            list = friendLinkMapper.selectAll();
        }
        return ResponseResult.SUCCESS("友情链接列表查询成功").setData(list);
    }

    @Override
    public ResponseResult updateFriendLink(String friendLinkId, FriendLink friendLink) {
        FriendLinkMapper friendLinkMapper = MapperUtil.getMapper(FriendLinkMapper.class);
        FriendLink friendLinkFromDB = friendLinkMapper.selectByPrimaryKey(friendLinkId);
        if (friendLinkFromDB == null) return ResponseResult.FAILED("该友情链接不存在");
        if (!TextUtil.isEmpty(friendLink.getName())) friendLinkFromDB.setName(friendLink.getName());
        if (!TextUtil.isEmpty(friendLink.getLogo())) friendLinkFromDB.setLogo(friendLink.getLogo());
        if (!TextUtil.isEmpty(friendLink.getUrl())) friendLinkFromDB.setUrl(friendLink.getUrl());
        friendLinkFromDB.setOrder(friendLink.getOrder());
        friendLinkFromDB.setState(friendLink.getState());
        friendLinkFromDB.setUpdateTime(new Date());
        friendLinkMapper.updateByPrimaryKey(friendLinkFromDB);
        return ResponseResult.SUCCESS("友情链接修改成功");
    }

    @Override
    public ResponseResult deleteFriendLink(String friendLinkId) {
        int result = MapperUtil.getMapper(FriendLinkMapper.class).deleteByPrimaryKey(friendLinkId);
        return result > 0 ? ResponseResult.SUCCESS("友情链接删除成功") : ResponseResult.FAILED("该友情链接不存在");
    }
}
