package com.mock.core.service.transaction.component.message.parser.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.common.logging.Logger;
import com.alibaba.common.logging.LoggerFactory;
import com.mock.core.service.transaction.component.ComponetHandler;
import com.mock.core.service.transaction.component.message.parser.vo.FieldParseInfo;
import com.mock.core.service.transaction.component.message.parser.vo.Message;
import com.mock.core.service.transaction.component.message.parser.vo.Type;
import com.mock.core.service.transaction.component.message.parser.vo.Value;
import com.mock.common.util.LoggerUtil;

/**
 * 这是一个中国版的8583格式标准的类，初始代码来源于MessageFactory类。
 * 
 * This class is used to create messages, either from scratch or from an
 * existing String or byte buffer. It can be configured to put default values on
 * newly created messages, and also to know what to expect when reading messages
 * from an InputStream.
 * <P>
 * The factory can be configured to know what values to set for newly created
 * messages, both from a template (useful for fields that must be set with the
 * same value for EVERY message created) and individually (for trace [field 11]
 * and message date [field 7]).
 * <P>
 * It can also be configured to know what fields to expect in incoming messages
 * (all possible values must be stated, indicating the date type for each). This
 * way the messages can be parsed from a byte buffer.
 * 
 * @author zyplanke
 * 
 */
public class MessageFactory {

    /** logger */
    protected static final Logger                           logger         = LoggerFactory
                                                                               .getLogger(ComponetHandler.class);

    private final String                                    j8583xml;
    /**
     * This map stores the message template for each message msgtypeid.
     * (msgtypeid, Message)
     */
    private Map<String, Message>                            typeTemplates  = new HashMap<String, Message>();

    /**
     * Stores the information needed to parse messages sorted by type.
     * (msgtypeid, (fieldID, fieldInfo))
     */
    private final Map<String, Map<Integer, FieldParseInfo>> parseMap       = new HashMap<String, Map<Integer, FieldParseInfo>>();

    /**
     * Stores the field numbers to be parsed, in order of appearance.
     * (msgtypeid, fieldID)
     */
    private final Map<String, List<Integer>>                parseOrder     = new HashMap<String, List<Integer>>();

    /**
     * The 8583 header to be included in each message msgtypeid. (msgtypeid,
     * headerlength)
     */
    private final Map<String, Integer>                      msgheadersattr = new HashMap<String, Integer>();

    /**
     * Indicates if the factory should create binary messages and also parse
     * binary messages.
     */
    private boolean                                         useBinary;

    // String MLtype;

    public String                                           TPDU;

    int                                                     MTIlength;

    String                                                  bitmap;

    String                                                  MTItype;

    private int                                             etx            = -1;

    public MessageFactory(String j8583xml, String useBinary, String TPDU, int MTIlength,
                          String MTItype, String bitmap) {
        this.MTItype = MTItype;
        this.useBinary = Boolean.valueOf(useBinary);
        this.TPDU = TPDU;
        this.MTIlength = MTIlength;
        this.bitmap = bitmap;

        this.j8583xml = j8583xml;
        InputStream ins = new ByteArrayInputStream(j8583xml.getBytes());
        try {
            ConfigParser.createFromResource(this, ins);
        } catch (IOException e) {
            LoggerUtil.info(logger, "MessageFactory init error", e);
        }
    }

    /**
     * <pre>
     *   Tells the receiver to create and parse binary messages if the flag is true.
     *   Default is false, that is, create and parse ASCII messages.
     * </pre>
     * 
     * @param flag
     */
    public void setUseBinary(boolean flag) {
        useBinary = flag;
    }

    /**
     * <pre>
     *   Returns true is the factory is set to create and parse binary messages,
     *   false if it uses ASCII messages. Default is false.
     * </pre>
     * 
     * @return
     */
    public boolean getUseBinary() {
        return useBinary;

    }

