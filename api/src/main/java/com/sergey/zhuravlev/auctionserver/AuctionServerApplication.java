package com.sergey.zhuravlev.auctionserver;

import com.sergey.zhuravlev.auctionserver.security.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableConfigurationProperties(AppProperties.class)
public class AuctionServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionServerApplication.class, args);
    }

}
