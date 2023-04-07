package tv.memoryleakdeath.hex.ingest.rtmp.handler;

import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.ByteToMessageDecoder;
import tv.memoryleakdeath.hex.ingest.rtmp.pojo.RtmpHeader;

public class RtmpChunkDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(RtmpChunkDecoder.class);
    private static final int CACHED_HEADERS_TTL_MILLIS = 5_000;
    private static final int CACHED_PAYLOADS_TTL_MILLS = 5_000;
    private static final int MAX_PAYLOAD_SIZE_BYTES = 32_000;

    private DecodeState state = DecodeState.HEADER;
    private Map<Integer, RtmpHeader> cachedHeaders = new PassiveExpiringMap<>(CACHED_HEADERS_TTL_MILLIS);
    private Map<Integer, ByteBuffer> partialPayloads = new PassiveExpiringMap<>(CACHED_PAYLOADS_TTL_MILLS);
    private int payloadChunkSize = 128; // 128 is default, can be changed by SetChunkSize message
    private int currentChunkStreamId;

    @Override
    protected void decode(ChannelHandlerContext ctx, Buffer in) throws Exception {
        if (DecodeState.HEADER.equals(state)) {
            RtmpHeader header = parseHeader(in);
            if (header.getFormat() != 0) {
                finalizeHeader(header);
            } else {
                currentChunkStreamId = header.getChunkStreamId();
            }
            state = DecodeState.PAYLOAD;
        } else if (DecodeState.PAYLOAD.equals(state)) {
            if (parsePayload(in)) {
                state = DecodeState.HEADER;
            }
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

            // cache header details for other chunk formats
            cachedHeaders.put(header.getChunkStreamId(), header);

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

    private void finalizeHeader(RtmpHeader header) {
        RtmpHeader previousHeader = cachedHeaders.get(header.getChunkStreamId());
        if (previousHeader == null) {
            logger.error("HEADER DETAILS NOT FOUND IN CACHE!  Error Decoding Chunk: {}", header);
            return;
        }

        switch (header.getFormat()) {
        case 1:
            header.setMessageStreamId(previousHeader.getMessageStreamId());
            break;
        case 2:
            header.setMessageStreamId(previousHeader.getMessageStreamId());
            header.setMessageLength(previousHeader.getMessageLength());
            header.setMessageTypeId(previousHeader.getMessageTypeId());
            break;
        case 3:
            header.setMessageStreamId(previousHeader.getMessageStreamId());
            header.setMessageTypeId(previousHeader.getMessageTypeId());
            header.setTimestamp(previousHeader.getTimestamp());
            header.setTimestampDelta(previousHeader.getTimestampDelta());
            break;
        default:
            logger.error("Unknown header format, can't finalize header details for header: {}", header);
            return;
        }
        cachedHeaders.put(header.getChunkStreamId(), header);
        currentChunkStreamId = header.getChunkStreamId();
    }

    private boolean parsePayload(Buffer in) {
        RtmpHeader header = cachedHeaders.get(currentChunkStreamId);
        ByteBuffer buf = partialPayloads.get(currentChunkStreamId);
        if (buf == null) {
            buf = ByteBuffer.allocate(MAX_PAYLOAD_SIZE_BYTES);
            logger.debug("Allocated payload buffer for chunkStreamId: {} of Size: {}", currentChunkStreamId, MAX_PAYLOAD_SIZE_BYTES);
        }
        int bytesToRead = Math.min(header.getMessageLength() - buf.position(), payloadChunkSize);
        if (in.readableBytes() < bytesToRead) {
            return false;
        }
        in.readBytes(buf);
        if (buf.position() == header.getMessageLength()) {
            // decode message
        } else {
            partialPayloads.put(currentChunkStreamId, buf);
        }
        return true;
    }

    private enum DecodeState {
        HEADER, PAYLOAD;
    }

}
