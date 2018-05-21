package top.siki.weixin.docker.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wiki
 * @date 2016/11/8
 */
public interface CoreService {
    public  String processRequest(HttpServletRequest request) ;
}