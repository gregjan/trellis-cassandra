package edu.si.trellis.cassandra;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * An {@link InputStream} that sequentially streams two underlying streams. {@link #skip(long)} calls {@code skip} on
 * the underlying streams before defaulting to using {@link IOUtils#skip(InputStream, long)}, and
 * {@link #read(byte[], int, int)} also calls {@code read(byte[], int, int)} on the underlying streams. This is useful
 * in particular with {@link ByteArrayInputStream}s, which have very fast {@link ByteArrayInputStream#skip(long)} and
 * {@link ByteArrayInputStream#read(byte[], int, int)} implementations.
 *
 */
class SequenceInputStream extends InputStream {

    private final InputStream s1, s2;
    
    private InputStream current;
    
    public SequenceInputStream(InputStream s1, InputStream s2) {
        this.current = (this.s1 = s1);
        this.s2 = s2;
    }

    @Override
    public long skip(long n) throws IOException {
        if (current == null || n <= 0) return 0;
        long toSkip = n;
        toSkip -= current.skip(toSkip);
        toSkip -= IOUtils.skip(current, toSkip);
        if (toSkip > 0) { // we ran out of bytes to skip or read from current
            next();
            toSkip -= skip(toSkip);
        }
        return n - toSkip;
    }

    @Override
    public int read() throws IOException {
        if (current == null) return -1;
        int take = current.read();
        if (take == -1) {
            next();
            return read();
        }
        return take;
    }

    @Override
    public int read(byte b[], int offset, int length) throws IOException {
        if (offset < 0 || length < 0 || length > b.length - offset) throw new IndexOutOfBoundsException();
        if (length == 0) return 0;
        if (current == null) return -1;
        int read = current.read(b, offset, length);
        if (read <= 0) { // we couldn't get any bytes from current
            next();
            return read(b, offset, length);
        }
        return read;
    }

    private void next() throws IOException {
        if (current != null) current.close();
        current = current == s1 ? s2 : null;
    }
}