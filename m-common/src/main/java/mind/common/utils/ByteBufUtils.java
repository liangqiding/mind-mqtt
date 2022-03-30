package mind.common.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 用于ByteBuf转byte便于序列化
 *
 * @author qiding
 */
public class ByteBufUtils {

    /**
     * ByteBuf -> byte[]
     */
    public static byte[] toBytes(ByteBuf byteBuf) {
        byte[] messageBytes = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(byteBuf.readerIndex(), messageBytes);
        return messageBytes;
    }

    /**
     * byte[] -> ByteBuf
     */
    public static ByteBuf toByteBuf(byte[] payloads) {
        ByteBuf buf = null;
        try {
            buf = Unpooled.wrappedBuffer(payloads);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf;
    }

}
