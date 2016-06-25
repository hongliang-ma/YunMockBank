package com.mock.core.service.transaction.util;

public class KeyUtil {
    /**
     * <pre>
     * 签到获得的加密密钥 8位加密密钥+8位0+4位验证字
     * 解密时 用ZMK 将 8位的加密密钥做3DES解密，获得的工作密钥明文EWK，
     * EWK对8位0做1次des加密,密文的前4位 与验证字相符
     * 例：701CA66BF4698CA 解密后为3BE034468094A716
     * 对     00000000000000000加密，密文为061C720962136FFE
     * 与061C7209 比较，相符
     * </pre>
     */
    public static String SIGN_KEY = "894DA3363683B02E0000000000000000B23FD643";

    /**
     * EDK 11111111111111113333333333333333
     * 消费时，作为62域的3des加密密钥
     */
    public static byte[] EDK      = Converts.hexStr2Bytes("11111111111111113333333333333333");

    /**
     * ZMK 10101010101010103232323232323232
     * 签到之后，用于解密 签到下送的密文
     */
    public static byte[] ZMK      = Converts.hexStr2Bytes("10101010101010103232323232323232");

    /**
     * EMK 签到获得的8位密钥密文+后8位0，即签到获得16位密钥
     * 用作62域的加密明文key
     * 
     */
    public static byte[] EMK      = Converts.hexStr2Bytes(SIGN_KEY.substring(0, 32));

    /**
     * 解密之后的签到密钥key,用于生成MAC摘要时所需要的des密钥
     */
    public static byte[] EWK      = DES.decrypt3DESofdouble(
                                      Converts.hexStr2Bytes(SIGN_KEY.substring(0, 16)), ZMK);

    public KeyUtil() {
        super();
    }

}
