package com.polaris.blog.pojo;

import java.util.Date;

public class Label {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_label.id
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    private String id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_label.name
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_label.count
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    private Integer count;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_label.create_time
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_label.update_time
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    private Date updateTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_label.id
     *
     * @return the value of tb_label.id
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_label.id
     *
     * @param id the value for tb_label.id
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_label.name
     *
     * @return the value of tb_label.name
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_label.name
     *
     * @param name the value for tb_label.name
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_label.count
     *
     * @return the value of tb_label.count
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public Integer getCount() {
        return count;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_label.count
     *
     * @param count the value for tb_label.count
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_label.create_time
     *
     * @return the value of tb_label.create_time
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_label.create_time
     *
     * @param createTime the value for tb_label.create_time
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_label.update_time
     *
     * @return the value of tb_label.update_time
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_label.update_time
     *
     * @param updateTime the value for tb_label.update_time
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}