package top.siki.weixin.docker.service;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import top.siki.weixin.docker.response.Article;
import top.siki.weixin.docker.response.NewsMessage;
import top.siki.weixin.docker.response.TextMessage;
import top.siki.weixin.docker.util.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 核心服务类
 */
@Service("coreService")
public class CoreServiceImpl implements CoreService {

    private static Logger log = LoggerFactory.getLogger(CoreServiceImpl.class);

    /**
     * 处理微信发来的请求（包括事件的推送）
     *
     * @param request
     * @return
     */
    public  String processRequest(HttpServletRequest request) {

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
            textMessage.setCreateTime(new Date().getTime());
            textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
            textMessage.setFuncFlag(0);


            // 创建图文消息
            NewsMessage newsMessage = new NewsMessage();
            newsMessage.setToUserName(fromUserName);
            newsMessage.setFromUserName(toUserName);
            newsMessage.setCreateTime(new Date().getTime());
            newsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
            newsMessage.setFuncFlag(0);

            List<Article> articleList = new ArrayList<Article>();
            // 接收文本消息内容
            String content = requestMap.get("Content");
            // 自动回复文本消息
            if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {

                //如果用户发送表情，则回复同样表情。
                if (isQqFace(content)) {
                    respContent = content;
                    textMessage.setContent(respContent);
                    // 将文本消息对象转换成xml字符串
                    respMessage = MessageUtil.textMessageToXml(textMessage);
                } else {
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
                        case "11": {
                            //测试单图文回复
                            Article article = new Article();
                            article.setTitle("微信公众帐号开发教程Java版");
                            // 图文消息中可以使用QQ表情、符号表情
                            article.setDescription("这是测试有没有换行\n\n如果有空行就代表换行成功\n\n点击图文可以跳转到百度首页");
                            // 将图片置为空
                            article.setPicUrl("http://www.sinaimg.cn/dy/slidenews/31_img/2016_38/28380_733695_698372.jpg");
                            article.setUrl("http://www.baidu.com");
                            articleList.add(article);
                            newsMessage.setArticleCount(articleList.size());
                            newsMessage.setArticles(articleList);
                            respMessage = MessageUtil.newsMessageToXml(newsMessage);
                            break;
                        }
                        case "12": {
                            //多图文发送
                            Article article = new Article();
                            article.setTitle("紧急通知，不要捡这种钱！湛江都已经传疯了！\n");
                            article.setDescription("");
                            article.setPicUrl("http://www.sinaimg.cn/dy/slidenews/31_img/2016_38/28380_733695_698372.jpg");
                            article.setUrl("http://mp.weixin.qq.com/s?__biz=MjM5Njc2OTI4NQ==&mid=2650924309&idx=1&sn=8bb6ae54d6396c1faa9182a96f30b225&chksm=bd117e7f8a66f769dc886d38ca2d4e4e675c55e6a5e01e768b383f5859e09384e485da7bed98&scene=4#wechat_redirect");

                            Article article2 = new Article();
                            article2.setTitle("湛江谁有这种女儿，请给我来一打！");
                            article2.setDescription("");
                            article2.setPicUrl("http://www.sinaimg.cn/dy/slidenews/31_img/2016_38/28380_733695_698372.jpg");
                            article2.setUrl("http://mp.weixin.qq.com/s?__biz=MjM5Njc2OTI4NQ==&mid=2650924309&idx=2&sn=d7ffc840c7e6d91b0a1c886b16797ee9&chksm=bd117e7f8a66f7698d094c2771a1114853b97dab9c172897c3f9f982eacb6619fba5e6675ea3&scene=4#wechat_redirect");

                            Article article3 = new Article();
                            article3.setTitle("以上图片我就随意放了");
                            article3.setDescription("");
                            article3.setPicUrl("http://www.sinaimg.cn/dy/slidenews/31_img/2016_38/28380_733695_698372.jpg");
                            article3.setUrl("http://mp.weixin.qq.com/s?__biz=MjM5Njc2OTI4NQ==&mid=2650924309&idx=3&sn=63e13fe558ff0d564c0da313b7bdfce0&chksm=bd117e7f8a66f7693a26853dc65c3e9ef9495235ef6ed6c7796f1b63abf1df599aaf9b33aafa&scene=4#wechat_redirect");

                            articleList.add(article);
                            articleList.add(article2);
                            articleList.add(article3);
                            newsMessage.setArticleCount(articleList.size());
                            newsMessage.setArticles(articleList);
                            respMessage = MessageUtil.newsMessageToXml(newsMessage);
                            break;
                        }

                        case "00": {
                            //测试网址回复
                            respContent = "<a href=\"http://www.wiki2link.cn\">12.12</a>";
                            textMessage.setContent(respContent);
                            // 将文本消息对象转换成xml字符串
                            respMessage = MessageUtil.textMessageToXml(textMessage);
                            break;
                        }

                        default: {
                            respContent = "很抱歉，时无法提供此功能给您使用。\n\n回复“1”显示帮助信息";
                            textMessage.setContent(respContent);
                            // 将文本消息对象转换成xml字符串
                            respMessage = MessageUtil.textMessageToXml(textMessage);
                        }
                    }
                }
            }
            // 图片消息
            else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
//                InetAddress ip= InetAddress.getByName(this.picService);
//                String url = "http://"+ip.getHostAddress()+"/im2txt?url=" + picUrl;
//                String quote = restTemplate.getForObject(url, String.class);
//                JSONObject json = new JSONObject(quote);
//                JSONArray jsonArray = json.getJSONArray("results");
//                StringBuilder rep = new StringBuilder();
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
//                    Double num = jsonObject2.getDouble("probability");
//                    String talk =  jsonObject2.getString("sentence");
//                    rep.append(talk +" "+num+"%可能性\n");
//                }
//                respContent = rep.toString();

//                HttpHeaders headers = new HttpHeaders();
//                MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
//                headers.setContentType(type);
//                headers.add("Accept", MediaType.APPLICATION_JSON.toString());


//                StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append("{\"requests\":[{\"image\":{\"source\":{\"imageUri\":\"");
//                stringBuilder.append(picUrl);
//                stringBuilder.append("\"}},\"features\":[{\"type\":\"LABEL_DETECTION\",\"maxResults\":2},{\"type\":\"WEB_DETECTION\",\"maxResults\":3},{\"type\":\"SAFE_SEARCH_DETECTION\"}]}]}");
//                JsonObject jsonObj = new JsonParser().parse(stringBuilder.toString()).getAsJsonObject();

//                JSONObject jsonObj = new JSONObject();
//                JSONObject  requests = new JSONObject();
//
//                JSONObject image = new JSONObject();
//                JSONObject source = new JSONObject();
//                source.put("imageUri",picUrl);
//                image.put("source",source);
//                requests.put("image",image);
//
//                JSONArray features = new JSONArray();
//                JSONObject object1 = new JSONObject();
//                object1.put("type","SAFE_SEARCH_DETECTION");
//                features.put(object1);
//                JSONObject object2 = new JSONObject();
//                object2.put("type","LABEL_DETECTION");
//                object2.put("maxResults",2);
//                features.put(object2);
//                JSONObject object3 = new JSONObject();
//                object3.put("type","WEB_DETECTION");
//                object3.put("maxResults",3);
//                features.put(object3);
//                requests.put("features",features);
//
//
//                jsonObj.put("requests",requests);

//                log.info(jsonObj.toString());
//                HttpEntity<String> formEntity = new HttpEntity<String>(jsonObj.toString(), headers);
//                log.info(formEntity.toString());
//                JSONObject res = restTemplate.postForObject("https://vision.googleapis.com/v1/images:annotate?key=AIzaSyDmPxnCgbegDGs4eO8eG0Ww7C2vXq3fMac", formEntity, JSONObject.class);

//                String url = "https://vision.googleapis.com/v1/images:annotate?key=AIzaSyDmPxnCgbegDGs4eO8eG0Ww7C2vXq3fMac";
//                JSONObject res = restTemplate.postForEntity(url, jsonObj, JSONObject.class).getBody();

                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\"requests\":[{\"image\":{\"source\":{\"imageUri\":\""+picUrl+"\"}},\"features\":[{\"type\":\"LABEL_DETECTION\",\"maxResults\":4},{\"type\":\"WEB_DETECTION\",\"maxResults\":1},{\"type\":\"SAFE_SEARCH_DETECTION\"}]}]}");
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
                if (null != webDetection.get("fullMatchingImages")){
                    for (JsonElement object1 : webDetection.get("fullMatchingImages").getAsJsonArray()){
                        article.setTitle("最佳匹配");
                        uremic = object1.getAsJsonObject().get("url").getAsString();
                    }
                }else if (null != webDetection.get("visuallySimilarImages")){
                    for (JsonElement object1 : webDetection.get("visuallySimilarImages").getAsJsonArray()){
                        article.setTitle("视觉相似");
                        uremic = object1.getAsJsonObject().get("url").getAsString();
                    }
                }else if (null != webDetection.get("pagesWithMatchingImages")){
                    for (JsonElement object1 : webDetection.get("pagesWithMatchingImages").getAsJsonArray()){
                        article.setTitle("可能出处");
                        uremic = object1.getAsJsonObject().get("url").getAsString();
                    }
                }else if (null != webDetection.get("partialMatchingImages")){
                    for (JsonElement object1 : webDetection.get("partialMatchingImages").getAsJsonArray()){
                        article.setTitle("部分相似");
                        uremic = object1.getAsJsonObject().get("url").getAsString();
                    }
                }
                String picaddress = "http://www.wiki2link.top/api/show?path="+ makepic(uremic)+".jpg";
                article.setPicUrl(picaddress);//本地显示
                //原地址
                article.setUrl(uremic);

                StringBuilder repbody = new StringBuilder();
                repbody.append("成人: "+toCn(safeSearchAnnotation.getAsJsonObject().get("adult").getAsString()));
                repbody.append(" 恶搞: "+toCn(safeSearchAnnotation.getAsJsonObject().get("spoof").getAsString()));
                repbody.append(" 暴力: "+toCn(safeSearchAnnotation.getAsJsonObject().get("violence").getAsString()));
                repbody.append("\n\n包含内容: ");
                for (JsonElement object1 : object.get("labelAnnotations").getAsJsonArray()){
                    repbody.append(object1.getAsJsonObject().get("description").getAsString()+"  ");
                    repbody.append(object1.getAsJsonObject().get("score").getAsString().substring(0,4)+"\n\n");
                }

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
                respContent = "您发送的是链接消息！";textMessage.setContent(respContent);
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return respMessage;
    }
    /**
     * 判断是否是QQ表情
     *
     * @param content
     * @return
     */
    public static boolean isQqFace(String content) {
        boolean result = false;

        // 判断QQ表情的正则表达式
        String qqfaceRegex = "/::\\)|/::~|/::B|/::\\||/:8-\\)|/::<|/::$|/::X|/::Z|/::'\\(|/::-\\||/::@|/::P|/::D|/::O|/::\\(|/::\\+|/:--b|/::Q|/::T|/:,@P|/:,@-D|/::d|/:,@o|/::g|/:\\|-\\)|/::!|/::L|/::>|/::,@|/:,@f|/::-S|/:\\?|/:,@x|/:,@@|/::8|/:,@!|/:!!!|/:xx|/:bye|/:wipe|/:dig|/:handclap|/:&-\\(|/:B-\\)|/:<@|/:@>|/::-O|/:>-\\||/:P-\\(|/::'\\||/:X-\\)|/::\\*|/:@x|/:8\\*|/:pd|/:<W>|/:beer|/:basketb|/:oo|/:coffee|/:eat|/:pig|/:rose|/:fade|/:showlove|/:heart|/:break|/:cake|/:li|/:bome|/:kn|/:footb|/:ladybug|/:shit|/:moon|/:sun|/:gift|/:hug|/:strong|/:weak|/:share|/:v|/:@\\)|/:jj|/:@@|/:bad|/:lvu|/:no|/:ok|/:love|/:<L>|/:jump|/:shake|/:<O>|/:circle|/:kotow|/:turn|/:skip|/:oY|/:#-0|/:hiphot|/:kiss|/:<&|/:&>";
        Pattern p = Pattern.compile(qqfaceRegex);
        Matcher m = p.matcher(content);
        if (m.matches()) {
            result = true;
        }
        return result;
    }

    public static String toCn(String content) {
        String result = content;

        // 判断可能性
        if ("UNKNOWN".equals(content)) {
            result = "未知的";
        }else if ("UNLIKELY".equals(content)) {
            result = "不太像";
        }else if ("VERY_UNLIKELY".equals(content)) {
            result = "非常不像";
        }else if ("POSSIBLE".equals(content)) {
            result = "可能的";
        }else if ("LIKELY".equals(content)) {
            result = "很像";
        }else if ("VERY_LIKELY".equals(content)) {
            result = "非常像";
        }
        return result;
    }

    public static String makepic(String geturl) throws Exception {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String path="/tmp/"+uuid+".jpg";
        downloadPicture(geturl,path);
        return uuid;
    }

    //链接url下载图片
    private static void downloadPicture(String urlList,String path) {
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



}
