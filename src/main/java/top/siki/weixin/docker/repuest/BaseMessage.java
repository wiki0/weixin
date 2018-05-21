package top.siki.weixin.docker.repuest;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息基类（普通用户 -> 公众帐号）
 * @author wiki
 */
@Getter
@Setter
public class BaseMessage {
    /**
     * 开发者微信号
     */
    private String toUserName;
    /**
     * 发送方帐号（一个OpenID）
     */
    private String fromUserName;
    /**
     * 消息创建时间 （整型）
     */
    private long createTime;
    /**
     * 消息类型（text/image/location/link）
     */
    private String msgType;
    /**
     * 消息id，64位整型
     */
    private long msgId;

}