package top.siki.weixin.docker.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.siki.weixin.docker.service.CoreService;
import top.siki.weixin.docker.util.SignUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author wiki
 */
@RestController
@RequestMapping("/")
@Slf4j
public class CoreController {

    @Autowired
    private CoreService coreService;

    /**
     * 验证是否来自微信服务器的消息
     * @param signature
     * @param nonce
     * @param timestamp
     * @param echostr
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.GET)
    public String checkSignature(@RequestParam(name = "signature" ,required = false) String signature  ,
                                 @RequestParam(name = "nonce",required = false) String  nonce ,
                                 @RequestParam(name = "timestamp",required = false) String  timestamp ,
                                 @RequestParam(name = "echostr",required = false) String  echostr){
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
        if (SignUtil.checkSignature(signature, timestamp, nonce)) {
            log.info("接入成功");
            return echostr;
        }
        log.error("接入失败");
        return "";
    }

    /**
     *  调用核心业务类接收消息、处理消息跟推送消息
     * @param req
     * @return
     */
    @RequestMapping(value = "",method = RequestMethod.POST)
    public  String post(HttpServletRequest req){
        String respMessage = coreService.processRequest(req);
        return respMessage;
    }

    @RequestMapping(value = "/api/show")
    public void showPic(String path, HttpServletResponse response) {
        response.setContentType("image/jpeg; charset=GBK");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            FileInputStream inputStream = new FileInputStream("/tmp/"+path);
            byte[] buffer = new byte[1024];
            int i = -1;
            while ((i = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, i);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}