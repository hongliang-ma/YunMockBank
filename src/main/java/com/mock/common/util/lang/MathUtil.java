
package com.mock.common.util.lang;

import org.apache.commons.lang.math.RandomUtils;

/**
 * 数学工具类
 * 
 * @author 松雪
 * @version $Id: MathUtil.java, v 0.1 2012-3-5 下午1:35:58 hao.zhang Exp $
 */
public final class MathUtil {

    /**
     * 生成只包含正整数，且每个数字唯一，顺序随机的数组
     * 例如：getUniqueRandomArray(10) = [5,0,3,2,6,4,7,9,1,8]
     * 
     * @param size 数组大小
     * @return
     */
    public static int[] getUniqueRandomArray(int size) {
        int[] result = new int[size];

        // 首先使用顺序填充，从0开始
        for (int i = 0; i < size; i++) {
            result[i] = i;
        }

        // 使用Fisher–Yates shuffle算法进行随机排序
        for (int i = 0; i < size; i++) {
            int r = i + RandomUtils.nextInt(size - i);
            int swap = result[r];
            result[r] = result[i];
            result[i] = swap;
        }

        return result;

    }
}
