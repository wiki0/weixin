package top.siki.weixin.docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author wiki
 */
@SpringBootApplication
@EnableScheduling
public class DockerWeixinApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerWeixinApplication.class, args);
    }
}
