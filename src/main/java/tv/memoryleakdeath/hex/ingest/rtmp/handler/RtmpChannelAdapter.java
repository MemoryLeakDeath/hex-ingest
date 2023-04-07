package tv.memoryleakdeath.hex.ingest.rtmp.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty5.channel.ChannelHandlerAdapter;
import io.netty5.channel.ChannelHandlerContext;

public class RtmpChannelAdapter extends ChannelHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(RtmpChannelAdapter.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("New connection from: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Channel Disconnected: {}", ctx.channel().remoteAddress());
        ctx.fireChannelInactive();
    }

    @Override
    public void channelExceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Channel ERROR for: " + ctx.channel().remoteAddress(), cause);
    }

}
