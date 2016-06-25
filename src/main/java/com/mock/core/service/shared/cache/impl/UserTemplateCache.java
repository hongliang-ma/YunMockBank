
package com.mock.core.service.shared.cache.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.mock.core.model.transaction.detail.DetailMsg;
import com.mock.core.model.transaction.template.TemplateDetail;
import com.mock.core.model.transaction.template.Usertemplate;
import com.mock.common.util.CacheMessageUtil;
import com.mock.common.util.lang.StringUtil;

/**
 * 
 * @author hongliang.ma
 * @version $Id: UserTemplateCache.java, v 0.1 2012-6-2 下午5:05:22 hongliang.ma Exp $
 */
public final class UserTemplateCache {
    /** 读写锁 */
    private static final ReadWriteLock       lock              = new ReentrantReadWriteLock();

    //第一个匹配，为system+macht,value为userTemplateID 
    private static Map<String, String>       fMatchCache       = new ConcurrentHashMap<String, String>();

    //第二个匹配，为system+macht,value为userTemplateID 
    private static Map<String, String>       sMatchCache       = new ConcurrentHashMap<String, String>();

    //第三个匹配，为system+macht,value为userTemplateID 
    private static Map<String, String>       tMatchCache       = new ConcurrentHashMap<String, String>();

    //用户模板缓存,key为匹配值作为userTemplateID 
    private static Map<String, Usertemplate> userTemplateCache = new ConcurrentHashMap<String, Usertemplate>();

    /**实际用户操作的Msg缓存,key为TemplateDetail的ID号, value为用户ID号*/
    private static Map<Long, String>         detailMsgCache    = new ConcurrentHashMap<Long, String>();

    private UserTemplateCache() {
        //不要用这个构造函数
    }

    /**
     * 刷新组件相关的缓存
     * 
     * @param users
     */
    public static void putAll(List<Usertemplate> userTemplates) {
        Lock writelock = lock.writeLock();
        writelock.lock();

        try {
            userTemplateCache.clear();
            detailMsgCache.clear();
            fMatchCache.clear();
            sMatchCache.clear();
            tMatchCache.clear();

            List<TemplateDetail> listTemplateDetail = null;
            String[] strMatch = new String[3];
            String strSysId = null;
            String innerid = null;
            for (Usertemplate usertemplate : userTemplates) {
                if (null != usertemplate) {
                    //以匹配值作为match对象
                    innerid = usertemplate.getInnerid();
                    userTemplateCache.put(innerid, usertemplate);
                    strSysId = usertemplate.getSystemId();
                    strMatch = usertemplate.getMatchstr().split("\\|\\|");
                    if (1 <= strMatch.length && StringUtil.isNotBlank(strMatch[0])) {
                        fMatchCache.put(strSysId + strMatch[0], innerid);
                    }
                    if (2 <= strMatch.length && StringUtil.isNotBlank(strMatch[1])) {
                        sMatchCache.put(strSysId + strMatch[1], innerid);
                    }

                    if (3 <= strMatch.length && StringUtil.isNotBlank(strMatch[2])) {
                        tMatchCache.put(strSysId + strMatch[2], innerid);
                    }

                    listTemplateDetail = usertemplate.getTemplateDetail();
                    for (TemplateDetail templateDetail : listTemplateDetail) {
                        if (templateDetail == null) {
                            continue;
                        }
                        detailMsgCache.put(templateDetail.getId(), innerid);
                    }
                }

            }
        } finally {
            writelock.unlock();
        }
    }

    /**
     * 更新匹配值
     * 
     * @param innerId
     * @param newMatch
     */
    public static void changeMatch(String innerId, String newMatch) {
        if (StringUtil.isEmpty(innerId) || StringUtil.isEmpty(newMatch)) {
            return;
        }

        String sysId = userTemplateCache.get(innerId).getSystemId();
        String[] strOldMatch = userTemplateCache.get(innerId).getMatchstr().split("\\|\\|");

        if (1 <= strOldMatch.length && StringUtil.isNotBlank(strOldMatch[0])) {
            fMatchCache.remove(sysId + strOldMatch[0]);
        }
        if (2 <= strOldMatch.length && StringUtil.isNotBlank(strOldMatch[1])) {
            sMatchCache.remove(sysId + strOldMatch[1]);
        }

        if (3 <= strOldMatch.length && StringUtil.isNotBlank(strOldMatch[2])) {
            tMatchCache.remove(sysId + strOldMatch[2]);
        }

        String[] strMatch = newMatch.split("\\|\\|");
        if (1 <= strMatch.length && StringUtil.isNotBlank(strMatch[0])) {
            fMatchCache.put(sysId + strMatch[0], innerId);
        }
        if (2 <= strMatch.length && StringUtil.isNotBlank(strMatch[1])) {
            sMatchCache.put(sysId + strMatch[1], innerId);
        }

        if (3 <= strMatch.length && StringUtil.isNotBlank(strMatch[2])) {
            tMatchCache.put(sysId + strMatch[2], innerId);
        }

        userTemplateCache.get(innerId).setMatchstr(newMatch);
    }

