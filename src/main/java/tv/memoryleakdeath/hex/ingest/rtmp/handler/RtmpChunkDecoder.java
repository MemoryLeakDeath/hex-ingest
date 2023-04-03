package tv.memoryleakdeath.hex.ingest.rtmp.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.ByteToMessageDecoder;

public class RtmpChunkDecoder extends ByteToMessageDecoder {
	private static final Logger logger = LoggerFactory.getLogger(RtmpChunkDecoder.class);

	private DecodeState state = DecodeState.HEADER;

	@Override
	protected void decode(ChannelHandlerContext ctx, Buffer in) throws Exception {
		// TODO Auto-generated method stub

	}

	private enum DecodeState {
		HEADER, PAYLOAD;
	}

}