    /**
     * <pre>
     *   Sets the ETX character to be sent at the end of the message. This is optional and the
     *   default is -1, which means nothing should be sent as terminator.
     * </pre>
     * 
     * @param value
     *            The ASCII value of the ETX character or -1 to indicate no
     *            terminator should be used.
     */
    public void setEtx(int value) {
        etx = value;
    }

    /**
     * <pre>
     *   Creates a new message of the specified type id from message template. If the factory is set to use binary
     *   messages, then the returned message will be written using binary coding.
     * </pre>
     * 
     * @param msgtypeid
     *            The message type id, 应为4个字节字符
     * @return
     */
    public Message newMessagefromTemplate(String msgtypeid) {
        Message m = new Message(msgtypeid, msgheadersattr.get(msgtypeid));
        m.setEtx(etx);
        m.setBinary(useBinary);

        // Copy the values from the template (通过报文模板来赋初值)
        Message templ = typeTemplates.get(msgtypeid);
        if (templ != null) {
            for (int i = 2; i < 128; i++) {
                if (templ.hasField(i)) {
                    m.setField(i, templ.getField(i).clone());
                }
            }
        }
        /*
        if (SystraceNumGen != null) {
            m.setValue(11, SystraceNumGen.nextTrace(), Type.NUMERIC, 6, "header");
        }
        if (usecurrentdata) {
            m.setValue(7, new Date(), Type.DATE10, 10,"header");
        }*/
        return m;
    }

    /**
     * <pre>
     *   根据请求报文生产回应报文 (回应报文的标示的第三位为：请求报文第三位加一) 
     *   
     *   Creates a message to respond to a request.     
     *   sets all fields from the template if there is one, and copies all values from the request,
     *   overwriting fields from the template if they overlap.
     * </pre>
     * 
     * @param request
     *            An 8583 message with a request type (ending in 00)
     * @return
     */
    public Message createResponse(Message request) {
        String resptypeid = request.getMsgTypeId().substring(0, 2)
                            + Integer.toString(Integer.parseInt(request.getMsgTypeId().substring(2,
                                3)) + 1) + request.getMsgTypeId().substring(3, 4);
        Message resp = new Message(resptypeid, msgheadersattr.get(resptypeid));

        resp.setBinary(request.isBinary());
        resp.setEtx(etx);
        // Copy the values from the template
        Message templ = typeTemplates.get(resp.getMsgTypeId());
        if (templ != null) {
            for (int i = 2; i < 128; i++) {
                if (templ.hasField(i)) {
                    resp.setField(i, templ.getField(i).clone());
                }
            }
        }
        // copy the values from request message
        for (int i = 2; i < 128; i++) {
            if (request.hasField(i)) {
                resp.setField(i, request.getField(i).clone());
            }
        }
        return resp;
    }

