package web.Interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import web.controller.IndexController;

public class LoginInterceptor extends HandlerInterceptorAdapter {
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception
	{
		HttpSession session = request.getSession();
		if(session.getAttribute("user") != "user")
		{
			response.sendRedirect(request.getContextPath()+ "/login.htm");
			IndexController.login=false;
			return false;
		}
		return true;
	}
}
