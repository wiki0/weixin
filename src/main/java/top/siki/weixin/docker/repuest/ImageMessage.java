package top.siki.weixin.docker.repuest;

import lombok.Getter;
import lombok.Setter;

/**
 * 图片消息
 * @author wiki
 */
@Getter
@Setter
public class ImageMessage extends BaseMessage {
    /**
     * 图片链接
      */
    private String picUrl;

}