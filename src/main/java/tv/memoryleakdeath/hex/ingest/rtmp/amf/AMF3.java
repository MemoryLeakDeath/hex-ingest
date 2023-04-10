package tv.memoryleakdeath.hex.ingest.rtmp.amf;

import java.util.Date;
import java.util.LinkedHashMap;

public enum AMF3 {

    UNDEFINED(0x00), NULL(0x01), FALSE(0x02), TRUE(0x03), INTEGER(0x04), DOUBLE(0x05), STRING(0x06), XMLDOCUMENT(0x07), DATE(0x08), ARRAY(0x09), OBJECT(0x0A), XML(0x0B), BYTEARRAY(0x0C),
    VECTORINT(0x0D), VECTORUINT(0x0E), VECTORDOUBLE(0x0F), VECTOROBJECT(0x10), DICTIONARY(0x11);

    private int type;

    private AMF3(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static AMF3 fromValue(int value) {
        for (AMF3 dataType : values()) {
            if (dataType.getType() == value) {
                return dataType;
            }
        }
        return null;
    }

    public static AMF3 fromObject(final Object value) {
        if(value == null) {
            return NULL;
        } else if(value instanceof Boolean) {
            if (Boolean.TRUE.equals(value)) {
                return TRUE;
            }
            return FALSE;
        } else if (value instanceof Integer) {
            return INTEGER;
        } else if (value instanceof Double) {
            return DOUBLE;
        } else if(value instanceof String) {
            return STRING;
        } else if (value instanceof Date) {
            return DATE;
        } else if (value instanceof Object[]) {
            return ARRAY;
        } else if (value instanceof LinkedHashMap) {
            return OBJECT;
        } else if (value instanceof byte[]) {
            return BYTEARRAY;
        } else {
            return UNDEFINED;
        }
    }
}
