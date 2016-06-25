
package com.mock.core.model.transaction.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
public final class MapCustomConverter implements Converter {

    public MapCustomConverter() {
        super();
    }

    public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
        String classname = clazz.getName();

        return (classname.indexOf("Map") >= 0 || classname.indexOf("List") >= 0 || classname
            .indexOf("Bean") >= 0) ? true : false;
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {

        map2xml(value, writer, context);
    }

    @SuppressWarnings("unchecked")
    protected void map2xml(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        boolean bMap = true;
        String classname = value.getClass().getName();

        bMap = (classname.indexOf("List") < 0);
        Map<String, Object> map;
        List<Object> list;
        StringBuilder key = new StringBuilder();
        Object subvalue;
        Entry<String, Object> entry = null;
        if (bMap) {
            map = (Map<String, Object>) value;
            for (Iterator<Entry<String, Object>> iterator = map.entrySet().iterator(); iterator
                .hasNext();) {
                entry = iterator.next();
                key.setLength(0);
                key.append(entry.getKey());
                subvalue = entry.getValue();
                if (subvalue == null) {
                    continue;
                }
                writer.startNode(key.toString());
                if (subvalue.getClass().getName().indexOf("String") >= 0) {
                    writer.setValue((String) subvalue);
                } else {
                    map2xml(subvalue, writer, context);
                }
                writer.endNode();
            }

        } else {
            list = (List<Object>) value;
            for (Object subval : list) {
                subvalue = subval;
                writer.startNode("child");
                if (subvalue.getClass().getName().indexOf("String") >= 0) {
                    writer.setValue((String) subvalue);
                } else {
                    map2xml(subvalue, writer, context);
                }
                writer.endNode();
            }
        }
    }

    public Map<String, Object> unmarshal(HierarchicalStreamReader reader,
                                         UnmarshallingContext context) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) populateMap(reader, context);

        return map;
    }

    protected Object populateMap(HierarchicalStreamReader reader, UnmarshallingContext context) {
        boolean bMap = true;
        Map<String, Object> map = new HashMap<String, Object>();
        List<Object> list = new ArrayList<Object>();
        Object value = null;
        StringBuilder key = new StringBuilder();
        Iterator<Entry<String, Object>> iter = null;
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            key.setLength(0);
            key.append(reader.getNodeName());
            if (reader.hasMoreChildren()) {
                value = populateMap(reader, context);
            } else {
                value = reader.getValue();
            }
            if (bMap) {
                if (map.containsKey(key)) {
                    // convert to list
                    bMap = false;
                    iter = map.entrySet().iterator();
                    while (iter.hasNext()) {
                        list.add(iter.next().getValue());
                    }
                    iter = null;
                    // insert into list
                    list.add(value);
                } else {
                    // insert into map
                    map.put(key.toString(), value);
                }
            } else {
                // insert into list
                list.add(value);
            }
            reader.moveUp();
            value = null;
        }
        if (bMap) {
            return map;
        } else {
            return list;
        }
    }
}
