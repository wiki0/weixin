package top.siki.weixin.docker.repuest;

import lombok.Getter;
import lombok.Setter;

/**
 * 文本消息
 * @author wiki
 */
@Getter
@Setter
public class TextMessage extends BaseMessage {
    /**
     * 消息内容
     *
     */
    private String content;
}