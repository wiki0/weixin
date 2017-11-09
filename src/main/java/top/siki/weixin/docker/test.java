package top.siki.weixin.docker;




import com.google.gson.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class test {
    public static void xxx(String[] args) {
        String host = "https://dm-11.data.aliyun.com";
        String path = "/rest/160601/mt/translate.json";
        String method = "POST";
        String appcode = "1c1781d5f80b4cffbb56e05e0e2f0c85";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        Map<String, String> querys = new HashMap<String, String>();
        Map<String, String> bodys = new HashMap<String, String>();
        bodys.put("format", "text");
        bodys.put("q", "how are you , no no no");
        bodys.put("source", "en");
        bodys.put("target", "zh");


//        try {
//            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
////            System.out.println(response.toString());
////            获取response的body
//            String respones = EntityUtils.toString(response.getEntity());
//            System.out.println(respones);
//            JSONObject json = new JSONObject(respones);
//            JSONObject personObject = json.getJSONObject("data");
//            System.out.println(personObject.getString("translatedText"));
////            person.setId(personObject.getInt("id"));
////            person.setName(personObject.getString("name"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        String res = "{\"responses\":[{\"labelAnnotations\":[{\"mid\":\"/m/01jwgf\",\"description\":\"product\",\"score\":0.65031695},{\"mid\":\"/m/04n4x\",\"description\":\"lens\",\"score\":0.5239135}],\"safeSearchAnnotation\":{\"adult\":\"VERY_UNLIKELY\",\"spoof\":\"VERY_UNLIKELY\",\"medical\":\"VERY_UNLIKELY\",\"violence\":\"VERY_UNLIKELY\"},\"webDetection\":{\"webEntities\":[{\"entityId\":\"/m/04n4x\",\"score\":1.059375,\"description\":\"Lens\"}],\"visuallySimilarImages\":[{\"url\":\"http://www.rockycameras.com/ekmps/shops/rockcameras/images/tamron-ad2-sp-24mm-f2.5-last-version-adaptall-2-superwide-angle-prime-lens-case-79.99-[2]-26481-p.jpg\"},{\"url\":\"http://www.rockycameras.com/ekmps/shops/rockcameras/images/tamron-ad2-sp-24mm-f2.5-last-version-adaptall-2-superwide-angle-prime-lens-case-79.99-[3]-26481-p.jpg\"},{\"url\":\"https://i.ebayimg.com/images/g/xJcAAOSwa39UzPwB/s-l300.jpg\"}]}}]}";
        //先转JsonObject
        JsonObject jsonObject = new JsonParser().parse(res).getAsJsonObject();
        //再转JsonArray 加上数据头
        JsonArray jsonArray = jsonObject.getAsJsonArray("responses");
        JsonObject object = jsonArray.get(0).getAsJsonObject();
        JsonObject webDetection = object.get("webDetection").getAsJsonObject();
        JsonObject safeSearchAnnotation = object.get("safeSearchAnnotation").getAsJsonObject();
        System.out.println(safeSearchAnnotation.getAsJsonObject().toString());
        if (null != webDetection.get("fullMatchingImages")){
            for (JsonElement object1 : webDetection.get("fullMatchingImages").getAsJsonArray()){
                System.out.println(object1.getAsJsonObject().get("url"));
            }
        }

        if (null != webDetection.get("visuallySimilarImages")){
            for (JsonElement object1 : webDetection.get("visuallySimilarImages").getAsJsonArray()){
                System.out.println(object1.getAsJsonObject().get("url"));
            }
        }
        if (null != webDetection.get("pagesWithMatchingImages")){
            for (JsonElement object1 : webDetection.get("pagesWithMatchingImages").getAsJsonArray()){
                System.out.println(object1.getAsJsonObject().get("url"));
            }
        }
        if (null != webDetection.get("partialMatchingImages")){
            for (JsonElement object1 : webDetection.get("partialMatchingImages").getAsJsonArray()){
                System.out.println(object1.getAsJsonObject().get("url"));
            }
        }

    }


    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        //new一个URL对象
        URL url = new URL("http://www.haociju.com/uploads/allimg/170831/364-1FS1111Z1455.jpg");
        //打开链接
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        //设置请求方式为"GET"
        conn.setRequestMethod("GET");
        //超时响应时间为5秒
        conn.setConnectTimeout(5 * 1000);
        //通过输入流获取图片数据
        InputStream inStream = conn.getInputStream();
        //得到图片的二进制数据，以二进制封装得到数据，具有通用性
        byte[] data = readInputStream(inStream);
        //new一个文件对象用来保存图片，默认保存当前工程根目录
        File imageFile = new File("./src/main/resources/BeautyGirl.jpg");
        //创建输出流
        FileOutputStream outStream = new FileOutputStream(imageFile);
        //写入数据
        outStream.write(data);
        //关闭输出流
        outStream.close();
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        //创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        //每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        //使用一个输入流从buffer里把数据读取出来
        while( (len=inStream.read(buffer)) != -1 ){
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        //关闭输入流
        inStream.close();
        //把outStream里的数据写入内存
        return outStream.toByteArray();
    }
}
