package tv.memoryleakdeath.hex.ingest.rtmp.messages;

import java.nio.ByteBuffer;

import tv.memoryleakdeath.hex.ingest.rtmp.pojo.RtmpHeader;

public class RtmpSetPeerBandwidthMessage implements RtmpMessage {

    private static final long serialVersionUID = 1L;
    public static final int MESSAGE_TYPE = 6;

    private int windowSize;
    private byte limitType;

    @Override
    public RtmpMessage decode(RtmpHeader header, ByteBuffer payload) {
        throw new UnsupportedOperationException("Rtmp SetPeerBandwidth message decode not supported!");
    }

    @Override
    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(5);
        buf.putInt(windowSize).put(limitType);
        return buf;
    }

    @Override
    public int getMessageType() {
        return MESSAGE_TYPE;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public byte getLimitType() {
        return limitType;
    }

    public void setLimitType(byte limitType) {
        this.limitType = limitType;
    }

}
