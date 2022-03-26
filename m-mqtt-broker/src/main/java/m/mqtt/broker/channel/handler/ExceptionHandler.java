package m.mqtt.broker.channel.handler;

import io.netty.channel.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.net.SocketAddress;
import java.util.Optional;


/**
 * description : TODO 异常处理
 *
 * @author : qiding
 */
@Slf4j
@ChannelHandler.Sharable
@Component
@RequiredArgsConstructor
public class ExceptionHandler extends ChannelDuplexHandler {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("exceptionCaught,断开连接:", cause);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise.addListener((ChannelFutureListener) future ->
                Optional.of(future.isSuccess())
                        .filter(b -> !b)
                        .ifPresent(b -> log.error("connect exceptionCaught", future.cause()))));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.write(msg, promise.addListener((ChannelFutureListener) future ->
                Optional.of(future.isSuccess())
                        .filter(b -> !b)
                        .ifPresent(b -> log.error("write exceptionCaught", future.cause()))));
    }
}
