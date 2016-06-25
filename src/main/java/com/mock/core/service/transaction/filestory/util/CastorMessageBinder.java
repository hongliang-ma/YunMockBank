package com.mock.core.service.transaction.filestory.util;

import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.util.LocalConfiguration;
import org.exolab.castor.xml.Marshaller;

import com.mock.core.model.shared.exception.AnymockException;
import com.mock.core.model.shared.exception.code.SystemErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;

public class CastorMessageBinder {

    public static final String INDENT_PROP_NAME = "org.exolab.castor.indent";
    public static final String NAME_PROP_NAME   = "org.exolab.castor.xml.naming";

    /**
     * 这个方法用来设置缩进，我个人比较喜欢缩进的xml，看起来舒服一点。
     * @param indentFlag
     */
    public void setIndent(boolean indentFlag) {
        Properties props = LocalConfiguration.getInstance().getProperties();
        props.setProperty(INDENT_PROP_NAME, (indentFlag ? "true" : "false"));
        /*这个属性本来是设置有无下划线的。
         * mixed: Example: personInfo = personInfo
         * lower (default): personInfo = person-info
         * 不过因为我们指定了映射文件，所以这个属性没有太大的用处
         * */
        props.setProperty(NAME_PROP_NAME, "mixed");
    }

    public String bindMessage(Message msg) throws Exception {
        if (msg == null) {
            throw new Exception("报文对象为null!");
        }

        try {
            setIndent(true);
            Mapping mapping = new Mapping();

            String mappingFile = "file:/home/admin/build/app/core/service/transaction/src/main/java/com/alipay/anymock/transaction/filestory/mapping"
                                 + msg.getMsgType() + "-mapping.xml";

            URL mappingResource = new URL(mappingFile);
            AssertUtil.isNotNull(mappingResource, SystemErrorCode.ILLEGAL_PARAMETER);

            mapping.loadMapping(mappingResource);

            StringWriter writer = new StringWriter();

            Marshaller marshaller = new Marshaller(writer);
            marshaller.setMapping(mapping);

            marshaller.marshal(new Cartoon(msg));

            return writer.toString();
        } catch (Exception e) {
            throw new AnymockException(SystemErrorCode.SYSTEM_ERROR, e);
        }

    }
}
