package tv.memoryleakdeath.hex.ingest.rtmp.messages;

import java.nio.ByteBuffer;
import java.util.List;

import tv.memoryleakdeath.hex.ingest.rtmp.amf.AMF0HelperUtil;
import tv.memoryleakdeath.hex.ingest.rtmp.pojo.RtmpHeader;

public class RtmpCommandMessage implements RtmpMessage {

    private static final long serialVersionUID = 1L;
    public static final int MESSAGE_TYPE = 20; // AMF0 = 20, AMF3 = 17
    private static final int CSID = 3;
    private static final int CHUNK_SIZE = 128;

    private List<Object> commands;

    @Override
    public RtmpMessage decode(RtmpHeader header, ByteBuffer payload) {
        commands = AMF0HelperUtil.decodeEverything(payload);
        return this;
    }

    @Override
    public ByteBuffer encode() {
        throw new UnsupportedOperationException("Rtmp command message encoding requires a value");
    }

    public ByteBuffer encode(Object value) {
        ByteBuffer buf = ByteBuffer.allocate(CHUNK_SIZE);
        AMF0HelperUtil.encode(buf, value);
        return buf;
    }

    public int getCsId() {
        return CSID;
    }

    @Override
    public int getMessageType() {
        return MESSAGE_TYPE;
    }

    public List<Object> getCommands() {
        return commands;
    }

}
