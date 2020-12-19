package com.polaris.blog.services.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.polaris.blog.dao.ArticleMapper;
import com.polaris.blog.dao.CommentMapper;
import com.polaris.blog.dao.LabelMapper;
import com.polaris.blog.pojo.*;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.ArticleService;
import com.polaris.blog.services.SearchService;
import com.polaris.blog.services.UserService;
import com.polaris.blog.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Slf4j
@Service
@Transactional
public class ArticleServiceImpl extends BaseService implements ArticleService {
    @Autowired
    private UserService userService;
    @Autowired
    private SnowflakeIdWorker idWorker;
    @Autowired
    private Random random;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SearchService searchService;
    @Autowired
    private Gson gson;

    /**
     * tip：
     *    1.后面可以添加定时发布的功能
     *    2.如果是多人博客系统，得考虑审核的问题
     *    3.保存成草稿
     *       ① 用户手动提交，会发生页面跳转
     *       ② 代码自动提交，每隔一段时间就会提交，不会发生页面跳
     *         转，多次提交，如果没有唯一标识就会重复添加到数据库
     *         => 不管是哪种草稿都会有标题
     *         方案一：每次用户发新文章之前，先向后台请求一个唯一文
     *                章ID，如果是更新文章则不需要请求这个唯一的ID，
     *                此时提交文章就会携带ID避免重复添加
     *         方案二：可以直接提交，后台判断有没有ID，如果没有就创建并且
     *                ID就作为此次返回的结果，如果有ID就直接根据ID修改内容
     *         推荐做法：自动保存草稿在前端本地完成，也就是保存在本地
     *                 如果是用户手动提交的就提交到后台
     *    4.防止重复提交（网络延迟，用户点击了多次）
     *      ① 通过携带ID的方式
     *      ② 通过token_key的提交频率来计算，如果30秒之内有多次提交，只有第
     *        一次提交有效，其他的提交直接return提示用户不要重复提交
     *      ③ 前端处理：点击了提交以后就禁止使用提交按钮，等到有响应结果再改变按钮状态
     * @param article
     * @return
     */
    @Override
    public ResponseResult addArticle(Article article) {
        //检查用户，获取到用户对象
        BlogUser blogUser = userService.checkBlogUser();
        if(blogUser == null) return ResponseResult.ACCOUNT_NOT_LOGIN();
        //检查数据：必须要有 title，状态，分类ID，内容，类型，摘要，标签
        String title = article.getTitle();
        if (TextUtil.isEmpty(title)) return ResponseResult.FAILED("文章标题不可以为空");

        //2种状态（草稿和发布）可以执行此操作
        String state = article.getState();
        if (!Constants.Article.STATE_PUBLISH.equals(state) &&
                !Constants.Article.STATE_DRAFT.equals(state)) {
            return ResponseResult.FAILED("不支持此操作");
        }

        String type = article.getType();
        if (TextUtil.isEmpty(type)) return ResponseResult.FAILED("文章类型不可以为空");
        if (!"0".equals(type) && !"1".equals(type)) {
            return ResponseResult.FAILED("文章格式不正确");
        }

        //以下的检查是发布的检查，草稿不需要下列检查
        if (Constants.Article.STATE_PUBLISH.equals(state)) {
            if (title.length() > Constants.Article.TITLE_MAX_LENGTH) {
                return ResponseResult.FAILED("文章标题长度不能超过" +
                        Constants.Article.TITLE_MAX_LENGTH + "个字符");
            }
            if (TextUtil.isEmpty(article.getCategoryId())) return ResponseResult.FAILED("分类ID不可以为空");
            if (TextUtil.isEmpty(article.getContent())) return ResponseResult.FAILED("内容不可以为空");
            String summary = article.getSummary();
            if (TextUtil.isEmpty(summary)) return ResponseResult.FAILED("摘要不可以为空");
            if (summary.length() > Constants.Article.SUMMARY_MAX_LENGTH) {
                return ResponseResult.FAILED("文章摘要长度不能超过" +
                        Constants.Article.SUMMARY_MAX_LENGTH + "个字符");
            }
            if (TextUtil.isEmpty(article.getLabels())) return ResponseResult.FAILED("标签不可以为空");
        }

        String s = Constants.Article.STATE_DRAFT.equals(state) ? "草稿保存成功" : "文章发表成功";
        String articleId = article.getId();
        ArticleMapper articleMapper = MapperUtil.getMapper(ArticleMapper.class);
        if (TextUtil.isEmpty(articleId)) {
            //新的内容，数据库没有
            //补充数据
            article.setId(idWorker.nextId() + "");
            article.setUserId(blogUser.getId());
            article.setCreateTime(new Date());
            article.setUpdateTime(new Date());
            //保存数据到数据库
            articleMapper.insert(article);
        } else {
            //修改内容,对状态进行处理，如果是已经发布的则不能保存为草稿
            Article articleFromDB = articleMapper.selectByPrimaryKey(articleId);
            if (Constants.Article.STATE_PUBLISH.equals(articleFromDB.getState())
                    && Constants.Article.STATE_DRAFT.equals(state)) {
                //已经发布了，只能更新不能保存为草稿
                return ResponseResult.FAILED("已发布的文章不支持成为草稿");
            }
            article.setUpdateTime(new Date());
            articleMapper.updateByPrimaryKey(article);
        }
        //如果状态为正式发布，则保存到搜索的数据库里，即Solr
        if(Constants.Article.STATE_PUBLISH.equals(state)){
            searchService.addArticle(article);
        }
        //封装label字段，打散标签，入库，统计
        this.setupLabels(article.getLabels());
        //返回结果
        return ResponseResult.SUCCESS(s).setData(articleId);
    }

