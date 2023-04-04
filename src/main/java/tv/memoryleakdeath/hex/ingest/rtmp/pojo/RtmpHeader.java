package tv.memoryleakdeath.hex.ingest.rtmp.pojo;

import java.io.Serializable;

public class RtmpHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	private int chunkStreamId; // bits 0-5 (6 bits) for IDS 2-63 OR ID=0 (2 bytes) OR ID=1 (3 bytes)
	private int format; // chunk message header format indicator
	private int timestamp; // 3 bytes type 0 chunk; if equal to 16777215 (0xFFFFFF) use the
							// extendedTimestamp field
	private int timestampDelta; // 3 bytes type 1 or 2 chunk; if equal to 0xFFFFFF use the extendedTimestamp
								// field
	private int messageLength; // 3 bytes type 0 or 1 chunk; not the same as the chunk payload length.
	private short messageTypeId; // 1 byte type 0 or 1 chunk; type of message
	private int messageStreamId; // 4 bytes type 0 chunk; message stream id (little-endian)
	private long extendedTimestamp; // complete 32-bit timestamp or timestamp delta

	public boolean hasExtendedTimestamp() {
		return (format == 0 && timestamp == 0xFFFFFF) || ((format == 1 || format == 2) && timestampDelta == 0xFFFFFF);
	}

	public int getChunkStreamId() {
		return chunkStreamId;
	}

	public void setChunkStreamId(int chunkStreamId) {
		this.chunkStreamId = chunkStreamId;
	}

	public int getFormat() {
		return format;
	}

	public void setFormat(int format) {
		this.format = format;
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

	public int getMessageLength() {
		return messageLength;
	}

	public void setMessageLength(int messageLength) {
		this.messageLength = messageLength;
	}

	public short getMessageTypeId() {
		return messageTypeId;
	}

	public void setMessageTypeId(short messageTypeId) {
		this.messageTypeId = messageTypeId;
	}

	public int getMessageStreamId() {
		return messageStreamId;
	}

	public void setMessageStreamId(int messageStreamId) {
		this.messageStreamId = messageStreamId;
	}

	public long getExtendedTimestamp() {
		return extendedTimestamp;
	}

	public void setExtendedTimestamp(long extendedTimestamp) {
		this.extendedTimestamp = extendedTimestamp;
	}

	@Override
	public String toString() {
		return "RtmpHeader [chunkStreamId=" + chunkStreamId + ", format=" + format + ", timestamp=" + timestamp
				+ ", timestampDelta=" + timestampDelta + ", messageLength=" + messageLength + ", messageTypeId="
				+ messageTypeId + ", messageStreamId=" + messageStreamId + ", extendedTimestamp=" + extendedTimestamp
				+ "]";
	}
}
