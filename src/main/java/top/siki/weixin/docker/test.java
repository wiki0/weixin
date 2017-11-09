package top.siki.weixin.docker;




import com.google.gson.*;

import java.util.HashMap;
import java.util.Map;

public class test {
    public static void main(String[] args) {
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
}
