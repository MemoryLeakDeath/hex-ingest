package tv.memoryleakdeath.hex.ingest.rtmp.messages;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tv.memoryleakdeath.hex.ingest.rtmp.pojo.RtmpHeader;

public class RtmpVideoMessage implements RtmpMessage {
    private static final Logger logger = LoggerFactory.getLogger(RtmpVideoMessage.class);

    private static final long serialVersionUID = 1L;
    public static final int MESSAGE_TYPE = 9;
    private static final int CSID = 12;

    private byte[] videoData;
    private int timestamp;
    private int timestampDelta;

    @Override
    public RtmpMessage decode(RtmpHeader header, ByteBuffer payload) {
        payload.get(videoData);
        if (header.getFormat() == 0) {
            timestamp = header.getTimestamp();
        } else if (header.getFormat() == 1 || header.getFormat() == 2) {
            timestampDelta = header.getTimestampDelta();
        } else {
            logger.error("Unknown header format: {}", header.getFormat());
        }
        return this;
    }

    @Override
    public ByteBuffer encode() {
        return ByteBuffer.wrap(videoData);
    }

    public int getCsId() {
        return CSID;
    }

    @Override
    public int getMessageType() {
        return MESSAGE_TYPE;
    }

    public byte[] getVideoData() {
        return videoData;
    }

    public void setVideoData(byte[] videoData) {
        this.videoData = videoData;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getTimestampDelta() {
        return timestampDelta;
    }

    public void setTimestampDelta(int timestampDelta) {
        this.timestampDelta = timestampDelta;
    }

}
