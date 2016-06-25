package com.mock.core.service.transaction.component.message.parser.vo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import com.mock.core.service.transaction.component.message.parser.util.Converts;

/**
 * 这是一个中国版的8583格式标准的类，初始源代码来自于IsoValue。
 * 
 * Represents a value that is stored in a field inside a china 8583 message.
 * It can format the value when the message is generated.
 * Some values have a fixed length, other values require a length to be specified
 * so that the value can be padded to the specified length. LLVAR and LLLVAR
 * values do not need a length specification because the length is calculated
 * from the stored value.
 * 
 * @author zyplanke
 */
public class Value<T> implements Cloneable {

    private Type    datatype;

    private T       value;

    private int     length;

    private String  name;

    //left = true; right = false
    private boolean position = true;

    public Value(Type t, T value) {
        this(t, value, true);
    }

    public Value(Type t, T val, int len) {
        this(t, val, len, true);
    }

    /** 
     * Creates a new instance that stores the specified value as the specified type.
     * Useful for storing LLVAR or LLLVAR types, as well as fixed-length value types
     * like DATE10, DATE4, AMOUNT, etc.
     * 
     * @param t the Type.
     * @param value The value to be stored. 
     */
    public Value(Type t, T value, boolean position) {

        datatype = t;
        this.value = value;
        this.position = position;
        if (t.needsLength()) {
            throw new IllegalArgumentException(
                "Fixed-value types must use constructor that specifies length");
        }
        if (datatype == Type.LLVAR || datatype == Type.LLLVAR || datatype == Type.LLLVARHEX
            || datatype == Type.LLVARHEX) {
            //不能简单的用value.toString().getLength()方法，因为当报文中存在中文字符时，一个中文字符占两个字节。
            //用value.toString().getLength()得到的是字符数，不是实际需要取得的字节数
            length = value.toString().getBytes().length;
            if (t == Type.LLVAR && length > 99) {
                throw new IllegalArgumentException("LLVAR can only hold values up to 99 chars");
            } else if (t == Type.LLLVAR && length > 999) {
                throw new IllegalArgumentException("LLLVAR can only hold values up to 999 chars");
            }
        } else if ((this.datatype == Type.BCD_LLVAR) || (this.datatype == Type.BCD_LLLVAR)
                   || (this.datatype == Type.BCD_LLLVAR_BINARY)) {

            if (value instanceof byte[]) {
                this.length = ((byte[]) value).length;
            } else {
                this.length = this.value.toString().getBytes().length;
            }
            if ((t == Type.BCD_LLVAR) && (this.length > 99))
                throw new IllegalArgumentException("LLVAR can only hold values up to 99 chars");
            if ((t == Type.BCD_LLLVAR) && (this.length > 999))
                throw new IllegalArgumentException("LLLVAR can only hold values up to 999 chars");
            if ((t == Type.BCD_LLLVAR_BINARY) && (this.length > 999))
                throw new IllegalArgumentException("LLLVAR can only hold values up to 999 chars");

        } else if ((this.datatype == Type.BCD_ALPHA)) {

            if (value instanceof byte[]) {
                this.length = ((byte[]) value).length;
            } else {
                this.length = this.value.toString().getBytes().length;
            }

        } else {
            length = datatype.getLength();
        }
    }

