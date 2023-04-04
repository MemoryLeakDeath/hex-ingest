package tv.memoryleakdeath.hex.ingest.rtmp.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.ByteToMessageDecoder;
import tv.memoryleakdeath.hex.ingest.rtmp.pojo.RtmpHeader;

public class RtmpChunkDecoder extends ByteToMessageDecoder {
	private static final Logger logger = LoggerFactory.getLogger(RtmpChunkDecoder.class);

	private DecodeState state = DecodeState.HEADER;

	@Override
	protected void decode(ChannelHandlerContext ctx, Buffer in) throws Exception {
		if (DecodeState.HEADER.equals(state)) {

		}
	}

	private RtmpHeader parseHeader(Buffer in) {
		RtmpHeader header = new RtmpHeader();
		byte formatAndCsId = in.readByte();
		header.setFormat((formatAndCsId & 0xFF) >> 6);

		// get proper chunkStreamId
		int chunkStreamId = formatAndCsId & 0x3F;
		if (chunkStreamId == 0) {
			// 2 Byte form
			chunkStreamId = (in.readByte() & 0xFF) + 64;
		} else if (chunkStreamId == 1) {
			// 3 Byte form
			byte[] read = new byte[2];
			in.readBytes(read, 0, 2);
			chunkStreamId = (read[1] & 0xFF) << 8 + (read[0] & 0xFF) + 64;
		}
		header.setChunkStreamId(chunkStreamId);

		switch (header.getFormat()) {
		case 0:
			// Chunk Format 0
			header.setTimestamp(in.readMedium());
			header.setMessageLength(in.readMedium());
			header.setMessageTypeId((short) (in.readByte() & 0xFF));
			header.setMessageStreamId(Integer.reverseBytes(in.readMedium())); // little-endian (read methods are
																				// big-endian
																			// and the *LE versions were removed in
																			// netty 5)
			// check for extended timestamp
			if (header.hasExtendedTimestamp()) {
				if (in.readableBytes() <= 4) {
					header.setExtendedTimestamp(in.readInt());
				} else {
					logger.error("Not Enough bytes in the buffer to get the extended timestamp!");
				}
			}
			break;
		case 1:
			// chunk format 1
			header.setTimestampDelta(in.readMedium());
			header.setMessageLength(in.readMedium());
			header.setMessageTypeId((short) (in.readByte() & 0xFF));

			// check for extended timestamp delta
			if (header.hasExtendedTimestamp()) {
				if (in.readableBytes() <= 4) {
					header.setExtendedTimestamp(in.readInt());
				}
			}
			break;
		case 2:
			// chunk format 2
			header.setTimestampDelta(in.readMedium());

			// check for extended timestamp delta
			if (header.hasExtendedTimestamp()) {
				if (in.readableBytes() <= 4) {
					header.setExtendedTimestamp(in.readInt());
				}
			}
			break;
		case 3:
			// chunk format 3 has no header
			break;
		default:
			logger.error("Received an unknown chunk format type: {}", header.getFormat());
			break;
		}
		return header;
	}

	private enum DecodeState {
		HEADER, PAYLOAD;
	}

}
