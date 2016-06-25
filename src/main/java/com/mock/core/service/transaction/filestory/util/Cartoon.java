package com.mock.core.service.transaction.filestory.util;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author calvin.lil@alibaba-inc.com
 *
 * @version $Id$
 */
public class Cartoon implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2739306821052799666L;

    private Message           message;

    public Cartoon() {
    }

    public Cartoon(Message msg) {
        this.message = msg;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}