    /**
     * Creates a new instance that stores the specified value as the specified type.
     * Useful for storing fixed-length value types.
     * 
     * @param t
     * @param val
     * @param len
     */
    public Value(Type t, T val, int len, boolean position) {
        datatype = t;
        value = val;
        length = len;
        this.position = position;
        if (length == 0 && t.needsLength()) {
            throw new IllegalArgumentException(
                "Fixed-value types must use constructor that specifies length");
        }
        if (datatype == Type.LLVAR || datatype == Type.LLLVAR || datatype == Type.LLLVARHEX
            || datatype == Type.LLVARHEX) {
            //不能简单的用value.toString().getLength()方法，因为当报文中存在中文字符时，一个中文字符占两个字节。
            //用value.toString().getLength()得到的是字符数，不是实际需要取得的字节数
            length = value.toString().getBytes().length;
            if (t == Type.LLVAR && length > 99) {
                throw new IllegalArgumentException("LLVAR can only hold values up to 99 chars");
            } else if (t == Type.LLLVAR && length > 999) {
                throw new IllegalArgumentException("LLLVAR can only hold values up to 999 chars");
            }
        } else if ((this.datatype == Type.BCD_LLVAR) || (this.datatype == Type.BCD_LLLVAR)
                   || (this.datatype == Type.BCD_LLLVAR_BINARY)) {

            if (value instanceof byte[]) {
                this.length = ((byte[]) value).length;
            } else {
                this.length = this.value.toString().getBytes().length;
            }
            if ((t == Type.BCD_LLVAR) && (this.length > 99))
                throw new IllegalArgumentException("LLVAR can only hold values up to 99 chars");
            if ((t == Type.BCD_LLLVAR) && (this.length > 999))
                throw new IllegalArgumentException("LLLVAR can only hold values up to 999 chars");
            if ((t == Type.BCD_LLLVAR_BINARY) && (this.length > 999))
                throw new IllegalArgumentException("LLLVAR can only hold values up to 999 chars");

        } else if ((this.datatype == Type.BCD_ALPHA)) {

            if (value instanceof byte[]) {
                this.length = ((byte[]) value).length;
            } else {
                this.length = this.value.toString().getBytes().length;
            }

        }
    }

    /**
     * <pre>
     * Returns the Type to which the value must be formatted.
     * </pre>
     *
     * @return
     */
    public Type getType() {
        return datatype;
    }

    /**
     * <pre>
     * Returns the length of the stored value, of the length of the formatted value
     * in case of NUMERIC or ALPHA. It doesn't include the field length header in case
     * of LLVAR or LLLVAR.
     * </pre>
     *
     * @return
     */
    public int getLength() {
        return length;
    }

    /**
     * <pre>
     * Returns the stored value without any conversion or formatting. 
     * </pre>
     *
     * @return
     */
    public T getValue() {
        return value;
    }

    /** 
     * Returns the formatted value as a String. The formatting depends on the type of the
     * receiver.
     * 
     * @return
     * @see java.lang.Object#toString()
     */
    public String toString() {
        if (value == null) {
            return "FieldValue<null>";
        }
        if (datatype == Type.NUMERIC || datatype == Type.AMOUNT) {
            if (datatype == Type.AMOUNT) {
                if (value instanceof String) {
                    return datatype.format(new BigDecimal((String) value), 12);
                } else if (value instanceof BigDecimal) {
                    return datatype.format((BigDecimal) value, 12);
                }
            } else if (value instanceof Number) {
                return datatype.format(((Number) value).longValue(), length);
            } else {
                return datatype.format(value.toString(), length);
            }
        } else if (datatype == Type.ALPHA) {
            return datatype.format(value.toString(), length);
        } else if (datatype == Type.LLLVAR) {
            return value.toString();
        } else if (datatype == Type.LLLVARHEX || datatype == Type.LLVARHEX) {
            return value.toString();
        } else if (value instanceof Date) {
            return datatype.format((Date) value);
        } else if (value instanceof byte[]) {
            return Converts.bytesToHexString((byte[]) value);
        }

        return value.toString();
    }

    /** 
     * Returns a copy of the receiver that references the same value object.
     * 
     * @return
     * @see java.lang.Object#clone()
     */
    public Value<T> clone() {
        return (Value<T>) (new Value(this.datatype, this.value, this.length, this.position));

    }

