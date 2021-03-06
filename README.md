# polaris-blog-system
## 个人博客系统
- polaris-blog-system是由SpringBoot2x + MyBatis + Redis + JWT等技术实现的个人网站后端
- 如果觉得项目不错，请帮忙Star支持一下。

## 适用对象
SpringBot学习者，Java开发，大家可以结合该项目学习。项目中都有对应注释

## 技术栈

##### 后端
- 核心框架：SpringBoot
- 持久层框架：MyBatis
- 分页插件：PageHelper
- 缓存框架：Redis
- Token：JWT
- 搜索引擎：Solr
- Web容器：Tomcat

##### 前端
- 核心框架：Vue.js
- 服务器渲端染：Nuxt.js
- 状态管理：Vuex
- 异步请求工具：Axios
- 布局：Element-UI

##### 后端接口API：[Polaris博客后端Swagger-UI](http://mpolaris.top:8080/swagger-ui.html)

##### 注：前端管理中心与门户已完成 

- 项目地址：[Polaris博客管理中心项目地址](https://github.com/mp-polaris/polaris-blog-admin)               [Polaris博客门户项目地址](https://github.com/mp-polaris/polaris-blog-portal)

- [Polaris博客管理中心](http://mp.mpolaris.top)

- [Polaris博客门户](http://www.mpolaris.top)

> 用户名：mpolaris     密码：1234321

## 功能说明

- **门户**
  - 用户
    - 登录
    - 退出登录
    - 用户注册
    - 找回密码
  - 文章
    - 文章详情
    - 获取文章列表
    - 文章搜索
    - 获取推荐文章
  - 评论内容
    - 添加评论
    - 删除评论
    - 获取评论列表
  - 网站信息
    - 获取分类列表
    - 访问总量-每天增量
    - 获取轮播图列表
    - 获取网站标题
    - 获取友情链接
    - 获取网站SEO数据
- **管理中心**
  - 用户
    - 管理员帐户初始化
    - 用户信息更新
    - 密码重置
    - 获取用户列表
    - 删除用户
    - 退出登录
  - 文章内容
    - 更新文章
    - 发布文章
    - 删除文章
    - 获取文章列表
    - 预览文章
    - 文章置顶
    - 文章搜索
  - 轮播图设置
    - 是否开启轮播图
    - 添加轮播图内容
    - 删除轮播图内容
    - 获取轮播图
    - 获取轮播图列表
  - 文章分类
    - 添加/修改文章分类
    - 删除文章分类
    - 获取分类列表
  - 友情链接
    - 添加/修改友情链接
    - 删除友情链接
    - 获取友情链接
  - 评论内容
    - 评论置顶
    - 删除评论
    - 获取评论列表
  - 设置
    - seo信息
      - 修改网站SEO信息
      - 获取网站SEO信息
    - 网站标题
      - 获取网站标题
      - 设置网站标题