    /**
     * 8583解析   parameter:buf | length | msgheaderlength | typelength
     * buf 解析源
     * length 总长度
     * msgheaderlength 报文头长度
     * typelength 消息类型长度
     */
    public Message parseMessage(byte[] buf) throws ParseException, IOException {

        int tpduLength = TPDU.length() / 2;
        Message m = getMessage(buf);
        if (MTItype.equals("ASCII")) {
            MTIlength = MTIlength / 2;
        }
        int isoHeaderLength = tpduLength + MTIlength;

        String bitset[] = bitmap.split("_");
        boolean mapBinary = bitset[0].equals("Hex") ? false : true;

        //Parse the bitmap (primary first)
        BitSet bs = new BitSet(64);
        int pos = 0;
        if (mapBinary) {
            for (int i = isoHeaderLength; i < isoHeaderLength + 8; i++) {
                int bit = 128;
                for (int b = 0; b < 8; b++) {
                    bs.set(pos++, (buf[i] & bit) != 0);
                    bit >>= 1;
                }
            }
            LoggerUtil.info(logger, "BIT map (0-64bit): " + bs);
            //Check for secondary bitmap and parse if necessary
            if (bs.get(0)) {
                for (int i = isoHeaderLength + 8; i < isoHeaderLength + 16; i++) {
                    int bit = 128;
                    for (int b = 0; b < 8; b++) {
                        bs.set(pos++, (buf[i] & bit) != 0);
                        bit >>= 1;
                    }
                }
                pos = 16 + isoHeaderLength;
                LoggerUtil.info(logger, "BIT map (64-128bit): " + bs);
            } else {
                pos = 8 + isoHeaderLength;
            }
        } else {
            try {
                for (int i = isoHeaderLength; i < isoHeaderLength + 16; i++) {
                    if (buf[i] >= '0' && buf[i] <= '9') {
                        bs.set(pos++, ((buf[i] - 48) & 8) > 0);
                        bs.set(pos++, ((buf[i] - 48) & 4) > 0);
                        bs.set(pos++, ((buf[i] - 48) & 2) > 0);
                        bs.set(pos++, ((buf[i] - 48) & 1) > 0);
                    } else if (buf[i] >= 'A' && buf[i] <= 'F') {
                        bs.set(pos++, ((buf[i] - 55) & 8) > 0);
                        bs.set(pos++, ((buf[i] - 55) & 4) > 0);
                        bs.set(pos++, ((buf[i] - 55) & 2) > 0);
                        bs.set(pos++, ((buf[i] - 5) & 1) > 0);
                    } else if (buf[i] >= 'a' && buf[i] <= 'f') {
                        bs.set(pos++, ((buf[i] - 87) & 8) > 0);
                        bs.set(pos++, ((buf[i] - 87) & 4) > 0);
                        bs.set(pos++, ((buf[i] - 87) & 2) > 0);
                        bs.set(pos++, ((buf[i] - 87) & 1) > 0);
                    }
                }
                //Check for secondary bitmap and parse it if necessary
                if (bs.get(0)) {
                    for (int i = isoHeaderLength + 16; i < isoHeaderLength + 32; i++) {
                        if (buf[i] >= '0' && buf[i] <= '9') {
                            bs.set(pos++, ((buf[i] - 48) & 8) > 0);
                            bs.set(pos++, ((buf[i] - 48) & 4) > 0);
                            bs.set(pos++, ((buf[i] - 48) & 2) > 0);
                            bs.set(pos++, ((buf[i] - 48) & 1) > 0);
                        } else if (buf[i] >= 'A' && buf[i] <= 'F') {
                            bs.set(pos++, ((buf[i] - 55) & 8) > 0);
                            bs.set(pos++, ((buf[i] - 55) & 4) > 0);
                            bs.set(pos++, ((buf[i] - 55) & 2) > 0);
                            bs.set(pos++, ((buf[i] - 5) & 1) > 0);
                        } else if (buf[i] >= 'a' && buf[i] <= 'f') {
                            bs.set(pos++, ((buf[i] - 87) & 8) > 0);
                            bs.set(pos++, ((buf[i] - 87) & 4) > 0);
                            bs.set(pos++, ((buf[i] - 87) & 2) > 0);
                            bs.set(pos++, ((buf[i] - 87) & 1) > 0);
                        }
                    }
                    pos = 32 + isoHeaderLength;
                } else {
                    pos = 16 + isoHeaderLength;
                }
            } catch (NumberFormatException ex) {
                ParseException _e = new ParseException("Invalid ISO8583 bitmap", pos);
                _e.initCause(ex);
                throw _e;
            }
        }

        // Parse each field
        Map<Integer, FieldParseInfo> parseGuide = parseMap.get(m.getMsgTypeId());
        List<Integer> index = parseOrder.get(m.getMsgTypeId()); // 该类型报文应该存在的域ID集合
        for (Integer i : index) {
            FieldParseInfo fpi = parseGuide.get(i);
            if (bs.get(i - 1)) {
                @SuppressWarnings("rawtypes")
                Value val = null;
                //   if (i == 2) {
                //   val = fpi.parseCardNo(buf, pos);
                //  } else {
                val = useBinary ? fpi.parseBinary(buf, pos) : fpi.parse(buf, pos);
                // }
                m.setField(i, val);
                int valLen = val.getLength();
                LoggerUtil.info(logger, "解析第", i.intValue(), "域：", "type=", val.getType(), "\t",
                    "value=", val, "\tpos = (", pos, ")", "\tlength =", valLen);
                if (useBinary
                    && !(val.getType() == Type.ALPHA || val.getType() == Type.LLVAR
                         || val.getType() == Type.LLLVAR || val.getType() == Type.LLLVARHEX || val
                        .getType() == Type.LLVARHEX)) {
                    pos += valLen / 2 + valLen % 2;
                } else {
                    pos += valLen;
                }
                if (val.getType() == Type.LLVAR) {
                    pos += useBinary ? 1 : 2;
                } else if (val.getType() == Type.LLLVAR || val.getType() == Type.LLLVARHEX) {
                    pos += useBinary ? 2 : 3;
                }
                if (val.getType() == Type.BCD_LLVAR) {
                    pos += 1;
                } else if (val.getType() == Type.BCD_LLLVAR_BINARY
                           || val.getType() == Type.BCD_LLLVAR) {
                    pos += 2;
                }
            }
        }
        return m;
    }

