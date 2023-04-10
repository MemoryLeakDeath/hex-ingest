package tv.memoryleakdeath.hex.ingest.rtmp.amf;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public enum AMF0 {

    NUMBER(0x00), BOOLEAN(0x01), STRING(0x02), OBJECT(0x03), NULL(0x05), ECMAARRAY(0x08), OBJECTEND(0x09), STRICTARRAY(0x0A), DATE(0x0B), LONGSTRING(0x0C), XML(0x0F), TYPEDDOCUMENT(0x10),
    SWITCHAMF3(0x11);

    private int type;

    private AMF0(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static AMF0 fromValue(int value) {
        for (AMF0 dataType : values()) {
            if (dataType.getType() == value) {
                return dataType;
            }
        }
        return null;
    }

    public static AMF0 fromObject(final Object value) {
        if(value == null) {
            return NULL;
        } else if(value instanceof Number) {
            return NUMBER;
        } else if(value instanceof Boolean) {
            return BOOLEAN;
        } else if(value instanceof String) {
            return STRING;
        } else if (value instanceof LinkedHashMap) {
            return OBJECT;
        } else if (value instanceof Map) {
            return ECMAARRAY;
        } else if (value instanceof Date) {
            return DATE;
        } else if (value instanceof Object[]) {
            return STRICTARRAY;
        } else {
            return null;
        }
    }
}
