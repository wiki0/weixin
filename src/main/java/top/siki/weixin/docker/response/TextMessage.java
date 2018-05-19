package top.siki.weixin.docker.response;

import lombok.Getter;
import lombok.Setter;

/**
 * 文本消息
 * @author admin
 */
@Getter
@Setter
public class TextMessage extends BaseMessage {
    /**
     * 回复的消息内容
     */
    private String content;

}