    private void setupLabels(String labels) {
        ArrayList<String> labelList = new ArrayList<>();
        if(labels.contains("-")){
            labelList.addAll(Arrays.asList(labels.split("-")));
        } else {
            labelList.add(labels);
        }
        //label入库，统计
        for(String label : labelList){
            //通过label修改count，如果修改不成功则没有没有对应label，直接添加
            LabelMapper labelMapper = MapperUtil.getMapper(LabelMapper.class);
            int result = labelMapper.updateCountByName(label);
            if(result == 0){
                Label targetLabel = new Label();
                targetLabel.setId(idWorker.nextId() + "");
                targetLabel.setCount(1);
                targetLabel.setName(label);
                targetLabel.setCreateTime(new Date());
                targetLabel.setUpdateTime(new Date());
                labelMapper.insert(targetLabel);
            }
        }
    }

    /**
     * 注意：
     *  如果有文章审核机制,审核中的文章只有管理员和作者自己可以获取
     *  状态为删除/草稿的文章只有管理员能获取
     * <p>
     *  统计文章的阅读量：
     *     要精确一点的话要对IP进行处理，如果是同一个IP则不保存阅读量
     *  实现步骤：
     *      先把阅读量在redis中更新，文章也会在redis里缓存一份，比如10分钟
     *      当redis中没有文章的时候，就从mysql中取，这个时候同时更新阅读量
     *      10分钟以后，在下一次访问的时候更新一次阅读量
     * </p>
     */
    @Override
    public ResponseResult getArticle(String articleId) {
        //先从redis里获取文章
        String articleJson = (String)redisUtil.get(Constants.Article.KEY_ARTICLE_CACHE + articleId);
        if (!TextUtil.isEmpty(articleJson)) {
            Article article = gson.fromJson(articleJson, Article.class);
            //增加一次阅读数量
            redisUtil.incr(Constants.Article.KEY_ARTICLE_VIEW_COUNT + articleId,1);
            return ResponseResult.SUCCESS("文章获取成功").setData(article);
        }
        //redis中没有，去mysql里获取
        ArticleMapper articleMapper = MapperUtil.getMapper(ArticleMapper.class);
        Article article = articleMapper.selectArticleAndBlogUserByArticleId(articleId);
        if (article == null) return ResponseResult.FAILED("文章不存在");
        String state = article.getState();
        //正常发布的状态，才能增加阅读量
        if (Constants.Article.STATE_PUBLISH.equals(state)
                || Constants.Article.STATE_TOP.equals(state)) {
            //将查出的文章放入redis中，五分钟过期
            redisUtil.set(Constants.Article.KEY_ARTICLE_CACHE + articleId,
                    gson.toJson(article),Constants.TimeValue.MIN_5);
            //设置阅读量的key,先从redis里拿，如果没有就从article中获取并且添加到redis里
            String viewCount = (String)redisUtil.get(Constants.Article.KEY_ARTICLE_VIEW_COUNT + articleId);
            if (TextUtil.isEmpty(viewCount)) {
                int newCount = article.getViewCount() + 1;
                redisUtil.set(Constants.Article.KEY_ARTICLE_VIEW_COUNT + articleId,String.valueOf(newCount));
            } else{
                //redis中有就加1，并更新到mysql中去
                int newCount = (int)redisUtil.incr(Constants.Article.KEY_ARTICLE_VIEW_COUNT + articleId, 1);
                article.setViewCount(newCount);
                articleMapper.updateByPrimaryKey(article);
                //更新solr里的阅读量
                searchService.updateArticle(articleId,article);
            }
            return ResponseResult.SUCCESS("文章获取成功").setData(article);
        }
        // 删除/草稿的文章只有管理员能获取
        BlogUser blogUser = userService.checkBlogUser();
        if (blogUser == null || !Constants.User.ROLE_ADMIN.equals(blogUser.getRoles())) {
            return ResponseResult.PERMISSION_DENIED();
        }
        return ResponseResult.SUCCESS("文章获取成功").setData(article);
    }