    public byte[] getByteSection(byte[] buf, int stIndx, int edIndx) {
        byte[] rsBuf = new byte[edIndx - stIndx + 1];
        System.arraycopy(buf, stIndx, rsBuf, 0, edIndx - stIndx + 1);
        return rsBuf;
    }

    public Message getMessage(byte[] buf) throws ParseException, UnsupportedEncodingException {

        int tpduLength = TPDU.length() / 2;

        byte[] msgTPDUBuf = getByteSection(buf, 0, tpduLength - 1);
        LoggerUtil.info(logger, "TPDU String : ", new String(msgTPDUBuf));

        byte[] msgMTIBuf = getByteSection(buf, tpduLength, tpduLength + MTIlength - 1);
        String msgType;
        if (MTItype.equals("ASCII")) {
            msgType = CipherOperator.BCD2ASCII(msgMTIBuf, msgMTIBuf.length);
            msgType = Converts.hexStringToString(msgType);
        } else {
            msgType = CipherOperator.bcd2StrWithoutClipe(msgMTIBuf);
        }
        LoggerUtil.info(logger, "MTI String : ", new String(msgType));

        Message m = new Message(msgType, tpduLength);
        m.setBinary(useBinary);

        m.setMsgTypeId(msgType);
        //得到报文头
        m.setMessageHeaderData(0, msgTPDUBuf);
        return m;
    }

    /**
     * <pre>
     *   Sets the 8583 header to be used in each message type.
     * </pre>
     * 
     * @param value
     *            A map where the keys are the message type id and the values
     *            are the message headers length.
     */
    public void setHeaders(Map<String, Integer> value) {
        msgheadersattr.clear();
        msgheadersattr.putAll(value);
    }

    /**
     * <pre>
     *   Sets the 8583 header attr for a specific message type.
     * </pre>
     * 
     * @param msgtypeid
     *            The message type( 4 bytes)
     * @param headerlen
     *            The message header length
     */
    public void setHeaderLengthAttr(String msgtypeid, Integer headerlen) {
        msgheadersattr.put(msgtypeid, headerlen);
    }

    /**
     * <pre>
     *   Returns the 8583 header length for the specified type.
     * </pre>
     * 
     * @param msgtypeid
     * @return
     */
    public Integer getHeaderLengthAttr(String msgtypeid) {
        return msgheadersattr.get(msgtypeid);
    }

    /**
     * <pre>
     *   Adds a message template to the factory. If there was a template for the same
     *   message type id as the new one, it is overwritten.
     * </pre>
     * 
     * @param templ
     */
    public void addMessageTemplate(Message templ) {
        if (templ != null) {
            typeTemplates.put(templ.getMsgTypeId(), templ);
        }
    }

    /**
     * <pre>
     *   Removes the message template for the specified message type id
     * </pre>
     * 
     * @param msgtypeid
     */
    public void removeMessageTemplate(String msgtypeid) {
        typeTemplates.remove(msgtypeid);
    }

