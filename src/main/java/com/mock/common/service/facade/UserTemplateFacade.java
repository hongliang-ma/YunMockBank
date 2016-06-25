package com.mock.common.service.facade;

import java.util.List;
import java.util.Map;

import com.mock.common.service.facade.common.J8583Template;

/**
 * UserTemplateFacade
 * @author jun.qi
 */
public interface UserTemplateFacade {

    /**
     * 异步发送消息
     * 
     * @param transferId   用户配置ID
     * @param transCodeRule  发送报文的协议，可空,为空时，根据配置的发送协议发送
     * @return 返回获取的报文
     */
    public String sendAsyncMessage(final String transferId, final String transCodeRule);

    /**
     * 异步发送消息
     * 
     * @param transferId 用户配置ID
     * @param MsgSend 发送报文
     * @return
     */
    public String sendMessageDirect(final String transferId, final String MsgSend);

    /**
     * 修改某个具体的返回值，不包含8583
     * 
     * @param transferId   用户配置ID
     * @param keyValues  key-value，key为发送过来的报文中的匹配值，value为要发送的内容
     * @return
     */
    public Boolean changeReturnMessage(final String transferId, final Map<String, String> keyValues);

    /**
     * 修改8583某个具体的返回值
     * 
     * @param transferId   用户配置ID
     * @param J8583Template 8583返回对象，为一个msgtypeid对应多个FiledID和返回值
     * @return
     */
    public Boolean change8583Message(final String transferId,
                                     final List<J8583Template> change8593Return);

    /**
     * 修改超时时间
     * 
     * @param transferId   用户配置ID
     * @param delayTime  延时时间，单位毫秒
     * @return
     */
    public Boolean changeDelay(final String transferId, final String delayTime);

    /**
     * 设置成默认配置
     * 
     * @paramtransferId   用户配置ID
     * @param isDefault 是否是默认值
     * @return
     */
    public String setDefault(final String transferId, final Boolean isDefault);

}