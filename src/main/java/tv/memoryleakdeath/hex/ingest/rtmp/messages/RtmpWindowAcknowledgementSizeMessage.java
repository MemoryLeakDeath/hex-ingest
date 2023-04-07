package tv.memoryleakdeath.hex.ingest.rtmp.messages;

import java.nio.ByteBuffer;

import tv.memoryleakdeath.hex.ingest.rtmp.pojo.RtmpHeader;

public class RtmpWindowAcknowledgementSizeMessage implements RtmpMessage {

    private static final long serialVersionUID = 1L;
    public static final int MESSAGE_TYPE = 5;

    private int windowSize;

    @Override
    public RtmpMessage decode(RtmpHeader header, ByteBuffer payload) {
        throw new UnsupportedOperationException("Rtmp WindowAcknowledgementSize message decode not supported!");
    }

    @Override
    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(windowSize);
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

}
