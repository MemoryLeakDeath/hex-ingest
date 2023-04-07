package tv.memoryleakdeath.hex.ingest.rtmp.messages;

import java.nio.ByteBuffer;

import tv.memoryleakdeath.hex.ingest.rtmp.pojo.RtmpHeader;

public class RtmpAbortMessage implements RtmpMessage {

    private static final long serialVersionUID = 1L;
    public static final int MESSAGE_TYPE = 2;

    private int csId;

    @Override
    public RtmpMessage decode(RtmpHeader header, ByteBuffer payload) {
        throw new UnsupportedOperationException("RTMP Abort decode not supported!");
    }

    @Override
    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(csId);
        return buf;
    }

    @Override
    public int getMessageType() {
        return MESSAGE_TYPE;
    }

    public int getCsId() {
        return csId;
    }

    public void setCsId(int csId) {
        this.csId = csId;
    }

}
