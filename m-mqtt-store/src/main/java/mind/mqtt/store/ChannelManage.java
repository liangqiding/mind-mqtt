package mind.mqtt.store;

import io.netty.channel.ChannelHandlerContext;

/**
 * 连接管理
 *
 * @author qiding
 */
public class ChannelManage {

    /**
     * 关闭连接
     */
    public static void close(ChannelHandlerContext ctx) {
        ctx.close();
    }
}