    /**
     * 获取文章列表
     */
    @Override
    public ResponseResult getArticleList(int page, int size, String state,String keyword, String categoryId) {
        page = checkPage(page);
        size = checkSize(size);

        PageHelper.startPage(page,size,"create_time DESC");
        List<Article> articles = MapperUtil.getMapper(ArticleMapper.class).selectAllByCondition(state,keyword,categoryId);
        PageInfo<Article> pageInfo = new PageInfo<>(articles);
        return ResponseResult.SUCCESS("文章列表查询成功").setData(pageInfo);
    }

    /**
     * 只支持修改：标题，内容，分类,标签,摘要
     * @param articleId
     * @param article
     * @return
     */
    @Override
    public ResponseResult updateArticle(String articleId, Article article) {
        ArticleMapper articleMapper = MapperUtil.getMapper(ArticleMapper.class);
        Article articleFromDB = articleMapper.selectByPrimaryKey(articleId);
        if (articleFromDB == null) return ResponseResult.FAILED("文章不存在");
        String title = article.getTitle();
        if (!TextUtil.isEmpty(title)) articleFromDB.setTitle(title);
        String content = article.getContent();
        if (!TextUtil.isEmpty(content)) articleFromDB.setContent(content);
        String categoryId = article.getCategoryId();
        if (!TextUtil.isEmpty(categoryId)) articleFromDB.setCategoryId(categoryId);
        String labels = article.getLabels();
        if (!TextUtil.isEmpty(labels)) articleFromDB.setLabels(labels);
        String summary = article.getSummary();
        if (!TextUtil.isEmpty(summary)) articleFromDB.setSummary(summary);
        String state = article.getState();
        if(!TextUtil.isEmpty(state)) articleFromDB.setState(state);

        String cover = article.getCover();
        if (!TextUtil.isEmpty(cover)) articleFromDB.setSummary(cover);

        articleFromDB.setUpdateTime(new Date());
        articleMapper.updateByPrimaryKey(articleFromDB);
        return ResponseResult.SUCCESS("文章更新成功");
    }

    /**
     * 文章是否置顶
     * 注意：文章必须已经是发布了的才能置顶
     */
    @Override
    public ResponseResult TopArticle(String articleId) {
        ArticleMapper articleMapper = MapperUtil.getMapper(ArticleMapper.class);
        Article article = articleMapper.selectByPrimaryKey(articleId);
        if (article == null) return ResponseResult.SUCCESS("文章不存在");
        String state = article.getState();
        if (Constants.Article.STATE_PUBLISH.equals(state)){
            articleMapper.updateStateById(articleId, Constants.Article.STATE_TOP);
            return ResponseResult.SUCCESS("文章置顶成功");
        }
        if(Constants.Article.STATE_TOP.equals(state)){
            articleMapper.updateStateById(articleId, Constants.Article.STATE_PUBLISH);
            return ResponseResult.SUCCESS("取消文章置顶成功");
        }
        return ResponseResult.SUCCESS("当前文章状态不支持该操作");
    }

    @Override
    public ResponseResult AllTopArticle() {
        List<Article> articles = MapperUtil.getMapper(ArticleMapper.class).selectAllByCondition(Constants.Article.STATE_TOP, null, null);
        return ResponseResult.SUCCESS("获取所有置顶文章成功").setData(articles);
    }

