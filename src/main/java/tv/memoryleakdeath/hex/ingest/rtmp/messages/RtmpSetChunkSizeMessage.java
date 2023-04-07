package tv.memoryleakdeath.hex.ingest.rtmp.messages;

import java.nio.ByteBuffer;

import tv.memoryleakdeath.hex.ingest.rtmp.pojo.RtmpHeader;

public class RtmpSetChunkSizeMessage implements RtmpMessage {

    private static final long serialVersionUID = 1L;
    public static final int MESSAGE_TYPE = 1;

    private int chunkSize;

    @Override
    public RtmpMessage decode(RtmpHeader header, ByteBuffer payload) {
        throw new UnsupportedOperationException("Rtmp SetChunkSize message decode not supported!");
    }

    @Override
    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(chunkSize);
        return buf;
    }

    @Override
    public int getMessageType() {
        return MESSAGE_TYPE;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

}
