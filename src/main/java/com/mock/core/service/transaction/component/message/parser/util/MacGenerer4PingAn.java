package com.mock.core.service.transaction.component.message.parser.util;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.mock.core.service.transaction.util.KeyUtil;


/**
 * 平安银行 8583协议所用到的MAC加密算法. <br>
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
 * <font color="red">c) 将异或运算后的最后8个字节（RESULT BLOCK）转换成16 个HEXDECIMAL： </font><br>
 * RESULT BLOCK = TM31 TM32 TM33 TM34 TM35 TM36 TM37 TM38 = <br>
 * TM311 TM312 TM321 TM322 TM331 TM332 TM341 TM342 || <br>
 * TM351 TM352 TM361 TM362 TM371 TM372 TM381 TM382 <br>
 * 
 * <font color="red">d) 取前8 个字节用MAK加密： </font><br>
 * ENC BLOCK1 = eMAK（TM311 TM312 TM321 TM322 TM331 TM332 TM341 TM342）<br>
 * = EN11 EN12 EN13 EN14 EN15 EN16 EN17 EN18<br>
 * 
 * <font color="red">e) 将加密后的结果与后8 个字节异或：</font><br>
 * EN11 EN12 EN13 EN14 EN15 EN16 EN17 EN18 XOR） <br>
 * TM351 TM352 TM361 TM362 TM371 TM372 TM381 TM382<br>
 * TEMP BLOCK= TE11 TE12 TE13 TE14 TE15 TE16 TE17 TE18<br>
 * 
 * <font color="red">f) 用异或的结果TEMP BLOCK 再进行一次单倍长密钥算法运算。 </font><br>
 * ENC BLOCK2 = eMAK（TE11 TE12 TE13 TE14 TE15 TE16 TE17 TE18） <br>=
 * EN21 EN22 EN23 EN24 EN25 EN26 EN27 EN28<br>
 * 
 * <font color="red">g) 将运算后的结果（ENC BLOCK2）转换成16 个HEXDECIMAL：</font><br>
 * ENC BLOCK2 = EN21 EN22 EN23 EN24 EN25 EN26 EN27 EN28 = <br>
 * EM211 EM212 EM221 EM222 EM231 EM232 EM241 EM242 || <br>
 * EM251 EM252 EM261 EM262 EM271 EM272 EM281 EM282 <br>
 * 示例 ： ENC RESULT= %H84, %H56, %HB1, %HCD, %H5A, %H3F, %H84, %H84 <br>
 * 转换成16 个HEXDECIMAL: “8456B1CD5A3F8484” <br>
 * <font color="red">h) 取前8个字节作为MAC值。 取”8456B1CD”为MAC值。</font><br>
 * 
 * 报文从msg type开始
 * 
 * @author song.xu
 * 
 */
public final class MacGenerer4PingAn {
    private static byte[] padding   = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    public static byte[]  MAK       = KeyUtil.EWK;
    public static byte[]  ZMK       = KeyUtil.ZMK;
    private static String ALGORITHM = "DES/ECB/NoPadding";

