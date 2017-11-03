package top.siki.weixin.docker.util;

public class UserInfoUtil {

    //获取code的请求地址
    public static String Get_Code = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=STAT#wechat_redirect";

    //替换字符串
    public static String getCode(String APPID, String REDIRECT_URI, String SCOPE) {
        return String.format(Get_Code, APPID, REDIRECT_URI, SCOPE);
    }

    public static void main(String[] args) {
        String REDIRECT_URI = "https://www.siki.top/vote.do";
        String SCOPE = "snsapi_userinfo";
        //appId
        String appId = "wx69b8accef39ebb40";

        String getCodeUrl = getCode(appId, REDIRECT_URI, SCOPE);
        System.out.println("getCodeUrl:" + getCodeUrl);
    }
}