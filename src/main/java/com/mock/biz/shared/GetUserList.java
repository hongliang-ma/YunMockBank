
package com.mock.biz.shared;

import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;
import com.mock.biz.shared.domain.DetailInfoInner;
import com.mock.biz.shared.domain.UserInfoList;
import com.mock.core.model.transaction.J8583.J8583Model;


/**
 * 获取基本的数据
 * @author hongliang.ma
 * @version $Id: GetUserList.java, v 0.1 2012-7-25 下午12:50:36 hongliang.ma Exp $
 */
public interface GetUserList {

    /**
     * 根据用户名获取所有的用户列表，主要它用于首页中的左侧显示
     * 
     * @param userName
     */
    public List<UserInfoList> getTotalListByName(final String userName);

    /**
     * 根据用户模板ID号获取所有信息
     * 
     * @param innerID
     * @param transferId  存在转发时的ID
     * @return 
     */
    public List<DetailInfoInner> getDetailInfo(final String innerID);

    /**
     * 获取所有的通讯配置相关的配置,主要用于新增显示
     * @param userName 当前用户
     * @return
     */
    public List<UserInfoList> getAllConfigList(final String userName);

    /**
     * 根据ID，从数据库中获取8583的结果
     * 
     * @param Id
     * @return  J8583Model
     * @throws XPathExpressionException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public J8583Model get8583Template(final String Id) throws XPathExpressionException,
                                                      ParserConfigurationException, SAXException,
                                                      IOException;
}
