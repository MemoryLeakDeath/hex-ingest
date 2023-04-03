package tv.memoryleakdeath.hex.ingest.rtmp.handler;

import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty5.buffer.Buffer;
import io.netty5.buffer.DefaultBufferAllocators;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.ByteToMessageDecoder;

public class RtmpHandshakeDecoder extends ByteToMessageDecoder {
	private static final Logger logger = LoggerFactory.getLogger(RtmpHandshakeDecoder.class);
	private static final int VERSION_LENGTH = 1;
	private static final int HANDSHAKE_LENGTH = 1536;
	private static final byte RTMP_VERSION = 3;

	private boolean initialHandshakeDone = false;
	private byte c0 = 0;
	private byte[] c1 = new byte[HANDSHAKE_LENGTH];

	@Override
	protected void decode(ChannelHandlerContext ctx, Buffer in) throws Exception {
		if (in.readableBytes() < VERSION_LENGTH + HANDSHAKE_LENGTH) {
			return;
		}

		if (!initialHandshakeDone) {
			c0 = in.readByte(); // Client RTMP version request
			in.readBytes(c1, 0, HANDSHAKE_LENGTH); // Client initial handshake
			logger.debug("Handshake request, c0: {}, c1: {}", c0, c1);
			Buffer responseS01 = DefaultBufferAllocators.preferredAllocator()
					.allocate(VERSION_LENGTH + HANDSHAKE_LENGTH);
			responseS01.writeByte(generateS0()); // Write S0 server RTMP version
			responseS01.writeBytes(generateS1()); // Write S1
			ctx.writeAndFlush(responseS01);
			initialHandshakeDone = true;
		} else {
			if (in.readableBytes() < HANDSHAKE_LENGTH) {
				return;
			}

			Buffer responseS2 = DefaultBufferAllocators.preferredAllocator().allocate(HANDSHAKE_LENGTH);
			responseS2.writeBytes(generateS2(c1));
			ctx.writeAndFlush(responseS2);
			c0 = 0;
			c1 = null;
			initialHandshakeDone = false;
			logger.debug("Handshake complete!");
			ctx.fireChannelReadComplete();
		}
	}

	private byte generateS0() {
		return RTMP_VERSION;
	}

	private byte[] generateS1() {
		byte[] s1 = new byte[HANDSHAKE_LENGTH];
		SecureRandom random = new SecureRandom();
		random.nextBytes(s1);
		// overwrite the first 8 bytes with zero
		for (int i = 0; i < 8; i++) {
			s1[i] = 0;
		}
		return s1;
	}

	private byte[] generateS2(byte[] c1) {
		return c1;
	}
}
