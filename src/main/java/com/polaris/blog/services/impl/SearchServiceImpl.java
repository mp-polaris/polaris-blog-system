package com.polaris.blog.services.impl;

import com.polaris.blog.pojo.Article;
import com.polaris.blog.pojo.PageList;
import com.polaris.blog.pojo.SearchResult;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.SearchService;
import com.polaris.blog.utils.Constants;
import com.polaris.blog.utils.TextUtil;
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.SimTocExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 搜索内容crud时机：
 *      搜索内容添加：文章发表的时候，即状态变为1的时候
 *      搜索内容删除：文章删除的时候，包括物理删除和修改状态删除
 *      文章内容更新：当文章阅读量更新时才更新
 */
@Service
@Transactional
public class SearchServiceImpl extends BaseService implements SearchService {
    @Autowired
    private SolrClient solrClient;

    @Override
    public ResponseResult doSearch(String keyword, int page, int size, String categoryId, Integer sort) {
        //1.检查page，size
        page = checkPage(page);
        size = checkSize(size);
        //2.分页设置
        // ① 设置每页的数量
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setRows(size);
        // ② 设置开始的位置
        //   规律：第一页->0 ,第二页->size，第三页->2*size,第n页->(n-1)*size
        int start = (page - 1) * size;
        solrQuery.setStart(start);//也可以换种写法：solrQuery.set("start",start);
        //3.设置搜索条件
        // ① 关键字
        solrQuery.set("df","search_item");
        // ② 条件过滤
        if (TextUtil.isEmpty(keyword)) {
            solrQuery.set("q","*");
        } else {
            solrQuery.set("q",keyword);
        }
        // ③ 排序
        //   4-case: 时间的升序（1）降序（2），浏览量的升序（3）降序（4）
        if (sort != null) {
            switch (sort) {
                case 1 :
                    solrQuery.setSort("blog_create_time",SolrQuery.ORDER.asc);
                    break;
                case 2 :
                    solrQuery.setSort("blog_create_time",SolrQuery.ORDER.desc);
                    break;
                case 3 :
                    solrQuery.setSort("blog_view_count",SolrQuery.ORDER.asc);
                    break;
                case 4 :
                    solrQuery.setSort("blog_view_count",SolrQuery.ORDER.desc);
                    break;
            }
        }
        // ④ 分类
        if (!TextUtil.isEmpty(categoryId)) {
            solrQuery.setFilterQueries("blog_category_id:" + categoryId);
        }
        // ⑤ 关键字高亮
        solrQuery.setHighlight(true);
        solrQuery.addHighlightField("blog_title,blog_content");
        solrQuery.setHighlightSimplePre("<font color='red>");
        solrQuery.setHighlightSimplePost("</font>");
        solrQuery.setHighlightFragsize(500);//设置size，否则会截取很少内容
        // ⑥ 设置返回字段
        solrQuery.addField("id,blog_content,blog_create_time,blog_labels,blog_url,blog_title,blog_view_count");
        //4.搜索
        try {
            QueryResponse result = solrClient.query(solrQuery);
            //处理搜索结果
            //获取到高亮内容
            Map<String, Map<String, List<String>>> highlighting = result.getHighlighting();
            //把数据转成bean类
            List<SearchResult> resultList = result.getBeans(SearchResult.class);
            for (SearchResult item : resultList) {
                Map<String, List<String>> stringListMap = highlighting.get(item.getId());
                List<String> blogContent = stringListMap.get("blog_content");
                if (blogContent != null) {
                    item.setBlogContent(blogContent.get(0));
                }
                List<String> blogTitle = stringListMap.get("blog_title");
                if (blogTitle != null) {
                    item.setBlogTitle(blogTitle.get(0));
                }
            }
            //5.返回搜索结果
            long numFound = result.getResults().getNumFound();//获取总记录数
            PageList<SearchResult> pageList = new PageList<>(page,size,numFound);
            pageList.setContents(resultList);
            return ResponseResult.FAILED("搜索成功").setData(pageList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.FAILED("搜索失败，请稍后重试");
    }

    /**
     * 向Solr中添加一篇文章
     * @param article
     */
    @Override
    public void addArticle(Article article){
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
        try {
            solrClient.add(doc);
            solrClient.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteArticle(String articleId) {
        try {
            //删除一条
            solrClient.deleteById(articleId);
            //删除所有
            //solrClient.deleteByQuery("*");
            solrClient.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateArticle(String articleId,Article article) {
        article.setId(articleId);
        this.addArticle(article);
    }
}
