package com.mock.core.service.transaction.filestory.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.exolab.castor.mapping.GeneralizedFieldHandler;

/**
 *日期handler，用来处理xml到java对象转换过程中的date字段的处理。
 *
 */
public class DateFieldHandler extends GeneralizedFieldHandler {
    private static final String DATA_FORMAT = "yyyyMMdd HH:mm:ss";

    @Override
    public Object convertUponGet(Object value) {
        if (value == null) {
            return null;
        }

        if (!(value instanceof Date)) {
            throw new IllegalArgumentException("");
        }

        DateFormat dateFormat = new SimpleDateFormat(DATA_FORMAT);

        return dateFormat.format((Date) value);
    }

    @Override
    public Object convertUponSet(Object value) {
        if (value == null) {
            return null;
        }

        DateFormat dateFormat = new SimpleDateFormat(DATA_FORMAT);

        try {
            return dateFormat.parse(value.toString());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Class getFieldType() {
        return Date.class;
    }

}