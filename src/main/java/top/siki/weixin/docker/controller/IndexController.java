//package top.siki.weixin.docker.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.ModelAndView;
//
//@Controller
//public class IndexController extends BaseController{
//    @RequestMapping("/vote.do")
//    public ModelAndView listVote(@RequestParam(name="code",required=false)String code,
//                                 @RequestParam(name="state")String state) {
//
//        System.out.println("-----------------------------收到请求，请求数据为："+code+"-----------------------"+state);
//        //……………………业务代码，此处省略
//        return new ModelAndView("mypages/index", model);
//
//    }
//
//}