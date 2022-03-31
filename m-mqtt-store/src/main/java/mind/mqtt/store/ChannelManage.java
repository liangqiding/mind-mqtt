package mind.mqtt.store;

import io.netty.channel.ChannelHandlerContext;
import mind.mqtt.store.channel.ChannelStore;

import java.util.Optional;

/**
 * 连接管理
 *
 * @author qiding
 */
public class ChannelManage {

    /**
     * 关闭连接
     */
    public static void sendByCid(String clientId, Object msg) {
        ChannelStore.getChannel(clientId);
        Optional.ofNullable(ChannelStore.getChannel(clientId))
                .ifPresent(channel -> channel.writeAndFlush(msg));
    }
}
