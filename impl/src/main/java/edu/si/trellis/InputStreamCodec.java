package edu.si.trellis;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.TypeCodec;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;

import org.apache.commons.io.IOUtils;

/**
 * Serializes {@link InputStream}s in Cassandra text fields.
 *
 */
class InputStreamCodec extends TypeCodec<InputStream> {

    public static final InputStreamCodec inputStreamCodec = new InputStreamCodec();

    private InputStreamCodec() {
        super(DataType.blob(), InputStream.class);
    }

    @Override
    public ByteBuffer serialize(InputStream value, ProtocolVersion protocolVersion) {
        return value == null ? null : ByteBuffer.wrap(toBytes(value));
    }

    @Override
    public InputStream deserialize(ByteBuffer bytes, ProtocolVersion protocolVersion) {
        return bytes == null ? null : new ByteBufferInputStream(bytes);
    }

    @Override
    public InputStream parse(String value) {
        return value == null ? null : new ByteArrayInputStream(value.getBytes(UTF_8));
    }

    private static byte[] toBytes(InputStream in) {
        try {
            return IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String format(InputStream in) {
        return in == null ? null : new String(toBytes(in), UTF_8);
    }
}
