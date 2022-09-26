package se.skltp.tak.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthController {
    @RequestMapping("auth/login")
    public String login() {
        return "auth/login";
    }

    @PostMapping("auth/signIn")
    public String signIn(HttpServletRequest request, @RequestParam String username, @RequestParam String password) {
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            Subject currentUser = SecurityUtils.getSubject();
            currentUser.login(token);
            request.getSession().setAttribute("username", username);

            SavedRequest savedRequest = WebUtils.getSavedRequest(request);
            String targetUri = savedRequest != null ? savedRequest.getRequestURI() : "/";
            return "redirect:" + targetUri;
        }
        catch (Exception e) {
            return "redirect:/auth/login";
        }
    }

    @RequestMapping("auth/signOut")
    public String signOut() {
        SecurityUtils.getSubject().logout();
        return "redirect:auth/login";
    }
}
