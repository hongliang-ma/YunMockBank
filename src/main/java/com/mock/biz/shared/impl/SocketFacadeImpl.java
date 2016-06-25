
package com.mock.biz.shared.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.mock.biz.shared.SocketFacade;
import com.mock.core.model.shared.communication.CommunicationConfig;
import com.mock.core.model.shared.exception.code.CommunicationErrorCode;
import com.mock.core.model.shared.exception.util.AssertUtil;
import com.mock.core.model.shared.message.MessageEnvelope;
import com.mock.core.model.shared.message.MessageSendResult;
import com.mock.core.service.communication.cache.NetworkConfigCache;
import com.mock.core.service.communication.framework.instance.tcp.TcpClient;
import com.mock.common.util.ExceptionUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * 
 * @author hongliang.ma
 * @version $Id: SocketFacadeImpl.java, v 0.1 2012-8-22 下午5:02:37 hongliang.ma Exp $
 */
public class SocketFacadeImpl implements SocketFacade {
    /** 
     * @see com.mock.biz.shared.SocketFacade#sendSocketMessage(int, java.lang.String)
     */
    public void sendSocketMessage(HttpServletRequest request, HttpServletResponse response) {
        String port = request.getParameter("sendPort");
        CommunicationConfig tcpConfig = NetworkConfigCache.findByReceiver("127.0.0.1:" + port);
        AssertUtil.isNotNull(request, CommunicationErrorCode.ILLGE_PARAMS);
        MessageSendResult rendMsg = null;
        TcpClient tcpClient = null;
        try {
            tcpClient = new TcpClient(tcpConfig);
            MessageEnvelope sendMessageEnvelope = new MessageEnvelope(
                tcpConfig.getSendMessageFormat(), getTextContent(request, tcpConfig));

            rendMsg = tcpClient.send(sendMessageEnvelope);
        } catch (Exception e) {
            ExceptionUtil.caught(e, "发送socket消息失败，发送端口为", port);
        } finally {
            tcpClient.dispose();
            response(response, rendMsg);
        }
    }

    /**
     * 从HttpServletRequest中读取文本格式的报文
     * 
     * @param request
     * @param config
     * @return
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    private Object getTextContent(HttpServletRequest request, CommunicationConfig config)
                                                                                         throws UnsupportedEncodingException,
                                                                                         IOException {
        Object content;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(request.getInputStream(),
                config.getCharset()));
            StringBuilder contentString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                contentString.append(line);
            }
            content = contentString.toString();
        } finally {
            IOUtils.closeQuietly(reader);
        }

        return content;
    }

    /**
     * 响应消息
     * 
     * @param response  http请求
     * @param getCharset  编码格式
     * @param responseMsg   返回的实际消息
     * @throws IOException
     */
    private void response(HttpServletResponse response, MessageSendResult responseResult) {

        try {
            if (responseResult.isTimeout()) {
                response.setContentType("text/xml;charset=GBK");
                response.getWriter().write("Time Out超时");
                return;
            }

            Object responseMsg = responseResult.getMessageEnvelope().getContent();

            if (responseMsg == null || StringUtil.isBlank((String) responseMsg)) {
                response.setContentType("text/xml;charset=GBK");
                response.getWriter().write("有错误发生，返回错误");
            } else {
                response.setContentType("text/xml;charset=GBK");
                response.getWriter().write((String) responseMsg);
            }
        } catch (IOException e) {
            ExceptionUtil.caught(e, "response执行getWriter错误发生");
        }
    }
}
