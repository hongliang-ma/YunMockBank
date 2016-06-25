package com.mock.core.service.transaction.component.extension;

/**
 * @author jun.qi
 */
public class ResultWrapper {

    private String value;

    public ResultWrapper() {
    }

    public ResultWrapper(String value) {
        super();
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "value: " + value;
    }
}