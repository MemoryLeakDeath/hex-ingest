package tv.memoryleakdeath.hex.ingest.rtmp.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty5.buffer.Buffer;
import io.netty5.buffer.DefaultBufferAllocators;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.MessageToByteEncoder;
import tv.memoryleakdeath.hex.ingest.rtmp.messages.RtmpMessage;
import tv.memoryleakdeath.hex.ingest.rtmp.messages.RtmpSetChunkSizeMessage;

public class RtmpChunkEncoder extends MessageToByteEncoder<RtmpMessage> {
    private static final Logger logger = LoggerFactory.getLogger(RtmpChunkEncoder.class);
    private static final int DEFAULT_BUFFER_SIZE = 128;

    private int currentBufSize = DEFAULT_BUFFER_SIZE;

    @Override
    protected Buffer allocateBuffer(ChannelHandlerContext ctx, RtmpMessage msg) throws Exception {
        if (msg instanceof RtmpSetChunkSizeMessage) {
            currentBufSize = ((RtmpSetChunkSizeMessage) msg).getChunkSize();
        }
        return DefaultBufferAllocators.preferredAllocator().allocate(currentBufSize);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, RtmpMessage msg, Buffer out) throws Exception {
        // TODO Auto-generated method stub

    }

}
