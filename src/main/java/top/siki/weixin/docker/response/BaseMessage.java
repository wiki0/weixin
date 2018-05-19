package top.siki.weixin.docker.response;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息基类（公众帐号 -> 普通用户）
 * @author admin
 */
@Getter
@Setter
public class BaseMessage {
    /**
     * 接收方帐号（收到的OpenID）
     */
    private String toUserName;
    /**
     *  开发者微信号
     */
    private String fromUserName;
    /**
     * 消息创建时间 （整型）
     */
    private long createTime;
    /**
     * 消息类型（text/music/news）
     */
    private String msgType;
    /**
     * 位0x0001被标志时，星标刚收到的消息
     */
    private int funcFlag;

}