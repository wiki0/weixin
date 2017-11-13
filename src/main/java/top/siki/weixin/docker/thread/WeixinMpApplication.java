package top.siki.weixin.docker.thread;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // 这里，启用定时任务
public class WeixinMpApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeixinMpApplication.class, args);
    }
}