    /**
     * 新增一个缓存
     * 
     * @param systemTemplate
     */
    public static void putNew(Usertemplate usertemplate) {
        if (null == usertemplate) {
            return;
        }
        String innerid = usertemplate.getInnerid();
        if (null != userTemplateCache.get(innerid)) {
            userTemplateCache.remove(innerid);
        }
        String sysId = usertemplate.getSystemId();
        userTemplateCache.put(innerid, usertemplate);

        String[] strMatch = usertemplate.getMatchstr().split("\\|\\|");
        if (1 <= strMatch.length && StringUtil.isNotBlank(strMatch[0])) {
            fMatchCache.put(sysId + strMatch[0], innerid);
        }
        if (2 <= strMatch.length && StringUtil.isNotBlank(strMatch[1])) {
            sMatchCache.put(sysId + strMatch[1], innerid);
        }

        if (3 <= strMatch.length && StringUtil.isNotBlank(strMatch[2])) {
            tMatchCache.put(sysId + strMatch[2], innerid);
        }

        List<TemplateDetail> listTemplateDetail = usertemplate.getTemplateDetail();
        for (TemplateDetail templateDetail : listTemplateDetail) {
            if (templateDetail == null) {
                continue;
            }
            detailMsgCache.put(templateDetail.getId(), innerid);
        }
    }

    /**
     * 从缓存中删除掉整个
     * 
     * @param communicationId
     */
    public static void deleteTotal(String innerId) {
        Usertemplate usertemplate = UserTemplateCache.getUsertemplateById(innerId);
        if (null != usertemplate) {
            List<TemplateDetail> listTemplateDetail = usertemplate.getTemplateDetail();
            Long templateDetailId = null;
            for (TemplateDetail templateDetail : listTemplateDetail) {
                if (templateDetail == null) {
                    continue;
                }
                templateDetailId = templateDetail.getId();
                detailMsgCache.remove(templateDetailId);
            }
            String[] strMatch = usertemplate.getMatchstr().split("\\|\\|");
            String sysId = usertemplate.getSystemId();
            if (1 <= strMatch.length && StringUtil.isNotBlank(strMatch[0])) {
                fMatchCache.remove(sysId + strMatch[0]);
            }
            if (2 <= strMatch.length && StringUtil.isNotBlank(strMatch[1])) {
                sMatchCache.remove(sysId + strMatch[1]);
            }

            if (3 <= strMatch.length && StringUtil.isNotBlank(strMatch[2])) {
                tMatchCache.remove(sysId + strMatch[2]);
            }

            userTemplateCache.remove(innerId);
        }
    }

    /**
     * 根据第一个匹配符号获取用户模板
     * 
     * @param strMatch
     * @return
     */
    public static Usertemplate getUsertemplateByMacht(String matchValue, String sysId) {
        if (StringUtil.isEmpty(matchValue)) {
            return null;
        }

        String[] strMatch = matchValue.split("\\|\\|");
        if (1 <= strMatch.length && StringUtil.isNotBlank(strMatch[0])
            && null != fMatchCache.get(sysId + strMatch[0])) {
            return getUsertemplateById(fMatchCache.get(sysId + strMatch[0]));
        }
        if (2 <= strMatch.length && StringUtil.isNotBlank(strMatch[1])
            && null != sMatchCache.get(sysId + strMatch[1])) {
            return getUsertemplateById(sMatchCache.get(sysId + strMatch[1]));
        }

        if (3 <= strMatch.length && StringUtil.isNotBlank(strMatch[2])
            && null != sMatchCache.get(sysId + strMatch[2])) {
            return getUsertemplateById(sMatchCache.get(sysId + strMatch[2]));
        }
        return null;
    }

    public static Usertemplate getUsertemplateById(String strUserId) {
        if (StringUtil.isEmpty(strUserId)) {
            return null;
        }
        return userTemplateCache.get(strUserId);
    }

    public static List<Usertemplate> getUsertemplateByName(String strUserName) {
        List<Usertemplate> listUsertemplate = new ArrayList<Usertemplate>();
        for (Usertemplate usertemplate : userTemplateCache.values()) {
            if (StringUtil.equals(strUserName, usertemplate.getUserName())) {
                listUsertemplate.add(usertemplate);
            }
        }
        return listUsertemplate;
    }

    public static List<Usertemplate> getUsertemplateBySysId(String sysId) {
        List<Usertemplate> listUsertemplate = new ArrayList<Usertemplate>();
        for (Usertemplate usertemplate : userTemplateCache.values()) {
            if (StringUtil.equals(sysId, usertemplate.getSystemId())) {
                listUsertemplate.add(usertemplate);
            }
        }
        return listUsertemplate;
    }

    /**
     * 根据流水号获取模板详情的DetailMsg
     * 
     * @param innerId
     * @return
     */
    public static DetailMsg getDetailMsg(Long id) {
        Usertemplate usertemplate = getUsertemplateById(detailMsgCache.get(id));
        if (null == usertemplate) {
            return null;
        }
        List<TemplateDetail> listTemplateDetail = usertemplate.getTemplateDetail();
        for (TemplateDetail templateDetail : listTemplateDetail) {
            if (null == templateDetail) {
                continue;
            }
            if (id == templateDetail.getId()) {
                return templateDetail.getDetailValue();
            }
        }
        return null;

    }

    /**
     * 打印缓存内容
     * 
     * @return
     */
    public static String getCacheInfo() {
        return CacheMessageUtil.mapStringAndObject(userTemplateCache).toString();
    }
}
