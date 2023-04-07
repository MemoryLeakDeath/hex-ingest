package tv.memoryleakdeath.hex.ingest.rtmp.messages;

import java.io.Serializable;
import java.nio.ByteBuffer;

import tv.memoryleakdeath.hex.ingest.rtmp.pojo.RtmpHeader;

public interface RtmpMessage extends Serializable {

    RtmpMessage decode(RtmpHeader header, ByteBuffer payload);

    ByteBuffer encode();

    int getMessageType();
}
