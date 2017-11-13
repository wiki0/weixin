package top.siki.weixin.docker.service;


import com.google.gson.JsonObject;

import java.util.Map;


public interface MenuService {
    public JsonObject getMenu(String accessToken);
    public int createMenu(Map<String, Object> menu, String accessToken);
    public int deleteMenu(String accessToken);

}