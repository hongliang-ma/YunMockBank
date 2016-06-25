package com.mock.core.service.transaction.component.message.parser.vo;

/**
 * 浙江农业银行 8583协议所用到的MAC加密算法的数据异或步骤. <br>
 * <font color="red">a) 将欲发送给POS中心的消息中，从消息类型（MTI）到63域之间的部分构成MAC ELEMEMENT BLOCK
 * （MAB）。</font><br>
 * <font color="red">b) 对MAB，按每8个字节做异或（不管信息中的字符格式），如果最后不满8个字节，则添加“0X00”。</font> <br>
 * 示例 ： MAB = M1 M2 M3 M4 <br>
 * 其中： M1 = MS11 MS12 MS13 MS14 MS15 MS16 MS17 MS18 <br>
 * M2 = MS21 MS22 MS23 MS24 MS25 MS26 MS27 MS28 <br>
 * M3 = MS31 MS32 MS33 MS34 MS35 MS36 MS37 MS38 <br>
 * M4 = MS41 MS42 MS43 MS44 MS45 MS46 MS47 MS48 <br>
 * 
 * <font color="blue">按如下规则进行异或运算：</font><br>
 * MS11 MS12 MS13 MS14 MS15 MS16 MS17 MS18 XOR） <br>
 * MS21 MS22 MS23 MS24 MS25 MS26 MS27 MS28<br>
 * TEMP BLOCK1 = TM11 TM12 TM13 TM14 TM15 TM16 TM17 TM18<br>
 * 
 * <font color="blue">然后，进行下一步的运算：</font><br>
 * TM11 TM12 TM13 TM14 TM15 TM16 TM17 TM18 XOR） <br>
 * MS31 MS32 MS33 MS34 MS35 MS36 MS37 MS38 <br>
 * TEMP BLOCK2 = TM21 TM22 TM23 TM24 TM25 TM26 TM27 TM28<br>
 * 
 * <font color="blue">再进行下一步的运算：</font><br>
 * TM21 TM22 TM23 TM24 TM25 TM26 TM27 TM28 XOR） <br>
 * MS41 MS42 MS43 MS44 MS45 MS46 MS47 MS48 <br>
 * RESULT BLOCK = TM31 TM32 TM33 TM34 TM35 TM36 TM37 TM38<br>
 * 
 * 
 */
public final class BytesMacGenerater {

    private static byte[] padding = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    /**
     * 
     * @param byteRes
     * @param resLength
     * @return
     */
    public static byte[] generateBytesForMac(byte[] byteRes) {

        byte[] tmpBuffer = null;
        int resLength = byteRes.length;

        // 填充字节数据,填充之后的字节数必须是8的整数倍
        int checkLength = resLength % 8;
        if (!(checkLength == 0)) {
            tmpBuffer = new byte[resLength + 8 - checkLength];
            System.arraycopy(padding, checkLength, tmpBuffer, resLength, (8 - checkLength));
        } else {
            tmpBuffer = new byte[resLength];
        }
        System.arraycopy(byteRes, 0, tmpBuffer, 0, resLength);

        // 填充数据准备完成,进行异或运算.
        byte[] checkData = new byte[8];
        System.arraycopy(tmpBuffer, 0, checkData, 0, 8);
        return getBytesForMac(checkData, tmpBuffer, 8);
    }

    /**
     * 
     * @param byteRes
     * @param tmpBuffer
     * @return
     */
    private static byte[] getBytesForMac(byte[] checkData, byte[] tmpBuffer, int length) {
        int i, j;
        j = tmpBuffer.length / length;
        for (i = 1; i < j; i++) {
            doXor(checkData, tmpBuffer, i * length);
        }
        byte[] tmp = new byte[length];
        System.arraycopy(checkData, 0, tmp, 0, length);
        return tmp;
    }

    /**
     * 异或运算
     * 
     * @param xor1
     * @param xor2
     * @return xor
     */
    public static byte[] doXor(byte[] xor1, byte[] xor2, int offset) {
        for (int i = 0; i < 8; i++) {
            xor1[i] = (byte) (xor1[i] ^ xor2[i + offset]);
        }
        return xor1;
    }

    /**
     * 异或运算
     * 
     * @param xor1
     * @param xor2
     * @return xor
     */
    public static byte[] doXor(byte[] xor1, byte[] xor2) {
        for (int i = 0; i < 8; i++)
            xor1[i] = (byte) (xor1[i] ^ xor2[i]);
        return xor1;
    }

    public static void main(String[] args) {
        String source = "123456dasdfas7";
        byte[] bs = source.getBytes();
        System.out.println(generateBytesForMac(bs).length);
    }
}
