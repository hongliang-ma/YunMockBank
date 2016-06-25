
package com.mock.core.model.transaction.J8583;

import com.mock.common.util.AtsframeStrUtil;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * 
 * @author hongliang.ma
 * @version $Id: MapCustomConverter.java, v 0.1 2012-6-19 下午3:12:22 hongliang.ma Exp $
 */
public class J8583Converter implements Converter {

    public J8583Converter() {
        super();
    }

    /**
     * 判断是否要转的依据
     * @see com.thoughtworks.xstream.converters.ConverterMatcher#canConvert(java.lang.Class)
     */
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class clazz) {
        String classname = clazz.getName();
        return (classname.indexOf("J8583Header") >= 0 || classname.indexOf("J8583Field") >= 0) ? true
            : false;
    }

    /**
     * J8583Field 和J8583Header 重新转一下，注意addAttribute 一定要在setValue之前
     * @see com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object, com.thoughtworks.xstream.io.HierarchicalStreamWriter, com.thoughtworks.xstream.converters.MarshallingContext)
     */
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        boolean header = true;
        String classname = value.getClass().getName();

        header = (classname.indexOf("J8583Field") < 0);
        if (header) {
            J8583Header schema = (J8583Header) value;
            writer.addAttribute("length", schema.getLength());
            writer.setValue(schema.getHeaderValue());
        } else {
            J8583Field schema = (J8583Field) value;
            writer.addAttribute("id", schema.getId());
            writer.addAttribute("datatype", schema.getDatatype());
            if (AtsframeStrUtil.isNotEmpty(schema.getCrule())) {
                writer.addAttribute("crule", schema.getCrule());
            }
            if (AtsframeStrUtil.isNotEmpty(schema.getLength())) {
                writer.addAttribute("length", schema.getLength());
            }
            if (AtsframeStrUtil.isNotEmpty(schema.getName())) {
                writer.addAttribute("name", schema.getName());
            }
            writer.setValue(schema.getFieldValue());
        }

    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return populateMap(reader, context);
    }

    /**
     * J8583Field 和J8583Header 重新转一下，注意getAttribute 一定要在getValue之前
     * 
     * @param reader
     * @param context
     * @return
     */
    protected Object populateMap(HierarchicalStreamReader reader, UnmarshallingContext context) {
        J8583Header myJ8583Header = new J8583Header();
        J8583Field myJ8583Field = new J8583Field();

        if (AtsframeStrUtil.equals("header", reader.getNodeName())) {
            myJ8583Header.setLength(reader.getAttribute("length"));
            myJ8583Header.setHeaderValue(reader.getValue());
            return myJ8583Header;
        } else if (AtsframeStrUtil.equals("field", reader.getNodeName())) {
            myJ8583Field.setId(reader.getAttribute("id"));
            myJ8583Field.setDatatype(reader.getAttribute("datatype"));
            myJ8583Field.setCrule(reader.getAttribute("crule"));
            myJ8583Field.setLength(reader.getAttribute("length"));
            myJ8583Field.setName(reader.getAttribute("name"));
            myJ8583Field.setFieldValue(reader.getValue());
            return myJ8583Field;
        }
        return null;

    }
}
