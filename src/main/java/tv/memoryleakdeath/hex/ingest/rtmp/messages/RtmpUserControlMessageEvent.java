package tv.memoryleakdeath.hex.ingest.rtmp.messages;

import java.nio.ByteBuffer;

import tv.memoryleakdeath.hex.ingest.rtmp.pojo.RtmpHeader;

public class RtmpUserControlMessageEvent implements RtmpMessage {

    private static final long serialVersionUID = 1L;
    public static final int MESSAGE_TYPE = 4;
    private static final int CSID = 2;

    private short eventType;
    private int data;

    @Override
    public RtmpMessage decode(RtmpHeader header, ByteBuffer payload) {
        eventType = payload.getShort();
        data = payload.getInt();
        return this;
    }

    @Override
    public ByteBuffer encode() {
        ByteBuffer buf = ByteBuffer.allocate(6);
        buf.putShort(eventType);
        buf.putInt(data);
        return buf;
    }

    public int getCsId() {
        return CSID;
    }

    @Override
    public int getMessageType() {
        return MESSAGE_TYPE;
    }

    public short getEventType() {
        return eventType;
    }

    public void setEventType(short eventType) {
        this.eventType = eventType;
    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

}
