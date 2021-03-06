package com.polaris.blog.dao;

import com.polaris.blog.pojo.FriendLink;
import java.util.List;

public interface FriendLinkMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_friend_link
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_friend_link
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    int insert(FriendLink record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_friend_link
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    FriendLink selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_friend_link
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    List<FriendLink> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table tb_friend_link
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    int updateByPrimaryKey(FriendLink record);

    List<FriendLink> selectAllByStatus();
}