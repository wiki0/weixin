package top.siki.weixin.docker.service;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;
import top.siki.weixin.docker.response.Article;
import top.siki.weixin.docker.response.NewsMessage;
import top.siki.weixin.docker.response.TextMessage;
import top.siki.weixin.docker.thread.DownloadThread;
import top.siki.weixin.docker.util.MessageUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 核心服务类
 *
 * @author wiki
 */
@Slf4j
@Service("coreService")
public class CoreServiceImpl implements CoreService {


    /**
     * 处理微信发来的请求（包括事件的推送）
     *
     * @param request
     * @return
     */
    @Override
    public String processRequest(HttpServletRequest request) {

        String respMessage = null;
        try {
            // 默认返回的文本消息内容
            String respContent = "请求处理异常，请稍候尝试！";
            // xml请求解析
            Map<String, String> requestMap = MessageUtil.parseXml(request);
            // 发送方帐号（open_id）
            String fromUserName = requestMap.get("FromUserName");
            // 公众帐号
            String toUserName = requestMap.get("ToUserName");
            // 消息类型
            String msgType = requestMap.get("MsgType");
            //图片地址
            String picUrl = requestMap.get("PicUrl");
            // 回复文本消息
            TextMessage textMessage = new TextMessage();
            textMessage.setToUserName(fromUserName);
            textMessage.setFromUserName(toUserName);
            textMessage.setCreateTime(getBeforeHourTime(0).getTime());
            textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
            textMessage.setFuncFlag(0);


            // 创建图文消息
            NewsMessage newsMessage = new NewsMessage();
            newsMessage.setToUserName(fromUserName);
            newsMessage.setFromUserName(toUserName);
            newsMessage.setCreateTime(getBeforeHourTime(0).getTime());
            newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
            newsMessage.setFuncFlag(0);

            List<Article> articleList = new ArrayList<>();

            // 接收文本消息内容
            String content = requestMap.get("Content");
            // 自动回复文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
                //回复固定消息
                switch (content) {
                    case "1": {
                        StringBuffer buffer = new StringBuffer();
                        buffer.append("测试：发送图片获得描述").append("\n\n");
                        buffer.append("或者您可以尝试发送表情").append("\n\n");
                        buffer.append("回复“1”显示此帮助菜单").append("\n");
                        respContent = String.valueOf(buffer);
                        textMessage.setContent(respContent);
                        respMessage = MessageUtil.textMessageToXml(textMessage);
                        break;
                    }
                    case "00": {
                        //查询更新版本
                        File f = new File("/app.jar");
                        Calendar cal = Calendar.getInstance();
                        long time = f.lastModified();
                        cal.setTimeInMillis(time);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss ");
                        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                        respContent = sdf.format(cal.getTime());
                        textMessage.setContent(respContent);
                        respMessage = MessageUtil.textMessageToXml(textMessage);
                        break;
                    }

                    default: {
                        log.info("{} 用户发送了:{}", fromUserName, content);
                        respContent = "很抱歉，时无法提供此功能给您使用。\n\n回复“1”显示帮助信息";
                        textMessage.setContent(respContent);
                        // 将文本消息对象转换成xml字符串
                        respMessage = MessageUtil.textMessageToXml(textMessage);
                    }
                }
            }
            // 图片消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
                //远程调取谷歌服务
                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"requests\":[{\"image\":{\"source\":{\"imageUri\":\"" + picUrl + "\"}},\"features\":[{\"type\":\"WEB_DETECTION\",\"maxResults\":5},{\"type\":\"SAFE_SEARCH_DETECTION\"}]}]}");
                Request request2 = new Request.Builder()
                        .url("https://vision.googleapis.com/v1/images:annotate?key=AIzaSyDmPxnCgbegDGs4eO8eG0Ww7C2vXq3fMac")
                        .post(body)
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("postman-token", "33546e9d-8c2a-fb75-e955-3be6796f0767")
                        .build();
                Response response = client.newCall(request2).execute();
                String res = response.body().string();
                //先转JsonObject
                JsonObject jsonObject = new JsonParser().parse(res).getAsJsonObject();
                //再转JsonArray 加上数据头
                JsonArray jsonArray = jsonObject.getAsJsonArray("responses");
                JsonObject object = jsonArray.get(0).getAsJsonObject();
                JsonObject webDetection = object.get("webDetection").getAsJsonObject();
                JsonObject safeSearchAnnotation = object.get("safeSearchAnnotation").getAsJsonObject();
                //测试单图文回复
                String uremic = "";
                Article article = new Article();
                if (null != webDetection.get("fullMatchingImages")) {
                    for (JsonElement object1 : webDetection.get("fullMatchingImages").getAsJsonArray()) {
                        article.setTitle("最佳匹配(确定)");
                        uremic = object1.getAsJsonObject().get("url").getAsString();
                    }
                } else if (null != webDetection.get("partialMatchingImages")) {
                    for (JsonElement object1 : webDetection.get("partialMatchingImages").getAsJsonArray()) {
                        article.setTitle("部分相似(大概)");
                        uremic = object1.getAsJsonObject().get("url").getAsString();
                    }
                } else if (null != webDetection.get("pagesWithMatchingImages")) {
                    for (JsonElement object1 : webDetection.get("pagesWithMatchingImages").getAsJsonArray()) {
                        article.setTitle("可能出处(不确定)");
                        uremic = object1.getAsJsonObject().get("url").getAsString();
                    }
                } else if (null != webDetection.get("visuallySimilarImages")) {
                    for (JsonElement object1 : webDetection.get("visuallySimilarImages").getAsJsonArray()) {
                        article.setTitle("视觉相似(猜的)");
                        uremic = object1.getAsJsonObject().get("url").getAsString();
                    }
                }

//                String picaddress = "http://47.74.153.66/api/show?path=" + makepic(uremic) + ".jpg";

