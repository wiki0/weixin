package top.siki.weixin.docker.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class runtest {

    public static void main(String[] args) {

        String res = "{\"responses\":[{\"labelAnnotations\":[{\"mid\":\"/m/01jwgf\",\"description\":\"product\",\"score\":0.65031695},{\"mid\":\"/m/04n4x\",\"description\":\"lens\",\"score\":0.5239135}],\"safeSearchAnnotation\":{\"adult\":\"VERY_UNLIKELY\",\"spoof\":\"VERY_UNLIKELY\",\"medical\":\"VERY_UNLIKELY\",\"violence\":\"VERY_UNLIKELY\"},\"webDetection\":{\"webEntities\":[{\"entityId\":\"/m/04n4x\",\"score\":1.059375,\"description\":\"Lens\"}],\"visuallySimilarImages\":[{\"url\":\"http://www.rockycameras.com/ekmps/shops/rockcameras/images/tamron-ad2-sp-24mm-f2.5-last-version-adaptall-2-superwide-angle-prime-lens-case-79.99-[2]-26481-p.jpg\"},{\"url\":\"http://www.rockycameras.com/ekmps/shops/rockcameras/images/tamron-ad2-sp-24mm-f2.5-last-version-adaptall-2-superwide-angle-prime-lens-case-79.99-[3]-26481-p.jpg\"},{\"url\":\"https://i.ebayimg.com/images/g/xJcAAOSwa39UzPwB/s-l300.jpg\"}]}}]}";
        //先转JsonObject
        JsonObject jsonObject = new JsonParser().parse(res).getAsJsonObject();
        //再转JsonArray 加上数据头
        JsonArray jsonArray = jsonObject.getAsJsonArray("responses");
        JsonObject object = jsonArray.get(0).getAsJsonObject();
        JsonObject webDetection = object.get("webDetection").getAsJsonObject();
        JsonObject safeSearchAnnotation = object.get("safeSearchAnnotation").getAsJsonObject();
        System.out.println(safeSearchAnnotation.getAsJsonObject().get("adult").getAsString());

        if (null != webDetection.get("webEntities")){
            for (JsonElement object1 : webDetection.get("webEntities").getAsJsonArray()){
                System.out.println(object1.getAsJsonObject().get("description").getAsString());
            }
        }

    }
}
