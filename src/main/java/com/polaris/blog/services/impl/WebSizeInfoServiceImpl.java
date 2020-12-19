package com.polaris.blog.services.impl;

import com.polaris.blog.dao.SettingMapper;
import com.polaris.blog.pojo.Setting;
import com.polaris.blog.response.ResponseResult;
import com.polaris.blog.services.WebSizeInfoService;
import com.polaris.blog.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.ws.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class WebSizeInfoServiceImpl extends BaseService implements WebSizeInfoService {
    @Autowired
    private SnowflakeIdWorker idWorker;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ResponseResult getWebSizeTitle() {
        Setting title = MapperUtil.getMapper(SettingMapper.class).selectByKey(Constants.Setting.WEB_SIZE_TITLE);
        if (title == null) return ResponseResult.FAILED("你还没有设置网站标题");
        Map<String,String> result = new HashMap<>();
        result.put(title.getKey(),title.getValue());
        return ResponseResult.SUCCESS("获取网站Title成功").setData(result);
    }

    @Override
    public ResponseResult updateWebSizeTitle(String title) {
        if (TextUtil.isEmpty(title)) return ResponseResult.FAILED("网站标题不可以为空");
        SettingMapper settingMapper = MapperUtil.getMapper(SettingMapper.class);
        Setting titleFromDB = settingMapper.selectByKey(Constants.Setting.WEB_SIZE_TITLE);
        if (titleFromDB == null) {
            titleFromDB = new Setting();
            titleFromDB.setId(idWorker.nextId() + "");
            titleFromDB.setKey(Constants.Setting.WEB_SIZE_TITLE);
            titleFromDB.setValue(title);
            titleFromDB.setCreateTime(new Date());
            titleFromDB.setUpdateTime(new Date());
            settingMapper.insert(titleFromDB);
            return ResponseResult.SUCCESS("网站标题修改成功");
        }
        titleFromDB.setValue(title);
        titleFromDB.setUpdateTime(new Date());
        settingMapper.updateByPrimaryKey(titleFromDB);
        return ResponseResult.SUCCESS("网站标题修改成功");
    }

    @Override
    public ResponseResult getSeoInfo() {
        SettingMapper settingMapper = MapperUtil.getMapper(SettingMapper.class);
        Setting keywords = settingMapper.selectByKey(Constants.Setting.WEB_SIZE_KEYWORDS);
        Setting description = settingMapper.selectByKey(Constants.Setting.WEB_SIZE_DESCRIPTION);
        if (keywords == null) return ResponseResult.FAILED("关键字不存在");
        if (description == null) return ResponseResult.FAILED("网站描述不存在");
        Map<String,String> result = new HashMap<>();
        result.put(description.getKey(),description.getValue());
        result.put(keywords.getKey(),keywords.getValue());
        return ResponseResult.SUCCESS("SEO信息获取成功").setData(result);
    }

    @Override
    public ResponseResult updateSeoInfo(String keywords, String description) {
        if (TextUtil.isEmpty(keywords)) return ResponseResult.FAILED("关键字不可以为空");
        if (TextUtil.isEmpty(description)) return ResponseResult.FAILED("网站描述不可以为空");
        SettingMapper settingMapper = MapperUtil.getMapper(SettingMapper.class);
        Setting keywordsFromDB = settingMapper.selectByKey(Constants.Setting.WEB_SIZE_KEYWORDS);
        if(keywordsFromDB == null){
            keywordsFromDB = new Setting();
            keywordsFromDB.setId(idWorker.nextId() + "");
            keywordsFromDB.setKey(Constants.Setting.WEB_SIZE_KEYWORDS);
            keywordsFromDB.setValue(keywords);
            keywordsFromDB.setCreateTime(new Date());
            keywordsFromDB.setUpdateTime(new Date());
            settingMapper.insert(keywordsFromDB);
        }
        Setting descriptionFromDB = settingMapper.selectByKey(Constants.Setting.WEB_SIZE_DESCRIPTION);
        if(descriptionFromDB == null){
            Setting setting = this.initViewItem();
            setting.setKey(Constants.Setting.WEB_SIZE_DESCRIPTION);
            descriptionFromDB = setting;
            settingMapper.insert(descriptionFromDB);
        }
        keywordsFromDB.setValue(keywords);
        keywordsFromDB.setUpdateTime(new Date());
        settingMapper.updateByPrimaryKey(keywordsFromDB);

        descriptionFromDB.setValue(description);
        descriptionFromDB.setUpdateTime(new Date());
        settingMapper.updateByPrimaryKey(descriptionFromDB);

        return ResponseResult.SUCCESS("网站Seo信息修改成功");
    }

    /**
     * 统计网站的访问量（包括访问来源）：这里只统计文章浏览量
     * 注意：用户读取网站总访问量的时候，我们就读取redis中的，并且更新到mysql中
     * @return
     */
    @Override
    public ResponseResult getWebSizeViewCount() {
        //先从redis中拿出来
        String viewCount = (String)redisUtil.get(Constants.Setting.WEB_SIZE_VIEW_COUNT);

        SettingMapper settingMapper = MapperUtil.getMapper(SettingMapper.class);
        Setting viewCountFromDB = settingMapper.selectByKey(Constants.Setting.WEB_SIZE_VIEW_COUNT);
        if(viewCountFromDB == null){
            Setting setting = initViewItem();
            settingMapper.insert(setting);
            viewCountFromDB = setting;
        }

        if (TextUtil.isEmpty(viewCount)) {
            viewCount = viewCountFromDB.getValue();
            redisUtil.set(Constants.Setting.WEB_SIZE_VIEW_COUNT,viewCount);
        } else {
            //把redis里的跟新到数据库中
            viewCountFromDB.setValue(viewCount);
            settingMapper.updateByPrimaryKey(viewCountFromDB);
        }

        Map<String,Integer> result = new HashMap<>();
        result.put(viewCountFromDB.getKey(),Integer.valueOf(viewCount));
        return ResponseResult.SUCCESS("获取网站浏览量成功").setData(result);
    }


    /**
     * 思考：
     *      统计访问量，每个页面都统计一次
     *      直接增加一个访问量，可以刷量
     *      可以根据ip进行一些过滤，可以集成第三方的一个统计工具
     *      并发量
     *       防止攻击
     * 递增的统计：
     *      统计信息，通过redis来统计，数据在某些时机下如用户读取网站总访问量才会
     *      保存在mysql里（不会每次都更新到mysql里，当用户去获取访问量时会更新一
     *      次，平时的调用只会增加redis里的访问量）
     *   redis时机：每个页面访问的时候，如果不存在就从mysql中读取数据写到redis中
     *            如果存在就自增
     *   mysql时机：用户读取网站总访问量的时候，我们就读取redis中的，并且更新到mysql中
     *            如果redis中没有，那就读取mysql写到redis里的
     */
    @Override
    public void updateViewCount() {
        SettingMapper settingMapper = MapperUtil.getMapper(SettingMapper.class);
        //redis的更新时机
        Object viewCount = redisUtil.get(Constants.Setting.WEB_SIZE_VIEW_COUNT);
        if (viewCount == null) {
            Setting setting = settingMapper.selectByKey(Constants.Setting.WEB_SIZE_VIEW_COUNT);
            if(setting == null) {
                setting = this.initViewItem();
                settingMapper.insert(setting);
            }
            redisUtil.set(Constants.Setting.WEB_SIZE_VIEW_COUNT,setting.getValue());
        } else {
            //自增
            redisUtil.incr(Constants.Setting.WEB_SIZE_VIEW_COUNT,1);
        }
    }

    private Setting initViewItem() {
        Setting viewCount = new Setting();
        viewCount.setId(idWorker.nextId() + "");
        viewCount.setKey(Constants.Setting.WEB_SIZE_VIEW_COUNT);
        viewCount.setValue("1");
        viewCount.setCreateTime(new Date());
        viewCount.setUpdateTime(new Date());
        return viewCount;
    }
}
