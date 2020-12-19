package com.polaris.blog.services;

import com.polaris.blog.pojo.FriendLink;
import com.polaris.blog.response.ResponseResult;

public interface FriendLinkService {

    ResponseResult addFriendLink(FriendLink friendLink);

    ResponseResult getFriendLink(String friendLinkId);

    ResponseResult getFriendLinkList();

    ResponseResult updateFriendLink(String friendLinkId, FriendLink friendLink);

    ResponseResult deleteFriendLink(String friendLinkId);
}