    /** 
     * Returns true of the other object is also an Value and has the same type and length,
     * and if other.getValue().equals(getValue()) returns true.
     * 
     * @param other
     * @return
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Value)) {
            return false;
        }
        Value comp = (Value) other;
        return (comp.getType() == getType() && comp.getValue().equals(getValue()) && comp
            .getLength() == getLength());
    }

    /** 
     * 根据FindBUG的建议添加的方法
     * 
     * @return
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        assert false : "hashCode not designed";
        return 42; // any arbitrary constant will do 
    }

    /**
     * <pre>
     * Writes the formatted value to a stream, with the length header
     * if it's a variable length type.
     * </pre>
     *
     * @param outs
     * @param binary
     * @throws IOException
     */
    public void write(OutputStream outs, boolean binary) throws IOException {
        if (this.datatype == Type.BCD_LLVAR || this.datatype == Type.BCD_LLLVAR
            || this.datatype == Type.BCD_ALPHA || this.datatype == Type.BCD_LLLVAR_BINARY) {

            writeBCD(outs);

            return;
        }
        if (datatype == Type.LLLVAR || datatype == Type.LLVAR || datatype == Type.LLLVARHEX
            || datatype == Type.LLVARHEX) {
            if (binary) {
                int len = length;
                if (datatype == Type.LLLVARHEX || datatype == Type.LLVARHEX) {
                    len = len / 2 + len % 2;
                }

                if (datatype == Type.LLLVAR || datatype == Type.LLLVARHEX) {
                    outs.write(len / 100); //00 to 09 automatically in BCD
                }
                //BCD encode the rest of the length
                outs.write((((len % 100) / 10) << 4) | (len % 10));
            } else {
                //write the length in ASCII
                if (datatype == Type.LLLVAR) {
                    outs.write((length / 100) + 48);
                }
                if (length >= 10) {
                    outs.write(((length % 100) / 10) + 48);
                } else {
                    outs.write(48);
                }
                outs.write((length % 10) + 48);
            }
        } else if (binary) {
            //numeric types in binary are coded like this
            byte[] buf = null;
            if (datatype == Type.NUMERIC) {
                buf = new byte[(length / 2) + (length % 2)];
            } else if (datatype == Type.AMOUNT) {
                buf = new byte[6];
            } else if (datatype == Type.DATE10 || datatype == Type.DATE4
                       || datatype == Type.DATE_EXP || datatype == Type.TIME) {
                buf = new byte[length / 2];
            }

            //Encode in BCD if it's one of these types
            if (buf != null) {
                toBcd(toString(), buf);
                outs.write(buf);
                return;
            }
        }
        //Just write the value as text
        if (datatype == Type.LLLVARHEX || datatype == Type.LLVARHEX) {
            outs.write(Converts.hexStr2Bytes(toString()));
        } else {
            outs.write(toString().getBytes());
        }
    }

    /**
     * @param outs
     * @param binary
     * @throws IOException
     */
    public void writeLVARBCD(OutputStream outs, boolean binary) throws IOException {
        if (datatype == Type.LLLVAR || datatype == Type.LLVAR || datatype == Type.LLLVARHEX
            || datatype == Type.LLVARHEX) {

            if (datatype == Type.LLLVAR || datatype == Type.LLLVARHEX || datatype == Type.LLVARHEX) {
                outs.write(length / 100); //00 to 09 automatically in BCD
            }
            //BCD encode the rest of the length
            outs.write((((length % 100) / 10) << 4) | (length % 10));
            byte[] buf = null;
            buf = new byte[(length / 2) + (length % 2)];
            toBcd(toString(), buf);
            outs.write(buf);

        }
    }

    /**
     * <pre>
     * Encode the value as BCD and put it in the buffer. The buffer must be big enough
     * to store the digits in the original value (half the length of the string).
     * </pre>
     *
     * @param value
     * @param buf
     */
    private void toBcd(String value, byte[] buf) {
        if (!position) {
            toBcdright(value, buf);
        } else {
            toBcdleft(value, buf);
        }
    }

