package tv.memoryleakdeath.hex.ingest.rtmp.amf;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMF0HelperUtil {
    private static final Logger logger = LoggerFactory.getLogger(AMF0HelperUtil.class);
    private static final byte BOOLEAN_TRUE = 0x01;
    private static final byte BOOLEAN_FALSE = 0x00;
    private static final byte[] OBJECT_END_MARKER = { 0x00, 0x00, 0x09 };

    private AMF0HelperUtil() {
    }

    public static Object decode(final ByteBuffer input) {
        AMF0 type = AMF0.fromValue(input.get());
        return processType(input, type);
    }

    public static List<Object> decodeEverything(final ByteBuffer input) {
        List<Object> tokens = new ArrayList<>();
        while (input.hasRemaining()) {
            tokens.add(decode(input));
        }
        return tokens;
    }

    @SuppressWarnings("unchecked")
    public static void encode(final ByteBuffer destination, final Object value) {
        AMF0 type = AMF0.fromObject(value);

        destination.put((byte) type.getType()); // write the type out
        switch (type) {
        case NUMBER:
            destination.putLong(Double.doubleToLongBits(Double.valueOf(value.toString())));
            break;
        case BOOLEAN:
            destination.put(Boolean.TRUE.equals(value) ? BOOLEAN_TRUE : BOOLEAN_FALSE);
            break;
        case STRING:
            encodeString(destination, (String) value);
            break;
        case STRICTARRAY:
            encodeArray(destination, (Object[]) value);
            break;
        case ECMAARRAY:
            destination.putInt(0); // size
        case OBJECT:
            encodeMap(destination, (Map<String, Object>) value);
            break;
        case DATE:
            encodeDate(destination, (Date) value);
            break;
        case NULL:
            break;
        default:
            logger.debug("Ignoring encode request for type: {}", type);
            break;
        }
    }

    private static Object processType(final ByteBuffer input, AMF0 type) {
        switch (type) {
        case NUMBER:
            return Double.longBitsToDouble(input.getLong());
        case BOOLEAN:
            return Boolean.valueOf(input.get() == BOOLEAN_TRUE);
        case STRING:
            return decodeString(input);
        case STRICTARRAY:
            return decodeArray(input);
        case OBJECT:
        case ECMAARRAY:
            return decodeMap(input, type);
        case DATE:
            return decodeDate(input);
        case LONGSTRING:
            return decodeLongString(input);
        case NULL:
            return null;
        default:
            logger.error("Unable to decode AMF0 message of type: {}", type);
            return null;
        }
    }

    private static String decodeString(final ByteBuffer input) {
        short stringSize = input.getShort();
        byte[] data = new byte[stringSize];
        input.get(data);
        return new String(data, StandardCharsets.UTF_8);
    }

    private static void encodeString(final ByteBuffer dest, String value) {
        dest.putShort((short) value.length());
        dest.put(value.getBytes(StandardCharsets.UTF_8));
    }

    private static Object[] decodeArray(final ByteBuffer input) {
        int size = input.getInt();
        Object array[] = new Object[size];
        for (int i = 0; i < size; i++) {
            array[i] = decode(input);
        }
        return array;
    }

    private static void encodeArray(final ByteBuffer dest, Object[] value) {
        dest.putInt(value.length);
        for (Object item : value) {
            encode(dest, item);
        }
    }

    private static Map<String, Object> decodeMap(final ByteBuffer input, AMF0 type) {
        int count = 0;
        Map<String, Object> map = new LinkedHashMap<>();

        if (type == AMF0.ECMAARRAY) {
            count = input.getInt();
        }

        int i = 0;
        byte[] checkEndMarker = new byte[3];
        while (input.hasRemaining()) {
            checkEndMarker = input.slice(input.position(), 3).array();
            if (Arrays.equals(checkEndMarker, OBJECT_END_MARKER)) {
                input.position(input.position() + 3);
                break;
            }
            map.put(decodeString(input), decode(input));
            i++;
            if (count > 0 && count == i) {
                break;
            }
        }
        return map;
    }

    private static void encodeMap(final ByteBuffer dest, Map<String, Object> value) {
        value.entrySet().forEach(entry -> {
            encodeString(dest, entry.getKey());
            encode(dest, entry.getValue());
        });
        dest.put(OBJECT_END_MARKER);
    }

    private static Date decodeDate(final ByteBuffer input) {
        long value = input.getLong();
        input.getShort(); // ignore timezone for now
        return new Date(value);
    }

    private static void encodeDate(final ByteBuffer dest, Date value) {
        dest.putLong(Double.doubleToLongBits(value.getTime()));
        dest.putShort((short) 0);
    }

    private static String decodeLongString(final ByteBuffer input) {
        int size = input.getInt();
        byte[] data = new byte[size];
        input.get(data);
        return new String(data, StandardCharsets.UTF_8);
    }
}
