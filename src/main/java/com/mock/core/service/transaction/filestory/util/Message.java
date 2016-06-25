/*
 * Alipay.com Inc.
 * Copyright (c) 2004-2005 All Rights Reserved.
 */
package com.mock.core.service.transaction.filestory.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.mock.common.util.lang.UniqID;
import com.mock.core.service.transaction.filestory.enums.MessageTypeEnum;

/**
 * 报文根对象。
 * 
 * @author calvin.lil@alibaba-inc.com
 * 
 * @version $Id$
 */
public abstract class Message implements Serializable {

	private static final long serialVersionUID = 2815810959382042121L;

	/** 报文ID */
	protected String msgId;

	/** 报文版本 */
	protected String msgVer;

	/** 请求发起方 **/
	protected String msgSrc;

	/** 请求接收方 */
	protected String msgDest;

	/** 报文签名使用的证书ID */
	protected String msgCertId;

	/** 扩展信息 */
	protected List<Extension> extensions = new ArrayList<Extension>();

	public Message() {
		setMsgId(UniqID.getInstance().getUniqIDHash());
	}

	/**
	 * @param extention
	 */
	public void addExtension(Extension extension) {
		if (extensions == null) {
			extensions = new ArrayList<Extension>();
		}

		this.extensions.add(extension);
	}

	/**
	 * @param name
	 * @param value
	 */
	public void addExtension(String name, String value) {
		addExtension(new Extension(name, value));
	}

	/**
	 * @return Returns the extentions.
	 */
	public List<Extension> getExtensions() {
		return extensions;
	}

	/**
	 * Get extension value from extension that match the name
	 * 
	 * @param extensionName
	 * @return extension value
	 */
	public String getExtensionValue(String extensionName) {
		if (null != extensionName && null != extensions) {
			for (Extension ext : extensions) {
				if (extensionName.equals(ext.getName())) {
					return ext.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * @return Returns the msgCertId.
	 */
	public String getMsgCertId() {
		return msgCertId;
	}

	/**
	 * @return Returns the msgDest.
	 */
	public String getMsgDest() {
		return msgDest;
	}

	/**
	 * @return Returns the msgId.
	 */
	public String getMsgId() {
		return msgId;
	}

	/**
	 * @return Returns the msgSrc.
	 */
	public String getMsgSrc() {
		return msgSrc;
	}

	/**
	 * @return
	 */
	public abstract MessageTypeEnum getMsgType();

	/**
	 * @return Returns the msgVer.
	 */
	public String getMsgVer() {
		return msgVer;
	}

	/*
	 * 提现请求报文要重载这个方法，返回true。 其他报文不同重载。
	 */
	public boolean resendDisable() {
		return false;
	}

	/**
	 * @param extentions
	 *            The extentions to set.
	 */
	public void setExtensions(List<Extension> extentions) {
		if (extentions != null) {
			this.extensions = new ArrayList<Extension>(extentions);
		}
	}

	/**
	 * @param msgCertId
	 *            The msgCertId to set.
	 */
	public void setMsgCertId(String msgCertId) {
		this.msgCertId = msgCertId;
	}

	/**
	 * @param msgDest
	 *            The msgDest to set.
	 */
	public void setMsgDest(String msgDest) {
		this.msgDest = msgDest;
	}

	/**
	 * @param msgId
	 *            The msgId to set.
	 */
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	/**
	 * @param msgSrc
	 *            The msgSrc to set.
	 */
	public void setMsgSrc(String msgSrc) {
		this.msgSrc = msgSrc;
	}

	/**
	 * @param msgType
	 */
	public void setMsgType(MessageTypeEnum msgType) {
		// do nothing
	}

	/**
	 * @param msgVer
	 *            The msgVer to set.
	 */
	public void setMsgVer(String msgVer) {
		this.msgVer = msgVer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
