/*
package com.polaris.blog;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.polaris.blog.dao.ArticleMapper;
import com.polaris.blog.dao.BlogUserMapper;
import com.polaris.blog.pojo.Article;
import com.polaris.blog.pojo.BlogUser;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.impl.UserServiceImpl;
import com.polaris.blog.utils.*;
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.SimTocExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import io.jsonwebtoken.*;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.*;

@SpringBootTest
class PolarisBlogSystemApplicationTests {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SolrClient solrClient;
    @Autowired
    UserServiceImpl userService;

    */
/**
     * 将MySQL中的文章表内容全部导入到Solr中
     * 两个知识点：MarkDown转html，富文本转纯文本
     *//*

    @Test
    public void importAllArticleToSolr(){
        try {
            List<Article> articles = MapperUtil.getMapper(ArticleMapper.class).selectAll();
            for(Article article : articles){
                SolrInputDocument doc = new SolrInputDocument();
                doc.addField("id",article.getId());
                doc.addField("blog_view_count",article.getViewCount());
                doc.addField("blog_title",article.getTitle());
                //对内容进行处理，去掉标签，提取出纯文本
                //① markdown内容 ==> type = 1
                //② 富文本内容 => type = 0
                //=> 如果type = 1，需要先转成html，再从html转为纯文字
                //   如果 type = 0，要转为纯文本
                String type = article.getType();
                String html = null;
                if (Constants.Article.TYPE_MARKDOWN.equals(type)) {
                    //转成html
                    MutableDataSet options = new MutableDataSet().set(Parser.EXTENSIONS, Arrays.asList(
                            TablesExtension.create(),
                            JekyllTagExtension.create(),
                            TocExtension.create(),
                            SimTocExtension.create()
                    ));
                    Parser parser = Parser.builder(options).build();
                    HtmlRenderer renderer = HtmlRenderer.builder(options).build();
                    Node document =  parser.parse(article.getContent());
                    html = renderer.render(document);
                } else {
                    html = article.getContent();
                }
                //到这里都是html了
                String text = Jsoup.parse(html).text();
                doc.addField("blog_content",text);
                doc.addField("blog_create_time",article.getCreateTime());
                doc.addField("blog_labels",article.getLabels());
                doc.addField("blog_url","www.baidu.com");
                doc.addField("blog_category_id",article.getCategoryId());
                solrClient.add(doc);
                solrClient.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */
/**
     * 测试Solr增删改查
     *//*

    @Test
    public void TestSolrAddOrUpdate(){
        try {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id","766083075414154653");
            doc.addField("blog_view_count",10);
            doc.addField("blog_title","草");
            doc.addField("blog_content","我是文章");
            doc.addField("blog_create_time",new Date());
            doc.addField("blog_labels","test-java");
            doc.addField("blog_url","www.baidu.com");
            doc.addField("blog_category_id","765708998820233212");
            solrClient.add(doc);
            solrClient.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void TestSolrDelete(){
        try {
            //删除一条
            //solrClient.deleteById("766083075414154653");
            //删除所有
            solrClient.deleteByQuery("*");
            solrClient.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    */
/**
     * 测试PageHelper
     *//*

    @Test
    public void testDao(){
        PageHelper.startPage(1,2,"id DESC");//这行是重点，表示从page页开始，每页size条数据
        java.util.List<BlogUser> list = MapperUtil.getMapper(BlogUserMapper.class).selectAll();
        PageInfo<BlogUser> pageInfo = new PageInfo<BlogUser>(list);
        System.out.println(list);;
        System.out.println(pageInfo);;
    }

    */
/**
     * 测试JWT生成Token与解析Tocken
     *//*

    @Test
    public void testJWTCreateToken(){
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.SECOND,60);
//
//        String s = Jwts.builder()
//                .setExpiration(calendar.getTime())
//                .setId("2")
//                .signWith(SignatureAlgorithm.HS256,"polaris")
//                .compact();
//        System.out.println(s);
        Map<String,Object> map = new HashMap<>();
        map.put("name","polaris");
        System.out.println(JwtUtil.getToken(map));
    }
    @Test
    public void testJWTCreateToken2(){
//        Claims claims = Jwts.parser()
//                .setSigningKey("polaris")
//                .parseClaimsJws("eyJhbGciOiJIUzI1NiJ9" +
//                ".eyJleHAiOjE2MDI0OTM5MzAsImp0aS" +
//                "I6IjIifQ.QvZjy99g4jv-eTecqvFN8ZNfsf34_opiWLP9LpHonog")
//                .getBody();
//        //Mon Oct 12 17:12:10 CST 2020===2
//        System.out.println(claims.getExpiration() + "===" + claims.getId());
        Claims claims = JwtUtil.parseToken("eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoicG9sYXJpcyIsImV4cCI6MTYwMjQ5NDg5MX0.Ae1INJ1MxbCKGOg2FrfSLm3dHBfoa7Wa7lB5VQz5AP0");
        System.out.println(claims.getExpiration() + "===" + claims.get("name"));
    }

    */
/**
     * 测试Redis
     *//*

    @Test
    public void testRedis() {
        //redisUtil.set(Constants.User.KEY_CAPTCHA_CONTENT + "123",
        //      "验证码内容",60 * 10);
        System.out.println((String)redisUtil.get(Constants.User.KEY_CAPTCHA_CONTENT + "123"));
    }

    */
/**
     * 测试邮件发送
     * @throws MessagingException
     *//*

    @Test
    public void testSendEmail() throws MessagingException {
        EmailSender.subject("测试邮件发送")
                .from("polaris博客系统")
                .text("这是发送的内容：123")
                .to("polaris424@163.com")
                .send();
    }

    */
/**
     * 测试mybatis逆向工程
     *//*

    @Test
    void contextLoads() throws Exception {
        //使用java代码的形式运行MBG，在官方文档中寻找代码
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        File configFile = new File("src/main/resources/mybatisGeneratorConfig.xml");
        ConfigurationParser cp = new ConfigurationParser(warnings);
        Configuration config = cp.parseConfiguration(configFile);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
        myBatisGenerator.generate(null);
    }
}*/
