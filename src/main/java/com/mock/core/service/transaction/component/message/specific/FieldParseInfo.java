package com.mock.core.service.transaction.component.message.specific;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import com.mock.core.service.transaction.util.Converts;

/** 
 * 这是一个中国版的8583格式标准的类，初始代码来源于类FieldParseInfo  <P/>
 * This class contains the information needed to parse a field from a message buffer.
 *
 * @author zyplanke
 */
public class FieldParseInfo {

    private Integer    fieldid;

    private final Type type;

    //left = true; right = false
    private boolean    position = true;

    private int        length;

    private String     name;

    private String     defaultValue;

    /**
     * Creates a new instance that parses a value of the specified type, with the specified length.
     * The length is only useful for ALPHA and NUMERIC types.
     * 
     * @param t The 8583 type to be parsed.
     * @param len The length of the data to be read (useful only for ALPHA and NUMERIC types). 
     * 
     * */
    public FieldParseInfo(Integer fieldid, Type t, int len, String nam, String defVal, boolean pos) {
        if (t == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        this.fieldid = fieldid;
        type = t;
        length = len;
        name = nam;
        defaultValue = defVal;
        position = pos;
    }

    /**
     * <pre>
     * Returns the specified length for the data to be parsed.
     * </pre>
     *
     * @return
     */
    public int getLength() {
        return length;
    }

    /**
     * <pre>
     * Returns the data type for the data to be parsed.
     * </pre>
     *
     * @return
     */
    public Type getType() {
        return type;
    }

    /**
     * <pre>
     * Parses the character data from the buffer and returns the
     * FieldValue with the correct data type in it.
     * </pre>
     *
     * @param buf
     * @param pos
     * @return
     * @throws ParseException
     */
    public Value<?> parse(byte[] buf, int pos) throws ParseException {
        if (type == Type.NUMERIC || type == Type.ALPHA || type == Type.BCD) {
            return new Value<String>(type, new String(buf, pos, length), length);
        } else if (type == Type.LLVAR) {
            length = ((buf[pos] - 48) * 10) + (buf[pos + 1] - 48);
            return new Value<String>(type, new String(buf, pos + 2, length));
        } else if (type == Type.LLLVAR) {
            length = ((buf[pos] - 48) * 100) + ((buf[pos + 1] - 48) * 10) + (buf[pos + 2] - 48);
            return new Value<String>(type, new String(buf, pos + 3, length));
        } else if (type == Type.AMOUNT) {
            byte[] c = new byte[13];
            System.arraycopy(buf, pos, c, 0, 10);
            System.arraycopy(buf, pos + 10, c, 11, 2);
            c[10] = '.';
            return new Value<BigDecimal>(type, new BigDecimal(new String(c)));
        } else if (type == Type.DATE10) {
            //A SimpleDateFormat in the case of dates won't help because of the missing data
            //we have to use the current date for reference and change what comes in the buffer
            Calendar cal = Calendar.getInstance();
            //Set the month in the date
            cal.set(Calendar.MONTH, ((buf[pos] - 48) * 10) + buf[pos + 1] - 49);
            cal.set(Calendar.DATE, ((buf[pos + 2] - 48) * 10) + buf[pos + 3] - 48);
            cal.set(Calendar.HOUR_OF_DAY, ((buf[pos + 4] - 48) * 10) + buf[pos + 5] - 48);
            cal.set(Calendar.MINUTE, ((buf[pos + 6] - 48) * 10) + buf[pos + 7] - 48);
            cal.set(Calendar.SECOND, ((buf[pos + 8] - 48) * 10) + buf[pos + 9] - 48);
            if (cal.getTime().after(new Date())) {
                cal.add(Calendar.YEAR, -1);
            }
            return new Value<Date>(type, cal.getTime());
        } else if (type == Type.DATE4) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            //Set the month in the date
            cal.set(Calendar.MONTH, ((buf[pos] - 48) * 10) + buf[pos + 1] - 49);
            cal.set(Calendar.DATE, ((buf[pos + 2] - 48) * 10) + buf[pos + 3] - 48);
            if (cal.getTime().after(new Date())) {
                cal.add(Calendar.YEAR, -1);
            }
            return new Value<Date>(type, cal.getTime());
        } else if (type == Type.DATE_EXP) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.DATE, 1);
            //Set the month in the date
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - (cal.get(Calendar.YEAR) % 100)
                                   + ((buf[pos] - 48) * 10) + buf[pos + 1] - 48);
            cal.set(Calendar.MONTH, ((buf[pos + 2] - 48) * 10) + buf[pos + 3] - 49);
            return new Value<Date>(type, cal.getTime());
        } else if (type == Type.TIME) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, ((buf[pos] - 48) * 10) + buf[pos + 1] - 48);
            cal.set(Calendar.MINUTE, ((buf[pos + 2] - 48) * 10) + buf[pos + 3] - 48);
            cal.set(Calendar.SECOND, ((buf[pos + 4] - 48) * 10) + buf[pos + 5] - 48);
            return new Value<Date>(type, cal.getTime());
        }
        return null;
    }

    /**
     * <pre>
     * Parses binary data from the buffer, creating and returning an Value of the configured
     * type and length. 
     * </pre>
     *
     * @param buf
     * @param pos
     * @return
     * @throws ParseException
     */
    public Value<?> parseBinary(byte[] buf, int pos) throws ParseException {

        if (type == Type.ALPHA) {

            return new Value<String>(type, new String(buf, pos, length), length);

        } else if (type == Type.BCD || type == Type.MACBINARY) {
            //A long covers up to 18 digits

            byte[] tmpBuf = new byte[(length / 2) + (length % 2)];
            System.arraycopy(buf, pos, tmpBuf, 0, tmpBuf.length);
            return new Value<String>(type, Converts.bytesToHexString(tmpBuf), length);

        } else if (type == Type.BCD_ALPHA) {
            byte[] temp = new byte[length / 2 + length % 2];
            System.arraycopy(buf, pos, temp, 0, length / 2 + length % 2);
            return new Value(type, Converts.bcd2Str(temp), length);

            //            return new Value<String>(type, new String(buf, pos, length), length);

        } else if (type == Type.NUMERIC) {

            //A long covers up to 18 digits
            if (length < 19) {
                long l = 0;
                int power = 1;
                for (int i = pos + (length / 2) + (length % 2) - 1; i >= pos; i--) {
                    l += (buf[i] & 0x0f) * power;
                    power *= 10;
                    l += ((buf[i] & 0xf0) >> 4) * power;
                    power *= 10;
                }
                return new Value<Number>(Type.NUMERIC, l, length);
            } else {
                //Use a BigInteger
                char[] digits = new char[length];
                int start = 0;
                for (int i = pos; i < pos + (length / 2) + (length % 2); i++) {
                    digits[start++] = (char) (((buf[i] & 0xf0) >> 4) + 48);
                    digits[start++] = (char) ((buf[i] & 0x0f) + 48);
                }
                return new Value<Number>(Type.NUMERIC, new BigInteger(new String(digits)), length);
            }

        } else if (type == Type.BCD_LLVAR) {
            byte[] lenth = new byte[1];
            System.arraycopy(buf, pos, lenth, 0, 1);
            int len = Integer.parseInt(Converts.bcd2Str(lenth));
            int cut_len = len / 2 + len % 2;
            byte[] temp = new byte[cut_len];
            System.arraycopy(buf, pos + 1, temp, 0, cut_len);

            String cont = Converts.bcd2Str(temp);
            int rl = cont.length();
            String rs;
            if (rl < len) {
                len = rl;
            }
            if (position) {
                rs = cont.substring(rl - len);
            } else {
                rs = cont.substring(0, len);
            }

            return new Value(type, rs, len);

            //            return new Value<String>(type, new String(buf, pos, length), length);

        } else if (type == Type.BCD_LLLVAR) {
            byte[] lenth = new byte[2];
            System.arraycopy(buf, pos, lenth, 0, 2);
            int len = Integer.parseInt(Converts.bcd2Str(lenth));
            int cut_len = len / 2 + len % 2;
            byte[] temp = new byte[cut_len];
            System.arraycopy(buf, pos + 2, temp, 0, cut_len);
            String cont = Converts.bcdToStr(temp);
            int rl = cont.length();
            if (rl < len) {
                len = rl;
            }

            String rs;
            if (position) {
                rs = cont.substring(rl - len);
            } else {
                rs = cont.substring(0, len);
            }

            return new Value(type, rs, len);

            //            return new Value<String>(type, new String(buf, pos, length), length);

        } else if (type == Type.BCD_LLLVAR_BINARY) {
            byte[] lenth = new byte[2];
            System.arraycopy(buf, pos, lenth, 0, 2);
            int len = Integer.parseInt(Converts.bcd2Str(lenth));

            byte[] temp = new byte[len];
            System.arraycopy(buf, pos + 2, temp, 0, len);
            return new Value(type, temp, length);

            //            return new Value<String>(type, new String(buf, pos, length), length);

        } else if (type == Type.LLVAR) {

            length = (((buf[pos] & 0xf0) >> 4) * 10) + (buf[pos] & 0x0f);
            return new Value<String>(type, new String(buf, pos + 1, length));

        } else if (type == Type.LLVARHEX) {

            length = (((buf[pos] & 0xf0) >> 4) * 10) + (buf[pos] & 0x0f);

            byte[] tempBuf = new byte[length];
            System.arraycopy(buf, pos + 1, tempBuf, 0, length);
            return new Value<String>(type, Converts.bytesToHexString(tempBuf), length);

        } else if (type == Type.LLLVAR) {

            length = ((buf[pos] & 0x0f) * 100) + (((buf[pos + 1] & 0xf0) >> 4) * 10)
                     + (buf[pos + 1] & 0x0f);

            return new Value<String>(type, new String(buf, pos + 2, length), length);

        } else if (type == Type.LLLVARHEX) {
            length = ((buf[pos] & 0x0f) * 100) + (((buf[pos + 1] & 0xf0) >> 4) * 10)
                     + (buf[pos + 1] & 0x0f);
            byte[] tempBuf = new byte[length];
            System.arraycopy(buf, pos + 2, tempBuf, 0, length);
            return new Value<String>(type, Converts.bytesToHexString(tempBuf), length);

        } else if (type == Type.AMOUNT) {

            char[] digits = new char[13];
            digits[10] = '.';
            int start = 0;
            for (int i = pos; i < pos + 6; i++) {
                digits[start++] = (char) (((buf[i] & 0xf0) >> 4) + 48);
                digits[start++] = (char) ((buf[i] & 0x0f) + 48);
                if (start == 10) {
                    start++;
                }
            }
            return new Value<BigDecimal>(Type.AMOUNT, new BigDecimal(new String(digits)));
        } else if (type == Type.DATE10 || type == Type.DATE4 || type == Type.DATE_EXP
                   || type == Type.TIME) {

            int[] tens = new int[(type.getLength() / 2) + (type.getLength() % 2)];
            int start = 0;
            for (int i = pos; i < pos + tens.length; i++) {
                tens[start++] = (((buf[i] & 0xf0) >> 4) * 10) + (buf[i] & 0x0f);
            }
            Calendar cal = Calendar.getInstance();
            if (type == Type.DATE10) {
                //A SimpleDateFormat in the case of dates won't help because of the missing data
                //we have to use the current date for reference and change what comes in the buffer
                //Set the month in the date
                cal.set(Calendar.MONTH, tens[0] - 1);
                cal.set(Calendar.DATE, tens[1]);
                cal.set(Calendar.HOUR_OF_DAY, tens[2]);
                cal.set(Calendar.MINUTE, tens[3]);
                cal.set(Calendar.SECOND, tens[4]);
                if (cal.getTime().after(new Date())) {
                    cal.add(Calendar.YEAR, -1);
                }
                return new Value<Date>(type, cal.getTime());
            } else if (type == Type.DATE4) {
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                //Set the month in the date
                cal.set(Calendar.MONTH, tens[0] - 1);
                cal.set(Calendar.DATE, tens[1]);
                if (cal.getTime().after(new Date())) {
                    cal.add(Calendar.YEAR, -1);
                }
                return new Value<Date>(type, cal.getTime());
            } else if (type == Type.DATE_EXP) {
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.DATE, 1);
                //Set the month in the date
                cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - (cal.get(Calendar.YEAR) % 100)
                                       + tens[0]);
                cal.set(Calendar.MONTH, tens[1] - 1);
                return new Value<Date>(type, cal.getTime());
            } else if (type == Type.TIME) {
                cal.set(Calendar.HOUR_OF_DAY, tens[0]);
                cal.set(Calendar.MINUTE, tens[1]);
                cal.set(Calendar.SECOND, tens[2]);
                return new Value<Date>(type, cal.getTime());
            }
            return new Value<Date>(type, cal.getTime());
        }
        return null;
    }

    /**
     * <pre>
     * 处理卡号
     * </pre>
     *
     * @param buf
     * @param pos
     * @return
     * @throws ParseException
     */
    public Value<?> parseCardNo(byte[] buf, int pos) throws ParseException {
        byte[] a = new byte[] { buf[pos] };
        System.out.println("16进制卡号长度:" + Converts.bytesToHexString(a) + ",pos=" + pos);
        length = (((buf[pos] & 0xf0) >> 4) * 10) + (buf[pos] & 0x0f);
        System.out.println("卡号长度:" + length + ",pos=" + pos);

        String cardNo = Converts.bytesToHexString(buf).substring(pos * 2 + 2, pos * 2 + 2 + length);
        System.out.println(cardNo);

        //        long l = 0;
        //        long power = 1;
        //        for (int i = pos + (length / 2) + (length % 2); i > pos; i--) {
        //            l += (buf[i] & 0x0f) * power;
        //            System.out.println("la:" + l + ",i=" + i);
        //            power *= 10;
        //            System.out.println("powera:" + power + ",i=" + i);
        //            l += ((buf[i] & 0xf0) >> 4) * power;
        //            System.out.println("lb:" + l + ",i=" + i);
        //            power *= 10;
        //            System.out.println("powerb:" + power + ",i=" + i);
        //        }

        return new Value<String>(type, "" + cardNo);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFieldid() {
        return fieldid;
    }

    public void setFieldid(Integer fieldid) {
        this.fieldid = fieldid;
    }

    /**
     * Setter method for property <tt>defaultValue</tt>.
     * 
     * @param defaultValue value to be assigned to property defaultValue
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Getter method for property <tt>defaultValue</tt>.
     * 
     * @return property value of defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Setter method for property <tt>position</tt>.
     * 
     * @param position value to be assigned to property position
     */
    public void setPosition(boolean position) {
        this.position = position;
    }

    /**
     * Getter method for property <tt>position</tt>.
     * 
     * @return property value of position
     */
    public boolean isPosition() {
        return position;
    }
}
