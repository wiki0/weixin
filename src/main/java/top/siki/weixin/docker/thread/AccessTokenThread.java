package top.siki.weixin.docker.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import top.siki.weixin.docker.repuest.AccessToken;

/**
 * 定时获取微信access_token的线程
 * 在WechatMpDemoApplication中注解@EnableScheduling，在程序启动时就开启定时任务。
 * 每7200秒执行一次
 * @author wiki
 */
@Component
public class AccessTokenThread {
    private static Logger log = LoggerFactory.getLogger(AccessTokenThread.class);

    // 第三方用户唯一凭证
    public static String appid = "wxd84d3230eb686234";

    // 第三方用户唯一凭证密钥
    public static String appsecret = "77010f7f2eda6d39c37a4d70f80613ff";
    // 第三方用户唯一凭证
    public static AccessToken accessToken = null;

//    @Scheduled(fixedDelay = 2*3600*1000)
//    //7200秒执行一次
//    public void gettoken(){
//        accessToken= WeixinUtil.getAccessToken(appid,appsecret);
//        if(null!=accessToken){
//            log.info("获取成功，accessToken:"+accessToken.getToken());
//        }else {
//            log.error("获取token失败");
//        }
//    }
}