    public static byte[] getBytesMac(byte[] needEncode) {
        // Step 1做异或,数据填充过程在异或移位中实现,见doEcb
        byte[] xordata = doEcb(needEncode);
        // step2 将异或运算后的最后8个字节转换成16 个HEXSTRING
        String hexXordata = Converts.bytesToHexString(xordata);
        // step3 取前8 个字节用MAK加密：
        String hexbef = hexXordata.substring(0, 8);
        byte[] bytebef = toBytesOfAscii(hexbef);
        byte[] encryptTmp1 = new byte[8];
        System.arraycopy(encrypt(bytebef, MAK), 0, encryptTmp1, 0, 8);
        String hexencryptTmp = Converts.bytesToHexString(encryptTmp1);
        System.out.println("第一次加密密文,Hex进制:" + hexencryptTmp);
        String hexend = hexXordata.substring(8, 16);
        // step4 & 5 将加密后的结果与后8 个字节异或,用异或的结果TEMP BLOCK 再进行一次单倍长密钥算法运算。
        byte[] byteend = toBytesOfAscii(hexend);
        byte[] encryptTmp2 = new byte[8];
        System.arraycopy(encrypt(doXor(encryptTmp1, byteend), MAK), 0, encryptTmp2, 0, 8);
        // step 6 将运算后的结果（ENC BLOCK2）转换成16 个HEXDECIMAL
        String hexencrpt = Converts.bytesToHexString(encryptTmp2);
        System.out.println("最终加密密文,Hex进制:" + hexencrpt);

        // // step final 取前8个字节作为MAC值
        String hexfinal = null;
        try {
            hexfinal = Converts.StringToHexString(hexencrpt.substring(0, 8));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("最终MAC,Hex进制:" + hexfinal);
        byte[] finaldata = toZipBcd(hexfinal);

        //        byte[] encryptMac = DES.encrypt3DESofdouble(finaldata, ZMK);
        return finaldata;

    }

    /**
     * 
     * @param byteRes
     * @param resLength
     * @return
     */
    public static byte[] doEcb(byte[] byteRes) {

        byte[] CheckData = null;
        byte[] TmpBuffer = null;
        int resLength = byteRes.length;
        // 移位之前先做数据位不足的填充准备
        // 填充之后的数据为TmpBuffer
        // 此处准备需要填充多少数据.

        int checkLength = resLength % 8;
        if (!(checkLength == 0)) {
            TmpBuffer = new byte[resLength + 8 - checkLength];

            System.arraycopy(padding, checkLength, TmpBuffer, resLength, (8 - checkLength));
        } else {
            TmpBuffer = new byte[resLength];
        }
        System.arraycopy(byteRes, 0, TmpBuffer, 0, resLength);
        // 填充数据准备完成,进行异或运算.
        CheckData = new byte[8];
        System.arraycopy(byteRes, 0, CheckData, 0, 8);
        int i, j;
        j = TmpBuffer.length / 8;

        for (i = 1; i < j; i++) {
            doXor(CheckData, TmpBuffer, i * 8);
        }
        byte[] tmp = new byte[8];
        System.arraycopy(CheckData, 0, tmp, 0, 8);
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

        for (int i = 0; i < 8; i++) {
            xor1[i] = (byte) (xor1[i] ^ xor2[i]);
        }
        return xor1;
    }

    /**
     * 解密函数
     * 
     * @param data
     *            解密数据
     * @param key
     *            密钥
     * @return 返回解密后的数据
     */
    public static byte[] decrypt(byte[] data, byte[] key) {
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();

            // byte rawKeyData[] = /* 用某种方法获取原始密匙数据 */;

            // 从原始密匙数据创建一个DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // using DES in ECB mode
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);

            // 正式执行解密操作
            byte decryptedData[] = cipher.doFinal(data);

            return decryptedData;
        } catch (Exception e) {
            System.err.println("DES算法，解密出错。");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 加密函数<br>
     * DES/ECB/PKCS5Padding,算法DES,模式ECB,填充PKCS5Padding
     * 
     * @param data
     *            待加密数据
     * @param key
     *            加密密钥
     * @return encodeData
     */
    public static byte[] encrypt(byte[] data, byte[] key) {

        try {

            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();

            // 从原始密钥数据创建DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // using DES in ECB mode
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);

            // 执行加密操作
            byte encryptedData[] = cipher.doFinal(data);

            return encryptedData;
        } catch (Exception e) {
            System.err.println("DES算法，加密数据出错!");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 转换为压缩的BCD码<br>
     * 如12,转换BCD码<br>
     * byte[0]=18,00010010,BCD码即为12
     * 
     * @param value
     * @return buf
     */
    public static byte[] toZipBcd(String value) {

        byte[] buf = new byte[value.length() / 2];
        int charpos = 0; // char where we start
        int bufpos = 0;
        if (value.length() % 2 == 1) {
            // for odd lengths we encode just the first digit in the first byte
            buf = new byte[value.length() / 2 + 1];
            buf[0] = (byte) (value.charAt(0) - 48);
            charpos = 1;
            bufpos = 1;
        }
        // encode the rest of the string
        while (charpos < value.length()) {
            buf[bufpos] = (byte) (((value.charAt(charpos) - 48) << 4) | (value.charAt(charpos + 1) - 48));
            charpos += 2;
            bufpos++;
        }
        return buf;
    }

    /**
     * 转换为压缩的BCD码<br>
     * 如12,转换BCD码<br>
     * byte[0]=18,00010010,BCD码即为12
     * 此方法为反转
     * @param value
     * @return buf
     */
    public static String ZipBcdtoString(byte[] value) {

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < value.length; i++) {
            byte[] temp = new byte[2];

            temp[0] = (byte) (value[i] >> 4);
            temp[1] = (byte) (value[i] & 0x0f);
            int k1 = temp[0] < 0 ? (16 + temp[0]) : (int) temp[0];
            sb.append(k1).append(temp[1]);

        }

        return sb.toString();
    }

    /**
     * 将字符转换为ASCII码的字节形式 <br>
     * 如CEE0088A 对应 asc码为4345453030383841 <br>
     * byte[0]=43,源字符为 C
     * 
     * @param value
     * @param buf
     */
    public static byte[] toBytesOfAscii(String value) {
        byte[] buf = new byte[value.length()];
        int charpos = 0; // char where we start
        int bufpos = 0;
        if (value.length() % 2 == 1) {
            // for odd lengths we encode just the first digit in the first byte
            buf[0] = (byte) (value.charAt(0) - 48);
            charpos = 1;
            bufpos = 1;
        }
        // encode the rest of the string
        while (charpos < value.length()) {
            buf[bufpos] = (byte) (value.charAt(charpos));
            charpos++;
            bufpos++;
        }
        return buf;
    }

    public static void main(String[] args) {
        String source = "5ABDA1EB10365EEC0000000000000000E9449619";
        System.out.println(ZipBcdtoString(toZipBcd(source)));
    }
}
