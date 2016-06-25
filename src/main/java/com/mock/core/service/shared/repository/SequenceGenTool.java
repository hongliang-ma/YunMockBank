
package com.mock.core.service.shared.repository;

import com.mock.common.util.DateUtil;

/**
 *  生成的内部流水号实现类
 *  
 * @author hongliang.ma
 * @version $Id: SequenceRepositoryImpl.java, v 0.1 2012-5-30 下午7:15:33 hongliang.ma Exp $
 */
public final class SequenceGenTool {

    /**
     * 产生18位的随机数
     * 
     * @return
     */
    public static String genComponetInnerid() {
        StringBuilder result = new StringBuilder(18);
        result.append(DateUtil.getTimestamp(4));

        return result.toString();
    }
}