    /**
     * 通过修改文章状态删除文章
     */
    @Override
    public ResponseResult deleteArticleByUpdateState(String articleId) {
        //"删除"文章对应的评论
        MapperUtil.getMapper(CommentMapper.class).updateStateOfAllCommentByArticleId(articleId);
        //"删除"文章
        int result = MapperUtil.getMapper(ArticleMapper.class).updateStateById(articleId, Constants.Article.STATE_DELETE);
        if(result > 0){
            redisUtil.del(Constants.Article.KEY_ARTICLE_CACHE + articleId);
            //删除搜索库中对应的内容
            searchService.deleteArticle(articleId);
            return ResponseResult.SUCCESS("文章删除成功");
        }
        return ResponseResult.SUCCESS("文章不存在");
    }

    /**
     * 注意：
     *     如果是多用户，用户不可以直接删除而只是改变状态
     *     管理员能直接删除但是需要二次确认
     * 这里做成真的删除吧时机
     */
    @Override
    public ResponseResult deleteArticle(String articleId) {
        //先删除文章对应的评论
        MapperUtil.getMapper(CommentMapper.class).deleteAllByArticleId(articleId);
        //删除文章
        int result = MapperUtil.getMapper(ArticleMapper.class).deleteByPrimaryKey(articleId);
        if(result > 0){
            redisUtil.del(Constants.Article.KEY_ARTICLE_CACHE + articleId);
            //删除搜索库中对应的内容
            searchService.deleteArticle(articleId);
            return ResponseResult.SUCCESS("文章删除成功");
        }
        return ResponseResult.SUCCESS("文章不存在");
    }

    @Override
    public ResponseResult getRecommendArticleList(String articleId, int size) {
        //查询文章
        ArticleMapper articleMapper = MapperUtil.getMapper(ArticleMapper.class);
        Article article = articleMapper.selectByPrimaryKey(articleId);
        String currentArticleId = article.getId();
        //打散标签
        String labels = article.getLabels();
        List<String> labelList= new ArrayList<>();
        if(labels.contains("-")){
            labelList.addAll(Arrays.asList(labels.split("-")));
        } else {
            labelList.add(labels);
        }
        //从列表中随机获取一个标签,查询其他拥有该标签的文章
        String targetLabel = labelList.get(random.nextInt(labelList.size()));

        PageHelper.startPage(0,size);
        List<Article> list = articleMapper.selectAllByLabel(targetLabel,currentArticleId);
        if (list.size() < size){
            //按标签查的文章数量不够，再查询最新文章补上
            int dxSize = size - list.size();
            List<Article> dxList = articleMapper.getLastArticleListByDxSize(dxSize,currentArticleId);
            list.addAll(dxList);
        }
        PageInfo<Article> pageInfo = new PageInfo<>(list);
        return ResponseResult.SUCCESS("推荐文章获取成功").setData(pageInfo);
    }

    @Override
    public ResponseResult getArticleListByLabel(String label, int page, int size) {
        page = checkPage(page);
        size = checkSize(size);
        List<Article> articles = null;
        PageHelper.startPage(page,size,"create_time DESC");
        //先从缓存中获取
        String cacheJson = (String) redisUtil.get(Constants.Article.KEY_ARTICLE_FIRST_PAGE_CACHE_BY_LABEL + label);
        if (!TextUtil.isEmpty(cacheJson) && page == 1) {
            articles = gson.fromJson(cacheJson,new TypeToken<List<Article>>(){
            }.getType());
            log.info("articles list from redis...");
        } else {
            articles = MapperUtil.getMapper(ArticleMapper.class).selectAllByLabel(label, null);
        }
        PageInfo<Article> pageInfo = new PageInfo<>(articles);
        //如果是第一页就保存一份到缓存
        if(page == 1) {
            redisUtil.set(Constants.Article.KEY_ARTICLE_FIRST_PAGE_CACHE_BY_LABEL + label, gson.toJson(articles),Constants.TimeValue.HOUR);
        }
        return ResponseResult.SUCCESS("文章列表获取成功").setData(pageInfo);
    }

    @Override
    public ResponseResult getLabelList(int size) {
        size = checkSize(size);

        PageHelper.startPage(0,size,"count DESC");
        List<Label> labels = MapperUtil.getMapper(LabelMapper.class).selectAll();
        PageInfo<Label> pageInfo = new PageInfo<>(labels);
        return ResponseResult.SUCCESS("标签列表获取成功").setData(pageInfo);
    }

    @Override
    public ResponseResult getArticleCount(){
        int count = MapperUtil.getMapper(ArticleMapper.class).selectArticleCount();
        return ResponseResult.SUCCESS("文章总数查询成功").setData(count);
    }

}
