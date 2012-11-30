package com.profiler.common.util;

import java.nio.charset.Charset;

/**
 *
 */
public class Buffer {

    public static final int BOOLEAN_FALSE = 0;
    public static final int BOOLEAN_TRUE = 1;

    public static byte[] EMPTY = new byte[0];

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private byte[] buffer;
    private int offset;

    public Buffer(int size) {
        this.buffer = new byte[size];
        this.offset = 0;
    }

    public Buffer(byte[] buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer must not be null");
        }
        this.buffer = buffer;
        this.offset = 0;
    }

    public Buffer(byte[] buffer, int offset) {
        if (buffer == null) {
            throw new NullPointerException("buffer must not be null");
        }
        this.buffer = buffer;
        this.offset = offset;
    }

    public void put1PrefixedBytes(byte[] bytes) {
        if (bytes == null) {
            put(0);
        } else {
            int length = (byte) bytes.length;
            if (length > Byte.MAX_VALUE) {
                throw new IllegalArgumentException("too large bytes:" + bytes.length);
            }
            put((byte) length);
            put(bytes);
        }
    }

    public void put2PrefixedBytes(byte[] bytes) {
        if (bytes == null) {
            put(0);
        } else {
            int length = (byte) bytes.length;
            if (length > Short.MAX_VALUE) {
                throw new IllegalArgumentException("too large bytes:" + bytes.length);
            }
            put((short) length);
            put(bytes);
        }
    }

    public void putPrefixedBytes(byte[] bytes) {
        if (bytes == null) {
            put(0);
        } else {
            put(bytes.length);
            put(bytes);
        }
    }

    public void putNullTerminatedBytes(byte[] bytes) {
        if (bytes == null) {
            put(0);
        } else {
            put(bytes);
            put((byte) 0);
        }
    }

    public byte readByte() {
        return this.buffer[offset++];
    }

    public int readUnsignedByte() {
        return readByte() & 0xff;
    }

    public boolean readBoolean() {
        byte b = readByte();
        if (b == BOOLEAN_FALSE) {
            return true;
        } else {
            return false;
        }
    }

    public int readInt() {
        int i = BytesUtils.bytesToInt(buffer, offset);
        this.offset = this.offset + 4;
        return i;
    }

    public short readShort() {
        short i = BytesUtils.bytesToShort(buffer, offset);
        this.offset = this.offset + 2;
        return i;
    }

    public long readLong() {
        long l = BytesUtils.bytesToLong(buffer, offset);
        this.offset = this.offset + 8;
        return l;
    }

    public byte[] readPrefixedBytes() {
        int size = readInt();
        if (size == 0) {
            return EMPTY;
        }
        return readBytes(size);
    }

    public byte[] read1PrefixedBytes() {
        int b = readByte();
        if (b == 0) {
            return EMPTY;
        }
        return readBytes(b);
    }

    public byte[] read2PrefixedBytes() {
        int b = readShort();
        if (b == 0) {
            return EMPTY;
        }
        return readBytes(b);
    }

    private byte[] readBytes(int size) {
        byte[] b = new byte[size];
        System.arraycopy(buffer, offset, b, 0, size);
        this.offset = offset + size;
        return b;
    }

    public String readPrefixedString() {
        int size = readInt();
        if (size == 0) {
            return "";
        }
        return readString(size);
    }

    public String read1PrefixedString() {
        int size = readByte();
        if (size == 0) {
            return "";
        }
        return readString(size);
    }

    public String read1UnsignedPrefixedString() {
        int size = readUnsignedByte();
        if (size == 0) {
            return "";
        }
        return readString(size);
    }

    public String read2PrefixedString() {
        int size = readShort();
        if (size == 0) {
            return "";
        }
        return readString(size);
    }

    public String readNullTerminatedString() {
        int size = findNull();
        if (size == 0) {
            return "";
        } else if (size == -1) {
            throw new IllegalArgumentException("null not found");
        }
        return readString(size);
    }

    private int findNull() {
        for (int i = offset; i < buffer.length; i++) {
            byte b = this.buffer[i];
            if (b == 0) {
                return i - offset;
            }
        }
        return -1;
    }


    private String readString(int size) {
        String s = new String(buffer, offset, size, UTF8);
        this.offset = offset + size;
        return s;
    }

    public void put(byte v) {
        this.buffer[offset++] = v;
    }

    public void put(boolean v) {
        if (v) {
            this.buffer[offset++] = BOOLEAN_TRUE;
        } else {
            this.buffer[offset++] = BOOLEAN_FALSE;
        }
    }

    public void put(int v) {
        BytesUtils.writeInt(v, buffer, offset);
        this.offset = offset + 4;
    }

    public void put(short v) {
        BytesUtils.writeShort(v, buffer, offset);
        this.offset = offset + 2;
    }

    public void put(long v) {
        BytesUtils.writeLong(v, buffer, offset);
        this.offset = offset + 8;
    }

    public void put(byte[] v) {
        if (v == null) {
            throw new NullPointerException("v must not be null");
        }
        System.arraycopy(v, 0, buffer, offset, v.length);
        this.offset = offset + v.length;
    }

    public byte[] getBuffer() {
        return this.buffer;
    }

    public int getOffset() {
        return offset;
    }

    public int limit() {
        return buffer.length - offset;
    }
}
