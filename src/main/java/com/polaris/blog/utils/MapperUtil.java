package com.polaris.blog.utils;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class MapperUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MapperUtil.applicationContext = applicationContext;
    }

    public static <E> E getMapper(Class<E> clazz){
        SqlSessionFactory bean = applicationContext.getBean(SqlSessionFactory.class);
        SqlSession sqlSession = bean.openSession();
        return sqlSession.getMapper(clazz);
    }
}
