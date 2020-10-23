package com.polaris.blog;

import com.google.gson.Gson;
import com.polaris.blog.utils.RedisUtil;
import com.polaris.blog.utils.SnowflakeIdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class PolarisBlogSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PolarisBlogSystemApplication.class, args);
    }

    @Bean
    public SnowflakeIdWorker snowflakeIdWorker(){
        return new SnowflakeIdWorker(0,0);
    }

    @Bean
    public RedisUtil redisUtil(){
        return new RedisUtil();
    }

    @Bean
    public Random random(){
        return new Random();
    }

    @Bean
    public Gson gson(){
        return new Gson();
    }
}
