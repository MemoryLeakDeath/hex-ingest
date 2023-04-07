package tv.memoryleakdeath.hex.ingest.rtmp.messages;

import java.nio.ByteBuffer;

import tv.memoryleakdeath.hex.ingest.rtmp.pojo.RtmpHeader;

public class RtmpAcknowledgementMessage implements RtmpMessage {

    private static final long serialVersionUID = 1L;
    public static final int MESSAGE_TYPE = 3;

    private int sequenceNumber;

    @Override
    public RtmpMessage decode(RtmpHeader header, ByteBuffer payload) {
        throw new UnsupportedOperationException("Acknowledgement decode not supported!");
    }

    @Override
    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(sequenceNumber);
        return buf;
    }

    @Override
    public int getMessageType() {
        return MESSAGE_TYPE;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

}
