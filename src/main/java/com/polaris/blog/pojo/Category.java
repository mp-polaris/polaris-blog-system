package com.polaris.blog.pojo;

import java.util.Date;

public class Category {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_category.id
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    private String id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_category.name
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_category.pinyin
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    private String pinyin;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_category.order
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    private Integer order = 0;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_category.status
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    private String status = "1";

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_category.create_time
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_category.update_time
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    private Date updateTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_category.description
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    private String description;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_category.id
     *
     * @return the value of tb_category.id
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public String getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_category.id
     *
     * @param id the value for tb_category.id
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_category.name
     *
     * @return the value of tb_category.name
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_category.name
     *
     * @param name the value for tb_category.name
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_category.pinyin
     *
     * @return the value of tb_category.pinyin
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public String getPinyin() {
        return pinyin;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_category.pinyin
     *
     * @param pinyin the value for tb_category.pinyin
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public void setPinyin(String pinyin) {
        this.pinyin = pinyin == null ? null : pinyin.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_category.order
     *
     * @return the value of tb_category.order
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public Integer getOrder() {
        return order;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_category.order
     *
     * @param order the value for tb_category.order
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public void setOrder(Integer order) {
        this.order = order;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_category.status
     *
     * @return the value of tb_category.status
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_category.status
     *
     * @param status the value for tb_category.status
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_category.create_time
     *
     * @return the value of tb_category.create_time
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_category.create_time
     *
     * @param createTime the value for tb_category.create_time
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_category.update_time
     *
     * @return the value of tb_category.update_time
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_category.update_time
     *
     * @param updateTime the value for tb_category.update_time
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_category.description
     *
     * @return the value of tb_category.description
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_category.description
     *
     * @param description the value for tb_category.description
     *
     * @mbg.generated Sun Oct 04 17:25:41 CST 2020
     */
    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", pinyin='" + pinyin + '\'' +
                ", order=" + order +
                ", status='" + status + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", description='" + description + '\'' +
                '}';
    }
}