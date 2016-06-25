
package com.mock.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.mock.service.UserRepository;

/**
 * 注册页面请求
 * 
 * @author hongliang.ma
 * @version $Id: Login.java, v 0.1 2012-7-17 下午4:18:47 hongliang.ma Exp $
 */
@Controller
public class Login {

	@Autowired
    private UserRepository userRepository;
	

    /**
     * 显示注册的页面
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = "/login.do")
    public String renderIndex(HttpServletRequest request, HttpServletResponse response) {
        return "login";
    }

    /**
     *  登陆，并进入的登陆页面
     * 
     * @param request
     * @param response
     * @return 
     */
    @RequestMapping(value = "/login.do", method = RequestMethod.POST)
    public String login(ModelMap model,HttpServletRequest request, HttpServletResponse response) {
    	String loginName = request.getParameter("loginName").trim();
        String loginPassword = request.getParameter("loginPassword").trim();

        String bLogIn = userRepository.getUserInfo(loginName, loginPassword);
        model.addAttribute("loginResult", bLogIn);

        return "login";
    }

}
