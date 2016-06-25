package com.mock.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.mock.biz.shared.GetUserList;
import com.mock.biz.shared.domain.UserInfoList;
import com.mock.common.util.ApiUtilTool;
import com.mock.common.util.AtsframeStrUtil;
import com.mock.common.util.lang.StringUtil;


@Controller
public class Homepage {

	@Autowired
	private GetUserList getUserList;
	
	
	@RequestMapping(value = "/homePage.do", method = RequestMethod.GET)
    public String renderIndex(ModelMap model,HttpServletRequest request, HttpServletResponse response) {
		String loginName = getLoginName(request);
        if (StringUtil.isEmpty(loginName)) {
            return "error.vm";
        }
        List<UserInfoList> listUserInfo = getUserList.getTotalListByName(loginName);
        model.addAttribute("loginName", loginName);
        model.addAttribute("listUserInfo", listUserInfo);
        model.addAttribute("serverIp", ApiUtilTool.getHostName());

        return "homePage";
    }
	
	
	
	
	
	private String getLoginName(HttpServletRequest request) {
        String cookieName = ApiUtilTool.getCookieValue("anymock_cookie", request);
        return AtsframeStrUtil.substringBefore(cookieName, "SPDFS");
    }
}
