package tv.memoryleakdeath.hex.ingest.rtmp;

import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty5.bootstrap.ServerBootstrap;
import io.netty5.channel.Channel;
import io.netty5.channel.ChannelInitializer;
import io.netty5.channel.ChannelOption;
import io.netty5.channel.MultithreadEventLoopGroup;
import io.netty5.channel.nio.NioHandler;
import io.netty5.channel.socket.SocketChannel;
import io.netty5.channel.socket.nio.NioServerSocketChannel;
import io.netty5.handler.logging.LogLevel;
import io.netty5.handler.logging.LoggingHandler;
import tv.memoryleakdeath.hex.ingest.rtmp.handler.RtmpChannelAdapter;
import tv.memoryleakdeath.hex.ingest.rtmp.handler.RtmpHandshakeDecoder;

public class RtmpServer {
    private static final Logger logger = LoggerFactory.getLogger(RtmpServer.class);

    private int port;
    private int maxConnections;
    private int maxWorkerThreads;

    public RtmpServer(int port, int maxConnections, int maxWorkerThreads) {
        this.port = port;
        this.maxConnections = maxConnections;
        this.maxWorkerThreads = maxWorkerThreads;
    }

    public void run() throws InterruptedException {
        MultithreadEventLoopGroup mainGroup = new MultithreadEventLoopGroup(1, NioHandler.newFactory());
        MultithreadEventLoopGroup workerGroup = new MultithreadEventLoopGroup(maxWorkerThreads, NioHandler.newFactory());
        Channel channel = null;
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(mainGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, maxConnections).handler(new LoggingHandler(getClass(), LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new RtmpChannelAdapter()).addLast(new RtmpHandshakeDecoder());
                        }
                    });

            channel = bootstrap.bind(port).asStage().get();

            channel.closeFuture().asStage().sync();
        } catch (ExecutionException e) {
            logger.error("Unable to bind port: " + port, e);
        } finally {
            if (channel != null) {
                channel.close();
            }
            workerGroup.shutdownGracefully();
            mainGroup.shutdownGracefully();
        }
    }

}
