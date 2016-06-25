

package com.mock.biz.messages.receiver.impl;

import com.mock.biz.messages.routing.RoutingEngine;
import com.mock.core.model.shared.message.MessageDescription;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.service.shared.communication.MessageReceiver;

/**
 *   收到报文的时候，进行的内部处理
 *   
 * @author hongliang.ma
 * @version $Id: MessageReceiverImpl.java, v 0.1 2012-6-28 下午3:33:32 hongliang.ma Exp $
 */
public class MessageReceiverImpl implements MessageReceiver {

    private RoutingEngine RoutingEngine;

    /** 
     * @see com.mock.core.service.shared.communication.MessageReceiver#receive(com.mock.core.model.shared.message.MessageDescription, boolean)
     */
    public MessageEnvelope receive(MessageDescription description, boolean sendDirect) {
        return RoutingEngine.route(description, sendDirect);
    }

    /**
     * Setter method for property <tt>routingEngine</tt>.
     * 
     * @param RoutingEngine value to be assigned to property routingEngine
     */
    public void setRoutingEngine(RoutingEngine routingEngine) {
        RoutingEngine = routingEngine;
    }

}