    private void toBcdleft(String value, byte[] buf) {
        int charpos = 0; //char where we start
        int bufpos = 0;
        if (value.length() % 2 == 1) {
            //for odd lengths we encode just the first digit in the first byte
            buf[0] = (byte) (value.charAt(0) - 48);
            charpos = 1;
            bufpos = 1;
        }
        //encode the rest of the string
        while (charpos < value.length()) {
            buf[bufpos] = (byte) (((value.charAt(charpos) - 48) << 4) | (value.charAt(charpos + 1) - 48));
            charpos += 2;
            bufpos++;
        }
    }

    private void toBcdright(String value, byte[] buf) {
        int charpos = 0; //char where we start
        int bufpos = 0;
        System.out.println("value:" + value + ",value.charAt(0):" + value.charAt(0) + ",-48:"
                           + ((byte) (value.charAt(value.length() - 1) - 48)));
        int length = value.length();
        if (length % 2 == 1) {
            if (datatype == Type.LLLVAR || datatype == Type.LLLVARHEX || datatype == Type.LLVAR
                || datatype == Type.LLVARHEX) {
                buf[buf.length - 1] = (byte) ((value.charAt(length - 1) - 48) << 4);
                length--;
            } else {

                //for odd lengths we encode just the first digit in the first byte

                buf[0] = (byte) (value.charAt(0) - 48);
                charpos = 0;
                bufpos = 0;
            }
        }
        //encode the rest of the string
        while (charpos < length - length % 2) {
            buf[bufpos] = (byte) (((value.charAt(charpos) - 48) << 4) | (value.charAt(charpos + 1) - 48));
            charpos += 2;
            bufpos++;
        }
        if (length % 2 == 1) {
            buf[bufpos] = (byte) (((value.charAt(charpos) - 48) << 4) | ("0".charAt(0) - 48));
            charpos += 2;
            bufpos++;
        }
        String me = Converts.bytesToHexString(buf);
        System.out.print(me);
    }

    /**
     * @param outs
     * @param isbinary
     * @param b
     */
    public void writeSpecial(ByteArrayOutputStream outs, boolean isbinary, boolean b) {
        if (datatype == Type.LLLVAR && isbinary && b) {
            outs.write(length / 100); //00 to 09 automatically in BCD
            outs.write((((length % 100) / 10) << 4) | (length % 10));
            try {
                byte[] buf = new byte[length];
                System.arraycopy(value, 0, buf, 0, length);
                outs.write(buf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Setter method for property <tt>name</tt>.
     * 
     * @param name value to be assigned to property name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter method for property <tt>name</tt>.
     * 
     * @return property value of name
     */
    public String getName() {
        return name;
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

    /**
     * 
     * @param outs
     * @throws IOException
     */
    private void writeBCD(OutputStream outs) throws IOException {
        byte[] buf = null;
        if (this.datatype == Type.BCD_LLVAR || this.datatype == Type.BCD_LLLVAR
            || this.datatype == Type.BCD_LLLVAR_BINARY) {

            if (datatype == Type.BCD_LLLVAR || datatype == Type.BCD_LLLVAR_BINARY) {
                outs.write(length / 100); //00 to 09 automatically in BCD
            }
            //BCD encode the rest of the length
            outs.write((((length % 100) / 10) << 4) | (length % 10));
            buf = new byte[(length / 2) + (length % 2)];
            writeData(outs, buf, true);
        } else {

            buf = new byte[(length / 2) + (length % 2)];
            writeData(outs, buf, true);
        }
    }

    /**
     * 对于不需要BCD编码的直接输出字节形式
     * 对于需要BCD编码输出,需要做转换输出
     * 直接byte[]输入的.直接输出,不需做转换
     * 
     * @param outs
     * @throws IOException
     */
    private void writeData(OutputStream outs, byte[] buf, boolean needBCD) throws IOException {
        if (value instanceof byte[]) {
            outs.write((byte[]) value);
        } else {
            if (needBCD) {
                toBcd(toString(), buf);
                outs.write(buf);
            } else {
                outs.write(toString().getBytes());
            }

        }
    }
}