    /**
     * <pre>
     *   Sets a map with the fields that are to be expected when parsing a certain type of
     *   message.
     * </pre>
     * 
     * @param msgtypeid
     *            msgtypeid The message type id.
     * @param map
     *            A map of FieldParseInfo instances, each of which define what
     *            type and length of field to expect. The keys will be the field
     *            numbers.
     */
    public void setParseMap(String msgtypeid, Map<Integer, FieldParseInfo> map) {
        parseMap.put(msgtypeid, map);
        ArrayList<Integer> index = new ArrayList<Integer>();
        index.addAll(map.keySet());
        Collections.sort(index);
        parseOrder.put(msgtypeid, index);
    }

    public Map<Integer, FieldParseInfo> getParseMap(String msgtypeid) {
        return parseMap.get(msgtypeid);
    }

    /**
     * Setter method for property <tt>typeTemplates</tt>.
     * 
     * @param typeTemplates value to be assigned to property typeTemplates
     */
    public void setTypeTemplates(Map<String, Message> typeTemplates) {
        this.typeTemplates = typeTemplates;
    }

    /**
     * Getter method for property <tt>typeTemplates</tt>.
     * 
     * @return property value of typeTemplates
     */
    public Map<String, Message> getTypeTemplates() {
        return typeTemplates;
    }

    public void setParameters(String useBinary, String TPDU, int MTIlength, String bitmap,
                              String MTItype) {
        this.useBinary = Boolean.valueOf(useBinary);
        this.TPDU = TPDU;
        this.MTIlength = MTIlength;
        this.MTItype = MTItype;
        this.bitmap = bitmap;
    }

    /**
     * Setter method for property <tt>j8583xml</tt>.
     * 
     * @param j8583xml value to be assigned to property j8583xml
     */
    public void setJ8583xml(String j8583xml) {
        InputStream ins = new ByteArrayInputStream(j8583xml.getBytes());
        try {
            ConfigParser.createFromResource(this, ins);
        } catch (IOException e) {
            LoggerUtil.info(logger, "MessageFactory init error", e);
        }
    }

    /**
     * Getter method for property <tt>j8583xml</tt>.
     * 
     * @return property value of j8583xml
     */
    public String getJ8583xml() {
        return j8583xml;
    }

    public byte[] build(Message messageReq) {

        // 组装原交易信息
        return buildMessageAfter(messageReq);
    }

    /**
     * <pre>
     * 组装附加的报文信息
     * </pre>
     *
     * @param message
     * @return
     */
    protected byte[] buildMessageAfter(Message message) {

        String bitset[] = bitmap.split("_");

        byte[] messageStr = message.writeInternal(bitset[0].equals("Hex") ? false : true,
            Integer.parseInt(bitset[1]), MTIlength, MTItype);

        //1. 组装报文长度部分 prepare the message length
        /*int strlength = messageStr.length;
        byte[] lengthBCD = new byte[length];
        String lengthStr = Integer.toHexString(strlength);
        
        if(lengthEncode.equals("BCD")){
            byte[] srcByte = CipherOperator.ASCII2BCD(lengthStr.getBytes(), 2);
            System.arraycopy(srcByte, 0, lengthBCD, length-1, 1);
        }
        if(lengthEncode.equals("ASCII")){
            System.arraycopy(lengthStr.getBytes(), 0, lengthBCD, length-1, 1);
        }
        if(lengthEncode.equals("ASCIIP")){
            byte[] srcByte=CipherOperator.encode(strlength);
            System.arraycopy(srcByte, 0, lengthBCD, 0, 4);
        }
        

        //2. 将报文数据和长度合并  merge the length and body
        byte[] messageSended = new byte[lengthBCD.length + messageStr.length];
        System.arraycopy(lengthBCD, 0, messageSended, 0, lengthBCD.length);
        System.arraycopy(messageStr, 0, messageSended, lengthBCD.length, messageStr.length);
        */
        return messageStr;
    }

}