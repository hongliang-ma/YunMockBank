package com.mock.core.service.transaction.component.message.parser.vo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.common.lang.StringUtil;
import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.message.parser.util.CipherOperator;
import com.mock.core.service.transaction.component.message.parser.util.Converts;
import com.mock.core.service.transaction.component.message.parser.util.MacGenerer4PingAn;
import com.mock.common.util.LoggerUtil;

/**
 * 这是一个中国版的8583格式标准的类，初始代码来源于IsoMessage类。
 * This is the core class of the framework.
 * Contains the bitmap which is modified as fields are added/removed.
 * This class makes no assumptions as to what types belong in each field,
 * nor what fields should each different message type have; that is left
 * for the developer
 * 
 * @author zyplanke
 */
public class Message {

    /** logger */
    protected static final Logger        logger = LoggerFactory.getLogger(ComponetHandler.class);

    static final byte[]                  HEX    = new byte[] { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /** 消息类型 */
    private String                       msgTypeId;

    private final List<MyWrapper>        rs     = new ArrayList<MyWrapper>();

    /** 
     * Indicates if the message is binary-coded. 
     * 如果设置为true, 报文中的各报文域按照二进制组成报文。(报文头、报文类型标示和位图不受影响)
     * 对于中国的8583报文，一般应该设置为false。 
     * */
    private boolean                      binary;

    /** This is where the field values are stored. */
    private final Map<Integer, Value<?>> fields = new ConcurrentHashMap<Integer, Value<?>>();

    /** Stores the optional 8583 header. */
    private byte[]                       msgHeader;

    private int                          etx    = -1;

    public Message() {
        super();
    }

    /**
     * Creates a new message with the specified 8583 header attributes .
     * 
     * @param msgtypeid 
     * @param headerlength 报文头（整个报文头）的长度（单位字节）
     */
    public Message(String msgtypeid, int headerlength) {
        this.msgTypeId = msgtypeid;
        msgHeader = new byte[headerlength];
    }

    /**
     * <pre>
     * Returns the 8583 total header that this message was created with. 
     * </pre>
     *
     * @return
     */
    public byte[] getmsgHeader() {
        return msgHeader;
    }

    /**
     * <pre>
     * 设置报文头的数据，由于不同的报文报文的格式完全不同，所以直接设置报文的字节数据。
     * </pre>
     *
     * @param startindex 待设置报文头的起始字节位置。（0为第一个位置）
     * @param data 要设置的数据，（长度为data的长度，startindex和data的长度的和应小于报文头的总长度）
     * @return 是否设置成功
     */
    public boolean setMessageHeaderData(int startindex, byte[] data) {
        if (startindex + data.length > msgHeader.length) {
            return false;
        }
        for (int i = 0; i < data.length; i++) {
            msgHeader[startindex + i] = data[i];
        }
        return true;
    }

    /**
     * <pre>
     * 从报文头中取得数据
     * </pre>
     *
     * @param startindex 起始字节位置（0为第一个位置，应小于报文头的总长度）
     * @param count 需要取得的字节数（正整数） 如遇到报文尾，则取得实际能取道的最大字节数
     * @return 取得的数据（如未取得则返回null）
     */
    public byte[] getMessageHeaderData(int startindex, int count) {
        if (startindex >= msgHeader.length) {
            return null;
        }
        byte[] b = null;
        if (msgHeader.length - startindex < count) {
            b = new byte[msgHeader.length - startindex];
        } else {
            b = new byte[count];
        }
        for (int i = 0; i < b.length; i++) {
            b[i] = msgHeader[startindex + i];
        }
        return b;
    }

    /**
     * <pre>
     * Sets the ETX character, which is sent at the end of the message as a terminator.
     * Default is -1, which means no terminator is sent.
     * </pre>
     *
     * @param value
     */
    public void setEtx(int value) {
        etx = value;
    }

    /**
     * <pre>
     * Returns the stored value in the field, without converting or formatting it.
     * </pre>
     *
     * @param fieldid The field number. 1 is the secondary bitmap and is not returned as such;
     * real fields go from 2 to 128.
     * @return
     */
    public Object getObjectValue(int fieldid) {
        Value<?> v = fields.get(fieldid);
        if (v == null) {
            return null;
        }
        return v.getValue();
    }

    /**
     * <pre>
     * Returns the Value for the specified field.
     * </pre>
     *
     * @param fieldid 应该在2-128范围
     * @return
     */
    public Value<?> getField(int fieldid) {
        return fields.get(fieldid);
    }

    /**
     * <pre>
     * Stored the field in the specified index. The first field is the secondary bitmap and has index 1,
     * so the first valid value for index must be 2.
     * </pre>
     *
     * @param fieldid
     * @param field
     */
    public void setField(int fieldid, Value<?> field) {
        if (fieldid < 2 || fieldid > 128) {
            throw new IndexOutOfBoundsException("Field index must be between 2 and 128");
        }
        if (field == null) {
            fields.remove(fieldid);
        } else {
            fields.put(fieldid, field);
        }
    }

    /**
     * <pre>
     * Sets the specified value in the specified field, creating an Value internally.
     * </pre>
     *
     * @param fieldid The field number (2 to 128) 
     * @param value The value to be stored.
     * @param t The 8583 cntype.
     * @param length The length of the field, used for ALPHA and NUMERIC values only, ignored
     *               with any other type.
     */
    public void setValue(int fieldid, Object value, Type t, int length, String name, boolean pos) {
        if (fieldid < 2 || fieldid > 128) {
            throw new IndexOutOfBoundsException("Field index must be between 2 and 128");
        }
        if (value == null) {
            fields.remove(fieldid);
        } else {
            @SuppressWarnings("rawtypes")
            Value v = null;
            if (t.needsLength()) {
                v = new Value<Object>(t, value, length, pos);
            } else {
                v = new Value<Object>(t, value, pos);
            }
            if (name != null && !name.equals("")) {
                v.setName(name);
            }
            fields.put(fieldid, v);
        }
    }

    /**
     * <pre>
     * Returns true is the message has a value in the specified field.
     * </pre>
     *
     * @param fieldid The field id.
     * @return
     */
    public boolean hasField(int fieldid) {
        return fields.get(fieldid) != null;
    }

    /**
     * <pre>
     * Writes a message to a stream, after writing the specified number of bytes indicating
     * the message's length. The message will first be written to an internal memory stream
     * which will then be dumped into the specified stream. This method flushes the stream
     * after the write. There are at most three write operations to the stream: one for the
     * length header, one for the message, and the last one with for the ETX.
     * </pre>
     *
     * @param outs The stream to write the message to.
     * @param lengthBytes The size of the message tatol length header. Valid ranges are 2 to 4. （报文长度头，一搬4个字节）
     * @param radixoflengthBytes 表示整个报文长度的字节（lengthBytes）的表示进制（只能取10或16）
     * @throws IOException if there is a problem writing to the stream.
     */
    public void write(boolean mapBinary, int bitset, OutputStream outs, int lengthBytes,
                      int radixoflengthBytes, int typeLength, String MTItype) throws IOException {
        if (lengthBytes > 4) {
            throw new IllegalArgumentException("The length header can have at most 4 bytes");
        }
        byte[] body = writeInternal(mapBinary, bitset, typeLength, MTItype);

        byte[] macbinary = writeMac();
        byte[] data = new byte[body.length + macbinary.length];
        System.arraycopy(body, 0, data, 0, body.length);
        System.arraycopy(macbinary, 0, data, body.length, macbinary.length);

        int len = data.length;
        if (etx > -1) {
            len++;
        }
        if (lengthBytes >= 2) {
            if (radixoflengthBytes == 16) { // 如果以十六进制表示

                byte[] buf = new byte[lengthBytes];
                int pos = 0;
                if (lengthBytes == 4) {
                    buf[0] = (byte) ((len & 0xff000000) >> 24);
                    pos++;
                }
                if (lengthBytes > 2) {
                    buf[pos] = (byte) ((len & 0xff0000) >> 16);
                    pos++;
                }
                if (lengthBytes > 1) {
                    buf[pos] = (byte) ((len & 0xff00) >> 8);
                    pos++;
                }
                buf[pos] = (byte) (len & 0xff);
                outs.write(buf);

            } else if (radixoflengthBytes == 10) { // 如果为10进制               
                byte[] buf = new byte[lengthBytes];
                int temp = 1;
                for (int i = 0; i < lengthBytes; i++) {
                    buf[lengthBytes - 1 - i] = (byte) (0x30 + ((len / (temp)) % 10));
                    temp = temp * 10;
                }
                outs.write(buf);

            } else {
                throw new IllegalArgumentException("参数错，进制只能为10或16");
            }

        }
        outs.write(data);
        getSimpleSend(data);
        // ETX
        if (etx > -1) {
            outs.write(etx);
        }
        outs.flush();

    }

    /**
     * <pre>
     * Creates and returns a ByteBuffer with the data of the message, including the length header.
     * The returned buffer is already flipped, so it is ready to be written to a Channel.
     * </pre>
     *
     * @param lengthBytes
     * @return
     */
    public ByteBuffer writeToBuffer(boolean mapBinary, int bitset, int lengthBytes, int typeLength,
                                    String MTItype) {
        if (lengthBytes > 4) {
            throw new IllegalArgumentException("The length header can have at most 4 bytes");
        }

        byte[] data = writeInternal(mapBinary, bitset, typeLength, MTItype);
        ByteBuffer buf = ByteBuffer.allocate(lengthBytes + data.length + (etx > -1 ? 1 : 0));
        if (lengthBytes > 0) {
            int l = data.length;
            if (etx > -1) {
                l++;
            }
            byte[] bbuf = new byte[lengthBytes];
            int pos = 0;
            if (lengthBytes == 4) {
                bbuf[0] = (byte) ((l & 0xff000000) >> 24);
                pos++;
            }
            if (lengthBytes > 2) {
                bbuf[pos] = (byte) ((l & 0xff0000) >> 16);
                pos++;
            }
            if (lengthBytes > 1) {
                bbuf[pos] = (byte) ((l & 0xff00) >> 8);
                pos++;
            }
            bbuf[pos] = (byte) (l & 0xff);
            buf.put(bbuf);
        }
        buf.put(data);
        //ETX
        if (etx > -1) {
            buf.put((byte) etx);
        }
        buf.flip();
        return buf;
    }

    private byte[] getSimpleSend(byte[] data) {
        String url = "172.17.13.84#36010#" + Converts.bytesToHexString(data);
        System.out.println(url);
        return url.getBytes();
    }

    @SuppressWarnings("unchecked")
    protected byte[] writeMac() {
        ByteArrayOutputStream bmat = new ByteArrayOutputStream();
        try {
            if (binary) {

                bmat.write((Integer.parseInt(this.msgTypeId) & 0xff00) >> 8);
                bmat.write(Integer.parseInt(this.msgTypeId) & 0xff);
            } else {
                bmat.write(this.msgTypeId.getBytes());
            }
        } catch (IOException localIOException) {
        }

        ArrayList keys = new ArrayList();
        keys.addAll(this.fields.keySet());
        Collections.sort(keys);
        BitSet bs = new BitSet(64);
        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            int index = ((Integer) iter.next()).intValue() - 1;
            bs.set(index, true);
        }

        if (bs.length() > 64) {
            BitSet b2 = new BitSet(128);
            b2.or(bs);
            bs = b2;
            bs.set(0, true);
        }
        // this.printbs(bs);
        // Write bitmap to stream
        if (binary) {
            int pos = 128;
            int b = 0;
            for (int i = 0; i < bs.size(); i++) {
                if (bs.get(i)) {
                    b |= pos;
                }
                pos >>= 1;
                if (pos == 0) {
                    bmat.write(b);
                    pos = 128;
                    b = 0;
                }
            }
        } else {
            int pos = 0;
            int lim = bs.size() / 4;
            for (int i = 0; i < lim; i++) {
                int nibble = 0;
                if (bs.get(pos++)) {
                    nibble += 8;
                }
                if (bs.get(pos++)) {
                    nibble += 4;
                }
                if (bs.get(pos++)) {
                    nibble += 2;
                }
                if (bs.get(pos++)) {
                    nibble++;
                }
                bmat.write(HEX[nibble]);
            }
        }
        // 做MAC加密之前,取得加密块,屏蔽待加密域的设置.

        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            Value v = this.fields.get(iter.next());
            try {
                v.write(bmat, this.binary);
            } catch (IOException localIOException1) {
            }
        }
        return MacGenerer4PingAn.getBytesMac(bmat.toByteArray());
    }

    /**
     * <pre>
     * Writes the message to a memory buffer and returns it. The message does not include
     * the ETX character or the header length.
     * </pre>
     *
     * @return
     */
    public byte[] writeInternal(boolean mapBinary, int bitset, int typeLength, String MTItype) {

        boolean forceb2;
        if (bitset == 128) {
            forceb2 = true;
        } else {
            forceb2 = false;
        }
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        // 步骤一： 组装报文头部信息 
        try {
            // -- PART 1: TPDU
            if (msgHeader != null) {
                bout.write(msgHeader);
            }
            if (MTItype.equals("ASCII")) {
                bout.write(msgTypeId.getBytes());
            } else {
                bout.write(CipherOperator.ASCII2BCD(msgTypeId.getBytes(), typeLength * 2));
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // 步骤二：组装位图
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.addAll(fields.keySet());
        Collections.sort(keys);
        BitSet bs = new BitSet(forceb2 ? 128 : 64);

        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            int index = ((Integer) iter.next()).intValue() - 1;
            bs.set(index, true);
        }
        if (!forceb2) {
            bs.set(0);
        } else if (bs.length() > 64) {
            //Extend to 128 if needed
            BitSet b2 = new BitSet(128);
            b2.or(bs);
            bs = b2;
            bs.set(0);
        }

        if (forceb2) {
            bs.set(0, true);
        } else {
            bs.set(0, false);
        }
        // bs.set(1, false);
        // this.printbs(bs);
        // Write bitmap to stream
        if (mapBinary) {
            int pos = 128;
            int b = 0;
            for (int i = 0; i < bs.size(); i++) {
                if (bs.get(i)) {
                    b |= pos;
                }
                pos >>= 1;
                if (pos == 0) {
                    bout.write(b);
                    pos = 128;
                    b = 0;
                }
            }
        } else {
            int pos = 0;
            int lim = bs.size() / 4;
            for (int i = 0; i < lim; i++) {
                int nibble = 0;
                if (bs.get(pos++)) {
                    nibble += 8;
                }
                if (bs.get(pos++)) {
                    nibble += 4;
                }
                if (bs.get(pos++)) {
                    nibble += 2;
                }
                if (bs.get(pos++)) {
                    nibble++;
                }
                bout.write(HEX[nibble]);
            }
        }
        LoggerUtil.info(logger, "BIT map (bit): " + bs);

        // 步骤三： 组装消息域
        for (Integer i : keys) {
            Value v = fields.get(i);
            try {
                v.write(bout, binary);
                LoggerUtil.info(logger, "[Message] 组装第", i.intValue(), "域的值为", v);
            } catch (IOException ex) {
                System.out.println("[Message] 写第" + i + "域失败");
            }
        }

        return bout.toByteArray();
    }

    //    private byte[] generateMAC() {
    //        
    //    }

    /**
     * <pre>
     * 根据当前的报文内容，估计最终报文的的长度（单位为字节）
     * </pre>
     *
     * @return 估算出来的报文字节个数（含报文头、报文类型标示、位图和各个有效的报文域）
     */
    public int estimatetotalmsglength() {
        int totalmsglen = 0;
        if (msgHeader != null) {
            totalmsglen += msgHeader.length;
        }
        if (StringUtil.isNotBlank(msgTypeId)) {
            totalmsglen += msgTypeId.length();
        }

        // 位图
        ArrayList<Integer> keys = new ArrayList<Integer>();
        keys.addAll(fields.keySet());
        Collections.sort(keys);
        if (keys.get(keys.size() - 1) <= 64) {
            totalmsglen += 8;
        } else {
            totalmsglen += 16;
        }

        // 报文域 , 48域区别对待
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        for (Integer i : keys) {
            Value v = fields.get(i);
            try {
                if (i.intValue() == 48) {
                    v.writeSpecial(bout, binary, true);
                } else {
                    v.write(bout, binary);
                }
            } catch (IOException ex) {
                System.out.println("should never happen, writing to a ByteArrayOutputStream");
            }
        }
        totalmsglen += bout.toByteArray().length;
        return totalmsglen;
    }

    /** 
     * @return
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[msgTypeId=" + msgTypeId);
        Iterator it = fields.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Integer key = (Integer) entry.getKey();
            Value value = (Value) entry.getValue();
            buffer.append("," + key + "=" + value.toString());
        }
        buffer.append("]");

        return buffer.toString();
    }

    /**
     * Getter method for property <tt>msgTypeId</tt>.
     * 
     * @return property value of msgTypeId
     */
    public String getMsgTypeId() {
        return msgTypeId;
    }

    /**
     * Setter method for property <tt>msgTypeId</tt>.
     * 
     * @param msgTypeId value to be assigned to property msgTypeId
     */
    public void setMsgTypeId(String msgTypeId) {
        this.msgTypeId = msgTypeId;
    }

    /**
     * Getter method for property <tt>binary</tt>.
     * 
     * @return property value of binary
     */
    public boolean isBinary() {
        return binary;
    }

    /**
     * Setter method for property <tt>binary</tt>.
     * 
     * @param binary value to be assigned to property binary
     */
    public void setBinary(boolean isbinary) {
        this.binary = isbinary;
    }

    public List<MyWrapper> getMsgData() {
        rs.clear();
        for (Integer key : fields.keySet()) {
            Value<?> value = fields.get(key);
            rs.add(new MyWrapper(key.toString(), value.getValue().toString(), value.getName(),
                value.getType().toString(), String.valueOf(value.getLength())));
        }
        return rs;
    }

    public List<MyWrapper> getUniqueData(Integer k) {
        rs.clear();
        for (Integer key : fields.keySet()) {
            if (key == k) {
                Value<?> value = fields.get(k);
                rs.add(new MyWrapper(key.toString(), value.getValue().toString(), value.getName(),
                    value.getType().toString(), String.valueOf(value.getLength())));
            }
        }
        return rs;
    }
}