                //创建一个可重用固定线程数的线程池
                ExecutorService pool = Executors.newFixedThreadPool(2);
                //创建实现了Runnable接口对象
                Thread tt1 = new DownloadThread();
                //将线程放入池中并执行
                pool.execute(tt1);
                //关闭
                pool.shutdown();
                article.setPicUrl(uremic);
                log.info(uremic);
                article.setUrl(uremic);

                StringBuilder repbody = new StringBuilder();
                String adult = safeSearchAnnotation.getAsJsonObject().get("adult").getAsString();
                String spoof = safeSearchAnnotation.getAsJsonObject().get("spoof").getAsString();
                String violence = safeSearchAnnotation.getAsJsonObject().get("violence").getAsString();

                if ("POSSIBLE".equals(adult) || "LIKELY".equals(adult) || "VERY_LIKELY".equals(adult))
                    repbody.append("成人: " + toCn(safeSearchAnnotation.getAsJsonObject().get("adult").getAsString()) + "\n\n");

                if ("POSSIBLE".equals(spoof) || "LIKELY".equals(spoof) || "VERY_LIKELY".equals(spoof))
                    repbody.append(" 恶搞: " + toCn(safeSearchAnnotation.getAsJsonObject().get("spoof").getAsString()) + "\n\n");

                if ("POSSIBLE".equals(violence) || "LIKELY".equals(violence) || "VERY_LIKELY".equals(violence))
                    repbody.append(" 暴力: " + toCn(safeSearchAnnotation.getAsJsonObject().get("violence").getAsString()) + "\n\n");

                repbody.append("包含内容:\n\n");
                for (JsonElement object1 : webDetection.get("webEntities").getAsJsonArray()) {
                    repbody.append("des: " + object1.getAsJsonObject().get("description").getAsString() + " like: ");
                    repbody.append(object1.getAsJsonObject().get("score").getAsString().substring(0, 4) + "\n\n");
                }
                repbody.delete(repbody.length() - 4, repbody.length());
                article.setDescription(repbody.toString());
                articleList.add(article);
                newsMessage.setArticleCount(articleList.size());
                newsMessage.setArticles(articleList);
                respMessage = MessageUtil.newsMessageToXml(newsMessage);

            }
            // 地理位置消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
                respContent = "您发送的是地理位置消息！";
                textMessage.setContent(respContent);
                // 将文本消息对象转换成xml字符串
                respMessage = MessageUtil.textMessageToXml(textMessage);
            }
            // 链接消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
                respContent = "您发送的是链接消息！";
                textMessage.setContent(respContent);
                // 将文本消息对象转换成xml字符串
                respMessage = MessageUtil.textMessageToXml(textMessage);
            }
            // 音频消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
                respContent = "您发送的是音频消息！";
                textMessage.setContent(respContent);
                // 将文本消息对象转换成xml字符串
                respMessage = MessageUtil.textMessageToXml(textMessage);
            }

            // 事件推送
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
                // 事件类型
                String eventType = requestMap.get("Event");
                if (eventType.equals(MessageUtil.EVENT_TYPE_VIEW)) {
                    // 对于点击菜单转网页暂时不做推送
                }
                // 订阅
                else if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    Article article = new Article();
                    article.setTitle("谢谢您的关注！");
                    article.setDescription("主要提供面向人工智能服务");
                    article.setPicUrl("http://47.74.153.66/bown.png");
                    article.setUrl("http://www.siki.top");
                    articleList.add(article);
                    newsMessage.setArticleCount(articleList.size());
                    newsMessage.setArticles(articleList);
                    respMessage = MessageUtil.newsMessageToXml(newsMessage);
                } else if (eventType.equals(MessageUtil.EVENT_TYPE_SCAN)) {
                    //测试单图文回复
                    Article article = new Article();
                    article.setTitle("这是已关注用户扫描二维码弹到的界面");
                    // 图文消息中可以使用QQ表情、符号表情
                    article.setDescription("点击图文可以跳转到百度首页");
                    // 将图片置为空
                    article.setPicUrl("http://www.sinaimg.cn/dy/slidenews/31_img/2016_38/28380_733695_698372.jpg");
                    article.setUrl("http://www.baidu.com");
                    articleList.add(article);
                    newsMessage.setArticleCount(articleList.size());
                    newsMessage.setArticles(articleList);
                    respMessage = MessageUtil.newsMessageToXml(newsMessage);
                }
                // 取消订阅
                else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                    log.info("{} 用户取消订阅", fromUserName);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return respMessage;
    }

    public static String toCn(String content) {
        String result = content;

        // 判断可能性
        if ("UNKNOWN".equals(content)) {
            result = "未知的";
        } else if ("UNLIKELY".equals(content)) {
            result = "不太像";
        } else if ("VERY_UNLIKELY".equals(content)) {
            result = "不可能";
        } else if ("POSSIBLE".equals(content)) {
            result = "可能的";
        } else if ("LIKELY".equals(content)) {
            result = "很像";
        } else if ("VERY_LIKELY".equals(content)) {
            result = "非常像";
        }
        return result;
    }

    public static String makepic(String geturl) throws Exception {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String path = "/tmp/" + uuid + ".jpg";
        downloadPicture(geturl, path);
        return uuid;
    }

    //链接url下载图片
    private static void downloadPicture(String urlList, String path) {
        URL url = null;
        try {
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取几小时前的时间
     */
    public Date getBeforeHourTime(int ihour) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss ");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - ihour);
        return calendar.getTime();
    }

}
